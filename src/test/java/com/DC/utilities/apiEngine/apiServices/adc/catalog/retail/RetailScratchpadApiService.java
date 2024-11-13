package com.DC.utilities.apiEngine.apiServices.adc.catalog.retail;

import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.SummaryResponseBody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.Type;
import java.util.List;

public class RetailScratchpadApiService {
    private static final Logger logger = Logger.getLogger(RetailScratchpadApiService.class);

    public static SummaryResponseBody getScratchpadResponse(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        SummaryResponseBody scratchpadResponseModel = null;
        try {
            scratchpadResponseModel = gson.fromJson(response.getBody().asString(), SummaryResponseBody.class);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return scratchpadResponseModel;
    }

    public static List<SummaryResponseBody> getRetailScratchpadResponseSummary(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        List scratchpadResponseModel = null;
        Type collectionType = new TypeToken<List<SummaryResponseBody>>(){}.getType();

        try {
            scratchpadResponseModel = gson.fromJson(response.getBody().asString(), collectionType);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return (List<SummaryResponseBody>) scratchpadResponseModel;
    }
}
