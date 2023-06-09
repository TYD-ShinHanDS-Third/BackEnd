package com.shinhan.education.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PanFavoritesId implements Serializable {

	private String panid;
	private String memberid;
	
	// 생성자, equals(), hashCode() 등의 메소드 구현
}