package com.shinhan.education;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shinhan.education.entity.KookminHTML;
import com.shinhan.education.respository.KookminRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest
public class KookminCrawling {

	@Autowired
	KookminRepository kbRepo;
	// 1. pom.mxl수정하기

//   

	// String names[] = {"iTouch 전세론(주택금융보증)","iTouch 전세론(서울보증일반)"};
	//@Test
	void getlinks() {

		WebDriverManager.chromedriver().setup();
		// Chrome 옵션 설정 (headless 모드)
		ChromeOptions options = new ChromeOptions();
		// options.addArguments("--headless"); // 브라우저를 표시하지 않음
		// ChromeDriver 생성
		WebDriver driver = new ChromeDriver(options);
		// 웹 페이지 접속
		// iTouch 전세론(서울보증일반)
		driver.get("https://obank.kbstar.com/quics?page=C103507");

		WebElement pagenum = driver.findElement(By.xpath("//*[@id=\"b104363\"]/div[2]/form[2]/h2/strong"));
		// System.out.println("pagenum check");
		int nums = Integer.parseInt(pagenum.getText());
		// int pages = 1;
		// if (pages % 10 == 0)
		// pages = nums / 10;
		// else
		// pages = nums / 10 + 1;

		List<String> links = new ArrayList<>();
		// get all links
		// for (int p = 1; p <= pages; p++) {

		for (int j = 1; j <= 3; j++) {

			if (j > 1) {
				WebElement nextpage = driver.findElement(By.xpath("//*[@id='pageinput" + j + "']/span/input"));
				nextpage.click();
				WebDriverWait wait = new WebDriverWait(driver, 10);
				By locator = By.xpath("//*[@id='pageinput" + j + "']/span");
				wait.until(ExpectedConditions.attributeContains(locator, "class", "selected"));
			}

			for (int i = 1; i <= 10; i++) {

				WebElement linkElement = driver
						.findElement(By.xpath("//*[@id=\"b104363\"]/div[2]/ul[2]/li[" + i + "]/div[1]/a"));
				String onclickValue = linkElement.getAttribute("onclick");
				String[] onclickParams = onclickValue.split(",");
				String param0 = onclickParams[0].trim().replaceAll("'", "").substring(8);
				String param1 = onclickParams[1].trim().replaceAll("'", "");
				String param2 = onclickParams[2].trim().replaceAll("'", "");
				String param3 = onclickParams[3].trim().replaceAll("'", "");
				String param4 = onclickParams[4].trim().replaceAll("'", "");
				String param5 = onclickParams[5].trim().replaceAll("'", "");

				String newlink = "https://obank.kbstar.com/quics?page=C103507&cc=b104363:b104516&isNew=" + param4
						+ "&prcode=" + param0 + "&QSL=F";
				links.add(newlink);
				// System.out.println(newlink);

				if (links.size() >= nums)
					break;
			}

		}

		// }

		List<KookminHTML> kblist = new ArrayList<>();

		// System.out.println("-----------------------------------");
		// int aa = 1;
		for (String l : links) {
			// System.out.println("=========================================" + aa);
			kblist.add(crawl(l, driver));
			// aa++;
		}
		// crawl("https://obank.kbstar.com/quics?page=C103507&cc=b104363:b104516&isNew=N&prcode=LN20000129&QSL=F",
		// driver);
		driver.quit();

	//	System.out.println(kblist.get(0).toString());
		//System.out.println("finish : " + kblist.size());
		
		kblist.forEach((k)->{
			kbRepo.save(k);
		});
	}

	KookminHTML crawl(String link, WebDriver driver) {
		String productname = null;

		String bankname = "국민";

		String starbanking = null;// kbstart뱅킹
		String internet = null;// 인터넷
		String branch = null;// 영업점
		String smart = null;// 스마트대출

		String summary = null;// 상품 안내
		String interestrate = null;// 금리 및 이율
		String useinfo = null;// 이용 안내
		String catuion = null;// 유의 및 기타사항
		String download = null;// 다운로드
		driver.get(link);

		List<WebElement> whereList = driver.findElements(By.xpath("//*[@id=\"b104516\"]/div[1]/dl/dd"));
		for (WebElement we : whereList) {
			if (we.getText().toString().equals("인터넷")) {
				internet = "인터넷";
			}
			if (we.getText().toString().equals("스타뱅킹")) {
				starbanking = "스타뱅킹";
			}
			if (we.getText().toString().equals("스마트대출")) {
				smart = "스마트대출";
			}
			if (we.getText().toString().equals("영업점")) {
				branch = "영업점";
			}

		}

		WebElement nameElement = driver.findElement(By.xpath("//*[@id=\"b104516\"]/div[1]/h2/b"));
		productname = nameElement.getText().toString();

		WebElement ulElement = driver.findElement(By.id("uiProTab"));
		List<WebElement> liElements = ulElement.findElements(By.tagName("li"));

		int i = 1;
		for (WebElement liElement : liElements) {
			if (!liElement.getAttribute("style").contains("display: none")) {
				liElement.click();
				WebDriverWait wait = new WebDriverWait(driver, 10);
				By locator = By.xpath(" //*[@id=\"uiProTab\"]/li[" + i + "]");
				wait.until(ExpectedConditions.attributeContains(locator, "aria-selected", "true"));

				WebElement information = driver.findElement(By.xpath("//*[@id=\"areaBox" + i + "\"]"));
				// System.out.println(name.getAttribute("outerHTML"));

				if (i == 1) {
					summary = information.getAttribute("outerHTML").toString();
				} else if (i == 2) {
					interestrate = information.getAttribute("outerHTML").toString();
				} else if (i == 3) {
					useinfo = information.getAttribute("outerHTML").toString();
				} else if (i == 4) {
					catuion = information.getAttribute("outerHTML").toString();
				} else if (i == 5) {
					download = information.getAttribute("outerHTML").toString();
				}

			}
			i++;
		}

		KookminHTML kb = KookminHTML.builder().productname(productname).bankname(bankname).starbanking(starbanking)
				.internet(internet).branch(branch).smart(smart).summary(summary).interestrate(interestrate)
				.useinfo(useinfo).catuion(catuion).download(download).build();

		return kb;
	}

}