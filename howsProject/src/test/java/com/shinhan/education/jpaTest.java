package com.shinhan.education;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shinhan.education.respository.DetailPanRepository;
import com.shinhan.education.respository.PanFavRepository;
import com.shinhan.education.respository.PanRepository;
import com.shinhan.education.vo.DetailPans;
import com.shinhan.education.vo.PanFavorites;
import com.shinhan.education.vo.PanFavoritesId;
import com.shinhan.education.vo.Pans;

@SpringBootTest
public class jpaTest {
	@Autowired
	PanFavRepository favRepo;
	@Autowired
	PanRepository panRepo;
	@Autowired
	DetailPanRepository dpRepo;
	@Test
	void jpatesting() {
		String memberId = "ckdrua1";
		String panid = "2015122300013854";
		String location = "서울";
		//DetailPans dp =  dpRepo.findById(panid).get();
		//System.out.println(dp);
	}
}
