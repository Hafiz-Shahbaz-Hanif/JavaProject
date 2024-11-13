package com.DC.apitests.adc.manage.clientAdministration;

import com.DC.db.analyze.ClipperQueries;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.PostgreSqlUtility;
import com.DC.utilities.SQLUtility;
import com.DC.utilities.SecurityAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class ClientManagementApiTest extends BaseClass {
    private Map<String, String> headers;
    private static final String currency = "USD";
    private static final String URL = READ_CONFIG.getHubExternalGateway() + "/admin/client/selection/businessunit/";
    private static final String PARTNER_ID_WFL = READ_CONFIG.getPartnerIdForWFL();
    private static final String PARTNER_ID_SUPPLY_CHAIN = READ_CONFIG.getPartnerIdForSupplyChain();
    private String urlForCredentials = READ_CONFIG.getHubExternalGateway() + "/authorization/credentials";
    private String urlForAuth0 = READ_CONFIG.getAuthNonProdURL() + "/oauth/token";
    private int clientAccountToCreate = 1000 + (int) (Math.random() * 1000);
    private String username = "AutoTestUser" + clientAccountToCreate;
    private String password = "AutoTestPassword" + clientAccountToCreate;
    private String qrCodeKey = "123456auto" + clientAccountToCreate;
    private String fccClientAccountId = "AutoTestClient" + clientAccountToCreate + "!";
    private final String CLIENT_ID = "OoMLZE79LrWJ0BgvL9eYmuRgGeA177ZC";
    private final String CLIENT_SECRET = "e6klA5zbcqGhS23VulMVh6cYJZALQ0hdQrsQ3qOULR9bm4vAl4pSOHyhukwAKgLs";
    private final String AUDIENCE = "https://dc-nonprod-api/";
    private final String GRANT_TYPE = "client_credentials";

    public ClientManagementApiTest() {}

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, true);
        driver.get(READ_CONFIG.getDcAppUrl());
        new DCLoginPage(driver).login(READ_CONFIG.getUsername(), READ_CONFIG.getPassword());

        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SecurityAPI.getAuthToken(driver));
        headers.put("Content-Type", "application/json");
    }

    public Map<String, Object> createFactFilterForAllCampaigns(String startDate, String endDate) {
        Map<String, Object> factFilter = new HashMap<>();
        factFilter.put("startDate", startDate);
        factFilter.put("endDate", endDate);
        factFilter.put("showMe", "CAMPAIGNS");
        return factFilter;
    }

    public Map<String, Object> createPaginationAttributesForAllCampaigns() {
        Map<String, Object> paginationAttributes = new HashMap<>();
        paginationAttributes.put("pageSize", 5);
        paginationAttributes.put("page", 1);
        paginationAttributes.put("orderAscending", false);
        paginationAttributes.put("orderAttribute", "spend");
        paginationAttributes.put("orderingAttribute", "_spend");
        return paginationAttributes;
    }

    @Test(description = "Client Management API - Verify Client is enabled in WFL", dataProvider = "businessUnitIds")
    public void verifyClientIsEnabledInWFL(int businessUnitId) throws Exception {
        String startDate = DateUtility.getFirstDayOfLastWeek();
        String endDate = DateUtility.getYesterday();
        headers.put("X-Currencycontext", currency);
        Map<String, Object> payload = new HashMap<>();
        payload.put("factFilter", createFactFilterForAllCampaigns(startDate, endDate));
        payload.put("paginationAttributes", createPaginationAttributesForAllCampaigns());

        var response = callEndpoint(URL + businessUnitId, "GET", new ObjectMapper().writeValueAsString(payload), headers, null);
        Assert.assertEquals(response.getStatusCode(), 200);

        Map<Integer, String> clientAccountIdAndStatuses = getCampaignsStatusForWFL(businessUnitId);
        List<String> clientAccounts = response.jsonPath().getList("clientAccount.clientAccounts");
        for (int i = 0; i < clientAccounts.size(); i++) {
            int clientAccountId = response.jsonPath().getInt("clientAccount.clientAccounts[" + i + "].clientAccountId");
            String wflStatus = response.jsonPath().getString("clientAccount.clientAccounts[" + i + "].wflEnabled");
            if (clientAccountIdAndStatuses.containsKey(clientAccountId)) {
                if (wflStatus.equalsIgnoreCase("true")) {
                    Assert.assertEquals(clientAccountIdAndStatuses.get(clientAccountId), "Y", "WFL Status is enabled in API but not in DB");
                } else {
                    Assert.assertTrue(clientAccountIdAndStatuses.get(clientAccountId).equalsIgnoreCase("N"), "WFL Status is disabled in API but not in DB");
                }
            } else {
                LOGGER.info("Client Account Id: " + clientAccountId + " is not present in DB. Which means this client account is not enabled in WFL. Checking Status in API");
                Assert.assertTrue(wflStatus.equalsIgnoreCase("false"), "WFL Status not disabled in API");
            }
        }
    }

    @Test(description = "Client Management API - Verify create/update credentials endpoint is working")
    public void verifyCreateCredentialsEndpoint() throws Exception {
        String clientAccountId = getClientAccountId();
        headers.put("x-client-account-id", clientAccountId);
        Map<String, Object> initialPayload = generateInitialPayload(PARTNER_ID_WFL, PARTNER_ID_SUPPLY_CHAIN);

        var response = callEndpoint(urlForCredentials, "POST", new ObjectMapper().writeValueAsString(initialPayload), headers, null);
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertTrue(response.jsonPath().getBoolean("success"), "Credentials creation failed");

        List<String> valuesFromResponse = getCredentialsEndpoint(clientAccountId);
        Assert.assertTrue(compareValues(valuesFromResponse), "Values are not as expected");

        //update the credentials
        updateValuesForChange();
        Map<String, Object> changePayload = generateChangePayload();
        response = callEndpoint(urlForCredentials, "PUT", new ObjectMapper().writeValueAsString(changePayload), headers, null);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"), "Credentials update failed");

        valuesFromResponse = getCredentialsEndpoint(clientAccountId);
        Assert.assertTrue(compareValues(valuesFromResponse), "Values are not as expected");

        // verify PUT request removes partner client account mapping if isActive set to false
        Map<String, Object> disablePayload = generateDisablePayload(initialPayload);
        response = callEndpoint(urlForCredentials, "PUT", new ObjectMapper().writeValueAsString(disablePayload), headers, null);
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(response.jsonPath().getBoolean("success"), "Credentials update failed");

        getCredentialsEndpoint(clientAccountId);

        String clientAccountIdEmpty = getPartnerClientAccountMappingTable(Integer.parseInt(clientAccountId));
        Assert.assertTrue(clientAccountIdEmpty.isEmpty(), "Partner client account mapping is not removed");

        // clean up
        deleteCredentialsFromTable(Integer.parseInt(clientAccountId));
        deletePartnerClientAccountMappingFromTable(Integer.parseInt(clientAccountId), PARTNER_ID_WFL, PARTNER_ID_SUPPLY_CHAIN);
        clientAccountIdEmpty = getClientAccountIdEmpty(Integer.parseInt(clientAccountId));
        if (!clientAccountIdEmpty.isEmpty()) {
            Assert.fail("Client Account ID is not deleted from the table");
        }
    }

    @Test(description = "Verify iframe code pulls appropriate org", dataProvider = "businessUnitIds")
    public void verifyIframeCode(int businessUnitId) throws Exception {
        headers.put("x-businessunitcontext", String.valueOf(businessUnitId));
        headers.put("x-currencycontext", currency);
        var response = callEndpoint(READ_CONFIG.getHubExternalGateway() + "/catalog/automated/case/wfl/service-account-auth", "GET", "", headers, null);
        boolean success = response.jsonPath().getBoolean("success");
        String token = response.jsonPath().getString("token");
        if (response.getStatusCode() == 400) {
            Assert.assertFalse(success, "Success is not false");
            Assert.assertNull(token, "Token is not null");
            String expectedMessage = "Account mapping not found";
            Assert.assertEquals(response.jsonPath().getString("message"), expectedMessage, "Message is not as expected");
            LOGGER.info("Account mapping not found for business unit id: " + businessUnitId + ". Token is null");
        } else {
            Assert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");
            Assert.assertTrue(success, "Success is not true");
            Assert.assertTrue(token.matches("[\\w-]+\\.[\\w-]+\\.[\\w-]+"), "Token is not as expected");
            LOGGER.info("Token is as expected for business unit id: " + businessUnitId);
        }
    }

    private Map<String, Object> generateInitialPayload(String... partnerIds) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userName", username);
        payload.put("password", password);
        payload.put("qrCodeKey", qrCodeKey);
        List<Map<String, Object>> credentialMappings = new ArrayList<>();

        if (partnerIds.length > 0) {
            for (String partnerId : partnerIds) {
                Map<String, Object> credentialMapping = new HashMap<>();
                credentialMapping.put("partnerId", partnerId);
                credentialMapping.put("isActive", true);
                credentialMappings.add(credentialMapping);
            }
        } else {
            Map<String, Object> credentialMapping = new HashMap<>();
            credentialMapping.put("partnerId", PARTNER_ID_WFL);
            credentialMapping.put("isActive", true);
            credentialMappings.add(credentialMapping);
        }
        payload.put("credentialMappings", credentialMappings);
        return payload;
    }

    private void updateValuesForChange() {
        username += "Changed";
        password += "Changed";
        qrCodeKey += "Changed";
    }

    private Map<String, Object> generateChangePayload() {
        Map<String, Object> payloadForChange = new HashMap<>();
        payloadForChange.put("userName", username);
        payloadForChange.put("password", password);
        payloadForChange.put("qrCodeKey", qrCodeKey);
        return payloadForChange;
    }

    public Map<Integer, String> getCampaignsStatusForWFL(int businessUnitId) throws SQLException {
        Map<Integer, String> clientAccountIdAndStatuses = new HashMap<>();
        SQLUtility.connectToServer();
        ResultSet rs = SQLUtility.executeQuery(ClipperQueries.getEnabledDisabledStatus(businessUnitId));
        while (rs.next()) {
            int clientAccountId = rs.getInt("clientAccountId");
            String status = rs.getString("IS_ACTIVE");
            clientAccountIdAndStatuses.put(clientAccountId, status);
        }
        SQLUtility.closeConnections();
        return clientAccountIdAndStatuses;
    }

    @DataProvider(name = "businessUnitIds")
    public Object[][] businessUnitIds() {
        return new Object[][]{
                {48},
                {319},
                {493},
                {1303}
        };
    }

    public List<String> getCredentialsEndpoint(String clientId) throws Exception {
        Map<String, String> originalHeaders = new HashMap<>(headers);

        String tokenForM2M = getM2MToken();
        headers.put("x-partner-name", "WFL");
        headers.put("x-client-account-id", clientId);
        headers.put("Authorization", "Bearer " + tokenForM2M);

        List<String> valuesFromGetCall = new ArrayList<>();

        var response = callEndpoint(urlForCredentials, "GET", "", headers, null);

        switch (response.getStatusCode()) {
            case 404:
                String message = response.jsonPath().getString("message");
                Assert.assertEquals(message, "Credentials not found", "Message is not as expected");
                break;
            case 200:
                String usernameFromResponse = response.jsonPath().getString("data.userName");
                String passwordFromResponse = response.jsonPath().getString("data.password");
                String qrCodeKeyFromResponse = response.jsonPath().getString("data.qrCodeKey");
                valuesFromGetCall.add(usernameFromResponse);
                valuesFromGetCall.add(passwordFromResponse);
                valuesFromGetCall.add(qrCodeKeyFromResponse);
                break;
            default:
                Assert.fail("Status code is not 200 or 404");
        }

        headers.clear();
        headers.putAll(originalHeaders);

        return valuesFromGetCall;
    }

    public boolean compareValues(List<String> valuesFromResponse) {
        Assert.assertEquals(valuesFromResponse.get(0), username, "Username is not as expected");
        Assert.assertEquals(valuesFromResponse.get(1), password, "Password is not as expected");
        Assert.assertEquals(valuesFromResponse.get(2), qrCodeKey, "QrCodeKey is not as expected");
        return true;
    }

    private String getM2MToken() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("client_id", CLIENT_ID);
        payload.put("client_secret", CLIENT_SECRET);
        payload.put("audience", AUDIENCE);
        payload.put("grant_type", GRANT_TYPE);

        var response = callEndpoint(urlForAuth0, "POST", new ObjectMapper().writeValueAsString(payload), headers, null);
        return response.jsonPath().getString("access_token");
    }

    public String getClientAccountId() throws SQLException {
        PostgreSqlUtility pu = new PostgreSqlUtility();
        Connection con;
        con = pu.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(ClipperQueries.queryToGetUniqueClientID());
        String clientAccountId = "";
        while (rs.next()) {
            clientAccountId = rs.getString("id");
        }
        pu.closeConnection(con);
        return clientAccountId;
    }

    public void deleteCredentialsFromTable(int clientId) throws SQLException {
        PostgreSqlUtility pu = new PostgreSqlUtility();
        Connection con;
        con = pu.getConnection();
        Statement stmt = con.createStatement();
        stmt.executeQuery(ClipperQueries.queryToDeleteFromCredentialsTable(clientId));
        pu.closeConnection(con);
    }

    public void deletePartnerClientAccountMappingFromTable(int clientId, String... partnerId) throws SQLException {
        PostgreSqlUtility pu = new PostgreSqlUtility();
        Connection con = pu.getConnection();
        try (Statement stmt = con.createStatement()) {
            for (String partner : partnerId) {
                stmt.executeUpdate(ClipperQueries.queryToDeleteFromPartnerClientAccountMappingTable(clientId, partner));
            }
        } finally {
            pu.closeConnection(con);
        }
    }

    public String getClientAccountIdEmpty(int clientId) throws SQLException {
        String clientAccountId = "";
        PostgreSqlUtility pu = new PostgreSqlUtility();
        Connection con;
        con = pu.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(ClipperQueries.queryToGetClientAccountID(clientId));
        while (rs.next()) {
            clientAccountId = rs.getString("id");
        }
        pu.closeConnection(con);
        return clientAccountId;
    }

    public String getPartnerClientAccountMappingTable(int clientId, String... partnerId) throws SQLException {
        String clientAccountId = "";
        PostgreSqlUtility pu = new PostgreSqlUtility();
        Connection con;
        con = pu.getConnection();
        Statement stmt = con.createStatement();
        for (String partner : partnerId) {
            ResultSet rs = stmt.executeQuery(ClipperQueries.queryToGetClientMapping(clientId, partner));
            while (rs.next()) {
                clientAccountId = rs.getString("api_client_account_id");
            }
        }
        pu.closeConnection(con);
        return clientAccountId;
    }

    private Map<String, Object> generateDisablePayload(Map<String, Object> initialPayload) {
        Map<String, Object> disablePayload = new HashMap<>(initialPayload);
        List<Map<String, Object>> credentialMappings;
        credentialMappings = (List<Map<String, Object>>) initialPayload.get("credentialMappings");
        for (Map<String, Object> credentialMapping : credentialMappings) {
            credentialMapping.put("isActive", false);
        }
        return disablePayload;
    }
}
