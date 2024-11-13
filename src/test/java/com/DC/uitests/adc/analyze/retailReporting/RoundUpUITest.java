package com.DC.uitests.adc.analyze.retailReporting;

import com.DC.constants.NetNewConstants;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.retailReporting.RoundUpPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.CsvUtility;
import com.DC.utilities.SharedMethods;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

public class RoundUpUITest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();

    private RoundUpPage roundUpPage;

    @BeforeMethod()
    public void setupTests(ITestContext testContext) throws Exception {

        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("McCormick");
        roundUpPage = new RoundUpPage(driver);
    }

    @Test(description = "RAR-547 - Verify the Round Up navigation path at Analyze then Round Up")
    public void RU_RoundUpScreenIsDisplayed_Test() throws InterruptedException {
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");
        Assert.assertEquals(driver.getCurrentUrl(), NetNewConstants.getReportsUrl("catalog/reporting", "round-up"), "Goal Hub page URL is not correct");
    }

    @Test(description = "RAR-547 - Verify hourly interval is added to Time Intervals")
    public void RU_RoundUpHourlyIntervalIsDisplayed_Test() throws InterruptedException {
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");

        LOGGER.info("Select Hourly Interval");
        roundUpPage.selectInterval("Hourly");
        Assert.assertTrue(roundUpPage.verifyHourlyIntervalDisplayed("Hourly"), "Hourly Interval is not Displayed");

        roundUpPage.clickDateRangeSelector();
        LOGGER.info("Verify that Default Date is selected as 'Last Five Days' ");
        Assert.assertTrue(roundUpPage.verifyLastFiveDayDateIsDisplayed("Last 5 Days"), "Last 5 Days Date is not Displayed");
    }

    @Test(description = "RAR-548 - Verify only Ordered Revenue and Ordered Unit as an option in the “COGS, UNITS, REVENUE” dropdown")
    public void RU_RoundUp_Cogs_Units_Revenue_Dropdown_Values_Displayed_Test() throws InterruptedException {
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");

        LOGGER.info("Select Hourly Interval");
        roundUpPage.selectInterval("Hourly");

        LOGGER.info("Verify COGS,UNITS,REVENUE value are correctly Displayed");
        roundUpPage.verifyAllIntervalsCOGSValues("Clear", "Hourly");
    }

    @Test(description = "RAR-548 - Verify when Monthly interval is selected - it displays all available metrics ")
    public void RU_RoundUp_Cogs_Units_Revenue_Dropdown_Values_Monthly_Test() throws InterruptedException {
        String interval = "Monthly";
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");

        LOGGER.info("Select Monthly Interval");
        roundUpPage.selectInterval(interval);

        LOGGER.info("Verify COGS,UNITS,REVENUE value are correctly Displayed");
        roundUpPage.verifyAllIntervalsCOGSValues("Clear", interval);

    }

    @Test(description = "RAR-548 - Verify when Weekly Interval is selected - it displays all available metrics ")
    public void RU_RoundUp_Cogs_Units_Revenue_Dropdown_Values_Weekly_Test() throws InterruptedException {
        String interval = "Weekly";
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");

        LOGGER.info("Select Hourly Interval");
        roundUpPage.selectInterval(interval);

        LOGGER.info("Verify COGS,UNITS,REVENUE value are correctly Displayed");
        roundUpPage.verifyAllIntervalsCOGSValues("Clear", interval);
    }

    @Test(description = "RAR-548 - Verify when Daily interval is selected - it displays all available metrics ")
    public void RU_RoundUp_Cogs_Units_Revenue_Dropdown_Values_Daily_Test() throws InterruptedException {
        String interval = "Daily";
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");

        LOGGER.info("Select Hourly Interval");
        roundUpPage.selectInterval(interval);

        LOGGER.info("Verify COGS,UNITS,REVENUE value are correctly Displayed");
        roundUpPage.verifyAllIntervalsCOGSValues("Clear", interval);

    }

    @Test(description = "RAR-547 - Verify Users will still have the ability to expand the graph")
    public void RU_RoundUp_Expand_Graph() throws InterruptedException {
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");

        LOGGER.info("Select Monthly Interval");
        roundUpPage.selectInterval("Daily");

        LOGGER.info("Expand the Data High chart button");
        Assert.assertTrue(roundUpPage.verifyDataHighChartExpandIconfunctionality(), "Cogs Category Expand icon is not displayed");

        LOGGER.info("Expand the Cogs Category button");
        Assert.assertTrue(roundUpPage.verifyExpandIconfunctionality("cogs-category"), "Cogs Category Expand icon is not displayed");

        LOGGER.info("Expand the Cogs Sub Category button");
        Assert.assertTrue(roundUpPage.verifyExpandIconfunctionality("cogs-subcategory"), "Cogs Sub Category Expand icon is not displayed");

        LOGGER.info("Expand the Cogs Segment button");
        Assert.assertTrue(roundUpPage.verifyExpandIconfunctionality("cogs-segment"), "Cogs Segment Expand icon is not displayed");

    }

    @Test(dataProvider = "downloadOptions", dataProviderClass = RoundUpPage.class, description = "RAR-547 - Verify Users will still have the ability to download the graph in all forms ")
    public void RU_RoundUp_Real_Time_Sales_Download_Graph_All_Forms_Test(String downloadOption, String expectedExtension) throws InterruptedException {
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");
        LOGGER.info("Select Monthly Interval");
        roundUpPage.selectInterval("Hourly");

        Assert.assertTrue(roundUpPage.isDataHighChartExportIconDisplayed(), "Data High Chart Export Icon is not displayed");
        try {
            roundUpPage.clickOnDataHighChartExportIcon();
            roundUpPage.clickExportButton(downloadOption);
            String downloadedFilePath = RoundUpPage.isFileDownloaded(expectedExtension, "chart", 60, downloadFolder);
            Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
            Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");
            LOGGER.info("File is downloaded in " + expectedExtension + " format");
            roundUpPage.hitEscKey();
        } catch (Exception e) {
            Assert.fail("Exception occurred: " + e.getMessage());
        }

    }

    @Test(description = "RAR-532 - Verify Change in data status API and Export to incorporate Hourly interval for Single BU roundup")
    public void RU_HourlyRoundUpChangeInDataStatusSingleBU_Test() throws Exception {
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");

        LOGGER.info("Select Hourly Interval");
        roundUpPage.selectInterval("Hourly");

        LOGGER.info("Verify Data Status Grid");
        if (roundUpPage.dataStausButtonForSingleAndMultiBu()) {
            Assert.assertTrue(roundUpPage.dataStatusHourlyIntervalIsDisplayed(), "Hourly interval is not displayed");
            Assert.assertTrue(roundUpPage.dataStutusExportButtonDisplayed(), "Export to Asin Button is not displayed");

            LOGGER.info("Verify Data Status Export");
            roundUpPage.clickExportToAsinButton();
            String exportFilePath = SharedMethods.isFileDownloaded("csv", "asin_level_data_health", 60, downloadFolder);
            Assert.assertEquals(CsvUtility.getAllColumnNames(exportFilePath), Arrays.asList(RoundUpPage.createHeaderCsvSingleBu), "Column names not matching.");
        } else {
            LOGGER.info("Data Status API All Good");
        }
    }

    @Test(description = "RAR-532 - Verify Change in data status API and Export to incorporate Hourly interval for Multi BU roundup")
    public void RU_HourlyRoundUpChangeInDataStatusMultiBU_Test() throws Exception {
        LOGGER.info("Click on Analyze Section");
        roundUpPage.clickOnSection("Analyze");
        roundUpPage.clickOnPage("Round Up");

        LOGGER.info("Slect Multi BU");
        roundUpPage.selectMultiBU("McCormick CA");

        LOGGER.info("Select Hourly Interval");
        roundUpPage.selectInterval("Hourly");

        LOGGER.info("Verify Data Status Grid");
        if (roundUpPage.dataStausButtonForSingleAndMultiBu()) {
            Assert.assertTrue(roundUpPage.dataStatusHourlyIntervalIsDisplayed(), "Hourly interval is not displayed");
            Assert.assertTrue(roundUpPage.dataStutusExportButtonDisplayed(), "Export to Asin Button is not displayed");

            LOGGER.info("Verify Data Status Export");
            roundUpPage.clickExportToAsinButton();
            String exportFilePath = SharedMethods.isFileDownloaded("csv", "asin_level_agg_bu_data_health", 60, downloadFolder);
            Assert.assertEquals(CsvUtility.getAllColumnNames(exportFilePath), Arrays.asList(RoundUpPage.createHeaderCsvMultiBu), "Column names not matching.");
        } else {
            LOGGER.info("Data Status API All Good");
        }

    }

    @AfterMethod()
    public void killDriver() {
        quitBrowser();
    }
}
