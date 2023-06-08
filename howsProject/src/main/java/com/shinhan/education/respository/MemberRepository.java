package com.shinhan.education.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinhan.education.vo.Members;

public interface MemberRepository extends JpaRepository<Members, String> {
	//메서드는 회원 ID를 기준으로 회원을 조회
	Optional<Members> findByMemberid(String memberid);
}
