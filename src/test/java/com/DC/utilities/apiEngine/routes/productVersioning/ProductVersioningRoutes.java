package com.DC.utilities.apiEngine.routes.productVersioning;


public class ProductVersioningRoutes extends SharedRoutes {

    public static String getProductMasterRoutePath() {
        return PRODUCT_VARIANT_REPO_HOST + "/product-master";
    }

    public static String getProductMasterRoutePath(String productMasterId) {
        return PRODUCT_VARIANT_REPO_HOST + "/product-master/" + productMasterId;
    }

    public static String getProductMasterByUniqueIdRoutePath(String uniqueId) {
        return PRODUCT_VARIANT_REPO_HOST + "/product-master/unique/" + uniqueId;
    }

    public static String getProductVariantRoutePath(String productMasterId) {
        return getProductMasterRoutePath(productMasterId) + "/variant";
    }

    public static String getProductVariantsRoutePath(String productMasterId) {
        return getProductMasterRoutePath(productMasterId) + "/variants";
    }

    public static String getVariantInstancesRoutePath(String productMasterId) {
        return getProductMasterRoutePath(productMasterId) + "/instances";
    }

    public static String getVariantInstanceRoutePath(String productMasterId) {
        return getProductMasterRoutePath(productMasterId) + "/instance";
    }

    public static String getProductVariantRoutePath(String productMasterId, String localeId, String type) {
        return getProductVariantRoutePath(productMasterId) + "/locale/" + localeId + "/type/" + type;
    }

    public static String getProductVariantByUniqueIdRoutePath(String uniqueId, String localeId, String type) {
        return getProductMasterByUniqueIdRoutePath(uniqueId) + "/variant/locale/" + localeId + "/type/" + type;
    }

    public static String getProductInstanceByUniqueIdRoutePath(String uniqueId, String localeId, String type) {
        return getProductMasterByUniqueIdRoutePath(uniqueId) + "/instance/locale/" + localeId + "/type/" + type;
    }

    public static String getProductInstanceRoutePath(String productMasterId, String localeId, String type) {
        return getProductMasterRoutePath(productMasterId) + "/instance/locale/" + localeId + "/type/" + type;
    }

    public static String getProductVariantPropertiesRoutePath(String productMasterId) {
        return getProductMasterRoutePath(productMasterId) + "/properties";
    }

    public static String getProductVariantPropertiesRoutePath(String productMasterId, String localeId, String type) {
        return getProductVariantPropertiesRoutePath(productMasterId) + "/locale/" + localeId + "/type/" + type;
    }

    public static String getProductVariantPropertiesByUniqueIdRoutePath(String uniqueId, String localeId, String type) {
        return getProductMasterByUniqueIdRoutePath(uniqueId) + "/properties/locale/" + localeId + "/type/" + type;
    }

    public static String getProductVariantDigitalAssetsRoutePath(String productMasterId) {
        return getProductMasterRoutePath(productMasterId) + "/digital-asset";
    }

    public static String getProductVariantDigitalAssetsRoutePath(String productMasterId, String localeId, String type) {
        return getProductVariantDigitalAssetsRoutePath(productMasterId) + "/locale/" + localeId + "/type/" + type;
    }

    public static String getProductVariantDigitalAssetsByUniqueIdRoutePath(String uniqueId, String localeId, String type) {
        return getProductMasterByUniqueIdRoutePath(uniqueId) + "/digital-asset/locale/" + localeId + "/type/" + type;
    }

    public static String getProductInvariantAttributesRoutePath(String productMasterId) {
        return getProductMasterRoutePath(productMasterId) + "/attributes";
    }

    public static String getProductInvariantAttributesRoutePath(String productMasterId, String localeId, String type) {
        return getProductInvariantAttributesRoutePath(productMasterId) + "/locale/" + localeId + "/type/" + type;
    }

    public static String getProductInvariantAttributesByUniqueIdRoutePath(String uniqueId, String localeId, String type) {
        return getProductMasterByUniqueIdRoutePath(uniqueId) + "/attributes/locale/" + localeId + "/type/" + type;
    }

    public static String getProductVariantKeywordsRoutePath(String productMasterId) {
        return getProductMasterRoutePath(productMasterId) + "/keywords";
    }

    public static String getProductVariantKeywordsRoutePath(String productMasterId, String localeId, String type) {
        return getProductVariantKeywordsRoutePath(productMasterId) + "/locale/" + localeId + "/type/" + type;
    }

    public static String getProductVariantKeywordsByUniqueIdRoutePath(String uniqueId, String localeId, String type) {
        return getProductMasterByUniqueIdRoutePath(uniqueId) + "/keywords/locale/" + localeId + "/type/" + type;
    }

    public static String getProductPropertyExportRoutePath() {
        return getProductVariantExportHost() + "/property/debug";
    }

    public static String getProductAttributeExportRoutePath() {
        return getProductVariantExportHost() + "/attribute/debug";
    }

    public static String getProductKeywordExportRoutePath() {
        return getProductVariantExportHost() + "/keyword/debug";
    }

    public static String getProductDigitalAssetsExportRoutePath() {
        return getProductVariantExportHost() + "/digital-asset/debug";
    }

    public static String getProductMasterCompositionRoutePath() {
        return PRODUCT_VARIANT_REPO_HOST + "/product-master-composition";
    }

    public static String getProductMasterCompositionRoutePath(String productMasterId) {
        return getProductMasterCompositionRoutePath() + "/" + productMasterId;
    }

    public static String getProductVariantCompositionRoutePath(String productMasterId, String localeId) {
        return getProductMasterCompositionRoutePath(productMasterId) + "/variant/" + localeId;
    }

    public static String getProductInstanceCompositionRoutePath(String productMasterId, String localeId) {
        return getProductVariantCompositionRoutePath(productMasterId, localeId) + "/instance";
    }
}
