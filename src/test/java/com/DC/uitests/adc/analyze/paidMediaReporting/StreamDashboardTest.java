package com.DC.uitests.adc.analyze.paidMediaReporting;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.paidMediaReporting.StreamDashboardPage;
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

public class StreamDashboardTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private static final String STREAM_DASHBOARD_PATH = "media/stream-hourly-reporting-dashboard/";
    private static final String STREAM_DASHBOARD_URL = LOGIN_ENDPOINT + STREAM_DASHBOARD_PATH;

    private StreamDashboardPage streamDashboardPage;
    private AppHomepage appHomepage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Analyze");
        appHomepage.clickLink("Stream Dashboard");
        netNewNavigationMenu.selectBU("Logitech");

        streamDashboardPage = new StreamDashboardPage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @BeforeMethod
    public void setupMethod() {
        if (!driver.getTitle().contains("Stream Hourly Reporting Dashboard - Flywheel")) {
            driver.get(STREAM_DASHBOARD_URL);
        }
    }

    @Test(priority = 1, description = "Verify that User is able to extract file from Stream Dashboard")
    public void MDR_StreamDashboard_ExportFile_Verification() throws Exception {
        List<String> availableOptionsInCampaignTypeFilter = streamDashboardPage.getAvailableOptionsFromCampaignTypeFilter();
        for (String optionToSelect : availableOptionsInCampaignTypeFilter) {
            performTest(optionToSelect);
        }
    }

    private void performTest(String optionToSelect) throws Exception {
        streamDashboardPage.selectItemFromCampaignTypeFilterDropdown(optionToSelect);
        Assert.assertEquals(streamDashboardPage.getHourlyStreamReportingRowsCount(), 25);

        streamDashboardPage.clickDownloadButton();
        String expectedExtension = ".csv";
        String downloadedFilePath = SharedMethods.isFileDownloaded(expectedExtension, "STREAM_DASHBOARD", 60, downloadFolder);
        Assert.assertNotNull(downloadedFilePath, "File is not downloaded");
        Assert.assertTrue(downloadedFilePath.endsWith(expectedExtension), "File does not have " + expectedExtension + " extension");

        SharedMethods.deletePath(Path.of(downloadedFilePath));
    }
}