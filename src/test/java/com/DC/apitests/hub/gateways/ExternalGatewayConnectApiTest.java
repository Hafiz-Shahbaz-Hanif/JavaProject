package com.DC.apitests.hub.gateways;

import com.DC.testcases.BaseClass;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.hub.connect.authservice.ConnectAuthServiceApiRequest;
import com.DC.utilities.hub.ConnectMethods;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class ExternalGatewayConnectApiTest {

    Logger logger;
    ReadConfig readConfig;
    String auth0Token;
    BaseClass base;
    WebDriver driver;

    ExternalGatewayConnectApiTest() {
        logger = Logger.getLogger(ExternalGatewayAdcApiTest.class);
        readConfig =  ReadConfig.getInstance();
        PropertyConfigurator.configure("log4j.properties");
        base = new BaseClass();
    }

    @BeforeClass
    private void getTestData(ITestContext testContext) throws Exception {
        driver = base.initializeBrowser(testContext, readConfig.getHeadlessMode());
        auth0Token = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubConnectUserEmail(), readConfig.getHubConnectUserPassword());
    }

    @Test(description = "PH-151 - Verify Hub external gateway routes to Connect - User Info")
    public void Hub_External_Gateway_Routing_To_Connect_User_Info_Api_Test() throws Exception {
        logger.info("** Test has started.");

        JSONObject userInfo =  ConnectAuthServiceApiRequest.getUserInfoJson(auth0Token);
        JSONObject userInfoExternalgateway =  ConnectAuthServiceApiRequest.getUserInfoJsonExternalGateway(auth0Token);
        Assert.assertTrue(userInfo.similar(userInfoExternalgateway), "** Direct vs external gateway JSON responses do not match!");

        logger.info("** Test completed successfully");
    }

    @Test(description = "PH-151 - Verify Hub external gateway routes to Connect - At A Glance Media")
    public void Hub_External_Gateway_Routing_To_Connect_Glance_Media_Api_Test() throws Exception {
        logger.info("** Test has started.");

        SoftAssert softAssert = new SoftAssert();
        ConnectMethods connectMethods = new ConnectMethods();
        String params = "interval=weekly;";
        String headers = "x-currencycontext=USD;x-clientcontext=";

        JSONObject clientForUser =  connectMethods.getRandomClientExternalGateway(auth0Token);
        headers = headers + clientForUser.getInt("internalId");

        JSONObject retailerForUser =  connectMethods.getRandomRetailer(clientForUser);
        params = params + "retailerIds=" + retailerForUser.getString("id") + ";";
        JSONObject dateInterval =  ConnectAuthServiceApiRequest.getDateIntervalJsonExternalGateway(auth0Token, headers, params);
        params = params + "startDate=" + dateInterval.getString("startDate") + ";" + "endDate=" + dateInterval.getString("endDate");

        JSONObject glance =  ConnectAuthServiceApiRequest.getGlanceMediaJson(auth0Token, headers, params);
        JSONObject glanceExternalgateway =  ConnectAuthServiceApiRequest.getGlanceMediaJsonExternalGateway(auth0Token, headers, params);

        softAssert.assertEquals(glance.getJSONObject("clicks").get("currentValue"), glanceExternalgateway.getJSONObject("clicks").get("currentValue"), "** Direct vs external gateway Click values do not match.");
        softAssert.assertEquals(glance.getJSONObject("conversions").get("currentValue"), glanceExternalgateway.getJSONObject("conversions").get("currentValue"), "** Direct vs external gateway Conversions values do not match.");
        softAssert.assertEquals(glance.getJSONObject("cpa").get("currentValue"), glanceExternalgateway.getJSONObject("cpa").get("currentValue"), "** Direct vs external gateway Cpa values do not match.");

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }

}