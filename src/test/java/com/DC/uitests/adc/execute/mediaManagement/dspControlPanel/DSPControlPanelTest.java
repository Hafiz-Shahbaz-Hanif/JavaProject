package com.DC.uitests.adc.execute.mediaManagement.dspControlPanel;

import com.DC.constants.NetNewConstants;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.dspControlPanel.DSPControlPanelPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.testcases.BaseClass;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class DSPControlPanelTest extends BaseClass {

    private DSPControlPanelPage dspControlPanelPage;
    private DCFilters dcFilters;
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppFilaLegacyUrl();

    private SoftAssert softAssert = new SoftAssert();

    @BeforeMethod
    public void setupTests(ITestContext testContext, ITestResult tr) throws Exception {
        testMethodName.set(tr.getMethod().getMethodName());
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName + " ***************");
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("Logitech");
        String dspControlPanelUrl = NetNewConstants.getReportsUrl("media", "dsp-control-panel");
        driver.get(dspControlPanelUrl);

        if (!driver.getTitle().equals("Scratchpad - Flywheel - Staging")) {
            driver.get(dspControlPanelUrl);
        }

        dspControlPanelPage = new DSPControlPanelPage(driver);
        dcFilters = new DCFilters(driver);
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify Automations Bulk Select pop-up by default values on DSP Control Panel grid")
    public void dspControlPanelAutomationBulkSelectPopup() throws InterruptedException {

        dspControlPanelPage.moveToDspControlPanelGrid();
        dspControlPanelPage.clickBulkEditButtonOnDspControlPanelGrid();
        softAssert.assertTrue(dspControlPanelPage.verifyAutomationBulkSelectPopupElements());
        dspControlPanelPage.closeAutomationsBulkSelectPopup();
        softAssert.assertAll();
    }

    @Test(description = "Verify Automation Filter is selected and the data is populated on the grid")
    public void dspControlPanelAutomationFilterAndDataVerification() throws InterruptedException {

        dspControlPanelPage.moveToDspControlPanelGrid();
        dcFilters.openAutomationFilterDropdown();
        dspControlPanelPage.selectAutomationFilter("PAUSED");
        softAssert.assertTrue(dspControlPanelPage.verifyDspControlPanelGridDataAgainstPausedAutomationStatus());
        softAssert.assertAll();
    }
}
