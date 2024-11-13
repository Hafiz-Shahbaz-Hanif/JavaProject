package com.DC.utilities.apiEngine.apiRequests.productVersioning;

import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateProductListRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.InstancePathBase;
import com.DC.utilities.apiEngine.routes.productVersioning.ProductListRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class ProductListApiRequests {

    public static Response getProductList(String listId, String jwt) throws Exception {
        return callEndpoint(ProductListRoutes.getProductListRoutePath(listId), jwt, "GET", "", "");
    }

    public static Response getAllCompanyProductLists(String jwt) throws Exception {
        return callEndpoint(ProductListRoutes.getProductListHost(), jwt, "GET", "", "");
    }

    public static Response createProductList(CreateProductListRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return createProductList(reqBody, jwt);
    }

    public static Response createProductList(String requestBody, String jwt) throws Exception {
        return callEndpoint(ProductListRoutes.getProductListHost(), jwt, "POST", requestBody, "");
    }

    public static Response updateProductList(CreateProductListRequestBody requestBody, String listId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return callEndpoint(ProductListRoutes.getProductListRoutePath(listId), jwt, "PATCH", reqBody, "");
    }

    public static Response deleteProductList(String listId, String jwt) throws Exception {
        return callEndpoint(ProductListRoutes.getProductListRoutePath(listId), jwt, "DELETE", "", "");
    }

    public static Response removeProductsFromList(List<String> instanceIds, String listId, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("instanceIds", instanceIds);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(ProductListRoutes.getRemoveProductsFromListRoutePath(listId), jwt, "PATCH", reqBody, "");
    }

    public static Response addProductInstancesToList(List<InstancePathBase> productsToAdd, String listId, String jwt) throws Exception {
        Map<String, List<InstancePathBase>> bodyData = new HashMap<>();
        bodyData.put("products", productsToAdd);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return addProductInstancesToList(reqBody, listId, jwt);
    }

    public static Response addProductInstancesToList(String requestBody, String listId, String jwt) throws Exception {
        return callEndpoint(ProductListRoutes.getAddProductsFromListRoutePath(listId), jwt, "PATCH", requestBody, "");
    }
}
