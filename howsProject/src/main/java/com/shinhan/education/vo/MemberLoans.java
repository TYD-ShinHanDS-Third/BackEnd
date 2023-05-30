package com.shinhan.education.vo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "MEMBERLOANS")
public class MemberLoans {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int memloanid; //대출코드(pk)
	private String loanname; //대출상품명(fk)
	@ManyToOne
	@JoinColumn(name = "memid")
	private String member;//회원id(fk)
	private String loanstate;//대출진행상태
	private String applyurl;//대출신청url
	private String exlimit;//예상한도
}
