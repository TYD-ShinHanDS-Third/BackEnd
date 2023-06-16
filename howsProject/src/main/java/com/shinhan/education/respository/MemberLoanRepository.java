package com.shinhan.education.respository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shinhan.education.entity.Loans;
import com.shinhan.education.entity.MemberLoans;
import com.shinhan.education.entity.Members;

public interface MemberLoanRepository extends JpaRepository<MemberLoans, Integer> {

	List<MemberLoans> findByMemberid(Members memberid);

	@Query("select ml from MemberLoans ml, Loans l, Members m "
			+ "where ml.memberid = m.memberid and l.loanname = ml.loanname and ml.loanstate = :stateA")
	Page<MemberLoans> findByState(@Param("stateA") String stateA, Pageable pageable);

	List<MemberLoans> findByMemberidAndLoanname(Members member, Loans loan);

}
