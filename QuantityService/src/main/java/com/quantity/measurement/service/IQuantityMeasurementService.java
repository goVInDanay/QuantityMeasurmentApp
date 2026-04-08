package com.quantity.measurement.service;

import com.quantity.measurement.model.QuantityDTO;

public interface IQuantityMeasurementService {

	boolean compare(QuantityDTO thisQuantity, QuantityDTO thatQuantity);

	QuantityDTO convert(QuantityDTO thisQuantity, String targetUnit);

	QuantityDTO add(QuantityDTO thisQuantity, QuantityDTO thatQuantity);

	QuantityDTO subtract(QuantityDTO thisQuantity, QuantityDTO thatQuantity);

	double divide(QuantityDTO thisQuantity, QuantityDTO thatQuantity);
}