package com.history.historyservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.history.historyservice.entity.QuantityMeasurementEntity;
import com.history.historyservice.model.HistoryResponseDto;
import com.history.historyservice.model.QuantityHistoryDto;
import com.history.historyservice.repository.QuantityMeasurementRepository;
import com.history.historyservice.util.QuantityMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {

	private final QuantityMeasurementRepository repository;

	public HistoryResponseDto getHistory(Long userId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Page<QuantityMeasurementEntity> historyPage = repository.findByUserId(userId, pageable);

		List<QuantityHistoryDto> items = historyPage.getContent().stream().map(QuantityMapper::toDTO).toList();

		return new HistoryResponseDto(items, historyPage.getNumber(), historyPage.getSize(),
				historyPage.getTotalElements(), historyPage.isLast());
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