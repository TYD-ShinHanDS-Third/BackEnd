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
		int page = 1;
		int size = 10;

		String location = "경기도";
		Pageable pageable = PageRequest.of(page, size,Sort.by("panstartdate").ascending());
		List<Pans> loclist = panRepo.findByLocationContaining(location, pageable);
		
		loclist.forEach((loc)->{System.out.println(loc);});
		
		System.out.println(loclist);

	}

}
