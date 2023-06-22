package com.shinhan.education.respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shinhan.education.entity.MemberLoans;

public interface MemberStateLoansRepository extends JpaRepository<MemberLoans, Integer> {
    MemberLoans findByMemloanid(Integer memloanid);
}