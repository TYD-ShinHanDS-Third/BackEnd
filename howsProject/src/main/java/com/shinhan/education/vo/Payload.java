package com.shinhan.education.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class Payload {
	private String sub;
    private List<String> roles;
    private long iat;
    private long exp;
}
