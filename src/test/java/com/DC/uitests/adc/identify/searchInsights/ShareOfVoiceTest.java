package com.DC.uitests.adc.identify.searchInsights;

import com.DC.constants.NetNewConstants;
import com.DC.db.identify.SOVQueries;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.identify.executiveDashboard.ShareOfVoicePage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SnowflakeUtility;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.DC.utilities.SharedMethods.getRandomNumber;


public class ShareOfVoiceTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private ShareOfVoicePage shareOfVoicePage;
    private AppHomepage appHomepage;

    @BeforeMethod
    public void setupTestMethodForNetNew(final ITestContext testContext, ITestResult tr) throws InterruptedException {
        testMethodName.set(tr.getMethod().getMethodName());
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName + " ***************");
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu navigationMenu = new NetNewNavigationMenu(driver);
        navigationMenu.selectBU("McCormick US");

        appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Identify");
        appHomepage.clickLink("Share of Voice");
        shareOfVoicePage = new ShareOfVoicePage(driver);
        shareOfVoicePage.closeInformationalPopUp("Share of Voice");
    }

    @AfterMethod()
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify Display of Central SOV Screen")
    public void SOV_SOVScreenIsDisplayed() {

        Assert.assertTrue(shareOfVoicePage.verifyDisplayOfCentralSOVScreen(), "The central screen for SOV is not displayed.");
        String currentUrl = shareOfVoicePage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("sov"), "Page url does not contain \nsov\n");

    }

    @Test(description = "Verify Date Selection is displayed and Last 7 Days by default")
    public void SOV_DateSelectionIsDisplayedAndCurrentDateByDefault() {

        Assert.assertTrue(shareOfVoicePage.dateAndIntervalPickerPage.isDateSelectionDisplayed(), "The date selection is not displayed");

        String defaultDateValue = shareOfVoicePage.dateAndIntervalPickerPage.getDefaultDateSelection();

        String startOfLast7Days = DateUtility.getFirstDayOfLastSevenDays();
        String endOfLast7Days = DateUtility.getYesterday();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String startOfLast7DaysFormatted = LocalDate.parse(startOfLast7Days).format(formatter);
        String endOfLast7DaysFormatted = LocalDate.parse(endOfLast7Days).format(formatter);

        String expectedDateRange = startOfLast7DaysFormatted + " - " + endOfLast7DaysFormatted;
        LOGGER.info("Expected Date Range: " + expectedDateRange);
        LOGGER.info("Default Date Range: " + defaultDateValue);

        Assert.assertEquals(defaultDateValue, expectedDateRange, "Default date selection is not set to the current single day.");

    }

    @Test(description = "Verify left side filters collapse after clicking on Filter icon")
    public void SOV_LeftSideFiltersCollapse() throws Exception {

        Assert.assertFalse(shareOfVoicePage.dcFilters.verifyCollapseOfLeftSideFilters(), "The left side filters are not collapsed after clicking on Filter icon");

    }

    @Test(description = "Verify the presence of all left side filters, Cancel and Apply buttons")
    public void SOV_AllLeftSideFiltersAndButtonsArePresent() {

        Assert.assertTrue(shareOfVoicePage.dcFilters.verifyPresenceOfAllLeftSideFilters(), "Not all the filters are displayed.");
        Assert.assertTrue(shareOfVoicePage.dcFilters.verifyDisplayOfCancelButton(), "The Cancel button is not displayed.");
        Assert.assertTrue(shareOfVoicePage.dcFilters.verifyDisplayOfApplyButton(), "The Apply button is not displayed.");

    }

    @Test(description = "Verify that user can select a single retailer")
    public void SOV_SingleRetailerSelectionIsWorking() throws Exception {

        String retailerToSelect = "Amazon";
        shareOfVoicePage.dcFilters.selectRetailer(retailerToSelect);
        Assert.assertTrue(shareOfVoicePage.dcFilters.verifySingleRetailerSelection(retailerToSelect), "The single retailer selection is not working.");
        Assert.assertEquals(shareOfVoicePage.dcFilters.getSelectedRetailer(retailerToSelect), "Amazon", "The selected retailer is not correct.");

        shareOfVoicePage.dcFilters.clickCancelButton();

    }

    @Test(description = "Verify that user can select multiple retailers")
    public void SOV_MultipleRetailerSelectionIsWorking() throws Exception {
        int numOfRetailersSelected = shareOfVoicePage.dcFilters.selectMultipleRetailers("Amazon", "Walmart");
        List<String> retailersInFilter = shareOfVoicePage.dcFilters.getRetailersSelected();
        Assert.assertEquals(retailersInFilter.size(), numOfRetailersSelected, "Number of retailers selected is not correct");
        shareOfVoicePage.dcFilters.apply();

        shareOfVoicePage.scrollToBottomOfPage();
        List<String> retailersInUI = shareOfVoicePage.getDataRetailers();

        Assert.assertEquals(retailersInFilter, retailersInUI, "Retailers in filter and in table are not the same");

    }

    // FIX AFTER DEMO
    //    @Test(priority = 6, description = "Verify that when the competitor option is enabled (switched ON) in the left hand filters, header of SOV table changes to 'Share of Voice by Term'")
    //    public void SOV_CompetitorOptionEnabledIsWorking() throws Exception {
    //
    //        shareOfVoicePage.verifyCompetitorOptionEnabled();
    //        LOGGER.info("The header of SOV table changes to 'Share of Voice by Term'");
    //    }
    //
    //    @Test(priority = 7, description = "Verify that when the competitor option is ON, Manufacturer is selected by default in the View By dropdown")
    //    public void SOV_CompetitorOptionDisabledIsWorking() throws Exception {
    //
    //        verifyManufacturerIsChosenByDefault();
    //        LOGGER.info("The Manufacturer option is selected by default in the View By drop-down");
    //        shareOfVoicePage.clickCompetitorToggle();
    //    }

    @Test(description = "Verify that single date selection is working")
    public void SOV_SingleDateSelectionIsWorking() throws Exception {

        String expectedDate = DateUtility.formattingDate(DateUtility.getFirstDayOfThisMonth());
        String expectedDateText = expectedDate + " - " + expectedDate;
        shareOfVoicePage.dateAndIntervalPickerPage.selectSingleDate();

        String selectedDate = shareOfVoicePage.dateAndIntervalPickerPage.getSelectedDate();
        Assert.assertEquals(selectedDate, expectedDateText, "The selected date is not correct.");

    }

    @Test(description = "Verify that the date range selection is working")
    public void SOV_DateRangeSelectionIsWorking() throws Exception {

        List<String> expectedDateRanges = shareOfVoicePage.dateAndIntervalPickerPage.getExpectedDateRanges();
        List<String> selectedDateRanges = shareOfVoicePage.dateAndIntervalPickerPage.selectDateRange();

        Assert.assertEquals(selectedDateRanges, expectedDateRanges, "Selected date ranges are not correct");
    }

    @Test(description = "Verify Interval Selection is displayed and Daily by default")
    public void SOV_IntervalSelectionIsDisplayedAndDailyByDefault() {

        Assert.assertTrue(shareOfVoicePage.dateAndIntervalPickerPage.isIntervalSelectionDisplayed(), "The interval selection is not displayed");

        String defaultIntervalValue = shareOfVoicePage.dateAndIntervalPickerPage.getDefaultIntervalSelection();
        Assert.assertEquals(defaultIntervalValue, "Daily", "Default interval selection is not set to Daily.");

    }

    @Test(description = "Verify that the interval dropdown has options: Hourly, Daily, Weekly, Monthly")
    public void SOV_IntervalDropdownHasRequiredOptions() throws Exception {

        List<String> expectedIntervalOptions = Arrays.asList("Hourly", "Daily", "Weekly", "Monthly");

        Assert.assertTrue(intervalDropdownVerification(expectedIntervalOptions), "The interval dropdown does not have required options");
        shareOfVoicePage.dateAndIntervalPickerPage.clickIntervalDropdown();

    }

    @Test(description = "Verify that the top bar of SOV table includes required columns and download icon")
    public void SOV_ColumnHeaderInGridAreDisplayed() {

        Assert.assertTrue(shareOfVoicePage.verifyDisplayOfAllTermsColumnHeader(), "The All Terms column header is not displayed.");
        Assert.assertTrue(shareOfVoicePage.verifyDisplayOfAmazonSFRColumnHeader(), "The Amazon SFR column header is not displayed.");
        Assert.assertTrue(shareOfVoicePage.verifyDisplayOfAverageScoreColumnHeader(), "The Average Score column header is not displayed.");
        Assert.assertTrue(shareOfVoicePage.verifyDisplayOfDownloadIcon(), "The Download Icon is not displayed.");

    }

    @Test(description = "Verify that The report page is titled: Share of Voice by Retailer")
    public void SOV_ReportPageTitleChangesAfterCompetitorToggle() {

        verifyReportPageTitle();
        LOGGER.info("Report page title is correct.");
    }

    @Test(description = "Verify that Average Score column = the average SOV % for the search term across all selected/filtered/displayed retailers")
    public void SOV_AverageScoreColumnIsCalculatedCorrectly() throws InterruptedException {

        List<String> searchTermToSelect = Arrays.asList("coconut milk", "garlic", "cinnamon sugar");

        shareOfVoicePage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 21, 21);

        shareOfVoicePage.searchForSearchTerm(searchTermToSelect, "All 1st Page results", "Amazon", "Walmart");
        Map<String, String> averageValuesFromUI = shareOfVoicePage.getValuesFromUI("label-avf-score");
        Map<String, String> averageValuesFromDB = getAverageSOVValuesFromDB("McCormick", "2023-08-21", "2023-08-21", "_virtual_entire_page", searchTermToSelect);
        Assert.assertEquals(averageValuesFromUI, averageValuesFromDB, "Average Score column is not calculated correctly.");

        shareOfVoicePage.refreshPage();
    }

    @Test(description = "Verify that Placement Type Dropdown is displayed, single select only and All 1st Page Results is selected by default")
    public void SOV_PlacementTypeDropdownIsDisplayedAndSingleSelectOnly() throws Exception {

        Assert.assertTrue(shareOfVoicePage.commonFeatures.verifyDisplayOfPlacementTypeDropdown(), "The Placement Type Dropdown is not displayed.");
        shareOfVoicePage.commonFeatures.isDropdownSingleSelect();
        shareOfVoicePage.refreshPage();
    }

    @Test(description = "Verify that required placement types are available in the dropdown all the time")
    public void SOV_PlacementTypesAvailableInDropdown() throws InterruptedException {

        verifyPlacementTypesForRetailer("Target", expectedPlacementsListForTarget);
        verifyPlacementTypesForRetailer("Walmart", expectedPlacementsListForWalmart);
        verifyPlacementTypesForRetailer("Amazon", expectedPlacementsListForAmazon);
        // For future when Instacart will have data
        //verifyPlacementTypesForRetailer("Instacart", expectedPlacementsListForInstacart);
    }

    @Test(description = "Verify Weight Search Term toggle and Weight Rank toggle are displayed and working as intended")
    public void SOV_WeightSearchTermToggleAndWeightRankToggleDisplayedAndWorkingAsIntended() throws Exception {

        Assert.assertTrue(shareOfVoicePage.verifyDisplayOfWeightSearchTermToggle(), "The Weight Search Term toggle is not displayed.");
        Assert.assertTrue(shareOfVoicePage.verifyDisplayOfWeightRankToggle(), "The Weight Rank toggle is not displayed.");

        shareOfVoicePage.toggleValidation(shareOfVoicePage.getWeightSearchTermToggle(), "Weight Search Term");
        shareOfVoicePage.toggleValidation(shareOfVoicePage.getWeightRankToggle(), "Weight Rank");
        LOGGER.info("The Weight Search Term toggle and Weight Rank toggle are displayed and working as intended");

    }

    @Test(description = "Verify Content of Search Term Details Page")
    public void SOV_SearchTermDetailsPageColumnsArePresent() throws Exception {

        shareOfVoicePage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Sept", 13, 14);
        shareOfVoicePage.verifyTableInDetailsPageIncludesSpecifiedColumns();
        shareOfVoicePage.closeDetailsPage();
        LOGGER.info("The table in the Search Term Details page includes the specified columns");
    }

    @Test(description = "Verify that by default 50 terms are displayed per page")
    public void SOV_DefaultNumberOfTermsIsCorrect() {

        Assert.assertTrue(verifyDefaultNumberOfTermsDisplayedPerPage(), "The default number of terms displayed per page is not 50.");
    }

    @Test(description = "Verify that the table cells are colored according to the shading and percentages")
    public void SOV_TableCellsColoredAccordingToShadingAndPercentages() throws InterruptedException {

        shareOfVoicePage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 13, 14);

        Assert.assertTrue(shareOfVoicePage.verifyShadingColor(),
                "The table cells are not colored according to the shading and percentages");
        shareOfVoicePage.refreshPage();
    }

    @Test(description = "Verify that toggle in Export is disabled if a single date is selected")
    public void SOV_ToggleInExportIsDisabledIfSingleDateSelected() throws InterruptedException {

        shareOfVoicePage.dateAndIntervalPickerPage.selectSingleDate();
        shareOfVoicePage.clickExportIcon();
        Assert.assertTrue(shareOfVoicePage.isToggleDisabled(), "The toggle in Export is not disabled if a single date is selected");
        shareOfVoicePage.dateAndIntervalPickerPage.clickCloseIcon();
        LOGGER.info("The toggle in Export is disabled if a single date is selected");
    }

    @Test(description = "Verify that toggle in Export is enabled if date range is selected")
    public void SOV_ToggleInExportIsEnabledIfDateRangeSelected() throws InterruptedException {

        shareOfVoicePage.dateAndIntervalPickerPage.selectDateRange();
        shareOfVoicePage.clickExportIcon();
        Assert.assertTrue(shareOfVoicePage.isToggleEnabled(), "The toggle in Export is not enabled if date range is selected");
        shareOfVoicePage.dateAndIntervalPickerPage.clickCloseIcon();
        LOGGER.info("The toggle in Export is enabled if date range is selected");
    }

    @Test(description = "Verify that default date selection mirrors the date selection on the main page")
    public void SOV_DefaultDateSelectionMirrorsDateSelectionOnMainPage() throws InterruptedException {

        shareOfVoicePage.clickExportIcon();
        Assert.assertTrue(shareOfVoicePage.commonFeatures.isExportHeaderDisplayed(), "The Export Header is not displayed.");
        Assert.assertTrue(shareOfVoicePage.commonFeatures.isExportButtonInExportWindowDisplayed(), "The Export button in Export window is not displayed.");
        Assert.assertTrue(shareOfVoicePage.dateAndIntervalPickerPage.isExportDateSelectionMirrorUI(), "The default date selection does not mirror the date selection on the main page");
    }

    @Test(description = "Verify that after clicking SOV link on Home Page user is redirected to the SOV page")
    public void SOV_ClickingHomePageLinkRedirectsUserToHomePage() throws InterruptedException {

        appHomepage = shareOfVoicePage.clickFWLogo();
        LOGGER.info("After clicking HomePage link user is redirected to the Home Page");
        appHomepage.clickOnSection("Identify");
        appHomepage.clickLink("Share of Voice");
        Assert.assertTrue(shareOfVoicePage.verifyDisplayOfCentralSOVScreen(), "The Share of Voice page is not displayed.");
        String currentUrl = shareOfVoicePage.getCurrentUrl();
        LOGGER.info("Current page url: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("sov"), "Page url does not contain sov");
    }

    @Test(description = "Verify that correct value of Share of Voice (SOV) % to be populated in each cell in table as per filter selection")
    public void SOV_CorrectValueOfSOVPopulatedInEachCellInTable() throws InterruptedException {

        String startDate = "2023-08-27";
        String endDate = "2023-08-27";
        String retailer = "Amazon";
        List<String> searchTermToSelect = Arrays.asList("black pepper", "food coloring");

        shareOfVoicePage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 27, 27);

        shareOfVoicePage.searchForSearchTerm(searchTermToSelect, "All 1st Page results", retailer);

        List<Map<String, String>> sovValuesFromDB = getSOVValuesFromDB("McCormick", startDate, endDate, "amazon.com", "_virtual_entire_page", searchTermToSelect);

        List<String> weightedShareValues = extractShareValuesFromDB(sovValuesFromDB, "Weighted Share");
        Collections.sort(weightedShareValues);
        List<String> unweightedShareValues = extractShareValuesFromDB(sovValuesFromDB, "Unweighted Share");
        Collections.sort(unweightedShareValues);

        checkShareValuesAgainstUI(weightedShareValues, unweightedShareValues);
        LOGGER.info("Results in UI match results in DB");
    }

    @Test(description = "Verify that column for Amazon SFR displays the relevant data for selected date selection")
    public void SOV_ColumnForAmazonSFRDisplaysRelevantDataForSelectedDateSelection() throws InterruptedException {

        String startDate = "2023-08-23";
        String endDate = "2023-08-27";
        String retailer = "Amazon";
        List<String> searchTermToSelect = Arrays.asList("asian noodles", "curry paste");

        shareOfVoicePage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 23, 27);

        shareOfVoicePage.searchForSearchTerm(searchTermToSelect, "All 1st Page results", retailer);
        Map<String, String> sfrValuesFromUI = shareOfVoicePage.getValuesFromUI("sfr-cell-renderer");
        Map<String, String> sfrValuesFromDB = getSFRValuesFromDB(startDate, endDate, "amazon.com", searchTermToSelect);
        Assert.assertEquals(sfrValuesFromUI, sfrValuesFromDB, "SFR values from UI and DB do not match");
    }

    @Test(description = "Verify that the 'Average Overall' row, which represents the average SOV % across all search terms for a specific retailer, displays correct data")
    public void SOV_VerifyAverageOverallSOVData() throws InterruptedException {
        shareOfVoicePage.refreshPage();

        String startDate = "2023-08-18";
        String endDate = "2023-08-18";
        List<String> searchTermToSelect = Arrays.asList("lemon extract", "minced onion", "pumpkin pie spice");

        shareOfVoicePage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 18, 18);

        shareOfVoicePage.searchForSearchTerm(searchTermToSelect, "All 1st Page results", "Amazon");

        boolean weightRankTogglePosition = shareOfVoicePage.getToggleStatus(shareOfVoicePage.getWeightSearchTermToggle());
        if (weightRankTogglePosition) {
            LOGGER.info("Weight Search Term toggle is enabled");
            verifyAverageOverallForWeightedSearchTerms("McCormick", startDate, endDate, "amazon.com", "_virtual_entire_page", searchTermToSelect);
        } else {
            LOGGER.info("Weight Search Term toggle is already disabled");
        }

        LOGGER.info("Average Overall for Weighted Search Terms from UI and calculated match. Checking Average Overall for Unweighted Search Terms");

        shareOfVoicePage.clickToggle(shareOfVoicePage.getWeightSearchTermToggle());
        LOGGER.info("Weight Search Term toggle is clicked");

        boolean unweightedRankTogglePosition = shareOfVoicePage.getToggleStatus(shareOfVoicePage.getWeightSearchTermToggle());
        if (!unweightedRankTogglePosition) {
            LOGGER.info("Weight Search Term toggle is disabled");
            verifyAverageOverallForUnweightedSearchTerms();
        } else {
            LOGGER.info("Weight Search Term toggle is already enabled");
        }

        LOGGER.info("Average Overall for Unweighted Search Terms from UI and calculated match");
    }

    @Test(description = "Verify that Search Term details page is working as expected")
    public void SOV_SearchTermDetailsPageIsWorkingAsExpected() throws InterruptedException {

        String date = "2023-08-26";
        String retailer = "Amazon";
        List<String> searchTermToSelect = Arrays.asList("pumpkin pie spice");
        String placement = "All 1st Page results";

        shareOfVoicePage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 26, 26);

        shareOfVoicePage.searchForSearchTerm(searchTermToSelect, placement, retailer);
        shareOfVoicePage.verifyDetailFlyoutValuesMirrorMainPage(placement);

        String hourValueInUI = shareOfVoicePage.getHourValue();

        int rowNumber = getRandomNumber(5);
        String rpcValueInUI = shareOfVoicePage.getRPCValueFromUI(rowNumber);
        String changeInRank = shareOfVoicePage.getRankChangeValueFromUI(rowNumber);
        LOGGER.info("RPC: " + rpcValueInUI);

        Map<Integer, Boolean> valuesInDB = getRPCAndRankValuesFromDB(date, "amazon.com", "_virtual_entire_page", hourValueInUI, rpcValueInUI, searchTermToSelect);
        Map.Entry<Integer, Boolean> highestRankRowInDB = valuesInDB.entrySet().iterator().next();
        int highestRankInDB = highestRankRowInDB.getKey();
        boolean associatedSponsored = highestRankRowInDB.getValue();

        LOGGER.info("Running query to get the rank value from 7 days ago");
        String date7DaysAgo = "2023-08-19";
        Map<Integer, Boolean> valuesInDB7DaysAgo = getRPCAndRankValuesFromDB(date7DaysAgo, "amazon.com", "_virtual_entire_page", hourValueInUI, rpcValueInUI, searchTermToSelect);
        Map.Entry<Integer, Boolean> highestRankRowInDB7DaysAgo = valuesInDB7DaysAgo.entrySet().iterator().next();
        int highestRankInDB7DaysAgo = highestRankRowInDB7DaysAgo.getKey();
        boolean associatedSponsoredInDB7DaysAgo = highestRankRowInDB7DaysAgo.getValue();
        if (!associatedSponsoredInDB7DaysAgo == associatedSponsored) {
            LOGGER.info("Associated Sponsored from 7 days ago is not the same as the current associated sponsored. Iterating through the map to find the matching sponsored value and rank");
            for (Map.Entry<Integer, Boolean> entry : valuesInDB7DaysAgo.entrySet()) {
                if (entry.getValue() == associatedSponsored) {
                    highestRankInDB7DaysAgo = entry.getKey();
                    break;
                }
            }
        }
        int rankChange = highestRankInDB7DaysAgo - highestRankInDB;
        String rankChangeInUI = rankChange > 0 ? "Up " + rankChange : (rankChange < 0 ? "Down " + Math.abs(rankChange) : "No Change");
        Assert.assertEquals(changeInRank, rankChangeInUI, "Change in rank value in UI and DB do not match");

        LOGGER.info("Change in rank value in UI and DB match");
    }

    public boolean verifyDefaultNumberOfTermsDisplayedPerPage() {

        shareOfVoicePage.scrollToBottomOfPage();
        String defaultNumberOfTermsDisplayedPerPage = shareOfVoicePage.commonFeatures.getDefaultNumberOfTermsDisplayed();
        LOGGER.info("Default number of terms displayed per page: " + defaultNumberOfTermsDisplayedPerPage);

        Assert.assertEquals(defaultNumberOfTermsDisplayedPerPage, "50", "The default number of terms displayed per page is not 50.");

        return true;
    }

    public void verifyReportPageTitle() {
        String expectedTitle = "Share of Voice by Retailer";
        String actualTitle = shareOfVoicePage.getReportPageTitle();

        Assert.assertEquals(actualTitle, expectedTitle, "Report page title is not correct");
    }

    public boolean intervalDropdownVerification(List<String> expectedIntervalOptions) throws InterruptedException {

        List<String> actualIntervalOptions = shareOfVoicePage.dateAndIntervalPickerPage.getIntervalDropdownOptions();

        Assert.assertEqualsNoOrder(actualIntervalOptions.toArray(), expectedIntervalOptions.toArray(),
                "Interval options do not match expected options" +
                        "\nExpected:\n" + expectedIntervalOptions +
                        "\nActual:\n" + actualIntervalOptions
        );

        return true;
    }

    public boolean verifyAverageScoreColumnValue() {

        int averageScoreColumnValueFromUI = shareOfVoicePage.getAverageScoreColumnValueFromUI();
        int calculatedAverageScore = shareOfVoicePage.calculateAverageScoreAmongColumns();

        Assert.assertEquals(averageScoreColumnValueFromUI, calculatedAverageScore,
                "The Average Score column value does not match the calculated average score" +
                        "\nExpected:\n" + calculatedAverageScore +
                        "\nActual:\n" + averageScoreColumnValueFromUI
        );

        return true;
    }

    public void verifyManufacturerIsChosenByDefault() {

        String defaultViewByOption = shareOfVoicePage.getDefaultViewByOption();
        Assert.assertEquals(defaultViewByOption, "Manufacturer", "The default option in View By drop-down is not Manufacturer");
    }

    public boolean verifyAverageOverallColumnValue() {

        int averageOverallColumnValueFromUI = shareOfVoicePage.getOverallScoresValueFromUI();
        int calculatedAverageOverall = shareOfVoicePage.calculateAverageOverallAmongColumns();

        Assert.assertEquals(averageOverallColumnValueFromUI, calculatedAverageOverall,
                "The Average Overall column value does not match the calculated average overall" +
                        "\nExpected:\n" + calculatedAverageOverall +
                        "\nActual:\n" + averageOverallColumnValueFromUI
        );

        return true;
    }

    public List<Map<String, String>> getSOVValuesFromDB(String BU, String startDate, String endDate, String retailerPlatform, String placementType, List<String> searchTerm) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        List<Map<String, String>> valuesList = new ArrayList<>();

        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SOVQueries.queryToFetchSOVStatuses(BU, startDate, endDate, retailerPlatform, placementType, searchTerm))) {

                while (resultSet.next()) {
                    Map<String, String> valuesMap = new HashMap<>();

                    double weightedValueInDB = Double.parseDouble(resultSet.getString("WEIGHTED_SHARE"));
                    int roundedWeightedValue = (int) Math.round(weightedValueInDB * 100);
                    valuesMap.put("Weighted Share", String.valueOf(roundedWeightedValue));

                    double unweightedValueInDB = Double.parseDouble(resultSet.getString("UNWEIGHTED_SHARE"));
                    int roundedUnweightedValue = (int) Math.round(unweightedValueInDB * 100);
                    valuesMap.put("Unweighted Share", String.valueOf(roundedUnweightedValue));

                    valuesList.add(valuesMap);
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Values from DB: " + valuesList);
        return valuesList;
    }

    private List<String> extractShareValuesFromDB(List<Map<String, String>> dbValues, String columnName) {
        List<String> shareValues = new ArrayList<>();
        for (Map<String, String> rowValues : dbValues) {
            String shareValue = rowValues.get(columnName);
            shareValues.add(shareValue);
        }
        return shareValues;
    }

    private void checkShareValuesAgainstUI(List<String> weightedShareValues, List<String> unweightedShareValues) {
        LOGGER.info("Checking position of Weight Rank toggle");
        boolean weightRankTogglePosition = shareOfVoicePage.getToggleStatus(shareOfVoicePage.getWeightRankToggle());

        List<String> uiWeightedRanks = shareOfVoicePage.getRanksFromUI();
        LOGGER.info("Weighted Ranks from UI: " + uiWeightedRanks);

        if (weightRankTogglePosition) {
            if (uiWeightedRanks.equals(weightedShareValues)) {
                LOGGER.info("Weighted SOV values in table are the same as in DB");
            } else {
                Assert.fail("Weighted SOV values in table are not the same as in DB");
            }
            shareOfVoicePage.clickToggle(shareOfVoicePage.getWeightRankToggle());
        } else {
            Assert.fail("Weight Rank toggle should be in the 'true' position but it is not.");
        }

        boolean unweightedRankTogglePosition = shareOfVoicePage.getToggleStatus(shareOfVoicePage.getWeightRankToggle());

        if (!unweightedRankTogglePosition) {
            List<String> uiUnweightedRanks = shareOfVoicePage.getRanksFromUI();
            LOGGER.info("Unweighted Ranks from UI: " + uiUnweightedRanks);

            if (uiUnweightedRanks.equals(unweightedShareValues)) {
                LOGGER.info("Unweighted Ranks from DB match the UI.");
            } else {
                Assert.fail("Unweighted Ranks from DB do not match the UI.");
            }
        } else {
            Assert.fail("Weight Rank toggle should be in the 'false' position but it is not.");
        }
    }

    public Map<String, String> getSFRValuesFromDB(String startDate, String endDate, String retailerPlatform, List<String> searchTerm) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        Map<String, String> sfrsList = new HashMap<>();

        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SOVQueries.queryToFetchSFRValues(startDate, endDate, retailerPlatform, searchTerm))) {

                while (resultSet.next()) {
                    String searchTermFromDB = resultSet.getString("SEARCH_TERM");
                    String searchFrequencyRankFromDB = resultSet.getString("SEARCH_FREQUENCY_RANK");
                    sfrsList.put(searchTermFromDB, searchFrequencyRankFromDB);
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Values from DB: " + sfrsList);
        return sfrsList;
    }

    public Map<String, String> getAverageSOVValuesFromDB(String BU, String startDate, String endDate, String placementType, List<String> searchTerm) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        Map<String, String> valuesMap = new HashMap<>();

        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SOVQueries.queryToFetchAverageSOV(BU, startDate, endDate, placementType, searchTerm))) {

                while (resultSet.next()) {
                    String searchTermFromDB = resultSet.getString("SEARCH_TERM");
                    String averageValueFromDB = resultSet.getString("AVERAGE_VALUE");
                    double averageValueFromDBDouble = Double.parseDouble(averageValueFromDB) * 100;
                    int roundedAverageValueFromDB = (int) Math.round(averageValueFromDBDouble);
                    averageValueFromDB = String.valueOf(roundedAverageValueFromDB);
                    valuesMap.put(searchTermFromDB, averageValueFromDB);
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Values from DB: " + valuesMap);
        return valuesMap;
    }

    public void checkPlacements(List<String> expectedPlacementsList) throws InterruptedException {
        List<String> actualPlacementsList = shareOfVoicePage.commonFeatures.getPlacementsFromUI();
        LOGGER.info("Actual placements from UI: " + actualPlacementsList);
        Assert.assertEqualsNoOrder(actualPlacementsList.toArray(), expectedPlacementsList.toArray(), "Placements are not as expected. " +
                "Expected: " + expectedPlacementsList + " Actual: " + actualPlacementsList);
    }

    public void verifyPlacementTypesForRetailer(String retailer, List<String> expectedPlacementsList) throws InterruptedException {
        shareOfVoicePage.dcFilters.selectRetailer(retailer);
        checkPlacements(expectedPlacementsList);
        LOGGER.info("Placement types are correct for " + retailer);
        shareOfVoicePage.refreshPage();
    }

    List<String> expectedPlacementsListForTarget = Arrays.asList("Organic Only", "Sponsored Only", "SP ATF", "SP MTF", "SP BTF", "All 1st Page results");
    List<String> expectedPlacementsListForWalmart = Arrays.asList("Organic Only", "Sponsored Only", "SP ATF", "SP MTF", "SP BTF", "SP Carousel", "All 1st Page results", "Brand Amplifier");
    List<String> expectedPlacementsListForInstacart = Arrays.asList("Sponsored Only", "SP ATF", "SP MTF", "SP BTF", "All 1st Page results", "Organic Only");
    List<String> expectedPlacementsListForAmazon = Arrays.asList("Organic Only", "Sponsored Only", "SP ATF", "SP MTF", "SP BTF", "All 1st Page results", "Sponsored Brands", "SB Video",
            "Climate Pledge Friendly", "Black Friday deals", "Cyber Monday deals", "Prime Day deals", "Featured from Amazon brands", "Highly rated", "Shop by features from reviews", "Top rated from our brands");

    private void verifyAverageOverallForWeightedSearchTerms(String BU, String startDate, String endDate, String retailerPlatform, String placementType, List<String> searchTerms) throws InterruptedException {
        String averageOverallValuesFromUI = shareOfVoicePage.getAverageOverallRankFromUI();
        LOGGER.info("Average Overall values from UI: " + averageOverallValuesFromUI);
        String averageOverallCalculated = shareOfVoicePage.calculateAverageOverallWeighted(BU, startDate, endDate, retailerPlatform, placementType, searchTerms);
        LOGGER.info("Average Overall values calculated: " + averageOverallCalculated);

        Assert.assertEquals(averageOverallValuesFromUI, averageOverallCalculated, "Average Overall values from UI and calculated do not match. " +
                "Average Overall values from UI: " + averageOverallValuesFromUI + " Average Overall values calculated: " + averageOverallCalculated);
    }

    private void verifyAverageOverallForUnweightedSearchTerms() throws InterruptedException {
        String averageOverallValuesFromUI = shareOfVoicePage.getAverageOverallRankFromUI();
        String averageOverallCalculated = shareOfVoicePage.calculateAverageOverallUnweighted();

        Assert.assertEquals(averageOverallValuesFromUI, averageOverallCalculated, "Average Overall values from UI and calculated do not match. " +
                "Average Overall values from UI: " + averageOverallValuesFromUI + " Average Overall values calculated: " + averageOverallCalculated);
    }

    public Map<Integer, Boolean> getRPCAndRankValuesFromDB(String date, String retailerPlatform, String placementType, String hour, String rpc, List<String> searchTerms) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        Map<Integer, Boolean> rankValues = new HashMap<>();

        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SOVQueries.queryToFetchRPCAndRankValues(date, retailerPlatform, placementType, hour, rpc, searchTerms))) {

                while (resultSet.next()) {
                    int rankValueFromDB = resultSet.getInt("RANK");
                    boolean sponsoredValueFromDB = resultSet.getBoolean("SPONSORED");
                    rankValues.put(rankValueFromDB, sponsoredValueFromDB);
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        rankValues = rankValues.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return rankValues;
    }
}
