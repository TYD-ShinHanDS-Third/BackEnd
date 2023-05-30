package com.shinhan.education.vo;

import javax.persistence.Id;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shinhanhtml") 
public class Loans {
	
	@Id
	private String loanname;//상품명
	private String bankname;//은행명
}
