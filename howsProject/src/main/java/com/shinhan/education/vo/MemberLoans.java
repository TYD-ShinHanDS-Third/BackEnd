package com.shinhan.education.vo;

import java.sql.Blob;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
	
   // @JsonIgnore
    @ManyToOne
	Loans loanname; //대출상품명(fk)
    
	private String loanstate;//대출진행상태
	private String applyurl;//대출신청url
	private String exlimit;//예상한도
	
	
   // @JsonIgnore
    @ManyToOne
    private Members memberid; // 회원아이디
    
   //BLOB타입으로 써야 pdf등 이진파일 내용을 저장할 수 있다. 
    private Blob leaseContract; //확정 일자부 임대차(전세)계약서
    private Blob propertyRegistration; //임차주택 건물 등기부등본
    private Blob depositReceipt; //임차보증금 5%이상 납입한 영수증
    private Blob residenceRegistration; //주민등록등본
    private Blob identificationCard; //신분증
    private Blob incomeProof; //소득증빙자료
    private Blob marriageProof; //결혼예정 증빙서류
    private Blob employmentProof; //근로자인 경우 건강보험자격득실확인서 or 근로소득원천징수영수증:
    private Blob businessProof; // 자영업자인 경우 사업자등록증명원 or 소득금액증명원
    private Blob interestLimitDocuments; //금리 및 한도우대를 받고자 하는 서류

}
