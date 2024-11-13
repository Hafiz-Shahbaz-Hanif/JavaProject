package com.DC.apitests.adc.catalog.retail;

import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail.RetailApiRequests;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.AsinSegmentationRequestbody;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.CreateAsinRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.DeleteAsinRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.catalog.retail.*;
import com.DC.utilities.apiEngine.routes.adc.catalog.retail.RetailRoutes;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static com.DC.apitests.ApiValidations.*;

public class AsinSegmentationApiTest extends BaseClass {

    private static Logger logger;
    private String token;

    AsinSegmentationApiTest() {
        logger = Logger.getLogger(AsinSegmentationApiTest.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    @BeforeClass
    public void setup() throws Exception {
        token = SecurityAPI.getTokenAPI();
    }

    @Test(groups = "NoDataProvider", description = "Asin Segmentation test: get Asin Segmentation data")
    public void Api_AsinSegmentation_CanGetData() throws Exception {

        logger.info("** Asin Segmentation test case ("+ testMethodName + ") has started.");

        AsinSegmentationRequestbody requestBody = new AsinSegmentationRequestbody (
                new AsinSegmentationRequestbody.PagingAttributes(100,1,"releaseDate"),
                39,"HYBRID",true,false,3829);

        Response response = RetailApiRequests.getAsinSegmentationData(requestBody, token);

        AsinSegmentationResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), AsinSegmentationResponseBody.class);

        logger.info("Verifying Asin Segmentation Meta data");
        verifyMetaData(responseModel.getMeta());

        logger.info("Verifying Items data");
        for (int i = 0; i < responseModel.items.size(); i++) {
            List<String> itemsData = responseModel.items.get(i).getItemsData();
            for (String itemsDatum : itemsData) {
                Assert.assertNotNull(itemsDatum, "Failure! Null value found in response body for Items data");
            }
        }
        logger.info("** Execution for test case ("+ testMethodName + ") has completed successfully");
    }

    @Test(groups = "NoDataProvider", description = "Asin Segmentation test: get Asin Segmentation Po Golden data")
    public void Api_AsinSegmentation_CanGetGoldenData() throws Exception {

        logger.info("** Asin Segmentation test case (" + testMethodName + ") has started.");

        AsinSegmentationRequestbody requestBody = new AsinSegmentationRequestbody (
                new AsinSegmentationRequestbody.PagingAttributes(100,1,"releaseDate"),
                39,"HYBRID",false,false,3829,false);

        Response response = RetailApiRequests.getAsinSegmentationPoGoldenData(requestBody, token);

        logger.info("** Deserializing the response");
        AsinSegmentationPoGoldenDataResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), AsinSegmentationPoGoldenDataResponseBody.class);

        logger.info("Verifying Asin Segmentation PO Golden Meta data");
        verifyMetaData(responseModel.getMeta());

        logger.info("Verifying Items data");
        for (int i = 0; i < responseModel.items.size(); i++) {
            List<String> itemsData = responseModel.items.get(i).getItemsData();
            for (String itemsDatum : itemsData) {
                Assert.assertNotNull(itemsDatum, "Failure! Null value found in response body for Items data");
            }
        }
        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");
    }

    @Test(groups = "NoDataProvider", description = "Asin Segmentation test: get Asin Segmentation Master data")
    public void Api_AsinSegmentation_CanGetMasterData() throws Exception {

        logger.info("** Asin Segmentation test case (" + testMethodName + ") has started.");

        AsinSegmentationRequestbody requestBody = new AsinSegmentationRequestbody (
                new AsinSegmentationRequestbody.PagingAttributes(100,1,"releaseDate"),
                39,"PREMIUM",false,false,3829,false);

        Response response = RetailApiRequests.getAsinSegmentationMasterData(requestBody, token);

        AsinSegmentationMasterDataResponseBody responseModel = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), AsinSegmentationMasterDataResponseBody.class);

        logger.info("Verifying Meta data");
        verifyMetaData(responseModel.getMeta());

        logger.info("Verifying Items data");
        for (int i = 0; i < responseModel.items.size(); i++) {
            List<String> itemsData = responseModel.items.get(i).getItemsData();
            for (String itemsDatum : itemsData) {
                Assert.assertNotNull(itemsDatum, "Failure! Null value found in response body for Items data");
            }
        }
        logger.info("** Execution for test case (" + testMethodName+ ") has completed successfully");
    }

    @Test(groups = "NoDataProvider", description = "Asin Segmentation test: Create Asin and Delete Asin")
    public void Api_AsinSegmentation_CanCreateAndDeleteSegment() throws Exception {

        logger.info("** Asin Segmentation test case (" + testMethodName + ") has started.");

        String segmentationTypeId = "1874";
        String segmentationValue = "AsinTest";
        int asinSegmentationId = 1874;

        logger.info("Request for Creating the New ASIN");
        CreateAsinRequestBody requestBody = new CreateAsinRequestBody (segmentationTypeId,segmentationValue,asinSegmentationId);

        Response response = RetailApiRequests.getCreateAsinData(requestBody, token);
        CreateAndDeleteAsinResponseBody responseModel = verifyEndpointReturnsCorrectCreatedObject(response, testMethodName.get(), CreateAndDeleteAsinResponseBody.class);

        logger.info("Verify Created Asin Attributes Value");
        CreateAndDeleteAsinResponseBody.verifyCreatedAsinData(responseModel);

        logger.info("Storing the Created Asin Value");
        int asinId = responseModel.getId();

        logger.info("Request for Deleting the Newly Created ASIN");
        DeleteAsinRequestBody deleteRequestBody = new DeleteAsinRequestBody (asinId,asinSegmentationId,segmentationValue,"Brand",true,false);

        Response deleteResponse = RetailApiRequests.getDeleteAsinData(deleteRequestBody, RetailRoutes.CREATE_ASIN, token);
        CreateAndDeleteAsinResponseBody deleteResponseModel = verifyEndpointReturnsCorrectObject(deleteResponse, testMethodName.get(), CreateAndDeleteAsinResponseBody.class);

        logger.info("Verify Deleted Asin Attributes Value");
        CreateAndDeleteAsinResponseBody.verifyDeletedAsinData(deleteResponseModel);

        logger.info("** Execution for test case (" + testMethodName + ") has completed successfully");
    }

    public static void verifyMetaData(BaseClassAsinSegmentationResponseBody.Meta meta) {
        Assert.assertNotNull(meta.getCurrentPage(), "Current page is null");
        Assert.assertNotNull(meta.getPageCount(), "page count is null");
        Assert.assertNotNull(meta.getPageSize(), "page size is null");
        Assert.assertNotNull(meta.getTotalCount(), "total count is null");
        Assert.assertNotNull(meta.getSortAttribute(),"Sort Attribute is null");
    }
}
