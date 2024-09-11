package com.aurionpro.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aurionpro.bank.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findByCustomer_Email(String email);
}
