package com.aurionpro.bank.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.dto.PageResponseDto;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.enums.TransactionType;
import com.aurionpro.bank.exceptions.TransactionNotFoundException;
import com.aurionpro.bank.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bankapp/transactions")
public class TransactionController {
	@Autowired
	private TransactionService transactionService;

	@PostMapping("/deposit")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> deposit(@Valid @RequestParam long accountNumber, @RequestParam double amount) {
		transactionService.deposit(accountNumber, amount, accountNumber);
		return ResponseEntity.ok("Amount deposited successfully.");
	}

	@PostMapping("/withdraw")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> withdraw(@Valid @RequestParam long accountNumber, @RequestParam double amount) {
		try {
			transactionService.withdraw(accountNumber, amount, accountNumber);
			return ResponseEntity.ok("Amount withdrawn successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/transfer")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> transfer(@Valid @RequestParam long fromAccountNumber,
			@RequestParam long toAccountNumber, @RequestParam double amount) {
		try {
			transactionService.transfer(fromAccountNumber, toAccountNumber, amount);
			return ResponseEntity.ok("Amount transferred successfully.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/filtered")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<TransactionDto>> getFilteredTransactions(
			@RequestParam(required = false) long accountNumber,
			@RequestParam(required = false) String typeOfTransaction, @RequestParam(required = false) Double minAmount,
			@RequestParam(required = false) Double maxAmount) {

		List<TransactionDto> filteredTransactions = transactionService.getFilteredTransactions(accountNumber,
				typeOfTransaction, minAmount, maxAmount);

		return ResponseEntity.ok(filteredTransactions);
	}

	@GetMapping("/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PageResponseDto<TransactionDto>> getAllTransactions(@RequestParam int page,
			@RequestParam int size) {

		PageResponseDto<TransactionDto> transactionsPage = transactionService.getAllTransactions(page, size);
		return new ResponseEntity<>(transactionsPage, HttpStatus.OK);
	}

	@GetMapping("/passbook")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<List<TransactionDto>> viewPassbook() {
		List<TransactionDto> transactions = transactionService.viewPassbook();
		return new ResponseEntity<>(transactions, HttpStatus.OK);
	}
}
