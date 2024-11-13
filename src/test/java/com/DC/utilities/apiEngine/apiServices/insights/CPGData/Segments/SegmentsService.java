package com.DC.utilities.apiEngine.apiServices.insights.CPGData.Segments;

import com.DC.utilities.apiEngine.apiRequests.insights.CPGData.Segments.SegmentsRequests;
import com.DC.utilities.apiEngine.models.responses.insights.CPGData.Segments.SegmentValuesWithCategory;
import io.restassured.response.Response;

public class SegmentsService {
    public static SegmentValuesWithCategory getCategorySegmentValuesWithCategory(String categoryId, String jwt) throws Exception {
        Response response = SegmentsRequests.getCategorySegmentValuesWithCategory(categoryId, jwt);
        return response.getBody().as(SegmentValuesWithCategory.class);
    }
}
