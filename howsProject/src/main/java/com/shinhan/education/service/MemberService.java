package com.shinhan.education.service;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.shinhan.education.dto.MemberDTO;
import com.shinhan.education.dto.MemberSignUpRequest;
import com.shinhan.education.dto.MemberUpdateRequest;
import com.shinhan.education.entity.Members;
import com.shinhan.education.vo.MemberLevel;
import com.shinhan.education.vo.RequestVO;

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
    boolean update(HttpServletRequest request, MemberUpdateRequest updaterequest) throws Exception;
    
    //회원가입된 회원정보리스트 전달(페이징)
    public RequestVO<List<MemberDTO>> getMembers(int page, int pageSize);

    //관리작 사용자 Roles 수정
    public void updateMemberRoles(String memberid, List<String> roles);
	
	//회원조회
	public Optional<Members> getMemberByid(String memberId);
	
	//memberid로 MemberLevel 조회
	public MemberLevel getMemberLevel(String memberid);
	
}