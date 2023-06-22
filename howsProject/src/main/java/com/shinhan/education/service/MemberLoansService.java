package com.shinhan.education.service;

public interface MemberLoansService {
	
	// 은행원이 대출 승인시
    boolean updateApprovalLoanStatus(Integer memloanid);
    
    // 은행원이 및 관리자가 대출 거절시
    boolean updateRefusalLoanStatus(Integer memloanid);
    
    // 관리자가 대출 승인시
    boolean adminupdateApprovalLoanStatus(Integer memloanid);

}