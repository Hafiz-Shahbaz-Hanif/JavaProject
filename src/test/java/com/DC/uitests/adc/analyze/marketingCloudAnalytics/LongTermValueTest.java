package com.DC.uitests.adc.analyze.marketingCloudAnalytics;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.marketingCloudAnalytics.LongTermValuePage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.List;

public class LongTermValueTest extends BaseClass {
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private static final String LONG_TERM_VALUE_PATH = "amc/ltv-dashboard";
    private static final String LONG_TERM_VALUE_URL = LOGIN_ENDPOINT + LONG_TERM_VALUE_PATH;

    private LongTermValuePage longTermValuePage;
    private AppHomepage appHomepage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("McCormick US");
        appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Analyze");
        appHomepage.clickLink("Long Term Value");
        longTermValuePage = new LongTermValuePage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @BeforeMethod
    public void setupMethod() {
        if (!driver.getTitle().contains("LTV Dashboard - Flywheel")) {
            driver.get(LONG_TERM_VALUE_URL);
        }
    }

    @Test(priority = 1, description = "Verify that User is able to download CSV and PNG files from LTV page")
    public void MDR_LongTermValue_ExportDataVerification() throws Exception {
        longTermValuePage.clickCustomerValueRadioButton();
        longTermValuePage.clickApplyButton();
        var categoryNameNTB = "NTB";
        performTest(categoryNameNTB, false);

        longTermValuePage.clickPercentageReturnRadioButton();
        longTermValuePage.clickApplyButton();
        performTest(categoryNameNTB, true);

        longTermValuePage.clickNTBCheckbox();
        longTermValuePage.clickLoyaltyCheckbox();
        longTermValuePage.clickCustomerValueRadioButton();
        longTermValuePage.clickApplyButton();
        var categoryNameLoyalty = "Loyalty";
        performTest(categoryNameLoyalty, false);

        longTermValuePage.clickPercentageReturnRadioButton();
        longTermValuePage.clickApplyButton();
        performTest(categoryNameLoyalty, true);
    }

    private void performTest(String categoryName, boolean isPercentageData) throws Exception {
        Assert.assertTrue(longTermValuePage.isTablePresent(categoryName), categoryName + " table is not present");

        longTermValuePage.selectOptionFromDownloadButton("Screenshot");
        String screenshotExtension = ".png";
        String screenshotFilePath = SharedMethods.isFileDownloaded(screenshotExtension, "img", 60, downloadFolder);
        assertFileDownloaded(screenshotExtension, screenshotFilePath);
        SharedMethods.deletePath(Path.of(screenshotFilePath));

        longTermValuePage.selectOptionFromDownloadButton("CSV");
        String csvExtension = ".xlsx";
        String csvFilePath = SharedMethods.isFileDownloaded(csvExtension, "export", 60, downloadFolder);
        assertFileDownloaded(csvExtension, csvFilePath);

        var tableData = longTermValuePage.loadTableData(isPercentageData);
        List<List<String>> sheetData = longTermValuePage.loadExcelData(csvFilePath, isPercentageData);
        Assert.assertEquals(sheetData, tableData, "Data from table and excel file are not equal");

        SharedMethods.deletePath(Path.of(csvFilePath));
    }

    private void assertFileDownloaded(String expectedExtension, String downloadedFilePath) {
        Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
        Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");
    }
}