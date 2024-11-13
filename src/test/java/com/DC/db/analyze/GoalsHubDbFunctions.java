package com.DC.db.analyze;

import com.DC.utilities.PostgreSqlUtility;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class GoalsHubDbFunctions {

    private PostgreSqlUtility pu;
    private Logger logger;

    public GoalsHubDbFunctions() {
        pu = new PostgreSqlUtility();
        logger = Logger.getLogger(GoalsHubDbFunctions.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    public String getGoalId(String goalId) throws SQLException {
        Connection con = null;
        String userId = null;
        try {
            con = pu.getConnection();
            PreparedStatement stmt = con.prepareStatement(GoalsHubQueries.goalId);
            stmt.setObject(1, goalId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                userId = resultSet.getString("id");
            }

        } catch (Exception e) {
            logger.info("Failed to execute query for goal id. Exception: " + e.getMessage());
            pu.closeConnection(con);
            throw e;
        }

        pu.closeConnection(con);
        return userId;
    }

    public String getGoalId() throws SQLException {
        Connection con = null;
        String goalId = null;
        try {
            con = pu.getConnection();
            PreparedStatement stmt = con.prepareStatement(GoalsHubQueries.goal);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                goalId = resultSet.getString("id");
            }

        } catch (Exception e) {
            logger.info("Failed to execute query for goal id. Exception: " + e.getMessage());
            pu.closeConnection(con);
            throw e;
        }

        pu.closeConnection(con);
        return goalId;
    }

    public Map<String, String> getGoalsHubMetrics(String metricId) throws SQLException {
        Map<String, String> metric = new LinkedHashMap<>();
        Connection con = null;
        try {
            con = pu.getConnection();
            PreparedStatement stmt = con.prepareStatement(GoalsHubQueries.goalsHubMetrics);
            stmt.setString(1, metricId);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    metric.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }

        } catch (Exception e) {
            logger.info("Failed to execute query for goal metric. Exception: " + e.getMessage());
            pu.closeConnection(con);
            throw e;
        }

        pu.closeConnection(con);
        return metric;
    }

    public Map<String, String> getGoalMetricDetails(String metricId) throws SQLException {
        Map<String, String> metric = new LinkedHashMap<>();
        Connection con = null;
        try {
            con = pu.getConnection();
            PreparedStatement stmt = con.prepareStatement(GoalsHubQueries.goalMetricDetails);
            stmt.setString(1, metricId);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    metric.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }

        } catch (Exception e) {
            logger.info("Failed to execute query for goal metric details. Exception: " + e.getMessage());
            pu.closeConnection(con);
            throw e;
        }

        pu.closeConnection(con);
        return metric;
    }

    public Map<String, Object> getGoalsHubMetricGoal(String goalId) throws SQLException {
        Map<String, Object> metric = new LinkedHashMap<>();
        Connection con = null;
        try {
            con = pu.getConnection();
            PreparedStatement stmt = con.prepareStatement(GoalsHubQueries.goalsHubMetricGoal);
            stmt.setString(1, goalId);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    metric.put(rsmd.getColumnName(i), resultSet.getObject(i));
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for metric goal. Exception: " + e.getMessage());
            pu.closeConnection(con);
            throw e;
        }

        pu.closeConnection(con);
        return metric;
    }

    public Map<String, Object> getGoalCalculation(String metricGoalId) throws SQLException {
        Map<String, Object> metric = new LinkedHashMap<>();
        Connection con = null;
        try {
            con = pu.getConnection();
            PreparedStatement stmt = con.prepareStatement(GoalsHubQueries.goalsHubGoalCalculation);
            stmt.setString(1, metricGoalId);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    metric.put(rsmd.getColumnName(i), resultSet.getObject(i));
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for goal calculation. Exception: " + e.getMessage());
            pu.closeConnection(con);
            throw e;
        }

        pu.closeConnection(con);
        return metric;
    }


}