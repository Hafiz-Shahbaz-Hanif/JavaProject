package com.DC.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class PostgreSqlUtility {

	private ReadConfig readConfig;
	private Logger logger;
	private String url;
	private String user;
	private String password;
	private String port;
	private String host;

	public PostgreSqlUtility() {
		logger = Logger.getLogger(PostgreSqlUtility.class);
		PropertyConfigurator.configure("log4j.properties");
		readConfig = ReadConfig.getInstance();
		host = readConfig.getPostgresDbHost();
		port = readConfig.getPostgresDbPort();
		user = readConfig.getPostgresDbUsername();
		password = readConfig.getPostgresDbPassword();
		url = "jdbc:postgresql://" + host + ":" + port + "/fwdb";
	}

	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, user, password);
			logger.info("Connection to PostgreSQL server successful!");
		} catch (Exception e) {
			logger.info("Failed to connect to PostgreSQL database. Exception: " + e.getMessage());
		}
		return connection;
	}

	public void closeConnection(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
				logger.info("Closed PostgreSQL Database Connection");
			}
		} catch (Exception e) {
			logger.info("Failed to close PostgreSQL database connection. Exception: " + e.getMessage());
		}

	}

}