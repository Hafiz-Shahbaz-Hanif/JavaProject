package com.DC.utilities.apiEngine.apiRequests.hub.connect.authservice;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.routes.hub.connect.authservice.ConnectAuthServiceRoutes;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

public class ConnectAuthServiceApiRequest {

    public static Response getUserInfoExternalGateway(String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(ConnectAuthServiceRoutes.getConnectUserInfoExternalGatewayRoutePath(), "GET", "", "", "", auth0Token);
    }

    public static JSONObject getUserInfoJsonExternalGateway(String auth0Token) throws Exception {
        Response response = getUserInfoExternalGateway(auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** User info call through external gateway not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getUserInfo(String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(ConnectAuthServiceRoutes.getConnectUserInfoRoutePath(), "GET", "", "", "", auth0Token);
    }

    public static JSONObject getUserInfoJson(String auth0Token) throws Exception {
        Response response = getUserInfo(auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** User info direct call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getDateIntervalExternalGateway(String auth0Token, String headers, String params) throws Exception {
        return CommonApiMethods.callEndpoint(ConnectAuthServiceRoutes.getDateIntervalExternalGatewayRoutePath(), "GET", "", headers, params, auth0Token);
    }

    public static JSONObject getDateIntervalJsonExternalGateway(String auth0Token, String headers, String params) throws Exception {
        Response response = getDateIntervalExternalGateway(auth0Token, headers, params);
        Assert.assertEquals(response.statusCode(), 200, "** Date interval call through external gateway not successful.");
        JSONArray arr = new JSONArray(response.asString());
        return arr.getJSONObject(0);
    }

    public static Response getDateInterval(String auth0Token, String headers, String params) throws Exception {
        return CommonApiMethods.callEndpoint(ConnectAuthServiceRoutes.getDateIntervalRoutePath(), "GET", "", headers, params, auth0Token);
    }

    public static JSONObject getDateIntervalJson(String auth0Token, String headers, String params) throws Exception {
        Response response = getDateInterval(auth0Token, headers, params);
        Assert.assertEquals(response.statusCode(), 200, "** User info call through external gateway not successful.");
        JSONArray arr = new JSONArray(response.asString());
        return arr.getJSONObject(0);
    }

    public static Response getGlanceMediaExternalGateway(String auth0Token, String headers, String params) throws Exception {
        return CommonApiMethods.callEndpoint(ConnectAuthServiceRoutes.getGlanceMediaExternalGatewayRoutePath(), "GET", "", headers, params, auth0Token);
    }

    public static JSONObject getGlanceMediaJsonExternalGateway(String auth0Token, String headers, String params) throws Exception {
        Response response = getGlanceMediaExternalGateway(auth0Token, headers, params);
        Assert.assertEquals(response.statusCode(), 200, "** Glance media call through external gateway not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getGlanceMedia(String auth0Token, String headers, String params) throws Exception {
        return CommonApiMethods.callEndpoint(ConnectAuthServiceRoutes.getGlanceMediaRoutePath(), "GET", "", headers, params, auth0Token);
    }

    public static JSONObject getGlanceMediaJson(String auth0Token, String headers, String params) throws Exception {
        Response response = getGlanceMedia(auth0Token, headers, params);
        Assert.assertEquals(response.statusCode(), 200, "** Glance media direct call not successful.");
        return new JSONObject(response.asString());
    }

}