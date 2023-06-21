package com.shinhan.education.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shinhan.education.entity.ChatInfo;
import com.shinhan.education.entity.ChatRoom;
import com.shinhan.education.jwt.TokenToString;
import com.shinhan.education.respository.ChatInfoRepository;
import com.shinhan.education.respository.ChatRoomRepository;

@Service //// bean 등록
@ServerEndpoint(value = "/socket/chatt/{room}",configurator = ServerEndpointConfigurator.class) //// 해당 URL로 Socket연결 (Singleton pattern)
public class WebSocketChat {
	//list를 set으로 바꿔야 중복이 없다.
	private static Map<Long, List<Session>> clients = Collections.synchronizedMap(new HashMap<>());
	// 모든 클라이언트가 다들어와버린다. 채팅방 번호를 줘서 그 채팅방 참여인운들만 볼수있게하자.

	private static Logger logger = LoggerFactory.getLogger(WebSocketChat.class);
	

	TokenToString tts = new TokenToString();
	@Autowired
	ChatRoomRepository roomRepo;
	@Autowired
	ChatInfoRepository chatinfoRepo;

	// @ServerEndpoint 에서 명시한 URL로 요청이 들어올 경우 해당 메서드가 실행되어 클라이언트의 정보를 매개변수로 전달받습니다.
	// 상수인 clients에 해당 session이 존재하지 않으면 clients에 접속된 클라이언트를 추가합니다.
	@OnOpen // 클라이언트가 접속할 때마다 실행
	public void onOpen(Session session, @PathParam("room") String chatRoomId) {
		// DB에서 과거 채팅 내역을 clent에게 보내준다.
		Long room = Long.parseLong(chatRoomId);
		logger.info("open session : {}, clients={}", session.toString(), clients);
		Map<String, List<String>> res = session.getRequestParameterMap();
		logger.info("res={}", res);

		System.err.println("room : " + room);
		// 클라 정보를 저장한다
		if (clients.containsKey(room)) {
			// 리스트에 추가하기
			clients.get(room).add(session);
			System.out.println("클라이언트 추가1");
		} else {
			List<Session> ls = new ArrayList<Session>();
			ls.add(session);
			clients.put(room, ls);
			System.out.println("클라이언트 추가2");
		}

		// 과거 데이터 전송
//		roomRepo.findById(room).ifPresent((x) -> {
//
//			List<ChatInfo> chatlist = chatinfoRepo.findByChatroomOrderByTime(x);
//			for (ChatInfo ci : chatlist) {
//				String chatHistoryJson  = null;
//				ObjectMapper mapper = new ObjectMapper();
//				try {
//					chatHistoryJson = mapper.writeValueAsString(ci);
//				} catch (JsonProcessingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				try {
//					session.getBasicRemote().sendText(chatHistoryJson);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});

	}

	// 클라이언트와 서버Socket이 연결된 상태에서, 메세지가 전달되면 해당 메서드가 실행되어 상수인 clients에 있는 모든
	// session에게 메세지를 전달합니다.
	
	
	@OnMessage // 메세지 수신 시
	public void onMessage(String message, Session session) throws IOException {
		System.err.println("웹소켓온메세지");
		// 들어온 채팅메세지와 사용자 정보를 DB저장한다.
		// 사용자 이름은 토큰으로 온다.

		// DB에 채팅 메세지와 시간을 저장한다.
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(message);
		String msg = jsonNode.get("msg").asText();
		//String token = jsonNode.get("token").asText();
		String membername = jsonNode.get("myname").asText();
		Long chatid = Long.parseLong(jsonNode.get("room").asText());
		// String memberid = tts.getMemberId(token);
		System.err.println("2 : " + chatid);
		ChatRoom cr = roomRepo.findById(chatid).get();
		if (cr == null) {
			System.out.println("no chat room");
			return;
		}
		ChatInfo ci = ChatInfo.builder().myname(membername).msg(msg).chatroom(cr).build();

		chatinfoRepo.save(ci);

		// 방에 참여 중인 모든 client들에게 메세지를 보낸다.
		logger.info("receive message : {}", message);
		// System.out.println("1 : " + message);

		// 참여중인 클라이언트에게 전송하자
		
		List<Session> slist= clients.get(chatid);
		System.err.println("slist : " + slist);
		for (Session s : slist) {
			System.out.println("데이터 전송시작");
			logger.info("메세지 : test send data : {}", message);
			System.out.println("메세지 : " + message);
			s.getBasicRemote().sendText(message);
			System.out.println("데이터 전송완료");
		}

	}

	// 클라이언트가 URL을 바꾸거나 브라우저를 종료하면 해당 메서드가 실행되어 클라이언트의 세션정보를 clients에서 제거합니다.
	@OnClose // 클라이언트가 접속을 종료할 시
	public void onClose(Session session, @PathParam("room") String chatRoomId) {
		logger.info("session close : {}", session);
		Long room = Long.parseLong(chatRoomId);
		clients.remove(room);
	}
}
