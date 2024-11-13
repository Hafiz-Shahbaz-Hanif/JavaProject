package com.DC.db.analyze;

import com.DC.utilities.RedShiftUtility;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RoundUpDbFunctions {

    private RedShiftUtility ru;
    private Logger logger;

    public RoundUpDbFunctions () {
        ru = new RedShiftUtility();
        logger = Logger.getLogger(RoundUpDbFunctions.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    public Map<String, String> getRealTimeSalesHourlyData(int date, int buId, int hour) throws SQLException {
        Map<String, String> hourlyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesHourlyData);
            stmt.setInt(1, date);
            stmt.setInt(2, buId);
            stmt.setInt(3, hour);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    hourlyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for hourlyData. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return hourlyData;
    }

    public List<Integer> getRealTimeSalesHourlyData(int date, int buId) throws SQLException {
        List<Integer> hourlyOrderedUnites = new ArrayList<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesHourlyDataDay);
            stmt.setInt(1, date);
            stmt.setInt(2, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                hourlyOrderedUnites.add(resultSet.getInt("units"));
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for hourlyOrderedUnits. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return hourlyOrderedUnites;
    }

    public Map<String, String> getRealTimeSalesDailyData(int date, int buId) throws SQLException {
        Map<String, String> realTimeSalesDailyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesDaily);
            stmt.setInt(1, date);
            stmt.setInt(2, buId);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    realTimeSalesDailyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for realTimeSalesDailyData. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return realTimeSalesDailyData;
    }

    public int getLatestDayDataAvailableFromSource(int buId, String dataSource) throws SQLException {
        int latestDaySpApi = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.dateSpApiDailyAvailable);
            stmt.setInt(1, buId);
            stmt.setString(2, dataSource);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                latestDaySpApi = resultSet.getInt("date_sk");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for latestDaySpApi. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return latestDaySpApi;
    }

    public int getLatestWeekDataAvailableFromSource(int buId, String dataSource) throws SQLException {
        int latestWeekSpApiAvailable = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.dateSpApiWeeklyAvailable);
            stmt.setInt(1, buId);
            stmt.setString(2, dataSource);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                latestWeekSpApiAvailable = resultSet.getInt("date_sk");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for latestWeekSpApiAvailable. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return latestWeekSpApiAvailable;
    }

    public int getLatestMonthSpApiAvailable(int buId, String dataSource) throws SQLException {
        int latestWeekSpApiAvailable = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.dateSpApiMonthlyAvailable);
            stmt.setInt(1, buId);
            stmt.setString(2, dataSource);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                latestWeekSpApiAvailable = resultSet.getInt("date_sk");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for latestWeekSpApiAvailable. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return latestWeekSpApiAvailable;
    }

    public int getAsinWithOrderedUnits(int startDate, int endDate, int buId) throws SQLException {
        int asinId = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.asinIdForProductGrid);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setInt(3, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                asinId = resultSet.getInt("asin_id");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for asin id. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return asinId;
    }

    public double getRealTimeSalesDailyOrderedRevenue(int startDate, int endDate, int asinId, int buId, String dataSource) throws SQLException {
        double dailyOrderedRevenue = 0.0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesDailyOrderedRevenue);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setInt(3, asinId);
            stmt.setInt(4, buId);
            stmt.setString(5, dataSource);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                dailyOrderedRevenue = resultSet.getDouble("ordered_revenue");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for daily_ordered_revenue. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return dailyOrderedRevenue;
    }

    public int getRealTimeSalesOrderedUnitsForLatestWeek(int buId) throws SQLException {
        int latestDaySpApi = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesDataWeekly);
            stmt.setInt(1, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                latestDaySpApi = resultSet.getInt("ordered_units");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for real time sales data latest week. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return latestDaySpApi;
    }

    public int getRealTimeSalesOrderedUnitsForLatestMonth(int buId) throws SQLException {
        int latestDaySpApi = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesDataMonthly);
            stmt.setInt(1, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                latestDaySpApi = resultSet.getInt("ordered_units");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for real time sales data latest month. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return latestDaySpApi;
    }

    public int getRealTimeSalesHourlyOrderedUnitsForSegment(int startDate, int endDate, String segment, int buId) throws SQLException {
        int latestDaySpApi = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesHourlyForSegment);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setString(3, segment);
            stmt.setInt(4, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                latestDaySpApi = resultSet.getInt("ordered_units");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for real time sales data latest month. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return latestDaySpApi;
    }

    public Map<String, Object> getLatestDayFullHourlyDataAvailable(int today, int buId) throws SQLException {
        Map<String, Object> hourlyDate = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.latestDayForFullHourlyData);
            stmt.setInt(1, today);
            stmt.setInt(2, buId);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    hourlyDate.put(rsmd.getColumnName(i), resultSet.getObject(i));
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for latest day full hourly data. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return hourlyDate;
    }

    public Map<String, String> getSumOfHourlyData(int startDate, int endDate, int buId) throws SQLException {
        Map<String, String> monthlyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.sumOfHourlyData);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setInt(3, buId);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    monthlyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }

        } catch (Exception e) {
            logger.info("Failed to execute query for sum of hourly data. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return monthlyData;
    }

    public int getTotalHourlyRecords(int startDate, int endDate, int buId) throws SQLException {
        int totalHourlyRecords = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.totalHourlyRecords);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setInt(3, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                totalHourlyRecords = resultSet.getInt("count");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for total hourly records. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return totalHourlyRecords;
    }

    public int getTotalDailyRecords(int startDate, int endDate, String dataSource, int buId) throws SQLException {
        int totalHourlyRecords = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.totalDailyRecords);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setString(3, dataSource);
            stmt.setInt(4, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                totalHourlyRecords = resultSet.getInt("count");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for total daily records. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return totalHourlyRecords;
    }

    public int getTotalWeeklyRecords(int startDate, int endDate, int buId) throws SQLException {
        int totalWeeklyRecords = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.totalWeeklyRecords);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setInt(3, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                totalWeeklyRecords = resultSet.getInt("count");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for total weekly records. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return totalWeeklyRecords ;
    }

    public int getTotalMonthlyRecords(int startDate, int endDate, int buId) throws SQLException {
        int totalMonthlyRecords = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.totalMonthlyRecords);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setInt(3, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                totalMonthlyRecords = resultSet.getInt("count");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for total monthly records. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return totalMonthlyRecords ;
    }

    public int getMultiBuTotalHourlyRecords(int startDate, int endDate, int buId1, int buId2) throws SQLException {
        int totalHourlyRecords = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.totalMultiBuHourlyRecords);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setInt(3, buId1);
            stmt.setInt(4, buId2);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                totalHourlyRecords = resultSet.getInt("count");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for total hourly records. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return totalHourlyRecords;
    }

    public Map<String, String> getRealTimeSalesHourlyDataByAsinByBuId(int date, String asin, int buId, int hour) throws SQLException {
        Map<String, String> hourlyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesHourlyDataByAsin);
            stmt.setInt(1, date);
            stmt.setString(2, asin);
            stmt.setInt(3, buId);
            stmt.setInt(4, hour);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    hourlyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for hourly data by asin. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return hourlyData;
    }

    public Map<String, String> getRealTimeSalesMultiBuHourlyDataByAsinByBuName(int date, String asin, String buName, int hour) throws SQLException {
        Map<String, String> hourlyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesMultiBuHourlyDataByAsin);
            stmt.setInt(1, date);
            stmt.setString(2, asin);
            stmt.setString(3, buName);
            stmt.setInt(4, hour);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    hourlyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for hourly data by asin. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return hourlyData;
    }

    public int getTotalHourlyRecordsByAsin(int startDate, int endDate, int buId) throws SQLException {
        int totalHourlyRecords = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.totalHourlyRecordsByAsin);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setInt(3, buId);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                totalHourlyRecords = resultSet.getInt("count");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for total hourly records. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return totalHourlyRecords;
    }

    public int getMultiBuTotalHourlyRecordsByAsin(int startDate, int endDate, int buId1, int buId2) throws SQLException {
        int totalHourlyRecords = 0;
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.totalMultiBuHourlyRecordsByAsin);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setInt(3, buId1);
            stmt.setInt(4, buId2);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                totalHourlyRecords = resultSet.getInt("count");
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for multi BU total hourly records by Asin. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return totalHourlyRecords;
    }

    public Map<String, String> getRealTimeSalesHourlyDataForFullDayByAsin(int startDate, int endDate, String asin, int buId) throws SQLException {
        Map<String, String> hourlyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesDailyHourlyDataByAsin);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setString(3, asin);
            stmt.setInt(4, buId);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    hourlyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for hourly data for full day by asin. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return hourlyData;
    }

    public Map<String, String> getRealTimeSalesMultiBuFullDayHourlyDataByAsinByBuName(int startDate, int endDate, String asin, String buName) throws SQLException {
        Map<String, String> hourlyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesMultiBuFullDayHourlyDataByAsin);
            stmt.setInt(1, startDate);
            stmt.setInt(2, endDate);
            stmt.setString(3, asin);
            stmt.setString(4, buName);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    hourlyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for hourly data by asin. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return hourlyData;
    }

    public Map<String, String> getRealTimeSalesDailyDataByAsinByBuId(int startDate, String asin, int buId, String dataSource) throws SQLException {
        Map<String, String> dailyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesDailyDataByAsin);
            stmt.setInt(1, startDate);
            stmt.setString(2, asin);
            stmt.setInt(3, buId);
            stmt.setString(4, dataSource);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    dailyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for daily data by asin. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return dailyData;
    }

    public Map<String, String> getRealTimeSalesWeeklyDataByAsinByBuId(int startDate, String asin, int buId, String dataSource) throws SQLException {
        Map<String, String> weeklyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesWeeklyDataByAsin);
            stmt.setInt(1, startDate);
            stmt.setString(2, asin);
            stmt.setInt(3, buId);
            stmt.setString(4, dataSource);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    weeklyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for weekly data by asin. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return weeklyData;
    }

    public Map<String, String> getRealTimeSalesMonthlyDataByAsinByBuId(int startDate, String asin, int buId, String dataSource) throws SQLException {
        Map<String, String> monthlyData = new LinkedHashMap<>();
        Connection connection = null;
        Session session = null;
        try {
            session = ru.connectSshForRedshift();
            connection = ru.connectToRedshift();
            PreparedStatement stmt = connection.prepareStatement(RoundUpQueries.realTimeSalesMonthlyDataByAsin);
            stmt.setInt(1, startDate);
            stmt.setString(2, asin);
            stmt.setInt(3, buId);
            stmt.setString(4, dataSource);
            ResultSet resultSet = stmt.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    monthlyData.put(rsmd.getColumnName(i), resultSet.getObject(i).toString());
                }
            }
        } catch (Exception e) {
            logger.info("Failed to execute query for monthly data by asin. Exception: " + e.getMessage());
            ru.closeConnections(connection, session);
            throw e;
        }
        ru.closeConnections(connection, session);
        return monthlyData;
    }

}