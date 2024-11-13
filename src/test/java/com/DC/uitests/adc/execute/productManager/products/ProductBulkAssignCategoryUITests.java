package com.DC.uitests.adc.execute.productManager.products;

import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage.AttributesTab;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiServices.insights.CPGData.Segments.SegmentsService;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

import static com.DC.constants.InsightsConstants.INSIGHTS_PRODUCTS_URL;
import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static java.util.Arrays.asList;

public class ProductBulkAssignCategoryUITests extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String PRODUCT_IDENTIFIER_PREFIX = "ATTRIBUTES";
    private ProductsPage productsPage;
    private ProductsPage.BottomActionBar bottomActionBar;
    private ProductsPage.AssignCategoryModal assignCategoryModal;
    private AttributesTab attributesTab;

    private final UserFriendlyInstancePath PRODUCT_WITH_TAGGED_VALUES = new UserFriendlyInstancePath(PRODUCT_IDENTIFIER_PREFIX + "-001", "en-US", "Amazon.com", null);
    private final UserFriendlyInstancePath EMPTY_PRODUCT = new UserFriendlyInstancePath(PRODUCT_IDENTIFIER_PREFIX + "-001", "es-MX", "Walmart.com", "Halloween");
    private final UserFriendlyInstancePath PRODUCT_WITH_ONLY_CATEGORY = new UserFriendlyInstancePath(PRODUCT_IDENTIFIER_PREFIX + "-002", "es-MX", null, null);

    private final List<String> CATEGORY_TREE_TO_SELECT = asList("Toys & Games", "Games", "Battle Tops");
    private final List<UserFriendlyInstancePath> PRODUCTS_TO_ADD = asList(PRODUCT_WITH_TAGGED_VALUES, PRODUCT_WITH_ONLY_CATEGORY, EMPTY_PRODUCT);

    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).loginDcApp(USERNAME, PASSWORD);

        jwt = SecurityAPI.getJwtForInsightsUser(driver);
        deleteProducts();

        driver.get(INSIGHTS_PRODUCTS_URL);
        productsPage = new ProductsPage(driver);
        productsPage.paginator.selectNumberOfItemsPerPage(50);
        productsPage = productsPage.addProducts(PRODUCTS_TO_ADD);
    }

    @AfterMethod
    public void cleanupTest() throws Exception {
        productsPage = productsPage.recoverPageIfBlankPageIsDisplayed(INSIGHTS_PRODUCTS_URL, productsPage);
        if (!driver.getTitle().contains("Products | Flywheel")) {
            driver.get(INSIGHTS_PRODUCTS_URL);
            productsPage = new ProductsPage(driver);
        }
    }

    @AfterClass(alwaysRun = true)
    public void cleanupProducts() {
        try {
            deleteProducts();
        } catch (Exception ignored) {
        }
        quitBrowser();
    }

    @Test(priority = 1, description = "Clicking Edit > Category opens a modal to assign category to products selected")
    public void PPV_BulkAssignCategory_ClickingEditCategoryOpensModal() {
        bottomActionBar = productsPage.selectProducts(PRODUCTS_TO_ADD);
        try {
            assignCategoryModal = bottomActionBar.clickEditCategoryButton();
        } catch (Exception e) {
            Assert.fail("Failed to open modal to assign category to products selected");
        }
    }

    @Test(priority = 2, description = "")
    public void PPV_BulkAssignCategory_CanSearchCategory() throws InterruptedException {
        var categoryToSearch = "Alcoholic Beverages";
        assignCategoryModal = goToEditBulkCategoryIfNeeded(PRODUCTS_TO_ADD, PRODUCT_IDENTIFIER_PREFIX);
        assignCategoryModal.categoryTree.searchCategory(categoryToSearch);
        var valueInSearchField = assignCategoryModal.categoryTree.getCategorySearchValue();
        Assert.assertEquals(valueInSearchField, categoryToSearch, "Value in search field doesn't match expected value");
    }

    @Test(priority = 3, description = "")
    public void PPV_BulkAssignCategory_AutofillDropdownWorks() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();
        List<String> categoryTreeToSearch = asList("Alcoholic Beverages", "Beer & Cider", "Beer");
        var searchTerm = categoryTreeToSearch.get(categoryTreeToSearch.size() - 1);

        assignCategoryModal = goToEditBulkCategoryIfNeeded(PRODUCTS_TO_ADD, PRODUCT_IDENTIFIER_PREFIX);
        assignCategoryModal.categoryTree.searchCategory(searchTerm);
        var optionsInCategorySearchDropdown = assignCategoryModal.categoryTree.getOptionsInCategorySearchDropdown();
        var allOptionsContainSearchTerm = optionsInCategorySearchDropdown.stream().allMatch(option -> option.contains(searchTerm));
        softAssert.assertTrue(
                allOptionsContainSearchTerm,
                "Not all options in category search dropdown contain the search term: " + searchTerm + ". Options: " + optionsInCategorySearchDropdown
        );

        assignCategoryModal.categoryTree.selectOptionFromAutofillOptions(categoryTreeToSearch);
        var categoryTreeFromTabValue = assignCategoryModal.categoryTree.getCategoryTreeFromTabValue();
        softAssert.assertEquals(categoryTreeFromTabValue, categoryTreeToSearch, "Category value in tab doesn't match category tree value from autofill dropdown");

        softAssert.assertAll();
    }

    @Test(priority = 4, description = "All selected products don't have any category assigned")
    public void PPV_BulkAssignCategory_CanSelectAndAssignCategoryToProducts_NoCategoryAssigned() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        var productsToTest = asList(PRODUCT_WITH_TAGGED_VALUES, PRODUCT_WITH_ONLY_CATEGORY);

        assignCategoryModal = goToEditBulkCategoryIfNeeded(productsToTest, PRODUCT_IDENTIFIER_PREFIX);

        assignCategoryModal.categoryTree.selectCategoryTree(CATEGORY_TREE_TO_SELECT);
        assignCategoryModal.categoryTree.clickChooseCategoryButton();
        var warningMessageDisplayed = assignCategoryModal.isWarningMessageDisplayed();
        softAssert.assertFalse(warningMessageDisplayed, "Warning message displayed when assigning category to products with no category assigned");

        productsPage = assignCategoryModal.clickSaveButton();
        productsPage = verifyCategoryTreeWasAssignedToProducts(productsToTest, CATEGORY_TREE_TO_SELECT, softAssert);

        softAssert.assertAll();
    }

    @Test(priority = 5, description = "Assigning same category doesn't remove attributes from product")
    public void PPV_BulkAssignCategory_AssigningSameCategoryDoesNotRemoveAttributes() throws Exception {
        var productsToTest = asList(PRODUCT_WITH_TAGGED_VALUES, PRODUCT_WITH_ONLY_CATEGORY);
        assignCategoryModal = goToEditBulkCategoryIfNeeded(productsToTest, PRODUCT_IDENTIFIER_PREFIX);

        assignCategoryModal.categoryTree.selectCategoryTree(CATEGORY_TREE_TO_SELECT);
        assignCategoryModal.categoryTree.clickChooseCategoryButton();
        productsPage = assignCategoryModal.clickSaveButton();

        var productToTest = productsToTest.get(0);
        var valueToTag = "Board Game";

        attributesTab = productsPage.goToPropertiesTab(productToTest)
                .clickAttributesTab()
                .selectAndApplyTag(valueToTag);
        var attributesBefore = attributesTab.getAttributesDisplayed();

        attributesTab.navigateBack();
        productsPage = new ProductsPage(driver);

        assignCategoryModal = goToEditBulkCategoryIfNeeded(productsToTest, PRODUCT_IDENTIFIER_PREFIX);

        assignCategoryModal.categoryTree.selectCategoryTree(CATEGORY_TREE_TO_SELECT);
        assignCategoryModal.categoryTree.clickChooseCategoryButton();
        productsPage = assignCategoryModal.clickSaveButton();

        attributesTab = productsPage.goToPropertiesTab(productToTest).clickAttributesTab();
        var attributesAfter = attributesTab.getAttributesDisplayed();

        Assert.assertEquals(attributesBefore, attributesAfter, "Attributes were removed from product after assigning same category");
    }

    @Test(priority = 6, description = "Mix of products with and without category assigned")
    public void PPV_BulkAssignCategory_CanSelectAndAssignCategoryToProducts_MixOfProducts() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        assignCategoryModal = goToEditBulkCategoryIfNeeded(PRODUCTS_TO_ADD, PRODUCT_IDENTIFIER_PREFIX);

        var categoryTreeToSelect = asList("Toys & Games", "Games", "Puzzles");
        assignCategoryModal.categoryTree.selectCategoryTree(categoryTreeToSelect);
        var categoryTreeFromTabValue = assignCategoryModal.categoryTree.getCategoryTreeFromTabValue();
        softAssert.assertEquals(categoryTreeToSelect, categoryTreeFromTabValue, "Category value doesn't match expected value");

        assignCategoryModal.categoryTree.clickChooseCategoryButton();
        var warningMessageDisplayed = assignCategoryModal.isWarningMessageDisplayed();
        softAssert.assertTrue(warningMessageDisplayed, "Warning message not displayed when assigning category to products with category already assigned");

        productsPage = assignCategoryModal.clickSaveButton();
        var successMessageDisplayed = productsPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, "Categories added successfully!");
        softAssert.assertTrue(successMessageDisplayed, "Success message not displayed after assigning category to products");

        productsPage = verifyCategoryTreeWasAssignedToProducts(PRODUCTS_TO_ADD, categoryTreeToSelect, softAssert);

        softAssert.assertAll();
    }

    private ProductsPage verifyCategoryTreeWasAssignedToProducts(List<UserFriendlyInstancePath> productsToSelect, List<String> expectedCategoryTree, SoftAssert softAssert) throws Exception {
        for (var product : productsToSelect) {
            attributesTab = productsPage.goToPropertiesTab(product).clickAttributesTab();
            var categoryTreeFromTabValue = attributesTab.categoryTree.getCategoryTabValue();

            var localeId = CompanyApiService.getCompany(jwt).getLocaleId(product.localeName);

            var categoryPathOfProduct = String.join("|", expectedCategoryTree);
            var attributesData = ProductVersioningApiService.getAttributeSetDataByUniqueId(product.productIdentifier, localeId, Enums.ProductVariantType.LIVE, jwt);
            var categoryPathFromCPGData = SegmentsService.getCategorySegmentValuesWithCategory(attributesData.categoryId.toString(), jwt).category.path;

            softAssert.assertEquals(
                    categoryPathFromCPGData,
                    categoryPathOfProduct,
                    "Category didn't change to expected category. Product: " + product.productIdentifier + " " + product.getProductVersion()
            );

            var expectedFullCategoryTree = String.join(" > ", expectedCategoryTree);
            softAssert.assertEquals(
                    categoryTreeFromTabValue,
                    expectedFullCategoryTree,
                    "Category tree in table doesn't match expected value. Product: " + product.productIdentifier + " " + product.getProductVersion()
            );
            attributesTab.navigateBack();
            productsPage = new ProductsPage(driver);
            productsPage.closeContentSuggestionsBanner();
        }
        return productsPage;
    }

    private void selectProductsAndClickEdiCategoryButton(List<UserFriendlyInstancePath> productsToSelect, String prefix) throws InterruptedException {
        productsPage.closeContentSuggestionsBanner().deselectAllProducts();
        productsPage.searchByProductIdentifier(prefix);
        bottomActionBar = productsPage.selectProducts(productsToSelect);
        assignCategoryModal = bottomActionBar.clickEditCategoryButton();
        assignCategoryModal.waitForDOMStabilization();
    }

    private ProductsPage.AssignCategoryModal goToEditBulkCategoryIfNeeded(List<UserFriendlyInstancePath> productsToSelect, String prefix) throws InterruptedException {
        if (!productsPage.isAssignCategoryModalDisplayed()) {
            selectProductsAndClickEdiCategoryButton(productsToSelect, prefix);
        } else {
            var numberOfSelectedProducts = bottomActionBar.getNumberOfSelectedProducts();
            if (numberOfSelectedProducts != productsToSelect.size()) {
                productsPage = assignCategoryModal.clickCancelButton();
                selectProductsAndClickEdiCategoryButton(productsToSelect, prefix);
            }
        }
        return assignCategoryModal;
    }

    private void deleteProducts() throws Exception {
        for (var product : PRODUCTS_TO_ADD) {
            ProductMaster productMaster = ProductVersioningApiService.getProductWithUniqueIdIfExist(product.productIdentifier, jwt);
            if (productMaster != null) {
                ProductVersioningApiService.deleteProductMaster(productMaster._id, jwt);
            }
        }
    }
}
