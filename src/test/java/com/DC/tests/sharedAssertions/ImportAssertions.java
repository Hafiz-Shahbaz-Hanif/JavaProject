package com.DC.tests.sharedAssertions;

import com.DC.db.productVersioning.ImportsCollection;
import com.DC.objects.productVersioning.ImportRecord;
import com.DC.pageobjects.adc.execute.productManager.properties.PropertiesPage;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.SharedRequests;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ImportResponse;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ImportTemplatesResponse;
import com.DC.utilities.apiEngine.routes.productVersioning.ProductVersioningRoutes;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.ImportModal;
import com.DC.utilities.sharedElements.ProductsImportModal;
import io.restassured.response.Response;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.awaitility.core.ConditionTimeoutException;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.constants.ProductVersioningConstants.PRODUCT_IMPORT_EXPORT_BASE_HEADERS;
import static com.DC.utilities.CommonApiMethods.callEndpoint;
import static com.DC.utilities.SharedMethods.downloadFileFromUrl;
import static com.DC.utilities.XLUtils.XLUtils.*;
import static java.util.Arrays.asList;
import static org.awaitility.Awaitility.await;

public abstract class ImportAssertions {

    public static void validateUserCannotImportFileWithInvalidParameters(Enums.ImportType importType, String importUrl, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.importFileProductVariantRepo(importUrl, "invalidType", jwt);

        validateUrlNotFoundError(response);

        String reqBody = "{\n\"url\": 123\n}";
        response = callEndpoint(
                ProductVersioningRoutes.getProductVariantImportRoutePath(importType.getImportType()),
                jwt,
                "POST",
                reqBody,
                ""
        );
        String expectedError = "\"url\" must be a string";
        validateInvalidRequestParametersError(response, Collections.singletonList(expectedError));
    }

    public static void validateUserCannotImportFileWithUnexpectedHeaders(Enums.ImportType importType, String importUrl, String jwt, String testCaseID) throws Exception {
        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importUrl, importType, testCaseID, jwt);

        Assert.assertEquals(importRecord.source, importUrl, "Import source didn't match with expected source");

        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, importRecord._id, jwt);
        importRecord = verifyImportMessageChangesToExpectedMessage("OK", importRecord._id);

        verifyDataIdsCountMatchWithCompletedIds(importRecord);

        String expectedError = "Invalid Properties in Import";
        List<String> expectedRow1 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, expectedError, "value", null);
        List<String> expectedRow2 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, importRecord.errors.get(0), null, "\"{}\"");
        verifyErrorReportContainsCorrectData(importRecord.errorReport.link, List.of(expectedRow1, expectedRow2));
    }

    public static void validateUserCanImportValidFile(Enums.ImportType importType, Enums.ImportTrackingType trackingType, String importUrl, String jwt, String testCaseID) throws Exception {
        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importUrl, importType, testCaseID, jwt);

        // TEST VARIANT IMPORTS COLLECTION
        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.SUCCESS, importRecord._id, jwt);
        importRecord = verifyImportMessageChangesToExpectedMessage("OK", importRecord._id);

        assert importRecord != null;
        Assert.assertEquals(importRecord.trackingType, trackingType, "Import type didn't match with expected type");
        Assert.assertEquals(importRecord.trackingSubType, importType, "Import subtype didn't match with expected subtype");
        Assert.assertEquals(importRecord.source, importUrl, "Import source didn't match with expected source");
        Assert.assertNull(importRecord.errorReport.link, "Error report generated for a valid import file");

        Assert.assertTrue(
                importRecord.errors.isEmpty(),
                "Error list was not empty:\n" + String.join("\n", importRecord.errors)
        );

        verifyDataIdsCountMatchWithCompletedIds(importRecord);
    }

    public static void validateUserCanImportKeywordFile(String importUrl, Enums.KeywordFlag keywordFlag, String jwt, String testCaseID) throws Exception {
        Response response = ProductVersioningApiRequests.importProductKeywords(importUrl, keywordFlag, jwt);
        ImportResponse responseBody = verifyEndpointReturnsCorrectObject(response, testCaseID, ImportResponse.class);

        Assert.assertNotNull(responseBody.importId, "Import id was null");
        ImportRecord importRecord = waitForImportToBeInDB(responseBody.importId);

        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.SUCCESS, importRecord._id, jwt);
        Assert.assertEquals(importRecord.source, importUrl, "Import source didn't match with expected source");
        Assert.assertTrue(
                importRecord.errors.isEmpty(),
                "Error list was not empty:\n" + String.join("\n", importRecord.errors)
        );

        verifyDataIdsCountMatchWithCompletedIds(importRecord);
    }

    public static void verifyErrorInImportRecordContainsExpectedMessage(ImportRecord importRecord, String expectedErrorMsg) {
        Assert.assertFalse(importRecord.errors.isEmpty(), "Error list was empty");
        Assert.assertTrue(importRecord.errors.get(0).contains(expectedErrorMsg), "Error didn't have the expected message");
    }

    public static ImportRecord waitForImportToBeInDB(String expectedImportRecordId) {
        var importsCollection = new ImportsCollection();
        long timeout = 3;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            ImportRecord[] importRecords = new ImportRecord[1];
            await().atMost(timeout, timeUnit).until(() -> {
                ImportRecord expectedImportRecord = importsCollection.getImportRecord(expectedImportRecordId);
                if (expectedImportRecord != null) {
                    importRecords[0] = expectedImportRecord;
                    return true;
                } else {
                    return false;
                }
            });
            return importRecords[0];
        } catch (ConditionTimeoutException exception) {
            Assert.fail("Import file was not generated within " + timeout + " " + timeUnit);
        }
        return null;
    }

    public static ImportResponse importFile(String url, Enums.ImportType importType, String testCaseID, String jwt) throws Exception {
        Response response = ProductVersioningApiRequests.importFileProductVariantRepo(url, importType, jwt);
        return verifyEndpointReturnsCorrectObject(response, testCaseID, ImportResponse.class);
    }

    public static ImportRecord verifyResponseBodyHasExpectedErrorAnReturnImportRecord(ImportResponse response, String expectedResponseMessage, String expectedError, String expectedDataUrl, String jwt) throws Exception {
        Assert.assertFalse(response.success, "Success property was set to true");
        Assert.assertEquals(response.message, expectedResponseMessage, "Response message didn't match with expected msg");
        Assert.assertEquals(response.data.errorMessage, expectedError, "Error msg in data object didn't match with expected error");
        Assert.assertEquals(response.data.url, expectedDataUrl, "Data url didn't match with expected url");
        return SharedRequests.getImportTrackingRecord(response.importId, jwt).getBody().as(ImportRecord.class);
    }

    public static ImportRecord verifyImportProcessIsInitiatedAndGetImportRecord(String url, Enums.ImportType importType, String testCaseID, String jwt) throws Exception {
        ImportResponse responseBody = importFile(url, importType, testCaseID, jwt);
        Assert.assertNotNull(responseBody.importId, "Import id was null");
        Assert.assertFalse(responseBody.importId.isEmpty(), "ImportId was empty");
        return SharedRequests.getImportTrackingRecord(responseBody.importId, jwt).getBody().as(ImportRecord.class);
    }

    public static ImportRecord verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus expectedStatus, String importId, String jwt) throws Exception {
        var importsCollection = new ImportsCollection();
        long timeout = 15;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            ImportRecord[] importRecords = new ImportRecord[1];
            await().atMost(timeout, timeUnit).until(() -> {
                ImportRecord expectedRecord = importsCollection.getImportRecord(importId);
                importRecords[0] = expectedRecord;
                return expectedRecord.status.equals(expectedStatus);
            });

            if (expectedStatus.equals(Enums.ProcessStatus.FAILED) || expectedStatus.equals(Enums.ProcessStatus.PARTIAL_FAILURE)) {
                SharedRequests.getErrorReportForImport(importRecords[0]._id, jwt).jsonPath().getString("data.url");
                return importsCollection.getImportRecord(importId);
            }

            return importRecords[0];
        } catch (ConditionTimeoutException exception) {
            Assert.fail("Status of import " + importId + " didn't change to " + expectedStatus + " within " + timeout + " " + timeUnit);
        }
        return null;
    }

    public static ImportRecord verifyImportMessageChangesToExpectedMessage(String expectedMessage, String importId) {
        var importsCollection = new ImportsCollection();
        long timeout = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            ImportRecord[] importRecords = new ImportRecord[1];
            await().atMost(timeout, timeUnit).until(() -> {
                ImportRecord expectedRecord = importsCollection.getImportRecord(importId);
                importRecords[0] = expectedRecord;
                return expectedRecord.message.equals(expectedMessage);
            });
            return importRecords[0];
        } catch (ConditionTimeoutException exception) {
            Assert.fail("Message for import with id " + importId +
                    " didn't change to " + expectedMessage +
                    " within " + timeout + " " + timeUnit
            );
        }
        return null;
    }

    public static void verifyDataIdsCountMatchWithCompletedIds(ImportRecord importRecord) {
        int expectedIdsCount = importRecord.stageStatusCounts.standardize.completed + importRecord.stageStatusCounts.standardize.failed;
        Assert.assertEquals(
                importRecord.standardizedDataIds.size(),
                expectedIdsCount,
                "Standardized data ids count didn't match with the expected ids count"
        );

        expectedIdsCount = importRecord.stageStatusCounts.publish.completed >= 1 ? 1 : 0;
        Assert.assertEquals(
                importRecord.publishedIds.size(),
                expectedIdsCount,
                "Published data ids count didn't match with the expected ids count"
        );
    }

    public static void verifyErrorReportContainsCorrectData(String errorReportLink, List<List<String>> expectedErrors) throws IOException {
        String filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/errorReportImports.xlsx";
        downloadFileFromUrl(errorReportLink, filePath);
        List<List<String>> sheetData = getSheetDataByRow(filePath, true);
        Assert.assertTrue(sheetData.size() >= 2, "Error report file was empty");

        List<String> expectedHeaders = asList("Stage", "Error Message", "Value");
        List<String> headersInReport = sheetData.remove(0);

        Assert.assertEquals(headersInReport, expectedHeaders, "Headers in error report don't match with expected headers");
        Assert.assertEquals(sheetData, expectedErrors, "Errors in report don't match with expected errors");
    }

    public static List<String> getExpectedErrorRowData(Enums.ImportStage stage, Integer rowNumber, String error, String data, String value) {
        value = value == null ? "\"N/A\"" : value;
        if (rowNumber == null) {
            return asList(stage.toString().toLowerCase(), error, value);
        } else {
            if (data == null) {
                error = "{\"rowNumber\":" + rowNumber + ",\"error\":\"" + error + "\",\"data\":" + null + "}";
            } else if (data.equals("{}")) {
                error = "{\"rowNumber\":" + rowNumber + ",\"error\":\"" + error + "\",\"data\":{}}";
            } else {
                error = "{\"rowNumber\":" + rowNumber + ",\"error\":\"" + error + "\",\"data\":\"" + data + "\"}";
            }
        }
        return asList(stage.toString().toLowerCase(), error, value);
    }

    public static void verifyLocaleRetailerAndCampaignColumnsAreDropdowns(XSSFSheet sheet, Company company) {
        SoftAssert softAssert = new SoftAssert();
        String rowRange = "2";
        String expectedRange = SharedMethods.convertNumberToAlphabetLetter(PRODUCT_IMPORT_EXPORT_BASE_HEADERS.indexOf("locale") + 1) + rowRange;
        List<String> dropdownValuesInRange = getCellDropdownValues(sheet, expectedRange);

        softAssert.assertEquals(
                dropdownValuesInRange,
                company.locales.stream().map(locale -> locale.localeName).collect(Collectors.toList()),
                "Dropdown values in locale column don't match with company locales"
        );

        expectedRange = SharedMethods.convertNumberToAlphabetLetter(PRODUCT_IMPORT_EXPORT_BASE_HEADERS.indexOf("retailer") + 1) + rowRange;
        dropdownValuesInRange = getCellDropdownValues(sheet, expectedRange);
        softAssert.assertEquals(
                dropdownValuesInRange,
                company.retailers.stream().map(retailer -> retailer.clientRetailerName).collect(Collectors.toList()),
                "Dropdown values in retailer column don't match with company retailers"
        );

        expectedRange = SharedMethods.convertNumberToAlphabetLetter(PRODUCT_IMPORT_EXPORT_BASE_HEADERS.indexOf("campaign") + 1) + rowRange;
        dropdownValuesInRange = getCellDropdownValues(sheet, expectedRange);
        softAssert.assertEquals(
                dropdownValuesInRange,
                company.campaigns.stream().map(campaign -> campaign.name.trim()).collect(Collectors.toList()),
                "Dropdown values in campaign column don't match with company campaigns"
        );

        softAssert.assertAll();
    }

    public static void verifyProductImportHasCorrectHeaders(XSSFSheet sheet, List<String> expectedBaseHeaders, List<String> expectedSubsequentColumns) {
        List<String> exportedHeaders = getSheetHeaders(sheet);
        List<String> expectedHeaders = Stream.of(expectedBaseHeaders, expectedSubsequentColumns)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Assert.assertEquals(exportedHeaders, expectedHeaders, "Incorrect headers in product import file");
    }

    public static void verifyCompanyPropertiesTemplateHasCorrectHeaders(ImportModal importModal, SoftAssert softAssert, String downloadFolder) throws Exception {
        var templateName = importModal.getExcelTemplateNameWithExtension();
        var expectedTemplateName = "properties.xlsx";
        softAssert.assertEquals(templateName, expectedTemplateName, "Template name is not as expected");

        importModal.downloadExcelTemplate();
        var templateFilePath = SharedMethods.isFileDownloaded("xlsx", templateName, 3, downloadFolder);

        List<String> exportedHeaders = getFileHeaders(templateFilePath);
        List<String> expectedHeaders = asList(
                "property id",
                "property name",
                "data type",
                "dropdown values",
                "property group",
                "help text",
                "multiple values"
        );
        softAssert.assertEquals(exportedHeaders, expectedHeaders, "Incorrect headers in company properties template file");
    }

    public static void verifyCompanyPropertiesWereImported(PropertiesPage propertiesPage, String expectedPropertyId, SoftAssert softAssert) {
        var maxNumberOfRetries = 5;
        for (int i = 1; i <= maxNumberOfRetries; i++) {
            propertiesPage = propertiesPage.refreshPage(PropertiesPage.class);
            var propertyWasImported = propertiesPage.searchForProperty(expectedPropertyId)
                    .isPropertyDisplayed(expectedPropertyId);
            if (propertyWasImported) {
                break;
            } else if (!propertyWasImported && i == maxNumberOfRetries) {
                softAssert.fail("Property: " + expectedPropertyId + " was not imported after searching for it " + maxNumberOfRetries + " times");
            }
        }
    }

    public static void verifyProductTemplateHasCorrectHeaders(Enums.ImportType importType, ProductsImportModal importModal, SoftAssert softAssert, String downloadFolder, String jwt) throws Exception {
        importModal.selectTypeOfImport(importType);

        var templateNameWithExtension = importModal.getExcelTemplateNameWithExtension();
        var templateName = templateNameWithExtension.replace(".xlsx", "");
        var expectedNamePrefix = importType.equals(Enums.ImportType.PROPERTY) ? "ProductPropertyImport" : "ProductKeywordImport";
        softAssert.assertTrue(templateNameWithExtension.startsWith(expectedNamePrefix), "Template " + templateNameWithExtension + " does not start with " + expectedNamePrefix);

        Response response;
        if (importType.equals(Enums.ImportType.PROPERTY)) {
            response = SharedRequests.getProductPropertiesImportTemplate(jwt);
        } else {
            response = SharedRequests.getProductKeywordsImportTemplate(jwt);
        }

        var templateResponse = response.getBody().as(ImportTemplatesResponse.class);
        String filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/" + templateNameWithExtension;
        downloadFileFromUrl(templateResponse.data.s3Link, filePath);

        XSSFWorkbook workbook = getWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);
        List<String> expectedHeaders = getSheetHeaders(sheet);

        importModal.downloadExcelTemplate();
        var templateFilePath = SharedMethods.isFileDownloaded("xlsx", templateName, 3, downloadFolder);

        List<String> exportedHeaders = getFileHeaders(templateFilePath);
        softAssert.assertEquals(
                exportedHeaders,
                expectedHeaders,
                "Headers in exported template are not as expected" + "\nExpected headers: " + expectedHeaders + "\nExported headers: " + exportedHeaders
        );
    }

    public static <T> T verifyProductPropertyImportModalWorks(String invalidFilePath, String validFilePath, ProductsImportModal importModal, SoftAssert softAssert, String downloadFolder, Class<T> clazz) throws Exception {
        importModal.uploadFile(invalidFilePath);
        var invalidFileErrorDisplayed = importModal.isInvalidFileErrorDisplayed();
        softAssert.assertTrue(invalidFileErrorDisplayed, "Invalid file error was not displayed after uploading an invalid file");

        var fileNameWithExtension = java.nio.file.Paths.get(validFilePath).getFileName().toString();
        var fileName = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));
        importModal.uploadFile(validFilePath);

        var saveAndExitModalEnabled = importModal.isSaveAndExitButtonEnabled();
        softAssert.assertTrue(saveAndExitModalEnabled, "Save and exit button was not enabled after uploading a valid file");

        var linkToPreviewFile = importModal.getLinkToPreview(fileNameWithExtension);
        softAssert.assertTrue(linkToPreviewFile.endsWith(fileNameWithExtension), "Link to preview file does not end with the file name. Preview might not have the correct data");
        importModal.downloadFilePreview(fileNameWithExtension);

        var fileDownloaded = SharedMethods.isFileDownloaded("xlsx", fileName, 5, downloadFolder);
        softAssert.assertNotNull(fileDownloaded, "File was not downloaded after clicking on preview");

        importModal.clickSaveAndExitButton();
        return importModal.getPage(clazz);
    }

    public static void verifyMasterProductDataTemplateHasCorrectHeaders(ProductsImportModal importModal, SoftAssert softAssert, String downloadFolder, String jwt) throws Exception {
        importModal.selectTypeOfImport(null);

        var templateNameWithExtension = importModal.getExcelTemplateNameWithExtension();
        var templateName = templateNameWithExtension.replace(".xlsx", "");
        var expectedTemplateName = "MasterDataImport";
        softAssert.assertEquals(templateName, expectedTemplateName, "Template to import master product data doesn't match with expected name");

        var expectedHeaders = new ArrayList<>(List.of("product identifier", "product name", "thumbnail"));

        importModal.downloadExcelTemplate();
        var templateFilePath = SharedMethods.isFileDownloaded("xlsx", templateName, 3, downloadFolder);

        List<String> exportedHeaders = getFileHeaders(templateFilePath);
        softAssert.assertEquals(
                exportedHeaders,
                expectedHeaders,
                "Headers in exported template are not as expected" + "\nExpected headers: " + expectedHeaders + "\nExported headers: " + exportedHeaders
        );
    }

}
