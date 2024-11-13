package com.DC.utilities.apiEngine.routes.hub.marketshare.authservice;

import com.DC.utilities.ReadConfig;

public class MarketShareAuthServiceRoutes {

    public static final String MARKETSHARE_BASE_AUTH_URI = ReadConfig.getInstance().getMarketShareBaseUri();
    public static final String HUB_EXTERNAL_GATEWAY = ReadConfig.getInstance().getHubExternalGateway() + "/ms";
    public static final String AUTH_SERVICE = "/v2/user_auth/token_exchange";
    public static final String AUTH_SERVICE_USER_INFO = "/v2/user_auth/userinfo";
    public static final String CLIENTS = "/clients";
    public static final String TLDS = "/tld";
    public static final String TLD_CURRENCY = "/tld/currency";



    public static String getMarketShareAuthServiceRoutePath() {
        return MARKETSHARE_BASE_AUTH_URI + AUTH_SERVICE;
    }

    public static String getClientsRoutePath() {
        return MARKETSHARE_BASE_AUTH_URI + CLIENTS;
    }

    public static String getClientsExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY + CLIENTS;
    }

    public static String getTldsRoutePath() {
        return MARKETSHARE_BASE_AUTH_URI + TLDS;
    }

    public static String getTldsExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY + TLDS;
    }

    public static String getTldCurrencyRoutePath() {
        return MARKETSHARE_BASE_AUTH_URI + TLD_CURRENCY;
    }

    public static String getTldCurrencyExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY + TLD_CURRENCY;
    }

    public static String getMarketShareAuthServiceUserInfoRoutePath() {
        return MARKETSHARE_BASE_AUTH_URI + AUTH_SERVICE_USER_INFO;
    }

    public static String getMarketShareUserInfoExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY + AUTH_SERVICE_USER_INFO;
    }

    public static String updateMsUserRoutePath(String clientId, String userId) {
        return MARKETSHARE_BASE_AUTH_URI + "/clients/"+clientId+"/users/"+userId;
    }


}