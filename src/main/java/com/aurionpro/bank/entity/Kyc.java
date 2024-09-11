package com.aurionpro.bank.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "kyc")
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class Kyc {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int kycId;

	private String documentUrl;
	private String kycStatus;

	@ManyToOne
	@JoinColumn(name = "customer_id", referencedColumnName = "customerId")
	private Customer customer;
}
