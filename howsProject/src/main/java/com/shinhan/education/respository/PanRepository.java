package com.shinhan.education.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinhan.education.vo.Pans;

public interface PanRepository extends JpaRepository<Pans, String>{
	
	List<Pans> findByLocationContaining(String location);//지역별 공고 가져오기	
	
}


