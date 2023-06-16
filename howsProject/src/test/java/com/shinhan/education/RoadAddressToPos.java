package com.shinhan.education;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shinhan.education.entity.Houses;
import com.shinhan.education.respository.HouseRepository;

@SpringBootTest
class RoadAddressToPos {

	@Autowired
	HouseRepository houseRepo;

	//@Test
	void roadToPos() {
		// houses테이블의 도로명 주소를 받아와서 좌표로 모두 바꾼다
		List<String> roadaddresslist = houseRepo.findDistinctRoadAddresses();// 된다

		// System.out.println(roadaddresslist);

		// set에 도로 주소를 모두 저장하기

		// 각 set의 x,y좌표를 가져오기
		// houses테이블에 저장하기
		roadaddresslist.forEach((road) -> {

			// geocode api를 사용해서 도로명 주소를 좌표로 변환
			// --------------------------------------------------------------------------------------------------------
			String key = "D9237540-1867-3672-8234-301BA69F1BD8";

			String apikey = key;
			String searchType = "road";
			String searchAddr = road;

			String epsg = "epsg:4326";

			StringBuilder sb = new StringBuilder("https://api.vworld.kr/req/address");
			sb.append("?service=address");
			sb.append("&request=getCoord");
			sb.append("&format=json");
			sb.append("&crs=" + epsg);
			sb.append("&key=" + apikey);
			sb.append("&type=" + searchType);
			sb.append("&address=" + URLEncoder.encode(searchAddr, StandardCharsets.UTF_8));
			BigDecimal x = null;
			BigDecimal y = null;
			try {
				URL url = new URL(sb.toString());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

				JSONParser jspa = new JSONParser();
				JSONObject jsob = (JSONObject) jspa.parse(reader);
				JSONObject jsrs = (JSONObject) jsob.get("response");
				JSONObject jsResult = (JSONObject) jsrs.get("result");
				if (jsResult != null) {
					JSONObject jspoitn = (JSONObject) jsResult.get("point");
					x = new BigDecimal(jspoitn.get("x").toString());
					y = new BigDecimal(jspoitn.get("y").toString());
				}

			} catch (IOException | ParseException e) {
				throw new RuntimeException(e);
			}
			if (x != null) {
				final BigDecimal finalX = x;
				final BigDecimal finalY = y;
				// System.out.println(finalX);
				// System.out.println(finalY);
				// --------------------------------------------------------------------------------------------------------

				List<Houses> hlist = houseRepo.findByRoadaddress(road);// 현재 도로의 주소들
				hlist.forEach((h) -> {
					h.setXPos(finalX);// BigDecimal
					h.setYPos(finalY);// BigDecimal
					houseRepo.save(h);
				});
			}

		});

	}

	// 서울특별시 중랑구 겸재로30길 35-5
	// x좌표 : 127.084250581
	// y좌표 : 37.585908470

	// x좌표 : 129.110665740
	// y좌표 : 35.165558165

	// 부산광역시 수영구 광서로40번길 24
	// @Test
	void get() {
		List<String> roadaddresslist = houseRepo.findDistinctRoadAddresses();

	}

}
