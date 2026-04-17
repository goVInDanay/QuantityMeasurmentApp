package com.quantity.measurement.request;

import com.quantity.measurement.model.QuantityDTO;

public class CompareRequest {

	private QuantityDTO thisQuantity;
	private QuantityDTO thatQuantity;

	public QuantityDTO getThisQuantity() {
		return thisQuantity;
	}

	public QuantityDTO getThatQuantity() {
		return thatQuantity;
	}
}