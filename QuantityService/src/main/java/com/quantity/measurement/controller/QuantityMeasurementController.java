package com.quantity.measurement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public ResponseEntity<Boolean> compare(@RequestBody CompareRequest request) {
		return ResponseEntity.ok(service.compare(request.getThisQuantity(), request.getThatQuantity()));
	}

	@PostMapping("/convert")
	public ResponseEntity<QuantityDTO> convert(@RequestBody QuantityDTO dto, @RequestParam String targetUnit) {

		return ResponseEntity.ok(service.convert(dto, targetUnit));
	}

	@PostMapping("/add")
	public ResponseEntity<QuantityDTO> add(@RequestBody CompareRequest request) {
		return ResponseEntity.ok(service.add(request.getThisQuantity(), request.getThatQuantity()));
	}

	@PostMapping("/subtract")
	public ResponseEntity<QuantityDTO> subtract(@RequestBody CompareRequest request) {
		return ResponseEntity.ok(service.subtract(request.getThisQuantity(), request.getThatQuantity()));
	}

	@PostMapping("/divide")
	public ResponseEntity<Double> divide(@RequestBody CompareRequest request) {
		return ResponseEntity.ok(service.divide(request.getThisQuantity(), request.getThatQuantity()));
	}
}