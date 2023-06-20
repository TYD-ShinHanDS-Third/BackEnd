package com.shinhan.education.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
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
		http.csrf().disable();
		http.formLogin().disable();
		http.exceptionHandling();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		http.authorizeRequests()
			
			//	.antMatchers("/hows/user/**").hasRole("USER")
				.antMatchers("/hows/auth/login").hasRole("ADMIN")
		//		.antMatchers("/hows/admin/user").hasRole("ADMIN")
		//		.antMatchers("/hows/teller/**").hasRole("TELLER")
			//	.antMatchers("/hows/auth/**").permitAll()
	//			.antMatchers("/hows/auth/login").permitAll()
				.anyRequest().authenticated();
		http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		System.err.println(http.toString());
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
}