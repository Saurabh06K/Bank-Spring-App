package com.aurionpro.bank.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.dto.PageResponseDto;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.exceptions.AccountNotFoundException;
import com.aurionpro.bank.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bankapp")
public class AccountController {
	@Autowired
	private AccountService accountService;

	@PostMapping("/accounts")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AccountDto> createAccount(@RequestParam int customerId, @RequestParam double balance) {
		AccountDto createdAccount = accountService.createAccount(customerId, balance);
		return ResponseEntity.ok(createdAccount);
	}

	@GetMapping("/accounts")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<AccountDto> getAccountById(@RequestParam Long accountNumber) {
		try {
			AccountDto account = accountService.getAccountById(accountNumber);
			return new ResponseEntity<>(account, HttpStatus.OK);
		} catch (AccountNotFoundException e) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/accounts/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PageResponseDto<AccountDto>> getAllAccounts(@RequestParam int pageNumber,
			@RequestParam int pageSize) {
		PageResponseDto<AccountDto> accounts = accountService.getAllAccounts(pageNumber, pageSize);
		return ResponseEntity.ok(accounts);
	}

//	@PutMapping("/accounts/{accountNumber}")
//	public ResponseEntity<AccountDto> updateAccountById(@PathVariable Long accountNumber,
//			@RequestBody AccountDto accountDetails) {
//		try {
//			AccountDto updatedAccount = accountService.updateAccountById(accountNumber, accountDetails);
//			return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
//		} catch (AccountNotFoundException e) {
//			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
//		}
//	}
//

}
