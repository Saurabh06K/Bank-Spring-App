package com.aurionpro.bank.dto;

import java.time.LocalDate;
import com.aurionpro.bank.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {
	private long senderAccNo;
	private long receiverAccNo;
	private TransactionType typeOfTransaction;
	private double amount;
	private LocalDate date;
}
