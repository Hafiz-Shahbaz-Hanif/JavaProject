package com.DC.apitests.cpgServer;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.testcases.BaseClass;
import com.DC.utilities.CsvUtility;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.hub.insights.HubInsightsCpgServerApiRequest;
import com.DC.utilities.apiEngine.apiRequests.insights.CPGAccount.CPGAccountRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.DC.apitests.ApiValidations.checkResponseStatus;
import static com.DC.utilities.SecurityAPI.changeInsightsCompanyAndGetJwt;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;

public class CpgServerApiTests extends BaseClass {
    private static String jwt;
    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    @BeforeClass()
    public void setupTests() throws Exception {
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        jwt = changeInsightsCompanyAndGetJwt(jwt, TEST_CONFIG.companyID, TEST_CONFIG.companyName);
    }

    @Test(groups = {"CpgServerApiTests", "NoDataProvider"})
    public void Api_CpgServer_CanExportLaunchFile() throws Exception {
        JSONObject firstObject = generateRandomBatchInput(List.of(SharedMethods.generateUUID(), SharedMethods.generateUUID()));
        JSONObject secondObject = generateRandomBatchInput(List.of(SharedMethods.generateUUID(), SharedMethods.generateUUID()));

        var payload = new JSONObject().put("batchName", "Batch_" + SharedMethods.generateRandomNumber())
                .put("batchInputs", List.of(firstObject, secondObject))
                .put("additionalBatchInputHeaders", List.of("Unique_id", "Product_Ids"));

        var response = HubInsightsCpgServerApiRequest.exportLaunchFile(payload, jwt);
        checkResponseStatus(testMethodName.get(), "200", response.getStatusCode());

        var filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/" + payload.get("batchName") + ".csv";
        SharedMethods.downloadFileFromUrl(response.getBody().jsonPath().getString("url"), filePath);

        List<String> expectedColumns = new ArrayList<>();
        expectedColumns.add("Topic");
        expectedColumns.add("Title");
        expectedColumns.add("Subject");
        var additionalHeaders = payload.getJSONArray("additionalBatchInputHeaders").toList().stream().map(Object::toString).collect(Collectors.toList());
        expectedColumns.addAll(additionalHeaders);

        List<List<String>> expectedRows = new ArrayList<>();
        expectedRows.add(expectedColumns);

        for (int i = 0; i < payload.getJSONArray("batchInputs").length(); i++) {
            var batchInput = payload.getJSONArray("batchInputs").getJSONObject(i);

            List<String> expectedRow = new ArrayList<>();
            expectedRow.add(batchInput.get("Topic").toString());
            expectedRow.add(batchInput.get("Title").toString());
            expectedRow.add(batchInput.get("Subject").toString());
            var additionalValues = batchInput.getJSONArray("additionalBatchInputValues").toList().stream().map(Object::toString).collect(Collectors.toList());
            expectedRow.addAll(additionalValues);
            expectedRows.add(expectedRow);
        }

        var rowsInExportedFile = CsvUtility.getContentOfAllRows(filePath);

        Assert.assertEquals(rowsInExportedFile, expectedRows, "The rows in the exported file doesn't match the batchInput in the payload" +
                "\nExpected rows: " + expectedRows +
                "\nActual rows: " + rowsInExportedFile);
    }

    @Test(groups = {"CpgServerApiTests", "NoDataProvider"}, enabled = false, description = "This test is disabled for now until we find a way to get token to cleanup batch after test")
    public void Api_CpgServer_CanStageBatchFile() throws Exception {
        var prod = ProductVersioningApiService.getProductWithUniqueIdIfExist("QA-STATIC-PRODUCT-001", jwt);
        var projectIdAndChain = getProjectAndChainIdToTest();
        var payload = new JSONObject();

        JSONObject firstObject = generateRandomBatchInput(List.of(prod.variantSets.live.get(0).instances.global.id));

        var swfCompanyId = CPGAccountRequests.getCompanyInfo(TEST_CONFIG.companyID, jwt).getBody().jsonPath().getString("swfCompanyId");

        var uploadData = new JSONObject()
                .put("batchName", "Batch_" + SharedMethods.generateRandomNumber())
                .put("batchInputs", List.of(firstObject))
                .put("additionalBatchInputHeaders", List.of("Unique_Id", "Product_Ids"))
                .put("companyId", swfCompanyId);

        payload.put("uploadData", uploadData);
        payload.put("chainId", projectIdAndChain.get("chainId"));
        payload.put("projectId", projectIdAndChain.get("projectId"));
        payload.put("releaseBatch", false);

        var response = HubInsightsCpgServerApiRequest.stageOrReleaseBatch(payload, jwt);
        checkResponseStatus(testMethodName.get(), "200", response.getStatusCode());

        Assert.assertEquals(response.jsonPath().getString("status"), "staged", "The batch file status was not staged");
    }

    private JSONObject generateRandomBatchInput(List<String> productIds) {

        var randomNumber = SharedMethods.generateRandomNumber();

        var batchInputsObject = new JSONObject();
        batchInputsObject.put("Topic", "Test topic " + randomNumber);
        batchInputsObject.put("Title", "Test title " + randomNumber);
        batchInputsObject.put("Subject", "Test subject " + randomNumber);

        var additionalValuesArray = new ArrayList<>();
        additionalValuesArray.add("UNIQUE-ID-" + randomNumber);
        var productIdsString = productIds.stream().map(id -> "\"" + id + "\"").collect(Collectors.joining(","));
        additionalValuesArray.add("{\"productIds\":[" + productIdsString + "]}");

        return batchInputsObject.put("additionalBatchInputValues", additionalValuesArray);
    }

    // PV Content Create - One Review is the project name
    private Map<String, String> getProjectAndChainIdToTest() {
        switch (TEST_CONFIG.environment) {
            case "development":
                return Map.of("projectId", "20680", "chainId", "01919f40-5e14-11ee-9e42-73cbba6f2735");
            case "qa":
                return Map.of("projectId", "13325", "chainId", "014f12c0-5e14-11ee-98f8-eb2fb0231593");
            case "staging":
                return Map.of("projectId", "1976", "chainId", "fc2edd70-9604-11ee-9583-97b65b99c841");
            default:
                throw new IllegalArgumentException("Invalid environment: " + TEST_CONFIG.environment);
        }
    }
}
