package com.DC.apitests.hub.token;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.adc.admin.AdminApiRequests;
import com.DC.utilities.apiEngine.apiRequests.adc.advertising.media.ReportingDashboardApiRequests;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail.RetailApiRequests;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.search.SearchApiRequests;
import com.DC.utilities.apiEngine.apiRequests.adc.daas.DataApiRequests;
import com.DC.utilities.apiEngine.models.requests.adc.admin.AdminManageQueriesRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.FlightdeckRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.MultiPlatformViewFiltersRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ReportingDashboardRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.advertising.media.MultiPlatformViewFilterCreationResponseBody;
import com.DC.utilities.apiEngine.models.responses.adc.advertising.media.MultiPlatformViewFiltersResponseBody;
import com.DC.utilities.apiEngine.models.responses.adc.advertising.media.SegmentationTypesResponseBody;
import com.DC.utilities.apiEngine.models.responses.adc.advertising.media.SegmentationTypesResponseBody.Segmentations;
import io.restassured.response.Response;

public class Auth0TokenForAdcApiTest {

	Logger logger;
	ReadConfig readConfig;
	String oktaToken;
	String auth0Token;
	String expiredAuth0Token;
	BaseClass base;
	WebDriver driver;
	FilaUser filaUser;
	String header = "Content-Type=application/json";
	String param = "platform=AMAZON";

	Auth0TokenForAdcApiTest() {
		logger = Logger.getLogger(Auth0TokenForAdcApiTest.class);
		readConfig =  ReadConfig.getInstance();
		PropertyConfigurator.configure("log4j.properties");
		base = new BaseClass();
	}

	@BeforeClass
	private void getTestData(ITestContext testContext) throws Exception {
		filaUser = new FilaUser();
		driver = base.initializeBrowser(testContext, false);
		oktaToken = SecurityAPI.getOktaTokenForUser(readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserOktaPassword());
		auth0Token = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());
		expiredAuth0Token = SecurityAPI.getExpiredAuth0TokenForFila();
		restoreFilaUser(filaUser, auth0Token);
	}

	private final String DAILY = "DAILY";
	private final String WEEKLY = "WEEKLY";

	@Test(description = "PH-60 - Verify Reporting Dashboard (Advertising) accepts Auth0 access token")
	public void ReportingDashboard_Auth0_Okta_Token_Api_Test() throws Exception {
		logger.info("** Test has started.");

		String expectedFirstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
		String expectedYesterday = DateUtility.getYesterday();
		ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(DAILY, "actual", "direct_sales", SharedMethods.createList("clickRevenue,relatedClickRevenue,brandClickRevenue"), expectedFirstDayOfLastSevenDays, expectedYesterday, 39, "AMAZON", null, "ATTR_14D");

		logger.info("** Making call with Okta token");
		Response responseWithOktaToken = ReportingDashboardApiRequests.reportingDashboard(rd, header, "", oktaToken);
		Assert.assertEquals(responseWithOktaToken.statusCode(), 200, "Okta call unsuccessful.");
		JSONObject oktaResponseJson = new JSONObject(responseWithOktaToken.getBody().asString());

		logger.info("** Making call with Auth0 token");
		Response responseWithAuth0Token = ReportingDashboardApiRequests.reportingDashboard(rd, header, "", auth0Token);
		Assert.assertEquals(responseWithAuth0Token.statusCode(), 200, "Auth0 call unsuccessful.");
		JSONObject authOResponseJson = new JSONObject(responseWithAuth0Token.getBody().asString());

		Assert.assertTrue(oktaResponseJson.similar(authOResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-60 - Verify Admin Client Selection (Admin) accepts Auth0 access token")
	public void AdminClientSelection_Auth0_Okta_Token_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making call with Okta token");
		Response responseWithOktaToken = AdminApiRequests.adminClientSelection(header, "", oktaToken);
		Assert.assertEquals(responseWithOktaToken.statusCode(), 200, "Okta call unsuccessful.");
		JSONObject oktaResponseJson = new JSONObject(responseWithOktaToken.getBody().asString());

		logger.info("** Making call with Auth0 token");
		Response responseWithAuth0Token = AdminApiRequests.adminClientSelection(header, "", auth0Token);
		Assert.assertEquals(responseWithAuth0Token.statusCode(), 200, "Auth0 call unsuccessful.");
		JSONObject authOResponseJson = new JSONObject(responseWithOktaToken.getBody().asString());

		Assert.assertTrue(oktaResponseJson.similar(authOResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-60 - Verify expired Auth0 token is not accepted - Flightdeck (Advertising)")
	public void Expired_Auth0_Token_FlightDeck_Api_Test() throws Exception {
		logger.info("** Test has started.");

		String expectedFirstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
		String expectedYesterday = DateUtility.getYesterday();

		FlightdeckRequestBody.PagingAttributes pa = new FlightdeckRequestBody.PagingAttributes(100, 1);
		FlightdeckRequestBody.DateRange dr = new FlightdeckRequestBody.DateRange("Last 7 Days");
		FlightdeckRequestBody rd = new FlightdeckRequestBody(pa, "CAMPAIGNS", dr, 39, expectedFirstDayOfLastSevenDays, expectedYesterday, "AMAZON", new ArrayList<>(), 1941);

		logger.info("** Making call with Okta token");
		Response responseWithOktaToken = ReportingDashboardApiRequests.flightdeck(rd, header, "", oktaToken, "amazon");
		Assert.assertEquals(responseWithOktaToken.statusCode(), 200, "Okta call unsuccessful.");

		logger.info("** Making call with expired Auth0 token");
		Response responseWithAuth0Token = ReportingDashboardApiRequests.flightdeck(rd, header, "", expiredAuth0Token, "amazon");
		Assert.assertEquals(responseWithAuth0Token.statusCode(), 500, "Expired auth0 token call successful.");

		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-60 - Verify Segmentation Groups (Catalog) accepts Auth0 access token")
	public void SegmentationGroups_Auth0_Okta_Token_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Making call with Okta token");
		Response responseWithOktaToken = RetailApiRequests.segmentationsGrouped(header, "", oktaToken, 39);
		Assert.assertEquals(responseWithOktaToken.statusCode(), 200, "Okta call unsuccessful.");
		JSONObject oktaResponseJson = new JSONObject(responseWithOktaToken.getBody().asString());

		logger.info("** Making call with Auth0 token");
		Response responseWithAuth0Token = RetailApiRequests.segmentationsGrouped(header, "", auth0Token, 39);
		Assert.assertEquals(responseWithAuth0Token.statusCode(), 200, "Call with Auth0 token unsuccessful.");
		JSONObject authOResponseJson = new JSONObject(responseWithOktaToken.getBody().asString());

		Assert.assertTrue(oktaResponseJson.similar(authOResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-60 - Verify Daas Automated Data Source Origins (Daas) accepts Auth0 access token")
	public void DaasAutomatedDataSourceOrigins_Auth0_Okta_Token_Api_Test() throws Exception {
		logger.info("** Test has started.");
		header = header + ";x-businessunitcontext=26";

		logger.info("** Making call with Okta token");
		Response responseWithOktaToken = DataApiRequests.daasDataSourceOrigins(header, "", oktaToken);
		Assert.assertEquals(responseWithOktaToken.statusCode(), 200, "Call with Okta token unsuccessful.");
		JSONArray oktaResponseJson = new JSONArray(responseWithOktaToken.getBody().asString());

		logger.info("** Making call with Auth0 token");
		Response responseWithAuth0Token =  DataApiRequests.daasDataSourceOrigins(header, "", auth0Token);
		Assert.assertEquals(responseWithAuth0Token.statusCode(), 200, "Call with Auth0 token unsuccessful.");
		JSONArray authOResponseJson = new JSONArray(responseWithOktaToken.getBody().asString());

		Assert.assertTrue(oktaResponseJson.similar(authOResponseJson), "** JSON response objects do not match!");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-60 - Verify Campaign Segmentation Manager accepts Auth0 access token to create/delete MPV filter")
	public void MPV_Create_Delete_Auth0_Okta_Token_Api_Test() throws Exception {
		logger.info("** Test has started.");
		header = header + ";x-businessunitcontext=39";

		logger.info("** Getting existing MPV filters");
		Response responseExistingMpvFilters = ReportingDashboardApiRequests.getExistingMpvFilters(header, "", auth0Token);
		Assert.assertEquals(responseExistingMpvFilters.statusCode(), 200, "Failure in getting existing MPV filters (Auth0 token).");

		MultiPlatformViewFiltersResponseBody mpvResponseBody =	responseExistingMpvFilters.getBody().as(MultiPlatformViewFiltersResponseBody.class);
		List<String> existingMpvFilters = mpvResponseBody.getExistingMpvFilters(mpvResponseBody);

		logger.info("** Getting all available MPV filter options");
		Response responseMpvFilterOptions = ReportingDashboardApiRequests.getAllMpvFilters(header, param, auth0Token, 39);
		Assert.assertEquals(responseMpvFilterOptions.statusCode(), 200, "Failure in getting all available MPV filter options (Auth0 token).");

		SegmentationTypesResponseBody stResponseBody = responseMpvFilterOptions.getBody().as(SegmentationTypesResponseBody.class);
		List<Segmentations> filterOptionsToSelect = stResponseBody.removeSelectedMpvFilterOptions(stResponseBody, existingMpvFilters);

		logger.info("** Creating a new MPV filter");
		MultiPlatformViewFiltersRequestBody.LinkDetails mpvFilterLinkDetails = new MultiPlatformViewFiltersRequestBody.LinkDetails("AMAZON", filterOptionsToSelect.get(0).getId(), filterOptionsToSelect.get(0).getSegmentationName(), filterOptionsToSelect.get(0).getSegmentationLabel());
		MultiPlatformViewFiltersRequestBody mpvFilterRequestBody = new MultiPlatformViewFiltersRequestBody(null, "Test MPV", Arrays.asList(mpvFilterLinkDetails));

		Response responseMpvCreation = ReportingDashboardApiRequests.createMpvFilter(mpvFilterRequestBody, header, "", auth0Token);
		Assert.assertEquals(responseMpvCreation.statusCode(), 200, "Failure in creating new filter (Auth0 token).");

		logger.info("** Deleting the MPV filter created");
		MultiPlatformViewFilterCreationResponseBody mpvCreationResponseBody = responseMpvCreation.getBody().as(MultiPlatformViewFilterCreationResponseBody.class);

		Response responseMpvDelete = ReportingDashboardApiRequests.deleteMpvFilter(header, auth0Token, mpvCreationResponseBody.getlinkId());
		Assert.assertEquals(responseMpvDelete.statusCode(), 200, "Failure in deleting the filter created (Auth0 token).");

		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-60 - Verify Search Manage Queries accepts Auth0 access token to import/export file")
	public void SearchManageQueries_Export_Import_File_Auth0_Okta_Token_Api_Test() throws Exception {
		logger.info("** Test has started.");

		logger.info("** Exporting CSV file");
		AdminManageQueriesRequestBody.PagingAttributes pa = new AdminManageQueriesRequestBody.PagingAttributes("keyword");
		AdminManageQueriesRequestBody amq = new AdminManageQueriesRequestBody(pa, 39, filaUser.getUserId(auth0Token), "box", false);

		Response responseExport = SearchApiRequests.exportManageQueries(amq, header, "", auth0Token);
		Assert.assertEquals(responseExport.statusCode(), 200, "Failure in exporting file (Auth0 token).");

		logger.info("** Getting file url and copying in folder");
		String exportUrl =  responseExport.jsonPath().getString("fileUrl");
		File file =	SharedMethods.importFileFromUrl(exportUrl, System.getProperty("user.dir") + "/downloads/import/keywords.csv");

		logger.info("** Importing the file");
		Response responseImport = SearchApiRequests.importManageQueries(file, "", auth0Token, 39);
		Assert.assertEquals(responseImport.statusCode(), 200, "Failure in importing file (Auth0 token)" );

		logger.info("** Test completed successfully");
	}

	private void restoreFilaUser(FilaUser filaUser, String authToken) throws Exception {
		String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: Base User","Internal Only: User Administration", "Internal Only: Role Administration");
		String buIds = filaUser.getFilaBus(authToken,"3M", "Hersheys - US");
		filaUser.updateFilaUser(authToken, userRolesIds, buIds, true, true);
	}
}