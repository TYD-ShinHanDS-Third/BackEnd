package com.shinhan.education.respository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shinhan.education.vo.Pans;

public interface PanRepository extends JpaRepository<Pans, String> {
	Page<Pans> findAll(Pageable pageable);

	Page<Pans> findByLocationContaining(String location, Pageable pageable);// 지역별 공고 가져오기
	
	@Query("SELECT p FROM Pans p WHERE p.panstate <> :panstate")
	Page<Pans> findByPanstate(String panstate, Pageable pageable);

	@Query("SELECT p FROM Pans p JOIN PanFavorites pf ON p.panid = pf.panid WHERE pf.memberid = :memberid")
	Page<Pans> findPansByMemberId(@Param("memberid") String memberid, Pageable pageable);

	@Query("SELECT p FROM Pans p JOIN PanFavorites pf ON p.panid = pf.panid WHERE pf.memberid = :memberid AND p.panstate <> :panstate")
	Page<Pans> findPansByMemberIdAndPanstate(@Param("memberid") String memberid, @Param("panstate") String panstate,
			Pageable pageable);

	@Query("SELECT count(p) FROM Pans p")
	int countAllPans();
}
