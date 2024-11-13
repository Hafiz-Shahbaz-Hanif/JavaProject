package com.DC.db.analyze;

public class SearchFrequencyRankQueries {
    public static String queryToFetchSFRValues(String startDate, String endDate, String searchTerm, String retailerPlatform) {
        return "select DATE_KEY, SEARCH_FREQUENCY_RANK, SEARCH_TERM from REPORTING.T_SEARCH_FREQUENCY_RANK_WEEKLY" +
                " where DATE_KEY between '" + startDate + "' and '" + endDate + "'" +
                " and SEARCH_TERM like '%" + searchTerm + "%'" + " and RETAILER_PLATFORM = '" + retailerPlatform + "'" +
                " ORDER BY DATE_KEY;";
    }
}
