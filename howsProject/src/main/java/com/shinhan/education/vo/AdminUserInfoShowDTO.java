package com.shinhan.education.vo;

import java.util.Date;

import lombok.Data;

@Data
//대출상담 시 클라이언트에게 멤버정보 제공 
public class AdminUserInfoShowDTO {
	private Integer hasjob; //직장유무
	private String jobname; //직장명
	private Date hiredate; //입사일
	private Integer marry; //결혼유무
	private Integer haschild; //자녀수
}
