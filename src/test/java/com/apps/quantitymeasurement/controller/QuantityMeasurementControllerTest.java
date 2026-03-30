package com.apps.quantitymeasurement.controller;

import com.apps.quantitymeasurement.controller.QuantityMeasurementController;
import com.apps.quantitymeasurement.model.QuantityDTO;
import com.apps.quantitymeasurement.request.CompareRequest;
import com.apps.quantitymeasurement.service.IQuantityMeasurementService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuantityMeasurementController.class)
public class QuantityMeasurementControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IQuantityMeasurementService service;

	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		objectMapper = new ObjectMapper();
	}

	private CompareRequest buildCompareRequest(double val1, String unit1, String type1, double val2, String unit2,
			String type2) {
		QuantityDTO q1 = new QuantityDTO(val1, unit1, type1);
		QuantityDTO q2 = new QuantityDTO(val2, unit2, type2);
		CompareRequest request = new CompareRequest();
		request.setThisQuantity(q1);
		request.setThatQuantity(q2);
		return request;
	}

	@Test
	public void testCompareEndpoint() throws Exception {
		CompareRequest request = buildCompareRequest(5, "FEET", "LengthUnit", 60, "INCHES", "LengthUnit");

		Mockito.when(service.compare(request.getThisQuantity(), request.getThatQuantity())).thenReturn(true);

		mockMvc.perform(post("/quantity/compare").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(content().string("true"));
	}

	@Test
	public void testConvertEndpoint() throws Exception {
		QuantityDTO input = new QuantityDTO(1, "FEET", "LengthUnit");
		QuantityDTO output = new QuantityDTO(12, "INCHES", "LengthUnit");

		Mockito.when(service.convert(input, "INCHES")).thenReturn(output);

		mockMvc.perform(post("/quantity/convert").param("targetUnit", "INCHES").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(input))).andExpect(status().isOk())
				.andExpect(jsonPath("$.value").value(12)).andExpect(jsonPath("$.unit").value("INCHES"))
				.andExpect(jsonPath("$.measurementType").value("LengthUnit"));
	}

	@Test
	public void testAddEndpoint() throws Exception {
		CompareRequest request = buildCompareRequest(2, "FEET", "LengthUnit", 1, "FEET", "LengthUnit");
		QuantityDTO result = new QuantityDTO(3, "FEET", "LengthUnit");

		Mockito.when(service.add(request.getThisQuantity(), request.getThatQuantity())).thenReturn(result);

		mockMvc.perform(post("/quantity/add").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.value").value(3)).andExpect(jsonPath("$.unit").value("FEET"))
				.andExpect(jsonPath("$.measurementType").value("LengthUnit"));
	}

	@Test
	public void testSubtractEndpoint() throws Exception {
		CompareRequest request = buildCompareRequest(5, "FEET", "LengthUnit", 2, "FEET", "LengthUnit");
		QuantityDTO result = new QuantityDTO(3, "FEET", "LengthUnit");

		Mockito.when(service.subtract(request.getThisQuantity(), request.getThatQuantity())).thenReturn(result);

		mockMvc.perform(post("/quantity/subtract").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.value").value(3)).andExpect(jsonPath("$.unit").value("FEET"))
				.andExpect(jsonPath("$.measurementType").value("LengthUnit"));
	}

	@Test
	public void testDivideEndpoint() throws Exception {
		CompareRequest request = buildCompareRequest(10, "FEET", "LengthUnit", 2, "FEET", "LengthUnit");

		Mockito.when(service.divide(request.getThisQuantity(), request.getThatQuantity())).thenReturn(5.0);

		mockMvc.perform(post("/quantity/divide").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(content().string("5.0"));
	}
}