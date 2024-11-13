package com.DC.db.identify;

import java.util.List;

public class SOVQueries {

    public static String queryToFetchSOVStatuses(String BU, String startDate, String endDate, String retailerPlatform, String placementType, List<String> searchTerms) {
        return "select * from REPORTING.T_FACT_MANUFACTURER_SOV_DAILY " +
                "where placement = '" + placementType + "' " +
                "and region = 'us' " +
                "and search_term IN ('" + String.join("','", searchTerms) + "') " +
                "and manufacturer ILIKE '%" + BU + "%' " +
                "and retailer_platform = '" + retailerPlatform + "' " +
                "and date_key IN ('" + startDate + "', '" + endDate + "') " +
                "order by SEARCH_TERM asc;";
    }

    public static String queryToFetchSFRValues(String startDate, String endDate, String retailerPlatform, List<String> searchTerms) {
        return "select DATE_KEY, RETAILER_PLATFORM, SEARCH_TERM, SEARCH_FREQUENCY_RANK " +
                "from REPORTING.T_SEARCH_FREQUENCY_RANK_WEEKLY " +
                "where search_term IN ('" + String.join("','", searchTerms) + "') " +
                "and region = 'us' " +
                "and retailer_platform = '" + retailerPlatform + "' " +
                "and date_key IN ('" + startDate + "', '" + endDate + "');";
    }

    public static String queryToFetchAverageSOV(String BU, String startDate, String endDate, String placementType, List<String> searchTerms) {
        return "SELECT search_term, " +
                "       AVG(WEIGHTED_SHARE) AS average_value " +
                "FROM REPORTING.T_FACT_MANUFACTURER_SOV_DAILY " +
                "WHERE placement = '" + placementType + "' " +
                "  AND region = 'us' " +
                "  AND search_term IN ('" + String.join("','", searchTerms) + "') " +
                "  AND manufacturer ILIKE '%" + BU + "%' " +
                "  AND ((retailer_platform = 'walmart.com' AND ZIPCODE = '72712') OR retailer_platform = 'amazon.com') " +
                "  AND date_key IN ('" + startDate + "', '" + endDate + "') " +
                "GROUP BY search_term;";
    }

    public static String queryToCalculateWeight(int SFRNumber, String date) {
        return "SELECT ROUND(POWER(" + SFRNumber + ", EXPONENT), 8) AS weight " +
                "FROM REPORTING.SEARCH_RATE_CURVE_LOOKUP " +
                "WHERE REGION = 'us' " +
                "  AND INTERVAL = 'DAILY'" +
                "  AND '" + date + "' BETWEEN START_DATE AND END_DATE;";
    }

    public static String queryToFetchRPCAndRankValues(String date, String retailerPlatform, String placementType, String hour, String rpc, List<String> searchTerms) {
        return "select * " +
                "from REPORTING.T_FACT_SEARCH_RANK_HOURLY " +
                "where DATE_KEY = '" + date + "' " +
                "and RETAILER_PLATFORM = '" + retailerPlatform + "' " +
                "and REGION = 'us' " +
                "and PLACEMENT = '" + placementType + "' " +
                "and SEARCH_TERM IN ('" + String.join("','", searchTerms) + "') " +
                "and HOUR_KEY = '" + hour + "' " +
                "and RPC = '" + rpc + "' " +
                "order by RANK asc;";
    }
}
