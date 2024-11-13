package com.DC.db.execute;

import com.DC.utilities.RedShiftUtility;
import com.DC.utilities.SQLUtility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SharedDBMethods {

    public static Map<String, String> getItemToSelect(int buId, int campaignToSelect) throws SQLException {
        Map<String, String> itemAndCampaignName = new HashMap<>();
        SQLUtility.connectToServer();
        ResultSet rs = SQLUtility.executeQuery(FlightdeckQueries.queryToFetchActiveCampaigns(buId, campaignToSelect));
        if (rs.next()) {
            String itemToSelect = rs.getString("itemApiUnitId");
            String campaignName = rs.getString("CAMPAIGN_NAME");
            itemAndCampaignName.put("itemApiUnitId", itemToSelect);
            itemAndCampaignName.put("CAMPAIGN_NAME", campaignName);
        }
        SQLUtility.closeConnections();
        return itemAndCampaignName;
    }

    public static String getLastDateFromDB(String itemToSelect) throws SQLException {
        RedShiftUtility.connectToServer();
        ResultSet rs = RedShiftUtility.executeQuery(FlightdeckQueries.queryToGetMostRecentAvailableDate(itemToSelect));
        rs.next();
        String mostRecentDate = rs.getString("bu_date");
        RedShiftUtility.closeConnections();
        return mostRecentDate;
    }

    public static void updateDownloadDate(String itemToSelect, String mostRecentDate) throws SQLException {
        RedShiftUtility.connectToServer();
        RedShiftUtility.executeUpdateQuery(FlightdeckQueries.queryToUpdateDownloadDate(itemToSelect, mostRecentDate));
        RedShiftUtility.closeConnections();
    }

    public static void resetCampaignToOriginalValues(String itemToSelect, String oldDate) throws SQLException {
        String mostRecentDateFromDB = getLastDateFromDB(itemToSelect);
        RedShiftUtility.connectToServer();
        RedShiftUtility.executeUpdateQuery("UPDATE fw.amazon_intra_day_bidding_recommendation SET bu_date = '" + oldDate + "'\n" +
                "WHERE unit_id IN ('" + itemToSelect + "')\n" +
                "and bu_date = '" + mostRecentDateFromDB + "';");
        RedShiftUtility.closeConnections();
    }
}
