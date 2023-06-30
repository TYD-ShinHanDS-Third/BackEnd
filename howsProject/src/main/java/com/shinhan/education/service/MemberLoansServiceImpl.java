package com.shinhan.education.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shinhan.education.entity.MemberLoans;
import com.shinhan.education.respository.MemberRepository;
import com.shinhan.education.respository.MemberStateLoansRepository;

@Transactional
@Service
public class MemberLoansServiceImpl implements MemberLoansService {
	
    private final MemberStateLoansRepository memberStateLoansRepository;

    public MemberLoansServiceImpl(MemberStateLoansRepository memberStateLoansRepository ) {
        this.memberStateLoansRepository = memberStateLoansRepository;
    }
    // 은행원이 대출 승인 시 대출상태 변경
    @Override
    public boolean updateApprovalLoanStatus(Integer memloanid) {
        Optional<MemberLoans> optionalMemberLoan = memberStateLoansRepository.findById(memloanid);
        if (optionalMemberLoan.isPresent()) {
            MemberLoans memberLoan = optionalMemberLoan.get();
            memberLoan.setLoanstate("대출승인완료");
            memberStateLoansRepository.save(memberLoan);
            return true;
        }
        return false;
    }
   
    // 은행원이 및 관리자가 대출 거절 시 대출상태 변경
    @Override
    public boolean updateRefusalLoanStatus(Integer memloanid) {
        Optional<MemberLoans> optionalMemberLoan = memberStateLoansRepository.findById(memloanid);
        if (optionalMemberLoan.isPresent()) {
            MemberLoans memberLoan = optionalMemberLoan.get();
            // 대출 상태 업데이트
            memberLoan.setLoanstate("대출심사 부적격");
            memberStateLoansRepository.save(memberLoan);
            return true;
        }
        return false;
    }
    
    // 관리자가 승인시 승인상태 변경
    @Override
    public boolean adminupdateApprovalLoanStatus(Integer memloanid) {
        Optional<MemberLoans> optionalMemberLoan = memberStateLoansRepository.findById(memloanid);
        if (optionalMemberLoan.isPresent()) {
            MemberLoans memberLoan = optionalMemberLoan.get();
            memberLoan.setLoanstate("대출 승인대기");
            memberStateLoansRepository.save(memberLoan);
            return true;
        }
        return false;
    }
    
    // 관리자가 대출상담 종류 후 대출상태 변경
    @Override
    public boolean adminchatendLoanStatus(Integer memloanid) {
        Optional<MemberLoans> optionalMemberLoan = memberStateLoansRepository.findById(memloanid);
        if (optionalMemberLoan.isPresent()) {
            MemberLoans memberLoan = optionalMemberLoan.get();
            memberLoan.setLoanstate("대출상담완료");
            memberStateLoansRepository.save(memberLoan);
            return true;
        }
        return false;
    }
   
}