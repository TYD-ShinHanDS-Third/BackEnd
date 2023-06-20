package com.shinhan.education.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shinhan.education.entity.Members;
import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberid) throws UsernameNotFoundException {
        System.out.println("xxxxxxxxxxxxxxxx");
        Members member = memberRepository.findByMemberid(memberid)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        System.out.println("tttttttttttttt" +  member);
        return new CustomUserDetails(member);
    }

}