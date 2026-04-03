package com.apps.quantitymeasurement.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.apps.quantitymeasurement.entities.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.entities.User;
import com.apps.quantitymeasurement.model.QuantityHistoryDto;
import com.apps.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.apps.quantitymeasurement.repository.UserRepository;
import com.apps.quantitymeasurement.util.QuantityMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
	private final QuantityMeasurementDatabaseRepository repository;
	private final UserRepository userRepository;

	@GetMapping
	public List<QuantityHistoryDto> getHistory(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, Authentication authentication) {

		String email = authentication.getName();
		User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		Page<QuantityMeasurementEntity> history = repository.findByUserOrderByCreatedAtDesc(user,
				PageRequest.of(page, size));

		return history.stream().map(QuantityMapper::toDTO).toList();
	}
}