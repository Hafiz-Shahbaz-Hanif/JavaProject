package com.DC.db.analyze;

public class SearchRankQueries {

    public static String queryToFetchSearchRankStatuses(String startDate, String endDate, String retailerPlatform, String searchTerm, String RPC, String placementType) {
        return "select DATE_KEY, RETAILER_PLATFORM, PLACEMENT, RPC, SPONSORED, AVERAGE_RANK, BEST_RANK " +
                "from REPORTING.T_FACT_SEARCH_RANK_DAILY " +
                "where DATE_KEY BETWEEN '" + startDate + "' and '" + endDate + "' " +
                "and RETAILER_PLATFORM = '" + retailerPlatform + "' " +
                "and REGION = 'us' " +
                "and PLACEMENT = '" + placementType + "' " +
                "and SEARCH_TERM = '" + searchTerm + "' " +
                "and RPC = '" + RPC + "';";
    }
}
