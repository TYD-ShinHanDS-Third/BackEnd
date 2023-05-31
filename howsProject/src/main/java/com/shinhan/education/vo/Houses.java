package com.shinhan.education.vo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "houses") 
public class Houses {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String housecode; //auto id
	private String si;
	private String sigungu;
	private String roadaddress;
	private Integer housecount;
	private String supplytype;
	private Integer style;
	private Double supplyarea
	;
	private Integer rent;
	private Integer monthrent;
	
	
//	- HOUSECODE: AUTO(PK)
//	- SI:"서울특별시" 광역시도명
//	- SIGUNGU":"중구", 시군구명
//	- ROADADDRESS:"서울특별시 중구 청구로17길 80", 도로명주소
//	- HOUSECOUNT:14, 세대수
//	- SUPPLYTYPE: "매입임대", 공급유형명
//	- STYLE:"47",  형 명
//	- SUPPLYAREA: 47.73, 공급 전용 면적
//	- "Rent":2000000, 기본 임대보증금
//	- "MONTHRENT":889880 기본 월임대료
	

}
