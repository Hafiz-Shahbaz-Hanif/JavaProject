package com.DC.apitests.productversioning.exports;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.CompanyCollection;
import com.DC.db.productVersioning.ProductMasterCollection;
import com.DC.objects.productVersioning.ExportRecord;
import com.DC.objects.productVersioning.ProductKeywordsDataInExcel;
import com.DC.testcases.BaseClass;
import com.DC.tests.sharedAssertions.ExportCoreAssertions;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.apitests.ApiValidations.validateInvalidRequestParametersError;
import static com.DC.tests.sharedAssertions.ExportCoreAssertions.verifyErrorInExportRecordContainsExpectedMessage;
import static com.DC.tests.sharedAssertions.ExportCoreAssertions.verifyResponseBodyHasExpectedError;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.SharedMethods.downloadFileFromUrl;
import static com.DC.utilities.XLUtils.ProductKeywordsXLUtils.EXPECTED_HEADERS_IN_EXPORT_FILE;
import static com.DC.utilities.XLUtils.ProductKeywordsXLUtils.getProductKeywordsInFile;
import static com.DC.utilities.XLUtils.XLUtils.getFileHeaders;

public class ProductKeywordsExportApiTests extends BaseClass {
    private static String jwt;

    private static Company company;

    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    private static final ProductMasterCollection PRODUCT_COLLECTION = new ProductMasterCollection();

    private static final String ES_LOCALE = "es-MX";

    private static final String FR_LOCALE = "fr-FR";

    private static final String EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS = "Unable to Enqueue Product Keyword Export";

    ProductKeywordsExportApiTests() {
    }

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        LOGGER.info("Setting up keyword export api tests");
        company = new CompanyCollection().getCompany(TEST_CONFIG.companyID);
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
    }

    @Test(groups = {"ProductKeywordsExportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsExport_CanExportLiveProductKeywords() throws Exception {
        List<ProductVariantInstancePath> productInstancesToTest = new ArrayList<>();

        var productMaster = PRODUCT_COLLECTION.getProductMaster("QA-EXPORTS-001", TEST_CONFIG.companyID);

        var esLocaleId = company.getLocaleId(ES_LOCALE);

        var variantSet = productMaster.variantSets.live
                .stream()
                .filter(locale -> locale.localeId.equals(esLocaleId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        var instancePath = new ProductVariantInstancePath(
                productMaster._id,
                variantSet.localeId,
                Enums.ProductVariantType.LIVE,
                null,
                null
        );

        productInstancesToTest.add(instancePath);
        ProductVariantKeywordSet keywordSet = ProductVersioningApiService.getProductKeywordSet(instancePath, null, jwt);
        List<ProductKeywordsDataInExcel> expectedData = new ArrayList<>(generateExpectedRowData(keywordSet, productMaster.uniqueId, ES_LOCALE));

        instancePath = new ProductVariantInstancePath(
                productMaster._id,
                variantSet.localeId,
                Enums.ProductVariantType.LIVE,
                variantSet.instances.retailer.get(0).retailerId,
                null
        );

        productInstancesToTest.add(instancePath);
        keywordSet = ProductVersioningApiService.getProductKeywordSet(instancePath, null, jwt);
        expectedData.addAll(generateExpectedRowData(keywordSet, productMaster.uniqueId, ES_LOCALE));

        instancePath = new ProductVariantInstancePath(
                productMaster._id,
                variantSet.localeId,
                Enums.ProductVariantType.LIVE,
                null,
                variantSet.instances.globalCampaign.get(0).campaignId
        );

        productInstancesToTest.add(instancePath);
        keywordSet = ProductVersioningApiService.getProductKeywordSet(instancePath, null, jwt);
        expectedData.addAll(generateExpectedRowData(keywordSet, productMaster.uniqueId, ES_LOCALE));

        instancePath = new ProductVariantInstancePath(
                productMaster._id,
                variantSet.localeId,
                Enums.ProductVariantType.LIVE,
                variantSet.instances.retailerCampaign.get(0).retailerId,
                variantSet.instances.retailerCampaign.get(0).campaignId
        );

        productInstancesToTest.add(instancePath);
        keywordSet = ProductVersioningApiService.getProductKeywordSet(instancePath, null, jwt);
        expectedData.addAll(generateExpectedRowData(keywordSet, productMaster.uniqueId, ES_LOCALE));

        productMaster = PRODUCT_COLLECTION.getProductMaster("QA-EXPORTS-002", TEST_CONFIG.companyID);

        var frLocaleId = company.getLocaleId(FR_LOCALE);

        variantSet = productMaster.variantSets.live
                .stream()
                .filter(locale -> locale.localeId.equals(frLocaleId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        instancePath = new ProductVariantInstancePath(
                productMaster._id,
                variantSet.localeId,
                Enums.ProductVariantType.LIVE,
                null,
                null
        );

        productInstancesToTest.add(instancePath);
        keywordSet = ProductVersioningApiService.getProductKeywordSet(instancePath, null, jwt);
        expectedData.addAll(generateExpectedRowData(keywordSet, productMaster.uniqueId, FR_LOCALE));

        instancePath = new ProductVariantInstancePath(
                productMaster._id,
                variantSet.localeId,
                Enums.ProductVariantType.LIVE,
                variantSet.instances.retailerCampaign.get(0).retailerId,
                variantSet.instances.retailerCampaign.get(0).campaignId
        );

        productInstancesToTest.add(instancePath);
        keywordSet = ProductVersioningApiService.getProductKeywordSet(instancePath, null, jwt);
        expectedData.addAll(generateExpectedRowData(keywordSet, productMaster.uniqueId, FR_LOCALE));

        var filePath = sendRequestAndGetPathOfExportedFile(productInstancesToTest, "live");

        // TEST EXPORTED DATA
        List<ProductKeywordsDataInExcel> exportedData = getProductKeywordsInFile(filePath);
        List<String> exportedHeaders = getFileHeaders(filePath);
        Assert.assertEquals(exportedHeaders, EXPECTED_HEADERS_IN_EXPORT_FILE, "Incorrect headers in export file");
        Assert.assertEquals(exportedData.size(), expectedData.size());


        for (ProductKeywordsDataInExcel expectedRow : expectedData) {
            boolean expectedRowExists = exportedData.
                    stream()
                    .anyMatch(row -> Objects.equals(row.productIdentifier, expectedRow.productIdentifier) &&
                            Objects.equals(row.locale, expectedRow.locale) &&
                            Objects.equals(row.retailer, expectedRow.retailer) &&
                            Objects.equals(row.campaign, expectedRow.campaign) &&
                            Objects.equals(row.keywordBucket, expectedRow.keywordBucket) &&
                            Objects.equals(row.searchPhrase, expectedRow.searchPhrase)
                    );

            if (!expectedRowExists) {
                LOGGER.error("Expected row was not exported" +
                        "\nEXPECTED ROW:\n" + expectedRow +
                        "\nEXPORTED DATA:\n" + exportedData
                );
            }
            Assert.assertTrue(expectedRowExists, "Expected row was not exported");
        }
    }

    @Test(groups = {"ProductKeywordsExportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsExport_CanExportStagedProductKeywords_SpecificProducts() throws Exception {
        List<ProductVariantInstancePath> productInstancesToTest = new ArrayList<>();
        var productMaster = PRODUCT_COLLECTION.getProductMaster("QA-EXPORTS-001", TEST_CONFIG.companyID);
        var esLocaleId = company.getLocaleId(ES_LOCALE);

        var variantSet = productMaster.variantSets.live
                .stream()
                .filter(locale -> locale.localeId.equals(esLocaleId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        var instancePath = new ProductVariantInstancePath(
                productMaster._id,
                variantSet.localeId,
                Enums.ProductVariantType.STAGED,
                null,
                null
        );

        productInstancesToTest.add(instancePath);

        productMaster = PRODUCT_COLLECTION.getProductMaster("QA-EXPORTS-002", TEST_CONFIG.companyID);

        var frLocaleId = company.getLocaleId(FR_LOCALE);

        variantSet = productMaster.variantSets.live
                .stream()
                .filter(locale -> locale.localeId.equals(frLocaleId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        instancePath = new ProductVariantInstancePath(
                productMaster._id,
                variantSet.localeId,
                Enums.ProductVariantType.STAGED,
                null,
                null
        );

        productInstancesToTest.add(instancePath);

        var filePath = sendRequestAndGetPathOfExportedFile(productInstancesToTest, "staged");

        List<String> exportedHeaders = getFileHeaders(filePath);
        Assert.assertEquals(exportedHeaders, EXPECTED_HEADERS_IN_EXPORT_FILE, "Incorrect headers in export file");

        // TODO - verify exported data

    }

    @Test(groups = {"ProductKeywordsExportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsExport_CanExportStagedProductKeywords_AllProducts() throws Exception {
        var filePath = sendRequestAndGetPathOfExportedFile(new ArrayList<>(), "staged");

        List<String> exportedHeaders = getFileHeaders(filePath);
        Assert.assertEquals(exportedHeaders, EXPECTED_HEADERS_IN_EXPORT_FILE, "Incorrect headers in export file");

        // TODO - verify exported data
    }

    @Test(groups = {"ProductKeywordsExportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsExport_CannotExportProductKeywords_UnauthorizedErrorIsThrown() throws Exception {
        String unauthorizedUser = "qa+productmanagerviewonly@juggle.com";
        String jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, unauthorizedUser, TEST_CONFIG.password);
        Response response = ProductVersioningApiRequests.exportProductKeywords(new ArrayList<>(), "live", jwt);
        validateUnauthorizedError(response);
    }

    @Test(groups = {"ProductKeywordsExportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsExport_CannotExportProductKeywords_MissingParameters() throws Exception {
        String reqBody = "{\n" +
                "    \"products\": [{}]\n" +
                "}";

        Response response = ProductVersioningApiRequests.exportProductKeywords(reqBody, jwt);
        List<String> missingParameters = List.of("productMasterId", "localeId", "type", "retailerId", "campaignId");
        validateMissingRequestParametersError(response, missingParameters);
    }

    @Test(groups = {"ProductKeywordsExportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsExport_CannotExportProductKeywords_InvalidParameters() throws Exception {
        String reqBody = "{\n" +
                "    \"products\": \n" +
                "        {\n" +
                "        }\n" +
                "}";

        Response response = ProductVersioningApiRequests.exportProductKeywords(reqBody, jwt);
        List<String> expectedErrors = Collections.singletonList("\"products\" must be an array");
        validateInvalidRequestParametersError(response, expectedErrors);

        reqBody = "{\n" +
                "    \"products\": [\n" +
                "        {\n" +
                "            \"productMasterId\": null,\n" +
                "            \"localeId\": 12345,\n" +
                "            \"type\": \"invalid type\",\n" +
                "            \"retailerId\": false,\n" +
                "            \"campaignId\": \"null\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        response = ProductVersioningApiRequests.exportProductKeywords(reqBody, jwt);
        expectedErrors = List.of(
                "\"productMasterId\" needs to be a mongo Binary object",
                "\"localeId\" needs to be a mongo Binary object",
                "\"type\" must be one of [live, staged]",
                "\"retailerId\" needs to be a mongo Binary object",
                "\"campaignId\" needs to be a mongo Binary object"
        );
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductKeywordsExportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsExport_CannotExportProductKeywords_NonExistentProductMaster() throws Exception {
        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Enums.ProductVariantType.LIVE,
                null,
                null
        );

        ExportResponse response = ProductVersioningApiService.exportProductKeywords(Collections.singletonList(instancePath), "live", jwt);

        String expectedMsg = "Type: ProductMasterError. Subtype: ProductMasterMissingError. Message: Could not find product master.";
        verifyResponseBodyHasExpectedError(response, expectedMsg, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS);

        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(response.exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, exportRecord._id);
        Assert.assertNull(exportRecord.exportWorkbook, "Export file was generated with a non existent product master");

        verifyErrorInExportRecordContainsExpectedMessage(exportRecord, expectedMsg);

        String errorMessageInFailedJob = exportRecord.getImportFailedJobCountErrorMessage(exportRecord.standardizedDataIds.get(0));
        Assert.assertEquals(errorMessageInFailedJob, exportRecord.errors.get(0), "Error message in failed job doesn't match with expected error");
    }

    @Test(groups = {"ProductKeywordsExportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsExport_CannotExportProductKeywords_NonExistentVariantForLocale() throws Exception {
        String idOfProductToTest = PRODUCT_COLLECTION.getProductMaster("QA-EXPORTS-001", TEST_CONFIG.companyID)._id;

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                idOfProductToTest,
                UUID.randomUUID().toString(),
                Enums.ProductVariantType.LIVE,
                null,
                null
        );

        ExportResponse response = ProductVersioningApiService.exportProductKeywords(Collections.singletonList(instancePath), "live", jwt);

        String expectedMsg = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Could not find variant for the locale.";
        verifyResponseBodyHasExpectedError(response, expectedMsg, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS);

        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(response.exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, exportRecord._id);
        Assert.assertNull(exportRecord.exportWorkbook, "Export file was generated with a non existent product master");

        verifyErrorInExportRecordContainsExpectedMessage(exportRecord, expectedMsg);

        String errorMessageInFailedJob = exportRecord.getImportFailedJobCountErrorMessage(exportRecord.standardizedDataIds.get(0));
        Assert.assertEquals(errorMessageInFailedJob, exportRecord.errors.get(0), "Error message in failed job doesn't match with expected error");
    }

    @Test(groups = {"ProductKeywordsExportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsExport_CannotExportProductKeywords_NonExistentVariantSet() throws Exception {
        String idOfProductToTest = ProductVersioningApiService.getProductMasterByUniqueId("QA-EXPORTS-EMPTY", jwt)._id;

        String localeId = company.getLocaleId(FR_LOCALE);

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                idOfProductToTest,
                localeId,
                Enums.ProductVariantType.LIVE,
                null,
                UUID.randomUUID().toString()
        );

        ExportResponse response = ProductVersioningApiService.exportProductKeywords(Collections.singletonList(instancePath), "live", jwt);

        String expectedMsg = "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Instance does not exist on variant.";
        verifyResponseBodyHasExpectedError(response, expectedMsg, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS);

        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(response.exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, exportRecord._id);
        Assert.assertNull(exportRecord.exportWorkbook, "Export file was generated with a non existent product master");

        verifyErrorInExportRecordContainsExpectedMessage(exportRecord, expectedMsg);

        String errorMessageInFailedJob = exportRecord.getImportFailedJobCountErrorMessage(exportRecord.standardizedDataIds.get(0));
        Assert.assertEquals(errorMessageInFailedJob, exportRecord.errors.get(0), "Error message in failed job doesn't match with expected error");
    }

    private List<ProductKeywordsDataInExcel> generateExpectedRowData(ProductVariantKeywordSet keywordSet, String productIdentifier, String localeName) throws Exception {
        List<ProductKeywordsDataInExcel> expectedData = new ArrayList<>();

        if (keywordSet._id == null) {
            ProductKeywordsDataInExcel expectedRow = new ProductKeywordsDataInExcel();
            expectedRow.productIdentifier = productIdentifier;
            expectedRow.locale = localeName;
            expectedData.add(expectedRow);
            return expectedData;
        }

        for (String keyword : keywordSet.keywords.title) {
            ProductKeywordsDataInExcel expectedRow = getExpectedRowData(keywordSet, keyword, productIdentifier, localeName, Enums.KeywordBucketType.TITLE);
            expectedData.add(expectedRow);
        }

        for (String keyword : keywordSet.keywords.onPage) {
            ProductKeywordsDataInExcel expectedRow = getExpectedRowData(keywordSet, keyword, productIdentifier, localeName, Enums.KeywordBucketType.ON_PAGE);
            expectedData.add(expectedRow);
        }

        for (String keyword : keywordSet.keywords.branded) {
            ProductKeywordsDataInExcel expectedRow = getExpectedRowData(keywordSet, keyword, productIdentifier, localeName, Enums.KeywordBucketType.BRANDED);
            expectedData.add(expectedRow);
        }

        for (String keyword : keywordSet.keywords.hidden) {
            ProductKeywordsDataInExcel expectedRow = getExpectedRowData(keywordSet, keyword, productIdentifier, localeName, Enums.KeywordBucketType.HIDDEN);
            expectedData.add(expectedRow);
        }

        for (String keyword : keywordSet.keywords.rankTracking) {
            ProductKeywordsDataInExcel expectedRow = getExpectedRowData(keywordSet, keyword, productIdentifier, localeName, Enums.KeywordBucketType.RANK_TRACKING);
            expectedData.add(expectedRow);
        }

        for (String keyword : keywordSet.keywords.optional) {
            ProductKeywordsDataInExcel expectedRow = getExpectedRowData(keywordSet, keyword, productIdentifier, localeName, Enums.KeywordBucketType.OPTIONAL);
            expectedData.add(expectedRow);
        }

        for (String keyword : keywordSet.keywords.reserved) {
            ProductKeywordsDataInExcel expectedRow = getExpectedRowData(keywordSet, keyword, productIdentifier, localeName, Enums.KeywordBucketType.RESERVED);
            expectedData.add(expectedRow);
        }

        for (String keyword : keywordSet.keywords.unused) {
            ProductKeywordsDataInExcel expectedRow = getExpectedRowData(keywordSet, keyword, productIdentifier, localeName, Enums.KeywordBucketType.UNUSED);
            expectedData.add(expectedRow);
        }

        return expectedData;
    }

    private ProductKeywordsDataInExcel getExpectedRowData(ProductVariantKeywordSet keywordSet, String keyword, String productIdentifier, String localeName, Enums.KeywordBucketType bucketType) throws Exception {
        ProductKeywordsDataInExcel expectedRow = new ProductKeywordsDataInExcel();
        expectedRow.productIdentifier = productIdentifier;
        expectedRow.locale = localeName;
        expectedRow.retailer = keywordSet.retailerId == null ? null : company.getRetailerName(keywordSet.retailerId);
        expectedRow.campaign = keywordSet.campaignId == null ? null : company.getCampaignName(keywordSet.campaignId);
        expectedRow.keywordBucket = bucketType;
        expectedRow.searchPhrase = keyword;
        return expectedRow;
    }

    private String sendRequestAndGetPathOfExportedFile(List<ProductVariantInstancePath> instancesToExport, String type) throws Exception {
        var response = ProductVersioningApiRequests.exportProductKeywords(instancesToExport, type, jwt);
        var exportId = ExportCoreAssertions.verifyResponseReturnsAnExportId(testMethodName.get(), response);

        response = ProductVersioningApiRequests.getExportTrackingRecord(exportId, jwt);

        var exportStatus = response.jsonPath().getString("status");
        Assert.assertEquals(exportStatus, Enums.ProcessStatus.SUCCESS.getProcessStatus(), "Export status is not SUCCESS");

        var linkToFile = response.jsonPath().getString("exportWorkbook.link");

        var fileName = linkToFile.substring(linkToFile.lastIndexOf('/') + 1, linkToFile.lastIndexOf('?'));
        var filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/" + fileName;
        downloadFileFromUrl(linkToFile, filePath);
        return filePath;
    }
}
