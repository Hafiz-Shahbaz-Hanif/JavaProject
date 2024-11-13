package com.DC.uitests.adc.analyze.productHealth;

import com.DC.constants.NetNewConstants;
import com.DC.db.analyze.PriceQueries;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.productHealth.PricePage;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class PriceTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private PricePage pricePage;
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
        appHomepage.clickLink("Price");
        pricePage = new PricePage(driver);
        pricePage.closeInformationalPopUp("Price");
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify that the Price page is displayed")
    public void Price_PriceScreenIsDisplayed() {
        Assert.assertEquals(driver.getCurrentUrl(), NetNewConstants.getReportsUrl("/analyze", "price/"), "Price page URL is not correct");
    }

    @Test(description = "Verify that the Date picker is displayed and date range by default is Last 14 Days")
    public void Price_DatePickerIsDisplayedAndUserCanSelectSingleDate() {
        Assert.assertTrue(pricePage.dateAndIntervalPickerPage.isDateSelectionDisplayed(), "Date Selection is not displayed");

        String defaultDateValue = pricePage.dateAndIntervalPickerPage.getDefaultDateSelection();

        String expectedStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastFourteenDays());
        String expectedEndDate = DateUtility.formattingDate(DateUtility.getYesterday());

        Assert.assertEquals(defaultDateValue, expectedStartDate + " - " + expectedEndDate, "Default Date Range is not Last 14 Days");
    }

    @Test(description = "Verify that the user can select a single date")
    public void Price_UserCanSelectSingleDate() throws InterruptedException {
        pricePage.dateAndIntervalPickerPage.selectDateRange("This Month");
        String expectedDate = DateUtility.formattingDate(DateUtility.getFirstDayOfThisMonth());
        String expectedDateText = expectedDate + " - " + expectedDate;
        pricePage.dateAndIntervalPickerPage.selectSingleDate();
        String actualDate = pricePage.dateAndIntervalPickerPage.getSelectedDate();
        Assert.assertEquals(actualDate, expectedDateText, "Selected date is not displayed");
    }

    //    @Test(description = "Verify that the user can select each date range")
    //    public void Price_UserCanSelectDateRange() throws InterruptedException {
    //        List<String> expectedDateRanges = pricePage.dateAndIntervalPickerPage.getExpectedDateRanges();
    //        List<String> selectedDateRanges = pricePage.dateAndIntervalPickerPage.selectDateRange();
    //
    //        Assert.assertEquals(selectedDateRanges, expectedDateRanges, "Selected date ranges are not correct");
    //    }

    @Test(description = "Verify that Products with Price Changes header is present and says Products with Price Changes")
    public void Price_ProductsWithPriceChangesHeaderIsPresent() {
        Assert.assertTrue(pricePage.isProductsWithPriceChangesHeaderDisplayed(), "Products with Price Changes header is not displayed");
        Assert.assertEquals(pricePage.getPriceScreenHeading("Products with Price Changes"), "Products with Price Changes", "Chart Heading is not Products with Price Changes");
    }

    @Test(description = "Verify that when user selected #, the graph should show the numeric values of data and when user selects % then graph should display data in percentage")
    public void Price_UserCanSelectPercentageOrNumeric() throws InterruptedException {
        Assert.assertTrue(pricePage.isValuePercentageToggleDisplayed(), "Value Percentage Toggle is not displayed");

        pricePage.dateAndIntervalPickerPage.selectDateRange("Last Month");
        pricePage.dateAndIntervalPickerPage.selectCustomDateRange(20, 26);

        pricePage.selectValuePercentageToggle("#");
        Assert.assertTrue(pricePage.isTableDataValid("#"), "Numeric values are not displayed");
        LOGGER.info("Numeric values are displayed");

        pricePage.selectValuePercentageToggle("%");
        Assert.assertTrue(pricePage.isTableDataValid("%"), "Percentage values are not displayed");

        LOGGER.info("Percentage values are displayed");
    }

    @Test(description = "Verify that Price change field is displayed, value by default is 0 and user can edit the value")
    public void Price_PriceChangeFieldIsDisplayedAndUserCanEditTheValue() {
        Assert.assertTrue(pricePage.isPriceChangeFieldDisplayed(), "Price Change Field is not displayed");

        Assert.assertTrue(pricePage.isPriceChangeFieldDefaultValueZero(), "Price Change Field default value is not 0");
        Assert.assertTrue(pricePage.isPriceChangeFieldEditable("10"), "Price Change Field is not editable");
    }

    @Test(description = "Verify that Price change Group by is present and set to By Brand by default")
    public void Price_PriceChangeGroupByIsPresentAndSetToByBrandByDefault() {
        Assert.assertTrue(pricePage.isPriceChangeGroupByDisplayed(), "Price Change Group By is not displayed");
        Assert.assertEquals(pricePage.getGroupByDefaultValue(pricePage.PRICE_CHANGE_GROUP_BY), "By Brand", "Price Change Group By default value is not By Brand");
    }

    @Test(description = "Verify that Heading of the chart is Average Selling Price")
    public void Price_HeadingOfTheChartShouldBeAverageSellingPrice() {
        Assert.assertTrue(pricePage.isAverageSellingPriceHeaderDisplayed(), "Average Selling Price header is not displayed");
        Assert.assertEquals(pricePage.getPriceScreenHeading("Average Selling Price"), "Average Selling Price", "Chart Heading is not Average Selling Price");
    }

    @Test(description = "Verify that Average Selling Price Group by is present and set to By Brand by default")
    public void Price_AverageSellingPriceGroupByIsPresentAndSetToByBrandByDefault() {
        Assert.assertTrue(pricePage.isAverageSellingPriceGroupByDisplayed(), "Average Selling Price Group By is not displayed");
        Assert.assertEquals(pricePage.getGroupByDefaultValue(pricePage.AVERAGE_SELLING_PRICE_GROUP_BY), "By Brand", "Average Selling Price Group By default value is not By Brand");
    }

    @Test(description = "Verify that Price table has all correct fields and buttons")
    public void Price_PriceTableHasAllCorrectFieldsAndButtons() {
        Assert.assertTrue(pricePage.isAllRequiredPriceTableButtonsDisplayed(), "Price Table Buttons are not displayed");
        Assert.assertTrue(pricePage.commonFeatures.getActualTableColumnNames().containsAll(getExpectedOptionsInPriceTable()), "Price Table Column Names are not correct");
    }

    @Test(description = "Verify that user can click on the cog icon to de-select columns")
    public void Price_UserCanClickOnTheCogIconToDeSelectColumns() throws InterruptedException {
        pricePage.commonFeatures.deselectOptionInMoreOptionsDropdown("Image", "% of Time on Promotion");
    }

    @Test(description = "Verify that prices in Price table are shown starting from most recent date selected in date picker")
    public void Price_PricesInPriceTableAreShownStartingFromMostRecentDate() throws InterruptedException {
        pricePage.dateAndIntervalPickerPage.selectDateRange("Last 14 Days");
        pricePage.scrollIntoView(pricePage.PRICE_TABLE_HEADER);
        Assert.assertTrue(pricePage.dateAndIntervalPickerPage.isDateColumnSortedCorrectly(), "Date column is not sorted correctly");
    }

    @Test(description = "Verify that user can download file in all formats from Price Change chart", dataProvider = "downloadOptions", dataProviderClass = SharedMethods.class)
    public void Price_UserCanDownloadFileInAllFormatsFromPriceChange(String downloadOption, String expectedExtension) {
        Assert.assertTrue(pricePage.isPriceChangeExportIconDisplayed(), "Price Change Export Icon is not displayed");
        try {
            pricePage.clickOnPriceChangeExportIcon();
            pricePage.clickExportButton(downloadOption);
            String[] namesOfDownloadedFiles = {"chart", "Price Change"};
            String downloadedFilePath = SharedMethods.checkDownloadsWithDifferentNames(namesOfDownloadedFiles, expectedExtension, 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");
            LOGGER.info("File is downloaded in " + expectedExtension + " format");
            pricePage.hitEscKey();
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(description = "Verify that user can download file in all formats from Average Selling Price chart", dataProvider = "downloadOptions", dataProviderClass = SharedMethods.class)
    public void Price_UserCanDownloadFileInAllFormatsFromAveragePrice(String downloadOption, String expectedExtension) {
        Assert.assertTrue(pricePage.isAverageSellingPriceExportIconDisplayed(), "Average Selling Price Export Icon is not displayed");
        try {
            pricePage.clickOnAverageSellingPriceExportIcon();
            pricePage.clickExportButton(downloadOption);
            String[] namesOfDownloadedFiles = {"chart", "Average Selling Price"};
            String downloadedFilePath = SharedMethods.checkDownloadsWithDifferentNames(namesOfDownloadedFiles, expectedExtension, 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");
            LOGGER.info("File is downloaded in " + expectedExtension + " format");
            pricePage.hitEscKey();
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test(description = "Verify that RPC data is clickable on hover and open into the product URL on the website")
    public void Price_RPCDataIsClickableOnHoverAndOpenIntoTheProductURLOnTheWebsite() {
        Assert.assertTrue(pricePage.commonFeatures.areOptionsRedirectedToSameRPCUrl(), "Clicking on RPC in a table does not redirect the product URL on the website");
    }

    @Test(description = "Verify that sorting within the Price table has all required options")
    public void Price_SortingWithinThePriceTableHasAllRequiredOptions() throws InterruptedException {
        Assert.assertEqualsNoOrder(pricePage.getAllSortingDropdownOptions().toArray(), getExpectedOptionsInSortingDropdown().toArray(), "All Required Options in Sorting Dropdown are not displayed");
        pricePage.hitEscKey();
        LOGGER.info("All Required Options in Sorting Dropdown are displayed");
    }

    @Test(description = "Verify that color of the sell aligns to it's status")
    public void Price_UserCanSelectFiltersInCatalogFilters() throws InterruptedException, ParseException {
        String retailerToSelect = "Amazon";
        String brandToSelect = "mccormick bag n season";
        pricePage.dcFilters.selectRetailer(retailerToSelect);
        pricePage.dcFilters.selectBrand(brandToSelect);
        pricePage.dcFilters.apply();
        pricePage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Sept", 16, 20);
        Map<String, Map<String, String>> uiData = pricePage.getItemsFromUITable();
        Map<String, Map<String, String>> dbData = getItemsFromDB("McCormick US", "09/16/2023", "09/20/2023", "amazon.com", brandToSelect);

        compareData(uiData, dbData);
        LOGGER.info("Data in UI table matches data in DB");
    }

    @Test(description = "Verify that user can search for a product by RPC and retrieved results are matching DB")
    public void Price_UserCanSearchForProductByRPC() throws InterruptedException {
        String rpcToSearch = "B000LLI";
        String retailerToSelect = "Amazon";
        pricePage.dcFilters.selectRetailer(retailerToSelect);
        pricePage.dcFilters.apply();
        pricePage.dateAndIntervalPickerPage.selectCustomDateRangeWithYear("2023", "Sept", 16, 22);
        pricePage.commonFeatures.searchForText(rpcToSearch);

        List<String> itemsFoundInUI = pricePage.isPriceProductDisplayedInSearchResults();
        List<String> itemsFoundInDB = getItemsFoundInDB("McCormick US", "2023-09-16", "2023-09-22", rpcToSearch);
        Assert.assertEquals(itemsFoundInUI.size(), itemsFoundInDB.size(), "The number of items found in UI is not matching the number of items found in DB");

        LOGGER.info("The number of items found in UI is matching. Number of items found in UI: " + itemsFoundInUI.size() + ", number of items found in DB: " + itemsFoundInDB.size());
        Assert.assertEquals(itemsFoundInUI, itemsFoundInDB, "The items found in UI are not matching the items found in DB");

        LOGGER.info("The items found in UI are matching the items found in DB");
    }

    public List<String> getExpectedOptionsInSortingDropdown() {
        List<String> expectedOptions = Arrays.asList("Select All", "Price Increase", "Price Decrease", "On Promotion", "Off Promotion");
        LOGGER.info("Expected Options in Sorting Dropdown are " + expectedOptions);
        return expectedOptions;
    }

    public List<String> getExpectedOptionsInPriceTable() {
        List<String> expectedOptions = Arrays.asList("Image", "Product Title", "RPC", "Retailer", "Average Price", "% of Time on Promotion");
        LOGGER.info("Expected Options in Price Table are " + expectedOptions);
        return expectedOptions;
    }

    public Map<String, Map<String, String>> getItemsFromDB(String BU, String startDate, String endDate, String retailer, String brand) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        Map<String, Map<String, String>> statusMapFromDB = new HashMap<>();
        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(PriceQueries.queryToFetchPriceStatuses(BU, startDate, endDate, retailer, brand))) {
                while (resultSet.next()) {
                    String date = resultSet.getString("DATE_KEY");
                    String item = resultSet.getString("RPC");
                    String priceStatus = resultSet.getString("OBSERVED_PRICE");
                    statusMapFromDB.putIfAbsent(date, new HashMap<>());
                    statusMapFromDB.get(date).put(item, priceStatus);
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
                 ResultSet resultSet = statement.executeQuery(PriceQueries.queryToFetchRPCsForAutoSearch(BU, startDate, endDate, rpcToSearch))) {
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