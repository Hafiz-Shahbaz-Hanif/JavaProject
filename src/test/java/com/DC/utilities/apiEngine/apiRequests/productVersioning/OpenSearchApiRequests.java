package com.DC.utilities.apiEngine.apiRequests.productVersioning;

import com.DC.utilities.apiEngine.routes.productVersioning.OpenSearchRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class OpenSearchApiRequests {
    public static Response getInstanceWithPropertyIndex(String jwt, String instanceId, List<String> indexes) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("includeIndexes", indexes);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(OpenSearchRoutes.getSearchInstanceWithIndexRoutePath(instanceId), jwt, "POST", reqBody, "");
    }

    public static Response search(JSONObject payload, String jwt) throws Exception {
        return callEndpoint(OpenSearchRoutes.OPEN_SEARCH_HOST, jwt, "POST", payload.toString(), "");
    }
}
