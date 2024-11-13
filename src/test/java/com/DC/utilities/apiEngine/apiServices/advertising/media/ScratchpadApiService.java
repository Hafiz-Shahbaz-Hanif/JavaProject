package com.DC.utilities.apiEngine.apiServices.advertising.media;

import com.DC.objects.CommonBase;
import com.DC.objects.advertising.MediaBase;
import com.DC.utilities.apiEngine.models.requests.advertising.media.ScratchpadRequestBody;
import com.DC.utilities.apiEngine.models.responses.advertising.media.Scratchpad;
import com.DC.utilities.enums.Enums;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public class ScratchpadApiService {
    private static final Logger logger = Logger.getLogger(ScratchpadApiService.class);

    public static Scratchpad getScratchpadResponse(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        Scratchpad scratchpadResponseModel = null;
        try {
            scratchpadResponseModel = gson.fromJson(response.getBody().asString(), Scratchpad.class);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return scratchpadResponseModel;
    }

    public static List<Scratchpad> getScratchpadResponseSummary(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        List scratchpadResponseModel = null;
        Type collectionType = new TypeToken<List<Scratchpad>>(){}.getType();

        try {
            scratchpadResponseModel = gson.fromJson(response.getBody().asString(), collectionType);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return (List<Scratchpad>) scratchpadResponseModel;
    }

    public static ScratchpadRequestBody scratchpadRequestBody(String interval, List<String> saleMetrics, String firstDay, String lastDay) {
        return new ScratchpadRequestBody(interval, MediaBase.ScratchpadObjects.PROJECTION, MediaBase.ScratchpadObjects.METRIC, true,
                saleMetrics, firstDay, lastDay, CommonBase.BUSINESS_UNIT_ID,
                Enums.Platform.AMAZON.toString(), MediaBase.ScratchpadObjects.ATTRIBUTION,
                MediaBase.ScratchpadObjects.PERIOD_COMPARISON, MediaBase.ScratchpadObjects.SLICE_BY_TYPE);
    }
}
