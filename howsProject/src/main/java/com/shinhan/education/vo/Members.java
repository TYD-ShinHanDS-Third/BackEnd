package com.shinhan.education.vo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@Entity
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MEMBERS")
public class Members {

	@Id
	private String memberid; // 회원아이디
	private String membername; // 이름
	private String pswd; // 비밀번호
	private Date bday; // 생년월일
	private String phone; // 휴대폰 번호
	@Enumerated(EnumType.STRING)
	private Role roles; // 사용자, 관리자, 은행원
	private String memberlevel; // 기본정보 및 추가정보 입력여부에 따라 등급부여
	private String accBank; // 계좌은행
	private String accno; // 계좌
	private Integer hasjob; // 직업유무
	private Date hiredate; // 입사일
	private Integer marry; // 결혼유무
	private int haschild; // 자녀 수

	// 입력된 비밀번호와 저장된 비밀번호를 비교하여 유효성을 확인하는 메서드
	public boolean isPswdValid(PasswordEncoder passwordEncoder, String password) {
	    return passwordEncoder.matches(password, this.pswd);
	}
	
    public List<String> getRoleNames() {
        return Arrays.asList(roles.getRoleName());
    }
}
	

