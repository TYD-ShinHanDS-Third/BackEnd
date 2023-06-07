package com.shinhan.education;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

import com.shinhan.education.respository.HanaRepository;
import com.shinhan.education.vo.HanaHTML;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest
public class HanaCrawling {

	@Autowired
	HanaRepository hanaRepo;

	String html1 = null;
	String html2 = null;
	String html3 = null;
	String html4 = null;
	String html5 = null;
	String html6 = null;
	String html7 = null;
	String html8 = null;
	String html9 = null;
	String html10 = null;
	String html11 = null;
	String html12 = null;
	String html13 = null;
	String html14 = null;
	String html15 = null;
	String html16 = null;
	String html17 = null;
	String html18 = null;
	String html19 = null;
	String html20 = null;
	String html21 = null;
	String html22 = null;
	String html23 = null;
	String html24 = null;
	String html25 = null;
	String html26 = null;
	String html27 = null;
	String html28 = null;
	String html29 = null;
	String html30 = null;
	// 1. pom.mxl수정하기

//   

	// String names[] = {"iTouch 전세론(주택금융보증)","iTouch 전세론(서울보증일반)"};
	//@Test
	void gotopage() {
		// 전세 페이지로 이동하기
		WebDriverManager.chromedriver().setup();
		// Chrome 옵션 설정 (headless 모드)
		ChromeOptions options = new ChromeOptions();
		// options.addArguments("--headless"); // 브라우저를 표시하지 않음
		// ChromeDriver 생성
		WebDriver driver = new ChromeDriver(options);

		driver.get("https://www.kebhana.com/cont/mall/mall08/mall0805/index.jsp");

		WebElement loanmenu = driver.findElement(By.xpath("//*[@id=\"search\"]/ul/li[2]/a"));
		loanmenu.click();
		WebDriverWait wait = new WebDriverWait(driver, 10);
		By locator = By.xpath("//*[@id=\"search\"]/ul/li[2]/div/ul/li[2]/a");
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
		WebElement houseloan = driver.findElement(By.xpath("//*[@id=\"search\"]/ul/li[2]/div/ul/li[2]/a"));
		houseloan.click();
		locator = By.xpath("//*[@id=\"contents\"]/div[2]/div/div[2]/div/strong");
		Pattern pattern = Pattern.compile("전체 \\d+개 전\\(월\\)세대출");
		wait.until(ExpectedConditions.textMatches(locator, pattern));

		int total = 0;
		WebElement tot_num = driver.findElement(By.xpath("//*[@id=\"contents\"]/div[2]/p/span[1]"));

		total = Integer.parseInt(tot_num.getText());
		// getlinks(driver, total);
		getlinks(driver, total);

	}

	void getlinks(WebDriver driver, int total) {
		// total = 1;
		System.out.println("getlinks start");
		List<String> linklist = new ArrayList<>();

		for (int i = 1; i <= 10; i++) {

			WebElement li = driver.findElement(
					By.xpath("//*[@id=\"contents\"]/div[2]/div/div[3]/ul/li[" + i + "]/div/label/span/span[1]/em/a"));
			String link = li.getAttribute("href");
			// System.out.println(link);
			linklist.add(link);

		}

		WebElement nextmenu = driver.findElement(By.xpath("//*[@id=\"contents\"]/div[2]/div/div[4]/a[4]"));
		nextmenu.click();
		By locator = By.xpath("//*[@id=\"contents\"]/div[2]/div/div[4]/a[4]");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.attributeContains(locator, "class", "on"));

		for (int i = 1; i <= 10; i++) {

			WebElement li = driver.findElement(
					By.xpath("//*[@id=\"contents\"]/div[2]/div/div[3]/ul/li[" + i + "]/div/label/span/span[1]/em/a"));

			String link = li.getAttribute("href");
			// System.out.println(link);
			linklist.add(link);

		}
		// int i = 0;
		List<HanaHTML> hanalist = new ArrayList<>();
		for (String l : linklist) {
			// if(i>=2)break;
			hanalist.add(crawl(driver, l));
			// i++;
		}

		// add to table
		hanalist.forEach((h) -> {
			hanaRepo.save(h);
		});

	}

	HanaHTML crawl(WebDriver driver, String link) {
		driver.get(link);

		WebElement prodname = driver.findElement(By.xpath("//*[@id=\"contents\"]/div[2]/h3"));
		String productname = prodname.getText().toString();

		String bankname = "하나";

		String internet = null;
		String branch = "영업점";

		// 더보기 열기
		List<WebElement> elements = driver.findElements(By.xpath("//*[@id='contents']/div[2]/dl/dd[22]/div/a/img"));
		if (elements.size() > 0) {
			WebElement element = elements.get(0);
			element.click();
		}

		List<WebElement> dt = driver.findElements(By.xpath("//*[@id=\"contents\"]/div[2]/dl/dt"));

		for (int i = 1; i <= dt.size(); i++) {

			WebElement dd = driver.findElement(By.xpath("//*[@id=\"contents\"]/div[2]/dl/dd[" + i + "]"));
			String html = dt.get(i - 1).getAttribute("outerHTML").toString() + dd.getAttribute("outerHTML").toString();
			String variableName = "html" + i;

			try {
				Field field = getClass().getDeclaredField(variableName);
				field.set(this, html);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		HanaHTML hana = HanaHTML.builder().productname(productname).bankname(bankname).internet(internet).branch(branch)
				.html1(html1).html2(html2).html3(html3).html4(html4).html5(html5).html6(html6).html7(html7).html8(html8)
				.html9(html9).html10(html10).html11(html11).html12(html12).html13(html13).html14(html14).html15(html15)
				.html16(html16).html17(html17).html18(html18).html19(html19).html20(html20).html21(html21)
				.html22(html22).html23(html23).html24(html24).html25(html25).html25(html26).html25(html27)
				.html25(html28).html25(html29).html25(html30).build();

		for (int i = 1; i <= 30; i++) {
			String variableName = "html" + i;

			try {
				Field field = getClass().getDeclaredField(variableName);
				field.set(this, null);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return hana;

	}

}