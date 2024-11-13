package com.DC.uitests.hub.connect;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.navigationMenus.BaseNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.uitests.hub.marketshare.HubMarketShareTokenExchangeForUsersTest;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SharedMethods;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class HubConnectTest extends BaseClass {

    Logger logger;
    ReadConfig readConfig;
    String dcAppUrl;

    HubConnectTest() {
        readConfig = ReadConfig.getInstance();
        logger = Logger.getLogger(HubMarketShareTokenExchangeForUsersTest.class);
        PropertyConfigurator.configure("log4j.properties");
        dcAppUrl = readConfig.getDcAppUrl();
    }

    @BeforeClass()
    public void setup(ITestContext testContext) {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
    }

    @AfterClass()
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "PH-157 - Connect Organization Selector")
    public void Hub_Connect_Organization_Selector_Test() throws Exception {
        logger.info("** Test has started.");

        DCLoginPage lp = new DCLoginPage(driver);
        SoftAssert softAssert = new SoftAssert();
        AppHomepage hp;

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubConnectUserEmail(), readConfig.getHubConnectUserPassword());
        hp = new AppHomepage(driver);
        hp.openPage("Identify", "At a Glance");
        lp.click(BaseNavigationMenu.CONNECT_BU_DROPDOWN);

        List<WebElement> buList = lp.findElementsVisible(BaseNavigationMenu.CONNECT_BU_DROPDOWN_LIST);
        WebElement randomBu = (WebElement) SharedMethods.getRandomItemFromList(buList);
        String buToSelect = lp.getAttribute(randomBu.findElement(By.xpath(".//img")), "src");
        randomBu.click();

        hp.openPage("Identify", "Overview");
        String buSelected = lp.waitUntilAttributeValuePresentInElement(BaseNavigationMenu.CONNECT_BU_DROPDOWN_IMAGE, "src");
        softAssert.assertEquals(buToSelect, buSelected, "BU selected on a Connect page does not carry over to other Connect pages.");

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }
}