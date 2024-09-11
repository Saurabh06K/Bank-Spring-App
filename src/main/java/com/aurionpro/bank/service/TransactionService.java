package com.aurionpro.bank.service;

import java.util.List;

import com.aurionpro.bank.dto.PageResponseDto;
import com.aurionpro.bank.dto.TransactionDto;

public interface TransactionService {
	void deposit(long accountNumber, double amount, long fromAccountNum);

	void withdraw(long accountNumber, double amount, long toAccountNum);

	void transfer(long fromAccountNum, long toAccountNum, double amount);

	PageResponseDto<TransactionDto> getAllTransactions(int page, int size);

	List<TransactionDto> getFilteredTransactions(long accountNumber, String typeOfTransaction, Double minAmount,
			Double maxAmount);

	List<TransactionDto> viewPassbook();
}
