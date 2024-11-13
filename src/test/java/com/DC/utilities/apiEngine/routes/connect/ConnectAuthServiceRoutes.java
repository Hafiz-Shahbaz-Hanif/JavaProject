package com.DC.utilities.apiEngine.routes.hub.connect.authservice;

import com.DC.utilities.ReadConfig;

public class ConnectAuthServiceRoutes {

    public static final String CONNECT_PATH = "/cockpit/1.0.0";
    public static final String HUB_EXTERNAL_GATEWAY_CONNECT = ReadConfig.getInstance().getHubExternalGateway() + CONNECT_PATH;
    public static final String CONNECT_BASE_URL = ReadConfig.getInstance().getFilaBaseUri() + CONNECT_PATH;
    public static final String USER_INFO = "/user/info";
    public static final String DATE_INTERVAL = "/client/metadata/available/metail";
    public static final String GLANCE_MEDIA = "/atglance/grid/media";

    public static String getConnectUserInfoExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY_CONNECT + USER_INFO;
    }

    public static String getConnectUserInfoRoutePath() {
        return CONNECT_BASE_URL + USER_INFO;
    }

    public static String getDateIntervalExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY_CONNECT + DATE_INTERVAL;
    }

    public static String getDateIntervalRoutePath() {
        return CONNECT_BASE_URL + DATE_INTERVAL;
    }

    public static String getGlanceMediaExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY_CONNECT + GLANCE_MEDIA;
    }

    public static String getGlanceMediaRoutePath() {
        return CONNECT_BASE_URL + GLANCE_MEDIA;
    }
}