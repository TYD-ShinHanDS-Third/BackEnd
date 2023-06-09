package com.shinhan.education.respository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.shinhan.education.entity.Loans;

public interface LoansRepository extends CrudRepository<Loans, String>{

	Page<Loans> findAll(Pageable pageable);
	
	Page<Loans> findByBankname(String bankname, Pageable pageable);
	
}


