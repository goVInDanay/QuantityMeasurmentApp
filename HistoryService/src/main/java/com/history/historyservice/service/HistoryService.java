package com.history.historyservice.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.history.historyservice.entity.QuantityMeasurementEntity;
import com.history.historyservice.model.QuantityHistoryDto;
import com.history.historyservice.repository.QuantityMeasurementRepository;
import com.history.historyservice.util.QuantityMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

	private final QuantityMeasurementRepository repository;

	public List<QuantityHistoryDto> getHistory(Long userId, int page, int size) {
		return repository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size)).stream()
				.map(QuantityMapper::toDTO).toList();
	}

	public void saveHistory(QuantityHistoryDto dto) {
		QuantityMeasurementEntity entity = QuantityMeasurementEntity.builder().thisValue(dto.getThisValue())
				.thisUnit(dto.getThisUnit()).thisMeasurementType(dto.getThisMeasurementType())
				.thatValue(dto.getThatValue()).thatUnit(dto.getThatUnit())
				.thatMeasurementType(dto.getThatMeasurementType()).operation(dto.getOperation())
				.resultValue(dto.getResultValue()).resultUnit(dto.getResultUnit())
				.resultMeasurementType(dto.getResultMeasurementType()).resultString(dto.getResultString())
				.isError(dto.isError()).errorMessage(dto.getErrorMessage()).userId(dto.getUserId()).build();

		repository.save(entity);
	}
}