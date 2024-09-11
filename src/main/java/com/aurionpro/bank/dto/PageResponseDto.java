package com.aurionpro.bank.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class PageResponseDto<T> {
	private long totalPages;
	private long totalElements;
	private int size;
	private List<T> content;
	private boolean lastPage;
}
