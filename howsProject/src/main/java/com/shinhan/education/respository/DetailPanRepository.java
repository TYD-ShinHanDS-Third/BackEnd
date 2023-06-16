package com.shinhan.education.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinhan.education.entity.DetailPans;
import com.shinhan.education.entity.Pans;

public interface DetailPanRepository extends JpaRepository<DetailPans, Long>{
	
	DetailPans findByPan(Pans Pan);

}


