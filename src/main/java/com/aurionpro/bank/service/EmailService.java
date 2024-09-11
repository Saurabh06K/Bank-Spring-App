package com.aurionpro.bank.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.aurionpro.bank.dto.EmailDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;

	public void sendRegistrationEmail(String to, String firstName) {
		String subject = "Welcome to Our Bank!";
		String body = "Dear " + firstName
				+ ",\n\nThank you for registering with us! We're excited to have you on board.";

		sendEmail(to, subject, body);
	}

	public void sendAccountCreationEmail(String to, String firstName, Long accountNumber, double balance) {
		String subject = "Your New Bank Account";
		String body = "Dear " + firstName
				+ ",\n\nYour new bank account has been created successfully. Your account number is: " + accountNumber
				+ " with balance of Rs." + balance;

		sendEmail(to, subject, body);
	}

	public void sendTransactionEmail(String to, String firstName, String transactionDetails) {
		String subject = "Transaction Notification";
		String body = "Dear " + firstName
				+ ".\n\nA recent transaction has been made in your account. \nHere are the details:\n"
				+ transactionDetails;

		sendEmail(to, subject, body);
	}

	public void sendPassbookEmail(String to, String firstName, String attachmentPath) throws MessagingException {
		String subject = "Your Requested Passbook";
		String body = "Dear " + firstName + ",\n\nPlease find your requested passbook attached.";

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body);

		// Attach the passbook file
		FileSystemResource file = new FileSystemResource(new File(attachmentPath));
		helper.addAttachment(file.getFilename(), file);

		mailSender.send(mimeMessage);
	}

	private void sendEmail(String to, String subject, String body) {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body);
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
