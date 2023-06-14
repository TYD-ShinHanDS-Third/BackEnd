package com.shinhan.education;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shinhan.education.respository.DetailPanRepository;
import com.shinhan.education.respository.HanaRepository;
import com.shinhan.education.respository.KookminRepository;
import com.shinhan.education.respository.LoansRepository;
import com.shinhan.education.respository.PanRepository;
import com.shinhan.education.respository.ShinhanRepository;
import com.shinhan.education.respository.WooriRepository;
import com.shinhan.education.vo.DetailPans;
import com.shinhan.education.vo.HanaHTML;
import com.shinhan.education.vo.KookminHTML;
import com.shinhan.education.vo.Loans;
import com.shinhan.education.vo.Pans;
import com.shinhan.education.vo.ShinhanHTML;
import com.shinhan.education.vo.WooriHTML;

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

	@Test
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

	// @Test
	void test1() {
		String panid = "2015122300013847";

		Pans pan = panRepo.findById(panid).get();
		System.out.println(pan);
		DetailPans dp = dpRepo.findByPan(pan);
		System.out.println(dp);
	}

}
