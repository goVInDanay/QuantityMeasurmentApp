package com.apps.quantitymeasurement.util;

import com.apps.quantitymeasurement.entities.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.model.QuantityHistoryDto;

public class QuantityMapper {

    public static QuantityHistoryDto toDTO(QuantityMeasurementEntity entity) {

        String input = entity.getThisValue() + " " + entity.getThisUnit();
        if (entity.getThatValue() != null) {
            input += " " + entity.getOperation() + " " +
                    entity.getThatValue() + " " + entity.getThatUnit();
        }
        return QuantityHistoryDto.builder()
                .input(input)
                .operation(entity.getOperation().name())
                .result(entity.getResultString())
                .isError(entity.isError())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}