package com.DC.apitests.hub.token;

import com.DC.apitests.hub.gateways.ExternalGatewayAdcApiTest;
import com.DC.testcases.BaseClass;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.hub.connect.authservice.ConnectAuthServiceApiRequest;
import com.DC.utilities.hub.ConnectMethods;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.IOException;


public class Auth0TokenForConnectApiTest {

    Logger logger;
    ReadConfig readConfig;
    String auth0Token;
    BaseClass base;
    WebDriver driver;

    Auth0TokenForConnectApiTest() throws IOException {
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

    @Test(description = "PH-151 - Verify Connect accepts Auth0 access token - User Info - At A Glance Media")
    public void Connect_Glance_Media_Auth0_Token_Api_Test() throws Exception {
        logger.info("** Test has started.");

        ConnectMethods connectMethods = new ConnectMethods();
        String params = "interval=weekly;";
        String headers = "x-currencycontext=USD;x-clientcontext=";

        JSONObject clientForUser =  connectMethods.getRandomClient(auth0Token);
        headers = headers + clientForUser.getInt("internalId");

        JSONObject retailerForUser =  connectMethods.getRandomRetailer(clientForUser);
        params = params + "retailerIds=" + retailerForUser.getString("id") + ";";
        JSONObject dateInterval =  ConnectAuthServiceApiRequest.getDateIntervalJson(auth0Token, headers, params);
        params = params + "startDate=" + dateInterval.getString("startDate") + ";" + "endDate=" + dateInterval.getString("endDate");

        ConnectAuthServiceApiRequest.getGlanceMediaJson(auth0Token, headers, params);
        logger.info("** Test completed successfully");
    }

}