package com.DC.utilities.apiEngine.apiRequests.adc.analyze.paidMediaReporting;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.advertising.media.MediaReportsRequestBody;
import com.DC.utilities.apiEngine.routes.advertising.MediaRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class MediaReportsRequests {

    public static Map<String, String> multipleHeaders(String jwt) {
        return multipleHeaders("920", jwt);
    }

    public static Map<String, String> multipleHeaders(String buId, String jwt) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Authorization", jwt);
        headerMap.put("x-businessunitcontext", buId);
        headerMap.put("x-currencycontext", "USD");

        return headerMap;
    }

    public static Response mediaReportsMetricOverview(MediaReportsRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);

        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaReportsMetricOverviewRoutePath(),
                "POST", reqBody, multipleHeaders(jwt), "");
    }

    public static Response mediaReportsMMGraphActualData(MediaReportsRequestBody requestBody, String buId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);

        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaReportsMultiMetricRoutePath(),
                "POST", reqBody, multipleHeaders(buId, jwt), "");
    }

    public static Response mediaReportsSummaryCampaignType(MediaReportsRequestBody requestBody, String buId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaReportsSummaryCampaignTypeRoutePath(), "POST",
                reqBody, multipleHeaders(buId, jwt), "");
    }

    public static Response mediaReportsSummaryDate(MediaReportsRequestBody requestBody, String buId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaReportsSummaryDateRoutePath(), "POST",
                reqBody, multipleHeaders(buId, jwt), "");
    }

    public static Response mediaReportsSummarySegmentation(MediaReportsRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaReportsSummarySegmentationRoutePath(), "POST", reqBody, multipleHeaders(jwt), "");
    }

    public static Response mediaReportsYoy(MediaReportsRequestBody requestBody, String buId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaReportsYoyRoutePath(), "POST",
                reqBody, multipleHeaders(buId, jwt), "");
    }

    public static Response mediaReportsSlicerCampaignType(MediaReportsRequestBody requestBody, String buId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaReportsSlicerCampaignTypeRoutePath(), "POST", reqBody, multipleHeaders(buId, jwt), "");
    }

    public static Response mediaReportsSlicerSlicerSegmentation(MediaReportsRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(MediaRoutes.getMediaReportsSlicerSegmentationRoutePath(), "POST", reqBody, multipleHeaders(jwt), "");
    }
}