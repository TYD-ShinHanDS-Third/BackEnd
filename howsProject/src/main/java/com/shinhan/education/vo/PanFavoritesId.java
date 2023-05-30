package com.shinhan.education.vo;

import java.io.Serializable;

import lombok.Data;
@Data
public class PanFavoritesId implements Serializable {

	private int panid;
	private String memberid;
	
	// 생성자, equals(), hashCode() 등의 메소드 구현
}