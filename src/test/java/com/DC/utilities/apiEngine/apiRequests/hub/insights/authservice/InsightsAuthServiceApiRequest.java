package com.DC.utilities.apiEngine.apiRequests.hub.insights.authservice;

import com.DC.utilities.apiEngine.models.requests.hub.insights.authservice.HubInsightsRolesRequestBody;
import org.testng.Assert;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.hub.insights.authservice.InsightsAuthServiceRequestBody;
import com.DC.utilities.apiEngine.models.responses.hub.insights.authservice.InsightsAuthServiceResponseBody;
import com.DC.utilities.apiEngine.routes.hub.insights.authservice.AuthServiceRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

public class InsightsAuthServiceApiRequest {

    public static Response getUserToken(InsightsAuthServiceRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
		String reqBody = new ObjectMapper().writeValueAsString(requestBody);
		return CommonApiMethods.callEndpoint(AuthServiceRoutes.getAuthServiceRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response getJwt(InsightsAuthServiceResponseBody requestBody, String headers, String parameters, String jwt) throws Exception {
		String reqBody  = new ObjectMapper().writeValueAsString(requestBody);
		return CommonApiMethods.callEndpoint(AuthServiceRoutes.getJwtRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response getCompanySchema(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(AuthServiceRoutes.getCompanySchemaRoutePath(), "GET", "", headers, parameters, jwt);
    }
    
    public static Response getCompanySchemaExternalGateway(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(AuthServiceRoutes.getCompanySchemaRouteExternalGatewayPath(), "GET", "", headers, parameters, jwt);
    }
    
    public static Response getCompanySchemaProperties(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(AuthServiceRoutes.getCompanySchemaPropertiesPath(), "GET", "", headers, parameters, jwt);
    }

    public static Response getCompanySchemaPropertiesExternalGateway(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(AuthServiceRoutes.getCompanySchemaPropertiesRouteExternalGatewayPath(), "GET", "", headers, parameters, jwt);
    }
    
    public static Response getCompanyAllCountries(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(AuthServiceRoutes.getCompanyAllCountriesRoutePath(), "GET", "", headers, parameters, jwt);
    }

    public static Response getCompanyAllCountriesExternalGateway(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(AuthServiceRoutes.getCompanyAllCountriesRouteExternalGatewayPath(), "GET", "", headers, parameters, jwt);
    }

    public static Response getCategoriesFavoriteExternalGateway(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(AuthServiceRoutes.getCategoriesFavoriteRouteExternalGatewayPath(), "GET", "", headers, parameters, jwt);
    }
    
    public static void getCompAllCountriesExternalGateway(String headers, String parameters, String jwt) throws Exception {
    	Response response = getCompanyAllCountriesExternalGateway(headers, parameters, jwt);
		Assert.assertEquals(response.statusCode(), 200, "Call to get company all countries unsuccesfull.");
    }
    
    public static Response supportLogin(String requestBody, String jwt) throws Exception {
    	Response response = CommonApiMethods.callEndpoint(AuthServiceRoutes.getSupportLoginRouteExternalGatewayPath(), "POST", requestBody, "", "", jwt);
		Assert.assertEquals(response.statusCode(), 200, "Unable to switch company.");
		return response;
    }

    public static Response getCompaniesForUserExternalGateway(String header, String jwt) throws Exception {
        Response response = CommonApiMethods.callEndpoint(AuthServiceRoutes.getCompaniesRouteExternalGatewayPath(), "GET", "", header, "", jwt);
        Assert.assertEquals(response.statusCode(), 200, "Call to get companies unsuccesfull.");
        return response;
    }

    public static void updateUserRoles(HubInsightsRolesRequestBody requestBody, String userId, String jwt) throws Exception {
        String reqBody  = new ObjectMapper().writeValueAsString(requestBody);
        Response response = CommonApiMethods.callEndpoint(AuthServiceRoutes.getUserRoleUpdateRoutePath(userId), "PUT", reqBody, "", "", jwt);
        Assert.assertEquals(response.statusCode(), 200, "Cannot update roles for Insights user.");
    }

}