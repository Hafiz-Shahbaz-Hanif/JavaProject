package com.DC.utilities.apiEngine.apiServices.adc.catalog.retail;

import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.GainersAndDrainersIntervalResponseBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.GainersAndDrainersResponseBody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.Type;
import java.util.List;

public class GainersAndDrainersApiService {
    private static final Logger logger = Logger.getLogger(RetailScratchpadApiService.class);

    public static List<GainersAndDrainersIntervalResponseBody> getRetailGainerAndDrainersResponseInterval(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        List GainerAndDrainersResponseModel = null;
        Type collectionType = new TypeToken<List<GainersAndDrainersIntervalResponseBody>>(){}.getType();

        try {
            GainerAndDrainersResponseModel = gson.fromJson(response.getBody().asString(), collectionType);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return (List<GainersAndDrainersIntervalResponseBody>) GainerAndDrainersResponseModel;
    }

    public static List<GainersAndDrainersResponseBody> getRetailGainerAndDrainersResponse(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        List GainerAndDrainersResponseModel = null;
        Type collectionType = new TypeToken<List<GainersAndDrainersResponseBody>>(){}.getType();

        try {
            GainerAndDrainersResponseModel = gson.fromJson(response.getBody().asString(), collectionType);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return (List<GainersAndDrainersResponseBody>) GainerAndDrainersResponseModel;
    }
}
