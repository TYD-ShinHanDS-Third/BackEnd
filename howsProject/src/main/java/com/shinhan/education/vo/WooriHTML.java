package com.shinhan.education.vo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wooriHTML")
public class WooriHTML {
	@Id
	private String productname;

	private String bankname;

	private String types;

	@Lob
	private String html1;
	@Lob
	private String html2;
	@Lob
	private String html3;
	@Lob
	private String html4;
	@Lob
	private String html5;
	@Lob
	private String html6;
	@Lob
	private String html7;
	@Lob
	private String html8;
	@Lob
	private String html9;
	@Lob
	private String html10;
	@Lob
	private String html11;
	@Lob
	private String html12;
	@Lob
	private String html13;
	@Lob
	private String html14;
	@Lob
	private String html15;
	@Lob
	private String html16;
	@Lob
	private String html17;
	@Lob
	private String html18;
	@Lob
	private String html19;
	@Lob
	private String html20;

}
