package com.shinhan.education.service;

public interface MemberLoansService {
	
    boolean updateApprovalLoanStatus(Integer memloanid);
    
    boolean updateRefusalLoanStatus(Integer memloanid);

}