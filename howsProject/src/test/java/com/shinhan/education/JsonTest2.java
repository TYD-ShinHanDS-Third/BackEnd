package com.shinhan.education;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.education.entity.Houses;
import com.shinhan.education.respository.HouseRepository;

@SpringBootTest
public class JsonTest2 {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	HouseRepository houseRepo;

	//@Test
	void getlocation() throws IOException {
		System.out.println("start");
		// 11
		int[] seoul = { 110, 140, 170, 200, 215, 230, 260, 290, 305, 320, 350, 380, 410, 440, 470, 500, 530, 545, 560,
				590, 620, 650, 680, 710, 740, };

		// 26
		int[] busan = { 110, 140, 170, 200, 230, 260, 290, 320, 350, 380, 410, 440, 470, 500, 530, 710 };

		// 27
		int[] daegu = { 110, 140, 170, 200, 230, 260, 290, 710, };

		// 28 제외 인천광역시 미추홀구
		int[] incheon = { 110, 170, 140, 185, 200, 237, 245, 260, 710, 720 };

		// 29
		int[] gwangju = { 110, 140, 155, 170, 200 };

		// 30
		int[] daejeon = { 110, 140, 170, 200, 230 };

		// 31
		int[] woolsan = { 110, 140, 170, 200, 710 };

		// 완료
		int[] gyeonggido = { 41, 110, 111, 113, 115, 117, 130, 131, 133, 135, 150, 170, 171, 173, 190, 210, 220, 250,
				270, 271, 273, 280, 281, 285, 287, 290, 310, 360, 370, 390, 410, 430, 450, 460, 461, 463, 465, 480, 500,
				550, 570, 590, 610, 630, 650, 670, 800, 820, 830 };

		// 42
		int[] kangwondo = { 110, 130, 150, 170, 190, 210, 230, 720, 730, 750, 760, 770, 780, 790, 800, 810, 820, 830 };

		// 43
		int[] choongbook = { 110, 111, 112, 113, 114, 130, 150, 720, 730, 740, 745, 750, 760, 770, 800 };

		// 44
		int[] choongnam = { 130, 131, 133, 150, 180, 200, 210, 230, 250, 270, 710, 760, 770, 790, 800, 810, 825 };

		// 45
		int[] jeonbook = { 110, 111, 113, 130, 140, 180, 190, 210, 710, 720, 730, 740, 750, 770, 790, 800 };

		// 46
		int[] jeonnam = { 110, 130, 150, 170, 230, 710, 720, 730, 770, 780, 790, 800, 810, 820, 830, 840, 860, 870, 880,
				890, 900, 910 };

		// 47
		int[] kyungbook = { 110, 111, 113, 130, 150, 170, 190, 210, 230, 250, 280, 290, 720, 730, 750, 760, 770, 820,
				830, 840, 850, 900, 920, 930, 940 };

		// 48
		int[] kyungnam = { 120, 121, 123, 125, 127, 129, 170, 220, 240, 250, 270, 310, 330, 720, 730, 740, 820, 840,
				850, 860, 870, 880, 890 };

		// 50
		int[] jeju = { 110, 130 };

		// int[] loc = { 11, 26, 27, 28, 29, 30, 31, 41, 42, 43, 44, 45, 46, 47, 48, 50
		// };

		// getHouseCnt(seoul[0],11);

		for (int i = 0; i < seoul.length; i++) {
			getHouseCnt(seoul[i], 11);
		}
		for (int i = 0; i < busan.length; i++) {
			getHouseCnt(busan[i], 26);
		}
		for (int i = 0; i < daegu.length; i++) {
			getHouseCnt(daegu[i], 27);
		}
		for (int i = 0; i < incheon.length; i++) {
			getHouseCnt(incheon[i], 28);
		}
		for (int i = 0; i < gwangju.length; i++) {
			getHouseCnt(gwangju[i], 29);
		}
		for (int i = 0; i < daejeon.length; i++) {
			getHouseCnt(daejeon[i], 30);
		}
		for (int i = 0; i < woolsan.length; i++) {
			getHouseCnt(woolsan[i], 31);
		}
		for (int i = 0; i < gyeonggido.length; i++) {
			getHouseCnt(gyeonggido[i], 41);
		}
		for (int i = 0; i < kangwondo.length; i++) {
			getHouseCnt(kangwondo[i], 42);
		}
		for (int i = 0; i < choongbook.length; i++) {
			getHouseCnt(choongbook[i], 43);
		}
		for (int i = 0; i < choongnam.length; i++) {
			getHouseCnt(choongnam[i], 44);
		}
		for (int i = 0; i < jeonbook.length; i++) {
			getHouseCnt(jeonbook[i], 45);
		}
		for (int i = 0; i < jeonnam.length; i++) {
			getHouseCnt(jeonnam[i], 46);
		}
		for (int i = 0; i < kyungbook.length; i++) {
			getHouseCnt(kyungbook[i], 47);
		}
		for (int i = 0; i < kyungnam.length; i++) {
			getHouseCnt(kyungnam[i], 48);
		}
		for (int i = 0; i < jeju.length; i++) {
			getHouseCnt(jeju[i], 50);
		}

		System.out.println("finish");
	}

	// 전체 주택 정보를 구해보자
	void getHouseCnt(int si, int brtc) throws IOException {

		int totalcnt = 0;

		// json가져오기 totalcount를 먼저 알아보자
		String skey = "3AiVDCxTbSRJFrcn5Qq1szauyrRoSAAgPEnAB7oOquJkfbN78KNdyfqWK%2FTE5fpxDVIkhtAKbWzd4Xp4oiTSsQ%3D%3D";
		String testurl = "https://data.myhome.go.kr/rentalHouseList?brtcCode=" + brtc + "&signguCode=" + si
				+ "&ServiceKey=" + skey + "&numOfRows=1";
		URL url = new URL(testurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		int resCode = conn.getResponseCode();

		// System.out.println(resCode);
		// System.out.println(conn.getResponseMessage());

		InputStream inputStream = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line;
		StringBuilder response = new StringBuilder();
		while ((line = bufferedReader.readLine()) != null) {
			response.append(line);
		}

		bufferedReader.close();
		conn.disconnect();

		// JSON 파싱

		JsonNode jsondata = objectMapper.readTree(response.toString());
		JsonNode hsmplist = jsondata.get("hsmpList");
		// System.out.println(hsmplist);

		if (hsmplist != null && hsmplist.size() > 0) {
			JsonNode totalCountNode = hsmplist.get(0).get("totalCount");
			System.out.println("totalCountNode: " + totalCountNode);

			if (totalCountNode != null && !totalCountNode.isNull()) {
				String temp = totalCountNode.asText();
				totalcnt = Integer.parseInt(temp);
			}
		}
		System.out.println(si + "-" + brtc + ", all cnt: " + totalcnt);

		// 각 지역마다 totalcount를 구하고 totalcount만큼...돌린다...
		if (totalcnt > 0) {
			getHouse(si, brtc, totalcnt);
		}

	}

	// 시군구 별로 나눠서 데이터 요청해야함
	void getHouse(int sgg, int brtc, int totalcnt) throws IOException {
		int retryCount = 0;
		HttpURLConnection conn = null;
		while (retryCount < 10) {
			String skey = "3AiVDCxTbSRJFrcn5Qq1szauyrRoSAAgPEnAB7oOquJkfbN78KNdyfqWK%2FTE5fpxDVIkhtAKbWzd4Xp4oiTSsQ%3D%3D";
			String testurl = "https://data.myhome.go.kr/rentalHouseList?brtcCode=" + brtc + "&signguCode=" + sgg
					+ "&ServiceKey=" + skey + "&numOfRows=" + totalcnt;

			URL url = new URL(testurl);
			/* HttpURLConnection */ conn = (HttpURLConnection) url.openConnection();

			int resCode = conn.getResponseCode();

			if (resCode == 500) {

				retryCount++;
				System.out.println(testurl);
				System.out.println("500발생 ... 재시도");
			} else
				break;
		}
		// System.out.println(resCode);
		// System.out.println(conn.getResponseMessage());

		InputStream inputStream = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line;
		StringBuilder response = new StringBuilder();
		while ((line = bufferedReader.readLine()) != null) {
			response.append(line);
		}

		bufferedReader.close();
		conn.disconnect();

		// house 정보 추출
		List<Houses> homelist = new ArrayList<>();
		JsonNode jsondata = objectMapper.readTree(response.toString());
		JsonNode hsmplist = jsondata.get("hsmpList");
		for (int i = 0; i < totalcnt; i++) {
			System.out.println(hsmplist.get(i));
			// 객체 생성

			String si = hsmplist.get(i).get("brtcNm").toString();// 광역도시
			si = si.substring(1, si.length() - 1);

			String sigungu = hsmplist.get(i).get("signguNm").toString();
			sigungu = sigungu.substring(1, sigungu.length() - 1);

			String roadaddress = hsmplist.get(i).get("rnAdres").toString();
			roadaddress = roadaddress.substring(1, roadaddress.length() - 1);

			Integer housecount;
			String housecounttemp = hsmplist.get(i).get("hshldCo").toString();
			if (housecounttemp.equals("{}"))
				housecount = 0;
			else
				housecount = Integer.parseInt(housecounttemp);

			String supplytype = hsmplist.get(i).get("suplyTyNm").toString();
			supplytype = supplytype.substring(1, supplytype.length() - 1);

			String style = hsmplist.get(i).get("styleNm").toString();
			style = style.substring(1, style.length() - 1);

			Double supplyarea;
			String supplyareatemp = hsmplist.get(i).get("suplyPrvuseAr").toString();
			if (supplyareatemp.equals("{}"))
				supplyarea = 0.0;
			else
				supplyarea = Double.parseDouble(supplyareatemp);

			Integer rent;
			String renttemp = hsmplist.get(i).get("bassRentGtn").toString();
			if (renttemp.equals("{}"))
				rent = 0;
			else
				rent = Integer.parseInt(renttemp);

			Integer monthrent;
			String monthrenttemp = hsmplist.get(i).get("bassMtRntchrg").toString();
			if (monthrenttemp.equals("{}"))
				monthrent = 0;
			else
				monthrent = Integer.parseInt(monthrenttemp);

			Houses home = Houses.builder().si(si).sigungu(sigungu).roadaddress(roadaddress).housecount(housecount)
					.supplytype(supplytype).style(style).supplyarea(supplyarea).rent(rent).monthrent(monthrent).build();

			homelist.add(home);
		}
		toDB(homelist);

	}

	void toDB(List<Houses> houselist) {
		System.out.println("here");
		houselist.forEach((home) -> {
			// System.out.println(home);
			houseRepo.save(home);
		});
	}

}
