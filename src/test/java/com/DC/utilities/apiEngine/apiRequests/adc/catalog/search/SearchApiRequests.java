package com.DC.utilities.apiEngine.apiRequests.adc.catalog.search;

import java.io.File;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.adc.admin.AdminManageQueriesRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.search.SearchOfVoiceRequestBody;
import com.DC.utilities.apiEngine.routes.adc.catalog.search.SearchRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;

public class SearchApiRequests {

    private static String header = "Content-Type=application/json";
	
    public static Response sovBrand(SearchOfVoiceRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(SearchRoutes.getShareOfVoiceBrandRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }
    
    public static Response exportManageQueries(AdminManageQueriesRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(SearchRoutes.getAdminManageQueriesExportRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }
    
    public static Response importManageQueries(File file, String parameters, String jwt, int businessUnitId) throws Exception {
    	return CommonApiMethods.callEndpointToUploadFile(file, SearchRoutes.getAdminManageQueriesImportRoutePath(businessUnitId), "POST", "", "", parameters, jwt);
    }
    
    public static Response sovBrandExternalGateway(SearchOfVoiceRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(SearchRoutes.getShareOfVoiceBrandExternalGatewayRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response exportManageQueriesExternalGateway(AdminManageQueriesRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(SearchRoutes.getAdminManageQueriesExportExternalGatewayRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response importManageQueriesExternalGateway(File file, String parameters, String jwt, int businessUnitId) throws Exception {
    	return CommonApiMethods.callEndpointToUploadFile(file, SearchRoutes.getAdminManageQueriesImportExternalGatewayRoutePath(businessUnitId), "POST", "", "", parameters, jwt);
    }

    public static Response sovBrand(SearchOfVoiceRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(SearchRoutes.getShareOfVoiceBrandRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static Response sovBrandForAmazon(String jwt) throws Exception {
        return CommonApiMethods.callEndpoint(SearchRoutes.getShareOfVoiceBrandAmazonRoutePath(), "GET", "", header, "", jwt);
    }
}
