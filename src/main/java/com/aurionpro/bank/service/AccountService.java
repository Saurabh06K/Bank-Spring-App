package com.aurionpro.bank.service;

import java.util.List;

import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.dto.PageResponseDto;
import com.aurionpro.bank.dto.TransactionDto;

public interface AccountService {
	AccountDto createAccount(int customerId, double balance);

	AccountDto getAccountById(Long accountNumber);

	PageResponseDto<AccountDto> getAllAccounts(int pageNumber, int pageSize);

	AccountDto updateAccountById(Long accountNumber, AccountDto accountDetails);

}
