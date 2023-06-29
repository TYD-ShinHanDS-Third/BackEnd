package com.shinhan.education.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
//수정할 회원의 정보를 담고 있는 DTO
public class MemberUpdateRequest {
	private final String memberid;
	private String membername; // 이름
	private String pswd; // 비밀번호
	private Date bday; // 생년월일
	private String phone; // 휴대폰 번호
	private String accBank; // 계좌은행
	private String accno; // 계좌
	private String jobname; // 직장명
	private Integer hasjob; // 직업유무
	private String hiredate; // 입사일
	private Integer marry; // 결혼유무
	private Integer haschild; // 자녀 수
}
