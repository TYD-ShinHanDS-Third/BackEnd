package com.shinhan.education.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.shinhan.education.jwt.JwtAuthenticationFilter;
import com.shinhan.education.jwt.JwtTokenProvider;
import com.shinhan.education.service.MemberService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final MemberService memberService;

	public SecurityConfig(UserDetailsService userDetailsService, MemberService memberService) {
		this.userDetailsService = userDetailsService;
		this.memberService = memberService;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable().formLogin().disable().exceptionHandling().accessDeniedHandler(accessDeniedHandler()).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

				.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()

				.antMatchers("/hows/admin/**").hasRole("ADMIN").antMatchers("/hows/bank/**").hasRole("TELLER")
				.antMatchers("/hows/notice/**").permitAll().antMatchers("/hows/find/**").permitAll() // 안해도 됨
				.antMatchers("/hows/auth/**").permitAll().antMatchers("/hows/loan/**").permitAll().anyRequest()
				.authenticated();

		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/css/**", "/js/**", "/images/**", "/lib/**");
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(userDetailsService);
		return new JwtAuthenticationFilter(jwtTokenProvider);
	}


//	@Bean
//	public AccessDeniedHandler accessDeniedHandler() {
//		return (request, response, accessDeniedException) -> {
//			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//			response.getWriter().write("Access Denied");
//		};
//	}



}