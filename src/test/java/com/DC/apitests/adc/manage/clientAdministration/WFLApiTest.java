package com.DC.apitests.adc.manage.clientAdministration;

import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SQLUtility;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class WFLApiTest extends BaseClass {
    private static final String baseURIForWFL = READ_CONFIG.getWFLApiUrl();
    private static final String wflUsername = READ_CONFIG.getWFLUsername();
    private static final String wflPassword = READ_CONFIG.getWFLPassword();

    private static final String payload = "{\n" +
            "  \"email\": \"" + wflUsername + "\",\n" +
            "  \"password\": \"" + wflPassword + "\"\n" +
            "}";

    String dateToCheck = DateUtility.getYesterday();
    int organizationIdForWFL = 414;
    int createdByIdForWFL = 524;

    public static String getTokenAPI() throws Exception {
        String token = null;
        RestAssured.baseURI = baseURIForWFL;
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.header("Content-Type", "application/json");
        httpRequest.body(payload);
        Response response = httpRequest.request(Method.POST, "/Auth/login");
        if (response.getStatusCode() == HttpStatus.SC_OK) {
            String rawToken = response.getBody().asString().trim();
            token = rawToken.replaceAll("^\"|\"$", "");
        }
        return token;
    }

    public Map<String, String> createHeaderMap(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("accept", "application/json");
        return headers;
    }

    public Map<String, Object> params() {
        Map<String, Object> params = new HashMap<>();
        params.put("organizationId", organizationIdForWFL);
        params.put("createdByIds", createdByIdForWFL);
        params.put("lookBack", dateToCheck);
        return params;
    }

    @Test(description = "Client Management API - Verify WFL received all the submitted cases")
    public void verifyWFLReceivedAllTheSubmittedCases() throws Exception {
        Map<String, String> headers = createHeaderMap(getTokenAPI());

        int totalPages = getTotalPagesFromAPI();
        int totalCasesSubmittedForWFL = 0;
        List<String> createdOnSpecificDate = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            Map<String, Object> params = params();
            params.put("page", page);

            String endpoint = baseURIForWFL + "/Ticket/log" + "?organizationId=" + params.get("organizationId") + "&createdByIds=" + params.get("createdByIds") +
                    "&lookBack=" + params.get("lookBack") + "&page=" + params.get("page");

            var response = callEndpoint(endpoint, "GET", "", headers, "");
            Assert.assertEquals(response.getStatusCode(), 200);

            List<String> allTickets = response.jsonPath().getList("tickets");

            for (int i = 0; i < allTickets.size(); i++) {
                String createdOn = response.jsonPath().getString("tickets[" + i + "].createdOn");
                String workflow = response.jsonPath().getString("tickets[" + i + "].workflow");
                if (createdOn.contains(dateToCheck) && !workflow.equals("ManageUser")) {
                    String id = response.jsonPath().getString("tickets[" + i + "].id");
                    createdOnSpecificDate.add(id);
                }
            }
        }
        totalCasesSubmittedForWFL += createdOnSpecificDate.size();

        String status = "Submitted For WFL";
        List<String> caseIds = getSubmittedCases(dateToCheck, status);
        int numberOfCasesSubmittedForWFL = caseIds.size();

        if (totalCasesSubmittedForWFL != numberOfCasesSubmittedForWFL) {
            Assert.fail("Number of cases submitted for WFL from API and DB are not matching. " +
                    "Non-matching cases found in API: " + getExtraCases(createdOnSpecificDate, caseIds) + ", Non-matching cases found in DB: " + getMissingCases(createdOnSpecificDate, caseIds));
        }
    }

    public List<String> getSubmittedCases(String date, String status) throws SQLException {
        List<String> caseIds = new ArrayList<>();
        SQLUtility.connectToServer();
        ResultSet rs = SQLUtility.executeQuery(getCasesAccordingToStatus(date, status));
        while (rs.next()) {
            caseIds.add(rs.getString("WFL_TICKET_ID"));
        }
        SQLUtility.closeConnections();
        return caseIds;
    }

    public int getTotalPagesFromAPI() throws Exception {
        Map<String, String> headers = createHeaderMap(getTokenAPI());
        Map<String, Object> params = params();

        String endpoint = baseURIForWFL + "/Ticket/log" + "?organizationId=" + params.get("organizationId") + "&createdByIds=" + params.get("createdByIds") + "&lookBack=" + params.get("lookBack");

        var response = callEndpoint(endpoint, "GET", "", headers, "");
        int numberOfRecords = response.jsonPath().getInt("total");
        int totalPages = numberOfRecords / 100;
        if (numberOfRecords % 100 != 0) {
            totalPages++;
        }
        return totalPages;
    }

    public String getCasesAccordingToStatus(String date, String status) {
        return "select *, a.STATUS\n" +
                "from T_CASE_LANGUAGE_DETAIL cld\n" +
                "join T_CASE_LANGUAGE_DETAIL_ASIN a on a.T_CASE_LANGUAGE_DETAIL_ID = cld.ID\n" +
                "where a.STATUS = '" + status + "'\n" +
                "and CAST(cld.CREATED_ON as DATE) = '" + date + "'";
    }

    public List<String> getExtraCases(List<String> createdOnSpecificDate, List<String> caseIds) {
        List<String> extraCases = new ArrayList<>();

        for (String caseId : createdOnSpecificDate) {
            if (!caseIds.contains(caseId)) {
                extraCases.add(caseId);
            }
        }
        return extraCases;
    }

    public List<String> getMissingCases(List<String> createdOnSpecificDate, List<String> caseIds) {
        List<String> missingCases = new ArrayList<>();

        for (String caseId : caseIds) {
            if (!createdOnSpecificDate.contains(caseId)) {
                missingCases.add(caseId);
            }
        }
        return missingCases;
    }
}
