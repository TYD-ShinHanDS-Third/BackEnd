package com.shinhan.education.entity;

import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Entity
@Table(name = "pans")
public class Pans {
	@Id
	private String panid; // 공고 아이디
	private String panname;// 공고명
	private String location;// 지역명
	private Date panstartdate;// 공고게시일
	private Date panenddate;// 공고마감일
	private String panstate;// 공고상태
	private String panurl;// 공고상세URL


	
//	@JsonIgnore
//	@OneToMany(mappedBy = "pans", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//	private List<PanFavorites> panfavlist;

	@Transient
	@Builder.Default
	private int like = 0;
}
