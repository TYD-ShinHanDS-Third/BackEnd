package com.shinhan.education.service;

import java.util.List;

import com.shinhan.education.vo.MemberLevel;
import com.shinhan.education.vo.MemberSignUpRequest;
import com.shinhan.education.vo.MemberUpdateRequest;

public interface MemberService {
	
    // 회원가입
	public void signUp(MemberSignUpRequest requestDto, MemberLevel memberLevel) throws Exception;
    
    //로그인
    public String login(String memberid, String pswd);
    
    // 권한 가져오기
    public List<String> getRoles(String memberid);
    
    //사용자인증
    boolean authenticate(String memberid, String pswd);
    
    // 회원 가입 여부 확인
    boolean checkMemberRegistration(String memberid);

   // 회원탈퇴시 아이디 확인
    boolean delete(String memberid);
  
    // 회원 정보 수정
    boolean update(MemberUpdateRequest request) throws Exception;
    
    
}
