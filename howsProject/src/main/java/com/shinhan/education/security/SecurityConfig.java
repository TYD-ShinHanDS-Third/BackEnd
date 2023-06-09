package com.shinhan.education.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shinhan.education.jwt.JwtAuthenticationFilter;
import com.shinhan.education.jwt.JwtTokenProvider;
import com.shinhan.education.service.MemberService;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
    private final MemberService memberService;
    
    public SecurityConfig(UserDetailsService userDetailsService,@Lazy MemberService memberService) {
        this.memberService = memberService;
        this.userDetailsService = userDetailsService;
    }

    //클라이언트에서 서버쪽으로 요청하면 검증하는곳
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authorizeRequests() // 요청에 대한 인가 설정을 시작
				.antMatchers("/member/login").permitAll() // 경로로의 요청은 모든 사용자에게 허용됩니다. 즉, 인증되지 않은 사용자도 접근할 수 있다.
				.antMatchers("/member/signup").permitAll() // 경로로의 요청은 모든 사용자에게 허용됩니다. 즉, 인증되지 않은 사용자도 접근할 수 있다.
				.antMatchers("/member/login-success").permitAll()
				.antMatchers("/member/update").permitAll()
				.antMatchers("/member/checkDuplicateId").permitAll()
				.antMatchers("/member/delete").permitAll()
				.antMatchers("/main").permitAll()
				.antMatchers("/**").permitAll()
				//.antMatchers("/member").hasRole("USER") // "/member" 경로로의 요청은 "USER" 권한을 가진 사용자에게만 허용됩니다. 즉, 해당 권한을 가지지
														// 않은 사용자는 접근할 수 없다.
				// .antMatchers("/admin/**").hasRole("ADMIN") //단일 권한 예시추가
				// .antMatchers("/user/**").hasAnyRole("USER", "ADMIN") //다중 권한 예시추가
				.anyRequest().authenticated() // 위에서 설정한 경로 이외의 모든 요청은 인증된 사용자에게만 허용됩니다. 인증되지 않은 사용자는 이러한 요청에 접근할 수
												// 없습니다.
				//addFilterBefore()사용하여 토큰 검증과 인증 처리를 수행한다.
				.and()
				.formLogin()
	            .loginPage("/login.html") // 로그인 페이지 설정
	            .permitAll()
	            .and()
				.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider()),
						UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public JwtTokenProvider jwtTokenProvider() {
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(userDetailsService,memberService);
		return jwtTokenProvider;
	}

	@Override // WebSecurity를 통해 HTTP 요청에 대한 웹 기반 보안을 구성
	public void configure(WebSecurity web) throws Exception {
		// 파일 기준은 resources/static 디렉터리의 하위 파일 목록은 인증 무시 ( = 항상통과 )
		web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/lib/**");
	}

}
