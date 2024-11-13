package com.DC.utilities.apiEngine.apiRequests.hub.aggregation;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.hub.aggregation.*;
import com.DC.utilities.apiEngine.routes.hub.aggregation.AggregationServiceRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;

import java.util.List;

public class AggregationServiceApiRequest {

    public static Response postAggregationType(HubAggregationTypeRequestBody requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postAggregationTypeRoutePath(), "POST", reqBody, "", "", auth0Token);
    }

    public static JSONObject postAggregationTypeJson(HubAggregationTypeRequestBody requestBody, String auth0Token) throws Exception {
        Response response = postAggregationType(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Create aggregation type call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getAggregationType(String aggregationTypeId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getAggregationTypeRoutePath(aggregationTypeId), "GET", "", "", "", auth0Token);
    }

    public static JSONObject getAggregationTypeJson(String aggregationTypeId, String auth0Token) throws Exception {
        Response response = getAggregationType(aggregationTypeId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Get aggregation type call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response deleteAggregationType(String aggregationTypeId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getAggregationTypeRoutePath(aggregationTypeId), "DELETE", "", "", "", auth0Token);
    }

    public static JSONObject deleteAggregationTypeJson(String aggregationTypeId, String auth0Token) throws Exception {
        Response response = deleteAggregationType(aggregationTypeId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Delete aggregation type call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response postOrganizationAggregation(HubOrganizationAggregationRequestBody requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postOrganizationAggregationRoutePath(), "POST", reqBody, "", "", auth0Token);
    }

    public static JSONObject postOrganizationAggregationJson(HubOrganizationAggregationRequestBody requestBody, String auth0Token) throws Exception {
        Response response = postOrganizationAggregation(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Create organization aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response putOrganizationAggregation(HubOrganizationAggregationRequestBody requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postOrganizationAggregationRoutePath(), "PUT", reqBody, "", "", auth0Token);
    }

    public static JSONObject putOrganizationAggregationJson(HubOrganizationAggregationRequestBody requestBody, String auth0Token) throws Exception {
        Response response = putOrganizationAggregation(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Create/Update organization aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response postBulkOrganizationAggregation(List<HubOrganizationAggregationRequestBody> requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postBulkOrganizationAggregationRoutePath(), "POST", reqBody, "", "", auth0Token);
    }

    public static JSONObject postBulkOrganizationAggregationJson(List<HubOrganizationAggregationRequestBody> requestBody, String auth0Token) throws Exception {
        Response response = postBulkOrganizationAggregation(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Create bulk organization aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response putBulkOrganizationAggregation(List<HubOrganizationAggregationRequestBody> requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postBulkOrganizationAggregationRoutePath(), "PUT", reqBody, "", "", auth0Token);
    }

    public static JSONObject putBulkOrganizationAggregationJson(List<HubOrganizationAggregationRequestBody> requestBody, String auth0Token) throws Exception {
        Response response = putBulkOrganizationAggregation(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Update/Create bulk organization aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response putBulkBuAggregation(List<HubBuAggregationRequestBody> requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.putBulkBuAggregationRoutePath(), "PUT", reqBody, "", "", auth0Token);
    }

    public static JSONObject putBulkBuAggregationJson(List<HubBuAggregationRequestBody> requestBody, String auth0Token) throws Exception {
        Response response = putBulkBuAggregation(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Update bulk BU aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getBuAggregationList(HubBuAggregationRequestBody requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getBuAggregationListRoutePath(), "POST", reqBody, "", "", auth0Token);
    }

    public static JSONObject getBuAggregationListJson(HubBuAggregationRequestBody requestBody, String auth0Token) throws Exception {
        Response response = getBuAggregationList(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Get list of BU aggregations call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getOrgAggregationList(HubOrganizationAggregationRequestBody requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getOrgAggregationListRoutePath(), "POST", reqBody, "", "", auth0Token);
    }

    public static JSONObject getOrgAggregationListJson(HubOrganizationAggregationRequestBody requestBody, String auth0Token) throws Exception {
        Response response = getOrgAggregationList(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Get list of Org aggregations call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getOrganizationAggregationByAggrId(String aggregationId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getOrganizationAggregationAggrIdRoutePath(aggregationId), "GET", "", "", "", auth0Token);
    }

    public static JSONObject getOrganizationAggregationByAggrIdJson(String aggregationId, String auth0Token) throws Exception {
        Response response = getOrganizationAggregationByAggrId(aggregationId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Get organization aggregation by AggrId call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getOrganizationAggregationByOrgId(String orgId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getOrganizationAggregationOrgIdRoutePath(orgId), "GET", "", "", "", auth0Token);
    }

    public static JSONObject getOrganizationAggregationByOrgIdJson(String orgId, String auth0Token) throws Exception {
        Response response = getOrganizationAggregationByOrgId(orgId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Get organization aggregation by OrgId call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response deleteOrganizationAggregation(String aggregationId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getOrganizationAggregationAggrIdRoutePath(aggregationId), "DELETE", "", "", "", auth0Token);
    }

    public static JSONObject deleteOrganizationAggregationJson(String aggregationId, String auth0Token) throws Exception {
        Response response = deleteOrganizationAggregation(aggregationId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Delete organization aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response patchOrganizationAggregation(HubOrganizationAggregationRequestBody requestBody, String aggregationId, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.patchOrganizationAggregationRoutePath(aggregationId), "PATCH", reqBody, "", "", auth0Token);
    }

    public static JSONObject patchOrganizationAggregationJson(HubOrganizationAggregationRequestBody requestBody, String aggregationId,  String auth0Token) throws Exception {
        Response response = patchOrganizationAggregation(requestBody, aggregationId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Update organization aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response postBuAggregation(HubBuAggregationRequestBody requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postBuAggregationRoutePath(), "POST", reqBody, "", "", auth0Token);
    }

    public static JSONObject postBuAggregationJson(HubBuAggregationRequestBody requestBody, String auth0Token) throws Exception {
        Response response = postBuAggregation(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Create BU aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getBuAggregationByAggrId(String aggregationId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getBuAggregationAggrIdRoutePath(aggregationId), "GET", "", "", "", auth0Token);
    }

    public static JSONObject getBuAggregationByAggrIdJson(String aggregationId, String auth0Token) throws Exception {
        Response response = getBuAggregationByAggrId(aggregationId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Get BU aggregation by AggrId call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response deleteBuAggregation(String aggregationId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getBuAggregationAggrIdRoutePath(aggregationId), "DELETE", "", "", "", auth0Token);
    }

    public static JSONObject deleteBuAggregationJson(String aggregationId, String auth0Token) throws Exception {
        Response response = deleteBuAggregation(aggregationId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Delete BU aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getBuAggregationByBuId(String buId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getBuAggregationBuIdRoutePath(buId), "GET", "", "", "", auth0Token);
    }

    public static JSONObject getBuAggregationByBuIdJson(String buId, String auth0Token) throws Exception {
        Response response = getBuAggregationByBuId(buId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Get BU aggregation by BuId call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response patchBuAggregation(HubBuAggregationRequestBody requestBody, String aggregationId, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.patchBuAggregationRoutePath(aggregationId), "PATCH", reqBody, "", "", auth0Token);
    }

    public static JSONObject patchBuAggregationJson(HubBuAggregationRequestBody requestBody, String aggregationId,  String auth0Token) throws Exception {
        Response response = patchBuAggregation(requestBody, aggregationId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Update Bu aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response putBuAggregation(HubBuAggregationRequestBody requestBody, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postBuAggregationRoutePath(), "PUT", reqBody, "", "", auth0Token);
    }

    public static JSONObject putBuAggregationJson(HubBuAggregationRequestBody requestBody, String auth0Token) throws Exception {
        Response response = putBuAggregation(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Create/Update BU aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response postAuthAggregation(HubAuthAggregationRequestBody requestBody, String userId, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postAuthAggregationRoutePath(userId), "POST", reqBody, "", "", auth0Token);
    }

    public static JSONObject postAuthAggregationJson(HubAuthAggregationRequestBody requestBody, String userId, String auth0Token) throws Exception {
        Response response = postAuthAggregation(requestBody, userId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Create Auth aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response postAuthAggregation(String requestBody, String userId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postAuthAggregationRoutePath(userId), "POST", requestBody, "", "", auth0Token);
    }

    public static JSONObject postAuthAggregationJson(String requestBody, String userId, String auth0Token) throws Exception {
        Response response = postAuthAggregation(requestBody, userId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Create Auth aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response patchAuthAggregation(HubAuthAggregationRequestBody requestBody, String aggregationId, String auth0Token) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postAuthAggregationRoutePath(aggregationId), "PATCH", reqBody, "", "", auth0Token);
    }

    public static JSONObject patchAuthAggregationJson(HubAuthAggregationRequestBody requestBody, String aggregationId, String auth0Token) throws Exception {
        Response response = patchAuthAggregation(requestBody, aggregationId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Update Auth aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response patchAuthAggregation(String requestBody, String aggregationId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.postAuthAggregationRoutePath(aggregationId), "PATCH", requestBody, "", "", auth0Token);
    }

    public static JSONObject patchAuthAggregationJson(String requestBody, String aggregationId, String auth0Token) throws Exception {
        Response response = patchAuthAggregation(requestBody, aggregationId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Update Auth aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response deleteAuthAggregation(String aggregationId, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.deleteAuthAggregationRoutePath(aggregationId), "DELETE", "", "", "", auth0Token);
    }

    public static JSONObject deleteAuthAggregationJson(String aggregationId, String auth0Token) throws Exception {
        Response response = deleteAuthAggregation(aggregationId, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Delete Auth aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response putAuthAggregation(String requestBody, String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getAuthAggregationRoutePath(), "PUT", requestBody, "", "", auth0Token);
    }

    public static JSONObject putAuthAggregationJson(String requestBody, String auth0Token) throws Exception {
        Response response = putAuthAggregation(requestBody, auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Update/Create Auth aggregation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getUserAuthorizationAggregationInfo(String authToken) throws Exception {
        return CommonApiMethods.callEndpoint(AggregationServiceRoutes.getAuthAggregationRoutePath(), "GET", "", "", "", authToken);
    }

}