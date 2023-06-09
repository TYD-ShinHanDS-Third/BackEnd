package com.shinhan.education.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDeleteRequest {
    private String memberid; // 탈퇴할 회원 아이디
}
