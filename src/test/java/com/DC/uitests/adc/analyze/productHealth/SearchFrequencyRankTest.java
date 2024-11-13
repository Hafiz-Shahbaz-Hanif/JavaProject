package com.DC.uitests.adc.analyze.productHealth;

import com.DC.db.analyze.SearchFrequencyRankQueries;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.productHealth.SearchFrequencyRankPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.SnowflakeUtility;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public class SearchFrequencyRankTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private SearchFrequencyRankPage searchFrequencyRankPage;
    private AppHomepage appHomepage;

    private SoftAssert softAssert = new SoftAssert();

    @BeforeMethod
    public void setupTestMethodForNetNew(final ITestContext testContext, ITestResult tr) throws InterruptedException {
        testMethodName.set(tr.getMethod().getMethodName());
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName + " ***************");
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu navigationMenu = new NetNewNavigationMenu(driver);
        navigationMenu.selectBU("McCormick US");

        appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Identify");
        appHomepage.clickLink("Search Frequency Rank");
        searchFrequencyRankPage = new SearchFrequencyRankPage(driver);
        searchFrequencyRankPage.closeInformationalPopUp("Search Frequency Rank");
    }

    @Test(description = "Verify Display of Search Frequency Rank Screen")
    public void SFR_SearchFrequencyScreenIsDisplayed() {

        String currentUrl = searchFrequencyRankPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("search-frequency-rank"), "Search Frequency Rank Screen is not displayed");
    }

    @Test(description = "Verify that the Date picker is displayed and date range by default is Last 13 Weeks")
    public void SFR_SearchFrequencySingleDayAndDateRangeCanBeSelected() {

        softAssert.assertTrue(searchFrequencyRankPage.dateAndIntervalPickerPage.isDateSelectionDisplayed(), "Date Selection is not displayed");

        String defaultDateValue = searchFrequencyRankPage.dateAndIntervalPickerPage.getDefaultDateSelection();

        String expectedStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastThirteenWeeks());
        String expectedEndDate = DateUtility.formattingDate(DateUtility.getLastDayOfLastThirteenWeeks());

        softAssert.assertEquals(defaultDateValue, expectedStartDate + " - " + expectedEndDate, "Default Date Range is not Last 13 Weeks");
        softAssert.assertAll();
    }

    @Test(description = "Verify that the user can select a single date")
    public void SFR_UserCanSelectSingleDate() throws InterruptedException {

        String expectedDate = DateUtility.getFirstDayOfThisMonth();
        searchFrequencyRankPage.dateAndIntervalPickerPage.selectSingleDate();
        List<String> actualDate = searchFrequencyRankPage.getDateLabelsFromSFRChart();
        softAssert.assertTrue(actualDate.size() == 1);
        softAssert.assertEquals(actualDate.get(0), expectedDate, "Selected date is not displayed");
    }

    @Test(description = "Verify that the user can select each date range")
    public void SFR_UserCanSelectDateRange() throws InterruptedException {
        List<String> expectedDateRanges = searchFrequencyRankPage.dateAndIntervalPickerPage.getExpectedDateRangesForWeeklyInterval();
        List<String> selectedDateRanges = searchFrequencyRankPage.dateAndIntervalPickerPage.selectDateRangeForWeeklyInterval();

        Assert.assertEquals(selectedDateRanges, expectedDateRanges, "Selected date ranges are not correct");
    }

    @Test(description = "Verify left side filters collapse after clicking on Filter icon")
    public void SFR_LeftSideFiltersCollapse() throws Exception {

        Assert.assertFalse(searchFrequencyRankPage.dcFilters.verifyCollapseOfLeftSideFiltersSFR("retailer", "department", "metrics-displayed"), "The left side filters are not collapsed after clicking on Filter icon");

    }

    @Test(description = "Verify left side filters expanded after clicking on Filter icon")
    public void SFR_AllLeftSideFiltersAndButtonsArePresent() throws Exception {

        searchFrequencyRankPage.dcFilters.collapseFilters();
        Assert.assertTrue(searchFrequencyRankPage.dcFilters.verifyPresenceOfAllLeftSideFiltersSFR("retailer", "metrics-displayed"), "Not all the filters are displayed.");
    }

    @Test(description = "Verify that user can select a single retailer")
    public void SFR_SingleRetailerSelectionIsWorking() throws Exception {

        searchFrequencyRankPage.dcFilters.clickCancelButton();
        softAssert.assertTrue(searchFrequencyRankPage.isSearchFrequencyRankSectionNotDisplayed(), "The Search Frequency Rank page is Displayed.");

        String retailerToSelect = "Amazon";
        searchFrequencyRankPage.dcFilters.selectRetailer(retailerToSelect);
        softAssert.assertTrue(searchFrequencyRankPage.dcFilters.verifySFRRetailerSelection(retailerToSelect), "The single retailer selection is not working.");
        softAssert.assertAll();
    }

    @Test(description = "Verify that user can select multiple Metrics")
    public void SFR_MultipleMetricsSelectionIsWorking() throws Exception {
        //int numOfMetricsSelected = searchFrequencyRankPage.dcFilters.selectMultipleMetrics("Search Frequency Rank", "Conversion Share", "Click Share");
        List<String> metricsInFilter = searchFrequencyRankPage.dcFilters.getMetricsSelected();
        softAssert.assertEquals(metricsInFilter.size(), 3, "Number of Metrics selected is not correct");
        searchFrequencyRankPage.verifySearchFunctionality("serums for skin care");
        ArrayList<String> uiData = new ArrayList<>();

        for (int i = 0; i < searchFrequencyRankPage.getValuesFromSFRChart().size(); i++) {
            uiData.add(searchFrequencyRankPage.getValuesFromSFRChart().get(i).replace(",", ""));
        }
        LOGGER.info("UI Data is " + uiData);

        String startDate = DateUtility.getFirstDayOfLastThirteenWeeks();
        String endDate = DateUtility.getLastDayOfLastThirteenWeeks();

        ArrayList<String> dbData = getSFRValuesFromDB(startDate, endDate, searchFrequencyRankPage.getSelectedSearchTerm(), "amazon.com");
        softAssert.assertTrue(uiData.equals(dbData), "UI and DB data are not equal");
    }

    @Test(description = "Verify Interval Selection is displayed and Weekly by default")
    public void SFR_IntervalSelectionIsDisplayedAndWeeklyByDefault() {

        softAssert.assertTrue(searchFrequencyRankPage.dateAndIntervalPickerPage.isIntervalSelectionDisplayed(), "The interval selection is not displayed");

        String defaultIntervalValue = searchFrequencyRankPage.dateAndIntervalPickerPage.getDefaultIntervalSelection();
        softAssert.assertEquals(defaultIntervalValue, "Weekly", "Default interval selection is not set to Weekly.");
        softAssert.assertAll();

    }

    @Test(description = "Verify that the interval dropdown has options: Weekly, Monthly")
    public void SFR_IntervalDropdownHasRequiredOptions() throws Exception {

        List<String> expectedIntervalOptions = Arrays.asList("Weekly", "Monthly", "Daily");

        Assert.assertTrue(intervalDropdownVerification(expectedIntervalOptions), "The interval dropdown does not have required options");
        searchFrequencyRankPage.dateAndIntervalPickerPage.clickMonthlyIntervalDropdown();

    }

    @Test(description = "Verify that First Metrics Heading: Search Frequency Rank and download icon")
    public void SFR_FirstMetricsHeadingDisplayed() {

        verifyFirstMetricsTitle();
        LOGGER.info("SFR title is correct.");
        Assert.assertTrue(searchFrequencyRankPage.verifyDisplayOfDownloadIconSFR(), "The Download Icon is not displayed.");

    }

    @Test(description = "Verify that Second Metrics Heading: Click Share & Conversion Share - Paper Towels")
    public void SFR_SecondMetricsHeadingDisplayed() throws InterruptedException {

        String searchTermToSelect = "christmas tree decorations";
        searchFrequencyRankPage.dateAndIntervalPickerPage.selectInterval("Weekly");
        searchFrequencyRankPage.dateAndIntervalPickerPage.selectDateRange("Last 13 Weeks");
        searchFrequencyRankPage.selectSearchTermInSearchBar(searchTermToSelect);
        verifySecondMetricsTitle(searchTermToSelect);
        LOGGER.info("Click & Conversion title is correct.");
        Assert.assertTrue(searchFrequencyRankPage.verifyDisplayOfDownloadIconClickConversion(), "The Download Icon is not displayed.");

        //Check for the data verification scenario here

    }

    @Test(description = "Verify Chart Labels toggle is displayed and working as intended")
    public void SFR_ChartLabelsToggleDisplayedAndWorkingAsIntended() throws Exception {

        Assert.assertTrue(searchFrequencyRankPage.verifyDisplayOfChartLabelsToggle(), "The Chart Labels toggle is not displayed.");
        LOGGER.info("Chart Labels Toggle is Displayed");

        searchFrequencyRankPage.toggleValidation(searchFrequencyRankPage.getChartLabelsToggle(), "Chart Labels");
        LOGGER.info("The Chart Labels toggle is displayed and working as intended");

    }

    @Test(description = "Verify Search bar is displayed and working as intended")
    public void SFR_SearchBarDisplayedAndWorkingAsIntended() throws Exception {

        Assert.assertTrue(searchFrequencyRankPage.verifySearchBarDisplayed(), "The Search Bar is not displayed.");
        LOGGER.info("Search Bar is Displayed");

        String searchItem = "botox face serum";
        searchFrequencyRankPage.verifySearchFunctionality(searchItem);
        verifySecondMetricsTitle(searchItem);
        LOGGER.info("Search Filter Title is correct.");

        //Data verification scenario here

    }

    @Test(description = "Verify that user can download file in all formats from SFR chart", dataProvider = "downloadOptions", dataProviderClass = SharedMethods.class)
    public void SFR_UserCanDownloadFileInAllFormatsFromSFR(String downloadOption, String expectedExtension) {
        Assert.assertTrue(searchFrequencyRankPage.isSFRExportIconDisplayed(), "SFR Export Icon is not displayed");
        try {
            searchFrequencyRankPage.clickOnSFRExportIcon();
            searchFrequencyRankPage.clickExportButton(downloadOption);
            String[] namesOfDownloadedFiles = {"chart", "Search Frequency Rank"};
            String downloadedFilePath = SharedMethods.checkDownloadsWithDifferentNames(namesOfDownloadedFiles, expectedExtension, 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");
            LOGGER.info("File is downloaded in " + expectedExtension + " format");
            searchFrequencyRankPage.hitEscKey();
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }

        //Verification on excel sheeet
    }

    @Test(description = "Verify that user can download file in all formats from Click and Conversion chart", dataProvider = "downloadOptions", dataProviderClass = SharedMethods.class)
    public void SFR_UserCanDownloadFileInAllFormatsFromClickAndConversion(String downloadOption, String expectedExtension) {
        Assert.assertTrue(searchFrequencyRankPage.isClickAndConversionExportIconDisplayed(), "Click And Conversion Export Icon is not displayed");
        try {
            searchFrequencyRankPage.clickOnClickConversionExportIcon();
            searchFrequencyRankPage.clickExportButton(downloadOption);
            String[] namesOfDownloadedFiles = {"chart", "Click Share & Conversion Share"};
            String downloadedFilePath = SharedMethods.checkDownloadsWithDifferentNames(namesOfDownloadedFiles, expectedExtension, 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");
            LOGGER.info("File is downloaded in " + expectedExtension + " format");
            searchFrequencyRankPage.hitEscKey();
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(description = "Verify Display of Search Volume Screen")
    public void SFR_SearchVolumeScreenIsDisplayed() throws InterruptedException {
        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        LOGGER.info("Search Volume Tab is Clicked");
        Assert.assertTrue(searchFrequencyRankPage.isSearchVolumeScreenDisplayed(), "Search Volume Header is not displayed");
    }


    @Test(description = "Verify left side filters collapse after clicking on Filter icon on Search Volume Screen")
    public void SearchVolume_LeftSideFiltersCollapse() throws Exception {
        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        Assert.assertFalse(searchFrequencyRankPage.dcFilters.verifyCollapseOfLeftSideFiltersSearchVolume("retailer"), "The left side filters are not collapsed after clicking on Filter icon");

    }

    @Test(description = "Verify left side filters collapse after clicking on Filter icon on Search Volume Screen")
    public void SearchVolume_AllLeftSideFiltersAndButtonsArePresent() throws Exception {
        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        searchFrequencyRankPage.dcFilters.collapseFilters();
        Assert.assertTrue(searchFrequencyRankPage.dcFilters.verifyPresenceOfAllLeftSideFiltersSearchVolume("retailer"), "Not all the filters are displayed.");
    }

    @Test(description = "Verify that Heading: Search Volume and download icon are Displayed")
    public void SearchVolumeHeadingAndExportIconDisplayed() throws InterruptedException {
        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        verifySearchVolumeTitle();
        LOGGER.info("SFR title is correct.");
        Assert.assertTrue(searchFrequencyRankPage.verifyDisplayOfDownloadIconSearchVolume(), "The Download Icon is not displayed.");

    }

    @Test(description = "Verify Chart Labels toggle is displayed and working as intended on Search Volume Screen")
    public void SearchVolume_ChartLabelsToggleDisplayedAndWorkingAsIntended() throws Exception {

        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        Assert.assertTrue(searchFrequencyRankPage.verifyDisplayOfChartLabelsToggle(), "The Chart Labels toggle is not displayed.");
        LOGGER.info("Chart Labels Toggle is Displayed");

        searchFrequencyRankPage.toggleValidation(searchFrequencyRankPage.getChartLabelsToggle(), "Chart Labels");
        LOGGER.info("The Chart Labels toggle is displayed and working as intended");

    }

    @Test(description = "Verify Search bar is displayed On Search Volume Screen")
    public void SearchVolume_SearchBarDisplayedAndWorkingAsIntended() throws Exception {

        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        Assert.assertTrue(searchFrequencyRankPage.verifyVolumeTabSearchBarDisplayed(), "The Search Bar is not displayed On Search Volume Screen.");
        LOGGER.info("Search Bar is Displayed");

    }

    @Test(description = "Verify that user can download file in all formats from Search Volume chart", dataProvider = "downloadOptions", dataProviderClass = SharedMethods.class)
    public void SearchVolume_UserCanDownloadFileInAllFormatsFromSearchVolume(String downloadOption, String expectedExtension) throws InterruptedException {
        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        Assert.assertTrue(searchFrequencyRankPage.isSearchVolumeExportIconDisplayed(), "Search Volume Export Icon is not displayed");
        try {
            searchFrequencyRankPage.clickOnSearchVolumeExportIcon();
            searchFrequencyRankPage.clickExportButton(downloadOption);
            String[] namesOfDownloadedFiles = {"chart", "Search Volume"};
            String downloadedFilePath = SharedMethods.checkDownloadsWithDifferentNames(namesOfDownloadedFiles, expectedExtension, 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");
            LOGGER.info("File is downloaded in " + expectedExtension + " format");
            searchFrequencyRankPage.hitEscKey();
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }

        //Data verification on excel sheet
    }

    @Test(description = "Verify that after clicking SFR link on Home Page user is redirected to the SFR page")
    public void SFR_ClickingHomePageLinkRedirectsUserToHomePage() throws InterruptedException {

        AppHomepage appHomepage = searchFrequencyRankPage.clickFWLogo();
        LOGGER.info("After clicking HomePage link user is redirected to the Home Page");
        appHomepage.clickOnSection("Identify");
        appHomepage.clickLink("Search Frequency Rank");
        Assert.assertTrue(searchFrequencyRankPage.verifyDisplayOfCentralSFRScreen(), "The Share of Voice page is not displayed.");
        String currentUrl = searchFrequencyRankPage.getCurrentUrl();
        LOGGER.info("Current page url: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("search-frequency-rank"), "Page url does not contain search-frequency-rank");
    }

    @Test(description = "Verify that the Date picker is displayed and date range by default is Last 13 Weeks on Search Volume Screen")
    public void SearchVolumeSingleDayAndDateRangeCanBeSelected() throws InterruptedException {

        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        Assert.assertTrue(searchFrequencyRankPage.dateAndIntervalPickerPage.isDateSelectionDisplayed(), "Date Selection is not displayed");

        String defaultDateValue = searchFrequencyRankPage.dateAndIntervalPickerPage.getDefaultDateSelection();

        String expectedStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastThirteenWeeks());
        String expectedEndDate = DateUtility.formattingDate(DateUtility.getLastDayOfLastThirteenWeeks());

        Assert.assertEquals(defaultDateValue, expectedStartDate + " - " + expectedEndDate, "Default Date Range is not Last 13 Weeks");
    }

    @Test(description = "Verify that the user can select a single date on Search Volume Screen")
    public void SearchVolume_UserCanSelectSingleDate() throws InterruptedException {
        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        String expectedDate = DateUtility.calculateWeekRange("06/01/2023");
        searchFrequencyRankPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "June", 1, 1);
        String actualDate = searchFrequencyRankPage.dateAndIntervalPickerPage.getSelectedDate();
        Assert.assertEquals(actualDate, expectedDate, "Selected date is not displayed");
    }

    @Test(description = "Verify that the user can select each date range on Search Volume Screen")
    public void SearchVolume_UserCanSelectDateRange() throws InterruptedException {
        searchFrequencyRankPage.verifySearchVolumeTabIsCickable();
        List<String> expectedDateRanges = searchFrequencyRankPage.dateAndIntervalPickerPage.getExpectedDateRangesForWeeklyInterval();
        List<String> selectedDateRanges = searchFrequencyRankPage.dateAndIntervalPickerPage.selectDateRangeForWeeklyInterval();

        Assert.assertEquals(selectedDateRanges, expectedDateRanges, "Selected date ranges are not correct");
    }

    public boolean intervalDropdownVerification(List<String> expectedIntervalOptions) throws InterruptedException {

        List<String> actualIntervalOptions = searchFrequencyRankPage.dateAndIntervalPickerPage.getIntervalDropdownOptions();

        Assert.assertEqualsNoOrder(actualIntervalOptions.toArray(), expectedIntervalOptions.toArray(),
                "Interval options do not match expected options" +
                        "\nExpected:\n" + expectedIntervalOptions +
                        "\nActual:\n" + actualIntervalOptions
        );

        return true;
    }

    public void verifyFirstMetricsTitle() {
        String expectedTitle = "Search Frequency Rank";
        String actualTitle = searchFrequencyRankPage.getFirstMetricsTitle();

        Assert.assertEquals(actualTitle, expectedTitle, "First Metrics title is not correct");
    }

    public void verifySecondMetricsTitle(String searchTerm) {
        String expectedTitle = "Click Share & Conversion Share - " + searchTerm;
        String actualTitle = searchFrequencyRankPage.getSecondMetricsTitle();

        Assert.assertEquals(actualTitle, expectedTitle, "Second Metrics title is not correct");
    }

    public void verifySearchVolumeTitle() {
        String expectedTitle = "Search Volume";
        String actualTitle = searchFrequencyRankPage.getSearchVolumeTitle();

        Assert.assertEquals(actualTitle, expectedTitle, "Search Volume title is not correct");
    }

    public Map<String, Map<String, String>> getSFRMappedValuesFromDB(String startDate, String endDate, String searchTerm, String retailerPlatform) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        Map<String, Map<String, String>> statusMapFromDB = new HashMap<>();
        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SearchFrequencyRankQueries.queryToFetchSFRValues(startDate, endDate, searchTerm, retailerPlatform))) {
                while (resultSet.next()) {
                    String date = resultSet.getString("DATE_KEY");
                    String queriedSearchTerm = resultSet.getString("SEARCH_TERM");
                    String sfrValues = resultSet.getString("SEARCH_FREQUENCY_RANK");
                    statusMapFromDB.putIfAbsent(date, new HashMap<>());
                    statusMapFromDB.get(date).put(sfrValues, queriedSearchTerm);
                }
                statusMapFromDB = statusMapFromDB.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            } catch (SQLException e) {
                LOGGER.error("Exception running the query. Exception: " + e.getMessage());
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Items from DB: " + statusMapFromDB);
        return statusMapFromDB;
    }

    public ArrayList<String> getSFRValuesFromDB(String startDate, String endDate, String searchTerm, String retailerPlatform) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        ArrayList<String> sfrValuesFromDB = new ArrayList<>();
        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SearchFrequencyRankQueries.queryToFetchSFRValues(startDate, endDate, searchTerm, retailerPlatform))) {
                while (resultSet.next()) {
                    String sfrValues = resultSet.getString("SEARCH_FREQUENCY_RANK");
                    sfrValuesFromDB.add(sfrValues);
                }
            } catch (SQLException e) {
                LOGGER.error("Exception running the query. Exception: " + e.getMessage());
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Items from DB: " + sfrValuesFromDB);
        return sfrValuesFromDB;
    }

    public static void compareData(Map<String, Map<String, String>> uiData, Map<String, Map<String, String>> dbData) {

        int uiSfrItems = 0;
        for (Map<String, String> dateItemStatusMap : uiData.values()) {
            uiSfrItems += dateItemStatusMap.size();
        }

        int dbSfrItems = 0;
        for (Map<String, String> dateItemStatusMap : dbData.values()) {
            dbSfrItems += dateItemStatusMap.size();
        }

        LOGGER.info("Number of items in UI: " + uiSfrItems + " Number of items in DB: " + dbSfrItems);
        Assert.assertEquals(uiSfrItems, dbSfrItems, "Number of items in UI and DB is not equal");

        /*for (String date : uiData.keySet()) {
            if (dbData.containsKey(date)) {
                Map<String, String> uiItemStatuses = uiData.get(date);
                Map<String, String> dbItemStatuses = dbData.get(date);

                for (String itemId : uiItemStatuses.keySet()) {
                    if (dbItemStatuses.containsKey(itemId)) {
                        String uiStatus = uiItemStatuses.get(itemId);
                        String dbStatus = dbItemStatuses.get(itemId);

                        Assert.assertEquals(uiStatus, dbStatus, "Status for item " + itemId + " on date " + date + " does not match. UI status: " + uiStatus + ", DB status: " + dbStatus);
                    } else {
                        Assert.fail("Item " + itemId + " not found in DB data for date " + date);
                    }
                }
            } else {
                Assert.fail("Date " + date + " not found in DB data");
            }
        }*/

        LOGGER.info("UI and DB data match");
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

}
