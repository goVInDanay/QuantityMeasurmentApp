package com.history.historyservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.common.events.HistoryEvent;
import com.history.historyservice.entity.QuantityMeasurementEntity;
import com.history.historyservice.repository.QuantityMeasurementRepository;

@Service
public class KafkaConsumerService {

	private final QuantityMeasurementRepository historyRepository;

	public KafkaConsumerService(QuantityMeasurementRepository historyRepository) {
		this.historyRepository = historyRepository;
	}

	@KafkaListener(topics = "history-topic", groupId = "history-group-2")
	public void consume(HistoryEvent event) {

		QuantityMeasurementEntity entity = QuantityMeasurementEntity.builder().userId(event.getUserId())
				.operation(event.getOperation()).thisValue(event.getThisValue()).thisUnit(event.getThisUnit())
				.thisMeasurementType(event.getThisMeasurementType()).thatValue(event.getThatValue())
				.thatUnit(event.getThatUnit()).thatMeasurementType(event.getThatMeasurementType())
				.resultValue(event.getResultValue()).resultUnit(event.getResultUnit())
				.resultMeasurementType(event.getResultMeasurementType()).resultString(event.getResultString())
				.isError(Boolean.TRUE.equals(event.getIsError())).errorMessage(event.getErrorMessage()).build();
		historyRepository.save(entity);
	}
}