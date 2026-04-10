package com.quantity.measurement.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.quantity.measurement.model.QuantityHistoryDto;

@FeignClient(name = "history-service")
public interface HistoryClient {

	@PostMapping("/api/history/internal")
	void saveHistory(@RequestBody QuantityHistoryDto dto);
}