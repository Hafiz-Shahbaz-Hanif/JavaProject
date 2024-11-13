package com.DC.uitests.hub.userInfo;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.hub.HubCommonMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class FrontEndUserInfoTest extends BaseClass {

    Logger logger;
    ReadConfig readConfig;
    WebDriver driver;
    boolean headless;
    String dcAppUrl;

    FrontEndUserInfoTest () {
        logger = Logger.getLogger(FrontEndUserInfoTest.class);
        readConfig =  ReadConfig.getInstance();
        PropertyConfigurator.configure("log4j.properties");
        headless = readConfig.getHeadlessMode();
        dcAppUrl = readConfig.getDcAppUrl();
    }

    @Test(description = "PH-189 - Implement User Info Endpoint - Insights User")
    public void Hub_FE_Insights_Implement_User_Info_Endpoint_Test() throws Exception {
        logger.info("** Test has started.");

        DCLoginPage lp = new DCLoginPage(driver);
        HubCommonMethods hubCommonMethods = new HubCommonMethods();

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());
        String oneSpaceUserToken = "Bearer " +  SecurityAPI.getAuthToken(driver);

        Response userResponse = hubCommonMethods.getUserHubInfo(oneSpaceUserToken);
        String osRoles = (String) hubCommonMethods.getUserPlatformInfo(userResponse, "onespace");

        JSONObject osRolesLocalStorageJson = new JSONObject(lp.getLocalStorageItemValue("onespace"));
        String osRolesLocalStorage = osRolesLocalStorageJson.getString("token");

        Assert.assertEquals(osRoles, osRolesLocalStorage, "OS roles in local storage do not match!");
        logger.info("** Test completed successfully");
    }

    @Test( description = "PH-191-192 - Implement User Info Endpoint - Fila User")
    public void Hub_FE_FilaLegacy_FilaNext_Implement_User_Info_Endpoint_Test() throws Exception {
        logger.info("** Test has started.");

        DCLoginPage lp = new DCLoginPage(driver);
        HubCommonMethods hubCommonMethods = new HubCommonMethods();

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());
        String filaToken = "Bearer " +  SecurityAPI.getAuthToken(driver);

        Response userResponse = hubCommonMethods.getUserHubInfo(filaToken);
        JSONObject filaRoles = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "fila");

        JSONObject filaRolesLocalStorage = new JSONObject(lp.getLocalStorageItemValue("fila"));
        Assert.assertTrue(filaRolesLocalStorage.similar(filaRoles), "Fila roles in local storage do not match!");

        logger.info("** Test completed successfully");
    }

    @Test(description = "PH-190 - Implement User Info Endpoint - Edge User")
    public void Hub_FE_MarketShare_Implement_User_Info_Endpoint_Test() throws Exception {
        logger.info("** Test has started.");

        DCLoginPage lp = new DCLoginPage(driver);
        HubCommonMethods hubCommonMethods = new HubCommonMethods();

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubEdgeOnlyUserEmail(), readConfig.getHubEdgeOnlyUserPassword());
        String edgeUserToken = "Bearer " +  SecurityAPI.getAuthToken(driver);

        Response userResponse = hubCommonMethods.getUserHubInfo(edgeUserToken);
        JSONObject msRoles = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "marketshare");

        JSONObject msRolesLocalStorage = new JSONObject(lp.getLocalStorageItemValue("marketshare"));
        Assert.assertTrue(msRolesLocalStorage.similar(msRoles), "MS roles in local storage do not match!");

        logger.info("** Test completed successfully");
    }


    @Test(description = "PH-189-190-191-192 - Implement User Info Endpoint - Fila Insights Edge User")
    public void Hub_FE_FilaLegacy_FilaNext_Insights_MarketShare_Implement_User_Info_Endpoint_Test() throws Exception {
        logger.info("** Test has started.");

        SoftAssert softAssert = new SoftAssert();
        DCLoginPage lp = new DCLoginPage(driver);
        HubCommonMethods hubCommonMethods = new HubCommonMethods();

        lp.openLoginPage(driver, dcAppUrl);
        lp.loginDcApp(readConfig.getHubFilaInsightsEdgeUserEmail(), readConfig.getHubFilaInsightsEdgeUserPassword());
        String userToken = "Bearer " +  SecurityAPI.getAuthToken(driver);

        Response userResponse = hubCommonMethods.getUserHubInfo(userToken);
        JSONObject msRoles = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "marketshare");
        JSONObject filaRoles = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "fila");
        String osRoles = (String) hubCommonMethods.getUserPlatformInfo(userResponse, "onespace");

        JSONObject filaRolesLocalStorage = new JSONObject(lp.getLocalStorageItemValue("fila"));
        JSONObject msRolesLocalStorage = new JSONObject(lp.getLocalStorageItemValue("marketshare"));

        JSONObject osRolesLocalStorageJson = new JSONObject(lp.getLocalStorageItemValue("onespace"));
        String osRolesLocalStorage = osRolesLocalStorageJson.getString("token");

        softAssert.assertEquals(osRoles, osRolesLocalStorage, "OS roles in local storage do not match!");
        softAssert.assertTrue(msRoles.similar(msRolesLocalStorage), "MS roles in local storage do not match!");
        softAssert.assertTrue(filaRoles.similar(filaRolesLocalStorage), "Fila roles in local storage do not match!");

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }

    @AfterMethod
    public void killDriver() {
        quitBrowser();
    }

    @BeforeMethod()
    public void initializeBrowser(ITestContext testContext) {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
    }
}