package com.DC.utilities.apiEngine.apiRequests.productVersioning;

import com.DC.objects.RestAPI;
import com.DC.utilities.apiEngine.models.requests.productVersioning.*;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductInvariantAttribute;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantInstancePath;
import com.DC.utilities.apiEngine.models.responses.productVersioning.InstancePathBase;
import com.DC.utilities.apiEngine.routes.productVersioning.ProductVersioningRoutes;
import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.*;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class ProductVersioningApiRequests extends SharedRequests {

    public static Response createProductMaster(CreateProductMasterRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response getProductMaster(String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath(productMasterId), jwt, "GET", "", "");
    }

    public static Response getProductMasterByUniqueId(String uniqueId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductMasterByUniqueIdRoutePath(uniqueId), jwt, "GET", "", "");
    }

    public static Response getAllProductMastersFromCompany(String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath(), jwt, "GET", "", "");
    }

    public static Response deleteProductMaster(String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath(productMasterId), jwt, "DELETE", "", "");
    }

    public static Response createProductVariant(String productMasterId, String localeId, String jwt) throws Exception {
        String reqBody = "{\"localeId\"" + ": \"" + localeId + "\"}";
        return callEndpoint(ProductVersioningRoutes.getProductVariantRoutePath(productMasterId), jwt, "POST", reqBody, "");
    }

    public static Response getProductVariantData(String productMasterId, String localeId, String type, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantRoutePath(productMasterId, localeId, type), jwt, "GET", "", "");
    }

    public static Response getProductVariantDataByUniqueId(String uniqueId, String localeId, String type, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantByUniqueIdRoutePath(uniqueId, localeId, type), jwt, "GET", "", "");
    }

    public static Response getLiveProductInstanceByUniqueId(String uniqueId, String localeId, String campaignId, String retailerId, String jwt) throws Exception {
        String reqURI = ProductVersioningRoutes.getProductInstanceByUniqueIdRoutePath(uniqueId, localeId, Enums.ProductVariantType.LIVE.getType());
        InstancePathBase pathBase = new InstancePathBase();
        pathBase.campaignId = campaignId;
        pathBase.retailerId = retailerId;
        return generateRestAPIAndCallGetEndpoint(reqURI, pathBase, null, jwt);
    }

    public static Response getLiveProductInstance(InstancePathBase instancePath, String jwt) throws Exception {
        String reqURI = ProductVersioningRoutes.getProductInstanceRoutePath(instancePath.productMasterId, instancePath.localeId, Enums.ProductVariantType.LIVE.getType());
        InstancePathBase pathBase = new InstancePathBase();
        pathBase.campaignId = instancePath.campaignId;
        pathBase.retailerId = instancePath.retailerId;
        return generateRestAPIAndCallGetEndpoint(reqURI, pathBase, null, jwt);
    }

    public static Response deleteProductMasterVariants(String productMasterId, List<String> localeIdsToDelete, String jwt) throws Exception {
        StringBuilder ids = new StringBuilder();
        for (String id : localeIdsToDelete) {
            ids.append("\"").append(id).append("\",");
        }
        ids.deleteCharAt(ids.length() - 1);

        String reqBody = "{\n" +
                "   \"localeIdsToDelete\": [" + ids + "]" +
                "\n}";

        return callEndpoint(ProductVersioningRoutes.getProductVariantsRoutePath(productMasterId), jwt, "DELETE", reqBody, "");
    }

    public static Response deleteVariantInstances(String productMasterId, DeleteVariantInstancesRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return deleteVariantInstances(reqBody, productMasterId, jwt);
    }

    public static Response deleteVariantInstances(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getVariantInstancesRoutePath(productMasterId), jwt, "DELETE", requestBody, "");
    }

    public static Response deleteVariantInstances(String productMasterId, List<String> deletedInstances, String jwt) throws Exception {
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("productMasterId", productMasterId);
        bodyData.put("deletedInstances", deletedInstances);
        var reqBody = new ObjectMapper().writeValueAsString(Collections.singletonList(bodyData));

        var endpoint = ProductVersioningRoutes.getProductMasterRoutePath() + "/instances";
        return callEndpoint(endpoint, jwt, "DELETE", reqBody, "");
    }

    public static Response getProductMasterComposed(String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductMasterCompositionRoutePath(productMasterId), jwt, "GET", "", "");
    }

    public static Response getProductMasterComposedStaged(String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductMasterCompositionRoutePath(productMasterId) + "/staged", jwt, "GET", "", "");
    }

    public static Response getProductVariantComposed(String productMasterId, String localeId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantCompositionRoutePath(productMasterId, localeId), jwt, "GET", "", "");
    }

    public static Response getProductInstanceComposed(InstancePathBase instancePath, String jwt) throws Exception {
        String reqURI = ProductVersioningRoutes.getProductInstanceCompositionRoutePath(instancePath.productMasterId, instancePath.localeId);
        return generateRestAPIAndCallGetEndpoint(reqURI, instancePath, null, jwt);
    }

    public static Response replaceVariantPropertySet(ProductVariantPropertySetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return replaceVariantPropertySet(reqBody, productMasterId, jwt);
    }

    public static Response replaceVariantPropertySet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantPropertiesRoutePath(productMasterId), jwt, "POST", requestBody, "");
    }

    public static Response getPropertySetData(String productMasterId, String localeId, String type, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantPropertiesRoutePath(productMasterId, localeId, type), jwt, "GET", "", "");
    }

    public static Response getPropertySetData(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        String reqURI = ProductVersioningRoutes.getProductVariantPropertiesRoutePath(instancePath.productMasterId) +
                "/locale/" + instancePath.localeId + "/type/" + instancePath.type.getType();

        return generateRestAPIAndCallGetEndpoint(reqURI, instancePath, chainItemId, jwt);
    }

    // instead of productMasterId use uniqueId
    public static Response getPropertySetDataByUniqueId(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        String reqURI = ProductVersioningRoutes.getProductVariantPropertiesByUniqueIdRoutePath(instancePath.productMasterId, instancePath.localeId, instancePath.type.getType());
        InstancePathBase pathBase = new InstancePathBase();
        pathBase.campaignId = instancePath.campaignId;
        pathBase.retailerId = instancePath.retailerId;
        return generateRestAPIAndCallGetEndpoint(reqURI, pathBase, chainItemId, jwt);
    }

    public static Response updateVariantProperties(ProductVariantPropertySetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return updateVariantProperties(reqBody, productMasterId, jwt);
    }

    public static Response updateVariantProperties(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantPropertiesRoutePath(productMasterId), jwt, "PUT", requestBody, "");
    }

    public static Response addPropertiesToVariantPropertySet(ProductVariantPropertySetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return addPropertiesToVariantPropertySet(reqBody, productMasterId, jwt);
    }

    public static Response addPropertiesToVariantPropertySet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantPropertiesRoutePath(productMasterId), jwt, "PATCH", requestBody, "");
    }

    public static Response deletePropertiesFromVariantPropertySet(DeleteVariantPropertiesRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return deletePropertiesFromVariantPropertySet(reqBody, productMasterId, jwt);
    }

    public static Response deletePropertiesFromVariantPropertySet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantPropertiesRoutePath(productMasterId), jwt, "DELETE", requestBody, "");
    }

    public static Response replaceVariantDigitalAssetSet(ProductVariantDigitalAssetSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return replaceVariantDigitalAssetSet(reqBody, productMasterId, jwt);
    }

    public static Response replaceVariantDigitalAssetSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantDigitalAssetsRoutePath(productMasterId), jwt, "POST", requestBody, "");
    }

    public static Response getDigitalAssetSetData(String productMasterId, String localeId, String type, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantDigitalAssetsRoutePath(productMasterId, localeId, type), jwt, "GET", "", "");
    }

    public static Response getDigitalAssetSetData(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        String reqURI = ProductVersioningRoutes.getProductVariantDigitalAssetsRoutePath(instancePath.productMasterId) +
                "/locale/" + instancePath.localeId + "/type/" + instancePath.type.getType();

        return generateRestAPIAndCallGetEndpoint(reqURI, instancePath, chainItemId, jwt);
    }

    public static Response getDigitalAssetSetDataByUniqueId(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        String reqURI = ProductVersioningRoutes.getProductVariantDigitalAssetsByUniqueIdRoutePath(instancePath.productMasterId, instancePath.localeId, instancePath.type.getType());
        InstancePathBase pathBase = new InstancePathBase();
        pathBase.campaignId = instancePath.campaignId;
        pathBase.retailerId = instancePath.retailerId;
        return generateRestAPIAndCallGetEndpoint(reqURI, pathBase, chainItemId, jwt);
    }

    public static Response updateVariantDigitalAssetSet(ProductVariantDigitalAssetSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return updateVariantDigitalAssetSet(reqBody, productMasterId, jwt);
    }

    public static Response updateVariantDigitalAssetSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantDigitalAssetsRoutePath(productMasterId), jwt, "PUT", requestBody, "");
    }

    public static Response addDigitalAssetsToVariantDigitalAssetsSet(ProductVariantDigitalAssetSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return addDigitalAssetsToVariantDigitalAssetsSet(reqBody, productMasterId, jwt);
    }

    public static Response addDigitalAssetsToVariantDigitalAssetsSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantDigitalAssetsRoutePath(productMasterId), jwt, "PATCH", requestBody, "");
    }

    public static Response deleteDigitalAssetsFromVariantDigitalAssetsSet(DeleteVariantDigitalAssetsRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return deleteDigitalAssetsFromVariantDigitalAssetsSet(reqBody, productMasterId, jwt);
    }

    public static Response deleteDigitalAssetsFromVariantDigitalAssetsSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantDigitalAssetsRoutePath(productMasterId), jwt, "DELETE", requestBody, "");
    }

    public static Response replaceVariantAttributeSet(ProductInvariantAttributeSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return replaceVariantAttributeSet(reqBody, productMasterId, jwt);
    }

    public static Response replaceVariantAttributeSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductInvariantAttributesRoutePath(productMasterId), jwt, "POST", requestBody, "");
    }

    public static Response addAttributesToVariantAttributeSet(ProductInvariantAttributeSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return addAttributesToVariantAttributeSet(reqBody, productMasterId, jwt);
    }

    public static Response addAttributesToVariantAttributeSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductInvariantAttributesRoutePath(productMasterId), jwt, "PUT", requestBody, "");
    }

    public static Response getAttributeSetData(String productMasterId, String localeId, String type, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductInvariantAttributesRoutePath(productMasterId, localeId, type), jwt, "GET", "", "");
    }

    public static Response getAttributeSetDataByUniqueId(String uniqueId, String localeId, String type, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductInvariantAttributesByUniqueIdRoutePath(uniqueId, localeId, type), jwt, "GET", "", "");
    }

    public static Response deleteAttributesFromVariantAttributeSet(String productMasterId, String localeId, List<ProductInvariantAttribute> attributesToRemove, String jwt) throws Exception {
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("localeId", localeId);
        bodyData.put("attributes", attributesToRemove);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return deleteAttributesFromVariantAttributeSet(reqBody, productMasterId, jwt);
    }

    public static Response deleteAttributesFromVariantAttributeSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductInvariantAttributesRoutePath(productMasterId), jwt, "DELETE", requestBody, "");
    }

    public static Response replaceVariantKeywordSet(ProductVariantKeywordSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return replaceVariantKeywordSet(reqBody, productMasterId, jwt);
    }

    public static Response replaceVariantKeywordSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantKeywordsRoutePath(productMasterId), jwt, "POST", requestBody, "");
    }

    public static Response getKeywordSetData(String productMasterId, String localeId, String type, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantKeywordsRoutePath(productMasterId, localeId, type), jwt, "GET", "", "");
    }

    public static Response getKeywordSetDataByUniqueId(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        String reqURI = ProductVersioningRoutes.getProductVariantKeywordsByUniqueIdRoutePath(instancePath.productMasterId, instancePath.localeId, instancePath.type.getType());
        InstancePathBase pathBase = new InstancePathBase();
        pathBase.campaignId = instancePath.campaignId;
        pathBase.retailerId = instancePath.retailerId;
        return generateRestAPIAndCallGetEndpoint(reqURI, pathBase, chainItemId, jwt);
    }

    public static Response getProductKeywordSet(ProductVariantInstancePath instancePath, String chainItemId, String jwt) throws Exception {
        String reqURI = ProductVersioningRoutes.getProductVariantKeywordsRoutePath(instancePath.productMasterId) +
                "/locale/" + instancePath.localeId + "/type/" + instancePath.type.getType();

        return generateRestAPIAndCallGetEndpoint(reqURI, instancePath, chainItemId, jwt);
    }

    public static Response addKeywordsToVariantKeywordSet(ProductVariantKeywordSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return addKeywordsToVariantKeywordSet(reqBody, productMasterId, jwt);
    }

    public static Response addKeywordsToVariantKeywordSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantKeywordsRoutePath(productMasterId), jwt, "PUT", requestBody, "");
    }

    public static Response deleteKeywordsFromVariantKeywordSet(ProductVariantKeywordSetRequestBody requestBody, String productMasterId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return deleteKeywordsFromVariantKeywordSet(reqBody, productMasterId, jwt);
    }

    public static Response deleteKeywordsFromVariantKeywordSet(String requestBody, String productMasterId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantKeywordsRoutePath(productMasterId), jwt, "DELETE", requestBody, "");
    }

    public static Response exportProductProperties(ExportProductPropertyRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return callEndpoint(ProductVersioningRoutes.getProductPropertyExportRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response exportProductKeywords(List<ProductVariantInstancePath> requestBody, String type, String jwt) throws Exception {
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("products", requestBody);
        bodyData.put("type", type);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return exportProductKeywords(reqBody, jwt);
    }

    public static Response exportProductKeywords(String requestBody, String jwt) throws Exception {
        RestAPI restAPI = new RestAPI(
                ProductVersioningRoutes.getProductKeywordExportRoutePath(),
                jwt,
                Enums.APIRequestMethod.POST,
                requestBody,
                null,
                null
        );
        return callEndpoint(restAPI);
    }

    public static Response exportProductDigitalAssets(List<ProductVariantInstancePath> productsToExport, List<String> digitalAssetIds, String type, String jwt) throws Exception {
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("products", productsToExport);
        bodyData.put("digitalAssetIds", digitalAssetIds);
        bodyData.put("type", type);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(ProductVersioningRoutes.getProductDigitalAssetsExportRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response exportProductAttributes(List<ProductAttributesExportRequestBody> requestBody, String type, String jwt) throws Exception {
        Map<String, Object> bodyData = new HashMap<>();
        bodyData.put("products", requestBody);
        bodyData.put("type", type);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(ProductVersioningRoutes.getProductAttributeExportRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response createInstanceLive(InstancePathBase instancePath, String jwt) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("localeId", instancePath.localeId);
        map.put("retailerId", instancePath.retailerId);
        map.put("campaignId", instancePath.campaignId);

        var reqBody = new ObjectMapper().writeValueAsString(map);
        String uri = ProductVersioningRoutes.getVariantInstanceRoutePath(instancePath.productMasterId) + "/live";
        var response = callEndpoint(uri, jwt, "POST", reqBody, "");
        if (response.statusCode() == 200) {
            return response;
        } else {
            throw new Exception("Instance was not created. Response: " + response.getBody().asString());
        }
    }

    public static Response updateInstanceRPCAndBusinessUnits(InstancePathBase instancePath, Map.Entry<String, Boolean> rpc, Map.Entry<List<String>, Boolean> businessUnits, String jwt) throws Exception {
        Map<String, Object> map = new HashMap<>();

        if (rpc == null) {
            map.put("rpc", null);
        } else if (rpc.getValue()) {
            map.put("rpc", rpc.getKey());
        }

        if (businessUnits != null && businessUnits.getValue()) {
            map.put("businessUnits", businessUnits.getKey());
        }

        var reqBody = new ObjectMapper().writeValueAsString(map);
        return updateInstanceRPCAndBusinessUnits(instancePath, reqBody, jwt);
    }

    public static Response updateInstanceRPCAndBusinessUnits(InstancePathBase instancePath, String reqBody, String jwt) throws Exception {
        String uri = ProductVersioningRoutes.getVariantInstanceRoutePath(instancePath.productMasterId) +
                "/locale/" + instancePath.localeId +
                "/retailer/" + instancePath.retailerId;

        Map<String, Object> map = null;
        if (instancePath.campaignId != null) {
            map = new HashMap<>();
            map.put("campaignId", instancePath.campaignId);
        }

        RestAPI restAPI = new RestAPI(uri, jwt, Enums.APIRequestMethod.PATCH, reqBody, null, map);
        return callEndpoint(restAPI);
    }

    public static Response generateCarouselInMotion(JSONObject payload, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath() + "/generate-carousel", jwt, "POST", payload.toString(), "");
    }

    public static Response deleteProductVariantStaged(String chainItemIdToDelete, String productMasterId, String jwt) throws Exception {
        String reqBody = "{\"chainItemIdToDelete\"" + ": \"" + chainItemIdToDelete + "\"}";
        return callEndpoint(ProductVersioningRoutes.getProductVariantsRoutePath(productMasterId) + "/staged", jwt, "DELETE", reqBody, "");
    }

    public static Response clearOrCommitStagedData(CommitOrClearStagedDataRequestBody requestBody, String productMasterId, String jwt, String action) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        String uri = ProductVersioningRoutes.getProductMasterRoutePath(productMasterId);
        uri += action.equals("commit") ? "/commit-staged-data" : "/clear-staged-data";
        return callEndpoint(uri, jwt, "POST", reqBody, "");
    }

    public static Response bulkProductMasterInstances(JSONObject payload, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductMasterRoutePath() + "/bulk", jwt, "PATCH", payload.toString(), "");
    }

    private static Response generateRestAPIAndCallGetEndpoint(String reqURI, InstancePathBase instancePath, String chainItemId, String jwt) throws Exception {
        Map<String, Object> map = new HashMap<>();

        if (instancePath.campaignId != null) {
            map.put("campaignId", instancePath.campaignId);
        }

        if (instancePath.retailerId != null) {
            map.put("retailerId", instancePath.retailerId);
        }

        if (chainItemId != null) {
            map.put("chainItemId", chainItemId);
        }

        RestAPI restAPI = new RestAPI(reqURI, jwt, Enums.APIRequestMethod.GET, null, null, map);
        return callEndpoint(restAPI);
    }

}
