package com.quantity.measurement.service;

import org.springframework.stereotype.Service;

import com.quantity.measurement.exception.QuantityMeasurementException;
import com.quantity.measurement.model.OperationType;
import com.quantity.measurement.model.QuantityDTO;
import com.quantity.measurement.model.QuantityHistoryDto;
import com.quantity.measurement.model.QuantityModel;
import com.quantity.measurement.unit.IMeasurable;
import com.quantity.measurement.unit.LengthUnit;
import com.quantity.measurement.unit.TemperatureUnit;
import com.quantity.measurement.unit.VolumeUnit;
import com.quantity.measurement.unit.WeightUnit;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

	private static final double EPSILON = 0.00001;
	private final HistoryClient historyClient;

	public QuantityMeasurementServiceImpl(HistoryClient historyClient) {
		this.historyClient = historyClient;
	}

	@Override
	public boolean compare(QuantityDTO thisDTO, QuantityDTO thatDTO, Long userId) {
		try {
			QuantityModel<IMeasurable> q1 = getModel(thisDTO);
			QuantityModel<IMeasurable> q2 = getModel(thatDTO);

			double base1 = q1.getUnit().convertToBaseUnit(q1.getValue());
			double base2 = q2.getUnit().convertToBaseUnit(q2.getValue());

			boolean result = Math.abs(base1 - base2) < EPSILON;

			QuantityHistoryDto history = QuantityHistoryDto.builder().thisValue(thisDTO.getValue())
					.thisUnit(thisDTO.getUnit()).thisMeasurementType(thisDTO.getMeasurementType())
					.thatValue(thatDTO.getValue()).thatUnit(thatDTO.getUnit())
					.thatMeasurementType(thatDTO.getMeasurementType()).operation("COMPARE")
					.resultString(String.valueOf(result)).userId(userId).build();
			historyClient.saveHistory(history);
			return result;

		} catch (Exception e) {
			saveError(thisDTO, thatDTO, "COMPARE", e, userId);
			throw e;
		}
	}

	@Override
	public QuantityDTO convert(QuantityDTO dto, String targetUnitName, Long userId) {

		try {
			QuantityModel<IMeasurable> source = getModel(dto);
			IMeasurable target = source.getUnit().getUnitInstance(targetUnitName);

			validateType(source.getUnit(), target);

			double base = source.getUnit().convertToBaseUnit(source.getValue());
			double result = target.convertFromBaseUnit(base);

			QuantityDTO response = new QuantityDTO(result, target.getUnitName(), target.getMeasurementType());

			QuantityHistoryDto history = QuantityHistoryDto.builder().thisValue(dto.getValue()).thisUnit(dto.getUnit())
					.thisMeasurementType(dto.getMeasurementType()).operation("CONVERT").resultValue(response.getValue())
					.resultUnit(response.getUnit()).resultMeasurementType(response.getMeasurementType())
					.resultString(response.getValue() + " " + response.getUnit()).userId(userId).build();

			historyClient.saveHistory(history);

			return response;

		} catch (Exception e) {
			saveError(dto, null, "CONVERT", e, userId);
			throw e;
		}
	}

	@Override
	public QuantityDTO add(QuantityDTO d1, QuantityDTO d2, Long userId) {
		return arithmetic(d1, d2, OperationType.ADD, userId);
	}

	@Override
	public QuantityDTO subtract(QuantityDTO d1, QuantityDTO d2, Long userId) {
		return arithmetic(d1, d2, OperationType.SUBTRACT, userId);
	}

	@Override
	public double divide(QuantityDTO d1, QuantityDTO d2, Long userId) {

		try {
			QuantityModel<IMeasurable> q1 = getModel(d1);
			QuantityModel<IMeasurable> q2 = getModel(d2);

			validateArithmetic(q1, q2);

			double base1 = q1.getUnit().convertToBaseUnit(q1.getValue());
			double base2 = q2.getUnit().convertToBaseUnit(q2.getValue());

			if (base2 == 0)
				throw new QuantityMeasurementException("Division by zero");

			double result = base1 / base2;

			QuantityHistoryDto history = QuantityHistoryDto.builder().thisValue(d1.getValue()).thisUnit(d1.getUnit())
					.thisMeasurementType(d1.getMeasurementType()).thatValue(d2.getValue()).thatUnit(d2.getUnit())
					.thatMeasurementType(d2.getMeasurementType()).operation("DIVIDE")
					.resultString(String.valueOf(result)).userId(userId).build();

			historyClient.saveHistory(history);

			return result;

		} catch (Exception e) {
			saveError(d1, d2, "DIVIDE", e, userId);
			throw e;
		}
	}

	private QuantityDTO arithmetic(QuantityDTO d1, QuantityDTO d2, OperationType op, Long userId) {

		try {
			QuantityModel<IMeasurable> q1 = getModel(d1);
			QuantityModel<IMeasurable> q2 = getModel(d2);

			validateArithmetic(q1, q2);

			double base1 = q1.getUnit().convertToBaseUnit(q1.getValue());
			double base2 = q2.getUnit().convertToBaseUnit(q2.getValue());

			double result = (op == OperationType.ADD) ? base1 + base2 : base1 - base2;

			double finalVal = q1.getUnit().convertFromBaseUnit(result);

			QuantityDTO response = new QuantityDTO(finalVal, d1.getUnit(), d1.getMeasurementType());

			QuantityHistoryDto history = QuantityHistoryDto.builder().thisValue(d1.getValue()).thisUnit(d1.getUnit())
					.thisMeasurementType(d1.getMeasurementType()).thatValue(d2.getValue()).thatUnit(d2.getUnit())
					.thatMeasurementType(d2.getMeasurementType()).operation(op.name()).resultValue(response.getValue())
					.resultUnit(response.getUnit()).resultMeasurementType(response.getMeasurementType())
					.resultString(response.getValue() + " " + response.getUnit()).userId(userId).build();

			historyClient.saveHistory(history);

			return response;

		} catch (Exception e) {
			saveError(d1, d2, op.name(), e, userId);
			throw e;
		}
	}

	private void saveError(QuantityDTO d1, QuantityDTO d2, String operation, Exception e, Long userId) {
		System.out.println(e.getMessage().length());
		QuantityHistoryDto history = QuantityHistoryDto.builder().thisValue(d1 != null ? d1.getValue() : 0)
				.thisUnit(d1 != null ? d1.getUnit() : null)
				.thisMeasurementType(d1 != null ? d1.getMeasurementType() : null)
				.thatValue(d2 != null ? d2.getValue() : null).thatUnit(d2 != null ? d2.getUnit() : null)
				.thatMeasurementType(d2 != null ? d2.getMeasurementType() : null).operation(operation).isError(true)
				.errorMessage(e.getMessage()).userId(userId).build();

		historyClient.saveHistory(history);
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