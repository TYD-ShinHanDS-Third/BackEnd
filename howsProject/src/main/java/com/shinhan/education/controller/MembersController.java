package com.shinhan.education.controller;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.shinhan.education.dto.AdminUserInfoShowDTO;
import com.shinhan.education.dto.MemberDTO;
import com.shinhan.education.dto.MemberLoginRequest;
import com.shinhan.education.dto.MemberSignUpRequest;
import com.shinhan.education.dto.MemberUpdateRequest;
import com.shinhan.education.entity.Members;
import com.shinhan.education.entity.Nice;
import com.shinhan.education.jwt.JwtTokenProvider;
import com.shinhan.education.mail.Approvalemail;
import com.shinhan.education.mail.RegisterMail;
import com.shinhan.education.naversms.Naver_Sens_Approved;
import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.respository.NiceRepository;
import com.shinhan.education.service.MemberLoansService;
import com.shinhan.education.service.MemberService;
import com.shinhan.education.vo.MemberLevel;
import com.shinhan.education.vo.Payload;
import com.shinhan.education.vo.RequestVO;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/hows")
@RestController
public class MembersController {

	@Autowired
	private MemberRepository memberRepo;

	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	private MemberService memberService;

	@Autowired
	private RegisterMail registermail;
	
	@Autowired
	private Approvalemail approvalemail;

	@Autowired
	private NiceRepository niceRepo;
	
	@Autowired
	private MemberLoansService memberLoansService;

	// 토큰에서 memberid 추출
	String getMemberId(String token) {
		System.out.println("token: " + token);
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

	// 회원가입
	@PostMapping("/auth/signup")
	public ResponseEntity<String> signUp(@RequestBody MemberSignUpRequest request) {
		try {

			// 회원 가입 요청 정보에 추가 정보가 포함되어 있는지 확인하고 회원 등급 설정
			boolean hasAdditionalInfo = request.hasAdditionalInfo();
			MemberLevel memberLevel = hasAdditionalInfo ? MemberLevel.GOLDUSER : MemberLevel.SILVERUSER;

			// 회원 가입 처리
			memberService.signUp(request, memberLevel);

			// 주민등록번호 자동생성해서 nice테이블에 저장. 신용등급은 기본 800
			SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
			String juminfront = sdf.format(request.getBday());
			Random random = new Random();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 7; i++) {
				int digit;
				if (i == 0)
					digit = random.nextInt(4) + 1;
				else
					digit = random.nextInt(10);
				sb.append(digit);
			}
			String juminback = sb.toString();
			String jumin = juminfront + juminback;
			Nice nice = Nice.builder().jumin(jumin).name(request.getMembername()).score(800).build();
			niceRepo.save(nice);

			return new ResponseEntity<>("success.", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	// "Bearer "는 토큰 타입을 나타내는 접두사이다.
	// 클라이언트는 이 토큰을 이후의 요청에 포함시켜 서버에 인증을 요청할 수 있다.
	// 로그인
	@PostMapping("/auth/login")
	public ResponseEntity<String> login(@RequestBody MemberLoginRequest member) {

		try {
			// 입력된 아이디와 비밀번호로 사용자 인증을 진행한다.
			boolean isAuthenticated = memberService.authenticate(member.getMemberid(), member.getPswd());

			if (!isAuthenticated) {
				// 인증 실패한 경우
				String errorMessage = "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.";
				
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
			}

			// 인증에 성공한 경우, 토큰을 생성한다.
			String token = jwtTokenProvider.createToken(member.getMemberid(),
					memberService.getRoles(member.getMemberid()));
			// 토큰을 클라이언트에게 전달하기 위해 HTTP 응답 헤더에 포함시킵니다.
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", token);
			//headers.add("myname", member.getMemberid());
			headers.add("roles", memberService.getRoles(member.getMemberid()).toString());

			// 토큰이 포함된 응답을 클라이언트에게 전달합니다.
			return ResponseEntity.ok().headers(headers).build();

		} catch (Exception e) {
			String errorMessage = "로그인 실패: 서버 오류";
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	// 회원탈퇴(토큰으로 완료)
	@DeleteMapping("/my/withdraw")
	public ResponseEntity<String> delete(HttpServletRequest request) {
		String token = request.getHeader("token");
		if (token == null) {
			// token이 없을 경우에 대한 처리
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰이 없습니다.");
		}
		String memberid = getMemberId(token);
		try {
			boolean isDeleted = memberService.delete(memberid);

			if (isDeleted) {
				return ResponseEntity.ok("success");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("없는 회원 아이디입니다."); // 클라이언트에게 "없는 회원 아이디입니다."라는
																							// 메시지와 함께 400 상태 코드를 반환할 수
																							// 있다.
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴 중 오류가 발생했습니다.");
		}
	}

	//HttpServletRequest request는 클라이언트로부터의 요청을 받고 그 정보를 활용한다.
	//@RequestBody MemberUpdateRequest updaterequest는 클라이언트가 요청의 본문에 담은 데이터를 객체로 변환한다.
	// 아이디 중복체크
	@GetMapping("/auth/checkDuplicateId")
	public ResponseEntity<String> checkDuplicateId(@RequestParam("memberid") String memberid) {
		// 데이터베이스에서 아이디 조회
		Optional<Members> existingMember = memberRepo.findByMemberid(memberid);

		if (existingMember.isPresent()) {
			// 중복된 아이디인 경우
			return ResponseEntity.ok("중복된아이디입니다.");
		} else {
			// 중복되지 않은 아이디인 경우
			return ResponseEntity.ok("사용가능한아이디입니다.");
		}
	}

	// 토큰에서 회원id 추출 후 해당 회원id값으로 회원정보 제공(Mypage)
	@GetMapping("/auth/mypage")
	public ResponseEntity<?> getMemberForEdit(HttpServletRequest request) {
		System.err.println("마이페이지 정보 ");
		String token = request.getHeader("token");
		if (token == null) {
			// token이 없을 경우에 대한 처리
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰이 없습니다.");
		}
		String memberid = getMemberId(token);
		try {
			Optional<Members> memberOptional = memberRepo.findByMemberid(memberid);
			if (memberOptional.isPresent()) {
				Members member = memberOptional.get();
				System.out.println("OK");
				System.out.println(member);
				return ResponseEntity.ok(member); // 회원 정보를 반환합니다.
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원이 존재하지 않습니다.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 조회 중 오류가 발생했습니다.");
		}
	}

	// 클라이언트에게 회원가입된 회원정보리스트 전달(관리자 페이지)
	@GetMapping("/admin/user")
	public RequestVO<List<MemberDTO>> getMemberList(@RequestParam int page, @RequestParam int size) {
		RequestVO<List<MemberDTO>> memberDTOList = memberService.getMembers(page, size);
		return memberDTOList;
	}

	// 관리자가 사용자 Roles 수정 및 권한 승인 완료 메일 전송
	@PutMapping("/admin/user")
	public ResponseEntity<String> updateMemberRolesAndSendEmail(@RequestParam String memberid, @RequestParam List<String> roles, @RequestParam("email") String email) {
	    try {
	        // 회원 업데이트
	    	Members mem = memberRepo.findByMemberid(memberid).get();
	    	roles.clear();
	    	roles.add(mem.getWantrole());
	        memberService.updateMemberRoles(memberid, roles);

	        // 이메일 발송
	        String code = approvalemail.sendSimpleMessage(email);

	        // 업데이트 및 이메일 발송 완료 메시지 반환
	        return ResponseEntity.ok("발송성공");
	    } catch (Exception e) {
	        // 서버 오류 발생 시 500 응답 반환
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("발송실패");
	    }
	}

	// 재직자 이메일 인증
	@PostMapping("/auth/email")
	String mailConfirm(@RequestParam("email") String email) throws Exception {
		String code = registermail.sendSimpleMessage(email);
		return code;
	}

	// 상담신청 후 해당 userid값으로 user의추가정보 및 선택한상품명을 클라이언트에게 제공
	@GetMapping("/admin/userinfoshow")
	public ResponseEntity<?> getUserInfoShow(@RequestParam String memberid) {
		try {
			// memberid를 기반으로 DB에서 추가 정보 조회
			Optional<Members> memberOptional = memberRepo.findByMemberid(memberid);
			if (memberOptional.isPresent()) {
				Members member = memberOptional.get();
				// 필요한 정보를 AdminUserInfoShowDTO에 설정
				AdminUserInfoShowDTO userInfoShowDTO = new AdminUserInfoShowDTO();
				userInfoShowDTO.setHasjob(member.getHasjob());
				userInfoShowDTO.setJobname(member.getJobname());
				userInfoShowDTO.setHiredate(member.getHiredate());
				userInfoShowDTO.setMarry(member.getMarry());
				userInfoShowDTO.setHaschild(member.getHaschild());
				userInfoShowDTO.setMembername(member.getMembername());
				userInfoShowDTO.setBday(member.getBday());
				userInfoShowDTO.setPhone(member.getPhone());
				return ResponseEntity.ok(userInfoShowDTO);
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원이 존재하지 않습니다.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("추가 정보 조회 중 오류가 발생했습니다.");
		}
	}
	
	// 회원 정보 수정
	@PutMapping("/my/myedit")
	public ResponseEntity<String> updateMember(HttpServletRequest request,
			@RequestBody MemberUpdateRequest updaterequest) {
		System.out.println("member controller update member ; "+ updaterequest);
		try {
			boolean isUpdated = memberService.update(request, updaterequest);
			if (isUpdated) {
				return ResponseEntity.ok("success");
			} else {
				return ResponseEntity.badRequest().body("회원 정보 업데이트에 실패했습니다.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 수정 중 오류가 발생했습니다.");
		}
	}
	
	//은행원이 대출 승인 버튼 누르면 
	@PutMapping("/bank/approval")
	public ResponseEntity<String> loansApprovalUpdate(@RequestParam("memloanid") Integer memloanid,
	                                          @RequestParam("tel") String tel,
	                                          @RequestParam("membername") String membername,
	                                          @RequestParam("loanname") String loanname) {
	    try {
	        boolean stateUpdate = memberLoansService.updateApprovalLoanStatus(memloanid);
	        if (stateUpdate) {
	            // 문자 보내기
	            Naver_Sens_Approved sensApproved = new Naver_Sens_Approved();
	            sensApproved.send_msg(tel, membername, loanname);
	            return ResponseEntity.ok("success");
	        } else {
	            return ResponseEntity.badRequest().body("대출상태 업데이트에 실패하였습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("에러가 발생하였습니다: " + e.getMessage());
	    }
	}
	
	
	//은행원이 대출승인 거절버튼 누르면
	@PutMapping("/bank/refusal")
	public ResponseEntity<String> loansRefusalUpdate(@RequestParam("memloanid") Integer memloanid,
	                                          @RequestParam("tel") String tel,
	                                          @RequestParam("membername") String membername,
	                                          @RequestParam("loanname") String loanname) {
	    try {
	        boolean stateUpdate = memberLoansService.updateRefusalLoanStatus(memloanid);
	        if (stateUpdate) {
	            // 문자 보내기
	            Naver_Sens_Approved sensApproved = new Naver_Sens_Approved();
	            sensApproved.send_msg(tel, membername, loanname);
	            return ResponseEntity.ok("success");
	        } else {
	            return ResponseEntity.badRequest().body("대출상태 업데이트에 실패하였습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("에러가 발생하였습니다: " + e.getMessage());
	    }
	}
	
	
	//관리자가 대출 승인 버튼 누르면 
	@PutMapping("/admin/approval")
	public ResponseEntity<String> adminloansApprovalUpdate(@RequestParam("memloanid") Integer memloanid
	                                       ) {
	    try {
	        boolean stateUpdate = memberLoansService.updateApprovalLoanStatus(memloanid);
	        if (stateUpdate) {
	            return ResponseEntity.ok("success");
	        } else {
	            return ResponseEntity.badRequest().body("대출상태 업데이트에 실패하였습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("에러가 발생하였습니다: " + e.getMessage());
	    }
	}
	
	//관리자가 대출승인 거절버튼 누르면
	@PutMapping("/admin/refusal")
	public ResponseEntity<String> adminloansRefusalUpdate(@RequestParam("memloanid") Integer memloanid,
	                                          @RequestParam("tel") String tel,
	                                          @RequestParam("membername") String membername,
	                                          @RequestParam("loanname") String loanname) {
	    try {
	        boolean stateUpdate = memberLoansService.updateRefusalLoanStatus(memloanid);
	        if (stateUpdate) {
	            // 문자 보내기
	            Naver_Sens_Approved sensApproved = new Naver_Sens_Approved();
	            sensApproved.send_msg(tel, membername, loanname);
	            return ResponseEntity.ok("success");
	        } else {
	            return ResponseEntity.badRequest().body("대출상태 업데이트에 실패하였습니다.");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.badRequest().body("에러가 발생하였습니다: " + e.getMessage());
	    }
	}
	
	//상담버튼을 누르면 memberlevel에 따라 접속여부 결정해서 보냄
	 @GetMapping("/auth/request")
	  public ResponseEntity<String> requestConsultation(HttpServletRequest request) {
		String token = request.getHeader("token");
			if (token == null) {
				// token이 없을 경우에 대한 처리
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰이 없습니다.");
			}
		String memberid = getMemberId(token);
	    MemberLevel memberLevel = memberService.getMemberLevel(memberid);
	    if (memberLevel == MemberLevel.SILVERUSER) {
	      return ResponseEntity.ok("fail");
	    } else if (memberLevel == MemberLevel.GOLDUSER) {
	      return ResponseEntity.ok("success");
	    } else {
	      return ResponseEntity.badRequest().body("Invalid memberid");
	    }
	  }
	 
		//관리자가 대출상담 종료 버튼 누르면 
		@PutMapping("/admin/chatend")
		public ResponseEntity<String> adminchatend(@RequestParam("memloanid") Integer memloanid){
		    try {
		        boolean stateUpdate = memberLoansService.adminchatendLoanStatus(memloanid);
		        if (stateUpdate) {
		            return ResponseEntity.ok("success");
		        } else {
		            return ResponseEntity.badRequest().body("대출상태 업데이트에 실패하였습니다.");
		        }
		    } catch (Exception e) {
		        return ResponseEntity.badRequest().body("에러가 발생하였습니다: " + e.getMessage());
		    }
		}
	
}