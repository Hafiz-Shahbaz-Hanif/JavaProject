package com.DC.utilities.apiEngine.apiRequests.hub.insights;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.hub.insights.HubInsightsCategoriesFilterExportRequestBody;
import com.DC.utilities.apiEngine.models.requests.hub.insights.HubInsightsProductChainProgressRequestBody;
import com.DC.utilities.apiEngine.routes.hub.insights.cpgserver.HubInsightsCpgServerRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;
import org.json.JSONObject;

public class HubInsightsCpgServerApiRequest {

    public static Response getCurrentUsersExport(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(HubInsightsCpgServerRoutes.getCurrentUsersExportRoutePath(), "GET", "", headers, parameters, jwt);
    }

    public static Response productChainProgress(HubInsightsProductChainProgressRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
    	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(HubInsightsCpgServerRoutes.getProductChainProgressRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response categoriesFilterExport(HubInsightsCategoriesFilterExportRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
    	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(HubInsightsCpgServerRoutes.getCategoriesFilterExportRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response exportLaunchFile(JSONObject payload, String jwt) throws Exception {
        return CommonApiMethods.callEndpoint(HubInsightsCpgServerRoutes.getLaunchFileRoutePath(), jwt, "POST", payload.toString(), "");
    }

    public static Response stageOrReleaseBatch(JSONObject payload, String jwt) throws Exception {
        return CommonApiMethods.callEndpoint(HubInsightsCpgServerRoutes.getBatchFileCreteRoutePath(), jwt, "POST", payload.toString(), "");
    }

}