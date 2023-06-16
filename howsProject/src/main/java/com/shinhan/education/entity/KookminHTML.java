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
@Table(name = "kookminhtml") 
public class KookminHTML {
	
	@Id
	private String productname;
	
	private String bankname;
	
	private String starbanking;//kbstart뱅킹
	private String internet;//인터넷
	private String branch;//영업점
	private String smart;//스마트대출
	
	//인터넷, 스타뱅킹(mobile), 리브next, 영업점, 스마트대출
	@Lob
	private String summary;//상품 안내
	@Lob
	private String interestrate;//금리 및 이율
	@Lob
	private String useinfo;//이용 안내
	@Lob
	private String catuion;//유의 및 기타사항
	@Lob
	private String download;//다운로드
	

}
