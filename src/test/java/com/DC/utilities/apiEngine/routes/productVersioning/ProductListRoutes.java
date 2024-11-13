package com.DC.utilities.apiEngine.routes.productVersioning;

import com.DC.utilities.ReadConfig;

public class ProductListRoutes {

    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    private static final String PRODUCT_LIST_HOST = READ_CONFIG.getProductVariantRepoEndpoint() + "/api/product-variant-list";

    public static String getProductListRoutePath(String listId) {
        return PRODUCT_LIST_HOST + "/" + listId;
    }

    public static String getProductListHost() {
        return PRODUCT_LIST_HOST;
    }

    public static String getRemoveProductsFromListRoutePath(String listId) {
        return getProductListRoutePath(listId) + "/remove-products";
    }

    public static String getAddProductsFromListRoutePath(String listId) {
        return getProductListRoutePath(listId) + "/add-products";
    }
}
