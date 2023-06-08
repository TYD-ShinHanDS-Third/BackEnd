package com.shinhan.education.vo;

import java.sql.Date;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // 기본 생성자 추가
public class MemberSignUpRequest{

	private String memberid; // 회원아이디
	private String membername; // 이름
	private String pswd; // 비밀번호
	private String checkedpswd; // 비밀번호 확인
	private Date bday; // 생년월일
	private String phone; // 휴대폰 번호
	private Role roles; // 역할

	public Members toEntity(PasswordEncoder passwordEncoder) {
		return Members.builder()
				.memberid(memberid)
				.membername(membername)
				.pswd(passwordEncoder
				.encode(pswd)) // 비밀번호를																							// 저장
				.bday(bday)
				.phone(phone)
				.roles(roles) // roles 필드 사용
				.build();
	}

	public MemberSignUpRequest(String memberid, String membername, String pswd, String checkedpswd, Date bday,
			String phone, Role roles) {
		this.memberid = memberid;
		this.membername = membername;
		this.pswd = pswd;
		this.checkedpswd = checkedpswd;
		this.bday = bday;
		this.phone = phone;
		this.roles = roles;
	}
	
}