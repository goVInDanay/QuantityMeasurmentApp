package com.apps.quantitymeasurement.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apps.quantitymeasurement.entities.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.entities.User;

@Repository
public interface QuantityMeasurementDatabaseRepository extends JpaRepository<QuantityMeasurementEntity, Long> {

	List<QuantityMeasurementEntity> findByOperation(String operation);

	List<QuantityMeasurementEntity> findByThisMeasurementType(String type);

	Page<QuantityMeasurementEntity> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}