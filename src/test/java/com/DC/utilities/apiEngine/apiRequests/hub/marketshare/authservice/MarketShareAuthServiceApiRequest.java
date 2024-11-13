package com.DC.utilities.apiEngine.apiRequests.hub.marketshare.authservice;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.models.requests.hub.insights.HubInsightsProductChainProgressRequestBody;
import com.DC.utilities.apiEngine.models.requests.hub.marketshare.authservice.MarketShareUserUpdateRequestBody;
import com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice.MarketShareUserInfoResponseBody;
import com.DC.utilities.apiEngine.routes.hub.insights.cpgserver.HubInsightsCpgServerRoutes;
import com.DC.utilities.apiEngine.routes.hub.marketshare.authservice.MarketShareAuthServiceRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;

import java.util.List;

public class MarketShareAuthServiceApiRequest {

	public static String header = "Content-Type=application/json";

	public static Response getApiKey(String auth0Token) throws Exception {
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.getMarketShareAuthServiceRoutePath(), "GET", "", "", "", auth0Token);
	}

	public static Response getUserInfo(String auth0Token) throws Exception {
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.getMarketShareAuthServiceUserInfoRoutePath(), "GET", "", "", "", auth0Token);
	}

	public static Response getClients(String apiKey) throws Exception {
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.getClientsRoutePath(), "GET", "", "", "", apiKey);
	}

	public static Response getClientsExternalGateway(String headers, String authToken) throws Exception {
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.getClientsExternalGatewayRoutePath(), "GET", "", headers, "", authToken);
	}

	public static Response getTlds(String apiKey) throws Exception {
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.getTldsRoutePath(), "GET", "", "", "", apiKey);
	}

	public static Response getTldsExternalGateway(String headers, String authToken) throws Exception {
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.getTldsExternalGatewayRoutePath(), "GET", "", headers, "", authToken);
	}

	public static Response getTldCurrencyExternalGateway(String headers, String authToken) throws Exception {
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.getTldCurrencyExternalGatewayRoutePath(), "GET", "", headers, "", authToken);
	}

	public static Response getTldCurrency(String apiKey) throws Exception {
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.getTldCurrencyRoutePath(), "GET", "", "", "", apiKey);
	}

	public static Response getUserInfoExternalGateway(String auth0Token) throws Exception {
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.getMarketShareUserInfoExternalGatewayRoutePath(), "GET", "", "", "", auth0Token);
	}

	public static MarketShareUserInfoResponseBody getUserInfoResponseBodyExternalGateway(String auth0Token) throws Exception {
		return getUserInfoExternalGateway(auth0Token).getBody().as(MarketShareUserInfoResponseBody.class);
	}

	public static Response updateMsUser(String clientId, String userId, MarketShareUserUpdateRequestBody requestBody, String jwt) throws Exception {
		String reqBody = new ObjectMapper().writeValueAsString(requestBody);
		return CommonApiMethods.callEndpoint(MarketShareAuthServiceRoutes.updateMsUserRoutePath(clientId, userId), "PATCH", reqBody, header, "", jwt);
	}

	public static MarketShareUserInfoResponseBody.User getRandomMsUserAssociatedWithToken(String auth0Token) throws Exception {
		Response usersForToken = MarketShareAuthServiceApiRequest.getUserInfo(auth0Token);
		Assert.assertEquals(usersForToken.statusCode(), 201, "");
		MarketShareUserInfoResponseBody users = usersForToken.getBody().as(MarketShareUserInfoResponseBody.class);

		List<MarketShareUserInfoResponseBody.User> usrs = users.getUsers();
		usrs.remove(0	);
		return (MarketShareUserInfoResponseBody.User) SharedMethods.getRandomItemFromList(usrs);
	}

	public static JSONObject getUserInfoJson(String auth0Token) throws Exception {
		Response response = getUserInfo(auth0Token);
		Assert.assertEquals(response.statusCode(), 201, "Cannot get MS user info.");
		return new JSONObject(response.asString());
	}

}