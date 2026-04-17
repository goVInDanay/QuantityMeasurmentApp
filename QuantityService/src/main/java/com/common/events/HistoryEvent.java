package com.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryEvent {

	private Long userId;
	private String operation;

	private Double thisValue;
	private String thisUnit;
	private String thisMeasurementType;

	private Double thatValue;
	private String thatUnit;
	private String thatMeasurementType;

	private Double resultValue;
	private String resultUnit;
	private String resultMeasurementType;

	private String resultString;

	private Boolean isError;
	private String errorMessage;
}