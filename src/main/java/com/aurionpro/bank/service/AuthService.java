package com.aurionpro.bank.service;

import com.aurionpro.bank.dto.LoginDto;
import com.aurionpro.bank.dto.RegistrationDto;
import com.aurionpro.bank.entity.User;

public interface AuthService {
	User register(RegistrationDto registrationDto);

	String login(LoginDto loginDto);
}
