package com.apps.quantitymeasurement.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuantityHistoryDto {
	private String input;
	private String operation;
	private String result;
	private boolean isError;
	private String errorMessage;
	private LocalDateTime createdAt;
}