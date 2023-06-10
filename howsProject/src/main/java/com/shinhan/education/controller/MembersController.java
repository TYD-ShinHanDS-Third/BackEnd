package com.shinhan.education.controller;
import java.util.List;
import java.util.Optional;

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

import com.shinhan.education.jwt.JwtTokenProvider;
import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.service.MemberService;
import com.shinhan.education.vo.MemberDTO;
import com.shinhan.education.vo.MemberDeleteRequest;
import com.shinhan.education.vo.MemberLevel;
import com.shinhan.education.vo.MemberLoginRequest;
import com.shinhan.education.vo.MemberSignUpRequest;
import com.shinhan.education.vo.MemberUpdateRequest;
import com.shinhan.education.vo.Members;

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

			// 토큰이 포함된 응답을 클라이언트에게 전달합니다.
			return ResponseEntity.ok().headers(headers).build();

		} catch (Exception e) {
			String errorMessage = "로그인 실패: 서버 오류";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}
    
    //로그아웃시 쿠키(토큰) 삭제
//    @GetMapping("/logout")
//    public String logout(HttpServletResponse response) {
//        // 쿠키 삭제
//    	// "Authorization" 이름의 쿠키를 생성하고 값을 null로 설정합니다. 이렇게 생성된 쿠키는 클라이언트에게 전달되어 토큰을 삭제하는 역할을 수행
//        Cookie cookie = new Cookie("Authorization", null);
//        // 코드는 쿠키의 유효 기간을 0으로 설정하여 즉시 만료되도록 합니다. 이를 통해 클라이언트 측에서 해당 쿠키를 삭제할 수 있게 됩니다.
//        cookie.setMaxAge(0);
//        //코드는 쿠키의 경로를 "/"로 설정합니다. 이는 쿠키가 전체 애플리케이션의 경로에서 유효하도록 합니다.
//        cookie.setPath("/");
//        //코드는 응답에 쿠키를 추가합니다. 이를 통해 클라이언트에게 쿠키가 전달되어 삭제되도록 한다.
//        response.addCookie(cookie);
//        
//        // 로그아웃 후 리다이렉트할 경로
//        return "redirect:/main";
//    }
    
    //클라이언트가 로그아웃을 요청보내면 서버쪽에서 저렇게 쿠키삭제를 로직을 보내고 
    //클라이언트는 document.cookie = "Authorization=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"; 이렇게 작성하여 쿠리를 삭제한다.
    //위의 코드는 "Authorization" 이름의 쿠키를 현재 시간보다 이전으로 설정하여 삭제하는 것을 의미합니다.
    
    //회원탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody MemberDeleteRequest request) {
        try {
            boolean isDeleted = memberService.delete(request.getMemberid());

            if (isDeleted) {
                return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
            } else {
            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("없는 회원 아이디입니다."); // 클라이언트에게 "없는 회원 아이디입니다."라는 메시지와 함께 400 상태 코드를 반환할 수 있다.
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴 중 오류가 발생했습니다.");
        }
    }
    
    
 // 회원정보 수정
    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody MemberUpdateRequest request) {
        try {
            boolean isUpdated = memberService.update(request);

            if (isUpdated) {
                return ResponseEntity.ok("회원 정보가 수정되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원 정보 수정에 실패했습니다.");
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

    //클라이언트에게 회원가입된 회원정보리스트 전달
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