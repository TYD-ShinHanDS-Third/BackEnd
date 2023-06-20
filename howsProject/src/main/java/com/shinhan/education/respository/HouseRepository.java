package com.shinhan.education.respository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.shinhan.education.entity.Houses;

public interface HouseRepository extends CrudRepository<Houses, Long> {

	List<Houses> findByRoadaddress(String road);

	@Query("SELECT DISTINCT roadaddress FROM Houses")
	List<String> findDistinctRoadAddresses();

	@Query("SELECT h FROM Houses h WHERE h.roadaddress = :address")
	List<Houses> findXY(@Param("address") String address);
	
	@Query("SELECT h FROM Houses h WHERE h.xPos BETWEEN :x1 AND :x2 AND h.yPos BETWEEN :y1 AND :y2")
	List<Houses> findByXPosBetweenAndYPosBetween(BigDecimal x1, BigDecimal x2, BigDecimal y1, BigDecimal y2);


}
