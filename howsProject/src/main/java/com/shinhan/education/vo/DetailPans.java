package com.shinhan.education.vo;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"pan","fileinfolist"})
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "detailpans")
public class DetailPans implements Serializable{


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long detailpanid;// 공고아이디
	
	private Date applicationstartdate;// 접수기간시작일
	private Date applicationenddate;// 접수기간종료일
	private Date winnersannouncement;// 당첨자발표일
	@JsonIgnore
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "panid", referencedColumnName = "panid")
	private Pans pan;
	
	private Date docstartdate;// 서류접수기간시작일
	private Date docannouncement;// 서류제출대상자발표일
	private Date docenddate;// 서류접수기간종료일

	private Date contractstartdate;// 계약기간시작일
	private Date contractenddate;// 계약기간종료일
	//

	@JsonIgnore
	@OneToMany(mappedBy = "detailpans",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<FileInfos> fileinfolist;

	private String area;// 전용면적
	private String address;// 단지주소
	private String detailaddress;// 단지상세주소
	private String addressname;// 단지명
	private Date moveindate;// 입주예정월
	private String totalcount;// 총세대수

//	@OneToOne(mappedBy = "detailpan", cascade = CascadeType.ALL)
//	Pans pan;

}
