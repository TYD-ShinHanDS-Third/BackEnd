package com.shinhan.education.respository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shinhan.education.vo.Pans;

public interface PanRepository extends JpaRepository<Pans, String>{
	Page<Pans> findAll(Pageable pageable);
	List<Pans> findByLocationContaining(String location, Pageable pageable);//지역별 공고 가져오기	
	
	
}


