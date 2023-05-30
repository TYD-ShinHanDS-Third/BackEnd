package com.shinhan.education.vo;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PANFAVORITES")
public class PanFavorites {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int panid; //모집공고번호
	private String memberid; // 회원아이디
	private String panname; //모집 공고 이름
	private Date panstartdate; // 공고 시작일
	private Date panenddate; //공고 종료일
}
