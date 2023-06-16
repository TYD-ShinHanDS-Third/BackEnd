package com.shinhan.education;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.shinhan.education.entity.DetailPans;
import com.shinhan.education.entity.PanFavorites;
import com.shinhan.education.entity.PanFavoritesId;
import com.shinhan.education.entity.Pans;
import com.shinhan.education.respository.DetailPanRepository;
import com.shinhan.education.respository.PanFavRepository;
import com.shinhan.education.respository.PanRepository;

@SpringBootTest
public class jpaTest {
//	@Autowired
//	PanFavRepository favRepo;
//	@Autowired
//	PanRepository panRepo;
//	@Autowired
//	DetailPanRepository dpRepo;
//	@Test
//	void jpatesting() {
//		String memberid = "ckdrua1";
//		String panid = "2015122300013854";
//		String location = "서울";
//		int page = 1;
//		int size = 10;
//		System.out.println("관심공고 요청 들어옴");
//
//
//		Pageable pageable = PageRequest.of(page, size, Sort.by("panstartdate").ascending());
//		List<Pans> panList = panRepo.findPansByMemberId(memberid, pageable);// 수정->fav
//
//
//		panList.forEach((pan) -> {
//			pan.setLike(1);
//		});
//		System.out.println(panList);
//	}
}
