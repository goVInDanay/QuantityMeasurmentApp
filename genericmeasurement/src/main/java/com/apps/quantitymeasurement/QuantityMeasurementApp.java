package com.apps.quantitymeasurement;

import com.apps.quantitymeasurement.controller.QuantityMeasurementController;
import com.apps.quantitymeasurement.entity.QuantityDTO;
import com.apps.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.apps.quantitymeasurement.repository.QuantityMeasurementCacheRepository;
import com.apps.quantitymeasurement.service.QuantityMeasurementServiceImpl;
import java.util.logging.Logger;

public class QuantityMeasurementApp {

	private static final Logger logger = Logger.getLogger(QuantityMeasurementApp.class.getName());

	private static QuantityMeasurementApp instance;

	public QuantityMeasurementController controller;
	public IQuantityMeasurementRepository repository;

	private QuantityMeasurementApp() {
		repository = QuantityMeasurementCacheRepository.getInstance();
		QuantityMeasurementServiceImpl service = new QuantityMeasurementServiceImpl(repository);
		controller = new QuantityMeasurementController(service);
		logger.info("Quantity Measurement Application initialized with Cache Repository");
	}

	public static QuantityMeasurementApp getInstance() {
		if (instance == null) {
			instance = new QuantityMeasurementApp();
		}
		return instance;
	}

	public static void main(String[] args) {

		QuantityMeasurementApp app = QuantityMeasurementApp.getInstance();
		QuantityMeasurementController controller = app.controller;

		QuantityDTO feet = QuantityDTO.ofLength(3, "FEET");
		QuantityDTO inches = QuantityDTO.ofLength(36, "INCHES");

		boolean comparisonResult = controller.performComparison(feet, inches);
		logger.info("3 FEET == 36 INCHES ? " + comparisonResult);

		QuantityDTO resultAdd = controller.performAddition(feet, inches);
		logger.info("Addition Result: " + resultAdd.getValue() + " " + resultAdd.getUnit().getUnitName());

		QuantityDTO conversionResult = controller.performConversion(feet, "YARDS");

		logger.info("3 FEET in YARDS: " + conversionResult.getValue());

		QuantityDTO celsius = QuantityDTO.ofTemperature(0, "CELSIUS");

		QuantityDTO tempResult = controller.performConversion(celsius, "FAHRENHEIT");

		logger.info("0 CELSIUS in FAHRENHEIT: " + tempResult.getValue());

		double divisionResult = controller.performDivision(feet, inches);
		logger.info("Division Result: " + divisionResult);
	}
}