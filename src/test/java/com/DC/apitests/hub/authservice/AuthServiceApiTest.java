package com.DC.apitests.hub.authservice;

import java.util.*;
import com.DC.db.hubDbFunctions.HubDbFunctions;
import com.DC.db.hubDbFunctions.UserBuModule;
import com.DC.db.hubDbFunctions.UserModule;
import com.DC.testcases.BaseClass;
import com.DC.utilities.apiEngine.models.responses.hub.aggregation.RetailerPlatform;
import com.DC.db.hubDbFunctions.UserBuAggregation;
import com.DC.utilities.RedisUtility;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.hub.HubCommonMethods;
import com.DC.utilities.hub.MsMethods;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;
import com.DC.utilities.hub.FilaUser;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.admin.AdminApiRequests;
import com.DC.utilities.apiEngine.apiRequests.hub.authservice.AuthServiceApiRequest;
import com.DC.utilities.apiEngine.apiRequests.hub.marketshare.authservice.MarketShareAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.responses.hub.authservice.UserAuthorization;
import com.DC.utilities.apiEngine.models.responses.hub.authservice.UserAuthorization.Data;
import io.restassured.response.Response;
import org.testng.asserts.SoftAssert;

public class AuthServiceApiTest {

	Logger logger;
	ReadConfig readConfig;
	BaseClass base;
	WebDriver driver;
	boolean headless;
	FilaUser filaUser;
	MsMethods msMethods;

	AuthServiceApiTest() {
		logger = Logger.getLogger(AuthServiceApiTest.class);
		readConfig =  ReadConfig.getInstance();
		PropertyConfigurator.configure("log4j.properties");
		base = new BaseClass();
		headless = readConfig.getHeadlessMode();
		filaUser = new FilaUser();
		msMethods = new MsMethods();
	}

	@Test(description = "PH-99 - AuthService Health Checks")
	public void Hub_Auth_Service_Health_Checks_Api_Test() throws Exception {
		logger.info("** Test has started.");
		Response healthResponse = AuthServiceApiRequest.getLiveHealthCheck();
		Assert.assertEquals(healthResponse.statusCode(), 200, "Health check unsuccessful.");

		Response readyResponse = AuthServiceApiRequest.getReadyHealthCheck();
		Assert.assertEquals(readyResponse.statusCode(), 200, "Ready check unsuccessful.");
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-99 - Auth User Authorization")
	public void Hub_Authorize_Auth_User_Api_Test(ITestContext testContext) throws Exception {
		logger.info("** Test has started.");
		driver = base.initializeBrowser(testContext, headless);
		String insightsOnlyAuthToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());

		Response userResponse = AuthServiceApiRequest.getAuthorization(insightsOnlyAuthToken);
		Assert.assertEquals(userResponse.statusCode(), 200, "Authorization call unsuccessful.");

		UserAuthorization user = userResponse.getBody().as(UserAuthorization.class);
		Data userData = user.getData();

		Assert.assertTrue(user.success, "** User info not fetched successfully");
		Assert.assertNotNull(userData, "** User data is null");
		Assert.assertNotNull(userData.sub, "** User auth id is null");
		Assert.assertNotNull(userData.nickname, "** User nickname is null");
		Assert.assertNotNull(userData.name, "** User name is null");
		Assert.assertNotNull(userData.picture, "** User picture url is null");
		Assert.assertEquals(userData.email, readConfig.getHubInsightsUserEmail(), "** User email does not match");

		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-102 - Auth Service API - Insights Only")
	public void Hub_Sync_User_Info_Into_Hub_Insights_Only_Auth_User_Api_Test(ITestContext testContext) throws Exception {
		logger.info("** Test has started.");

		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		HubDbFunctions db = new HubDbFunctions();
		RedisUtility redis = new RedisUtility();
		SoftAssert softAssert = new SoftAssert();

		String auth0Id = db.getUserAuth0Id(readConfig.getHubInsightsUserEmail());
		redis.clearRedisCache("oneSpaceTokenCache::" + auth0Id);

		driver = base.initializeBrowser(testContext, headless);
		String insightsOnlyAuthToken = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubInsightsUserEmail(), readConfig.getHubInsightsUserPassword());
		softAssert.assertEquals(redis.getRedisCache("oneSpaceTokenCache::" + auth0Id), db.getUserRolesForInsights(readConfig.getHubInsightsUserEmail()), "Cleared Redis. New jwt in Redis and DB should match.");

		Response userResponse = hubCommonMethods.getUserHubInfo(insightsOnlyAuthToken);
		String hubNewJwtForInsightUser = (String)  hubCommonMethods.getUserPlatformInfo(userResponse, "onespace");
		softAssert.assertEquals(redis.getRedisCache("oneSpaceTokenCache::" + auth0Id), hubNewJwtForInsightUser, "Jwt cached in Redis. Redis and userinfo response should match.");
		softAssert.assertAll();

		logger.info("** Test completed successfully");
	}

	@Test( description = "PH-102 - Auth Service API - Fila Only")
	public void Hub_Sync_User_Info_Into_Hub_Fila_Only_Auth_User_Api_Test(ITestContext testContext) throws Exception {
		logger.info("** Test has started.");
		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		driver = base.initializeBrowser(testContext, headless);
		String filaToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());

		updateFilaUser(filaUser, filaToken);
		JSONObject filaUserInfo = AdminApiRequests.getAdminUserInfoJson(filaToken);

		Response userResponse = hubCommonMethods.getUserHubInfo(filaToken);
		JSONObject hubUserInfoForFilaUser = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "fila");
		restoreFilaUser(filaUser, filaToken);
		Assert.assertTrue(filaUserInfo.similar(hubUserInfoForFilaUser), "Updated and synced user info are not the same.");

		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-102 - Auth Service API - Edge Only")
	public void Hub_Sync_User_Info_Into_Hub_Edge_Only_Auth_User_Api_Test(ITestContext testContext) throws Exception {
		logger.info("** Test has started.");

		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		driver = base.initializeBrowser(testContext, headless);
		String	edgeTokenForAdmin = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubEdgeUserEmail(), readConfig.getHubEdgeUserPassword());
		driver = base.initializeBrowser(testContext, headless);
		String	edgeTokenForUserToUpdate = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubEdgeOnlyUserEmail(), readConfig.getHubEdgeOnlyUserPassword());
		msMethods.updateEdgeUser(edgeTokenForAdmin, edgeTokenForUserToUpdate, 600);

		msMethods.updateEdgeUser(edgeTokenForAdmin, edgeTokenForUserToUpdate, 200);
		Response userResponse = hubCommonMethods.getUserHubInfo(edgeTokenForUserToUpdate);
		JSONObject hubUserInfoForEdgeUser = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "marketshare");

		JSONObject edgeUserInfo = MarketShareAuthServiceApiRequest.getUserInfoJson(edgeTokenForUserToUpdate);
		Assert.assertTrue(edgeUserInfo.similar(hubUserInfoForEdgeUser), "Updated and synced user info are not the same.");

		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-102 - Auth Service API - Fila Insights Edge")
	public void Hub_Sync_User_Info_Into_Hub_Fila_Insights_Edge_Auth_User_Api_Test(ITestContext testContext) throws Exception {
		logger.info("** Test has started.");

		SoftAssert softAssert = new SoftAssert();
		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		HubDbFunctions db = new HubDbFunctions();
		RedisUtility redis = new RedisUtility();

		String auth0Id = db.getUserAuth0Id(readConfig.getHubFilaInsightsEdgeUserEmail());
		redis.clearRedisCache("oneSpaceTokenCache::" + auth0Id);

		driver = base.initializeBrowser(testContext, headless);
		String	filaInsightsEdgeToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaInsightsEdgeUserEmail(), readConfig.getHubFilaInsightsEdgeUserPassword());
		driver = base.initializeBrowser(testContext, headless);
		String	edgeTokenForAdmin = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubEdgeUserEmail(), readConfig.getHubEdgeUserPassword());
		softAssert.assertEquals(redis.getRedisCache("oneSpaceTokenCache::" + auth0Id), db.getUserRolesForInsights(readConfig.getHubFilaInsightsEdgeUserEmail()), "Cleared Redis. New jwt in Redis and DB should match.");

		updateFilaUser(filaUser, filaInsightsEdgeToken);
		msMethods.updateEdgeUser(edgeTokenForAdmin, filaInsightsEdgeToken, 200);
		Response userResponse = hubCommonMethods.getUserHubInfo(filaInsightsEdgeToken);

		String hubNewJwtForInsightUser = (String)  hubCommonMethods.getUserPlatformInfo(userResponse, "onespace");
		softAssert.assertEquals(redis.getRedisCache("oneSpaceTokenCache::" + auth0Id), hubNewJwtForInsightUser, "Jwt cached in Redis. Redis and userinfo response should match.");

		JSONObject hubUserInfoForFilaUser = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "fila");
		JSONObject filaUserInfo = AdminApiRequests.getAdminUserInfoJson(filaInsightsEdgeToken);
		softAssert.assertTrue(filaUserInfo.similar(hubUserInfoForFilaUser), "Updated and synced user info are not the same for Fila.");

		JSONObject hubUserInfoForEdgeUser = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "marketshare");
		JSONObject edgeUserInfo = MarketShareAuthServiceApiRequest.getUserInfoJson(filaInsightsEdgeToken);
		softAssert.assertTrue(edgeUserInfo.similar(hubUserInfoForEdgeUser), "Updated and synced user info are not the same for Edge.");

		msMethods.updateEdgeUser(edgeTokenForAdmin, filaInsightsEdgeToken, 600);
		restoreFilaUser(filaUser, filaInsightsEdgeToken);

		softAssert.assertAll();
		logger.info("** Test completed successfully");
	}

	@Test(dataProvider = "Hub_NetNew_User_Info", dataProviderClass = AuthServiceDataProvider.class, description = "PH-502 - Hub User Organizations - Net new authorization userinfo for classifier user to 1 BU")
	public void Hub_NetNew_User_Info_Api_Test(ITestContext testContext, Dictionary<String, String> user) throws Exception {
		logger.info("** Test has started.");

		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		HubDbFunctions db = new HubDbFunctions();
		SoftAssert softAssert = new SoftAssert();
		driver = base.initializeBrowser(testContext, headless);
		String userToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, user.get("username"), user.get("password"));

		String userAuth0Id = hubCommonMethods.getAuth0IdFromToken(userToken);
		Response userResponse = hubCommonMethods.getUserHubInfo(userToken);

		JSONObject hubUserInfoForFilaUser = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "netnew");
		JSONObject userInfo = hubUserInfoForFilaUser.getJSONObject("userInformation");
		Assert.assertEquals(userInfo.getString("email"), user.get("username"), "User emails do not match.");

		List<JSONObject> organizationsForUser = hubCommonMethods.getOrganizationsForUser(hubUserInfoForFilaUser);
		List<String> organizationIdsForUser = hubCommonMethods.getOrganizationIdsForUser(hubUserInfoForFilaUser);
		List<String> organizationIdsForUserDb = db.getOrganizationsForUser(userAuth0Id);
		softAssert.assertEquals(organizationIdsForUser, organizationIdsForUserDb, "Organizations assigned to user("+organizationIdsForUser+") do not match with DB("+organizationIdsForUserDb+"). Json: " + hubUserInfoForFilaUser);

		JSONObject randomOrganization = (JSONObject) SharedMethods.getRandomItemFromList(organizationsForUser);
		String organizationId = randomOrganization.getString("organizationId");
		List<JSONObject> businessUnitsForUser = hubCommonMethods.getBusinessUnitsForUser(randomOrganization);
		List<String> getBusinesUnitIdsForUser = hubCommonMethods.getBusinessUnitIdsForUser(randomOrganization);
		List<String> getBusinesUnitIdsForUserDb = db.getBusinessUnitsForUser(userAuth0Id, organizationId);
		softAssert.assertEquals(getBusinesUnitIdsForUser, getBusinesUnitIdsForUserDb, "Business Units assigned to organization("+getBusinesUnitIdsForUser+") do not match with DB("+getBusinesUnitIdsForUserDb+"). Json: " + hubUserInfoForFilaUser);

		JSONObject randomBusinessUnit = (JSONObject) SharedMethods.getRandomItemFromList(businessUnitsForUser);
		String businessUnitId = randomBusinessUnit.getString("businessUnitId");
		List<JSONObject> buRetailersForUser = hubCommonMethods.getBuRetailersForUser(randomBusinessUnit);
		List<String> buRetailerIdsForUser = hubCommonMethods.getBuRetailerIdsForUser(randomBusinessUnit);
		List<String> buRetailerIdsForUserDb = db.getBuRetailersForUser(userAuth0Id, organizationId, businessUnitId);
		softAssert.assertEquals(buRetailerIdsForUser, buRetailerIdsForUserDb, "Retailers assigned to BU("+buRetailerIdsForUser+") do not match with DB("+buRetailerIdsForUserDb+"). Json: " + hubUserInfoForFilaUser);

		for(JSONObject buRetailerForUser: buRetailersForUser){
			String retailerId = buRetailerForUser.getString("retailerId");
			List<String> buRetailerPlatformIdsForUser = hubCommonMethods.getBuRetailerPlatformIdsForUser(buRetailerForUser);
			List<String> buRetailerPlatformIdsForUserDb = db.getBuRetailerPlatformsForUser(userAuth0Id, organizationId, businessUnitId, retailerId);
			softAssert.assertEquals(buRetailerPlatformIdsForUser, buRetailerPlatformIdsForUserDb, "Platforms assigned to retailer("+buRetailerPlatformIdsForUser+") do not match with DB("+buRetailerPlatformIdsForUserDb+"). Json: " + hubUserInfoForFilaUser);
		}

		softAssert.assertAll();
		logger.info("** Test completed successfully");
	}

	@Test(dataProvider = "Hub_NetNew_User_Info", dataProviderClass = AuthServiceDataProvider.class, description = "PH-565 - Hub User Module Provisions")
	public void Hub_User_Module_Provisions_Api_Test(ITestContext testContext, Dictionary<String, String> user) throws Exception {
		logger.info("** Test has started.");

		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		SoftAssert softAssert = new SoftAssert();
		HubDbFunctions db = new HubDbFunctions();
		driver = base.initializeBrowser(testContext, headless);
		String userToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, user.get("username"), user.get("password"));

		String auth0Id = hubCommonMethods.getAuth0IdFromToken(userToken);
		List<String> buIdsForUserDb = db.getBusinessUnitsForAuthorizeUser(auth0Id);

		Response userResponse = hubCommonMethods.getUserHubInfo(userToken);

		JSONObject hubUserInfo = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "netnew");
		JSONObject userInfo = hubUserInfo.getJSONObject("userInformation");
		Assert.assertEquals(userInfo.getString("email"), user.get("username"), "User emails do not match.");

		List<JSONObject>  buModules = hubCommonMethods.getBusinessUnitModulesForUserInfo(hubUserInfo);
		List<String> buIdsForModules = hubCommonMethods.getBusinessUnitIdsForModules(buModules);
		softAssert.assertEquals(buIdsForModules, buIdsForUserDb, "BU ids for Modules ("+buIdsForModules+") do not match with Db ("+buIdsForUserDb+"). Json: " + hubUserInfo);

		JSONObject randomBuModules = (JSONObject) SharedMethods.getRandomItemFromList(buModules);
		List<String> moduleIdsForBu = hubCommonMethods.getModuleIdsForBusinessUnit(randomBuModules);
		List<String> moduleIdsForUserDb = db.getModuleIdsForAuthorizeUser(auth0Id, randomBuModules.getString("businessUnitId"));
		softAssert.assertEquals(moduleIdsForBu, moduleIdsForUserDb, "Module ids for BU ("+moduleIdsForBu+") do not match with Db ("+moduleIdsForUserDb+"). Json: " + randomBuModules);

		List<JSONObject>  userModules = hubCommonMethods.getBusinessUnitAgnosticModulesForUserInfo(hubUserInfo);
		List<String> buAgnosticModulesIds = hubCommonMethods.getBusinessUnitAgnosticModuleIdsForAuthorizeUser(userModules);
		List<String> buAgnosticModulesIdsDb = db.getBuAgnosticModuleIdsForUser(auth0Id);
		softAssert.assertEquals(buAgnosticModulesIds, buAgnosticModulesIdsDb, "Module ids for User ("+buAgnosticModulesIds+") do not match with Db ("+buAgnosticModulesIdsDb+"). Json: " + hubUserInfo);

		softAssert.assertAll();
		logger.info("** Test completed successfully");
	}

	@Test(dataProvider = "Hub_NetNew_User_Info", dataProviderClass = AuthServiceDataProvider.class, description = "PNN-243 (PH-244) - Hub User Authorization BU Provisions")
	public void Hub_NetNew_User_Authorization_Bu_Provisions_Api_Test(ITestContext testContext, Dictionary<String, String> user) throws Exception {
		logger.info("** Test has started.");

		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		HubDbFunctions db = new HubDbFunctions();
		SoftAssert softAssert = new SoftAssert();
		driver = base.initializeBrowser(testContext, headless);
		String userToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, user.get("username"), user.get("password"));
		String auth0Id = hubCommonMethods.getAuth0IdFromToken(userToken);

		List<String> buIdsForUserDb = db.getBusinessUnitsForAuthorizeUser(auth0Id);
		String buId = (String) SharedMethods.getRandomItemFromList(buIdsForUserDb);
		String headers = "X-BusinessUnitContext=" + buId;
		JSONObject userAuthorizations = AuthServiceApiRequest.getUserAuthorizationJson(userToken, headers, "");

		List<JSONObject>  bus = hubCommonMethods.getBusinessUnitsForAuthorizeUser(userAuthorizations);
		List<String> buIdsForUser = hubCommonMethods.getBusinessUnitIdsForAuthorizeUser(userAuthorizations);
		softAssert.assertEquals(buIdsForUser, buIdsForUserDb, "Business Units assigned to User("+buIdsForUser+") do not match with DB("+buIdsForUserDb+"). Json: " + userAuthorizations);

		JSONObject randomBusinessUnit = (JSONObject) SharedMethods.getRandomItemFromList(bus);
		String businessUnitId = randomBusinessUnit.getString("businessUnitId");
		List<JSONObject> buRetailersForUser = hubCommonMethods.getBuRetailersForUser(randomBusinessUnit);
		List<String> buRetailerIdsForUser = hubCommonMethods.getBuRetailerIdsForUser(randomBusinessUnit);
		List<String> buRetailerIdsForUserDb = db.getBuRetailersForAuthorizeUser(auth0Id, businessUnitId);
		softAssert.assertEquals(buRetailerIdsForUser, buRetailerIdsForUserDb, "Retailers assigned to BU("+buRetailerIdsForUser+") do not match with DB("+buRetailerIdsForUserDb+"). Json: " + userAuthorizations);

		for(JSONObject buRetailerForUser: buRetailersForUser){
			String retailerId = buRetailerForUser.getString("retailerId");
			List<String> buRetailerPlatformIdsForUser = hubCommonMethods.getBuRetailerPlatformIdsForAuthorizeUser(buRetailerForUser);
			List<String> buRetailerPlatformIdsForUserDb = db.getBuRetailerPlatformsForAuthorizeUser(auth0Id, businessUnitId, retailerId);
			softAssert.assertEquals(buRetailerPlatformIdsForUser, buRetailerPlatformIdsForUserDb, "Platforms assigned to retailer("+buRetailerPlatformIdsForUser+") do not match with DB("+buRetailerPlatformIdsForUserDb+"). Json: " + userAuthorizations);
		}

		softAssert.assertAll();
		logger.info("** Test completed successfully");
	}

	@Test(dataProvider = "Hub_NetNew_User_Info", dataProviderClass = AuthServiceDataProvider.class, description = "PNN-243 (PH-244) - Hub User Authorization Module Provisions")
	public void Hub_NetNew_User_Authorization_Module_Provisions_Api_Test(ITestContext testContext, Dictionary<String, String> user) throws Exception {
		logger.info("** Test has started.");

		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		SoftAssert softAssert = new SoftAssert();
		HubDbFunctions db = new HubDbFunctions();
		driver = base.initializeBrowser(testContext, headless);
		String userToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, user.get("username"), user.get("password"));
		String auth0Id = hubCommonMethods.getAuth0IdFromToken(userToken);

		List<String> buIdsForUserDb = db.getBusinessUnitsForAuthorizeUser(auth0Id);
		String buId = (String) SharedMethods.getRandomItemFromList(buIdsForUserDb);

		String headers = "X-BusinessUnitContext=" + buId;
		JSONObject userAuthorizations = AuthServiceApiRequest.getUserAuthorizationJson(userToken, headers, "");
		String userAuth0IdFromJson = userAuthorizations.getJSONObject("data").getString("authId");
		softAssert.assertEquals(userAuth0IdFromJson, auth0Id, "Auth0 id in response and in token does not match.");

		List<JSONObject>  buModules = hubCommonMethods.getBusinessUnitModulesForAuthorizeUser(userAuthorizations);
		List<String> buIdsForModules = hubCommonMethods.getBusinessUnitIdsForModules(buModules);
		softAssert.assertEquals(buIdsForModules, buIdsForUserDb, "BU ids for Modules ("+buIdsForModules+") do not match with Db ("+buIdsForUserDb+"). Json: " + userAuthorizations);

		JSONObject randomBuModules = (JSONObject) SharedMethods.getRandomItemFromList(buModules);
		List<String> moduleIdsForBu = hubCommonMethods.getModuleIdsForBusinessUnit(randomBuModules);
		List<String> moduleIdsForUserDb = db.getModuleIdsForAuthorizeUser(auth0Id, randomBuModules.getString("businessUnitId"));
		softAssert.assertEquals(moduleIdsForBu, moduleIdsForUserDb, "Module ids for BU ("+moduleIdsForBu+") do not match with Db ("+moduleIdsForUserDb+"). Json: " + randomBuModules);

		List<JSONObject>  userModules = hubCommonMethods.getBusinessUnitAgnosticModulesForAuthorizeUser(userAuthorizations);
		List<String> buAgnosticModulesIds = hubCommonMethods.getBusinessUnitAgnosticModuleIdsForAuthorizeUser(userModules);
		List<String> buAgnosticModulesIdsDb = db.getBuAgnosticModuleIdsForUser(auth0Id);
		softAssert.assertEquals(buAgnosticModulesIds, buAgnosticModulesIdsDb, "Module ids for User ("+buAgnosticModulesIds+") do not match with Db ("+buAgnosticModulesIdsDb+"). Json: " + userAuthorizations);

		softAssert.assertAll();
		logger.info("** Test completed successfully");
	}

	@Test(description = "PNN-243 (PH-244) - X-BusinessUnitContext Header is Missing")
	public void Hub_NetNew_User_Authorization_Bu_Header_Missing_Api_Test(ITestContext testContext) throws Exception {
		logger.info("** Test has started.");

		String header = "X-BusinessUnitContext= ";
		driver = base.initializeBrowser(testContext, headless);
		String userToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaInsightsUserEmail(), readConfig.getHubFilaInsightsUserPassword());
		Response response = AuthServiceApiRequest.getUserAuthorization(userToken, header, "");
		Assert.assertEquals(response.statusCode(), 400, "Expected to fail with 400 but got " + response.statusCode());

		logger.info("** Test completed successfully");
	}

	@Test(description = "PNN-243 (PH-244) - X-RetailerPlatformContext Header Not Assigned to BU")
	public void Hub_NetNew_User_Authorization_RetailerPlatform_Header_Not_Assigned_To_Bu_Api_Test(ITestContext testContext) throws Exception {
		logger.info("** Test has started.");

		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		HubDbFunctions db = new HubDbFunctions();
		driver = base.initializeBrowser(testContext, headless);
		String userToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaInsightsUserEmail(), readConfig.getHubFilaInsightsUserPassword());
		String auth0Id = hubCommonMethods.getAuth0IdFromToken(userToken);

		List<String> buIdsForUserDb = db.getBusinessUnitsForAuthorizeUser(auth0Id);
		String buId = (String) SharedMethods.getRandomItemFromList(buIdsForUserDb);

		List<String> dbPlatforms = db.getRetailerPlatforms();
		List<String> dbPlatformsForUser = db.getRetailerPlatformsForUser(auth0Id, buId);
		dbPlatforms.removeAll(dbPlatformsForUser);
		String dbPlatform = (String) SharedMethods.getRandomItemFromList(dbPlatforms);

		String headers = "X-BusinessUnitContext=" + buId + ";";
		headers = headers + "X-RetailerPlatformContext=" + dbPlatform;

		Response response = AuthServiceApiRequest.getUserAuthorization(userToken, headers, "");
		Assert.assertEquals(response.statusCode(), 403, "Expected to fail with 403 but got " + response.statusCode());

		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-243 - Redis Caching - Json Validation")
	public void Hub_NetNew_User_Authorization_Redis_Validation_Api_Test(ITestContext testContext) throws Exception {
		logger.info("** Test has started.");

		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		HubDbFunctions db = new HubDbFunctions();
		RedisUtility redis = new RedisUtility();
		driver = base.initializeBrowser(testContext, headless);
		String userToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaInsightsUserEmail(), readConfig.getHubFilaInsightsUserPassword());
		String auth0Id = hubCommonMethods.getAuth0IdFromToken(userToken);

		redis.clearRedisCache("authContextCache::" + auth0Id);
		List<String> buIdsForUserDb = db.getBusinessUnitsForAuthorizeUser(auth0Id);
		String buId = (String) SharedMethods.getRandomItemFromList(buIdsForUserDb);

		String headers = "X-BusinessUnitContext=" + buId;
		JSONObject jsonApiCall = AuthServiceApiRequest.getUserAuthorizationJson(userToken, headers, "");
		JSONObject jsonApi = jsonApiCall.getJSONObject("data");
		JSONObject jsonRedis = redis.getRedisCacheJson("authContextCache::" + auth0Id);
		Assert.assertTrue(jsonApi.similar(jsonRedis), "Json cached in Redis does not match with the one from API call.");

		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-565 - BU Aggregations - BU Mappings")
	public void Hub_Bu_Aggregations_Bu_Mappings_Api_Test(ITestContext testContext) throws Exception {
		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		HubDbFunctions db = new HubDbFunctions();
		SoftAssert softAssert = new SoftAssert();
		driver = base.initializeBrowser(testContext, headless);
		String userToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaOnlyUserEmail(), readConfig.getHubFilaOnlyUserPassword());
		String auth0Id = hubCommonMethods.getAuth0IdFromToken(userToken);

		Response userResponse = hubCommonMethods.getUserHubInfo(userToken);
		JSONObject hubUserInfoForFilaUser = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "netnew");
		List<JSONObject> organizationsForUser = hubCommonMethods.getOrganizationsForUser(hubUserInfoForFilaUser);
		JSONObject randomOrganization = (JSONObject) SharedMethods.getRandomItemFromList(organizationsForUser);
		String organizationId = randomOrganization.getString("organizationId");

		List<JSONObject> businessUnitsForUser = hubCommonMethods.getBusinessUnitsForUser(randomOrganization);
		JSONObject randomBusinessUnit = (JSONObject) SharedMethods.getRandomItemFromList(businessUnitsForUser);
		String businessUnitId = randomBusinessUnit.getString("businessUnitId");

		List<JSONObject> buRetailersForUser = hubCommonMethods.getBuRetailersForUser(randomBusinessUnit);
		JSONObject randomRetailer = (JSONObject) SharedMethods.getRandomItemFromList(buRetailersForUser);

		List<JSONObject> buRetailerPlatformsForUser = hubCommonMethods.getBuRetailerPlatformsForUser(randomRetailer);
		JSONObject randomRetailerPlatform = (JSONObject) SharedMethods.getRandomItemFromList(buRetailerPlatformsForUser);
		String retailerPlatformId = randomRetailerPlatform.getString("retailerPlatformId");

		RetailerPlatform retailerPlatform = new ObjectMapper().readValue(randomRetailerPlatform.toString(), RetailerPlatform.class);
		List<UserBuAggregation> buAggregations = db.getUserBuAggregations(auth0Id, organizationId, businessUnitId, retailerPlatformId);

		if (buRetailerPlatformsForUser.size() > 0 && buAggregations.size() < 1 ) {
			Assert.fail("Retailer platform not available in Db.");
		}

		for (UserBuAggregation userBuAggregation : buAggregations) {
			softAssert.assertEquals(userBuAggregation.getCurrencyCode(), retailerPlatform.getCurrencyCode(), "Currency codes do not match.");
			softAssert.assertEquals(userBuAggregation.getRegion(), retailerPlatform.getRegion(), "Regions do not match.");
			softAssert.assertEquals(userBuAggregation.getCurrencySymbol(), retailerPlatform.getCurrencySymbol(), "Currency symbols do not match.");
			softAssert.assertEquals(userBuAggregation.getDomain(), retailerPlatform.getDomain(), "Domains do not match.");
			softAssert.assertEquals(userBuAggregation.isMediaOffsiteEnabled(), retailerPlatform.getMediaOffsiteEnabled(), "MediaOffsiteEnabled names do not match.");
			softAssert.assertEquals(userBuAggregation.isMediaOnsiteEnabled(), retailerPlatform.getMediaOnsiteEnabled(), "MediaOnsiteEnabled names do not match.");
			softAssert.assertEquals(userBuAggregation.isRetailEnabled(), retailerPlatform.getRetailEnabled(), "RetailEnabled do not match.");

			Object buAggregation = userBuAggregation.getBuAggregation();
			if (buAggregation != null) {
				JSONObject buAggregationJson = new JSONObject(buAggregation.toString());
				Object legacyBuIdDb = buAggregationJson.get("data");
				if (userBuAggregation.getPlatformName().equalsIgnoreCase("fila")){
					softAssert.assertEquals((Integer) legacyBuIdDb, retailerPlatform.getFlywheelBusinessUnitId().get(0), "Fila Legacy BU Ids do not match.");
				} else if (userBuAggregation.getPlatformName().equalsIgnoreCase("onespace")){
					softAssert.assertEquals(legacyBuIdDb, retailerPlatform.getFlywheelContentStudioCompanyId().get(0), "OS Legacy BU Ids do not match.");
				}  else if (userBuAggregation.getPlatformName().equalsIgnoreCase("marketshare")){
					softAssert.assertEquals((Integer) legacyBuIdDb, retailerPlatform.getMarketShareId().get(0), "MS Legacy BU Ids do not match.");
				}
			}

			if (buAggregation == null) {
				softAssert.assertTrue(retailerPlatform.getFlywheelBusinessUnitId() == null && retailerPlatform.getFlywheelContentStudioCompanyId() == null && retailerPlatform.getMarketShareId() == null, "Fila Legacy BU Ids do not match.");
			}
		}
		softAssert.assertAll();
	}

	@Test(dataProvider = "Hub_NetNew_User_Info", dataProviderClass = AuthServiceDataProvider.class, description = "PH-526 - Hub User Modules for BUs")
	public void Hub_User_Modules_For_Bus_Api_Test(ITestContext testContext, Dictionary<String, String> user) throws Exception {
		HubCommonMethods hubCommonMethods = new HubCommonMethods();
		SoftAssert softAssert = new SoftAssert();
		HubDbFunctions db = new HubDbFunctions();
		driver = base.initializeBrowser(testContext, headless);
		String userToken = "Bearer " +  SecurityAPI.loginToDcAppToGetAuthToken(driver, user.get("username"), user.get("password"));

		String auth0Id = hubCommonMethods.getAuth0IdFromToken(userToken);
		Response userResponse = hubCommonMethods.getUserHubInfo(userToken);
		JSONObject hubUserInfo = (JSONObject) hubCommonMethods.getUserPlatformInfo(userResponse, "netnew");
		List<JSONObject>  buModules = hubCommonMethods.getBusinessUnitModulesForUserInfo(hubUserInfo);

		JSONObject randomBuModules = (JSONObject) SharedMethods.getRandomItemFromList(buModules);
		String buId = randomBuModules.getString("businessUnitId");

		List<JSONObject>  modules = hubCommonMethods.getModulesForBu(randomBuModules);
		JSONObject randomBuModule = (JSONObject) SharedMethods.getRandomItemFromList(modules);
		String buModuleId = randomBuModule.getString("moduleId");
		UserBuModule buModule = db.getUserBuModule(auth0Id, buId, buModuleId);

		if (modules.size() < 1) {
			Assert.assertNull(buModule, "BU module available in Db.");
		}

		List<String> privilegesDb = buModule.getPrivileges();
		List<String> privilegesResponse = hubCommonMethods.getModulePrivileges(randomBuModule);
		softAssert.assertEquals(buModule.getModuleId(), randomBuModule.getString("moduleId"), "BU module id do not match.");
		softAssert.assertEquals(buModule.getBuId(), randomBuModules.getString("businessUnitId"), "Business unit ids do not match.");
		softAssert.assertEquals(privilegesDb, privilegesResponse, "BU module privileges do not match.");

		List<JSONObject>  userModules = hubCommonMethods.getBusinessUnitAgnosticModulesForUserInfo(hubUserInfo);
		JSONObject randomUserModule = (JSONObject) SharedMethods.getRandomItemFromList(userModules);
		String moduleId = randomUserModule.getString("moduleId");
		UserModule userModule = db.getUserModule(auth0Id, moduleId);

		if (userModules.size() < 1) {
			Assert.assertNull(userModule, "User module available in Db.");
		}

		List<String> userModulePrivilegesDb = userModule.getPrivileges();
		List<String> userModuleResponse = hubCommonMethods.getModulePrivileges(randomUserModule);
		softAssert.assertEquals(userModule.getModuleId(), randomUserModule.getString("moduleId"), "User module id do not match.");
		softAssert.assertEquals(userModulePrivilegesDb, userModuleResponse, "User module privileges do not match.");
		softAssert.assertAll();
	}

	private void updateFilaUser(FilaUser filaUser, String authToken) throws Exception {
		String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: User Administration", "Internal Only: Role Administration");
		String buIds = filaUser.getFilaBus(authToken,"3M", "Hersheys - US");
		filaUser.updateFilaUser(authToken, userRolesIds, buIds, true, true);
	}

	private void restoreFilaUser(FilaUser filaUser, String authToken) throws Exception {
		String userRolesIds = filaUser.getFilaRoles(authToken,"Internal Only: Base User","Internal Only: User Administration", "Internal Only: Role Administration");
		String buIds = filaUser.getFilaBus(authToken,"3M", "Hersheys - US");
		filaUser.updateFilaUser(authToken, userRolesIds, buIds, true, true);
	}

}