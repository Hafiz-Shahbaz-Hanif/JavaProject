package com.DC.apitests.hub.token;

import com.DC.testcases.BaseClass;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.hub.insights.authservice.InsightsAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.requests.hub.insights.authservice.InsightsAuthServiceRequestBody;
import com.DC.utilities.apiEngine.models.responses.hub.insights.authservice.InsightsAuthServiceResponseBody;
import io.restassured.response.Response;

public class Auth0TokenForInsightsApiTest {

	Logger logger;
	ReadConfig readConfig;
	String expiredAuth0Token;
	String auth0Token;
	BaseClass base;
	WebDriver driver;
	String header = "Content-Type=application/json";

	Auth0TokenForInsightsApiTest() {
		logger = Logger.getLogger(Auth0TokenForInsightsApiTest.class);
		readConfig =  ReadConfig.getInstance();
		PropertyConfigurator.configure("log4j.properties");
		base = new BaseClass();
	}

	@BeforeClass
	private void getTestData(ITestContext testContext) throws Exception {
		driver = base.initializeBrowser(testContext, readConfig.getHeadlessMode());
		auth0Token = SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());
		expiredAuth0Token = SecurityAPI.getExpiredAuth0TokenForInsights();
	}

	@Test(description = "PH-66 - Verify Auth0 token used to generate a valid Insights Jwt")
	public void Valid_Auth0_Token_Generates_Insights_JWT_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Sending auth0 token and getting user token");
		InsightsAuthServiceRequestBody authServiceRequestBody = new InsightsAuthServiceRequestBody(auth0Token);
		Response authServiceResponse = InsightsAuthServiceApiRequest.getUserToken(authServiceRequestBody, header, "", SecurityAPI.getBasicAuthValue());
		Assert.assertEquals(authServiceResponse.statusCode(), 200, "** Unable to get user token.");

		InsightsAuthServiceResponseBody authServiceResponseBody = authServiceResponse.getBody().as(InsightsAuthServiceResponseBody.class);
		String userToken = authServiceResponseBody.getJwt().getToken();

		logger.info("** Sending user token to generate a Jwt");
		Response jwtResponse = InsightsAuthServiceApiRequest.getJwt(authServiceResponseBody, "", "", userToken);
		Assert.assertEquals(jwtResponse.statusCode(), 200, "** Unable to get Insights Jwt.");
		String jwt = jwtResponse.jsonPath().getString("jwt");

		logger.info("** Making call to company schema endpoint using the Jwt");
		Response compSchemaResponse = InsightsAuthServiceApiRequest.getCompanySchema("", "", jwt);
		Assert.assertEquals(compSchemaResponse.statusCode(), 200, "** Call using Insights Jwt failed. Unable to get company schema.");

		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-66 - Verify expired Auth0 token does not generate an Insights Jwt")
	public void Expired_Auth0_Token_Does_Not_Generate_Insights_JWT_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Sending auth0 token and getting user token");
		InsightsAuthServiceRequestBody authServiceRequestBody = new InsightsAuthServiceRequestBody(expiredAuth0Token);
		Response authServiceResponse = InsightsAuthServiceApiRequest.getUserToken(authServiceRequestBody, header, "", SecurityAPI.getBasicAuthValue());
		Assert.assertEquals(authServiceResponse.statusCode(), 500, "** Expired auth0 token should expected to throw 500.");

		logger.info("** Test completed successfully");
	}

}