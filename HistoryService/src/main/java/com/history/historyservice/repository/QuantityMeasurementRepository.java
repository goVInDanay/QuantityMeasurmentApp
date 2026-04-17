package com.history.historyservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.history.historyservice.entity.QuantityMeasurementEntity;

public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {
	Page<QuantityMeasurementEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

	Page<QuantityMeasurementEntity> findByUserId(Long userId, Pageable pageable);
}