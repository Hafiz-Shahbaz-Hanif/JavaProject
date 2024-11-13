package com.DC.apitests.adc.catalog.search.ShareOfVoice;

import com.DC.objects.CommonBase;
import com.DC.objects.catalog.SearchBase;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.search.SearchApiRequests;
import com.DC.utilities.apiEngine.apiServices.adc.catalog.search.ShareOfVoiceApiServices;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.search.SearchOfVoiceRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.search.ShareOfVoiceResponseBody;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static com.DC.apitests.ApiValidations.checkResponseStatus;

public class BrandApiTest extends BaseClass {
    private static Logger logger;

    static List<String> keywordsList = SharedMethods.createList(SearchBase.ShareOfVoiceObjects.AMP_BREAKER_KEYWORDS);
    static List<String> placementList = SharedMethods.createList(SearchBase.ShareOfVoiceObjects.SP_ATF_PLACEMENT);
    static SearchOfVoiceRequestBody.DateRange dateRangeLabel = new SearchOfVoiceRequestBody.DateRange(CommonBase.LAST_14_DAYS_LABEL);
    static List<String> aggregationIntervalList = SharedMethods.createList("Monthly,Weekly,Daily");

    private SearchOfVoiceRequestBody ADD_SOV_BRAND_REQUEST = ShareOfVoiceApiServices.shareOfVoiceBrandRequestBody(dateRangeLabel, DateUtility.getFirstDayOfLastFourteenDays(),
            DateUtility.getYesterday(), SearchBase.ShareOfVoiceObjects.NON_WEIGHTED_REPORT_TYPE, placementList, keywordsList);

    BrandApiTest() {
        logger = Logger.getLogger(BrandApiTest.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    @Test(groups = "NoDataProvider", description = "Share Of Voice Brand API test - Verifying brand sov data")
    public void Share_Of_Voice_Brand_Api_Test() throws Exception {

        SoftAssert softAssert = new SoftAssert();

        logger.info("Extracting response");
        Response response = SearchApiRequests.sovBrand(ADD_SOV_BRAND_REQUEST, SecurityAPI.getTokenAPI());

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        ShareOfVoiceResponseBody brandResponse = ShareOfVoiceApiServices.getShareOfVoiceBrandResponse(response, testMethodName.get());

        logger.info("Verifying Sov and Brand Sov data");
        for (int i = 0; i < brandResponse.brandSov.sovData.size(); i++) {
            softAssert.assertNotNull(brandResponse.brandSov.sovData.get(i).getSovDataParameters(), "** Failure! Null value found in response body for Sov data");
            softAssert.assertEquals(brandResponse.brandSov.sovData.get(i).placement, SearchBase.ShareOfVoiceObjects.SP_ATF_PLACEMENT, "Placement field is not correct");
            softAssert.assertEquals(brandResponse.brandSov.sovData.get(i).searchQuery, SearchBase.ShareOfVoiceObjects.AMP_BREAKER_KEYWORDS, "Search Query field is not correct");
            for (int j = 0; j < brandResponse.brandSov.sovData.get(i).brandSovData.size(); j++) {
                softAssert.assertNotNull(brandResponse.brandSov.sovData.get(i).brandSovData.get(j).getbrandSovData(), "** Failure! Null value found in response body for Brand Sov data");
            }
        }

        softAssert.assertAll();
    }

    @Test(groups = "NoDataProvider", description = "Share Of Voice Brand API test - Verifying fetching of amazon platfom data for aggregation intervals")
    public void Share_Of_Voice_Brand_Amazon_Api_Test() throws Exception {

        SoftAssert softAssert = new SoftAssert();

        logger.info("Extracting response");
        Response response = SearchApiRequests.sovBrandForAmazon(SecurityAPI.getTokenAPI());

        logger.info("Verifying response");
        checkResponseStatus(testMethodName.get(), HttpStatus.SC_OK, response.statusCode());

        logger.info("** Deserializing the response");
        List<ShareOfVoiceResponseBody> brandResponse = ShareOfVoiceApiServices.getShareOfVoiceBrandForPlatformResponse(response, testMethodName.get());

        logger.info("Verifying fetching of aggregation interval data for Amazon platform");
        for (int i = 0; i < brandResponse.size(); i++) {
            softAssert.assertNotNull(brandResponse.get(i).getAggregationIntervalForAmazonPlatform(), "** Failure! Null value found in response body for Aggregation Interval data");
            softAssert.assertTrue(SharedMethods.hasMatchingSubstring(brandResponse.get(i).aggregationInterval, aggregationIntervalList), "** Failure! Aggregation Interval is not found in response body for data");
        }

        softAssert.assertAll();
    }
}
