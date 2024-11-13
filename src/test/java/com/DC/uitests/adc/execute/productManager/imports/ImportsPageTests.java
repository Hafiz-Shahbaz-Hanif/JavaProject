package com.DC.uitests.adc.execute.productManager.imports;

import com.DC.constants.InsightsConstants;
import com.DC.objects.productVersioning.ImportsTableData;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.imports.ImportsPage;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.pageobjects.adc.execute.productManager.properties.PropertiesPage;
import com.DC.testcases.BaseClass;
import com.DC.tests.sharedAssertions.ImportAssertions;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.XLUtils.CompanyPropertiesXLUtils;
import com.DC.utilities.XLUtils.ProductKeywordsXLUtils;
import com.DC.utilities.XLUtils.ProductPropertiesXLUtils;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.SharedRequests;
import com.DC.utilities.apiEngine.apiServices.insights.CPGAccount.CPGAccountService;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantInstancePath;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static com.DC.tests.sharedAssertions.ImportAssertions.getExpectedErrorRowData;
import static com.DC.tests.sharedAssertions.ImportAssertions.verifyErrorReportContainsCorrectData;
import static com.DC.utilities.DateUtility.getCurrentDateTime;
import static org.awaitility.Awaitility.await;

public class ImportsPageTests extends BaseClass {
    private final String SUPPORT_USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String COMPANY = READ_CONFIG.getInsightsAutomatedTestCompanyName();
    private final String INVALID_FILE_PATH = System.getProperty("user.dir") + "/src/test/java/com/DC/utilities/samples/images/stlcity.png";
    private ImportsPage importsPage;

    private final List<String> companyPropertiesToDelete = new ArrayList<>();
    private final List<String> productsToDelete = new ArrayList<>();

    private String jwt;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        importsPage = loginAndNavigateToImportsPage();
        jwt = SecurityAPI.getJwtForInsightsUser(driver);
    }

    @AfterClass(alwaysRun = true)
    public void cleanupPropertiesAndGroups() {
        try {
            CompanyApiService.deleteRegularPropertiesFromCompany(companyPropertiesToDelete, jwt);
            for (var productUniqueId : productsToDelete) {
                ProductVersioningApiService.deleteProductMasterByUniqueId(productUniqueId, jwt);
            }
        } catch (Exception ignored) {
        }
        quitBrowser();
    }

    @AfterMethod()
    public void goToImportsPageIfNotThere() throws InterruptedException {
        var isOnLoginPage = driver.getTitle().equals("Flywheel");
        if (isOnLoginPage) {
            importsPage = loginAndNavigateToImportsPage();
        } else if (!Objects.equals(driver.getTitle(), "Imports | Flywheel")) {
            driver.get(InsightsConstants.INSIGHTS_IMPORTS_URL);
            importsPage = new ImportsPage(driver);
        }
        importsPage.closeReactModalIfDisplayed();
    }

    @Test(priority = 1, description = "C243980. Correct headers displayed")
    public void CGEN_ImportsPage_CorrectColumnsAreDisplayed() {
        var columnsDisplayed = importsPage.tableCommonFeatures.getColumnsDisplayed();
        var expectedColumns = new ArrayList<>(List.of("Name", "Type", "# of Versions Updated", "Created By", "Date Completed", "Status"));

        Assert.assertEquals(columnsDisplayed, expectedColumns, "Headers are not as expected");
    }

    @Test(priority = 2, description = "C243981. Can search by name, type, username and status")
    public void CGEN_ImportsPage_CanSearchForImports() throws InterruptedException {
        importsPage.searchForImport("");

        var searchTerm = "ImportProductProperties_WithList.xlsx";
        performSearchTestAndGetDisplayedData(searchTerm);

        searchTerm = "property";
        performSearchTestAndGetDisplayedData(searchTerm);

        searchTerm = "Partial Failure";
        performSearchTestAndGetDisplayedData(searchTerm);

        // negative tests
        var latestImport = importsPage.getDataOfLatestImport();
        performSearchTestForInvalidColumns(latestImport.createdBy);
        performSearchTestForInvalidColumns(latestImport.dateCompleted);
    }

    @Test(priority = 3, description = "C243982. Pagination works")
    public void CGEN_ImportsPage_PaginationWorks() throws InterruptedException {
        importsPage.searchForImport("");

        int activePageNumberBefore = importsPage.paginator.getActivePageNumber();
        var firstImportDataBefore = importsPage.getDataOfLatestImport();

        SoftAssert softAssert = new SoftAssert();

        // TESTING CLICKING ON NEXT BUTTON
        importsPage.paginator.goToNextPage();
        importsPage.waitForPageToFullyLoad();
        int activePageNumber = importsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, activePageNumberBefore + 1, "Active page number did not change after clicking next page");
        var firstImportDataAfter = importsPage.getDataOfLatestImport();
        softAssert.assertNotEquals(firstImportDataAfter, firstImportDataBefore, "Data did not change after clicking next page");
        softAssert.assertAll();

        // TESTING CLICKING ON PREVIOUS BUTTON
        softAssert = new SoftAssert();
        importsPage.paginator.goToPreviousPage();
        importsPage.waitForPageToFullyLoad();
        activePageNumber = importsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, activePageNumberBefore, "Active page number did not change after clicking previous page");
        firstImportDataAfter = importsPage.getDataOfLatestImport();
        softAssert.assertEquals(firstImportDataAfter, firstImportDataBefore, "Data did not change after clicking previous page");
        softAssert.assertAll();

        // TESTING CLICKING ON SPECIFIC PAGE NUMBER
        softAssert = new SoftAssert();
        var numberToSelect = 3;
        importsPage.paginator.goToSpecificPage(numberToSelect);
        importsPage.waitForPageToFullyLoad();
        activePageNumber = importsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, numberToSelect, "Active page number did not change after clicking page number " + numberToSelect);
        var firstImportDataAfterClickOnNumber = importsPage.getDataOfLatestImport();
        softAssert.assertNotEquals(firstImportDataAfterClickOnNumber, firstImportDataAfter, "Data did not change after clicking page number " + numberToSelect);
        softAssert.assertAll();

        // TESTING CLICKING ON LAST PAGE
        importsPage.paginator.goToLastPage();
        importsPage.waitForPageToFullyLoad();
        activePageNumber = importsPage.paginator.getActivePageNumber();
        softAssert.assertNotEquals(activePageNumber, numberToSelect, "Active page number did not change after clicking last page");

        // SEARCHING FOR IMPORT IN OTHER PAGE
        importsPage.searchForImport(firstImportDataBefore.name);
        importsPage.waitForPageToFullyLoad();
        activePageNumber = importsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, 1, "Active page number did not change to 1 after searching for import in other page");
        firstImportDataAfter = importsPage.getDataOfLatestImport();
        var firstImportMatchSearchTerm = firstImportDataAfter.name.equals(firstImportDataBefore.name);
        softAssert.assertTrue(firstImportMatchSearchTerm, "Import displayed does not contain search term");

        // CHANGING ITEMS PER PAGE FROM LAST PAGE
        importsPage.paginator.goToLastPage();
        importsPage.paginator.selectNumberOfItemsPerPage(50);
        importsPage.waitForPageToFullyLoad();
        activePageNumber = importsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, 1, "Active page number did not change to 1 after changing items per page from last page");
        var importsDisplayed = importsPage.getImportsDisplayed();
        softAssert.assertFalse(importsDisplayed.isEmpty(), "No imports were displayed after changing items per page from last page");

        softAssert.assertAll();
    }

    @Test(priority = 4, description = "C243983. Clicking on import name of successful file downloads the file")
    public void CGEN_ImportsPage_ClickingOnSuccessfulImportDownloadsFile() throws InterruptedException {
        var importFile = "ImportProductVersioning_SuccessOneRow.xlsx";
        importsPage.paginator.goToSpecificPage(1);
        importsPage.waitForPageToFullyLoad();
        importsPage.downloadImportedFile(importFile, downloadFolder);
    }

    @Test(priority = 5, description = "C243984. It tests template and imports a valid file")
    public void CGEN_ImportsPage_CompanyImportsModalWorksAsExpected() throws Exception {
        var softAssert = new SoftAssert();

        var importModal = importsPage.openModalToImportCompanyProperties();

        // Testing template
        ImportAssertions.verifyCompanyPropertiesTemplateHasCorrectHeaders(importModal, softAssert, downloadFolder);

        // Testing invalid import
        importModal.uploadFile(INVALID_FILE_PATH);
        var invalidFileErrorDisplayed = importModal.isInvalidFileErrorDisplayed();
        softAssert.assertTrue(invalidFileErrorDisplayed, "Invalid file error was not displayed after uploading an invalid file");

        // Testing valid import
        var validFile = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/CompanyPropertiesImportForUITests.xlsx";
        importModal.uploadFile(validFile);

        var successMessageDisplayed = importsPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after importing company properties");

        var expectedProperty = CompanyPropertiesXLUtils.getCompanyPropertiesInImportFile(validFile).get(0);
        companyPropertiesToDelete.add(expectedProperty.id);

        PropertiesPage propertiesPage = importsPage.navigateToUrl(InsightsConstants.INSIGHTS_PROPERTIES_URL, PropertiesPage.class);

        ImportAssertions.verifyCompanyPropertiesWereImported(propertiesPage, expectedProperty.id, softAssert);

        softAssert.assertAll();
    }

    @Test(priority = 6, description = "C243985. It tests template and imports a valid file for product properties")
    public void CGEN_ImportsPage_ProductPropertiesImportModalWorksAsExpected() throws Exception {
        var softAssert = new SoftAssert();

        var importModal = importsPage.openModalToImportProductData();

        var saveAndExitModalEnabled = importModal.isSaveAndExitButtonEnabled();
        softAssert.assertFalse(saveAndExitModalEnabled, "Save and exit button was enabled before uploading a file");

        ImportAssertions.verifyProductTemplateHasCorrectHeaders(Enums.ImportType.PROPERTY, importModal, softAssert, downloadFolder, jwt);

        var fileName = "ProductPropertiesImportForUITests.xlsx";
        var validFilePath = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/" + fileName;
        importsPage = ImportAssertions.verifyProductPropertyImportModalWorks(INVALID_FILE_PATH, validFilePath, importModal, softAssert, downloadFolder, ImportsPage.class);
        verifyFileWasSuccessfullyImported(fileName, Enums.ImportType.PROPERTY, 1, softAssert);

        // Testing product was added to db
        var expectedProductIdentifier = ProductPropertiesXLUtils.getVariantDataToImport(validFilePath).get(0).productIdentifier;
        productsToDelete.add(expectedProductIdentifier);
        var productWithUniqueId = ProductVersioningApiService.getProductWithUniqueIdIfExist(expectedProductIdentifier, jwt);
        softAssert.assertNotNull(productWithUniqueId, "Product was not added to db");

        // Testing product is displayed in UI
        var productsPage = importsPage.navigateToUrl(InsightsConstants.INSIGHTS_PRODUCTS_URL, ProductsPage.class);
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

        importsPage = productsPage.navigateToUrl(InsightsConstants.INSIGHTS_IMPORTS_URL, ImportsPage.class);

        softAssert.assertAll();
    }

    @Test(priority = 7, description = "C243986. It tests template and imports a valid file for product keywords")
    public void CGEN_ImportsPage_ProductKeywordsImportModalWorksAsExpected() throws Exception {
        var softAssert = new SoftAssert();
        var randomNumber = SharedMethods.generateRandomNumber();

        // GETTING KEYWORD SET BEFORE IMPORT
        var fileNameWithExtension = "ProductKeywordsImportForUITests.xlsx";
        var validFilePath = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/" + fileNameWithExtension;
        var productToTest = ProductKeywordsXLUtils.getProductKeywordsInFile(validFilePath).get(0);

        var company = CompanyApiService.getCompany(jwt);
        String localeId = company.getLocaleId(productToTest.locale);
        String campaignId = company.getCampaignId(productToTest.campaign);
        String retailerId = company.getRetailerId(productToTest.retailer);

        var productMasterInstancePath = new ProductVariantInstancePath(productToTest.productIdentifier, localeId, Enums.ProductVariantType.LIVE, retailerId, campaignId);
        var keywordBucketBeforeImport = ProductVersioningApiService.getProductKeywordSetByUniqueId(productMasterInstancePath, null, jwt).keywords.optional;

        var keywordToAdd = "automated value " + randomNumber;
        ProductKeywordsXLUtils.replaceCellValue(validFilePath, 1, 5, keywordToAdd);

        // TESTING TEMPLATE
        var importModal = importsPage.openModalToImportProductData();
        ImportAssertions.verifyProductTemplateHasCorrectHeaders(Enums.ImportType.KEYWORD, importModal, softAssert, downloadFolder, jwt);

        // TESTING IMPORT WITHOUT OVERWRITING EXISTING DATA
        importModal.selectFlagForKeywordImport(false);
        importsPage = ImportAssertions.verifyProductPropertyImportModalWorks(INVALID_FILE_PATH, validFilePath, importModal, softAssert, downloadFolder, ImportsPage.class);
        verifyFileWasSuccessfullyImported(fileNameWithExtension, Enums.ImportType.KEYWORD, 1, softAssert);

        var keywordBucketAfterImport = ProductVersioningApiService.getProductKeywordSetByUniqueId(productMasterInstancePath, null, jwt).keywords.optional;
        var expectedKeywordsInBucket = new ArrayList<>(keywordBucketBeforeImport);
        expectedKeywordsInBucket.add(keywordToAdd);
        softAssert.assertTrue(keywordBucketAfterImport.contains(keywordToAdd), "Keyword was not imported to keyword bucket");
        softAssert.assertEquals(keywordBucketAfterImport, expectedKeywordsInBucket, "Keyword bucket is not as expected after import");

        // TESTING IMPORT OVERWRITING EXISTING DATA
        keywordToAdd = "automated value " + randomNumber + 1;
        ProductKeywordsXLUtils.replaceCellValue(validFilePath, 1, 5, keywordToAdd);

        importModal = importsPage.openModalToImportProductData();
        importModal.importKeywords(validFilePath, true, ImportsPage.class);
        verifyFileWasSuccessfullyImported(fileNameWithExtension, Enums.ImportType.KEYWORD, 1, softAssert);

        keywordBucketAfterImport = ProductVersioningApiService.getProductKeywordSetByUniqueId(productMasterInstancePath, null, jwt).keywords.optional;
        expectedKeywordsInBucket.clear();
        expectedKeywordsInBucket.add(keywordToAdd);
        softAssert.assertEquals(keywordBucketAfterImport, expectedKeywordsInBucket, "Keyword bucket was not overwritten after import");

        softAssert.assertAll();
    }

    @Test(priority = 8, description = "C245434. Clicking on Failed status downloads the error report")
    public void CGEN_ImportsPage_ClickingOnFailedImportDownloadsReportError() throws Exception {
        var invalidFilePath = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/ImportProductKeywords_Duplicates.xlsx";

        var importModal = importsPage.openModalToImportProductData();
        importsPage = importModal.importKeywords(invalidFilePath, true, ImportsPage.class);

        var latestImportData = importsPage.refreshPage(ImportsPage.class)
                .getDataOfLatestImport();

        Assert.assertEquals(latestImportData.status, "Failed", "Latest import status is not as expected");

        importsPage.clickOnFailedImport(latestImportData.name);
        var pathToDownloadedFile = SharedMethods.isFileDownloaded("xlsx", "ImportErrors", 5, downloadFolder);
        Assert.assertNotNull(pathToDownloadedFile, "File was not downloaded after clicking on failed import");

        var fileNameWithExtension = Paths.get(pathToDownloadedFile).getFileName().toString();

        var importId = importsPage.getIdOfFirstImport();
        var errorReport = SharedRequests.getErrorReportForImport(importId, jwt);
        var urlToErrorReport = errorReport.jsonPath().getString("data.url");
        var filePath = new URL(urlToErrorReport).getPath();
        var expectedFileNameWithExtension = Paths.get(filePath).getFileName().toString();

        Assert.assertEquals(fileNameWithExtension, expectedFileNameWithExtension, "File downloaded is not the same as the one returned from backend");
        var expectedError = "Duplicate Variant targeted in Import";
        var expectedRow1 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 2, expectedError, null, null);
        var expectedRow2 = getExpectedErrorRowData(Enums.ImportStage.STANDARDIZE, 3, expectedError, null, null);
        verifyErrorReportContainsCorrectData(urlToErrorReport, Arrays.asList(expectedRow1, expectedRow2));
    }

    @Test(priority = 9, description = ". It tests template and imports a valid file for product master data")
    public void CGEN_ImportsPage_ProductMasterDataImportModalWorksAsExpected() throws Exception {
        var softAssert = new SoftAssert();

        var importModal = importsPage.openModalToImportProductData();

        var saveAndExitModalEnabled = importModal.isSaveAndExitButtonEnabled();
        softAssert.assertFalse(saveAndExitModalEnabled, "Save and exit button was enabled before uploading a file");

        ImportAssertions.verifyMasterProductDataTemplateHasCorrectHeaders(importModal, softAssert, downloadFolder, jwt);

        var fileName = "MasterProductDataImportForUITests.xlsx";
        var validFilePath = System.getProperty("user.dir") + "/src/test/java/com/DC/testData/" + fileName;
        importsPage = ImportAssertions.verifyProductPropertyImportModalWorks(INVALID_FILE_PATH, validFilePath, importModal, softAssert, downloadFolder, ImportsPage.class);
        verifyFileWasSuccessfullyImported(fileName, Enums.ImportType.PROPERTY, 0, softAssert);

        softAssert.assertAll();
    }

    @Test(priority = 10, description = "Clicking on # of versions updated takes to product page with filter by importId")
    public void CGEN_ImportsPage_ClickingOnVersionsUpdatedTakesToProductPageWithFilterByImportId() {
        var softAssert = new SoftAssert();
        var importToTest = "ImportProductKeywords_Empty.xlsx";
        importsPage.searchForImport(importToTest);
        var expectedId = importsPage.getIdOfFirstImport();
        var expectedVersionsUpdated = importsPage.getDataOfLatestImport().versionsUpdated;
        var productsPage = importsPage.clickOnVersionsUpdated(importToTest);
        var pageFilteredByImportId = productsPage.isPageFilteredByImportId(expectedId);
        var versionsDisplayed = productsPage.getNumberOfVersionsDisplayedNextToSearchInput();
        softAssert.assertTrue(pageFilteredByImportId, "Products page was not filtered by import id:" + expectedId);
        softAssert.assertEquals(expectedVersionsUpdated, versionsDisplayed, "Number of filtered versions do not match with expected count");
        softAssert.assertAll();
    }

    private ImportsPage loginAndNavigateToImportsPage() throws InterruptedException {
        new DCLoginPage(driver).login(SUPPORT_USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_IMPORTS_URL);
        importsPage = new ImportsPage(driver).switchCompany(COMPANY, ImportsPage.class);
        importsPage.paginator.selectNumberOfItemsPerPage(10);
        return importsPage.waitForPageToFullyLoad();
    }

    private void performSearchTestAndGetDisplayedData(String searchTerm) throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();

        var numberNextToSearchInputBeforeSearch = importsPage.getNumberDisplayedNextToSearchInput();

        importsPage.searchForImport(searchTerm);

        var numberNextToSearchInputAfterSearch = importsPage.getNumberDisplayedNextToSearchInput();

        softAssert.assertNotEquals(
                numberNextToSearchInputAfterSearch, numberNextToSearchInputBeforeSearch,
                "Number next to search input did not change after search. Search term: " + searchTerm
        );

        var allRowsMatchCriteria = importsPage.doAllImportsMatchCriteria(searchTerm);
        softAssert.assertTrue(allRowsMatchCriteria, "One or more rows don't match the search criteria: " + searchTerm);

        importsPage.paginator.goToNextPage();
        importsPage.waitForPageToFullyLoad();

        allRowsMatchCriteria = importsPage.doAllImportsMatchCriteria(searchTerm);
        softAssert.assertTrue(allRowsMatchCriteria, "One or more rows don't match the search criteria after using pagination: " + searchTerm);

        importsPage.paginator.selectPageNumber(1);
        importsPage.waitForPageToFullyLoad();

        softAssert.assertAll();
    }

    private void performSearchTestForInvalidColumns(String searchTerm) {
        importsPage.searchForImport(searchTerm);
        var rowsDisplayed = importsPage.getImportsDisplayed();
        Assert.assertTrue(rowsDisplayed.isEmpty(), "Rows were displayed for invalid search term: " + searchTerm);
    }

    private void verifyFileWasSuccessfullyImported(String fileName, Enums.ImportType importType, int expectedVersionsUpdated, SoftAssert softAssert) throws Exception {
        var successMessageDisplayed = importsPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        softAssert.assertTrue(successMessageDisplayed, "Success message was not displayed after importing file");

        var latestImportData = importsPage.refreshPage(ImportsPage.class)
                .getDataOfLatestImport();

        var accountInfo = CPGAccountService.getAccountInfo(jwt);
        var expectedUser = accountInfo.firstName + " " + accountInfo.lastName;
        var expectedImportInfo = new ImportsTableData(fileName, importType, expectedVersionsUpdated, expectedUser, getCurrentDateTime("MM/dd/yyyy"), "Success");
        softAssert.assertEquals(latestImportData, expectedImportInfo, "Latest import data is not as expected");
    }
}
