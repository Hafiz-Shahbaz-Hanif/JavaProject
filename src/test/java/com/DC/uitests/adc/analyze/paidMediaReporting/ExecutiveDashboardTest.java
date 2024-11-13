package com.DC.uitests.adc.analyze.paidMediaReporting;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.paidMediaReporting.ExecutiveDashboardPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class ExecutiveDashboardTest extends BaseClass {

    private ExecutiveDashboardPage executiveDashboardPage;
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private static final String EXECUTIVE_DASHBOARD_URL = LOGIN_ENDPOINT + "media/executive-dashboard/";

    private SoftAssert softAssert = new SoftAssert();
    NetNewNavigationMenu netNewNavigationMenu;

    @BeforeMethod
    public void setupTests(ITestContext testContext, ITestResult tr) throws Exception {
        testMethodName.set(tr.getMethod().getMethodName());
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName + " ***************");
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("Logitech");
        driver.get(EXECUTIVE_DASHBOARD_URL);

        if (!driver.getTitle().contains("Media Executive Dashboard - Flywheel")) {
            driver.get(EXECUTIVE_DASHBOARD_URL);
        }

        executiveDashboardPage = new ExecutiveDashboardPage(driver);
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify that on selection of multiple platforms will show the option of platform in source of change widget listbox options")
    public void ExecutiveDashboard_SourceOfChangeWidgetForMultiplePlatform() throws InterruptedException {
        softAssert.assertTrue(executiveDashboardPage.dateAndIntervalPickerPage.isIntervalSelectionDisplayed(), "The interval selection is not displayed");
        softAssert.assertTrue(executiveDashboardPage.selectMultiplePlatforms());
        softAssert.assertTrue(executiveDashboardPage.verifySourceOfChangeWidgetListBox("Platform"));
        softAssert.assertTrue(executiveDashboardPage.verifySourceOfChangeWidgetTable("Platform", SharedMethods.createList("AMAZON,AMAZON_DSP"))
                , "Selected source of change is not available in the source of change widget table");
        softAssert.assertAll();
    }

    @Test(description = "Verify that on selection of multiple business units will show the option of Business Unit in source of change widget listbox options")
    public void ExecutiveDashboard_SourceOfChangeWidgetForMultipleBusinessUnits() throws InterruptedException {
        softAssert.assertTrue(executiveDashboardPage.dateAndIntervalPickerPage.isIntervalSelectionDisplayed(), "The interval selection is not displayed");
        softAssert.assertTrue(executiveDashboardPage.selectMultiplePlatforms());
        netNewNavigationMenu.selectBU("Logitech", "Logitech AU");
        softAssert.assertTrue(executiveDashboardPage.verifySourceOfChangeWidgetListBox("Business Unit"));
        softAssert.assertTrue(executiveDashboardPage.verifySourceOfChangeWidgetTable("Business Unit", SharedMethods.createList("Logitech,Logitech AU"))
                , "Selected source of change is not available in the source of change widget table");
        softAssert.assertAll();
    }
}
