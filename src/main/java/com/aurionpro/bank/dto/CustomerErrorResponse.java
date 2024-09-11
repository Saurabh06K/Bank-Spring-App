package com.aurionpro.bank.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class CustomerErrorResponse {
	private int status;
	private String erroeMessage;
	private List<String> errors;
	private long timeStamp;
}
