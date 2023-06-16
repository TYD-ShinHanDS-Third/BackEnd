package com.shinhan.education.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
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
	@Enumerated(EnumType.STRING)
	@Column(name = "memberlevel")
	private MemberLevel memberLevel; // 기본정보 및 추가정보 입력여부에 따라 등급부여(SILVERUSER -> 기본정보만 입력한 유저, GOLDUSER -> 추가정보까지 입력한 유저)
	private String accBank; // 계좌은행
	private String accno; // 계좌
	private String jobname; // 직장명
	private Integer hasjob; // 직업유무
	private Date hiredate; // 입사일
	private Integer marry; // 결혼유무
	private Integer haschild; // 자녀 수
	private String email; //이메일(관리자,은행원)
	 @CreationTimestamp
	 @Temporal(TemporalType.TIMESTAMP)
	 @Column(name = "join_date")
	 private Date joindate;

	// 입력된 비밀번호와 저장된 비밀번호를 비교하여 유효성을 확인하는 메서드
	public boolean isPswdValid(PasswordEncoder passwordEncoder, String password) {
		return passwordEncoder.matches(password, this.pswd);
	}

	public List<String> getRoleNames() {
		return Arrays.asList(roles.getRoleName());
	}
	// java.sql.Date로 변환하여 저장
	public void setBday(Date bday) {
	
		this.bday = new java.sql.Date(bday.getTime());
	}
	// java.sql.Date로 변환하여 저장
	public void sethiredate(Date hiredate) {
		
		this.hiredate = new java.sql.Date(bday.getTime());
	}
	//회원의 역할을 설정함
	public void setRoles(List<String> roleNames) {
	    if (!roleNames.isEmpty()) {
	        this.roles = Role.valueOf(roleNames.get(0));
	    }
	}

}
