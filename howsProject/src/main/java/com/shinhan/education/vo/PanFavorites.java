package com.shinhan.education.vo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
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
@IdClass(PanFavoritesId.class)
@Table(name = "PANFAVORITES")
public class PanFavorites {
	
	@Id
	private String  panid; // 모집공고번호
	
	@Id
	private String memberid; // 회원아이디
	
	@ManyToOne
	@JoinColumn(name = "panid", referencedColumnName = "panid", insertable = false, updatable = false)
	private Pans pans; // 참조하는 Pan 엔티티
	
	@ManyToOne
	@JoinColumn(name = "memberid", referencedColumnName = "memberid", insertable = false, updatable = false)
	private Members mems; // 참조하는 Pan 엔티티
	
	private String panname; // 모집 공고 이름
	private Date panstartdate; // 공고 시작일
	private Date panenddate; // 공고 종료일
}


