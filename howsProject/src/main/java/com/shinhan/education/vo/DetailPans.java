package com.shinhan.education.vo;

import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = "detailpans") 
public class DetailPans {
	
	@Id
	private String panid;//공고아이디
	private Date ApplicationStartDate;//접수기간시작일
	private Date ApplicationEndDate;//접수기간종료일
	private Date WinnersAnnouncement;//당첨자발표일
	
	private Date DocStartDate;//서류접수기간시작일
	private Date DocAnnouncement;//서류제출대상자발표일
	private Date DocEndDate;//서류접수기간종료일
	
	private Date ContractStartDate;//계약기간시작일
	private Date ContractEndDate;//계약기간종료일
	//
	private String FILETYPE;//파일구분명
	private String FILENAME;//첨부파일명
	private String FILEURL;//다운로드
	
	private String AREA;//전용면적
	private String ADDRESS;//단지주소
	private String DETAILADDRESS;//단지상세주소
	private String ADDRESSNAME;//단지명
	private Date MOVEINDATE;//입주예정월
	private String TOTALCOUNT;//총세대수
	
	
	@OneToOne(mappedBy = "detailpan", cascade = CascadeType.ALL)
	Pans pan;

}
