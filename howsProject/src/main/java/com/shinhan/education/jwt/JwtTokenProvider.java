package com.shinhan.education.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    private String secretKey = "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK";
    private long tokenValidTime = 24 * 60 * 60 * 1000L;
    private final UserDetailsService userDetailsService;
    private final Key key;
    @Autowired
    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        byte[] keybytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keybytes);
    }

    // 토큰 생성 (memberId, roles)
    public String createToken(String memberId, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(memberId);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 주어진 JWT 토큰을 기반으로 사용자의 인증 정보를 추출하여 Authentication 객체로 반환 //roles의 권한의 유무를 확인한디.
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
        String memberid = claims.getSubject();
        List<String> roles = (List<String>) claims.get("roles");
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberid);
        List<GrantedAuthority> authorities = roles.stream()
                .map(x -> new SimpleGrantedAuthority("ROLE_" + x))
              //  .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    // 주어진 JWT 토큰에서 회원 ID를 추출하여 반환
    public String getMemberId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 토큰을 추출하여 반환합니다. 요청의 헤더에서 "token" 값을 가져옴
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("token");
    }

    // 주어진 JWT 토큰의 유효성을 검증합니다.
    public boolean validateToken(String token) {
    	System.out.println("validateToken token검증 :" + token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    // 주어진 JWT 토큰의 유효성을 검증하고, 토큰이 만료되지 않았을 경우 토큰에 포함된 클레임(claims)을 반환합니다.
    // 유효하지 않은 토큰인 경우나 토큰이 만료된 경우에는 null을 반환합니다.
    public Claims validateTokenAndExtractClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody();

            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return null; // 토큰이 만료되었을 경우 null 반환
            }

            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            return null; // 유효하지 않은 토큰인 경우 null 반환
        }
    }
}