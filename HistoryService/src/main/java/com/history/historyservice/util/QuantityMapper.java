package com.history.historyservice.util;

import com.history.historyservice.entity.QuantityMeasurementEntity;
import com.history.historyservice.model.QuantityHistoryDto;

public class QuantityMapper {
	public static QuantityHistoryDto toDTO(QuantityMeasurementEntity entity) {
		return QuantityHistoryDto.builder().thisValue(entity.getThisValue()).thisUnit(entity.getThisUnit())
				.thisMeasurementType(entity.getThisMeasurementType()).thatValue(entity.getThatValue())
				.thatUnit(entity.getThatUnit()).thatMeasurementType(entity.getThatMeasurementType())
				.operation(entity.getOperation()).resultValue(entity.getResultValue())
				.resultUnit(entity.getResultUnit()).resultMeasurementType(entity.getResultMeasurementType())
				.resultString(entity.getResultString()).isError(entity.isError()).errorMessage(entity.getErrorMessage())
				.userId(entity.getUserId()).build();
	}
}