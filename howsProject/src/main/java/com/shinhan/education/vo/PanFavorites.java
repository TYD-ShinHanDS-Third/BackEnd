package com.shinhan.education.vo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@Entity
@AllArgsConstructor
@ToString(exclude = {"pans","mems"})
@NoArgsConstructor
@IdClass(PanFavoritesId.class)
@Table(name = "PANFAVORITES")
public class PanFavorites {
	
	@Id
	private String panid; // 모집공고번호
	
	@Id
	private String memberid; // 회원아이디
	
    // Pans와의 관계 설정
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "panid", referencedColumnName = "panid", insertable = false, updatable = false)
    private Pans pans; // Pan 엔티티 참조
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "memberid", referencedColumnName = "memberid", insertable = false, updatable = false)
	private Members mems; // 참조하는 Pan 엔티티
	
	@Column(name="panname")
	private String title; // 모집 공고 이름
	@Column(name="panstartdate")
	private Date start; // 공고 시작일
	@Column(name="panenddate")
	private Date end; // 공고 종료일
	
	
	@Transient
	private String panname;
}


