package com.DC.apitests.hub.gateways;

import java.util.List;

import com.DC.testcases.BaseClass;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.hub.marketshare.authservice.MarketShareAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice.MarketShareUserInfoResponseBody;
import com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice.MarketShareUserInfoResponseBody.User;
import io.restassured.response.Response;

public class ExternalGatewayMarketShareApiTest {

	Logger logger;
	ReadConfig readConfig;
	String auth0Token;
	String randomUserApiKey;
	BaseClass base;
	WebDriver driver;
	String headers;

	ExternalGatewayMarketShareApiTest() {
		logger = Logger.getLogger(ExternalGatewayMarketShareApiTest.class);
		readConfig =  ReadConfig.getInstance();
		PropertyConfigurator.configure("log4j.properties");
		base = new BaseClass();
	}

	@BeforeClass
	private void getTestData(ITestContext testContext) throws Exception {
		driver = base.initializeBrowser(testContext, readConfig.getHeadlessMode());
		auth0Token = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubEdgeUserEmail(), readConfig.getHubEdgeUserPassword());
	}

	@BeforeMethod
	private void getMsUserApiKey() throws Exception {
		MarketShareUserInfoResponseBody userInfo = MarketShareAuthServiceApiRequest.getUserInfoResponseBodyExternalGateway(auth0Token);
		List<User> users = userInfo.getUsers();
		User randomUserAssociatedWithEmail = (User) SharedMethods.getRandomItemFromList(users);
		randomUserApiKey = randomUserAssociatedWithEmail.getKey().getKey();
		headers = "X-API-KEY="+randomUserApiKey;
	}

	@Test(description = "PH-120 - Verify Hub external gateway routes to Market Share - User Info")
	public void Hub_External_Gateway_Routing_To_MarketShare_UserInfo_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making direct call to User Info endpoint");
		Response userInfoResponse = MarketShareAuthServiceApiRequest.getUserInfo(auth0Token);
		Assert.assertEquals(userInfoResponse.statusCode(), 201, "** Direct call to User Info endpoint unsuccessful.");
		JSONObject uiResponseJson = new JSONObject(userInfoResponse.getBody().asString());

		logger.info("** Making call through hub external gateway to User Info endpoint");
		Response userInfoExternalGatewayResponse = MarketShareAuthServiceApiRequest.getUserInfoExternalGateway(auth0Token);
		Assert.assertEquals(userInfoExternalGatewayResponse.statusCode(), 201, "** Call through external gateway not successful.");
		JSONObject uiExternalGatewayResponse = new JSONObject(userInfoExternalGatewayResponse.getBody().asString());

		Assert.assertTrue(uiResponseJson.similar(uiExternalGatewayResponse), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-120-489 - Verify Hub external gateway routes to MS and MS accepts Auth0 token - Tld Currency")
	public void Hub_External_Gateway_Routing_To_MarketShare_Tld_Currency_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making direct call to Tld Currency endpoint");
		Response clientsResponse = MarketShareAuthServiceApiRequest.getTldCurrency("X-API-KEY "+ randomUserApiKey);
		Assert.assertEquals(clientsResponse.statusCode(), 200, "** Direct call to Clients endpoint unsuccessful.");
		JSONObject clientsResponseJson = new JSONObject(clientsResponse.getBody().asString());

		logger.info("** Making call through hub external gateway to Tld Currency endpoint");
		Response clientsExternalGatewayResponse = MarketShareAuthServiceApiRequest.getTldCurrencyExternalGateway(headers, auth0Token);
		Assert.assertEquals(clientsExternalGatewayResponse.statusCode(), 200, "** Call through external gateway not successful.");
		JSONObject clientsExternalGatewayResponseJson = new JSONObject(clientsExternalGatewayResponse.getBody().asString());

		Assert.assertTrue(clientsResponseJson.similar(clientsExternalGatewayResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-120-489 - Verify Hub external gateway routes to MS and MS accepts Auth0 token - Tlds")
	public void Hub_External_Gateway_Routing_To_MarketShare_Tlds_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making direct call to Tlds endpoint");
		Response tldsResponse = MarketShareAuthServiceApiRequest.getTlds("X-API-KEY "+ randomUserApiKey);
		Assert.assertEquals(tldsResponse.statusCode(), 200, "** Direct call to Tlds endpoint unsuccessful.");
		JSONObject tldsResponseJson = new JSONObject(tldsResponse.getBody().asString());

		logger.info("** Making call through hub external gateway to Tlds endpoint");
		Response tldsExternalGatewayResponse = MarketShareAuthServiceApiRequest.getTldsExternalGateway(headers, auth0Token);
		Assert.assertEquals(tldsExternalGatewayResponse.statusCode(), 200, "** Call through external gateway not successful.");
		JSONObject tldsExternalGatewayResponseJson = new JSONObject(tldsExternalGatewayResponse.getBody().asString());

		Assert.assertTrue(tldsResponseJson.similar(tldsExternalGatewayResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-489 - Hub cannot call MS endpoint without Auth0 token")
	public void Hub_External_Gateway_MarketShare_Call_Without_Auth0_Token_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making Hub call using only x-api-key");
		Response clientsExternalGatewayResponse = MarketShareAuthServiceApiRequest.getTldCurrencyExternalGateway("", "X-API-KEY "+ randomUserApiKey);
		Assert.assertEquals(clientsExternalGatewayResponse.statusCode(), 401, "** Hub call to MS endpoint without Auth0 token is successful.");

		logger.info("** Test completed successfully");
	}

}