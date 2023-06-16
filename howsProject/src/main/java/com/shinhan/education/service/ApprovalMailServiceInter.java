package com.shinhan.education.service;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

// mail 서비스 interface
public interface ApprovalMailServiceInter {

	// 메일 내용 작성
	MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException;

	// 메일 발송
	String sendSimpleMessage(String to) throws Exception;
}