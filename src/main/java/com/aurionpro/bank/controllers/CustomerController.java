package com.aurionpro.bank.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.PageResponseDto;
import com.aurionpro.bank.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bankapp")
@Validated
public class CustomerController {
	@Autowired
	private CustomerService customerService;

	@PostMapping("/customers")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CustomerDto> addCustomer(@Valid @RequestBody CustomerDto customerDto) {
		CustomerDto createdCustomer = customerService.createCustomer(customerDto);
		return ResponseEntity.ok(createdCustomer);
	}

	@GetMapping("/customers/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<PageResponseDto<CustomerDto>> getAllCustomers(@RequestParam int pageNumber,
			@RequestParam int pageSize) {
		PageResponseDto<CustomerDto> customers = customerService.getAllCustomers(pageNumber, pageSize);
		return ResponseEntity.ok(customers);
	}

	@GetMapping("/customers/{customerId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CustomerDto> getCustomerById(@PathVariable int customerId) {
		CustomerDto customer = customerService.getCustomerById(customerId);
		return ResponseEntity.ok(customer);
	}

	@GetMapping("/customers")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CustomerDto> getCustomerByFirstName(@RequestParam String firstName) {
		CustomerDto customer = customerService.getCustomerByFirstName(firstName);
		return ResponseEntity.ok(customer);
	}

	@PutMapping("/customers/editProfile")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<String> updateCustomer(@Valid @RequestBody CustomerDto customerDto) {
		try {
			customerService.updateCustomer(customerDto);
			return ResponseEntity.ok("Profile updated successfully.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

}
