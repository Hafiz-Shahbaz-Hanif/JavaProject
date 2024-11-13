package com.DC.utilities.apiEngine.routes.insights;

import com.DC.utilities.ReadConfig;

public class ProductRepoRoutes {
    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();
    private static final String PRODUCT_REPO_HOST = READ_CONFIG.getInsightsProductRepoEndpoint();

    public static String getTaskUIMappingConfigRoutePath(String mappingId) {
        return PRODUCT_REPO_HOST + "/api/taskuiconfigs/" + mappingId;
    }

    public static String getProductDetails(String productId) {
        return PRODUCT_REPO_HOST + "/api/products/" + productId;
    }
}
