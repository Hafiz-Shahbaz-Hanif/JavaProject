package com.DC.constants;

import com.DC.utilities.ReadConfig;

public class InsightsConstants {
    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();
    public static final String INSIGHTS_BASE_ENDPOINT = READ_CONFIG.getDcAppInsightsUrl();
    public static final String INSIGHTS_LEGACY_ENDPOINT = READ_CONFIG.getCpgEndpoint();
    public static final String INSIGHTS_PRODUCTS_URL = INSIGHTS_BASE_ENDPOINT + "/product-manager/products";
    public static final String INSIGHTS_PRODUCT_LIST_URL = INSIGHTS_BASE_ENDPOINT + "/product-manager/product-lists";
    public static final String INSIGHTS_CAMPAIGNS_URL = INSIGHTS_BASE_ENDPOINT + "/product-manager/campaigns";
    public static final String INSIGHTS_PROPERTIES_URL = INSIGHTS_BASE_ENDPOINT + "/product-manager/properties";
    public static final String INSIGHTS_IMPORTS_URL = INSIGHTS_BASE_ENDPOINT + "/product-manager/imports";
    public static final String INSIGHTS_WATCHLISTS_URL = INSIGHTS_BASE_ENDPOINT + "/watchlists";
    public static final String INSIGHTS_TASK_UI_MAPPING_URL = INSIGHTS_BASE_ENDPOINT + "/task-ui-mapping";
    public static final String INSIGHTS_TASK_HISTORY_URL = INSIGHTS_BASE_ENDPOINT + "/task-history";
    public static final String INSIGHTS_TASKS_URL = INSIGHTS_BASE_ENDPOINT + "/tasks";

    public static String getProductDetailsUrl(String productMasterId) {
        return INSIGHTS_BASE_ENDPOINT + "/product-manager/product-details/" + productMasterId;
    }

    public static String getProductDetailsUrl(String productMasterId, String localeId, String retailerId, String campaignId) {
        return INSIGHTS_BASE_ENDPOINT + "/product-manager/product-details/" + productMasterId + "?l=" + localeId + "&r=" + retailerId + "&c=" + campaignId;
    }

    public static String getMediaSiteDomain() {
        if (READ_CONFIG.getInsightsEnvironment().equalsIgnoreCase("prod")) {
            return "media.onespace.com";
        }
        return "media." + READ_CONFIG.getInsightsEnvironment() + ".onespace.com";
    }
}
