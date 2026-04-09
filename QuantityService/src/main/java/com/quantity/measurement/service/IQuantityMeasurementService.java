package com.quantity.measurement.service;

import com.quantity.measurement.model.QuantityDTO;

public interface IQuantityMeasurementService {

	boolean compare(QuantityDTO thisQuantity, QuantityDTO thatQuantity, Long userId);

	QuantityDTO convert(QuantityDTO thisQuantity, String targetUnit, Long userId);

	QuantityDTO add(QuantityDTO thisQuantity, QuantityDTO thatQuantity, Long userId);

	QuantityDTO subtract(QuantityDTO thisQuantity, QuantityDTO thatQuantity, Long userId);

	double divide(QuantityDTO thisQuantity, QuantityDTO thatQuantity, Long userId);
}