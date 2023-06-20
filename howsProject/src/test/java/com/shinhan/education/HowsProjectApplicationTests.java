package com.shinhan.education;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shinhan.education.entity.DetailPans;
import com.shinhan.education.entity.HanaHTML;
import com.shinhan.education.entity.Houses;
import com.shinhan.education.entity.KookminHTML;
import com.shinhan.education.entity.Loans;
import com.shinhan.education.entity.Pans;
import com.shinhan.education.entity.ShinhanHTML;
import com.shinhan.education.entity.WooriHTML;
import com.shinhan.education.respository.DetailPanRepository;
import com.shinhan.education.respository.HanaRepository;
import com.shinhan.education.respository.HouseRepository;
import com.shinhan.education.respository.KookminRepository;
import com.shinhan.education.respository.LoansRepository;
import com.shinhan.education.respository.PanRepository;
import com.shinhan.education.respository.ShinhanRepository;
import com.shinhan.education.respository.WooriRepository;
import com.shinhan.education.vo.XYResult;

@SpringBootTest
class HowsProjectApplicationTests {
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
	DetailPanRepository dpRepo;

	@Autowired
	HouseRepository houseRepo;

	// @Test
	void intoloans() {
		List<Loans> loanList = new ArrayList<>();
		List<ShinhanHTML> slit = (List<ShinhanHTML>) shinhanRepo.findAll();
		List<KookminHTML> klist = (List<KookminHTML>) kookminRepo.findAll();
		List<WooriHTML> wlit = (List<WooriHTML>) wooriRepo.findAll();
		List<HanaHTML> hlit = (List<HanaHTML>) hanaRepo.findAll();

		slit.forEach((a) -> {
			String internet = a.getInternet() != null ? a.getInternet() : "";
			String mobile = a.getMobile() != null ? a.getMobile() : "";
			String branch = a.getBranch() != null ? a.getBranch() : "";
			String type = (internet + " " + mobile + " " + branch).trim();

			if (!type.isEmpty()) {
				System.out.println(type);
				Loans l = new Loans(a.getProductname(), a.getBankname(), type);
				loanList.add(l);
			}
		});

		klist.forEach((a) -> {
			String internet = a.getInternet() != null ? a.getInternet() : "";
			String smart = a.getSmart() != null ? a.getSmart() : "";
			String starbanking = a.getStarbanking() != null ? a.getStarbanking() : "";
			String branch = a.getBranch() != null ? a.getBranch() : "";
			String type = (internet + " " + smart + " " + starbanking + " " + branch).trim();

			if (!type.isEmpty()) {
				System.out.println(type);
				Loans l = new Loans(a.getProductname(), a.getBankname(), type);
				loanList.add(l);
			}
		});
		wlit.forEach((a) -> {
			String type = a.getTypes();
			type.replaceAll("null", "");
			Loans l = new Loans(a.getProductname(), a.getBankname(), type);
			loanList.add(l);
		});
		hlit.forEach((a) -> {
			String type = a.getBranch();

			Loans l = new Loans(a.getProductname(), a.getBankname(), type);
			loanList.add(l);
		});

		loanList.forEach((loan) -> {

			loanRepo.save(loan);

		});

	}

	@Test
	void test2() {

		String houseaddress = "서울특별시 중랑구 겸재로30길 35-5";
		List<Houses> hlist = houseRepo.findXY(houseaddress);
		BigDecimal x = hlist.get(0).getXPos();
		BigDecimal y = hlist.get(0).getYPos();
		System.out.println(x + " " + y);
		String num = "0.00500000";
		BigDecimal x1 = x.add(new BigDecimal(num));
		BigDecimal x2 = x.subtract(new BigDecimal(num));
		BigDecimal y1 = y.add(new BigDecimal(num));
		BigDecimal y2 = y.subtract(new BigDecimal(num));
		System.out.println(x1 + " " + x2 + " " + y1 + " " + y2 );
		List<Houses> hlist2 = houseRepo.findByXPosBetweenAndYPosBetween(x2, x1, y2, y1);
		System.out.println("size : " + hlist2.size());
		Map<String, XYResult> xyMap = new HashMap<>();
		hlist2.forEach((house) -> {

			XYResult xy = new XYResult();
			xy.setX(house.getXPos());
			xy.setY(house.getYPos());
			xyMap.put(house.getRoadaddress(), xy);
		});
		
		System.out.println(xyMap);
		
	}

	// @Test
	void test1() {
		String panid = "2015122300013847";

		Pans pan = panRepo.findById(panid).get();
		System.out.println(pan);
		DetailPans dp = dpRepo.findByPan(pan);
		System.out.println(dp);
	}

}
