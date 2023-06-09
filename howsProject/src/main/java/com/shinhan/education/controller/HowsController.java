package com.shinhan.education.controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.shinhan.education.respository.DetailPanRepository;
import com.shinhan.education.respository.LoansRepository;
import com.shinhan.education.respository.PanFavRepository;
import com.shinhan.education.respository.PanRepository;
import com.shinhan.education.vo.DetailPans;
import com.shinhan.education.vo.Houses;
import com.shinhan.education.vo.Loans;
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
	@Autowired
	LoansRepository loanRepo;

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
	public List<Pans> allpans(int page, int size, String token) {
		System.out.println("요청 들어옴");
		// PageRequest.of(int page, int size, sort)
		// page : 요청하는 페이지 번호
		// size : 한 페이지 당 조회할 크기 (기본값 : 20)
		// sort : Sorting 설정 (기본값 : 오름차순)
		// String token = request.getParameter("token");
		// String page = (request.getParameter("page"));
		// String size =(request.getParameter("size"));
		// String token = null;
		System.out.println("page : " + page);
		System.out.println("size : " + size);

		Pageable pageable = PageRequest.of(page, size, Sort.by("panstartdate").ascending());
		Page<Pans> panPage = panRepo.findAll(pageable);
		List<Pans> panList = panPage.getContent();
		if (token != null) {// 로그인 된 상태 -> 좋아요 확인하자
			String memberid = getMemberId(token);
			panList.forEach((pan) -> {
				PanFavoritesId id = new PanFavoritesId(pan.getPanid(), memberid);
				Optional<PanFavorites> favorite = (Optional<PanFavorites>) favRepo.findById(id);
				if (favorite.isPresent()) {
					pan.setLike(1);
				}
				// System.out.println(pan);
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
	public List<Pans> selectByLocation(@PathVariable String location, HttpServletRequest request) {

		int page = Integer.parseInt(request.getParameter("page"));
		int size = Integer.parseInt(request.getParameter("size"));

		Pageable pageable = PageRequest.of(page, size, Sort.by("panstartdate").ascending());
		List<Pans> loclist = panRepo.findByLocationContaining(location, pageable);

		return loclist;
	}

	// 공고 상세 조회
	@GetMapping(value = "/notice/detail")
	public DetailPans selectbyid(String panid) {
		System.out.println("공고 상세 요청 들어옴");
		Pans pan = panRepo.findById(panid).get();
		// System.out.println("pan" + pan );
		DetailPans dp = dpRepo.findByPan(pan);// 객체라서 문제 발생
		// System.out.println("detail pan : " + dp);
		return dp;
	}

	@GetMapping(value = "/notice/find")
	public List<Houses> selectByAddress(String houseaddress) {

		return null;

	}

	@GetMapping(value = "notice/loan")
	public List<Loans> loanlist() {
		return (List<Loans>) loanRepo.findAll();
	}

}
