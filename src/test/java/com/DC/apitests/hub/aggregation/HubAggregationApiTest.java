package com.DC.apitests.hub.aggregation;

import com.DC.db.hubDbFunctions.HubDbFunctions;
import com.DC.testcases.BaseClass;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.hub.aggregation.AggregationServiceApiRequest;
import com.DC.utilities.apiEngine.models.requests.hub.aggregation.*;
import com.DC.utilities.hub.HubCommonMethods;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

public class HubAggregationApiTest {

    ReadConfig readConfig;
    Logger logger;
    String auth0Token;
    BaseClass base;
    WebDriver driver;
    List<String> legacyPlatformList = Arrays.asList("fila", "onespace", "marketshare");

    HubAggregationApiTest() {
        readConfig =  ReadConfig.getInstance();
        base = new BaseClass();
        logger = Logger.getLogger(HubAggregationApiTest.class);
    }

    @BeforeClass
    private void getTestData(ITestContext testContext) throws Exception {
        driver = base.initializeBrowser(testContext, readConfig.getHeadlessMode());
        auth0Token = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubFilaInsightsEdgeUserEmail(), readConfig.getHubFilaInsightsEdgeUserPassword());
    }

    @Test(description = "PH-289 - Hub Aggregation Type - Post, Get, Delete")
    public void Hub_Aggregation_Type_Post_Get_Delete_Api_Test() throws Exception {
        HubDbFunctions hubDbFunctions = new HubDbFunctions();
        String aggregationTypeNameSlug = SharedMethods.generateRandomString();
        List<String> ids = hubDbFunctions.getLegacyPlatformIds();
        String legacyPlatformId = (String) SharedMethods.getRandomItemFromList(ids);

        HubAggregationTypeRequestBody reqBody = new HubAggregationTypeRequestBody(legacyPlatformId, aggregationTypeNameSlug, aggregationTypeNameSlug);
        JSONObject postResponseBody = AggregationServiceApiRequest.postAggregationTypeJson(reqBody, auth0Token);

        String aggregationTypeId =  postResponseBody.getJSONObject("data").getString("id");
        logger.info("Aggregation type id: " + aggregationTypeId);
        JSONObject getResponseBody = AggregationServiceApiRequest.getAggregationTypeJson(aggregationTypeId, auth0Token);

        Assert.assertEquals(getResponseBody.getJSONObject("data").getString("id"), aggregationTypeId, "Aggregation type id does not match in Post and Get responses.");
        Assert.assertEquals(getResponseBody.getJSONObject("data").getString("legacy_platform_id"), legacyPlatformId, "Legacy platform id does not match in Post and Get responses.");

        AggregationServiceApiRequest.deleteAggregationTypeJson(aggregationTypeId, auth0Token);
        Assert.assertTrue(hubDbFunctions.getAggregationTypeId(aggregationTypeId).size() == 0, "Newly created aggregation type not deleted.");
    }

    @Test(description = "PH-289 - Hub Aggregation Type - Creation With Invalid Legacy PlatformId")
    public void Hub_Aggregation_Type_Creation_With_Invalid_Legacy_PlatformId_Api_Test() throws Exception {
        String aggregationTypeNameSlug = SharedMethods.generateRandomString();
        String invalidLegacyPlatformId = SharedMethods.generateUUID();
        logger.info("Invalid legacy platform id: " + invalidLegacyPlatformId + " / " + "aggregation type name slug: " + aggregationTypeNameSlug);

        HubAggregationTypeRequestBody reqBody = new HubAggregationTypeRequestBody(invalidLegacyPlatformId, aggregationTypeNameSlug, aggregationTypeNameSlug);
        Response postResponseBody = AggregationServiceApiRequest.postAggregationType(reqBody, auth0Token);
        Assert.assertEquals(postResponseBody.statusCode(), 422, "** Create aggregation type successful.");
    }

    @Test(dataProvider = "Hub_Aggregation", dataProviderClass = HubAggregationServiceDataProvider.class, description = "PH-289 - Hub Organization Aggregation - Post, Get(ByAggregationId), Delete")
    public void Hub_Organization_Aggregation_Post_GetByAggregationId_Delete_Api_Test(Dictionary<String, Object> legacyPlatform) throws Exception {
        HubDbFunctions hubDbFunctions = new HubDbFunctions();
        String legacyPlatformName = legacyPlatform.get("legacyPlatformName").toString();
        String organizationId = legacyPlatform.get("organizationId").toString();
        Object legacyPlatformOrganizationId = legacyPlatform.get("legacyPlatformOrganizationId");

        HubOrganizationAggregationRequestBody reqBody = new HubOrganizationAggregationRequestBody(legacyPlatformOrganizationId, organizationId, legacyPlatformName);
        JSONObject postResponseBody = AggregationServiceApiRequest.postOrganizationAggregationJson(reqBody, auth0Token);

        String aggregationId =  postResponseBody.getJSONObject("data").getString("id");
        logger.info("Aggregation id: " + aggregationId);
        JSONObject getResponseBody = AggregationServiceApiRequest.getOrganizationAggregationByAggrIdJson(aggregationId, auth0Token);

        Assert.assertEquals(getResponseBody.getJSONObject("data").getString("id"), aggregationId, "Aggregation id does not match in Post and Get responses.");
        Assert.assertEquals(getResponseBody.getJSONObject("data").getJSONObject("serialized_value").get("data"), legacyPlatformOrganizationId, "Legacy platform org id does not match in Post and Get responses.");

        AggregationServiceApiRequest.deleteOrganizationAggregationJson(aggregationId, auth0Token);
        Assert.assertTrue(hubDbFunctions.getAggregationId(aggregationId).size() == 0, "Newly created organization aggregation not deleted.");
    }

    @Test(dataProvider = "Hub_Aggregation", dataProviderClass = HubAggregationServiceDataProvider.class, description = "PH-289 - Hub Organization Aggregation - Put, Get(ByOrgId), Patch")
    public void Hub_Organization_Aggregation_Put_GetByOrgId_Patch_Api_Test(Dictionary<String, Object> legacyPlatform) throws Exception {
        HubDbFunctions hubDbFunctions = new HubDbFunctions();
        String legacyPlatformName = legacyPlatform.get("legacyPlatformName").toString();
        String organizationId = legacyPlatform.get("organizationId").toString();
        Object legacyPlatformOrganizationId = legacyPlatform.get("legacyPlatformOrganizationId");

        HubOrganizationAggregationRequestBody reqBody = new HubOrganizationAggregationRequestBody(legacyPlatformOrganizationId, organizationId, legacyPlatformName);
        JSONObject putResponseBody = AggregationServiceApiRequest.putOrganizationAggregationJson(reqBody, auth0Token);
        String aggregationId =  putResponseBody.getJSONObject("data").getString("id");
        logger.info("Aggregation id: " + aggregationId);

        JSONObject getResponseBody = AggregationServiceApiRequest.getOrganizationAggregationByOrgIdJson(organizationId, auth0Token);
        Assert.assertTrue(putResponseBody.getJSONObject("data").similar(getResponseBody.getJSONArray("data").getJSONObject(0)), "Organization aggregation response does not match.");

        legacyPlatformOrganizationId = legacyPlatform.get("legacyPlatformName").equals("onespace") ? SharedMethods.generateUUID() : SharedMethods.getRandomNumber(150);
        reqBody = new HubOrganizationAggregationRequestBody(legacyPlatformOrganizationId, null, null);
        JSONObject patchResponseBody = AggregationServiceApiRequest.patchOrganizationAggregationJson(reqBody, aggregationId, auth0Token);
        Assert.assertEquals(patchResponseBody.getJSONObject("data").getJSONObject("serialized_value").get("data"), legacyPlatformOrganizationId, "Legacy platform org id not updated in response.");

        AggregationServiceApiRequest.deleteOrganizationAggregationJson(aggregationId, auth0Token);
        Assert.assertTrue(hubDbFunctions.getAggregationId(aggregationId).size() == 0, "Cannot delete organization aggregation.");
    }

    @Test(description = "PH-289 - Hub Organization Aggregation - Duplicate Entry")
    public void Hub_Organization_Aggregation_Post_Duplicate_Api_Test() throws Exception {
        String legacyPlatformName = (String) SharedMethods.getRandomItemFromList(legacyPlatformList);
        String organizationId = SharedMethods.generateUUID();
        Object legacyPlatformOrganizationId = legacyPlatformName.equals("onespace") ? SharedMethods.generateUUID() : SharedMethods.getRandomNumber(150);

        HubOrganizationAggregationRequestBody reqBody = new HubOrganizationAggregationRequestBody(legacyPlatformOrganizationId, organizationId, legacyPlatformName);
        JSONObject postResponseBody = AggregationServiceApiRequest.postOrganizationAggregationJson(reqBody, auth0Token);
        String aggregationId =  postResponseBody.getJSONObject("data").getString("id");
        logger.info("Aggregation id: " + aggregationId);

        Response response = AggregationServiceApiRequest.postOrganizationAggregation(reqBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 422, "** Create organization aggregation successful.");
        AggregationServiceApiRequest.deleteOrganizationAggregation(aggregationId, auth0Token);
    }

    @Test(dataProvider = "Hub_Aggregation_Organization_Bulk_Creation", dataProviderClass = HubAggregationServiceDataProvider.class, description = "PH-289 - Hub Organization Aggregation - Create Bulk Organization")
    public void Hub_Organization_Aggregation_Bulk_Creation_Api_Test(List<Dictionary<String, Object>> orgAggregationsToCreate) throws Exception {
        List<HubOrganizationAggregationRequestBody> reqBody = new ArrayList<>();
        String organizationId = null;

        for(Dictionary<String, Object> org : orgAggregationsToCreate){
            String legacyPlatformName = (String) org.get("legacyPlatformName");
            organizationId = (String) org.get("organizationId");
            Object legacyPlatformOrganizationId = org.get("legacyPlatformOrganizationId");
            HubOrganizationAggregationRequestBody orgAggr = new HubOrganizationAggregationRequestBody(legacyPlatformOrganizationId, organizationId, legacyPlatformName);
            reqBody.add(orgAggr);
        }

        JSONObject postResponseBody = AggregationServiceApiRequest.postBulkOrganizationAggregationJson(reqBody, auth0Token);
        JSONArray aggregations =  postResponseBody.getJSONArray("data");

        for(int i=0; i<aggregations.length(); i++){
            logger.info("Aggregation id: " + aggregations.getJSONObject(i).getString("id"));
        }

        JSONObject getResponseBody = AggregationServiceApiRequest.getOrganizationAggregationByOrgIdJson(organizationId, auth0Token);
        Assert.assertTrue(postResponseBody.similar(getResponseBody), "Bulk organization aggregation response does not match.");

        for(int i=0; i<aggregations.length(); i++){
            String aggregationId = aggregations.getJSONObject(i).getString("id");
            AggregationServiceApiRequest.deleteOrganizationAggregationJson(aggregationId, auth0Token);
        }
    }

    @Test(dataProvider = "Hub_Aggregation", dataProviderClass = HubAggregationServiceDataProvider.class, description = "PH-289 - Hub BU Aggregation - Post, Get(ByAggregationId), Delete")
    public void Hub_Bu_Aggregation_Post_GetByAggregationId_Delete_Api_Test(Dictionary<String, Object> legacyPlatform) throws Exception {
        HubDbFunctions hubDbFunctions = new HubDbFunctions();
        Object legacyPlatformBusinessUnitId = legacyPlatform.get("legacyPlatformBusinessUnitId");
        String businessUnitId = legacyPlatform.get("businessUnitId").toString();
        String retailerPlatformId = legacyPlatform.get("retailerPlatformId").toString();
        String legacyPlatformName = legacyPlatform.get("legacyPlatformName").toString();

        HubBuAggregationRequestBody reqBody = new HubBuAggregationRequestBody(legacyPlatformBusinessUnitId, businessUnitId, retailerPlatformId, legacyPlatformName);
        JSONObject postResponseBody = AggregationServiceApiRequest.postBuAggregationJson(reqBody, auth0Token);

        String aggregationId =  postResponseBody.getJSONObject("data").getString("id");
        logger.info("Aggregation id: " + aggregationId);
        JSONObject getResponseBody = AggregationServiceApiRequest.getBuAggregationByAggrIdJson(aggregationId, auth0Token);

        Assert.assertEquals(getResponseBody.getJSONObject("data").getString("id"), aggregationId, "Aggregation id does not match in Post and Get responses.");
        Assert.assertEquals(getResponseBody.getJSONObject("data").getJSONObject("serialized_value").get("data"), legacyPlatformBusinessUnitId, "Legacy platform BU id does not match in Post and Get responses.");

        AggregationServiceApiRequest.deleteBuAggregationJson(aggregationId, auth0Token);
        Assert.assertTrue(hubDbFunctions.getAggregationId(aggregationId).size() == 0, "Newly created BU aggregation not deleted.");
    }

    @Test(dataProvider = "Hub_Aggregation", dataProviderClass = HubAggregationServiceDataProvider.class, description = "PH-289 - Hub BU Aggregation - Put, Get(ByBuId), Patch")
    public void Hub_Bu_Aggregation_Put_GetByOrgId_Patch_Api_Test(Dictionary<String, Object> legacyPlatform) throws Exception {
        HubDbFunctions hubDbFunctions = new HubDbFunctions();
        Object legacyPlatformBusinessUnitId = legacyPlatform.get("legacyPlatformBusinessUnitId");
        String businessUnitId = legacyPlatform.get("businessUnitId").toString();
        String retailerPlatformId = legacyPlatform.get("retailerPlatformId").toString();
        String legacyPlatformName = legacyPlatform.get("legacyPlatformName").toString();

        HubBuAggregationRequestBody reqBody = new HubBuAggregationRequestBody(legacyPlatformBusinessUnitId, businessUnitId, retailerPlatformId, legacyPlatformName);
        JSONObject putResponseBody = AggregationServiceApiRequest.putBuAggregationJson(reqBody, auth0Token);
        String aggregationId =  putResponseBody.getJSONObject("data").getString("id");
        logger.info("Aggregation id: " + aggregationId);

        JSONObject getResponseBody = AggregationServiceApiRequest.getBuAggregationByBuIdJson(businessUnitId, auth0Token);
        Assert.assertTrue(putResponseBody.getJSONObject("data").similar(getResponseBody.getJSONArray("data").getJSONObject(0)), "Organization aggregation response does not match.");

        legacyPlatformBusinessUnitId = legacyPlatform.get("legacyPlatformName").equals("onespace") ? SharedMethods.generateUUID() : SharedMethods.getRandomNumber(150);
        reqBody = new HubBuAggregationRequestBody(legacyPlatformBusinessUnitId, null, null, null);
        JSONObject patchResponseBody = AggregationServiceApiRequest.patchBuAggregationJson(reqBody, aggregationId, auth0Token);
        Assert.assertEquals(patchResponseBody.getJSONObject("data").getJSONObject("serialized_value").get("data"), legacyPlatformBusinessUnitId, "Legacy platform org id not updated in response.");

        AggregationServiceApiRequest.deleteBuAggregationJson(aggregationId, auth0Token);
        Assert.assertTrue(hubDbFunctions.getAggregationId(aggregationId).size() == 0, "Cannot delete BU aggregation.");
    }

    @Test(dataProvider = "Hub_Aggregation_Organization_Bulk_Creation", dataProviderClass = HubAggregationServiceDataProvider.class, description = "PH-609 - Hub BU Aggregation - Put Bulk - Get List of BU Aggregations")
    public void Hub_Bu_Aggregation_Put_Bulk_Get_List_Of_Bu_Aggregations_Api_Test(List<Dictionary<String, Object>> buAggregationsToCreate) throws Exception {
        List<HubBuAggregationRequestBody> aggregationsList = new ArrayList<>();
        SoftAssert softAssert = new SoftAssert();

        for(Dictionary<String, Object> bu : buAggregationsToCreate){
            String legacyPlatformName = (String) bu.get("legacyPlatformName");
            String businessUnitId = (String) bu.get("businessUnitId");
            String retailerPlatformId = (String) bu.get("retailerPlatformId");
            Object legacyPlatformBusinessUnitId = bu.get("legacyPlatformBusinessUnitId");
            HubBuAggregationRequestBody reqBody = new HubBuAggregationRequestBody(legacyPlatformBusinessUnitId, businessUnitId, retailerPlatformId, legacyPlatformName);
            aggregationsList.add(reqBody);
        }

        JSONObject putBulkResponseBody = AggregationServiceApiRequest.putBulkBuAggregationJson(aggregationsList, auth0Token);
        JSONArray putBulkResponseArray = putBulkResponseBody.getJSONArray("data");
        List<String> buIds = new ArrayList<>();

        for(int i=0; i<putBulkResponseArray.length(); i++){
            buIds.add(putBulkResponseArray.getJSONObject(i).getString("owner_id"));
        }

        logger.info("Bu ids: " + buIds);

        HubBuAggregationRequestBody buList = new HubBuAggregationRequestBody(buIds);
        JSONObject getBuAggregationList = AggregationServiceApiRequest.getBuAggregationListJson(buList, auth0Token);
        JSONArray getBuAggregationListArray = getBuAggregationList.getJSONArray("data");

        for(int i=0; i<getBuAggregationListArray.length(); i++){
            JSONObject buAggregation =  getBuAggregationListArray.getJSONObject(i);
            for(int j=0; j<aggregationsList.size(); j++) {
                if (buAggregation.getString("legacyPlatformName").equals(aggregationsList.get(j).getLegacyPlatformName())) {
                    softAssert.assertEquals(buAggregation.get("legacyPlatformBusinessUnitId"), aggregationsList.get(j).getLegacyPlatformBusinessUnitId(), "Legacy Bu id does not match in list response.");
                    softAssert.assertEquals(buAggregation.getString("businessUnitId"), aggregationsList.get(j).getBusinessUnitId(), "Bu id does not match in list response.");
                    softAssert.assertEquals(buAggregation.getString("retailerPlatformId"), aggregationsList.get(j).getRetailerPlatformId(), "Retailer platform id does not match in list response.");
                }
            }
        }

        for(String buId : buIds){
            JSONObject getResponseBody = AggregationServiceApiRequest.getBuAggregationByBuIdJson(buId, auth0Token);
            AggregationServiceApiRequest.deleteBuAggregationJson(getResponseBody.getJSONArray("data").getJSONObject(0).getString("id"), auth0Token);
        }

        softAssert.assertAll();
    }

    @Test(dataProvider = "Hub_Aggregation_Organization_Bulk_Creation", dataProviderClass = HubAggregationServiceDataProvider.class, description = "PH-609 - Hub Organization Aggregation - Put Bulk - Get List of Org Aggregations")
    public void Hub_Org_Aggregation_Put_Bulk_Get_List_Of_Org_Aggregations_Api_Test(List<Dictionary<String, Object>> buAggregationsToCreate) throws Exception {
        List<HubOrganizationAggregationRequestBody> aggregationsList = new ArrayList<>();
        SoftAssert softAssert = new SoftAssert();

        for(Dictionary<String, Object> bu : buAggregationsToCreate){
            String legacyPlatformName = (String) bu.get("legacyPlatformName");
            String orgId = (String) bu.get("orgId");
            Object legacyPlatformOrganizationId = bu.get("legacyPlatformOrganizationId");
            HubOrganizationAggregationRequestBody orgAggr = new HubOrganizationAggregationRequestBody(legacyPlatformOrganizationId, orgId, legacyPlatformName);
            aggregationsList.add(orgAggr);
        }

        JSONObject putBulkResponseBody = AggregationServiceApiRequest.putBulkOrganizationAggregationJson(aggregationsList, auth0Token);
        JSONArray putBulkResponseArray = putBulkResponseBody.getJSONArray("data");
        List<String> orgIds = new ArrayList<>();

        for(int i=0; i<putBulkResponseArray.length(); i++){
            orgIds.add(putBulkResponseArray.getJSONObject(i).getString("owner_id"));
        }

        logger.info("Org ids: " + orgIds);

        HubOrganizationAggregationRequestBody orgList = new HubOrganizationAggregationRequestBody(orgIds);
        JSONObject getOrgAggregationList = AggregationServiceApiRequest.getOrgAggregationListJson(orgList, auth0Token);
        JSONArray getOrgAggregationListArray = getOrgAggregationList.getJSONArray("data");

        for(int i=0; i<getOrgAggregationListArray.length(); i++){
            JSONObject orgAggregation =  getOrgAggregationListArray.getJSONObject(i);
            for(int j=0; j<aggregationsList.size(); j++) {
                if (orgAggregation.getString("legacyPlatformName").equals(aggregationsList.get(j).getLegacyPlatformName())) {
                    softAssert.assertEquals(orgAggregation.get("legacyPlatformOrganizationId"), aggregationsList.get(j).getLegacyPlatformOrganizationId(), "Legacy platform org id does not match in list response.");
                    softAssert.assertEquals(orgAggregation.getString("organizationId"), aggregationsList.get(j).getOrganizationId(), "Org id does not match in list response.");
                }
            }
        }

        for(String orgId : orgIds){
            JSONObject getResponseBody = AggregationServiceApiRequest.getOrganizationAggregationByOrgIdJson(orgId, auth0Token);
            AggregationServiceApiRequest.deleteOrganizationAggregationJson(getResponseBody.getJSONArray("data").getJSONObject(0).getString("id"), auth0Token);
        }
        softAssert.assertAll();
    }

    @Test(description = "PH-289 - Hub BU Aggregation - Duplicate Entry")
    public void Hub_Bu_Aggregation_Post_Duplicate_Api_Test() throws Exception {
        String legacyPlatformName = (String) SharedMethods.getRandomItemFromList(legacyPlatformList);
        String businessUnitId = SharedMethods.generateUUID();
        String retailerPlatformId = SharedMethods.generateUUID();
        Object legacyPlatformBusinessUnitId = legacyPlatformName.equals("onespace") ? SharedMethods.generateUUID() : SharedMethods.getRandomNumber(150);

        HubBuAggregationRequestBody reqBody = new HubBuAggregationRequestBody(legacyPlatformBusinessUnitId, businessUnitId, retailerPlatformId, legacyPlatformName);
        JSONObject postResponseBody = AggregationServiceApiRequest.postBuAggregationJson(reqBody, auth0Token);
        String aggregationId =  postResponseBody.getJSONObject("data").getString("id");
        logger.info("Aggregation id: " + aggregationId);

        Response response = AggregationServiceApiRequest.postBuAggregation(reqBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 422, "** Create BU aggregation successful.");
        AggregationServiceApiRequest.deleteBuAggregation(aggregationId, auth0Token);
    }

    @Test(dataProvider = "Hub_Aggregation", dataProviderClass = HubAggregationServiceDataProvider.class, description = "PH-289 - Hub Auth Aggregation - Get, Post, Delete, Put")
    public void Hub_Auth_Aggregation_Get_Post_Delete_Put_Api_Test(Dictionary<String, String> legacyPlatform) throws Exception {
        HubCommonMethods hubCommonMethods = new HubCommonMethods();
        HubDbFunctions hubDbFunctions = new HubDbFunctions();

        String userId = hubDbFunctions.getUserIdByAuth0Token(auth0Token);
        String aggregationId = hubDbFunctions.getAggregationId("authorization", userId, legacyPlatform.get("legacyPlatformName"));
        logger.info("User id: " + userId +" / legacy platform: " + legacyPlatform.get("legacyPlatformName"));

        Response userResponse = hubCommonMethods.getAuthorizationAggregationInfo(auth0Token);
        JSONObject aggregation = (JSONObject) hubCommonMethods.getAuthorizationAggregationPlatformInfo(userResponse, legacyPlatform.get("legacyPlatformName"));
        String aggregationBody = "{\"aggregation\":" + aggregation + "}";

        AggregationServiceApiRequest.deleteAuthAggregationJson(aggregationId, auth0Token);
        Assert.assertTrue(hubDbFunctions.getAggregationId(aggregationId).size() == 0, "Newly created Auth aggregation not deleted.");

        JSONObject postResponseBody = AggregationServiceApiRequest.postAuthAggregationJson(aggregationBody, userId, auth0Token);
        aggregationId = hubDbFunctions.getAggregationId("authorization", userId, legacyPlatform.get("legacyPlatformName"));
        Assert.assertEquals(aggregationId, postResponseBody.getJSONObject("data").getString("id"), "Aggregation id does not match in Post response.");

        AggregationServiceApiRequest.deleteAuthAggregationJson(aggregationId, auth0Token);

        JSONObject putResponseBody = AggregationServiceApiRequest.putAuthAggregationJson(aggregationBody, auth0Token);
        aggregationId = hubDbFunctions.getAggregationId("authorization", userId, legacyPlatform.get("legacyPlatformName"));
        Assert.assertEquals(aggregationId, putResponseBody.getJSONObject("data").getString("id"), "Aggregation id does not match in Put response.");
    }

    @Test(dataProvider = "Hub_Aggregation", dataProviderClass = HubAggregationServiceDataProvider.class, description = "PH-289 - Hub Auth Aggregation - Patch, Put")
    public void Hub_Auth_Aggregation_Patch_Put_Api_Test(Dictionary<String, String> legacyPlatform) throws Exception {
        HubCommonMethods hubCommonMethods = new HubCommonMethods();
        HubDbFunctions hubDbFunctions = new HubDbFunctions();
        String userId = hubDbFunctions.getUserIdByAuth0Token(auth0Token);

        Response userResponse = hubCommonMethods.getAuthorizationAggregationInfo(auth0Token);
        JSONObject aggregation = (JSONObject) hubCommonMethods.getAuthorizationAggregationPlatformInfo(userResponse, legacyPlatform.get("legacyPlatformName"));
        String aggregationBody = "{\"aggregation\":" + aggregation + "}";

        String aggregationId = hubDbFunctions.getAggregationId("authorization", userId, legacyPlatform.get("legacyPlatformName"));
        logger.info("User id: " + userId +" / legacy platform: " + legacyPlatform.get("legacyPlatformName"));
        String userInfo = legacyPlatform.get("legacyPlatformName").equals("onespace") ? SharedMethods.generateRandomString("jwt") : "{\"user\":\"info\"}";

        HubAuthAggregationRequestBody.Aggregation aggregationPatch = new HubAuthAggregationRequestBody.Aggregation(userInfo, legacyPlatform.get("type"), legacyPlatform.get("legacyPlatformName"));
        HubAuthAggregationRequestBody reqBodyPatch = new HubAuthAggregationRequestBody(aggregationPatch);
        JSONObject patchResponseBody = AggregationServiceApiRequest.patchAuthAggregationJson(reqBodyPatch, aggregationId, auth0Token);
        Assert.assertEquals(patchResponseBody.getJSONObject("data").getJSONObject("serialized_value").get("data").toString(), userInfo, "Updated (patch) auth aggregation info does not match in response.");

        JSONObject putResponseBody = AggregationServiceApiRequest.putAuthAggregationJson(aggregationBody, auth0Token);
        Assert.assertTrue(aggregation.similar(putResponseBody.getJSONObject("data").getJSONObject("serialized_value")), "Updated (put) auth aggregation info does not match in response.");
    }

    @Test(description = "PH-289 - Hub Auth Aggregation - Duplicate Entry")
    public void Hub_Auth_Aggregation_Duplicate_Entry_Api_Test() throws Exception {
        HubCommonMethods hubCommonMethods = new HubCommonMethods();
        HubDbFunctions hubDbFunctions = new HubDbFunctions();
        String legacyPlatformName = (String) SharedMethods.getRandomItemFromList(legacyPlatformList);
        String userId = hubDbFunctions.getUserIdByAuth0Token(auth0Token);

        Response userResponse = hubCommonMethods.getAuthorizationAggregationInfo(auth0Token);
        JSONObject aggregation = (JSONObject) hubCommonMethods.getAuthorizationAggregationPlatformInfo(userResponse, legacyPlatformName);
        String aggregationBody = "{\"aggregation\":" + aggregation + "}";

        Response postResponse = AggregationServiceApiRequest.postAuthAggregation(aggregationBody, userId, auth0Token);
        Assert.assertEquals(postResponse.statusCode(), 422, "** Create auth aggregation successful.");
    }

}