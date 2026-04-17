package com.history.historyservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quantity_measurement_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuantityMeasurementEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private double thisValue;
	private String thisUnit;
	private String thisMeasurementType;

	@Column(nullable = true)
	private Double thatValue;
	private String thatUnit;
	private String thatMeasurementType;

	private String operation;

	@Column(nullable = true)
	private Double resultValue;
	private String resultUnit;
	private String resultMeasurementType;

	private String resultString;

	private boolean isError;
	@Lob
	private String errorMessage;

	private Long userId;

	@Column(updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
}