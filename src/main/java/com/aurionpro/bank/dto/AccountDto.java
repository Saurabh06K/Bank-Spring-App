package com.aurionpro.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccountDto {
	@NotNull(message = "Account number cannot be null")
	@Min(value = 1000000000L, message = "Account number must be at least 10 digits")
	private Long accountNumber;

	@NotNull(message = "First name cannot be null")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	private String firstName;

	@NotNull(message = "Last name cannot be null")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	private String lastName;

	@NotNull(message = "Balance cannot be null")
	@DecimalMin(value = "0.0", inclusive = true, message = "Balance must be non-negative")
	private Double balance;
}
