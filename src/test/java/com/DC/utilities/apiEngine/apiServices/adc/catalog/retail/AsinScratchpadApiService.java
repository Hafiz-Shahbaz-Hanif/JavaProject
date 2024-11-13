package com.DC.utilities.apiEngine.apiServices.adc.catalog.retail;

import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.AsinScratchpadRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.AsinScratchpadResponseBody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.Type;
import java.util.List;

public class AsinScratchpadApiService {
    private static final Logger logger = Logger.getLogger(AsinScratchpadApiService.class);

    public static List<AsinScratchpadResponseBody> getAsinScratchpadResponse(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        List scratchpadResponseModel = null;
        Type collectionType = new TypeToken<List<AsinScratchpadResponseBody>>(){}.getType();

        try {
            scratchpadResponseModel = gson.fromJson(response.getBody().asString(), collectionType);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return (List<AsinScratchpadResponseBody>) scratchpadResponseModel;
    }

    public static AsinScratchpadRequestBody asinScratchpadRequestBody(String interval, String metric, AsinScratchpadRequestBody.DateRange dateRangeLabel, String firstDay, String lastDay, int businessUnitId, String clientCategory, String clientAccountType, List<String> segmentationFilters,
                                                                        int obsoleteAsinTypeId, String retailerPlatform, String distributionView, List<String> asinIds) {
        return new AsinScratchpadRequestBody(interval, true, metric, dateRangeLabel, firstDay, lastDay,
                true, businessUnitId, clientCategory, clientAccountType, segmentationFilters,
                obsoleteAsinTypeId, retailerPlatform, distributionView, asinIds);
    }
}
