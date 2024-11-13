package com.DC.utilities.apiEngine.routes.insights.CPGAccount;

import com.DC.utilities.ReadConfig;

public class CPGAccountRoutes {

    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    private static final String CPG_ACCOUNT_HOST = READ_CONFIG.getInsightsAccountServiceUrl() + "/api";

    public static String getCpgAccountHost() {
        return CPG_ACCOUNT_HOST;
    }
}
