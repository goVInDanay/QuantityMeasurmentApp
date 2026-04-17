package com.quantity.measurement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.common.events.HistoryEvent;

@Configuration
public class KafkaConfig {

	@Bean
	KafkaTemplate<String, HistoryEvent> kafkaTemplate(ProducerFactory<String, HistoryEvent> pf) {
		KafkaTemplate<String, HistoryEvent> template = new KafkaTemplate<>(pf);
		template.setDefaultTopic("history-topic");
		return template;
	}
}