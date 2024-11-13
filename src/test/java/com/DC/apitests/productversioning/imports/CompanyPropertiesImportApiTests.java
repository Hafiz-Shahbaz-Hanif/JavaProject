package com.DC.apitests.productversioning.imports;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.objects.productVersioning.ImportRecord;
import com.DC.testcases.BaseClass;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;

import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.DC.apitests.ApiValidations.validateUnauthorizedError;
import static com.DC.tests.sharedAssertions.ImportAssertions.*;
import static com.DC.utilities.SecurityAPI.changeInsightsCompanyAndGetJwt;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.SharedMethods.downloadFileFromUrl;
import static com.DC.utilities.XLUtils.CompanyPropertiesXLUtils.getCompanyPropertiesInImportFile;
import static com.DC.utilities.productManager.ProductVersioningCommonMethods.generateImageMappingProperties;
import static java.util.Arrays.asList;

public class CompanyPropertiesImportApiTests extends BaseClass {
    private static final Enums.ImportType IMPORT_TYPE = Enums.ImportType.COMPANY;
    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();
    private static final String testDataDirectory = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/";
    private static final String FILE_VALID_DATA = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportCompanyProperties_Success.xlsx";
    private static final String EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS = "Unable to perform Company Property Import";
    private static Logger logger;
    private static String jwt;

    CompanyPropertiesImportApiTests() {
        logger = Logger.getLogger(CompanyPropertiesImportApiTests.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        logger.info("Setting up company properties import api tests");
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        jwt = changeInsightsCompanyAndGetJwt(jwt, TEST_CONFIG.companyID, TEST_CONFIG.companyName);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTests() throws Exception {
        logger.info("Cleaning company properties import api tests");
        cleanupCompany();
    }

    @Test(groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesImport_CanImportCompanyProperties() throws Exception {
        var companyBefore = CompanyApiService.getCompanyWithProperties(jwt);

        validateUserCanImportValidFile(IMPORT_TYPE, Enums.ImportTrackingType.FILE, FILE_VALID_DATA, jwt, testMethodName.get());

        String filePath = testDataDirectory + Paths.get(new URI(FILE_VALID_DATA).getPath()).getFileName().toString();
        downloadFileFromUrl(FILE_VALID_DATA, filePath);

        List<CompanyProperties.Property> propertiesInImportFile = getCompanyPropertiesInImportFile(filePath);

        var companyAfterImport = CompanyApiService.getCompanyWithProperties(jwt);

        List<CompanyProperties.DigitalAssetCompanyProperty> expectedImportedDigitalAssetProperties = new ArrayList<>();
        List<CompanyProperties.Property> expectedImportedStandardProperties = new ArrayList<>();
        List<CompanyProperties.Group> expectedNormalGroups = new ArrayList<>(companyBefore.companyProperties.groups);
        List<CompanyProperties.Group> expectedDigitalAssetGroups = new ArrayList<>(companyBefore.companyProperties.groupsDigitalAssets);

        for (CompanyProperties.Property property : propertiesInImportFile) {

            if (property.type != Enums.PropertyType.DIGITAL_ASSET) {
                expectedImportedStandardProperties.add(property);
                generateExpectedGroups(expectedNormalGroups, property);
            } else {
                List<CompanyPropertiesBase.Property> imageMappingProperties = generateImageMappingProperties(property.id, property.name);
                CompanyPropertiesBase.DigitalAssetCompanyProperty digitalAssetCompanyProperty = generateExpectedDigitalAssetProperty(property, imageMappingProperties);

                expectedImportedDigitalAssetProperties.add(digitalAssetCompanyProperty);
                expectedImportedStandardProperties.addAll(imageMappingProperties);

                generateExpectedGroups(expectedNormalGroups, imageMappingProperties.get(0));
                generateExpectedGroups(expectedNormalGroups, imageMappingProperties.get(1));
                generateExpectedGroups(expectedDigitalAssetGroups, digitalAssetCompanyProperty);
            }
        }

        verifyCorrectNormalPropertiesWereImported(companyBefore.companyProperties.propertySchema, companyAfterImport.companyProperties.propertySchema, expectedImportedStandardProperties);
        verifyCorrectDigitalAssetsPropertiesWereImported(companyBefore.companyProperties.digitalAssetPropertySchema, companyAfterImport.companyProperties.digitalAssetPropertySchema, expectedImportedDigitalAssetProperties);
        verifyCorrectGroupsWereImported(companyAfterImport.companyProperties, expectedNormalGroups, expectedDigitalAssetGroups);

        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportCompanyProperties_SuccessTwo.xlsx";
        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);
        verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.SUCCESS, importRecord._id, jwt);

        filePath = testDataDirectory + Paths.get(new URI(importUrl).getPath()).getFileName().toString();
        downloadFileFromUrl(importUrl, filePath);

        propertiesInImportFile = getCompanyPropertiesInImportFile(filePath);

        String previousCompanyPropertiesId = companyAfterImport.companyPropertiesId;
        var companyAfterSecondImport = CompanyApiService.getCompanyWithProperties(jwt);
        Assert.assertNotEquals(previousCompanyPropertiesId, companyAfterSecondImport.companyPropertiesId, "Company property set didn't change");

        for (CompanyProperties.Property property : propertiesInImportFile) {
            if (property.type != Enums.PropertyType.DIGITAL_ASSET) {
                expectedImportedStandardProperties.stream()
                        .filter(prop -> prop.id.equals(property.id))
                        .findFirst().ifPresent(expectedImportedStandardProperties::remove);

                expectedImportedStandardProperties.add(property);

                generateExpectedGroups(expectedNormalGroups, property);
            } else {
                List<CompanyPropertiesBase.Property> imageMappingProperties = generateImageMappingProperties(property.id, property.name);
                CompanyPropertiesBase.DigitalAssetCompanyProperty digitalAssetCompanyProperty = generateExpectedDigitalAssetProperty(property, imageMappingProperties);

                expectedImportedStandardProperties.removeIf(prop -> prop.id.equals(imageMappingProperties.get(0).id) | prop.id.equals(imageMappingProperties.get(1).id));
                expectedImportedDigitalAssetProperties.removeIf(prop -> prop.id.equals(digitalAssetCompanyProperty.id));

                expectedImportedDigitalAssetProperties.add(digitalAssetCompanyProperty);
                expectedImportedStandardProperties.addAll(imageMappingProperties);

                generateExpectedGroups(expectedNormalGroups, imageMappingProperties.get(0));
                generateExpectedGroups(expectedNormalGroups, imageMappingProperties.get(1));
                generateExpectedGroups(expectedDigitalAssetGroups, digitalAssetCompanyProperty);
            }
        }

        verifyCorrectNormalPropertiesWereImported(companyBefore.companyProperties.propertySchema, companyAfterSecondImport.companyProperties.propertySchema, expectedImportedStandardProperties);
        verifyCorrectDigitalAssetsPropertiesWereImported(companyBefore.companyProperties.digitalAssetPropertySchema, companyAfterSecondImport.companyProperties.digitalAssetPropertySchema, expectedImportedDigitalAssetProperties);
        verifyCorrectGroupsWereImported(companyAfterSecondImport.companyProperties, expectedNormalGroups, expectedDigitalAssetGroups);
    }

    @Test(
            groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"},
            description = "File to import contains some bad data. API should import valid data and throw partial failure"
    )
    public void Api_CompanyPropertiesImport_CanImportSomeCompanyProperties_PartialFailure() throws Exception {
        var company = CompanyApiService.getCompanyWithProperties(jwt);
        var propertySchemaBefore = company.companyProperties.propertySchema;

        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportCompanyProperties_PartialFailure.xlsx";
        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);

        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.PARTIAL_FAILURE, importRecord._id, jwt);

        Assert.assertEquals(importRecord.publishedIds.size(), 1, "Expected published ids count didn't match with expected count");

        List<String> expectedRow1 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, "Invalid Data Type found for Company in Import", "invalid", null);
        List<String> expectedRow2 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, "Invalid Data Type found for Company Groups in Import", "{}", null);
        verifyErrorReportContainsCorrectData(importRecord.errorReport.link, asList(expectedRow1, expectedRow2));

        String filePath = testDataDirectory + Paths.get(new URI(importUrl).getPath()).getFileName().toString();
        downloadFileFromUrl(importUrl, filePath);
        List<CompanyProperties.Property> propertiesInImportFile = getCompanyPropertiesInImportFile(filePath);

        company = CompanyApiService.getCompanyWithProperties(jwt);
        verifyCorrectNormalPropertiesWereImported(propertySchemaBefore, company.companyProperties.propertySchema, List.of(propertiesInImportFile.get(propertiesInImportFile.size() - 1)));
    }

    @Test(groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesImport_CannotImportCompanyProperties_InvalidData() throws Exception {
        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportCompanyProperties_InvalidData.xlsx";
        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);
        var errorReportLink = CompanyApiRequests.getErrorReportForImport(importRecord._id, jwt).jsonPath().getString("data.url");
        String errorInImportRecord = importRecord.errors.get(0);

        String errorMessageInFailedJob = importRecord.getImportFailedJobCountErrorMessage();
        Assert.assertEquals(errorMessageInFailedJob, errorInImportRecord, "Error message in failed job doesn't match with expected error");

        List<String> expectedRow1 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, "Invalid Data Type found for Company in Import", "invalid", null);
        List<String> expectedRow2 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, "Invalid Data Type found for Company Groups in Import", "{}", null);
        List<String> expectedRow3 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 3, "Invalid Data Type found for Company Groups in Import", "{}", null);
        List<String> expectedRow4 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, "Data failed validation: Cannot add dropdown values to a property of a different type.", null, null);
        List<String> expectedRow5 = getExpectedErrorRowData(Enums.ImportStage.PUBLISH, null, errorInImportRecord, null, "\"{}\"");
        verifyErrorReportContainsCorrectData(errorReportLink, asList(expectedRow1, expectedRow2, expectedRow3, expectedRow4, expectedRow5));
    }

    @Test(groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesImport_CannotImportCompanyProperties_DuplicateIds() throws Exception {
        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportCompanyProperties_DuplicateIds.xlsx";

        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);
        var errorReportLink = CompanyApiRequests.getErrorReportForImport(importRecord._id, jwt).jsonPath().getString("data.url");

        String expectedError = "Duplicate Company Property Ids in Import";
        List<String> expectedRow1 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, expectedError, null, null);
        List<String> expectedRow2 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 3, expectedError, null, null);
        List<String> expectedRow3 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, importRecord.errors.get(0), null, "\"{}\"");
        verifyErrorReportContainsCorrectData(errorReportLink, asList(expectedRow1, expectedRow2, expectedRow3));
    }

    @Test(groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesImport_CannotImportCompanyProperties_DuplicateNames() throws Exception {
        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportCompanyProperties_DuplicateNames.xlsx";

        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);
        var errorReportLink = CompanyApiRequests.getErrorReportForImport(importRecord._id, jwt).jsonPath().getString("data.url");

        String expectedError = "Duplicate Company Property Name in Import";
        List<String> expectedRow1 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, expectedError, null, null);
        List<String> expectedRow2 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 3, expectedError, null, null);
        List<String> expectedRow3 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, importRecord.errors.get(0), null, "\"{}\"");
        verifyErrorReportContainsCorrectData(errorReportLink, asList(expectedRow1, expectedRow2, expectedRow3));
    }

    @Test(groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesImport_CannotImportCompanyProperties_DuplicateGroupsAcrossGroupTypes() throws Exception {
        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportCompanyProperties_DuplicateGroupsAcrossGroupType.xlsx";
        String expectedErrorMsg = "Duplicate names detected across groups types: Duplicate Import Group";

        ImportResponse response = importFile(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);
        ImportRecord importRecord = verifyResponseBodyHasExpectedErrorAnReturnImportRecord(response, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS, expectedErrorMsg, importUrl, jwt);
        verifyErrorInImportRecordContainsExpectedMessage(importRecord, expectedErrorMsg);
    }

    @Test(groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesImport_CannotImportCompanyProperties_UnexpectedHeader() throws Exception {
        String importWithUnexpectedHeader = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportCompanyProperties_UnexpectedHeader.xlsx";

        String expectedErrorMsg = "Invalid headers in import: invalid header";
        ImportResponse importResponse = importFile(importWithUnexpectedHeader, IMPORT_TYPE, testMethodName.get(), jwt);

        ImportRecord importRecord = verifyResponseBodyHasExpectedErrorAnReturnImportRecord(importResponse, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS, expectedErrorMsg, importWithUnexpectedHeader, jwt);
        Assert.assertEquals(importRecord.source, importWithUnexpectedHeader, "Import source didn't match with expected source");

        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, importRecord._id, jwt);

        verifyErrorInImportRecordContainsExpectedMessage(importRecord, expectedErrorMsg);

        String errorMessageInFailedJob = importRecord.getImportFailedJobCountErrorMessage(importRecord.standardizedDataIds.get(0));
        Assert.assertEquals(errorMessageInFailedJob, importRecord.errors.get(0), "Error message in failed job doesn't match with expected error");

        List<String> expectedRow = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, errorMessageInFailedJob, null, "\"{}\"");
        verifyErrorReportContainsCorrectData(importRecord.errorReport.link, Collections.singletonList(expectedRow));
    }

    @Test(groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesImport_CannotImportCompanyProperties_MissingRequiredHeader() throws Exception {
        String importUrl = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportCompanyProperties_MissingRequiredHeader.xlsx";

        String expectedErrorMsg = "Type: ImportsCoreError. Subtype: NullIdentifierValidationError. Message: propertyId or propertyName not defined for import.";
        ImportResponse response = importFile(importUrl, IMPORT_TYPE, testMethodName.get(), jwt);
        ImportRecord importRecord = verifyResponseBodyHasExpectedErrorAnReturnImportRecord(response, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS, expectedErrorMsg, importUrl, jwt);

        Assert.assertEquals(importRecord.source, importUrl, "Import source didn't match with expected source");
        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, importRecord._id, jwt);

        verifyErrorInImportRecordContainsExpectedMessage(importRecord, expectedErrorMsg);

        String errorMessageInFailedJob = importRecord.getImportFailedJobCountErrorMessage(importRecord.standardizedDataIds.get(0));
        Assert.assertEquals(errorMessageInFailedJob, importRecord.errors.get(0), "Error message in failed job doesn't match with expected error");

        String errorValue = "\"{\\\"name\\\":\\\"ImportsCoreError:NullIdentifierValidationError\\\"}\"";
        List<String> expectedRow = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, errorMessageInFailedJob, null, errorValue);
        verifyErrorReportContainsCorrectData(importRecord.errorReport.link, Collections.singletonList(expectedRow));
    }

    @Test(groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesImport_CannotInitiateImportProcess_InvalidParameters() throws Exception {
        validateUserCannotImportFileWithInvalidParameters(IMPORT_TYPE, FILE_VALID_DATA, jwt);
    }

    @Test(groups = {"CompanyPropertiesImportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesImport_CannotInitiateImportProcess_UnauthorizedErrorIsThrown() throws Exception {
        String unauthorizedUser = "qa+productmanagerviewonly@juggle.com";
        String jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, unauthorizedUser, TEST_CONFIG.password);

        Response response = CompanyApiRequests.importFileProductVariantRepo(FILE_VALID_DATA, IMPORT_TYPE, jwt);

        validateUnauthorizedError(response);
    }

    public void generateExpectedGroups(List<CompanyProperties.Group> expectedGroups, CompanyProperties.Property property) {
        if (property.group != null) {
            CompanyProperties.Group existingGroup = expectedGroups.stream()
                    .filter(group -> group.name.equals(property.group))
                    .findFirst()
                    .orElse(null);

            if (existingGroup != null) {
                int groupPropertyIndex = existingGroup.properties.size();
                CompanyProperties.Group.GroupProperty groupProperty = new CompanyProperties.Group.GroupProperty(property.id, groupPropertyIndex);

                List<CompanyProperties.Group.GroupProperty> groupProperties = new ArrayList<>(existingGroup.properties);
                groupProperties.add(groupProperty);
                existingGroup.properties = groupProperties;
            } else {
                int groupIndex = expectedGroups.size();
                CompanyProperties.Group.GroupProperty groupProperty = new CompanyProperties.Group.GroupProperty(property.id, 0);
                CompanyProperties.Group group = new CompanyProperties.Group(property.group, "", groupIndex, Collections.singletonList(groupProperty));
                expectedGroups.add(group);
            }
        }
    }

    private CompanyPropertiesBase.DigitalAssetCompanyProperty generateExpectedDigitalAssetProperty(CompanyProperties.Property property, List<CompanyPropertiesBase.Property> imageMappingProperties) {
        CompanyPropertiesBase.DigitalAssetCompanyProperty digitalAssetCompanyProperty = new CompanyPropertiesBase.DigitalAssetCompanyProperty();
        digitalAssetCompanyProperty.id = property.id;
        digitalAssetCompanyProperty.name = property.name;
        digitalAssetCompanyProperty.type = property.type;
        digitalAssetCompanyProperty.dropdownValues = property.dropdownValues;
        digitalAssetCompanyProperty.helpText = property.helpText;
        digitalAssetCompanyProperty.allowMultipleValues = property.allowMultipleValues;
        digitalAssetCompanyProperty.group = property.group;
        digitalAssetCompanyProperty.imageSpecMapping = new CompanyPropertiesBase.ImageSpecMapping(imageMappingProperties.get(0).id, imageMappingProperties.get(1).id);
        return digitalAssetCompanyProperty;
    }

    private void verifyCorrectNormalPropertiesWereImported(List<CompanyProperties.Property> propertySchemaBefore, List<CompanyProperties.Property> propertySchemaAfter, List<CompanyProperties.Property> expectedImportedProperties) {
        List<CompanyProperties.Property> expectedProperties = Stream.concat(propertySchemaBefore.stream(), expectedImportedProperties.stream())
                .distinct()
                .collect(Collectors.toList());

        Assert.assertEqualsNoOrder(
                propertySchemaAfter.toArray(),
                expectedProperties.toArray(),
                "Standard properties were not imported to company properties set" +
                        "\nEXPECTED PROPERTIES: \n" + expectedProperties +
                        "\nPROPERTIES IMPORTED: \n" + propertySchemaAfter
        );
    }

    private void verifyCorrectDigitalAssetsPropertiesWereImported(List<CompanyProperties.DigitalAssetCompanyProperty> digitalAssetPropertySchemaBefore, List<CompanyProperties.DigitalAssetCompanyProperty> digitalAssetPropertySchemaAfter, List<CompanyProperties.DigitalAssetCompanyProperty> expectedImportedDigitalAssetProperties) {
        List<CompanyProperties.DigitalAssetCompanyProperty> expectedDigitalAssetProperties = Stream.concat(digitalAssetPropertySchemaBefore.stream(), expectedImportedDigitalAssetProperties.stream())
                .distinct()
                .collect(Collectors.toList());

        Assert.assertEqualsNoOrder(
                digitalAssetPropertySchemaAfter.toArray(),
                expectedDigitalAssetProperties.toArray(),
                "Digital Asset properties were not imported to company properties set" +
                        "\nEXPECTED DIGITAL ASSETS: \n" + expectedDigitalAssetProperties +
                        "\nACTUAL DIGITAL ASSETS: \n" + digitalAssetPropertySchemaAfter
        );
    }

    private void verifyCorrectGroupsWereImported(CompanyPropertiesBase companyPropertiesAfter, List<CompanyProperties.Group> expectedImportedNormalGroups, List<CompanyProperties.Group> expectedImportedDigitalAssetGroups) {
        Comparator<CompanyProperties.Group> comparator = Comparator.comparing((CompanyProperties.Group group) -> group.sortIndex.intValue());
        expectedImportedNormalGroups.sort(comparator);
        expectedImportedDigitalAssetGroups.sort(comparator);

        Assert.assertEquals(companyPropertiesAfter.groups.size(), expectedImportedNormalGroups.size(), "Incorrect amount of groups after import");
        Assert.assertEquals(companyPropertiesAfter.groupsDigitalAssets.size(), expectedImportedDigitalAssetGroups.size(), "Incorrect amount of digital assets groups after import");

        List<Number> indexes = companyPropertiesAfter.groups.stream().map(group -> group.sortIndex).collect(Collectors.toList());
        Assert.assertEquals(indexes.size(), (int) indexes.stream().distinct().count(), "At least one group index was duplicated");

        indexes = companyPropertiesAfter.groupsDigitalAssets.stream().map(group -> group.sortIndex).collect(Collectors.toList());
        Assert.assertEquals(indexes.size(), (int) indexes.stream().distinct().count(), "At least one digital asset group index was duplicated");

        for (CompanyProperties.Group expectedGroup : expectedImportedNormalGroups) {
            verifyCorrectPropertiesWereAssignedToGroup(companyPropertiesAfter.groups, expectedGroup);
        }

        for (CompanyProperties.Group expectedGroup : expectedImportedDigitalAssetGroups) {
            verifyCorrectPropertiesWereAssignedToGroup(companyPropertiesAfter.groupsDigitalAssets, expectedGroup);
        }
    }

    private void verifyCorrectPropertiesWereAssignedToGroup(List<CompanyProperties.Group> groupsInCompany, CompanyProperties.Group expectedGroup) {
        CompanyProperties.Group groupInCompany = groupsInCompany.stream().filter(group -> group.name.equals(expectedGroup.name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Group " + expectedGroup.name + " was not generated"));

        Object[] actualPropertyIds = groupInCompany.properties.stream().map(property -> property.id).toArray();
        Object[] expectedPropertyIds = expectedGroup.properties.stream().map(property -> property.id).distinct().toArray();

        Assert.assertEqualsNoOrder(
                actualPropertyIds,
                expectedPropertyIds,
                "Wrong properties were assigned to group " + groupInCompany.name +
                        "\nEXPECTED PROPERTY IDS: " + Arrays.toString(expectedPropertyIds) +
                        "\nPROPERTY IDS IN GROUP: " + Arrays.toString(actualPropertyIds)
        );
    }

    private void cleanupCompany() throws Exception {
        var company = CompanyApiService.getCompanyWithProperties(jwt);
        var standardPropertyIdsToRemove = company.companyProperties.propertySchema.stream().filter(prop -> prop.id.contains("qa_import")).map(prop -> prop.id).collect(Collectors.toList());
        var digitalAssetIdsToRemove = company.companyProperties.digitalAssetPropertySchema.stream().filter(prop -> prop.id.contains("qa_import")).map(prop -> prop.id).collect(Collectors.toList());
        var standardGroupsToRemove = company.companyProperties.groups.stream().filter(prop -> prop.name.contains("Imported")).map(group -> group.name).collect(Collectors.toList());
        var digitalAssetGroupsToRemove = company.companyProperties.groupsDigitalAssets.stream().filter(prop -> prop.name.contains("Imported")).map(group -> group.name).collect(Collectors.toList());

        CompanyApiService.deleteDigitalAssetPropertyFromCompany(digitalAssetIdsToRemove, jwt);
        CompanyApiService.deleteRegularPropertiesFromCompany(standardPropertyIdsToRemove, jwt);
        CompanyApiService.deletePropertyGroupsFromCompany(standardGroupsToRemove, false, jwt);
        CompanyApiService.deletePropertyGroupsFromCompany(digitalAssetGroupsToRemove, true, jwt);
    }
}