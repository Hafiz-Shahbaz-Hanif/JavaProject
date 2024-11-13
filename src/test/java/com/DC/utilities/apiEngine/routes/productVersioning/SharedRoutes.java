package com.DC.utilities.apiEngine.routes.productVersioning;

import com.DC.utilities.ReadConfig;

public abstract class SharedRoutes {

    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    protected static final String PRODUCT_VARIANT_REPO_HOST = READ_CONFIG.getProductVariantRepoEndpoint() + "/api";

    public static String getProductVariantImportHost() {
        return PRODUCT_VARIANT_REPO_HOST + "/pim-integration-import";
    }

    public static String getProductVariantImportRoutePath(String variantType) {
        return getProductVariantImportHost() + "/" + variantType;
    }

    public static String getProductVariantImportRoutePathDebug(String variantType) {
        return getProductVariantImportRoutePath(variantType) + "/debug";
    }

    public static String getProductVariantExportHost() {
        return PRODUCT_VARIANT_REPO_HOST + "/pim-integration-export";
    }

    public static String getProductVariantExportCoreRoutePath(String exportId) {
        return getProductVariantExportHost() + "/" + exportId;
    }

    public static String getProductVariantExportRoutePath(String variantType) {
        return getProductVariantExportHost() + "/" + variantType;
    }

    public static String getProductVariantExportRoutePathDebug(String variantType) {
        return getProductVariantExportRoutePath(variantType) + "/debug";
    }
}
