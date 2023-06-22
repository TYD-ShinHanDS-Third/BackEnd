package com.shinhan.education.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shinhan.education.entity.MemberLoans;
import com.shinhan.education.respository.MemberStateLoansRepository;

@Transactional
@Service
public class MemberLoansServiceImpl implements MemberLoansService {
	
    private final MemberStateLoansRepository memberStateLoansRepository;

    public MemberLoansServiceImpl(MemberStateLoansRepository memberStateLoansRepository) {
        this.memberStateLoansRepository = memberStateLoansRepository;
    }

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
}