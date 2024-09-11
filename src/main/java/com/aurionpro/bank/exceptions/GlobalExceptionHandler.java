package com.aurionpro.bank.exceptions;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.aurionpro.bank.dto.CustomerErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler
	public ResponseEntity<CustomerErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException exception){
		CustomerErrorResponse error = new CustomerErrorResponse();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setErroeMessage("Id can be intger only");
		error.setTimeStamp(System.currentTimeMillis());
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); 
	}
	
	@ExceptionHandler
	public ResponseEntity<CustomerErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception){
		List<String> exceptionalErrors = exception.getBindingResult()
		          .getFieldErrors()
		          .stream()
		          .map(x -> x.getDefaultMessage())
		          .collect(Collectors.toList());
		CustomerErrorResponse error = new CustomerErrorResponse();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setErroeMessage("Something went wrong");
		error.setErrors(exceptionalErrors);
		error.setTimeStamp(System.currentTimeMillis());
		
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); 
	}
	
	@ExceptionHandler
	public ResponseEntity<String> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
	    return new ResponseEntity<>("HTTP method not supported", HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
