package com.shinhan.education;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shinhan.education.respository.HanaRepository;
import com.shinhan.education.respository.KookminRepository;
import com.shinhan.education.respository.LoansRepository;
import com.shinhan.education.respository.ShinhanRepository;
import com.shinhan.education.respository.WooriRepository;
import com.shinhan.education.vo.HanaHTML;
import com.shinhan.education.vo.KookminHTML;
import com.shinhan.education.vo.Loans;
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

	@Test
	void intoloans() {
		List<Loans> loanList = new ArrayList<>();
		List<ShinhanHTML> slit = (List<ShinhanHTML>) shinhanRepo.findAll();
		List<KookminHTML> klist = (List<KookminHTML>) kookminRepo.findAll();
		List<WooriHTML> wlit = (List<WooriHTML>) wooriRepo.findAll();
		List<HanaHTML> hlit = (List<HanaHTML>) hanaRepo.findAll();

		slit.forEach((a) -> {
			Loans l = new Loans(a.getProductname(), a.getBankname());
			loanList.add(l);
		});
		klist.forEach((a) -> {
			Loans l = new Loans(a.getProductname(), a.getBankname());
			loanList.add(l);
		});
		wlit.forEach((a) -> {
			Loans l = new Loans(a.getProductname(), a.getBankname());
			loanList.add(l);
		});
		hlit.forEach((a) -> {
			Loans l = new Loans(a.getProductname(), a.getBankname());
			loanList.add(l);
		});

		loanList.forEach((loan)->{
			
			loanRepo.save(loan);
			
		});
	}

}
