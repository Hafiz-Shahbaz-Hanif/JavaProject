package com.DC.utilities.apiEngine.apiRequests.advertising.media;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.advertising.media.ScratchpadRequestBody;
import com.DC.utilities.apiEngine.routes.advertising.MediaRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import com.DC.utilities.apiEngine.headers;

import java.util.HashMap;
import java.util.Map;

public class ScratchpadRequests {

    private static Logger logger = Logger.getLogger(ScratchpadRequests.class);
    private static String header = "Content-Type=application/json";
    public static String getBaseRequestSpec() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setContentType("application/json");
        return requestSpecBuilder.build().toString();
    }

    public static Response scratchpadSlicer(ScratchpadRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getScratchpadSlicerRoutePath(), "POST", reqBody, header , "", jwt);
    }

    public static Response scratchpadSlicerYoy(ScratchpadRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getScratchpadSlicerYoyRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static Response scratchpadSlicerSummary(ScratchpadRequestBody requestBody, String headers, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getScratchpadSlicerSummaryRoutePath(), "POST", reqBody, headers, "", jwt);
    }

    public static Response scratchpadSlicerYoyAggBUs(ScratchpadRequestBody requestBody, String bearerToken) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getScratchpadSlicerYoyAggbus(), "POST", reqBody, headers.multipleHeadersForAggregatedBUs(bearerToken), "");
    }
}
