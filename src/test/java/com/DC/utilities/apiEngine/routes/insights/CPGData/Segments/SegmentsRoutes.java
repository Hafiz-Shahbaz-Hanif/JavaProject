package com.DC.utilities.apiEngine.routes.insights.CPGData.Segments;

import com.DC.utilities.ReadConfig;

public class SegmentsRoutes {

    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    public static final String CATEGORIES_HOST = READ_CONFIG.getCpgDataServiceUrl() + "/categories";

    public static String getCategoryRoute(String categoryId) {
        return CATEGORIES_HOST + "/" + categoryId;
    }
}
