package com.apps.quantitymeasurement.service;

import org.springframework.stereotype.Service;
import com.apps.quantitymeasurement.entities.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.entities.User;
import com.apps.quantitymeasurement.model.OperationType;
import com.apps.quantitymeasurement.model.QuantityDTO;
import com.apps.quantitymeasurement.model.QuantityModel;
import com.apps.quantitymeasurement.repository.QuantityMeasurementDatabaseRepository;
import com.apps.quantitymeasurement.unit.IMeasurable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuantityHistoryService {
	private final QuantityMeasurementDatabaseRepository repository;

	public void saveSuccess(QuantityModel<IMeasurable> thisModel, QuantityModel<IMeasurable> thatModel,
			QuantityModel<IMeasurable> resultModel, OperationType operation, User user) {
		QuantityMeasurementEntity entity = QuantityMeasurementEntity.success(thisModel, thatModel, resultModel,
				operation, user);
		if (user == null) {
			return;
		}
		repository.save(entity);
	}

	public void saveError(QuantityModel<IMeasurable> thisModel, QuantityModel<IMeasurable> thatModel,
			OperationType operation, String errorMessage, User user) {
		QuantityMeasurementEntity entity = QuantityMeasurementEntity.error(thisModel, thatModel, operation,
				errorMessage, user);
		if (user == null) {
			return;
		}
		repository.save(entity);
	}

	public void saveConversion(QuantityModel<IMeasurable> thisModel, QuantityModel<IMeasurable> resultModel,
			OperationType operation, User user) {
		if (user == null) {
			return;
		}
		QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
		entity.setThisValue(thisModel.getValue());
		entity.setThisUnit(thisModel.getUnit().getUnitName());
		entity.setThatValue(resultModel.getValue());
		entity.setThatUnit(resultModel.getUnit().getUnitName());
		entity.setResultValue(resultModel.getValue());
		entity.setResultUnit(resultModel.getUnit().getUnitName());
		entity.setOperation(operation);
		entity.setUser(user);
		repository.save(entity);
	}
}