package com.DC.utilities.apiEngine.apiRequests.insights;

import com.DC.utilities.apiEngine.apiRequests.productVersioning.SharedRequests;
import com.DC.utilities.apiEngine.models.responses.insights.TaskUIConfigBase;
import com.DC.utilities.apiEngine.routes.insights.ProductRepoRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class ProductRepoRequests extends SharedRequests {

    public static Response getTaskUIMappingConfig(String mappingId, String jwt) throws Exception {
        return callEndpoint(ProductRepoRoutes.getTaskUIMappingConfigRoutePath(mappingId), jwt, "GET", "", "");
    }

    public static Response updateTaskUIConfig(String mappingId, TaskUIConfigBase requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return callEndpoint(ProductRepoRoutes.getTaskUIMappingConfigRoutePath(mappingId), jwt, "PUT", reqBody, "");
    }

    public static Response getProductDetails(String productId, String jwt) throws Exception {
        return callEndpoint(ProductRepoRoutes.getProductDetails(productId), jwt, "GET", "", "");
    }
}
