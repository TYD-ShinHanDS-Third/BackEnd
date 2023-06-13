package com.shinhan.education;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.shinhan.education.respository.HanaRepository;
import com.shinhan.education.respository.KookminRepository;
import com.shinhan.education.respository.LoansRepository;
import com.shinhan.education.respository.PanFavRepository;
import com.shinhan.education.respository.PanRepository;
import com.shinhan.education.respository.ShinhanRepository;
import com.shinhan.education.respository.WooriRepository;
import com.shinhan.education.vo.HanaHTML;
import com.shinhan.education.vo.KookminHTML;
import com.shinhan.education.vo.Loans;
import com.shinhan.education.vo.PanFavorites;
import com.shinhan.education.vo.PanFavoritesId;
import com.shinhan.education.vo.Pans;
import com.shinhan.education.vo.RequestVO;
import com.shinhan.education.vo.ShinhanHTML;
import com.shinhan.education.vo.WooriHTML;

@SpringBootTest
class PagingTest {
	@Autowired
	LoansRepository loanRepo;
	@Autowired
	ShinhanRepository shinhanRepo;

	@Autowired
	KookminRepository kookminRepo;

	@Autowired
	WooriRepository wooriRepo;

	@Autowired
	HanaRepository hanaRepo;
	@Autowired
	PanRepository panRepo;

	@Autowired
	PanFavRepository favRepo;

	@Test
	void pagetest() {
		String memberid = "ckdrua1";
		int page = 0;
		int size = 10;

		System.out.println("요청 들어옴");
		// PageRequest.of(int page, int size, sort)
		// page : 요청하는 페이지 번호
		// size : 한 페이지 당 조회할 크기 (기본값 : 20)
		// sort : Sorting 설정 (기본값 : 오름차순)
		// String token = request.getParameter("token");
		// int page = Integer.parseInt(request.getParameter("page"));
		// int size = Integer.parseInt(request.getParameter("size"));
		// String token = null;
		System.out.println("page : " + page);
		System.out.println("size : " + size);

		Pageable pageable = PageRequest.of(page, size, Sort.by("panstartdate").ascending());
		Page<Pans> panPage = panRepo.findAll(pageable);
		List<Pans> panList = panPage.getContent();

		// if (token != null) {// 로그인 된 상태 -> 좋아요 확인하자
		// String memberid = getMemberId(token);
		panList.forEach((pan) -> {
			PanFavoritesId id = new PanFavoritesId(pan.getPanid(), memberid);
			Optional<PanFavorites> favorite = (Optional<PanFavorites>) favRepo.findById(id);
			if (favorite.isPresent()) {
				pan.setLike(1);
			}
			// System.out.println(pan);
		});
		// }
		RequestVO<List<Pans>> r = new RequestVO<List<Pans>>();
		int total = panRepo.countAllPans();
		r.setObj(panList);
		r.setTotal(total);
		panList.forEach((x)->{System.out.println(x);});

	}

}
