package com.DC.uitests.adc.analyze.retailReporting;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.retailReporting.CaseManagementReportingPage;

import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.sql.SQLException;

public class CaseManagementCreationReportingTests extends BaseClass {
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private static final String WFL_USER_NAME = READ_CONFIG.getWFLUsername();
    private static final String WFL_PASSWORD = READ_CONFIG.getWFLPassword();
    private static final String WFL_LOGIN_ENDPOINT = READ_CONFIG.getWFLUrl();

    private CaseManagementReportingPage caseManagementReportingPage;
    private AppHomepage appHomepage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Analyze");
        appHomepage.clickLink("Case Management Reporting");
        appHomepage.selectBU("McCormick");
        caseManagementReportingPage = new CaseManagementReportingPage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify the metrics displayed on the Clipper page")
    public void Clipper_VerificationOfMetricsDisplayed() {
        Assert.assertTrue(caseManagementReportingPage.isTotalCaseSubmittedPresent(), "Total Cases Submitted metric was not displayed");
        Assert.assertTrue(caseManagementReportingPage.isTimeSavedPresent(), "Time Saved metric was not displayed");
        Assert.assertTrue(caseManagementReportingPage.isConversionRatePresent(), "Conversion Rate metric was not displayed");
    }

    @Test(priority = 2, description = "Verify the Clipper screen is displayed")
    public void Clipper_VerificationOfClipperScreenDisplayed() {
        Assert.assertTrue(caseManagementReportingPage.isClipperScreenAdded(), "Clipper screen was not displayed");
    }

    @Test(priority = 3, description = "Verify the Download as a CSV file is displayed")
    public void Clipper_VerificationOfDownloadAsACsvFile() throws Exception {
        Assert.assertTrue(caseManagementReportingPage.isDownloadButtonVisible(), "Download as a CSV file was not available");
        caseManagementReportingPage.clickDownloadButton();
        String expectedExtension = ".csv";
        String downloadedFilePath = SharedMethods.isFileDownloaded(expectedExtension, "Clipper_Export", 60, downloadFolder);
        Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
    }

    @Test(priority = 4, description = "Verify accounts were successfully onboarded on WFL", dataProvider = "clients")
    public void Clipper_VerificationOfAccountsOnboarded(String clientName) throws SQLException, InterruptedException {
        int idAssignedByWFL = caseManagementReportingPage.getWFLId(clientName);
        Assert.assertTrue(idAssignedByWFL > 0, "WFL ID was not found in the database");
        LOGGER.info("WFL ID for client " + clientName + " is " + idAssignedByWFL);
        caseManagementReportingPage.loginToWFL(WFL_LOGIN_ENDPOINT, WFL_USER_NAME, WFL_PASSWORD);
        Assert.assertTrue(caseManagementReportingPage.isClientNameFound(clientName), "Client was not found in the menu in WFL");
        Assert.assertTrue(caseManagementReportingPage.isWFLIdInUrl(idAssignedByWFL), "WFL ID was not found in the URL");
    }

    @Test(description = "Verify Alert was added when RBS Case is Open >3 Days")
    public void Clipper_VerificationOfAlertAdded() throws InterruptedException {
        caseManagementReportingPage.selectBU("3M");
        Assert.assertTrue(caseManagementReportingPage.isDaysOpenColumnVisible(), "Days Open column was not displayed");
        String asinToCheck = "B088GFPYJM";
        caseManagementReportingPage.searchForAsin(asinToCheck);
        Assert.assertTrue(caseManagementReportingPage.checkDaysOpenCalculation(), "Days Open calculation was not correct");
    }

    // Will be filled with actual clients in the future
    @DataProvider(name = "clients")
    public Object[][] getClientNames() {
        return new Object[][]{
                {"Jack Links"}
        };
    }
}