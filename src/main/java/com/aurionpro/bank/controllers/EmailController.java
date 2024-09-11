//package com.aurionpro.bank.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.aurionpro.bank.dto.EmailDto;
//import com.aurionpro.bank.service.EmailService;
//
//@RestController
//@RequestMapping("/bankapp")
//public class EmailController {
//	@Autowired
//	private EmailService emailService;
//
//	@PostMapping("/email")
//	public String sendEmail(@RequestBody EmailDto emailDto) {
//		emailService.sendSimpleEmail(emailDto);
//		return "Email sent successfully!";
//	}
//}
