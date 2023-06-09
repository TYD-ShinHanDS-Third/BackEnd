package com.shinhan.education;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.service.MemberService;
import com.shinhan.education.vo.MemberLevel;
import com.shinhan.education.vo.MemberSignUpRequest;
import com.shinhan.education.vo.Members;
import com.shinhan.education.vo.Role;

@SpringBootTest
public class MembersControllerTest {
	
	@Autowired
    private MemberRepository memberRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MemberService memberservice;
	
	
	 @Autowired
	 private WebApplicationContext webApplicationContext;
	
	
	
	
	
//	@Test
//	public void testSaveMember() {
//	    MemberSignUpRequest requestDto = MemberSignUpRequest.builder()
//	            .memberid("ckdrua122")
//	            .membername("김테스트")
//	            .pswd("1234")
//	            .bday(Date.valueOf(LocalDate.parse("1497-04-25")))
//	            .phone("010-4312-1112")
//	            .roles(Role.USER)
//	            .accBank("국민은행")
//	            .accno("45645645646")
//	            .hasjob(1)
//	            .hiredate(Date.valueOf(LocalDate.parse("1542-04-25")))
//	            .marry(1)
//	            .haschild(2)
//	            .build();
//
//	    try {
//			memberservice.signUp(requestDto, requestDto.getMemberLevel());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	//@Test
	public void test5() {
		//CRUD....delete
		memberRepository.findByMemberid("ckdrua3").ifPresent(user->{
			System.out.println(user + "존재한다.");
			memberRepository.delete(user);
		});
	}
	
	
}