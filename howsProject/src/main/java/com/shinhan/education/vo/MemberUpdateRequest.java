package com.shinhan.education.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
//수정할 회원의 정보를 담고 있는 DTO
public class MemberUpdateRequest {
	private final String memberid;
    private String pswd; // 수정할 비밀번호/
    private String accBank; // 수정할 계좌 은행/
    private String accno; // 수정할 계좌 번호/
    private Integer hasJob; // 수정할 직업 유무/
    private Date hiredate; // 수정할 입사일/
    private Integer marry; // 수정할 결혼 유무/
    private Integer haschild; // 수정할 자녀 수/
}
