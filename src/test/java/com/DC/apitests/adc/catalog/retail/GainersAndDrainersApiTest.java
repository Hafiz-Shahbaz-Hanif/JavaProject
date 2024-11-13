package com.DC.apitests.adc.catalog.retail;

import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail.RetailApiRequests;
import com.DC.utilities.apiEngine.apiServices.adc.catalog.retail.GainersAndDrainersApiService;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.GainersAndDrainersRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.GainersAndDrainersIntervalResponseBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.GainersAndDrainersResponseBody;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.DC.apitests.ApiValidations.checkResponseStatus;

public class GainersAndDrainersApiTest extends BaseClass {
    private static String token;
    private static final String expectedFirstDayOfLastMonth = DateUtility.getFirstDayOfLastMonth();
    private static final String expectedLastDayOfLastTMonth = DateUtility.getLastDayOfLastMonth();

    @BeforeClass
    public void setup() throws Exception {
        token = SecurityAPI.getTokenAPI();
    }

    @Test(groups = "NoDataProvider",description = "Gainers & Drainers : get Gainers & Drainer All Detail Data")
    public static void GainersAndDrainersApiTest_1() throws Exception {
        LOGGER.info("** Gainers & Drainer test case (" + testMethodName + ") has started.");

        GainersAndDrainersRequestBody.DateRange dr = new GainersAndDrainersRequestBody.DateRange("Last Month");
        GainersAndDrainersRequestBody requestBody = new GainersAndDrainersRequestBody( new GainersAndDrainersRequestBody.PagingAttributes(100,
                1
        ),
                true,"MONTHLY",expectedFirstDayOfLastMonth,dr,expectedFirstDayOfLastMonth,expectedLastDayOfLastTMonth,
                true,true,39,"PREMIUM","VENDOR",false,
                3829,false,false,"AMAZON RETAIL","Manufacturing","SHIPPED_COGS",
                "AVERAGE",3,true,"Shipped COGS","currency","shortHandCurrency");
        Response response = RetailApiRequests.getGainerAndDrainerData(requestBody, token);

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        List<GainersAndDrainersResponseBody> responseModel = GainersAndDrainersApiService.getRetailGainerAndDrainersResponse(response, testMethodName.get());

        LOGGER.info("Verify Gainers Data");
        for (GainersAndDrainersResponseBody gainersAndDrainersResponseBody : responseModel) {
            Assert.assertNotNull(gainersAndDrainersResponseBody.getSegmentationValue(),"** Failure! Null value found in response body for Segmentation data");
            Assert.assertNotNull(gainersAndDrainersResponseBody.getGainers(), "** Failure! Null value found in response body for Gainers data");
        }

        LOGGER.info("Verify Drainers Data");
        for (GainersAndDrainersResponseBody gainersAndDrainersResponseBody : responseModel) {
            Assert.assertNotNull(gainersAndDrainersResponseBody.getDrainers(), "** Failure! Null value found in response body for Drainers data");
        }

    }

    @Test(groups = "NoDataProvider",description = "Gainers & Drainers : get Gainers & Drainer Data for Weekly Interval")
    public static void GainersAndDrainersApiTest_2() throws Exception {
        LOGGER.info("** Gainers & Drainer test case (" + testMethodName + ") has started.");

        GainersAndDrainersRequestBody requestBody = new GainersAndDrainersRequestBody(39,"Manufacturing","WEEKLY","PREMIUM");
        Response response = RetailApiRequests.getGainerAndDrainerIntervalData(requestBody, token);

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        List<GainersAndDrainersIntervalResponseBody> responseModel = GainersAndDrainersApiService.getRetailGainerAndDrainersResponseInterval(response, testMethodName.get());

        LOGGER.info("** Verify Health Info Data");
        for (GainersAndDrainersIntervalResponseBody gainersAndDrainersIntervalResponseBody : responseModel) {
            Assert.assertNotNull(gainersAndDrainersIntervalResponseBody.dataHealthItemInfo.getDataHealthItemInfoValues(), "** Failure! Null value found in response body for Health Info data");
        }

        LOGGER.info("** Verify Health Interval Dates Data");
        for (GainersAndDrainersIntervalResponseBody gainersAndDrainersIntervalResponseBody : responseModel) {
            Assert.assertNotNull(gainersAndDrainersIntervalResponseBody.dataHealthIntervalDates.get(0).interval, "** Failure! Null value found in response body for Interval data");
            Assert.assertNotNull(gainersAndDrainersIntervalResponseBody.dataHealthIntervalDates.get(0).getDates(), "** Failure! Null value found in response body for Health Info data");
        }
    }

}
