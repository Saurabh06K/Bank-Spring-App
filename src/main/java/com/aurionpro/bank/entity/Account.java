package com.aurionpro.bank.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "accounts")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Account {
	@Id
	@NotNull(message = "Account number cannot be null")
	@Min(value = 1000000000L, message = "Account number must be at least 10 digits")
	@Column(unique = true, nullable = false)
	private Long accountNumber;

	@NotNull(message = "First name cannot be null")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	@Column
	private String firstName;

	@NotNull(message = "Last name cannot be null")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	@Column
	private String lastName;

	@NotNull(message = "Balance cannot be null")
	@DecimalMin(value = "0.0", inclusive = true, message = "Balance must be non-negative")
	@Column(nullable = false)
	private Double balance;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.DETACH }, fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "account", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.DETACH }, fetch = FetchType.LAZY)
	private List<Transaction> transactions;

}
