package com.shinhan.education;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shinhan.education.entity.ShinhanHTML;
import com.shinhan.education.respository.ShinhanRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest
public class ShinhanCrawling {

	// 1. pom.mxl수정하기
//   <!-- 크롤링 JSOUP -->
//   <dependency>
//      <groupId>org.jsoup</groupId>
//      <artifactId>jsoup</artifactId>
//      <version>1.11.2</version>
//   </dependency>
//
//   <!-- 셀리니움 -->
//   
//   <dependency>
//      <groupId>org.seleniumhq.selenium</groupId>
//      <artifactId>selenium-java</artifactId>
//      <version>3.141.59</version>
//   </dependency>
//   <!-- 구글드라이브 -->
//<dependency>
//    <groupId>io.github.bonigarcia</groupId>
//    <artifactId>webdrivermanager</artifactId>
//    <version>5.1.0</version>
//    <scope>test</scope>
//</dependency>
//   
	@Autowired
	ShinhanRepository shinhanRepo;

	//@Test
	void getall() {
		System.out.println("start");
		String names[] = { "버팀목 전세자금 대출", "쏠편한 전세대출(서울보증보험)", "쏠편한 전세대출(주택금융공사)", "쏠편한 전세대출(주택도시보증)", "신한전세대출(서울보증보험)",
				"신한전세대출(주택금융공사)", "신한전세대출(주택도시보증)", "목돈 안드는 행복 전세대출", "신한 더드림 전세대출(서울보증_외국인)", "신한 청년전세대출", "신한 청년월세대출",
				"중소기업 취업청년 전월세보증금대출", "부도임대주택퇴거자전세자금대출", "비정상거처 이주자금지원 버팀목 전세자금 대출", "전세피해 임차인 버팀목전세대출",
				"전세피해 임차인 버팀목전세대출(대환)" };/*
											 * , "아낌e보금자리론","신한주택대출(아파트)", ,, "쏠편한 전세대출(주택도시보증)","미래설계 크레바스 주택연금대출",
											 * "Tops 부동산대출","공유형모기지론(손익형)","신한전세대출(서울보증보험)","신한전세대출(주택금융공사)",
											 * "신한전세대출(주택도시보증)","주택연금 역모기지론","신한주택대출","생애최초구입자금대출(특례구입자금보증)",
											 * "목돈 안드는 행복 전세대출","오피스텔 구입자금대출",
											 * "내집마련디딤돌대출","신한 더드림 전세대출(서울보증_외국인)","신한 청년전세대출",
											 * "신한 청년월세대출","서울특별시 신혼부부 임차보증금 대출","신혼희망타운 전용 주택담보장기대출상품(수익공유형모기지)",
											 * "청년전용 버팀목 보증부 월세대출","중소기업 취업청년 전월세보증금대출",
											 * "부도임대주택퇴거자전세자금대출","주거안정 주택구입자금대출",
											 * "비정상거처 이주자금지원 버팀목 전세자금 대출","전세피해 임차인 버팀목전세대출","전세피해 임차인 버팀목전세대출(대환)"};
											 */
		List<ShinhanHTML> shinhanlist = new ArrayList<>();

		for (String n : names) {
			String loanname = "a[title='" + n + "']";
			shinhanlist.add(crawl(loanname,n));
		}

		// add to table
		shinhanlist.forEach((s) -> {
			shinhanRepo.save(s);
		});

		System.out.println("finish");
	}

	ShinhanHTML crawl(String loanname,String prodname) {

		// ChromeDriver
		WebDriverManager.chromedriver().setup();
		// Chrome 옵션 설정 (headless 모드)
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless"); // 브라우저를 표시하지 않음
		// ChromeDriver 생성
		WebDriver driver = new ChromeDriver(options);
		// 웹 페이지 접속
		driver.get("https://oldm.shinhan.com/pages/financialPrdt/loan/loan_list2.jsp?LOAN_TYPE=20");
		// driver.get("https://oldm.shinhan.com/pages/financialPrdt/loan/loan_detail.jsp?zIdx=16860257656329645.806972085933");
		WebElement element = driver.findElement(By.cssSelector(loanname));

		element.click();// 전체 대출중에서 원하는 대출 페이지로 이동
		// ----------------------------------------------------------------------------------------------------

		String productname = prodname;

		String bankname = "신한";

		String mobile = null;// 모바일
		String internet = null;// 인터넷
		String branch = null;// 영업점

		// html여기서부터
		String summary = null;// 상품개요
		String loancust = null;// 대출고객
		String loanlimit = null;// 대출한도
		String interestrate = null;// 대출금리
		String loanterm = null;// 대출기간
		String repaymentmethod = null;// 상환방법
		String prepaymentpenalty = null;// 중도상한해약금
		String loancosts = null;// 대출비용
		String taxbenefits = null;// 세제/금리
		String reqdocs = null;// 필요서류
		String moreinfo = null;// 추가사항->필요서류,추가대출,담보,신청기간
		String custprotection = null;// 금융소비자보호
		String caution = null;// ->유의사항

		WebElement mob = driver.findElement(By.xpath("//*[@id=\"ICO1\"]"));
		mobile = mob.getText().toString();

		WebElement inter = driver.findElement(By.xpath("//*[@id=\"ICO2\"]"));
		internet = inter.getText().toString();

		WebElement bran = driver.findElement(By.xpath("//*[@id=\"ICO3\"]"));
		branch = bran.getText().toString();

		// 페이지에서 요소 추출
		WebElement temp = driver.findElement(By.id("C_PROD_OUTLINE2_TTL"));// 상품개요
		summary = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_CLIENT2_TTL"));// 대출고객
		loancust = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_LOAN_LIMIT2_TTL"));// 대출한도
		loanlimit = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("loanrate"));// 대출금리
		interestrate = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_LOAN_DATE_TTL"));// 대출기간
		loanterm = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_EXCHANGE_WAY_TTL"));// 상환방법
		repaymentmethod = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_EXCHANGE_FEE2_TTL"));// 중도상한해약금
		prepaymentpenalty = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("LOAN_COST"));// 대출비용
		loancosts = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_TAX_RATE_TTL"));// 세제/금리
		taxbenefits = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_NEED_DOC_TTL"));// 필요서류
		reqdocs = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_APPND_FACTS_TTL"));// 추가사항->필요서류,추가대출,담보,신청기간
		moreinfo = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_PROD_PROCT_TTL"));// 금융소비자보호
		custprotection = temp.getAttribute("outerHTML");

		temp = driver.findElement(By.id("C_ATTENTION_TTL"));// ->유의사항
		caution = temp.getAttribute("outerHTML");

		ShinhanHTML shinhan = ShinhanHTML.builder().productname(productname).bankname(bankname).mobile(mobile)
				.internet(internet).branch(branch).summary(summary).loancust(loancust).loanlimit(loanlimit)
				.interestrate(interestrate).loanterm(loanterm).repaymentmethod(repaymentmethod)
				.prepaymentpenalty(prepaymentpenalty).loancosts(loancosts).taxbenefits(taxbenefits).reqdocs(reqdocs)
				.moreinfo(moreinfo).custprotection(custprotection).caution(caution).build();

		// -------------------------------------------------------------------------------------------------------------

		return shinhan;
	}

}