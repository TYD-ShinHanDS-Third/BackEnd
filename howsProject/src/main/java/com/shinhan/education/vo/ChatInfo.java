package com.shinhan.education.vo;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Table(name = "CHATINFO")
@Builder
public class ChatInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long chatid; // 채팅 번호
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chat_room_id")
	private ChatRoom chatroom; // 채팅방
	
	private String sender; // 채팅을 보낸 사람
	private String message; // 메시지 내용
	
	@CreationTimestamp
	private Timestamp time; // 채팅 발송 시간
}