package com.shinhan.education.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.shinhan.education.entity.ChatInfo;
import com.shinhan.education.entity.ChatRoom;
import com.shinhan.education.entity.DetailPans;
import com.shinhan.education.entity.Houses;
import com.shinhan.education.entity.Loans;
import com.shinhan.education.entity.MemberLoans;
import com.shinhan.education.entity.Members;
import com.shinhan.education.entity.Nice;
import com.shinhan.education.entity.PanFavorites;
import com.shinhan.education.entity.PanFavoritesId;
import com.shinhan.education.entity.Pans;
import com.shinhan.education.respository.ChatInfoRepository;
import com.shinhan.education.respository.ChatRoomRepository;
import com.shinhan.education.respository.DetailPanRepository;
import com.shinhan.education.respository.HanaRepository;
import com.shinhan.education.respository.HouseRepository;
import com.shinhan.education.respository.KookminRepository;
import com.shinhan.education.respository.LoansRepository;
import com.shinhan.education.respository.MemberLoanRepository;
import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.respository.NiceRepository;
import com.shinhan.education.respository.PanFavRepository;
import com.shinhan.education.respository.PanRepository;
import com.shinhan.education.respository.ShinhanRepository;
import com.shinhan.education.respository.WooriRepository;
import com.shinhan.education.vo.Payload;
import com.shinhan.education.vo.RequestVO;
import com.shinhan.education.vo.XYResult;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin // (origins = "3000")
@RestController
@RequestMapping("/hows")
@Slf4j
/* 이자율계산기 : https://wildeveloperetrain.tistory.com/118 */

public class HowsController {
	@Autowired
	PanRepository panRepo;

	@Autowired
	PanFavRepository favRepo;

	@Autowired
	DetailPanRepository dpRepo;

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
	MemberLoanRepository memloanRepo;

	@Autowired
	MemberRepository memRepo;

	@Autowired
	ChatRoomRepository roomRepo;
	@Autowired
	ChatInfoRepository chatinfoRepo;

	@Autowired
	NiceRepository niceRepo;

	@Autowired
	HouseRepository houseRepo;

	// 버킷 이름 동적 할당
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	// 버킷 주소 동적 할당
	@Value("${cloud.aws.s3.bucket.url}")
	private String defaultUrl;

	// private final AmazonS3Client amazonS3Client = new AmazonS3Client(new
	// BasicAWSCredentials("${cloud.aws.credentials.accessKey}",
	// "${cloud.aws.credentials.secretKey}"));
	private final AmazonS3Client amazonS3Client;

	@Autowired
	public HowsController(AmazonS3Client amazonS3Client) {
		this.amazonS3Client = amazonS3Client;
	}

//	public HowsController() {
//		
//		
//		super();
//		this.credentials = null;
//		//this.credentials =
//		//this.amazonS3Client  = 
//		this.amazonS3Client = null;
//		
//	}

	String getMemberId(String token) {
		// String token =
		// "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJja2RydWExIiwicm9sZXMiOlsiVXNlciJdLCJpYXQiOjE2ODYxODkwNzIsImV4cCI6MTY4NjI3NTQ3Mn0.BuOvMeMhLfIlwMZcGioJbSbxtJnEKE5aWwAj1ntaCPE";
		String[] chunks = token.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();

		String payloadJson = new String(decoder.decode(chunks[1]));

		Gson gson = new Gson();

		Payload payload = gson.fromJson(payloadJson, Payload.class);
		String memberid = payload.getSub();
		System.out.println("memberid : " + memberid);
		return memberid;
	}

	@GetMapping("/notice") // 공고 조회
	public RequestVO<List<Pans>> allpans(HttpServletRequest request) {
		System.err.println("요청 들어옴");
		// PageRequest.of(int page, int size, sort)
		// page : 요청하는 페이지 번호
		// size : 한 페이지 당 조회할 크기 (기본값 : 20)
		// sort : Sorting 설정 (기본값 : 오름차순)
		String token = request.getHeader("token");
		System.out.println("token : " + token);
		int page = Integer.parseInt(request.getParameter("page"));
		int size = Integer.parseInt(request.getParameter("size"));
		// String token = null;
		System.out.println("page : " + page);
		System.out.println("size : " + size);

		Pageable pageable = PageRequest.of(page, size, Sort.by("panstartdate").ascending());
		Page<Pans> panPage = panRepo.findAllByOrderByPanstartdateDesc(pageable);
		List<Pans> panList = panPage.getContent();

		if (token != null) {// 로그인 된 상태 -> 좋아요 확인하자
			String memberid = getMemberId(token);
			System.out.println("memberid : " + memberid);
			panList.forEach((pan) -> {
				PanFavoritesId id = new PanFavoritesId(pan.getPanid(), memberid);
				Optional<PanFavorites> favorite = (Optional<PanFavorites>) favRepo.findById(id);
				if (favorite.isPresent()) {
					pan.setLike(1);
				}
				// System.out.println(pan);
			});
		}
		RequestVO<List<Pans>> r = new RequestVO<List<Pans>>();
		int total = panRepo.countAllPans();
		r.setObj(panList);
		r.setTotal(total);
		panList.forEach((x) -> {
			System.out.println(x);
		});
		return r;
	}

	@PostMapping("/notice") // 공고 좋아요
	public String addLike(HttpServletRequest request, @RequestBody String requestBody) {
		System.out.println("요청 URL: " + request.getRequestURL().toString());
		System.err.println("좋아요요청 들어옴");
		System.out.println(requestBody);
		System.out.println(request.toString());
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode;
		String panid = null;
		try {
			jsonNode = objectMapper.readTree(requestBody);
			panid = jsonNode.get("panid").asText();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// String panid = requestBody;
		String token = request.getHeader("token");
		System.out.println("panid : " + panid);
		if (token == null) {
			System.out.println("좋아요 실패 토큰 없음");
			return "좋아요 실패 토큰 없음";
		}

		String memberId = getMemberId(token);

		PanFavoritesId id = new PanFavoritesId(panid, memberId);

		Pans pan = panRepo.findById(panid).orElse(null);
		if (pan == null)
			return "fail";
		PanFavorites panfav = PanFavorites.builder().panid(pan.getPanid()).memberid(memberId).title(pan.getPanname())
				.start(pan.getPanstartdate()).end(pan.getPanenddate()).build();
		favRepo.save(panfav);
		System.out.println("좋아요 성공");
		return "like success";
	}

	@DeleteMapping("/notice") // 공고 좋아요 취소
	public String deletelike(HttpServletRequest request) {
		System.err.println("좋아요취소요청 들어옴");
		String token = request.getHeader("token");
		String panid = request.getParameter("panid");
		System.out.println(panid);
//		String panid = null;
//		ObjectMapper objectMapper = new ObjectMapper();
//		JsonNode jsonNode;
//		
//		
//		try {
//			jsonNode = objectMapper.readTree(requestBody);
//			 panid = jsonNode.get("panid").asText();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		

		String memberId = getMemberId(token);
		PanFavoritesId id = new PanFavoritesId(panid, memberId);
		favRepo.findById(id).ifPresent(panFavorites -> {
			favRepo.deleteById(id);
		});
		return "like delete success";
	}

	// 지역별 공고 가져오기
	@GetMapping(value = "/notice/{location}", produces = "application/json;charset=UTF-8")
	public RequestVO<List<Pans>> selectByLocation(@PathVariable String location, HttpServletRequest request) {
		System.err.println(location + "지역별 공고 가져오기 요청 들어옴");

		int page = Integer.parseInt(request.getParameter("page"));
		int size = Integer.parseInt(request.getParameter("size"));

		Pageable pageable = PageRequest.of(page, size, Sort.by("panstartdate").ascending());
		Page<Pans> loclist = panRepo.findByLocationContainingOrderByPanstartdateDesc(location, pageable);

		RequestVO<List<Pans>> r = new RequestVO<List<Pans>>();

		List<Pans> panList = loclist.getContent();
		panList.forEach((x) -> {
			System.out.println(x);
		});
		r.setObj(panList);
		int total = (int) loclist.getTotalElements();
		r.setTotal(total);
		System.out.println("R : " + r);
		return r;

	}

	// 공고 상세 조회
	@GetMapping(value = "/notice/detail")
	public DetailPans selectbyid(String panid) {
		System.err.println("공고 상세 요청 들어옴");
		Pans pan = panRepo.findById(panid).get();
		// System.out.println("pan" + pan );
		DetailPans dp = dpRepo.findByPan(pan);// 객체라서 문제 발생
		// System.out.println("detail pan : " + dp);
		return dp;
	}

	@GetMapping(value = "/notice/fav/{num}") // 관심 공고 조회 & 모집 중인 공고 조회
	public RequestVO<List<Pans>> selectByFav(@PathVariable int num, HttpServletRequest request) {
		System.err.println("관심 공고 조회 & 모집 중인 공고 조회 요청 들어옴");
		List<Pans> panList = new ArrayList<>();
		int page = Integer.parseInt(request.getParameter("page"));
		int size = Integer.parseInt(request.getParameter("size"));
		String token = request.getHeader("token");
		String memberid = getMemberId(token);
		System.out.println("notice/fav 요청 들어옴");
		System.out.println("page : " + page);
		System.out.println("size : " + size);
		Pageable pageable = PageRequest.of(page, size, Sort.by("panstartdate").ascending());
		Page<Pans> panPage = null;
		if (num == 1) {
			System.err.println("1. 관심공고");
			panPage = panRepo.findPansByMemberId(memberid, pageable);// 수정->fav
			panPage.forEach((pan) -> {
				pan.setLike(1);
			});
		}
		if (num == 2) {
			System.err.println("2. 모집중");
			panPage = panRepo.findByPanstate("접수마감", pageable);
//			panlist.addall(panRepo.findByPanstate("접수중", pageable));
//			panlist.addall(panRepo.findByPanstate("정정공고중", pageable));
		}
		if (num == 3) {
			System.err.println("3. 관심 + 모집공고");
			panPage = panRepo.findPansByMemberIdAndPanstate(memberid, "접수마감", pageable);
			panPage.forEach((pan) -> {
				pan.setLike(1);
			});
		}
		// System.out.println(panList);

		RequestVO<List<Pans>> r = new RequestVO<List<Pans>>();
		long total = panPage.getTotalElements();
		panList = panPage.getContent();
		System.err.println("**" + panList);
		r.setObj(panList);
		r.setTotal((int) total);
		System.out.println("panList");
		panList.forEach((x) -> {
			System.out.println(x);
		});
		System.out.println("totalcount : " + total);
		return r;
	}

	// 주택 위치를 검색
	// ex) { houseaddress : “서울시 마포구” }
	// { 도로명 주소from houses }
	// ?
	@GetMapping(value = "/find")
	public Map<String, XYResult> selectByAddress(HttpServletRequest request) {
		// String houseaddress = "서울특별시 중랑구 겸재로30길 35-5";
		System.out.println("find요청 들어옴");
//		String houseaddress = request.getParameter("houseaddress");
		// System.out.println("주소 : " + houseaddress);
		// if (houseaddress != null) {
		// List<Houses> hlist = houseRepo.findXY(houseaddress);
		// BigDecimal x = hlist.get(0).getXPos();
		// BigDecimal y = hlist.get(0).getYPos();
		// System.out.println(x + " " + y);

		String string_x = request.getParameter("x").toString();
		String string_y = request.getParameter("y").toString();

		System.out.println(string_x + " " + string_y);
		BigDecimal x = new BigDecimal(string_x);
		BigDecimal y = new BigDecimal(string_y);

		String num = "0.00500000";
		BigDecimal x1 = x.add(new BigDecimal(num));
		BigDecimal x2 = x.subtract(new BigDecimal(num));
		BigDecimal y1 = y.add(new BigDecimal(num));
		BigDecimal y2 = y.subtract(new BigDecimal(num));
		System.out.println(x1 + " " + x2 + " " + y1 + " " + y2);
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

		return xyMap;
		// } else
		// return null;
	}

	// 대출 전체 목록
	@GetMapping(value = "/loan")
	public RequestVO<List<Loans>> loanlist(HttpServletRequest request) {
		int page = Integer.parseInt(request.getParameter("page"));
		int size = Integer.parseInt(request.getParameter("size"));

		String bankname = request.getParameter("bankname");

		RequestVO<List<Loans>> r = new RequestVO<List<Loans>>();
		System.err.println("대출 " + bankname + " 목록 요청 들어옴");
		Pageable pageable = PageRequest.of(page, size, Sort.by("loanname").ascending());
		Page<Loans> loanlist = null;
		if (bankname.equals("전체")) {
			loanlist = loanRepo.findAll(pageable);

		} else {
			loanlist = loanRepo.findByBankname(bankname, pageable);// ->은행별로바꾸기 전체, 신한 국민 우리 하나

		}

		List<Loans> loans = loanlist.getContent();
		int total = (int) loanlist.getTotalElements();
		r.setObj(loans);
		r.setTotal(total);
		return r;
	}

	// 상세 대출 html -> 대출 상세보기했을 때 룸생성안하고 상세 정보만 보여주기
	@GetMapping(value = { "/loan/detail" })
	public <T> T detailhtml(HttpServletRequest request) {
		System.err.println("/loan/detail html 요청 들어옴");
		String bankname = request.getParameter("bankname");
		String loanname = request.getParameter("loanname");// 변수 이름 확인
		System.err.println("bank : " + bankname);
		System.err.println("loan : " + loanname);
		if (bankname.equals("신한")) {
			System.out.println(shinhanRepo.findById(loanname));
			return (T) shinhanRepo.findById(loanname);
		} else if (bankname.equals("국민")) {
			System.out.println(kookminRepo.findById(loanname));
			return (T) kookminRepo.findById(loanname);
		} else if (bankname.equals("하나")) {
			System.out.println(hanaRepo.findById(loanname));
			return (T) hanaRepo.findById(loanname);
		} else if (bankname.equals("우리")) {
			System.out.println(wooriRepo.findById(loanname));
			return (T) wooriRepo.findById(loanname);
		} else
			return null;
	}

	// 한도조회
	@GetMapping(value = "/loan/detail/limit")
	public Map<String, Object> getlimit(HttpServletRequest request) {
		Map<String, Object> mp = new HashMap<>();
		System.out.println("한도조회 요청들어옴");

		String jumin = request.getParameter("jumin");
		String price = request.getParameter("price");
		System.out.println(price);
		if (jumin != null && price != null) {
			Integer iprice = Integer.parseInt(price);
			jumin = jumin.replace("-", "");
			System.out.println(jumin);
			Nice niceinfo = niceRepo.findByJumin(jumin);
			if (niceinfo != null) {
				System.out.println("나이스 회워 발견");
				Integer score = niceinfo.getScore();
				String maxloan = "0원";
				if (score >= 900) {
					// price활용해서 계산하기
					iprice = (int) Math.round(iprice * 1.0);
					String formattedPrice = String.format("%,d", iprice);
					maxloan = formattedPrice;
				} else if (score >= 800) {
					iprice = (int) Math.round(iprice * 0.9);
					String formattedPrice = String.format("%,d", iprice);
					maxloan = formattedPrice;
				} else if (score >= 700) {
					iprice = (int) Math.round(iprice * 0.8);
					String formattedPrice = String.format("%,d", iprice);
					maxloan = formattedPrice;
				} else if (score >= 500) {
					iprice = (int) Math.round(iprice * 0.7);
					String formattedPrice = String.format("%,d", iprice);
					maxloan = formattedPrice;
				} else if (score >= 400) {
					iprice = (int) Math.round(iprice * 0.6);
					String formattedPrice = String.format("%,d", iprice);
					maxloan = formattedPrice;
				} else if (score >= 300) {
					iprice = (int) Math.round(iprice * 0.5);
					String formattedPrice = String.format("%,d", iprice);
					maxloan = formattedPrice;
				} else if (score >= 200) {
					iprice = (int) Math.round(iprice * 0.4);
					String formattedPrice = String.format("%,d", iprice);
					maxloan = formattedPrice;
				} else if (score >= 100) {
					iprice = (int) Math.round(iprice * 0.3);
					String formattedPrice = String.format("%,d", iprice);
					maxloan = formattedPrice;
				} else {
					maxloan = "신용불량";
				}
				mp.put("maxloan", maxloan);
				mp.put("myname", niceinfo.getName());
			}
			return mp;
		} else {
			mp.put("maxloan", "한도조회ERROR");
			return null;
		}
	}

	@GetMapping("/loan/verification")
	public String verify(HttpServletRequest request) {
		//
		System.out.println("본인인증 요청 들어옴");
		String jumin = request.getParameter("jumin");
		System.out.println("jumin : " + jumin);

		jumin = jumin.replace("-", "");
		String name = request.getParameter("name");
		System.out.println("name : " + name);

		String token = request.getHeader("token");
		System.out.println("token : " + token);

		String memberid = getMemberId(token);

		Members mem = memRepo.findByMemberid(memberid).get();

		Nice nice = niceRepo.findByJumin(jumin);

		if (nice != null) {
			// System.out.println("a");
			if (nice.getName().equals(name)) {
				// System.out.println("k");
				if (mem.getMembername().equals(name)) {
					System.out.println("성공");
					return "1";
				}
			}
		}
		System.out.println("실패");
		return "0";
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	// roomnumber

	// 채팅

	// 관리자가 채팅룸 입장하는 부분
	// 과거 대화내용을 보내야한다.
	@GetMapping(value = { "/admin/consult/chartoom" })
	public <T> T adminEnterChatRoom(HttpServletRequest request) {
		System.out.println("관리자 채팅 입장 요청 들어옴");

		String roomnumber = request.getParameter("roomnumber");
		ChatRoom room = roomRepo.findById(Long.parseLong(roomnumber)).get();
		List<ChatInfo> chatlist = chatinfoRepo.findByChatroomOrderByTime(room);

		return (T) chatlist;
	}

	// 회원이 상담신청을 통해 채팅을 처음 들어오는 부분
	// 상세 대출 html 상담용
	// 대출 상담 신청 들어옴->memloan생성
	// 없으면 채팅룸 생성-> 있으면 룸번호 반환
	@GetMapping("/loan/detail/consult")
	public <T> T userCreateRoom(HttpServletRequest request) {
		System.err.println("상세 대출상담 요청 들어옴");
		String bankname = request.getParameter("bankname");
		String loanname = request.getParameter("loanname");// 변수 이름 확인ㅌ
		String token = request.getHeader("token");
		String loanid = request.getParameter("loanid");
		// System.out.println("loanid : " + loanid);
		System.out.println("token : " + token);
		String memberid = getMemberId(token);
		Loans loan = loanRepo.findById(loanname).get();
		Members mem = memRepo.findById(memberid).get();

		// 이미 관련 톡방이 있는지 확인
		// 없으면 새로 룸 생성
		Map<String, Object> map = new HashMap<>();
		List<MemberLoans> listml = memloanRepo.findByMemberidAndLoanname(mem, loan);

		// System.out.println("role : " + mem.getRoles());
		// String memrole = mem.getRoles().toString();
		if (!(mem.getRoles().toString().equals("ADMIN"))) {// 관리자가 아닌경우

			if (listml.size() == 0) {// 방생성...처음 대출을 한다
				System.out.println("1");
				MemberLoans ml = MemberLoans.builder().memberid(mem).loanname(loan).loanstate("상담신청").build();
				ml = memloanRepo.save(ml);// 새로운 대출신청을 만들고
				ml.getMemloanid();
				ChatRoom room = ChatRoom.builder().memloanid(ml.getMemloanid()).build();// 룸생성
				room = roomRepo.save(room);// db에 룸저장
				ml.setRoomnumber(room.getRoomId());// 해당 memloan에다가 룸번호 저장
				memloanRepo.save(ml);// db에 저장
				map.put("message", "상담 신청 완료, 상담사와 채팅을 연결합니다.");
				map.put("room", room.getRoomId());
				map.put("myname", mem.getMemberid());
				return (T) map;
			} else {// 방이 이미 있으면 이전 대출을 재활용한다
				System.out.println("2");
				MemberLoans ml = listml.get(0);

				ChatRoom room = roomRepo.findByMemloanid(ml.getMemloanid());
				map.put("message", "상담 신청 내역이 있습니다, 이전 채팅방에 입장합니다.");
				map.put("room", room.getRoomId());
				map.put("myname", mem.getMemberid());
				map.put("chathistory", chatinfoRepo.findByChatroomOrderByTime(room));
				return (T) map;
			}
		} else {// 관리자인경우
			System.err.println("3");
			ChatRoom room = roomRepo.findByMemloanid(Integer.parseInt(loanid));
			map.put("message", "관리자입니다, 이전 채팅방에 입장합니다.");
			map.put("room", room.getRoomId());
			map.put("myname", mem.getMemberid());
			map.put("chathistory", chatinfoRepo.findByChatroomOrderByTime(room));
			return (T) map;
		}
	}

	// 회원이 채팅을 입장하는부분(재입장) 마이페이지에서..
	@GetMapping(value = { "/user/consult/chatroom" })
	public <T> T userEnterChatRoom(HttpServletRequest request) {
		System.out.println("회원 채팅 입장 요청 들어옴");

		String roomnumber = request.getParameter("roomnumber");
		ChatRoom room = roomRepo.findById(Long.parseLong(roomnumber)).get();
		List<ChatInfo> chatlist = chatinfoRepo.findByChatroomOrderByTime(room);

		return (T) chatlist;
	}

	// -------------------------------------------------------------------------------------------------------------------------------------

	// member loans 테이블에 서류 제출
	@PostMapping("/loan/detail/limit/uploaddocs")
	public String insertDocs(HttpServletRequest request, @RequestPart(value = "contentsData") MemberLoans memloanVO) {
		System.out.println("서류 제출 요청 들어옴");
		String token = request.getParameter("token");
		String memberid = getMemberId(token);
		String loanname = request.getParameter("loan");
		String loanstate = "";
		String applyurl = request.getParameter("applyurl");

		Members mem = memRepo.findById(memberid).get();
		Loans loan = loanRepo.findById(loanname).get();
		memloanVO.setMemberid(mem);
		memloanVO.setLoanname(loan);

		memloanRepo.save(memloanVO);

		return "완료";
	}

	// 공고 즐겨찾기 목록
	@GetMapping(value = "/my/mypage/pan")
	public List<PanFavorites> mypagefav(HttpServletRequest request) {
		System.err.println("마이페이지 공고 즐겨찾기 목록 요청 들어옴");

		List<PanFavorites> panfavList = new ArrayList<>();
		// int page = Integer.parseInt(request.getParameter("page"));
		// int size = Integer.parseInt(request.getParameter("size"));
		String token = request.getHeader("token");
		System.out.println("token : " + token);
		String memberid = getMemberId(token);

		System.out.println("mypage 요청 들어옴");
		// System.out.println("page : " + page);
		// System.out.println("size : " + size);
		// Pageable pageable = PageRequest.of(page, size,
		// Sort.by("panstartdate").ascending());

		panfavList = favRepo.findByMemberidOrderByStart(memberid);
		panfavList.forEach((fav) -> {
			String panname = fav.getPans().getPanname();
			fav.setPanname(panname);
		});
		System.out.println("pan: " + panfavList);
		panfavList.forEach((x) -> {
			System.out.println(x);
		});
		return panfavList;
	}

	// 마이페이지 대출목록
	@GetMapping(value = "/my/mypage/loan")
	public List<List<Map<String, Object>>> mypageloan(HttpServletRequest request) {
		System.err.println("마이페이지 대출목록 요청 들어옴");
		String token = request.getHeader("token");
		String memberid = getMemberId(token);

		System.out.println("mypage 요청 들어옴");
		Members mem = memRepo.findById(memberid).get();
		List<MemberLoans> mllist = memloanRepo.findByMemberid(mem);
		System.out.println("대출목록 : ");

		List<Map<String, Object>> listA = new ArrayList<>();
		List<Map<String, Object>> listB = new ArrayList<>();

		mllist.forEach((x) -> {

			Map<String, Object> mapA = new HashMap<>();
			Map<String, Object> mapB = new HashMap<>();
			mapA.put("loanname", x.getLoanname().getLoanname());
			mapA.put("bankname", x.getLoanname().getBankname());
			mapA.put("loanstate", x.getLoanstate());
			mapA.put("applyurl", x.getApplyurl());
			mapA.put("memloanid", x.getMemloanid());

			mapB.put("loanname", x.getLoanname().getLoanname());
			mapB.put("bankname", x.getLoanname().getBankname());
			mapB.put("loanstate", x.getLoanstate());
			mapB.put("room", x.getRoomnumber());

			listA.add(mapA);
			listB.add(mapB);

			System.out.println(x);
		});

		List<List<Map<String, Object>>> listC = new ArrayList();

		listC.add(listA);
		listC.add(listB);

		// 맵두개를 하나로 묶어서 보낸다
		// 1.List 대출상품이름loanname, 은행이름bankname, 진행상태loanstate, 신청링크applyurl
		// 2.List bankname, room
		System.err.println(listC);
		return listC;

	}

//-----------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/admin/consult") // 관리자와 회원의 상담목록//room번호도 같이 보내줘야한다.
	public List<Map<String, Object>> consultlist(HttpServletRequest request) {
		System.err.println("관리자와 회원의 상담목록");
		Integer page = Integer.parseInt(request.getParameter("page"));
		Integer size = Integer.parseInt(request.getParameter("size"));
		Pageable pageable = PageRequest.of(page, size);

		List<Map<String, Object>> objList = new ArrayList<>();
		Page<MemberLoans> mllist2 = memloanRepo.findAll(pageable);
		Long total = mllist2.getTotalElements();
		List<MemberLoans> mllist = mllist2.getContent();
		System.out.println("mllist : " + mllist);
		mllist.forEach((mem) -> {
			Map<String, Object> obj = new HashMap<>();
			obj.put("loanid", mem.getMemloanid());
			obj.put("memberid", mem.getMemberid());
			obj.put("membername", mem.getMemberid().getMembername());
			obj.put("bankname", mem.getLoanname().getBankname());
			obj.put("loanname", mem.getLoanname());
			obj.put("loanstate", mem.getLoanstate());
			obj.put("total", total);
			obj.put("roomnumber", mem.getRoomnumber());// 방번호
			objList.add(obj);
		});

		objList.sort(Comparator.comparing(m -> m.get("membername").toString()));
		// System.out.println(objList);
		System.out.println("return map : " + objList);
		return objList;
	}

//-----------------------------------------------------------------------------------------------------------------------------------------
	@GetMapping("/admin/form") /*
								 * 관리자가 회원의 서류 제출 내역 목록 확인하기 위한 회원 리스트
								 * 
								 * 대출심사대기중인 회원의 목록을 보여준다
								 */
	public List<Map<String, Object>> getMemberDocs(HttpServletRequest request) {
		// Integer memloanid = Integer.parseInt(request.getParameter("memloanid"));
		System.err.println("admin/form 대출심사대기중인 목록");
		Integer page = Integer.parseInt(request.getParameter("page"));
		Integer size = Integer.parseInt(request.getParameter("size"));

		Pageable pageable = PageRequest.of(page, size);
		Page<MemberLoans> mllist2 = memloanRepo.findByState("대출심사대기", pageable);// 대출중인 회원을 찾자
		Long total = mllist2.getTotalElements();
		List<MemberLoans> mllist = mllist2.getContent();
		List<Map<String, Object>> objList = new ArrayList<>();
		mllist.forEach((mem) -> {
			Map<String, Object> obj = new HashMap<>();
			obj.put("loanid", mem.getMemloanid());
			obj.put("memberid", mem.getMemberid());
			obj.put("membername", mem.getMemberid().getMembername());
			obj.put("bankname", mem.getLoanname().getBankname());
			obj.put("loanname", mem.getLoanname());
			obj.put("loanstate", mem.getLoanstate());
			obj.put("total", total);

			objList.add(obj);
		});
		System.out.println(objList);
		return objList;

	}

	@GetMapping("/admin/form/checklist") // 회원 한명당 상세 제출 내역 확인
	public Map<String, Object> getform(HttpServletRequest request) {
		Map<String, Object> objlist = new HashMap<>();
		String loanid = request.getParameter("loanid");

		MemberLoans ml = memloanRepo.findById(Integer.parseInt(loanid)).get();
		objlist.put("loanid", ml.getMemloanid());
		objlist.put("membername", ml.getMemberid().getMembername());
		objlist.put("bankname", ml.getLoanname().getBankname());
		objlist.put("loanname", ml.getLoanname());
		objlist.put("loanstate", ml.getLoanstate());

		objlist.put("leaseContract", ml.getLeaseContract());
		objlist.put("propertyRegistration", ml.getPropertyRegistration());
		// objlist.put("depositReceipt", ml.getDepositReceipt());
		objlist.put("residenceRegistration", ml.getResidenceRegistration());
		// objlist.put("identificationCard", ml.getIdentificationCard());
		// objlist.put("incomeProof", ml.getIncomeProof());
		objlist.put("marriageProof", ml.getMarriageProof());
		objlist.put("employmentProof", ml.getEmploymentProof());
		// objlist.put("businessProof", ml.getBusinessProof());
		// objlist.put("interestLimitDocuments", ml.getInterestLimitDocuments());

		return objlist;
		// bankname이 전달되나??

	}

	@PutMapping("/admin/form/checklist") // 대출승인대기로 바꾼다
	public String formchecked(HttpServletRequest request) {
		String loanid = request.getParameter("loanid");
		MemberLoans ml = memloanRepo.findById(Integer.parseInt(loanid)).get();
		ml.setLoanstate("대출승인대기");
		memloanRepo.save(ml);
		return "대출심사완료!!";
	}

	@GetMapping("/bank/form") // 대출승인대기중인 목록을 가져온다
	public List<Map<String, Object>> getMemberDocs2(HttpServletRequest request) {
		System.out.println("bank/form");
		// Integer memloanid = Integer.parseInt(request.getParameter("memloanid"));
		Integer page = Integer.parseInt(request.getParameter("page"));
		Integer size = Integer.parseInt(request.getParameter("size"));

		Pageable pageable = PageRequest.of(page, size);
		Page<MemberLoans> mllist2 = memloanRepo.findByState("대출승인대기", pageable);// 대출중인 회원을 찾자
		Long total = mllist2.getTotalElements();
		List<MemberLoans> mllist = mllist2.getContent();
		List<Map<String, Object>> objList = new ArrayList<>();
		mllist.forEach((mem) -> {
			Map<String, Object> obj = new HashMap<>();
			obj.put("loanid", mem.getMemloanid());
			obj.put("memberid", mem.getMemberid());
			obj.put("membername", mem.getMemberid().getMembername());
			obj.put("bankname", mem.getLoanname().getBankname());
			obj.put("loanname", mem.getLoanname());
			obj.put("loanstate", mem.getLoanstate());
			obj.put("total", total);

			objList.add(obj);
		});
		System.err.println(objList);
		return objList;
	}

	@GetMapping("/bank/loanlist/detail") // 은행원이 한명의 회원의 제출된 서류 목록을 가져온다
	public Map<String, Object> getform2(HttpServletRequest request) {
		Map<String, Object> objlist = new HashMap<>();
		String loanid = request.getParameter("loanid");

		MemberLoans ml = memloanRepo.findById(Integer.parseInt(loanid)).get();
		objlist.put("loanid", ml.getMemloanid());
		objlist.put("membername", ml.getMemberid().getMembername());
		objlist.put("bankname", ml.getLoanname().getBankname());
		objlist.put("loanname", ml.getLoanname());
		objlist.put("loanstate", ml.getLoanstate());

		objlist.put("leaseContract", ml.getLeaseContract());
		objlist.put("propertyRegistration", ml.getPropertyRegistration());
		// objlist.put("depositReceipt", ml.getDepositReceipt());
		objlist.put("residenceRegistration", ml.getResidenceRegistration());
		// objlist.put("identificationCard", ml.getIdentificationCard());
		// objlist.put("incomeProof", ml.getIncomeProof());
		objlist.put("marriageProof", ml.getMarriageProof());
		objlist.put("employmentProof", ml.getEmploymentProof());
		// objlist.put("businessProof", ml.getBusinessProof());
		// objlist.put("interestLimitDocuments", ml.getInterestLimitDocuments());

		return objlist;
	}

	@PutMapping("/bank/loanlist/detail") // 대출승인대기로 바꾼다
	public String formchecked2(HttpServletRequest request) {
		String loanid = request.getParameter("loanid");
		MemberLoans ml = memloanRepo.findById(Integer.parseInt(loanid)).get();
		ml.setLoanstate("대출승인완료");
		memloanRepo.save(ml);
		return "대출승인완료!!";
	}

	// --------------------------------------------------------------------------

	@PutMapping("/updateurl") // 상담 후 신청 url을 DB에 저장한다
	public String updateurl(HttpServletRequest request) {
		String newurl = request.getParameter("url");
		String loanid = request.getParameter("loanid");
		MemberLoans ml = memloanRepo.findById(Integer.parseInt(loanid)).get();
		ml.setApplyurl(newurl);
		memloanRepo.save(ml);
		return "url수정완료";
	}

//	// 은행원이 승인 거부
//	@PutMapping("bank/reject")
//	public String rejectloan(HttpServletRequest request) {
//
//		String loanid = request.getParameter("loanid");
//		MemberLoans ml = memloanRepo.findById(Integer.parseInt(loanid)).get();
//		ml.setLoanstate("승인거부");
//		memloanRepo.save(ml);
//		return "승인거부완료";
//
//	}

	@GetMapping("/admin/check")
	public String authcheck(HttpServletRequest request) {
		String url = request.getParameter("url");
		return url;
	}

	@GetMapping("/loan/detail/getdocs")
	public Map<String, Object> getDocs(HttpServletRequest request) {
		Map<String, Object> doclist = new HashMap<>();
		System.out.println("getDocs요청");
		String strloanid = request.getParameter("loanid");
		Integer loanid = Integer.parseInt(strloanid);
		MemberLoans ml = memloanRepo.findById(loanid).get();
		String url = "https://s3.ap-southeast-2.amazonaws.com/shinhandshowsbucket/";
		doclist.put("ResidenceRegistration", url + ml.getResidenceRegistration());
		doclist.put("LeaseContract", url + ml.getLeaseContract());
		doclist.put("PropertyRegistration", url + ml.getPropertyRegistration());
		doclist.put("MarriageProof", url + ml.getMarriageProof());
		doclist.put("EmploymentProof", url + ml.getEmploymentProof());
		System.out.println(doclist);
		return doclist;
	}

	@PostMapping("/loan/detail/uploaddocs")
	public String uploadDocs(@RequestParam("files") List<MultipartFile> files, HttpServletRequest request) {
		System.out.println("파일업로드 요청 들어옴");
//		String memloanid = request.getParameter("loanid");
//대출심사대기 로 바뀌기
		String uploadPath1 = System.getProperty("user.dir");
		String uploadPath2 = uploadPath1 + "\\src\\main\\resources\\allfiles\\"; // 파일 저장 경로
		System.out.println(uploadPath2);
		String response = "";
		String token = request.getHeader("token");// 토큰
		String memberid = getMemberId(token);
		Members mem = memRepo.findByMemberid(memberid).get();
		MemberLoans ml = memloanRepo.findByMemberid(mem).get(0);// 로직 수정하기...member가 여러 대출했을때 안됨.
		List<String> filenames = new ArrayList<>();
		String url = null;
		String result = "fail";
		for (MultipartFile file : files) {
			System.out.println("반복!");
			if (!file.isEmpty()) {
				String originalFilename = file.getOriginalFilename();
				System.out.println(originalFilename);
				String uniqueFilename = generateUniqueFilename(originalFilename);

				try {
					File destFile = new File(uploadPath2 + uniqueFilename);
					file.transferTo(destFile);
					response += "File " + originalFilename + " uploaded successfully.\n";
					System.out.println(response);

					uploadOnS3(uniqueFilename, destFile);
					url = defaultUrl + "/" + uniqueFilename;
					// destFile.dekte
					filenames.add(uniqueFilename);

					result = "success!!";

				} catch (IOException e) {
					response += "Failed to upload file " + originalFilename + ".\n";
					System.out.println(response);
					result = "fail!!";

				}
			}
		}
		// 서류를 저장하고

		ml.setLoanstate("대출심사대기");
		ml.setResidenceRegistration(filenames.get(0));
		ml.setLeaseContract(filenames.get(1));
		ml.setPropertyRegistration(filenames.get(2));
		ml.setMarriageProof(filenames.get(3));
		ml.setEmploymentProof(filenames.get(4));

		// 데이터 베이스에 서류 이름을 저장한다
		memloanRepo.save(ml);
//성공시 데이터베이스에 저장하기
		return result;
	}

	//재직증명서 불러오기
	@GetMapping("/loan/detail/getworkdocs")
	public Map<String, Object> getWorkDocs(HttpServletRequest request) {
		Map<String, Object> doclist = new HashMap<>();
		System.out.println("getDocs요청");
//		String token = request.getHeader("token");
		String memberid = request.getParameter("memberid");

		Members mem = memRepo.findById(memberid).get();
		
		String url = "https://s3.ap-southeast-2.amazonaws.com/shinhandshowsbucket/";

		doclist.put("workdocs", url + mem.getWorkDoc());
		
		System.out.println(doclist);
		return doclist;
	}
	
	
	// 재직증명서 업로드
	@PostMapping("/loan/detail/workdocs/{memberid}")
	public String uploadWorkDocs(@RequestParam("file") MultipartFile files,
			HttpServletRequest request , @PathVariable String memberid /* , @RequestBody String requestBody */) {
		System.out.println("재직증명서업로드 요청 들어옴");
//		String memloanid = request.getParameter("loanid");
//대출심사대기 로 바뀌기
		String uploadPath1 = System.getProperty("user.dir");
		String uploadPath2 = uploadPath1 + "\\src\\main\\resources\\allfiles\\"; // 파일 저장 경로
		System.out.println(uploadPath2);
		String response = "";
		//String token = request.getHeader("token");// 토큰
	//	String memberid = getMemberId(token);
		
		//MemberLoans ml = memloanRepo.findByMemberid(mem).get(0);// 로직 수정하기...member가 여러 대출했을때 안됨.
		String url = null;
		String result = "fail";
//		
//		ObjectMapper objectMapper = new ObjectMapper();
//		JsonNode jsonNode = null;
//		try {
//			jsonNode = objectMapper.readTree(requestBody);
//		} catch (JsonMappingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (JsonProcessingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		String memberid = jsonNode.get("memberid").asText();
//		String memberid = request.getHeader("memberid");
		Members mem = memRepo.findByMemberid(memberid).get();
		String originalFilename = files.getOriginalFilename();
		System.out.println(originalFilename);
		String uniqueFilename = generateUniqueFilename(originalFilename);
		System.out.println("unique file name 생성");
		try {
			File destFile = new File(uploadPath2 + uniqueFilename);
			files.transferTo(destFile);
			response += "File " + originalFilename + " uploaded successfully.\n";
			System.out.println(response);

			uploadOnS3(uniqueFilename, destFile);
			System.out.println("업로드 성공");
			url = defaultUrl + "/" + uniqueFilename;
			// destFile.dekte
			mem.setWorkDoc(uniqueFilename);
			result = "success";
		} catch (IOException e) {
			response += "Failed to upload file " + originalFilename + ".\n";
			System.out.println(response);
			result = "fail";
		}
		// 서류를 저장하고
		memRepo.save(mem);
		return result;
	}

	private String generateUniqueFilename(String originalFilename) {
		// String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new
		// Date());
		String extension = extractFileExtension(originalFilename);
		final String saveFileName = getUuid() + "." + extension;
		return saveFileName;
	}

	private static String getUuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	private String extractFileExtension(String filename) {
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex > 0 && dotIndex < filename.length() - 1) {
			return filename.substring(dotIndex + 1);
		}
		return "";
	}

	private String getRandomString(int length) {
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int randomIndex = (int) (Math.random() * characters.length());
			sb.append(characters.charAt(randomIndex));
		}
		return sb.toString();
	}

	private void uploadOnS3(final String findName, final File file) {
		// AWS S3 전송 객체 생성

		final TransferManager transferManager = new TransferManager(amazonS3Client);
		// 요청 객체 생성
		final PutObjectRequest request = new PutObjectRequest(bucket, findName, file);
		// 업로드 시도
		final Upload upload = transferManager.upload(request);

		try {
			upload.waitForCompletion();
			System.out.println("업로드 완료");
		} catch (AmazonClientException amazonClientException) {
			log.error(amazonClientException.getMessage());
			System.err.println(bucket);
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}
}
