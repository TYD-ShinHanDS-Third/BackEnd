package com.shinhan.education.entity;

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
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hanaHTML") 
public class HanaHTML {
	
	
	@Id
	private String productname;
	
	private String bankname;
	
	private String internet;
	private String branch;
	
	
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
	@Lob
	private String html21;
	@Lob
	private String html22;
	@Lob
	private String html23;
	@Lob
	private String html24;
	@Lob
	private String html25;
	@Lob
	private String html26;
	@Lob
	private String html27;
	@Lob
	private String html28;
	@Lob
	private String html29;
	@Lob
	private String html30;
	
}
