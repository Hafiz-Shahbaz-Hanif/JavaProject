package com.DC.apitests.hub.gateways;

import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.adc.advertising.media.ReportingDashboardApiRequests;
import com.DC.utilities.apiEngine.apiRequests.hub.marketshare.authservice.MarketShareAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ReportingDashboardRequestBody;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.*;

public class InternalGatewaysApiExceptionHandlingTest {

    Logger logger;
    ReadConfig readConfig;
    String oktaToken;
    String auth0Token;
    String hubAuth0Token;
    WebDriver driver;
    BaseClass base;

    InternalGatewaysApiExceptionHandlingTest() throws IOException {
        logger = Logger.getLogger(ExternalGatewayAdcApiTest.class);
        readConfig =  ReadConfig.getInstance();
        PropertyConfigurator.configure("log4j.properties");
        base = new BaseClass();
    }

    @BeforeClass
    private void getTestData(ITestContext testContext) throws Exception {
        driver = base.initializeBrowser(testContext, readConfig.getHeadlessMode());
        oktaToken = SecurityAPI.getOktaTokenForUser(readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserOktaPassword());
    }

    private final String DAILY = "DAILY";

    @Test(description = "PH-124 - Internal Gateway Exception Handling while routes to Reporting Dashboard (Advertising)")
    public void Hub_Internal_Gateway_Exception_To_ReportingDashboard_Api_Test(ITestContext testContext) throws Exception {
        logger.info("** Test has started.");

        driver = base.initializeBrowser(testContext, readConfig.getHeadlessMode());
        auth0Token = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());

        String expectedFirstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
        String expectedYesterday = DateUtility.getYesterday();
        ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(DAILY, "actual", "direct_sales", SharedMethods.createList("clickRevenue,relatedClickRevenue,brandClickRevenue"), expectedFirstDayOfLastSevenDays, expectedYesterday, 39, "AMAZON", null, "ATTR_14D");

        Response responseWithOktaToken = ReportingDashboardApiRequests.reportingDashboard(rd, "", "", oktaToken);
        Response responseWithAuth0Token = ReportingDashboardApiRequests.reportingDashboardExternalGateway(rd, "", "", auth0Token);
        Assert.assertEquals(responseWithOktaToken.statusCode(), responseWithAuth0Token.statusCode(),  "** Status code is not same for both calls.");

        logger.info("** Test completed successfully");
    }

    @Test(description = "PH-124 - Internal Gateway Exception Handling Market Share - Get Clients")
    public void Hub_Internal_Gateway_Exception_To_MarketShare_UserInfo_Api_Test(ITestContext testContext) throws Exception {
        logger.info("** Test has started.");

        driver = base.initializeBrowser(testContext, readConfig.getHeadlessMode());
        hubAuth0Token = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubEdgeUserEmail(), readConfig.getHubEdgeUserPassword());

        Response clientsResponse = MarketShareAuthServiceApiRequest.getClients("X-API-KEY invalidUserApiKey");
        Response clientsExternalGatewayResponse = MarketShareAuthServiceApiRequest.getClientsExternalGateway("X-API-KEY=invalidUserApiKey", hubAuth0Token);
        Assert.assertEquals(clientsResponse.statusCode(), clientsExternalGatewayResponse.statusCode(), "** Status code is not same for both calls.");

        logger.info("** Test completed successfully");
    }
}