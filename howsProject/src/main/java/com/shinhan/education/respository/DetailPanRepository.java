package com.shinhan.education.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinhan.education.vo.DetailPans;
import com.shinhan.education.vo.Pans;

public interface DetailPanRepository extends JpaRepository<DetailPans, String>{
	
	
}


