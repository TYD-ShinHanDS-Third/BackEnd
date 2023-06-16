package com.shinhan.education.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
@RequestMapping("/hows")

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
		System.out.println(panList);

		RequestVO<List<Pans>> r = new RequestVO<List<Pans>>();
		long total = panPage.getTotalElements();
		panList = panPage.getContent();
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
	@GetMapping(value = "/notice/find")
	public List<Houses> selectByAddress(String houseaddress) {
		return null;
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
			return (T) shinhanRepo.findById(loanname);
		} else if (bankname.equals("국민")) {
			return (T) kookminRepo.findById(loanname);
		} else if (bankname.equals("하나")) {
			return (T) hanaRepo.findById(loanname);
		} else if (bankname.equals("우리")) {
			return (T) wooriRepo.findById(loanname);
		} else
			return null;
	}

	// 한도조회
	@GetMapping(value = "/loan/detail/limit")
	public String getlimit(HttpServletRequest request) {

		String juminfront = request.getParameter("juminfront");
		String juminback = request.getParameter("juminback");
		String jumin = juminfront + juminback;
		Nice niceinfo = niceRepo.findById(jumin).get();
		Integer score = niceinfo.getScore();
		String maxloan = "0원";
		if (score >= 900) {
			maxloan = "100,000,000원";
		} else if (score >= 800) {
			maxloan = "90,000,000원";
		} else if (score >= 700) {
			maxloan = "80,000,000원";
		} else if (score >= 600) {
			maxloan = "70,000,000원";
		} else if (score >= 500) {
			maxloan = "60,000,000원";
		} else if (score >= 400) {
			maxloan = "50,000,000원";
		} else if (score >= 300) {
			maxloan = "40,000,000원";
		} else if (score >= 200) {
			maxloan = "30,000,000원";
		} else if (score >= 100) {
			maxloan = "20,000,000원";
		} else {
			maxloan = "신용불량";
		}
		return maxloan;
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
		System.out.println("token : " + token);
		String memberid = getMemberId(token);
		Loans loan = loanRepo.findById(loanname).get();
		Members mem = memRepo.findById(memberid).get();

		// 이미 관련 톡방이 있는지 확인
		// 없으면 새로 룸 생성

		List<MemberLoans> listml = memloanRepo.findByMemberidAndLoanname(mem, loan);

		Map<String, Object> map = new HashMap<>();
		if (listml.size() == 0) {// 방생성
			System.out.println("1");
			MemberLoans ml = MemberLoans.builder().memberid(mem).loanname(loan).loanstate("상담신청").build();
			ml = memloanRepo.save(ml);
			ml.getMemloanid();
			ChatRoom room = ChatRoom.builder().memloanid(ml.getMemloanid()).build();// 룸생성
			room = roomRepo.save(room);// db에 룸저장
			ml.setRoomnumber(room.getRoomId());// 해당 memloan에다가 룸번호 저장
			memloanRepo.save(ml);// db에 저장
			map.put("message", "상담 신청 완료, 상담사와 채팅을 연결합니다.");
			map.put("room", room.getRoomId());
			map.put("myname", mem.getMemberid());
			return (T) map;
		} else {
			System.out.println("2");
			MemberLoans ml = listml.get(0);

			ChatRoom room = roomRepo.findByMemloanid(ml.getMemloanid());
			map.put("message", "상담 신청 내역이 있습니다, 이전 채팅방에 입장합니다.");
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

		List<Map<String, Object>> listA = new ArrayList();
		List<Map<String, Object>> listB = new ArrayList();

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
		objlist.put("depositReceipt", ml.getDepositReceipt());
		objlist.put("residenceRegistration", ml.getResidenceRegistration());
		objlist.put("identificationCard", ml.getIdentificationCard());
		objlist.put("incomeProof", ml.getIncomeProof());
		objlist.put("marriageProof", ml.getMarriageProof());
		objlist.put("employmentProof", ml.getEmploymentProof());
		objlist.put("businessProof", ml.getBusinessProof());
		objlist.put("interestLimitDocuments", ml.getInterestLimitDocuments());

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
		objlist.put("depositReceipt", ml.getDepositReceipt());
		objlist.put("residenceRegistration", ml.getResidenceRegistration());
		objlist.put("identificationCard", ml.getIdentificationCard());
		objlist.put("incomeProof", ml.getIncomeProof());
		objlist.put("marriageProof", ml.getMarriageProof());
		objlist.put("employmentProof", ml.getEmploymentProof());
		objlist.put("businessProof", ml.getBusinessProof());
		objlist.put("interestLimitDocuments", ml.getInterestLimitDocuments());

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

	// 은행원이 승인 거부
	@PutMapping("bank/reject")
	public String rejectloan(HttpServletRequest request) {

		String loanid = request.getParameter("loanid");
		MemberLoans ml = memloanRepo.findById(Integer.parseInt(loanid)).get();
		ml.setLoanstate("승인거부");
		memloanRepo.save(ml);
		return "승인거부완료";

	}

}
