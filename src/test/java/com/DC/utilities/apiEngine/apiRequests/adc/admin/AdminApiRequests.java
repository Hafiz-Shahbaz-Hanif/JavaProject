package com.DC.utilities.apiEngine.apiRequests.adc.admin;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.adc.admin.AdminUserRequestBody;
import com.DC.utilities.apiEngine.routes.adc.admin.AdminRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

public class AdminApiRequests {

    public static String header = "Content-Type=application/json";

    public static Response adminClientSelection(String headers, String parameters, String jwt) throws Exception {
        return CommonApiMethods.callEndpoint(AdminRoutes.getAdminClientSelectionRoutePath(), "GET", "", headers, parameters, jwt);
    }

    public static Response adminUser(AdminUserRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        Response responseUpdateUser = CommonApiMethods.callEndpoint(AdminRoutes.getAdminUserRoutePath(), "PUT", reqBody, header, "", jwt);
        Assert.assertEquals(responseUpdateUser.statusCode(), 200, "Cannot update Fila user.");
        return responseUpdateUser;
    }

    public static Response getAdminUserInfo(String jwt) throws Exception {
        Response userInfo =	CommonApiMethods.callEndpoint(AdminRoutes.getAdminUserInfoRoutePath(), "GET", "", header, "", jwt);
        Assert.assertEquals(userInfo.statusCode(), 200, "Cannot get Fila user info.");
        return userInfo;
    }

    public static Response userLogout(String headers, String parameters, String token) throws Exception {
        return	CommonApiMethods.callEndpoint(AdminRoutes.getLogoutRoutePath(), "PUT", "", headers, parameters, token);
    }

    public static Response adminClientSelectionExternalGateway(String headers, String parameters, String jwt) throws Exception {
        return CommonApiMethods.callEndpoint(AdminRoutes.getAdminClientSelectionExternalGatewayRoutePath(), "GET", "", headers, parameters, jwt);
    }

    public static String getAdminUserInfoString(String jwt) throws Exception {
        Response response = getAdminUserInfo(jwt);
        return response.asString();
    }

    public static JSONObject getAdminUserInfoJson(String jwt) throws Exception {
        String response = getAdminUserInfoString(jwt);
        return new JSONObject(response);
    }

    public static Response getFilaRoles(String auth0Token) throws Exception {
        String reqBody = "{\"pagingAttributes\":{\"pageSize\":100,\"page\":1}}";
        return CommonApiMethods.callEndpoint(AdminRoutes.getFilaRolesRoutePath(), "POST", reqBody, header, "", auth0Token);
    }

    public static JSONObject getFilaRolesJson(String auth0Token) throws Exception {
        Response response = getFilaRoles(auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Get Fila roles call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getFilaBus(String auth0Token) throws Exception {
        return CommonApiMethods.callEndpoint(AdminRoutes.getFilaBusRoutePath(), "GET", "", header, "", auth0Token);
    }

    public static JSONArray getFilaBuJson(String auth0Token) throws Exception {
        Response response = getFilaBus(auth0Token);
        Assert.assertEquals(response.statusCode(), 200, "** Get Fila BUs call not successful.");
        return new JSONArray(response.asString());
    }

}