package com.aurionpro.bank.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.dto.PageResponseDto;
import com.aurionpro.bank.entity.Account;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.enums.TransactionType;
import com.aurionpro.bank.exceptions.AccountNotFoundException;
import com.aurionpro.bank.exceptions.CustomerNotFoundException;
import com.aurionpro.bank.repository.AccountRepository;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.TransactionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AccountServiceImpl implements AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	CustomerRepository customerRepo;

	@Autowired
	TransactionRepository transactionRepo;

	@Autowired
	private EmailService emailService;

	@Override
	public AccountDto createAccount(int customerId, double balance) {
		Customer customer = customerRepo.findById(customerId).orElseThrow(() -> {
			logger.error("Customer not found for ID: {}", customerId);
			return new CustomerNotFoundException("Customer not found for ID " + customerId);
		});

		Account account = new Account();
		Long accountNumber = generateAccountNumber();
		account.setAccountNumber(accountNumber);
		account.setCustomer(customer);
		account.setFirstName(customer.getFirstName());
		account.setLastName(customer.getLastName());
		account.setBalance(balance);
		System.out.println("Customer First Name: " + customer.getFirstName());
		System.out.println("Customer Last Name: " + customer.getLastName());
		account = accountRepo.save(account);
		logger.info("Account created successfully with account number: {}", accountNumber);
		emailService.sendAccountCreationEmail(account.getCustomer().getEmail(), account.getCustomer().getFirstName(),
				account.getAccountNumber(), account.getBalance());
		return toAccountDto(account);
	}

	@Override
	public AccountDto getAccountById(Long accountNumber) {
		logger.info("Fetching account details for account number: {}", accountNumber);
		Account account = accountRepo.findById(accountNumber).orElseThrow(() -> {
			logger.error("Account not found for account number: {}", accountNumber);
			return new AccountNotFoundException("Account not found for ID " + accountNumber);
		});
		return toAccountDto(account);
	}

	@Override
	public PageResponseDto<AccountDto> getAllAccounts(int pageNumber, int pageSize) {
		logger.info("Fetching all accounts");

		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		Page<Account> accountPage = accountRepo.findAll(pageable);

		List<AccountDto> accountDtos = accountPage.getContent().stream().map(this::toAccountDto)
				.collect(Collectors.toList());

		PageResponseDto<AccountDto> pageResponse = new PageResponseDto<>(accountPage.getTotalPages(),
				accountPage.getTotalElements(), accountPage.getSize(), accountDtos, accountPage.isLast());

		return pageResponse;
	}

	@Override
	public AccountDto updateAccountById(Long accountNumber, AccountDto accountDetails) {
		Optional<Account> optionalAccount = accountRepo.findById(accountNumber);
		if (!optionalAccount.isPresent()) {
			throw new AccountNotFoundException("Account not found for ID");
		}

		Account dbAccount = optionalAccount.get();
		dbAccount.setFirstName(accountDetails.getFirstName());
		dbAccount.setLastName(accountDetails.getLastName());
		dbAccount.setBalance(accountDetails.getBalance());

		return toAccountDto(dbAccount);
	}

	private Account toAccount(AccountDto accountDto) {
		Account account = new Account();
		account.setAccountNumber(accountDto.getAccountNumber());
		account.setBalance(accountDto.getBalance());
		account.setFirstName(accountDto.getFirstName());
		account.setLastName(accountDto.getLastName());
		return account;
	}

	private AccountDto toAccountDto(Account account) {
		AccountDto accountDto = new AccountDto();
		accountDto.setAccountNumber(account.getAccountNumber());
		accountDto.setBalance(account.getBalance());
		accountDto.setFirstName(account.getFirstName());
		accountDto.setLastName(account.getLastName());
		return accountDto;
	}

	private Long generateAccountNumber() {
		Random random = new Random();

		// Generate a random 10-digit number
		Long accountNumber = 1000000000L + (long) (random.nextDouble() * 9000000000L);

		// Ensure that the generated account number is unique in the database
		while (accountRepo.existsById(accountNumber)) {
			accountNumber = 1000000000L + (long) (random.nextDouble() * 9000000000L);
		}

		return accountNumber;
	}

}
