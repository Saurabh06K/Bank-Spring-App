package com.aurionpro.bank.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.aurionpro.bank.dto.AccountDto;
import com.aurionpro.bank.dto.PageResponseDto;
import com.aurionpro.bank.dto.TransactionDto;
import com.aurionpro.bank.entity.Account;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.Transaction;
import com.aurionpro.bank.enums.TransactionType;
import com.aurionpro.bank.exceptions.AccountNotFoundException;
import com.aurionpro.bank.exceptions.CustomerNotFoundException;
import com.aurionpro.bank.exceptions.TransactionNotFoundException;
import com.aurionpro.bank.repository.AccountRepository;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.TransactionRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepo;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	@Transactional
	public void deposit(long accountNumber, double amount, long fromAccountNum) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = getAccount(accountNumber);

		if (amount <= 0) {
			throw new IllegalArgumentException("Transfer amount must be greater than zero");
		}
		account.setBalance(account.getBalance() + amount);
		accountRepository.save(account);
		LocalDate date = LocalDate.now();
		// Create a transaction record for deposit
		createTransaction(account, fromAccountNum, accountNumber, date, amount, TransactionType.Credit);
		emailService.sendTransactionEmail(account.getCustomer().getEmail(),
				account.getFirstName() + ", \nDeposit Successful",
				"Amount: " + amount + " has been deposited to your account " + accountNumber + "\nYour Balance is:"
						+ account.getBalance());
	}

	@Override
	@Transactional
	public void withdraw(long accountNumber, double amount, long toAccountNum) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = getAccount(accountNumber);

		if (!account.getCustomer().getEmail().equals(userEmail)) {
			throw new IllegalArgumentException("You can only withdraw from your own account.");
		}
		if (amount <= account.getBalance()) {
			account.setBalance(account.getBalance() - amount);
			accountRepository.save(account);
			LocalDate date = LocalDate.now();
			// Create a transaction record for withdrawal
			createTransaction(account, accountNumber, toAccountNum, date, amount, TransactionType.Debit);
			emailService.sendTransactionEmail(account.getCustomer().getEmail(),
					account.getFirstName() + ", \nWithdrawal Successful",
					"Amount: " + amount + " has been withdrawn from your account: " + accountNumber
							+ "\nYour Balance is:" + account.getBalance());
		} else {
			throw new IllegalArgumentException("Insufficient balance in your account");
		}
	}

	@Override
	@Transactional
	public void transfer(long fromAccountNum, long toAccountNum, double amount) {
		String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
		Account fromAccount = getAccount(fromAccountNum);

		if (!fromAccount.getCustomer().getEmail().equals(userEmail)) {
			throw new IllegalArgumentException("You can only transfer from your own account.");
		}
		if (fromAccountNum == toAccountNum) {
			throw new IllegalArgumentException("Transfer requires two different accounts.");
		}

		withdraw(fromAccountNum, amount, toAccountNum);
		deposit(toAccountNum, amount, fromAccountNum);

	}

	@Override
	public List<TransactionDto> getFilteredTransactions(long accountNumber, String typeOfTransaction, Double minAmount,
			Double maxAmount) {
		List<Transaction> transactions = transactionRepo.findByAccount_AccountNumber(accountNumber);

		return transactions.stream()
				.filter(transaction -> (typeOfTransaction == null
						|| transaction.getTypeOfTransaction().toString().equals(typeOfTransaction)))
				.filter(transaction -> (minAmount == null || transaction.getAmount() >= minAmount))
				.filter(transaction -> (maxAmount == null || transaction.getAmount() <= maxAmount))
				.map(this::toTransactionDto).collect(Collectors.toList());
	}

	@Override
	public PageResponseDto<TransactionDto> getAllTransactions(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
		Page<Transaction> transactionPage = transactionRepo.findAll(pageable);

		List<TransactionDto> transactionDtos = transactionPage.stream().map(this::toTransactionDto)
				.collect(Collectors.toList());

		PageResponseDto<TransactionDto> response = new PageResponseDto<>();
		response.setTotalPages(transactionPage.getTotalPages());
		response.setTotalElements(transactionPage.getTotalElements());
		response.setSize(transactionPage.getSize());
		response.setContent(transactionDtos);
		response.setLastPage(transactionPage.isLast());

		return response;
	}

	@Override
	public List<TransactionDto> viewPassbook() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();

		Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);
		if (!optionalCustomer.isPresent()) {
			throw new CustomerNotFoundException("Customer not found with email: " + email);
		}

		Customer customer = optionalCustomer.get();
		String firstName = customer.getFirstName();

		List<Account> userAccounts = accountRepository.findByCustomer_Email(email);

		List<Transaction> allTransactions = new ArrayList<>();

		// Loop through each account and fetch transactions
		for (Account account : userAccounts) {
			List<Transaction> transactions = transactionRepo.findByAccount_AccountNumber(account.getAccountNumber());
			allTransactions.addAll(transactions);
		}

		// Convert List<Transaction> to List<TransactionDto>
		List<TransactionDto> transactionDtos = allTransactions.stream()
				.map(transaction -> toTransactionDto(transaction)).collect(Collectors.toList());

		// Generate passbook file (for example, a CSV file)
		String passbookFilePath = generatePassbookFile(transactionDtos);

		try {
			emailService.sendPassbookEmail(email, firstName, passbookFilePath);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return transactionDtos;
	}

	private String generatePassbookFile(List<TransactionDto> transactionDtos) {
	    String fileName = "passbook_" + LocalDate.now() + ".csv";
	    String filePath = "D:\\Passbooks/" + fileName;

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
	        // Write header
	        writer.write("Sender Account Number, Receiver Account Number, Transaction Type, Amount, Date");
	        writer.newLine();

	        // Write each transaction detail
	        for (TransactionDto transactionDto : transactionDtos) {
	            writer.write(transactionDto.getSenderAccNo() + "," + 
	                         transactionDto.getReceiverAccNo() + "," + 
	                         transactionDto.getTypeOfTransaction() + "," + 
	                         transactionDto.getAmount() + "," + 
	                         transactionDto.getDate());
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return filePath;
	}

	private Account getAccount(long accountNumber) {
		return accountRepository.findById(accountNumber).orElseThrow(
				() -> new AccountNotFoundException("Account not found for account number: " + accountNumber));
	}

	private void createTransaction(Account account, long senderAccount, long receiverAccount, LocalDate date,
			double amount, TransactionType transactionType) {
		Transaction transaction = new Transaction();
		transaction.setAccount(account);
		transaction.setSenderAccNo(senderAccount);
		transaction.setReceiverAccNo(receiverAccount);
		transaction.setAmount(amount);
		transaction.setTypeOfTransaction(transactionType);
		transaction.setDate(date);
		// Save the transaction
		transactionRepo.save(transaction);

		// Print success message
		System.out.println("Transaction inserted successfully: ");

	}

	private Transaction toTransaction(TransactionDto transactionDto) {
		Transaction transaction = new Transaction();
		transaction.setSenderAccNo(transactionDto.getSenderAccNo());
		transaction.setReceiverAccNo(transactionDto.getReceiverAccNo());
		transaction.setTypeOfTransaction(transactionDto.getTypeOfTransaction());
		transaction.setAmount(transactionDto.getAmount());
		transaction.setDate(transactionDto.getDate());
		return transaction;
	}

	private TransactionDto toTransactionDto(Transaction transaction) {
		TransactionDto transactionDto = new TransactionDto();
		transactionDto.setSenderAccNo(transaction.getSenderAccNo());
		transactionDto.setReceiverAccNo(transaction.getReceiverAccNo());
		transactionDto.setTypeOfTransaction(transaction.getTypeOfTransaction());
		transactionDto.setAmount(transaction.getAmount());
		transactionDto.setDate(transaction.getDate());
		return transactionDto;
	}

}
