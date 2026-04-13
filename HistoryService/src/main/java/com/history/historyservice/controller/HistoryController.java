package com.history.historyservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.history.historyservice.model.QuantityHistoryDto;
import com.history.historyservice.service.HistoryService;
import com.history.historyservice.util.RequestUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class HistoryController {

	private final HistoryService historyService;

	@GetMapping
	public ResponseEntity<List<QuantityHistoryDto>> getHistory(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Long userId = RequestUtils.getUserId();
		return ResponseEntity.ok(historyService.getHistory(userId, page, size));
	}

	@PostMapping("/internal")
	public ResponseEntity<Void> saveHistory(@RequestBody QuantityHistoryDto dto) {
		historyService.saveHistory(dto);
		return ResponseEntity.ok().build();
	}
}