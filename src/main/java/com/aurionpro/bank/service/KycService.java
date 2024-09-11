package com.aurionpro.bank.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aurionpro.bank.dto.KycDto;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.Kyc;
import com.aurionpro.bank.exceptions.CustomerNotFoundException;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.KycRepository;

@Service
public class KycService {
	@Autowired
	private CloudinaryService cloudinaryService;
	@Autowired
	private KycRepository kycRepo;
	@Autowired
	private CustomerRepository customerRepo;

	public KycDto uploadKycDocuments(int customerId, MultipartFile kycDocument) throws IOException {

		Optional<Customer> optionalCustomer = customerRepo.findById(customerId);
		if (!optionalCustomer.isPresent()) {
			throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
		}

		Customer customer = optionalCustomer.get();

		String documentUrl = cloudinaryService.uploadFile(kycDocument);

		// Save the document details in the database
		Kyc kyc = new Kyc();
		kyc.setCustomer(customer);
		kyc.setDocumentUrl(documentUrl);
		kyc.setKycStatus("Pending"); // Default status

		kyc = kycRepo.save(kyc);
		return toKycDto(kyc);
	}

	public KycDto toKycDto(Kyc kyc) {
		KycDto kycDto = new KycDto();
		kycDto.setKycId(kyc.getKycId());
		kycDto.setCustomerId(kyc.getCustomer().getCustomerId());
		kycDto.setDocumentUrl(kyc.getDocumentUrl());
		kycDto.setKycStatus(kyc.getKycStatus());
		return kycDto;
	}

}
