package com.apps.quantitymeasurement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.apps.quantitymeasurement.entities.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.entities.User;
import com.apps.quantitymeasurement.exception.QuantityMeasurementException;
import com.apps.quantitymeasurement.model.*;
import com.apps.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.apps.quantitymeasurement.unit.*;

@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

	private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);
	private static final double EPSILON = 0.00001;

	private final UserService userService;
	private final QuantityHistoryService historyService;

	public QuantityMeasurementServiceImpl(UserService userService, QuantityHistoryService historyService) {
		this.userService = userService;
		this.historyService = historyService;
	}

	private enum Operation {
		COMPARISON, CONVERSION, ARITHMETIC;
	}

	private enum ArithmeticOperation {
		ADD, SUBTRACT;
	}

	@Override
	public boolean compare(QuantityDTO thisDTO, QuantityDTO thatDTO) {

		User user = userService.getCurrentUser();

		try {
			QuantityModel<IMeasurable> thisModel = getQuantityModel(thisDTO);
			QuantityModel<IMeasurable> thatModel = getQuantityModel(thatDTO);

			double base1 = thisModel.getUnit().convertToBaseUnit(thisModel.getValue());
			double base2 = thatModel.getUnit().convertToBaseUnit(thatModel.getValue());

			boolean result = Math.abs(base1 - base2) < EPSILON;

			QuantityModel<IMeasurable> resultModel = new QuantityModel<>(result ? 1 : 0, thisModel.getUnit());
			if (user != null) {
				historyService.saveSuccess(thisModel, thatModel, resultModel, OperationType.COMPARE, user);
			}

			return result;

		} catch (Exception e) {
			historyService.saveError(null, null, OperationType.COMPARE, e.getMessage(), user);
			throw e;
		}
	}

	@Override
	public QuantityDTO convert(QuantityDTO thisDTO, String targetUnitName) {
		logger.info("DTO: {}", thisDTO);
		logger.info("Target Unit: {}", targetUnitName);
		User user = userService.getCurrentUser();
		try {
			QuantityModel<IMeasurable> source = getQuantityModel(thisDTO);

			IMeasurable sourceUnit = source.getUnit();
			IMeasurable targetUnit = sourceUnit.getUnitInstance(targetUnitName);

			validateMeasurementType(sourceUnit, targetUnit);

			double baseValue = sourceUnit.convertToBaseUnit(source.getValue());
			double convertedValue = targetUnit.convertFromBaseUnit(baseValue);

			QuantityModel<IMeasurable> resultModel = new QuantityModel<>(convertedValue, targetUnit);
			if (user != null) {
				historyService.saveConversion(source, resultModel, OperationType.CONVERT, user);
			}
			return new QuantityDTO(convertedValue, targetUnit.getUnitName(), targetUnit.getMeasurementType());

		} catch (Exception e) {
			historyService.saveError(null, null, OperationType.CONVERT, e.getMessage(), user);
			throw e;
		}
	}

	@Override
	public QuantityDTO add(QuantityDTO thisDTO, QuantityDTO thatDTO, QuantityDTO targetDTO) {
		return performArithmetic(thisDTO, thatDTO, targetDTO, OperationType.ADD);
	}

	@Override
	public QuantityDTO add(QuantityDTO thisDTO, QuantityDTO thatDTO) {
		return performArithmetic(thisDTO, thatDTO, thisDTO, OperationType.ADD);
	}

	@Override
	public QuantityDTO subtract(QuantityDTO thisDTO, QuantityDTO thatDTO) {
		return performArithmetic(thisDTO, thatDTO, thisDTO, OperationType.SUBTRACT);
	}

	@Override
	public QuantityDTO subtract(QuantityDTO thisDTO, QuantityDTO thatDTO, QuantityDTO targetDTO) {
		return performArithmetic(thisDTO, thatDTO, targetDTO, OperationType.SUBTRACT);
	}

	@Override
	public double divide(QuantityDTO thisDTO, QuantityDTO thatDTO) {
		User user = userService.getCurrentUser();
		try {
			QuantityModel<IMeasurable> thisModel = getQuantityModel(thisDTO);
			QuantityModel<IMeasurable> thatModel = getQuantityModel(thatDTO);
			validateArithmeticOperands(thisModel, thatModel);
			double base1 = thisModel.getUnit().convertToBaseUnit(thisModel.getValue());
			double base2 = thatModel.getUnit().convertToBaseUnit(thatModel.getValue());
			if (base2 == 0) {
				throw new QuantityMeasurementException("Division by zero not allowed");
			}
			double result = base1 / base2;
			QuantityModel<IMeasurable> resultModel = new QuantityModel<>(result, thisModel.getUnit());
			if (user != null) {
				historyService.saveSuccess(thisModel, thatModel, resultModel, OperationType.DIVIDE, user);
			}
			return result;
		} catch (Exception e) {
			historyService.saveError(null, null, OperationType.DIVIDE, e.getMessage(), user);
			throw e;
		}
	}

	private QuantityDTO performArithmetic(QuantityDTO thisDTO, QuantityDTO thatDTO, QuantityDTO targetDTO,
			OperationType operation) {

		User user = userService.getCurrentUser();

		try {
			QuantityModel<IMeasurable> thisModel = getQuantityModel(thisDTO);
			QuantityModel<IMeasurable> thatModel = getQuantityModel(thatDTO);
			QuantityModel<IMeasurable> targetModel = getQuantityModel(targetDTO);

			validateArithmeticOperands(thisModel, thatModel);
			validateArithmeticOperands(thisModel, targetModel);

			double base1 = thisModel.getUnit().convertToBaseUnit(thisModel.getValue());
			double base2 = thatModel.getUnit().convertToBaseUnit(thatModel.getValue());

			double resultBase = (operation == OperationType.ADD) ? base1 + base2 : base1 - base2;

			double finalValue = targetModel.getUnit().convertFromBaseUnit(resultBase);

			QuantityModel<IMeasurable> resultModel = new QuantityModel<>(finalValue, targetModel.getUnit());
			if (user != null) {
				historyService.saveSuccess(thisModel, thatModel, resultModel, operation, user);
			}

			return new QuantityDTO(finalValue, targetDTO.getUnit(), targetDTO.getMeasurementType());

		} catch (Exception e) {
			historyService.saveError(null, null, operation, e.getMessage(), user);
			throw e;
		}
	}

	private <U extends IMeasurable> void validateArithmeticOperands(QuantityModel<U> q1, QuantityModel<U> q2) {

		if (!q1.getUnit().getClass().equals(q2.getUnit().getClass())) {
			throw new QuantityMeasurementException("Different measurement types not allowed");
		}

		if (q1.getUnit() instanceof TemperatureUnit) {
			throw new QuantityMeasurementException("Arithmetic operations not allowed for temperature");
		}
	}

	private void validateMeasurementType(IMeasurable sourceUnit, IMeasurable target) {

		if (!sourceUnit.getMeasurementType().equals(target.getMeasurementType())) {
			throw new IllegalArgumentException("Cannot convert different measurement types");
		}
	}

	private QuantityModel<IMeasurable> getQuantityModel(QuantityDTO dto) {

		String type = dto.getMeasurementType();
		String unitName = dto.getUnit();

		IMeasurable measurableUnit;

		switch (type) {

		case "LengthUnit" -> {
			measurableUnit = LengthUnit.valueOf(unitName);
		}

		case "WeightUnit" -> {
			measurableUnit = WeightUnit.valueOf(unitName);
		}

		case "VolumeUnit" -> {
			measurableUnit = VolumeUnit.valueOf(unitName);
		}

		case "TemperatureUnit" -> {
			measurableUnit = TemperatureUnit.valueOf(unitName);
		}

		default -> {
			throw new QuantityMeasurementException("Invalid measurement type");
		}
		}

		return new QuantityModel<>(dto.getValue(), measurableUnit);
	}
}