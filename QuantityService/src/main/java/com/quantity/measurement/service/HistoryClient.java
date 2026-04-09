package com.quantity.measurement.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.quantity.measurement.model.QuantityHistoryDto;

@Service
public class HistoryClient {

	private final RestTemplate restTemplate;

	public HistoryClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void saveHistory(QuantityHistoryDto dto) {
		String url = "http://localhost:8080/api/history/internal";

		restTemplate.postForEntity(url, dto, Void.class);
	}
}