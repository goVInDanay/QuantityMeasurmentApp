package com.apps.quantitymeasurement.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.apps.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.apps.quantitymeasurement.util.ConnectionPool;

public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {

	private static final Logger logger = Logger.getLogger(QuantityMeasurementDatabaseRepository.class.getName());

	private static QuantityMeasurementDatabaseRepository instance;

	private static final String INSERT_QUERY = "INSERT INTO quantity_measurement_entity "
			+ "(this_value, this_unit, this_measurement_type, that_value, that_unit, "
			+ "that_measurement_type, operation, result_value, result_unit, "
			+ "result_measurement_type, result_string, is_error, error_message, created_at, updated_at) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

	private static final String SELECT_ALL_QUERY = "SELECT * FROM quantity_measurement_entity ORDER BY created_at DESC";

	private static final String SELECT_BY_OPERATION = "SELECT * FROM quantity_measurement_entity WHERE operation = ? ORDER BY created_at DESC";

	private static final String SELECT_BY_MEASUREMENT_TYPE = "SELECT * FROM quantity_measurement_entity WHERE this_measurement_type = ? ORDER BY created_at DESC";

	private static final String DELETE_ALL_QUERY = "DELETE FROM quantity_measurement_entity";

	private static final String COUNT_QUERY = "SELECT COUNT(*) FROM quantity_measurement_entity";

	private ConnectionPool connectionPool;

	private QuantityMeasurementDatabaseRepository() {
		connectionPool = ConnectionPool.getInstance();
		initializeDatabase();
	}

	private void initializeDatabase() {
		logger.info("Database repository initialized.");
	}

	public static synchronized QuantityMeasurementDatabaseRepository getInstance() {

		if (instance == null) {
			instance = new QuantityMeasurementDatabaseRepository();
		}
		return instance;
	}

	@Override
	public void save(QuantityMeasurementEntity entity) {

		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = connectionPool.getConnection();
			stmt = conn.prepareStatement(INSERT_QUERY);
			stmt.setDouble(1, entity.getThisValue());
			stmt.setString(2, entity.getThisUnit());
			stmt.setString(3, entity.getThisMeasurementType());
			stmt.setDouble(4, entity.getThatValue());
			stmt.setString(5, entity.getThatUnit());
			stmt.setString(6, entity.getThatMeasurementType());
			stmt.setString(7, entity.getOperation());
			stmt.setDouble(8, entity.getResultValue());
			stmt.setString(9, entity.getResultUnit());
			stmt.setString(10, entity.getResultMeasurementType());
			stmt.setString(11, entity.getResultString());
			stmt.setBoolean(12, entity.isError());
			stmt.setString(13, entity.getErrorMessage());
			stmt.executeUpdate();
		} catch (SQLException e) {
			logger.severe("Error saving entity: " + e.getMessage());
		} finally {
			closeResources(stmt, conn);
		}
	}

	@Override
	public List<QuantityMeasurementEntity> getAllMeasurements() {

		List<QuantityMeasurementEntity> list = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = connectionPool.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SELECT_ALL_QUERY);

			while (rs.next()) {
				list.add(mapResultSetToEntity(rs));
			}

		} catch (SQLException e) {
			logger.severe("Error fetching measurements: " + e.getMessage());
		} finally {
			closeResources(rs, stmt, conn);
		}

		return list;
	}

	public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {

		List<QuantityMeasurementEntity> list = new ArrayList<>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = connectionPool.getConnection();
			stmt = conn.prepareStatement(SELECT_BY_OPERATION);
			stmt.setString(1, operation);
			rs = stmt.executeQuery();
			while (rs.next()) {
				list.add(mapResultSetToEntity(rs));
			}
		} catch (SQLException e) {
			logger.severe("Error fetching by operation: " + e.getMessage());
		} finally {
			closeResources(rs, stmt, conn);
		}
		return list;
	}

	public List<QuantityMeasurementEntity> getAllMeasurementsByType(String type) {

		List<QuantityMeasurementEntity> list = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			conn = connectionPool.getConnection();
			stmt = conn.prepareStatement(SELECT_BY_MEASUREMENT_TYPE);
			stmt.setString(1, type);

			rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(mapResultSetToEntity(rs));
			}

		} catch (SQLException e) {
			logger.severe("Error fetching by measurement type: " + e.getMessage());
		} finally {
			closeResources(rs, stmt, conn);
		}
		return list;
	}

	public int getTotalCount() {
		int count = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = connectionPool.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(COUNT_QUERY);
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.severe("Error counting records: " + e.getMessage());
		} finally {
			closeResources(rs, stmt, conn);
		}

		return count;
	}

	public void deleteAll() {

		Connection conn = null;
		Statement stmt = null;

		try {
			conn = connectionPool.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(DELETE_ALL_QUERY);
		} catch (SQLException e) {
			logger.severe("Error deleting records: " + e.getMessage());
		} finally {
			closeResources(stmt, conn);
		}
	}

	public String getPoolStatistics() {
		return "Active: " + connectionPool.getUsedConnectionCount() + ", Idle: "
				+ connectionPool.getAvailableConnectionCount() + ", Total: " + connectionPool.getTotalConnectionCount();
	}

	public void releaseResources() {
		connectionPool.closeAll();
	}

	private QuantityMeasurementEntity mapResultSetToEntity(ResultSet rs) {

		try {
			return new QuantityMeasurementEntity(rs.getDouble("this_value"), rs.getString("this_unit"),
					rs.getString("this_measurement_type"), rs.getDouble("that_value"), rs.getString("that_unit"),
					rs.getString("that_measurement_type"), rs.getString("operation"), rs.getDouble("result_value"),
					rs.getString("result_unit"), rs.getString("result_measurement_type"), rs.getString("result_string"),
					rs.getBoolean("is_error"), rs.getString("error_message"));
		} catch (SQLException e) {
			logger.severe("Error mapping entity: " + e.getMessage());
			return null;
		}
	}

	private void closeResources(ResultSet rs, Statement stmt, Connection conn) {

		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		closeResources(stmt, conn);
	}

	private void closeResources(Statement stmt, Connection conn) {

		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}