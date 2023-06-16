package com.shinhan.education.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shinhanHTML") 
public class ShinhanHTML {
	
	@Id
	private String productname;
	
	private String bankname;

	private String mobile;//모바일
	private String internet;//인터넷
	private String branch;//영업점
	
	//html여기서부터
	@Lob
	private String summary;//상품개요
	@Lob
	private String loancust;//대출고객
	@Lob
	private String loanlimit;//대출한도
	@Lob
	private String interestrate;//대출금리
	@Lob
	private String loanterm;//대출기간
	@Lob
	private String repaymentmethod;//상환방법
	@Lob
	private String prepaymentpenalty;//중도상한해약금
	@Lob
	private String loancosts;//대출비용
	@Lob
	private String taxbenefits;//세제/금리
	@Lob
	private String reqdocs;//필요서류
	@Lob
	private String moreinfo;//추가사항->필요서류,추가대출,담보,신청기간
	@Lob
	private String custprotection;//금융소비자보호
	@Lob
	private String caution;// ->유의사항

}

