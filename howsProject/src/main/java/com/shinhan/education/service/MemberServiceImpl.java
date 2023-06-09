package com.shinhan.education.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shinhan.education.jwt.JwtTokenProvider;
import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.vo.MemberLevel;
import com.shinhan.education.vo.MemberSignUpRequest;
import com.shinhan.education.vo.MemberUpdateRequest;
import com.shinhan.education.vo.Members;


@Transactional
@Service
public class MemberServiceImpl implements MemberService {


    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository,JwtTokenProvider jwtTokenProvider ,PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider =jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    //회원가입
    @Override
    @Transactional
    public void signUp(MemberSignUpRequest requestDto, MemberLevel memberLevel) throws Exception {
        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(requestDto.getPswd());

        Members member = requestDto.toEntity();
        member.setPswd(encryptedPassword);
        member.setMemberLevel(memberLevel);
        member = memberRepository.save(member);
        System.out.println(member);
    }

    
    @Override
    public String login(String memberid, String password) {
        // 회원 가입 여부 확인
        boolean isMemberRegistered = checkMemberRegistration(memberid);
        if (!isMemberRegistered) {
            throw new IllegalArgumentException("가입되지 않은 ID입니다.");
        }

        // 주어진 회원 ID와 비밀번호를 사용하여 사용자의 인증을 진행
        boolean isAuthenticated = authenticate(memberid, password);
        if (!isAuthenticated) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        // 토큰 발행을 위한 회원 정보 가져오기
        Members member = memberRepository.findByMemberid(memberid)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 가져올 수 없습니다."));

        // 권한 리스트 생성
        List<String> roles = new ArrayList<>();
        roles.add(member.getRoles().name());

        // 토큰 발행
        return jwtTokenProvider.createToken(member.getMemberid(), roles);
    }
    
    //메서드는 주로 로그인 기능에서 사용되며, 주어진 회원 ID와 비밀번호를 사용하여 사용자의 인증함
    @Override
    public boolean authenticate(String memberid, String password) {
        Members member = memberRepository.findByMemberid(memberid)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID 입니다."));

        return passwordEncoder.matches(password, member.getPswd());
    }
    
    //메서드는 주어진 회원 ID를 사용하여 해당 회원의 역할(Role) 목록을 가져온다.
    @Override
    public List<String> getRoles(String memberid) {
        Members member = memberRepository.findByMemberid(memberid)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID 입니다."));

        return member.getRoleNames();
    }
    
    //회원 가입 전에 중복된 회원 ID가 있는지 확인하거나, 특정 회원의 가입 여부를 확인할 때 사용
    @Override
    public boolean checkMemberRegistration(String memberid) {
        Optional<Members> memberOptional = memberRepository.findByMemberid(memberid);
        return memberOptional.isPresent();
    }
    
    //회원탈퇴
    @Override
    public boolean delete(String memberid) {
        Optional<Members> memberOptional = memberRepository.findByMemberid(memberid);

        if (memberOptional.isPresent()) {
            Members member = memberOptional.get();
            memberRepository.delete(member);
            return true;
        }

        return false;
    }
    
    //회원정보수정
    @Transactional
    @Override
    public boolean update(MemberUpdateRequest request) throws Exception {
        // 회원 ID를 기준으로 회원을 조회
        Members member = memberRepository.findByMemberid(request.getMemberid())
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        // 수정할 필드들을 업데이트
        member.setPswd(request.getPswd());
        member.setAccBank(request.getAccBank());
        member.setAccno(request.getAccno());
        member.setHasjob(request.getHasJob());
        member.sethiredate(request.getHiredate());
        member.setMarry(request.getMarry());
        member.setHaschild(request.getHaschild());
        memberRepository.save(member);
        return true;
    }
}