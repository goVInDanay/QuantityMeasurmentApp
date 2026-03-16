package com.apps.quantitymeasurement.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool {

	private static final Logger logger = Logger.getLogger(ConnectionPool.class.getName());

	private static ConnectionPool instance;
	private HikariDataSource dataSource;

	private ConnectionPool() {

		ApplicationConfig config = ApplicationConfig.getInstance();
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName(config.getProperty(ApplicationConfig.ConfigKey.DB_DRIVER_CLASS.getKey()));
		hikariConfig.setJdbcUrl(config.getProperty(ApplicationConfig.ConfigKey.DB_URL.getKey()));
		hikariConfig.setUsername(config.getProperty(ApplicationConfig.ConfigKey.DB_USERNAME.getKey()));
		hikariConfig.setPassword(config.getProperty(ApplicationConfig.ConfigKey.DB_PASSWORD.getKey()));
		hikariConfig.setMaximumPoolSize(
				config.getIntProperty(ApplicationConfig.ConfigKey.HIKARI_MAX_POOL_SIZE.getKey(), 10));
		hikariConfig.setMinimumIdle(config.getIntProperty(ApplicationConfig.ConfigKey.HIKARI_MIN_IDLE.getKey(), 2));
		hikariConfig.setConnectionTimeout(
				config.getIntProperty(ApplicationConfig.ConfigKey.HIKARI_CONNECTION_TIMEOUT.getKey(), 30000));
		hikariConfig.setIdleTimeout(
				config.getIntProperty(ApplicationConfig.ConfigKey.HIKARI_IDLE_TIMEOUT.getKey(), 600000));
		hikariConfig.setMaxLifetime(
				config.getIntProperty(ApplicationConfig.ConfigKey.HIKARI_MAX_LIFETIME.getKey(), 1800000));
		hikariConfig.setPoolName(
				config.getProperty(ApplicationConfig.ConfigKey.HIKARI_POOL_NAME.getKey(), "QuantityMeasurementPool"));
		hikariConfig.setConnectionTestQuery(
				config.getProperty(ApplicationConfig.ConfigKey.HIKARI_CONNECTION_TEST_QUERY.getKey(), "SELECT 1"));

		dataSource = new HikariDataSource(hikariConfig);
		logger.info("HikariCP Connection Pool initialized successfully.");
	}

	public static synchronized ConnectionPool getInstance() {
		if (instance == null) {
			instance = new ConnectionPool();
		}
		return instance;
	}

	public synchronized Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public synchronized void releaseConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.warning("Error releasing connection: " + e.getMessage());
			}
		}
	}

	public boolean validateConnection(Connection connection) {
		try (Statement stmt = connection.createStatement()) {
			stmt.execute("SELECT 1");
			return true;
		} catch (SQLException e) {
			logger.warning("Connection validation failed: " + e.getMessage());
			return false;
		}
	}

	public synchronized void closeAll() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
			logger.info("All connections closed.");
		}
	}

	public int getAvailableConnectionCount() {
		return dataSource.getHikariPoolMXBean().getIdleConnections();
	}

	public int getUsedConnectionCount() {
		return dataSource.getHikariPoolMXBean().getActiveConnections();
	}

	public int getTotalConnectionCount() {
		return dataSource.getHikariPoolMXBean().getTotalConnections();
	}

	@Override
	public String toString() {
		return "ConnectionPool{" + "availableConnections=" + getAvailableConnectionCount() + ", usedConnections="
				+ getUsedConnectionCount() + ", totalConnections=" + getTotalConnectionCount() + '}';
	}
}