package com.DC.uitests.adc.execute.productManager.productLists;

import com.DC.db.productVersioning.ProductVariantListCollection;
import com.DC.objects.productVersioning.ProductListUI;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.productLists.AddToListModal;
import com.DC.pageobjects.adc.execute.productManager.productLists.ProductListsPage;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.pageobjects.filters.AdvancedFilters.FilterType;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SeleniumDriver;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiServices.insights.CPGAccount.CPGAccountService;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductListApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.insights.CPGAccount.CpgAccount;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.FriendlyProductVariantList;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.hub.HubCommonMethods;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.DC.constants.InsightsConstants.*;
import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static com.DC.utilities.DateUtility.formattingDate;
import static org.awaitility.Awaitility.await;

public class ProductListUITests extends BaseClass {
    private final String SUPPORT_USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String CPG_USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String STATIC_LIST_NAME = "Static List For UI Product List Tests";
    private final String STATIC_LIST_NEW_NAME = "Static List For UI Product List Tests " + SharedMethods.generateRandomNumber();
    private final AbstractMap.SimpleEntry<String, Enums.ProductListPermission> SINGLE_PUBLIC_LIST_TO_ADD = new AbstractMap.SimpleEntry<>("Automated Public List To Add", Enums.ProductListPermission.PUBLIC);
    private final AbstractMap.SimpleEntry<String, Enums.ProductListPermission> SINGLE_PUBLIC_LIST_TO_ADD_2 = new AbstractMap.SimpleEntry<>("Automated Public List To Add 2", Enums.ProductListPermission.PUBLIC);
    private final AbstractMap.SimpleEntry<String, Enums.ProductListPermission> SINGLE_PRIVATE_LIST_TO_ADD = new AbstractMap.SimpleEntry<>("Automated Private List To Add", Enums.ProductListPermission.PRIVATE);
    private final LinkedHashMap<String, Enums.ProductListPermission> MULTIPLE_LISTS_TO_ADD = new LinkedHashMap<>() {{
        put("List To Add 1", Enums.ProductListPermission.PUBLIC);
        put("List To Add 2", Enums.ProductListPermission.PRIVATE);
        put("List To Add 4", Enums.ProductListPermission.PUBLIC);
        put("List To Add 5", Enums.ProductListPermission.PRIVATE);
    }};

    private final String PREFIX_PRODUCTS_TO_ADD = "QA-BULK-EDIT-";
    private final UserFriendlyInstancePath PRODUCT_TO_ADD_TO_LIST = new UserFriendlyInstancePath(PREFIX_PRODUCTS_TO_ADD + "001", "es-MX", "Amazon.com", null);
    private final UserFriendlyInstancePath PRODUCT_TO_ADD_TO_LIST_2 = new UserFriendlyInstancePath(PREFIX_PRODUCTS_TO_ADD + "001", "en-US", null, null);
    private final UserFriendlyInstancePath PRODUCT_TO_ADD_TO_LIST_3 = new UserFriendlyInstancePath(PREFIX_PRODUCTS_TO_ADD + "002", "en-US", "Amazon.com", null);
    private final UserFriendlyInstancePath PRODUCT_TO_ADD_TO_LIST_4 = new UserFriendlyInstancePath(PREFIX_PRODUCTS_TO_ADD + "002", "en-US", "Amazon.com", "Halloween");

    private final List<UserFriendlyInstancePath> FIRST_BATCH_PRODUCTS_TO_ADD = Arrays.asList(PRODUCT_TO_ADD_TO_LIST, PRODUCT_TO_ADD_TO_LIST_3);
    private final List<UserFriendlyInstancePath> SECOND_BATCH_PRODUCTS_TO_ADD = Arrays.asList(PRODUCT_TO_ADD_TO_LIST_2, PRODUCT_TO_ADD_TO_LIST_4);

    private ProductListsPage productListsPage;
    private ProductsPage productsPage;
    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(SUPPORT_USERNAME, PASSWORD);
        driver.get(INSIGHTS_PRODUCT_LIST_URL);
        productListsPage = new ProductListsPage(driver);
        jwt = SecurityAPI.getJwtForInsightsUser(driver);
    }

    @BeforeMethod()
    public void setupTestMethod() throws Exception {
        productListsPage = productListsPage.recoverPageIfBlankPageIsDisplayed(INSIGHTS_PRODUCT_LIST_URL, productListsPage);
        productListsPage = navigateToProductListPageIfNeeded();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownTestMethod() {
        if (!Objects.equals(driver.getCurrentUrl(), INSIGHTS_PRODUCT_LIST_URL)) {
            productListsPage = productListsPage.navigateToUrl(INSIGHTS_PRODUCT_LIST_URL, ProductListsPage.class);
        } else {
            productListsPage = productListsPage.refreshPage(ProductListsPage.class);
        }
        productListsPage.waitForDOMStabilization();
    }

    @AfterClass(alwaysRun = true)
    public void cleanupData() {
        try {
            List<String> listsToDelete = new ArrayList<>(MULTIPLE_LISTS_TO_ADD.keySet());
            listsToDelete.add(SINGLE_PUBLIC_LIST_TO_ADD.getKey());
            listsToDelete.add(SINGLE_PRIVATE_LIST_TO_ADD.getKey());
            listsToDelete.add(SINGLE_PUBLIC_LIST_TO_ADD_2.getKey());

            ProductListApiService.deleteProductLists(listsToDelete, jwt);

            String companyId = new HubCommonMethods().decodeInsightsJwtToGetCompanyId(jwt);
            new ProductVariantListCollection().editProductListName(STATIC_LIST_NEW_NAME, STATIC_LIST_NAME, companyId);
        } catch (Exception ignored) {
        } finally {
            quitBrowser();
        }
    }

    @Test(priority = 1, description = "C244413. Can search lists by list name or owner. This input is case insensitive.")
    public void CSCAT_ProductLists_CanSearchListByNameOrOwner() {
        SoftAssert softAssert = new SoftAssert();

        verifySearchInputWorks(softAssert, STATIC_LIST_NAME);
        verifySearchInputWorks(softAssert, STATIC_LIST_NAME.toLowerCase());
        verifySearchInputWorks(softAssert, STATIC_LIST_NAME.toUpperCase());

        String ownerToSearch = "QA Support";
        verifySearchInputWorks(softAssert, ownerToSearch);
        verifySearchInputWorks(softAssert, ownerToSearch.toLowerCase());
        verifySearchInputWorks(softAssert, ownerToSearch.toUpperCase());
        productListsPage.emptyOutSearchInput();
        softAssert.assertAll();
    }

    @Test(priority = 2, description = "C244416. All company lists are displayed with proper data.")
    public void CSCAT_ProductLists_AllCompanyListsAreDisplayed() throws Exception {
        List<FriendlyProductVariantList> listsInCompany = ProductListApiService.getAllCompanyProductLists(jwt);
        List<ProductListUI> listsInUI = productListsPage.emptyOutSearchInput().getAllListsData();
        LOGGER.info("listsInCompany: " + listsInCompany);
        Assert.assertEquals(listsInUI.size(), listsInCompany.size(), "Number of lists displayed is not equal to number of lists in company");
        for (FriendlyProductVariantList listInCompany : listsInCompany) {
            ProductListUI listInUI = listsInUI.stream()
                    .filter(list -> list.name.equals(listInCompany.name))
                    .findFirst()
                    .orElse(null);

            Assert.assertNotNull(listInUI, "List: " + listInCompany.name + " is not displayed in UI");
            String expectedLastActivity = formattingDate(listInCompany.dateUpdated.toLocalDate().toString());
            ProductListUI expectedListInUI = new ProductListUI(listInCompany.name, listInCompany.products.size(), listInCompany.ownerName, listInCompany.permission, expectedLastActivity);
            Assert.assertEquals(listInUI, expectedListInUI, "List: " + listInCompany.name + " is not displayed correctly in UI");
        }
    }

    @Test(priority = 3, description = "C243931. Clicking on the number of products in a list takes you to the product page filtered by the list.")
    public void CSCAT_ProductLists_ClickingNumberOfProductTakesToProductPageFilteredByTheList() throws InterruptedException {
        addProductsToListIfNeededAndGoBackToProductListsPage(FIRST_BATCH_PRODUCTS_TO_ADD, STATIC_LIST_NAME);
        productListsPage.emptyOutSearchInput().clickOnListProductNumber(STATIC_LIST_NAME);
        productsPage = new ProductsPage(driver);
        boolean isProductListFiltered = productsPage.isPageFilteredByList(STATIC_LIST_NAME);
        Assert.assertTrue(isProductListFiltered, "Products page is not filtered by list: " + STATIC_LIST_NAME);
        driver.get(INSIGHTS_PRODUCT_LIST_URL);
        productListsPage = new ProductListsPage(driver);
        isProductListFiltered = productListsPage.openProductsOfListInNewTab(STATIC_LIST_NAME);
        Assert.assertTrue(isProductListFiltered, "Products page is not filtered by list when navigating to products by opening a new tab");
    }

    @Test(priority = 4, description = "C244417. User can add a single public product list.")
    public void CSCAT_ProductLists_CanAddAPublicList() throws Exception {
        productListsPage = verifyProductListCanBeCreated(SINGLE_PUBLIC_LIST_TO_ADD);
        verifyPermissionWorksAsExpected(SINGLE_PUBLIC_LIST_TO_ADD);
    }

    @Test(priority = 5, description = "C244418. User can add multiple product lists.")
    public void CSCAT_ProductLists_CanAddMultipleLists() throws Exception {
        ProductListsPage.NewListModal newListModal = productListsPage.clickAddListButton();

        try {
            productListsPage = newListModal.createNewLists(MULTIPLE_LISTS_TO_ADD);
        } catch (Exception e) {
            String message = "Exception occurred while creating multiple lists: " + e.getMessage();
            LOGGER.info(message);
            Assert.fail(message);
        }
        productListsPage.refreshPage(ProductListsPage.class);

        CpgAccount accountInfo = CPGAccountService.getAccountInfo(jwt);
        String expectedOwnerName = accountInfo.firstName + " " + accountInfo.lastName;
        String expectedLastActivity = formattingDate(LocalDate.now().toString());

        for (Map.Entry<String, Enums.ProductListPermission> listToAdd : MULTIPLE_LISTS_TO_ADD.entrySet()) {
            ProductListUI expectedListInfo = new ProductListUI(listToAdd.getKey(), 0, expectedOwnerName, listToAdd.getValue(), expectedLastActivity);
            verifyListIsDisplayedWithCorrectInfo(expectedListInfo);
        }
    }

    @Test(priority = 6, description = "C244419. Cancel adding a list closes modal without adding list")
    public void CSCAT_ProductLists_CanCancelAddingAList() throws InterruptedException {
        ProductListsPage.NewListModal newListModal = productListsPage.clickAddListButton();
        newListModal.insertListName("Test List");
        productListsPage = newListModal.cancelCreatingANewList();
        boolean isNoteDisplayed = productListsPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        Assert.assertFalse(isNoteDisplayed, "Success note is displayed after canceling creating a new list");
    }

    @Test(priority = 7, description = "C244420. User can add a single private list")
    public void CSCAT_ProductLists_CanAddAPrivateList() throws Exception {
        productListsPage = verifyProductListCanBeCreated(SINGLE_PRIVATE_LIST_TO_ADD);
        verifyPermissionWorksAsExpected(SINGLE_PRIVATE_LIST_TO_ADD);
    }

    @Test(priority = 8, description = "C244421. User cannot add a list that already exists in company.")
    public void CSCAT_ProductLists_CannotAddADuplicateList() throws InterruptedException {
        ProductListUI currentListInfo = productListsPage.getListData(STATIC_LIST_NAME);

        ProductListsPage.NewListModal newListModal = productListsPage.clickAddListButton();
        
        // CLICKING THE ADD BUTTON
        newListModal.insertListSettings(currentListInfo.name, currentListInfo.permission);
        newListModal.clickAddButton();
        boolean errorMessageIsDisplayed = newListModal.isErrorDisplayedForDuplicateList(STATIC_LIST_NAME);
        Assert.assertTrue(errorMessageIsDisplayed, "Error message is not displayed when adding a duplicate list by clicking the add button");

        // TESTING WITH DIFFERENT PERMISSION AND CLICKING THE ADD & CLOSE BUTTON
        Enums.ProductListPermission permission = currentListInfo.permission == Enums.ProductListPermission.PUBLIC ? Enums.ProductListPermission.PRIVATE : Enums.ProductListPermission.PUBLIC;
        newListModal.insertListSettings(currentListInfo.name, permission);
        newListModal.clickAddAndCloseButton();
        errorMessageIsDisplayed = newListModal.isErrorDisplayedForDuplicateList(STATIC_LIST_NAME);
        Assert.assertTrue(errorMessageIsDisplayed, "Error message is not displayed when adding a duplicate list by clicking the add & close button");

        productListsPage = newListModal.cancelCreatingANewList();
    }

    @Test(priority = 9, description = "C244422. User can edit list name and permission if it is public or user is owner of private list.")
    public void CSCAT_ProductLists_CanEditList() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();

        ProductListUI listDataBeforeEdit = addProductsToListIfNeededAndGoBackToProductListsPage(FIRST_BATCH_PRODUCTS_TO_ADD, STATIC_LIST_NAME);

        ProductListsPage.BottomActionBar bottomActionBar = productListsPage.updateListNameAndSwitchPermissions(STATIC_LIST_NAME, STATIC_LIST_NEW_NAME);
        boolean nameCellHighlighted = productListsPage.isCellHighlighted(STATIC_LIST_NEW_NAME, productListsPage.LIST_NAME_COLUMN_ID);
        softAssert.assertTrue(nameCellHighlighted, "Name cell is not highlighted after editing the list name");
        boolean permissionCellHighlighted = productListsPage.isCellHighlighted(STATIC_LIST_NEW_NAME, productListsPage.PERMISSION_COLUMN_ID);
        softAssert.assertTrue(permissionCellHighlighted, "Permission cell is not highlighted after editing the list permission");

        productListsPage = bottomActionBar.clickSubmitButton();
        boolean successMessageDisplayed = productListsPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        softAssert.assertTrue(successMessageDisplayed, "Success message is not displayed after submitting changes");

        productListsPage = productListsPage.refreshPage(ProductListsPage.class);
        boolean listWithOldNameIsDisplayed = productListsPage.doesListExist(STATIC_LIST_NAME);
        softAssert.assertFalse(listWithOldNameIsDisplayed, "List with old name is still displayed after editing the list name");
        boolean listWithNewNameIsDisplayed = productListsPage.doesListExist(STATIC_LIST_NEW_NAME);
        softAssert.assertTrue(listWithNewNameIsDisplayed, "List with new name is not displayed after editing the list name");

        ProductListUI updatedListData = productListsPage.getListData(STATIC_LIST_NEW_NAME);
        softAssert.assertEquals(updatedListData.numberOfProducts, listDataBeforeEdit.numberOfProducts, "Number of products is not the same after editing the list");
        softAssert.assertEquals(updatedListData.ownerName, listDataBeforeEdit.ownerName, "Owner name changed after editing the list name");

        if (!Objects.equals(listDataBeforeEdit.lastActivity, formattingDate(LocalDate.now().toString()))) {
            softAssert.assertNotEquals(updatedListData.lastActivity, listDataBeforeEdit.lastActivity, "Last activity didn't change after editing the list");
        }

        softAssert.assertAll();

        verifyPermissionWorksAsExpected(new AbstractMap.SimpleEntry<>(updatedListData.name, updatedListData.permission));
    }

    @Test(priority = 10, description = "C244424. User cannot edit a public list if already exists")
    public void CSCAT_ProductLists_CannotEditList_DuplicateName() throws Exception {
        String companyId = CompanyApiService.getCompany(jwt)._id;
        String duplicateListName = "Static List For Automated API Tests";
        String listToUpdate = "Static List For UI Product List Tests 2";
        productListsPage = productListsPage.updateListNameAndSubmitChanges(listToUpdate, duplicateListName);
        String expectedError = "List with name \"" + duplicateListName + "\" already exists on company with ID " + companyId;
        boolean errorMessageDisplayed = productListsPage.isNoteDisplayedWithMessage(Enums.NoteType.INFO, expectedError);
        Assert.assertTrue(errorMessageDisplayed, "Error message is not displayed when editing a list with a duplicate name");
        List<String> listsDisplayed = productListsPage.refreshPage(ProductListsPage.class)
                .getAllListNames();
        Assert.assertTrue(listsDisplayed.contains(listToUpdate), "List with name: " + listToUpdate + " is not displayed after editing a list with a duplicate name");
    }

    @Test(priority = 11, description = "C244423. User can add products to single and multiple product list. User can add list from 'add to list' modal")
    public void CSCAT_ProductLists_CanAddProductsToSingleAndMultipleProductLists() throws Exception {
        String listToTest = SINGLE_PUBLIC_LIST_TO_ADD.getKey();
        productListsPage = productListsPage.addListIfNotExistent(listToTest, Enums.ProductListPermission.PUBLIC);

        productsPage = productListsPage.navigateToUrl(INSIGHTS_PRODUCTS_URL, ProductsPage.class);
        productsPage.paginator.selectNumberOfItemsPerPage(25);
        productsPage.removeAllAdvancedFilters();
        productsPage.searchByProductIdentifier(PREFIX_PRODUCTS_TO_ADD);

        // MULTIPLE LISTS
        var addToListModal = productsPage.selectProducts(FIRST_BATCH_PRODUCTS_TO_ADD).openAddToListModal();
        var selectedLists = addToListModal.addNewList(SINGLE_PUBLIC_LIST_TO_ADD_2)
                .selectList(listToTest)
                .getFilterChipsDisplayed();

        String[] expectedLists = {listToTest, SINGLE_PUBLIC_LIST_TO_ADD_2.getKey()};
        Assert.assertEqualsNoOrder(selectedLists.toArray(), expectedLists,
                "Selected lists are not the same as the expected lists" +
                        "\nExpected: " + Arrays.toString(expectedLists) +
                        "\nActual: " + selectedLists
        );

        var listsInInput = addToListModal.clickApplyButton().getListsInSearchInputValue();
        Assert.assertEqualsNoOrder(listsInInput.toArray(), expectedLists, "Lists in input are not the same as the expected lists" +
                "\nExpected: " + Arrays.toString(expectedLists) +
                "\nActual: " + listsInInput
        );

        addToListModal.clickAddButton();

        var isNoteDisplayed = productListsPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        Assert.assertTrue(isNoteDisplayed, "Success note was not displayed after adding products to lists");

        productListsPage = productsPage.navigateToUrl(INSIGHTS_PRODUCT_LIST_URL, ProductListsPage.class);

        for (var list : listsInInput) {
            productListsPage.scrollUntilListIsPresent(list);
            int productCountAfterAdding = productListsPage.getNumberOfProductsInList(list);
            Assert.assertEquals(productCountAfterAdding, FIRST_BATCH_PRODUCTS_TO_ADD.size(), "Product instances were not added to list " + list);
        }

        productListsPage.clickOnListProductNumber(listToTest);
        productsPage = new ProductsPage(driver);

        List<String> expectedIdsOfInstances = getExpectedIdsOfInstancesFromAPI(FIRST_BATCH_PRODUCTS_TO_ADD);
        waitForExpectedInstancesToDisplay(expectedIdsOfInstances);

        // SINGLE LIST
        productsPage.removeAllAdvancedFilters();

        productsPage.searchByProductIdentifier(PREFIX_PRODUCTS_TO_ADD);
        addToListModal = productsPage.selectProducts(SECOND_BATCH_PRODUCTS_TO_ADD).openAddToListModal();
        addToListModal.selectList(listToTest)
                .clickApplyButton()
                .clickAddButton();

        String expectedSuccessMessage = SECOND_BATCH_PRODUCTS_TO_ADD.size() + " items have been added to " + "\"" + listToTest + "\"";
        isNoteDisplayed = productsPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, expectedSuccessMessage);
        Assert.assertTrue(isNoteDisplayed, "Success note is not displayed after adding products to list: " + listToTest);

        productListsPage = productsPage.navigateToUrl(INSIGHTS_PRODUCT_LIST_URL, ProductListsPage.class);
        List<String> expectedIdsOfInstancesFromSecondBatch = getExpectedIdsOfInstancesFromAPI(SECOND_BATCH_PRODUCTS_TO_ADD);
        expectedIdsOfInstances.addAll(expectedIdsOfInstancesFromSecondBatch);

        productListsPage.scrollUntilListIsPresent(listToTest);
        int productCountAfterAddingSecondBatch = productListsPage.getNumberOfProductsInList(listToTest);
        Assert.assertEquals(productCountAfterAddingSecondBatch, expectedIdsOfInstances.size(), "Second batch of product instances were not added to list");

        productListsPage.clickOnListProductNumber(listToTest);
        productsPage = new ProductsPage(driver);
        waitForExpectedInstancesToDisplay(expectedIdsOfInstances);
        productsPage.removeAllAdvancedFilters();
        productListsPage = productsPage.navigateToUrl(INSIGHTS_PRODUCT_LIST_URL, ProductListsPage.class);
    }

    @Test(priority = 12, description = "C244429. User can remove product instances from list")
    public void CSCAT_ProductLists_CanRemoveProductsFromProductList() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String listToTest = SINGLE_PUBLIC_LIST_TO_ADD.getKey();
        productListsPage = productListsPage.addListIfNotExistent(listToTest, Enums.ProductListPermission.PUBLIC);

        ProductListUI listData = productListsPage.getListData(listToTest);
        if (listData.numberOfProducts == 0) {
            productsPage = addProductsToList(SECOND_BATCH_PRODUCTS_TO_ADD, listToTest);
            productsPage.closeNoteIfDisplayed(Enums.NoteType.SUCCESS);
        } else {
            productsPage = productListsPage.navigateToUrl(INSIGHTS_PRODUCTS_URL, ProductsPage.class);
            productsPage.paginator.selectNumberOfItemsPerPage(25);
            productsPage.closeContentSuggestionsBanner();
            productsPage.removeAllAdvancedFilters();
        }

        productsPage.openAdvancedFiltersSection()
                .addAndCreateFirstRule(FilterType.PRODUCT_LIST, listToTest, 1);

        List<String> uniqueIdsDisplayed = productsPage.getInstanceUniqueIdsDisplayed();
        String uniqueIdToRemoveFromList = uniqueIdsDisplayed.get(0);
        productsPage.deselectAllProducts();
        productsPage.selectProductsByInstanceUniqueId(Collections.singletonList(uniqueIdToRemoveFromList))
                .removeSelectedProductsFromList();

        boolean successNoteDisplayed = productsPage.isNoteDisplayed(Enums.NoteType.SUCCESS);
        softAssert.assertTrue(successNoteDisplayed, "Success note is not displayed after removing product from list");
        productsPage.closeNoteIfDisplayed(Enums.NoteType.SUCCESS);

        uniqueIdsDisplayed = productsPage.getInstanceUniqueIdsDisplayed();
        softAssert.assertFalse(
                uniqueIdsDisplayed.contains(uniqueIdToRemoveFromList),
                "Product instance: " + uniqueIdToRemoveFromList + " is still in list: " + listToTest + "\n" + uniqueIdsDisplayed
        );

        productListsPage = productsPage.navigateToUrl(INSIGHTS_PRODUCT_LIST_URL, ProductListsPage.class);

        ProductListUI listDataAfterRemove = productListsPage.getListData(listToTest);
        softAssert.assertEquals(
                listDataAfterRemove.numberOfProducts,
                uniqueIdsDisplayed.size(),
                "Number of products in list: " + listToTest + " is not correct after removing a product instance from it"
        );
        softAssert.assertAll();
    }

    @Test(priority = 13, description = "C244430. User can delete product lists")
    public void CSCAT_ProductLists_CanDeleteProductLists() throws InterruptedException {
        productListsPage.addListIfNotExistent(SINGLE_PUBLIC_LIST_TO_ADD.getKey(), SINGLE_PUBLIC_LIST_TO_ADD.getValue());
        productListsPage.addListIfNotExistent(SINGLE_PRIVATE_LIST_TO_ADD.getKey(), SINGLE_PRIVATE_LIST_TO_ADD.getValue());

        List<String> listNamesDisplayedBeforeRemoving = productListsPage.refreshPage(ProductListsPage.class).getAllListNames();

        List<String> listsToRemove = Arrays.asList(SINGLE_PUBLIC_LIST_TO_ADD.getKey(), SINGLE_PRIVATE_LIST_TO_ADD.getKey());
        try {
            productListsPage.deleteLists(listsToRemove);
        } catch (Exception e) {
            String errorMessage = "Exception while deleting lists: " + listsToRemove;
            LOGGER.error(errorMessage);
            Assert.fail(errorMessage);
        }

        List<String> expectedListNamesToBeDisplayed = new ArrayList<>(listNamesDisplayedBeforeRemoving);
        expectedListNamesToBeDisplayed.removeAll(listsToRemove);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(
                () ->
                {
                    var listNamesDisplayedAfterRemoving = productListsPage.getAllListNames();
                    Assert.assertEquals(listNamesDisplayedAfterRemoving, expectedListNamesToBeDisplayed, "Lists were not deleted");
                }
        );
    }

    @Test(priority = 14, description = "C243926. Verify sorting functionality in all the columns from the grid")
    public void CSCAT_ProductLists_CanSortColumns() throws Exception {
        var sortableColumns = productListsPage.tableCommonFeatures.getSortableColumns();
        if (sortableColumns.isEmpty()) {
            throw new Exception("No sortable columns were found");
        }

        var dataBeforeSort = productListsPage.getAllListsData();

        for (String column : sortableColumns) {
            performSortingTest(dataBeforeSort, column, true);
            performSortingTest(dataBeforeSort, column, false);
        }

        var columnToMove = sortableColumns.get(0);
        var columnToMoveTo = sortableColumns.get(2);

        productListsPage.tableCommonFeatures.rearrangeColumnPosition(columnToMove, columnToMoveTo);

        sortableColumns.remove(columnToMove);
        sortableColumns.add(2, columnToMove);

        var columnsDisplayed = productListsPage.tableCommonFeatures.getSortableColumns();
        Assert.assertEquals(columnsDisplayed, sortableColumns, "Columns were not rearranged correctly");
    }

    @Test(priority = 15, description = "C245642. Verify sorting functionality in all the columns from the grid")
    public void CSCAT_ProductLists_EditIconsShowInCorrectCellAfterSorting() throws Exception {
        String companyId = new HubCommonMethods().decodeInsightsJwtToGetCompanyId(jwt);
        new ProductVariantListCollection().editProductListName(STATIC_LIST_NEW_NAME, STATIC_LIST_NAME, companyId);

        productListsPage.refreshPage();
        
        productListsPage.tableCommonFeatures.sortColumn("List Name", true);

        productListsPage.scrollUntilListIsPresent(STATIC_LIST_NAME);
        productListsPage.clickEditIcon(STATIC_LIST_NAME, productListsPage.LIST_NAME_COLUMN_ID);
        productListsPage.clickEditIcon(STATIC_LIST_NAME, productListsPage.PERMISSION_COLUMN_ID);
        var editIconIsDisplayed = productListsPage.areEditIconsDisplayed(STATIC_LIST_NAME);
        Assert.assertFalse(editIconIsDisplayed, "Edit icons are still displayed in row for list name: " + STATIC_LIST_NAME);
    }

    private void performSortingTest(List<ProductListUI> dataBeforeSort, String columnName, boolean ascending) throws Exception {
        List<ProductListUI> expectedData = new ArrayList<>(dataBeforeSort);
        Comparator<ProductListUI> comparator;

        switch (columnName) {
            case "List Name":
                comparator = Comparator.comparing(o -> o.name.toLowerCase());
                break;
            case "# of Products":
                comparator = Comparator.comparingInt(o -> o.numberOfProducts);
                break;
            case "Owner":
                comparator = Comparator.comparing(o -> o.ownerName.toLowerCase());
                break;
            case "Permissions":
                comparator = Comparator.comparing(o -> o.permission);
                break;
            case "Last Activity":
                comparator = Comparator.comparing(o -> o.lastActivity);
                break;
            default:
                throw new Exception("Column name: " + columnName + " is not valid");
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }
        expectedData.sort(comparator);

        productListsPage.tableCommonFeatures.sortColumn(columnName, ascending);
        List<ProductListUI> tableDataAfterSorting = productListsPage.getAllListsData();

        Assert.assertEquals(tableDataAfterSorting, expectedData, "Column: " + columnName + " was not sorted correctly in " + (ascending ? "ascending" : "descending") + " order");
    }

    private ProductListsPage navigateToProductListPageIfNeeded() {
        if (!Objects.equals(driver.getTitle(), "Product Lists | Flywheel")) {
            driver.get(INSIGHTS_PRODUCT_LIST_URL);
            productListsPage = new ProductListsPage(driver);
        }
        return productListsPage;
    }

    private void verifySearchInputWorks(SoftAssert softAssert, String listNameOrOwner) {
        productListsPage.searchForList(listNameOrOwner);
        boolean noDataMessageIsDisplayed = productListsPage.tableCommonFeatures.isNoDataMessageDisplayed();
        softAssert.assertFalse(noDataMessageIsDisplayed, "No data message is displayed when searching for list or owner: " + listNameOrOwner);

        List<ProductListUI> productLists = productListsPage.getAllListsData();
        for (ProductListUI productListUI : productLists) {
            boolean listNameContainsSearchInput = productListUI.name.toLowerCase().contains(listNameOrOwner.toLowerCase());
            boolean ownerNameContainsSearchInput = productListUI.ownerName.toLowerCase().contains(listNameOrOwner.toLowerCase());
            boolean searchInputIsInListNameOrOwnerName = listNameContainsSearchInput || ownerNameContainsSearchInput;
            softAssert.assertTrue(searchInputIsInListNameOrOwnerName, "Search input: " + listNameOrOwner + " is not in list name or owner name: " + productListUI);
        }
    }

    private ProductListsPage verifyProductListCanBeCreated(AbstractMap.SimpleEntry<String, Enums.ProductListPermission> listToTest) throws Exception {
        try {
            productListsPage = productListsPage.addList(listToTest.getKey(), listToTest.getValue());
        } catch (Exception e) {
            String message = "Exception occurred while creating a single list: " + e.getMessage();
            LOGGER.info(message);
            Assert.fail(message);
        }

        String expectedSuccessMessage = "Your list has been added!";
        boolean isNoteDisplayed = productListsPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, expectedSuccessMessage);
        Assert.assertTrue(isNoteDisplayed, "Success note is not displayed after adding a new list");

        CpgAccount accountInfo = CPGAccountService.getAccountInfo(jwt);
        String expectedOwnerName = accountInfo.firstName + " " + accountInfo.lastName;
        String expectedLastActivity = formattingDate(LocalDate.now().toString());

        ProductListUI expectedListInfo = new ProductListUI(listToTest.getKey(), 0, expectedOwnerName, listToTest.getValue(), expectedLastActivity);
        verifyListIsDisplayedWithCorrectInfo(expectedListInfo);
        return productListsPage;

    }

    private void verifyListIsDisplayedWithCorrectInfo(ProductListUI expectedListInfo) {
        SoftAssert softAssert = new SoftAssert();

        boolean listNameExists = productListsPage.doesListExist(expectedListInfo.name);
        softAssert.assertTrue(listNameExists, "List: " + expectedListInfo.name + " is not in company lists");

        ProductListUI listInUI = productListsPage.getListData(expectedListInfo.name);
        softAssert.assertEquals(listInUI, expectedListInfo, "List: " + expectedListInfo.name + " was not displayed with the correct data");

        softAssert.assertAll();
    }

    private void verifyPermissionWorksAsExpected(AbstractMap.SimpleEntry<String, Enums.ProductListPermission> listToTest) {
        WebDriver webDriver = new SeleniumDriver().initializeChromeDriver(READ_CONFIG.getHeadlessMode(), true);
        webDriver.get(DC_LOGIN_ENDPOINT);
        String listName = listToTest.getKey();
        Enums.ProductListPermission permission = listToTest.getValue();

        try {
            new DCLoginPage(webDriver).login(CPG_USERNAME, PASSWORD);
            webDriver.get(INSIGHTS_PRODUCT_LIST_URL);
            ProductListsPage productListsPage = new ProductListsPage(webDriver);
            boolean listIsDisplayed = productListsPage.doesListExist(listName);
            Assert.assertTrue(listIsDisplayed, permission + " product list " + listName + " was not visible for other user.");
            boolean editIconsDisplayed = productListsPage.areEditIconsDisplayed(listName);
            if (permission == Enums.ProductListPermission.PUBLIC) {
                Assert.assertTrue(editIconsDisplayed, "Actions should be enabled. List: " + listName);
            } else {
                Assert.assertFalse(editIconsDisplayed, "Actions should be disabled. List: " + listName);
            }
        } catch (Exception e) {
            String message = "Exception occurred while verifying permission: " + e.getMessage();
            LOGGER.info(message);
            Assert.fail(message);
        } finally {
            webDriver.close();
        }
    }

    private ProductListUI addProductsToListIfNeededAndGoBackToProductListsPage(List<UserFriendlyInstancePath> instancesToAddToList, String listName) throws InterruptedException {
        ProductListUI listData = productListsPage.getListData(listName);

        if (listData.numberOfProducts == 0) {
            addProductsToList(instancesToAddToList, listName);
            productListsPage = productsPage.navigateToUrl(INSIGHTS_PRODUCT_LIST_URL, ProductListsPage.class);
            listData = productListsPage.getListData(listName);
        }

        return listData;
    }

    private ProductsPage addProductsToList(List<UserFriendlyInstancePath> instancesToAddToList, String listName) throws InterruptedException {
        productsPage = productListsPage.navigateToUrl(INSIGHTS_PRODUCTS_URL, ProductsPage.class);
        productsPage.paginator.selectNumberOfItemsPerPage(25);
        productsPage.closeContentSuggestionsBanner();
        productsPage.removeAllAdvancedFilters();
        productsPage.searchByProductIdentifier(PREFIX_PRODUCTS_TO_ADD);

        AddToListModal addToListModal;
        try {
            addToListModal = productsPage.selectProducts(instancesToAddToList).openAddToListModal();
        } catch (NoSuchElementException e) {
            LOGGER.info("Race condition occurred. Trying again...\n");
            productsPage.closeReactModalIfDisplayed();
            productsPage.deselectAllProducts();
            addToListModal = productsPage.selectProducts(instancesToAddToList).openAddToListModal();
        }
        addToListModal.selectList(listName).clickAddButton();
        return productsPage;
    }

    private List<String> getExpectedIdsOfInstancesFromAPI(List<UserFriendlyInstancePath> products) throws Exception {
        List<String> expectedIdsOfInstances = new ArrayList<>();
        for (var product : products) {
            var instanceUniqueId = getInstanceUniqueIdFromAPI(product);
            expectedIdsOfInstances.add(instanceUniqueId);
        }
        return expectedIdsOfInstances;
    }

    private String getInstanceUniqueIdFromAPI(UserFriendlyInstancePath userFriendlyInstancePath) throws Exception {
        Company company = CompanyApiService.getCompany(jwt);

        String localeId = company.getLocaleId(userFriendlyInstancePath.localeName);
        String retailerId = company.getRetailerId(userFriendlyInstancePath.retailerName);
        String campaignId = company.getCampaignId(userFriendlyInstancePath.campaignName);

        ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal instance =
                ProductVersioningApiService.getLiveProductInstanceByUniqueId(userFriendlyInstancePath.productIdentifier, localeId, campaignId, retailerId, jwt);

        return instance.uniqueId;
    }

    private void waitForExpectedInstancesToDisplay(List<String> expectedIdsOfInstances) {
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(
                () ->
                {
                    List<String> uniqueIdsOfInstancesDisplayed = productsPage.getInstanceUniqueIdsDisplayed();

                    Assert.assertEqualsNoOrder(
                            uniqueIdsOfInstancesDisplayed.toArray(),
                            expectedIdsOfInstances.toArray(),
                            "Incorrect products in list after adding instances to it" +
                                    "\nActual: " + uniqueIdsOfInstancesDisplayed +
                                    "\nExpected: " + expectedIdsOfInstances
                    );
                }
        );
    }
}
