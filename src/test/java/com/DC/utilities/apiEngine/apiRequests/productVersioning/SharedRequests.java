package com.DC.utilities.apiEngine.apiRequests.productVersioning;

import com.DC.utilities.apiEngine.routes.productVersioning.ProductVersioningRoutes;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.json.JSONObject;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public abstract class SharedRequests {

    public static Response importFileProductVariantRepo(String url, Enums.ImportType importType, String jwt) throws Exception {
        return importFileProductVariantRepo(url, importType.getImportType(), jwt);
    }

    public static Response importFileProductVariantRepo(String url, String importType, String jwt) throws Exception {
        String reqBody = "{\"url\":\"" + url + "\"}";
        return callEndpoint(ProductVersioningRoutes.getProductVariantImportRoutePathDebug(importType), jwt, "POST", reqBody, "");
    }

    public static Response importProductKeywords(String url, Enums.KeywordFlag keywordFlag, String jwt) throws Exception {
        String reqBody;
        if (keywordFlag != null) {
            reqBody = "{\n" +
                    "    \"url\":\"" + url + "\",\n" +
                    "    \"keywordFlag\": \"" + keywordFlag.toString().toLowerCase() + "\"\n" +
                    "}";
        } else {
            reqBody = "{\"url\":\"" + url + "\"}";
        }
        return callEndpoint(ProductVersioningRoutes.getProductVariantImportRoutePathDebug(Enums.ImportType.KEYWORD.getImportType()), jwt, "POST", reqBody, "");
    }

    public static Response getExportCore(String exportId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantExportCoreRoutePath(exportId), jwt, "GET", "", "");
    }

    public static Response getProductPropertiesImportTemplate(String jwt) throws Exception {
        String url = ProductVersioningRoutes.getProductVariantImportHost() + "/product-properties-template";
        return callEndpoint(url, jwt, "GET", "", "");
    }

    public static Response getProductKeywordsImportTemplate(String jwt) throws Exception {
        String url = ProductVersioningRoutes.getProductVariantImportHost() + "/product-keywords-template";
        return callEndpoint(url, jwt, "GET", "", "");
    }

    public static Response getErrorReportForImport(String importId, String jwt) throws Exception {
        String url = ProductVersioningRoutes.getProductVariantImportHost() + "/error-workbook/" + importId;
        return callEndpoint(url, jwt, "GET", "", "");
    }

    public static Response getExportTrackingRecord(String exportId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantExportCoreRoutePath(exportId), jwt, "GET", "", "");
    }

    public static Response getImportTrackingRecord(String importId, String jwt) throws Exception {
        return callEndpoint(ProductVersioningRoutes.getProductVariantImportRoutePath(importId), jwt, "GET", "", "");
    }

    public static Response getProductImportsForCompany(JSONObject payload, String jwt) throws Exception {
        String url = ProductVersioningRoutes.getProductVariantImportHost();
        return callEndpoint(url, jwt, "POST", payload.toString(), "");
    }
}
