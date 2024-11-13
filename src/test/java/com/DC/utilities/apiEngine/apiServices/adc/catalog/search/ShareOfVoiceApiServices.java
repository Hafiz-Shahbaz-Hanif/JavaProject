package com.DC.utilities.apiEngine.apiServices.adc.catalog.search;

import com.DC.objects.CommonBase;
import com.DC.objects.catalog.SearchBase;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.search.SearchOfVoiceRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.search.ShareOfVoiceResponseBody;
import com.DC.utilities.enums.Enums;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.lang.reflect.Type;
import java.util.List;

public class ShareOfVoiceApiServices {

    private static final Logger LOGGER = Logger.getLogger(ShareOfVoiceApiServices.class);

    public static ShareOfVoiceResponseBody getShareOfVoiceBrandResponse(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        ShareOfVoiceResponseBody shareOfVoiceBrandResponseModel = null;
        try {
            shareOfVoiceBrandResponseModel = gson.fromJson(response.getBody().asString(), ShareOfVoiceResponseBody.class);
        } catch (Exception e) {
            LOGGER.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return shareOfVoiceBrandResponseModel;
    }

    public static SearchOfVoiceRequestBody shareOfVoiceBrandRequestBody(SearchOfVoiceRequestBody.DateRange dateRangeLabel, String firstDay, String lastDay, String reportType, List<String> placementList, List<String> keywordsList) {
        return new SearchOfVoiceRequestBody(2, true, dateRangeLabel,
                firstDay, lastDay, CommonBase.BUSINESS_UNIT_ID, SearchBase.ShareOfVoiceObjects.COUNTRY_CODE,
                reportType, CommonBase.AMAZON_PLATFORM, "1", placementList, keywordsList,
                true, SearchBase.ShareOfVoiceObjects.AMAZON_RETAILER_PLATFORM, Enums.Platform.AMAZON.toString(), "1");
    }

    public static List<ShareOfVoiceResponseBody> getShareOfVoiceBrandForPlatformResponse(Response response, String testCaseID) {
        Gson gson = new GsonBuilder().create();

        List shareOfVoiceBrandResponseModel = null;
        Type collectionType = new TypeToken<List<ShareOfVoiceResponseBody>>(){}.getType();

        try {
            shareOfVoiceBrandResponseModel = gson.fromJson(response.getBody().asString(), collectionType);
        } catch (Exception e) {
            LOGGER.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return (List<ShareOfVoiceResponseBody>) shareOfVoiceBrandResponseModel;
    }
}
