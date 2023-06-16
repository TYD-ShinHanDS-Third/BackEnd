package com.shinhan.education.respository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shinhan.education.entity.Houses;

public interface HouseRepository extends CrudRepository<Houses, Long>{

	List<Houses> findByRoadaddress(String road);
	
	
	@Query("SELECT DISTINCT roadaddress FROM Houses")
	List<String> findDistinctRoadAddresses();
}


