package com.DC.uitests.adc.execute.productManager.products;

import com.DC.constants.InsightsConstants;
import com.DC.objects.productVersioning.ImportsTableData;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.imports.ImportsPage;
import com.DC.pageobjects.adc.execute.productManager.products.ExportModal;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.testcases.BaseClass;
import com.DC.tests.sharedAssertions.ImportAssertions;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.XLUtils.ProductKeywordsXLUtils;
import com.DC.utilities.XLUtils.ProductPropertiesXLUtils;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantInstancePath;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static com.DC.utilities.DateUtility.getCurrentDateTime;
import static org.awaitility.Awaitility.await;

public class ProductsGridExportAndImportUITests extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String INVALID_FILE_PATH = System.getProperty("user.dir") + "/src/test/java/com/DC/utilities/samples/images/stlcity.png";
    private ProductsPage productsPage;
    private ExportModal exportModal;

    private final List<String> productsToDelete = new ArrayList<>();

    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).loginDcApp(USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_PRODUCTS_URL);
        productsPage = new ProductsPage(driver);
        jwt = SecurityAPI.getJwtForInsightsUser(driver);
    }

    @AfterMethod
    public void resetPage() {
        try {
        productsPage.closeReactModalIfDisplayed();
        productsPage.closeNoteIfDisplayed(Enums.NoteType.SUCCESS);
        } catch (Exception ignored) {
        }

        if (!driver.getCurrentUrl().contains(InsightsConstants.INSIGHTS_PRODUCTS_URL)) {
            driver.get(InsightsConstants.INSIGHTS_PRODUCTS_URL);
            productsPage = new ProductsPage(driver);
        }
    }

    @AfterClass(alwaysRun = true)
    public void killDriver() {
        try {
            for (var productUniqueId : productsToDelete) {
                ProductVersioningApiService.deleteProductMasterByUniqueId(productUniqueId, jwt);
            }
        } catch (Exception ignored) {
        }
        quitBrowser();
    }

    @Test(priority = 1, description = "C253472. All products option is selected by default. Create Export button & Only Selected Products option are disabled")
    public void CGEN_ProductsGrid_Exports_CorrectDefaultOptionsAreSelected() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();

        productsPage.deselectAllProducts();
        exportModal = productsPage.openModalToExportData();

        var selectedOption = exportModal.getSelectedProductSettings();
        softAssert.assertEquals(selectedOption, "all", "All Products option was not selected by default when no products were selected");

        var defaultDataSettings = exportModal.getSelectedDataSettings();
        softAssert.assertEquals(defaultDataSettings, "live", "Live Data option was not selected by default");

        var onlySelectedProductsOptionEnabled = exportModal.isOnlySelectedProductsOptionEnabled();
        softAssert.assertFalse(onlySelectedProductsOptionEnabled, "'Only Selected Products' option was enabled");

        var createExportButtonEnabled = exportModal.isCreateExportButtonEnabled();
        softAssert.assertFalse(createExportButtonEnabled, "'Create Export' button was enabled");

        productsPage = exportModal.clickCancelButton();
        productsPage.selectAllProductsOnPage();
        exportModal = productsPage.openModalToExportData();

        onlySelectedProductsOptionEnabled = exportModal.isOnlySelectedProductsOptionEnabled();
        softAssert.assertTrue(onlySelectedProductsOptionEnabled, "'Only Selected Products' option was disabled");

        selectedOption = exportModal.getSelectedProductSettings();
        softAssert.assertEquals(selectedOption, "selected", "'Only Selected Products' option was not selected by default when products were selected");

        softAssert.assertAll();
    }

    @Test(priority = 2, description = "C253474. Property Settings section is displayed after selecting 'Properties' option")
    public void CGEN_ProductsGrid_Exports_PropertySettingsSectionDisplaysAfterSelectingPropertiesOption() {
        SoftAssert softAssert = new SoftAssert();

        var dataTypesToExport = List.of(Enums.ExportSubType.PROPERTY);
        exportModal = productsPage.openModalToExportData();
        exportModal.selectDataTypesToExport(dataTypesToExport);

        var propertySettingsSectionDisplayed = exportModal.isPropertySettingsSectionDisplayed();
        softAssert.assertTrue(propertySettingsSectionDisplayed, "Property Settings section was not displayed after selecting 'Properties' option");

        var selectedPropertySettings = exportModal.getSelectedPropertySettings();
        softAssert.assertNull(selectedPropertySettings, "One of the Property Settings options was selected by default");

        var createExportButtonEnabled = exportModal.isCreateExportButtonEnabled();
        softAssert.assertFalse(createExportButtonEnabled, "Create Export button was enabled");

        exportModal.deselectDataTypes(dataTypesToExport);
        propertySettingsSectionDisplayed = exportModal.isPropertySettingsSectionDisplayed();
        softAssert.assertFalse(propertySettingsSectionDisplayed, "Property Settings section was still displayed after unselecting 'Properties' option");

        softAssert.assertAll();
    }

    @Test(priority = 3, description = "C253475. User can export product properties", dataProvider = "dataTypeToExport")
    public void CGEN_ProductsGrid_Exports_CanExportProperties(Enums.ProductVariantType dataType) throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        var dataTypesToExport = List.of(Enums.ExportSubType.PROPERTY);

        productsPage.selectAllProductsOnPage();
        exportModal = productsPage.openModalToExportData()
                .selectProductSettings(false)
                .selectDataTypesToExport(dataTypesToExport)
                .selectPropertySettings(true);

        productsPage = createExportAndVerifySuccessMessageIsDisplayed(dataType);

        productsPage.selectAllProductsOnPage();
        exportModal = productsPage.openModalToExportData()
                .selectProductSettings(true)
                .selectDataTypesToExport(dataTypesToExport)
                .selectPropertySettings(false);

        productsPage = createExportAndVerifySuccessMessageIsDisplayed(dataType);

        softAssert.assertAll();
    }

    @Test(priority = 4, description = "C253476. User can export product attributes", dataProvider = "dataTypeToExport")
    public void CGEN_ProductsGrid_Exports_CanExportAttributes(Enums.ProductVariantType dataType) throws InterruptedException {
        performExportTestForSingleDataType(Enums.ExportSubType.ATTRIBUTE, dataType);
    }

    @Test(priority = 5, description = "C253477. User can export product keywords", dataProvider = "dataTypeToExport")
    public void CGEN_ProductsGrid_Exports_CanExportKeywords(Enums.ProductVariantType dataType) throws InterruptedException {
        performExportTestForSingleDataType(Enums.ExportSubType.KEYWORDS, dataType);
    }

    @Test(priority = 6, description = "C253478. User can export product digital assets", dataProvider = "dataTypeToExport")
    public void CGEN_ProductsGrid_Exports_CanExportDigitalAssets(Enums.ProductVariantType dataType) throws InterruptedException {
        performExportTestForSingleDataType(Enums.ExportSubType.DIGITAL_ASSET, dataType);
    }

    @Test(priority = 7, description = "C253479. User can export product all data types", dataProvider = "dataTypeToExport")
    public void CGEN_ProductsGrid_Exports_CanExportAllDataTypes(Enums.ProductVariantType dataType) throws InterruptedException {
        productsPage.selectAllProductsOnPage();
        exportModal = productsPage.openModalToExportData();

        var availableDataTypes = exportModal.getAvailableDataTypes();
        exportModal.selectProductSettings(true);
        exportModal.selectAllDataTypes();
        var selectedDataTypes = exportModal.getSelectedDataTypes();
        Assert.assertEquals(selectedDataTypes, availableDataTypes, "Not all data types were selected");

        exportModal.selectPropertySettings(true);
        createExportAndVerifySuccessMessageIsDisplayed(dataType);
    }

    @Test(priority = 8, description = "It tests template, invalid extension and imports a valid file for product properties.")
    public void CGEN_ProductsGrid_ProductPropertiesImportModalWorksAsExpected() throws Exception {
        var softAssert = new SoftAssert();

        var importModal = productsPage.openModalToImportData();

        var saveAndExitModalEnabled = importModal.isSaveAndExitButtonEnabled();
        softAssert.assertFalse(saveAndExitModalEnabled, "Save and exit button was enabled before uploading a file");

        ImportAssertions.verifyProductTemplateHasCorrectHeaders(Enums.ImportType.PROPERTY, importModal, softAssert, downloadFolder, jwt);

        var fileName = "ProductPropertiesImportForUITests.xlsx";
        var validFilePath = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/" + fileName;
        productsPage = ImportAssertions.verifyProductPropertyImportModalWorks(INVALID_FILE_PATH, validFilePath, importModal, softAssert, downloadFolder, ProductsPage.class);
        productsPage = verifyFileWasSuccessfullyImported(fileName, Enums.ImportType.PROPERTY, 1, softAssert);

        // Testing product was added to db
        var expectedProductIdentifier = ProductPropertiesXLUtils.getVariantDataToImport(validFilePath).get(0).productIdentifier;
        productsToDelete.add(expectedProductIdentifier);
        var productWithUniqueId = ProductVersioningApiService.getProductWithUniqueIdIfExist(expectedProductIdentifier, jwt);
        softAssert.assertNotNull(productWithUniqueId, "Product was not added to db");

        // Testing product is displayed in UI
        productsPage.searchByProductIdentifier(expectedProductIdentifier);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(
                () ->
                {
                    var instancesDisplayed = productsPage.getInstanceUniqueIdsDisplayed();
                    var expectedInstancesCount = 2; // 1 for the base product and 1 for the version
                    softAssert.assertEquals(
                            instancesDisplayed.size(),
                            expectedInstancesCount,
                            "Expected " + expectedInstancesCount + " instances of product " + expectedProductIdentifier + " to be displayed but found " + instancesDisplayed.size()
                    );
                }
        );
        softAssert.assertAll();
    }

    @Test(priority = 9, description = "It tests template, invalid extension and imports a valid file for product keywords")
    public void CGEN_ProductsGrid_ProductKeywordsImportModalWorksAsExpected() throws Exception {
        var softAssert = new SoftAssert();
        var randomNumber = SharedMethods.generateRandomNumber();

        // GETTING KEYWORD SET BEFORE IMPORT
        var fileNameWithExtension = "ProductKeywordsImportForUITests.xlsx";
        var validFilePath = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/" + fileNameWithExtension;
        var productToTest = ProductKeywordsXLUtils.getProductKeywordsInFile(validFilePath).get(0);

        var company = CompanyApiService.getCompany(jwt);
        var localeId = company.getLocaleId(productToTest.locale);
        var campaignId = company.getCampaignId(productToTest.campaign);
        var retailerId = company.getRetailerId(productToTest.retailer);

        var instancePath = new ProductVariantInstancePath(productToTest.productIdentifier, localeId, Enums.ProductVariantType.LIVE, retailerId, campaignId);
        var keywordBucketBeforeImport = ProductVersioningApiService.getProductKeywordSetByUniqueId(instancePath, null, jwt).keywords.optional;
        var keywordToAdd = "automated value " + randomNumber;
        ProductKeywordsXLUtils.replaceCellValue(validFilePath, 1, 5, keywordToAdd);

        // TESTING TEMPLATE
        var importModal = productsPage.openModalToImportData();
        ImportAssertions.verifyProductTemplateHasCorrectHeaders(Enums.ImportType.KEYWORD, importModal, softAssert, downloadFolder, jwt);

        // TESTING IMPORT WITHOUT OVERWRITING EXISTING DATA
        importModal.selectFlagForKeywordImport(false);
        productsPage = ImportAssertions.verifyProductPropertyImportModalWorks(INVALID_FILE_PATH, validFilePath, importModal, softAssert, downloadFolder, ProductsPage.class);
        verifyFileWasSuccessfullyImported(fileNameWithExtension, Enums.ImportType.KEYWORD, 1, softAssert);

        var keywordBucketAfterImport = ProductVersioningApiService.getProductKeywordSetByUniqueId(instancePath, null, jwt).keywords.optional;
        var expectedKeywordsInBucket = new ArrayList<>(keywordBucketBeforeImport);
        expectedKeywordsInBucket.add(keywordToAdd);
        softAssert.assertTrue(keywordBucketAfterImport.contains(keywordToAdd), "Keyword was not imported to keyword bucket");
        softAssert.assertEquals(keywordBucketAfterImport, expectedKeywordsInBucket, "Keyword bucket is not as expected after import");

        // TESTING IMPORT OVERWRITING EXISTING DATA
        keywordToAdd = "automated value " + randomNumber + 1;
        ProductKeywordsXLUtils.replaceCellValue(validFilePath, 1, 5, keywordToAdd);

        importModal = productsPage.openModalToImportData();
        importModal.selectTypeOfImport(Enums.ImportType.KEYWORD);
        importModal.selectFlagForKeywordImport(true);
        importModal.uploadFile(validFilePath);
        importModal.waitForImportToUpload();
        importModal.clickSaveAndExitButton();
        verifyFileWasSuccessfullyImported(fileNameWithExtension, Enums.ImportType.KEYWORD, 1, softAssert);

        keywordBucketAfterImport = ProductVersioningApiService.getProductKeywordSetByUniqueId(instancePath, null, jwt).keywords.optional;

        expectedKeywordsInBucket.clear();
        expectedKeywordsInBucket.add(keywordToAdd);
        softAssert.assertEquals(keywordBucketAfterImport, expectedKeywordsInBucket, "Keyword bucket was not overwritten after import");

        softAssert.assertAll();
    }

    @Test(priority = 10, description = "It tests template, invalid extension and imports a valid file for master data.")
    public void CGEN_ProductsGrid_ProductMasterDataImportModalWorksAsExpected() throws Exception {
        var softAssert = new SoftAssert();

        var fileName = "MasterProductDataImportForUITests.xlsx";
        var validFilePath = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/" + fileName;

        var data = ProductPropertiesXLUtils.getDataToImport(validFilePath).get(0);
        var expectedProductIdentifier = data.get("product identifier");
        var productMasterBefore = ProductVersioningApiService.getProductMasterByUniqueId(expectedProductIdentifier, jwt);
        var productMasterNameBefore = productMasterBefore.name;
        var productMasterThumbnailBefore = productMasterBefore.thumbnail;

        var newMasterName = productMasterNameBefore + "-" + SharedMethods.generateRandomString();
        ProductPropertiesXLUtils.replaceCellValue(validFilePath, 1, 1, newMasterName);

        var newMasterThumbnail = Objects.equals(productMasterThumbnailBefore, "https://os-media-service.s3.amazonaws.com/development/OneSpaceTest/Image+Comparison/imageCompareTestData/small.jpg")
                ? "https://thumbs.dreamstime.com/z/quality-assurance-service-guarantee-standard-internet-business-technology-concept-quality-assurance-service-guarantee-standard-123697462.jpg"
                : productMasterThumbnailBefore;
        ProductPropertiesXLUtils.replaceCellValue(validFilePath, 1, 2, newMasterThumbnail);

        var importModal = productsPage.openModalToImportData();

        var saveAndExitModalEnabled = importModal.isSaveAndExitButtonEnabled();
        softAssert.assertFalse(saveAndExitModalEnabled, "Save and exit button was enabled before uploading a file");

        ImportAssertions.verifyMasterProductDataTemplateHasCorrectHeaders(importModal, softAssert, downloadFolder, jwt);

        productsPage = ImportAssertions.verifyProductPropertyImportModalWorks(INVALID_FILE_PATH, validFilePath, importModal, softAssert, downloadFolder, ProductsPage.class);
        productsPage = verifyFileWasSuccessfullyImported(fileName, Enums.ImportType.PROPERTY, 0, softAssert);

        // Testing master product data was updated
        var productMasterAfterImport = ProductVersioningApiService.getProductMasterByUniqueId(expectedProductIdentifier, jwt);
        Assert.assertEquals(productMasterAfterImport.name, newMasterName, "Product master name was not updated");
        Assert.assertEquals(productMasterAfterImport.thumbnail, newMasterThumbnail, "Product master thumbnail was not updated");

        softAssert.assertAll();
    }

    @Test(priority = 11, description = "Error message displays if user exports product with no staged data")
    public void CGEN_ProductsGrid_StagedExportFailsIfProductDoesNotHaveStagedData() {
        var dataTypesToExport = List.of(Enums.ExportSubType.PROPERTY);

        var productToSelect = new UserFriendlyInstancePath("QA-EXPORTS-EMPTY", "fr-FR", "Amazon.com", "Christmas");
        productsPage.searchByProductIdentifier(productToSelect.productIdentifier);
        productsPage.selectProduct(productToSelect);
        exportModal = productsPage.openModalToExportData()
                .selectProductSettings(false)
                .selectDataTypesToExport(dataTypesToExport)
                .selectPropertySettings(true)
                .selectDataSettings(Enums.ProductVariantType.STAGED);

        var message = "Unable to Enqueue Product Property Export. Could not find staged instances that have properties or digital assets.";
        productsPage = exportModal.clickCreateExportButton();
        var isMessageDisplayed = productsPage.isNoteDisplayedWithMessage(Enums.NoteType.INFO, message);
        Assert.assertTrue(isMessageDisplayed, "Info message was not displayed after exporting product with no staged data");
    }

    private ProductsPage createExportAndVerifySuccessMessageIsDisplayed(Enums.ProductVariantType dataToExport) {
        exportModal.selectDataSettings(dataToExport);

        var createExportButtonEnabled = exportModal.isCreateExportButtonEnabled();
        Assert.assertTrue(createExportButtonEnabled, "Create Export button was disabled");

        var message = "Your requested exports are being prepared and will soon be emailed.";
        productsPage = exportModal.clickCreateExportButton();
        var isMessageDisplayed = productsPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, message);
        Assert.assertTrue(isMessageDisplayed, "Success message was not displayed after clicking Create Export button");

        return productsPage;
    }

    private void performExportTestForSingleDataType(Enums.ExportSubType dataTypeToExport, Enums.ProductVariantType dataToExport) throws InterruptedException {
        // ALL PRODUCTS
        productsPage.selectAllProductsOnPage();
        exportModal = productsPage.openModalToExportData()
                .selectProductSettings(true)
                .selectDataTypesToExport(List.of(dataTypeToExport));

        var propertySettingSectionDisplayed = exportModal.isPropertySettingsSectionDisplayed();
        Assert.assertFalse(propertySettingSectionDisplayed, "Property Settings section was displayed after selecting " + dataTypeToExport + " option");

        var createExportButtonEnabled = exportModal.isCreateExportButtonEnabled();
        Assert.assertTrue(createExportButtonEnabled, "Create Export button was disabled");

        productsPage = createExportAndVerifySuccessMessageIsDisplayed(dataToExport);

        // ONLY SELECTED PRODUCTS
        productsPage.selectAllProductsOnPage();
        exportModal = productsPage.openModalToExportData()
                .selectProductSettings(false)
                .selectDataTypesToExport(List.of(dataTypeToExport));

        productsPage = createExportAndVerifySuccessMessageIsDisplayed(dataToExport);
    }

    private ProductsPage verifyFileWasSuccessfullyImported(String fileName, Enums.ImportType importType, int expectedVersionsUpdated, SoftAssert softAssert) {
        var successMessageDisplayed = productsPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after importing file " + fileName + " for import type " + importType);

        var importsPage = productsPage.navigateToUrl(InsightsConstants.INSIGHTS_IMPORTS_URL, ImportsPage.class);
        var latestImportData = importsPage.getDataOfLatestImport();

        var expectedImportInfo = new ImportsTableData(fileName, importType, expectedVersionsUpdated, "", getCurrentDateTime("MM/dd/yyyy"), "Success");
        softAssert.assertEquals(latestImportData, expectedImportInfo, "Latest import data is not as expected");

        return productsPage.navigateToUrl(InsightsConstants.INSIGHTS_PRODUCTS_URL, ProductsPage.class);
    }

    @DataProvider
    private static Object[][] dataTypeToExport() {
        return new Object[][]{
                {Enums.ProductVariantType.LIVE},
                {Enums.ProductVariantType.STAGED},
        };
    }
}
