package com.shinhan.education.respository;

import org.springframework.data.repository.CrudRepository;

import com.shinhan.education.vo.ChatRoom;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long>{
	
	ChatRoom findByMemloanid(Integer integer);
	
}


