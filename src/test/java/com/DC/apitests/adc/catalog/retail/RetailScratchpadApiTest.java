package com.DC.apitests.adc.catalog.retail;

import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail.RetailApiRequests;
import com.DC.utilities.apiEngine.apiServices.adc.catalog.retail.RetailScratchpadApiService;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.RetailScratchpadRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.*;
import com.DC.utilities.apiEngine.routes.adc.catalog.retail.RetailRoutes;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;
import java.util.List;

import static com.DC.apitests.ApiValidations.checkResponseStatus;
import static com.DC.apitests.ApiValidations.verifyEndpointReturnsCorrectObject;

public class RetailScratchpadApiTest extends BaseClass {

    private static Logger logger;
    private final String WEEKLY = "WEEKLY";

    RetailScratchpadApiTest() {
        logger = Logger.getLogger(RetailScratchpadApiTest.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    private String token;

    @BeforeClass
    public void setup() throws Exception {
        token = SecurityAPI.getTokenAPI();
    }

    @Test(groups = "NoDataProvider", description = "Scratchpad test: get Retail Scratchpad data")
    public void Api_Scratchpad_CanGetScratchpadData() throws Exception {

        logger.info("** Scratchpad test case (" + "RS_1" + ") has started.");

        String expectedFirstDayOfLastThirteenWeeks = DateUtility.getFirstDayOfLastThirteenWeeks();
        String expectedLastDayOfLastThirteenWeeks = DateUtility.getLastDayOfLastThirteenWeeks();

        RetailScratchpadRequestBody.DateRange dr = new RetailScratchpadRequestBody.DateRange("Last 13 Weeks");
        List<RetailScratchpadRequestBody.FilterString> fs = Arrays.asList(new RetailScratchpadRequestBody.FilterString("Retailer Platform", "AMAZON RETAIL"),new RetailScratchpadRequestBody.FilterString("Distributor View", "Manufacturing"), new RetailScratchpadRequestBody.FilterString("Business Units", "Performance Health East"));
        RetailScratchpadRequestBody rs = new RetailScratchpadRequestBody(
                new RetailScratchpadRequestBody.PagingAttributes(
                        100,
                        1
                ),
                true, WEEKLY, false, false, 1, "SHIPPED_COGS", dr, expectedFirstDayOfLastThirteenWeeks, expectedLastDayOfLastThirteenWeeks, false, 113,
                "PREMIUM", "VENDOR", false, fs, "yoy", 1988, "-1", "segmentation",Arrays.asList(), 3875, false, "AMAZON RETAIL", "Manufacturing" );

        Response response = RetailApiRequests.getScratchpadData(rs, RetailRoutes.RETAIL_SCRATCHPAD_ENDPOINT, token);

        RetailScratchpadResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), RetailScratchpadResponseBody.class);

        verifyCharData(responseModel.getchartData());

        List<RetailScratchpadResponseBody.MetricDateValues> metricDateAndValue = responseModel.getmetricDateValues();

        logger.info("Verifying metricsDataAndValues data");
        for (int i = 0; i < metricDateAndValue.size(); i++) {
            Assert.assertNotNull(metricDateAndValue.get(0).metric, "** Failure! Null value found in response body for MetricDateAndValue data");
        }
        logger.info("Verifying metricsDataAndValues data");
        List<RetailScratchpadResponseBody.MetricDateValues.DatesAndValues> DatesAndValues = metricDateAndValue.get(0).datesAndValues;
        for (int i = 0; i < DatesAndValues.size(); i++) {
            Assert.assertNotNull(DatesAndValues.get(i).date, "Date in DatesAndValues is null.");
            Assert.assertNotNull(DatesAndValues.get(i).value, "Date in DatesAndValues is null.");
        }

        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");

    }


    @Test(groups = "NoDataProvider", description = "Scratchpad test: get Retail Scratchpad Summary data")
    public void Api_Scratchpad_CanGetScratchpadSummaryData() throws Exception {

        logger.info("** Scratchpad test case (" + "RS_2" + ") has started.");

        SoftAssert softAssert = new SoftAssert();

        String expectedFirstDayOfLastFourWeeks = DateUtility.getFirstDayOfLastFourWeeks();
        String expectedLastDayOfLastFourWeeks = DateUtility.getLastDayOfLastFourWeeks();

        RetailScratchpadRequestBody.DateRange dr = new RetailScratchpadRequestBody.DateRange("Last 4 Weeks");
        List<RetailScratchpadRequestBody.FilterString> fs = Arrays.asList(new RetailScratchpadRequestBody.FilterString("Retailer Platform", "AMAZON RETAIL"),new RetailScratchpadRequestBody.FilterString("Distributor View", "Manufacturing"), new RetailScratchpadRequestBody.FilterString("Business Units", "Performance Health East"));
        RetailScratchpadRequestBody rs = new RetailScratchpadRequestBody(
                new RetailScratchpadRequestBody.PagingAttributes(
                        100,
                        1
                ),
                true, WEEKLY, false, false, 1, "SHIPPED_COGS", dr, expectedFirstDayOfLastFourWeeks, expectedLastDayOfLastFourWeeks, false, 39,
                "PREMIUM", "VENDOR", false, fs, "yoy", 1877, "-1", "segmentation",Arrays.asList(), 3829, false, "AMAZON RETAIL", "Manufacturing" );


        logger.info("Extracting response");
        Response response = RetailApiRequests.getScratchpadData(rs, RetailRoutes.RETAIL_SCRATCHPAD_SUMMARY_ENDPOINT, token);

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        List<SummaryResponseBody> scratchpadResponse = RetailScratchpadApiService.getRetailScratchpadResponseSummary(response, testMethodName.get());

        logger.info("Verifying summary data");
        for(int i = 0; i < scratchpadResponse.size(); i++) {
            List<String> summaryDataList = scratchpadResponse.get(i).summary.getSummary();
            for (int j = 1; j < summaryDataList.size(); j++) {
                softAssert.assertNotNull(scratchpadResponse.get(i).summary.getSummary(), "** Failure! Null value found in response body for Summary data");
            }
        }

        logger.info("Verifying slicer previous summary data");
        for (int i = 0; i < scratchpadResponse.size(); i++) {
            List<String> summaryDataList = scratchpadResponse.get(i).previousSummary.getPreviousSummary();
            for (int j = 0; j < summaryDataList.size(); j++) {
                softAssert.assertNotNull(scratchpadResponse.get(i).previousSummary.getPreviousSummary(), "** Failure! Null value found in response body for Previous Summary data");
            }
        }
        softAssert.assertAll();
        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");

    }

    @Test(groups = "NoDataProvider",  description = "Scratchpad test: get Retail Scratchpad Slicer Chart data")
    public void Api_Scratchpad_CanGetSlicerChartData() throws Exception {

        logger.info("** Scratchpad test case (" + "RS_3" + ") has started.");

        String expectedFirstDayOfLastFourWeeks = DateUtility.getFirstDayOfLastFourWeeks();
        String expectedLastDayOfLastFourWeeks = DateUtility.getLastDayOfLastFourWeeks();

        RetailScratchpadRequestBody.DateRange dr = new RetailScratchpadRequestBody.DateRange("Last 4 Weeks");
        List<RetailScratchpadRequestBody.FilterString> fs = Arrays.asList(new RetailScratchpadRequestBody.FilterString("Retailer Platform", "AMAZON RETAIL"),new RetailScratchpadRequestBody.FilterString("Distributor View", "Manufacturing"), new RetailScratchpadRequestBody.FilterString("Business Units", "Hersheys - US"));
        RetailScratchpadRequestBody rs = new RetailScratchpadRequestBody(
                new RetailScratchpadRequestBody.PagingAttributes(
                        100,
                        1
                ),
                true, WEEKLY, false, true, 1, "SHIPPED_COGS", dr, expectedFirstDayOfLastFourWeeks, expectedLastDayOfLastFourWeeks, false, 39,
                "PREMIUM", "VENDOR", false, fs, "yoy", 12896, "-1", "segmentation",Arrays.asList(), 3829, false, "AMAZON RETAIL", "Manufacturing" );


        Response response = RetailApiRequests.getRetailScratchpadData(rs, RetailRoutes.SLICER_CHART, token);

        SlicerChartResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), SlicerChartResponseBody.class);

        List<SlicerChartResponseBody.SlicerData> SlicerData = responseModel.getslicerData();
        logger.info("Verifying Slicer data");
        for (int i = 0; i < SlicerData.size(); i++) {
            Assert.assertNotNull(SlicerData.get(i).segmentationValue, "** Failure! Null value found in response body for Slicer data");
            Assert.assertNotNull(SlicerData.get(i).segmentationValueId, "** Failure! Null value found in response body for Slicer data");
        }

        List<SlicerChartResponseBody.SlicerData.Data> DataValues = SlicerData.get(0).data;
        logger.info("Verifying Data Values data");
        for (int i = 0; i < DataValues.size(); i++) {
            Assert.assertNotNull(DataValues.get(i).date, " Data Values date is null.");
            Assert.assertNotNull(DataValues.get(i).value, " Data Value is null.");
            Assert.assertNotNull(DataValues.get(i).percentage, " Data Value percentage is null.");
        }

        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");

    }

    @Test(groups = "NoDataProvider", description = "Scratchpad test: get Retail Scratchpad Slicer Grid data")
    public void Api_Scratchpad_CanGetSlicerGridData() throws Exception {

        logger.info("** Scratchpad test case (" + "RD_4" + ") has started.");

        String expectedFirstDayOfLastFourWeeks = DateUtility.getFirstDayOfLastFourWeeks();
        String expectedLastDayOfLastFourWeeks = DateUtility.getLastDayOfLastFourWeeks();

        RetailScratchpadRequestBody.DateRange dr = new RetailScratchpadRequestBody.DateRange("Last 4 Weeks");
        List<RetailScratchpadRequestBody.FilterString> fs = Arrays.asList(new RetailScratchpadRequestBody.FilterString("Retailer Platform", "AMAZON RETAIL"),new RetailScratchpadRequestBody.FilterString("Distributor View", "Manufacturing"), new RetailScratchpadRequestBody.FilterString("Business Units", "Hersheys - US"));
        RetailScratchpadRequestBody Requestbody = new RetailScratchpadRequestBody (
                new RetailScratchpadRequestBody.PagingAttributes(
                        100,
                        1
                ),
                true, WEEKLY, false, true, 1, "SHIPPED_COGS", dr, expectedFirstDayOfLastFourWeeks, expectedLastDayOfLastFourWeeks, false, 39,
                "PREMIUM", "VENDOR", false, fs, "yoy", 12896, "-1", "segmentation",Arrays.asList(), 3829, false, "AMAZON RETAIL", "Manufacturing" );

        Response response = RetailApiRequests.getRetailScratchpadData(Requestbody, RetailRoutes.SLICER_GRID, token);

        SlicerGridResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), SlicerGridResponseBody.class);

        logger.info("Verifying Meta data");
        verifyMetaData(responseModel.getMeta());

        List<SlicerGridResponseBody.Items> items = responseModel.items;
        logger.info("Verifying Items data");
        for (int i = 0; i < items.size(); i++) {
            List<String> ItemsData = responseModel.items.get(i).getItemsData();
            for (int j=0; j<ItemsData.size(); j++){
            Assert.assertNotNull(ItemsData.get(j),"Failure! Null value found in response body for Items data");
            }
        }

        for (SlicerGridResponseBody.Items item : items) {
            List<SlicerGridResponseBody.Items.Data> data = item.data;
            for (SlicerGridResponseBody.Items.Data element : data) {
                List<String> DataValues = element.getData();
                for (int j = 0; j < DataValues.size(); j++) {
                    Assert.assertNotNull(DataValues.get(j), "** Failure! Null value found in response body at index " + j + " of metric in item " + items.indexOf(item));
                }
            }
        }

        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");

    }

    @Test(groups = "NoDataProvider", description = "Scratchpad test: get Retail Scratchpad AggBu's data")
    public void Api_Scratchpad_CanGetScratchpadAggBusData() throws Exception {

        logger.info("** Scratchpad test case (" + "RS_5" + ") has started.");

        String expectedFirstDayOfLastFourWeeks = DateUtility.getFirstDayOfLastFourWeeks();
        String expectedLastDayOfLastFourWeeks = DateUtility.getLastDayOfLastFourWeeks();

        RetailScratchpadRequestBody.DateRange dr = new RetailScratchpadRequestBody.DateRange("Last 4 Weeks");
        List<RetailScratchpadRequestBody.FilterString> fs = Arrays.asList(new RetailScratchpadRequestBody.FilterString("Retailer Platform", "AMAZON RETAIL"), new RetailScratchpadRequestBody.FilterString("Distributor View", "Manufacturing"), new RetailScratchpadRequestBody.FilterString("Amazon Retail Accounts", "All Accounts"), new RetailScratchpadRequestBody.FilterString("Currency", "CAD"), new RetailScratchpadRequestBody.FilterString("Business Units", "Hersheys - CA,Hersheys - US"));
        RetailScratchpadRequestBody Requestbody = new RetailScratchpadRequestBody(
                new RetailScratchpadRequestBody.PagingAttributes(
                        100,
                        1
                ),
                true, WEEKLY, false, false, 1, "SHIPPED_COGS", dr, expectedFirstDayOfLastFourWeeks, expectedLastDayOfLastFourWeeks, false,
                "PREMIUM", "VENDOR", true, fs, "yoy", -1, "fwcustomasinmetadata1", "segmentation", 3886, false,"AMAZON RETAIL" ,"Manufacturing", "CAD");

        Response response = RetailApiRequests.getScratchpadAggbusData(Requestbody, RetailRoutes.SCRATCHPAD_AGG_BU, token,"x-businessunitcontext=191,39","x-currencycontext=USD");

        RetailScratchpadResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), RetailScratchpadResponseBody.class);

        verifyCharData(responseModel.getchartData());

        List<RetailScratchpadResponseBody.MetricDateValues> metricDateAndValue = responseModel.getmetricDateValues();

        logger.info("Verifying metricsDataAndValues data");
        for (int i = 0; i < metricDateAndValue.size(); i++) {
            Assert.assertNotNull(metricDateAndValue.get(0).metric, "** Failure! Null value found in response body for MetricDateAndValue data");
        }
        logger.info("Verifying metricsDataAndValues data");
        List<RetailScratchpadResponseBody.MetricDateValues.DatesAndValues> DatesAndValues = metricDateAndValue.get(0).datesAndValues;
        for (int i = 0; i < DatesAndValues.size(); i++) {
            Assert.assertNotNull(DatesAndValues.get(i).date, "Date in DatesAndValues is null.");
            Assert.assertNotNull(DatesAndValues.get(i).value, "Date in DatesAndValues is null.");
        }
        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");
    }

    @Test(groups = "NoDataProvider", description = "Scratchpad test: get Retail Scratchpad Summary Agg Bu's data")
    public void Api_Scratchpad_CanGetSummaryAggBusData() throws Exception {

        logger.info("** Scratchpad test case (" + "RS_6" + ") has started.");

        SoftAssert softAssert = new SoftAssert();

        String expectedFirstDayOfLastThirteenWeeks = DateUtility.getFirstDayOfLastThirteenWeeks();
        String expectedLastDayOfLastThirteenWeeks = DateUtility.getLastDayOfLastThirteenWeeks();

        RetailScratchpadRequestBody.DateRange dr = new RetailScratchpadRequestBody.DateRange("Last 4 Weeks");
        List<RetailScratchpadRequestBody.FilterString> fs = Arrays.asList(new RetailScratchpadRequestBody.FilterString("Retailer Platform", "AMAZON RETAIL"), new RetailScratchpadRequestBody.FilterString("Distributor View", "Manufacturing"), new RetailScratchpadRequestBody.FilterString("Amazon Retail Accounts", "All Accounts"), new RetailScratchpadRequestBody.FilterString("Currency", "CAD"), new RetailScratchpadRequestBody.FilterString("Business Units", "Hersheys - US,Hersheys - CA"));
        RetailScratchpadRequestBody Requestbody = new RetailScratchpadRequestBody(
                new RetailScratchpadRequestBody.PagingAttributes(
                        100,
                        1
                ),
                true, WEEKLY, false, false, 1, "SHIPPED_COGS", dr, expectedFirstDayOfLastThirteenWeeks, expectedLastDayOfLastThirteenWeeks, false,
                "PREMIUM", "VENDOR", true, fs, "yoy", -1, "fwcustomasinmetadata1", "segmentation", 3886, false,  "AMAZON RETAIL", "Manufacturing", "CAD");

        logger.info("Extracting response");
        Response response = RetailApiRequests.getScratchpadAggbusData(Requestbody, RetailRoutes.SUMMARY_AGG_BU, token,"x-businessunitcontext=191,39","x-currencycontext=USD");

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        List<SummaryResponseBody> scratchpadResponse = RetailScratchpadApiService.getRetailScratchpadResponseSummary(response, testMethodName.get());

        logger.info("Verifying summary data");
        for(int i = 0; i < scratchpadResponse.size(); i++) {
            List<String> summaryDataList = scratchpadResponse.get(i).summary.getSummary();
            for (int j = 1; j < summaryDataList.size(); j++) {
                softAssert.assertNotNull(scratchpadResponse.get(i).summary.getSummary(), "** Failure! Null value found in response body for Summary data");
            }
        }

        logger.info("Verifying slicer previous summary data");
        for (int i = 0; i < scratchpadResponse.size(); i++) {
            List<String> summaryDataList = scratchpadResponse.get(i).previousSummary.getPreviousSummary();
            for (int j = 0; j < summaryDataList.size(); j++) {
                softAssert.assertNotNull(scratchpadResponse.get(i).previousSummary.getPreviousSummary(), "** Failure! Null value found in response body for Previous Summary data");
            }
        }
        softAssert.assertAll();
        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");
    }

    @Test(groups = "NoDataProvider", description = "Scratchpad test: get Retail Scratchpad Slicer Chart Agg Bu's data")
    public void Api_Scratchpad_CanGetSlicerChartAggBusData() throws Exception {

        logger.info("** Scratchpad test case (" + "RS_7" + ") has started.");

        String expectedFirstDayOfLastFourWeeks = DateUtility.getFirstDayOfLastFourWeeks();
        String expectedLastDayOfLastFourWeeks = DateUtility.getLastDayOfLastFourWeeks();

        RetailScratchpadRequestBody.DateRange dr = new RetailScratchpadRequestBody.DateRange("Last 4 Weeks");
        List<RetailScratchpadRequestBody.FilterString> fs = Arrays.asList(new RetailScratchpadRequestBody.FilterString("Retailer Platform", "AMAZON RETAIL"), new RetailScratchpadRequestBody.FilterString("Distributor View", "Manufacturing"), new RetailScratchpadRequestBody.FilterString("Amazon Retail Accounts", "All Accounts"), new RetailScratchpadRequestBody.FilterString("Currency", "CAD"), new RetailScratchpadRequestBody.FilterString("Business Units", "Hersheys - US,Hersheys - CA"));
        RetailScratchpadRequestBody Requestbody = new RetailScratchpadRequestBody(
                new RetailScratchpadRequestBody.PagingAttributes(
                        100,
                        1
                ),
                true, WEEKLY, false, true, 1, "SHIPPED_COGS", dr, expectedFirstDayOfLastFourWeeks, expectedLastDayOfLastFourWeeks, false,
                "PREMIUM", "VENDOR", true, fs, "yoy", -1, "fwcustomasinmetadata1", "segmentation", 3886, false, "AMAZON RETAIL", "Manufacturing", "CAD");

        Response response = RetailApiRequests.getScratchpadAggbusData(Requestbody, RetailRoutes.SLICER_CHART_AGG_BU, token,"x-businessunitcontext=191,39","x-currencycontext=CAD");

        SlicerChartAggbusResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), SlicerChartAggbusResponseBody.class);

        List<SlicerChartAggbusResponseBody.SlicerData> slicerData = responseModel.getslicerData();
        logger.info("Verifying Slicer data");
        for (int i = 0; i < slicerData.size(); i++) {
            Assert.assertNotNull(slicerData.get(i).segmentationValueId, "** Failure! Null value found in response body for Slicer data");
            Assert.assertNotNull(slicerData.get(i).segmentationValue, "** Failure! Null value found in response body for Slicer data");
            Assert.assertNotNull(slicerData.get(i).businessUnitId,"** Failure! Null value found in response body for Slicer data");
            Assert.assertNotNull(slicerData.get(i).businessUnitName,"** Failure! Null value found in response body for Slicer data");
        }

        List<SlicerChartAggbusResponseBody.SlicerData.Data> DataValues = slicerData.get(0).data;
        logger.info("Verifying Data Values data");
        for (int i = 0; i < DataValues.size(); i++) {
            Assert.assertNotNull(DataValues.get(i).date, " Data Values date is null.");
            Assert.assertNotNull(DataValues.get(i).value, " Data Value is null.");
            Assert.assertNotNull(DataValues.get(i).percentage, " Data Value percentage is null.");
        }

        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");

    }

    @Test(groups = "NoDataProvider", description = "Scratchpad test: get Retail Scratchpad Report Health Agg Bu's Data")
    public void Api_Scratchpad_CanGetSlicerGridAggBusData() throws Exception {

        logger.info("** Scratchpad test case (" + "RS_8" + ") has started.");

        String expectedFirstDayOfLastFourWeeks = DateUtility.getFirstDayOfLastFourWeeks();
        String expectedLastDayOfLastFourWeeks = DateUtility.getLastDayOfLastFourWeeks();

        RetailScratchpadRequestBody.DateRange dr = new RetailScratchpadRequestBody.DateRange("Last 4 Weeks");
        List<RetailScratchpadRequestBody.FilterString> fs = Arrays.asList(new RetailScratchpadRequestBody.FilterString("Retailer Platform", "AMAZON RETAIL"), new RetailScratchpadRequestBody.FilterString("Distributor View", "Manufacturing"), new RetailScratchpadRequestBody.FilterString("Amazon Retail Accounts", "All Accounts"), new RetailScratchpadRequestBody.FilterString("Currency", "CAD"), new RetailScratchpadRequestBody.FilterString("Business Units", "Hersheys - US,Hersheys - CA"));
        RetailScratchpadRequestBody Requestbody = new RetailScratchpadRequestBody(
                new RetailScratchpadRequestBody.PagingAttributes(
                        100,
                        1
                ),
                true, WEEKLY, false, true, 1, "SHIPPED_COGS", dr, expectedFirstDayOfLastFourWeeks, expectedLastDayOfLastFourWeeks, false,
                "PREMIUM", "VENDOR", true, fs, "yoy", -1, "fwcustomasinmetadata1", "segmentation", 3886, false, "AMAZON RETAIL", "Manufacturing", "CAD");

        Response response = RetailApiRequests.getScratchpadAggbusData(Requestbody, RetailRoutes.SLICER_GRID_AGG_BU, token,"x-businessunitcontext=191,39","x-currencycontext=CAD");
        SlicerGridResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), SlicerGridResponseBody.class);

        logger.info("Verifying Meta data");
        verifyMetaData(responseModel.getMeta());

        List<SlicerGridResponseBody.Items> items = responseModel.items;
        logger.info("Verifying Items data");
        for (int i = 0; i < items.size(); i++) {
            List<String> ItemsData = responseModel.items.get(i).getItemsData();
            for (int j=0; j<ItemsData.size(); j++){
                Assert.assertNotNull(ItemsData.get(j),"Failure! Null value found in response body for Items data");
            }
        }

        for (SlicerGridResponseBody.Items item : items) {
            List<SlicerGridResponseBody.Items.Data> data = item.data;
            for (SlicerGridResponseBody.Items.Data element : data) {
                List<String> DataValues = element.getData();
                for (int j = 0; j < DataValues.size(); j++) {
                    Assert.assertNotNull(DataValues.get(j), "** Failure! Null value found in response body at index " + j + " of metric in item " + items.indexOf(item));
                }
            }
        }
        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");

    }


    public static void verifyMetaData(SlicerGridResponseBody.Meta meta) {
        logger.info("** Verifying response model object fields");
        Assert.assertNotNull(meta.getCurrentPage(), "Current page is null");
        Assert.assertNotNull(meta.getPageCount(), "page count is null");
        Assert.assertNotNull(meta.getPageSize(), "page size is null");
        Assert.assertNotNull(meta.getTotalCount(), "total count is null");
    }

    public static void verifyCharData(RetailScratchpadResponseBody.ChartData chartData) {
        logger.info("** Verifying response model object fields");
        Assert.assertNotNull(chartData.getdownloadDate(), "getdownloadDate is null");
        Assert.assertNotNull(chartData.getorderedRevenue(), "getorderedRevenue is null");
        Assert.assertNotNull(chartData.getorderedUnits(), "getorderedUnits is null");
        Assert.assertNotNull(chartData.getspend(), "getspend is null");
        Assert.assertNotNull(chartData.getclicks(), "getclicks is null");
        Assert.assertNotNull(chartData.getcpc(), "getcpc is null");
    }
}
