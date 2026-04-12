package com.quantity.measurement.eventpublisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.common.events.HistoryEvent;
import com.quantity.measurement.model.QuantityHistoryDto;

@Service
public class KafkaProducerService {

	private final KafkaTemplate<String, HistoryEvent> kafkaTemplate;

	public KafkaProducerService(KafkaTemplate<String, HistoryEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void sendHistoryEvent(String topic, QuantityHistoryDto history) {

		HistoryEvent event = HistoryEvent.builder().userId(history.getUserId()).operation(history.getOperation())
				.thisValue(history.getThisValue()).thisUnit(history.getThisUnit())
				.thisMeasurementType(history.getThisMeasurementType()).thatValue(history.getThatValue())
				.thatUnit(history.getThatUnit()).thatMeasurementType(history.getThatMeasurementType())
				.resultValue(history.getResultValue()).resultUnit(history.getResultUnit())
				.resultMeasurementType(history.getResultMeasurementType()).resultString(history.getResultString())
				.isError(Boolean.TRUE.equals(history.getIsError())).errorMessage(history.getErrorMessage()).build();

		kafkaTemplate.send(topic, event);
	}
}
