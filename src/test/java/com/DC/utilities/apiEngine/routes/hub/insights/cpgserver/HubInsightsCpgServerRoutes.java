package com.DC.utilities.apiEngine.routes.hub.insights.cpgserver;

import com.DC.utilities.ReadConfig;

public class HubInsightsCpgServerRoutes {

    public static final String BASE_CPG_SERVER_URI = ReadConfig.getInstance().getCpgServerUrl();
    private static final String CURRENT_USERS_EXPORT = "/api/current-users-export";
    private static final String PRODUCT_CHAIN_PROGRESS = "/api/ProductChainProgress";
    private static final String CATEGORIES_FILTER_EXPORT = "/api/categoriesFilterExport";

    public static String getCurrentUsersExportRoutePath() {
    	return BASE_CPG_SERVER_URI + CURRENT_USERS_EXPORT;
    }

    public static String getProductChainProgressRoutePath() {
    	return BASE_CPG_SERVER_URI + PRODUCT_CHAIN_PROGRESS;
    }

    public static String getCategoriesFilterExportRoutePath() {
    	return BASE_CPG_SERVER_URI + CATEGORIES_FILTER_EXPORT;
    }

    public static String getLaunchFileRoutePath() {
        return BASE_CPG_SERVER_URI + "/api/launch-file-export";
    }

    public static String getBatchFileCreteRoutePath() {
        return BASE_CPG_SERVER_URI + "/api/batch-file-create";
    }

}