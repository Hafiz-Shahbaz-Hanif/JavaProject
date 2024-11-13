package com.DC.db.analyze;

public class ClipperQueries {

    public static String getEnabledDisabledStatus(int businessUnitId) {
        return "Select\n" +
                "bu.ID buId,\n" +
                "ca.ID clientAccountId,\n" +
                "ca.NAME clientAccountName,\n" +
                "wfl.IS_ACTIVE\n" +
                "from T_BUSINESS_UNIT bu\n" +
                "join T_BUSINESS_UNIT_CLIENT_ACCOUNT buca on bu.ID = buca.BUSINESS_UNIT_ID\n" +
                "join T_CLIENT_ACCOUNT ca on ca.ID = buca.CLIENT_ACCOUNT_ID\n" +
                "join T_CLIENT_ACCOUNT_WFL_ORGANIZATION wfl on wfl.CLIENT_ACCOUNT_ID = ca.ID\n" +
                "where bu.ID = " + businessUnitId + "";
    }

    public static String queryToGetUniqueClientID() {
        return "SELECT t1.*\n" +
                "FROM catalog.t_business_unit_retail_account t1\n" +
                "LEFT JOIN catalog.t_account_credentials t2 ON t1.id = t2.api_client_account_id\n" +
                "LEFT JOIN catalog.t_partner_client_account_mapping t3 on t1.id = t3.api_client_account_id\n" +
                "WHERE t2.api_client_account_id IS NULL\n" +
                "AND t3.api_client_account_id IS NULL;";
    }

    public static String queryToDeleteFromCredentialsTable(int clientId) {
        return "DELETE FROM catalog.t_account_credentials\n" +
                "WHERE ctid IN (\n" +
                "    SELECT ctid\n" +
                "    FROM catalog.t_account_credentials\n" +
                "    WHERE api_client_account_id = " + clientId + "\n" +
                "    LIMIT 1\n" +
                ")\n" +
                "RETURNING *;";
    }

    public static String queryToDeleteFromPartnerClientAccountMappingTable(int clientId, String partnerId) {
        return "delete from catalog.t_partner_client_account_mapping\n" +
                "where api_client_account_id = " + clientId + " and partner_id = '" + partnerId + "';";
    }

    public static String queryToGetClientAccountID(int clientAccountName) {
        return "select * from catalog.t_account_credentials where api_client_account_id = " + clientAccountName + ";";
    }

    public static String queryToGetClientMapping(int clientAccountName, String partnerId) {
        return "select * from catalog.t_partner_client_account_mapping where api_client_account_id = " + clientAccountName + "" +
                "and partner_id = '" + partnerId + "';";
    }
}
