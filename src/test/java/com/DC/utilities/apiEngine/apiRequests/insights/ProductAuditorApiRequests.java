package com.DC.utilities.apiEngine.apiRequests.insights;
import com.DC.utilities.apiEngine.models.requests.insights.CreateProductAuditorExportRequestBody;
import com.DC.utilities.apiEngine.models.requests.insights.CreateProductAuditorRequestBody;
import com.DC.utilities.apiEngine.routes.insights.ProductAuditorRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class ProductAuditorApiRequests {

    public static Response performSOTAudit(CreateProductAuditorRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return callEndpoint(ProductAuditorRoutes.getProductAuditorHost(), jwt, "POST", reqBody, "");
    }

    public static Response performSOTAuditExport(CreateProductAuditorExportRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return callEndpoint(ProductAuditorRoutes.getProductAuditorExportHost(), jwt, "POST", reqBody, "");
    }
}
