package com.shinhan.education.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shinhan.education.entity.Members;
import com.shinhan.education.vo.Role;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private Members member;
    
    public CustomUserDetails(Members member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Role> roles = Arrays.asList(member.getRoles()); // Role 값을 리스트로 변환

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        // 사용자의 비밀번호를 반환
        return member.getPswd();
    }

    @Override
    public String getUsername() {
        // 사용자의 아이디(memberid)를 반환하는 로직을 구현합니다.
        // 예시: member.getMemberid();
        return member.getMemberid();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정이 만료되지 않았는지를 반환하는 로직을 구현합니다.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정이 잠겨있지 않은지를 반환하는 로직을 구현합니다.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명(비밀번호)이 만료되지 않았는지를 반환하는 로직을 구현합니다.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정이 활성화되어 있는지를 반환하는 로직을 구현합니다.
        return true;
    }
}