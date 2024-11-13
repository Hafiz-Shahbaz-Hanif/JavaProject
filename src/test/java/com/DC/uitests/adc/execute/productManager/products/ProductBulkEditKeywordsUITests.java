package com.DC.uitests.adc.execute.productManager.products;

import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.products.bulkEditKeywordsPage.BulkAddDeleteKeywordsModal;
import com.DC.pageobjects.adc.execute.productManager.products.bulkEditKeywordsPage.BulkEditKeywordsPage;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.OpenSearchApiRequests;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantKeywords;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.GenericMultiListModal;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.DC.constants.InsightsConstants.INSIGHTS_PRODUCTS_URL;
import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static com.DC.constants.ProductVersioningConstants.*;
import static com.DC.utilities.SharedMethods.generateRandomNumber;
import static java.util.Arrays.asList;
import static org.awaitility.Awaitility.await;

public class ProductBulkEditKeywordsUITests extends BaseClass {
    private final String SUPPORT_USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private ProductsPage productsPage;
    private BulkEditKeywordsPage bulkEditKeywordsPage;
    private GenericMultiListModal genericMultiListModal;
    private final String PRODUCT_PREFIX = "QA-BULK-EDIT-";

    private final UserFriendlyInstancePath PRODUCT_INSTANCE_1 = new UserFriendlyInstancePath(PRODUCT_PREFIX + "001", "es-MX", "Amazon.com", null);
    private final UserFriendlyInstancePath PRODUCT_INSTANCE_2 = new UserFriendlyInstancePath(PRODUCT_PREFIX + "001", "en-US", null, null);
    private final UserFriendlyInstancePath PRODUCT_INSTANCE_3 = new UserFriendlyInstancePath(PRODUCT_PREFIX + "002", "en-US", "Amazon.com", "Halloween");

    private String uniqueIdOfInstance1;
    private String uniqueIdOfInstance2;
    private String uniqueIdOfInstance3;
    private final String SINGLE_KEYWORD_VALUE = "keywordValue";
    private final String EXTRA_KEYWORD_VALUE = "extra";

    private List<String> expectedColumns;
    private final List<String> EXTRA_KEYWORD_VALUES = asList(SINGLE_KEYWORD_VALUE.toUpperCase(), "extra1", "extra2", "extra3", "extra4");

    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).loginDcApp(SUPPORT_USERNAME, PASSWORD);

        driver.get(INSIGHTS_PRODUCTS_URL);
        productsPage = new ProductsPage(driver);
        productsPage.paginator.selectNumberOfItemsPerPage(50);

        jwt = SecurityAPI.getJwtForInsightsUser(driver);

        productsPage.searchByProductIdentifier(PRODUCT_PREFIX);

        uniqueIdOfInstance1 = productsPage.getInstanceId(PRODUCT_INSTANCE_1);
        uniqueIdOfInstance2 = productsPage.getInstanceId(PRODUCT_INSTANCE_2);
        uniqueIdOfInstance3 = productsPage.getInstanceId(PRODUCT_INSTANCE_3);

        expectedColumns = Stream.of(asList("Product Identifier", "Versions"), new ArrayList<>(PRODUCT_KEYWORD_BUCKETS_UI.values()))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @AfterMethod
    public void cleanupTestMethod() throws Exception {
        productsPage = productsPage.recoverPageIfBlankPageIsDisplayed(INSIGHTS_PRODUCTS_URL, productsPage);
        productsPage.closeReactModalIfDisplayed();
        if (driver.getTitle().contains("Products")) {
            productsPage.tableCommonFeatures.deselectAll();
        }
    }

    @AfterClass(alwaysRun = true)
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Clicking Edit > Keywords takes the user to the bulk edit experience." +
            "The keywords in database also show up in the edit mode" +
            "Selected products show up in the edit mode"
    )
    public void CGEN_BulkEditKeywords_ClickingEditKeywordsReturnsCorrectPageAndData() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        var instanceUniqueIdsToTest = asList(uniqueIdOfInstance1, uniqueIdOfInstance2, uniqueIdOfInstance3);
        LOGGER.info("Unique IDs of instances: " + instanceUniqueIdsToTest);

        ProductsPage.BottomActionBar bottomActionBar = productsPage.selectProductsByInstanceUniqueId(instanceUniqueIdsToTest);
        int expectedNumberOfProducts = bottomActionBar.getNumberOfSelectedProducts();

        try {
            bulkEditKeywordsPage = bottomActionBar.clickEditKeywordsButton();
        } catch (Exception e) {
            String msg = "Exception occurred while clicking edit keywords button: " + e.getMessage();
            LOGGER.error(msg);
            Assert.fail(msg);
        }

        // TESTING CORRECT COLUMNS ARE DISPLAYED (PRODUCT IDENTIFIER, VERSIONS & BUCKETS)
        List<String> columnsInEditBulkPage = bulkEditKeywordsPage.tableCommonFeatures.getColumnsDisplayed();

        softAssert.assertEquals(
                columnsInEditBulkPage,
                expectedColumns,
                "Columns displayed in the bulk edit page are not the same as the expected columns"
        );

        int numberOfProductsInBulkEditPage = bulkEditKeywordsPage.getNumberDisplayedNextToSearchInput();

        softAssert.assertEquals(
                expectedNumberOfProducts,
                numberOfProductsInBulkEditPage,
                "Number of products displayed in the bulk edit page is not the same as the number of products selected"
        );

        // TESTING CORRECT KEYWORD SETS ARE DISPLAYED
        for (String uniqueId : instanceUniqueIdsToTest) {
            ProductVariantKeywords keywordSetDisplayed = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueId);
            String idOfInstanceToTest = bulkEditKeywordsPage.getRowId(uniqueId);
            verifyKeywordSetDisplayedIsSameAsBackend(idOfInstanceToTest, keywordSetDisplayed, softAssert);
        }

        // TESTING SUBMIT BUTTON IS DISABLED
        boolean submitButtonEnabled = bulkEditKeywordsPage.isSubmitButtonEnabled();
        softAssert.assertFalse(submitButtonEnabled, "Submit button is enabled when no changes are made");

        softAssert.assertAll();
    }

    @Test(priority = 2, description = "C243879. Modal works and only selected keywords are displayed in the edit bulk page")
    public void CGEN_BulkEditKeywords_ManageColumnsWorksAsExpected() throws InterruptedException {
        bulkEditKeywordsPage = goToEditBulkKeywordsIfNeeded();

        // TESTING UPDATE BUTTON IS DISABLED WHEN NO COLUMNS SELECTED
        genericMultiListModal = bulkEditKeywordsPage.clickManageColumnsButton();
        genericMultiListModal.moveAllOptionsToTheLeft();
        boolean updateColumnsButtonEnabled = genericMultiListModal.isUpdateButtonEnabled();
        Assert.assertFalse(updateColumnsButtonEnabled, "Update button is enabled when no columns are selected");

        // TESTING SEARCH FUNCTIONALITY
        String searchTerm = "Title";
        genericMultiListModal.searchForOption(searchTerm);

        List<String> unselectedOptionsAfterSearch = genericMultiListModal.getUnselectedOptions();
        boolean allUnselectedBucketsContainSearchTerm = unselectedOptionsAfterSearch.stream().allMatch(option -> option.contains(searchTerm));
        Assert.assertTrue(allUnselectedBucketsContainSearchTerm, "Not all unselected buckets contain the search term");

        // TESTING ORDERING
        var optionsToMove = asList("Title", "On Page");
        genericMultiListModal.clearSearch();
        genericMultiListModal.moveOptionsToTheRight(optionsToMove);
        genericMultiListModal.reorderOption("Title", 2);
        List<String> selectedOptions = genericMultiListModal.getSelectedOptions();
        Collections.reverse(optionsToMove);
        Assert.assertEquals(selectedOptions, optionsToMove, "Columns are not ordered as expected in Manage Columns modal");

        // TESTING UPDATE BUTTON
        bulkEditKeywordsPage = genericMultiListModal.updateChanges(BulkEditKeywordsPage.class);
        List<String> columnsInBulkEditPage = bulkEditKeywordsPage.getNonDefaultColumnsDisplayed();

        Assert.assertEquals(
                columnsInBulkEditPage,
                selectedOptions,
                "Columns displayed in the edit bulk page are not the same as the selected columns in the manage columns modal"
        );
    }

    @Test(priority = 3, description = "Search input works as expected")
    public void CGEN_BulkEditKeywords_CanSearchForAKeywordValue() throws InterruptedException {
        bulkEditKeywordsPage = goToEditBulkKeywordsIfNeeded();
        bulkEditKeywordsPage = updateColumnsIfNeeded();

        SoftAssert softAssert = new SoftAssert();

        List<String> instanceUniqueIdsBeforeSearching = bulkEditKeywordsPage.getInstanceUniqueIdsDisplayed();

        // 1. GET EXPECTED DATA - INSTANCE IDS OF VERSIONS THAT CONTAIN THAT KEYWORD VALUE
        String keywordValueToSearch = "white wine";
        List<String> expectedInstanceUniqueIds = bulkEditKeywordsPage.getUniqueIdsOfInstancesThatContainKeywordValue(keywordValueToSearch);

        // 2. LOOK FOR KEYWORD VALUE
        bulkEditKeywordsPage = bulkEditKeywordsPage.searchForKeywordValues(keywordValueToSearch);

        // 3. GET ACTUAL DATA - INSTANCE IDS OF VERSIONS THAT ARE DISPLAYED AND COMPARE ACTUAL DATA WITH EXPECTED DATA. THEY SHOULD BE THE SAME
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(
                () ->
                {
                    var actualInstanceUniqueIds = bulkEditKeywordsPage.getInstanceUniqueIdsDisplayed();
                    softAssert.assertEquals(
                            actualInstanceUniqueIds,
                            expectedInstanceUniqueIds,
                            "Instance unique ids displayed are not the same as the expected instance unique ids after searching for: " + keywordValueToSearch
                    );
                }
        );

        int numberOfProductsNextToSearchInput = bulkEditKeywordsPage.getNumberDisplayedNextToSearchInput();
        int numberOfProductsDisplayed = bulkEditKeywordsPage.getInstanceUniqueIdsDisplayed().size();

        softAssert.assertEquals(numberOfProductsNextToSearchInput, numberOfProductsDisplayed, "Number of product versions next to search input is not the same as the number of versions displayed");
        softAssert.assertEquals(expectedInstanceUniqueIds.size(), numberOfProductsDisplayed, "Number of product versions displayed is not the expected number");

        // 5. CHECK THAT ALL VERSIONS ARE DISPLAYED AGAIN
        bulkEditKeywordsPage.clearSearchInput();
        bulkEditKeywordsPage.waitForDOMStabilization();
        List<String> instanceUniqueIdsAfterSearching = bulkEditKeywordsPage.getInstanceUniqueIdsDisplayed();
        softAssert.assertEqualsNoOrder(
                instanceUniqueIdsAfterSearching.toArray(),
                instanceUniqueIdsBeforeSearching.toArray(),
                "Instance unique ids displayed are not the same as the original unique ids"
        );

        softAssert.assertAll();
    }

    @Test(priority = 4, description = "Editing a keyword value makes that cell highlight in yellow. Submit works as expected")
    public void CGEN_BulkEditProperties_EditingKeywordValuesHighlightsCorrectProductInstance_SubmitButtonUpdatesAsExpected() throws Exception {
        bulkEditKeywordsPage = goToEditBulkKeywordsIfNeeded();
        bulkEditKeywordsPage = updateColumnsIfNeeded();

        SoftAssert softAssert = new SoftAssert();

        String idOfInstanceToTest = bulkEditKeywordsPage.getRowId(uniqueIdOfInstance1);
        String idOfSecondInstanceToTest = bulkEditKeywordsPage.getRowId(uniqueIdOfInstance2);
        String idOfThirdInstanceToTest = bulkEditKeywordsPage.getRowId(uniqueIdOfInstance3);

        int highlightedCellsBefore = bulkEditKeywordsPage.getNumberOfHighlightedCells();

        // EDITING A KEYWORD VALUE HIGHLIGHTS CELL
        String firstBucketToTest = "Rank Tracking";
        String secondBucketToTest = "Optional";
        String extraKeywordValue = "extra keyword " + generateRandomNumber();

        bulkEditKeywordsPage.clickEditIconAndEditCellValue(uniqueIdOfInstance1, firstBucketToTest, "test" + generateRandomNumber());
        bulkEditKeywordsPage.addNewValueToCell(uniqueIdOfInstance1, firstBucketToTest, extraKeywordValue);
        bulkEditKeywordsPage.clickSaveValueIcon(uniqueIdOfInstance1, firstBucketToTest);

        int highlightedCellsAfter = bulkEditKeywordsPage.getNumberOfHighlightedCells();
        softAssert.assertEquals(highlightedCellsAfter, highlightedCellsBefore + 1, "Number of highlighted cells is not correct");

        bulkEditKeywordsPage.clickEditIconAndEditCellValue(uniqueIdOfInstance3, secondBucketToTest, "riesling" + generateRandomNumber());
        bulkEditKeywordsPage.clickSaveValueIcon(uniqueIdOfInstance3, secondBucketToTest);
        boolean isCellHighlighted = bulkEditKeywordsPage.isCellHighlighted(uniqueIdOfInstance3, secondBucketToTest);
        softAssert.assertTrue(isCellHighlighted, "Cell " + secondBucketToTest + " on product version " + uniqueIdOfInstance3 + " is not highlighted");

        // CLICKING SUBMIT BUTTON UPDATES THE PRODUCT INSTANCE WITH THE NEW KEYWORD VALUES
        var response = OpenSearchApiRequests.getInstanceWithPropertyIndex(jwt, idOfSecondInstanceToTest, new ArrayList<>());
        var expectedKeywordSetOfInstance2 = response.jsonPath().getObject("cpgKeywordSets.master[0].keywordBuckets", ProductVariantKeywords.class);
        expectedKeywordSetOfInstance2 = expectedKeywordSetOfInstance2 != null ? expectedKeywordSetOfInstance2 : new ProductVariantKeywords(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        var expectedKeywordSetOfInstance1 = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance1);
        var expectedKeywordSetOfInstance3 = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance3);

        productsPage = bulkEditKeywordsPage.clickSubmitButton((ProductsPage.class));

        verifyKeywordSetDisplayedIsSameAsBackend(idOfInstanceToTest, expectedKeywordSetOfInstance1, softAssert);
        verifyKeywordSetDisplayedIsSameAsBackend(idOfSecondInstanceToTest, expectedKeywordSetOfInstance2, softAssert);
        verifyKeywordSetDisplayedIsSameAsBackend(idOfThirdInstanceToTest, expectedKeywordSetOfInstance3, softAssert);

        softAssert.assertAll();

        // REMOVE EXTRA KEYWORD VALUE FROM CELL AND VERIFY THAT KEYWORD VALUE IS REMOVED FROM INSTANCE
        softAssert = new SoftAssert();
        selectProductsAndClickEditKeywordsButton(Collections.singletonList(uniqueIdOfInstance1));
        bulkEditKeywordsPage.clickEditIcon(uniqueIdOfInstance1, firstBucketToTest);
        bulkEditKeywordsPage.removeInputValue(uniqueIdOfInstance1, firstBucketToTest, extraKeywordValue);
        bulkEditKeywordsPage.clickSaveValueIcon(uniqueIdOfInstance1, firstBucketToTest);

        productsPage = bulkEditKeywordsPage.clickSubmitButton(ProductsPage.class);
        response = OpenSearchApiRequests.getInstanceWithPropertyIndex(jwt, idOfInstanceToTest, new ArrayList<>());
        ProductVariantKeywords keywordSetFromAPI = response.jsonPath().getObject("cpgKeywordSets.master[0].keywordBuckets", ProductVariantKeywords.class);

        softAssert.assertFalse(keywordSetFromAPI.rankTracking.isEmpty(), "All keyword values from instance: " + idOfInstanceToTest + " were removed");
        softAssert.assertFalse(keywordSetFromAPI.rankTracking.contains(extraKeywordValue), "Keyword value was not removed from instance: " + idOfInstanceToTest);
        softAssert.assertAll();
    }

    @Test(priority = 5, description = "Bulk add keywords modal")
    public void CGEN_BulkEditProperties_BulkAddKeywordModalWorksAsExpected() throws Exception {
        bulkEditKeywordsPage = goToEditBulkKeywordsIfNeeded();
        bulkEditKeywordsPage = updateColumnsIfNeeded();

        ProductVariantKeywords expectedKeywordSetFirstInstance = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance1);
        ProductVariantKeywords expectedKeywordSetSecondInstance = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance2);
        ProductVariantKeywords expectedKeywordSetThirdInstance = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance3);

        SoftAssert softAssert = new SoftAssert();

        // TESTING ADD KEYWORD BUCKET BUTTON
        BulkAddDeleteKeywordsModal bulkModal = bulkEditKeywordsPage.clickBulkAddKeywordsButton();
        int originalNumberOfBuckets = bulkModal.getNumberOfKeywordBucketSections();
        bulkModal.clickAddKeywordBucketButton();
        bulkModal.selectBucket(1, "Title");
        int currentNumberOfBuckets = bulkModal.getNumberOfKeywordBucketSections();
        softAssert.assertEquals(currentNumberOfBuckets, originalNumberOfBuckets + 1, "Number of keyword bucket sections didn't increase by 1");

        // TESTING ADD KEYWORD BUTTON SAVES IN CORRECT BUCKET
        bulkEditKeywordsPage = bulkModal.clickCancelButton(BulkEditKeywordsPage.class);
        bulkModal = bulkEditKeywordsPage.clickBulkAddKeywordsButton();
        bulkModal.selectBucket("Title");
        bulkModal.clickAddKeywordBucketButton();
        bulkModal.selectBucket("On Page");

        bulkModal.addKeywordToBucket("Title", SINGLE_KEYWORD_VALUE);
        List<String> keywordsInTitleBucket = bulkModal.getKeywordsInBucket("Title");
        List<String> keywordsInOnPageBucket = bulkModal.getKeywordsInBucket("On Page");
        softAssert.assertTrue(keywordsInTitleBucket.contains(SINGLE_KEYWORD_VALUE), "Keyword value was not added to bucket");
        softAssert.assertFalse(keywordsInOnPageBucket.contains(SINGLE_KEYWORD_VALUE), "Keyword value was added to incorrect bucket");

        // TESTING DELETE ICON REMOVES KEYWORD VALUE FROM BUCKET

        var keywordToTest = EXTRA_KEYWORD_VALUES.get(0);
        bulkModal.addKeywordToBucket("Title", keywordToTest);
        bulkModal.addKeywordsToBucket("On Page", EXTRA_KEYWORD_VALUES);
        bulkModal.clickRemoveKeywordIcon("On Page", keywordToTest);
        keywordsInOnPageBucket = bulkModal.getKeywordsInBucket("On Page");
        keywordsInTitleBucket = bulkModal.getKeywordsInBucket("Title");
        softAssert.assertFalse(keywordsInOnPageBucket.contains(keywordToTest), "Keyword value was not removed from bucket");
        softAssert.assertTrue(keywordsInTitleBucket.contains(keywordToTest), "Keyword value was removed from incorrect bucket");
        bulkModal.clickRemoveKeywordIcon("Title", keywordToTest);

        // TESTING CLEAR ALL ICON REMOVES ALL KEYWORD VALUES FROM BUCKET
        bulkModal.clearAllKeywordsFromBucket("On Page");
        keywordsInOnPageBucket = bulkModal.getKeywordsInBucket("On Page");
        keywordsInTitleBucket = bulkModal.getKeywordsInBucket("Title");
        softAssert.assertTrue(keywordsInOnPageBucket.isEmpty(), "Keyword value was not empty after clicking clear all icon");
        softAssert.assertFalse(keywordsInTitleBucket.isEmpty(), "Keyword value was empty after clicking clear all icon in another bucket");

        bulkModal.addKeywordsToBucket("On Page", EXTRA_KEYWORD_VALUES);

        // TESTING SAVE AND EXIT BUTTON
        bulkModal.clickAddKeywordBucketButton();
        bulkModal.selectBucket("Branded");
        bulkModal.addKeywordToBucket("Branded", EXTRA_KEYWORD_VALUE);
        bulkEditKeywordsPage = bulkModal.clickSaveAndExitButton(BulkEditKeywordsPage.class);

        verifyKeywordsWereAddedToTable(expectedKeywordSetFirstInstance, uniqueIdOfInstance1, softAssert);
        verifyKeywordsWereAddedToTable(expectedKeywordSetSecondInstance, uniqueIdOfInstance2, softAssert);
        verifyKeywordsWereAddedToTable(expectedKeywordSetThirdInstance, uniqueIdOfInstance3, softAssert);
        
        bulkEditKeywordsPage.clickSubmitButton();
        var infoMessageDisplayed = bulkEditKeywordsPage.isNoteDisplayed(Enums.NoteType.INFO);
        Assert.assertTrue(infoMessageDisplayed, "The info banner was not displayed after clicking the apply changes button with duplicate keywords between buckets.");

        softAssert.assertAll();
    }

    @Test(priority = 6, description = "Bulk remove keywords modal")
    public void CGEN_BulkEditProperties_BulkRemoveKeywordModalWorksAsExpected() throws Exception {
        bulkEditKeywordsPage = goToEditBulkKeywordsIfNeeded();
        bulkEditKeywordsPage = updateColumnsIfNeeded();

        BulkAddDeleteKeywordsModal bulkModal = bulkEditKeywordsPage.clickBulkAddKeywordsButton();

        bulkModal.selectBucket("Title");
        bulkModal.clickAddKeywordBucketButton();
        bulkModal.selectBucket("On Page");
        bulkModal.clickAddKeywordBucketButton();
        bulkModal.selectBucket("Branded");

        bulkModal.addKeywordToBucket("Title", SINGLE_KEYWORD_VALUE);
        bulkModal.addKeywordsToBucket("On Page", EXTRA_KEYWORD_VALUES);
        bulkModal.addKeywordToBucket("Branded", EXTRA_KEYWORD_VALUE);
        bulkEditKeywordsPage = bulkModal.clickSaveAndExitButton(BulkEditKeywordsPage.class);

        ProductVariantKeywords expectedKeywordSetFirstInstance = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance1);
        ProductVariantKeywords expectedKeywordSetSecondInstance = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance2);
        ProductVariantKeywords expectedKeywordSetThirdInstance = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance3);

        SoftAssert softAssert = new SoftAssert();

        // TESTING ADD KEYWORD BUTTON SAVES IN CORRECT BUCKET
        bulkModal = bulkEditKeywordsPage.clickBulkDeleteKeywordsButton();
        bulkModal.selectBucket("Title");
        bulkModal.clickAddKeywordBucketButton();
        bulkModal.selectBucket("On Page");

        bulkModal.addKeywordToBucket("Title", SINGLE_KEYWORD_VALUE);
        List<String> keywordsInTitleBucket = bulkModal.getKeywordsInBucket("Title");
        List<String> keywordsInOnPageBucket = bulkModal.getKeywordsInBucket("On Page");
        softAssert.assertTrue(keywordsInTitleBucket.contains(SINGLE_KEYWORD_VALUE), "Keyword value was not added to bucket");
        softAssert.assertFalse(keywordsInOnPageBucket.contains(SINGLE_KEYWORD_VALUE), "Keyword value was added to incorrect bucket");

        // TESTING SAVE AND SELECT MORE BUTTON
        bulkModal.addKeywordsToBucket("On Page", EXTRA_KEYWORD_VALUES);

        // TESTING SAVE AND EXIT BUTTON
        bulkModal.clickAddKeywordBucketButton();
        bulkModal.selectBucket("Branded");

        bulkModal.addKeywordToBucket("Branded", EXTRA_KEYWORD_VALUE);
        bulkEditKeywordsPage = bulkModal.clickSaveAndExitButton(BulkEditKeywordsPage.class);

        verifyKeywordsWereRemovedFromTable(expectedKeywordSetFirstInstance, uniqueIdOfInstance1, softAssert);
        verifyKeywordsWereRemovedFromTable(expectedKeywordSetSecondInstance, uniqueIdOfInstance2, softAssert);
        verifyKeywordsWereRemovedFromTable(expectedKeywordSetThirdInstance, uniqueIdOfInstance3, softAssert);

        softAssert.assertAll();
    }

    private BulkEditKeywordsPage goToEditBulkKeywordsIfNeeded() throws InterruptedException {
        if (!driver.getTitle().contains("Bulk Edit")) {
            selectProductsAndClickEditKeywordsButton(asList(uniqueIdOfInstance1, uniqueIdOfInstance2, uniqueIdOfInstance3));
        }
        return bulkEditKeywordsPage;
    }

    private BulkEditKeywordsPage updateColumnsIfNeeded() throws InterruptedException {
        List<String> columnsDisplayed = bulkEditKeywordsPage.tableCommonFeatures.getColumnsDisplayed();

        if (columnsDisplayed.size() != expectedColumns.size()) {
            genericMultiListModal = bulkEditKeywordsPage.clickManageColumnsButton();
            genericMultiListModal.moveAllOptionsToTheRight();
            bulkEditKeywordsPage = genericMultiListModal.updateChanges(BulkEditKeywordsPage.class);
        }

        bulkEditKeywordsPage.clearSearchInput();
        return bulkEditKeywordsPage;
    }

    private void selectProductsAndClickEditKeywordsButton(List<String> uniqueIdsOfInstancesToSelect) throws InterruptedException {
        productsPage.closeContentSuggestionsBanner();
        productsPage.paginator.selectNumberOfItemsPerPage(50);
        productsPage.searchByProductIdentifier(PRODUCT_PREFIX);
        bulkEditKeywordsPage = productsPage.closeContentSuggestionsBanner()
                .selectProductsByInstanceUniqueId(uniqueIdsOfInstancesToSelect)
                .clickEditKeywordsButton();
        bulkEditKeywordsPage.waitForDOMStabilization();
    }

    private void verifyKeywordSetDisplayedIsSameAsBackend(String idOfInstanceToTest, ProductVariantKeywords keywordSetInUI, SoftAssert softAssert) throws Exception {
        Response response = OpenSearchApiRequests.getInstanceWithPropertyIndex(jwt, idOfInstanceToTest, new ArrayList<>());
        ProductVariantKeywords keywordSetFromAPI = response.jsonPath().getObject("cpgKeywordSets.master[0].keywordBuckets", ProductVariantKeywords.class);
        if (keywordSetFromAPI == null) {
            keywordSetFromAPI = new ProductVariantKeywords(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }
        softAssert.assertEquals(keywordSetFromAPI, keywordSetInUI, "Keyword values of instance: " + idOfInstanceToTest + " are not the same as in the backend");
    }

    private void verifyKeywordsWereAddedToTable(ProductVariantKeywords expectedKeywordSet, String uniqueIdOfInstance, SoftAssert softAssert) throws IOException {
        ProductVariantKeywords keywordSetAfterInFirstInstance = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance);
        expectedKeywordSet.title.add(SINGLE_KEYWORD_VALUE);
        expectedKeywordSet.onPage.addAll(EXTRA_KEYWORD_VALUES);
        expectedKeywordSet.branded.add(EXTRA_KEYWORD_VALUE);
        softAssert.assertEquals(keywordSetAfterInFirstInstance, expectedKeywordSet, "Keyword set was not updated as expected in instance: " + uniqueIdOfInstance);
    }

    private void verifyKeywordsWereRemovedFromTable(ProductVariantKeywords expectedKeywordSet, String uniqueIdOfInstance, SoftAssert softAssert) throws IOException {
        ProductVariantKeywords keywordSetAfterInInstance = bulkEditKeywordsPage.getKeywordSetDisplayed(uniqueIdOfInstance);
        expectedKeywordSet.title.removeIf(value -> value.equals(SINGLE_KEYWORD_VALUE));
        expectedKeywordSet.onPage.removeAll(EXTRA_KEYWORD_VALUES);
        expectedKeywordSet.branded.removeIf(value -> value.equals(EXTRA_KEYWORD_VALUE));
        softAssert.assertEquals(keywordSetAfterInInstance, expectedKeywordSet, "Keyword set was not updated as expected after using bulk remove modal in instance: " + uniqueIdOfInstance);
    }
}
