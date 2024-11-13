package com.DC.utilities;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUtility {
    public static Connection connection = null;
    public static Session session = null;
    public static int localPort; // any free port can be used
    public static Logger logger = Logger.getLogger("fila");

    public static ReadConfig readConfig = ReadConfig.getInstance();

    public static void connectToServer() throws SQLException {
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            connectSSH();
        }

        connectToDataBase();
    }

    private static void connectSSH() throws SQLException {
        try {
            java.util.Properties config = new java.util.Properties();
            JSch jsch = new JSch();
            session = jsch.getSession(readConfig.getSshUser(), readConfig.getSshHost(), readConfig.getSshPort());
            jsch.addIdentity(readConfig.getSshKey());
            config.put("StrictHostKeyChecking", "no");
            config.put("ConnectionAttempts", "3");
            session.setConfig(config);
            session.connect();

            logger.info("MySQL SSH Connected");

            localPort = session.setPortForwardingL(0, readConfig.getDBHost(), readConfig.getDBPort());

            logger.info("MySQL localhost:" + localPort + " -> " + readConfig.getDBHost() + ":" + readConfig.getDBPort());
        } catch (Exception e) {
            logger.error("connectSSH:" + e.getMessage());
        }
    }

    private static void connectToDataBase() {
        String dbuserName = readConfig.getSQLDBUsername();
        String dbpassword = readConfig.getSQLDBPassword();

        String os = System.getProperty("os.name");
        String localSSHUrl = "localhost";
        int port = localPort;
        if (!os.contains("Windows")) {
            localSSHUrl = readConfig.getDBHost();
            port = readConfig.getDBPort();
        }
        try {

            //mysql database connectivity
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(localSSHUrl);
            dataSource.setPortNumber(port);
            dataSource.setUser(dbuserName);
            dataSource.setAllowMultiQueries(true);
            //          dataSource.setUseSSL(false);
            dataSource.setPassword(dbpassword);
            dataSource.setDatabaseName(readConfig.getSQLDatabaseName());

            logger.info("OS: " + os + ", Connection URL: " + dataSource.getUrl());
            connection = dataSource.getConnection();

            logger.info("Connection to MySQL server successful!:" + connection + "\n\n");
        } catch (Exception e) {
            logger.info("Failed to connect to MySQL database. Exception: " + e.getMessage());
        }
    }


    public static void closeConnections() {
        CloseDataBaseConnection();
        CloseSSHConnection();
    }

    private static void CloseDataBaseConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                logger.info("Closed MySQL Database Connection");
                connection.close();
            }
        } catch (SQLException e) {
            logger.info("Failed to close MySQL database connection. Exception: " + e.getMessage());
        }

    }

    private static void CloseSSHConnection() {
        if (session != null && session.isConnected()) {
            logger.info("Closed MySQL SSH Connection");
            session.disconnect();
        }
    }

    public static ResultSet executeQuery(String query) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet result = stmt.executeQuery();
        return result;
    }

    public static void executeUpdate(String query) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.executeUpdate();
    }
}