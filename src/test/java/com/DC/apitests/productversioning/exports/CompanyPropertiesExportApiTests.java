package com.DC.apitests.productversioning.exports;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.*;
import com.DC.objects.productVersioning.CompanyPropertyExportExcelData;
import com.DC.objects.productVersioning.ExportRecord;
import com.DC.testcases.BaseClass;
import com.DC.tests.sharedAssertions.ExportCoreAssertions;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.log4testng.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.tests.sharedAssertions.ExportCoreAssertions.verifyResponseBodyHasExpectedError;
import static com.DC.utilities.SecurityAPI.changeInsightsCompanyAndGetJwt;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.SharedMethods.downloadFileFromUrl;
import static com.DC.utilities.XLUtils.CompanyPropertiesXLUtils.*;
import static io.restassured.path.json.JsonPath.with;
import static java.util.Arrays.asList;

public class CompanyPropertiesExportApiTests extends BaseClass {
    private static Logger logger;

    private static String jwt;

    private static Company company;

    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    private static final CompanyPropertiesCollection COMPANY_PROPERTIES_COLLECTION = new CompanyPropertiesCollection();

    private static final String EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS = "Unable to Enqueue Company Property Export";

    CompanyPropertiesExportApiTests() {
        logger = Logger.getLogger(CompanyPropertiesExportApiTests.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        logger.info("Setting up company properties export api tests");
        company = new CompanyCollection().getCompany(TEST_CONFIG.companyID);
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        jwt = changeInsightsCompanyAndGetJwt(jwt, company._id, company.name);
    }

    @Test(groups = {"CompanyPropertiesExportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesExport_CanExportCompanyProperties_AllCompanyProperties() throws Exception {
        List<CompanyPropertiesBase.DigitalAssetCompanyProperty> companyDigitalAssetsProperties = COMPANY_PROPERTIES_COLLECTION
                .getCompanyProperties(company.companyPropertiesId).digitalAssetPropertySchema;

        List<CompanyProperties.Property> companyNormalProperties = COMPANY_PROPERTIES_COLLECTION
                .getCompanyProperties(company.companyPropertiesId).propertySchema;

        List<CompanyProperties.Property> allCompanyProperties = Stream.of(companyNormalProperties, companyDigitalAssetsProperties)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Response response = CompanyApiRequests.exportCompanyProperties(new ArrayList<>(), jwt);
        String exportId = ExportCoreAssertions.verifyResponseReturnsAnExportId(testMethodName.get(), response);

        // TEST COLLECTION
        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.SUCCESS, exportRecord._id);

        ExportCoreAssertions.verifyBasicExportCoreData(exportRecord, Enums.ExportType.COMPANY, Enums.ExportSubType.PROPERTY, company._id);

        verifyMetaObject(exportRecord, new ArrayList<>());

        // TEST EXPORTED DATA
        verifyCompanyPropertiesWereExportedProperly(exportRecord.exportWorkbook.link, allCompanyProperties);
    }

    @Test(groups = {"CompanyPropertiesExportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesExport_CanExportCompanyProperties_SpecificProperty() throws Exception {
        CompanyProperties.Property digitalAssetPropertyToExport = COMPANY_PROPERTIES_COLLECTION
                .getCompanyProperties(company.companyPropertiesId).digitalAssetPropertySchema
                .get(0);

        CompanyProperties.Property propertyToExport = COMPANY_PROPERTIES_COLLECTION
                .getCompanyProperties(company.companyPropertiesId)
                .propertySchema
                .get(0);

        List<CompanyProperties.Property> propertiesToExport = asList(digitalAssetPropertyToExport, propertyToExport);
        List<String> propertyIdsToExport = propertiesToExport.stream().map(property -> property.id).collect(Collectors.toList());

        Response response = CompanyApiRequests.exportCompanyProperties(propertyIdsToExport, jwt);
        String exportId = ExportCoreAssertions.verifyResponseReturnsAnExportId(testMethodName.get(), response);

        // TEST COLLECTION
        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.SUCCESS, exportRecord._id);

        ExportCoreAssertions.verifyBasicExportCoreData(exportRecord, Enums.ExportType.COMPANY, Enums.ExportSubType.PROPERTY, company._id);

        verifyMetaObject(exportRecord, propertyIdsToExport);

        // TEST EXPORTED DATA
        verifyCompanyPropertiesWereExportedProperly(exportRecord.exportWorkbook.link, propertiesToExport);
    }

    @Test(groups = {"CompanyPropertiesExportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesExport_CannotExportCompanyProperties_UnauthorizedErrorIsThrown() throws Exception {
        String unauthorizedUser = "qa+productmanagerviewonly@juggle.com";
        String jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, unauthorizedUser, TEST_CONFIG.password);

        Response response = CompanyApiRequests.exportCompanyProperties(new ArrayList<>(), jwt);

        validateUnauthorizedError(response);
    }

    @Test(groups = {"CompanyPropertiesExportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesExport_CannotExportCompanyProperties_InvalidParameters() throws Exception {
        String reqBody = "{\n" +
                "    \"propertyIds\": \"invalid\"\n" +
                "}";

        Response response = CompanyApiRequests.exportCompanyProperties(reqBody, jwt);
        String expectedError = "\"propertyIds\" must be an array";
        validateInvalidRequestParametersError(response, Collections.singletonList(expectedError));

        reqBody = "{\n" +
                "    \"propertyIds\": [123]\n" +
                "}";

        response = CompanyApiRequests.exportCompanyProperties(reqBody, jwt);
        expectedError = "\"0\" must be a string";
        validateInvalidRequestParametersError(response, Collections.singletonList(expectedError));
    }

    @Test(groups = {"CompanyPropertiesExportApiTests", "NoDataProvider"})
    public void Api_CompanyPropertiesExport_CannotExportCompanyProperties_NonExistentPropertyId() throws Exception {
        String nonExistentPropertyId = "invalid";

        ExportResponse response = CompanyApiService.exportCompanyProperties(Collections.singletonList(nonExistentPropertyId), jwt);

        String expectedErrorMessage = "Property with ID " + nonExistentPropertyId + " does not exist on company";
        verifyResponseBodyHasExpectedError(response, expectedErrorMessage, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS);

        // TEST COLLECTION
        ExportRecord exportRecord = ExportCoreAssertions.waitForExportToBeInDB(response.exportId);
        exportRecord = ExportCoreAssertions.verifyExportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, exportRecord._id);
        Assert.assertNull(exportRecord.exportWorkbook.link, "Export file was generated with a non existent property");

        Assert.assertFalse(exportRecord.errors.isEmpty(), "Error list was empty");
        Assert.assertTrue(exportRecord.errors.get(0).contains(expectedErrorMessage), "Error didn't have the expected message");
    }

    private void verifyMetaObject(ExportRecord exportRecord, List<String> expectedPropertyIds) {
        List<String> propertyIdsInMetaObject = null;
        try {
            String json = new ObjectMapper().writeValueAsString(exportRecord.meta);
            propertyIdsInMetaObject = with(json).get("propertyIds");
        } catch (Exception e) {
            Assert.fail("Export meta object couldn't get casted to expected object");
        }
        Assert.assertEquals(
                propertyIdsInMetaObject,
                expectedPropertyIds,
                "Value of meta object didn't match with expected value" +
                        "\nEXPECTED META: " + expectedPropertyIds +
                        "\nMETA IN EXPORT RECORD: " + propertyIdsInMetaObject
        );
    }

    private void verifyCompanyPropertiesWereExportedProperly(String downloadLink, List<CompanyProperties.Property> expectedProperties) throws IOException, InvalidFormatException {
        String filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/exportedCompanyProperties.xlsx";
        downloadFileFromUrl(downloadLink, filePath);

        List<String> exportedHeaders = getFileHeaders(filePath);
        Assert.assertEquals(exportedHeaders, EXPECTED_HEADERS, "Incorrect headers in export file");

        List<CompanyPropertyExportExcelData> exportedData = getExportedCompanyProperties(filePath);

        Assert.assertEquals(
                exportedData.size(),
                expectedProperties.size(),
                "Row total in exported file didn't match with properties count in request"
        );

        for (CompanyProperties.Property expectedProperty : expectedProperties) {
            CompanyPropertyExportExcelData exportedProperty = exportedData.stream()
                    .filter(prop -> prop.propertyId.equals(expectedProperty.id))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

            expectedProperty.helpText = Objects.equals(exportedProperty.helpText, "") ? null : exportedProperty.helpText;

            Assert.assertEquals(exportedProperty.propertyId, expectedProperty.id, "Property ID didn't match");
            Assert.assertEquals(exportedProperty.propertyName, expectedProperty.name, "Property name didn't match");
            Assert.assertEquals(exportedProperty.helpText, expectedProperty.helpText, "Help text didn't match");
            Assert.assertEquals(exportedProperty.dataType, expectedProperty.type, "Data type didn't match");
            Assert.assertEquals(exportedProperty.propertyGroup, expectedProperty.group, "Property group didn't match");
            Assert.assertEquals(exportedProperty.multipleValues, expectedProperty.allowMultipleValues, "Multiple values didn't match");

            if (expectedProperty.dropdownValues != null) {
                Assert.assertEquals(
                        exportedProperty.dropdownValues,
                        expectedProperty.dropdownValues.stream().map(value -> value.name).collect(Collectors.toList()),
                        "Dropdown values didn't match"
                );
            } else {
                Assert.assertNull(exportedProperty.dropdownValues, "Dropdown values should be null");
            }

        }
    }
}
