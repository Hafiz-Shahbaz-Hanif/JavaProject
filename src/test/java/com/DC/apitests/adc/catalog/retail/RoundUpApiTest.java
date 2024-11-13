package com.DC.apitests.adc.catalog.retail;

import com.DC.objects.CommonBase;
import com.DC.objects.catalog.RetailBase;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail.RetailApiRequests;
import com.DC.utilities.apiEngine.apiServices.adc.catalog.retail.RoundUpApiService;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.RoundupRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.RoundUpProductResponseBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.RoundUpResponseBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.RoundUpSegmentationResponseBody;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Collections;
import java.util.List;

import static com.DC.apitests.ApiValidations.checkResponseStatus;

public class RoundUpApiTest extends BaseClass {

    static List<RoundupRequestBody.SegmentationFilters> segmentationFilters = Collections.emptyList();
    static List<String> segmentationByNameFilters = Collections.emptyList();
    static List<String> asinIds = Collections.emptyList();
    String startDate = DateUtility.getFirstDayOfLastFifteenWeeks();
    String lastDate = DateUtility.getLastDayOfLastThreeWeeks();
    String maxMonthlyDate  = DateUtility.getFirstDayOfLastMonth();
    String maxWeeklyDate = DateUtility.getFirstDayOfLastThreeWeeks();
    String maxDailyDate = DateUtility.getFirstDayOfThisMonth();

    private RoundupRequestBody.PagingAttributes PAGING_ATTRIBUTE = new RoundupRequestBody.PagingAttributes(100,
            1, "current", false);

    private RoundupRequestBody ROUNDUP_REQUEST = new RoundupRequestBody(PAGING_ATTRIBUTE, CommonBase.BUSINESS_UNIT_ID, CommonBase.WEEKLY_INTERVAL,
            startDate, lastDate, segmentationFilters, segmentationByNameFilters, 3875, asinIds,
            CommonBase.AMAZON_PLATFORM, RetailBase.YOY_COMPARISON_TYPE, RetailBase.PREMIUM_CLIENT_CATEGORY, RetailBase.VENDOR_CLIENT_ACCOUNT_TYPE,
            false, RetailBase.fytdStartDate, RetailBase.lfytdStartDate, maxMonthlyDate, "2442", false,
            false, RetailBase.ARAP_TITLE, RetailBase.OBSOLETE_ASIN_TYPE, RetailBase.AMAZON_RETAILER_PLATFORM,
            RetailBase.MANUFACTURING_DISTRIBUTION_VIEW, RetailBase.SHIPPED_COGS_METRIC, maxWeeklyDate, maxDailyDate);

    @Test(groups = "NoDataProvider", description = "Roundup API test - Verifying Roundup All view data")
    public void RoundUp_All_Retail_Api_Test() throws Exception {

        SoftAssert softAssert = new SoftAssert();

        LOGGER.info("Extracting response");
        Response response = RetailApiRequests.roundupAll(ROUNDUP_REQUEST, SecurityAPI.getTokenAPI());

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        RoundUpResponseBody roundUpResponse = RoundUpApiService.getRoundUpAllResponse(response, testMethodName.get());

        LOGGER.info("Verifying mtdAndFiscalWidget does not contains the null values");
        for (int i = 0; i < roundUpResponse.mtdAndFiscalWidget.getMtdAndFiscalWidgetValues().size(); i++) {
            softAssert.assertNotNull(roundUpResponse.mtdAndFiscalWidget.getMtdAndFiscalWidgetValues().get(i), "mtdAndFiscalWidget contains null values");
        }

        LOGGER.info("Verifying metricAndAspWidget does not contains the null values");
        for (int i = 0; i < roundUpResponse.metricAndAspWidget.metric.getMetricValues().size(); i++) {
            softAssert.assertNotNull(roundUpResponse.metricAndAspWidget.metric.getMetricValues().get(i), "metric values in metricAndAspWidget contains null values");
        }

        for (int i = 0; i < roundUpResponse.metricAndAspWidget.averageSellingPrice.averageSellingPriceValues().size(); i++) {
            softAssert.assertNotNull(roundUpResponse.metricAndAspWidget.averageSellingPrice.averageSellingPriceValues().get(i), "Average Selling Price values in metricAndAspWidget contains null values");
        }

        LOGGER.info("Verifying roundup view data for popwidget");
        for (int i = 0; i < roundUpResponse.popWidget.size(); i++) {
            softAssert.assertNotNull(roundUpResponse.popWidget.get(i), "POP Widget contains null values");
        }

        LOGGER.info("Verifying roundup view data for yoywidget");
        for (int i = 0; i < roundUpResponse.yoyWidget.size(); i++) {
            softAssert.assertNotNull(roundUpResponse.yoyWidget.get(i), "POP Widget contains null values");
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Roundup API test - Verifying Roundup Product view data")
    public void RoundUp_Product_Retail_Api_Test() throws Exception {

        SoftAssert softAssert = new SoftAssert();

        LOGGER.info("Extracting response");
        Response response = RetailApiRequests.roundupProduct(ROUNDUP_REQUEST, SecurityAPI.getTokenAPI());

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        RoundUpProductResponseBody roundUpProductResponse = RoundUpApiService.getRoundUpProductResponse(response, testMethodName.get());

        LOGGER.info("Verifying RoundUp Product Meta data");
        AsinSegmentationApiTest.verifyMetaData(roundUpProductResponse.getMeta());

        LOGGER.info("Verifying Round Up Product Items does not contains the null values");
        for (int i = 0; i < roundUpProductResponse.items.size(); i++) {
            List<String> itemsData = roundUpProductResponse.items.get(i).getRoundUpProductItemsData();
            for(String items : itemsData) {
                softAssert.assertNotNull(items, "Round Up Product Items contains null values");
            }
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Roundup API test - Verifying Roundup Segmentation view data")
    public void RoundUp_Segmentation_Retail_Api_Test() throws Exception {

        SoftAssert softAssert = new SoftAssert();

        LOGGER.info("Extracting response");
        Response response = RetailApiRequests.roundupSegmentation(ROUNDUP_REQUEST, SecurityAPI.getTokenAPI());

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        RoundUpSegmentationResponseBody roundUpSegmentationResponse = RoundUpApiService.getRoundUpSegmentationResponse(response, testMethodName.get());

        LOGGER.info("Verifying Round Up Segmentation does not contains the null values");
        for (int i = 0; i < roundUpSegmentationResponse.subCategory.size(); i++) {
            softAssert.assertNotNull(roundUpSegmentationResponse.subCategory.get(i).segmentationValue, "Segmentation Value is null in the Round Up Segmentation data");
            softAssert.assertNotNull(roundUpSegmentationResponse.subCategory.get(i).segmentationTypeId, "Segmentation Type ID is null in the Round Up Segmentation data");
            softAssert.assertNotNull(roundUpSegmentationResponse.subCategory.get(i).segmentationValueId, "Segmentation Value ID is null in the Round Up Segmentation data");
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Roundup API test - Verifying Roundup AggBus view data")
    public void RoundUp_AggBus_Retail_Api_Test() throws Exception {

        SoftAssert softAssert = new SoftAssert();
        String token = SecurityAPI.getTokenAPI();

        LOGGER.info("Extracting response for Segmentation AggBus");
        Response response = RetailApiRequests.getRoundUpAggbusData(ROUNDUP_REQUEST, "segmentation", token, "x-businessunitcontext=113,71","x-currencycontext=USD");

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        RoundUpSegmentationResponseBody roundUpSegmentationResponse = RoundUpApiService.getRoundUpSegmentationResponse(response, testMethodName.get());

        LOGGER.info("Verifying Round Up Segmentation does not contains the null values");
        for (int i = 0; i < roundUpSegmentationResponse.subCategory.size(); i++) {
            softAssert.assertNotNull(roundUpSegmentationResponse.subCategory.get(i).segmentationValue, "Segmentation Value is null in the Round Up Segmentation data");
            softAssert.assertNotNull(roundUpSegmentationResponse.subCategory.get(i).segmentationType, "Segmentation Type is null in the Round Up Segmentation data");
            softAssert.assertNotNull(roundUpSegmentationResponse.subCategory.get(i).segmentationTypeId, "Segmentation Type ID is null in the Round Up Segmentation data");
            softAssert.assertNotNull(roundUpSegmentationResponse.subCategory.get(i).segmentationValueId, "Segmentation Value ID is null in the Round Up Segmentation data");
            softAssert.assertNotNull(roundUpSegmentationResponse.subCategory.get(i).businessUnitId, "Business Unit ID is null in the Round Up Segmentation data");
            softAssert.assertNotNull(roundUpSegmentationResponse.subCategory.get(i).businessUnitName, "Business Unit Name is null in the Round Up Segmentation data");
        }

        LOGGER.info("Extracting response for Product AggBus");
        response = RetailApiRequests.getRoundUpAggbusData(ROUNDUP_REQUEST, "product", token, "x-businessunitcontext=113,71","x-currencycontext=USD");

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        RoundUpProductResponseBody roundUpProductResponse = RoundUpApiService.getRoundUpProductResponse(response, testMethodName.get());

        LOGGER.info("Verifying RoundUp Product Meta data");
        AsinSegmentationApiTest.verifyMetaData(roundUpProductResponse.getMeta());

        LOGGER.info("Verifying Round Up Product Items does not contains the null values");
        for (int i = 0; i < roundUpProductResponse.items.size(); i++) {
            softAssert.assertNotNull(roundUpProductResponse.items.get(i).businessUnitId, "Business Unit ID is null in the Round Up Product data");
            softAssert.assertNotNull(roundUpProductResponse.items.get(i).businessUnitName, "Business Unit Name is null in the Round Up Product data");
            softAssert.assertNotNull(roundUpProductResponse.items.get(i).asin, "Asin is null in the Round Up Product data");
            softAssert.assertNotNull(roundUpProductResponse.items.get(i).asinTitle, "Asin Title is null in the Round Up Product data");
            softAssert.assertNotNull(roundUpProductResponse.items.get(i).clientAccountName, "Client Account Name is null in the Round Up Product data");
            softAssert.assertNotNull(roundUpProductResponse.items.get(i).platform, "Platform is null in the Round Up Product data");
        }

        softAssert.assertAll();
    }
}
