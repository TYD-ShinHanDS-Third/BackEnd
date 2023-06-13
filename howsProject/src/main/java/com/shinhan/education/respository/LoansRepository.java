package com.shinhan.education.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.shinhan.education.vo.Loans;

public interface LoansRepository extends CrudRepository<Loans, String>{

	Page<Loans> findAll(Pageable pageable);
	
	
	
}


