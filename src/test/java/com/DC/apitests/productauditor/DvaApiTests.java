package com.DC.apitests.productauditor;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.insights.ProductAuditorApiRequests;
import com.DC.utilities.apiEngine.models.requests.insights.CreateProductAuditorExportRequestBody;
import com.DC.utilities.apiEngine.models.requests.insights.CreateProductAuditorRequestBody;
import com.DC.utilities.apiEngine.models.responses.insights.AuditResult;
import com.DC.utilities.apiEngine.models.responses.insights.AuditRow;
import com.DC.utilities.apiEngine.models.responses.insights.DVAData;
import com.DC.utilities.apiEngine.models.responses.insights.DVAExportData;
import com.DC.utilities.apiEngine.routes.insights.ProductAuditorRoutes;
import com.DC.utilities.enums.Enums;
import com.amazon.redshift.shaded.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.*;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.utilities.CommonApiMethods.callEndpoint;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;

public class DvaApiTests extends BaseClass {

    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    private static String jwt;

    private static final ArrayList<String> propertiesToNotTest = new ArrayList<>(
            Arrays.asList("Retailer Domain", "Brand", "Company Name", "Unique ID", "Quantity of SOT Images", "Product Status", "Scraped Zipcode", "Scraped Date",
                    "SOT Upload Date", "Retailer", "RPC", "Parent RPC", "Video", ""));

    private static final CreateProductAuditorRequestBody sotReqBody = new CreateProductAuditorRequestBody(
            "https://os-media-service.s3.amazonaws.com/qa/product_auditor/SOTAmazonDVANoImages.xlsx",
            Enums.ProductAuditorType.SOT
    );

    private static final CreateProductAuditorExportRequestBody sotReqExportBody = new CreateProductAuditorExportRequestBody(
            "https://os-media-service.s3.amazonaws.com/qa/product_auditor/SOTAmazonDVA.xlsx",
            Enums.ProductAuditorType.SOT,
            true
    );

    private static final CreateProductAuditorRequestBody sotApostropheAmpersandsFileBody = new CreateProductAuditorRequestBody(
            "https://os-media-service.s3.amazonaws.com/qa/product_auditor/DVA_ApostropheAmpersand_Test.xlsx",
            Enums.ProductAuditorType.SOT
    );

    @BeforeGroups("DVAAPITests")
    public void setupTests() throws Exception {
        LOGGER.info("Setting up DVA api tests");
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
    }

    @Test(groups = {"ProductAuditorTests", "DVAAPITests"})
    public void Api_ProductAuditor_ReturnsCorrectObjectWithSOTFile() throws Exception {
        Response response = ProductAuditorApiRequests.performSOTAudit(sotReqBody, jwt);
        DVAData[] auditReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), DVAData[].class);
        AuditResult[] auditResult = auditReturned[0].allRetailers;
        ArrayList<LinkedHashMap<String, String>> hashMapList = getHashMapListFromAuditResult(auditResult);
        for (LinkedHashMap<String, String> auditRow : hashMapList) {
            String sotHeader = auditRow.get("sotHeader");
            if (!propertiesToNotTest.contains(sotHeader) && !sotHeader.contains("Match Percent") && !sotHeader.contains("Image")) {
                String pdpValue = SharedMethods.convertObjectToEmptyStringIfNull(auditRow.get("pdpValue"));
                String sotValue = SharedMethods.convertObjectToEmptyStringIfNull(auditRow.get("sotValue"));
                verifyHasValueAuditResultsDisplaysCorrectValue(auditRow, sotValue, "sotHasValue");
                verifyHasValueAuditResultsDisplaysCorrectValue(auditRow, pdpValue, "pdpHasValue");
                verifyValueCountDisplaysCorrectValue(auditRow, sotValue, "sotValueCount");
                verifyValueCountDisplaysCorrectValue(auditRow, pdpValue, "pdpValueCount");
                if (pdpValue.equals(sotValue)) {
                    verifyAuditResultsReturnCorrectMatchStatus(auditRow, true);
                } else {
                    verifyAuditResultsReturnCorrectMatchStatus(auditRow, false);
                }
            }
        }
    }

    @Test(groups = {"ProductAuditorTests", "DVAAPITests"})
    public void Api_ProductAuditor_AuditTextWithApostrophesOrAmpersandsDoesNotAffectMatchStatus() throws Exception {
        Response response = ProductAuditorApiRequests.performSOTAudit(sotApostropheAmpersandsFileBody, jwt);
        DVAData[] auditReturned = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), DVAData[].class);
        AuditResult[] auditResult = auditReturned[0].allRetailers;
        AuditRow auditRowWithAmpersands = auditResult[0].bulletPoint4;
        AuditRow auditRowWithApostrophe = auditResult[0].title;
        Assert.assertFalse(auditRowWithAmpersands.sotValue.contains("&amp;"), "Ampersands were not converted to &amp; in SOT value.");
        Assert.assertEquals(auditRowWithAmpersands.auditResult, "Match", "Audit result did not return match when ampersands was used in both SOT and PDP values.");
        Assert.assertEquals(auditRowWithApostrophe.auditResult, "Match", "Audit result did not return match when apostrophe was used in both SOT and PDP values.");
    }

    @Test(groups = {"ProductAuditorTests", "DVAAPITests"})
    public void Api_ProductAuditorExportReturnsCorrectObjectWithSOTFile() throws Exception {
        Response response = ProductAuditorApiRequests.performSOTAuditExport(sotReqExportBody, jwt);
        DVAExportData dvaExportData = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), DVAExportData.class);
    }

    @Test(groups = {"ProductAuditorTests", "DVAAPITests"})
    public void Api_ProductAuditor_CannotSubmitIfWorkbookFieldIsInvalidFileType() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"importWorkbook\": \"https://media.dev.onespace.com/assets/3c25494b-9e27-4148-abc7-d9950cb36524/testing_meme13.jpg\",\n" +
                "    \"auditType\": \"source-of-truth\"\n" +
                "}";
        Response response = callEndpoint(ProductAuditorRoutes.getProductAuditorHost(), jwt, "POST", bodyWithInvalidParameters, "");
        validateInternalServerError(response);
    }

    @Test(groups = {"ProductAuditorTests", "DVAAPITests"})
    public void Api_ProductAuditor_CannotSubmitIfAuditTypeFieldIsEmpty() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"importWorkbook\": \"https://os-media-service.s3.amazonaws.com/development/OneSpaceTest/Product%20Auditor/AmazonSOT_Import_QA.xlsx\",\n" +
                "    \"auditType\": \"\"\n" +
                "}";
        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"auditType\" is not allowed to be empty");
        expectedErrors.add("\"auditType\" must be one of [source-of-truth, retailer-capabilities, retailer-scraped-data]");
        verifyErrorIsThrownIfResponseBodyIsInvalid(bodyWithInvalidParameters, expectedErrors);
    }

    @Test(groups = {"ProductAuditorTests", "DVAAPITests"})
    public void Api_ProductAuditor_CannotSubmitIfAuditTypeFieldHasInvalidData() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"importWorkbook\": \"https://os-media-service.s3.amazonaws.com/development/OneSpaceTest/Product%20Auditor/AmazonSOT_Import_QA.xlsx\",\n" +
                "    \"auditType\": \"123\"\n" +
                "}";
        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"auditType\" must be one of [source-of-truth, retailer-capabilities, retailer-scraped-data]");
        verifyErrorIsThrownIfResponseBodyIsInvalid(bodyWithInvalidParameters, expectedErrors);
    }

    @Test(groups = {"ProductAuditorTests", "DVAAPITests"})
    public void Api_ProductAuditor_CannotSubmitIfResponseBodyDoesNotHaveAuditType() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"importWorkbook\": \"https://os-media-service.s3.amazonaws.com/development/OneSpaceTest/Product%20Auditor/AmazonSOT_Import_QA.xlsx\"\n" +
                "}";
        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"auditType\" is required");
        verifyErrorIsThrownIfResponseBodyIsInvalid(bodyWithInvalidParameters, expectedErrors);
    }

    @Test(groups = {"ProductAuditorTests", "DVAAPITests"})
    public void Api_ProductAuditor_CannotSubmitIfWorkbookIsEmpty() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"importWorkbook\": \"\",\n" +
                "    \"auditType\": \"source-of-truth\"\n" +
                "}";
        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"importWorkbook\" is not allowed to be empty");
        verifyErrorIsThrownIfResponseBodyIsInvalid(bodyWithInvalidParameters, expectedErrors);
    }

    private void verifyErrorIsThrownIfResponseBodyIsInvalid(String bodyWithInvalidParameters, List<String> expectedErrors) throws Exception {
        Response response = callEndpoint(ProductAuditorRoutes.getProductAuditorHost(), jwt, "POST", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    public ArrayList<LinkedHashMap<String, String>> getHashMapListFromAuditResult(AuditResult[] auditResult) {
        HashMap<String, LinkedHashMap<String, String>> auditResults = new ObjectMapper().convertValue(auditResult[0], HashMap.class);
        Set<String> auditResultsKeys = auditResults.keySet();

        ArrayList<LinkedHashMap<String, String>> hashMapList = new ArrayList<>();
        for (String key : auditResultsKeys) {
            LinkedHashMap<String, String> auditHashMap = auditResults.get(key);
            hashMapList.add(auditHashMap);
        }
        return hashMapList;
    }

    public void verifyAuditResultsReturnCorrectMatchStatus(LinkedHashMap<String, String> auditRow, Boolean isMatch) {
        if (isMatch) {
            Assert.assertEquals(auditRow.get("auditResult"), "Match", auditRow.get("sotValue") + "and" + auditRow.get("pdpValue") + " are equal but audit result returned No Match");
            Assert.assertEquals(auditRow.get("valueCountMatch"), "Match", auditRow.get("sotValue") + "value count and" + auditRow.get("pdpValue") + " value count are equal but audit result returned Match");
        } else {
            Assert.assertEquals(auditRow.get("auditResult"), "No Match", auditRow.get("sotValue") + "and" + auditRow.get("pdpValue") + " are not equal but audit result returned Match");
            Assert.assertEquals(auditRow.get("valueCountMatch"), "No Match", auditRow.get("sotValue") + "value count and" + auditRow.get("pdpValue") + " value count are not equal but audit result returned Match");
        }
    }

    public void verifyHasValueAuditResultsDisplaysCorrectValue(LinkedHashMap<String, String> auditRow, String valueToTest, String hasValueRow) {
        if (!Objects.equals(valueToTest, "") && !Objects.equals(valueToTest, "[]")) {
            Assert.assertTrue(Boolean.parseBoolean(auditRow.get(hasValueRow)), valueToTest + "was not null but has value was false");
        } else {
            Assert.assertFalse(Boolean.parseBoolean(auditRow.get(hasValueRow)), valueToTest + "was null but has value was true");
        }
    }

    public void verifyValueCountDisplaysCorrectValue(LinkedHashMap<String, String> auditRow, String valueToTest, String valueCountRow) {
        Object valueCount = auditRow.get(valueCountRow);
        Assert.assertEquals(valueCount, valueToTest.length(), "Value count in audit result was not equal to character count of" + valueToTest);
    }


}
