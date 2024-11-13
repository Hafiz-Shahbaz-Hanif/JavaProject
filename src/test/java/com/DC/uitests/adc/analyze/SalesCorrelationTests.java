package com.DC.uitests.adc.analyze;

import com.DC.constants.NetNewConstants;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.retailReporting.SalesCorrelationModelPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.List;

public class SalesCorrelationTests extends BaseClass {
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private static final String SALES_CORRELATION_URL = NetNewConstants.getReportsUrl("catalog", "sales-correlation-model/");
    private SalesCorrelationModelPage salesCorrelationModelPage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("McCormick");
        Thread.sleep(1000);
        driver.get(SALES_CORRELATION_URL);
        salesCorrelationModelPage = new SalesCorrelationModelPage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @BeforeMethod
    public void setupMethod() {
        if(!driver.getTitle().equals("Sales Correlation Model - Flywheel")) {
            driver.get(SALES_CORRELATION_URL);
        }
    }

    @Test(priority = 1, description = "Verify Distributor View filter with Manufacturing option should be displayed by default once navigated to SCM screen")
    public void SCT_VerifyDistributorViewManufacturingOptionIsDefault() {
        Assert.assertEquals(salesCorrelationModelPage.getDistributorViewValue(), "Manufacturing", "Distributor view default filter was incorrect" +
                "\n EXPECTED: Manufacturing" +
                "\n ACTUAL: " + salesCorrelationModelPage.getDistributorViewValue());
    }

    @Test(priority = 2, description = "Verify that the options for Brand are all Brands within ASIN Segmentation for BU when on the SCM screen")
    public void SCT_VerifyBrandFilterIsDefault() {
        Assert.assertEquals(salesCorrelationModelPage.getDefaultBrandInSalesWidget(), "Brand", "Sales By filter default value was incorrect" +
                "\n EXPECTED: Brand" +
                "\n ACTUAL: " + salesCorrelationModelPage.getDefaultBrandInSalesWidget());
    }

    @Test(priority = 3, description = "Verify that Undo and Redo Filters function are displayed to the right of 'Copy Filters to Clipboard'")
    public void SCT_VerifyUndoAndRedoFiltersAreVisible() throws InterruptedException {
        salesCorrelationModelPage.openFiltersSectionIfClosed();
        Assert.assertTrue(salesCorrelationModelPage.isUndoFilterDisplayed(), "Undo filter was not displayed in filters section");
        Assert.assertTrue(salesCorrelationModelPage.isRedoFilterDisplayed(), "Redo filter was not displayed in filters section");
    }

    @Test(priority = 4, description = "Verify Monthly is selected as the default filter for Interval'")
    public void SCT_VerifyIntervalSelectorDisplaysDefaultOption() {
        String selectedInterval = salesCorrelationModelPage.dateAndIntervalPickerPage.getSelectedInterval();
        Assert.assertEquals(selectedInterval, "Monthly", "Interval selector default value was incorrect" +
                "\n EXPECTED: Monthly" +
                "\n ACTUAL: " + selectedInterval);
    }

    @Test(priority = 5, description = "Verify Monthly is selected as the default filter for Interval'")
    public void SCT_VerifyDateRangeDropdownDisplaysDefaultOption() throws InterruptedException {
        String selectedDateRange = salesCorrelationModelPage.dateAndIntervalPickerPage.openDateRangeAndGetSelectedDate();
        Assert.assertEquals(selectedDateRange, "Last 6 Months", "Date Range selector default value was incorrect" +
                "\n EXPECTED: Last 6 Months" +
                "\n ACTUAL: " + selectedDateRange);
        salesCorrelationModelPage.dateAndIntervalPickerPage.clickDateRangeCancelButton();
    }

    @Test(priority = 6, description = "Verify Products and Sales ‘widget' has a download button and will download a CSV file")
    public void SCT_VerifyProductsAndSalesHasDownloadButtonAndReturnsCsvFile() throws Exception {
        String salesBySection = "Sales By";
        String productsSection = "Products";
        Assert.assertTrue(salesCorrelationModelPage.isDownloadButtonVisible(salesBySection), salesBySection + " Download button was not displayed");
        Assert.assertTrue(salesCorrelationModelPage.isDownloadButtonVisible("Products"), productsSection + " Download button was not displayed");
        String expectedExtension = ".csv";
        verifyFileDownloadsReturnsExpectedFileExtension(salesBySection, "sales_products", expectedExtension);
        verifyFileDownloadsReturnsExpectedFileExtension(productsSection, "SCM_Export", expectedExtension);
    }

    @Test(priority = 7, description = "Verify Sales By and Products ‘widget' has expander button added which will expand the widget to fill the entire screen")
    public void SCT_VerifyProductsAndSalesByModalButtonOpensModal() throws Exception {
        String salesBySection = "Sales By";
        String productsSection = "Products";
        verifySectionModalButtonOpensModal(salesBySection);
        verifySectionModalButtonOpensModal(productsSection);
    }

    @Test(priority = 8, description = "Verify Sales By ‘widget' drop-down has Brand as an option and shows all Brands in ASIN Segmentation")
    public void SCT_VerifySalesByWidgetDisplaysBrandAsDefaultOption() {
        String dropdownValue = salesCorrelationModelPage.getSalesByDropdownValue();
        Assert.assertEquals(dropdownValue, "Brand", "Sales By widget dropdown filter default value was incorrect" +
                "\n EXPECTED: Brand" +
                "\n ACTUAL: " + dropdownValue);
    }

    @Test(priority = 8, description = "Verify Sales By ‘widget' drop-down has Category as an option and shows all Categories  in ASIN Segmentation")
    public void SCT_VerifySalesByWidgetDropdownCanSelectCategoryOption() throws InterruptedException {
        salesCorrelationModelPage.selectSalesByDropdownValue("Category");
        String dropdownValue = salesCorrelationModelPage.getSalesByDropdownValue();
        Assert.assertEquals(dropdownValue, "Category", "Sales By dropdown did not update after selecting Category" +
                "\n EXPECTED: Category" +
                "\n ACTUAL: " + dropdownValue);
    }

    @Test(priority = 10, description = "Verify Products ‘widget' has pagination at the bottom of the page and the default option will be \"100\" and the available options to select from are 10, 25, 50, 100, 200")
    public void SCT_ProductsWidgetPaginationDisplaysCorrectValues() throws InterruptedException {
        List<Integer> expectedPageSizeOptions = Arrays.asList(10, 25, 50, 100, 200);
        int paginatorValue = salesCorrelationModelPage.getProductsPageSizeValue();
        Assert.assertEquals(paginatorValue, 100, "Products widget page size default value was incorrect" +
                "\n EXPECTED: 100" +
                "\n ACTUAL: " + paginatorValue);
        salesCorrelationModelPage.clickProductsPageSizeSelector();
        List<Integer> pageSizeOptions = salesCorrelationModelPage.getAllPageSizeOptions();
        Assert.assertEquals(pageSizeOptions, expectedPageSizeOptions, "Products widget page size options were incorrect" +
                "\n EXPECTED: " + expectedPageSizeOptions +
                "\n ACTUAL: " + pageSizeOptions);
        salesCorrelationModelPage.clickProductsPageSizeSelectionOption(100);
    }

    @Test(priority = 11, description = "Verify that for the Products widget, an option to filter to descending or ascending order for the 3 columns which are Sales, Ad Spend and % Change in sales")
    public void SCT_ProductsWidgetCanFilterColumnsByAscendingDescending() throws InterruptedException {
        Enums.SCTProductsColumns column = salesCorrelationModelPage.getActiveProductsColumn();
        Assert.assertEquals(column, Enums.SCTProductsColumns.Sales, "Products widget column default sorting option was incorrect" +
                "\n EXPECTED: Sales" +
                "\n ACTUAL: " + column);
        Enums.ColumnArrowSorting sortingStatus = salesCorrelationModelPage.getProductsColumnSortingArrowStatus(Enums.SCTProductsColumns.Sales);
        Assert.assertEquals(sortingStatus, Enums.ColumnArrowSorting.Ascending, "Products widget column default sorting was incorrect" +
                "\n EXPECTED: Ascending" +
                "\n ACTUAL: " + sortingStatus);

        verifySortingProductColumnsWorksCorrectly(Enums.SCTProductsColumns.Sales, Enums.ColumnArrowSorting.Descending);
        verifySortingProductColumnsWorksCorrectly(Enums.SCTProductsColumns.AdSpend, Enums.ColumnArrowSorting.Ascending);
        verifySortingProductColumnsWorksCorrectly(Enums.SCTProductsColumns.AdSpend, Enums.ColumnArrowSorting.Descending);
        verifySortingProductColumnsWorksCorrectly(Enums.SCTProductsColumns.ChangeInSales, Enums.ColumnArrowSorting.Ascending);
        verifySortingProductColumnsWorksCorrectly(Enums.SCTProductsColumns.ChangeInSales, Enums.ColumnArrowSorting.Descending);
    }
    
    public void verifySortingProductColumnsWorksCorrectly(Enums.SCTProductsColumns expectedColumn, Enums.ColumnArrowSorting expectedSorting) throws InterruptedException {
        salesCorrelationModelPage.clickProductsColumn(expectedColumn);
        Enums.SCTProductsColumns column = salesCorrelationModelPage.getActiveProductsColumn();
        Assert.assertEquals(column, expectedColumn, "Products widget column did not update sorting option" +
                "\n EXPECTED: " + expectedColumn +
                "\n ACTUAL: " + column);
        Enums.ColumnArrowSorting sortingStatus = salesCorrelationModelPage.getProductsColumnSortingArrowStatus(expectedColumn);
        Assert.assertEquals(sortingStatus, expectedSorting, "Products widget column sorting was incorrect" +
                "\n EXPECTED: " + expectedSorting +
                "\n ACTUAL: " + sortingStatus);
    }

    public void verifySectionModalButtonOpensModal(String section) throws Exception {
        Assert.assertTrue(salesCorrelationModelPage.isModalExpanderButtonVisible(section), section + " Modal button was not displayed");
        salesCorrelationModelPage.clickModalExpanderButton(section);
        Assert.assertTrue(salesCorrelationModelPage.isModalExpanderHeaderVisible(section), section + " Modal was not displayed");
        salesCorrelationModelPage.closeModal();
    }

    public void verifyFileDownloadsReturnsExpectedFileExtension(String section, String fileName, String expectedExtension) throws Exception {
        salesCorrelationModelPage.clickDownloadButton(section);
        String downloadedFilePath = SharedMethods.isFileDownloaded(expectedExtension, fileName, 60, downloadFolder);
        Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
    }
}
