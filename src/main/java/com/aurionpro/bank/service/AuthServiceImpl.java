package com.aurionpro.bank.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aurionpro.bank.dto.LoginDto;
import com.aurionpro.bank.dto.RegistrationDto;
import com.aurionpro.bank.entity.Role;
import com.aurionpro.bank.entity.User;
import com.aurionpro.bank.exceptions.UserApiException;
import com.aurionpro.bank.repository.RoleRepo;
import com.aurionpro.bank.repository.UserRepo;
import com.aurionpro.bank.security.JwtTokenProvider;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private RoleRepo roleRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenProvider tokenProvider;
	@Autowired
	private TokenBlacklistService tokenBlacklistService;

	@Override
	public User register(RegistrationDto registrationDto) {
		if (userRepo.existsByEmail(registrationDto.getEmail())) {
			// If user already exists, fetch the existing user
			User existingUser = userRepo.findByEmail(registrationDto.getEmail())
					.orElseThrow(() -> new UserApiException(HttpStatus.BAD_REQUEST, "User already exists"));

			// Check if the role already exists for the user
			Role roleToAdd = roleRepo.findByRoleName(registrationDto.getRole())
					.orElseThrow(() -> new UserApiException(HttpStatus.BAD_REQUEST, "Role not found"));

			if (!existingUser.getRoles().contains(roleToAdd)) {
				existingUser.getRoles().add(roleToAdd);
				userRepo.save(existingUser);
			}

			return existingUser;
		}

		User newUser = new User();
		newUser.setEmail(registrationDto.getEmail());
		newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

		List<Role> roles = new ArrayList<>();
		Role userRole = roleRepo.findByRoleName(registrationDto.getRole())
				.orElseThrow(() -> new UserApiException(HttpStatus.BAD_REQUEST, "Role not found"));
		roles.add(userRole);
		newUser.setRoles(roles);

		return userRepo.save(newUser);
	}

	@Override
	public String login(LoginDto loginDto) {
		try {
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.generateToken(authentication);

			if (token != null && tokenBlacklistService.isTokenBlacklisted(token)) {
				throw new SecurityException("Token has been invalidated.");
			}

			return token;
		} catch (BadCredentialsException e) {
			throw new UserApiException(HttpStatus.NOT_FOUND, "Email or Password is incorrect");
		}
	}

}
