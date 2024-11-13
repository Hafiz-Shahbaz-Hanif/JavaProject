package com.DC.apitests.adc.catalog.retail;

import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail.RetailApiRequests;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.SalesCorrelationRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.*;
import com.DC.utilities.apiEngine.routes.adc.catalog.retail.RetailRoutes;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.DC.apitests.ApiValidations.checkResponseStatus;
import static com.DC.apitests.ApiValidations.verifyEndpointReturnsCorrectObject;

public class SalesCorrelationModelApiTest extends BaseClass {

    private static String token;
    private static final String expectedFirstDayOfLast6Month = DateUtility.getFirstDayOfLastSixMonths();
    private static final String expectedLastDayOfLastT6Month = DateUtility.getLastDayOfLastSixMonths();

    @BeforeClass
    public void setup() throws Exception {
        token = SecurityAPI.getTokenAPI();
    }

    @Test(groups = "NoDataProvider",description = "Sales Correlation Model : get SCM Detail Data")
    public static void SCM_ApiTest_1() throws Exception {
        LOGGER.info("** Sales Correlation test case ("+ testMethodName + ") has started.");

        SalesCorrelationRequestBody requestBody = new SalesCorrelationRequestBody("MONTHLY","PoP","PREMIUM",
                "VENDOR",true,expectedFirstDayOfLast6Month,expectedLastDayOfLastT6Month,3829,false,
                "Manufacturing","Brand","currency","shortHandCurrency",
                false,"AMAZON RETAIL",39,1662);

        Response response = RetailApiRequests.getSCMData(requestBody, RetailRoutes.RETAIL_SALESCORRELATION_DETAIL,token,"x-businessunitcontext=39","x-currencycontext=USD");

        LOGGER.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        LOGGER.info("** Deserializing the response");
        ScmDetailResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ScmDetailResponseBody.class);

        LOGGER.info("Verifying Shipped Revenue data");
        ScmDetailResponseBody.Metrics shippedRevenueDataList = responseModel.metrics;
        for(int i=0; i < shippedRevenueDataList.Shipped_Revenue.size(); i++) {
            Assert.assertNotNull(shippedRevenueDataList.Shipped_Revenue.get(i).getShippedRevenue(), "Null Value found in Shipped Revenue Data");
        }

        LOGGER.info("Verifying Conversion Rate data");
        ScmDetailResponseBody.Metrics conversionRateDataList = responseModel.metrics;
        for(int i=0; i < conversionRateDataList.Conversion_Rate.size(); i++) {
            Assert.assertNotNull(conversionRateDataList.Conversion_Rate.get(i).getConversionRate(), "Null Value found in Conversion Rate Data");
        }

        LOGGER.info("Verifying Ordered Revenue data");
        ScmDetailResponseBody.Metrics orderedRevenueDataList = responseModel.metrics;
        for(int i=0; i < orderedRevenueDataList.Ordered_Revenue.size(); i++) {
            Assert.assertNotNull(orderedRevenueDataList.Ordered_Revenue.get(i).getOrderedRevenue(), "Null Value found in Ordered Revenue  Data");
        }

        LOGGER.info("Verifying Average Sales Prices data");
        ScmDetailResponseBody.Metrics averageSalesPricesDataList = responseModel.metrics;
        for(int i=0; i < averageSalesPricesDataList.Average_Sales_Price.size(); i++) {
            Assert.assertNotNull(averageSalesPricesDataList.Average_Sales_Price.get(i).getAverageSalesPrice(), "Null Value found in Average Sales Prices Data");
        }

        LOGGER.info("Verifying Glance View data");
        ScmDetailResponseBody.Metrics glanceViewDataList = responseModel.metrics;
        for(int i=0; i < glanceViewDataList.Glance_Views.size(); i++) {
            Assert.assertNotNull(glanceViewDataList.Glance_Views.get(i).getGlanceViews(), "Null Value found in Glance View Data");
        }

        LOGGER.info("Verifying SP Spend data");
        ScmDetailResponseBody.Metrics spSpendDataList = responseModel.metrics;
        for(int i=0; i < spSpendDataList.SP_Spend.size(); i++) {
            Assert.assertNotNull(spSpendDataList.SP_Spend.get(i).getSPSpend(), "Null Value found in SP Spend Data");
        }
    }

    @Test(groups = "NoDataProvider",description = "Sales Correlation Model : get SCM Summary for Brand Data")
    public static void SCM_ApiTest_2() throws Exception {
        LOGGER.info("** Sales Correlation test case ("+ testMethodName + ") has started.");

        SalesCorrelationRequestBody requestBody = new SalesCorrelationRequestBody("MONTHLY","PoP","PREMIUM",
                "VENDOR",true,expectedFirstDayOfLast6Month,expectedLastDayOfLastT6Month,3829,false,
                "Manufacturing","Brand","currency","shortHandCurrency",
                false,"AMAZON RETAIL",39,1662);

        Response response = RetailApiRequests.getSCMData(requestBody,RetailRoutes.RETAIL_SALESCORRELATION_SUMMARY,token,"x-businessunitcontext=39","x-currencycontext=USD");

        LOGGER.info("** Deserializing the response");
        ScmSummaryForBrandResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ScmSummaryForBrandResponseBody.class);

        LOGGER.info("Verifying Date Range data");
        verifyDateRange(responseModel.getDateRange());

        ScmSummaryForBrandResponseBody.Metrics metricsDataList = responseModel.metrics;
        LOGGER.info("Verifying Shipped Revenue data");
        Assert.assertNotNull(metricsDataList.Shipped_Revenue.getCurrent(), "Null Value found in Shipped Revenue current Data");
        Assert.assertNotNull(metricsDataList.Shipped_Revenue.getPrevious(), "Null Value found in Shipped Revenue previous Data");

        LOGGER.info("Verifying Conversion Rate data");
        Assert.assertNotNull(metricsDataList.Conversion_Rate.getCurrent(), "Null Value found in Conversion Rate current Data");
        Assert.assertNotNull(metricsDataList.Conversion_Rate.getPrevious(), "Null Value found in Conversion Rate previous Data");

        LOGGER.info("Verifying Ordered Revenue data");
        Assert.assertNotNull(metricsDataList.Ordered_Revenue.getCurrent(), "Null Value found in Ordered Revenue current Data");
        Assert.assertNotNull(metricsDataList.Ordered_Revenue.getPrevious(), "Null Value found in Ordered Revenue previous Data");

        LOGGER.info("Verifying Glance View Revenue data");
        Assert.assertNotNull(metricsDataList.Glance_Views.getCurrent(), "Null Value found in Glance View current Data");
        Assert.assertNotNull(metricsDataList.Glance_Views.getPrevious(), "Null Value found in Glance View previous Data");

        LOGGER.info("Verifying Brand data");
        for (int i=0; i<responseModel.segmentedSales.BRAND.size();i++){
            List<String> segmentedSalesDataList = responseModel.segmentedSales.BRAND.get(i).getBrand();
            for (int j=0; j<segmentedSalesDataList.size();j++){
                Assert.assertNotNull(responseModel.segmentedSales.BRAND.get(i).getBrand(),"Null value found in Segmented Sales Brand");
            }
        }

        LOGGER.info("Verifying Segmented Ordered Revenues Brand data");
        for (int i =0; i<responseModel.segmentedOrderedRevenues.BRAND.size(); i++){
            List<String> brandDataList = responseModel.segmentedOrderedRevenues.BRAND.get(i).getBrand();
            for (int j=0; j<brandDataList.size(); j++){
                Assert.assertNotNull(responseModel.segmentedOrderedRevenues.BRAND.get(i).getBrand(),"Null value found in Segmented Ordered Revenue Brand");
            }
        }
    }

    @Test(groups = "NoDataProvider",description = "Sales Correlation Model : get SCM Summary for Category Data")
    public static void SCM_ApiTest_3() throws Exception {
        LOGGER.info("** Sales Correlation test case ("+ testMethodName + ") has started.");

        String expectedFirstDayOfLast6Month = DateUtility.getFirstDayOfLastSixMonths();
        String expectedLastDayOfLastT6Month = DateUtility.getLastDayOfLastSixMonths();

        SalesCorrelationRequestBody requestBody = new SalesCorrelationRequestBody("MONTHLY","PoP","PREMIUM",
                "VENDOR",true,expectedFirstDayOfLast6Month,expectedLastDayOfLastT6Month,3829,false,
                "Manufacturing","Category","currency","shortHandCurrency",
                false,"AMAZON RETAIL",39,1662);

        Response response = RetailApiRequests.getSCMData(requestBody,RetailRoutes.RETAIL_SALESCORRELATION_SUMMARY,token,"x-businessunitcontext=39","x-currencycontext=USD");

        LOGGER.info("** Deserializing the response");
        ScmSummaryForCategoryResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ScmSummaryForCategoryResponseBody.class);

        LOGGER.info("Verifying Date Ranges data");
        verifyDateRange(responseModel.getDateRange());

        ScmSummaryForCategoryResponseBody.MetricsCategory metricsDataList = responseModel.metrics;
        LOGGER.info("Verifying Shipped Revenue data");
        Assert.assertNotNull(metricsDataList.Shipped_Revenue.getCurrent(), "Null Value found in Shipped Revenue current Data");
        Assert.assertNotNull(metricsDataList.Shipped_Revenue.getPrevious(), "Null Value found in Shipped Revenue previous Data");

        LOGGER.info("Verifying Conversion Rate data");
        Assert.assertNotNull(metricsDataList.Conversion_Rate.getCurrent(), "Null Value found in Conversion Rate current Data");
        Assert.assertNotNull(metricsDataList.Conversion_Rate.getPrevious(), "Null Value found in Conversion Rate previous Data");

        LOGGER.info("Verifying Ordered Revenue data");
        Assert.assertNotNull(metricsDataList.Ordered_Revenue.getCurrent(), "Null Value found in Ordered Revenue current Data");
        Assert.assertNotNull(metricsDataList.Ordered_Revenue.getPrevious(), "Null Value found in Ordered Revenue previous Data");

        LOGGER.info("Verifying Glance View Revenue data");
        Assert.assertNotNull(metricsDataList.Glance_Views.getCurrent(), "Null Value found in Glance View current Data");
        Assert.assertNotNull(metricsDataList.Glance_Views.getPrevious(), "Null Value found in Glance View previous Data");


        LOGGER.info("Verifying Category data");
        for (int i=0; i<responseModel.segmentedSales.CATEGORY.size();i++){
            List<String> segmentedSalesDataList = responseModel.segmentedSales.CATEGORY.get(i).getCategory();
            for (int j=0; j<segmentedSalesDataList.size();j++){
                Assert.assertNotNull(responseModel.segmentedSales.CATEGORY.get(i).getCategory(),"Null value found in Segmented sales Category");
            }
        }

        LOGGER.info("Verifying Segmented Ordered Revenues Category data");
        for (int i =0; i<responseModel.segmentedOrderedRevenues.CATEGORY.size(); i++){
            List<String> CategoryDataList = responseModel.segmentedOrderedRevenues.CATEGORY.get(i).getCategory();
            for (int j=0; j<CategoryDataList.size(); j++){
                Assert.assertNotNull(responseModel.segmentedOrderedRevenues.CATEGORY.get(i).getCategory(),"Null Value found in Segmented Ordered Revenue Category");
            }
        }

    }

    @Test(groups = "NoDataProvider",description = "Sales Correlation Model : get SCM All Product Data")
    public static void SCM_ApiTest_4() throws Exception {
        LOGGER.info("** Sales Correlation test case ("+ testMethodName + ") has started.");

        SalesCorrelationRequestBody requestBody = new SalesCorrelationRequestBody(false, new SalesCorrelationRequestBody.PagingAttributes(100,1,"orderedRevenues"),
                true,39,"PREMIUM","VENDOR",true,3829,1662,"Manufacturing",expectedFirstDayOfLast6Month,expectedLastDayOfLastT6Month,
                "MONTHLY","AMAZON RETAIL","PoP");

        Response response = RetailApiRequests.getSCMData(requestBody,RetailRoutes.RETAIL_SALESCORRELATION_ALL,token,"x-businessunitcontext=39","x-currencycontext=USD");

        LOGGER.info("** Deserializing the response");
        ScmProductResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ScmProductResponseBody.class);

        LOGGER.info("Verifying Product data");
        for (int i = 0; i < responseModel.products.size(); i++) {
            List<String> productDataList = responseModel.products.get(i).getProduct();
            for (int j = 0; j < productDataList.size(); j++) {
                Assert.assertNotNull(responseModel.products.get(i).getProduct(), "** Failure! Null value found in response body for Product data");
            }
        }

        LOGGER.info("Verifying Meta data");
        verifyMetaData(responseModel.getMeta());

        LOGGER.info("Verifying Sales Impact data");
        for (int i=0; i<responseModel.salesImpacts.size(); i++){
            List<String> salesImpactDataList = responseModel.salesImpacts.get(i).getSalesImpact();
            for (int j=0; j< salesImpactDataList.size();j++){
                Assert.assertNotNull(responseModel.salesImpacts.get(i).getSalesImpact(),"Failure! Null value found in response body for Sales Impact data");
            }
        }
    }

    public static void verifyDateRange(ScmSummaryForBrandResponseBody.DateRange daterange){
        Assert.assertNotNull(daterange.getStartDate(),"Start Date is null");
        Assert.assertNotNull(daterange.getEndDate(),"End Date is null");

    }

    public static void verifyMetaData(BaseClassAsinSegmentationResponseBody.Meta meta) {
        Assert.assertNotNull(meta.getCurrentPage(), "Current page is null");
        Assert.assertNotNull(meta.getPageCount(), "page count is null");
        Assert.assertNotNull(meta.getPageSize(), "page size is null");
        Assert.assertNotNull(meta.getTotalCount(), "total count is null");
        Assert.assertNotNull(meta.getSortAttribute(),"Sort Attribute is null");
    }
}
