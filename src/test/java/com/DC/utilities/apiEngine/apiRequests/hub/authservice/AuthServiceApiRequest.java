package com.DC.utilities.apiEngine.apiRequests.hub.authservice;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.routes.hub.authservice.AuthServiceRoutes;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;

public class AuthServiceApiRequest {
	
	public static Response getLiveHealthCheck() throws Exception {
		return CommonApiMethods.callEndpoint(AuthServiceRoutes.getAuthServiceLiveHealthCheckRoutePath(), "GET", "", "", "", "");
	}
	
	public static Response getReadyHealthCheck() throws Exception {
		return CommonApiMethods.callEndpoint(AuthServiceRoutes.getAuthServiceReadyHealthCheckRoutePath(), "GET", "", "", "", "");
	}
	
	public static Response getAuthorization(String authToken) throws Exception {
		return CommonApiMethods.callEndpoint(AuthServiceRoutes.getAuthorizationRoutePath(), "GET", "", "", "", authToken);
	}
	
	public static Response postLegacyUserInfoSync(String authToken) throws Exception {
		return CommonApiMethods.callEndpoint(AuthServiceRoutes.getLegacyUserInfoSyncRoutePath(), "POST", "", "", "", authToken);
	}

	public static Response getUserAuthorizationInfo(String authToken) throws Exception {
		return CommonApiMethods.callEndpoint(AuthServiceRoutes.getUserAuthInfoRoutePath(), "GET", "", "", "", authToken);
	}

	public static Response getUserAuthorization(String auth0Token, String headers, String params) throws Exception {
		return CommonApiMethods.callEndpoint(AuthServiceRoutes.getUserAuthRoutePath(), "GET", "", headers, params, auth0Token);
	}

	public static JSONObject getUserAuthorizationJson(String auth0Token, String headers, String params) throws Exception {
		Response response = getUserAuthorization(auth0Token, headers, params);
		Assert.assertEquals(response.statusCode(), 202, "** Glance media direct call not successful.");
		return new JSONObject(response.asString());
	}

	public static void logOutUser(String auth0Token) throws Exception {
		Response response = CommonApiMethods.callEndpoint(AuthServiceRoutes.getlogOutUserAuthRoutePath(), "GET", "", "", "", auth0Token);
		Assert.assertEquals(response.statusCode(), 200, "FCC logout call not successful.");
	}

}
