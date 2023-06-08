package com.shinhan.education.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shinhan.education.jwt.JwtTokenProvider;
import com.shinhan.education.service.MemberService;
import com.shinhan.education.vo.MemberDeleteRequest;
import com.shinhan.education.vo.MemberLoginRequest;
import com.shinhan.education.vo.MemberSignUpRequest;
import com.shinhan.education.vo.MemberUpdateRequest;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class MembersController {
	
	private final JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private MemberService memberService;

	//회원가입
    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody MemberSignUpRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(request.getPswd());
        request.setPswd(encryptedPassword);
        
        try {
            String memberId = memberService.signUp(request);
            return ResponseEntity.ok("회원가입이 완료되었습니다. 회원 ID: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다.");
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
            boolean isDeleted = memberService.delete(request.getMemberId());

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
    
    
    
}