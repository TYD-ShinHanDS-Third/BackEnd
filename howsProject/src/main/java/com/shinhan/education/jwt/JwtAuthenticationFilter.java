package com.shinhan.education.jwt;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//HTTP 요청이 들어올 때마다 실행되며, JWT 토큰을 검증하고 인증 정보를 설정하여 사용자를 인증하는 역할
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtAuthenticationProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtAuthenticationProvider) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    //페이지 이동 시 토큰을 검증하고, 해당 사용자의 권한을 확인하여 접근을 허용하거나 막는 역할
    //Spring Security에서 JWT 토큰을 이용한 인증을 구현하는 일반적인 방법입니다. 
    //SecurityConfig 클래스에서 JwtAuthenticationFilter를 등록하여 필터 체인에 추가하고, JwtTokenProvider를 이용하여 토큰 검증과 인증을 수행합니다. 
    //이를 통해 인증이 필요한 경로에 접근할 때 JWT 토큰을 검증하고, 토큰이 유효하면 해당 사용자를 인증하여 요청을 허용합니다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtAuthenticationProvider.resolveToken(request);

        if (token != null && jwtAuthenticationProvider.validateToken(token)) {
            Authentication authentication = jwtAuthenticationProvider.getAuthentication(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
