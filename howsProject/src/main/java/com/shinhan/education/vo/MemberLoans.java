package com.shinhan.education.vo;

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
    
    private String doc1;
    private String doc2;
    private String doc3;
    private String doc4;
    private String doc5;
    private String doc6;
    private String doc7;
    private String doc8;

}
