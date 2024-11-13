package com.DC.utilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.sql.Connection;
import java.sql.DriverManager;

public class SnowflakeUtility {

    private ReadConfig readConfig;
    private Logger logger;
    private String url;
    private String user;
    private String password;
    private String port;
    private String host;
    private String database;
    private String warehouse;
    private String role;

    public SnowflakeUtility() {
        logger = Logger.getLogger(SnowflakeUtility.class);
        PropertyConfigurator.configure("log4j.properties");
        readConfig = ReadConfig.getInstance();
        host = readConfig.getSnowflakeDbHost();
        port = readConfig.getSnowflakeDbPort();
        user = readConfig.getSnowflakeDbUsername();
        password = readConfig.getSnowflakeDbPassword();
        database = readConfig.getSnowflakeDatabase();
        warehouse = readConfig.getSnowflakeWarehouse();
        role = readConfig.getSnowflakeRole();

        url = "jdbc:snowflake://" + host + ":" + port + "?db=" + database + "&warehouse=" + warehouse + "&role=" + role;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
            connection = DriverManager.getConnection(url, user, password);
            logger.info("Connection to Snowflake server successful!");
        } catch (Exception e) {
            logger.info("Failed to connect to Snowflake database. Exception: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Closed Snowflake Database Connection");
            }
        } catch (Exception e) {
            logger.info("Failed to close Snowflake database connection. Exception: " + e.getMessage());
        }

    }
}
