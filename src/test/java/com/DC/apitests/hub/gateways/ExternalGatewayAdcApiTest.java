package com.DC.apitests.hub.gateways;

import java.io.IOException;

import com.DC.testcases.BaseClass;
import com.DC.utilities.hub.FilaUser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.DC.utilities.DateUtility;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.adc.admin.AdminApiRequests;
import com.DC.utilities.apiEngine.apiRequests.adc.advertising.media.ReportingDashboardApiRequests;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.search.SearchApiRequests;
import com.DC.utilities.apiEngine.apiRequests.adc.daas.DataApiRequests;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ReportingDashboardRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.search.SearchOfVoiceRequestBody;
import io.restassured.response.Response;

public class ExternalGatewayAdcApiTest {

	Logger logger;
	ReadConfig readConfig;
	String oktaToken;
	String auth0Token;
	BaseClass base;
	WebDriver driver;
	FilaUser filaUser;
	String header = "Content-Type=application/json";

	ExternalGatewayAdcApiTest() throws IOException {
		logger = Logger.getLogger(ExternalGatewayAdcApiTest.class);
		readConfig =  ReadConfig.getInstance();
		PropertyConfigurator.configure("log4j.properties");
		base = new BaseClass();
	}

	@BeforeClass
	private void getTestData(ITestContext testContext) throws Exception {
		filaUser = new FilaUser();
		driver = base.initializeBrowser(testContext, readConfig.getHeadlessMode());
		oktaToken = SecurityAPI.getOktaTokenForUser(readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserOktaPassword());
		auth0Token = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());
		restoreFilaUser(filaUser, auth0Token);
	}

	private final String DAILY = "DAILY";

	@Test(description = "PH-92 - Verify Hub external gateway routes to Reporting Dashboard (Advertising)")
	public void Hub_External_Gateway_Routing_To_ReportingDashboard_Api_Test() throws Exception {
		logger.info("** Test has started.");

		String expectedFirstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
		String expectedYesterday = DateUtility.getYesterday();
		ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(DAILY, "actual", "direct_sales", SharedMethods.createList("clickRevenue,relatedClickRevenue,brandClickRevenue"), expectedFirstDayOfLastSevenDays, expectedYesterday, 39, "AMAZON", null, "ATTR_14D");

		logger.info("** Making call with Okta token");
		Response responseWithOktaToken = ReportingDashboardApiRequests.reportingDashboard(rd, header, "", oktaToken);
		Assert.assertEquals(responseWithOktaToken.statusCode(), 200, "Call with Okta token unsuccessful.");
		JSONObject oktaResponseJson = new JSONObject(responseWithOktaToken.getBody().asString());

		logger.info("** Making call with Auth0 token");
		Response responseWithAuth0Token = ReportingDashboardApiRequests.reportingDashboardExternalGateway(rd, header, "", auth0Token);
		Assert.assertEquals(responseWithAuth0Token.statusCode(), 200, "Call with Auth0 token unsuccessful.");
		JSONObject authOResponseJson = new JSONObject(responseWithAuth0Token.getBody().asString());

		Assert.assertTrue(oktaResponseJson.similar(authOResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-92 - Verify Hub external gateway routes to Search of Voice Brand (Catalog)")
	public void Hub_External_Gateway_Routing_To_SearchOfVoiceBrand_Api_Test() throws Exception {
		logger.info("** Test has started.");

		String expectedFirstDayOfLastFourteenDays = DateUtility.getFirstDayOfLastFourteenDays();
		String expectedYesterday = DateUtility.getYesterday();
		SearchOfVoiceRequestBody.DateRange dr = new SearchOfVoiceRequestBody.DateRange("Last 7 Days");
		SearchOfVoiceRequestBody rd = new SearchOfVoiceRequestBody(2, true, dr, expectedFirstDayOfLastFourteenDays, expectedYesterday, 39, "US", "NON_WEIGHTED", "amazon.com", "1", SharedMethods.createList("SP ATF"), SharedMethods.createList("watch"), true, "Amazon.com", "Amazon","1");

		logger.info("** Making call with Okta token");
		Response responseWithOktaToken = SearchApiRequests.sovBrand(rd, header, "", oktaToken);
		Assert.assertEquals(responseWithOktaToken.statusCode(), 200, "Call with Okta token unsuccessful.");
		JSONObject oktaResponseJson = new JSONObject(responseWithOktaToken.getBody().asString());

		logger.info("** Making call with Auth0 token");
		Response responseWithAuth0Token = SearchApiRequests.sovBrandExternalGateway(rd, header, "", auth0Token);
		Assert.assertEquals(responseWithAuth0Token.statusCode(), 200, "Call with Auth0 token unsuccessful.");
		JSONObject authOResponseJson = new JSONObject(responseWithAuth0Token.getBody().asString());

		Assert.assertTrue(oktaResponseJson.similar(authOResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-92 - Verify Hub external gateway routes to Admin Client Selection (Admin)")
	public void Hub_External_Gateway_Routing_To_AdminClientSelection_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making call with Okta token");
		Response responseWithOktaToken = AdminApiRequests.adminClientSelection(header, "", oktaToken);
		Assert.assertEquals(responseWithOktaToken.statusCode(), 200, "Call with Okta token unsuccessful.");
		JSONObject oktaResponseJson = new JSONObject(responseWithOktaToken.getBody().asString());

		logger.info("** Making call with Auth0 token");
		Response responseWithAuth0Token = AdminApiRequests.adminClientSelectionExternalGateway(header, "", auth0Token);
		Assert.assertEquals(responseWithAuth0Token.statusCode(), 200, "Call with Auth0 token unsuccessful.");
		JSONObject authOResponseJson = new JSONObject(responseWithOktaToken.getBody().asString());

		Assert.assertTrue(oktaResponseJson.similar(authOResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-92 - Verify Hub external gateway routes to Daas Automated Data Source Origins (Daas)")
	public void Hub_External_Gateway_Routing_To_DaasAutomatedDataSourceOrigins_Api_Test() throws Exception {
		logger.info("** Test has started.");
		header = header + ";x-businessunitcontext=26";

		logger.info("** Making call with Okta token");
		Response responseWithOktaToken = DataApiRequests.daasDataSourceOrigins(header, "", oktaToken);
		Assert.assertEquals(responseWithOktaToken.statusCode(), 200, "Call with Okta token unsuccessful.");
		JSONArray oktaResponseJson = new JSONArray(responseWithOktaToken.getBody().asString());

		logger.info("** Making call with Auth0 token");
		Response responseWithAuth0Token =  DataApiRequests.daasDataSourceOriginsExternalGateway(header, "", auth0Token);
		Assert.assertEquals(responseWithAuth0Token.statusCode(), 200, "Call with Auth0 token unsuccessful.");
		JSONArray authOResponseJson = new JSONArray(responseWithOktaToken.getBody().asString());

		Assert.assertTrue(oktaResponseJson.similar(authOResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	private void restoreFilaUser(FilaUser filaUser, String authToken) throws Exception {
		String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: Base User","Internal Only: User Administration", "Internal Only: Role Administration");
		String buIds = filaUser.getFilaBus(authToken,"3M", "Hersheys - US");
		filaUser.updateFilaUser(authToken, userRolesIds, buIds, true, true);
	}

}