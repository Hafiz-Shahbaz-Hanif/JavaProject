package com.DC.uitests.adc.execute.mediaManagement;

import com.DC.constants.NetNewConstants;
import com.DC.db.execute.ATCQueries;
import com.DC.db.execute.SharedDBMethods;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.paidMediaReporting.DownloadManager;
import com.DC.pageobjects.adc.execute.mediaManagement.AirTrafficControlPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.RedShiftUtility;
import com.DC.utilities.SharedMethods;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Map;

public class AirTrafficControlTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private AirTrafficControlPage airTrafficControlPage;
    private AppHomepage appHomepage;
    List<String> reportMetricsOptions = List.of("Clicks", "Conversions", "CPA", "CPC", "CTR", "CVR", "Impressions", "ROAS", "Sales", "SPC", "Spend");
    List<String> labelOptions = List.of("Perpetua", "Ensemble", "FIDO", "RBB", "No Automation");
    private static final String tealColor = "rgba(125, 196, 183, 1)";

    @BeforeMethod
    public void setupTestMethodForNetNew(final ITestContext testContext, ITestResult tr) throws InterruptedException {
        testMethodName.set(tr.getMethod().getMethodName());
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName + " ***************");
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Execute");
        appHomepage.clickLink("Air Traffic Control");
        appHomepage.selectBU("Performance Health East");

        airTrafficControlPage = new AirTrafficControlPage(driver);
    }

    @AfterMethod()
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify Display of Air Traffic Control Screen")
    public void MAU_ATC_ATCScreenIsDisplayed() {
        Assert.assertTrue(airTrafficControlPage.isATCScreenDisplayed(), "Air Traffic Control Screen is not displayed");
        String currentUrl = airTrafficControlPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("air-traffic-control"), "Page url does not contain 'Air Traffic Control'");
    }

    @Test(description = "C244597/1 - Verify the export options default/Daily/Weekly/Monthly")
    public void MAU_ATC_VerifyExportOptions() throws InterruptedException {
        Assert.assertTrue(airTrafficControlPage.isExportButtonDisplayed(), "Export button is not displayed");
        airTrafficControlPage.clickExportButton();
        List<String> expectedExportOptions = List.of("Default - Sum Totals/Bids", "Daily", "Weekly", "Monthly");
        Assert.assertTrue(airTrafficControlPage.getExportOptions().containsAll(expectedExportOptions), "Export options are not displayed");
    }

    @Test(description = "C244597/2 - Verify user can export every option", dataProvider = "exportOptions")
    public void MAU_ATC_VerifyExportOptionsExport(String downloadOption) {
        try {
            airTrafficControlPage.clickExportButton();
            airTrafficControlPage.clickExportButtonOption(downloadOption);
            var downloadManager = new DownloadManager(driver);
            Assert.assertTrue(airTrafficControlPage.isExportSuccessMessageDisplayed(), "Success message is not displayed");
            String timeOfDownload = DateUtility.getCurrentDateTime("M/d/yyyy, h:mm");
            LOGGER.info("Downloading " + downloadOption + " at " + timeOfDownload);
            airTrafficControlPage.clickDownloadManagerLink();
            LOGGER.info("Exported " + downloadOption + " successfully");
            Assert.assertEquals(downloadManager.moveToDownloadManagerTab(), "Download manager", "Download Manager tab is not displayed");
            downloadManager.clickOnCorrectDownloadOption(timeOfDownload, 40);
            LOGGER.info("Clicked on " + downloadOption + " download in Download Manager");
            String downloadedFilePath = SharedMethods.isFileDownloaded(".csv", "air_traffic_control", 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            Assert.assertTrue(downloadedFilePath.endsWith(".csv"), "File does not have .csv extension");
            LOGGER.info("File is downloaded in .csv format");
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(description = "C244597/3 - Verify that user cannot export when date range is more that allowed for each option", dataProvider = "exportOptions")
    public void MAU_ATC_VerifyExportOptionsDateLimit(String downloadOption) {
        try {
            LOGGER.info("Verifying date limit for " + downloadOption);
            LOGGER.info("Selecting date range in date picker");
            String dateRangeToSelect = "Last 13 Weeks";
            String maxDaysForErrorMessage = getMaxDaysForOption(downloadOption);
            airTrafficControlPage.dateAndIntervalPickerPage.selectDateRange(dateRangeToSelect);

            airTrafficControlPage.clickExportButton();
            airTrafficControlPage.clickExportButtonOption(downloadOption);
            Assert.assertTrue(airTrafficControlPage.isExportErrorMessageDisplayed(maxDaysForErrorMessage), "Error message is not displayed");
            LOGGER.info("Success! Download cannot be performed for " + downloadOption + " as selected date range: " + dateRangeToSelect + " is more than " + maxDaysForErrorMessage);
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(description = "C244599 - C244603, C244613, C244615 - Verify new columns are added to excel file", dataProvider = "exportOptions")
    public void MAU_ATC_VerifyNewColumnsAddedToExcelFile(String downloadOption) {
        try {
            airTrafficControlPage.clickExportButton();
            airTrafficControlPage.clickExportButtonOption(downloadOption);
            var downloadManager = new DownloadManager(driver);
            String timeOfDownload = DateUtility.getCurrentDateTime("M/d/yyyy, h:mm");
            LOGGER.info("Downloading " + downloadOption + " at " + timeOfDownload);
            airTrafficControlPage.clickDownloadManagerLink();
            LOGGER.info("Exported " + downloadOption + " successfully");
            Assert.assertEquals(downloadManager.moveToDownloadManagerTab(), "Download manager", "Download Manager tab is not displayed");
            downloadManager.clickOnCorrectDownloadOption(timeOfDownload, 40);

            LOGGER.info("Verifying new columns are added to excel file");
            String downloadPath = SharedMethods.isFileDownloaded(".csv", "air_traffic_control", 60, downloadFolder);
            Assert.assertNotNull(downloadPath, "File is not downloaded");
            Assert.assertTrue(airTrafficControlPage.verifyCSVHasCorrectColumns(downloadPath), "CSV file does not have correct columns");

            LOGGER.info("Verifying exported file has data per ASIN");
            Assert.assertTrue(airTrafficControlPage.verifyCSVHasDifferentValuesInColumn(downloadPath, "ASIN"));
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(description = "C244605/1 - Verify that Total automated spend and sales match with existing aggregated spend and sales figures")
    public void MAU_ATC_VerifyAutomatedSpendAndSales() throws InterruptedException {
        String downloadOption = "Daily";
        airTrafficControlPage.dateAndIntervalPickerPage.selectDateRange("Last 14 Days");
        LOGGER.info("Selected date range: Last 14 Days. Selecting CSQ to check in the table");
        airTrafficControlPage.selectCSQuery("Theraband");
        LOGGER.info("Verifying sum of spend and sales in the table matches with the downloaded file. Checking for column: Spend");
        double sumOfColumnSpendInUI = airTrafficControlPage.getTotalSumFromUI("spend");
        double sumOfColumnSalesInUI = airTrafficControlPage.getTotalSumFromUI("sales");
        try {
            airTrafficControlPage.clickExportButton();
            airTrafficControlPage.clickExportButtonOption(downloadOption);
            var downloadManager = new DownloadManager(driver);
            String timeOfDownload = DateUtility.getCurrentDateTime("M/d/yyyy, h:mm");
            airTrafficControlPage.clickDownloadManagerLink();
            downloadManager.moveToDownloadManagerTab();
            downloadManager.clickOnCorrectDownloadOption(timeOfDownload, 40);
            LOGGER.info("Clicked on " + downloadOption + " download in Download Manager");
            String downloadedFilePath = SharedMethods.isFileDownloaded(".csv", "air_traffic_control", 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            LOGGER.info("Verifying sum of Spend matches with sum of Spend in CSV file");
            Assert.assertTrue(airTrafficControlPage.isSumFromUIMatchingSumFromCSV(downloadedFilePath, "spend", sumOfColumnSpendInUI), "Sum of Spend from UI does not match with sum of Spend from CSV file");
            LOGGER.info("Success! Sum of Spend from UI matches with sum of sum in CSV file. Checking for column: Sales");
            Assert.assertTrue(airTrafficControlPage.isSumFromUIMatchingSumFromCSV(downloadedFilePath, "sales", sumOfColumnSalesInUI), "Sum of Sales from UI does not match with sum of Sales from CSV file");
            LOGGER.info("Success! Sum of Sales from UI matches with sum in CSV file");
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(description = "C244605/2 - Verify that automated spend and sales match with existing aggregated spend and sales figures for CSQ")
    public void MAU_ATC_VerifyAutomatedSpendAndSalesForEachCSQ() throws Exception {
        String downloadOption = "Default - Sum Totals/Bids";
        int rowNumber = SharedMethods.getRandomNumber(5);
        List<String> columnsToCheck = List.of("Ensemble Spend", "Perpetua Spend", "FIDO Spend", "Rule-Based Bidding Spend", "Non-Automated Spend");
        airTrafficControlPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Sept", 1, 14);
        LOGGER.info("Selected date range. Selecting CSQ to check in the table");
        airTrafficControlPage.selectCSQuery("exercise band");
        airTrafficControlPage.clickExportButton();
        airTrafficControlPage.clickExportButtonOption(downloadOption);
        var downloadManager = new DownloadManager(driver);
        String timeOfDownload = DateUtility.getCurrentDateTime("M/d/yyyy, h:mm");
        airTrafficControlPage.clickDownloadManagerLink();
        downloadManager.moveToDownloadManagerTab();
        downloadManager.clickOnCorrectDownloadOption(timeOfDownload, 50);
        LOGGER.info("Clicked on " + downloadOption + " download in Download Manager");
        String downloadedFilePath = SharedMethods.isFileDownloaded(".csv", "air_traffic_control", 60, downloadFolder);
        Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
        LOGGER.info("Verifying aggregated and automation types sum in CSV file");
        Assert.assertTrue(airTrafficControlPage.isAutomationTypeSumMatchesAggregated(downloadedFilePath, "Spend", rowNumber, columnsToCheck), "Automation types sum does not match with aggregated sum");
        LOGGER.info("Success! Automation types sum matches with aggregated sum for Spend category. Checking for Sales category");
        columnsToCheck = List.of("Ensemble Sales", "Perpetua Sales", "FIDO Sales", "Rule-Based Bidding Sales", "Non-Automated Sales");
        Assert.assertTrue(airTrafficControlPage.isAutomationTypeSumMatchesAggregated(downloadedFilePath, "Sales", rowNumber, columnsToCheck), "Automation types sum does not match with aggregated sum");
        LOGGER.info("Success! Automation types sum matches with aggregated sum for Sales category");
    }

    @Test(description = "C245355 - If no data is available in the chart, message should say “No Data to Display”")
    public void MAU_ATC_VerifyNoDataMessage() {
        LOGGER.info("Deselecting all labels in the legend");
        airTrafficControlPage.clickLegendLabel(labelOptions, false);
        Assert.assertTrue(airTrafficControlPage.isNoDataDisplayed(), "No Data message is not displayed");
        LOGGER.info("No Data message is displayed when no data is available in the chart");
    }

    @Test(description = "C245356 - Verify that a single-select selector is present at the top left of the chart and user can select any option")
    public void MAU_ATC_VerifyReportMetricsSelection() throws InterruptedException {
        Assert.assertTrue(airTrafficControlPage.isReportMetricsSelectionDisplayed(), "Report Metrics selection is not displayed");

        String reportMetricToSelect = "CPA";
        airTrafficControlPage.selectReportMetric(reportMetricToSelect);
        Assert.assertEquals(airTrafficControlPage.getSelectedReportMetricName(), reportMetricToSelect, "Report Metric is not selected");

        airTrafficControlPage.refreshPage();
        reportMetricToSelect = "Impressions";
        airTrafficControlPage.selectReportMetric(reportMetricToSelect);
        Assert.assertEquals(airTrafficControlPage.getSelectedReportMetricName(), reportMetricToSelect, "Report Metric is not selected");
        LOGGER.info("Report Metric selection is displayed and user can select options");
    }

    @Test(description = "C245357, C245358 - Verify that Spend is selected by default and Metrics in the single selector ordered Alphabetically")
    public void MAU_ATC_VerifyReportMetricsOptions() throws InterruptedException {
        Assert.assertTrue(airTrafficControlPage.isSpendSelectedByDefault(), "Spend is not selected by default");
        Assert.assertEquals(airTrafficControlPage.getReportMetricsOptions(), reportMetricsOptions, "Not all Report Metrics options are present or not in alphabetical order");
        LOGGER.info("Report Metrics options are displayed and in alphabetical order");
    }

    @Test(description = "C245363 - Verify Interval selector is displayed, has daily/weekly/monthly options and Daily is selected by default")
    public void MAU_ATC_VerifyIntervalSelector() throws InterruptedException {
        Assert.assertTrue(airTrafficControlPage.dateAndIntervalPickerPage.isIntervalSelectionDisplayed(), "Interval selector is not displayed");
        Assert.assertEquals(airTrafficControlPage.dateAndIntervalPickerPage.getDefaultIntervalSelection(), "Daily", "Daily interval is not selected by default");
        List<String> expectedIntervalOptions = List.of("Daily", "Weekly", "Monthly");
        Assert.assertEquals(airTrafficControlPage.dateAndIntervalPickerPage.getIntervalDropdownOptions(), expectedIntervalOptions, "Not all Interval options are present");
        LOGGER.info("Interval selector is displayed, has daily/weekly/monthly options and Daily is selected by default");
    }

    @Test(description = "C245379 - Verify that user can download file in all formats", dataProvider = "downloadOptions", dataProviderClass = SharedMethods.class)
    public void MAU_ATC_TestFileDownload(String downloadOption, String expectedExtension) {
        try {
            airTrafficControlPage.clickATCChartExportIcon();
            airTrafficControlPage.clickATCChartExportOption(downloadOption);
            String[] namesOfDownloadedFiles = {"chart", "Air Traffic Control"};
            String downloadedFilePath = SharedMethods.checkDownloadsWithDifferentNames(namesOfDownloadedFiles, expectedExtension, 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");
            LOGGER.info("File is downloaded in " + expectedExtension + " format");
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(description = "C245375 - Verify that If Daily interval is selected then verify the values on the line chart for a day to match database")
    public void MAU_ATC_VerifyDailyInterval() throws InterruptedException, SQLException {
        String metricToSelect = "Impressions";
        airTrafficControlPage.selectReportMetric(metricToSelect);
        airTrafficControlPage.dateAndIntervalPickerPage.selectInterval("Daily");
        airTrafficControlPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Nov", 1, 4);
        List<String> datesAndValuesFromLineChart = airTrafficControlPage.getDaysAndValuesFromFlyout();
        Map<String, Map<String, String>> datesAndValuesFromDB = getDatesAndValuesFromDB(113, metricToSelect, "'2023-11-01'", "'2023-11-04'");
        Assert.assertTrue(airTrafficControlPage.doValuesFromFlyoutMatchValuesFromDB(datesAndValuesFromLineChart, datesAndValuesFromDB), "Values from flyout do not match values from DB. " +
                "Expected values from DB: " + datesAndValuesFromDB + ". Actual values from UI: " + datesAndValuesFromLineChart);
    }

    @Test(description = "C245376 - Verify that If Weekly interval is selected then verify the values on the line chart for a week to match database")
    public void MAU_ATC_VerifyWeeklyInterval() throws InterruptedException, ParseException, SQLException {
        String metricToSelect = "Clicks";
        airTrafficControlPage.selectReportMetric(metricToSelect);
        airTrafficControlPage.dateAndIntervalPickerPage.selectInterval("Weekly");
        airTrafficControlPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Oct", 6, 9);
        List<String> expectedDatesInDateRange = DateUtility.getEverySundayInDateRange(airTrafficControlPage.dateAndIntervalPickerPage.getSelectedDate());
        List<String> sumsOfAllAutomations = airTrafficControlPage.getDaysAndValuesFromFlyout();
        Map<String, Map<String, String>> sumsForSundays = new HashMap<>();
        for (String sundayDate : expectedDatesInDateRange) {
            String saturdayDate = DateUtility.getFollowingDayFromDate(DateUtility.convertDateToInt(sundayDate), 6);
            Map<String, Map<String, String>> datesAndValuesFromDB = getDatesAndValuesFromDB(113, metricToSelect, "'" + sundayDate + "'", "'" + saturdayDate + "'");
            Map<String, Double> formattedSum = airTrafficControlPage.getSumOfValuesFromFlyout(datesAndValuesFromDB);
            Map<String, String> sumsForDate = new HashMap<>();
            for (String column : formattedSum.keySet()) {
                airTrafficControlPage.updateSumsForDate(sumsForDate, column, formattedSum.get(column));
            }
            sumsForSundays.put(sundayDate, sumsForDate);
        }
        Assert.assertTrue(airTrafficControlPage.doValuesFromFlyoutMatchValuesFromDB(sumsOfAllAutomations, sumsForSundays), "Values from flyout do not match values from DB. " +
                "Expected values from DB: " + sumsForSundays + ". Actual values from UI: " + sumsOfAllAutomations);
    }

    @Test(description = "C245377 - Verify that If Monthly interval is selected then verify the values on the line chart for a month to match database")
    public void MAU_ATC_VerifyMonthlyInterval() throws InterruptedException, SQLException {
        String metricToSelect = "Spend";
        airTrafficControlPage.selectReportMetric(metricToSelect);
        airTrafficControlPage.dateAndIntervalPickerPage.selectInterval("Monthly");
        airTrafficControlPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Oct", 23, 25);
        List<String> expectedDatesInDateRange = DateUtility.getFirstDayOfEveryMonthInDateRange(airTrafficControlPage.dateAndIntervalPickerPage.getSelectedDate());
        List<String> datesAndValuesFromLineChart = airTrafficControlPage.getDaysAndValuesFromFlyout();
        Map<String, Map<String, String>> sumsFromDb = new HashMap<>();
        for (String firstDayOfMonth : expectedDatesInDateRange) {
            String lastDayOfMonth = DateUtility.getLastDayOfAnyMonth(firstDayOfMonth);
            Map<String, Map<String, String>> datesAndValuesFromDB = getDatesAndValuesFromDB(113, metricToSelect, "'" + firstDayOfMonth + "'", "'" + lastDayOfMonth + "'");
            Map<String, Double> formattedSum = airTrafficControlPage.getSumOfValuesFromFlyout(datesAndValuesFromDB);
            Map<String, String> sumsForDate = new HashMap<>();
            for (String column : formattedSum.keySet()) {
                airTrafficControlPage.updateSumsForDate(sumsForDate, column, formattedSum.get(column));
            }
            sumsFromDb.put(firstDayOfMonth, sumsForDate);
        }
        Assert.assertTrue(airTrafficControlPage.doValuesFromFlyoutMatchValuesFromDB(datesAndValuesFromLineChart, sumsFromDb), "Values from flyout do not match values from DB. " +
                "Expected values from DB: " + sumsFromDb + ". Actual values from UI: " + datesAndValuesFromLineChart);
    }

    @Test(description = "C245364 - Verify Callouts checkbox is displayed, selected by default and Callouts values are displayed/hidden when Callouts checkbox is selected/unselected")
    public void MAU_ATC_VerifyCalloutsCheckbox() throws InterruptedException, SQLException {
        Assert.assertTrue(airTrafficControlPage.isCalloutsDisplayed(), "Callouts checkbox is not displayed");
        Assert.assertTrue(airTrafficControlPage.isCalloutsCheckboxSelected(), "Callouts checkbox is not selected by default");
        LOGGER.info("Callouts checkbox is displayed and selected by default. Verifying Callouts values are displayed");
        Assert.assertTrue(airTrafficControlPage.isCalloutsValuesDisplayed(), "Callouts values are not displayed");
        LOGGER.info("Callouts values are displayed when Callouts checkbox is selected. Verifying correct Callouts values are displayed");
        String metric = airTrafficControlPage.getSelectedReportMetricName();
        airTrafficControlPage.dateAndIntervalPickerPage.selectInterval("Daily");
        airTrafficControlPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Nov", 21, 24);

        List<String> datesAndValuesFromLineChart = airTrafficControlPage.getDaysAndValuesFromFlyout();
        Map<String, Map<String, String>> datesAndValuesFromDB = getDatesAndValuesFromDB(113, metric, "'2023-11-21'", "'2023-11-24'");
        Assert.assertTrue(airTrafficControlPage.isValuesFromFlyoutMatchValuesFromDB(datesAndValuesFromLineChart, datesAndValuesFromDB), "Values from flyout do not match values from DB. " +
                "Expected values from DB: " + datesAndValuesFromDB + ". Actual values from UI: " + datesAndValuesFromLineChart);
        LOGGER.info("Callouts values from UI match with Callouts values from DB");

        LOGGER.info("Verifying Unchecked Callouts checkbox functionality");
        if (airTrafficControlPage.isCalloutsCheckboxSelected()) {
            airTrafficControlPage.clickCalloutsCheckbox();
            LOGGER.info("Callouts checkbox is unchecked");
            Assert.assertFalse(airTrafficControlPage.isCalloutsValuesDisplayed(), "Callouts are displayed when Callouts checkbox is unchecked");
        } else {
            LOGGER.info("Callouts checkbox is already unchecked");
            Assert.assertFalse(airTrafficControlPage.isCalloutsValuesDisplayed(), "Callouts are displayed when Callouts checkbox is unchecked");
        }
        LOGGER.info("Callouts are not displayed when Callouts checkbox is unchecked");
    }

    @Test(description = "C245360 - All applicable labels in the legend are shown by default")
    public void MAU_ATC_VerifyLegendLabelsByDefault() throws InterruptedException {
        airTrafficControlPage.dateAndIntervalPickerPage.selectDateRange("Last 14 Days");
        List<String> expectedLegendLabels = List.of("Ensemble", "FIDO", "Perpetua", "RBB", "No Automation");
        Assert.assertTrue(airTrafficControlPage.getLegendLabels().containsAll(expectedLegendLabels), "Legend labels are not displayed");
        LOGGER.info("All labels in the legend are shown by default");
        Assert.assertEquals(airTrafficControlPage.getLinesInChart().size(), expectedLegendLabels.size(), "Lines in the chart are not the same as labels in the legend");
        LOGGER.info("Number of lines in the chart are the same as labels in the legend. Refreshing the page");
        airTrafficControlPage.refreshPage();
        Assert.assertEquals(airTrafficControlPage.getLinesInChart().size(), expectedLegendLabels.size(), "Lines in the chart are not the same as labels in the legend");
        LOGGER.info("Number of lines in the chart are the same as labels in the legend after refreshing the page");
    }

    @Test(description = "C245359 - Verify the legend with automation type clickable")
    public void MAU_ATC_VerifyLegendLabelsClickable() throws SQLException {
        LOGGER.info("Deselecting some labels in the legend");
        List<String> labelsToDeselect = List.of("Ensemble", "FIDO", "Perpetua");
        airTrafficControlPage.clickLegendLabel(labelsToDeselect, false);
        Assert.assertEquals(airTrafficControlPage.getLinesInChart().size(), airTrafficControlPage.getLegendLabels().size(), "Lines in the chart are not the same as labels in the legend");
        String selectedDate = airTrafficControlPage.dateAndIntervalPickerPage.getSelectedDate();
        String startDate = DateUtility.formattingDateStartWithYear(selectedDate.substring(0, 10));
        String endDate = DateUtility.formattingDateStartWithYear(selectedDate.substring(13));
        String metricToSelect = airTrafficControlPage.getSelectedReportMetricName().toLowerCase();
        List<String> datesAndValuesFromLineChart = airTrafficControlPage.getDaysAndValuesFromFlyout();
        Map<String, Map<String, String>> datesAndValuesFromDB = getDatesAndValuesFromDBForSpecificLabels(113, metricToSelect, "'" + startDate + "'", "'" + endDate + "'", airTrafficControlPage.getLegendLabels());
        Assert.assertTrue(airTrafficControlPage.doValuesFromFlyoutMatchValuesFromDB(datesAndValuesFromLineChart, datesAndValuesFromDB), "Values from flyout do not match values from DB. " +
                "Expected values from DB: " + datesAndValuesFromDB + ". Actual values from UI: " + datesAndValuesFromLineChart);

        LOGGER.info("Selecting some labels in the legend");
        List<String> labelsToSelect = List.of("Ensemble", "FIDO");
        airTrafficControlPage.clickLegendLabel(labelsToSelect, true);
        Assert.assertEquals(airTrafficControlPage.getLinesInChart().size(), airTrafficControlPage.getLegendLabels().size(), "Lines in the chart are not the same as labels in the legend");
        datesAndValuesFromLineChart = airTrafficControlPage.getDaysAndValuesFromFlyout();
        datesAndValuesFromDB = getDatesAndValuesFromDBForSpecificLabels(113, metricToSelect, "'" + startDate + "'", "'" + endDate + "'", airTrafficControlPage.getLegendLabels());
        Assert.assertTrue(airTrafficControlPage.isValuesFromFlyoutMatchValuesFromDB(datesAndValuesFromLineChart, datesAndValuesFromDB), "Values from flyout do not match values from DB. " +
                "Expected values from DB: " + datesAndValuesFromDB + ". Actual values from UI: " + datesAndValuesFromLineChart);
    }

    @Test(description = "C244847,48 - Update ATC Automate This with FIDO Message and enable it. Verify disabling FIDO in ATC is working")
    public void MAU_ATC_VerifyFIDOAutomation() throws InterruptedException, SQLException {
        int randomIndex = new Random().nextInt(NetNewConstants.someCampaignIds.size());
        int campaignToSelect = NetNewConstants.someCampaignIds.get(randomIndex);
        Map<String, String> itemAndCampaignName = SharedDBMethods.getItemToSelect(113, campaignToSelect);
        String campaignThatWasUpdated = itemAndCampaignName.get("CAMPAIGN_NAME");
        String itemToCheck = itemAndCampaignName.get("itemApiUnitId");
        String mostRecentDateFromDB = SharedDBMethods.getLastDateFromDB(itemToCheck);
        String asin = campaignThatWasUpdated.split("\\|")[4];
        SharedDBMethods.updateDownloadDate(itemToCheck, mostRecentDateFromDB);

        airTrafficControlPage.selectCampaign(campaignThatWasUpdated);
        Assert.assertNotEquals(airTrafficControlPage.getAsinBackgroundColor(asin), tealColor, "ASIN background color is teal");
        airTrafficControlPage.clickAsin(asin);
        airTrafficControlPage.launchFidoBidding();
        Assert.assertEquals(airTrafficControlPage.getAlertMessage(), "FIDO Bidding was enabled", "FIDO bidding is not enabled for this ASIN");
        Assert.assertEquals(airTrafficControlPage.getAsinBackgroundColor(asin), tealColor, "ASIN background color is not teal");

        airTrafficControlPage.clickAsin(asin);
        airTrafficControlPage.clickDisableFidoBiddingButton();
        Assert.assertEquals(airTrafficControlPage.getAlertMessage(), "FIDO Bidding was disabled", "FIDO bidding is not disabled for this ASIN");
        airTrafficControlPage.getAsinBackgroundColor(asin);
        Assert.assertNotEquals(airTrafficControlPage.getAsinBackgroundColor(asin), tealColor, "ASIN background color is teal");
    }

    public Map<String, Map<String, String>> getDatesAndValuesFromDB(int businessUnitId, String metric, String startDate, String endDate) throws SQLException {
        Map<String, Map<String, String>> datesAndValuesFromDB = new HashMap<>();
        RedShiftUtility.connectToServer();
        ResultSet rs = RedShiftUtility.executeQuery(ATCQueries.queryToFetchDatesAndValuesFromDB(businessUnitId, metric, startDate, endDate));

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            String dateKey = rs.getString("date_key");
            Map<String, String> columnValues = new HashMap<>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                if (!columnName.equals("date_key")) {
                    String columnValue = rs.getString(i);
                    columnValues.put(columnName, columnValue);
                }
            }

            datesAndValuesFromDB.put(dateKey, columnValues);
        }

        RedShiftUtility.closeConnections();
        return datesAndValuesFromDB;
    }

    public Map<String, Map<String, String>> getDatesAndValuesFromDBForSpecificLabels(int businessUnitId, String metric, String startDate, String endDate, List<String> labelsToCheck) throws SQLException {
        Map<String, String> labelToColumnNameMapping = new HashMap<>();
        labelToColumnNameMapping.put("No Automation", "non_automated");
        labelToColumnNameMapping.put("Perpetua", "perpetua");
        labelToColumnNameMapping.put("Ensemble", "ensemble");
        labelToColumnNameMapping.put("FIDO", "fido");
        labelToColumnNameMapping.put("RBB", "rbb");

        Map<String, Map<String, String>> datesAndValuesFromDB = new HashMap<>();
        RedShiftUtility.connectToServer();
        ResultSet rs = RedShiftUtility.executeQuery(ATCQueries.queryToFetchDatesAndValuesFromDB(businessUnitId, metric, startDate, endDate));

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (rs.next()) {
            String dateKey = rs.getString("date_key");
            Map<String, String> columnValues = datesAndValuesFromDB.get(dateKey);

            if (columnValues == null) {
                columnValues = new HashMap<>();
            }

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);

                for (String label : labelsToCheck) {
                    if (labelToColumnNameMapping.containsKey(label) && labelToColumnNameMapping.get(label).equalsIgnoreCase(columnName)) {
                        String columnValue = rs.getString(i);
                        columnValues.put(label, columnValue);
                        break;
                    }
                }
            }

            datesAndValuesFromDB.put(dateKey, columnValues);
        }

        RedShiftUtility.closeConnections();
        return datesAndValuesFromDB;
    }

    @DataProvider
    public static Object[][] exportOptions() {
        return new Object[][]{
                {"Default - Sum Totals/Bids"},
                {"Daily"},
                {"Weekly"},
                {"Monthly"}
        };
    }

    private String getMaxDaysForOption(String option) {
        switch (option) {
            case "Default - Sum Totals/Bids":
                return "30 days";
            case "Daily":
                return "14 days";
            case "Weekly":
                return "4 weeks";
            case "Monthly":
                return "3 months";
        }
        return null;
    }
}
