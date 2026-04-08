package com.quantity.measurement.service;

import org.springframework.stereotype.Service;

import com.quantity.measurement.exception.QuantityMeasurementException;
import com.quantity.measurement.model.OperationType;
import com.quantity.measurement.model.QuantityDTO;
import com.quantity.measurement.model.QuantityModel;
import com.quantity.measurement.unit.IMeasurable;
import com.quantity.measurement.unit.LengthUnit;
import com.quantity.measurement.unit.TemperatureUnit;
import com.quantity.measurement.unit.VolumeUnit;
import com.quantity.measurement.unit.WeightUnit;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

	private static final double EPSILON = 0.00001;

	@Override
	public boolean compare(QuantityDTO thisDTO, QuantityDTO thatDTO) {

		QuantityModel<IMeasurable> q1 = getModel(thisDTO);
		QuantityModel<IMeasurable> q2 = getModel(thatDTO);

		double base1 = q1.getUnit().convertToBaseUnit(q1.getValue());
		double base2 = q2.getUnit().convertToBaseUnit(q2.getValue());

		return Math.abs(base1 - base2) < EPSILON;
	}

	@Override
	public QuantityDTO convert(QuantityDTO dto, String targetUnitName) {

		QuantityModel<IMeasurable> source = getModel(dto);
		IMeasurable target = source.getUnit().getUnitInstance(targetUnitName);

		validateType(source.getUnit(), target);

		double base = source.getUnit().convertToBaseUnit(source.getValue());
		double result = target.convertFromBaseUnit(base);

		return new QuantityDTO(result, target.getUnitName(), target.getMeasurementType());
	}

	@Override
	public QuantityDTO add(QuantityDTO d1, QuantityDTO d2) {
		return arithmetic(d1, d2, OperationType.ADD);
	}

	@Override
	public QuantityDTO subtract(QuantityDTO d1, QuantityDTO d2) {
		return arithmetic(d1, d2, OperationType.SUBTRACT);
	}

	@Override
	public double divide(QuantityDTO d1, QuantityDTO d2) {

		QuantityModel<IMeasurable> q1 = getModel(d1);
		QuantityModel<IMeasurable> q2 = getModel(d2);

		validateArithmetic(q1, q2);

		double base1 = q1.getUnit().convertToBaseUnit(q1.getValue());
		double base2 = q2.getUnit().convertToBaseUnit(q2.getValue());

		if (base2 == 0)
			throw new QuantityMeasurementException("Division by zero");

		return base1 / base2;
	}

	private QuantityDTO arithmetic(QuantityDTO d1, QuantityDTO d2, OperationType op) {

		QuantityModel<IMeasurable> q1 = getModel(d1);
		QuantityModel<IMeasurable> q2 = getModel(d2);

		validateArithmetic(q1, q2);

		double base1 = q1.getUnit().convertToBaseUnit(q1.getValue());
		double base2 = q2.getUnit().convertToBaseUnit(q2.getValue());

		double result = (op == OperationType.ADD) ? base1 + base2 : base1 - base2;

		double finalVal = q1.getUnit().convertFromBaseUnit(result);

		return new QuantityDTO(finalVal, d1.getUnit(), d1.getMeasurementType());
	}

	private void validateArithmetic(QuantityModel<IMeasurable> q1, QuantityModel<IMeasurable> q2) {
		if (!q1.getUnit().getClass().equals(q2.getUnit().getClass()))
			throw new QuantityMeasurementException("Different types not allowed");

		if (q1.getUnit() instanceof TemperatureUnit)
			throw new QuantityMeasurementException("Temperature arithmetic not allowed");
	}

	private void validateType(IMeasurable s, IMeasurable t) {
		if (!s.getMeasurementType().equals(t.getMeasurementType()))
			throw new QuantityMeasurementException("Different measurement types");
	}

	private QuantityModel<IMeasurable> getModel(QuantityDTO dto) {

		return switch (dto.getMeasurementType()) {
		case "LengthUnit" -> new QuantityModel<>(dto.getValue(), LengthUnit.valueOf(dto.getUnit()));
		case "WeightUnit" -> new QuantityModel<>(dto.getValue(), WeightUnit.valueOf(dto.getUnit()));
		case "VolumeUnit" -> new QuantityModel<>(dto.getValue(), VolumeUnit.valueOf(dto.getUnit()));
		case "TemperatureUnit" -> new QuantityModel<>(dto.getValue(), TemperatureUnit.valueOf(dto.getUnit()));
		default -> throw new QuantityMeasurementException("Invalid type");
		};
	}
}