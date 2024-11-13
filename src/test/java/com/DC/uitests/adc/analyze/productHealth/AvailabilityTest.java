package com.DC.uitests.adc.analyze.productHealth;

import com.DC.db.analyze.AvailabilityQueries;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.analyze.productHealth.AvailabilityPage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
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
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


public class AvailabilityTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private AvailabilityPage availabilityPage;
    private AppHomepage appHomepage;

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
        appHomepage.clickOnSection("Analyze");
        appHomepage.clickLink("Availability");
        availabilityPage = new AvailabilityPage(driver);
        availabilityPage.closeInformationalPopUp("Availability");
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify Display of Central Availability Screen")
    public void Availability_AvailabilityScreenIsDisplayed() {

        Assert.assertTrue(availabilityPage.isAvailabilityScreenDisplayed(), "Availability Screen is not displayed");
        String currentUrl = availabilityPage.getCurrentUrl();
        LOGGER.info("Current URL is: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("availability"), "Availability Screen is not displayed");
    }

    @Test(description = "Verify that Date Range selection is set to 14 Days by default")
    public void Availability_DateRangeIsSetTo14DaysByDefault() {

        Assert.assertTrue(availabilityPage.dateAndIntervalPickerPage.isDateSelectionDisplayed(), "Date Selection is not displayed");

        String defaultDateValue = availabilityPage.dateAndIntervalPickerPage.getDefaultDateSelection();

        String expectedStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastFourteenDays());
        String expectedEndDate = DateUtility.formattingDate(DateUtility.getYesterday());

        LOGGER.info("Default Date Range is: " + defaultDateValue);
        LOGGER.info("Expected Date Range is: " + expectedStartDate + " - " + expectedEndDate);
        Assert.assertEquals(defaultDateValue, expectedStartDate + " - " + expectedEndDate, "Default Date Range is not Last 14 Days");
    }

    @Test(description = "Verify that Available For Purchase, Limited Availability and Unavailable For Purchase are displayed")
    public void Availability_AllOverallScoresAreDisplayed() {

        Assert.assertTrue(availabilityPage.isAllThreeHeadersDisplayed(), "All Three Headers are not displayed");
        Assert.assertTrue(availabilityPage.isAllTheChartsDisplayed(), "All Three Charts are not displayed");
    }

    @Test(description = "Verify that Available For Purchase bucket has a correct color based on target goal %")
    public void Availability_AvailableForPurchaseBucketHasCorrectColor() throws InterruptedException {

        availabilityPage.changeGoalSettings("Success is", "50");
        availabilityPage.changeGoalSettings("Failure is", "25");
        Assert.assertTrue(availabilityPage.isAvailableForPurchaseChartColorChangeBasedOnPerformance(25, 50), "Available For Purchase bucket does not have correct color");
    }

    @Test(description = "Verify that user can edit Success Goal field directly in the UI")
    public void Availability_UserCanEditSuccessGoalField() throws InterruptedException {

        availabilityPage.changeGoalSettings("Failure is", "25");
        Assert.assertTrue(availabilityPage.isEditGoalIconDisplayed(), "Edit Goal Icon is not displayed");
        availabilityPage.changeGoalSettings("Success is", "70");
        Assert.assertTrue(availabilityPage.isSuccessGoalChanged(), "Goal is not changed");
        int successGoalAfterEditing = availabilityPage.getNewGoalValue();

        Assert.assertTrue(availabilityPage.isAvailableForPurchaseChartColorChangeBasedOnPerformance(25, successGoalAfterEditing), "Available For Purchase bucket does not have correct color");
    }

    @Test(description = "Verify that user can edit Failure Goal field directly in the UI")
    public void Availability_UserCanEditFailureGoalField() throws InterruptedException {

        availabilityPage.changeGoalSettings("Failure is", "60");
        Assert.assertTrue(availabilityPage.isAvailableForPurchaseChartColorChangeBasedOnPerformance(60, 70), "Available For Purchase bucket does not have correct color");
    }

    @Test(description = "Verify that Title: Availability by Status, Group by dropdown, Toggle, Chart Labels and Export button are displayed")
    public void Availability_AllTheRequiredFieldsAreDisplayed() throws InterruptedException {

        Assert.assertTrue(availabilityPage.isAllRequiredFieldsDisplayed(), "All Required Fields are not displayed");
        Assert.assertTrue(availabilityPage.isAllTheFieldsInGroupByDropdownDisplayed(), "All Fields in Group By Dropdown are not displayed");
        LOGGER.info("All Required Fields are displayed");
        availabilityPage.clickByCategoryInGroupByDropdown();
    }

    @Test(description = "Verify that default value is set to 50 SKUs")
    public void Availability_DefaultValueIsSetTo50SKUs() {

        availabilityPage.scrollToBottomOfPage();
        Assert.assertEquals(availabilityPage.commonFeatures.getDefaultNumberOfTermsDisplayed(), "50", "Default Number of SKUs is not 50");
        LOGGER.info("Default Number of SKUs is " + availabilityPage.commonFeatures.getDefaultNumberOfTermsDisplayed());
    }

    @Test(description = "Verify that SKU table has all correct fields and buttons")
    public void Availability_SKUTableHasAllCorrectFieldsAndButtons() throws InterruptedException {

        Assert.assertTrue(availabilityPage.isAllRequiredSKUFieldsDisplayed(), "All Fields in SKU Table are not displayed");
        List<String> expectedOptions = Arrays.asList("Image", "Product Title", "RPC", "Retailer", "Average in Stock Availability %");
        Assert.assertEqualsNoOrder(availabilityPage.getAllOptionsInMoreOptionsDropdown().toArray(), expectedOptions.toArray(), "All Options in More Options Dropdown are not displayed");
        LOGGER.info("All Fields in SKU Table are displayed");
        Assert.assertTrue(availabilityPage.isAllOptionsInMoreOptionsDropdownCheckedByDefault(), "All Options in More Options Dropdown are not checked by default");
        availabilityPage.hitEscKey();
        LOGGER.info("All Options in More Options Dropdown are checked by default");
    }

    @Test(description = "Verify that user can deselect options in More Options dropdown and they are not displayed in SKU table")
    public void Availability_UserCanDeselectOptionsInMoreOptionsDropdown() throws InterruptedException {

        availabilityPage.commonFeatures.deselectOptionInMoreOptionsDropdown("Product Title");
        availabilityPage.hitEscKey();
    }

    @Test(description = "Verify that By default, no catalog filters will be selected when a user lands on the report page")
    public void Availability_ByDefaultNoCatalogFiltersWillBeSelected() {

        availabilityPage.scrollToTopOfPage();
        Assert.assertTrue(availabilityPage.dcFilters.isFilterEmpty("retailer"), "Retailer filter is not empty by default");
        Assert.assertTrue(availabilityPage.dcFilters.isFilterEmpty("brand"), "Brand filter is not empty by default");
        LOGGER.info("All Catalog Filters are empty by default");
    }

    @Test(description = "Verify that color of the sell aligns to it's status")
    public void Availability_UserCanSelectFiltersInCatalogFilters() throws InterruptedException, ParseException {
        String retailerToSelect = "Amazon";
        String brandToSelect = "mccormick bag n season";
        availabilityPage.dcFilters.selectRetailer(retailerToSelect);
        availabilityPage.dcFilters.selectBrand(brandToSelect);
        availabilityPage.dcFilters.apply();
        availabilityPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 16, 20);
        Map<String, Map<String, String>> uiData = availabilityPage.getItemsFromUITable();
        Map<String, Map<String, String>> dbData = getItemsFromDB("McCormick US", "08/16/2023", "08/20/2023", "amazon.com", brandToSelect);

        compareData(uiData, dbData);
        LOGGER.info("Colors of the buckets are correct. Data in UI table matches data in DB");
    }

    @Test(description = "Verify that sorting within the SKU table has all required options")
    public void Availability_SortingWithinSKUTableHasAllRequiredOptions() throws InterruptedException {

        Assert.assertEqualsNoOrder(availabilityPage.getAllSortingDropdownOptions().toArray(), getExpectedOptionsInSortingDropdown().toArray(), "All Required Options in Sorting Dropdown are not displayed");
        availabilityPage.hitEscKey();
        LOGGER.info("All Required Options in Sorting Dropdown are displayed");
    }

    @Test(description = "Verify that all required options appear after clicking on Export button in Availability table")
    public void Availability_AllRequiredOptionsAppearAfterClickingOnExportButton() throws Exception {

        availabilityPage.clickAvailabilityExportIcon();
        Assert.assertEqualsNoOrder(availabilityPage.getAllOptionsInExportDropdown().toArray(), getExpectedOptionsInExportDropdown().toArray(), "All Required Options in Export Dropdown are not displayed");
        availabilityPage.hitEscKey();
        LOGGER.info("All Required Options in Export Dropdown are displayed");
    }

    @Test(description = "Verify that user can download file in all formats", dataProvider = "downloadOptions", dataProviderClass = SharedMethods.class)
    public void Availability_TestFileDownload(String downloadOption, String expectedExtension) {
        try {
            availabilityPage.clickAvailabilityExportIcon();
            availabilityPage.clickExportButton(downloadOption);
            String[] namesOfDownloadedFiles = {"chart", "Availability"};
            String downloadedFilePath = SharedMethods.checkDownloadsWithDifferentNames(namesOfDownloadedFiles, expectedExtension, 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");
            LOGGER.info("File is downloaded in " + expectedExtension + " format");
            availabilityPage.hitEscKey();
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(description = "Verify that default date selection mirrors the date selection on the main page")
    public void Availability_DefaultDateSelectionMirrorsTheDateSelectionOnTheMainPage() throws InterruptedException {

        availabilityPage.clickProductsExportIcon();
        Assert.assertTrue(availabilityPage.commonFeatures.isExportHeaderDisplayed(), "The Export Header is not displayed.");
        Assert.assertTrue(availabilityPage.commonFeatures.isExportButtonInExportWindowDisplayed(), "The Export button in Export window is not displayed.");
        Assert.assertTrue(availabilityPage.dateAndIntervalPickerPage.isExportDateSelectionMirrorUI(), "The default date selection does not mirror the date selection on the main page");
    }

    @Test(description = "Verify that after clicking Availability link on Home Page user is redirected to the Availability page")
    public void Availability_UserIsRedirectedToAvailabilityPage() throws InterruptedException {
        appHomepage = availabilityPage.clickFWLogo();
        LOGGER.info("After clicking HomePage link user is redirected to the Home Page");
        appHomepage.clickOnSection("Analyze");
        appHomepage.clickLink("Availability");
        Assert.assertTrue(availabilityPage.isAvailabilityScreenDisplayed(), "User is not redirected to the Availability page");
        String currentUrl = availabilityPage.getCurrentUrl();
        LOGGER.info("Current page url: " + currentUrl);
        Assert.assertTrue(currentUrl.contains("availability"), "Availability Screen is not displayed");
    }

    @Test(description = "Verify that all 3 buckets display correct scores")
    public void Availability_PercentAvailableForPurchaseDisplaysCorrectScore() throws InterruptedException {
        String retailerToSelect = "Amazon";
        String brandToSelect = "golden dipt";
        String dateToSelect = "2023-09-10";
        availabilityPage.dcFilters.selectRetailer(retailerToSelect);
        availabilityPage.dcFilters.selectBrand(brandToSelect);
        availabilityPage.dcFilters.apply();
        availabilityPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Sept", 10, 10);

        String percentageFromUI;
        List<String> percentagesFromDB = getPercentageStatusesFromDB("McCormick US", dateToSelect, "amazon.com", brandToSelect);

        LOGGER.info("Checking Percent Available for Purchase score");
        percentageFromUI = availabilityPage.getValueOfAvailableForPurchaseChart().replace("%", "");
        String scoreValueInDB = percentagesFromDB.get(0);
        Assert.assertEquals(percentageFromUI, scoreValueInDB, "Percent Available for Purchase score is not correct");
        LOGGER.info("Percent Available for Purchase score is matching DB. Score in UI is: " + percentageFromUI + "%" + ", score in DB is: " + scoreValueInDB + "%");

        LOGGER.info("Checking Percent Limited Availability score");
        percentageFromUI = availabilityPage.getValueOfLimitedAvailabilityChart().replace("%", "");
        scoreValueInDB = percentagesFromDB.get(1);
        Assert.assertEquals(percentageFromUI, scoreValueInDB, "Percent Limited Availability score is not correct");
        LOGGER.info("Percent Limited Availability score is matching DB. Score in UI is: " + percentageFromUI + "%" + ", score in DB is: " + scoreValueInDB + "%");

        LOGGER.info("Checking Percent Unavailable for Purchase score");
        percentageFromUI = availabilityPage.getValueOfUnavailableForPurchaseChart().replace("%", "");
        scoreValueInDB = percentagesFromDB.get(2);
        Assert.assertEquals(percentageFromUI, scoreValueInDB, "Percent Unavailable for Purchase score is not correct");
        LOGGER.info("Percent Unavailable for Purchase score is matching DB. Score in UI is: " + percentageFromUI + "%" + ", score in DB is: " + scoreValueInDB + "%");

        int total = availabilityPage.isAllTheChartsSumUpTo100();
        if (total == 100 || total == 99 || total == 101) {
            Assert.assertTrue(true);
        } else {
            Assert.fail("The total of the 3 buckets is not 100% (or +-1)");
        }
    }

    @Test(description = "Verify that user can search for a product by RPC and retrieved results are matching DB")
    public void Availability_UserCanSearchForProductByRPC() throws InterruptedException {
        String rpcToSearch = "B000LLI";
        String retailerToSelect = "Amazon";
        availabilityPage.dcFilters.selectRetailer(retailerToSelect);
        availabilityPage.dcFilters.apply();
        availabilityPage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Aug", 16, 22);
        availabilityPage.commonFeatures.searchForText(rpcToSearch);
        List<String> itemsFoundInUI = availabilityPage.isProductDisplayedInSearchResults();
        List<String> itemsFoundInDB = getItemsFoundInDB("McCormick US", "2023-08-16", "2023-08-22", rpcToSearch);
        Assert.assertEquals(itemsFoundInUI.size(), itemsFoundInDB.size(), "The number of items found in UI is not matching the number of items found in DB");
        LOGGER.info("The number of items found in UI is matching. Number of items found in UI: " + itemsFoundInUI.size() + ", number of items found in DB: " + itemsFoundInDB.size());
        Assert.assertEquals(itemsFoundInUI, itemsFoundInDB, "The items found in UI are not matching the items found in DB");
        LOGGER.info("The items found in UI are matching the items found in DB");
    }

    public List<String> getExpectedOptionsInSortingDropdown() {

        List<String> expectedOptions = Arrays.asList("Select All", "Available to Purchase", "Limited Availability", "Unavailable to Purchase", "In Stock", "Limited Availability", "In Store Only", "Pre Order",
                "Not Released Yet", "Delayed Fulfillment", "Discontinued", "Lost Buy Box", "No Buy Box Offers", "Marketplace Only", "Void", "Out of Stock", "Andon Cord");
        LOGGER.info("Expected Options in Sorting Dropdown are " + expectedOptions);
        return expectedOptions;
    }

    public List<String> getExpectedOptionsInExportDropdown() {

        List<String> expectedOptions = Arrays.asList("Download PNG image", "Download PDF document", "Download CSV", "Download XLS");
        LOGGER.info("Expected Options in Export Dropdown are " + expectedOptions);
        return expectedOptions;
    }

    public List<String> getPercentageStatusesFromDB(String BU, String date, String retailer, String brand) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        List<String> mainBucketsPercentages = new ArrayList<>();
        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(AvailabilityQueries.queryToFetchAvailabilityPercentages(BU, date, retailer, brand))) {
                while (resultSet.next()) {
                    long inStockPercentage = resultSet.getLong("IN_PERCENTAGE");
                    long limitedAvailabilityPercentage = resultSet.getLong("LIMITED_PERCENTAGE");
                    long unavailablePercentage = resultSet.getLong("OUT_PERCENTAGE");
                    mainBucketsPercentages.add(String.valueOf(inStockPercentage));
                    mainBucketsPercentages.add(String.valueOf(limitedAvailabilityPercentage));
                    mainBucketsPercentages.add(String.valueOf(unavailablePercentage));
                    LOGGER.info(mainBucketsPercentages);
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
        return mainBucketsPercentages;
    }

    public Map<String, Map<String, String>> getItemsFromDB(String BU, String startDate, String endDate, String retailer, String brand) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        Map<String, Map<String, String>> statusMapFromDB = new HashMap<>();
        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(AvailabilityQueries.queryToFetchAvailabilityStatuses(BU, startDate, endDate, retailer, brand))) {
                while (resultSet.next()) {
                    String date = resultSet.getString("DATE_KEY");
                    String item = resultSet.getString("RPC");
                    String availabilityStatus = resultSet.getString("NORMALIZED_AVAILABILITY_STATUS");
                    statusMapFromDB.putIfAbsent(date, new HashMap<>());
                    statusMapFromDB.get(date).put(item, availabilityStatus);
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

    public static void compareData(Map<String, Map<String, String>> uiData, Map<String, Map<String, String>> dbData) {

        int uiTotalStatusItems = 0;
        for (Map<String, String> dateItemStatusMap : uiData.values()) {
            uiTotalStatusItems += dateItemStatusMap.size();
        }

        int dbTotalStatusItems = 0;
        for (Map<String, String> dateItemStatusMap : dbData.values()) {
            dbTotalStatusItems += dateItemStatusMap.size();
        }

        LOGGER.info("Number of tiles in UI: " + uiTotalStatusItems + " Number of tiles in DB: " + dbTotalStatusItems);
        Assert.assertEquals(uiTotalStatusItems, dbTotalStatusItems, "Number of tiles in UI and DB is not equal");

        for (String date : uiData.keySet()) {
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
        }

        LOGGER.info("UI and DB data match");
    }

    public List<String> getItemsFoundInDB(String BU, String startDate, String endDate, String rpcToSearch) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        List<String> itemsFoundInDB = new ArrayList<>();
        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(AvailabilityQueries.queryToFetchRPCsForAutoSearch(BU, startDate, endDate, rpcToSearch))) {
                while (resultSet.next()) {
                    String item = resultSet.getString("RPC");
                    itemsFoundInDB.add(item);
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
        Collections.sort(itemsFoundInDB);
        LOGGER.info("Items found in DB: " + itemsFoundInDB);
        return itemsFoundInDB;
    }
}