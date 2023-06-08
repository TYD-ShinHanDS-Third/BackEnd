package com.shinhan.education.controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.shinhan.education.respository.DetailPanRepository;
import com.shinhan.education.respository.PanFavRepository;
import com.shinhan.education.respository.PanRepository;
import com.shinhan.education.vo.DetailPans;
import com.shinhan.education.vo.PanFavorites;
import com.shinhan.education.vo.PanFavoritesId;
import com.shinhan.education.vo.Pans;
import com.shinhan.education.vo.Payload;

@RestController
@RequestMapping("/hows")
public class HowsController {
	@Autowired
	PanRepository panRepo;
	@Autowired
	PanFavRepository favRepo;
	@Autowired
	DetailPanRepository dpRepo;

	String getMemberId(String token) {
		// String token =
		// "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJja2RydWExIiwicm9sZXMiOlsiVXNlciJdLCJpYXQiOjE2ODYxODkwNzIsImV4cCI6MTY4NjI3NTQ3Mn0.BuOvMeMhLfIlwMZcGioJbSbxtJnEKE5aWwAj1ntaCPE";
		String[] chunks = token.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();

		String payloadJson = new String(decoder.decode(chunks[1]));

		Gson gson = new Gson();

		Payload payload = gson.fromJson(payloadJson, Payload.class);
		String memberid = payload.getSub();
		System.out.println(memberid);
		return memberid;
	}

	@GetMapping("/notice") // 공고 조회
	public List<Pans> allpans(@RequestBody String token) {
		List<Pans> panList = panRepo.findAll();
		if (token != null) {// 로그인 된 상태 -> 좋아요 확인하자
			String memberid = getMemberId(token);
			panList.forEach((pan) -> {
				PanFavoritesId id = new PanFavoritesId(pan.getPanid(), memberid);
				Optional<PanFavorites> favorite = (Optional<PanFavorites>) favRepo.findById(id);
				if (favorite.isPresent()) {
					pan.setLike(1);
				}
				//System.out.println(pan);
			});
		}
		return panList;
	}

	@PostMapping("/notice") // 공고 좋아요
	public String addLike(HttpServletRequest request) {
		String panid = request.getParameter("panid");
		String token = request.getParameter("token");
		String memberId = getMemberId(token);

		PanFavoritesId id = new PanFavoritesId(panid, memberId);

		Pans pan = panRepo.findById(panid).orElse(null);
		if (pan == null)
			return "fail";
		PanFavorites panfav = PanFavorites.builder().panid(pan.getPanid()).memberid(memberId).panname(pan.getPanname())
				.panstartdate(pan.getPanstartdate()).panenddate(pan.getPanenddate()).build();
		favRepo.save(panfav);
		return "like success";
	}

	@DeleteMapping("/notice") // 공고 좋아요 취소
	public String deletelike(HttpServletRequest request) {

		String panid = request.getParameter("panid");
		String token = request.getParameter("token");
		String memberId = getMemberId(token);
		PanFavoritesId id = new PanFavoritesId(panid, memberId);
		favRepo.findById(id).ifPresent(panFavorites -> {
			favRepo.deleteById(id);
		});
		return "like delete success";
	}

	// 지역별 공고 가져오기
	@GetMapping(value = "/notice/{location}", produces = "text/plain;charset=UTF-8")
	public List<Pans> selectByLocation(@PathVariable String location) {
		List<Pans> loclist = panRepo.findByLocationContaining(location);
		return loclist;
	}
	//공고 상세 조회
	@GetMapping(value = "/notice/detail")
	public DetailPans selectbyid(@RequestBody String panid) {
		DetailPans dp =  dpRepo.findById(panid).get();
		return dp;
	}

}
