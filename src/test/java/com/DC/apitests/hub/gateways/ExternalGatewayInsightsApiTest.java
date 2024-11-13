package com.DC.apitests.hub.gateways;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.DC.testcases.BaseClass;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.hub.insights.authservice.InsightsAuthServiceApiRequest;

import io.restassured.response.Response;

public class ExternalGatewayInsightsApiTest {

	Logger logger;
	ReadConfig readConfig;
	String auth0Token;
	String insightsJwtAfterTokenExchange;
	BaseClass base;
	WebDriver driver;
	String header = "Content-Type=application/json";

	ExternalGatewayInsightsApiTest() throws IOException {
		logger = Logger.getLogger(ExternalGatewayInsightsApiTest.class);
		readConfig =  ReadConfig.getInstance();
		PropertyConfigurator.configure("log4j.properties");
		base = new BaseClass();
	}

	@BeforeClass
	private void getTestData(ITestContext testContext) throws Exception {
		driver = base.initializeBrowser(testContext, readConfig.getHeadlessMode());
		auth0Token = SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());
		insightsJwtAfterTokenExchange = SecurityAPI.getHubInsightsJwt(auth0Token);
	}

	@Test(description = "PH-92 - Verify Hub external gateway routes to Insights - Company Schema Properties")
	public void Hub_External_Gateway_Routing_To_Insights_Company_Schema_Properties_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making direct call to company schema properties endpoint");
		Response cspResponse = InsightsAuthServiceApiRequest.getCompanySchemaProperties(header, "", insightsJwtAfterTokenExchange);
		Assert.assertEquals(cspResponse.statusCode(), 200, "** Direct call with Insights Jwt not successful.");
		JSONArray cspResponseJson = new JSONArray(cspResponse.getBody().asString());

		logger.info("** Making call through hub external gateway to company schema properties endpoint");
		Response cspExternalGatewayResponse = InsightsAuthServiceApiRequest.getCompanySchemaPropertiesExternalGateway(header, "", "Bearer " + auth0Token);
		Assert.assertEquals(cspExternalGatewayResponse.statusCode(), 200, "** Call through external gateway not successful.");
		JSONArray cspExternalGatewayResponseJson = getExternalGatewayResponseInJsonArray(cspExternalGatewayResponse);

		Assert.assertTrue(cspResponseJson.similar(cspExternalGatewayResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-92 - Verify Hub external gateway routes to Insights - Company All Countries")
	public void Hub_External_Gateway_Routing_To_Insights_Company_Countries_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making direct call to company all countries endpoint");
		Response cspResponse = InsightsAuthServiceApiRequest.getCompanyAllCountries(header, "", insightsJwtAfterTokenExchange);
		Assert.assertEquals(cspResponse.statusCode(), 200, "** Direct call with Insights Jwt not successful.");
		JSONArray cspResponseJson = new JSONArray(cspResponse.getBody().asString());

		logger.info("** Making call through hub external gateway to company all countries endpoint");
		Response cspExternalGatewayResponse = InsightsAuthServiceApiRequest.getCompanyAllCountriesExternalGateway(header, "", "Bearer " + auth0Token);
		Assert.assertEquals(cspExternalGatewayResponse.statusCode(), 200, "** Call through external gateway not successful.");
		JSONArray cspExternalGatewayResponseJson = getExternalGatewayResponseInJsonArray(cspExternalGatewayResponse);

		Assert.assertTrue(cspResponseJson.similar(cspExternalGatewayResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-92 - Verify Hub external gateway routes to Insights - Company Schema")
	public void Hub_External_Gateway_Routing_To_Insights_Company_Schema_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making direct call to Company Schema endpoint");
		Response csResponse = InsightsAuthServiceApiRequest.getCompanySchema(header, "", insightsJwtAfterTokenExchange);
		Assert.assertEquals(csResponse.statusCode(), 200, "** Direct call with Insights Jwt not successful.");
		JSONObject csResponseJson = new JSONObject(csResponse.getBody().asString());

		logger.info("** Making call through hub external gateway to Company Schema endpoint");
		Response csExternalGatewayResponse = InsightsAuthServiceApiRequest.getCompanySchemaExternalGateway(header, "", "Bearer " + auth0Token);
		Assert.assertEquals(csExternalGatewayResponse.statusCode(), 200, "** Call through external gateway not successful.");
		JSONObject csExternalGatewayResponseJson = getExternalGatewayResponseInJsonObject(csExternalGatewayResponse);

		Assert.assertTrue(csResponseJson.similar(csExternalGatewayResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	private JSONArray getExternalGatewayResponseInJsonArray(Response response) {
		String resp = response.getBody().asString();
		JSONObject jsonObj = new JSONObject(resp);
		return jsonObj.getJSONArray("data");
	}

	private JSONObject getExternalGatewayResponseInJsonObject(Response response) {
		String resp = response.getBody().asString();
		JSONObject jsonObj = new JSONObject(resp);
		return jsonObj.getJSONObject("data");
	}

}