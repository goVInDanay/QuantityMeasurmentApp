package com.apps.quantitymeasurement.entity;

import java.io.Serializable;
import java.util.Objects;

import com.apps.quantitymeasurement.unit.IMeasurable;

public class QuantityMeasurementEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public double thisValue;
	public String thisUnit;
	public String thisMeasurementType;

	public double thatValue;
	public String thatUnit;
	public String thatMeasurementType;

	public String operation;

	public double resultValue;
	public String resultUnit;
	public String resultMeasurementType;
	public String resultString;

	public boolean isError;
	public String errorMessage;

	private QuantityMeasurementEntity(QuantityModel<? extends IMeasurable> thisQuantity,
			QuantityModel<? extends IMeasurable> thatQuantity, String operation) {
		this.thisValue = thisQuantity.getValue();
		this.thisUnit = thisQuantity.getUnit().getUnitName();
		this.thisMeasurementType = thisQuantity.getUnit().getMeasurementType();

		this.thatValue = thatQuantity.getValue();
		this.thatUnit = thatQuantity.getUnit().getUnitName();
		this.thatMeasurementType = thatQuantity.getUnit().getMeasurementType();

		this.operation = operation;
		this.isError = false;
	}

	public QuantityMeasurementEntity(QuantityModel<? extends IMeasurable> thisQuantity,
			QuantityModel<? extends IMeasurable> thatQuantity, String operation, String result) {
		this(thisQuantity, thatQuantity, operation);
		this.resultString = result;
	}

	public QuantityMeasurementEntity(QuantityModel<? extends IMeasurable> thisQuantity,
			QuantityModel<? extends IMeasurable> thatQuantity, String operation,
			QuantityModel<? extends IMeasurable> result) {
		this(thisQuantity, thatQuantity, operation);
		this.resultValue = result.getValue();
		this.resultUnit = result.getUnit().getUnitName();
		this.resultMeasurementType = result.getUnit().getMeasurementType();
	}

	public QuantityMeasurementEntity(QuantityModel<? extends IMeasurable> thisQuantity,
			QuantityModel<? extends IMeasurable> thatQuantity, String operation, String errorMessage, boolean isError) {
		this(thisQuantity, thatQuantity, operation);
		this.errorMessage = errorMessage;
		this.isError = isError;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		QuantityMeasurementEntity that = (QuantityMeasurementEntity) obj;

		return Double.compare(this.thisValue, that.thisValue) == 0
				&& Double.compare(this.thatValue, that.thatValue) == 0
				&& Double.compare(this.resultValue, that.resultValue) == 0 && isError == that.isError
				&& Objects.equals(this.thisUnit, that.thisUnit)
				&& Objects.equals(this.thisMeasurementType, that.thisMeasurementType)
				&& Objects.equals(this.thatUnit, that.thatUnit)
				&& Objects.equals(this.thatMeasurementType, that.thatMeasurementType)
				&& Objects.equals(this.operation, that.operation) && Objects.equals(this.resultUnit, that.resultUnit)
				&& Objects.equals(this.resultMeasurementType, that.resultMeasurementType)
				&& Objects.equals(this.resultString, that.resultString)
				&& Objects.equals(this.errorMessage, that.errorMessage);
	}

	public QuantityMeasurementEntity(double thisValue, String thisUnit, String thisMeasurementType, double thatValue,
			String thatUnit, String thatMeasurementType, String operation, double resultValue, String resultUnit,
			String resultMeasurementType, String resultString, boolean isError, String errorMessage) {

		this.thisValue = thisValue;
		this.thisUnit = thisUnit;
		this.thisMeasurementType = thisMeasurementType;

		this.thatValue = thatValue;
		this.thatUnit = thatUnit;
		this.thatMeasurementType = thatMeasurementType;

		this.operation = operation;

		this.resultValue = resultValue;
		this.resultUnit = resultUnit;
		this.resultMeasurementType = resultMeasurementType;

		this.resultString = resultString;

		this.isError = isError;
		this.errorMessage = errorMessage;
	}

	public double getThisValue() {
		return thisValue;
	}

	public void setThisValue(double thisValue) {
		this.thisValue = thisValue;
	}

	public String getThisUnit() {
		return thisUnit;
	}

	public void setThisUnit(String thisUnit) {
		this.thisUnit = thisUnit;
	}

	public String getThisMeasurementType() {
		return thisMeasurementType;
	}

	public void setThisMeasurementType(String thisMeasurementType) {
		this.thisMeasurementType = thisMeasurementType;
	}

	public double getThatValue() {
		return thatValue;
	}

	public void setThatValue(double thatValue) {
		this.thatValue = thatValue;
	}

	public String getThatUnit() {
		return thatUnit;
	}

	public void setThatUnit(String thatUnit) {
		this.thatUnit = thatUnit;
	}

	public String getThatMeasurementType() {
		return thatMeasurementType;
	}

	public void setThatMeasurementType(String thatMeasurementType) {
		this.thatMeasurementType = thatMeasurementType;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public double getResultValue() {
		return resultValue;
	}

	public void setResultValue(double resultValue) {
		this.resultValue = resultValue;
	}

	public String getResultUnit() {
		return resultUnit;
	}

	public void setResultUnit(String resultUnit) {
		this.resultUnit = resultUnit;
	}

	public String getResultMeasurementType() {
		return resultMeasurementType;
	}

	public void setResultMeasurementType(String resultMeasurementType) {
		this.resultMeasurementType = resultMeasurementType;
	}

	public String getResultString() {
		return resultString;
	}

	public void setResultString(String resultString) {
		this.resultString = resultString;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {

		if (isError) {
			return "Operation: " + operation + " | This: " + thisValue + " " + thisUnit + " | That: " + thatValue + " "
					+ thatUnit + " | ERROR: " + errorMessage;
		}

		if (resultString != null) {
			return "Operation: " + operation + " | This: " + thisValue + " " + thisUnit + " | That: " + thatValue + " "
					+ thatUnit + " | Result: " + resultString;
		}

		return "Operation: " + operation + " | This: " + thisValue + " " + thisUnit + " | That: " + thatValue + " "
				+ thatUnit + " | Result: " + resultValue + " " + resultUnit;
	}
}