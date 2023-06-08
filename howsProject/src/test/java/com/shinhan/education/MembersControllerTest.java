package com.shinhan.education;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.vo.MemberSignUpRequest;
import com.shinhan.education.vo.Members;
import com.shinhan.education.vo.Role;

@SpringBootTest
public class MembersControllerTest {
	
	@Autowired
    private MemberRepository memberRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//@Test
	public void testSaveMember() {
	    MemberSignUpRequest requestDto = MemberSignUpRequest.builder()
	            .memberid("ckdrua1")
	            .membername("김창겸")
	            .pswd("1234")
	            .bday(Date.valueOf(LocalDate.parse("1997-04-25")))
	            .phone("010-4312-1112")
	            .roles(Role.USER)
	            .build();

	    Members member = requestDto.toEntity(passwordEncoder);
	    memberRepository.save(member);
	}
	
	//@Test
	public void test5() {
		//CRUD....delete
		memberRepository.findByMemberid("ckdrua1").ifPresent(job->{
			System.out.println(job + "존재한다.");
			memberRepository.delete(job);
		});
	}
	
	
}