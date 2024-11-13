package com.DC.utilities.apiEngine.routes.adc.admin;

import com.DC.utilities.ReadConfig;

public class AdminRoutes {

    private static final String BASE_URI = ReadConfig.getInstance().getFilaBaseUri();
    private static final String HUB_EXTERNAL_GATEWAY = ReadConfig.getInstance().getHubExternalGateway();
    public static final String ADMIN_CLIENT_SELECTION = "/admin/client/selection";
    private static final String ADMIN_USER = "/admin/user";
    private static final String ADMIN_USER_INFO = "/admin/user/info";
    private static final String LOGOUT = "/admin/user/logout";
    private static final String FILA_ROLE = "/admin/role";
    private static final String FILA_ROLES = "/admin/role/all";
    private static final String FILA_BUS = "/admin/businessunit/all";


    public static String getAdminClientSelectionRoutePath() {
        return BASE_URI + ADMIN_CLIENT_SELECTION;
    }

    public static String getAdminUserRoutePath() {
        return HUB_EXTERNAL_GATEWAY + ADMIN_USER;
    }

    public static String getFilaRoleRoutePath() {
        return HUB_EXTERNAL_GATEWAY + FILA_ROLE;
    }

    public static String getFilaRolesRoutePath() {
        return HUB_EXTERNAL_GATEWAY + FILA_ROLES;
    }

    public static String getFilaBusRoutePath() {
        return HUB_EXTERNAL_GATEWAY + FILA_BUS;
    }

    public static String getAdminUserInfoRoutePath() {
        return BASE_URI + ADMIN_USER_INFO;
    }

    public static String getLogoutRoutePath() {
        return BASE_URI + LOGOUT;
    }

    public static String getAdminClientSelectionExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY + ADMIN_CLIENT_SELECTION;
    }

}