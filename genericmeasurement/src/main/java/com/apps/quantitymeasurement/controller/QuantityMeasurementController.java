package com.apps.quantitymeasurement.controller;

import java.util.logging.Logger;

import com.apps.quantitymeasurement.QuantityMeasurementApp;
import com.apps.quantitymeasurement.entity.QuantityDTO;
import com.apps.quantitymeasurement.service.IQuantityMeasurementService;
import com.apps.quantitymeasurement.service.QuantityMeasurementServiceImpl;

public class QuantityMeasurementController {

	private static final Logger logger = Logger.getLogger(QuantityMeasurementController.class.getName());

	private IQuantityMeasurementService quantityMeasurementService;

	public QuantityMeasurementController(IQuantityMeasurementService quantityMeasurementService) {
		this.quantityMeasurementService = quantityMeasurementService;
		logger.info("Quantity Measurement Controller initialized with service : " + quantityMeasurementService);
	}

	public boolean performComparison(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
		return quantityMeasurementService.compare(thisQuantityDTO, thatQuantityDTO);
	}

	public QuantityDTO performConversion(QuantityDTO thisQuantityDTO, String targetUnit) {
		return quantityMeasurementService.convert(thisQuantityDTO, targetUnit);
	}

	public QuantityDTO performAddition(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
		return quantityMeasurementService.add(thisQuantityDTO, thatQuantityDTO);
	}

	public QuantityDTO performAddition(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
			QuantityDTO targetUnitDTO) {
		return quantityMeasurementService.add(thisQuantityDTO, thatQuantityDTO, targetUnitDTO);
	}

	public QuantityDTO performSubtraction(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
		return quantityMeasurementService.subtract(thisQuantityDTO, thatQuantityDTO);
	}

	public QuantityDTO performSubtraction(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO,
			QuantityDTO targetUnitDTO) {
		return quantityMeasurementService.subtract(thisQuantityDTO, thatQuantityDTO, targetUnitDTO);
	}

	public double performDivision(QuantityDTO thisQuantityDTO, QuantityDTO thatQuantityDTO) {
		return quantityMeasurementService.divide(thisQuantityDTO, thatQuantityDTO);
	}
}