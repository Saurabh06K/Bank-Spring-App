package com.aurionpro.bank.entity;

import java.time.LocalDate;
import com.aurionpro.bank.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "transactions")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long transId;

	@NotNull(message = "Sender account number cannot be null")
	@Min(value = 10, message = "Sender account number must be of 10 digits")
	@Column(name = "sender_acc_no", nullable = false)
	private long senderAccNo;

	@NotNull(message = "Receiver account number cannot be null")
	@Min(value = 10, message = "Receiver account number must be of 10 digits")
	@Column(name = "receiver_acc_no", nullable = false)
	private long receiverAccNo;

	@NotNull(message = "Type of transaction cannot be null")
	@Enumerated(EnumType.STRING)
	@Column(name = "type_of_transaction", nullable = false)
	private TransactionType typeOfTransaction;

	@NotNull(message = "Amount cannot be null")
	@DecimalMin(value = "0.01", inclusive = true, message = "Amount must be positive")
	@Column(nullable = false)
	private double amount;

	@NotNull(message = "Date cannot be null")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@Column(nullable = false)
	private LocalDate date;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "accountNumber",referencedColumnName = "accountNumber", nullable = false)
	private Account account;
}
