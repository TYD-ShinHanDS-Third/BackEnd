package com.shinhan.education.dto;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.shinhan.education.entity.Members;
import com.shinhan.education.vo.MemberLevel;
import com.shinhan.education.vo.Role;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // 기본 생성자 추가
public class MemberSignUpRequest {

	private String memberid; // 회원아이디
	private String membername; // 이름
	private String pswd; // 비밀번호
	private String checkedpswd; // 비밀번호 확인
	private Date bday; // 생년월일
	private String phone; // 휴대폰 번호
	private Role roles; // 역할
	private String accBank; // 계좌은행
	private String accno; // 계좌
	private Integer hasjob; // 직업유무
	private String hiredate; // 입사일
	private Integer marry; // 결혼유무
	private Integer haschild; // 자녀 수
	private String jobname; // 직장명
	private MemberLevel memberLevel; // 회원등급
	private String email; //이메일
	 @CreationTimestamp
	 @Temporal(TemporalType.TIMESTAMP)
	 @Column(name = "join_date")
	 private Date joindate;

	public Members toEntity() {
		return Members.builder().memberid(memberid).membername(membername).pswd(pswd).bday(bday).phone(phone)
				.roles(roles).accBank(accBank).accno(accno).hasjob(hasjob).hiredate(hiredate).marry(marry)
				.haschild(haschild).jobname(jobname).email(email).joindate(joindate).build();
	}

	public MemberSignUpRequest(String memberid, String membername, String pswd, String checkedpswd, Date bday,
			String phone, Role roles, String accBank, String accno, Integer hasjob, String hiredate, Integer marry,
			Integer haschild, String jobname, MemberLevel memberLevel, String email, Date joindate) {
		this.memberid = memberid;
		this.membername = membername;
		this.pswd = pswd;
		this.checkedpswd = checkedpswd;
		this.bday = bday;
		this.phone = phone;
		this.roles = roles;
		this.accBank = accBank;
		this.accno = accno;
		this.hasjob = hasjob;
		this.hiredate = hiredate;
		this.marry = marry;
		this.haschild = haschild;
		this.jobname = jobname;
		this.memberLevel = memberLevel;
		this.email = email;
		this.joindate=joindate;
	}

	// 추가 정보를 반환하는 메서드
	public String getAdditionalInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("계좌은행: ").append(accBank);
		sb.append(", 계좌: ").append(accno);
		sb.append(", 직업유무: ").append(hasjob);
		sb.append(", 입사일: ").append(hiredate);
		sb.append(", 결혼유무: ").append(marry);
		sb.append(", 자녀 수: ").append(haschild);
		sb.append(", 직장명: ").append(jobname);
		return sb.toString();
	}

	public boolean hasAdditionalInfo() {
		// accBank, accno, hasjob, hiredate, marry, haschild 중 모두 null이 아니면 추가 정보가 있다고
		// 판단
	//	return accBank != null && accno != null && hasjob != null && hiredate != null && marry != null && haschild != null;
		if (accBank != null) {
		    System.out.println("1");
		    if (accno != null) {
		        System.out.println("2");
		        if (hasjob != null) {
		            System.out.println("3");
		            if (hiredate != null) {
		                System.out.println("4");
		                if (marry != null) {
		                    System.out.println("5");
		                    if (haschild != null) {
		                    	System.out.println("6");
		                        // All conditions are met
		                        // Add your logic here
		                    	return true;
		                    }
		                    // haschild is null
		                    // Handle the condition where haschild is null
		                }
		                // marry is null
		                // Handle the condition where marry is null
		            }
		            // hiredate is null
		            // Handle the condition where hiredate is null
		        }
		        // hasjob is null
		        // Handle the condition where hasjob is null
		    }
		    // accno is null
		    // Handle the condition where accno is null
		}
		// accBank is null
		// Handle the condition where accBank is null
		return false;
	}

	public void setMemberLevel(MemberLevel memberLevel) {
		this.memberLevel = memberLevel;
	}

}