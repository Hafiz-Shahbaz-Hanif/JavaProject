package com.DC.apitests.adc.advertising.media;

import com.DC.objects.CommonBase;
import com.DC.objects.advertising.MediaBase;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.adc.advertising.media.ScratchpadRequests;
import com.DC.utilities.apiEngine.apiServices.adc.advertising.media.ScratchpadApiService;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ScratchpadRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.advertising.media.Scratchpad;
import com.DC.utilities.enums.Enums.Platform;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static com.DC.apitests.ApiValidations.checkResponseStatus;

public class ScratchpadServicesApiTest extends BaseClass {
    private static Logger logger;

    ScratchpadServicesApiTest() {
        logger = Logger.getLogger(ScratchpadServicesApiTest.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    private final String DAILY = "DAILY";

    @Test(dataProvider = "ReportingScratchpad_Slicer_Amazon", dataProviderClass = MediaApiDataProvider.class, description = "Media scratchpad API test for Amazon - Verifying slicer summary data")
    public void Scratchpad_Slicer_Amazon_Api_Test(String tcId, String headers) throws Exception {

        logger.info("** Media scratchpad test case ("+tcId + ") has started.");

        SoftAssert softAssert = new SoftAssert();

        String expectedFirstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
        String expectedYesterday = DateUtility.getYesterday();

        ScratchpadRequestBody requestBody = new ScratchpadRequestBody(DAILY, MediaBase.ScratchpadObjects.PROJECTION, MediaBase.ScratchpadObjects.METRIC, true,
                SharedMethods.createList("clickRevenue,relatedClickRevenue,brandClickRevenue"), expectedFirstDayOfLastSevenDays, expectedYesterday, CommonBase.BUSINESS_UNIT_ID,
                Platform.AMAZON.toString(), MediaBase.ScratchpadObjects.ATTRIBUTION, MediaBase.ScratchpadObjects.PERIOD_COMPARISON, MediaBase.ScratchpadObjects.SLICE_BY_TYPE);

        logger.info("Extracting response");
        Response response = ScratchpadRequests.scratchpadSlicer(requestBody, headers, SecurityAPI.getTokenAPI());

        logger.info("Verifying response");
        checkResponseStatus(tcId, HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        Scratchpad scratchpadResponse = ScratchpadApiService.getScratchpadResponse(response, tcId);

        logger.info("Verifying summary data");
        List<String> summaryDataList = scratchpadResponse.getSummaryData().getSummaryData();
        for (int i = 0; i < summaryDataList.size(); i++) {
            softAssert.assertNotNull(scratchpadResponse.summaryData.getSummaryData().get(i), "** Failure! Null value found in response body for Summary data");
        }

        logger.info("Verifying slicer data");
        for (int i = 0; i < scratchpadResponse.slicerData.size(); i++) {
            for (int j = 0; j < scratchpadResponse.slicerData.get(i).getSlicerData().size(); j++) {
                String slicerData = scratchpadResponse.slicerData.get(i).getSlicerData().get(j);
                softAssert.assertNotNull(slicerData, "** Failure! Null value found in response body for slicer data");
            }
        }

        softAssert.assertAll();
        logger.info("** Execution for test case (" + tcId + ") has completed successfully");
    }

    @Test(dataProvider = "ReportingScratchpad_Slicer_Yoy_Amazon", dataProviderClass = MediaApiDataProvider.class, description = "Media scratchpad slicer yoy API test for Amazon - Verifying slicer Yoy data")
    public void Scratchpad_Slicer_Yoy_Amazon_Api_Test(String tcId, String headers) throws Exception {

        logger.info("** Media scratchpad test case ("+tcId + ") has started.");

        SoftAssert softAssert = new SoftAssert();

        String expectedFirstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
        String expectedYesterday = DateUtility.getYesterday();

        ScratchpadRequestBody requestBody = new ScratchpadRequestBody(DAILY, MediaBase.ScratchpadObjects.PROJECTION, MediaBase.ScratchpadObjects.METRIC, true,
                SharedMethods.createList("clickRevenue,relatedClickRevenue,brandClickRevenue"), expectedFirstDayOfLastSevenDays, expectedYesterday, CommonBase.BUSINESS_UNIT_ID,
                Platform.AMAZON.toString(), MediaBase.ScratchpadObjects.ATTRIBUTION, MediaBase.ScratchpadObjects.PERIOD_COMPARISON, MediaBase.ScratchpadObjects.SLICE_BY_TYPE);

        logger.info("Extracting response");
        Response response = ScratchpadRequests.scratchpadSlicerYoy(requestBody, headers, SecurityAPI.getTokenAPI());

        logger.info("Verifying response");
        checkResponseStatus(tcId, HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        Scratchpad scratchpadResponse = ScratchpadApiService.getScratchpadResponse(response, tcId);

        logger.info("Verifying slicer yoy data");
        for (int i = 0; i < scratchpadResponse.data.size(); i++) {
            for (int j = 0; j < scratchpadResponse.data.get(i).getSlicerYoyData().size(); j++) {
                String slicerYoyData = scratchpadResponse.data.get(i).getSlicerYoyData().get(j);
                softAssert.assertNotNull(slicerYoyData, "** Failure! Null value found in response body for slicer yoy data");
            }
        }

        softAssert.assertAll();
        logger.info("** Execution for test case (" + tcId + ") has completed successfully");
    }

    @Test(dataProvider = "ReportingScratchpad_Slicer_Yoy_Summary_Amazon", dataProviderClass = MediaApiDataProvider.class, description = "Media scratchpad slicer yoy API test for Amazon - Verifying slicer yoy summary data")
    public void Scratchpad_Slicer_Yoy_Summary_Amazon_Api_Test(String tcId, String headers) throws Exception {

        logger.info("** Media scratchpad test case ("+tcId + ") has started.");

        SoftAssert softAssert = new SoftAssert();

        String expectedFirstDayOfLastSevenDays = DateUtility.getFirstDayOfLastFourteenDays();
        String expectedYesterday = DateUtility.getFirstDayOfLastSevenDays();

        ScratchpadRequestBody requestBody = new ScratchpadRequestBody(DAILY, MediaBase.ScratchpadObjects.PROJECTION, MediaBase.ScratchpadObjects.METRIC, true,
                SharedMethods.createList("clickRevenue,relatedClickRevenue,brandClickRevenue"), expectedFirstDayOfLastSevenDays, expectedYesterday, CommonBase.BUSINESS_UNIT_ID,
                Platform.AMAZON.toString(), MediaBase.ScratchpadObjects.ATTRIBUTION, MediaBase.ScratchpadObjects.PERIOD_COMPARISON, MediaBase.ScratchpadObjects.SLICE_BY_TYPE);

        logger.info("Extracting response");
        Response response = ScratchpadRequests.scratchpadSlicerSummary(requestBody, headers, SecurityAPI.getTokenAPI());

        logger.info("Verifying response");
        checkResponseStatus(tcId, HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        List<Scratchpad> scratchpadResponse = ScratchpadApiService.getScratchpadResponseSummary(response, tcId);

        logger.info("Verifying slicer summary data");
        for(int i = 0; i < scratchpadResponse.size(); i++) {
            List<String> summaryDataList = scratchpadResponse.get(i).summary.getSlicerSummary();
            for (int j = 1; j < summaryDataList.size(); j++) {
                softAssert.assertNotNull(scratchpadResponse.get(i).summary.getSlicerSummary().get(j), "** Failure! Null value found in response body for Slicer Summary data");
            }
        }

        logger.info("Verifying slicer previous summary data");
        for (int i = 0; i < scratchpadResponse.size(); i++) {
            List<String> summaryDataList = scratchpadResponse.get(i).previousSummary.getSlicerPreviousSummary();
            for (int j = 0; j < summaryDataList.size(); j++) {
                softAssert.assertNotNull(scratchpadResponse.get(i).previousSummary.getSlicerPreviousSummary(), "** Failure! Null value found in response body for Slicer Previous Summary data");
            }
        }

        softAssert.assertAll();
        logger.info("** Execution for test case (" + tcId + ") has completed successfully");
    }
}
