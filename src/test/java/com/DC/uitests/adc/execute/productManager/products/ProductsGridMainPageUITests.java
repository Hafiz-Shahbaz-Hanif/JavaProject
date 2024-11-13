package com.DC.uitests.adc.execute.productManager.products;

import com.DC.constants.InsightsConstants;
import com.DC.objects.productVersioning.ProductInstanceUIData;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.productLists.ProductListsPage;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.ProductVersioningApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductListApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.FriendlyProductVariantList;
import com.DC.utilities.apiEngine.models.responses.productVersioning.InstancePathBase;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class ProductsGridMainPageUITests extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final List<String> PRODUCTS_TO_CLEANUP = new ArrayList<>();
    private Company company;
    private ProductsPage productsPage;
    private ProductsPage.AddProductModal addProductModal;
    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).loginDcApp(USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_PRODUCTS_URL);
        productsPage = new ProductsPage(driver);
        productsPage.paginator.selectNumberOfItemsPerPage(5);
        productsPage.closeContentSuggestionsBanner();

        jwt = SecurityAPI.getJwtForInsightsUser(driver);
        company = CompanyApiService.getCompany(jwt);
    }

    @AfterMethod
    public void cleanupTestMethod() throws Exception {
        productsPage.recoverPageIfBlankPageIsDisplayed(InsightsConstants.INSIGHTS_PRODUCTS_URL, productsPage);
        if (!driver.getTitle().contains("Products | Flywheel")) {
            driver.get(InsightsConstants.INSIGHTS_PRODUCTS_URL);
            productsPage = new ProductsPage(driver);
        }
    }

    @AfterClass(alwaysRun = true)
    public void killDriver() {
        try {
            for (String productIdentifier : PRODUCTS_TO_CLEANUP) {
                ProductVersioningApiService.deleteProductMasterByUniqueId(productIdentifier, jwt);
            }
        } catch (Exception ignored) {
        } finally {
            quitBrowser();
        }
    }

    @Test(priority = 1, description = "C260149")
    public void CGEN_ProductsPage_DefaultColumnsAreCorrect() {
        List<String> defaultColumns = List.of("Product Identifier", "Versions", "Business Units", "Product Name", "RPC");
        List<String> columnsInProductsGrid = productsPage.tableCommonFeatures.getColumnsDisplayed();
        Assert.assertEquals(columnsInProductsGrid, defaultColumns, "Default columns are not correct");
    }

    @Test(priority = 1, description = "C244453. Paginator works. Clicking on next page, previous page, specific page number, last page, changing items per page from last page, searching for product in other page")
    public void CGEN_ProductsPage_PaginatorWorksAsExpected() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();

        productsPage.paginator.selectNumberOfItemsPerPage(5);
        productsPage.removeAllAdvancedFilters();

        int activePageNumberBefore = productsPage.paginator.getActivePageNumber();
        var productsDisplayedBefore = productsPage.getProductsDisplayed();

        // TESTING CLICKING ON NEXT BUTTON
        productsPage.paginator.goToNextPage();
        productsPage = new ProductsPage(driver);

        int activePageNumber = productsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, activePageNumberBefore + 1, "Active page number did not change after clicking next page");
        var productsDisplayedAfter = productsPage.getProductsDisplayed();
        softAssert.assertNotEquals(productsDisplayedAfter, productsDisplayedBefore, "Data did not change after clicking next page");
        softAssert.assertAll();

        // TESTING CLICKING ON PREVIOUS BUTTON
        softAssert = new SoftAssert();
        productsPage.paginator.goToPreviousPage();
        productsPage = new ProductsPage(driver);

        activePageNumber = productsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, activePageNumberBefore, "Active page number did not change after clicking previous page");
        productsDisplayedAfter = productsPage.getProductsDisplayed();
        softAssert.assertEqualsNoOrder(productsDisplayedAfter.toArray(), productsDisplayedBefore.toArray(), "Data did not change after clicking previous page");
        softAssert.assertAll();

        var productToSearch = productsDisplayedAfter.get(0);

        // TESTING CLICKING ON SPECIFIC PAGE NUMBER
        softAssert = new SoftAssert();
        var numberToSelect = 3;
        productsPage.paginator.goToSpecificPage(numberToSelect);
        productsPage = new ProductsPage(driver);
        activePageNumber = productsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, numberToSelect, "Active page number did not change after clicking page number " + numberToSelect);
        var productsDisplayedAfterClickOnNumber = productsPage.getProductsDisplayed();
        var productsAreDifferent = productsDisplayedAfterClickOnNumber.stream().noneMatch(productsDisplayedAfter::contains);
        softAssert.assertTrue(productsAreDifferent, "One or more products displayed are the same after clicking page number " + numberToSelect);
        softAssert.assertAll();

        // TESTING CLICKING ON LAST PAGE
        productsPage.paginator.goToLastPage();
        productsPage = new ProductsPage(driver);
        activePageNumber = productsPage.paginator.getActivePageNumber();
        softAssert.assertNotEquals(activePageNumber, numberToSelect, "Active page number did not change after clicking last page");
        softAssert.assertAll();

        // SEARCHING FOR PRODUCT IN OTHER PAGE
        productsPage.searchByProductIdentifier(productToSearch.productIdentifier);
        productsPage = new ProductsPage(driver);
        activePageNumber = productsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, 1, "Active page number did not change to 1 after searching for product in other page");
        var productIdentifiersDisplayedAfter = productsPage.getProductsDisplayed().stream().map(p -> p.productIdentifier).collect(Collectors.toList());
        var productsMatchSearchTerm = productIdentifiersDisplayedAfter.stream().allMatch(p -> p.contains(productToSearch.productIdentifier));
        softAssert.assertTrue(productsMatchSearchTerm, "Products displayed do not contain search term");
        softAssert.assertAll();

        // CHANGING ITEMS PER PAGE FROM LAST PAGE
        productsPage.removeAllAdvancedFilters();
        productsPage.paginator.goToLastPage();
        productsPage.paginator.selectNumberOfItemsPerPage(50);
        productsPage = new ProductsPage(driver);
        activePageNumber = productsPage.paginator.getActivePageNumber();
        softAssert.assertEquals(activePageNumber, 1, "Active page number did not change to 1 after changing items per page from last page");
        productsDisplayedAfter = productsPage.getProductsDisplayed();
        softAssert.assertFalse(productsDisplayedAfter.isEmpty(), "No products were displayed after changing items per page from last page");
        softAssert.assertAll();
    }

    @Test(priority = 2, description = "C243856. User cannot add a product if a required field is missing.")
    public void CGEN_ProductsPage_AddProductModal_CannotAddProductWithoutAddingRequiredFields() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();

        productsPage.closeReactModalIfDisplayed();
        addProductModal = productsPage.openModalToAddProducts();

        // TESTING WITH EMPTY FORM - Add button
        addProductModal.clickAddButton();
        var isErrorMessageDisplayed = addProductModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "Product Identifier is required. Locale is required.");
        softAssert.assertTrue(isErrorMessageDisplayed, "Error message is not displayed when trying to add a product with empty form");

        // TESTING WITH EMPTY FORM - Add & Close button
        addProductModal.clickAddAndCloseButton();
        isErrorMessageDisplayed = addProductModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "Product Identifier is required. Locale is required.");
        softAssert.assertTrue(isErrorMessageDisplayed, "Error message is not displayed when trying to add a product with empty form. Add & Close button");

        addProductModal.clickCancelButton();

        // TESTING ONLY WITH PRODUCT IDENTIFIER - Add button
        var productIdentifier = "Automated-Product-" + SharedMethods.generateRandomNumber();
        addProductModal = productsPage.openModalToAddProducts();
        addProductModal.insertProductIdentifier(productIdentifier);
        addProductModal.clickAddButton();
        isErrorMessageDisplayed = addProductModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "Locale is required.");
        softAssert.assertTrue(isErrorMessageDisplayed, "Error message was not displayed after trying to add a product with only product identifier");

        // TESTING ONLY WITH LOCALE - Add button
        addProductModal.insertProductIdentifier("");
        addProductModal.insertLocale("en-US");
        addProductModal.clickAddButton();
        isErrorMessageDisplayed = addProductModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "Product Identifier is required.");
        softAssert.assertTrue(isErrorMessageDisplayed, "Error message was not displayed after trying to add a product with only product identifier");

        // TESTING ONLY WITH LOCALE - Add & Close button
        addProductModal.clickAddAndCloseButton();
        isErrorMessageDisplayed = addProductModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "Product Identifier is required.");
        softAssert.assertTrue(isErrorMessageDisplayed, "Error message was not displayed after clicking Add & Close button");

        // VERIFY PRODUCT WAS NOT ADDED
        addProductModal.clickCancelButton();
        productsPage.searchByProductIdentifier(productIdentifier);
        var productsDisplayed = productsPage.getProductsDisplayed();
        softAssert.assertTrue(productsDisplayed.isEmpty(), "Product was added when user send form to add product without filling required fields");

        softAssert.assertAll();
    }

    @Test(priority = 3, description = "C243854. User cannot add a product if it already exists.")
    public void CGEN_ProductsPage_AddProductModal_CannotAddDuplicateProduct() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();

        productsPage.closeReactModalIfDisplayed();

        var numberOfProductsBefore = productsPage.getNumberOfProductsDisplayedNextToSearchInput();

        addProductModal = productsPage.openModalToAddProducts();

        var duplicateProduct = new UserFriendlyInstancePath("QA-STATIC-PRODUCT-001", "es-MX", "Amazon.com", null);
        addProductModal.fillForm(duplicateProduct);
        addProductModal.clickAddButton();
        var isErrorMessageDisplayed = addProductModal.isNoteDisplayedWithMessage(Enums.NoteType.INFO, "Type: ProductMasterError. Subtype: ProductMasterVariantError. Message: Instance already exists on variant.");
        softAssert.assertTrue(isErrorMessageDisplayed, "Error message was not displayed after adding a product that already exists");

        productsPage = addProductModal.clickCancelButton();

        // VERIFY PRODUCT WAS NOT ADDED
        var numberOfProductsAfter = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        softAssert.assertEquals(numberOfProductsAfter, numberOfProductsBefore, "Product was added when user sent form to add product that already exists");

        softAssert.assertAll();
    }

    @Test(priority = 4, description = "C243855. Cancel buttons close modal to add products.")
    public void CGEN_ProductsPage_AddProductModal_CancelButtonsCloseModal() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        productsPage.closeReactModalIfDisplayed();

        var numberOfProductsBefore = productsPage.getNumberOfProductsDisplayedNextToSearchInput();

        addProductModal = productsPage.openModalToAddProducts();

        var invalidProduct = new UserFriendlyInstancePath("QA-STATIC-PRODUCT-INVALID", "en-US", "Amazon.com", null);
        addProductModal.fillForm(invalidProduct);
        addProductModal.clickCancelButton();

        var numberOfProductsAfter = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        softAssert.assertEquals(numberOfProductsAfter, numberOfProductsBefore, "Product was added when user filled form but clicked cancel button");

        addProductModal = productsPage.openModalToAddProducts();

        addProductModal.fillForm(invalidProduct);
        addProductModal.clickCloseIcon();
        numberOfProductsAfter = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        softAssert.assertEquals(numberOfProductsAfter, numberOfProductsBefore, "Product was added when user filled form but clicked the close icon");

        softAssert.assertAll();
    }

    @Test(priority = 5, description = "C244492. User can add a base product first, then add versions of that product.")
    public void CGEN_ProductsPage_AddProductModal_CanAddBaseProductAndVersionsSeparately() throws Exception {
        var attempts = 5;
        SoftAssert softAssert = new SoftAssert();

        var productToAdd = new UserFriendlyInstancePath("QA-PRODUCT-VALID-001", "en-US", null, null);
        var numberOfProductsBefore = setupTestToAddProducts(productToAdd.productIdentifier);

        // ADDING BASE PRODUCT
        addProductModal = productsPage.openModalToAddProducts();
        addProductModal.fillForm(productToAdd);
        productsPage = addProductModal.clickAddAndCloseButtonAndWaitForInvisibility();

        verifyVersionIsInBackend(productToAdd);

        var errorMessage = "Number of versions displayed didn't change after adding a base product";
        var numberOfVersionsAfterAddingBaseProduct = productsPage.refreshUntilNumberNextToSearchInputEquals(numberOfProductsBefore + 1, attempts, errorMessage, true);
        var numberOfProductsAfterAddingBaseProduct = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        Assert.assertEquals(numberOfProductsAfterAddingBaseProduct, numberOfProductsBefore + 1, "Number of products displayed did not increase after adding a base product");

        var productsDisplayed = productsPage.getProductsDisplayed();
        verifyProductVersionIsInTable(productToAdd, productsDisplayed, softAssert);

        // ADDING VERSION OF PRODUCT FOR SAME LOCALE
        productToAdd.retailerName = "Amazon.com";
        addProductModal = productsPage.openModalToAddProducts();
        addProductModal.fillForm(productToAdd);
        productsPage = addProductModal.clickAddAndCloseButtonAndWaitForInvisibility();

        verifyVersionIsInBackend(productToAdd);

        errorMessage = "Number of versions displayed did not increase after adding a version of product." +
                " Version: " + productToAdd.getProductVersion() + ". Level: " + productToAdd.getProductLevel();
        var numberOfVersionsAfterAddingVersionOfProduct = productsPage.refreshUntilNumberNextToSearchInputEquals(numberOfVersionsAfterAddingBaseProduct + 1, attempts, errorMessage, true);
        var numberOfProductsAfterAddingVersionOfProduct = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        Assert.assertEquals(numberOfProductsAfterAddingVersionOfProduct, numberOfProductsAfterAddingBaseProduct, "Number of products displayed changed after adding a version of product");

        productsDisplayed = productsPage.getProductsDisplayed();
        verifyProductVersionIsInTable(productToAdd, productsDisplayed, softAssert);

        // ADDING ANOTHER RETAILER FOR SAME LOCALE
        productToAdd.retailerName = "Walmart.com";
        addProductModal = productsPage.openModalToAddProducts();
        addProductModal.fillForm(productToAdd);
        productsPage = addProductModal.clickAddAndCloseButtonAndWaitForInvisibility();

        verifyVersionIsInBackend(productToAdd);

        errorMessage = "Number of versions displayed did not increase after adding a version of product." +
                " Version: " + productToAdd.getProductVersion() + ". Level: " + productToAdd.getProductLevel();
        numberOfVersionsAfterAddingVersionOfProduct = productsPage.refreshUntilNumberNextToSearchInputEquals(numberOfVersionsAfterAddingVersionOfProduct + 1, attempts, errorMessage, true);
        Assert.assertEquals(numberOfProductsAfterAddingVersionOfProduct, numberOfProductsAfterAddingBaseProduct, "Number of products displayed changed after adding a version of product");

        productsDisplayed = productsPage.getProductsDisplayed();
        verifyProductVersionIsInTable(productToAdd, productsDisplayed, softAssert);

        // ADDING VERSION OF PRODUCT FOR DIFFERENT LOCALE
        productToAdd.localeName = "es-MX";
        productToAdd.retailerName = "Amazon.com";
        productToAdd.campaignName = "Christmas";

        addProductModal = productsPage.openModalToAddProducts();
        addProductModal.fillForm(productToAdd);
        addProductModal = addProductModal.clickAddButton();
        var isSuccessMessageDisplayed = addProductModal.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, "Product added successfully");
        softAssert.assertTrue(isSuccessMessageDisplayed, "Success message was not displayed after adding a version of product." +
                " Version: " + productToAdd.getProductVersion() + ". Level: " + productToAdd.getProductLevel()
        );

        var dataInForm = addProductModal.getDataInForm();
        softAssert.assertEquals(dataInForm, productToAdd, "Previous form was not kept applied after clicking add button");

        addProductModal.clickCancelButton();

        verifyVersionIsInBackend(productToAdd);

        errorMessage = "Number of products displayed does not match the expected number after adding a version of product." +
                " Version: " + productToAdd.getProductVersion() + ". Level:" + productToAdd.getProductLevel();
        productsPage.refreshUntilNumberNextToSearchInputEquals(numberOfVersionsAfterAddingVersionOfProduct + 2, attempts, errorMessage, true);
        Assert.assertEquals(numberOfProductsAfterAddingVersionOfProduct, numberOfProductsAfterAddingBaseProduct, "Number of products displayed changed after adding a version of product");

        productsDisplayed = productsPage.getProductsDisplayed();
        verifyBaseVersionIsInTable(productToAdd, productsDisplayed, softAssert);
        verifyProductVersionIsInTable(productToAdd, productsDisplayed, softAssert);

        softAssert.assertAll();
    }

    @Test(priority = 6, description = "C243853. If no product with that Product Identifier exists - it creates base version along with version from form.")
    public void CGEN_ProductsPage_AddProductModal_BaseVersionIsCreatedAlongWithOtherVersion() throws Exception {
        var attempts = 5;
        SoftAssert softAssert = new SoftAssert();
        var productToAdd = new UserFriendlyInstancePath("QA-PRODUCT-VALID-002", "en-US", "Amazon.com", "Christmas");
        var numberOfProductsBefore = setupTestToAddProducts(productToAdd.productIdentifier);
        var numberOfVersionsBefore = productsPage.getNumberOfVersionsDisplayedNextToSearchInput();

        // ADDING PRODUCT
        addProductModal = productsPage.openModalToAddProducts();
        addProductModal.fillForm(productToAdd);
        productsPage = addProductModal.clickAddAndCloseButtonAndWaitForInvisibility();

        verifyVersionIsInBackend(productToAdd);

        var errorMessage = "Number of products displayed didn't change after adding product";
        productsPage.refreshUntilNumberNextToSearchInputEquals(numberOfProductsBefore + 1, attempts, errorMessage, false);
        var numberOfVersionsAfterAddingProduct = productsPage.getNumberOfVersionsDisplayedNextToSearchInput();
        Assert.assertEquals(numberOfVersionsAfterAddingProduct, numberOfVersionsBefore + 2, "Number of versions displayed did not increase after adding a version without a base");

        var productsDisplayed = productsPage.getProductsDisplayed();
        verifyBaseVersionIsInTable(productToAdd, productsDisplayed, softAssert);
        verifyProductVersionIsInTable(productToAdd, productsDisplayed, softAssert);

        softAssert.assertAll();
    }

    @Test(priority = 7, description = "C244459. Warning modal displays if user selects a BASE product")
    public void CGEN_ProductsPage_RemoveProducts_ModalContentIsCorrectDependingOnProductsSelected() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        var nonBaseProductToTest = new UserFriendlyInstancePath("QA-STATIC-PRODUCT-001", "es-MX", "Amazon.com", null);
        var baseProductToTest = new UserFriendlyInstancePath("QA-STATIC-PRODUCT-001", "en-US", null, null);

        productsPage.closeReactModalIfDisplayed();
        productsPage.waitForDOMStabilization();
        productsPage.paginator.selectNumberOfItemsPerPage(50);
        productsPage.searchByProductIdentifier(nonBaseProductToTest.productIdentifier);

        var deleteProductModal = productsPage.selectProduct(nonBaseProductToTest).openModalToDeleteProducts();
        var warningMessageDisplayed = deleteProductModal.isWarningMessageDisplayed();
        softAssert.assertFalse(warningMessageDisplayed, "Warning message was displayed in modal to delete products when selecting a non base product");

        var getModalContent = deleteProductModal.getModalMessage();
        var expectedModalContent = "This will permanently delete 1 selected products. Are you sure you want to delete?";
        softAssert.assertEquals(getModalContent, expectedModalContent, "Message inside modal to delete product is not correct");

        deleteProductModal.closeReactModalIfDisplayed();
        productsPage.deselectAllProducts();
        deleteProductModal = productsPage.selectProduct(baseProductToTest).openModalToDeleteProducts();
        warningMessageDisplayed = deleteProductModal.isWarningMessageDisplayed();
        softAssert.assertTrue(warningMessageDisplayed, "Warning message was not displayed in modal to delete products when selecting a base product");

        deleteProductModal.closeReactModalIfDisplayed();
        productsPage.deselectAllProducts();
        deleteProductModal = productsPage.selectProduct(nonBaseProductToTest).selectProduct(baseProductToTest).openModalToDeleteProducts();
        warningMessageDisplayed = deleteProductModal.isWarningMessageDisplayed();
        softAssert.assertTrue(warningMessageDisplayed, "Warning message was not displayed in modal to delete products when selecting a base product and a non base product");

        softAssert.assertAll();
    }

    @Test(priority = 8, description = "C244513. Products are removed accordingly." +
            "If user selects a BASE product, it removes all versions of that product." +
            "If user selects a non-BASE product, it removes only that version." +
            "If user selects a mix of BASE and non-BASE products, it removes all versions of the BASE products and only the selected non-BASE products." +
            "Products are removed from a list if they are part of it."
    )
    public void CGEN_ProductsPage_RemoveProducts_ProductsAreRemovedAccordingly() throws Exception {
        // SETTING UP PRODUCTS TO REMOVE
        var productIdentifier = "QA-PRODUCT-VALID-003";
        PRODUCTS_TO_CLEANUP.add(productIdentifier);

        var productFromFirstLocaleToSelect1 = new UserFriendlyInstancePath(productIdentifier, "en-US", "Amazon.com", null);
        var productFromFirstLocaleToSelect2 = new UserFriendlyInstancePath(productIdentifier, "en-US", "Amazon.com", "Christmas");
        var productFromFirstLocaleToSelect3 = new UserFriendlyInstancePath(productIdentifier, "en-US", "Amazon.com", "Halloween");
        var productFromFirstLocaleToKeep = new UserFriendlyInstancePath(productIdentifier, "en-US", "Walmart.com", null);

        var productFromSecondLocaleToSelect1 = new UserFriendlyInstancePath(productIdentifier, "es-MX", "Amazon.com", null);
        var productFromSecondLocaleToSelect2 = new UserFriendlyInstancePath(productIdentifier, "es-MX", "Amazon.com", "Christmas");
        var productFromSecondLocaleToSelect3 = new UserFriendlyInstancePath(productIdentifier, "es-MX", "Walmart.com", null);
        var productFromSecondLocaleToKeep = new UserFriendlyInstancePath(productIdentifier, "es-MX", "Amazon.com", "Halloween");

        var differentProductToRemove = new UserFriendlyInstancePath(productIdentifier + 1, "es-MX", "Amazon.com", null);
        PRODUCTS_TO_CLEANUP.add(differentProductToRemove.productIdentifier);

        ProductVersioningApiService.createProductVersionIfNotExistent(productFromFirstLocaleToSelect1, jwt);
        ProductVersioningApiService.createProductVersionIfNotExistent(productFromFirstLocaleToSelect2, jwt);
        ProductVersioningApiService.createProductVersionIfNotExistent(productFromFirstLocaleToSelect3, jwt);
        ProductVersioningApiService.createProductVersionIfNotExistent(productFromFirstLocaleToKeep, jwt);
        ProductVersioningApiService.createProductVersionIfNotExistent(productFromSecondLocaleToSelect1, jwt);
        ProductVersioningApiService.createProductVersionIfNotExistent(productFromSecondLocaleToSelect2, jwt);
        ProductVersioningApiService.createProductVersionIfNotExistent(productFromSecondLocaleToSelect3, jwt);
        ProductVersioningApiService.createProductVersionIfNotExistent(productFromSecondLocaleToKeep, jwt);
        ProductVersioningApiService.createProductVersionIfNotExistent(differentProductToRemove, jwt);

        // ADD TO LIST
        var listName = "Static List For Product Deletion Automated Test";
        var list = ProductListApiService.getAllCompanyProductLists(jwt).stream().filter(l -> l.name.equals(listName)).findFirst().orElseThrow();
        var listId = list._id;
        ProductListApiService.removeProductsFromList(list.products.stream().map(p -> p.instanceId).collect(Collectors.toList()), listId, jwt);

        var productsToAddToList = new ArrayList<>(List.of(
                productFromFirstLocaleToSelect1,
                productFromFirstLocaleToSelect2,
                productFromFirstLocaleToSelect3,
                productFromFirstLocaleToKeep,
                productFromSecondLocaleToSelect1,
                productFromSecondLocaleToSelect2,
                productFromSecondLocaleToSelect3,
                productFromSecondLocaleToKeep,
                differentProductToRemove
        ));

        List<InstancePathBase> instancePathsToAddToList = new ArrayList<>();
        for (UserFriendlyInstancePath product : productsToAddToList) {
            InstancePathBase instancePathBase = product.convertToInstancePathBase(company, jwt);
            instancePathsToAddToList.add(instancePathBase);
        }
        ProductListApiService.addProductInstancesToList(instancePathsToAddToList, listId, jwt);

        // STARTING TEST
        productsPage.refreshPage(ProductsPage.class);
        productsPage.waitForDOMStabilization();
        productsPage.paginator.selectNumberOfItemsPerPage(50);
        productsPage.closeContentSuggestionsBanner();
        var numberOfProductsBefore = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        productsPage.searchByProductIdentifier(productIdentifier);
        var numberOfVersionsBefore = productsPage.getNumberOfVersionsDisplayedNextToSearchInput();

        // DELETING ONLY ONE PRODUCT VERSION
        var errorMessage = "Number of versions displayed did not decrease by 1 after removing a product version";
        var currentVersions = performTestToRemoveVersionsAndReturnCurrentVersionsCount(
                Collections.singletonList(productFromFirstLocaleToSelect1),
                numberOfVersionsBefore - 1,
                listId,
                errorMessage
        );
        productsToAddToList.remove(productFromFirstLocaleToSelect1);

        // DELETING MULTIPLE VERSIONS OF SAME PRODUCT MASTER (NO BASE)
        var versionsToDelete = List.of(productFromFirstLocaleToSelect2, productFromSecondLocaleToSelect2);
        errorMessage = "Number of versions displayed did not decrease by " + versionsToDelete.size() + " after removing " + versionsToDelete.size() + " product versions";
        currentVersions = performTestToRemoveVersionsAndReturnCurrentVersionsCount(versionsToDelete, currentVersions - versionsToDelete.size(), listId, errorMessage);
        productsToAddToList.removeAll(versionsToDelete);

        // DELETING MULTIPLE VERSIONS OF DIFFERENT PRODUCT MASTER
        errorMessage = "Number of versions displayed did not decrease by " + versionsToDelete.size() + " after removing " + versionsToDelete.size() + " product versions of different product masters";
        versionsToDelete = List.of(productFromFirstLocaleToSelect3, productFromSecondLocaleToSelect3, differentProductToRemove);
        performTestToRemoveVersionsAndReturnCurrentVersionsCount(versionsToDelete, currentVersions - versionsToDelete.size(), listId, errorMessage);
        productsToAddToList.removeAll(versionsToDelete);

        // DELETING BASE VERSION (BASE OF FIRST LOCALE, RETAILER VERSION OF SECOND LOCALE & BASE OF DIFFERENT PRODUCT)
        var firstBaseVersion = productFromFirstLocaleToSelect1.getBaseFriendlyInstancePath();
        var differentProductBaseVersion = differentProductToRemove.getBaseFriendlyInstancePath();
        var secondLocaleBaseVersion = productFromSecondLocaleToKeep.getBaseFriendlyInstancePath();
        var productsThatShouldRemain = List.of(secondLocaleBaseVersion, productFromSecondLocaleToKeep);

        Map<UserFriendlyInstancePath, Boolean> versionsToDeleteMap = Map.of(
                firstBaseVersion, true,
                differentProductBaseVersion, true,
                productFromSecondLocaleToSelect1, false
        );
        performTestToRemoveMixOfBaseAndNonBaseProducts(versionsToDeleteMap, productsThatShouldRemain, listId, productsToAddToList);
    }

    @Test(priority = 9, description = "C245349. Filters are applied when clicking from Products page into a PDP version")
    public void CGEN_ProductsPage_FiltersAreAppliedInPDPWhenClickingOnProduct() throws InterruptedException {
        var productToTest = new UserFriendlyInstancePath("QA-STATIC-PRODUCT-001", "es-MX", null, null);

        productsPage.paginator.selectNumberOfItemsPerPage(10);
        productsPage.removeAllAdvancedFilters();
        productsPage.searchByProductIdentifier(productToTest.productIdentifier);

        productsPage = performTestToVerifyVersionFilterIsAppliedInPDP(productToTest);

        productToTest.retailerName = "Amazon.com";
        productsPage = performTestToVerifyVersionFilterIsAppliedInPDP(productToTest);

        productToTest.campaignName = "Halloween";
        productsPage = performTestToVerifyVersionFilterIsAppliedInPDP(productToTest);

        productToTest.retailerName = null;
        productsPage = performTestToVerifyVersionFilterIsAppliedInPDP(productToTest);
    }

    @Test(priority = 10, description = "C256208. Clicking on product identifier header takes to PDP without filters applied")
    public void CGEN_ProductsPage_ClickingOnProductHeaderTakesToPDPWithoutFiltersApplied() throws InterruptedException {
        var productToTest = "QA-STATIC-PRODUCT-001";

        productsPage.paginator.selectNumberOfItemsPerPage(25);
        productsPage.removeAllAdvancedFilters();
        var productsBefore = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        productsPage.searchByProductIdentifier(productToTest);
        productsPage.waitForNumberOfProductsToChange(productsBefore);

        var expectedVersionCount = productsPage.getNumberOfVersionsDisplayedNextToSearchInput();
        var propertiesTab = productsPage.clickOnProductIdentifierHeader(productToTest);
        var versionsDisplayedCount = propertiesTab.getPanelsDisplayedCount();
        Assert.assertEquals(versionsDisplayedCount, expectedVersionCount, "Versions displayed in PDP don't match with versions of product: " + productToTest);

        var filtersAppliedCount = propertiesTab.productsLeftSideFilter.getFiltersAppliedCount();
        Assert.assertEquals(filtersAppliedCount, 0, "One or more filters were applied to PDP after clicking on product identifier headers");
    }

    @Test(priority = 11, description = "C258274. User cannot delete classifier products")
    public void CGEN_ProductsPage_CannotDeleteProductsStartingWithPM() throws InterruptedException {
        var productsToSelect = List.of(
                new UserFriendlyInstancePath("PM-1-1", "en-US", null, null),
                new UserFriendlyInstancePath("PM-1-2", "en-US", "Amazon.com", null)
        );

        productsPage.closeReactModalIfDisplayed();
        productsPage.paginator.selectNumberOfItemsPerPage(25);
        productsPage.removeAllAdvancedFilters();
        var productsBefore = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        productsPage.searchByProductIdentifier("PM-");
        productsPage.waitForNumberOfProductsToChange(productsBefore);

        var bottomActionBar = productsPage.selectProducts(productsToSelect);
        bottomActionBar.clickDeleteProductsButton();

        var bannerIsDisplayed = productsPage.isNoteDisplayedWithMessage(Enums.NoteType.INFO, productsToSelect.size() + " selections are system products and cannot be deleted.");
        Assert.assertTrue(bannerIsDisplayed, "Banner was not displayed after clicking delete products button");

        var modalToDeleteProductsIsDisplayed = productsPage.isModalToDeleteProductsDisplayed();
        Assert.assertFalse(modalToDeleteProductsIsDisplayed, "Modal to delete products was displayed for classifier products");
    }

    @Test(priority = 12, description = "Select products filter carry that selection (beyond Page 1) into bulk actions")
    public void CGEN_ProductsPage_SelectProductsFilterWorksAsExpected() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        productsPage.paginator.selectNumberOfItemsPerPage(5);
        productsPage.removeAllAdvancedFilters();

        var productDisplayedCount = productsPage.getProductsDisplayed().size();
        var bottomActionBar = productsPage.selectAllProductsOnPage();
        var selectedProductsCount = bottomActionBar.getNumberOfSelectedProducts();
        softAssert.assertEquals(selectedProductsCount, productDisplayedCount, "Selected products count don't match with products on page");

        productsPage.paginator.goToNextPage();
        var bottomActionBarVisible = productsPage.isBottomActionBarVisible();
        softAssert.assertFalse(bottomActionBarVisible, "Bottom action bar still visible after clicking on next page");

        productDisplayedCount = productsPage.getProductsDisplayed().size();
        bottomActionBar = productsPage.selectAllProductsOnPage();
        selectedProductsCount = bottomActionBar.getNumberOfSelectedProducts();
        softAssert.assertEquals(selectedProductsCount, productDisplayedCount, "Selected products count don't match with products on page 2");

        bottomActionBar = productsPage.selectAllFilterMatches();
        var allProductsCount = productsPage.getNumberOfVersionsDisplayedNextToSearchInput();
        selectedProductsCount = bottomActionBar.getNumberOfSelectedProducts();
        softAssert.assertEquals(selectedProductsCount, allProductsCount, "Selected products count don't match with all products matching filter");
        var checkboxEnabled = productsPage.isSelectedCheckboxEnabled();
        softAssert.assertFalse(checkboxEnabled, "Checkbox enabled after selecting all matches filter");

        productsPage.paginator.goToNextPage();
        bottomActionBarVisible = productsPage.isBottomActionBarVisible();
        softAssert.assertTrue(bottomActionBarVisible, "Bottom action bar was hidden after clicking on next page with matching filter selected");
        selectedProductsCount = bottomActionBar.getNumberOfSelectedProducts();
        softAssert.assertEquals(selectedProductsCount, allProductsCount, "Selected products count don't match with all products matching filter after clicking on next page");

        productsPage.deselectAllProducts();
        bottomActionBarVisible = productsPage.isBottomActionBarVisible();
        softAssert.assertFalse(bottomActionBarVisible, "Bottom action bar still visible after deselecting all products");
    }

    private ProductsPage performTestToVerifyVersionFilterIsAppliedInPDP(UserFriendlyInstancePath productToTest) throws InterruptedException {
        var propertiesTab = productsPage.goToPropertiesTab(productToTest);
        var filtersApplied = propertiesTab.productsLeftSideFilter.getAllAppliedFilters();

        var expectedFilterType = productToTest.getProductVersionFilter();
        var filterValuesApplied = filtersApplied.get(expectedFilterType);
        String expectedFilterValue;

        switch (productToTest.getProductVersionFilter()) {
            case BASE:
                expectedFilterValue = productToTest.localeName;
                break;
            case RETAILER:
                expectedFilterValue = productToTest.localeName + "|" + productToTest.retailerName;
                break;
            case CAMPAIGN:
                expectedFilterValue = productToTest.localeName + "|" + productToTest.campaignName;
                break;
            case RETAILER_CAMPAIGN:
                expectedFilterValue = productToTest.localeName + "|" + productToTest.retailerName + "|" + productToTest.campaignName;
                break;
            default:
                throw new IllegalStateException("Unexpected version filter: " + productToTest.getProductVersionFilter());
        }

        Assert.assertEquals(filterValuesApplied, List.of(expectedFilterValue), "Filter " + expectedFilterType + " was not applied when clicking on product" + productToTest);

        filtersApplied.remove(expectedFilterType);
        filtersApplied.values().forEach(f -> Assert.assertTrue(f.isEmpty(), "Wrong filter was applied when clicking on product" + productToTest));
        propertiesTab.navigateBack();
        return new ProductsPage(driver);
    }

    private int setupTestToAddProducts(String productIdentifier) throws Exception {
        PRODUCTS_TO_CLEANUP.add(productIdentifier);

        productsPage.closeReactModalIfDisplayed();
        productsPage.waitForDOMStabilization();
        productsPage.paginator.selectNumberOfItemsPerPage(50);
        productsPage.closeContentSuggestionsBanner();
        productsPage.removeAllAdvancedFilters();
        Thread.sleep(1000);
        var numberOfProductsBeforeSearch = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        productsPage.searchByProductIdentifier(productIdentifier);
        var currentNumberOfProducts = productsPage.waitForNumberOfProductsToChange(numberOfProductsBeforeSearch);

        if (currentNumberOfProducts != 0) {
            ProductVersioningApiService.deleteProductMasterByUniqueId(productIdentifier, jwt);
            productsPage.refreshPage(ProductsPage.class);
            currentNumberOfProducts = productsPage.getNumberOfProductsDisplayedNextToSearchInput();
        }
        return currentNumberOfProducts;
    }

    private void verifyVersionIsInBackend(UserFriendlyInstancePath productToAdd) throws Exception {
        var instancePathBase = productToAdd.convertToInstancePathBase(company, jwt);
        var response = ProductVersioningApiRequests.getLiveProductInstanceByUniqueId(productToAdd.productIdentifier, instancePathBase.localeId, instancePathBase.campaignId, instancePathBase.retailerId, jwt);
        if (response.statusCode() != 200 || response.getBody().asString().isEmpty()) {
            Assert.fail("Product with path: " + instancePathBase + " was not found in backend");
        }
    }

    private void verifyProductVersionIsNotInBackend(UserFriendlyInstancePath productToAdd) throws Exception {
        var instancePathBase = productToAdd.convertToInstancePathBase(company, jwt);
        var response = ProductVersioningApiRequests.getLiveProductInstanceByUniqueId(productToAdd.productIdentifier, instancePathBase.localeId, instancePathBase.campaignId, instancePathBase.retailerId, jwt);

        if (response.statusCode() == 200) {
            if (!response.getBody().asString().isEmpty()) {
                Assert.fail("Product with path: " + instancePathBase + " was found in backend");
            }
        } else if (response.statusCode() != 422) {
            throw new Exception("Unexpected status code: " + response.statusCode() + " when trying to get instance from backend");
        }
    }

    private void verifyProductVersionIsInTable(UserFriendlyInstancePath productToAdd, List<ProductInstanceUIData> productsDisplayed, SoftAssert softAssert) {
        var productVersionWasAdded = productsDisplayed.stream().anyMatch(
                p -> p.productIdentifier.equals(productToAdd.productIdentifier) &&
                        p.localeName.equals(productToAdd.localeName) &&
                        p.version.equals(productToAdd.getProductVersion())
        );
        softAssert.assertTrue(productVersionWasAdded, "Product version: " + productToAdd.getProductVersion() + " for locale " + productToAdd.localeName + " was not found in table");
    }

    private void verifyBaseVersionIsInTable(UserFriendlyInstancePath productToAdd, List<ProductInstanceUIData> productsDisplayed, SoftAssert softAssert) {
        var baseProductWasAdded = productsDisplayed.stream().anyMatch(
                p -> p.productIdentifier.equals(productToAdd.productIdentifier) &&
                        p.localeName.equals(productToAdd.localeName) &&
                        p.version.equals("Base")
        );
        softAssert.assertTrue(baseProductWasAdded, "Base product for product identifier " + productToAdd.productIdentifier + " was not found in table");
    }

    private void verifyVersionWasRemovedInUI(UserFriendlyInstancePath productToAdd, List<ProductInstanceUIData> productsDisplayed, SoftAssert softAssert) {
        var productVersionWasRemoved = productsDisplayed.stream().noneMatch(
                p -> p.productIdentifier.equals(productToAdd.productIdentifier) &&
                        p.localeName.equals(productToAdd.localeName) &&
                        p.version.equals(productToAdd.getProductVersion())
        );

        softAssert.assertTrue(productVersionWasRemoved, "Product with path: " + productToAdd + " was not deleted");
    }

    private int performTestToRemoveVersionsAndReturnCurrentVersionsCount(List<UserFriendlyInstancePath> versionsToDelete, int expectedNumberOfVersions, String listId, String errorMessage) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        var attempts = 5;
        var bottomActionBar = productsPage.selectProducts(versionsToDelete);
        productsPage = bottomActionBar.openModalToDeleteProducts().confirmDeletion();

        // TODO ENABLE ONCE CSCAT-236 IS FIXED
        /*var successMessage = "Product version was not removed after clicking delete button";
        var successfulMessageDisplayed = bottomActionBar.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, successMessage);
        softAssert.assertTrue(successfulMessageDisplayed, "Success message was not displayed after clicking delete button");*/

        var currentList = ProductListApiService.getProductList(listId, jwt);

        for (var versionToDelete : versionsToDelete) {
            verifyProductVersionIsNotInBackend(versionToDelete);
            verifyProductWasRemovedFromListInBackend(currentList, versionToDelete, softAssert);
        }

        var currentVersions = productsPage.refreshUntilNumberNextToSearchInputEquals(expectedNumberOfVersions, attempts, errorMessage, true);

        var productsDisplayed = productsPage.getProductsDisplayed();
        for (var versionToDelete : versionsToDelete) {
            verifyVersionWasRemovedInUI(versionToDelete, productsDisplayed, softAssert);
        }

        verifyNumberOfProductsInListChangedInUI(currentList.name, currentList.products.size(), softAssert);

        softAssert.assertAll();
        return currentVersions;
    }

    private void verifyProductWasRemovedFromListInBackend(FriendlyProductVariantList currentList, UserFriendlyInstancePath productToTest, SoftAssert softAssert) throws Exception {
        var productInstancePathBase = productToTest.convertToInstancePathBase(company, jwt);
        var productsInList = currentList.products.stream().filter(p -> p.getInstancePathBase().equals(productInstancePathBase)).findFirst().orElse(null);
        softAssert.assertNull(productsInList, "Product with path: " + productInstancePathBase + " was not removed from list: " + currentList.name);
    }

    private void verifyNumberOfProductsInListChangedInUI(String listName, int expectedNumberOfProducts, SoftAssert softAssert) throws Exception {
        productsPage.navigateToUrl(InsightsConstants.INSIGHTS_PRODUCT_LIST_URL);
        var listPage = new ProductListsPage(driver);
        listPage.searchForList(listName);
        var numberOfProductsInList = listPage.getNumberOfProductsInList(listName);
        softAssert.assertEquals(numberOfProductsInList, expectedNumberOfProducts, "Number of products of list : " + listName + " did not decrease after deleting a product");
        productsPage = listPage.navigateToUrl(InsightsConstants.INSIGHTS_PRODUCTS_URL, ProductsPage.class);
    }

    private void performTestToRemoveMixOfBaseAndNonBaseProducts(Map<UserFriendlyInstancePath, Boolean> versionsToDeleteMap, List<UserFriendlyInstancePath> productsThatShouldRemain, String listId, ArrayList<UserFriendlyInstancePath> productsToCheckInList) throws Exception {
        SoftAssert softAssert = new SoftAssert();

        var versionsToDelete = new ArrayList<>(versionsToDeleteMap.keySet());
        var bottomActionBar = productsPage.selectProducts(versionsToDelete);
        productsPage = bottomActionBar.openModalToDeleteProducts().confirmDeletion();

        for (var versionToDelete : versionsToDelete) {
            verifyProductVersionIsNotInBackend(versionToDelete);
        }

        var errorMessage = "Number of versions displayed did not decrease correctly after removing mix of base versions and non base versions. Products were removed from backend but not from UI.";
        productsPage.refreshUntilNumberNextToSearchInputEquals(productsThatShouldRemain.size(), 5, errorMessage, true);

        var productsDisplayed = productsPage.getProductsDisplayed();
        for (var product : productsThatShouldRemain) {
            verifyProductVersionIsInTable(product, productsDisplayed, softAssert);
        }

        var currentList = ProductListApiService.getProductList(listId, jwt);

        List<UserFriendlyInstancePath> baseVersions = new ArrayList<>();
        List<UserFriendlyInstancePath> nonBaseVersions = new ArrayList<>();
        versionsToDeleteMap.forEach((key, value) -> {
            if (value.equals(true)) {
                baseVersions.add(key);
            } else {
                nonBaseVersions.add(key);
            }
        });

        for (var product : productsToCheckInList) {
            var productWasPartOfRemovedBaseVersions = baseVersions.stream().anyMatch(baseVersion ->
                    product.productIdentifier.equals(baseVersion.productIdentifier) &&
                            product.localeName.equals(baseVersion.localeName));

            if (productWasPartOfRemovedBaseVersions || nonBaseVersions.contains(product)) {
                verifyProductWasRemovedFromListInBackend(currentList, product, softAssert);
            } else {
                var productInstancePathBase = product.convertToInstancePathBase(company, jwt);
                var productInList = currentList.products.stream()
                        .filter(p -> p.getInstancePathBase().equals(productInstancePathBase))
                        .findFirst().orElse(null);

                softAssert.assertNotNull(productInList, "Product with path: " + productInstancePathBase + " was removed from list: " + currentList.name);
            }
        }

        verifyNumberOfProductsInListChangedInUI(currentList.name, currentList.products.size(), softAssert);

        softAssert.assertAll();
    }
}
