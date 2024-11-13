package com.DC.uitests.adc.analyze.searchReporting;

import com.DC.db.analyze.SearchRankQueries;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.searchReporting.SearchRankPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SnowflakeUtility;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.*;

public class SearchRankTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private SearchRankPage searchRankPage;
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
        appHomepage.clickOnSection("Analyze");
        appHomepage.clickLink("Search Rank");
        searchRankPage = new SearchRankPage(driver);
        searchRankPage.closeInformationalPopUp("Search Rank");
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify that the Search Rank page is displayed")
    public void SearchRank_SearchRankPageIsDisplayed() {
        Assert.assertTrue(searchRankPage.isSearchRankHeaderDisplayed(), "Search Rank page is not displayed");
    }

    @Test(description = "Verify that Interval picker is displayed and date range by default is Daily")
    public void SearchRank_IntervalPickerIsDisplayedAndDateRangeByDefaultIsDaily() {
        Assert.assertTrue(searchRankPage.dateAndIntervalPickerPage.isIntervalSelectionDisplayed(), "Interval Selection is not displayed");

        String defaultIntervalValue = searchRankPage.dateAndIntervalPickerPage.getDefaultIntervalSelection();

        Assert.assertEquals(defaultIntervalValue, "Daily", "Default Interval is not Daily");
    }

    @Test(description = "Verify that the Date picker is displayed and date range by default is Last 14 Days")
    public void SearchRank_DatePickerIsDisplayedAndDateRangeByDefaultIsLast14Days() throws InterruptedException, ParseException {
        Assert.assertTrue(searchRankPage.dateAndIntervalPickerPage.isDateSelectionDisplayed(), "Date Selection is not displayed");

        String defaultDateValue = searchRankPage.dateAndIntervalPickerPage.getDefaultDateSelection();

        String expectedStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastFourteenDays());
        String expectedEndDate = DateUtility.formattingDate(DateUtility.getYesterday());

        Assert.assertEquals(defaultDateValue, expectedStartDate + " - " + expectedEndDate, "Default Date Range is not Last 14 Days");

        LOGGER.info("Default Date Range is Last 14 Days. Checking if correct columns are displayed");

        try {
            Assert.assertTrue(searchRankPage.commonFeatures.getActualTableColumnNames().containsAll(getExpectedSKUColumnHeadersDefaultDateRange()), "Column names are not correct for default date range");
        } catch (StaleElementReferenceException e) {
            LOGGER.info("StaleElementReferenceException occurred. Retrying...");
            Assert.assertTrue(searchRankPage.commonFeatures.getActualTableColumnNames().containsAll(getExpectedSKUColumnHeadersDefaultDateRange()), "Column names are not correct for default date range");
            e.printStackTrace();
        }

        LOGGER.info("Correct columns are displayed. Checking if correct dates are displayed in grid");

        LOGGER.info("Changing date range from default to custom 7 Days in Last month");
        searchRankPage.dateAndIntervalPickerPage.selectDateRange("Last Month");
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRange(1, 3);
        String dateAfterChangingDateRange = searchRankPage.dateAndIntervalPickerPage.getSelectedDate();
        int expectedNumberOfDates = searchRankPage.dateAndIntervalPickerPage.countDaysInDateRange(dateAfterChangingDateRange);

        List<String> actualDatesInTable = searchRankPage.commonFeatures.getDatesOrTimesFromTable(true);
        Assert.assertEquals(actualDatesInTable.size(), expectedNumberOfDates, "Number of days in table is not correct for chosen date range");
        Assert.assertTrue(searchRankPage.dateAndIntervalPickerPage.compareDates(actualDatesInTable), "Dates in table are not correct for chosen date range. " +
                "Expected dates: " + dateAfterChangingDateRange + ". Actual dates: " + actualDatesInTable);
    }

    @Test(description = "Verify that the user can select a single date")
    public void SearchRank_UserCanSelectSingleDate() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectDateRange("Last Month");
        String expectedDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastMonth());
        String expectedDateText = expectedDate + " - " + expectedDate;
        searchRankPage.dateAndIntervalPickerPage.selectSingleDate();
        String actualDate = searchRankPage.dateAndIntervalPickerPage.getSelectedDate();
        Assert.assertEquals(actualDate, expectedDateText, "Selected date is not displayed");

        LOGGER.info("Selected date is displayed. Checking if correct columns are displayed");

        try {
            Assert.assertTrue(searchRankPage.commonFeatures.getActualTableColumnNames().containsAll(getExpectedSKUColumnHeadersSingleDate()), "Column names are not correct for single date");
        } catch (StaleElementReferenceException e) {
            LOGGER.info("StaleElementReferenceException occurred. Retrying...");
            Assert.assertTrue(searchRankPage.commonFeatures.getActualTableColumnNames().containsAll(getExpectedSKUColumnHeadersSingleDate()), "Column names are not correct for single date");
            e.printStackTrace();
        }

        LOGGER.info("Correct columns are displayed. Checking if correct date is displayed in grid");

        Assert.assertEquals(searchRankPage.commonFeatures.getDatesOrTimesFromTable(true).size(), 1, "Number of dates in table is not 1 for single date");
        Assert.assertEquals(searchRankPage.commonFeatures.getDatesOrTimesFromTable(true).get(0), expectedDate, "Date in table is not correct for single date. " +
                "Expected date: " + expectedDate + ". Actual date: " + searchRankPage.commonFeatures.getDatesOrTimesFromTable(true).get(0));

        LOGGER.info("Correct date is displayed in grid. Changing Interval to Hourly");

        searchRankPage.dateAndIntervalPickerPage.selectInterval("Hourly");
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Dec", 1, 1);
        Assert.assertEquals(searchRankPage.commonFeatures.getDatesOrTimesFromTable(false).size(), 24, "Number of times in table is not 24 for Hourly interval");
    }

    @Test(description = "Verify that Interval selection has only Daily and Hourly options")
    public void SearchRank_IntervalSelectionHasOnlyDailyAndHourlyOptions() throws InterruptedException {
        Assert.assertEqualsNoOrder(searchRankPage.dateAndIntervalPickerPage.getIntervalDropdownOptions().toArray(), getExpectedIntervalOptions().toArray(), "Interval options are not displayed correctly");
        searchRankPage.hitEscKey();
    }

    @Test(description = "Verify if Hourly selected, Date Range has limited options")
    public void SearchRank_IfHourlySelectedDateRangeHasLimitedOptions() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectInterval("Hourly");
        Assert.assertEqualsNoOrder(searchRankPage.dateAndIntervalPickerPage.getDateRangeDropdownOptions().toArray(), getExpectedHourlyDateRanges().toArray(),
                "Actual Date Range options do not match expected Date Range options");
        searchRankPage.hitEscKey();
    }

    @Test(description = "Verify that RPC data is clickable on hover and opens into the product URL on the website")
    public void SearchRank_RPCDataIsClickableOnHoverAndOpenIntoTheProductURLOnTheWebsite() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectInterval("Daily");
        searchRankPage.dateAndIntervalPickerPage.selectDateRange("Last Month");
        searchRankPage.dateAndIntervalPickerPage.selectSingleDate();
        searchRankPage.selectTermInSearchAllTerms("cajun seasoning");
        Assert.assertTrue(searchRankPage.commonFeatures.areOptionsRedirectedToSameRPCUrl(), "Clicking on RPC in a table does not redirect the product URL on the website");
    }

    @Test(description = "Verify that pagination is displayed and set to 50 by default")
    public void SearchRank_PaginationIsDisplayedAndSetTo50ByDefault() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 16, 20);
        Assert.assertTrue(searchRankPage.commonFeatures.verifyPaginationIsPresent(), "Pagination is not displayed");
        Assert.assertEquals(searchRankPage.commonFeatures.getDefaultNumberOfTermsDisplayed(), "50", "Default number of terms displayed is not 50");

        LOGGER.info("Pagination is displayed and set to 50 by default. Changing number of terms displayed to 10");

        String expectedNumberOfTermsDisplayed = "10";

        String searchTermToSelect = "All Priority Terms";
        searchRankPage.resetAllTerms();
        searchRankPage.selectTermInSearchAllTerms(searchTermToSelect);
        LOGGER.info("Selected term: " + searchTermToSelect);

        searchRankPage.commonFeatures.changePageSize(expectedNumberOfTermsDisplayed);
        Assert.assertEquals(searchRankPage.commonFeatures.getDefaultNumberOfTermsDisplayed(), expectedNumberOfTermsDisplayed, "Number of terms displayed is not 10");
        LOGGER.info("Number of terms displayed is: " + expectedNumberOfTermsDisplayed);

        Assert.assertTrue(searchRankPage.commonFeatures.verifyNextButtonChangesDataInTable("cell-searchTerm", 3));

        searchRankPage.commonFeatures.changePageSize("50");
    }

    @Test(description = "Verify that Search Ranks table are shown starting from most recent date selected in date picker")
    public void SearchRank_SearchRanksTableAreShownStartingFromMostRecentDateSelectedInDatePicker() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 16, 20);
        Assert.assertTrue(searchRankPage.dateAndIntervalPickerPage.isDateColumnSortedCorrectly(), "Search Ranks table are not shown starting from most recent date selected in date picker");
    }

    @Test(description = "Verify that user should be able to deselect the standard columns on SKU table")
    public void SearchRank_UserShouldBeAbleToDeselectColumnsOnSKUTable() throws InterruptedException {
        searchRankPage.commonFeatures.deselectOptionInMoreOptionsDropdown("Search Term", "Best Rank", "Average Rank");
    }

    @Test(description = "Verify that Show More Options button is displayed and works correctly")
    public void SearchRank_ShowMoreOptionsButtonIsDisplayedAndWorksCorrectly() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 16, 20);
        searchRankPage.isShowMoreLessButtonWorksCorrectly();
        LOGGER.info("Show More Options button is displayed and works correctly");
    }

    @Test(description = "Verify that once a term(s) or group(s) is selected, they should be displayed at the top as a tag")
    public void SearchRank_SelectedTermsDisplayedAsATag() throws InterruptedException {
        searchRankPage.selectTermInSearchAllTerms("basil", "cumin");
        Assert.assertTrue(searchRankPage.areSelectedSearchTermsDisplayedAsTags("basil", "cumin"), "Selected terms are not displayed as a tag");
        Assert.assertTrue(searchRankPage.removeTermFromSearchAllTerms("basil"), "Individual terms cannot be removed by clicking on the tag");
    }

    @Test(description = "Verify that All terms can be reset by clicking on the Clear All text within the Search Term Picker")
    public void SearchRank_AllTermsCanBeReset() throws InterruptedException {
        Assert.assertTrue(searchRankPage.resetAllTerms(), "All terms cannot be reset by clicking on the Clear All text within the Search Term Picker");
    }

    @Test(description = "Verify that Placement Type Dropdown is displayed, single select only and All 1st Page Results is selected by default")
    public void SearchRank_PlacementTypeDropdownIsDisplayedSingleSelectOnly() throws InterruptedException {
        Assert.assertTrue(searchRankPage.commonFeatures.verifyDisplayOfPlacementTypeDropdown(), "The Placement Type Dropdown is not displayed.");
        searchRankPage.commonFeatures.isDropdownSingleSelect();
    }

    @Test(description = "Verify that required placement types are available in the dropdown all the time")
    public void SearchRank_RequiredPlacementTypesAreAvailableInTheDropdownAllTheTime() throws InterruptedException {
        Assert.assertTrue(searchRankPage.commonFeatures.verifyPlacementTypeDropdownHasRequiredOptions(), "The required placement types are not available in the dropdown all the time.");
    }

    @Test(description = "Verify that By default, no catalog filters will be selected when a user lands on the report page, retailer filter has All Retailers by default")
    public void SearchRank_ByDefaultNoCatalogFiltersWillBeSelectedWhenAUserLandsOnTheReportPage() {
        //Assert.assertTrue(searchRankPage.dcFilters.getRetailersSelected().contains("All Retailers"), "Retailer filter has All Retailers by default");
        Assert.assertFalse(searchRankPage.dcFilters.verifyNoCatalogFiltersSelectedByDefault("brand"), "Brand filter is not empty by default");
        Assert.assertFalse(searchRankPage.dcFilters.verifyNoCatalogFiltersSelectedByDefault("category"), "Category filter is not empty by default");
        Assert.assertFalse(searchRankPage.dcFilters.verifyNoCatalogFiltersSelectedByDefault("sub-category"), "SubCategory filter is not empty by default");
        Assert.assertFalse(searchRankPage.dcFilters.verifyNoCatalogFiltersSelectedByDefault("segment"), "Segment filter is not empty by default");
        Assert.assertFalse(searchRankPage.dcFilters.verifyNoCatalogFiltersSelectedByDefault("sku"), "SKU filter is not empty by default");
    }

    @Test(description = "Verify that default date selection in Export window mirrors the date selection on the main page")
    public void SearchRank_UserCanExportSKUTableInSelectedDateRange() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 5, 6);

        Assert.assertTrue(searchRankPage.isSearchRankExportIconDisplayed(), "Export icon is not displayed");
        searchRankPage.clickOnExportIcon();
        Assert.assertTrue(searchRankPage.dateAndIntervalPickerPage.isExportDateSelectionMirrorUI(), "Default date selection in Export window does not mirror the date selection on the main page");
    }

    @Test(description = "Verify that user can select multiple rows from the table")
    public void SearchRank_UserCanSelectMultipleRowsFromTheTable() throws InterruptedException {
        List<Integer> indices = Arrays.asList(0, 2);
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 5, 6);
        searchRankPage.selectTermInSearchAllTerms("basil");
        searchRankPage.selectRowsFromTable(indices);
        Assert.assertTrue(searchRankPage.isRowsNumberLineDisplayed(), "Number of selected rows is not displayed");
        Assert.assertTrue(searchRankPage.isNumberOfSelectedRowsCorrect(indices), "Number of selected rows is not correct");
    }

    @Test(description = "Verify that buttons appear after selecting row and work correctly")
    public void SearchRank_ButtonsAppearAfterSelectingRowAndWorkCorrectly() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 5, 15);
        searchRankPage.selectRowsFromTable(Collections.singletonList(2));
        Assert.assertTrue(searchRankPage.areRowButtonsDisplayed(), "Buttons do not appear after selecting row");
        searchRankPage.clickOnClearSelectionAndCloseButton();
        Assert.assertTrue(searchRankPage.isAnyRowSelected(), "Clear Selection and Close button does not work correctly");
    }

    @Test(description = "Verify that user is able to see a trend chart for product and it has correct dates")
    public void SearchRank_UserIsAbleToSeeTrendChartForProductSearchTermCombination() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 5, 15);
        searchRankPage.selectRowsFromTable(Arrays.asList(0, 1, 2));
        searchRankPage.clickOnCompareSearchRankButton();
        Assert.assertTrue(searchRankPage.isDateRangeSelectorChartDisplayed(), "Date Range Selector Chart is not displayed");
        Assert.assertTrue(searchRankPage.isDateRangeSelectorChartCorrect(), "Date Range Selector Chart is not correct");
        Assert.assertTrue(searchRankPage.isIntervalSelectorChartDisplayed(), "Interval Selector Chart is not displayed");
        Assert.assertTrue(searchRankPage.isIntervalSelectorChartCorrect(), "Interval Selector Chart is not correct");
        searchRankPage.clickCloseIconInChart();
        searchRankPage.clickOnClearSelectionAndCloseButton();
    }

    @Test(description = "Verify that user can see correct data in the chart")
    public void SearchRank_UserCanSeeCorrectDataInTheChart() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 5, 15);
        List<Integer> indices = Arrays.asList(0, 1, 2);
        searchRankPage.selectRowsFromTable(indices);
        List<String> rpcsFromTable = searchRankPage.getTextFromSelectedRows(indices);
        searchRankPage.clickOnCompareSearchRankButton();
        Assert.assertTrue(searchRankPage.areRpcsInChartCorrect(rpcsFromTable), "RPCs in chart are not correct");
        searchRankPage.clickCloseIconInChart();
        searchRankPage.clickOnClearSelectionAndCloseButton();
    }

    @Test(description = "Verify that user can close the flyout panel for the SKU Trend view and the previously selected rows remain selected")
    public void SearchRank_UserCanCloseTheFlyoutPanelForTheSKUTrendViewAndThePreviouslySelectedRowsRemainSelected() throws InterruptedException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 5, 15);
        List<Integer> indices = Arrays.asList(0, 1, 2);
        searchRankPage.selectRowsFromTable(indices);
        searchRankPage.clickOnCompareSearchRankButton();
        searchRankPage.clickCloseIconInChart();
        Assert.assertFalse(searchRankPage.isAnyRowSelected(), "Selected rows are not still selected");
        searchRankPage.clickOnClearSelectionAndCloseButton();
    }

    @Test(description = "Verify that Search Rank columns should include shading red/green")
    public void SearchRank_SearchRankColumnsShouldIncludeShadingRedGreen() throws InterruptedException, ParseException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 12, 13);
        searchRankPage.dcFilters.selectRetailer("Amazon");
        searchRankPage.dcFilters.selectBrand("lawrys");
        searchRankPage.selectTermInSearchAllTerms("montreal chicken seasoning");
        Assert.assertTrue(searchRankPage.isShadingRedGreenCorrect(), "Search Rank column's shading is not correct");
        searchRankPage.verifyUniquenessOfDaySells();
        LOGGER.info("Search Rank column's values are unique");
    }

    @Test(description = "Verify that Best Rank column values are correctly calculated")
    public void SearchRank_BestRankColumnValuesAreCorrectlyCalculated() throws InterruptedException, ParseException {
        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 12, 13);
        searchRankPage.dcFilters.selectRetailer("Amazon");
        searchRankPage.dcFilters.selectBrand("cholula");
        searchRankPage.selectTermInSearchAllTerms("buffalo sauce", "franks red hot sauce");
        searchRankPage.verifyBestRankInTable();
        LOGGER.info("Best Rank column's values are correctly calculated and displayed");
    }

    @Test(description = "Verify that statuses from UI match statuses from DB")
    public void SearchRank_StatusesFromUIMatchStatusesFromDB() throws InterruptedException {
        String startDate = "2023-08-24";
        String endDate = "2023-08-24";
        String searchTerm = "chicken seasoning";
        String rpc = "B01EOWEC8A";

        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 24, 24);

        searchRankPage.performSearch(searchTerm, rpc, "Amazon", "All 1st Page results");
        compareStatusesFromUIAndDB(startDate, endDate, searchTerm, rpc, "_virtual_entire_page");

        startDate = "2023-08-13";
        endDate = "2023-08-15";
        searchTerm = "crab boil";
        rpc = "B0009PCP6S";

        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 13, 15);

        searchRankPage.performSearch(searchTerm, rpc, "Amazon", "Organic Only");
        compareStatusesFromUIAndDB(startDate, endDate, searchTerm, rpc, "_virtual_non_sponsored");
    }

    @Test(description = "Verify that Best Rank and Average Rank columns in UI match Best Rank and Average Rank columns in DB")
    public void SearchRank_RanksFromUIMatchRanksFromDB() throws InterruptedException {
        String startDate = "2023-08-15";
        String endDate = "2023-08-22";
        String rpc = "B002HQGEUU";
        String searchTerm = "bay leaves";

        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 15, 22);

        searchRankPage.performSearch(searchTerm, rpc, "Amazon", "Organic Only");
        compareRanksFromUIAndDB(startDate, endDate, searchTerm, rpc, "_virtual_non_sponsored");

        startDate = "2023-08-23";
        endDate = "2023-08-29";
        rpc = "B00AWKBLUQ";
        searchTerm = "vegetable stock";

        searchRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 23, 29);

        searchRankPage.performSearch(searchTerm, rpc, "Amazon", "All 1st Page results");
        compareRanksFromUIAndDB(startDate, endDate, searchTerm, rpc, "_virtual_entire_page");
    }

    public List<String> getExpectedIntervalOptions() {
        List<String> expectedIntervalOptions = Arrays.asList("Daily", "Hourly");
        LOGGER.info("Expected Interval Options: " + expectedIntervalOptions);
        return expectedIntervalOptions;
    }

    public List<String> getExpectedHourlyDateRanges() {
        List<String> expectedHourlyDateRanges = Arrays.asList("Today", "Yesterday", "Last 5 Days", "Last 7 Days");
        LOGGER.info("Expected Hourly Date Ranges: " + expectedHourlyDateRanges);
        return expectedHourlyDateRanges;
    }

    public List<String> getExpectedSKUColumnHeadersDefaultDateRange() {
        List<String> expectedSKUColumnHeaders = Arrays.asList("Image", "Product Title", "RPC", "Retailer", "Search Term", "Average Rank", "Best Rank");
        LOGGER.info("Expected SKU Column Headers: " + expectedSKUColumnHeaders);
        return expectedSKUColumnHeaders;
    }

    public List<String> getExpectedSKUColumnHeadersSingleDate() {
        List<String> expectedSKUColumnHeaders = Arrays.asList("Image", "Product Title", "RPC", "Retailer", "Search Term");
        LOGGER.info("Expected SKU Column Headers: " + expectedSKUColumnHeaders);
        return expectedSKUColumnHeaders;
    }

    public List<String> getSearchRankStatusesFromDB(String startDate, String endDate, String retailerPlatform, String searchTerm, String RPC, String placementType) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        List<String> statuses = new ArrayList<>();
        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SearchRankQueries.queryToFetchSearchRankStatuses(startDate, endDate, retailerPlatform, searchTerm, RPC, placementType))) {
                while (resultSet.next()) {
                    if (resultSet.getString("PLACEMENT").equals(placementType)) {
                        if (resultSet.getString("SPONSORED").equalsIgnoreCase("true")) {
                            statuses.add(resultSet.getString("SPONSORED").replace("TRUE", "Sponsored"));
                        } else if (resultSet.getString("SPONSORED").equalsIgnoreCase("false")) {
                            statuses.add(resultSet.getString("SPONSORED").replace("FALSE", "Organic"));
                        } else {
                            Assert.fail("Sponsored is not true or false. Sponsored: " + resultSet.getString("SPONSORED"));
                        }
                    } else {
                        Assert.fail("Placement is not " + placementType);
                    }
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return statuses;
    }

    public Map<String, String> getSearchRankRanksFromDB(String startDate, String endDate, String retailerPlatform, String searchTerm, String RPC, String placementType) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        Map<String, String> rankMap = new HashMap<>();
        List<String> bestRanks = new ArrayList<>();
        double sum = 0.0;
        int count = 0;
        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SearchRankQueries.queryToFetchSearchRankStatuses(startDate, endDate, retailerPlatform, searchTerm, RPC, placementType))) {
                while (resultSet.next()) {
                    String bestRankInDB = resultSet.getString("BEST_RANK");
                    bestRanks.add(bestRankInDB);

                    String averageRankInDB = resultSet.getString("AVERAGE_RANK");
                    double averageRank = Double.parseDouble(averageRankInDB);
                    sum += averageRank;
                    count++;
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
            if (count > 0) {
                double average = sum / count;
                long roundedAverage = Math.round(average);
                rankMap.put("Average Rank", String.valueOf(roundedAverage));
            }
            String bestRankInDB = Collections.min(bestRanks);
            rankMap.put("Best Rank", bestRankInDB);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rankMap;
    }

    private void compareStatusesFromUIAndDB(String startDate, String endDate, String searchTerm, String rpc, String placementType) {
        Set<String> statusesFromUI = searchRankPage.getStatusesFromUI();
        LOGGER.info("Statuses from UI: " + statusesFromUI);

        List<String> statusesFromDB = getSearchRankStatusesFromDB(startDate, endDate, "amazon.com", searchTerm, rpc, placementType);
        LOGGER.info("Statuses from DB: " + statusesFromDB);

        Assert.assertEquals(statusesFromUI.size(), statusesFromDB.size(), "Statuses from UI and DB are not equal");
        Assert.assertEqualsNoOrder(statusesFromUI.toArray(), statusesFromDB.toArray(), "Statuses from UI and DB are not equal. UI: " + statusesFromUI + " DB: " + statusesFromDB);
        LOGGER.info("Statuses from UI and DB are the same");
    }

    private void compareRanksFromUIAndDB(String startDate, String endDate, String searchTerm, String rpc, String placementType) {
        String bestRankFromUI = searchRankPage.getRankTextFromUI("bestRank");
        String averageRankFromUI = searchRankPage.getRankTextFromUI("averageRank");
        LOGGER.info("Best Rank from UI: " + bestRankFromUI);
        LOGGER.info("Average Rank from UI: " + averageRankFromUI);

        Map<String, String> ranksFromDB = getSearchRankRanksFromDB(startDate, endDate, "amazon.com", searchTerm, rpc, placementType);
        LOGGER.info("Ranks from DB: " + ranksFromDB);

        Assert.assertEquals(averageRankFromUI, ranksFromDB.get("Average Rank"), "Average Rank from UI and DB are not equal");
        Assert.assertEquals(bestRankFromUI, ranksFromDB.get("Best Rank"), "Best Rank from UI and DB are not equal");
        LOGGER.info("Best Rank and Average Rank from UI and DB are the same");
    }

}