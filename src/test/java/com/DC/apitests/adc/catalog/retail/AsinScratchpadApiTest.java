package com.DC.apitests.adc.catalog.retail;

import com.DC.objects.CommonBase;
import com.DC.objects.catalog.RetailBase;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail.RetailApiRequests;
import com.DC.utilities.apiEngine.apiServices.adc.catalog.retail.AsinScratchpadApiService;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.AsinScratchpadRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.AsinScratchpadResponseBody;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Collections;
import java.util.List;

import static com.DC.apitests.ApiValidations.checkResponseStatus;

public class AsinScratchpadApiTest extends BaseClass {
    static AsinScratchpadRequestBody.DateRange dateRangeLabel = new AsinScratchpadRequestBody.DateRange(CommonBase.LAST_13_Weeks_LABEL);
    static List<String> asinIds = SharedMethods.createList("122303");
    static List<String> segmentationFilters = Collections.emptyList();

    private AsinScratchpadRequestBody ASIN_SCRATCHPAD_REQUEST_MANUFACTURING = AsinScratchpadApiService.asinScratchpadRequestBody(CommonBase.WEEKLY_INTERVAL,
            RetailBase.SHIPPED_COGS_METRIC, dateRangeLabel, DateUtility.getFirstDayOfLastThirteenWeeks(),
            DateUtility.getLastDayOfLastThirteenWeeks(),CommonBase.HERSHEYS_US_BUSINESS_UNIT_ID, RetailBase.PREMIUM_CLIENT_CATEGORY, RetailBase.VENDOR_CLIENT_ACCOUNT_TYPE,
            segmentationFilters, 3829, RetailBase.AMAZON_RETAILER_PLATFORM, RetailBase.MANUFACTURING_DISTRIBUTION_VIEW,
            asinIds);

    private AsinScratchpadRequestBody ASIN_SCRATCHPAD_REQUEST_SOURCING = AsinScratchpadApiService.asinScratchpadRequestBody(CommonBase.WEEKLY_INTERVAL,
            RetailBase.SHIPPED_COGS_METRIC, dateRangeLabel, DateUtility.getFirstDayOfLastThirteenWeeks(),
            DateUtility.getLastDayOfLastThirteenWeeks(),CommonBase.NIELSEN_MASSEY_US_BUSINESS_UNIT_ID, RetailBase.BASIC_CLIENT_CATEGORY, RetailBase.SELLER_CLIENT_ACCOUNT_TYPE,
            segmentationFilters, 3829, RetailBase.AMAZON_RETAILER_PLATFORM, RetailBase.SOURCING_DISTRIBUTION_VIEW,
            asinIds);

    @Test(groups = "NoDataProvider", description = "Asin Scratchpad API test - Verifying manufacturing distributor view data")
    public void Asin_Scratchpad_Manufacturing_Retail_Api_Test() throws Exception {

        SoftAssert softAssert = new SoftAssert();

        LOGGER.info("Extracting response");
        Response response = RetailApiRequests.asinScratchpad(ASIN_SCRATCHPAD_REQUEST_MANUFACTURING, SecurityAPI.getTokenAPI());

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        List<AsinScratchpadResponseBody> asinScratchpadResponse = AsinScratchpadApiService.getAsinScratchpadResponse(response, testMethodName.get());

        LOGGER.info("Verifying manufacturing distributor view data");
        for (int i = 0; i < asinScratchpadResponse.size(); i++) {
            softAssert.assertEquals(asinScratchpadResponse.get(i).clientAccountName, CommonBase.CLIENT_HERSHEYS_US , "** Failure! Client Account Name is not correct");
            softAssert.assertTrue(asinScratchpadResponse.get(i).asin.contains("B000F8GWRM"), "** Failure! Asin is not correct");
            softAssert.assertEquals(asinScratchpadResponse.get(i).title,
                    "TWIZZLERS Zero Sugar Twists Strawberry Flavored Chewy Candy, Bulk Aspartame Free, 5 oz Bags (12 Count)",
                    "** Failure! Asin Title is not correct");
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Asin Scratchpad API test - Verifying sourcing distributor view data")
    public void Asin_Scratchpad_Sourcing_Retail_Api_Test() throws Exception {

        SoftAssert softAssert = new SoftAssert();

        LOGGER.info("Extracting response");
        Response response = RetailApiRequests.asinScratchpad(ASIN_SCRATCHPAD_REQUEST_SOURCING, SecurityAPI.getTokenAPI());

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        List<AsinScratchpadResponseBody> asinScratchpadResponse = AsinScratchpadApiService.getAsinScratchpadResponse(response, testMethodName.get());

        LOGGER.info("Verifying manufacturing distributor view data");
        for (int i = 0; i < asinScratchpadResponse.size(); i++) {
            softAssert.assertEquals(asinScratchpadResponse.get(i).clientAccountName, CommonBase.CLIENT_NIELSEN_MASSEY_US , "** Failure! Client Account Name is not correct");
            softAssert.assertTrue(asinScratchpadResponse.get(i).asin.contains("B006OD5ISG"), "** Failure! Asin is not correct");
            softAssert.assertEquals(asinScratchpadResponse.get(i).title,
                    "Nielsen-Massey Pure Vanilla Bean Paste, with Gift Box, 4 ounces",
                    "** Failure! Asin Title is not correct");
        }

        softAssert.assertAll();
    }
}
