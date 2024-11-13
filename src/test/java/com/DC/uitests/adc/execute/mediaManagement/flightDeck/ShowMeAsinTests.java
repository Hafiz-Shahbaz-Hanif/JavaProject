package com.DC.uitests.adc.execute.mediaManagement.flightDeck;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.paidMediaReporting.ExecutiveDashboardPage;
import com.DC.pageobjects.adc.execute.mediaManagement.flightDeck.FlightDeck;
import com.DC.pageobjects.adc.execute.mediaManagement.flightDeck.ShowMeASIN;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.DC.utilities.SharedMethods.isSortedDescending;
import static java.util.Arrays.asList;

public class ShowMeAsinTests extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppFilaLegacyUrl();
    private static final String FLIGHT_DECK_URL = LOGIN_ENDPOINT + "/advertising/flightdeck/AMAZON";
    private static final String EXECUTIVE_DASHBOARD_PATH = "app/media/executive-dashboard";
    private static final String EXECUTIVE_DASHBOARD_URL = LOGIN_ENDPOINT.replace("#", "") + EXECUTIVE_DASHBOARD_PATH;

    private FlightDeck flightDeck;
    private ShowMeASIN showMeASIN;
    public ExecutiveDashboardPage executiveDashboardPage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("McCormick");
        Thread.sleep(1000);
        driver.get(FLIGHT_DECK_URL);
        Thread.sleep(2000);
        flightDeck = new FlightDeck(driver);
        showMeASIN = (ShowMeASIN) flightDeck.selectShowMeOption(Enums.FlightDeckShowMe.ASIN);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify that \"ASIN\" option is added in \"Show Me\" drop down in FlightDeck Screen")
    public void VerifyShowMeASINSOptionIsDisplayedInDropdown() throws InterruptedException {
        Assert.assertTrue(showMeASIN.isOptionInShowMeDropdownVisible(Enums.FlightDeckShowMe.ASIN), "Show Me ASIN option was not displayed in dropdown");
    }

    @Test(priority = 2, description = "Verify that when ASIN option is selcted in drop down then Show Me : ASIN screen load successfully and ASIN, Title, and State columns are displayed")
    public void VerifyShowMeAsinScreenDisplaysColumnsWithData() throws InterruptedException {
        showMeASIN.filterASINS("B000PEDTV8");
        List<String> asinColumnValues = showMeASIN.getAsinColumnValues();
        Assert.assertNotEquals(asinColumnValues.size(), 0, "ASIN column values were not displayed");
        List<String> titleColumnValues = showMeASIN.getAsinTitleColumnValues();
        Assert.assertNotEquals(titleColumnValues.size(), 0, "ASIN Title column values were not displayed");
        List<String> stateColumnValues = showMeASIN.getAutoSalesPauseColumnValues();
        Assert.assertNotEquals(stateColumnValues.size(), 0, "ASIN State column values were not displayed");
    }

    @Test(priority = 3, description = "Verify that Edit button present under breadcumb is disabled")
    public void VerifyShowMeAsinEditButtonIsDisabledByDefault() {
        Assert.assertFalse(showMeASIN.isEditButtonEnabled(), "Edit button was enabled by default");
    }

    @Test(priority = 4, description = "Verify that Upload file and Export File buttons are present right after Show Me drop down")
    public void VerifyShowMeAsinUploadAndExportFileButtonsAreVisible() {
        Assert.assertTrue(showMeASIN.isUploadFileButtonVisible(), "Upload file button was not visible after Show Me dropdown");
        Assert.assertTrue(showMeASIN.isExportFileButtonVisible(), "Export file button was not visible after Show Me dropdown");
    }

    @Test(priority = 5, description = "Verify that Auto Pause State filter option in column header works as expected")
    public void VerifyShowMeAsinAutoPauseFilterWorks() throws InterruptedException {
        List<String> asinsToFilter = asList("B000PEDTV8", "B00H5QPD8G", "B008OGCCLC");
        showMeASIN.filterMultipleASINS(asinsToFilter);
        List<String> asinColumnValuesBeforeFilter = showMeASIN.getAsinColumnValues();
        showMeASIN.sortAsinsByAutoPauseStateColumnFilter(Enums.AutoPauseState.Enabled);
        List<String> asinColumnValuesAfterFilter = showMeASIN.getAsinColumnValues();
        List<String> stateColumnValues = showMeASIN.getAutoSalesPauseColumnValues();
        Assert.assertFalse(stateColumnValues.contains("PAUSED"), "Filtering Auto Pause state to Enabled did not remove ASIN with status PAUSED");
        Assert.assertNotEquals(asinColumnValuesBeforeFilter, asinColumnValuesAfterFilter, "ASINs in table did not update after Auto Pause State filter was applied.");
    }

    @Test(priority = 6, description = "Verify that Spend Column is in descending order")
    public void flightDeck_VerifyDescendingOrderOfSpendColumn() throws InterruptedException {
        flightDeck.selectLastMonth();

        List<Double> flightDeckSpendColumnValues = flightDeck.getFlightDeckSpendColumnValues();
        Assert.assertTrue(isSortedDescending(flightDeckSpendColumnValues), "Spend column values are not in descending order");

        driver.get(EXECUTIVE_DASHBOARD_URL);
        executiveDashboardPage = new ExecutiveDashboardPage(driver);
        executiveDashboardPage.filterByASIN();
        executiveDashboardPage.selectMonthlyPeriod();
        executiveDashboardPage.dateAndIntervalPickerPage.selectDateRange("Last Month");

        List<Double> executiveDashboardSpendColumnValues = executiveDashboardPage.getEDPSpendColumnValues();
        Assert.assertTrue(isSortedDescending(executiveDashboardSpendColumnValues), "Spend column values are not in descending order");
    }
}