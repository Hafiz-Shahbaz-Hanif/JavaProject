package com.DC.apitests.productversioning.imports;

import com.DC.apitests.ApiValidations;
import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.constants.ProductVersioningConstants;
import com.DC.db.productVersioning.CompanyCollection;
import com.DC.objects.productVersioning.ImportRecord;
import com.DC.objects.productVersioning.ProductKeywordsDataInExcel;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.XLUtils.ProductKeywordsXLUtils;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.SharedRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.log4testng.Logger;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.constants.ProductVersioningConstants.PRODUCT_IMPORT_EXPORT_BASE_HEADERS;
import static com.DC.tests.sharedAssertions.ImportAssertions.*;
import static com.DC.utilities.SecurityAPI.changeInsightsCompanyAndGetJwt;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.SharedMethods.downloadFileFromUrl;
import static com.DC.utilities.XLUtils.ProductKeywordsXLUtils.getProductKeywordsInFile;
import static com.DC.utilities.XLUtils.ProductKeywordsXLUtils.getWholeKeywordSetWithNullsAsEmptyArray;
import static com.DC.utilities.XLUtils.XLUtils.getCellDropdownValues;
import static com.DC.utilities.XLUtils.XLUtils.getWorkbook;

public class ProductKeywordsImportApiTests extends BaseClass {

    private static Logger logger;

    private static String jwt;

    private static Company company;

    private static final Enums.ImportType IMPORT_TYPE = Enums.ImportType.KEYWORD;

    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    private static final CompanyCollection COMPANY_COLLECTION = new CompanyCollection();

    private static final String TEST_DATA_DIRECTORY = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/";

    private static final String VALID_DATA_IMPORT_URL = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductKeywords_Success.xlsx";

    private static final String CLEAN_UP_IMPORT_URL = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductKeywords_Empty.xlsx";

    private static final String EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS = "Unable to perform Keyword Property Import";

    ProductKeywordsImportApiTests() {
        logger = Logger.getLogger(ProductKeywordsImportApiTests.class);
        PropertyConfigurator.configure("log4j.properties");

    }

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        logger.info("Setting up keyword import api tests");
        company = COMPANY_COLLECTION.getCompany(TEST_CONFIG.companyID);
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        jwt = changeInsightsCompanyAndGetJwt(jwt, company._id, company.name);
        ProductVersioningApiRequests.importProductKeywords(CLEAN_UP_IMPORT_URL, Enums.KeywordFlag.REPLACE, jwt);
    }

    @AfterClass()
    public void cleanupTests() throws Exception {
        ProductVersioningApiRequests.importProductKeywords(CLEAN_UP_IMPORT_URL, Enums.KeywordFlag.REPLACE, jwt);
    }

    @Test(
            groups = {"ProductKeywordsImportApiTests", "NoDataProvider"},
            description = "Verify keywordFlag is optional. 'Add' is default value",
            priority = 1
    )
    public void Api_ProductKeywordsImport_CanImportProductKeywords_NoFlag() throws Exception {
        performImportTestWithAddFlag(VALID_DATA_IMPORT_URL, true);
    }

    @Test(
            groups = {"ProductKeywordsImportApiTests", "NoDataProvider"},
            description = "Verify all keywords are replaced when flag is set to replace",
            priority = 2
    )
    public void Api_ProductKeywordsImport_CanImportProductKeywords_ReplaceFlag() throws Exception {
        String fileToImport = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductKeywords_Success_Replace.xlsx";
        String path = TEST_DATA_DIRECTORY + Paths.get(new URI(fileToImport).getPath()).getFileName().toString();
        downloadFileFromUrl(fileToImport, path);
        List<ProductKeywordsDataInExcel> dataInExcelFile = getProductKeywordsInFile(path);

        List<List<ProductKeywordsDataInExcel>> groupedRows = new ArrayList<>(dataInExcelFile.stream()
                .collect(Collectors.groupingBy(row -> Arrays.asList(row.productIdentifier, row.locale, row.campaign, row.retailer)))
                .values());

        List<ProductVariantKeywordSet> variantKeywordSetsBefore = new ArrayList<>();

        for (List<ProductKeywordsDataInExcel> group : groupedRows) {
            ProductVariantKeywordSet currentKeywordSetInProduct = getInstanceKeywordSet(group.get(0));
            variantKeywordSetsBefore.add(currentKeywordSetInProduct);
        }

        validateUserCanImportKeywordFile(fileToImport, Enums.KeywordFlag.REPLACE, jwt, testMethodName.get());

        for (int index = 0; index < groupedRows.size() - 1; index++) {
            List<ProductKeywordsDataInExcel> group = groupedRows.get(index);
            ProductVariantKeywordSet currentKeywordSetInProduct = getInstanceKeywordSet(group.get(0));
            Assert.assertNotEquals(variantKeywordSetsBefore.get(index), currentKeywordSetInProduct, "Keyword set was not updated after import");

            ProductVariantKeywords expectedKeywordSet = getWholeKeywordSetWithNullsAsEmptyArray(group);
            Assert.assertEquals(currentKeywordSetInProduct.keywords, expectedKeywordSet, "Expected keywords were not imported to product");
        }
    }

    @Test(
            groups = {"ProductKeywordsImportApiTests", "NoDataProvider"},
            description = "Verify keywords are added to existing keywords when flag is set to add",
            priority = 3
    )
    public void Api_ProductKeywordsImport_CanImportProductKeywords_AddFlag() throws Exception {
        String fileToImport = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductKeywords_Success_Add.xlsx";
        performImportTestWithAddFlag(fileToImport, false);
    }

    @Test(groups = {"ProductKeywordsImportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsImport_CannotImportProductKeywords_InvalidData() throws Exception {
        var productMasterInstancePath = new UserFriendlyInstancePath("QA-IMPORTS-001", "es-MX", "Amazon.com", null)
                .convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE);
        var keywordSetBefore = ProductVersioningApiService.getProductKeywordSet(productMasterInstancePath, null, jwt);

        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductKeywords_InvalidData.xlsx";

        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);
        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, importRecord._id, jwt);

        Assert.assertEquals(importRecord.source, importUrl, "Import source didn't match with expected source");

        List<String> expectedRow1 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, "Invalid Keyword Bucket inside Keyword Import", null, null);
        List<String> expectedRow2 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 3, "Invalid retailer included for company", "invalid", null);
        List<String> expectedRow3 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 4, "Invalid Campaign found for company", "invalid", null);
        List<String> expectedRow4 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 5, "Invalid Locale Found for Company", "invalid", null);
        List<String> expectedRow5 = getExpectedErrorRowData(Enums.ImportStage.PUBLISH, null, importRecord.errors.get(0), null, "\"{}\"");
        verifyErrorReportContainsCorrectData(importRecord.errorReport.link, Arrays.asList(expectedRow1, expectedRow2, expectedRow3, expectedRow4, expectedRow5));

        var keywordSetAfter = ProductVersioningApiService.getProductKeywordSet(productMasterInstancePath, null, jwt);
        Assert.assertEquals(keywordSetAfter.keywords, keywordSetBefore.keywords, "Product was updated after importing invalid file");

    }

    @Test(groups = {"ProductKeywordsImportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsImport_CannotImportProductKeywords_DuplicateVariantInImport() throws Exception {
        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductKeywords_Duplicates.xlsx";

        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);
        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, importRecord._id, jwt);

        String expectedError = "Duplicate Variant targeted in Import";
        List<String> expectedRow1 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, expectedError, null, null);
        List<String> expectedRow2 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 3, expectedError, null, null);
        List<String> expectedRow3 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, importRecord.errors.get(0), null, "\"{}\"");
        verifyErrorReportContainsCorrectData(importRecord.errorReport.link, List.of(expectedRow1, expectedRow2, expectedRow3));
    }

    @Test(groups = {"ProductKeywordsImportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsImport_CannotImportProductKeywords_MissingRequiredHeader() throws Exception {
        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductKeywords_MissingHeader.xlsx";
        String expectedErrorMsg = "Type: ImportsCoreError. Subtype: NullIdentifierValidationError. Message: product identifier or locale not defined for import.";

        ImportResponse importResponse = importFile(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);
        ImportRecord importRecord = verifyResponseBodyHasExpectedErrorAnReturnImportRecord(importResponse, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS, expectedErrorMsg, importUrl, jwt);

        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, importRecord._id, jwt);

        verifyErrorInImportRecordContainsExpectedMessage(importRecord, expectedErrorMsg);

        String fullExpectedErrorMessage = importRecord.getImportFailedJobCountErrorMessage(importRecord.standardizedDataIds.get(0));
        String expectedErrorObjectName = "\"{\\\"name\\\":\\\"ImportsCoreError:NullIdentifierValidationError\\\"}\"";

        List<String> expectedRow = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, fullExpectedErrorMessage, null, expectedErrorObjectName);
        verifyErrorReportContainsCorrectData(importRecord.errorReport.link, Collections.singletonList(expectedRow));
    }

    @Test(groups = {"ProductKeywordsImportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsImport_CannotImportProductKeywords_UnauthorizedErrorIsThrown() throws Exception {
        String unauthorizedUser = "qa+productmanagerviewonly@juggle.com";
        String jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, unauthorizedUser, TEST_CONFIG.password);
        Response response = ProductVersioningApiRequests.importFileProductVariantRepo(VALID_DATA_IMPORT_URL, IMPORT_TYPE, jwt);
        validateUnauthorizedError(response);
    }

    @Test(groups = {"ProductKeywordsImportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsImport_CannotInitiateImportProcess_InvalidParameters() throws Exception {
        validateUserCannotImportFileWithInvalidParameters(IMPORT_TYPE, VALID_DATA_IMPORT_URL, jwt);
    }

    @Test(groups = {"ProductKeywordsImportApiTests", "NoDataProvider"})
    public void Api_ProductKeywordsImport_ImportTemplateHasCorrectData() throws Exception {
        Response response = SharedRequests.getProductKeywordsImportTemplate(jwt);
        ImportTemplatesResponse templateResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ImportTemplatesResponse.class);

        Assert.assertEquals(templateResponse.companyId, TEST_CONFIG.companyID,
                "Company id in the response doesn't match with the expected company id"
        );
        Assert.assertNotNull(templateResponse.data.s3Link, "S3 link in the response is null");

        String filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/importProductKeywordsTemplate.xlsx";
        downloadFileFromUrl(templateResponse.data.s3Link, filePath);

        XSSFWorkbook workbook = getWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);

        verifyProductImportHasCorrectHeaders(sheet, PRODUCT_IMPORT_EXPORT_BASE_HEADERS, ProductKeywordsXLUtils.SUBSEQUENT_COLUMNS);

        int expectedDropdownsCount = 4;
        Assert.assertEquals(
                sheet.getDataValidations().size(),
                expectedDropdownsCount,
                "Number of dropdowns in the template doesn't match with the expected number of dropdowns"
        );

        String rowRange = "2";
        String expectedRange = SharedMethods.convertNumberToAlphabetLetter(ProductKeywordsXLUtils.EXPECTED_HEADERS_IN_EXPORT_FILE.indexOf("keyword bucket") + 1) + rowRange;
        List<String> dropdownValuesInRange = getCellDropdownValues(sheet, expectedRange);

        Assert.assertEquals(
                dropdownValuesInRange,
                ProductVersioningConstants.PRODUCT_KEYWORD_BUCKETS,
                "Dropdown values in keyword bucket column don't match with the expected bucket types"
        );

        verifyLocaleRetailerAndCampaignColumnsAreDropdowns(sheet, company);
        workbook.close();
    }

    private static ProductVariantKeywordSet getInstanceKeywordSet(ProductKeywordsDataInExcel firstRowInGroup) throws Exception {
        String localeId = COMPANY_COLLECTION.getLocaleId(company, firstRowInGroup.locale);
        String campaignId = firstRowInGroup.campaign != null ? COMPANY_COLLECTION.getCampaignId(company, firstRowInGroup.campaign) : null;
        String retailerId = firstRowInGroup.retailer != null ? COMPANY_COLLECTION.getRetailerId(company, firstRowInGroup.retailer) : null;

        var productMasterInstancePath = new ProductVariantInstancePath(firstRowInGroup.productIdentifier, localeId, Enums.ProductVariantType.LIVE, retailerId, campaignId);
        return ProductVersioningApiService.getProductKeywordSetByUniqueId(productMasterInstancePath, null, jwt);
    }

    private void performImportTestWithAddFlag(String importUrl, boolean defaultFlag) throws Exception {
        String path = TEST_DATA_DIRECTORY + Paths.get(new URI(importUrl).getPath()).getFileName().toString();
        downloadFileFromUrl(importUrl, path);
        List<ProductKeywordsDataInExcel> dataInExcelFile = getProductKeywordsInFile(path);

        List<List<ProductKeywordsDataInExcel>> groupedRows = new ArrayList<>(dataInExcelFile.stream()
                .collect(Collectors.groupingBy(row -> Arrays.asList(row.productIdentifier, row.locale, row.campaign, row.retailer)))
                .values());

        List<ProductVariantKeywordSet> variantKeywordSetsBefore = new ArrayList<>();

        for (List<ProductKeywordsDataInExcel> group : groupedRows) {
            ProductVariantKeywordSet currentKeywordSetInProduct = getInstanceKeywordSet(group.get(0));
            variantKeywordSetsBefore.add(currentKeywordSetInProduct);
        }

        if (defaultFlag) {
            validateUserCanImportValidFile(IMPORT_TYPE, Enums.ImportTrackingType.FILE, VALID_DATA_IMPORT_URL, jwt, testMethodName.get());
        } else {
            validateUserCanImportKeywordFile(importUrl, Enums.KeywordFlag.ADD, jwt, testMethodName.get());
        }

        for (int index = 0; index < groupedRows.size() - 1; index++) {
            List<ProductKeywordsDataInExcel> group = groupedRows.get(index);
            ProductVariantKeywordSet currentKeywordSetInProduct = getInstanceKeywordSet(group.get(0));
            ProductVariantKeywords keywordsInProductBefore = variantKeywordSetsBefore.get(index).keywords;

            Assert.assertNotEquals(variantKeywordSetsBefore.get(index), currentKeywordSetInProduct, "Keyword set was not updated after import");

            ProductVariantKeywords expectedKeywordSet = getWholeKeywordSetWithNullsAsEmptyArray(group);
            expectedKeywordSet.title.addAll(keywordsInProductBefore.title);
            expectedKeywordSet.onPage.addAll(keywordsInProductBefore.onPage);
            expectedKeywordSet.optional.addAll(keywordsInProductBefore.optional);
            expectedKeywordSet.reserved.addAll(keywordsInProductBefore.reserved);
            expectedKeywordSet.branded.addAll(keywordsInProductBefore.branded);
            expectedKeywordSet.hidden.addAll(keywordsInProductBefore.hidden);
            expectedKeywordSet.unused.addAll(keywordsInProductBefore.unused);
            expectedKeywordSet.rankTracking.addAll(keywordsInProductBefore.rankTracking);

            Assert.assertEqualsNoOrder(currentKeywordSetInProduct.keywords.title.toArray(), expectedKeywordSet.title.toArray(), "Expected title keywords were not imported to product");
            Assert.assertEqualsNoOrder(currentKeywordSetInProduct.keywords.onPage.toArray(), expectedKeywordSet.onPage.toArray(), "Expected onPage keywords were not imported to product");
            Assert.assertEqualsNoOrder(currentKeywordSetInProduct.keywords.optional.toArray(), expectedKeywordSet.optional.toArray(), "Expected optional keywords were not imported to product");
            Assert.assertEqualsNoOrder(currentKeywordSetInProduct.keywords.reserved.toArray(), expectedKeywordSet.reserved.toArray(), "Expected reserved keywords were not imported to product");
            Assert.assertEqualsNoOrder(currentKeywordSetInProduct.keywords.branded.toArray(), expectedKeywordSet.branded.toArray(), "Expected branded keywords were not imported to product");
            Assert.assertEqualsNoOrder(currentKeywordSetInProduct.keywords.hidden.toArray(), expectedKeywordSet.hidden.toArray(), "Expected hidden keywords were not imported to product");
            Assert.assertEqualsNoOrder(currentKeywordSetInProduct.keywords.unused.toArray(), expectedKeywordSet.unused.toArray(), "Expected unused keywords were not imported to product");
            Assert.assertEqualsNoOrder(currentKeywordSetInProduct.keywords.rankTracking.toArray(), expectedKeywordSet.rankTracking.toArray(), "Expected rankTracking keywords were not imported to product");
        }
    }
}
