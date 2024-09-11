package com.aurionpro.bank.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aurionpro.bank.dto.CustomerDto;
import com.aurionpro.bank.dto.PageResponseDto;
import com.aurionpro.bank.entity.Customer;
import com.aurionpro.bank.entity.Role;
import com.aurionpro.bank.entity.User;
import com.aurionpro.bank.exceptions.CustomerNotFoundException;
import com.aurionpro.bank.repository.CustomerRepository;
import com.aurionpro.bank.repository.RoleRepo;
import com.aurionpro.bank.repository.UserRepo;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private UserRepo userRepository;

	@Autowired
	private RoleRepo roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private TokenBlacklistService tokenBlacklistService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private EmailService emailService;

	@Override
	public CustomerDto createCustomer(CustomerDto customerDto) {
		Customer customer = toCustomer(customerDto);
		Optional<Customer> dbCustomer = customerRepo.findByEmail(customerDto.getEmail());
		if (!dbCustomer.isEmpty())
			throw new CustomerNotFoundException("Customer already exists with this Email");
		// Create a corresponding User entity
		User user = new User();
		user.setEmail(customer.getEmail());
		user.setPassword(passwordEncoder.encode(customerDto.getPassword()));

		// Fetch the USER role from the database and assign it
		Role userRole = roleRepository.findByRoleName(customerDto.getRole())
				.orElseThrow(() -> new RuntimeException("Error: Role USER not found."));
		user.setRoles(Collections.singletonList(userRole));

		user.setCustomer(customer);
		customer.setUser(user);

		customer = customerRepo.save(customer);

		emailService.sendRegistrationEmail(customerDto.getEmail(), customerDto.getFirstName());

		return toCustomerDto(customer);
	}

	@Override
	public CustomerDto updateCustomer(CustomerDto customerDetails) {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String email = userDetails.getUsername();

		Optional<Customer> optionalCustomer = customerRepo.findByEmail(email);
		if (!optionalCustomer.isPresent()) {
			throw new CustomerNotFoundException("Customer Not Found");
		}

		Customer dbCustomer = optionalCustomer.get();

		User user = dbCustomer.getUser();
		if (user == null) {
			throw new RuntimeException("User details not found for this customer");
		}

		dbCustomer.setFirstName(customerDetails.getFirstName());
		dbCustomer.setLastName(customerDetails.getLastName());

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String newPassword = customerDetails.getPassword();

		// Only update the password if it's different from the current one
		if (!passwordEncoder.matches(newPassword, dbCustomer.getPassword())) {
			String encodedPassword = passwordEncoder.encode(newPassword);
			dbCustomer.setPassword(newPassword);
			user.setPassword(encodedPassword);

			String token = getJwtFromRequest();

			// Blacklist the current token
			tokenBlacklistService.addTokenToBlacklist(token);

			SecurityContextHolder.clearContext();

		}
		dbCustomer = customerRepo.save(dbCustomer);

		return toCustomerDto(dbCustomer);
	}

	private String getJwtFromRequest() {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7); // Remove "Bearer " prefix
		}
		return null;
	}

	@Override
	public CustomerDto getCustomerById(int id) {
		Optional<Customer> optionalCustomer = customerRepo.findById(id);
		if (!optionalCustomer.isPresent()) {
			throw new CustomerNotFoundException("Customer Not Found");
		}

		Customer dbCustomer = optionalCustomer.get();

		return toCustomerDto(dbCustomer);
	}

	@Override
	public PageResponseDto<CustomerDto> getAllCustomers(int pageNumber, int pageSize) {
		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		Page<Customer> customerPage = customerRepo.findAll(pageable);

		List<CustomerDto> customerDtos = customerPage.getContent().stream().map(this::toCustomerDto)
				.collect(Collectors.toList());

		PageResponseDto<CustomerDto> pageResponse = new PageResponseDto<>(customerPage.getTotalPages(),
				customerPage.getTotalElements(), customerPage.getSize(), customerDtos, customerPage.isLast());

		return pageResponse;
	}

	@Override
	public CustomerDto getCustomerByFirstName(String firstName) {
		Optional<Customer> optionalCustomer = customerRepo.findByFirstName(firstName);
		if (!optionalCustomer.isPresent()) {
			throw new CustomerNotFoundException("Customer Not Found");
		}

		Customer dbCustomer = optionalCustomer.get();

		return toCustomerDto(dbCustomer);
	}

	public Customer toCustomer(CustomerDto customerDto) {
		Customer customer = new Customer();

		customer.setCustomerId(customerDto.getCustomerId());
		customer.setFirstName(customerDto.getFirstName());
		customer.setLastName(customerDto.getLastName());
		customer.setEmail(customerDto.getEmail());
		customer.setPassword(customerDto.getPassword());

		return customer;
	}

	public CustomerDto toCustomerDto(Customer customer) {
		CustomerDto customerDto = new CustomerDto();

		customerDto.setCustomerId(customer.getCustomerId());
		customerDto.setFirstName(customer.getFirstName());
		customerDto.setLastName(customer.getLastName());
		customerDto.setEmail(customer.getEmail());
		customerDto.setPassword(customer.getPassword());
		customerDto.setRole("ROLE_USER");

		return customerDto;
	}

}
