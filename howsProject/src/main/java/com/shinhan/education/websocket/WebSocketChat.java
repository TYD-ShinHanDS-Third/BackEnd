package com.shinhan.education.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shinhan.education.jwt.TokenToString;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

@Service ////bean 등록
@ServerEndpoint("/socket/chatt") ////해당 URL로 Socket연결 (Singleton pattern)
public class WebSocketChat {
    private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
    //모든 클라이언트가 다들어와버린다. 채팅방 번호를 줘서 그 채팅방 참여인운들만 볼수있게하자.
    
    private static Logger logger = LoggerFactory.getLogger(WebSocketChat.class);

    TokenToString tts = new TokenToString();
    
    //@ServerEndpoint 에서 명시한 URL로 요청이 들어올 경우 해당 메서드가 실행되어 클라이언트의 정보를 매개변수로 전달받습니다.
    //상수인 clients에 해당 session이 존재하지 않으면 clients에 접속된 클라이언트를 추가합니다.
    @OnOpen //클라이언트가 접속할 때마다 실행
    public void onOpen(Session session) {
    	//DB에서 과거 채팅 내역을 clent에게 보내준다.
    	
        logger.info("open session : {}, clients={}", session.toString(), clients);
        Map<String, List<String>> res = session.getRequestParameterMap();
        logger.info("res={}", res);

        if(!clients.contains(session)) {
            clients.add(session);
            logger.info("session open : {}", session);
        }else{
            logger.info("이미 연결된 session");
        }
    }

    
    //클라이언트와 서버Socket이 연결된 상태에서, 메세지가 전달되면 해당 메서드가 실행되어 상수인 clients에 있는 모든 session에게 메세지를 전달합니다.
    @OnMessage //메세지 수신 시
    public void onMessage(Long roomid, String token, String message, Session session) throws IOException {
    	//들어온 채팅메세지와 사용자 정보를 DB저장한다.
    	//사용자 이름은 토큰으로 온다.
    	
    	//DB에 채팅 메세지와 시간을 저장한다.
    	String memberid = tts.getMemberId(token);
    	//Chatinfo ci = Chatinfo.builder().memberid(memberid).message(message).roomid(roomid).build();
    	//chatinfoRepo.save(ci);
    	
    	
    	
        logger.info("receive message : {}", message);
        
        for (Session s : clients) {
            logger.info("send data : {}", message);
            s.getBasicRemote().sendText(message);
            //해당 채팅방 사람들에게민 보내줘야한다.
        }
    }
    // 클라이언트가 URL을 바꾸거나 브라우저를 종료하면 해당 메서드가 실행되어 클라이언트의 세션정보를 clients에서 제거합니다.
    @OnClose //클라이언트가 접속을 종료할 시
    public void onClose(Session session) {
        logger.info("session close : {}", session);
        clients.remove(session);
    }
}
