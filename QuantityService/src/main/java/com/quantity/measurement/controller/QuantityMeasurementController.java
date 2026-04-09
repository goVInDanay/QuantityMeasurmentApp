package com.quantity.measurement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quantity.measurement.model.QuantityDTO;
import com.quantity.measurement.request.CompareRequest;
import com.quantity.measurement.service.IQuantityMeasurementService;

@RestController
@RequestMapping("/api/quantity")
public class QuantityMeasurementController {

	private final IQuantityMeasurementService service;

	public QuantityMeasurementController(IQuantityMeasurementService service) {
		this.service = service;
	}

	@PostMapping("/compare")
	public ResponseEntity<Boolean> compare(@RequestHeader("X-User-Id") String userId,
			@RequestBody CompareRequest request) {
		return ResponseEntity
				.ok(service.compare(request.getThisQuantity(), request.getThatQuantity(), Long.parseLong(userId)));
	}

	@PostMapping("/convert")
	public ResponseEntity<QuantityDTO> convert(@RequestHeader("X-User-Id") String userId, @RequestBody QuantityDTO dto,
			@RequestParam String targetUnit) {

		return ResponseEntity.ok(service.convert(dto, targetUnit, Long.parseLong(userId)));
	}

	@PostMapping("/add")
	public ResponseEntity<QuantityDTO> add(@RequestHeader("X-User-Id") String userId,
			@RequestBody CompareRequest request) {
		return ResponseEntity.ok(service.add(request.getThisQuantity(), request.getThatQuantity(), Long.parseLong(userId)));
	}

	@PostMapping("/subtract")
	public ResponseEntity<QuantityDTO> subtract(@RequestHeader("X-User-Id") String userId,
			@RequestBody CompareRequest request) {
		return ResponseEntity.ok(service.subtract(request.getThisQuantity(), request.getThatQuantity(), Long.parseLong(userId)));
	}

	@PostMapping("/divide")
	public ResponseEntity<Double> divide(@RequestHeader("X-User-Id") String userId,
			@RequestBody CompareRequest request) {
		return ResponseEntity
				.ok(service.divide(request.getThisQuantity(), request.getThatQuantity(), Long.parseLong(userId)));
	}
}