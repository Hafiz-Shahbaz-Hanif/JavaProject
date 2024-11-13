package com.DC.apitests.hub.gateways;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Hashtable;

import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.routes.adc.admin.AdminRoutes;
import com.DC.utilities.apiEngine.routes.hub.connect.authservice.ConnectAuthServiceRoutes;
import com.DC.utilities.apiEngine.routes.hub.insights.authservice.AuthServiceRoutes;
import com.DC.utilities.apiEngine.routes.hub.marketshare.authservice.MarketShareAuthServiceRoutes;
import org.testng.annotations.DataProvider;

public class GatewayDataProvider {

    @DataProvider(name = "Auth0_Token_Validation")
    public static Object[][] Auth0_Token_Validation(Method method) {
        Dictionary<String, String> fila = new Hashtable<String, String>() {
            {
                put("platform", "fila");
                put("url", AdminRoutes.getAdminClientSelectionExternalGatewayRoutePath());
                put("token", SecurityAPI.expiredAuth0TokenForFila);
            }
        };
        Dictionary<String, String> insights = new Hashtable<String, String>() {
            {
                put("platform", "insights");
                put("url", AuthServiceRoutes.getCompanySchemaRouteExternalGatewayPath());
                put("token", "Bearer " + SecurityAPI.expiredAut0TokenForInsights);
            }
        };
        Dictionary<String, String> edge = new Hashtable<String, String>() {
            {
                put("platform", "edge");
                put("url", MarketShareAuthServiceRoutes.getMarketShareUserInfoExternalGatewayRoutePath());
                put("token", SecurityAPI.expiredAuthTokenForMarketShare);
            }
        };
        Dictionary<String, String> connect = new Hashtable<String, String>() {
            {
                put("platform", "connect");
                put("url", ConnectAuthServiceRoutes.getConnectUserInfoExternalGatewayRoutePath());
                put("token", SecurityAPI.expiredAuthTokenForConnect);
            }
        };
        Object[][] testData = {{fila}, {insights}, {edge}, {connect}};
        return testData;
    }


}