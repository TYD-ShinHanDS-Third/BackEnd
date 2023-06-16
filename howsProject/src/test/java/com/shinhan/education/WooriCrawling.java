package com.shinhan.education;

import java.lang.reflect.Field;
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

import com.shinhan.education.entity.WooriHTML;
import com.shinhan.education.respository.WooriRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest
public class WooriCrawling {
	@Autowired
	WooriRepository wooriRepo;

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

	@Test
	void getlinks() {
		String list[] = {
				"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020000273&PRD_CD=P020000273&HOST_PRD_CD=2031037000079"
				//,
				//"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020006141&PRD_CD=P020006141&HOST_PRD_CD=2031161000000",
				//"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020006111&PRD_CD=P020006111&HOST_PRD_CD=2031305121100",
				//"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020006110&PRD_CD=P020006110&HOST_PRD_CD=2031305111100",
				//"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020006103&PRD_CD=P020006103&HOST_PRD_CD=2031159020000",
				//"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020000270&PRD_CD=P020000270&HOST_PRD_CD=2001037261100",
				//"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020000027&PRD_CD=P020000027&HOST_PRD_CD=2031139220079",
				//"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020000139&PRD_CD=P020000139&HOST_PRD_CD=2031139111179",
				//"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020000256&PRD_CD=P020000256&HOST_PRD_CD=2031139191179",
				//"https://spot.wooribank.com/pot/Dream?withyou=POLON0055&cc=c010528:c010531;c012425:c012399&PLM_PDCD=P020000265&PRD_CD=P020000265&HOST_PRD_CD=2031139241479" 
				};

		List<WooriHTML> woorilist = new ArrayList<>();

		for (String link : list) {
			woorilist.add(crawl(link));

		}

		System.out.println("finish : " + woorilist.size());
		
		
		woorilist.forEach((w)->{
			
			wooriRepo.save(w);
			
			
		});
	}

	WooriHTML crawl(String link) {

		// ChromeDriver
		WebDriverManager.chromedriver().setup();
		// Chrome 옵션 설정 (headless 모드)
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless"); // 브라우저를 표시하지 않음
		// ChromeDriver 생성
		WebDriver driver = new ChromeDriver(options);
		// 웹 페이지 접속
		// iTouch 전세론(서울보증일반)
		driver.get(link);

		String bankname = "우리";

		WebElement prodname = driver.findElement(By.xpath("//*[@id=\"content\"]/div[4]/div/div/div[1]/dl/dt"));
		String productname = prodname.getText();
		WebElement type = driver.findElement(By.xpath("//*[@id=\"content\"]/div[4]/div/div/div[1]/dl/dd[4]/ul/li[1]"));
		String prodtype = type.getText();
//		WebElement name = driver.findElement(By.xpath("//*[@id=\"content\"]/div[4]/div/div/div[1]/dl/dt"));
//		System.out.println(name.getText());

		// 페이지에서 요소 추출
		List<WebElement> temp = driver.findElements(By.className("po-detail-info"));
		// 우리 은행은 <dl class="po-detail-info">안에 항목별로 나눠서 들어있음. 반복문으로 처리 가능. 클래스가 모두 같음
		int i = 1;
		for (WebElement el : temp) {
			String html = el.getAttribute("outerHTML").toString();
			String variableName = "html" + i;

			try {
				Field field = getClass().getDeclaredField(variableName);
				field.set(this, html);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}

			i++;
		}

		WooriHTML woori = WooriHTML.builder().productname(productname).bankname(bankname).types(prodtype).html1(html1)
				.html2(html2).html3(html3).html4(html4).html5(html5).html6(html6).html7(html7).html8(html8).html9(html9)
				.html10(html10).html11(html11).html12(html12).html13(html13).html14(html14).html15(html15)
				.html16(html16).html17(html17).html18(html18).html19(html19).html20(html20).build();

		for (i = 1; i <= 20; i++) {
			String variableName = "html" + i;

			try {
				Field field = getClass().getDeclaredField(variableName);
				field.set(this, null);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		System.out.println("finish");
		driver.quit();

		return woori;
	}

}