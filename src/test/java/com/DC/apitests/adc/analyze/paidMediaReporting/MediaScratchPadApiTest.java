package com.DC.apitests.adc.analyze.paidMediaReporting;

import com.DC.apitests.ApiValidations;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.analyze.paidMediaReporting.MediaReportsRequests;
import com.DC.utilities.apiEngine.apiServices.adc.analyze.paidMediaReporting.MediaReportsApiService;
import com.DC.utilities.apiEngine.models.responses.adc.analyze.paidMediaReporting.MediaReportsData;
import com.DC.utilities.apiEngine.models.responses.adc.analyze.paidMediaReporting.MediaReportsResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.DC.apitests.ApiValidations.checkResponseStatus;

public class MediaScratchPadApiTest extends BaseClass {
    private static Logger logger;
    private static final String SPOT_CAMPAIGN_TYPE = "Sponsored TV";
    private static final String SPOT_BU_ID = "920";
    private static final String NON_SPOT_BU_ID = "198";
    private static final List<String> SPOT_METRICS = List.of("DPV", "NTB DPV", "VCR", "Branded Searches");
    private static final List<String> OVERVIEW_METRICS = List.of("Spend", "Sales", "ROAS", "Impressions", "Clicks", "CTR", "CVR", "CPC", "SPC", "CPA");
    private static final List<String> OTHER_CAMPAIGNS = List.of("Sponsored Brands", "Sponsored Display", "Sponsored Products", "Sponsored Video");

    MediaScratchPadApiTest() {
        logger = Logger.getLogger(MediaScratchPadApiTest.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    private String token;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, true);
        driver.get(READ_CONFIG.getDcAppUrl());
        new DCLoginPage(driver).login(READ_CONFIG.getUsername(), READ_CONFIG.getPassword());
        token = "Bearer " + SecurityAPI.getAuthToken(driver);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Metric Overview with Actual data ")
    public void MetricOverviewWithActualData_Amazon_Api_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        logger.info("Extracting response");
        Response response = MediaReportsRequests.mediaReportsMetricOverview(
                MediaReportsApiService.metricOverviewWithActualDataRequestBody(SPOT_CAMPAIGN_TYPE), token);

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);

        List<MediaReportsData> data = mediaReportsDataResponse.getData();
        softAssert.assertFalse(data.isEmpty(), "** No data found in response body");
        for (MediaReportsData mediaReportsData : data) {
            softAssert.assertNotNull(mediaReportsData.getMetric(), "** Null value found in response body for Metrics");
            softAssert.assertNotNull(mediaReportsData.getCurrentValue(), "**  Null value found in response body for Current Value");
        }

        var retrievedMetrics = data.stream().map(MediaReportsData::getMetric).collect(Collectors.toSet());
        softAssert.assertTrue(retrievedMetrics.containsAll(OVERVIEW_METRICS), "** Didn't find SPOT Metrics in response body for SPOT Metrics");

        var missingMetrics = new HashSet<>(OVERVIEW_METRICS);
        missingMetrics.removeAll(retrievedMetrics);
        softAssert.assertTrue(missingMetrics.isEmpty(), "** Didn't find SPOT Metrics in response body for SPOT Metrics: " + missingMetrics);

        var hasNoElementsInCommon = Collections.disjoint(retrievedMetrics, SPOT_METRICS);
        softAssert.assertTrue(hasNoElementsInCommon, "** No elements in common between the two sets");

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying MMGraph with Actual Data for Sponosred Tv")
    public void MMGraphWithActualData_Amazon_Api_Test_SPOT() throws Exception {

        logger.info("Extracting response");

        Response response = MediaReportsRequests.mediaReportsMMGraphActualData(
                MediaReportsApiService.mMGraphWithActualDataRequestBody(SPOT_CAMPAIGN_TYPE), SPOT_BU_ID, token);

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
        validateSPOTData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying MMGraph with Actual Data for Other Campaigns")
    public void MMGraphWithActualData_Amazon_Api_Test_Other_Campaigns() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        for (String otherCampaign : OTHER_CAMPAIGNS) {
            logger.info("Extracting response");
            Response response = MediaReportsRequests.mediaReportsMMGraphActualData(
                    MediaReportsApiService.mMGraphWithActualDataRequestBody(otherCampaign), SPOT_BU_ID, token);

            logger.info("** Deserializing the response");
            MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);

            validateOtherCampaignsData(mediaReportsDataResponse, softAssert);
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying MMGraph with Actual Data for Inaplicable BU")
    public void MMGraphWithActualData_Amazon_Api_Test_Inapplicable_BU() throws Exception {
        logger.info("Extracting response");
        Response response = MediaReportsRequests.mediaReportsMMGraphActualData(
                MediaReportsApiService.mMGraphWithActualDataRequestBody(SPOT_CAMPAIGN_TYPE), NON_SPOT_BU_ID, token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
        validateInapplicableBUData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Summary table slice by Campaign Type for SPOT")
    public void SummaryTableSliceByCampaignType_Amazon_Api_Test_SPOT() throws Exception {
        logger.info("Extracting response");
        Response response = MediaReportsRequests.mediaReportsSummaryCampaignType(
                MediaReportsApiService.summaryTableSliceByCampaignTypeRequestBody(SPOT_CAMPAIGN_TYPE), SPOT_BU_ID, token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);

        validateSPOTData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Summary table slice by Campaign Type for Other Campaigns")
    public void SummaryTableSliceByCampaignType_Amazon_Api_Test_Other_Campaigns() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        for (String otherCampaign : OTHER_CAMPAIGNS) {
            logger.info("Extracting response");
            Response response = MediaReportsRequests.mediaReportsSummaryCampaignType(
                    MediaReportsApiService.summaryTableSliceByCampaignTypeRequestBody(otherCampaign), SPOT_BU_ID, token);

            logger.info("** Deserializing the response");
            MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
            validateOtherCampaignsData(mediaReportsDataResponse, softAssert);
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying MMGraph with Actual Data for Inapplicable BU")
    public void SummaryTableSliceByCampaignType_Amazon_Api_Test_Inapplicable_BU() throws Exception {
        logger.info("Extracting response");

        Response response = MediaReportsRequests.mediaReportsSummaryCampaignType(
                MediaReportsApiService.summaryTableSliceByCampaignTypeRequestBody(SPOT_CAMPAIGN_TYPE), NON_SPOT_BU_ID, token);
        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
        validateInapplicableBUData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Summary table slice by Date for SPOT")
    public void SummaryTableSliceByDate_Amazon_Api_Test_SPOT() throws Exception {
        logger.info("Extracting response");
        Response response = MediaReportsRequests.mediaReportsSummaryDate(
                MediaReportsApiService.summaryTableSliceByDateRequestBody(SPOT_CAMPAIGN_TYPE), SPOT_BU_ID, token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);

        validateSPOTData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Summary table slice by Campaign Type for Other Campaigns")
    public void SummaryTableSliceByDate_Amazon_Api_Test_Other_Campaigns() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        for (String otherCampaign : OTHER_CAMPAIGNS) {
            logger.info("Extracting response");
            Response response = MediaReportsRequests.mediaReportsSummaryDate(
                    MediaReportsApiService.summaryTableSliceByDateRequestBody(otherCampaign), SPOT_BU_ID, token);

            logger.info("** Deserializing the response");
            MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
            validateOtherCampaignsData(mediaReportsDataResponse, softAssert);
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying MMGraph with Actual Data for Inapplicable BU")
    public void SummaryTableSliceByDate_Amazon_Api_Test_Inapplicable_BU() throws Exception {
        logger.info("Extracting response");

        Response response = MediaReportsRequests.mediaReportsSummaryDate(
                MediaReportsApiService.summaryTableSliceByDateRequestBody(SPOT_CAMPAIGN_TYPE), NON_SPOT_BU_ID, token);
        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
        validateInapplicableBUData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Summary table slice by Segmentation")
    public void SummaryTableSliceBySegmentation_Amazon_Api_Test() throws Exception {
        logger.info("Extracting response");
        Response response = MediaReportsRequests.mediaReportsSummarySegmentation(
                MediaReportsApiService.summaryTableSliceBySegmentationRequestBody(null), token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);

        validateSPOTData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying YOY for SPOT")
    public void YOY_Amazon_Api_Test_SPOT() throws Exception {
        logger.info("Extracting response");
        Response response = MediaReportsRequests.mediaReportsYoy(
                MediaReportsApiService.yoyRequestBody(SPOT_CAMPAIGN_TYPE), SPOT_BU_ID, token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
        validateSPOTData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Summary table slice by Campaign Type for Other Campaigns")
    public void YOY_Amazon_Api_Test_Other_Campaigns() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        for (String otherCampaign : OTHER_CAMPAIGNS) {
            logger.info("Extracting response");

            Response response = MediaReportsRequests.mediaReportsYoy(
                    MediaReportsApiService.yoyRequestBody(otherCampaign), SPOT_BU_ID, token);

            logger.info("** Deserializing the response");
            MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
            validateOtherCampaignsData(mediaReportsDataResponse, softAssert);
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying MMGraph with Actual Data for Inapplicable BU")
    public void YOY_Amazon_Api_Test_Inapplicable_BU() throws Exception {
        logger.info("Extracting response");

        Response response = MediaReportsRequests.mediaReportsYoy(
                MediaReportsApiService.yoyRequestBody(SPOT_CAMPAIGN_TYPE), NON_SPOT_BU_ID, token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
        validateInapplicableBUData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Slicer slice by campaign type for SPOT")
    public void SlicerSliceByCampaignType_Amazon_Api_Test_SPOT() throws Exception {
        logger.info("Extracting response");
        Response response = MediaReportsRequests.mediaReportsSlicerCampaignType(
                MediaReportsApiService.slicerSliceByCampaignTypeRequestBody(SPOT_CAMPAIGN_TYPE), SPOT_BU_ID, token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);

        validateSPOTDataSlicer(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Summary table slice by Campaign Type for Other Campaigns")
    public void SlicerSliceByCampaignType_Amazon_Api_Test_Other_Campaigns() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        for (String otherCampaign : OTHER_CAMPAIGNS) {
            logger.info("Extracting response");

            Response response = MediaReportsRequests.mediaReportsSlicerCampaignType(
                    MediaReportsApiService.slicerSliceByCampaignTypeRequestBody(otherCampaign), SPOT_BU_ID, token);

            logger.info("** Deserializing the response");
            MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
            validateOtherCampaignsData(mediaReportsDataResponse, softAssert);
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying MMGraph with Actual Data for Inapplicable BU")
    public void SlicerSliceByCampaignType_Amazon_Api_Test_Inapplicable_BU() throws Exception {
        logger.info("Extracting response");

        Response response = MediaReportsRequests.mediaReportsSlicerCampaignType(
                MediaReportsApiService.slicerSliceByCampaignTypeRequestBody(SPOT_CAMPAIGN_TYPE), NON_SPOT_BU_ID, token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);
        validateInapplicableBUData(mediaReportsDataResponse);
    }

    @Test(groups = "NoDataProvider", description = "Media scratchpad API test for Amazon - Verifying Summary table slice by Segmentation")
    public void SlicerSliceBySegmentation_Amazon_Api_Test() throws Exception {

        logger.info("Extracting response");
        Response response = MediaReportsRequests.mediaReportsSlicerSlicerSegmentation(
                MediaReportsApiService.slicerSliceBySegmentationRequestBody(null), token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        MediaReportsResponse mediaReportsDataResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), MediaReportsResponse.class);

        validateSPOTDataSlicer(mediaReportsDataResponse);
    }

    private void validateSPOTDataSlicer(MediaReportsResponse mediaReportsDataResponse) {
        validateSPOTData(mediaReportsDataResponse, true);
    }

    private void validateSPOTData(MediaReportsResponse mediaReportsDataResponse) {
        validateSPOTData(mediaReportsDataResponse, false);
    }

    private void validateSPOTData(MediaReportsResponse mediaReportsDataResponse, boolean isSlicer) {
        SoftAssert softAssert = new SoftAssert();

        List<MediaReportsData> data = mediaReportsDataResponse.getData();
        softAssert.assertFalse(data.isEmpty(), "**  No data found in response body");
        for (MediaReportsData mediaReportsData : data) {
            if (isSlicer) {
                softAssert.assertFalse(mediaReportsData.getEntityIntervalComparisons().isEmpty(), "**  No data found in response body for Entity Interval Comparisons");
                softAssert.assertNotNull(mediaReportsData.getEntityIntervalComparisons().get(0).getTotal().getCurrentValue(), "**  Null value found in response body for Current Value");
            }
        }

        var retrievedMetrics = data.stream().map(MediaReportsData::getMetric).collect(Collectors.toSet());
        softAssert.assertTrue(retrievedMetrics.containsAll(SPOT_METRICS), "** Didn't find SPOT Metrics values in response body for SPOT Metrics:");

        var missingMetrics = new HashSet<>(SPOT_METRICS);
        missingMetrics.removeAll(retrievedMetrics);
        softAssert.assertTrue(missingMetrics.isEmpty(), "**  Didn't find SPOT Metrics values in response body for SPOT Metrics: " + missingMetrics);

        softAssert.assertAll();
    }

    private void validateOtherCampaignsData(MediaReportsResponse mediaReportsDataResponse, SoftAssert softAssert) {
        List<MediaReportsData> data = mediaReportsDataResponse.getData();
        var retrievedMetrics = data.stream().map(MediaReportsData::getMetric).collect(Collectors.toSet());
        var hasNoElementsInCommon = Collections.disjoint(retrievedMetrics, SPOT_METRICS);
        softAssert.assertTrue(hasNoElementsInCommon, "** No elements in common for SPOT Metrics");
    }

    private void validateInapplicableBUData(MediaReportsResponse mediaReportsDataResponse) {
        SoftAssert softAssert = new SoftAssert();
        List<MediaReportsData> data = mediaReportsDataResponse.getData();

        for (MediaReportsData mediaReportsData : data) {
            softAssert.assertTrue(metricsMissingOrNoCurrentValue(mediaReportsData), String.format("Metrics %s is missing or has current value", mediaReportsData.getMetric()));
        }
        softAssert.assertAll();
    }

    private boolean metricsMissingOrNoCurrentValue(MediaReportsData mediaReportsData) {
        return mediaReportsData.getData().isEmpty() || !SPOT_METRICS.contains(mediaReportsData.getMetric())
                || (mediaReportsData.getTotal().getCurrentValue() == null || mediaReportsData.getTotal().getCurrentValue() == 0.0);

    }
}