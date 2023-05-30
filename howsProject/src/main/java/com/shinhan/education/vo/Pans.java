package com.shinhan.education.vo;

import java.sql.Date;



import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString(exclude = "detailpan")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pans") 
public class Pans {
	@Id
	private String pandid; //공고 아이디
	private String panname;//공고명
	private String location;//지역명
	private Date panstartdate;//공고게시일
	private Date panenddate;//공고마감일
	private String panstate;//공고상태
	private String panurl;//공고상세URL

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "panid")
	DetailPans detailpan;
	
	//@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	//panfav
}
