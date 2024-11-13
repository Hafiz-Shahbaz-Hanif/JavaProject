package com.DC.utilities.apiEngine.apiServices.adc.catalog.retail;

import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.RoundUpProductResponseBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.RoundUpResponseBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.RoundUpSegmentationResponseBody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;

public class RoundUpApiService {
    private static final Logger logger = Logger.getLogger(RoundUpApiService.class);

    public static RoundUpResponseBody getRoundUpAllResponse(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        RoundUpResponseBody roundupResponseModel = null;
        try {
            roundupResponseModel = gson.fromJson(response.getBody().asString(), RoundUpResponseBody.class);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return roundupResponseModel;
    }

    public static RoundUpProductResponseBody getRoundUpProductResponse(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        RoundUpProductResponseBody roundupProductResponseModel = null;
        try {
            roundupProductResponseModel = gson.fromJson(response.getBody().asString(), RoundUpProductResponseBody.class);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return roundupProductResponseModel;
    }

    public static RoundUpSegmentationResponseBody getRoundUpSegmentationResponse(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        RoundUpSegmentationResponseBody roundupSegmentationResponseModel = null;
        try {
            roundupSegmentationResponseModel = gson.fromJson(response.getBody().asString(), RoundUpSegmentationResponseBody.class);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return roundupSegmentationResponseModel;
    }
}
