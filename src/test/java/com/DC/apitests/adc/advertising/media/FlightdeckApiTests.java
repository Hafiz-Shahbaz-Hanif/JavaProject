package com.DC.apitests.adc.advertising.media;

import com.DC.testcases.BaseClass;
import com.DC.utilities.*;
import com.DC.utilities.apiEngine.apiRequests.adc.advertising.media.FlightdeckApiRequests;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.FlightdeckRequestBody;
import com.DC.utilities.apiEngine.routes.adc.advertising.media.MediaRoutes;
import com.DC.utilities.apiEngine.models.responses.adc.advertising.media.*;

import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.db.adc.advertising.media.FlightdeckQueries.executeQuery;

public class FlightdeckApiTests extends BaseClass {
    private static Logger logger;

    FlightdeckApiTests() {
        logger = Logger.getLogger(FlightdeckApiTests.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    private String token;

    @BeforeClass
    public void setup() throws Exception {
        token = SecurityAPI.getOktaTokenForUser(READ_CONFIG.getBearerUserName(), READ_CONFIG.getBearerPassword());
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Amazon Campaign data")
    public void Api_Flightdeck_CanGetFlightDeckCampaignData(String testCaseID, String businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfLastWeek();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "CAMPAIGNS", new FlightdeckRequestBody.DateRange(
                "Custom Range"
        ), 39, date, date,
                "AMAZON",
                new ArrayList<>(),
                1941
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.campaignEndpoint, token);

        CampaignResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, CampaignResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<CampaignResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForAmazonCampaign", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");

    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Amazon data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckData_InvalidToken(String testCaseID, String businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastWeek = DateUtility.getFirstDayOfLastWeek();
        String lastDayOfLastWeek = DateUtility.getLastDayOfLastWeek();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1
                ),
                "CAMPAIGNS", new FlightdeckRequestBody.DateRange(
                "Last Week"
        ), 3,
                firstDayOfLastWeek, lastDayOfLastWeek,

                "AMAZON",
                new ArrayList<>(),
                1941
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.campaignEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Amazon data with missing element")
    public void Api_Flightdeck_CannotGetFlightdeckData_MissingViewingElement(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfThisMonth = DateUtility.getFirstDayOfThisMonth();
        String yesterday = DateUtility.getYesterday();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1
                ),
                null,  //Pass null as required parameter
                new FlightdeckRequestBody.DateRange(
                        "This Month"
                ), 3,
                firstDayOfThisMonth, yesterday,

                "AMAZON",
                new ArrayList<>(),
                1941
        );

        //Send the request and check for a 500 Internal Server Error
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.campaignEndpoint, token);

        checkResponseStatus(testCaseID, 500, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");

    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Amazon data with invalid page size")
    public void Api_Flightdeck_CannotGetFlightdeckData_InvalidPageSize(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfThisYear = DateUtility.getFirstDayOfTheYear();
        String yesterday = DateUtility.getYesterday();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        -1, // Pass an invalid page size
                        1
                ),
                "CAMPAIGNS", new FlightdeckRequestBody.DateRange(
                "Year to date"
        ), 3,
                firstDayOfThisYear, yesterday,
                "AMAZON",
                new ArrayList<>(),
                1941
        );

        //Send the request and check for a 500 Internal Server Error
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.campaignEndpoint, token);

        checkResponseStatus(testCaseID, 500, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");

    }


    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Amazon Keywords by Campaigns data")
    public void Api_Flightdeck_CanGetFlightDeckAmazonKeywordsByCampaignData(String testCaseID, String businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getLastDayOfLastThirteenWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "KEYWORDS_BY_CAMPAIGN",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                new ArrayList<>(),
                "AMAZON",
                1941
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.keywordsByCampaignEndpoint, token);

        KeywordByCampaignResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, KeywordByCampaignResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<KeywordByCampaignResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForAmazonKeywordsByCampaign", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Amazon Keywords by Campaigns data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckKeywordsByCampaignData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastWeek = DateUtility.getFirstDayOfLastWeek();
        String lastDayOfLastWeek = DateUtility.getLastDayOfLastWeek();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "KEYWORDS_BY_CAMPAIGN",
                new FlightdeckRequestBody.DateRange(
                        "Last 30 Days"
                ),
                39,
                firstDayOfLastWeek,
                lastDayOfLastWeek,
                new ArrayList<>(),
                "AMAZON",
                1941
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.keywordsByCampaignEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Amazon Keywords Rolled Up data")
    public void Api_Flightdeck_CanGetFlightDeckAmazonKeywordsRolledUpData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfLastSevenDays();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "KEYWORDS_ROLLED_UP",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                new ArrayList<>(),
                "AMAZON",
                1941
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.keywordsRolledUpEndpoint, token);

        KeywordsRolledUpResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, KeywordsRolledUpResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<KeywordsRolledUpResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForAmazonKeywordsRolledUp", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Amazon Keywords Rolled Up data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckKeywordsRolledUpData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastWeek = DateUtility.getFirstDayOfLastWeek();
        String lastDayOfLastWeek = DateUtility.getLastDayOfLastWeek();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "KEYWORDS_ROLLED_UP",
                new FlightdeckRequestBody.DateRange(
                        "Last 14 Days"
                ),
                39,
                firstDayOfLastWeek,
                lastDayOfLastWeek,
                new ArrayList<>(),
                "AMAZON",
                1941
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.keywordsRolledUpEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Amazon ASIN data")
    public void Api_Flightdeck_CanGetFlightDeckAmazonASINData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfTheYear();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "ASINS",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                "AMAZON",
                1941,
                new ArrayList<>()
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.asinEndpoint, token);

        ASINResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, ASINResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<ASINResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForAmazonASIN", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }


    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Amazon ASIN data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckASINData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastWeek = DateUtility.getFirstDayOfLastWeek();
        String lastDayOfLastWeek = DateUtility.getLastDayOfLastWeek();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "ASINS",
                new FlightdeckRequestBody.DateRange(
                        "Last 7 Days"
                ),
                39,
                firstDayOfLastWeek,
                lastDayOfLastWeek,
                "AMAZON",
                1941,
                new ArrayList<>()
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "amazon", MediaRoutes.asinEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Amazon CSQ data")
    public void Api_Flightdeck_CanGetFlightDeckAmazonCSQData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfLastThirtyDays();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "QUERY_DRIVERS",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ), 39, date, date,
                new ArrayList<>(),
                "AMAZON",
                1941
        );

        Response response = FlightdeckApiRequests.getCampaignDataWithNewHeader(requestModel, "amazon", MediaRoutes.csqEndpoint, token, "x-businessunitcontext=39");

        AmazonCSQResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, AmazonCSQResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<AmazonCSQResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForAmazonCSQ", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");

    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Amazon CSQ data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckCSQData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastWeek = DateUtility.getFirstDayOfLastWeek();
        String lastDayOfLastWeek = DateUtility.getLastDayOfLastWeek();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "QUERY_DRIVERS",
                new FlightdeckRequestBody.DateRange(
                        "This Month"
                ),
                39,
                firstDayOfLastWeek,
                lastDayOfLastWeek,
                new ArrayList<>(),
                "AMAZON",
                1941
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignDataWithNewHeader(requestModel, "amazon", MediaRoutes.csqEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Walmart Campaigns data")
    public void Api_Flightdeck_CanGetFlightDeckWalmartCampaignData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getLastDayOfLastSixMonths();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        2,
                        1,
                        false,
                        "spend"
                ),
                "WALMART_CAMPAIGNS",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                SharedMethods.createList("advertisedSkuSales,otherSkuSales"),
                39, date, date,
                new ArrayList<>(),
                "WALMART",
                "ATTR_14D"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.campaignEndpoint, token);

        WalmartCampaignResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, WalmartCampaignResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<WalmartCampaignResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForWalmartCampaigns", true, true, true, false);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Walmart data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckWalmartData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastThirteenWeeks = DateUtility.getFirstDayOfLastThirteenWeeks();
        String lastDayOfLastThirteenWeeks = DateUtility.getLastDayOfLastThirteenWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1
                ),
                "WALMART_CAMPAIGNS",
                new FlightdeckRequestBody.DateRange(
                        "Last 13 Weeks"
                ),
                SharedMethods.createList("clickRevenue,relatedClickRevenue,brandClickRevenue"),
                39,
                firstDayOfLastThirteenWeeks, lastDayOfLastThirteenWeeks,
                new ArrayList<>(),
                "WALMART",
                "ATTR_14D"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.campaignEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Walmart data without choosing at least one Sale Metric")
    public void Api_Flightdeck_CannotGetFlightdeckWalmartData_SaleMetricsMissing(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastMonth = DateUtility.getFirstDayOfLastMonth();
        String lastDayOfLastMonth = DateUtility.getLastDayOfLastMonth();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1
                ),
                "WALMART_CAMPAIGNS",
                new FlightdeckRequestBody.DateRange(
                        "Last Month"
                ),
                null,  //No required data was chosen
                39,
                firstDayOfLastMonth, lastDayOfLastMonth,
                new ArrayList<>(),
                "WALMART",
                "ATTR_14D"
        );

        //Send the request and check for a 500 Internal Server Error
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.campaignEndpoint, token);

        checkResponseStatus(testCaseID, 500, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Walmart data with invalid page size")
    public void Api_Flightdeck_CannotGetFlightdeckWalmartData_InvalidPageSize(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String yesterday = DateUtility.getYesterday();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        -1, // Pass an invalid page size
                        1
                ),
                "WALMART_CAMPAIGNS",
                new FlightdeckRequestBody.DateRange(
                        "Yesterday"
                ),
                SharedMethods.createList("clickRevenue,relatedClickRevenue,brandClickRevenue"),
                39,
                yesterday, yesterday,
                new ArrayList<>(),
                "WALMART",
                "ATTR_14D"
        );

        //Send the request and check for a 500 Internal Server Error
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.campaignEndpoint, token);

        checkResponseStatus(testCaseID, 500, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");

    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Walmart Keyword by Ad Group data")
    public void Api_Flightdeck_CanGetFlightDeckWalmartKeywordByAdGroupData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfLastThirteenWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "KEYWORDS_BY_ADGROUP",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                SharedMethods.createList("advertisedSkuSales,otherSkuSales"),
                39,
                date, date,
                new ArrayList<>(),
                "WALMART",
                "ATTR_14D"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.keywordsByAdGroupEndpoint, token);

        KeywordByAdGroupResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, KeywordByAdGroupResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<KeywordByAdGroupResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForWalmartKeywords", true, true, true, false);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }


    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Walmart Keyword by Ad Group data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckWalmartKeywordByAdGroupData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastThirteenWeeks = DateUtility.getFirstDayOfLastThirteenWeeks();
        String lastDayOfLastThirteenWeeks = DateUtility.getLastDayOfLastThirteenWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1
                ),
                "KEYWORDS_BY_ADGROUP",
                new FlightdeckRequestBody.DateRange(
                        "Last 7 Days"
                ),
                SharedMethods.createList("clickRevenue"),
                39,
                firstDayOfLastThirteenWeeks, lastDayOfLastThirteenWeeks,
                new ArrayList<>(),
                "WALMART",
                "ATTR_14D"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.keywordsByAdGroupEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Walmart Keyword by Ad Group data without choosing at least one Sale Metric")
    public void Api_Flightdeck_CannotGetFlightdeckWalmartKeywordByAdGroupData_SaleMetricsMissing(String testCaseID, String businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastMonth = DateUtility.getFirstDayOfLastMonth();
        String lastDayOfLastMonth = DateUtility.getLastDayOfLastMonth();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1
                ),
                "KEYWORDS_BY_ADGROUP",
                new FlightdeckRequestBody.DateRange(
                        "Last 7 Days"
                ),
                null,
                39,
                firstDayOfLastMonth, lastDayOfLastMonth,
                new ArrayList<>(),
                "WALMART",
                "ATTR_14D"
        );

        //Send the request and check for a 500 Internal Server Error
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.keywordsByAdGroupEndpoint, token);

        checkResponseStatus(testCaseID, 500, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Walmart Customer Search Query data")
    public void Api_Flightdeck_CanGetFlightDeckWalmartCSQData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getLastDayOfLastFourWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "WALMART_CSQ",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                SharedMethods.createList("advertisedSkuSales,otherSkuSales"),
                39, date, date,
                new ArrayList<>(),
                "WALMART",
                "ATTR_14D"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.csqEndpoint, token);

        CSQResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, CSQResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<CSQResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForWalmartCSQ", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Walmart Customer Search Query data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckWalmartCSQData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastThirteenWeeks = DateUtility.getFirstDayOfLastThirteenWeeks();
        String lastDayOfLastThirteenWeeks = DateUtility.getLastDayOfLastThirteenWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "WALMART_CSQ",
                new FlightdeckRequestBody.DateRange(
                        "Last Month"
                ),
                SharedMethods.createList("brandClickRevenue"),
                39,
                firstDayOfLastThirteenWeeks, lastDayOfLastThirteenWeeks,
                new ArrayList<>(),
                "WALMART",
                "ATTR_3D"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.csqEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Walmart Customer Search Query data without choosing at least one Sale Metric")
    public void Api_Flightdeck_CannotGetFlightdeckWalmartCSQData_SaleMetricsMissing(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastMonth = DateUtility.getFirstDayOfLastMonth();
        String lastDayOfLastMonth = DateUtility.getLastDayOfLastMonth();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "WALMART_CSQ",
                new FlightdeckRequestBody.DateRange(
                        "Last Month"
                ),
                null,
                39,
                firstDayOfLastMonth, lastDayOfLastMonth,
                new ArrayList<>(),
                "WALMART",
                "ATTR_3D"
        );

        //Send the request and check for a 500 Internal Server Error
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.csqEndpoint, token);

        checkResponseStatus(testCaseID, 500, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Walmart Item data")
    public void Api_Flightdeck_CanGetFlightDeckWalmartItemData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfLastFourteenDays();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "WALMART_ITEMS_ROLLED_UP",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                SharedMethods.createList("advertisedSkuSales,otherSkuSales"),
                39, date, date,
                new ArrayList<>(),
                "WALMART",
                "ATTR_30D"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.itemEndpoint, token);

        WalmartItemResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, WalmartItemResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<WalmartItemResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForWalmartItem", true, true, true, false);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Walmart Item data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckWalmartItemData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastThirteenWeeks = DateUtility.getFirstDayOfLastThirteenWeeks();
        String lastDayOfLastThirteenWeeks = DateUtility.getLastDayOfLastThirteenWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "WALMART_ITEMS_ROLLED_UP",
                new FlightdeckRequestBody.DateRange(
                        "Last Week"
                ),
                SharedMethods.createList("relatedClickRevenue"),
                39,
                firstDayOfLastThirteenWeeks, lastDayOfLastThirteenWeeks,
                new ArrayList<>(),
                "WALMART",
                "ATTR_30D"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.itemEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Walmart Item data without choosing at least one Sale Metric")
    public void Api_Flightdeck_CannotGetFlightdeckWalmartItemData_SaleMetricsMissing(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastMonth = DateUtility.getFirstDayOfLastMonth();
        String lastDayOfLastMonth = DateUtility.getLastDayOfLastMonth();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "WALMART_ITEMS_ROLLED_UP",
                new FlightdeckRequestBody.DateRange(
                        "Last Month"
                ),
                null,
                39,
                firstDayOfLastMonth, lastDayOfLastMonth,
                new ArrayList<>(),
                "WALMART",
                "ATTR_3D"
        );

        //Send the request and check for a 500 Internal Server Error
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "walmart", MediaRoutes.itemEndpoint, token);

        checkResponseStatus(testCaseID, 500, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Citrus Ad Campaign data")
    public void Api_Flightdeck_CanGetFlightDeckCitrusAdCampaignData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfLastSevenDays();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "campaignName"
                ),
                "CITRUS_AD_CAMPAIGN",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                "CITRUS_AD"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "citrusAd", MediaRoutes.campaignEndpoint, token);

        CitrusAdCampaignResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, CitrusAdCampaignResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<CitrusAdCampaignResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForCitrusAdCampaign", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck CitrusAd Campaign data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckCitrusAdCampaignData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastThirteenWeeks = DateUtility.getFirstDayOfLastThirteenWeeks();
        String lastDayOfLastThirteenWeeks = DateUtility.getLastDayOfLastThirteenWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "campaignName"
                ),
                "CITRUS_AD_CAMPAIGN",
                new FlightdeckRequestBody.DateRange(
                        "Last 13 Weeks"
                ),
                39,
                firstDayOfLastThirteenWeeks, lastDayOfLastThirteenWeeks,
                "CITRUS_AD"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "citrusAd", MediaRoutes.campaignEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Criteo Line Item data")
    public void Api_Flightdeck_CanGetFlightDeckCriteoLineItemData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getLastDayOfLastFourWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "CRITEO_LINEITEM",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                "CRITEO"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "criteo", MediaRoutes.lineItemEndpoint, token);

        CriteoLineItemResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, CriteoLineItemResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<CriteoLineItemResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForCriteoItem", true, true, true, false);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Criteo Line Item data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckCriteoLineItemData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastWeek = DateUtility.getFirstDayOfLastWeek();
        String lastDayOfLastWeek = DateUtility.getLastDayOfLastWeek();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "CRITEO_LINEITEM",
                new FlightdeckRequestBody.DateRange(
                        "Last Week"
                ),
                39,
                firstDayOfLastWeek, lastDayOfLastWeek,
                "CRITEO"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "criteo", MediaRoutes.lineItemEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Criteo Product data")
    public void Api_Flightdeck_CanGetFlightDeckCriteoProductData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getLastDayOfLastTwelveMonths();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "CRITEO_PRODUCT",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                "CRITEO"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "criteo", MediaRoutes.productEndpoint, token);

        CriteoProductResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, CriteoProductResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<CriteoProductResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForCriteoProduct", true, true, true, false);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Criteo Product data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckCriteoProductData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastFourWeeks = DateUtility.getFirstDayOfLastFourWeeks();
        String lastDayOfLastFourWeeks = DateUtility.getLastDayOfLastFourWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "CRITEO_PRODUCT",
                new FlightdeckRequestBody.DateRange(
                        "Last 4 Weeks"
                ),
                39,
                firstDayOfLastFourWeeks, lastDayOfLastFourWeeks,
                "CRITEO"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "criteo", MediaRoutes.productEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Instacart AdGroup data")
    public void Api_Flightdeck_CanGetFlightDeckInstacartAdGroupData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfTheYear();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "INSTACART_ADGROUP",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                "INSTACART"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "instacart", MediaRoutes.adGroupEndpoint, token);

        InstacartAdGroupResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, InstacartAdGroupResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<InstacartAdGroupResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForInstacartAdGroup", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Instacart AdGroup data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckDeckInstacartAdGroupData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
        String lastDayOfLastSevenDays = DateUtility.getYesterday();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "INSTACART_ADGROUP",
                new FlightdeckRequestBody.DateRange(
                        "Last 7 Days"
                ),
                39,
                firstDayOfLastSevenDays, lastDayOfLastSevenDays,
                "INSTACART"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "instacart", MediaRoutes.adGroupEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Instacart Campaign data")
    public void Api_Flightdeck_CanGetFlightDeckInstacartCampaignData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfTheYear();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "INSTACART_CAMPAIGNS",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                "INSTACART"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "instacart", MediaRoutes.campaignEndpoint, token);

        InstacartCampaignResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, InstacartCampaignResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<InstacartCampaignResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForInstacartCampaign", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Instacart Campaign data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckDeckInstacartCampaignData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
        String lastDayOfLastSevenDays = DateUtility.getYesterday();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "INSTACART_CAMPAIGNS",
                new FlightdeckRequestBody.DateRange(
                        "Last 13 Weeks"
                ),
                39,
                firstDayOfLastSevenDays, lastDayOfLastSevenDays,
                "INSTACART"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "instacart", MediaRoutes.campaignEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Instacart Keyword By Ad Group data")
    public void Api_Flightdeck_CanGetFlightDeckInstacartKeywordByAdGroupData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getLastDayOfLastMonth();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "INSTACART_KEYWORDS_BY_ADGROUP",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                "INSTACART"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "instacart", MediaRoutes.keywordsByAdGroupEndpoint, token);

        InstacartKeywordByAdGroupResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, InstacartKeywordByAdGroupResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<InstacartKeywordByAdGroupResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForInstacartKeywords", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Instacart Keyword By AdGroup data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckDeckInstacartKeywordByAdGroupData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
        String lastDayOfLastSevenDays = DateUtility.getYesterday();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "INSTACART_KEYWORDS_BY_ADGROUP",
                new FlightdeckRequestBody.DateRange(
                        "Last 7 Days"
                ),
                39,
                firstDayOfLastSevenDays, lastDayOfLastSevenDays,
                "INSTACART"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "instacart", MediaRoutes.keywordsByAdGroupEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck Instacart Keyword RolledUp data")
    public void Api_Flightdeck_CanGetFlightDeckInstacartKeywordRolledUpData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String date = DateUtility.getFirstDayOfThisMonth();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "INSTACART_KEYWORDS_ROLLED_UP",
                new FlightdeckRequestBody.DateRange(
                        "Custom Range"
                ),
                39, date, date,
                new ArrayList<>(),
                "INSTACART"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "instacart", MediaRoutes.keywordsRolledUpEndpoint, token);

        InstacartKeywordsRolledUpResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, InstacartKeywordsRolledUpResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<InstacartKeywordsRolledUpResponseBody.Items> items = responseModel.getItems();

        compareData(items, date, "queryForInstacartKeywordsRolledUp", true, true, true, true);

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck Instacart Keyword RolledUp data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckDeckInstacartKeywordRolledUpData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
        String lastDayOfLastSevenDays = DateUtility.getYesterday();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "INSTACART_KEYWORDS_ROLLED_UP",
                new FlightdeckRequestBody.DateRange(
                        "Last 14 Days"
                ),
                39,
                firstDayOfLastSevenDays, lastDayOfLastSevenDays,
                new ArrayList<>(),
                "INSTACART"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "instacart", MediaRoutes.keywordsRolledUpEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck DoorDash Campaigns data")
    public void Api_Flightdeck_CanGetFlightDeckDoorDashCampaignsData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastFiftyTwoWeeks = DateUtility.getFirstDayOfLastFiftyTwoWeeks();
        String lastDayOfLastFiftyTwoWeeks = DateUtility.getLastDayOfLastFiftyTwoWeeks();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        5,
                        1,
                        false,
                        "spend"
                ),
                "DOORDASH_CAMPAIGN",
                new FlightdeckRequestBody.DateRange(
                        "Last 14 Days"
                ),
                39,
                firstDayOfLastFiftyTwoWeeks, lastDayOfLastFiftyTwoWeeks,
                "DOORDASH"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "doordash", MediaRoutes.campaignEndpoint, token);

        DoorDashCampaignsResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, DoorDashCampaignsResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<DoorDashCampaignsResponseBody.Items> items = responseModel.getItems();

        for (int i = 0; i < items.size(); i++) {
            List<String> itemsData = responseModel.items.get(i).getDoorDashCampaignsData();
            for (int j = 0; j < itemsData.size(); j++) {
                Assert.assertNotNull(itemsData.get(j), "** Failure! Null value found in response body at index " + j + " of item at index " + i);
            }
        }

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck DoorDash Campaigns data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckDeckDoorDashCampaignsData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
        String lastDayOfLastSevenDays = DateUtility.getYesterday();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "DOORDASH_CAMPAIGN",
                new FlightdeckRequestBody.DateRange(
                        "Last 52 Weeks"
                ),
                39,
                firstDayOfLastSevenDays, lastDayOfLastSevenDays,
                "DOORDASH"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "doordash", MediaRoutes.campaignEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: get Flightdeck DoorDash AdGroup data")
    public void Api_Flightdeck_CanGetFlightDeckDoorDashAdGroupData(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastOfLastMonth = DateUtility.getFirstDayOfLastMonth();
        String lastDayOfLastOfLastMonth = DateUtility.getLastDayOfLastMonth();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "DOORDASH_ADGROUP",
                new FlightdeckRequestBody.DateRange(
                        "Last Month"
                ),
                39,
                firstDayOfLastOfLastMonth, lastDayOfLastOfLastMonth,
                "DOORDASH"
        );

        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "doordash", MediaRoutes.adGroupEndpoint, token);

        DoorDashAdGroupResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testCaseID, DoorDashAdGroupResponseBody.class);

        verifyMetaData(responseModel.getMeta());

        List<DoorDashAdGroupResponseBody.Items> items = responseModel.getItems();

        for (int i = 0; i < items.size(); i++) {
            List<String> itemsData = responseModel.items.get(i).getDoorDashAdGroupData();
            for (int j = 0; j < itemsData.size(); j++) {
                Assert.assertNotNull(itemsData.get(j), "** Failure! Null value found in response body at index " + j + " of item at index " + i);
            }
        }

        for (DoorDashAdGroupResponseBody.Items item : items) {
            List<DoorDashAdGroupResponseBody.Items.BidPlacements> bidPlacements = item.bidPlacements;
            for (DoorDashAdGroupResponseBody.Items.BidPlacements bidPlacement : bidPlacements) {
                List<String> metricData = bidPlacement.getBidPlacementsData();
                for (int j = 0; j < metricData.size(); j++) {
                    Assert.assertNotNull(metricData.get(j), "** Failure! Null value found in response body at index " + j + " of metric in item " + items.indexOf(item));
                }
            }
        }

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    @Test(dataProvider = "Flightdeck", dataProviderClass = MediaApiDataProvider.class, description = "Flightdeck test: can't get Flightdeck DoorDash AdGroup data with invalid token")
    public void Api_Flightdeck_CannotGetFlightdeckDeckDoorDashAdGroupData_InvalidToken(String testCaseID, String
            businessUnit, String client, String headers, String params) throws Exception {

        logger.info("** Flightdeck test case (" + testCaseID + ") has started.");

        String firstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
        String lastDayOfLastSevenDays = DateUtility.getYesterday();

        FlightdeckRequestBody requestModel = new FlightdeckRequestBody(
                new FlightdeckRequestBody.PagingAttributes(
                        100,
                        1,
                        false,
                        "spend"
                ),
                "DOORDASH_ADGROUP",
                new FlightdeckRequestBody.DateRange(
                        "Last Month"
                ),
                39,
                firstDayOfLastSevenDays, lastDayOfLastSevenDays,
                "DOORDASH"
        );

        //Send the request and check for a 401 Unauthorized
        Response response = FlightdeckApiRequests.getCampaignData(requestModel, "doordash", MediaRoutes.adGroupEndpoint, "invalid token");

        checkResponseStatus(testCaseID, 401, response.statusCode());

        logger.info("** Execution for test case (" + testCaseID + ") has completed successfully");
    }

    public static void verifyMetaData(BaseClassFlightdeckResponseBody.Meta meta) {
        logger.info("** Verifying response model object fields");
        Assert.assertNotNull(meta.getCurrentPage(), "currentPage is null");
        Assert.assertNotNull(meta.getPageCount(), "pageCount is null");
        Assert.assertNotNull(meta.getPageSize(), "pageSize is null");
        Assert.assertNotNull(meta.getTotalCount(), "totalCount is null");
        Assert.assertNotNull(meta.getTotalDisplayCount(), "totalDisplayCount is null");
        Assert.assertNotNull(meta.isSortAscending(), "sortAscending is null");
    }

    public List<Double> queryDatabaseAndGetColumn(String queryName, String date, String columnLabel) throws SQLException {
        ResultSet rs = executeQuery(queryName, date);

        List<Double> dataList = new ArrayList<>();
        while (rs.next()) {
            double value = rs.getDouble(columnLabel);
            dataList.add(value);
        }
        return dataList;
    }

    public <T> void compareData(List<T> items, String date, String query, boolean checkSpend, boolean checkClicks, boolean checkImpressions, boolean checkSales) {
        try {
            RedShiftUtility.connectToServer();

            List<Double> spendList = checkSpend ? queryDatabaseAndGetColumn(query, date, "spend") : null;
            List<Double> clicksList = checkClicks ? queryDatabaseAndGetColumn(query, date, "clicks") : null;
            List<Double> impressionsList = checkImpressions ? queryDatabaseAndGetColumn(query, date, "impressions") : null;
            List<Double> salesList = checkSales ? queryDatabaseAndGetColumn(query, date, "sales") : null;

            for (int i = 0; i < items.size(); i++) {
                T item = items.get(i);
                Double spend = null;
                Double clicks = null;
                Double impressions = null;
                Double sales = null;
                try {

                    spend = checkSpend ? convertValueToDouble("getSpend", item) : null;
                    clicks = checkClicks ? convertValueToDouble("getClicks", item) : null;
                    impressions = checkImpressions ? convertValueToDouble("getImpressions", item) : null;
                    sales = checkSales ? convertValueToDouble("getSales", item) : null;
                } catch (Exception e) {
                    logger.error("Exception getting values from response object. Exception: " + e.getMessage());
                    Assert.fail("Exception getting values from response object. Exception: " + e.getMessage());
                }

                double dbSpend = checkSpend ? spendList.get(i) : 0;
                double dbClicks = checkClicks ? clicksList.get(i) : 0;
                double dbImpressions = checkImpressions ? impressionsList.get(i) : 0;
                double dbSales = checkSales ? salesList.get(i) : 0;

                DecimalFormat df = new DecimalFormat("#");
                if (checkSpend) Assert.assertEquals(df.format(spend), df.format(dbSpend), "Spend value does not match");
                if (checkClicks)
                    Assert.assertEquals(df.format(clicks), df.format(dbClicks), "Clicks value does not match");
                if (checkImpressions)
                    Assert.assertEquals(df.format(impressions), df.format(dbImpressions), "Impressions value does not match");
                if (checkSales) Assert.assertEquals(df.format(sales), df.format(dbSales), "Sales value does not match");
            }

            logger.info("** Amounts match between API response and DB");

        } catch (Exception e) {
            logger.error("Exception running the query. Exception: " + e.getMessage());
            Assert.fail("Exception running the query. Exception: " + e.getMessage());
        } finally {
            RedShiftUtility.closeConnections();
        }
    }

    private <T> double convertValueToDouble(String method, T item) throws Exception {

        return Double.parseDouble((String) item.getClass().getMethod(method).invoke(item));
    }
}