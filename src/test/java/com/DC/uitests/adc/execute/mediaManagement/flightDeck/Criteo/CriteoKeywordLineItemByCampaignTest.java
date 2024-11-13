package com.DC.uitests.adc.execute.mediaManagement.flightDeck.Criteo;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.mediaManagement.flightDeck.CriteoKeywordLineItemByCampaignPage;
import com.DC.pageobjects.adc.execute.mediaManagement.flightDeck.FlightDeck;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CriteoKeywordLineItemByCampaignTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private FlightDeck flightDeck;
    private CriteoKeywordLineItemByCampaignPage criteoKeywordLineItemByCampaignPage;

    @BeforeMethod()
    public void setupTests(ITestContext testContext) throws Exception {

        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("SuperFizz - US");
        Thread.sleep(2000);
        flightDeck = new FlightDeck(driver);
        flightDeck.clickOnSection("Execute");
        flightDeck.clickOnPage("FlightDeck");
        criteoKeywordLineItemByCampaignPage = new CriteoKeywordLineItemByCampaignPage(driver);

    }

    @Test(description = "EMP-721 - Verify Criteo Platform is displayed in the Platform Dropdown ")
    public void Criteo_Platform_Displayed_Test() throws InterruptedException {

        LOGGER.info("Verify Criteo displayed in Platform Dropdown");
        criteoKeywordLineItemByCampaignPage.criteoPlatform();

    }

    @Test(description = "EMP-721 - Verify Criteo ADD Keyword Model is displayed and his functionality ")
    public void Criteo_Keyword_Add_Modal_Displayed_Test() throws InterruptedException {

        LOGGER.info("Select the Platform");
        criteoKeywordLineItemByCampaignPage.criteoPlatform();


        LOGGER.info("Select Line item By Campaign Checkbox");
        criteoKeywordLineItemByCampaignPage.editAddKeyword();
        LOGGER.info("Verify ADD Keyword Modal is Displayed");
        Assert.assertTrue(criteoKeywordLineItemByCampaignPage.addKeywordModalDisplayed(),"Add Keyword Modal is not Displayed ");

        LOGGER.info("Validate the Add Keyword Modal");
        Assert.assertTrue(criteoKeywordLineItemByCampaignPage.addKeywordAddRowButtonDisplayed(),"Add Row button is not Displayed ");
        Assert.assertTrue(criteoKeywordLineItemByCampaignPage.addKeywordUploadButtonDisplayed(),"Upload Button is not Displayed ");
        Assert.assertTrue(criteoKeywordLineItemByCampaignPage.addKeywordModalClosedButtonDisplayed(),"Close Button is not Displayed ");
        Assert.assertTrue(criteoKeywordLineItemByCampaignPage.addKeywordModalSaveButtonDisplayed(),"Save Button is not Displayed ");

        LOGGER.info("Validate ADD Keyword Modal Functionality");
        criteoKeywordLineItemByCampaignPage.addKeywordModalFunctionality();
        Assert.assertTrue(criteoKeywordLineItemByCampaignPage.addKeywordSuccessMessage(),"The request has been successfully processed is not displayed ");
    }

    @AfterMethod()
    public void killDriver() {
        quitBrowser();
    }
}
