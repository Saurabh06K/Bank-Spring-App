package com.aurionpro.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KycDto {
	private int kycId;
    private int customerId;
	private String documentUrl;
	private String kycStatus;
}
