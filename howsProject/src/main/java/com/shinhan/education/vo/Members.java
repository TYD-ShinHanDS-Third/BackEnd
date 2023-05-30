package com.shinhan.education.vo;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MEMBERS")
public class Members {
	
	@Id
	private String memberid; //회원아이디
	private String memebername; //이름
	private String pswd; //비밀번호
	private Date bday; //생년월일
	private String phone; //휴대폰 번호
    private String role; // 사용자, 관리자, 은행원
    private String level; //기본정보 및 추가정보 등급부여
    private String accno; // 계좌
    private boolean hasjob; // 직업유무
    private String jobname; //회사명
    private Date hiredate; //입사일
    private boolean marry; // 결혼유무
    private int haschild; //자녀 수
}
