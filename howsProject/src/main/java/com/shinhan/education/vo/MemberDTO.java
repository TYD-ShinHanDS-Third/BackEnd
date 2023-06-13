package com.shinhan.education.vo;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Data;

//클라이언트한테 멤버정보를 주는 DTO
@Data
public class MemberDTO {
    private String memberid;
    private String membername;
    private Date bday;
	@Enumerated(EnumType.STRING)
	private Role roles; // 사용자, 관리자, 은행원

}