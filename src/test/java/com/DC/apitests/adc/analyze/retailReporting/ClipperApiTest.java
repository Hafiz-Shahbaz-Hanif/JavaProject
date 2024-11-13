package com.DC.apitests.adc.analyze.retailReporting;

import com.DC.db.analyze.CaseManagementReportingQueries;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.RedShiftUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.analyze.caseManagementReporting.CaseManagementReportingRequestBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class ClipperApiTest extends BaseClass {

    private HashMap<String, String> headers;
    private final String CLIPPER_URL = READ_CONFIG.getHubExternalGateway() + "/catalog/clipper/";
    Integer[] clientAccountIds = {918, 920, 923, 921, 922};
    int businessUnitId = 1320;
    List<String> segmentationFilters = new ArrayList<>();
    List<String> asinIds = new ArrayList<>();
    String startDate = DateUtility.getFirstDayOfThisMonth();
    String endDate = DateUtility.getYesterday();

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, true);
        driver.get(READ_CONFIG.getDcAppUrl());
        new DCLoginPage(driver).login(READ_CONFIG.getUsername(), READ_CONFIG.getPassword());

        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SecurityAPI.getAuthToken(driver));
        headers.put("Content-Type", "application/json");
    }

    @AfterClass()
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify total cases count")
    public void Api_VerifyTotalCasesCount() throws Exception {

        CaseManagementReportingRequestBody requestBody = new CaseManagementReportingRequestBody(
                startDate, endDate,
                new CaseManagementReportingRequestBody.PagingAttributes(
                        1,
                        100,
                        0,
                        false,
                        "createdOn"),
                segmentationFilters,
                "DAILY", businessUnitId, null, asinIds, clientAccountIds);

        var response = callEndpoint(CLIPPER_URL + "caseLog", "POST", new ObjectMapper().writeValueAsString(requestBody), headers, null);
        Assert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody().asString());
        JsonNode metaData = jsonNode.get("meta");
        int totalRecordsCurrent = metaData.get("totalCount").asInt();

        List<Integer> valuesFromDB = getCurrentAndPreviousWeekDataFromDB(startDate, endDate);
        int totalRecordsInDB = valuesFromDB.get(0);
        int totalRecordsInDBPreviousPeriod = valuesFromDB.get(1);
        Assert.assertEquals(totalRecordsCurrent, totalRecordsInDB, "Current total records count from API and DB do not match");
        double percentageChangeFromDB = calculatePercentageChange(totalRecordsInDB, totalRecordsInDBPreviousPeriod);

        Map<String, Object> valuesFromTopGraphAPI = getTopGraphData();
        double automatedCasePreviousPeriod = (double) valuesFromTopGraphAPI.get("automatedCasePreviousPeriod");
        double differenceBetweenCurrentAndPastRounded = Math.round(automatedCasePreviousPeriod * 100.0) / 100.0;

        Assert.assertEquals(percentageChangeFromDB, differenceBetweenCurrentAndPastRounded, "Automated cases count from API and DB do not match");
    }

    @Test(description = "Verify graph API and data")
    public void Api_VerifyGraphData() throws Exception {

        CaseManagementReportingRequestBody requestBody = new CaseManagementReportingRequestBody(
                businessUnitId, "DAILY", "Time Graph", startDate, endDate, segmentationFilters, null, asinIds, clientAccountIds);

        var response = callEndpoint(CLIPPER_URL + "graph", "POST", new ObjectMapper().writeValueAsString(requestBody), headers, null);
        Assert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");
        List<Integer> valuesFromDB = getCurrentAndPreviousWeekDataFromDB(startDate, endDate);
        double timeSavedCurrent = calculateTimeSaved(valuesFromDB.get(0));
        double timeSavedPreviousPeriod = calculateTimeSaved(valuesFromDB.get(1));
        double percentageChange = calculatePercentageChange(timeSavedCurrent, timeSavedPreviousPeriod);
        Map<String, Object> valuesFromTopGraphAPI = getTopGraphData();
        int timeSaved = (int) valuesFromTopGraphAPI.get("timeSaved");
        double timeSavedPreviousPeriodFromAPI = (double) valuesFromTopGraphAPI.get("timeSavedPreviousPeriod");
        Assert.assertEquals(Math.round(timeSavedCurrent), timeSaved, "Time saved current from API and DB do not match");
        Assert.assertEquals(percentageChange, timeSavedPreviousPeriodFromAPI, "Time saved previous period from API and DB do not match");
    }

    @Test(description = "Verify top grid API and data")
    public void Api_VerifyTopGraphData() throws Exception {

        List<Integer> conversionValuesFromDB = getCurrentAndPreviousConversionRateFromDB(startDate, endDate);
        int conversionRateFromDB = conversionValuesFromDB.get(0);
        int conversionRatePreviousPeriodFromDB = conversionValuesFromDB.get(1);
        double percentageChange = calculatePercentageChange(conversionRateFromDB, conversionRatePreviousPeriodFromDB);
        Map<String, Object> valuesFromTopGraphAPI = getTopGraphData();
        int conversionRateFromAPI = (int) valuesFromTopGraphAPI.get("conversionRate");
        Assert.assertEquals(conversionRateFromDB, conversionRateFromAPI, "Conversion rate from API and DB do not match");
        double conversionRatePreviousPeriodFromAPI = (double) valuesFromTopGraphAPI.get("conversionRatePreviousPeriod");
        double previousConversionRateRounded = Math.round(conversionRatePreviousPeriodFromAPI * 100.0) / 100.0;
        Assert.assertEquals(percentageChange, previousConversionRateRounded, "Conversion rate previous period from API and DB do not match");
    }

    public Map<String, Object> getTopGraphData() throws Exception {
        Map<String, Object> valuesFromTopGraphAPI = new HashMap<>();

        CaseManagementReportingRequestBody requestBody = new CaseManagementReportingRequestBody(
                startDate, endDate, segmentationFilters, "DAILY", businessUnitId, asinIds, null, clientAccountIds);

        var response = callEndpoint(CLIPPER_URL + "timegraph/topGrid", "POST", new ObjectMapper().writeValueAsString(requestBody), headers, null);
        Assert.assertEquals(response.getStatusCode(), 200, "Status code is not 200");

        double automatedCasePreviousPeriod = response.jsonPath().getDouble("automatedCasePreviousPeriod");
        int timeSaved = response.jsonPath().getInt("timeSaved");
        double timeSavedPreviousPeriod = response.jsonPath().getDouble("timeSavedPreviousPeriod");
        int conversionRate = response.jsonPath().getInt("conversionRate");
        double conversionRatePreviousPeriod = response.jsonPath().getDouble("conversionRatePreviousPeriod");
        valuesFromTopGraphAPI.put("automatedCasePreviousPeriod", automatedCasePreviousPeriod);
        valuesFromTopGraphAPI.put("timeSaved", timeSaved);
        valuesFromTopGraphAPI.put("timeSavedPreviousPeriod", timeSavedPreviousPeriod);
        valuesFromTopGraphAPI.put("conversionRate", conversionRate);
        valuesFromTopGraphAPI.put("conversionRatePreviousPeriod", conversionRatePreviousPeriod);
        return valuesFromTopGraphAPI;
    }

    public List<Integer> getCurrentAndPreviousWeekDataFromDB(String startDate, String endDate) throws SQLException {
        List<Integer> valuesFromDB = new ArrayList<>();
        RedShiftUtility.connectToServer();
        ResultSet rs = RedShiftUtility.executeQuery(CaseManagementReportingQueries.queryToFetchCurrentAndPreviousTotalAutomatedCases(String.valueOf(businessUnitId), startDate, endDate));
        while (rs.next()) {
            valuesFromDB.add(rs.getInt("totalautomatedcases"));
            valuesFromDB.add(rs.getInt("totalautomatedpreviouscases"));
        }
        RedShiftUtility.closeConnections();
        return valuesFromDB;
    }

    public double calculatePercentageChange(double currentValue, double previousValue) {
        double percentageChange = ((currentValue - previousValue) / previousValue) * 100.00;
        return Math.round(percentageChange * 100.0) / 100.0;
    }

    public double calculateTimeSaved(int casesSubmitted) {
        return ((double) casesSubmitted * 2) / 60;
    }

    public List<Integer> getCurrentAndPreviousConversionRateFromDB(String startDate, String endDate) throws SQLException {
        List<Integer> valuesFromDB = new ArrayList<>();
        RedShiftUtility.connectToServer();
        ResultSet rs = RedShiftUtility.executeQuery(CaseManagementReportingQueries.queryToFetchConversionRate(String.valueOf(businessUnitId), startDate, endDate));
        while (rs.next()) {
            valuesFromDB.add(rs.getInt("conversionrate"));
            valuesFromDB.add(rs.getInt("previousweek_conversionrate"));
        }
        RedShiftUtility.closeConnections();
        return valuesFromDB;
    }
}
