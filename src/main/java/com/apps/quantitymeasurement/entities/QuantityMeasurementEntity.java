package com.apps.quantitymeasurement.entities;

import java.time.LocalDateTime;

import com.apps.quantitymeasurement.model.OperationType;
import com.apps.quantitymeasurement.model.QuantityModel;
import com.apps.quantitymeasurement.unit.IMeasurable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quantity_measurement_entity", indexes = { @Index(name = "idx_user_id", columnList = "user_id"),
		@Index(name = "idx_created_at", columnList = "createdAt") })
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

	private Double thatValue;
	private String thatUnit;
	private String thatMeasurementType;

	@Enumerated(EnumType.STRING)
	private OperationType operation;

	private Double resultValue;
	private String resultUnit;
	private String resultMeasurementType;

	private String resultString;

	private boolean isError;
	private String errorMessage;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	public static QuantityMeasurementEntity success(QuantityModel<IMeasurable> thisModel,
			QuantityModel<IMeasurable> thatModel, QuantityModel<IMeasurable> resultModel, OperationType operation,
			User user) {
		return QuantityMeasurementEntity.builder().thisValue(thisModel.getValue())
				.thisUnit(thisModel.getUnit().getUnitName())
				.thisMeasurementType(thisModel.getUnit().getMeasurementType())

				.thatValue(thatModel != null ? thatModel.getValue() : null)
				.thatUnit(thatModel != null ? thatModel.getUnit().getUnitName() : null)
				.thatMeasurementType(thatModel != null ? thatModel.getUnit().getMeasurementType() : null)

				.operation(operation)

				.resultValue(resultModel.getValue()).resultUnit(resultModel.getUnit().getUnitName())
				.resultMeasurementType(resultModel.getUnit().getMeasurementType())

				.resultString(resultModel.getValue() + " " + resultModel.getUnit().getUnitName())

				.isError(false).user(user).build();
	}
	
	public static QuantityMeasurementEntity error(QuantityModel<IMeasurable> thisModel,
			QuantityModel<IMeasurable> thatModel, OperationType operation, String errorMessage, User user) {
		return QuantityMeasurementEntity.builder().thisValue(thisModel.getValue())
				.thisUnit(thisModel.getUnit().getUnitName())
				.thisMeasurementType(thisModel.getUnit().getMeasurementType())
				.thatValue(thatModel != null ? thatModel.getValue() : null)
				.thatUnit(thatModel != null ? thatModel.getUnit().getUnitName() : null)
				.thatMeasurementType(thatModel != null ? thatModel.getUnit().getMeasurementType() : null)
				.operation(operation)
				.isError(true).errorMessage(errorMessage)
				.user(user).build();
	}
	
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