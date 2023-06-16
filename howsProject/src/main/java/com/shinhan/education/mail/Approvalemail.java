package com.shinhan.education.mail;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.shinhan.education.service.ApprovalMailServiceInter;
import com.shinhan.education.service.MailServiceInter;

@Service
public class Approvalemail implements ApprovalMailServiceInter {

	@Autowired
	JavaMailSender emailsender; // Bean 등록해둔 MailConfig 를 emailsender 라는 이름으로 autowired

	private String ePw; // 인증번호

	// 메일 내용 작성
	@Override
	public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
//		System.out.println("보내는 대상 : " + to);
//		System.out.println("인증 번호 : " + ePw);
		
		MimeMessage message = emailsender.createMimeMessage();

		message.addRecipients(RecipientType.TO, to);// 보내는 대상
		message.setSubject("How's 계정권한 승인 완료 ");// 제목

		String msgg = "";
		msgg += "<div style='margin:100px;'>";
		msgg += "<h1> 안녕하세요</h1>";
		msgg += "<h1> How's 관리자입니다</h1>";
		msgg += "<br>";
		msgg += "<p>귀하의 재직증명서가 정상적으로 확인되었습니다.<p>";
		msgg += "<br>";
		msgg += "<p>이용해주세서 감사합니다.<p>";
		msgg += "<br>";
		//msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";;
		msgg += "<div style='font-size:130%'>";
		msgg += "</div>";
		message.setText(msgg, "utf-8", "html");// 내용, charset 타입, subtype
		// 보내는 사람의 이메일 주소, 보내는 사람 이름
		message.setFrom(new InternetAddress("ckdrua76@naver.com", "HOW's_Admin"));// 보내는 사람

		return message;
	}

	// 메일 발송
	// sendSimpleMessage 의 매개변수로 들어온 to 는 곧 이메일 주소가 되고,
	// MimeMessage 객체 안에 내가 전송할 메일의 내용을 담는다.
	// 그리고 bean 으로 등록해둔 javaMail 객체를 사용해서 이메일 send!!
	@Override
	public String sendSimpleMessage(String to) throws MessagingException, UnsupportedEncodingException {
	    MimeMessage message = null;
	    try {
	        message = createMessage(to); // 메일 발송
	        emailsender.send(message);
	        return "Success"; // 메일 발송 성공 시 "Success" 반환
	    } catch (MailException | MessagingException | UnsupportedEncodingException e) {
	        e.printStackTrace();
	        return "Failure"; // 메일 발송 실패 시 "Failure" 반환
	    }
	}
}
