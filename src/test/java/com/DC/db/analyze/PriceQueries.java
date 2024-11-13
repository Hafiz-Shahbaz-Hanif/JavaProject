package com.DC.db.analyze;

public class PriceQueries {
    public static String catalogToJoin(String BU) {
        return "WITH catalog AS (SELECT B.REGION,\n" +
                "                        B.RETAILER,\n" +
                "                        B.DOMAIN       RETAILER_PLATFORM,\n" +
                "                        A.PRODUCT_CODE RPC,\n" +
                "                        CAT.MANUFACTURER,\n" +
                "                        CAT.BRAND\n" +
                "                 FROM REPORTING.PG_T_PRODUCT A\n" +
                "                          JOIN REPORTING.PG_MVW_ORGANIZATION_BUSINESS_UNIT_RETAILER_PLATFORM B\n" +
                "                               ON A.RETAILER_PLATFORM_ID = B.RETAILER_PLATFORM_ID\n" +
                "                          JOIN REPORTING.PG_MVW_CATALOG_ASSIGNMENT CAT\n" +
                "                               ON CAT.BUSINESS_UNIT_PROVISION_ID = B.BUSINESS_UNIT_PROVISION_ID\n" +
                "                                   AND A.ID = CAT.PRODUCT_ID\n" +
                "                 WHERE B.BUSINESS_UNIT = '" + BU + "')";
    }

    public static String queryToFetchPriceStatuses(String BU, String startDate, String endDate, String retailerPlatform, String brand) {
        String catalog = catalogToJoin(BU);
        return catalog + "select DATE_KEY, PDP.RPC, OBSERVED_PRICE from REPORTING.T_FACT_PRODUCT_DETAIL_PAGE PDP" +
                " inner join catalog cat on PDP.RPC = cat.RPC" +
                " where DATE_KEY between '" + startDate + "' and '" + endDate + "'" +
                " and PDP.RETAILER_PLATFORM = '" + retailerPlatform + "'" +
                " and cat.BRAND = '" + brand + "' order by PDP.RPC;";
    }

    public static String queryToFetchRPCsForAutoSearch(String BU, String startDate, String endDate, String rpcToSearch) {
        String catalog = catalogToJoin(BU);
        return catalog + "select distinct PDP.RPC from REPORTING.T_FACT_PRODUCT_DETAIL_PAGE PDP" +
                " inner join catalog cat on PDP.RPC = cat.RPC" +
                " where DATE_KEY between '" + startDate + "' and '" + endDate + "'" +
                " and PDP.RPC like '%" + rpcToSearch + "%';";
    }
}
