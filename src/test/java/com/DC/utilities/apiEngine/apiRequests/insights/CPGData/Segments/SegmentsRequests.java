package com.DC.utilities.apiEngine.apiRequests.insights.CPGData.Segments;

import com.DC.utilities.apiEngine.routes.insights.CPGData.Segments.SegmentsRoutes;
import io.restassured.response.Response;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class SegmentsRequests {
    public static Response getCategorySegmentValuesWithCategory(String categoryId, String jwt) throws Exception {
        return callEndpoint(SegmentsRoutes.getCategoryRoute(categoryId) + "/segmentvalueswithcategory", jwt, "GET", "", "");
    }
}
