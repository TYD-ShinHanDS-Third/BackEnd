package com.shinhan.education.respository;

import org.springframework.data.repository.CrudRepository;

import com.shinhan.education.entity.FileInfos;
import com.shinhan.education.entity.HanaHTML;
import com.shinhan.education.entity.WooriHTML;

public interface FileInfoRepository extends CrudRepository<FileInfos, Long>{
	
	
	
}


