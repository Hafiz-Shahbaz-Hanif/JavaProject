package com.DC.utilities.apiEngine.apiRequests.adc.advertising.media;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ScratchpadRequestBody;
import com.DC.utilities.apiEngine.routes.adc.advertising.media.MediaRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.Logger;

public class ScratchpadRequests {

    private static Logger logger = Logger.getLogger(ScratchpadRequests.class);
    public static RequestSpecification getBaseRequestSpec() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                .setContentType("application/json");
        return requestSpecBuilder.build();
    }

    public static Response scratchpadSlicer(ScratchpadRequestBody requestBody, String headers, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getScratchpadSlicerRoutePath(), "POST", reqBody, headers, "", jwt);
    }

    public static Response scratchpadSlicerYoy(ScratchpadRequestBody requestBody, String headers, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getScratchpadSlicerYoyRoutePath(), "POST", reqBody, headers, "", jwt);
    }

    public static Response scratchpadSlicerSummary(ScratchpadRequestBody requestBody, String headers, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getScratchpadSlicerSummaryRoutePath(), "POST", reqBody, headers, "", jwt);
    }

    public static Response scratchpadSlicerYoyAggbus(ScratchpadRequestBody requestBody, String headers, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getScratchpadSlicerYoyAggbus(), "POST", reqBody, headers, "", jwt);
    }
}
