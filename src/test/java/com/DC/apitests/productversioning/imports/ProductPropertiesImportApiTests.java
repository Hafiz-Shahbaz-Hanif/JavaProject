package com.DC.apitests.productversioning.imports;

import com.DC.apitests.ApiValidations;
import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.*;
import com.DC.objects.productVersioning.*;
import com.DC.testcases.BaseClass;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.SharedRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductListApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.DC.constants.InsightsConstants.getMediaSiteDomain;
import static com.DC.tests.sharedAssertions.ImportAssertions.*;
import static com.DC.utilities.DateUtility.getDateFromSequentialSerialNumber;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.SharedMethods.downloadFileFromUrl;
import static com.DC.utilities.XLUtils.ProductPropertiesXLUtils.*;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

public class ProductPropertiesImportApiTests extends BaseClass {

    private static final Logger LOGGER = Logger.getLogger(ProductPropertiesImportApiTests.class);
    private String jwt;
    private final Enums.ImportType IMPORT_TYPE = Enums.ImportType.PROPERTY;
    private final String VALID_DATA_IMPORT_URL = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductVersioning_SuccessMultipleRows_" + READ_CONFIG.getInsightsEnvironment() + ".xlsx";
    private final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();
    private final String TEST_DATA_DIRECTORY = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/";
    private final CompanyCollection COMPANY_COLLECTION = new CompanyCollection();
    private List<String> normalProperties;
    private Company company;
    private List<FriendlyProductVariantList> companyProductListsBeforeTests;
    private FriendlyProductVariantList staticProductListBeforeTests;
    private final String STATIC_LIST_NAME = "Static List For Automated API Tests";
    private final List<String> INSTANCE_IDS_TO_REMOVE_FROM_STATIC_LIST = new ArrayList<>();
    private final List<String> IDS_OF_PRODUCTS_TO_DELETE = new ArrayList<>();
    private final String FILE_WITH_LISTS = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductProperties_WithLists.xlsx";
    private final String FILE_WITH_PARTIAL_FAILURE = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductVersioning_PartialFailure.xlsx";
    private final String EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS = "Unable to perform Property Variant Import";
    private final String EXPECTED_ERR_MSG_FOR_NEGATIVE_TESTS = "There were no successful standardized records to publish.";

    ProductPropertiesImportApiTests() {
        PropertyConfigurator.configure("log4j.properties");
    }

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        LOGGER.info("Setting up product imports api tests");
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.username, TEST_CONFIG.password);
        company = COMPANY_COLLECTION.getCompany(TEST_CONFIG.companyID);

        normalProperties = new CompanyPropertiesCollection()
                .getCompanyProperties(company.companyPropertiesId)
                .propertySchema.stream()
                .map(property -> property.id)
                .collect(Collectors.toList());

        companyProductListsBeforeTests = ProductListApiService.getAllCompanyProductLists(jwt);

        staticProductListBeforeTests = companyProductListsBeforeTests.stream()
                .filter(list -> list.name.equals(STATIC_LIST_NAME))
                .findFirst()
                .orElse(null);

        cleanupProductsAndLists();
    }

    @AfterClass()
    public void cleanupProductsAndLists() throws Exception {
        LOGGER.info("Cleaning up products and lists created during tests");

        if (IDS_OF_PRODUCTS_TO_DELETE.isEmpty()) {
            getNewProductsAndDeleteIfTheyExist();
        } else {
            for (String id : IDS_OF_PRODUCTS_TO_DELETE) {
                ProductVersioningApiService.deleteProductMasterByUniqueId(id, jwt);
            }
        }

        if (INSTANCE_IDS_TO_REMOVE_FROM_STATIC_LIST.isEmpty()) {
            List<VariantImportExcelData> dataInExcelFile = downloadFileAndGetVariantDataToImport(TEST_DATA_DIRECTORY, FILE_WITH_LISTS);
            ProductListApiService.removeProductsFromList(Collections.singletonList(getExpectedProductInfoInList(dataInExcelFile.get(1)).instanceId), staticProductListBeforeTests._id, jwt);
        } else {
            ProductListApiService.removeProductsFromList(INSTANCE_IDS_TO_REMOVE_FROM_STATIC_LIST, staticProductListBeforeTests._id, jwt);
        }
    }

    @Test(
            groups = {"ProductPropertiesImportApiTests", "NoDataProvider"},
            description = "It tests the variant-imports collection after hitting the endpoint to import valid product data" +
                    "File has multiple rows including normal & digital asset properties"
    )
    public void Api_ProductPropertiesImports_CanImportProductProperties_MultipleRows() throws Exception {
        var dataInExcelFile = downloadFileAndGetVariantDataToImport(TEST_DATA_DIRECTORY, VALID_DATA_IMPORT_URL);

        List<Map.Entry<String, VariantImportExcelData>> rowsAndOriginalName = new ArrayList<>();

        for (var row : dataInExcelFile) {
            var productToImport = ProductVersioningApiService.getProductWithUniqueIdIfExist(row.productIdentifier, jwt);
            if (productToImport != null) {
                var variantInstancePath = new ProductVariantInstancePath(productToImport._id, company.getLocaleId(row.locale), Enums.ProductVariantType.LIVE, company.getRetailerId(row.retailer), company.getCampaignId(row.campaign));
                if (row.locale == null && row.retailer == null && row.campaign == null) {
                    rowsAndOriginalName.add(new AbstractMap.SimpleEntry<>(productToImport.name, row));
                } else {
                    var instanceBaseData = productToImport.getInstanceBaseData(variantInstancePath);
                    rowsAndOriginalName.add(new AbstractMap.SimpleEntry<>(instanceBaseData.name, row));
                }
            } else {
                rowsAndOriginalName.add(new AbstractMap.SimpleEntry<>("null", row));
            }
        }

        validateUserCanImportValidFile(IMPORT_TYPE, Enums.ImportTrackingType.FILE, VALID_DATA_IMPORT_URL, jwt, testMethodName.get());

        var uniqueIdOfProductMasterToTest = dataInExcelFile.get(dataInExcelFile.size() - 1).productIdentifier;
        IDS_OF_PRODUCTS_TO_DELETE.add(uniqueIdOfProductMasterToTest);

        var currentDate = LocalDate.now();

        for (var rowAndOriginalName : rowsAndOriginalName) {
            var rowData = rowAndOriginalName.getValue();
            if (rowData.locale.isEmpty() && rowData.retailer.isEmpty() && rowData.campaign.isEmpty()) {
                verifyProductNameAndThumbnailWereAddedToMaster(rowAndOriginalName);
            } else {
                verifyProductPropertiesWereImportedProperly(company, normalProperties, currentDate, rowAndOriginalName.getValue());
                verifyRPCWasImportedProperly(company, rowAndOriginalName.getValue());
                verifyProductNameWasImportedProperly(company, rowAndOriginalName);
            }
        }
    }

    @Test(
            groups = {"ProductPropertiesImportApiTests", "NoDataProvider"},
            description = "File to import contains some bad data. API should import valid data and throw partial failure"
    )
    public void Api_ProductPropertiesImports_CanImportSomeProductProperties_SomeBadData() throws Exception {
        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(FILE_WITH_PARTIAL_FAILURE, IMPORT_TYPE, testMethodName.get(), jwt);
        Assert.assertEquals(importRecord.source, FILE_WITH_PARTIAL_FAILURE, "Import source didn't match with expected source");

        Assert.assertNotNull(importRecord.errorReport, "Error report was not null");

        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.PARTIAL_FAILURE, importRecord._id, jwt);
        importRecord = verifyImportMessageChangesToExpectedMessage("OK", importRecord._id);

        Assert.assertEquals(importRecord.publishedIds.size(), 1, "Expected published ids count didn't match with expected count");

        List<String> expectedFirstRowData = List.of(Enums.ImportStage.STANDARDIZE.toString().toLowerCase(), "Invalid Locale Found for Company");
        List<String> expectedSecondRowData = List.of(Enums.ImportStage.STANDARDIZE.toString().toLowerCase(), "Message: RPC 'BaseRPC' cannot be added to level 'global' for company.");
        List<String> expectedThirdRowData = List.of(Enums.ImportStage.STANDARDIZE.toString().toLowerCase(), "Message: RPC 'HalloweenRPC' cannot be added to level 'globalCampaign' for company");
        var expectedRows = List.of(expectedFirstRowData, expectedSecondRowData, expectedThirdRowData);

        String filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/errorReportImports.xlsx";
        downloadFileFromUrl(importRecord.errorReport.link, filePath);
        List<List<String>> sheetData = getSheetDataByRow(filePath, true);
        Assert.assertTrue(sheetData.size() >= 2, "Error report file was empty");


        for (int i = 0; i < expectedRows.size(); i++) {
            var expectedError = expectedRows.get(i);
            var rowToTest = sheetData.get(i + 1);
            Assert.assertEquals(expectedError.get(0), rowToTest.get(0), "Incorrect stage value in row " + i);
            Assert.assertTrue(rowToTest.get(1).contains(expectedError.get(1)), rowToTest.get(1) + " does not contain " + expectedError.get(1) + " in row " + i);
        }

        LocalDate currentDate = LocalDate.now();
        List<VariantImportExcelData> dataInExcelFile = downloadFileAndGetVariantDataToImport(TEST_DATA_DIRECTORY, FILE_WITH_PARTIAL_FAILURE);

        verifyProductWasNotAddedToCompany(dataInExcelFile.get(1));
        verifyProductWasNotAddedToCompany(dataInExcelFile.get(2));
        verifyProductPropertiesWereImportedProperly(company, normalProperties, currentDate, dataInExcelFile.get(3));
    }

    @Test(groups = {"ProductPropertiesImportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesImports_CannotImportProductProperties_InvalidData() throws Exception {
        String importWithInvalidData = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductVersioning_InvalidData.xlsx";

        ImportRecord importRecord = verifyImportProcessIsInitiatedAndGetImportRecord(importWithInvalidData, IMPORT_TYPE, testMethodName.get(), jwt);

        Assert.assertEquals(importRecord.source, importWithInvalidData, "Import source didn't match with expected source");

        Assert.assertNotNull(importRecord.errorReport, "Error report was not null");

        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, importRecord._id, jwt);
        importRecord = verifyImportMessageChangesToExpectedMessage("OK", importRecord._id);

        verifyDataIdsCountMatchWithCompletedIds(importRecord);

        String errorMessageInFailedJob = importRecord.getImportFailedJobCountErrorMessage();

        Assert.assertEquals(errorMessageInFailedJob, importRecord.errors.get(0), "Error message in failed job doesn't match with expected error");

        List<String> expectedRow2 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, "Invalid Locale Found for Company", "invalid", null);
        List<String> expectedRow3 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 3, "Invalid retailer included for company", "invalid", null);
        List<String> expectedRow4 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 4, "Invalid Campaign found for company", "invalid", null);
        List<String> expectedRow5 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 5, "Duplicate Variant targeted in Import", null, null);
        List<String> expectedRow6 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 6, "Duplicate Variant targeted in Import", null, null);
        List<String> expectedRow7 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 8, "Invalid Digital Asset Value - Should not be multiple values", "https://os-media-service.s3.amazonaws.com/development/OneSpaceTest/Image+Comparison/imageCompareTestData/small.jpg|https://thumbs.dreamstime.com/z/quality-assurance-service-guarantee-standard-internet-business-technology-concept-quality-assurance-service-guarantee-standard-123697462.jpg", null);
        List<String> expectedRow8 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 9, "Invalid Digital Asset Url", "invalid digital asset url", null);
        List<String> expectedRow9 = getExpectedErrorRowData(Enums.ImportStage.PUBLISH, null, errorMessageInFailedJob, null, "\"{}\"");
        verifyErrorReportContainsCorrectData(importRecord.errorReport.link, Arrays.asList(expectedRow2, expectedRow3, expectedRow4, expectedRow5, expectedRow6, expectedRow7, expectedRow8, expectedRow9));
    }

    @Test(groups = {"ProductPropertiesImportApiTests", "NoDataProvider"}, description = "User should not be able to update merge5 properties")
    public void Api_ProductPropertiesImports_CannotImportProductProperties_Merge5Properties() throws Exception {
        var importWithInvalidData = "https://os-media-service.s3.amazonaws.com/qa/imports/ProductPropertyImport-Merge5Properties.xlsx";

        ImportResponse response = importFile(importWithInvalidData, IMPORT_TYPE, testMethodName.get(), jwt);
        ImportRecord importRecord = verifyResponseBodyHasExpectedErrorAnReturnImportRecord(response, EXPECTED_RESPONSE_MSG_FOR_NEGATIVE_TESTS, EXPECTED_ERR_MSG_FOR_NEGATIVE_TESTS, importWithInvalidData, jwt);

        var errorReportUrl = SharedRequests.getErrorReportForImport(importRecord._id, jwt).jsonPath().getString("data.url");

        List<List<String>> expectedRows = new ArrayList<>();
        var merge5PropertiesCount = 5;
        for (var index = 1; index <= merge5PropertiesCount; index++) {
            var expectedRowData = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, "Invalid Properties in Import", "should not import", null);
            expectedRows.add(expectedRowData);
        }

        expectedRows.add(getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, null, importRecord.errors.get(0), null, "\"{}\""));
        verifyErrorReportContainsCorrectData(errorReportUrl, expectedRows);
    }

    @Test(groups = {"ProductPropertiesImportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesImports_CannotImportProductProperties_UnexpectedHeader() throws Exception {
        String importWithUnexpectedHeader = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductVersioning_UnexpectedHeader.xlsx";
        validateUserCannotImportFileWithUnexpectedHeaders(IMPORT_TYPE, importWithUnexpectedHeader, jwt, testMethodName.get());
    }

    @Test(groups = {"ProductPropertiesImportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesImports_CannotImportProductProperties_MissingRequiredHeader() throws Exception {
        String importWithoutRequiredHeader = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductVersioning_MissingRequiredHeader.xlsx";

        ImportResponse importResponse = importFile(importWithoutRequiredHeader, IMPORT_TYPE, testMethodName.get(), jwt);

        ImportRecord importRecord = waitForImportToBeInDB(importResponse.importId);
        Assert.assertEquals(importRecord.source, importWithoutRequiredHeader, "Import source didn't match with expected source");

        importRecord = verifyImportStatusChangesToExpectedStatus(Enums.ProcessStatus.FAILED, importRecord._id, jwt);
        importRecord = verifyImportMessageChangesToExpectedMessage("OK", importRecord._id);

        verifyDataIdsCountMatchWithCompletedIds(importRecord);
    }

    @Test(groups = {"ProductPropertiesImportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesImports_CannotInitiateImportProcess_InvalidParameters() throws Exception {
        validateUserCannotImportFileWithInvalidParameters(IMPORT_TYPE, VALID_DATA_IMPORT_URL, jwt);
    }

    @Test(groups = {"ProductPropertiesImportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesImports_ImportTemplateHasCorrectData() throws Exception {
        Response response = SharedRequests.getProductPropertiesImportTemplate(jwt);
        ImportTemplatesResponse templateResponse = ApiValidations.verifyEndpointReturnsCorrectObject(response, testMethodName.get(), ImportTemplatesResponse.class);

        Assert.assertEquals(templateResponse.companyId, TEST_CONFIG.companyID,
                "Company id in the response doesn't match with the expected company id"
        );
        Assert.assertNotNull(templateResponse.data.s3Link, "S3 link in the response is null");

        String filePath = System.getProperty("user.dir") + "/src/test/java/com/DC/downloads/importProductPropertiesTemplate.xlsx";
        downloadFileFromUrl(templateResponse.data.s3Link, filePath);

        XSSFWorkbook workbook = getWorkbook(filePath);
        XSSFSheet sheet = workbook.getSheetAt(0);

        List<String> digitalAssets = CompanyApiService.getCompanyWithProperties(jwt).companyProperties.digitalAssetPropertySchema
                .stream()
                .map(property -> property.id)
                .collect(Collectors.toList());

        List<String> expectedSubsequentColumns = Stream.of(normalProperties, digitalAssets)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        verifyProductImportHasCorrectHeaders(sheet, EXPECTED_PRODUCT_PROPERTIES_DEFAULT_HEADERS, expectedSubsequentColumns);

        int expectedDropdownsCount = 3;
        Assert.assertEquals(
                sheet.getDataValidations().size(),
                expectedDropdownsCount,
                "Number of dropdowns in the template doesn't match with the expected number of dropdowns"
        );
        verifyLocaleRetailerAndCampaignColumnsAreDropdowns(sheet, company);
        workbook.close();
    }

    @Test(groups = {"ProductPropertiesImportApiTests", "NoDataProvider"})
    public void Api_ProductPropertiesImports_CanAddProductToList() throws Exception {
        companyProductListsBeforeTests = ProductListApiService.getAllCompanyProductLists(jwt);
        staticProductListBeforeTests = companyProductListsBeforeTests.stream()
                .filter(list -> list.name.equals(STATIC_LIST_NAME))
                .findFirst()
                .orElse(null);

        // IMPORT SECOND FILE WITH EXISTENT LISTS
        validateUserCanImportValidFile(IMPORT_TYPE, Enums.ImportTrackingType.FILE, FILE_WITH_LISTS, jwt, testMethodName.get());
        var dataInExcelFile = downloadFileAndGetVariantDataToImport(TEST_DATA_DIRECTORY, FILE_WITH_LISTS);
        var companyProductListsAfter = ProductListApiService.getAllCompanyProductLists(jwt);

        // VERIFY CORRECT NUMBER OF PRODUCTS WERE ADDED TO STATIC LIST
        List<VariantImportExcelData> productsToImportToStaticList = dataInExcelFile.stream().filter(row -> row.productListName.equals(STATIC_LIST_NAME)).collect(Collectors.toList());
        FriendlyProductVariantList staticProductListAfter = companyProductListsAfter.stream()
                .filter(list -> list.name.equals(STATIC_LIST_NAME))
                .findFirst()
                .orElse(null);

        FriendlyProductVariantList staticProduct2ListAfter = companyProductListsAfter.stream()
                .filter(list -> list.name.equals("Static List For Automated API Tests 2"))
                .findFirst()
                .orElse(null);

        List<ProductVariantListProduct> productsInStaticListAfterFirstImport = staticProductListAfter.products;

        Assert.assertEquals(productsInStaticListAfterFirstImport.size(), staticProductListBeforeTests.products.size() + productsToImportToStaticList.size(),
                "Number of products in static list after import doesn't match with the expected number of products"
        );

        // VERIFY PRODUCTS WERE ADDED TO CORRECT LIST
        // Product-To-List es-MX
        verifyProductWasAddedToList(dataInExcelFile.get(0), staticProduct2ListAfter, true);
        // QA-IMPORTS-003 fr-FR	Amazon.com Christmas
        verifyProductWasAddedToList(dataInExcelFile.get(1), staticProductListAfter, false);
        // Product-To-List-2 es-MX
        verifyProductWasAddedToList(dataInExcelFile.get(2), staticProductListAfter, true);
    }

    @Test(
            groups = {"ProductPropertiesImportApiTests", "NoDataProvider"},
            description = "User can delete properties from a product via import by having <delete> in the appropriate cell"
    )
    public void Api_ProductPropertiesImports_DeleteTagRemovesPropertyValueFromProduct() throws Exception {
        // FIRST IMPORT IS TO SETUP DATA
        var firstImportFile = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductVersioning_DeletePropertyValues_Part1.xlsx";
        validateUserCanImportValidFile(IMPORT_TYPE, Enums.ImportTrackingType.FILE, firstImportFile, jwt, testMethodName.get());

        // SECOND IMPORT CONTAINS THE <DELETE> TAGS
        var secondImportFile = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductVersioning_DeletePropertyValues_Part2.xlsx";
        validateUserCanImportValidFile(IMPORT_TYPE, Enums.ImportTrackingType.FILE, secondImportFile, jwt, testMethodName.get());
        var dataInExcelFile = downloadFileAndGetProductDataToImport(TEST_DATA_DIRECTORY, secondImportFile);
        IDS_OF_PRODUCTS_TO_DELETE.add(dataInExcelFile.get(1).get("product identifier"));

        // VERIFY PRODUCT PROPERTIES WERE DELETED
        for (var row : dataInExcelFile) {
            var productIdentifier = row.get("product identifier");
            LOGGER.info("Testing product " + productIdentifier);
            LinkedHashMap<String, String> properties = row.entrySet().stream().skip(6).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

            var localeId = company.getLocaleId(row.get("locale"));
            var retailerId = company.getRetailerId(row.get("retailer"));
            var campaignId = company.getCampaignId(row.get("campaign"));

            var instancePath = new ProductVariantInstancePath(productIdentifier, localeId, Enums.ProductVariantType.LIVE, retailerId, campaignId);

            var propertySetAfterImport = ProductVersioningApiService.getPropertySetDataByUniqueId(instancePath, null, jwt);

            var digitalAssetPropertySetAfterImport = ProductVersioningApiService.getDigitalAssetSetDataByUniqueId(instancePath, null, jwt);

            for (var property : properties.entrySet()) {
                var propertyId = property.getKey();
                var propertyValue = property.getValue();
                
                if (!Objects.equals(propertyValue, "")) {
                    var isDigitalAssetProperty = !normalProperties.contains(propertyId);

                    if (isDigitalAssetProperty) {
                        // TODO: Uncomment when <delete> tag is implemented for digital assets
                        /*var digitalAssetPropertyInDB = digitalAssetPropertySetAfterImport.digitalAssets
                                .stream()
                                .filter(prop -> prop.id.equals(propertyId))
                                .findFirst()
                                .orElse(null);
                        verifyDigitalAssetInfo(digitalAssetPropertyInDB, propertyId, propertyValue);*/
                    } else {
                        var propertyInDB = propertySetAfterImport.properties
                                .stream()
                                .filter(prop -> prop.id.equals(propertyId))
                                .findFirst()
                                .orElse(null);
                        verifyPropertiesWereTransformedProperly(propertyId, propertyValue, propertyInDB, Enums.ImportStage.PUBLISH);
                    }
                }
            }
        }
    }

    @Test(
            groups = {"ProductPropertiesImportApiTests", "NoDataProvider"},
            description = "Values in product master row are saved in all versions (instances) of the product"
    )
    public void Api_ProductPropertiesImports_ProductMasterValuesAreImportedToAllVersions() throws Exception {
        companyProductListsBeforeTests = ProductListApiService.getAllCompanyProductLists(jwt);
        staticProductListBeforeTests = companyProductListsBeforeTests.stream()
                .filter(list -> list.name.equals(STATIC_LIST_NAME))
                .findFirst()
                .orElse(null);

        var fileToImport = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductProperties_InstancesOnly.xlsx";
        validateUserCanImportValidFile(IMPORT_TYPE, Enums.ImportTrackingType.FILE, fileToImport, jwt, testMethodName.get());

        fileToImport = "https://os-media-service.s3.amazonaws.com/qa/imports/ImportProductVersioning_MasterOnly.xlsx";
        var dataInExcelFile = downloadFileAndGetVariantDataToImport(TEST_DATA_DIRECTORY, fileToImport);
        var rowData = dataInExcelFile.get(dataInExcelFile.size() - 1);

        validateUserCanImportValidFile(IMPORT_TYPE, Enums.ImportTrackingType.FILE, fileToImport, jwt, testMethodName.get());

        var uniqueIdOfProductMasterToTest = rowData.productIdentifier;
        IDS_OF_PRODUCTS_TO_DELETE.add(uniqueIdOfProductMasterToTest);

        var productMasterId = ProductVersioningApiService.getProductMasterByUniqueId(uniqueIdOfProductMasterToTest, jwt)._id;
        var productMasterComposed = ProductVersioningApiService.getProductMasterComposed(productMasterId, jwt);

        List<ProductVariantProperty> standardPropertiesOfAllInstances = new ArrayList<>();
        List<DigitalAssetProperty> digitalAssetPropertiesOfAllInstances = new ArrayList<>();

        var companyProductListsAfter = ProductListApiService.getAllCompanyProductLists(jwt);
        var staticProductListAfter = companyProductListsAfter.stream()
                .filter(list -> list.name.equals(STATIC_LIST_NAME))
                .findFirst()
                .orElse(null);

        productMasterComposed.forEach(p -> {
            standardPropertiesOfAllInstances.addAll(p.global.properties);
            p.retailer.forEach(r -> {
                standardPropertiesOfAllInstances.addAll(r.properties);
                digitalAssetPropertiesOfAllInstances.addAll(r.digitalAssets);
                verifyProductNameAndThumbnail(rowData.productName, rowData.thumbnail, r.instanceName, r.instanceThumbnail);
                verifyInstanceIsInList(staticProductListAfter.products, r.instanceId);

            });
            p.globalCampaign.forEach(c -> {
                standardPropertiesOfAllInstances.addAll(c.properties);
                digitalAssetPropertiesOfAllInstances.addAll(c.digitalAssets);
                verifyProductNameAndThumbnail(rowData.productName, rowData.thumbnail, c.instanceName, c.instanceThumbnail);
                verifyInstanceIsInList(staticProductListAfter.products, c.instanceId);
            });
            p.retailerCampaign.forEach(rc -> {
                standardPropertiesOfAllInstances.addAll(rc.properties);
                digitalAssetPropertiesOfAllInstances.addAll(rc.digitalAssets);
                verifyProductNameAndThumbnail(rowData.productName, rowData.thumbnail, rc.instanceName, rc.instanceThumbnail);
                verifyInstanceIsInList(staticProductListAfter.products, rc.instanceId);
            });
        });

        for (VariantImportExcelData.PropertyData propertyInFile : rowData.properties) {
            if (!Objects.equals(propertyInFile.value, "")) {
                boolean isDigitalAssetProperty = !normalProperties.contains(propertyInFile.id);

                if (isDigitalAssetProperty) {
                    var digitalAssetsToTest = digitalAssetPropertiesOfAllInstances.stream().filter(p -> p.id.equals(propertyInFile.id)).collect(Collectors.toList());
                    for (var digitalAssetPropertyInInstance : digitalAssetsToTest) {
                        verifyDigitalAssetInfo(digitalAssetPropertyInInstance, propertyInFile.id, propertyInFile.value);
                    }
                } else {
                    var standardPropertiesToTest = standardPropertiesOfAllInstances.stream().filter(p -> p.id.equals(propertyInFile.id)).collect(Collectors.toList());
                    for (var standardPropertyInInstance : standardPropertiesToTest) {
                        verifyPropertiesWereTransformedProperly(propertyInFile.id, propertyInFile.value, standardPropertyInInstance, Enums.ImportStage.PUBLISH);
                    }
                }
            }
        }
    }

    private void verifyStageBaseData(ImportStageBase importStageBase, String expectedImportId, Enums.ImportStage stage) {
        String errorMessagePrefix = "Stage: " + stage + " . ";
        Assert.assertEquals(importStageBase.trackingId, expectedImportId, errorMessagePrefix + "ImportId didn't match with the expected id");
        Assert.assertEquals(importStageBase._version, 1, errorMessagePrefix + "Version didn't match with the expected version");
        Assert.assertTrue(importStageBase.errors.isEmpty(), errorMessagePrefix + "Error list was not empty");
    }

    private void verifyPropertiesWereTransformedProperly(
            String propertyId,
            String propertyValue,
            ProductVariantProperty propertyInDatabase,
            Enums.ImportStage stage
    ) {
        if (propertyValue.equals("<delete>")) {
            Assert.assertNull(propertyInDatabase, "Property: " + propertyId + " was not deleted from product after importing <delete> tag");
            return;
        }

        String errorMessage = "Values of property " + propertyId + " in stage " + stage + " don't match with expected values.";
        if (stage.equals(Enums.ImportStage.STANDARDIZE)) {
            throw new IndexOutOfBoundsException(stage + " not permitted on this method");
        } else {

            switch (propertyId) {
                case "test_prop_1":
                    errorMessage = errorMessage + "PropertyId: " + propertyId;
                    List<String> expectedValues = Arrays.asList(propertyValue.split("\\|"));
                    Assert.assertEquals(propertyInDatabase.values, expectedValues, errorMessage);
                    break;
                case "test_prop_2":
                    Assert.assertEquals(
                            Collections.singletonList((int) Double.parseDouble(propertyInDatabase.values.get(0).toString())),
                            Collections.singletonList(parseInt(propertyValue)),
                            errorMessage
                    );
                    break;
                case "test_prop_3":
                    if (stage.equals(Enums.ImportStage.PUBLISH)) {
                        LocalDate expectedDate;
                        if (!propertyValue.contains("/")) {
                            expectedDate = getDateFromSequentialSerialNumber(Long.parseLong(propertyValue));
                        } else {
                            try {
                                expectedDate = LocalDate.parse(propertyValue, DateTimeFormatter.ofPattern("M/d/yy"));
                            } catch (java.time.format.DateTimeParseException e) {
                                expectedDate = LocalDate.parse(propertyValue, DateTimeFormatter.ofPattern("M/d/yyyy"));
                            }
                        }
                        expectedDate.format(DateTimeFormatter.ISO_DATE);
                        LocalDate actualDate = LocalDate.parse(propertyInDatabase.values.get(0).toString(), DateTimeFormatter.ISO_DATE_TIME);
                        Assert.assertEquals(actualDate, expectedDate, errorMessage);
                    } else {
                        Assert.assertEquals(propertyInDatabase.values, Collections.singletonList(propertyValue), errorMessage);
                    }
                    break;
                case "test_prop_4":
                    Assert.assertEquals(
                            parseBoolean(propertyInDatabase.values.get(0).toString()),
                            parseBoolean(propertyValue),
                            errorMessage
                    );
                    break;
                case "test_prop_5":
                    Assert.assertEquals(propertyInDatabase.values, Collections.singletonList(propertyValue), errorMessage);
                    break;
                default:
                    throw new IndexOutOfBoundsException("Invalid property");
            }
        }
    }

    private void verifyProductPropertiesWereImportedProperly(Company company, List<String> normalProperties, LocalDate currentDate, VariantImportExcelData row) throws Exception {
        String localeId = company.getLocaleId(row.locale);
        String retailerId = row.retailer.isEmpty() ? null : company.getRetailerId(row.retailer);
        String campaignId = row.campaign.isEmpty() ? null : company.getCampaignId(row.campaign);

        var instancePath = new ProductVariantInstancePath(row.productIdentifier, localeId, Enums.ProductVariantType.LIVE, retailerId, campaignId);

        ProductVariantPropertySet propertySetAfterImport = ProductVersioningApiService.getPropertySetDataByUniqueId(instancePath, null, jwt);

        ProductVariantDigitalAssetSet digitalAssetPropertySetAfterImport = ProductVersioningApiService.getDigitalAssetSetDataByUniqueId(instancePath, null, jwt);

        LocalDate propertySetCreationDate = propertySetAfterImport.dateCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Assert.assertEquals(propertySetCreationDate, currentDate, "New property set was not generated after import");

        boolean rowHasDigitalAssetProperties = row.properties
                .stream()
                .anyMatch(property -> !normalProperties.contains(property.id) && !Objects.equals(property.value, ""));

        if (rowHasDigitalAssetProperties) {
            LocalDate digitalAssetSetCreationDate = digitalAssetPropertySetAfterImport.dateCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Assert.assertEquals(digitalAssetSetCreationDate, currentDate, "New digital asset property set was not generated after import");
        }

        for (VariantImportExcelData.PropertyData propertyInFile : row.properties) {
            if (!Objects.equals(propertyInFile.value, "")) {
                boolean isDigitalAssetProperty = !normalProperties.contains(propertyInFile.id);

                if (isDigitalAssetProperty) {
                    var digitalAssetPropertyInDB = digitalAssetPropertySetAfterImport.digitalAssets
                            .stream()
                            .filter(prop -> prop.id.equals(propertyInFile.id))
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException("Digital Asset Property: " + propertyInFile.id + " not found in property set"));
                    verifyDigitalAssetInfo(digitalAssetPropertyInDB, propertyInFile.id, propertyInFile.value);
                } else {
                    ProductVariantProperty propertyInDB = propertySetAfterImport.properties
                            .stream()
                            .filter(prop -> prop.id.equals(propertyInFile.id))
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException("Property: " + propertyInFile.id + " not found in property set"));
                    verifyPropertiesWereTransformedProperly(propertyInFile.id, propertyInFile.value, propertyInDB, Enums.ImportStage.PUBLISH);
                }
            }
        }
    }

    private void verifyProductWasNotAddedToCompany(VariantImportExcelData variant) {
        ProductMaster productMaster = ProductVersioningApiService.getProductWithUniqueIdIfExist(variant.productIdentifier, jwt);
        Assert.assertNull(productMaster, "Product with unique id: " + variant.productIdentifier + " was added to company");
    }

    private void verifyRPCWasImportedProperly(Company company, VariantImportExcelData row) throws Exception {
        Enums.ProductVariantLevel productVersion = new UserFriendlyInstancePath(row.productIdentifier, row.locale, row.retailer, row.campaign).getProductLevel();

        String localeId = company.getLocaleId(row.locale);
        String retailerId = company.getRetailerId(row.retailer);
        String campaignId = company.getCampaignId(row.campaign);

        ProductMaster productToImport = ProductVersioningApiService.getProductMasterByUniqueId(row.productIdentifier, jwt);
        ProductVariantInstancePath variantInstancePath = new ProductVariantInstancePath(productToImport._id, localeId, Enums.ProductVariantType.LIVE, retailerId, campaignId);

        if (productVersion.equals(Enums.ProductVariantLevel.RETAILER) || productVersion.equals(Enums.ProductVariantLevel.RETAILER_CAMPAIGN)) {
            ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceRetailer instanceToTest = (ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceRetailer)
                    productToImport.getInstanceBaseData(variantInstancePath);
            String expectedRPC = row.rpc.isEmpty() ? null : row.rpc;
            Assert.assertEquals(instanceToTest.rpc, expectedRPC, "RPC of instance with id: " + instanceToTest.id + " didn't match with RPC in imported file");
        }
    }

    private void verifyProductNameWasImportedProperly(Company company, Map.Entry<String, VariantImportExcelData> rowAndOriginalName) throws Exception {
        var row = rowAndOriginalName.getValue();
        Enums.ProductVariantLevel productVersion = new UserFriendlyInstancePath(row.productIdentifier, row.locale, row.retailer, row.campaign).getProductLevel();

        String localeId = company.getLocaleId(row.locale);
        String retailerId = company.getRetailerId(row.retailer);
        String campaignId = company.getCampaignId(row.campaign);

        ProductMaster productToImport = ProductVersioningApiService.getProductMasterByUniqueId(row.productIdentifier, jwt);
        ProductVariantInstancePath variantInstancePath = new ProductVariantInstancePath(productToImport._id, localeId, Enums.ProductVariantType.LIVE, retailerId, campaignId);

        var instanceBaseData = productToImport.getInstanceBaseData(variantInstancePath);

        var productNameBeforeImport = rowAndOriginalName.getKey();

        if (!productNameBeforeImport.equals("null") && row.productName.isEmpty()) {
            Assert.assertEquals(instanceBaseData.name, productNameBeforeImport, "Product name of instance with id: " + instanceBaseData.id + " didn't match with product name before import");
        } else if (productNameBeforeImport.equals("null") && row.productName.isEmpty()) {
            String defaultProductName = row.productIdentifier + " " + localeId;
            switch (productVersion) {
                case GLOBAL:
                    defaultProductName += " global";
                    break;
                case RETAILER:
                    defaultProductName += " retailer " + retailerId;
                    break;
                case GLOBAL_CAMPAIGN:
                    defaultProductName += " globalCampaign " + campaignId;
                    break;
                case RETAILER_CAMPAIGN:
                    defaultProductName += " retailerCampaign " + retailerId + " " + campaignId;
                    break;
            }
            Assert.assertEquals(instanceBaseData.name, defaultProductName, "Product name of instance with id: " + instanceBaseData.id + " didn't match with the default product name");
        } else {
            Assert.assertEquals(instanceBaseData.name, row.productName, "Product name of instance with id: " + instanceBaseData.id + " didn't match with product name in imported file");
        }
    }

    private String getVariantPropertySetId(ProductMaster product, String expectedLocaleId, String expectedCampaignId, String expectedSystemRetailerId) {
        return product.variantSets.live.stream().filter(variant -> variant.localeId.equals(expectedLocaleId)).findFirst()
                .orElseThrow(NoSuchElementException::new)
                .instances.retailerCampaign.stream()
                .filter(instance -> instance.campaignId.equals(expectedCampaignId) && instance.retailerId.equals(expectedSystemRetailerId))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .propertySetId;
    }

    private void verifyDigitalAssetInfo(DigitalAssetProperty digitalAssetPropertyInDB, String propertyId, String propertyValue) {
        // TODO: Uncomment when <delete> tag is implemented for digital asset properties
        /*if (propertyValue.equals("<delete>")) {
            Assert.assertNull(digitalAssetPropertyInDB, "Property: " + propertyId + " was not deleted from product after importing <delete> tag");
            return;
        }*/

        Assert.assertEquals(digitalAssetPropertyInDB.id, propertyId, "Digital Asset property id doesn't match with id in imported file");

        String[] expectedValues = Arrays.stream(propertyValue.split("\\|")).map(String::trim)
                .toArray(String[]::new);

        Assert.assertEquals(digitalAssetPropertyInDB.assets.size(), expectedValues.length, "Number of assets in property doesn't match with imported assets");

        for (var expectedValue : expectedValues) {
            String assetIdRegex = "([a-fA-F0-9]{8}(?:-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12})";
            String regex = "https://" + getMediaSiteDomain() + "/assets/" + assetIdRegex + "/";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(expectedValue);

            DigitalAssetProperty.Assets assetToTest;
            String fileName;

            // If the url in the file is already hosted by media, then we need to verify the link in the db matches the url in the file, spaces are replaced by %20
            if (matcher.find()) {
                expectedValue = expectedValue.replace(" ", "%20");
                fileName = new File(expectedValue).getName();
                assetToTest = digitalAssetPropertyInDB.assets
                        .stream()
                        .filter(asset -> asset.url.endsWith(fileName))
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new);
                LOGGER.info("Asset URL: " + assetToTest.url);

                Assert.assertEquals(assetToTest.url, expectedValue, "Digital Asset link doesn't match with expected value");
            } else {
                // For files that are not hosted in media, file name will have the space
                fileName = new File(expectedValue).getName();
                assetToTest = digitalAssetPropertyInDB.assets
                        .stream()
                        .filter(asset -> asset.url.endsWith(fileName))
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new);
            }

            regex = regex + Pattern.quote(fileName);
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(assetToTest.url);
            Assert.assertTrue(matcher.matches(), "Digital Asset link doesn't match with expected format");

            Assert.assertEquals(assetToTest.mediaAssetId, matcher.group(1), "Media asset id doesn't match with id in s3 link");
        }
    }

    private void removeProductIfExists(VariantImportExcelData variant) throws Exception {
        ProductMaster productMaster = ProductVersioningApiService.getProductWithUniqueIdIfExist(variant.productIdentifier, jwt);
        if (productMaster != null) {
            ProductVersioningApiService.deleteProductMaster(productMaster._id, jwt);
        }
    }

    private void getNewProductsAndDeleteIfTheyExist() throws Exception {
        List<VariantImportExcelData> dataInExcelFile = downloadFileAndGetVariantDataToImport(TEST_DATA_DIRECTORY, FILE_WITH_LISTS);
        removeProductIfExists(dataInExcelFile.get(0));
        removeProductIfExists(dataInExcelFile.get(2));
        dataInExcelFile = downloadFileAndGetVariantDataToImport(TEST_DATA_DIRECTORY, VALID_DATA_IMPORT_URL);
        removeProductIfExists(dataInExcelFile.get(dataInExcelFile.size() - 1));
        dataInExcelFile = downloadFileAndGetVariantDataToImport(TEST_DATA_DIRECTORY, FILE_WITH_PARTIAL_FAILURE);
        removeProductIfExists(dataInExcelFile.get(1));
        removeProductIfExists(dataInExcelFile.get(2));
    }

    private ProductVariantListProduct getExpectedProductInfoInList(VariantImportExcelData row) throws Exception {
        String localeId = company.getLocaleId(row.locale);
        String retailerId = company.getRetailerId(row.retailer);
        String campaignId = company.getCampaignId(row.campaign);

        ProductMaster productMaster = ProductVersioningApiService.getProductMasterByUniqueId(row.productIdentifier, jwt);
        InstancePathBase instancePath = new InstancePathBase(productMaster._id, localeId, retailerId, campaignId);
        ProductMasterInstanceComposition instance = ProductVersioningApiService.getProductInstanceComposition(instancePath, jwt);
        return new ProductVariantListProduct(instance.productMasterInfo, instance.masterUniqueId, instance.productMasterInfo.liveInstanceId);
    }

    private void verifyProductWasAddedToList(VariantImportExcelData row, FriendlyProductVariantList productList, boolean productWillBeRemoved) throws Exception {
        ProductVariantListProduct expectedInstanceInList = getExpectedProductInfoInList(row);

        if (productList.name.equals(STATIC_LIST_NAME) && !INSTANCE_IDS_TO_REMOVE_FROM_STATIC_LIST.contains(expectedInstanceInList.instanceId)) {
            INSTANCE_IDS_TO_REMOVE_FROM_STATIC_LIST.add(expectedInstanceInList.instanceId);
        }

        if (productWillBeRemoved && !IDS_OF_PRODUCTS_TO_DELETE.contains(row.productIdentifier)) {
            IDS_OF_PRODUCTS_TO_DELETE.add(row.productIdentifier);
        }

        long numberOfInstancesWithCriteria = productList.products.stream().filter(product -> product.equals(expectedInstanceInList)).count();
        Assert.assertNotEquals(numberOfInstancesWithCriteria, 0, "Product with instance path:" + expectedInstanceInList.getInstancePathBase() + " was not added to list " + row.productListName);
        Assert.assertEquals(numberOfInstancesWithCriteria, 1, "Product with instance path:" + expectedInstanceInList.getInstancePathBase() + " was added to list " + row.productListName + " more than once");
    }

    private void verifyProductNameAndThumbnailWereAddedToMaster(Map.Entry<String, VariantImportExcelData> rowAndOriginalName) throws Exception {
        var row = rowAndOriginalName.getValue();

        ProductMaster productToTest = ProductVersioningApiService.getProductMasterByUniqueId(row.productIdentifier, jwt);

        var productNameBeforeImport = rowAndOriginalName.getKey();

        if (!productNameBeforeImport.equals("null") && row.productName.isEmpty()) {
            Assert.assertEquals(productToTest.name, productNameBeforeImport, "Name of product master: " + row.productIdentifier + " didn't match with product name before import");
        } else if (productNameBeforeImport.equals("null") && row.productName.isEmpty()) {
            Assert.assertEquals(productToTest.name, row.productIdentifier, "Default name of product master: " + row.productIdentifier + " didn't match with the product identifier");
        } else {
            Assert.assertEquals(productToTest.name, row.productName, "Name of product master: " + row.productIdentifier + " didn't match with product name in imported file");
        }

        if (!row.thumbnail.isEmpty()) {
            Assert.assertEquals(productToTest.thumbnail, row.thumbnail, "Thumbnail of product master: " + row.productIdentifier + " didn't match with thumbnail in imported file");
        }
    }

    private void verifyProductNameAndThumbnail(String expectedName, String expectedThumbnail, String instanceName, String instanceThumbnail) {
        Assert.assertEquals(instanceName, expectedName, "Name of instance didn't match the name in imported file");
        Assert.assertEquals(instanceThumbnail, expectedThumbnail, "Thumbnail of instance didn't match the thumbnail in imported file");
    }

    private void verifyInstanceIsInList(List<ProductVariantListProduct> productsInList, String expectedInstanceId) {
        var instanceIsInList = productsInList.stream().anyMatch(prod -> prod.instanceId.equals(expectedInstanceId));
        Assert.assertTrue(instanceIsInList, "Instance: " + expectedInstanceId + " was not imported to product list.\nInstances in list: " + productsInList);
    }
}
