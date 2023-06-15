package com.shinhan.education.respository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shinhan.education.vo.ChatInfo;
import com.shinhan.education.vo.ChatRoom;

public interface ChatInfoRepository extends CrudRepository<ChatInfo, Long>{
	
	List<ChatInfo> findByChatroomOrderByTime (ChatRoom room);
	
}


