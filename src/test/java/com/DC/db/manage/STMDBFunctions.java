package com.DC.db.manage;

import com.DC.utilities.PostgreSqlUtility;
import org.testng.Assert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class STMDBFunctions {
    public static boolean getRetailersFromDB(String BU, String... retailerToBeSearched) {
        PostgreSqlUtility pu = new PostgreSqlUtility();
        Connection con;
        List<String> retailersInDB = new ArrayList<>();
        try {
            con = pu.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SearchTermManagementQueries.getQueryToFetchRetailers(BU, retailerToBeSearched))) {

                while (resultSet.next()) {
                    String retailer = resultSet.getString("retailer");
                    retailersInDB.add(retailer);
                }
                if (!retailersInDB.containsAll(Arrays.asList(retailerToBeSearched))) {
                    Assert.fail("Retailers have retailers that are not expected");
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                pu.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public static List<String> getRetailersAssignedToBUFromDB(String businessUnitId) {
        PostgreSqlUtility pu = new PostgreSqlUtility();
        Connection con;
        List<String> retailersAssignedFromDB = new ArrayList<>();
        try {
            con = pu.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SearchTermManagementQueries.getQueryToFetchRetailersAssignedToBU(businessUnitId))) {

                while (resultSet.next()) {
                    String retailer = resultSet.getString("retailers");
                    retailersAssignedFromDB.add(retailer);
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                pu.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return retailersAssignedFromDB;
    }

    public static int getNumberOfRecordsFromDB(String BU, String... retailerToBeSearched) {
        PostgreSqlUtility pu = new PostgreSqlUtility();
        Connection con;
        int recordCount = 0;
        try {
            con = pu.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SearchTermManagementQueries.fetchNumberOfRecords(BU, retailerToBeSearched))) {

                while (resultSet.next()) {
                    recordCount = resultSet.getInt("record_count");
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                pu.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return recordCount;
    }
}
