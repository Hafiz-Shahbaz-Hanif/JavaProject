package com.DC.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.Logger;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class RedShiftUtility {
	public static Connection connection = null;
	public static Session session = null;
	public static int localPort; // any free port can be used
	public static ReadConfig readConfig = ReadConfig.getInstance();
	public static Logger logger = Logger.getLogger(RedShiftUtility.class);

	public static void connectToServer() throws SQLException {

		String os = System.getProperty("os.name");
		if (os.contains("Windows"))
		{
			connectSSH();
		}
		connectToDataBase();
	}


	private static void connectSSH() throws SQLException
	{
		try {
			java.util.Properties config = new java.util.Properties();
			JSch jsch = new JSch();
			session = jsch.getSession(readConfig.getSshUser(), readConfig.getSshHost(), readConfig.getSshPort());
			jsch.addIdentity(readConfig.getSshKey());
			config.put("StrictHostKeyChecking", "no");
			config.put("ConnectionAttempts", "3");
			session.setConfig(config);
			session.connect();

			logger.info("RedShift SSH Connected");
			localPort = session.setPortForwardingL(0, readConfig.getRedShiftURL(), readConfig.getRedShiftPort());

			logger.info("RedShift localhost:" + localPort + " -> " + readConfig.getRedShiftURL() + ":" + readConfig.getRedShiftPort());
		} catch (Exception e) {
			logger.error("connectSSH:" + e.getMessage());
		}
	}

	private static void connectToDataBase() throws SQLException {
		try {

			String localSSHUrl = "jdbc:redshift://127.0.0.1:" + String.valueOf(localPort) + "/fwdb";
			String os = System.getProperty("os.name");
			if (!os.contains("Windows"))
			{
				localSSHUrl = "jdbc:redshift://"+ readConfig.getRedShiftURL() + ":" + readConfig.getRedShiftPort() + "/fwdb";
			}

			//mysql database connectivity
			Class.forName("com.amazon.redshift.jdbc42.Driver");
			Properties props = new Properties();
			props.setProperty("ssl", "true");
			props.setProperty("user", readConfig.getRedShiftUserName());
			props.setProperty("password",  readConfig.getRedShiftPassword());
			connection = DriverManager.getConnection(localSSHUrl, props);
			logger.info("Successfully connected to RedShift database: " + localSSHUrl);

		} catch (Exception e) {
			logger.info("Connection to RedShift database failed. Exception: " + e.getMessage());
		}
	}


	public static void closeConnections() {
		CloseDataBaseConnection();
		CloseSSHConnection();
	}

	private static void CloseSSHConnection() {
		if (session != null && session.isConnected()) {
			logger.info("Closed RedShift SSH Connection");
			session.disconnect();
		}
	}

	private static void CloseDataBaseConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				logger.info("Closed RedShift Database Connection");
				connection.close();
			}
		} catch (SQLException e) {
			logger.info("Failed to close RedShift database connection. Exception: " + e.getMessage());
		}

	}


	public static ResultSet executeQuery(String query) throws SQLException
	{
		PreparedStatement stmt = connection.prepareStatement(query);
		ResultSet result = stmt.executeQuery();
		return result;
	}

	public Session connectSshForRedshift() throws SQLException {
		Session session = null;
		String os = System.getProperty("os.name");
		if (os.contains("Windows")) {
			try {
				java.util.Properties config = new java.util.Properties();
				JSch jsch = new JSch();
				session = jsch.getSession(readConfig.getSshUser(), readConfig.getSshHost(), readConfig.getSshPort());
				jsch.addIdentity(readConfig.getSshKey());
				config.put("StrictHostKeyChecking", "no");
				config.put("ConnectionAttempts", "3");
				session.setConfig(config);
				session.connect();

				logger.info("RedShift SSH Connected");
				localPort = session.setPortForwardingL(0, readConfig.getRedShiftURL(), readConfig.getRedShiftPort());
				logger.info("RedShift localhost:" + localPort + " -> " + readConfig.getRedShiftURL() + ":" + readConfig.getRedShiftPort());

			} catch (Exception e) {
				logger.error("connectSSH:" + e.getMessage());
			}
		}
		return session;
	}

	public  Connection connectToRedshift() throws SQLException {
		Connection connection = null;

		try {
			String localSSHUrl = "jdbc:redshift://127.0.0.1:" + String.valueOf(localPort) + "/fwdb";
			String os = System.getProperty("os.name");

			if (!os.contains("Windows")) {
				localSSHUrl = "jdbc:redshift://"+ readConfig.getRedShiftURL() + ":" + readConfig.getRedShiftPort() + "/fwdb";
			}

			Class.forName("com.amazon.redshift.jdbc42.Driver");
			Properties props = new Properties();
			props.setProperty("ssl", "true");
			props.setProperty("user", readConfig.getRedShiftUserName());
			props.setProperty("password",  readConfig.getRedShiftPassword());
			connection = DriverManager.getConnection(localSSHUrl, props);
			logger.info("Successfully connected to RedShift database: " + localSSHUrl);

		} catch (Exception e) {
			logger.info("Connection to RedShift database failed. Exception: " + e.getMessage());
		}
		return connection;
	}

	public  void closeConnections(Connection connection, Session session) {
		CloseDataBaseConnection(connection);
		if (session!=null) {
			CloseSSHConnection(session);
		}
	}

	public  void CloseDataBaseConnection(Connection connection) {
		try {
			if (connection != null && !connection.isClosed()) {
				logger.info("Closed RedShift Database Connection");
				connection.close();
			}
		} catch (SQLException e) {
			logger.info("Failed to close RedShift database connection. Exception: " + e.getMessage());
		}
	}

	public  void CloseSSHConnection(Session session) {
		if (session != null && session.isConnected()) {
			logger.info("Closed RedShift SSH Connection");
			session.disconnect();
		}
	}

	public static void executeUpdateQuery(String query) throws SQLException
	{
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.executeUpdate();
	}
}