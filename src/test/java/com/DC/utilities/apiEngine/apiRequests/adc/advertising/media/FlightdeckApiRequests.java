package com.DC.utilities.apiEngine.apiRequests.adc.advertising.media;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.routes.adc.advertising.media.MediaRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

public class FlightdeckApiRequests {

    public static <T> Response getCampaignData(T requestBody, String platform, String endpoint, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return getCampaignData(reqBody, platform, endpoint, jwt);
    }

    public static Response getCampaignData(String requestBody, String platform, String endpoint, String jwt) throws Exception {

        String header = "Content-Type=application/json";

        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaFlightDeckRoutePath(platform.toLowerCase(), endpoint),
                "POST", requestBody, header, "", jwt);

    }

    public static <T> Response getCampaignDataWithNewHeader(T requestBody, String platform, String endpoint, String jwt, String... headers) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        String header = "Content-Type=application/json";
        if (headers != null && headers.length > 0) {
            header += ";" + String.join(",", headers);
        }
        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaFlightDeckRoutePath(platform.toLowerCase(), endpoint),
                "POST", reqBody, header, "", jwt);
    }
}
