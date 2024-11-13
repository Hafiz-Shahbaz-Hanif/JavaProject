package com.DC.utilities.apiEngine.routes.productVersioning;

import com.DC.utilities.ReadConfig;

public class OpenSearchRoutes {
    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    public static final String OPEN_SEARCH_HOST = READ_CONFIG.getProductVariantRepoEndpoint() + "/api/search";

    public static String getSearchInstanceWithIndexRoutePath(String instanceId) {
        return OPEN_SEARCH_HOST + "/" + instanceId + "/with-index";
    }
}
