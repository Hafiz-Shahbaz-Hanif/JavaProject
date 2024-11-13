package com.DC.uitests.adc.analyze.goalSetting;

import com.DC.constants.NetNewConstants;
import com.DC.db.analyze.GoalsHubDbFunctions;
import com.DC.db.hubDbFunctions.HubDbFunctions;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.goalSetting.GoalsHubPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.CsvUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.adc.analyze.goalSetting.GoalsHubRequests;
import com.DC.utilities.apiEngine.models.requests.adc.analyze.goalSetting.AllGoalsHubRequestBody;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;

public class GoalsHubTest extends BaseClass {

    String authToken;
    DCLoginPage loginPage;
    AppHomepage homePage;
    String headers;
    GoalsHubDbFunctions goalsHubDbFunctions;
    Map<String, String> buRetailerPlatform = new HashMap<>();
    GoalsHubPage goalsHubPage;
    HubDbFunctions hubDbFunctions;

    @BeforeMethod()
    public void setUp(ITestContext testContext) throws InterruptedException, SQLException {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        loginPage = new DCLoginPage(driver);
        loginPage.openLoginPage(driver, READ_CONFIG.getDcAppUrl());
        loginPage.loginDcApp(READ_CONFIG.getHubFilaOnlyUserEmail(), READ_CONFIG.getHubFilaOnlyUserPassword());
        homePage = new AppHomepage(driver);
        authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        goalsHubDbFunctions = new GoalsHubDbFunctions();
        goalsHubPage = new GoalsHubPage(driver);
        hubDbFunctions = new HubDbFunctions();

        buRetailerPlatform.put("buId", hubDbFunctions.getBuId("McCormick US"));
        buRetailerPlatform.put("retailerPlatformId", hubDbFunctions.getRetailerId("United States", "Amazon", "amazon.com"));

        headers = "X-BusinessUnitContext=" + buRetailerPlatform.get("buId") + ";" + "X-RetailerPlatformContext=" + buRetailerPlatform.get("retailerPlatformId");
        homePage.openPage("Analyze", "Goals Hub");
        homePage.selectBU("McCormick US");
    }

    @Test(description = "RAR-161-170 - Goals Hub - Goal delete")
    public void Goals_Hub_Delete_Icon_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        goalsHubPage.clickDeleteGoalIcon();

        softAssert.assertTrue(goalsHubPage.isDeleteButtonDisplayed(), "Delete button not displayed");
        softAssert.assertTrue(goalsHubPage.isCancelDeleteButtonDisplayed(), "Cancel button not displayed");
        goalsHubPage.clickCancelDelete();
        softAssert.assertAll();
    }

    @Test(description = "RAR-135 - 809 - Goals Hub - Create Goal Pop Up for Pdp Goals")
    public void Goals_Hub_Create_Goal_Pop_Up_For_Pdp_Goals_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        goalsHubPage.clickCreateGoal();

        List<String> metricsUi = goalsHubPage.getMetrics();
        List<String> intervalsUi = goalsHubPage.getIntervals();
        List<String> segmentationTypesUi = goalsHubPage.getSegmentationTypes();

        goalsHubPage.setMetric("AVAILABILITY");
        goalsHubPage.setInterval("MONTHLY");
        goalsHubPage.setRandomSegmentationType();
        goalsHubPage.setRandomSegmentationValue();
        goalsHubPage.setMinGoalValue("3");
        goalsHubPage.setMaxGoalValue("55");

        softAssert.assertEquals(goalsHubPage.goalMetrics, metricsUi, "** Metric options not matching.");
        softAssert.assertEquals(goalsHubPage.goalIntervals, intervalsUi, "** Interval options not matching.");
        softAssert.assertEquals(goalsHubPage.goalSegmentationTypes, segmentationTypesUi, "** Segmentation type options not matching.");
        softAssert.assertFalse(goalsHubPage.isDistributorViewDisplayed(), "Distributor view displayed for Availability metric");
        softAssert.assertTrue(goalsHubPage.metricSignDisplayed("%"), "% sign not displayed");
        softAssert.assertTrue(goalsHubPage.isSaveButtonEnabled(), "Save button disabled");
        softAssert.assertAll();
    }

    @Test(description = "RAR-135 - 809 - Goals Hub - Create Goal Pop Up for Sales Goals")
    public void Goals_Hub_Create_Goal_Pop_Up_For_Sales_Goals_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        goalsHubPage.clickCreateGoal();

        String metric = goalsHubPage.setRandomSalesMetric();
        String interval = goalsHubPage.setRandomInterval();

        if (interval.equalsIgnoreCase("custom")) {
            goalsHubPage.selectCustomDate();
            goalsHubPage.setGoalTitle("abc");
        }

        goalsHubPage.setRandomDistributorView();
        goalsHubPage.setRandomSegmentationType();
        goalsHubPage.setRandomSegmentationValue();

        if (metric.equalsIgnoreCase("Ordered Units") || metric.equalsIgnoreCase("Shipped Units")) {
            softAssert.assertFalse(goalsHubPage.metricSignDisplayed("$"), "Min goal value not displayed");
            softAssert.assertFalse(goalsHubPage.metricSignDisplayed("%"), "Min goal value not displayed");
            goalsHubPage.setMinGoalValue("55");
        } else {
            softAssert.assertTrue(goalsHubPage.metricSignDisplayed("$"), "$ sign not displayed");
            goalsHubPage.setMinGoalValue("55");
        }

        softAssert.assertTrue(goalsHubPage.isDistributorViewDisplayed(), "Distributor view displayed for Availability metric");
        softAssert.assertTrue(goalsHubPage.isSaveButtonEnabled(), "Save button disabled");
        softAssert.assertAll();
    }

    @Test(description = "RAR-1102 - 1067 - Goals Hub - Goal Create Pop Up")
    public void Goals_Hub_Create_Goal_Pop_Up_Required_Fields_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        goalsHubPage.clickCreateGoal();
        goalsHubPage.setSalesMetric(false, false);
        goalsHubPage.setDateInterval(true, true, true);
        goalsHubPage.setSegmentation(true, true);
        verifyRequiredField(softAssert, "Metric");

        goalsHubPage.setSalesMetric(true, true);
        goalsHubPage.setDateInterval(false, false, false);
        goalsHubPage.setSegmentation(true, true);
        goalsHubPage.setSalesGoalValue("55");
        verifyRequiredField(softAssert, "Interval");

        goalsHubPage.setSalesMetric(true, true);
        goalsHubPage.setDateInterval(true, false, true);
        goalsHubPage.setSegmentation(true, true);
        goalsHubPage.setSalesGoalValue("55");
        verifyRequiredField(softAssert, "Date");

        goalsHubPage.setSalesMetric(true, true);
        goalsHubPage.setDateInterval(true, true, false);
        goalsHubPage.setSegmentation(true, true);
        goalsHubPage.setSalesGoalValue("55");
        verifyRequiredField(softAssert, "Goal Title");

        goalsHubPage.setSalesMetric(true, false);
        goalsHubPage.setDateInterval(true, true, true);
        goalsHubPage.setSegmentation(true, true);
        goalsHubPage.setSalesGoalValue("55");
        verifyRequiredField(softAssert, "Distributor View");

        goalsHubPage.setSalesMetric(true, true);
        goalsHubPage.setDateInterval(true, true, true);
        goalsHubPage.setSegmentation(true, false);
        goalsHubPage.setSalesGoalValue("55");
        verifyRequiredField(softAssert, "Segmentation");

        goalsHubPage.setSalesMetric(true, true);
        goalsHubPage.setDateInterval(true, true, true);
        goalsHubPage.setSegmentation(true, true);
        verifyRequiredField(softAssert, "Goal");
        softAssert.assertAll();
    }

    @Test(description = "RAR-168 - Goals Hub - Goal Edit Pop Up")
    public void Goals_Hub_Edit_Pop_Up_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        goalsHubPage.clickEditGoalIcon();

        softAssert.assertFalse(goalsHubPage.isMetricDropdownEnabled(), "Metric dropdown enabled.");
        softAssert.assertFalse(goalsHubPage.isIntervalDropdownEnabled(), "Interval dropdown enabled.");
        softAssert.assertFalse(goalsHubPage.isSegmentationTypeDropdownEnabled(), "Segmentation type dropdown enabled.");
        softAssert.assertFalse(goalsHubPage.isSegmentationValueDropdownEnabled(), "Segmentation value dropdown enabled.");
        softAssert.assertFalse(goalsHubPage.isTimePeriodFieldEnabled(), "Time period field enabled.");

        if (!goalsHubPage.getMetricSelected().equalsIgnoreCase("Availability")) {
            softAssert.assertFalse(goalsHubPage.isDistributorViewEnabled(), "Distributor view dropdown enabled.");
            softAssert.assertTrue(goalsHubPage.isGoalMinFieldFieldEnabled(), "Min goal value field disabled.");
        } else {
            softAssert.assertTrue(goalsHubPage.isGoalMinFieldFieldEnabled(), "Min goal value field disabled.");
            softAssert.assertTrue(goalsHubPage.isGoalMaxFieldFieldEnabled(), "Max goal value field disabled.");
        }

        softAssert.assertTrue(goalsHubPage.isSaveButtonEnabled(), "Save button disabled.");
        goalsHubPage.clickGoalCreateCancelBtn();
        softAssert.assertAll();
    }

    @Test(dataProvider = "Goals_Hub", dataProviderClass = GoalsHubDataProvider.class, description = "RAR-162-145-142-153-167-166 - Goals Hub - Availability Speedometer Goal Calculations")
    public void Goals_Hub_Goal_Availability_Speedometer_Goal_Calculation_Test(Dictionary<String, String> dataSet) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String period = dataSet.get("period");
        String interval = dataSet.get("interval");
        String metric = "Availability";

        Map<String, String> speedometerColor = new HashMap<>();
        speedometerColor.put("red", "#F46E6E");
        speedometerColor.put("green", "#67C065");
        speedometerColor.put("yellow", "#FFCC48");

        String brandId = GoalsHubRequests.getBrandId(GoalsHubPage.brand, headers, authToken);
        AllGoalsHubRequestBody allGoalsRequestBody = new AllGoalsHubRequestBody(period.split(" ")[0].toUpperCase(), interval.toUpperCase(), List.of("AVAILABILITY_PERCENTAGE_IN_STOCK"), List.of(brandId));
        JSONObject goal = GoalsHubRequests.getRandomPdpGoal(allGoalsRequestBody, headers, authToken);
        BigDecimal goalReached = goal.getBigDecimal("goalReached").stripTrailingZeros();
        BigDecimal minValueApi = goal.getJSONObject("specification").getBigDecimal("minValue").stripTrailingZeros();
        BigDecimal maxValueApi = goal.getJSONObject("specification").getBigDecimal("maxValue").stripTrailingZeros();

        BigDecimal goalReachedPercentageApi = goalsHubPage.formatGoalReachedPercentageForAvailability(goalReached, minValueApi, maxValueApi);
        String goalReachedPercentageApiFormatted = goalReachedPercentageApi.toPlainString() + "%";
        String goalRangeApiFormatted = goalsHubPage.formatAvailabilityGoal(minValueApi, maxValueApi);

        goalsHubPage.displayGoalsBy("Availability", period, interval);
        goalsHubPage.setFilterBrand(GoalsHubPage.brand);
        goalsHubPage.sideBarApply();
        String goalReachedPercentageUi = goalsHubPage.getGoalReachedPercentage(goalRangeApiFormatted);
        String goalRemainingPercentageUi = goalsHubPage.getGoalRemainingPercentage(goalRangeApiFormatted);
        softAssert.assertEquals(goalReachedPercentageUi, goalReachedPercentageApiFormatted, "** Goal reached percentage not matching.");

        String goalRemainingPercentageFormatted = new BigDecimal(100).subtract(goalReachedPercentageApi).stripTrailingZeros().toPlainString() + "% to Goal";

        if (goalReachedPercentageApi.compareTo(new BigDecimal(100)) >= 0) {
            softAssert.assertEquals(goalRemainingPercentageUi, "Goal Met", "Goal remaining not matching.");
            softAssert.assertTrue(loginPage.isElementVisible(By.xpath(goalsHubPage.getGoalMetricColor(metric, goalRangeApiFormatted, speedometerColor.get("green")))), "Goal met, but speedometer color not green");
        } else if (goalReachedPercentageApi.compareTo(new BigDecimal(90)) >= 0 && goalReachedPercentageApi.compareTo(new BigDecimal(100)) < 0) {
            softAssert.assertEquals(goalRemainingPercentageUi, goalRemainingPercentageFormatted, "Goal remaining not matching for goal.");
            softAssert.assertTrue(loginPage.isElementVisible(By.xpath(goalsHubPage.getGoalMetricColor(metric, goalRangeApiFormatted, speedometerColor.get("yellow")))), "Goal over %90, but speedometer color not yellow");
        } else if (goalReachedPercentageApi.compareTo(new BigDecimal(90)) < 0) {
            softAssert.assertEquals(goalRemainingPercentageUi, goalRemainingPercentageFormatted, "Goal remaining not matching for goal.");
            softAssert.assertTrue(loginPage.isElementVisible(By.xpath(goalsHubPage.getGoalMetricColor(metric, goalRangeApiFormatted, speedometerColor.get("red")))), "Goal under %90, but speedometer color not red");
        }
        softAssert.assertAll();
    }

    @Test(dataProvider = "Sales_Goals_Hub", dataProviderClass = GoalsHubDataProvider.class, description = "RAR-133 - Goals Hub - Sales Metrics Goal Calculation")
    public void Goals_Hub_Goal_Sales_Donut_Goal_Calculation_Test(Dictionary<String, String> dataSet) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String period = dataSet.get("period");
        String interval = dataSet.get("interval");
        String salesMetricApi = dataSet.get("salesMetric");
        int sizeOfDonutWhenGoalReached = 171;
        int initialSizeOfDonutBeforeAnyProgress = 18;

        String brandId = GoalsHubRequests.getBrandId(GoalsHubPage.brand, headers, authToken);
        AllGoalsHubRequestBody allGoalsRequestBody = new AllGoalsHubRequestBody(period.split(" ")[0].toUpperCase(), interval.toUpperCase(), List.of(salesMetricApi), List.of(brandId));
        JSONObject goal = GoalsHubRequests.getRandomSalesGoal(allGoalsRequestBody, headers, authToken);
        BigDecimal goalReachedApi = goal.getBigDecimal("goalReached").stripTrailingZeros();
        BigDecimal value = goal.getJSONObject("specification").getBigDecimal("value").stripTrailingZeros();

        BigDecimal goalReachedPercentageApi = goalsHubPage.formatGoalReachedPercentageForSales(goalReachedApi, value);
        String goalReachedPercentageApiFormatted = goalReachedPercentageApi.toPlainString() + "%";
        String goalReachedApiFormatted = goalReachedApi.setScale(0, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();

        String goalRemainingAmountFormatted = null;
        BigDecimal remainingGoal = value.subtract(goalReachedApi);
        remainingGoal = remainingGoal.compareTo(new BigDecimal(0)) < 0 ? new BigDecimal(0) : remainingGoal;
        goalRemainingAmountFormatted = remainingGoal.setScale(0, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + " Remaining";

        String goalApiFormatted = goalsHubPage.formatSalesGoals(value, false);
        if (salesMetricApi.equalsIgnoreCase("ORDERED_UNITS")) {
            salesMetricApi = "Ordered Units";
        } else if (salesMetricApi.equalsIgnoreCase("ORDERED_REVENUE")) {
            salesMetricApi = "Ordered Revenue";
            goalApiFormatted = goalApiFormatted.replace(" ", " $");
            goalReachedApiFormatted = "$" + goalReachedApiFormatted;
            goalRemainingAmountFormatted = "$" + goalRemainingAmountFormatted;
        }

        goalsHubPage.displayGoalsBy(salesMetricApi, period, interval);
        goalsHubPage.setFilterBrand(GoalsHubPage.brand);
        goalsHubPage.sideBarApply();

        String goalReachedPercentageUi = goalsHubPage.getSalesGoalReachedPercentage(goalReachedPercentageApiFormatted);
        String goalUi = goalsHubPage.getSalesGoalSet(goalApiFormatted);
        String goalReachedAmountUi = goalsHubPage.getSalesGoalReachedAmount();
        String goalRemainingAmountUi = goalsHubPage.getSalesGoalRemainingAmount();
        int colorProgress = goalsHubPage.getSalesGoalProgress();

        if (goalReachedPercentageApi.compareTo(new BigDecimal(100)) >= 0) {
            softAssert.assertTrue(colorProgress >= sizeOfDonutWhenGoalReached, "Color progress is not matching.");
        } else {
            softAssert.assertTrue(colorProgress > initialSizeOfDonutBeforeAnyProgress && colorProgress < sizeOfDonutWhenGoalReached, "Color progress is not matching.");
        }

        softAssert.assertEquals(goalReachedPercentageUi, goalReachedPercentageApiFormatted, "** Goal reached percentage not matching.");
        softAssert.assertEquals(goalUi, goalApiFormatted, "** Goal set not matching.");
        softAssert.assertEquals(goalRemainingAmountUi.replaceAll(",", ""), goalRemainingAmountFormatted, "Goal remaining amount not matching.");
        softAssert.assertEquals(goalReachedAmountUi.replaceAll(",", ""), goalReachedApiFormatted, "** Goal reached amount not matching.");
        softAssert.assertAll();
    }

    @Test(dataProvider = "Goals_Hub_YoY_PoP", dataProviderClass = GoalsHubDataProvider.class, description = "RAR-505-526 - Goals Hub - Goals YoY and PoP Calculation")
    public void Goals_Hub_YoY_Pop_Calculation_Test(Dictionary<String, String> dataSet) throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String period = dataSet.get("period");
        String interval = dataSet.get("interval");
        String salesMetricApi = dataSet.get("salesMetric");
        JSONObject goal = null;
        String green = "rgba(103, 192, 101, 1)";
        String red = "rgba(244, 110, 110, 1)";
        BigDecimal minValueApi = null;
        BigDecimal maxValueApi = null;
        BigDecimal value = null;
        String goalApiFormatted = null;

        String brandId = GoalsHubRequests.getBrandId(GoalsHubPage.brand, headers, authToken);
        AllGoalsHubRequestBody allGoalsRequestBody = new AllGoalsHubRequestBody(period.split(" ")[0].toUpperCase(), interval.toUpperCase(), List.of(salesMetricApi), List.of(brandId));

        if (salesMetricApi.startsWith("AVAILABILITY")) {
            salesMetricApi = "Availability";
            goal = GoalsHubRequests.getRandomPdpGoal(allGoalsRequestBody, headers, authToken);
            minValueApi = goal.getJSONObject("specification").getBigDecimal("minValue").stripTrailingZeros();
            maxValueApi = goal.getJSONObject("specification").getBigDecimal("maxValue").stripTrailingZeros();
            goalApiFormatted = goalsHubPage.formatAvailabilityGoal(minValueApi, maxValueApi);
        } else if (salesMetricApi.equalsIgnoreCase("SHIPPED_REVENUE")) {
            salesMetricApi = "Shipped Revenue";
            goal = GoalsHubRequests.getRandomSalesGoal(allGoalsRequestBody, headers, authToken);
            value = goal.getJSONObject("specification").getBigDecimal("value").stripTrailingZeros();
            goalApiFormatted = goalsHubPage.formatSalesGoals(value, true);
        }

        String goalMetricID = goal.getString("goalMetricID");
        Map<String, Object> gl = goalsHubDbFunctions.getGoalCalculation(goalMetricID);
        BigDecimal current = (BigDecimal) gl.get("current_value");
        BigDecimal previous = (BigDecimal) gl.get("pop_value");
        BigDecimal last = (BigDecimal) gl.get("yoy_value");

        String yoyPercentageApi = goalsHubPage.calculateYoyPop(current, last);
        String popPercentageApi = goalsHubPage.calculateYoyPop(current, previous);

        goalsHubPage.displayGoalsBy(salesMetricApi, period, interval);
        goalsHubPage.setFilterBrand(GoalsHubPage.brand);
        goalsHubPage.sideBarApply();

        String yoyPercentageUi = goalsHubPage.getYoyValue(goalApiFormatted);
        String popPercentageUi = goalsHubPage.getPopValue(goalApiFormatted);

        String yoyColor = goalsHubPage.getYoy(goalApiFormatted).getCssValue("color");
        String popColor = goalsHubPage.getPop(goalApiFormatted).getCssValue("color");

        if (yoyPercentageApi.startsWith("-")) {
            softAssert.assertEquals(yoyColor, red, "** YoY color not matching.");
        } else {
            softAssert.assertEquals(yoyColor, green, "** YoY color not matching.");
        }

        if (popPercentageApi.startsWith("-")) {
            softAssert.assertEquals(popColor, red, "** PoP color not matching.");
        } else {
            softAssert.assertEquals(popColor, green, "** PoP color not matching.");
        }

        softAssert.assertEquals(yoyPercentageApi, yoyPercentageUi, "** YoY value not matching.");
        softAssert.assertEquals(popPercentageApi, popPercentageUi, "** PoP value not matching.");
        softAssert.assertAll();
    }

    @Test(description = "RAR-506 - Goals Hub - Export Sample File")
    public void Goals_Export_Sample_File_Test() throws Exception {
        goalsHubPage.clickCreateGoal();
        goalsHubPage.click(goalsHubPage.SAMPLE_BTN);
        String exportFilePath = SharedMethods.isFileDownloaded("csv", "bulk-goal-creation-template", 10, downloadFolder);
        Assert.assertEquals(CsvUtility.getAllColumnNames(exportFilePath), Arrays.asList(GoalsHubPage.createHeaderCsv), "Column names not matching.");
    }

    @Test(description = "RAR-945 - Goals Hub - Tooltips")
    public void Goals_Hub_Tooltips_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String exportTooltip = goalsHubPage.getTooltipText("export");
        softAssert.assertEquals("Download displayed goals to view or edit in bulk", exportTooltip, "Export tooltip not matching.");

        goalsHubPage.clickCreateGoal();
        String sampleTooltip = goalsHubPage.getTooltipText("sample");
        softAssert.assertEquals("Download a sample file to Bulk Create Goals", sampleTooltip, "Sample file tooltip not matching.");

        goalsHubPage.clickGoalCreateCancelBtn();
        String importTooltip = goalsHubPage.getTooltipText("import");
        softAssert.assertEquals("Upload bulk edited goals", importTooltip, "Import tooltip not matching.");
        softAssert.assertAll();
    }

    @Test(description = "RAR-130 - Verify the Goals Hub navigation path at Analyze then Goals Hub")
    public void GH_GoalHubScreenIsDisplayed() {
        Assert.assertEquals(driver.getCurrentUrl(), NetNewConstants.getReportsUrl("analyze", "goals-hub/"), "Goal Hub page URL is not correct");
    }

    @Test(description = "RAR-130-131-132-134-135-138 - Verify the layout of Goals Hub screen")
    public void GH_GoalHubLayout() throws InterruptedException {

        LOGGER.info("Verify All Intervals are displayed");
        Assert.assertTrue(goalsHubPage.isIntervalButtonIsDisplayed(), "Interval Selection is not displayed");
        Assert.assertTrue(goalsHubPage.isYearlyIntervalIsDisplayedAndClickable("Yearly"), "Yearly interval is not displayed and clickable");
        Assert.assertTrue(goalsHubPage.isQuarterlyIntervalIsDisplayedAndClickable("Quarterly"), "Quarterly interval is not displayed and clickable");
        Assert.assertTrue(goalsHubPage.isMonthlyIntervalIsDisplayedAndClickable("Monthly"), "Monthly interval is not displayed and clickable");
        Assert.assertTrue(goalsHubPage.isCustomIntervalIsDisplayedAndClickable("Custom"), "Custom interval is not displayed and clickable");

        LOGGER.info("Verify Goals Selections values are displayed ");
        Assert.assertTrue(goalsHubPage.isGoalsButtonIsDisplayed(), "Goals Selection is not displayed");
        Assert.assertTrue(goalsHubPage.isPresentGoalIsDisplayedAndClickable("Present Goals"), "Present Goals is not displayed and Clickable");
        Assert.assertTrue(goalsHubPage.isPastGoalIsDisplayedAndClickable("Past Goals"), "Past Goals is not displayed and Clickable");
        Assert.assertTrue(goalsHubPage.isFuturGoalIsDisplayedAndClickable("Future Goals"), "Future Goals is not displayed and Clickable");
        Assert.assertTrue(goalsHubPage.isAllGoalIsDisplayedAndClickable("All Goals"), "All Goals is not displayed and Clickable");

        Assert.assertTrue(goalsHubPage.isCreateGoalButtonIsDisplayed(), "Create Goal button is not displayed");
        Assert.assertTrue(goalsHubPage.isDownloadsButtonIsDisplayed(), "Downloads button is not displayed");
        Assert.assertTrue(goalsHubPage.isGoalHubChartIsDisplayed(), "Goal Hub chart is not displayed");


        LOGGER.info("Verify All filters with their dropdown values are Displayed ");
        Assert.assertTrue(goalsHubPage.verifyPresenceOfAllLeftSideFiltersGoalsHub("metrics", "category", "subcategory", "segment", "brand"), "Not all the filters are displayed.");
        goalsHubPage.isCategoryOptionsAreDisplayed();
        Assert.assertTrue(goalsHubPage.isAvailabilityValueIsDisplayedInMetricsDropdown("Availability"), "Availability Option is not displayed");
    }

    @Test(description = "RAR-130-131-132-134-135-138 - Verify the Distributor View Filter is Displayed")
    public void GH_DistributorViewFilter() throws InterruptedException {
        LOGGER.info("Verify Distributor View Filter is displayed ");
        Assert.assertTrue(goalsHubPage.verifyDistributorViewLeftSideFiltersGoalsHub("distributor-view"), "Distributor View Filter is not displayed.");
        goalsHubPage.distributorViewDropdownValues();

    }

    @Test(description = "RAR-145 - Verify the All Goals on Goals Hub screen")
    public void GH_AllGoalsIsDisplayed() throws InterruptedException {
        String metricsToSelect = "Availability";

        LOGGER.info("Select All GOAl Option from Goal Selector Dropdown");
        goalsHubPage.selectAllGoalOption("All Goals");
        goalsHubPage.selectMetrics(metricsToSelect);
        goalsHubPage.clickApplyButton();

        LOGGER.info("Verify that All With selected filter is displayed");
        Assert.assertTrue(goalsHubPage.allFilteredGoals(), "All Goal filters are displayed correctly");

    }

    @Test(description = "RAR-130,506,507 - Verify that Sample button ,PNG and PDF export is Displayed on the Goals Hub screen")
    public void GH_ExportOptionsAreDisplayed() throws InterruptedException {
        LOGGER.info("Click on the Create Goal");
        Assert.assertTrue(goalsHubPage.isSamplePDFDisplayed(), "Sample button option is not Displayed");
        Assert.assertTrue(goalsHubPage.isExportDisplayedOnCreateGoal(), "Export option is not displayed");
        goalsHubPage.clickCloseButton();

        LOGGER.info("Click on Export Button");
        Assert.assertTrue(goalsHubPage.isPngOptionIsDisplayed("Download PNG Image"), "Download PNG Option is not Displayed");
        Assert.assertTrue(goalsHubPage.isPdfOptionIsDisplayed("Download PDF Document"), "Download PDF Document Option is not Displayed");

    }

    @Test(description = "RAR-192 - Verify that Cancel and Apply buttons are below the filters")
    public void GH_VerifyClearAndApplyAreDisplayed() throws InterruptedException {
        LOGGER.info("Verify Clear and Apply button are Displayed");
        Assert.assertTrue(goalsHubPage.verifyCancelButton("Cancel"), "Cancel Button is not Displayed");
        Assert.assertTrue(goalsHubPage.verifyApplyButton("Apply"), "Apply button is not displayed");
    }

    @Test(description = "RAR-510,511 -  Add Change Logs tab to the Change logs pop up")
    public void GH_VerifyChangeLogUI() throws InterruptedException {
        LOGGER.info("Verify Change Logs button is Displayed");
        Assert.assertTrue(goalsHubPage.verifyChangeLogButton(), "Change Logs Button is not Displayed");

        LOGGER.info("Verify Change Logs Grid Column Values");
        Assert.assertTrue(goalsHubPage.verifyChangeLogFuntionality("Change Logs"), "Change log header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyBusinessUnitHeader("Business Unit"), "Business Unit header is not displayed");
        Assert.assertTrue(goalsHubPage.verifySegmentationTypeHeader("Segmentation Type"), "Segmentation Type header is not displayed");
        Assert.assertTrue(goalsHubPage.verifySegmentationValueHeader("Segmentation Value"), "Segmentation Value header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyCurrentValueHeader("Current Value"), "Current Value header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyDateRangeHeader("Date Range"), "Date Range header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyLastModifiedHeader("Last Modified"), "Last Modified header is not displayed");

        LOGGER.info("Verify Change logs popUp X button functionality");
        Assert.assertTrue(goalsHubPage.verifyCloseButtonFunctionality("close"), "Change log pop up is not closed");

    }

    @Test(description = "RAR-510,511,147,141,161,163,168,170 -  Add Change Logs tab to the Change logs pop up")
    public void GH_VerifyChangeLogFunctionality() throws Exception {
        String metricsToSelect = "Shipped COGS";
        String brand = "adolphs";
        String value = String.valueOf(100);
        LOGGER.info("Select the Metrics");
        goalsHubPage.selectMetrics(metricsToSelect);

        LOGGER.info("Select the Brand");
        goalsHubPage.selectBrand(brand);
        goalsHubPage.clickApplyButton();

        LOGGER.info("Create a GOAL");
        goalsHubPage.createGOAL(value);

        LOGGER.info("Open Change Logs Tab");
        goalsHubPage.verfiyLastModifiedDate();

        LOGGER.info("Edit newly Created Goal");
        goalsHubPage.editNewlyCreatedGoal();

        LOGGER.info("Click on Export Button");
        goalsHubPage.clickDownloadCSV("Download CSV");
        LOGGER.info("Verifying new columns are added to excel file");
        String downloadPath = SharedMethods.isFileDownloaded(".csv", "GOALS_HUB_EXPORT", 60, downloadFolder);
        Assert.assertNotNull(downloadPath, "File is not downloaded");

        LOGGER.info("Expand the Carrot");
        goalsHubPage.verifyCarrotHeaderValues();

        LOGGER.info("Delete the Goal");
        goalsHubPage.deleteGoal();

    }

    @Test(description = "RAR-512 -  Add Version History tab to the Change Logs popup")
    public void GH_VerifyVersionHistoryUI() throws InterruptedException {

        LOGGER.info("Verify Version History Tab Displayed");
        Assert.assertTrue(goalsHubPage.versionHistoryTab(), "Version History tab is not Displayed");

        LOGGER.info("Verify Version History Tab");
        Assert.assertTrue(goalsHubPage.verifyFileNameHeader("File Name"), "File Name header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyUploadedByHeader("Uploaded By"), "Uploaded By header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyUploadedOnHeader("Uploaded On"), "Uploaded On header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyUploadedFileHeader("Uploaded File"), "Uploaded File header is not displayed");

        LOGGER.info("Verify Change logs popUp X button functionality");
        Assert.assertTrue(goalsHubPage.verifyCloseButtonFunctionality("close"), "Change log pop up is not closed");
    }

    @Test(description = "RAR-512 -  Add Version History tab to the Change Logs popup")
    public void GH_VersionHistoryFunctionality() throws Exception {
        String metricsToSelect = "Shipped COGS";
        String brand = "stubbs";
        LOGGER.info("Select the Metrics");
        goalsHubPage.selectMetrics(metricsToSelect);

        LOGGER.info("Select the Brand");
        goalsHubPage.selectBrand(brand);
        goalsHubPage.clickApplyButton();

        LOGGER.info("Click on Export Button");
        goalsHubPage.clickDownloadCSV("Download CSV");
        LOGGER.info("Verifying new columns are added to excel file");
        String downloadPath = SharedMethods.isFileDownloaded(".csv", "GOALS_HUB_EXPORT", 60, downloadFolder);
        Assert.assertNotNull(downloadPath, "File is not downloaded");
        Assert.assertTrue(goalsHubPage.verifyCSVHasCorrectColumns(downloadPath), "CSV file does not have correct columns");

        LOGGER.info("Click on Bulk Upload Goals");
        goalsHubPage.fileUpload(downloadPath);

        LOGGER.info("Verify Version History Tab");
        Assert.assertTrue(goalsHubPage.versionHistoryTab(), "Version History tab is not Displayed");
        Assert.assertTrue(goalsHubPage.verifyFileNameHeader("File Name"), "File Name header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyUploadedByHeader("Uploaded By"), "Uploaded By header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyUploadedOnHeader("Uploaded On"), "Uploaded On header is not displayed");
        Assert.assertTrue(goalsHubPage.verifyUploadedFileHeader("Uploaded File"), "Uploaded File header is not displayed");

        LOGGER.info("Verify that uploaded is displayed in the Version History");
        String getUploadedFilePath = goalsHubPage.getFileNameText();
        Assert.assertTrue(getUploadedFilePath.contains("GOALS_HUB_EXPORT"), "File is not uploaded");

    }

    private void verifyRequiredField(SoftAssert softAssert, String fieldMissing) throws InterruptedException {
        softAssert.assertFalse(goalsHubPage.isSaveButtonEnabled(), fieldMissing + " value not set, but save button enabled");
        goalsHubPage.clickGoalCreateCancelBtn();
        goalsHubPage.clickCreateGoal();
    }

    @AfterMethod()
    public void killDriver() {
        quitBrowser();
    }
}