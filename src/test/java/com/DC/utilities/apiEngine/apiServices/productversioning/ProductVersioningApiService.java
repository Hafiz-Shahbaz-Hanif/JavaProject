package com.DC.utilities.apiEngine.apiServices.productversioning;

import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.models.requests.productVersioning.*;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.apiEngine.routes.productVersioning.ProductVersioningRoutes;
import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class ProductVersioningApiService {
    private static Logger LOGGER = Logger.getLogger(ProductVersioningApiService.class);

    public static ProductMaster createProductMaster(CreateProductMasterRequestBody requestBody, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.createProductMaster(requestBody, jwt);
        return response.getBody().as(ProductMaster.class);
    }

    public static ProductMaster getProductMasterByUniqueId(String uniqueId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getProductMasterByUniqueId(uniqueId, jwt);
        return response.getBody().as(ProductMaster.class);
    }

    public static ProductMaster getProductMaster(String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getProductMaster(productMasterId, jwt);
        return response.getBody().as(ProductMaster.class);
    }

    public static boolean deleteProductMaster(String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.deleteProductMaster(productMasterId, jwt);
        String responseBody = response.getBody().asString();
        return Boolean.parseBoolean(responseBody);
    }

    public static void deleteProductMasterByUniqueId(String productMasterUniqueId, String jwt) throws Exception {
        var product = getProductWithUniqueIdIfExist(productMasterUniqueId, jwt);
        if (product != null) {
            deleteProductMaster(product._id, jwt);
        }
    }

    public static List<ProductMaster> getAllProductMastersFromCompany(String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getAllProductMastersFromCompany(jwt);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.getBody().asString(), new TypeReference<List<ProductMaster>>() {});
    }

    public static ProductMaster createProductVariant(String productMasterId, String localeId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.createProductVariant(productMasterId, localeId, jwt);
        return response.getBody().as(ProductMaster.class);
    }

    public static ProductMaster.VariantSets.Live getProductVariantData(String productMasterId, String localeId, String type, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getProductVariantData(productMasterId, localeId, type, jwt);
        return response.getBody().as(ProductMaster.VariantSets.Live.class);
    }

    public static <T> T getLiveProductInstanceByUniqueId(String uniqueId, String localeId, String campaignId, String retailerId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getLiveProductInstanceByUniqueId(uniqueId, localeId, campaignId, retailerId, jwt);
        return getInstance(campaignId, retailerId, response);
    }

    public static <T> T getLiveProductInstance(InstancePathBase instancePath, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getLiveProductInstance(instancePath, jwt);
        return getInstance(instancePath.campaignId, instancePath.retailerId, response);
    }

    public static boolean deleteProductMasterVariants(String productMasterId, List<String> localeIdsToDelete, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.deleteProductMasterVariants(productMasterId, localeIdsToDelete, jwt);
        String responseBody = response.getBody().asString();
        return Boolean.parseBoolean(responseBody);
    }

    public static boolean deleteVariantInstances(String productMasterId, DeleteVariantInstancesRequestBody requestBody, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.deleteVariantInstances(productMasterId, requestBody, jwt);
        String responseBody = response.getBody().asString();
        return Boolean.parseBoolean(responseBody);
    }

    public static List<ProductMasterComposition> getProductMasterComposed(String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getProductMasterComposed(productMasterId, jwt);
        String responseBody = response.getBody().asString();
        TypeReference<List<ProductMasterComposition>> typeReference = new TypeReference<List<ProductMasterComposition>>() {};
        return new ObjectMapper().readValue(responseBody, typeReference);
    }

    public static List<ProductMasterCompositionStaged> getProductMasterComposedStaged(String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getProductMasterComposedStaged(productMasterId, jwt);
        String responseBody = response.getBody().asString();
        TypeReference<List<ProductMasterCompositionStaged>> typeReference = new TypeReference<>() {
        };
        return new ObjectMapper().readValue(responseBody, typeReference);
    }

    public static ProductMasterComposition getProductVariantComposed(String productMasterId, String localeId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getProductVariantComposed(productMasterId, localeId, jwt);
        return response.getBody().as(ProductMasterComposition.class);
    }

    public static ProductMasterInstanceComposition getProductInstanceComposed(InstancePathBase instancePath, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getProductInstanceComposed(instancePath, jwt);
        return response.getBody().as(ProductMasterInstanceComposition.class);
    }

    public static <T> T getProductInstanceComposed(InstancePathBase instancePath, String jwt, Class<T> clazz) throws Exception {
        Response response = ProductVersioningApiRequests.getProductInstanceComposed(instancePath, jwt);
        return response.getBody().as(clazz);
    }

    public static <T> T getProductInstanceComposition(InstancePathBase instancePath, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getProductInstanceComposed(instancePath, jwt);

        T responseBody;
        Class<T> responseClass;
        if (instancePath.campaignId == null && instancePath.retailerId == null) {
            responseClass = (Class<T>) ProductMasterInstanceComposition.class;
        } else if (instancePath.campaignId == null && instancePath.retailerId != null) {
            responseClass = (Class<T>) ProductMasterRetailerInstanceComposition.class;
        } else if (instancePath.campaignId != null && instancePath.retailerId == null) {
            responseClass = (Class<T>) ProductMasterInstanceComposition.class;
        } else {
            responseClass = (Class<T>) ProductMasterRetailerInstanceComposition.class;
        }
        return response.getBody().as(responseClass);
    }

    public static ProductVariantPropertySet replaceVariantPropertySet(ProductVariantPropertySetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.replaceVariantPropertySet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantPropertySet.class);
    }

    public static ProductVariantPropertySet updateVariantProperties(ProductVariantPropertySetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.updateVariantProperties(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantPropertySet.class);
    }

    public static ProductVariantPropertySet addPropertiesToVariantPropertySet(ProductVariantPropertySetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.addPropertiesToVariantPropertySet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantPropertySet.class);
    }

    public static ProductVariantPropertySet deletePropertiesFromVariantPropertySet(DeleteVariantPropertiesRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.deletePropertiesFromVariantPropertySet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantPropertySet.class);
    }
    
    public static ProductVariantPropertySet getPropertySetData(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getPropertySetData(instancePath, chainItemId, jwt);

        if (response.getBody().asString().isEmpty()) {
            return new ProductVariantPropertySet();
        }
        return response.getBody().as(ProductVariantPropertySet.class);
    }

    public static ProductVariantPropertySet getPropertySetDataByUniqueId(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getPropertySetDataByUniqueId(instancePath, chainItemId, jwt);
        if (response.getBody().asString().isEmpty()) {
            return new ProductVariantPropertySet();
        }
        return response.getBody().as(ProductVariantPropertySet.class);
    }

    public static ProductVariantDigitalAssetSet replaceVariantDigitalAssetSet(ProductVariantDigitalAssetSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.replaceVariantDigitalAssetSet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantDigitalAssetSet.class);
    }

    public static ProductVariantDigitalAssetSet updateVariantDigitalAssetSet(ProductVariantDigitalAssetSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.updateVariantDigitalAssetSet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantDigitalAssetSet.class);
    }

    public static ProductVariantDigitalAssetSet addDigitalAssetsToVariantDigitalAssetsSet(ProductVariantDigitalAssetSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.addDigitalAssetsToVariantDigitalAssetsSet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantDigitalAssetSet.class);
    }

    public static ProductVariantDigitalAssetSet deleteDigitalAssetsFromVariantDigitalAssetsSet(DeleteVariantDigitalAssetsRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.deleteDigitalAssetsFromVariantDigitalAssetsSet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantDigitalAssetSet.class);
    }

    public static ProductVariantDigitalAssetSet getDigitalAssetSetData(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getDigitalAssetSetData(instancePath, chainItemId, jwt);

        if (response.getBody().asString().isEmpty()) {
            return new ProductVariantDigitalAssetSet();
        }
        return response.getBody().as(ProductVariantDigitalAssetSet.class);
    }

    public static ProductVariantDigitalAssetSet getDigitalAssetSetDataByUniqueId(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getDigitalAssetSetDataByUniqueId(instancePath, chainItemId, jwt);
        if (response.getBody().asString().isEmpty()) {
            return new ProductVariantDigitalAssetSet();
        }
        return response.getBody().as(ProductVariantDigitalAssetSet.class);
    }

    public static ProductInvariantAttributeSet replaceVariantAttributeSet(ProductInvariantAttributeSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.replaceVariantAttributeSet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductInvariantAttributeSet.class);
    }

    public static ProductInvariantAttributeSet addAttributesToVariantAttributeSet(ProductInvariantAttributeSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.addAttributesToVariantAttributeSet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductInvariantAttributeSet.class);
    }

    public static ProductInvariantAttributeSet getAttributeSetData(String productMasterId, String localeId, Enums.ProductVariantType type, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getAttributeSetData(productMasterId, localeId, type.getType(), jwt);
        return response.getBody().as(ProductInvariantAttributeSet.class);
    }

    public static ProductInvariantAttributeSet getAttributeSetDataByUniqueId(String uniqueId, String localeId, Enums.ProductVariantType type, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getAttributeSetDataByUniqueId(uniqueId, localeId, type.getType(), jwt);
        return response.getBody().as(ProductInvariantAttributeSet.class);
    }

    public static ProductInvariantAttributeSet deleteAttributesFromVariantAttributeSet(String productMasterId, String localeId, List<ProductInvariantAttribute> attributesToRemove, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.deleteAttributesFromVariantAttributeSet(productMasterId, localeId, attributesToRemove, jwt);
        return response.getBody().as(ProductInvariantAttributeSet.class);
    }

    public static ProductVariantKeywordSet replaceVariantKeywordSet(ProductVariantKeywordSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.replaceVariantKeywordSet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantKeywordSet.class);
    }

    public static ProductVariantKeywordSet addKeywordsToVariantKeywordSet(ProductVariantKeywordSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.addKeywordsToVariantKeywordSet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantKeywordSet.class);
    }

    public static ProductVariantKeywordSet getProductKeywordSet(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getProductKeywordSet(instancePath, chainItemId, jwt);

        if (response.getBody().asString().isEmpty()) {
            return new ProductVariantKeywordSet();
        }

        return response.getBody().as(ProductVariantKeywordSet.class);
    }

    public static ProductVariantKeywordSet getProductKeywordSetByUniqueId(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getKeywordSetDataByUniqueId(instancePath, chainItemId, jwt);
        if (response.getBody().asString().isEmpty()) {
            return new ProductVariantKeywordSet();
        }
        return response.getBody().as(ProductVariantKeywordSet.class);
    }

    public static ProductVariantKeywordSet deleteKeywordsFromVariantKeywordSet(ProductVariantKeywordSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.deleteKeywordsFromVariantKeywordSet(requestBody, productMasterId, jwt);
        return response.getBody().as(ProductVariantKeywordSet.class);
    }

    public static ExportResponse exportProductProperties(ExportProductPropertyRequestBody requestBody, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.exportProductProperties(requestBody, jwt);
        return response.getBody().as(ExportResponse.class);
    }

    public static ExportResponse exportProductKeywords(List<ProductVariantInstancePath> requestBody, String type, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.exportProductKeywords(requestBody, type, jwt);
        return response.getBody().as(ExportResponse.class);
    }

    public static ExportCore getExportCore(String exportId, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.getExportCore(exportId, jwt);
        return response.getBody().as(ExportCore.class);
    }

    public static ProductMaster getProductWithUniqueIdIfExist(String uniqueId, String jwt) {
        try {
            return getProductMasterByUniqueId(uniqueId, jwt);
        } catch (Exception e) {
            LOGGER.info("Product with unique id " + uniqueId + " does not exist");
            return null;
        }
    }

    public static void createProductVersionIfNotExistent(UserFriendlyInstancePath instancePath, String jwt) throws Exception {
        ProductMaster productMaster = ProductVersioningApiService.getProductWithUniqueIdIfExist(instancePath.productIdentifier, jwt);
        if (productMaster == null) {
            productMaster = ProductVersioningApiService.createProductMaster(new CreateProductMasterRequestBody(
                    instancePath.productIdentifier,
                    instancePath.productIdentifier,
                    null
            ), jwt);
        }
        Company company = CompanyApiService.getCompany(jwt);
        String localeId = company.getLocaleId(instancePath.localeName);
        String retailerId = company.getRetailerId(instancePath.retailerName);
        String campaignId = company.getCampaignId(instancePath.campaignName);
        InstancePathBase instancePathBase = new InstancePathBase(productMaster._id, localeId, retailerId, campaignId);

        var response = ProductVersioningApiRequests.createInstanceLive(instancePathBase, jwt);
    }

    public static ProductMaster updateInstanceRPCAndBusinessUnits(InstancePathBase instancePath, Map.Entry<String, Boolean> rpc, Map.Entry<List<String>, Boolean> businessUnits, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.updateInstanceRPCAndBusinessUnits(instancePath, rpc, businessUnits, jwt);
        return response.getBody().as(ProductMaster.class);
    }

    private static <T> T getInstance(String campaignId, String retailerId, Response response) {
        T responseBody;
        Class<T> responseClass;

        if (campaignId == null && retailerId == null) {
            responseClass = (Class<T>) ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal.class;
        } else if (campaignId == null && retailerId != null) {
            responseClass = (Class<T>) ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceRetailer.class;
        } else if (campaignId != null && retailerId == null) {
            responseClass = (Class<T>) ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceCampaign.class;
        } else {
            responseClass = (Class<T>) ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceRetailerCampaign.class;
        }
        responseBody = response.getBody().as(responseClass);

        return responseBody;
    }

}
