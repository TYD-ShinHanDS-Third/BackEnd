package com.shinhan.education.controller;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.shinhan.education.jwt.JwtTokenProvider;
import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.service.MemberService;
import com.shinhan.education.vo.MemberDTO;
import com.shinhan.education.vo.MemberLevel;
import com.shinhan.education.vo.MemberLoginRequest;
import com.shinhan.education.vo.MemberSignUpRequest;
import com.shinhan.education.vo.MemberUpdateRequest;
import com.shinhan.education.vo.Members;
import com.shinhan.education.vo.Payload;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class MembersController {
	
	 @Autowired
	 private MemberRepository memberRepository;
	
	private final JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private MemberService memberService;
	
	//토큰에서 memberid 추출
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
	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@RequestBody MemberSignUpRequest request) {
	    try {
	        
	        // 회원 가입 요청 정보에 추가 정보가 포함되어 있는지 확인하고 회원 등급 설정
	        boolean hasAdditionalInfo = request.hasAdditionalInfo();
	        MemberLevel memberLevel = hasAdditionalInfo ? MemberLevel.GOLDUSER : MemberLevel.SILVERUSER;

	        // 회원 가입 처리
	        memberService.signUp(request, memberLevel);

	        return new ResponseEntity<>("success.", HttpStatus.OK);
	    } catch (Exception e) {
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    }
	}
    
    //"Bearer "는 토큰 타입을 나타내는 접두사이다.
    //클라이언트는 이 토큰을 이후의 요청에 포함시켜 서버에 인증을 요청할 수 있다.
    //로그인
    @PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody MemberLoginRequest member) {
    	
    	System.out.println(member.getMemberid());
    	System.out.println(member.getPswd());
    	
		try {
			// 입력된 아이디와 비밀번호로 사용자 인증을 진행한다.
			boolean isAuthenticated = memberService.authenticate(member.getMemberid(), member.getPswd());

			if (!isAuthenticated) {
				// 인증 실패한 경우
				String errorMessage = "로그인 실패: 아이디 또는 비밀번호가 일치하지 않습니다.";
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
			}

			// 인증에 성공한 경우, 토큰을 생성한다.
			String token = jwtTokenProvider.createToken(member.getMemberid(), memberService.getRoles(member.getMemberid()));

			// 토큰을 클라이언트에게 전달하기 위해 HTTP 응답 헤더에 포함시킵니다.
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", token);
			headers.add("myname", member.getMemberid());

			// 토큰이 포함된 응답을 클라이언트에게 전달합니다.
			return ResponseEntity.ok().headers(headers).build();

		} catch (Exception e) {
			String errorMessage = "로그인 실패: 서버 오류";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}
    
    //회원탈퇴(토큰으로 완료)
    @DeleteMapping("/delete")
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
            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("없는 회원 아이디입니다."); // 클라이언트에게 "없는 회원 아이디입니다."라는 메시지와 함께 400 상태 코드를 반환할 수 있다.
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴 중 오류가 발생했습니다.");
        }
    }
    
    
 // 회원정보 수정 //토큰
    @PutMapping("/update")
    public ResponseEntity<String> updateMember(HttpServletRequest request, @RequestBody MemberUpdateRequest updaterequest) {
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
    
    
    //아이디 중복체크
    @GetMapping("/checkDuplicateId")
    public ResponseEntity<String> checkDuplicateId(@RequestParam("memberid") String memberid) {
    	System.out.println("ff");
        // 데이터베이스에서 아이디 조회
        Optional<Members> existingMember = memberRepository.findByMemberid(memberid);

        if (existingMember.isPresent()) {
            // 중복된 아이디인 경우
            return ResponseEntity.ok("중복된아이디입니다.");
        } else {
            // 중복되지 않은 아이디인 경우
            return ResponseEntity.ok("사용가능한아이디입니다.");
        }
    }
    
    
    //토큰에서 회원id 추출 후 해당 회원id값으로 회원정보 제공(Mypage)
	@GetMapping("/mypage")
	public ResponseEntity<?> getMemberForEdit(HttpServletRequest request) {
		String token = request.getHeader("token");
		if (token == null) {
			// token이 없을 경우에 대한 처리
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("토큰이 없습니다.");
		}
		String memberid = getMemberId(token);
		try {
			Optional<Members> memberOptional = memberRepository.findByMemberid(memberid);
			if (memberOptional.isPresent()) {
				Members member = memberOptional.get();
				return ResponseEntity.ok(member); // 회원 정보를 반환합니다.
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("회원이 존재하지 않습니다.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 정보 조회 중 오류가 발생했습니다.");
		}
	}
    

    //클라이언트에게 회원가입된 회원정보리스트 전달(관리자 페이지)
    @GetMapping("/users")
    public List<MemberDTO> getMemberList() {
        List<MemberDTO> memberDTOList = memberService.getMembers();
        return memberDTOList;
    }
    
    //관리자가 사용자 Roles 수정
    @PutMapping("/members/{memberId}/roles")
    public ResponseEntity<String> updateMemberRoles(@PathVariable String memberId, @RequestBody List<String> roles) {
        try {
            // 회원 조회
            Optional<Members> memberOptional = memberService.getMemberByid(memberId);
            
            if (!memberOptional.isPresent()) {
                // 회원이 존재하지 않을 경우 404 응답 반환
                return ResponseEntity.notFound().build();
            }

            Members member = memberOptional.get();
            
            // 회원의 roles 설정
            member.setRoles(roles);
            
            // 회원 업데이트
            memberService.updateMemberRoles(memberId, roles);
            
            // 업데이트 완료 메시지 반환
            return ResponseEntity.ok("회원의 roles가 업데이트되었습니다.");
        } catch (Exception e) {
            // 서버 오류 발생 시 500 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원의 roles 업데이트 중 오류가 발생했습니다.");
        }
    }
}