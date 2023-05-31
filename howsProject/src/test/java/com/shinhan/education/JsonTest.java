package com.shinhan.education;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.education.respository.PanRepository;
import com.shinhan.education.vo.DetailPans;
import com.shinhan.education.vo.FileInfos;
import com.shinhan.education.vo.Pans;

@SpringBootTest
public class JsonTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	PanRepository panRepo;
	
	// 2015.05.05 -> 2015-05-05....월정보만 있는건 1일로 바꾼다
	static Date stringtodate(String stringdate) throws ParseException {
		if (stringdate == "")
			return null;
		if (stringdate.length() < 10) {
			stringdate += ".01";
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		java.util.Date utilDate = dateFormat.parse(stringdate);
		Date sqldate = new Date(utilDate.getTime());
		return sqldate;

	}

	@Test // 전체 공고문에서 page cnt정보를 반환한다.
	void getpanCnt() throws IOException, ParseException {
		System.out.println("start");
		int totalcnt = 0;

		// json가져오기
		String testurl = "https://apis.data.go.kr/B552555/lhLeaseNoticeInfo1/lhLeaseNoticeInfo1?serviceKey=7KhE1xpaLNaHgKzHB0z7B6a1K4n4IgOGFCOpN6dFixZMHI1d0CyE2TCISvehO7bkNEOt2MArA3SxrU3JPf%2BOjw%3D%3D&UPP_AIS_TP_CD=06&PG_SZ=1&PAGE=1";

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

		JsonNode jsonArray = objectMapper.readTree(response.toString());
		if (jsonArray.isArray()) {
			for (JsonNode jsonNode : jsonArray) {
				// 각각의 JSON 객체 처리
				// System.out.println(jsonNode);//전체 json출력코드
				if (jsonNode.get("dsList") != null) {
					// System.out.println("dsList발견 : " + jsonNode.get("dsList"));//dsList출력
					JsonNode temp = jsonNode.get("dsList");
					if (temp.isArray()) {
						if (temp.size() > 0) {
							JsonNode firstItem = temp.get(0);
							// System.out.println("cnt: " + firstItem.get("ALL_CNT"));
							String num = firstItem.get("ALL_CNT").toString();
							totalcnt = Integer.parseInt(num.substring(1, num.length() - 1));
						} else {
							System.out.println("dsList is empty.");
						}
					} else {
						System.out.println("dsList is not an array.");
					}
				}
				// key와 value 출력
				// printJsonNode(jsonNode);
			}
		} else {
			System.out.println("Received data is not in JSON array format.");
		}
		System.out.println("all cnt : " + totalcnt);
		// return answer;

		getPan(totalcnt);
		System.out.println("finish");
	}

	// 공고 전체
	void getPan(int cnt) throws IOException, ParseException {
		// List<String> panIdList = new ArrayList<>();
		List<Pans> panList = new ArrayList<>();
		//cnt = 10;
		String testurl = "https://apis.data.go.kr/B552555/lhLeaseNoticeInfo1/lhLeaseNoticeInfo1?serviceKey=7KhE1xpaLNaHgKzHB0z7B6a1K4n4IgOGFCOpN6dFixZMHI1d0CyE2TCISvehO7bkNEOt2MArA3SxrU3JPf%2BOjw%3D%3D&UPP_AIS_TP_CD=06&PG_SZ="
				+ cnt + "&PAGE=1";

		URL url = new URL(testurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		// int resCode = conn.getResponseCode();

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
		// dsList안에 253개의 json object가 들어있다
		JsonNode jsonArray = objectMapper.readTree(response.toString());
		if (jsonArray.isArray()) {
			for (JsonNode jsonNode : jsonArray) {
				// 각각의 JSON 객체 처리
				// System.out.println(jsonNode);//전체 json출력코드
				if (jsonNode.get("dsList") != null) {
					// System.out.println("dsList발견 : " + jsonNode.get("dsList"));//dsList출력
					JsonNode dataArray = jsonNode.get("dsList");
					if (dataArray.isArray()) {
						if (dataArray.size() > 0) {
							for (int i = 0; i < dataArray.size(); i++) {

								String panid = dataArray.get(i).get("PAN_ID").toString();// 공고 아이디
								panid = panid.substring(1, panid.length() - 1);
								// panIdList.add(panid);

								String panname = dataArray.get(i).get("PAN_NM").toString();// 공고명
								panname = panname.substring(1, panname.length() - 1);

								String location = dataArray.get(i).get("CNP_CD_NM").toString();// 지역명
								location = location.substring(1, location.length() - 1);

								String panstartdate = dataArray.get(i).get("PAN_NT_ST_DT").toString();// 공고게시일
								panstartdate = panstartdate.substring(1, panstartdate.length() - 1);
								Date sqlpanstartdate = null;
								if (panstartdate != "") {
									sqlpanstartdate = stringtodate(panstartdate);
								}

								String panenddate = dataArray.get(i).get("CLSG_DT").toString();// 공고마감일
								panenddate = panenddate.substring(1, panenddate.length() - 1);
								Date sqlpanenddate = null;
								if (panenddate != "") {
									sqlpanenddate = stringtodate(panenddate);
								}

								String panstate = dataArray.get(i).get("PAN_SS").toString();// 공고상태
								panstate = panstate.substring(1, panstate.length() - 1);

								String panurl = dataArray.get(i).get("DTL_URL").toString();// 공고상세URL
								panurl = panurl.substring(1, panurl.length() - 1);
								String happyhouse = dataArray.get(i).get("AIS_TP_CD_NM").toString();// 행복주택인지 판단.
								happyhouse = happyhouse.substring(1, happyhouse.length() - 1);
								//System.out.println("happyhouse : " + happyhouse);
								if (happyhouse.equals("행복주택")) { // 행복주택이면 나머지 정보를 찾아서 객체를 만들어서 리스트에 추가한다.
									DetailPans dp = getDetailPan(panid);
									// System.out.println(dp);
									// 공고 객체를 만든다.
									Pans tempPan = Pans.builder().panid(panid).panname(panname).location(location)
											.panstate(panstate).panurl(panurl).detailpan(dp).build();
									if (sqlpanstartdate != null)
										tempPan.setPanstartdate(sqlpanstartdate);
									if (sqlpanenddate != null)
										tempPan.setPanenddate(sqlpanenddate);
									// 추가한다.
									panList.add(tempPan);
									//System.out.println("누적 크기 : " + panList.size());
								}

							}

						} else {
							System.out.println("dsList is empty.");
						}
					} else {
						System.out.println("dsList is not an array.");
					}
				}
			}
		} else {
			System.out.println("Received data is not in JSON array format.");
		}

//		for (int i = 0; i < panList.size(); i++) {
//			System.out.println((i + 1) + "번째 pan" + panList.get(i));
//			
//		}
		
		//Map<String, String> pmap = new HashMap<>(); // HashMap 인스턴스 생성
		panList.forEach(pan->{
		//	if(pmap.containsKey(pan.getPanid())==false) {
				
				panRepo.save(pan);
		//		pmap.put(pan.getPanid(), "hi");
		//	}
		//	else {
		//		System.out.println("중복 : " + pan.getPanid());
		//	}
			
		});

		
	}

	// @Test
//	void testdp() throws IOException, ParseException {
//
//		getDetailPan("2015122300013750");
//		getDetailPan("0000060318");
//		getDetailPan("2015122300013431");
//
//	}

	// 상세공고
	DetailPans getDetailPan(String panid) throws IOException, ParseException {
		DetailPans dp = null;
		List<FileInfos> fileList = new ArrayList<>();
		
		Date ApplicationStartDate = null;// sql
		Date ApplicationEndDate = null;// sql
		Date WinnersAnnouncement = null;// sql

		Date DocStartDate = null;
		Date DocAnnouncement = null;
		Date DocEndDate = null;

		Date ContractStartDate = null;
		Date ContractEndDate = null;

		String FILETYPE = null;//
		String FILENAME = null;// 파일이 여러개라 문제가 있음
		String FILEURL = null;//

		String AREA = null;
		String ADDRESS = null;
		String DETAILADDRESS = null;
		String ADDRESSNAME = null;
		Date MOVEINDATE = null;
		String TOTALCOUNT = null;

		// json가져오기
		String testurl = "http://apis.data.go.kr/B552555/lhLeaseNoticeDtlInfo1/getLeaseNoticeDtlInfo1?serviceKey=19Gk40rL7q%2FZEZCBH36HX1Q9H20AzqV01x%2Bh6E%2F2BrCV%2FjARhSZ4b5oSxK%2B5hETeOreZ72eGj9ydEkRhb0l0xQ%3D%3D&SPL_INF_TP_CD=063&CCR_CNNT_SYS_DS_CD=03&PAN_ID="
				+ panid + "&UPP_AIS_TP_CD=06";
		// System.out.println(testurl);
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

		JsonNode jsonArray = objectMapper.readTree(response.toString());
		if (jsonArray.isArray()) {
			for (JsonNode jsonNode : jsonArray) {
				// 각각의 JSON 객체 처리
				// System.out.println(jsonNode);// 전체 json출력코드

				// dsSplScdl
				if (jsonNode.get("dsSplScdl") != null) {
					// System.out.println("dsSplScdl" + " 발견 : " + jsonNode.get("dsSplScdl"));//
					// dsList출력
					JsonNode dsSplScdlArry = jsonNode.get("dsSplScdl");
					if (dsSplScdlArry.isArray()) {
						if (dsSplScdlArry.size() > 0) {
							JsonNode firstItem = dsSplScdlArry.get(0);
							String datetemp = "";

							datetemp = firstItem.get("SBSC_ACP_ST_DT").toString();
							datetemp = datetemp.substring(1, datetemp.length() - 1);
							ApplicationStartDate = stringtodate(datetemp);// 접수기간시작일

							datetemp = firstItem.get("SBSC_ACP_CLSG_DT").toString();
							datetemp = datetemp.substring(1, datetemp.length() - 1);
							ApplicationEndDate = stringtodate(datetemp);// 접수기간종료일

							datetemp = firstItem.get("PZWR_ANC_DT").toString();
							datetemp = datetemp.substring(1, datetemp.length() - 1);
							WinnersAnnouncement = stringtodate(datetemp);// 접수기간종료일

							datetemp = firstItem.get("PPR_ACP_ST_DT").toString();
							datetemp = datetemp.substring(1, datetemp.length() - 1);
							DocStartDate = stringtodate(datetemp);// 서류접수기간시작일

							datetemp = firstItem.get("PPR_SBM_OPE_ANC_DT").toString();
							datetemp = datetemp.substring(1, datetemp.length() - 1);
							DocAnnouncement = stringtodate(datetemp);// 서류제출대상자발표일

							datetemp = firstItem.get("PPR_ACP_CLSG_DT").toString();
							datetemp = datetemp.substring(1, datetemp.length() - 1);
							DocEndDate = stringtodate(datetemp);// 서류접수기간종료일

							datetemp = firstItem.get("CTRT_ST_DT").toString();
							datetemp = datetemp.substring(1, datetemp.length() - 1);
							ContractStartDate = stringtodate(datetemp);// 계약기간시작일

							datetemp = firstItem.get("CTRT_ED_DT").toString();
							datetemp = datetemp.substring(1, datetemp.length() - 1);
							ContractEndDate = stringtodate(datetemp);// 계약기간종료일

						} else {
							System.out.println("dsSplScdl is empty.");
						}
					} else {
						System.out.println("dsSplScdl is not an array.");
					}

				}
				// dsAhflInfo
				/*
				 * 
				 * 
				 * 파일 테이블 따로 빼기
				 * 
				 * 
				 */
				if (jsonNode.get("dsAhflInfo") != null) {
					// System.out.println("dsAhflInfo" + " 발견 : " + jsonNode.get("dsAhflInfo"));//
					// dsList출력
					JsonNode dsAhflInfoArry = jsonNode.get("dsAhflInfo");
					// System.out.println("파일관련 : " + dsAhflInfoArry);
					if (dsAhflInfoArry.isArray()) {
						if (dsAhflInfoArry.size() > 0) {
							for (int i = 0; i < dsAhflInfoArry.size(); i++) {
								JsonNode firstItem = dsAhflInfoArry.get(i);// 반복문

								FILETYPE = firstItem.get("SL_PAN_AHFL_DS_CD_NM").toString();// 파일구분명
								FILENAME = firstItem.get("CMN_AHFL_NM").toString();// 첨부파일명
								FILEURL = firstItem.get("AHFL_URL").toString();// 다운로드
								FileInfos fi = FileInfos.builder().filename(FILENAME).filetype(FILETYPE).fileurl(FILEURL).build();
								fileList.add(fi);
							}

						} else {
							System.out.println("dsSplScdl is empty.");
						}
					} else {
						System.out.println("dsSplScdl is not an array.");
					}

				}

				// dsAhflInfo
				if (jsonNode.get("dsSbd") != null) {
					// System.out.println("dsSbd" + " 발견 : " + jsonNode.get("dsSbd"));// dsList출력
					JsonNode dsSbdArry = jsonNode.get("dsSbd");
					// System.out.println("단저정보관련 : " + dsSbdArry);
					if (dsSbdArry.isArray()) {
						if (dsSbdArry.size() > 0) {
							JsonNode firstItem = dsSbdArry.get(0);// 반복문

							AREA = firstItem.get("DDO_AR").toString();// 전용면적
							ADDRESS = firstItem.get("LGDN_ADR").toString();// 단지주소
							DETAILADDRESS = firstItem.get("LGDN_DTL_ADR").toString();// 단지상세주소
							ADDRESSNAME = firstItem.get("LCC_NT_NM").toString();// 단지명
							String datetemp = "";
							datetemp = firstItem.get("MVIN_XPC_YM").toString();
							datetemp = datetemp.substring(1, datetemp.length() - 1);
							MOVEINDATE = stringtodate(datetemp);// 입주예정월
							TOTALCOUNT = firstItem.get("HSH_CNT").toString();// 총세대수

						} else {
							System.out.println("dsSplScdl is empty.");
						}
					} else {
						System.out.println("dsSplScdl is not an array.");
					}

				}

				dp = DetailPans.builder().panid(panid).applicationstartdate(ApplicationStartDate)
						.applicationenddate(ApplicationEndDate).winnersannouncement(WinnersAnnouncement)
						.docstartdate(DocStartDate).docannouncement(DocAnnouncement).docenddate(DocEndDate)
						.contractstartdate(ContractStartDate).contractenddate(ContractEndDate).area(AREA)
						.address(ADDRESS).detailaddress(DETAILADDRESS).addressname(ADDRESSNAME).moveindate(MOVEINDATE)
						.totalcount(TOTALCOUNT).fileinfolist(fileList).build();
			}
		} else {
			System.out.println("Received data is not in JSON array format.");
		}

		return dp;
	}

}
