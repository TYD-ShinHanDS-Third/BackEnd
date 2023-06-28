package com.shinhan.education.dto;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.shinhan.education.vo.Role;

import lombok.Data;

//클라이언트한테 멤버정보를 주는 DTO
@Data
public class MemberDTO {
    private String memberid;
    private String membername;
    private Date bday;
    private String employdocument;
    private String email;
	@Enumerated(EnumType.STRING)
	private Role roles; // 사용자, 관리자, 은행원
	
	private String wantRole;
}