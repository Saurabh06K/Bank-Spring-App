package com.aurionpro.bank.service;

import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.PageResponseDto;

public interface CustomerService {
	CustomerDto createCustomer(CustomerDto customerDto);

	CustomerDto getCustomerById(int id);
	
	CustomerDto getCustomerByFirstName(String firstName);

	PageResponseDto<CustomerDto> getAllCustomers(int pageNumber, int pageSize);

	CustomerDto updateCustomer(CustomerDto customerDetails);
}
