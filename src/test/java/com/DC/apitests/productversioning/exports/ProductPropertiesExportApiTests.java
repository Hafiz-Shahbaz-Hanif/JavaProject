package com.DC.apitests.productversioning.exports;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.ProductMasterVariantDigitalAssetCollection;
import com.DC.db.productVersioning.ProductMasterVariantPropertySetCollection;
import com.DC.objects.productVersioning.ExportRecord;
import com.DC.objects.productVersioning.ProductPropertiesExportExcelData;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.testcases.BaseClass;
import com.DC.tests.sharedAssertions.ExportCoreAssertions;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.SharedRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ExportProductPropertyRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.apiEngine.routes.productVersioning.ProductVersioningRoutes;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.apitests.ApiValidations.validateInvalidRequestParametersError;
import static com.DC.tests.sharedAssertions.ExportCoreAssertions.verifyErrorInExportRecordContainsExpectedMessage;
import static com.DC.tests.sharedAssertions.ExportCoreAssertions.verifyResponseBodyHasExpectedError;
import static com.DC.utilities.CommonApiMethods.callEndpoint;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.SharedMethods.downloadFileFromUrl;
import static com.DC.utilities.XLUtils.ProductPropertiesXLUtils.EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS;
import static com.DC.utilities.XLUtils.ProductPropertiesXLUtils.getProductPropertiesExported;
import static com.DC.utilities.XLUtils.XLUtils.getFileHeaders;
import static java.util.Arrays.asList;

public class ProductPropertiesExportApiTests extends BaseClass {
    private static String jwt;
    private static CompanyWithProperties company;

    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    private static final UserFriendlyInstancePath PRODUCT_VERSION_TO_EXPORT = new UserFriendlyInstancePath(
            "QA-IMPORTS-003",
            "fr-FR",
            "Amazon.com",
            "Christmas"
    );

    private static final UserFriendlyInstancePath PRODUCT_BASE_VERSION_TO_EXPORT = new UserFriendlyInstancePath(
            "QA-IMPORTS-003",
            "fr-FR",
            null,
            null
    );

    private static final UserFriendlyInstancePath PRODUCT_VERSION_TO_EXPORT_2 = new UserFriendlyInstancePath(
            "QA-EXPORTS-EMPTY",
            "fr-FR",
            "Amazon.com",
            "Christmas"
    );

    private static final UserFriendlyInstancePath PRODUCT_VERSION_TO_EXPORT_3 = new UserFriendlyInstancePath(
            "QA-IMPORTS-001",
            "es-MX",
            "Amazon.com",
            "Halloween"
    );

    private static final String EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS = "Unable to perform Product Property Export";

    ProductPropertiesExportApiTests() {
    }

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        company = CompanyApiService.getCompanyWithProperties(jwt);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportLiveProductProperties_SpecificProductsWithAllPropertiesAndDigitalAssets() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT, PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE));
        versionsToExportMap.put(PRODUCT_BASE_VERSION_TO_EXPORT, PRODUCT_BASE_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE));
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT_2, PRODUCT_VERSION_TO_EXPORT_2.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE));
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT_3, PRODUCT_VERSION_TO_EXPORT_3.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), new ArrayList<>(), new ArrayList<>());
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);

        var exportedHeaders = getFileHeaders(filePath);
        var expectedHeaders = new ArrayList<>(EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS);
        expectedHeaders.addAll(company.companyProperties.propertySchema.stream().map(prop -> prop.id).collect(Collectors.toList()));
        expectedHeaders.addAll(company.companyProperties.digitalAssetPropertySchema.stream().map(prop -> prop.id).collect(Collectors.toList()));
        Assert.assertEqualsNoOrder(exportedHeaders.toArray(), expectedHeaders.toArray(), "Headers in exported file don't match expected headers" +
                "Expected headers:\n" + expectedHeaders +
                "\nExported headers:\n" + exportedHeaders
        );

        var exportedData = getProductPropertiesExported(filePath);
        verifySpecificProductsWereExportedWithCorrectLiveData(versionsToExportMap, requestBody, exportedData);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportLiveProductProperties_SpecificProductsWithSpecificStandardPropertiesAndAllDigitalAssets() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT, PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), asList("test_prop_1", "test_prop_2"), new ArrayList<>());
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);

        var exportedHeaders = getFileHeaders(filePath);
        var expectedHeaders = new ArrayList<>(EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS);
        expectedHeaders.addAll(requestBody.propertyIds);
        expectedHeaders.addAll(company.companyProperties.digitalAssetPropertySchema.stream().map(prop -> prop.id).collect(Collectors.toList()));
        Assert.assertEquals(exportedHeaders, expectedHeaders, "Headers in exported file don't match expected headers");

        var exportedData = getProductPropertiesExported(filePath);
        verifySpecificProductsWereExportedWithCorrectLiveData(versionsToExportMap, requestBody, exportedData);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportLiveProductProperties_SpecificProductsWithSpecificStandardPropertiesAndNoDigitalAssets() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT, PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), asList("test_prop_1", "test_prop_2"), null);
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);

        var exportedHeaders = getFileHeaders(filePath);
        var expectedHeaders = new ArrayList<>(EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS);
        expectedHeaders.addAll(requestBody.propertyIds);
        Assert.assertEquals(exportedHeaders, expectedHeaders, "Headers in exported file don't match expected headers");
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportLiveProductProperties_SpecificProductsWithSpecificDigitalAssetsAndNoStandardProperties() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT, PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), null, asList("calories_image", "test_digital_asset_1"));
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);

        var exportedHeaders = getFileHeaders(filePath);
        var expectedHeaders = new ArrayList<>(EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS);
        expectedHeaders.addAll(requestBody.digitalAssetIds);
        Assert.assertEquals(exportedHeaders, expectedHeaders, "Headers in exported file don't match expected headers");
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportLiveProductProperties_SpecificProductsWithAllStandardPropertiesAndSpecificDigitalAssets() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT, PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), new ArrayList<>(), asList("test_digital_asset_1", "test_digital_asset_2"));
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);

        var exportedHeaders = getFileHeaders(filePath);
        var expectedHeaders = new ArrayList<>(EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS);
        expectedHeaders.addAll(company.companyProperties.propertySchema.stream().map(prop -> prop.id).collect(Collectors.toList()));
        expectedHeaders.addAll(requestBody.digitalAssetIds);
        Assert.assertEquals(exportedHeaders, expectedHeaders, "Headers in exported file don't match expected headers");

        var exportedData = getProductPropertiesExported(filePath);
        verifySpecificProductsWereExportedWithCorrectLiveData(versionsToExportMap, requestBody, exportedData);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportLiveProductProperties_SpecificProductsWithSpecificStandardPropertiesAndDigitalAssets() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT, PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), asList("test_prop_1", "test_prop_5"), asList("test_digital_asset_1", "test_digital_asset_2"));
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);

        var exportedHeaders = getFileHeaders(filePath);
        var expectedHeaders = new ArrayList<>(EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS);
        expectedHeaders.addAll(requestBody.propertyIds);
        expectedHeaders.addAll(requestBody.digitalAssetIds);
        Assert.assertEquals(exportedHeaders, expectedHeaders, "Headers in exported file don't match expected headers");

        var exportedData = getProductPropertiesExported(filePath);
        verifySpecificProductsWereExportedWithCorrectLiveData(versionsToExportMap, requestBody, exportedData);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportLiveProductProperties_AllProductsWithAllPropertiesAndDigitalAssets() throws Exception {
        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);
        List<ProductPropertiesExportExcelData> exportedData = getProductPropertiesExported(filePath);
        Assert.assertFalse(exportedData.isEmpty(), "Exported file only had headers");
        //verifyAllProductsWereExportedWithCorrectLiveData(requestBody, exportedData); TODO: Remove if there is no way to reduce time for this validation
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportLiveProductProperties_AllProductsWithSpecificPropertiesAndDigitalAssets() throws Exception {
        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(), List.of("test_prop_1"), List.of("test_digital_asset_1"));
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);
        List<ProductPropertiesExportExcelData> exportedData = getProductPropertiesExported(filePath);
        Assert.assertFalse(exportedData.isEmpty(), "Exported file only had headers");
        //verifyAllProductsWereExportedWithCorrectLiveData(requestBody, exportedData); TODO: Remove if there is no way to reduce time for this validation
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportStagedProductProperties_SpecificProductsWithAllPropertiesAndDigitalAssets() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_BASE_VERSION_TO_EXPORT, PRODUCT_BASE_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT, PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT_2, PRODUCT_VERSION_TO_EXPORT_2.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT_3, PRODUCT_VERSION_TO_EXPORT_3.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), new ArrayList<>(), new ArrayList<>(), Enums.ProductVariantType.STAGED.getType());
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);

        var exportedHeaders = getFileHeaders(filePath);
        var expectedHeaders = new ArrayList<>(EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS);
        expectedHeaders.addAll(company.companyProperties.propertySchema.stream().map(prop -> prop.id).collect(Collectors.toList()));
        expectedHeaders.addAll(company.companyProperties.digitalAssetPropertySchema.stream().map(prop -> prop.id).collect(Collectors.toList()));
        Assert.assertEqualsNoOrder(exportedHeaders.toArray(), expectedHeaders.toArray(), "Headers in exported file don't match expected headers");

        var exportedData = getProductPropertiesExported(filePath);
        verifySpecificProductsWereExportedWithCorrectStagedData(versionsToExportMap, requestBody, exportedData);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportStagedProductProperties_SpecificProductsWithSpecificStandardPropertiesAndAllDigitalAssets() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_BASE_VERSION_TO_EXPORT, PRODUCT_BASE_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT, PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), asList("bullet_1", "test_prop_2"), new ArrayList<>(), Enums.ProductVariantType.STAGED.getType());
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);
        var exportedData = getProductPropertiesExported(filePath);
        verifySpecificProductsWereExportedWithCorrectStagedData(versionsToExportMap, requestBody, exportedData);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportStagedProductProperties_SpecificProductsWithAllStandardPropertiesAndSpecificDigitalAssets() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT, PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));
        versionsToExportMap.put(PRODUCT_BASE_VERSION_TO_EXPORT, PRODUCT_BASE_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), new ArrayList<>(), asList("calories_image", "flavor_image"), Enums.ProductVariantType.STAGED.getType());
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);
        var exportedData = getProductPropertiesExported(filePath);
        verifySpecificProductsWereExportedWithCorrectStagedData(versionsToExportMap, requestBody, exportedData);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportStagedProductProperties_SpecificProductsWithSpecificStandardPropertiesAndDigitalAssets() throws Exception {
        Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap = new HashMap<>();
        versionsToExportMap.put(PRODUCT_BASE_VERSION_TO_EXPORT, PRODUCT_BASE_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));
        versionsToExportMap.put(PRODUCT_VERSION_TO_EXPORT_2, PRODUCT_VERSION_TO_EXPORT_2.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED));

        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(versionsToExportMap.values()), asList("product_description", "bullet_1"), asList("calories_image", "flavor_image"), Enums.ProductVariantType.STAGED.getType());
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);
        var exportedData = getProductPropertiesExported(filePath);
        verifySpecificProductsWereExportedWithCorrectStagedData(versionsToExportMap, requestBody, exportedData);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportStagedProductProperties_AllProductsWithAllPropertiesAndDigitalAssets() throws Exception {
        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), Enums.ProductVariantType.STAGED.getType());
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);
        List<ProductPropertiesExportExcelData> exportedData = getProductPropertiesExported(filePath);
        Assert.assertFalse(exportedData.isEmpty(), "Exported file only had headers");
        //verifyAllProductsWereExportedWithCorrectStagedData(requestBody, exportedData); TODO: Remove if there is no way to reduce time for this validation
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CanExportStagedProductProperties_AllProductsWithSpecificPropertiesAndDigitalAssets() throws Exception {
        var requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(), List.of("product_description"), List.of("calories_image"), Enums.ProductVariantType.STAGED.getType());
        var filePath = sendRequestAndGetPathOfExportedFile(requestBody);
        List<ProductPropertiesExportExcelData> exportedData = getProductPropertiesExported(filePath);
        Assert.assertFalse(exportedData.isEmpty(), "Exported file only had headers");
        //verifyAllProductsWereExportedWithCorrectStagedData(requestBody, exportedData); TODO: Remove if there is no way to reduce time for this validation
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CannotExportProductProperties_UnauthorizedErrorIsThrown() throws Exception {
        String unauthorizedUser = "qa+productmanagerviewonly@juggle.com";
        String jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, unauthorizedUser, TEST_CONFIG.password);

        ExportProductPropertyRequestBody requestBody = new ExportProductPropertyRequestBody(new ArrayList<>(), new ArrayList<>());

        Response response = ProductVersioningApiRequests.exportProductProperties(requestBody, jwt);

        validateUnauthorizedError(response);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CannotExportProductProperties_MissingParameters() throws Exception {
        String reqBody = "{\n" + "    \"products\": [{}]\n" + "}";

        Response response = callEndpoint(ProductVersioningRoutes.getProductPropertyExportRoutePath(), jwt, "POST", reqBody, "");
        List<String> missingParameters = asList("productMasterId", "localeId", "type", "retailerId", "campaignId");
        validateMissingRequestParametersError(response, missingParameters);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CannotExportProductProperties_InvalidParameters() throws Exception {
        String reqBody = "{\n" + "    \"products\": \n" + "        {\n" + "            \"productMasterId\": \"125B1CDD-0A9D-47DB-9A90-7D3AD442971A\",\n" + "            \"localeId\": \"a4d375c6-564a-44f6-899d-97a695107f1d\",\n" + "            \"type\": \"live\",\n" + "            \"retailerId\": null,\n" + "            \"campaignId\": null\n" + "        }\n" + "    ,\n" + "    \"propertyIds\": [1]\n" + "}";

        Response response = callEndpoint(ProductVersioningRoutes.getProductPropertyExportRoutePath(), jwt, "POST", reqBody, "");
        List<String> expectedErrors = asList("\"products\" must be an array", "\"0\" must be a string");
        validateInvalidRequestParametersError(response, expectedErrors);

        reqBody = "{\n" + "    \"products\": [\n" + "        {\n" + "            \"productMasterId\": null,\n" + "            \"localeId\": 12345,\n" + "            \"type\": \"invalid type\",\n" + "            \"retailerId\": false,\n" + "            \"campaignId\": \"null\"\n" + "        }\n" + "    ]\n" + "}";

        response = callEndpoint(ProductVersioningRoutes.getProductPropertyExportRoutePath(), jwt, "POST", reqBody, "");
        expectedErrors = asList("\"productMasterId\" needs to be a mongo Binary object", "\"localeId\" needs to be a mongo Binary object", "\"type\" must be one of [live, staged]", "\"retailerId\" needs to be a mongo Binary object", "\"campaignId\" needs to be a mongo Binary object");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CannotExportProductProperties_NonExistentProductMaster() throws Exception {
        ProductVariantInstancePath productVariantToExport = new ProductVariantInstancePath(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Enums.ProductVariantType.LIVE, null, null);

        ExportProductPropertyRequestBody requestBody = new ExportProductPropertyRequestBody(Collections.singletonList(productVariantToExport), Collections.singletonList("test_prop_1"));

        ExportResponse response = ProductVersioningApiService.exportProductProperties(requestBody, jwt);

        String expectedMsg = "Could not find product master.";
        verifyResponseBodyHasExpectedError(response, expectedMsg, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS);

        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(response.exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, exportRecord._id);
        Assert.assertNull(exportRecord.exportWorkbook, "Export file was generated with a non existent product master");

        verifyErrorInExportRecordContainsExpectedMessage(exportRecord, expectedMsg);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CannotExportProductProperties_NonExistentVariantForLocale() throws Exception {
        var productToTest = PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE);
        ProductVariantInstancePath productVariantToExport = new ProductVariantInstancePath(productToTest.productMasterId, UUID.randomUUID().toString(), Enums.ProductVariantType.LIVE, null, null);

        ExportProductPropertyRequestBody requestBody = new ExportProductPropertyRequestBody(Collections.singletonList(productVariantToExport));

        ExportResponse response = ProductVersioningApiService.exportProductProperties(requestBody, jwt);

        String expectedMsg = "Could not find variant for the locale.";
        verifyResponseBodyHasExpectedError(response, expectedMsg, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS);

        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(response.exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, exportRecord._id);
        Assert.assertNull(exportRecord.exportWorkbook, "Export file was generated with a non existent locale variant");

        verifyErrorInExportRecordContainsExpectedMessage(exportRecord, expectedMsg);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CannotExportProductProperties_NonExistentVariantSet() throws Exception {
        var idOfProductToTest = ProductVersioningApiService.getProductMasterByUniqueId("QA-EXPORTS-EMPTY", jwt)._id;
        var localeId = company.getLocaleId("fr-FR");
        var campaignId = company.getCampaignId("New Year's");

        ProductVariantInstancePath productVariantToExport = new ProductVariantInstancePath(idOfProductToTest, localeId, Enums.ProductVariantType.LIVE, null, campaignId);

        ExportProductPropertyRequestBody requestBody = new ExportProductPropertyRequestBody(Collections.singletonList(productVariantToExport));

        ExportResponse response = ProductVersioningApiService.exportProductProperties(requestBody, jwt);

        String expectedMsg = "Could not find campaign on any instances.";
        verifyResponseBodyHasExpectedError(response, expectedMsg, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS);

        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(response.exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, exportRecord._id);

        verifyErrorInExportRecordContainsExpectedMessage(exportRecord, expectedMsg);
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CannotExportProductProperties_NonExistentProperty() throws Exception {
        var productToTest = PRODUCT_VERSION_TO_EXPORT.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE);
        var invalidPropertyId = "invalid_property";
        ExportProductPropertyRequestBody requestBody = new ExportProductPropertyRequestBody(List.of(productToTest), asList("test_prop_1", invalidPropertyId));

        ExportResponse response = ProductVersioningApiService.exportProductProperties(requestBody, jwt);

        String expectedMsg = "Property with ID " + invalidPropertyId + " does not exist on company";
        verifyResponseBodyHasExpectedError(response, expectedMsg, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS);

        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(response.exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, exportRecord._id);
        Assert.assertNull(exportRecord.exportWorkbook.link, "Export file was generated with a non existent propertyId");

        String errorInExportRecord = exportRecord.errors.get(0);
        expectedMsg = "Errors were encountered during standardization. Error: " + expectedMsg;
        Assert.assertEquals(errorInExportRecord, expectedMsg, "Error didn't match with the expected error");

        String errorMessageInFailedJob = exportRecord.getImportFailedJobCountErrorMessage(exportRecord.standardizedDataIds.get(0));
        Assert.assertEquals(errorMessageInFailedJob, errorInExportRecord, "Error message in failed job with id: " + exportRecord.standardizedDataIds.get(0) + "doesn't match with expected error");
    }

    @Test(groups = {"ProductPropertiesExportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesExports_CannotExportStagedProductProperties_ProductDoesNotHaveStagedData() throws Exception {
        var productToTest = PRODUCT_VERSION_TO_EXPORT_2.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED);
        var requestBody = new ExportProductPropertyRequestBody(Collections.singletonList(productToTest), new ArrayList<>(), new ArrayList<>(), Enums.ProductVariantType.STAGED.getType());
        var response = ProductVersioningApiService.exportProductProperties(requestBody, jwt);
        String expectedMsg = "Could not find staged instances that have properties or digital assets.";
        verifyResponseBodyHasExpectedError(response, expectedMsg, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS);

        var exportResponse = SharedRequests.getExportTrackingRecord(response.exportId, jwt);
        checkResponseStatus(testMethodName.get(), 404, exportResponse.getStatusCode());
    }

/*    private void verifyAllProductsWereExportedWithCorrectLiveData(ExportProductPropertyRequestBody requestBody, List<ProductPropertiesExportExcelData> exportedData) throws Exception {
        var productsWithLiveData = ProductVersioningApiService.getAllProductMastersFromCompany(jwt)
                .parallelStream()
                .filter(prod -> !prod.variantSets.live.isEmpty())
                .collect(Collectors.toList());

        List<ProductPropertiesExportExcelData> expectedData = new ArrayList<>();

        for (var productMaster : productsWithLiveData) {
            for (var variantSet : productMaster.variantSets.live) {
                ProductPropertiesExportExcelData expectedRow;

                var localeName = company.getLocaleName(variantSet.localeId);
                var userFriendlyInstancePath = new UserFriendlyInstancePath(productMaster.uniqueId, localeName, null, null);
                expectedRow = generateExpectedRow(requestBody, variantSet.instances.global.propertySetId, variantSet.instances.global.digitalAssetSetId, userFriendlyInstancePath, variantSet.instances.global.name, "");
                expectedData.add(expectedRow);

                for (var retailerInstance : variantSet.instances.retailer) {
                    userFriendlyInstancePath = new UserFriendlyInstancePath(productMaster.uniqueId, localeName, company.getRetailerName(retailerInstance.retailerId), null);
                    var expectedRPC = retailerInstance.rpc == null ? "" : retailerInstance.rpc;
                    expectedRow = generateExpectedRow(requestBody, retailerInstance.propertySetId, retailerInstance.digitalAssetSetId, userFriendlyInstancePath, retailerInstance.name, expectedRPC);
                    expectedData.add(expectedRow);
                }

                for (var campaignInstance : variantSet.instances.globalCampaign) {
                    userFriendlyInstancePath = new UserFriendlyInstancePath(productMaster.uniqueId, localeName, null, company.getCampaignName(campaignInstance.campaignId));
                    expectedRow = generateExpectedRow(requestBody, campaignInstance.propertySetId, campaignInstance.digitalAssetSetId, userFriendlyInstancePath, campaignInstance.name, "");
                    expectedData.add(expectedRow);
                }

                for (var retailerCampaignInstance : variantSet.instances.retailerCampaign) {
                    userFriendlyInstancePath = new UserFriendlyInstancePath(productMaster.uniqueId, localeName, company.getRetailerName(retailerCampaignInstance.retailerId), company.getCampaignName(retailerCampaignInstance.campaignId));
                    var expectedRPC = retailerCampaignInstance.rpc == null ? "" : retailerCampaignInstance.rpc;
                    expectedRow = generateExpectedRow(requestBody, retailerCampaignInstance.propertySetId, retailerCampaignInstance.digitalAssetSetId, userFriendlyInstancePath, retailerCampaignInstance.name, expectedRPC);
                    expectedData.add(expectedRow);
                }
            }
        }
        exportedData.forEach(row -> row.properties.sort(Comparator.comparing(o -> o.id)));
        expectedData.forEach(row -> row.properties.sort(Comparator.comparing(o -> o.id)));

        Assert.assertEquals(exportedData, expectedData, "Exported data doesn't match expected data");
    }*/

/*    private void verifyAllProductsWereExportedWithCorrectStagedData(ExportProductPropertyRequestBody requestBody, List<ProductPropertiesExportExcelData> exportedData) throws Exception {
        var productsWithStagedData = ProductVersioningApiService.getAllProductMastersFromCompany(jwt)
                .stream()
                .filter(prod -> !prod.variantSets.staged.isEmpty())
                .collect(Collectors.toList());

        List<ProductPropertiesExportExcelData> expectedData = new ArrayList<>();

        for (var productMaster : productsWithStagedData) {
            for (var variantSet : productMaster.variantSets.staged) {
                var liveVariantSetForLocale = productMaster.variantSets.live.stream().filter(variant -> variant.localeId.equals(variantSet.localeId)).findFirst().orElseThrow(NoSuchElementException::new);
                ProductPropertiesExportExcelData expectedRow;
                String instanceName;

                var localeName = company.getLocaleName(variantSet.localeId);
                var userFriendlyInstancePath = new UserFriendlyInstancePath(productMaster.uniqueId, localeName, null, null);

                if (variantSet.instances.global.propertySetId != null || variantSet.instances.global.digitalAssetSetId != null) {
                    expectedRow = generateExpectedRow(requestBody, variantSet.instances.global.propertySetId, variantSet.instances.global.digitalAssetSetId, userFriendlyInstancePath, liveVariantSetForLocale.instances.global.name, "");
                    expectedData.add(expectedRow);
                }

                var retailersWithPropertySetIds = variantSet.instances.retailer.parallelStream().filter(inst -> inst.propertySetId != null).collect(Collectors.toList());

                for (var retailerInstance : retailersWithPropertySetIds) {
                    var liveInstance = liveVariantSetForLocale.instances.retailer.parallelStream().filter(inst -> inst.retailerId.equals(retailerInstance.retailerId)).findFirst().orElseThrow(NoSuchElementException::new);
                    instanceName = liveInstance.name;
                    var expectedRPC = liveInstance.rpc == null ? "" : liveInstance.rpc;
                    userFriendlyInstancePath = new UserFriendlyInstancePath(productMaster.uniqueId, localeName, company.getRetailerName(retailerInstance.retailerId), null);
                    expectedRow = generateExpectedRow(requestBody, retailerInstance.propertySetId, retailerInstance.digitalAssetSetId, userFriendlyInstancePath, instanceName, expectedRPC);
                    expectedData.add(expectedRow);
                }

                var campaignsWithPropertySetIds = variantSet.instances.globalCampaign.parallelStream().filter(inst -> inst.propertySetId != null || inst.digitalAssetSetId != null).collect(Collectors.toList());

                for (var campaignInstance : campaignsWithPropertySetIds) {
                    instanceName = liveVariantSetForLocale.instances.globalCampaign.stream().filter(inst -> inst.campaignId.equals(campaignInstance.campaignId)).findFirst().orElseThrow(NoSuchElementException::new).name;
                    userFriendlyInstancePath = new UserFriendlyInstancePath(productMaster.uniqueId, localeName, null, company.getCampaignName(campaignInstance.campaignId));
                    expectedRow = generateExpectedRow(requestBody, campaignInstance.propertySetId, campaignInstance.digitalAssetSetId, userFriendlyInstancePath, instanceName, "");
                    expectedData.add(expectedRow);
                }

                var retailerCampaignsWithPropertySetIds = variantSet.instances.retailerCampaign.parallelStream().filter(inst -> inst.propertySetId != null || inst.digitalAssetSetId != null).collect(Collectors.toList());

                for (var retailerCampaignInstance : retailerCampaignsWithPropertySetIds) {
                    var liveInstance = liveVariantSetForLocale.instances.retailerCampaign.stream().filter(inst -> inst.retailerId.equals(retailerCampaignInstance.retailerId) && inst.campaignId.equals(retailerCampaignInstance.campaignId)).findFirst().orElseThrow(NoSuchElementException::new);
                    instanceName = liveInstance.name;
                    var expectedRPC = liveInstance.rpc == null ? "" : liveInstance.rpc;
                    userFriendlyInstancePath = new UserFriendlyInstancePath(productMaster.uniqueId, localeName, company.getRetailerName(retailerCampaignInstance.retailerId), company.getCampaignName(retailerCampaignInstance.campaignId));
                    expectedRow = generateExpectedRow(requestBody, retailerCampaignInstance.propertySetId, retailerCampaignInstance.digitalAssetSetId, userFriendlyInstancePath, instanceName, expectedRPC);
                    expectedData.add(expectedRow);
                }
            }
        }
        exportedData.forEach(row -> row.properties.sort(Comparator.comparing(o -> o.id)));
        expectedData.forEach(row -> row.properties.sort(Comparator.comparing(o -> o.id)));

        Assert.assertEquals(exportedData, expectedData, "Exported data doesn't match expected data");
    }*/

    private String sendRequestAndGetPathOfExportedFile(ExportProductPropertyRequestBody requestBody) throws Exception {
        var response = ProductVersioningApiRequests.exportProductProperties(requestBody, jwt);
        var exportId = ExportCoreAssertions.verifyResponseReturnsAnExportId(testMethodName.get(), response);

        response = ProductVersioningApiRequests.getExportTrackingRecord(exportId, jwt);

        var linkToFile = response.jsonPath().getString("exportWorkbook.link");

        var fileName = linkToFile.substring(linkToFile.lastIndexOf('/') + 1, linkToFile.lastIndexOf('?'));
        var filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/" + fileName;
        downloadFileFromUrl(linkToFile, filePath);
        return filePath;
    }

    private void verifySpecificProductsWereExportedWithCorrectLiveData(Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap, ExportProductPropertyRequestBody requestBody, List<ProductPropertiesExportExcelData> exportedData) throws Exception {
        List<ProductPropertiesExportExcelData> expectedData = new ArrayList<>();

        for (var entry : versionsToExportMap.entrySet()) {
            var friendlyInstancePath = entry.getKey();
            var instancePath = entry.getValue();

            var productMaster = ProductVersioningApiService.getProductMaster(instancePath.productMasterId, jwt);
            var variantSetForLocale = productMaster.variantSets.live.stream().filter(variant -> variant.localeId.equals(instancePath.localeId)).findFirst().orElse(null);

            if (variantSetForLocale == null) {
                continue;
            }

            ProductPropertiesExportExcelData expectedRow;

            switch (instancePath.getProductLevel()) {
                case GLOBAL:
                    var globalInstance = variantSetForLocale.instances.global;
                    expectedRow = generateExpectedRow(requestBody, globalInstance.propertySetId, globalInstance.digitalAssetSetId, friendlyInstancePath, globalInstance.name, "");
                    expectedData.add(expectedRow);
                    break;

                case RETAILER:
                    var retailerInstance = variantSetForLocale.instances.retailer.stream().filter(inst -> inst.retailerId.equals(instancePath.retailerId)).findFirst().orElse(null);
                    if (retailerInstance == null) {
                        continue;
                    }
                    expectedRow = generateExpectedRow(requestBody, retailerInstance.propertySetId, retailerInstance.digitalAssetSetId, friendlyInstancePath, retailerInstance.name, retailerInstance.rpc == null ? "" : retailerInstance.rpc);
                    expectedData.add(expectedRow);

                    break;

                case GLOBAL_CAMPAIGN:
                    var globalCampaignInstance = variantSetForLocale.instances.globalCampaign.stream().filter(inst -> inst.campaignId.equals(instancePath.campaignId)).findFirst().orElse(null);
                    if (globalCampaignInstance == null) {
                        continue;
                    }
                    expectedRow = generateExpectedRow(requestBody, globalCampaignInstance.propertySetId, globalCampaignInstance.digitalAssetSetId, friendlyInstancePath, globalCampaignInstance.name, "");
                    expectedData.add(expectedRow);

                    break;

                case RETAILER_CAMPAIGN:
                    var retailerCampaignInstance = variantSetForLocale.instances.retailerCampaign.stream().filter(inst -> inst.retailerId.equals(instancePath.retailerId) && inst.campaignId.equals(instancePath.campaignId)).findFirst().orElse(null);
                    if (retailerCampaignInstance == null) {
                        continue;
                    }

                    expectedRow = generateExpectedRow(requestBody, retailerCampaignInstance.propertySetId, retailerCampaignInstance.digitalAssetSetId, friendlyInstancePath, retailerCampaignInstance.name, retailerCampaignInstance.rpc == null ? "" : retailerCampaignInstance.rpc);
                    expectedData.add(expectedRow);
                    break;
            }
        }

        exportedData.forEach(row -> row.properties.sort(Comparator.comparing(o -> o.id)));
        expectedData.forEach(row -> row.properties.sort(Comparator.comparing(o -> o.id)));

        Assert.assertEquals(exportedData, expectedData, "Exported data doesn't match expected data");
    }

    private void verifySpecificProductsWereExportedWithCorrectStagedData(Map<UserFriendlyInstancePath, ProductVariantInstancePath> versionsToExportMap, ExportProductPropertyRequestBody requestBody, List<ProductPropertiesExportExcelData> exportedData) throws Exception {
        List<ProductPropertiesExportExcelData> expectedData = new ArrayList<>();

        for (var entry : versionsToExportMap.entrySet()) {
            var friendlyInstancePath = entry.getKey();
            var instancePath = entry.getValue();

            var productMaster = ProductVersioningApiService.getProductMaster(instancePath.productMasterId, jwt);
            var stagedVariantSetsForLocale = productMaster.variantSets.staged.stream().filter(variant -> variant.localeId.equals(instancePath.localeId)).collect(Collectors.toList());
            var liveVariantSetForLocale = productMaster.variantSets.live.stream().filter(variant -> variant.localeId.equals(instancePath.localeId)).findFirst().orElseThrow(NoSuchElementException::new);

            for (var variantSet : stagedVariantSetsForLocale) {
                ProductPropertiesExportExcelData expectedRow;
                String instanceName;

                switch (instancePath.getProductLevel()) {
                    case GLOBAL:
                        var globalInstance = variantSet.instances.global;
                        if (globalInstance.propertySetId == null && globalInstance.digitalAssetSetId == null) {
                            continue;
                        }
                        expectedRow = generateExpectedRow(requestBody, globalInstance.propertySetId, globalInstance.digitalAssetSetId, friendlyInstancePath, liveVariantSetForLocale.instances.global.name, "");
                        expectedData.add(expectedRow);
                        break;

                    case RETAILER:
                        var retailerInstance = variantSet.instances.retailer.stream().filter(inst -> inst.retailerId.equals(instancePath.retailerId)).findFirst().orElse(null);
                        if (retailerInstance == null || (retailerInstance != null && (retailerInstance.propertySetId == null && retailerInstance.digitalAssetSetId == null))) {
                            continue;
                        }
                        var retailerInstanceLive = liveVariantSetForLocale.instances.retailer.stream().filter(inst -> inst.retailerId.equals(instancePath.retailerId)).findFirst().orElseThrow(NoSuchElementException::new);
                        expectedRow = generateExpectedRow(requestBody, retailerInstance.propertySetId, retailerInstance.digitalAssetSetId, friendlyInstancePath, retailerInstanceLive.name, retailerInstanceLive.rpc == null ? "" : retailerInstanceLive.rpc);
                        expectedData.add(expectedRow);

                        break;

                    case GLOBAL_CAMPAIGN:
                        var globalCampaignInstance = variantSet.instances.globalCampaign.stream().filter(inst -> inst.campaignId.equals(instancePath.campaignId)).findFirst().orElse(null);
                        if (globalCampaignInstance == null || (globalCampaignInstance != null && (globalCampaignInstance.propertySetId == null && globalCampaignInstance.digitalAssetSetId == null))) {
                            continue;
                        }
                        instanceName = liveVariantSetForLocale.instances.globalCampaign.stream().filter(inst -> inst.campaignId.equals(instancePath.campaignId)).findFirst().orElseThrow(NoSuchElementException::new).name;
                        expectedRow = generateExpectedRow(requestBody, globalCampaignInstance.propertySetId, globalCampaignInstance.digitalAssetSetId, friendlyInstancePath, instanceName, "");
                        expectedData.add(expectedRow);

                        break;

                    case RETAILER_CAMPAIGN:
                        var retailerCampaignInstance = variantSet.instances.retailerCampaign.stream().filter(inst -> inst.retailerId.equals(instancePath.retailerId) && inst.campaignId.equals(instancePath.campaignId)).findFirst().orElse(null);
                        if (retailerCampaignInstance == null || (retailerCampaignInstance != null && (retailerCampaignInstance.propertySetId == null && retailerCampaignInstance.digitalAssetSetId == null))) {
                            continue;
                        }
                        var retailerCampaignInstanceLive = liveVariantSetForLocale.instances.retailerCampaign.stream().filter(inst -> inst.retailerId.equals(instancePath.retailerId) && inst.campaignId.equals(instancePath.campaignId)).findFirst().orElseThrow(NoSuchElementException::new);
                        expectedRow = generateExpectedRow(requestBody, retailerCampaignInstance.propertySetId, retailerCampaignInstance.digitalAssetSetId, friendlyInstancePath, retailerCampaignInstanceLive.name, retailerCampaignInstanceLive.rpc == null ? "" : retailerCampaignInstanceLive.rpc);
                        expectedData.add(expectedRow);
                        break;
                }
            }
        }

        exportedData.forEach(row -> row.properties.sort(Comparator.comparing(o -> o.id)));
        expectedData.forEach(row -> row.properties.sort(Comparator.comparing(o -> o.id)));

        exportedData.sort(Comparator.comparing(o -> o.productIdentifier));
        expectedData.sort(Comparator.comparing(o -> o.productIdentifier));

        Assert.assertEquals(exportedData, expectedData, "Exported data doesn't match expected data");
    }

    private void addExpectedStandardPropertiesToRow(ExportProductPropertyRequestBody requestBody, String propertySetId, ProductPropertiesExportExcelData expectedRow) throws IOException {
        List<ProductVariantProperty> propertiesToAdd = new ArrayList<>();
        List<String> missingProperties = new ArrayList<>();

        var propertyIdsSpecified = requestBody.propertyIds != null && !requestBody.propertyIds.isEmpty();

        List<ProductVariantProperty> propertiesInSet = new ArrayList<>();

        if (propertySetId != null) {
            propertiesInSet = new ProductMasterVariantPropertySetCollection().getPropertySet(propertySetId).properties;
        }

        var companyPropertiesIds = company.companyProperties.propertySchema.stream().map(prop -> prop.id).collect(Collectors.toList());
        var idsToCheck = propertyIdsSpecified ? requestBody.propertyIds : companyPropertiesIds;

        if (!propertyIdsSpecified) {
            if (requestBody.type != null && requestBody.type.equals(Enums.ProductVariantType.STAGED.getType())) {
                var companyDigitalAssetsIds = company.companyProperties.digitalAssetPropertySchema.stream().map(prop -> prop.id).collect(Collectors.toList());
                idsToCheck.addAll(propertiesInSet.stream().filter(prop -> companyDigitalAssetsIds.contains(prop.id)).map(prop -> prop.id).collect(Collectors.toList()));
            }
        }

        for (var prop : idsToCheck) {
            var propertyToAdd = propertiesInSet.stream().filter(p -> p.id.equals(prop)).findFirst().orElse(null);
            if (propertyToAdd != null) {
                propertiesToAdd.add(propertyToAdd);
            } else {
                missingProperties.add(prop);
            }
        }

        addStandardPropertiesToExpectedRow(propertiesToAdd, expectedRow);
        addMissingPropertiesToExpectedRow(missingProperties, expectedRow);
    }

    private void addExpectedDigitalAssetPropertiesToRow(ExportProductPropertyRequestBody requestBody, String digitalAssetSetId, ProductPropertiesExportExcelData expectedRow) throws IOException {
        var digitalAssetIdsSpecified = requestBody.digitalAssetIds != null && !requestBody.digitalAssetIds.isEmpty();
        var companyDigitalAssetsIds = company.companyProperties.digitalAssetPropertySchema.stream().map(prop -> prop.id).collect(Collectors.toList());
        var digitalIdsToCheck = digitalAssetIdsSpecified ? new ArrayList<>(requestBody.digitalAssetIds) : new ArrayList<>(companyDigitalAssetsIds);

        digitalIdsToCheck.removeAll(expectedRow.properties.stream().filter(prop -> companyDigitalAssetsIds.contains(prop.id)).map(prop -> prop.id).collect(Collectors.toList()));

        List<DigitalAssetProperty> propertiesInSet = new ArrayList<>();

        if (digitalAssetSetId != null) {
            propertiesInSet = new ProductMasterVariantDigitalAssetCollection().getDigitalAsset(digitalAssetSetId).digitalAssets;
        }

        List<DigitalAssetProperty> digitalAssetsToAdd = new ArrayList<>();
        List<String> missingProperties = new ArrayList<>();

        for (var prop : digitalIdsToCheck) {
            var propertyToAdd = propertiesInSet.stream().filter(p -> p.id.equals(prop)).findFirst().orElse(null);
            if (propertyToAdd != null && companyDigitalAssetsIds.contains(propertyToAdd.id)) {
                digitalAssetsToAdd.add(propertyToAdd);
            } else {
                missingProperties.add(prop);
            }
        }

        addDigitalAssetsToExpectedRow(digitalAssetsToAdd, expectedRow);
        addMissingPropertiesToExpectedRow(missingProperties, expectedRow);
    }

    private ProductPropertiesExportExcelData generateExpectedRow(ExportProductPropertyRequestBody requestBody, String propertySetId, String digitalAssetSetId, UserFriendlyInstancePath friendlyInstancePath, String instanceName, String instanceRPC) throws IOException {
        var expectedRow = new ProductPropertiesExportExcelData();
        expectedRow.productIdentifier = friendlyInstancePath.productIdentifier;
        expectedRow.locale = friendlyInstancePath.localeName == null ? "" : friendlyInstancePath.localeName;
        expectedRow.retailer = friendlyInstancePath.retailerName == null ? "" : friendlyInstancePath.retailerName;
        expectedRow.campaign = friendlyInstancePath.campaignName == null ? "" : friendlyInstancePath.campaignName;
        expectedRow.productName = instanceName;
        expectedRow.rpc = instanceRPC;
        expectedRow.properties = new ArrayList<>();

        addExpectedStandardPropertiesToRow(requestBody, propertySetId, expectedRow);
        addExpectedDigitalAssetPropertiesToRow(requestBody, digitalAssetSetId, expectedRow);

        return expectedRow;
    }

    private void addMissingPropertiesToExpectedRow(List<String> missingProperties, ProductPropertiesExportExcelData expectedRow) {
        for (var property : missingProperties) {
            var propertyData = new ProductPropertiesExportExcelData.PropertyData();
            propertyData.id = property;
            propertyData.value = "";
            expectedRow.properties.add(propertyData);
        }
    }

    private void addStandardPropertiesToExpectedRow(List<ProductVariantProperty> standardProperties, ProductPropertiesExportExcelData expectedRow) {
        for (var property : standardProperties) {
            var propertyData = new ProductPropertiesExportExcelData.PropertyData();
            propertyData.id = property.id;

            var propertySettings = company.companyProperties.propertySchema.stream().filter(prop -> prop.id.equals(propertyData.id)).findFirst().orElse(null);

            if (propertySettings == null) {
                propertySettings = company.companyProperties.digitalAssetPropertySchema.stream().filter(prop -> prop.id.equals(propertyData.id)).findFirst().orElseThrow(NoSuchElementException::new);
            }

            if (propertySettings.type == Enums.PropertyType.DATE) {
                propertyData.value = "";
                for (var value : property.values) {
                    Instant instant = Instant.parse(value.toString());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy HH:mm:ss 'GMT'Z");
                    ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
                    propertyData.value = zonedDateTime.format(formatter) + " (Coordinated Universal Time)";
                }
            } else {
                propertyData.value = property.values.stream().map(object -> Objects.toString(object, "")).collect(Collectors.joining(" | "));
            }
            expectedRow.properties.add(propertyData);
        }
    }

    private void addDigitalAssetsToExpectedRow(List<DigitalAssetProperty> digitalAssetProperties, ProductPropertiesExportExcelData expectedRow) {
        for (var property : digitalAssetProperties) {
            var propertyData = new ProductPropertiesExportExcelData.PropertyData();
            propertyData.id = property.id;
            propertyData.value = property.assets.stream().map(a -> a.url).collect(Collectors.joining("|"));
            expectedRow.properties.add(propertyData);
        }
    }
}
