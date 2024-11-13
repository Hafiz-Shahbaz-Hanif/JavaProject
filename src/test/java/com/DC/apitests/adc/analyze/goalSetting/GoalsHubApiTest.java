package com.DC.apitests.adc.analyze.goalSetting;

import com.DC.db.analyze.GoalsHubDbFunctions;
import com.DC.db.hubDbFunctions.HubDbFunctions;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.goalSetting.GoalsHubPage;
import com.DC.testcases.BaseClass;
import com.DC.uitests.adc.analyze.goalSetting.GoalsHubDataProvider;
import com.DC.utilities.CsvUtility;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.adc.analyze.goalSetting.GoalsHubRequests;
import com.DC.utilities.apiEngine.models.requests.adc.analyze.goalSetting.AllGoalsHubRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.analyze.goalSetting.GoalsHubRequestBody;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class GoalsHubApiTest extends BaseClass {

    String authToken;
    String pdpGoalId;
    String salesGoalId;
    String headers;
    GoalsHubDbFunctions goalsHubDbFunctions;
    int minGoalValueToUpdate;
    int maxGoalValueToUpdate;
    GoalsHubRequestBody goalsHubReqBody;
    Map<String, String> pdpGoal = new HashMap<>();
    Map<String, String> salesGoal = new HashMap<>();
    Map<String, String> buRetailerPlatform = new HashMap<>();
    List<String> goalIds = new ArrayList<>();
    GoalsHubPage goalsHubPage;
    HubDbFunctions hubDbFunctions;
    String quarterly = GoalsHubRequests.intervals.get("quarterly");
    String monthly = GoalsHubRequests.intervals.get("monthly");
    String yearly = GoalsHubRequests.intervals.get("yearly");
    String custom = GoalsHubRequests.intervals.get("custom");
    String availability = GoalsHubRequests.metrics.get("availability");
    String shipped_revenue = GoalsHubRequests.metrics.get("shipped_revenue");
    String ordered_units = GoalsHubRequests.metrics.get("ordered_units");
    String shipped_units = GoalsHubRequests.metrics.get("shipped_units");
    String shipped_cogs = GoalsHubRequests.metrics.get("shipped_cogs");
    String ordered_revenue = GoalsHubRequests.metrics.get("ordered_revenue");
    String future = GoalsHubRequests.periods.get("future");
    String past = GoalsHubRequests.periods.get("past");
    String all = GoalsHubRequests.periods.get("all");
    String present = GoalsHubRequests.periods.get("present");
    String metricRetail = GoalsHubRequests.metricTypes.get("retail");
    String metricPdp = GoalsHubRequests.metricTypes.get("pdp");
    String sourcing = GoalsHubRequests.distributorViews.get("sourcing");
    String manufacturing = GoalsHubRequests.distributorViews.get("manufacturing");
    int minGoalValue = SharedMethods.getRandomNumber(45);
    int maxGoalValue = 85;

    @BeforeClass()
    public void setUp(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(READ_CONFIG.getDcAppUrl());
        new DCLoginPage(driver).loginDcApp(READ_CONFIG.getHubFilaOnlyUserEmail(), READ_CONFIG.getHubFilaOnlyUserPassword());
        authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        goalsHubDbFunctions = new GoalsHubDbFunctions();
        goalsHubPage = new GoalsHubPage(driver);
        hubDbFunctions = new HubDbFunctions();

        buRetailerPlatform.put("buId", hubDbFunctions.getBuId("McCormick US"));
        buRetailerPlatform.put("retailerPlatformId", hubDbFunctions.getRetailerId("United States", "Amazon", "amazon.com"));

        pdpGoal.put("metricId", hubDbFunctions.getGoalMetricId(availability));
        pdpGoal.put("goalType", "RANGE");
        salesGoal.put("metricId", hubDbFunctions.getGoalMetricId(ordered_units));
        salesGoal.put("goalType", "ABSOLUTE");

        headers = "X-BusinessUnitContext=" + buRetailerPlatform.get("buId") + ";" + "X-RetailerPlatformContext=" + buRetailerPlatform.get("retailerPlatformId");
        String pivotId = GoalsHubRequests.getBrandId("mccormick", headers, authToken);

        pdpGoal.put("pivotid", pivotId);
        pdpGoal.put("pivot", "BRAND");
        salesGoal.put("pivotid", pivotId);
        salesGoal.put("pivot", "BRAND");
    }

    @Test(priority = 1, description = "RAR-147 - Goals Hub - PDP Goal creation")
    public void Api_Goals_Hub_Create_PDP_Goal_Test() throws Exception {
        String startDate = DateUtility.getFirstDayOfThisMonth();
        String endDate = DateUtility.getLastDayOfThisMonth();

        minGoalValue = SharedMethods.getRandomNumber(45);
        maxGoalValue = 85;

        GoalsHubRequestBody.GoalValueSpecification gvs = new GoalsHubRequestBody.GoalValueSpecification(String.valueOf(minGoalValue), String.valueOf(maxGoalValue), null, null);
        goalsHubReqBody = new GoalsHubRequestBody(pdpGoal.get("metricId"), monthly, startDate, endDate, pdpGoal.get("pivot"), pdpGoal.get("pivotid"), gvs, pdpGoal.get("goalType"), "");
        pdpGoalId = GoalsHubRequests.goalsHubString(goalsHubReqBody, headers, authToken);
        goalIds.add(pdpGoalId);
    }

    @Test(priority = 2, description = "RAR-147 - Goals Hub - Duplicate goal creation")
    public void Api_Goals_Hub_Create_Duplicate_Goal_Test() throws Exception {
        Response response = GoalsHubRequests.goalsHub(goalsHubReqBody, headers, authToken);
        Assert.assertEquals(response.statusCode(), 400, "** Duplicate goal creation error.");
    }

    @Test(priority = 3, description = "RAR-159 - Goals Hub - PDP Goal metric details")
    public void Api_Goals_Hub_PDP_Goal_Metrics_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        JSONObject goalCreated = null;

        AllGoalsHubRequestBody allGoalsRequestBody = new AllGoalsHubRequestBody(present, monthly, List.of(availability), null);
        JSONObject response = GoalsHubRequests.allGoalsHubJson(allGoalsRequestBody, headers, authToken);
        if (response.has(metricPdp)) {
            JSONArray goals = response.getJSONArray(metricPdp);
            for (int i = 0; i < goals.length(); i++) {
                JSONObject goal = goals.getJSONObject(i);
                if (goal.getString("goalMetricID").equals(pdpGoalId)) {
                    goalCreated = goal;
                }
            }
        }

        softAssert.assertEquals(pdpGoal.get("goalType"), goalCreated.getString("goalType"), "** Goal type not matched.");
        softAssert.assertEquals(pdpGoal.get("pivot"), goalCreated.getString("pivotType"), "** Pivot type not matched.");
        softAssert.assertEquals(availability, goalCreated.get("metric"), "** Metric not matched.");
        softAssert.assertEquals("PERCENTAGE", goalCreated.get("metricType"), "** Metric type not matched.");
        softAssert.assertEquals(metricPdp, goalCreated.get("metricDataSourceType"), "** Metric data source type not matched.");
        softAssert.assertTrue(Objects.equals(goalCreated.get("goalReached"), null), "** Goal reached not null");
        softAssert.assertTrue(Objects.equals(goalCreated.get("goalRemaining"), null), "** Goal remaining not null");
        softAssert.assertTrue(Objects.equals(goalCreated.get("goalReachedPercentage"), null), "** Goal reached percentage not null");
        softAssert.assertEquals(new BigDecimal(minGoalValue), goalCreated.getJSONObject("specification").getBigDecimal("minValue").setScale(0, RoundingMode.DOWN), "** Goal min value not matching.");
        softAssert.assertEquals(new BigDecimal(maxGoalValue), goalCreated.getJSONObject("specification").getBigDecimal("maxValue").setScale(0, RoundingMode.DOWN), "** Goal max value not matching.");
        softAssert.assertAll();
    }

    @Test(priority = 4, description = "RAR-141 - Goals Hub - Goal update")
    public void Api_Goals_Hub_Update_Test() throws Exception {
        minGoalValueToUpdate = SharedMethods.getRandomNumberBetweenRange(46, 55);
        maxGoalValueToUpdate = 75;

        GoalsHubRequestBody.GoalValueSpecification gvs = new GoalsHubRequestBody.GoalValueSpecification(String.valueOf(minGoalValueToUpdate), String.valueOf(maxGoalValueToUpdate), null, null);
        GoalsHubRequestBody goalsHubReqBody = new GoalsHubRequestBody(pdpGoalId, gvs);
        GoalsHubRequests.updateGoal(goalsHubReqBody, headers, authToken);

        Map<String, Object> goal = goalsHubDbFunctions.getGoalsHubMetricGoal(pdpGoalId);
        String goalValue = goal.get("goal_specification").toString();
        Assert.assertTrue(goalValue.contains(String.valueOf(minGoalValueToUpdate)));
    }

    @Test(priority = 5, description = "RAR-682-637 - Goals Hub - Sales Goal creation")
    public void Api_Goals_Hub_Create_Sales_Goal_Test() throws Exception {
        int goalValue = SharedMethods.getRandomNumberBetweenRange(1200, 4000);

        GoalsHubRequestBody.GoalValueSpecification gvs = new GoalsHubRequestBody.GoalValueSpecification(null, null, String.valueOf(goalValue), manufacturing);
        goalsHubReqBody = new GoalsHubRequestBody(salesGoal.get("metricId"), quarterly, "2024-01-01", "2024-03-31", salesGoal.get("pivot"), salesGoal.get("pivotid"), gvs, salesGoal.get("goalType"), "");
        salesGoalId = GoalsHubRequests.goalsHubString(goalsHubReqBody, headers, authToken);
        goalIds.add(salesGoalId);
    }

    @Test(priority = 6, description = "RAR-159-682 - Goals Hub - Sales Goal metric details")
    public void Api_Goals_Hub_Sales_Goal_Metrics_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        JSONObject goalCreated = null;

        AllGoalsHubRequestBody allGoalsRequestBody = new AllGoalsHubRequestBody(all, quarterly, List.of(ordered_units), null);
        JSONObject response = GoalsHubRequests.allGoalsHubJson(allGoalsRequestBody, headers, authToken);
        if (response.has(metricRetail)) {
            JSONArray goals = response.getJSONArray(metricRetail);
            for (int i = 0; i < goals.length(); i++) {
                JSONObject goal = goals.getJSONObject(i);
                if (goal.getString("goalMetricID").equals(salesGoalId)) {
                    goalCreated = goal;
                }
            }
        }

        String specification = goalCreated.getJSONObject("specification").getString("distributorView");
        softAssert.assertEquals(manufacturing, specification, "** Specification not matched.");
        softAssert.assertAll();
    }

    @Test(priority = 7, description = "RAR-161-170 - Goals Hub - Goal delete")
    public void Api_Goals_Hub_Delete_Test() throws Exception {
        for (String goalId : goalIds) {
            GoalsHubRequests.deleteGoal(goalId, headers, authToken);
            Map<String, Object> goal = goalsHubDbFunctions.getGoalsHubMetricGoal(pdpGoalId);
            Assert.assertTrue(Boolean.parseBoolean(goal.get("deleted").toString()));
        }
    }

    @Test(priority = 8, description = "RAR-158-160 - Goals Hub - Goal Period and Interval Selection")
    public void Api_Goals_Hub_Goal_Interval_Selection_Api_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String presentMonthInterval = DateUtility.getFirstDayOfThisMonth() + "-" + DateUtility.getLastDayOfThisMonth();
        String futureYearInterval = DateUtility.getFirstDayOfNextYear() + "-" + DateUtility.getLastDayOfNextYear();
        JSONArray goals = null;

        AllGoalsHubRequestBody allGoalsRequestBody = new AllGoalsHubRequestBody(present, monthly, List.of(availability), null);
        JSONObject response = GoalsHubRequests.allGoalsHubJson(allGoalsRequestBody, headers, authToken);

        if (response.has(metricPdp)) {
            goals = response.getJSONArray(metricPdp);
            for (int i = 0; i < goals.length(); i++) {
                JSONObject goal = goals.getJSONObject(i);
                softAssert.assertEquals(goal.getString("goalInterval"), monthly, "** Goal interval option is not correct for present Monthly.");
                softAssert.assertEquals(presentMonthInterval, goal.getString("startDate") + "-" + goal.getString("endDate"), "** Goal date interval is not correct for present Monthly.");
            }
        }

        allGoalsRequestBody = new AllGoalsHubRequestBody(past, quarterly, List.of(availability), null);
        response = GoalsHubRequests.allGoalsHubJson(allGoalsRequestBody, headers, authToken);

        if (response.has(metricPdp)) {
            goals = response.getJSONArray(metricPdp);
            for (int i = 0; i < goals.length(); i++) {
                JSONObject goal = goals.getJSONObject(i);

                LocalDate startDate = DateUtility.convertToLocalDate(goal.getString("startDate"));
                LocalDate endDate = DateUtility.convertToLocalDate(goal.getString("endDate"));

                softAssert.assertEquals(goal.getString("goalInterval"), quarterly, "** Goal interval option interval is not correct for past Quarterly.");
                softAssert.assertTrue(endDate.isBefore(DateUtility.convertToLocalDate(DateUtility.getFirstDayOfThisMonth())), "** Goal date interval is not correct for past Quarterly.");
                softAssert.assertEquals(endDate.getMonthValue() - startDate.getMonthValue(), 2, "** Goal interval range is not correct for past Quarterly-" + startDate + " - " + endDate);
            }
        }

        allGoalsRequestBody = new AllGoalsHubRequestBody(future, yearly, List.of(availability), null);
        response = GoalsHubRequests.allGoalsHubJson(allGoalsRequestBody, headers, authToken);
        if (response.has(metricPdp)) {
            goals = response.getJSONArray(metricPdp);
            for (int i = 0; i < goals.length(); i++) {
                JSONObject goal = goals.getJSONObject(i);
                softAssert.assertEquals(goal.getString("goalInterval"), yearly, "** Goal interval option is not correct for future Yearly.");
                softAssert.assertEquals(futureYearInterval, goal.getString("startDate") + "-" + goal.getString("endDate"), "** Goal date interval is not correct for future Yearly.");
            }
        }

        allGoalsRequestBody = new AllGoalsHubRequestBody(all, custom, List.of(availability), null);
        response = GoalsHubRequests.allGoalsHubJson(allGoalsRequestBody, headers, authToken);
        if (response.has(metricPdp)) {
            goals = response.getJSONArray(metricPdp);
            for (int i = 0; i < goals.length(); i++) {
                JSONObject goal = goals.getJSONObject(i);
                softAssert.assertEquals(goal.getString("goalInterval"), custom, "** Goal interval option is not correct for Custom.");
            }
        }
        softAssert.assertAll();
    }

    @Test(priority = 9, description = "RAR-158 - Goals Hub - Fetch Goal by Id")
    public void Api_Goals_Hub_Fetch_Goal_By_Id_Api_Test() throws Exception {
        String goalId = goalsHubDbFunctions.getGoalId();
        JSONObject response = GoalsHubRequests.getGoalJson(goalId, headers, authToken);
        Assert.assertEquals(goalId, response.getString("goalId"), "** Goal id is not correct.");
    }

    @Test(priority = 10, description = "RAR-158 - Goals Hub - Goals Hub - Get Metric for Filter")
    public void Api_Goals_Hub_Get_Metrics_For_Filter_Api_Test() throws Exception {
        List<JSONObject> response = GoalsHubRequests.getGoalMetricJson(headers, authToken);
        JSONObject goalMetric = (JSONObject) SharedMethods.getRandomItemFromList(response);
        String metricIdApi = goalMetric.getString("metricId");
        String metricApi = goalMetric.getString("metric");
        Map<String, String> goalMetricDb = goalsHubDbFunctions.getGoalsHubMetrics(metricIdApi);
        String metricDb = goalMetricDb.get("metric");
        Assert.assertEquals(metricApi, metricDb, "** Metric name not matching.");
    }

    @Test(priority = 11, description = "RAR-524 - Goals Hub - Bulk Goal Create - Duplicate Goal Creation")
    public void Api_Goals_Hub_Bulk_Goal_Create_Duplicate_Goal_Creation_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        int randomValueForNonRange = SharedMethods.getRandomNumberBetweenRange(15, 999);
        String monthlyStartDate = "09/01/2023";
        String monthlyEndDate = "09/30/2023";
        String segmentationType = "BRAND";
        String brand = "nosalt";

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(past, monthly, List.of(shipped_units, shipped_cogs), List.of(brandId));
        JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        GoalsHubRequests.deleteGoals(metricRetail, goals, headers, authToken);

        String[] createShippedUnitsCsv = {shipped_units, monthly, "", monthlyStartDate, monthlyEndDate, segmentationType, brand, String.valueOf(randomValueForNonRange), "", "", manufacturing};
        String[] createShippedCogsCsv = {shipped_cogs, monthly, "", monthlyStartDate, monthlyEndDate, segmentationType, brand, String.valueOf(randomValueForNonRange), "", "", manufacturing};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-creation", GoalsHubPage.createHeaderCsv, createShippedCogsCsv, createShippedUnitsCsv);
        GoalsHubRequests.goalsBulkCreateJson(filePath, headers, authToken);

        GoalsHubRequests.goalsBulkCreate(filePath, headers, authToken);
        goalsRequestBody = new AllGoalsHubRequestBody(past, monthly, List.of(shipped_units, shipped_cogs), List.of(brandId));
        goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);

        JSONArray saleGoals = goals.getJSONArray(metricRetail);
        softAssert.assertEquals(saleGoals.length(), 2, "** Sale goals count not matching. Duplicate goal created.");
        softAssert.assertAll();
    }

    @Test(priority = 12, description = "RAR-524 - Goals Hub - Bulk Goal Create - PDP and Sales Future Goals")
    public void Api_Goals_Bulk_Create_Pdp_Sales_Future_Goals_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        int minValueForRange = SharedMethods.getRandomNumberBetweenRange(15, 500);
        int maxValueForRange = SharedMethods.getRandomNumberBetweenRange(501, 999);
        String monthlyStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfNextMonth());
        String monthlyEndDate = DateUtility.formattingDate(DateUtility.getLastDayOfNextMonth());
        String segmentationType = "BRAND";
        String brand = "frenchs";

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(future, monthly, List.of(availability, shipped_cogs), List.of(brandId));
        JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        GoalsHubRequests.deleteGoals(metricRetail, goals, headers, authToken);
        GoalsHubRequests.deleteGoals(metricPdp, goals, headers, authToken);

        String[] createAvailabilityCsv = {availability.split("_")[0], monthly, "", monthlyStartDate, monthlyEndDate, segmentationType, brand, "", String.valueOf(minValueForRange), String.valueOf(maxValueForRange), ""};
        String[] createShippedCogsCsv = {shipped_cogs, monthly, "", monthlyStartDate, monthlyEndDate, segmentationType, brand, String.valueOf(maxValueForRange), "", "", manufacturing};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-creation", GoalsHubPage.createHeaderCsv, createShippedCogsCsv, createAvailabilityCsv);
        GoalsHubRequests.goalsBulkCreateJson(filePath, headers, authToken);

        goalsRequestBody = new AllGoalsHubRequestBody(future, monthly, List.of(availability, shipped_cogs), List.of(brandId));
        goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);

        JSONArray saleGoals = goals.getJSONArray(metricRetail);
        softAssert.assertEquals(saleGoals.length(), 1, "** Sale goals count not matching.");

        JSONArray pdpGoals = goals.getJSONArray(metricPdp);
        softAssert.assertEquals(pdpGoals.length(), 1, "** PDP goals count not matching.");
        softAssert.assertAll();
    }

    @Test(priority = 13, description = "RAR-524 - Goals Hub - Bulk Goal Create - PDP and Sales Goals - Custom Interval Without Goal Title")
    public void Api_Goals_Bulk_Create_Custom_Interval_Goals_Without_Goal_Title_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        int minValueForRange = SharedMethods.getRandomNumberBetweenRange(15, 500);
        int maxValueForRange = SharedMethods.getRandomNumberBetweenRange(501, 999);
        String monthlyStartDate = "09/10/2023";
        String monthlyEndDate = "09/20/2023";
        ;
        String segmentationType = "BRAND";
        String brand = "tigers milk";

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(past, custom, List.of(availability, shipped_cogs), List.of(brandId));
        JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        GoalsHubRequests.deleteGoals(GoalsHubRequests.metricTypes.get("retail"), goals, headers, authToken);
        GoalsHubRequests.deleteGoals(GoalsHubRequests.metricTypes.get("pdp"), goals, headers, authToken);

        String[] createAvailabilityCsv = {availability.split("_")[0], custom, "", monthlyStartDate, monthlyEndDate, segmentationType, brand, "", String.valueOf(minValueForRange), String.valueOf(maxValueForRange), ""};
        String[] createShippedCogsCsv = {shipped_cogs, custom, "", monthlyStartDate, monthlyEndDate, segmentationType, brand, String.valueOf(maxValueForRange), "", "", manufacturing};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-creation", GoalsHubPage.createHeaderCsv, createShippedCogsCsv, createAvailabilityCsv);
        Response response = GoalsHubRequests.goalsBulkCreate(filePath, headers, authToken);
        softAssert.assertEquals(response.getStatusCode(), 400, "** Custom goal without title does not give error.");

        String errorMsg = new JSONObject(response.asString()).getJSONObject("errorDetails").getJSONArray("errors").getString(0);
        softAssert.assertTrue(errorMsg.contains("Goal Title is required"), "** Error message not matching.");
        softAssert.assertAll();
    }

    @Test(priority = 14, description = "RAR-524 - Goals Hub - Bulk Goal Create - Date and Interval Not Matching")
    public void Api_Goals_Bulk_Create_Date_And_Interval_Not_Matching_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        int randomValueForNonRange = SharedMethods.getRandomNumberBetweenRange(345, 999);
        String monthlyStartDate = "07/01/2023";
        String monthlyEndDate = "09/30/2023";
        ;
        String segmentationType = "BRAND";
        String brand = "tigers milk";

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(past, monthly, List.of(shipped_units, shipped_cogs), List.of(brandId));
        JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        GoalsHubRequests.deleteGoals(metricRetail, goals, headers, authToken);

        String[] createShippedUnitsCsv = {shipped_units, monthly, "", monthlyStartDate, monthlyEndDate, segmentationType, brand, String.valueOf(randomValueForNonRange), "", "", manufacturing};
        String[] createShippedCogsCsv = {shipped_cogs, monthly, "", monthlyStartDate, monthlyEndDate, segmentationType, brand, String.valueOf(randomValueForNonRange), "", "", manufacturing};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-creation", GoalsHubPage.createHeaderCsv, createShippedCogsCsv, createShippedUnitsCsv);
        Response response = GoalsHubRequests.goalsBulkCreate(filePath, headers, authToken);
        softAssert.assertEquals(response.getStatusCode(), 400, "** Invalid goal interval does not give error.");

        String errorMsg = new JSONObject(response.asString()).getJSONObject("errorDetails").getJSONArray("errors").getString(0);
        softAssert.assertTrue(errorMsg.contains("not valid for interval"), "** Error message not matching.");
        softAssert.assertAll();
    }

    @Test(priority = 15, description = "RAR-521 - Goals Hub - Bulk Goal Edit or Delete - CSV File")
    public void Api_Goals_Hub_Bulk_Goal_Edit_Or_Delete_Csv_File_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        int minValueForRange = SharedMethods.getRandomNumberBetweenRange(15, 500);
        int maxValueForRange = SharedMethods.getRandomNumberBetweenRange(501, 999);
        String firstDayOfNextYear = DateUtility.formattingDate(DateUtility.getFirstDayOfNextYear());
        String lastDayOfNextYear = DateUtility.formattingDate(DateUtility.getLastDayOfNextYear());
        String firstDayOfLastMonth = DateUtility.formattingDate(DateUtility.getFirstDayOfLastMonth());
        String lastDayOfLastMonth = DateUtility.formattingDate(DateUtility.getLastDayOfLastMonth());
        String customStartDate = "09/15/2023";
        String customEndDate = "09/30/2023";
        String segmentationType = "BRAND";
        String brand = "kitchen basics";

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody availabilityRequestBody = new AllGoalsHubRequestBody(future, yearly, List.of(availability), List.of(brandId));
        JSONObject availabilityApi = GoalsHubRequests.allGoalsHubJson(availabilityRequestBody, headers, authToken);
        GoalsHubRequests.deleteGoals(metricPdp, availabilityApi, headers, authToken);

        AllGoalsHubRequestBody shippedRevenueRequestBody = new AllGoalsHubRequestBody(past, monthly, List.of(shipped_revenue), List.of(brandId));
        JSONObject shippedRevenueApi = GoalsHubRequests.allGoalsHubJson(shippedRevenueRequestBody, headers, authToken);
        GoalsHubRequests.deleteGoals(metricRetail, shippedRevenueApi, headers, authToken);

        AllGoalsHubRequestBody orderedUnitsRequestBody = new AllGoalsHubRequestBody(past, custom, List.of(ordered_units), List.of(brandId));
        JSONObject orderedUnitsApi = GoalsHubRequests.allGoalsHubJson(orderedUnitsRequestBody, headers, authToken);
        GoalsHubRequests.deleteGoals(metricRetail, orderedUnitsApi, headers, authToken);

        String[] createAvailabilityCsv = {availability.split("_")[0], yearly, "", firstDayOfNextYear, lastDayOfNextYear, segmentationType, brand, "", String.valueOf(minValueForRange), String.valueOf(maxValueForRange), ""};
        String[] createShippedRevenueCsv = {shipped_revenue, monthly, "", firstDayOfLastMonth, lastDayOfLastMonth, segmentationType, brand, String.valueOf(maxValueForRange), "", "", manufacturing};
        String[] createOrderedUnitsCsv = {ordered_units, custom, "unit goals", customStartDate, customEndDate, segmentationType, brand, String.valueOf(maxValueForRange), "", "", sourcing};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-creation", GoalsHubPage.createHeaderCsv, createShippedRevenueCsv, createOrderedUnitsCsv, createAvailabilityCsv);
        GoalsHubRequests.goalsBulkCreateJson(filePath, headers, authToken);

        availabilityApi = GoalsHubRequests.allGoalsHubJson(availabilityRequestBody, headers, authToken);
        String availabilityGoalId = availabilityApi.getJSONArray(metricPdp).getJSONObject(0).getString("goalMetricID");
        shippedRevenueApi = GoalsHubRequests.allGoalsHubJson(shippedRevenueRequestBody, headers, authToken);
        String shippedRevenueGoalId = shippedRevenueApi.getJSONArray(metricRetail).getJSONObject(0).getString("goalMetricID");
        orderedUnitsApi = GoalsHubRequests.allGoalsHubJson(orderedUnitsRequestBody, headers, authToken);
        String orderedUnitsGoalId = orderedUnitsApi.getJSONArray(metricRetail).getJSONObject(0).getString("goalMetricID");

        maxValueForRange = SharedMethods.getRandomNumberBetweenRange(1, 999);
        String[] editAvailabilityCvs = {availabilityGoalId, availability.split("_")[0], yearly, "", firstDayOfNextYear, lastDayOfNextYear, segmentationType, brand, "", String.valueOf(minValueForRange), String.valueOf(maxValueForRange), "", "Y"};
        String[] editShippedRevenueCvs = {shippedRevenueGoalId, shipped_revenue, monthly, "", firstDayOfLastMonth, lastDayOfLastMonth, segmentationType, brand, String.valueOf(maxValueForRange), "", "", manufacturing, "N"};
        String[] editOrderedUnitsCvs = {orderedUnitsGoalId, ordered_units, custom, "unit goals", customStartDate, customEndDate, segmentationType, brand, String.valueOf(maxValueForRange), "", "", sourcing, "N"};

        filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-edit-delete", GoalsHubPage.editHeaderCvs, editAvailabilityCvs, editShippedRevenueCvs, editOrderedUnitsCvs);
        GoalsHubRequests.goalsBulkEditJson(filePath, headers, authToken);

        availabilityApi = GoalsHubRequests.allGoalsHubJson(availabilityRequestBody, headers, authToken);
        softAssert.assertTrue(availabilityApi.isEmpty(), "Availability is not deleted");

        shippedRevenueApi = GoalsHubRequests.allGoalsHubJson(shippedRevenueRequestBody, headers, authToken);
        String shippedRevenueEditedGoal = shippedRevenueApi.getJSONArray(metricRetail).getJSONObject(0).getJSONObject("specification").getBigDecimal("value").stripTrailingZeros().toPlainString();
        softAssert.assertEquals(shippedRevenueEditedGoal, String.valueOf(maxValueForRange), "Shipped Revenue is not edited");

        orderedUnitsApi = GoalsHubRequests.allGoalsHubJson(orderedUnitsRequestBody, headers, authToken);
        String orderedUnitsEditedGoal = orderedUnitsApi.getJSONArray(metricRetail).getJSONObject(0).getJSONObject("specification").getBigDecimal("value").stripTrailingZeros().toPlainString();
        softAssert.assertEquals(orderedUnitsEditedGoal, String.valueOf(maxValueForRange), "Ordered Units is not edited");
        softAssert.assertAll();
    }

    @Test(priority = 16, description = "RAR-521-525 - Goals Hub - Bulk Goal Export - CSV File")
    public void Api_Goals_Hub_Bulk_Goal_Export_Csv_File_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        int minValueForRange = SharedMethods.getRandomNumberBetweenRange(15, 500);
        int maxValueForRange = SharedMethods.getRandomNumberBetweenRange(501, 999);
        String firstDayOfNextMonth = DateUtility.formattingDate(DateUtility.getFirstDayOfNextMonth());
        String lastDayOfNextMonth = DateUtility.formattingDate(DateUtility.getLastDayOfNextMonth());
        String segmentationType = "BRAND";
        String brand = "kitchen basics";

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(future, monthly, List.of(availability, shipped_revenue, ordered_units), List.of(brandId));
        JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);

        GoalsHubRequests.deleteGoals(metricPdp, goals, headers, authToken);
        GoalsHubRequests.deleteGoals(metricRetail, goals, headers, authToken);

        String[] createAvailabilityCsv = {availability.split("_")[0], monthly, "", firstDayOfNextMonth, lastDayOfNextMonth, segmentationType, brand, "", String.valueOf(minValueForRange), String.valueOf(maxValueForRange), ""};
        String[] createShippedRevenueCsv = {shipped_revenue, monthly, "", firstDayOfNextMonth, lastDayOfNextMonth, segmentationType, brand, String.valueOf(maxValueForRange), "", "", manufacturing};
        String[] createOrderedUnitsCsv = {ordered_units, monthly, "", firstDayOfNextMonth, lastDayOfNextMonth, segmentationType, brand, String.valueOf(maxValueForRange), "", "", sourcing};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-creation", GoalsHubPage.createHeaderCsv, createShippedRevenueCsv, createOrderedUnitsCsv, createAvailabilityCsv);
        GoalsHubRequests.goalsBulkCreateJson(filePath, headers, authToken);

        JSONObject goalsApi = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        String availabilityGoalId = goalsApi.getJSONArray(metricPdp).getJSONObject(0).getString("goalMetricID");
        String shippedRevenueGoalId = goalsApi.getJSONArray(metricRetail).getJSONObject(0).getString("goalMetricID");

        String exportUrl = GoalsHubRequests.goalsExportString(goalsRequestBody, headers, authToken);
        File file = SharedMethods.importFileFromUrl(exportUrl, downloadFolder + "/export.csv");
        String exportFilePath = file.getPath();
        softAssert.assertEquals(CsvUtility.getAllColumnNames(exportFilePath), Arrays.asList(GoalsHubPage.editHeaderCvs), "Column names not matching.");

        int rowCount = CsvUtility.getRowCount(exportFilePath) - 1;
        int randomRow = SharedMethods.getRandomNumberBetweenRange(1, rowCount);

        String metric = CsvUtility.getCellContent(exportFilePath, randomRow, "Metric");
        String goalId = CsvUtility.getCellContent(exportFilePath, randomRow, "Goal Id");

        if (metric.equalsIgnoreCase(shipped_revenue)) {
            softAssert.assertEquals(goalId, shippedRevenueGoalId, "Goal id (shipped revenue) not matching.");
        } else if (metric.equalsIgnoreCase(availability.split("_")[0])) {
            softAssert.assertEquals(goalId, availabilityGoalId, "Goal id (availability) not matching.");
        }
        softAssert.assertAll();
    }

    @Test(priority = 17, description = "RAR-168 - Goals Hub - Multi Metric Goal Selection - Future Monthly")
    public void Api_Goals_Hub_Multi_Metric_Selection_Future_Monthly_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        List<String> metricList = List.of(availability, shipped_revenue, ordered_units, shipped_cogs);
        int minValueForRange = SharedMethods.getRandomNumberBetweenRange(15, 90);
        int maxValueForRange = SharedMethods.getRandomNumberBetweenRange(91, 500);
        int valueForNonRange = SharedMethods.getRandomNumberBetweenRange(501, 999);
        String firstDayOfNextMonth = DateUtility.formattingDate(DateUtility.getFirstDayOfNextMonth());
        String lastDayOfNextMonth = DateUtility.formattingDate(DateUtility.getLastDayOfNextMonth());
        String segmentationType = "BRAND";
        String brand = "club house";

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(future, monthly, metricList, List.of(brandId));
        JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);

        GoalsHubRequests.deleteGoals(metricPdp, goals, headers, authToken);
        GoalsHubRequests.deleteGoals(metricRetail, goals, headers, authToken);

        String[] createAvailabilityCsv = {availability.split("_")[0], monthly, "", firstDayOfNextMonth, lastDayOfNextMonth, segmentationType, brand, "", String.valueOf(minValueForRange), String.valueOf(maxValueForRange), ""};
        String[] createShippedRevenueCsv = {shipped_revenue, monthly, "", firstDayOfNextMonth, lastDayOfNextMonth, segmentationType, brand, String.valueOf(maxValueForRange), "", "", manufacturing};
        String[] createOrderedUnitsCsv = {ordered_units, monthly, "", firstDayOfNextMonth, lastDayOfNextMonth, segmentationType, brand, String.valueOf(maxValueForRange), "", "", sourcing};
        String[] createShippedCogsCsv = {shipped_cogs, monthly, "", firstDayOfNextMonth, lastDayOfNextMonth, segmentationType, brand, String.valueOf(valueForNonRange), "", "", manufacturing};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-creation", GoalsHubPage.createHeaderCsv, createShippedRevenueCsv, createOrderedUnitsCsv, createAvailabilityCsv, createShippedCogsCsv);
        GoalsHubRequests.goalsBulkCreateJson(filePath, headers, authToken);

        JSONObject goalsApi = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        verifyMultiGoalSelection(softAssert, goalsApi, metricList, firstDayOfNextMonth, lastDayOfNextMonth, monthly, true);
        softAssert.assertAll();
    }

    @Test(priority = 18, description = "RAR-168 - Goals Hub - Multi Metric Goal Selection - Past Yearly")
    public void Api_Goals_Hub_Multi_Metric_Selection_Past_Yearly_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        List<String> metricList = List.of(availability, shipped_revenue, ordered_units, shipped_cogs);
        int minValueForRange = SharedMethods.getRandomNumberBetweenRange(15, 90);
        int maxValueForRange = SharedMethods.getRandomNumberBetweenRange(91, 500);
        int valueForNonRange = SharedMethods.getRandomNumberBetweenRange(501, 999);
        String startDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastMonth());
        String endDate = DateUtility.formattingDate(DateUtility.getLastDayOfLastMonth());
        String segmentationType = "BRAND";
        String brand = "club house";

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(past, custom, metricList, List.of(brandId));
        JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);

        GoalsHubRequests.deleteGoals(metricPdp, goals, headers, authToken);
        GoalsHubRequests.deleteGoals(metricRetail, goals, headers, authToken);

        String[] createAvailabilityCsv = {availability.split("_")[0], custom, "av", startDate, endDate, segmentationType, brand, "", String.valueOf(minValueForRange), String.valueOf(maxValueForRange), ""};
        String[] createShippedRevenueCsv = {shipped_revenue, custom, "sr", startDate, endDate, segmentationType, brand, String.valueOf(maxValueForRange), "", "", manufacturing};
        String[] createOrderedUnitsCsv = {ordered_units, custom, "ou", startDate, endDate, segmentationType, brand, String.valueOf(maxValueForRange), "", "", sourcing};
        String[] createShippedCogsCsv = {shipped_cogs, custom, "sc", startDate, endDate, segmentationType, brand, String.valueOf(valueForNonRange), "", "", manufacturing};
        String[] createShippedUnitsCsv = {shipped_units, custom, "su", startDate, endDate, segmentationType, brand, String.valueOf(minValueForRange), "", "", manufacturing};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-creation", GoalsHubPage.createHeaderCsv, createShippedUnitsCsv, createShippedRevenueCsv, createOrderedUnitsCsv, createAvailabilityCsv, createShippedCogsCsv);
        GoalsHubRequests.goalsBulkCreateJson(filePath, headers, authToken);

        JSONObject goalsApi = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        verifyMultiGoalSelection(softAssert, goalsApi, metricList, startDate, endDate, custom, false);
        softAssert.assertAll();
    }

    @Test(priority = 20, description = "RAR-524 - Goals Hub - Bulk Goal Create - Distributor View Field for PDP and Sales Metrics")
    public void Api_Goals_Bulk_Goal_Create_Distributor_View_Pdp_Sales_Metrics_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        int minValueForRange = SharedMethods.getRandomNumberBetweenRange(15, 500);
        int maxValueForRange = SharedMethods.getRandomNumberBetweenRange(501, 999);
        String startDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastMonth());
        String endDate = DateUtility.formattingDate(DateUtility.getLastDayOfLastMonth());
        String segmentationType = "BRAND";
        String brand = "tigers milk";

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(past, custom, List.of(availability, ordered_units), List.of(brandId));
        JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);

        GoalsHubRequests.deleteGoals(GoalsHubRequests.metricTypes.get("retail"), goals, headers, authToken);
        GoalsHubRequests.deleteGoals(GoalsHubRequests.metricTypes.get("pdp"), goals, headers, authToken);

        String[] createAvailabilityCsv = {availability.split("_")[0], monthly, "", startDate, endDate, segmentationType, brand, "", String.valueOf(minValueForRange), String.valueOf(maxValueForRange), manufacturing};
        String[] createOrderedUnitsCsv = {ordered_units, monthly, "", startDate, endDate, segmentationType, brand, String.valueOf(maxValueForRange), "", "", ""};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "pdp", GoalsHubPage.createHeaderCsv, createAvailabilityCsv);
        GoalsHubRequests.goalsBulkCreate(filePath, headers, authToken);

        filePath = CsvUtility.createCsvFile(downloadFolder, "sales", GoalsHubPage.createHeaderCsv, createOrderedUnitsCsv);
        Response orderedUnitsResponse = GoalsHubRequests.goalsBulkCreate(filePath, headers, authToken);

        goalsRequestBody = new AllGoalsHubRequestBody(past, custom, List.of(availability), List.of(brandId));
        goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        softAssert.assertTrue(goals.isEmpty(), "Availability goal created, not ignoring distributor view field.");

        goalsRequestBody = new AllGoalsHubRequestBody(past, custom, List.of(ordered_units), List.of(brandId));
        goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        softAssert.assertTrue(goals.isEmpty(), "Ordered units goal created without distributor view field.");

        String errorMsg = new JSONObject(orderedUnitsResponse.asString()).getJSONObject("errorDetails").getJSONArray("errors").getString(0);
        softAssert.assertTrue(errorMsg.contains("Distributor View is required"), "** Error message not matching for distributor view.");
        softAssert.assertAll();
    }

    @Test(priority = 21, description = "RAR-523 - Goals Hub - Bulk Goal Create - PDP and Sales Goals - Custom Interval Goal Title Verification")
    public void Api_Goals_Custom_Interval_Goal_Title_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        int minValueForRange = SharedMethods.getRandomNumberBetweenRange(15, 500);
        int maxValueForRange = SharedMethods.getRandomNumberBetweenRange(501, 999);
        String startDate = "2023-09-09";
        String endDate = "2023-09-17";
        ;
        String segmentationType = "BRAND";
        String brand = "old bay";

        String availabilityMetricId = hubDbFunctions.getGoalMetricId(availability);
        String orderedUnitsMetricId = hubDbFunctions.getGoalMetricId(ordered_units);
        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);

        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(past, custom, List.of(availability, ordered_units), List.of(brandId));
        JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);

        GoalsHubRequests.deleteGoals(metricRetail, goals, headers, authToken);
        GoalsHubRequests.deleteGoals(metricPdp, goals, headers, authToken);

        GoalsHubRequestBody.GoalValueSpecification gvsAvailability = new GoalsHubRequestBody.GoalValueSpecification(String.valueOf(minValueForRange), String.valueOf(maxValueForRange), null, null);
        GoalsHubRequestBody availabilityReqBody = new GoalsHubRequestBody(availabilityMetricId, custom, startDate, endDate, segmentationType, brandId, gvsAvailability, "RANGE", "av title");
        GoalsHubRequests.goalsHubString(availabilityReqBody, headers, authToken);

        GoalsHubRequestBody.GoalValueSpecification gvsOrderedUnits = new GoalsHubRequestBody.GoalValueSpecification(null, null, String.valueOf(maxValueForRange), manufacturing);
        GoalsHubRequestBody orderedUnitsReqBody = new GoalsHubRequestBody(orderedUnitsMetricId, custom, startDate, endDate, segmentationType, brandId, gvsOrderedUnits, "ABSOLUTE", "ou title");
        GoalsHubRequests.goalsHubString(orderedUnitsReqBody, headers, authToken);

        goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
        String availabilityGoalTitle = goals.getJSONArray(metricPdp).getJSONObject(0).getString("title");
        String orderedUnitsGoalTitle = goals.getJSONArray(metricRetail).getJSONObject(0).getString("title");

        softAssert.assertEquals(availabilityGoalTitle, "av title", "Availability goal title not matching.");
        softAssert.assertEquals(orderedUnitsGoalTitle, "ou title", "Ordered units goal title not matching.");
        softAssert.assertAll();
    }

    @Test(priority = 22, description = "RAR-522-519-526 - Goals Hub - PDP Goal, Pop, Yoy Calculation")
    public void Api_Goals_Hub_Pdp_Goal_Pop_Yoy_Calculation_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String brand = GoalsHubPage.brand;

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(past, monthly, List.of(availability), List.of(brandId));
        JSONObject availabilityJson = GoalsHubRequests.getRandomGoal(goalsRequestBody, metricPdp, headers, authToken);
        Optional.ofNullable(availabilityJson.isNull("goalReached") ? "There is no calculation available for availability goal." : null).ifPresent(Assert::fail);

        BigDecimal availabilityGoalCurrentValueApi = availabilityJson.getBigDecimal("goalReached");
        BigDecimal availabilityGoalPopValueApi = availabilityJson.isNull("popValue") ? new BigDecimal(0) : availabilityJson.getBigDecimal("popValue");
        BigDecimal availabilityGoalYoyValueApi = availabilityJson.isNull("yoyValue") ? new BigDecimal(0) : availabilityJson.getBigDecimal("yoyValue");
        BigDecimal availabilityGoalMinValueApi = availabilityJson.getJSONObject("specification").getBigDecimal("minValue");
        BigDecimal availabilityGoalMaxValueApi = availabilityJson.getJSONObject("specification").getBigDecimal("maxValue");
        BigDecimal availabilityGoalReachedPercentageValueApi = availabilityJson.getBigDecimal("goalReachedPercentage");

        if (availabilityGoalCurrentValueApi.compareTo(availabilityGoalMinValueApi) >= 0 && availabilityGoalCurrentValueApi.compareTo(availabilityGoalMaxValueApi) <= 0) {
            softAssert.assertEquals(availabilityGoalReachedPercentageValueApi.setScale(0, RoundingMode.HALF_UP), new BigDecimal(100), "Availability reached percentage not matching for goal value between range.");
        } else if (availabilityGoalCurrentValueApi.compareTo(availabilityGoalMinValueApi) < 0) {
            softAssert.assertEquals(availabilityGoalReachedPercentageValueApi.setScale(0, RoundingMode.HALF_UP), calculatePercentageChange(availabilityGoalCurrentValueApi, availabilityGoalMinValueApi), "Availability reached percentage not matching for goal value less than min value.");
        } else {
            softAssert.assertEquals(availabilityGoalReachedPercentageValueApi.setScale(0, RoundingMode.HALF_UP), calculatePercentageChange(availabilityGoalCurrentValueApi, availabilityGoalMaxValueApi), "Availability reached percentage not matching for goal value greater than max value.");
        }

        String availabilityGoalMetricIDApi = availabilityJson.getString("goalMetricID");
        Map<String, Object> availabilityGoalDb = goalsHubDbFunctions.getGoalCalculation(availabilityGoalMetricIDApi);
        BigDecimal availabilityGoalCurrentValueDb = (BigDecimal) availabilityGoalDb.get("current_value");
        BigDecimal availabilityGoalPopValueDb = (BigDecimal) availabilityGoalDb.get("pop_value");
        BigDecimal availabilityGoalYoyValueDb = (BigDecimal) availabilityGoalDb.get("yoy_value");

        BigDecimal availabilityPopPercentage = calculateYoyPop(availabilityGoalCurrentValueDb, availabilityGoalPopValueDb);
        BigDecimal availabilityYoyPercentage = calculateYoyPop(availabilityGoalCurrentValueDb, availabilityGoalYoyValueDb);

        softAssert.assertEquals(availabilityGoalCurrentValueApi.setScale(0, RoundingMode.DOWN), availabilityGoalCurrentValueDb.setScale(0, RoundingMode.DOWN), "Availability goal current value not matching.");
        softAssert.assertEquals(availabilityGoalPopValueApi.setScale(0, RoundingMode.DOWN), availabilityPopPercentage.setScale(0, RoundingMode.DOWN), "Availability goal pop value not matching.");
        softAssert.assertEquals(availabilityGoalYoyValueApi.setScale(0, RoundingMode.DOWN), availabilityYoyPercentage.setScale(0, RoundingMode.DOWN), "Availability goal yoy value not matching.");
        softAssert.assertAll();
    }

    @Test(priority = 23, description = "RAR-522-519-526-528 - Goals Hub - Sales Goal, Pop, Yoy Calculation")
    public void Api_Goals_Hub_Sales_Goal_Pop_Yoy_Calculation_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String brand = GoalsHubPage.brand;

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);
        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(past, monthly, List.of(ordered_units), List.of(brandId));
        JSONObject orderedUnitsJson = GoalsHubRequests.getRandomGoal(goalsRequestBody, metricRetail, headers, authToken);
        Optional.ofNullable(orderedUnitsJson.isNull("goalReached") ? "There is no calculation available for ordered units goal." : null).ifPresent(Assert::fail);

        BigDecimal orderedUnitsGoalCurrentValueApi = orderedUnitsJson.getBigDecimal("goalReached");
        BigDecimal orderedUnitsGoalPopValueApi = orderedUnitsJson.isNull("popValue") ? new BigDecimal(0) : orderedUnitsJson.getBigDecimal("popValue");
        BigDecimal orderedUnitsGoalYoyValueApi = orderedUnitsJson.isNull("yoyValue") ? new BigDecimal(0) : orderedUnitsJson.getBigDecimal("yoyValue");
        BigDecimal orderedUnitsGoalValueApi = orderedUnitsJson.getJSONObject("specification").getBigDecimal("value");
        BigDecimal orderedUnitsGoalReachedPercentageValueApi = orderedUnitsJson.getBigDecimal("goalReachedPercentage");
        BigDecimal orderedUnitsGoalRemainingValueApi = orderedUnitsJson.getBigDecimal("goalRemaining");

        softAssert.assertEquals(orderedUnitsGoalReachedPercentageValueApi.setScale(0, RoundingMode.DOWN), calculatePercentageChange(orderedUnitsGoalCurrentValueApi, orderedUnitsGoalValueApi).setScale(0, RoundingMode.DOWN), "Ordered units reached percentage not matching.");
        softAssert.assertEquals(orderedUnitsGoalRemainingValueApi.setScale(0, RoundingMode.DOWN), orderedUnitsGoalValueApi.subtract(orderedUnitsGoalCurrentValueApi).setScale(0, RoundingMode.DOWN), "Ordered units remaining value not matching.");

        String orderedUnitsGoalMetricIDApi = orderedUnitsJson.getString("goalMetricID");
        Map<String, Object> orderedUnitsGoalDb = goalsHubDbFunctions.getGoalCalculation(orderedUnitsGoalMetricIDApi);
        BigDecimal orderedUnitsGoalCurrentValueDb = (BigDecimal) orderedUnitsGoalDb.get("current_value");
        BigDecimal orderedUnitsGoalPopValueDb = (BigDecimal) orderedUnitsGoalDb.get("pop_value");
        BigDecimal orderedUnitsGoalYoyValueDb = (BigDecimal) orderedUnitsGoalDb.get("yoy_value");

        BigDecimal orderedUnitsPopPercentage = calculateYoyPop(orderedUnitsGoalCurrentValueDb, orderedUnitsGoalPopValueDb);
        BigDecimal orderedUnitsYoyPercentage = calculateYoyPop(orderedUnitsGoalCurrentValueDb, orderedUnitsGoalYoyValueDb);

        softAssert.assertEquals(orderedUnitsGoalCurrentValueApi.setScale(0, RoundingMode.DOWN), orderedUnitsGoalCurrentValueDb.setScale(0, RoundingMode.DOWN), "Ordered units goal current value not matching.");
        softAssert.assertEquals(orderedUnitsGoalPopValueApi.setScale(0, RoundingMode.DOWN), orderedUnitsPopPercentage.setScale(0, RoundingMode.DOWN), "Ordered units goal pop value not matching.");
        softAssert.assertEquals(orderedUnitsGoalYoyValueApi.setScale(0, RoundingMode.DOWN), orderedUnitsYoyPercentage.setScale(0, RoundingMode.DOWN), "Ordered units goal yoy value not matching.");
        softAssert.assertAll();
    }

    @Test(priority = 24, dataProvider = "Goals_Nightly_Calculations", dataProviderClass = GoalsHubDataProvider.class, description = "RAR-68 - Goals Hub - Sales and PDP Goals Nightly Calculation")
    public void Api_Goals_Hub_Sales_Pdp_Nightly_Calculation_Test(String metricType) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String metricId = null;
        int getGoalCount = 10;

        AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(present, monthly, List.of(availability, shipped_cogs, shipped_revenue, shipped_units,
                ordered_revenue, ordered_units), null);

        JSONObject salesMetric = GoalsHubRequests.getRandomGoal(goalsRequestBody, metricType, headers, authToken);
        metricId = salesMetric.getString("goalMetricID");
        Map<String, Object> orderedUnitsGoalDb = goalsHubDbFunctions.getGoalsHubMetricGoal(metricId);
        Timestamp dateGoalCreated = (Timestamp) orderedUnitsGoalDb.get("created_on");

        for (int i = 0; i < getGoalCount; i++) {
            if (!goalActive(dateGoalCreated)) {
                salesMetric = GoalsHubRequests.getRandomGoal(goalsRequestBody, metricType, headers, authToken);
                metricId = salesMetric.getString("goalMetricID");
                orderedUnitsGoalDb = goalsHubDbFunctions.getGoalsHubMetricGoal(metricId);
                dateGoalCreated = (Timestamp) orderedUnitsGoalDb.get("created_on");
            }

            if (goalActive(dateGoalCreated)) {
                break;
            }
        }

        Map<String, Object> goalCalc = goalsHubDbFunctions.getGoalCalculation(metricId);
        LocalDateTime dateTimeLastUpdatedDb = ((Timestamp) goalCalc.get("last_updated_on")).toLocalDateTime();
        LocalDate dateLastUpdatedDb = dateTimeLastUpdatedDb.toLocalDate();

        LocalDate currentUtcDate = ZonedDateTime.now(ZoneId.of("UTC")).toLocalDate();

        softAssert.assertEquals(dateLastUpdatedDb.compareTo(currentUtcDate), 0, "Overnight calculation failure for " + metricType + " metric (date).");
        softAssert.assertEquals(dateTimeLastUpdatedDb.getHour(), 0, "Overnight calculation failure for " + metricType + " metric (hour).");
        softAssert.assertAll();
    }

    //This will be updated.
    //@Test(priority = 19, description = "")
    public void Goals_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        List<String> metricList = List.of(availability, shipped_revenue, ordered_units, ordered_revenue, shipped_cogs);

        String presentMonthStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfThisMonth());
        String presentMonthEndDate = DateUtility.formattingDate(DateUtility.getLastDayOfThisMonth());

        String pastMonthStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastMonth());
        String pastMonthEndDate = DateUtility.formattingDate(DateUtility.getLastDayOfLastMonth());

        String pastYearStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastYear());
        String pastYearEndDate = DateUtility.formattingDate(DateUtility.getLastDayOfLastYear());

        String segmentationType = "BRAND";
        String brand = GoalsHubPage.brand;

        List<String> intervals = new ArrayList<>(GoalsHubRequests.intervals.values());
        List<String> periods = new ArrayList<>(GoalsHubRequests.periods.values());

        String brandId = GoalsHubRequests.getBrandId(brand, headers, authToken);

        for (String interval : intervals) {
            for (String period : periods) {
                AllGoalsHubRequestBody goalsRequestBody = new AllGoalsHubRequestBody(period, interval, metricList, List.of(brandId));
                JSONObject goals = GoalsHubRequests.allGoalsHubJson(goalsRequestBody, headers, authToken);
                GoalsHubRequests.deleteGoals(metricPdp, goals, headers, authToken);
                GoalsHubRequests.deleteGoals(metricRetail, goals, headers, authToken);
            }
        }

        String[] createAvailabilityCsv1 = {availability.split("_")[0], quarterly, "", "10/01/2023", "12/31/2023", segmentationType, brand, "", String.valueOf(10), String.valueOf(20), "", ""};
        String[] createAvailabilityCsv2 = {availability.split("_")[0], monthly, "", presentMonthStartDate, presentMonthEndDate, segmentationType, brand, "", String.valueOf(30), String.valueOf(70), "", ""};
        String[] createAvailabilityCsv3 = {availability.split("_")[0], yearly, "", pastYearStartDate, pastYearEndDate, segmentationType, brand, "", String.valueOf(60), String.valueOf(90), "", ""};
        String[] createAvailabilityCsv4 = {availability.split("_")[0], monthly, "", pastMonthStartDate, pastMonthEndDate, segmentationType, brand, "", String.valueOf(5), String.valueOf(10), "", ""};

        String[] createOrderedRevenueCsv = {ordered_revenue, quarterly, "", "10/01/2023", "12/31/2023", segmentationType, brand, String.valueOf(5000000), "", "", manufacturing, ""};
        String[] createOrderedUnitsCsv1 = {ordered_units, monthly, "", pastMonthStartDate, pastMonthEndDate, segmentationType, brand, String.valueOf(25000), "", "", manufacturing, ""};

        String[] createShippedRevenueCsv1 = {shipped_revenue, monthly, "", pastMonthStartDate, pastMonthEndDate, segmentationType, brand, String.valueOf(25000), "", "", manufacturing, ""};
        String[] createShippedRevenueCsv2 = {shipped_revenue, monthly, "", pastMonthStartDate, pastMonthEndDate, segmentationType, brand, String.valueOf(80000), "", "", manufacturing, ""};

        String[] createShippedCogs = {shipped_cogs, monthly, "", presentMonthStartDate, presentMonthEndDate, segmentationType, brand, String.valueOf(850000), "", "", manufacturing, ""};

        File filePath = CsvUtility.createCsvFile(downloadFolder, "bulk-goal-creation", GoalsHubPage.createHeaderCsv, createAvailabilityCsv1, createAvailabilityCsv2,
                createAvailabilityCsv3, createOrderedRevenueCsv, createOrderedUnitsCsv1, createShippedRevenueCsv1, createShippedRevenueCsv2,
                createAvailabilityCsv4, createShippedCogs);
        GoalsHubRequests.goalsBulkCreateJson(filePath, headers, authToken);

        softAssert.assertAll();
    }

    private void verifyMultiGoalSelection(SoftAssert softAssert, JSONObject goals, List<String> metricList, String startDate, String endDate, String interval, boolean isFutureGoal) throws Exception {
        List<JSONObject> listOfGoals = new ArrayList<>();
        List<String> goalNames = new ArrayList<>();
        List<String> metricTypes = new ArrayList<>(GoalsHubRequests.metricTypes.values());

        for (String metricType : metricTypes) {
            for (int i = 0; i < goals.getJSONArray(metricType).length(); i++) {
                listOfGoals.add(goals.getJSONArray(metricType).getJSONObject(i));
                goalNames.add(goals.getJSONArray(metricType).getJSONObject(i).getString("metric"));
            }
        }

        softAssert.assertEquals(new HashSet<>(metricList), new HashSet<>(goalNames), "Goal names not matching.");

        for (JSONObject goal : listOfGoals) {
            softAssert.assertEquals(DateUtility.formattingDate(goal.getString("startDate")), startDate, "Goal type not matching.");
            softAssert.assertEquals(DateUtility.formattingDate(goal.getString("endDate")), endDate, "Segmentation type not matching.");
            softAssert.assertEquals(goal.getString("goalInterval"), interval, "Segmentation value not matching.");
            softAssert.assertEquals(goal.getBoolean("isFutureGoal"), isFutureGoal, "Goal interval not matching.");
        }
    }

    private BigDecimal calculateYoyPop(BigDecimal currentValue, BigDecimal previousValue) {
        BigDecimal calculatedValue;
        if (previousValue != null && previousValue.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal difference = currentValue.subtract(previousValue);
            calculatedValue = (difference.divide(previousValue, 6, RoundingMode.HALF_UP)).multiply(new BigDecimal(100));
        } else {
            calculatedValue = BigDecimal.ZERO;
        }
        return calculatedValue;
    }

    private BigDecimal calculatePercentageChange(BigDecimal currentValue, BigDecimal previousValue) {
        return currentValue.multiply(new BigDecimal(100)).divide(previousValue, 0, RoundingMode.DOWN);
    }

    private boolean goalActive(Timestamp dateGoalCreated) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate goalDate = dateGoalCreated.toLocalDateTime().toLocalDate();
        return goalDate.isBefore(yesterday);
    }

    @AfterClass()
    public void killDriver() {
        quitBrowser();
    }

}