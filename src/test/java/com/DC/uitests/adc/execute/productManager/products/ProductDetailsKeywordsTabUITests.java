package com.DC.uitests.adc.execute.productManager.products;

import com.DC.constants.InsightsConstants;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.execute.productManager.products.bulkEditKeywordsPage.BulkAddDeleteKeywordsModal;
import com.DC.pageobjects.adc.execute.productManager.products.bulkEditKeywordsPage.BulkEditKeywordsPage;
import com.DC.pageobjects.adc.execute.productManager.products.BulkEditSelectProductsPage;
import com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage.KeywordsTab;
import com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage.PropertiesTab;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.SharedRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.enums.Enums;
import com.DC.pageobjects.filters.ProductsLeftSideFilter;
import com.DC.pageobjects.filters.MultiselectFilterWithSearchInput;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;
import static java.util.Arrays.asList;
import static org.awaitility.Awaitility.await;

public class ProductDetailsKeywordsTabUITests extends BaseClass {
    private static final String USERNAME = READ_CONFIG.getInsightsUsername();
    private static final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private static final String PREFIX_PRODUCTS_TO_TEST = "QA-STATIC-PRODUCT-";
    private static final String UNIQUE_ID_OF_PRODUCT_TO_TEST = PREFIX_PRODUCTS_TO_TEST + "001";
    private static final String RETAILER_TO_TEST = "Amazon.com";
    private static final String LOCALE_TO_TEST = "es-MX";
    private static final String CAMPAIGN_TO_TEST = "Halloween";
    private static final String VERSION_TO_TEST = "Base";
    private static final Enums.KeywordBucketType BUCKET_TO_COPY = Enums.KeywordBucketType.TITLE;
    private KeywordsTab keywordsTab;
    private Company company;
    private String productMasterId;
    private String productToTestUrl;
    private String jwt;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).loginDcApp(USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_PRODUCTS_URL);

        jwt = SecurityAPI.getJwtForInsightsUser(driver);
        productMasterId = ProductVersioningApiService.getProductMasterByUniqueId(UNIQUE_ID_OF_PRODUCT_TO_TEST, jwt)._id;
        importCleanupFile();

        company = CompanyApiService.getCompany(jwt);
        String localeId = company.getLocaleId(LOCALE_TO_TEST);

        productToTestUrl = InsightsConstants.getProductDetailsUrl(productMasterId, localeId, null, null);
        driver.get(productToTestUrl);
        keywordsTab = new PropertiesTab(driver).clickKeywordsTab();
    }

    @AfterClass(alwaysRun = true)
    public void cleanupTests() {
        importCleanupFile();
        quitBrowser();
    }

    @AfterMethod
    public void cleanupTestMethod() throws Exception {
        var currentTitle = driver.getTitle();
        if (!currentTitle.contains("Products Details")) {
            keywordsTab = keywordsTab.navigateToUrl(productToTestUrl, PropertiesTab.class).clickKeywordsTab();
        } else {
            keywordsTab.productsLeftSideFilter.clearAllAndApply();
        }
    }

    @Test(priority = 1, description = "C243968. Search bar works correctly in product details - keyword tab")
    public void CGEN_KeywordsTab_SearchBarWorksCorrectly() {
        keywordsTab.searchForKeyword("");

        int originalPanelsDisplayed = keywordsTab.getNumberOfPanelsDisplayed();

        // Search for keyword and verify only the panels that contain the keyword are displayed
        String searchKeyword = "static";
        keywordsTab = keywordsTab.searchForKeyword(searchKeyword);
        List<Map.Entry<String, String>> versionsAndLocales = keywordsTab.getProductVersionsAndLocaleDisplayed();

        for (Map.Entry<String, String> versionAndLocale : versionsAndLocales) {
            var keywordsInVersion = keywordsTab.getAllKeywordsOfProductVersion(versionAndLocale.getKey(), versionAndLocale.getValue());
            Assert.assertTrue(
                    keywordsInVersion.contains(searchKeyword),
                    "The keyword '" + searchKeyword + "' was not found in product version '" + versionAndLocale.getKey() + "' and locale '" + versionAndLocale.getValue() + "'"
            );
        }

        // Clear the search bar and verify all panels are displayed
        keywordsTab = keywordsTab.searchForKeyword("");
        int panelsDisplayed = keywordsTab.getNumberOfPanelsDisplayed();

        Assert.assertEquals(
                panelsDisplayed,
                originalPanelsDisplayed,
                "The number of panels displayed after clearing the search bar is not the same " +
                        "as the number of panels displayed before searching for a keyword."
        );
    }

    @Test(priority = 2, description = "C243969. Bucket selector filter works correctly in product details - keyword tab")
    public void CGEN_KeywordsTab_BucketSelectorFilterWorksCorrectly() throws InterruptedException {
        List<String> bucketsToShow = new LinkedList<>(asList("Title", "On Page"));

        MultiselectFilterWithSearchInput bucketSelectorFilter = keywordsTab.bucketSelectorFilter;
        bucketSelectorFilter.openFilter(keywordsTab.KEYWORD_BUCKETS_FILTER);
        String optionToTest = bucketsToShow.get(0);
        bucketSelectorFilter.selectOption(optionToTest);

        // Chip is displayed for the option selected
        boolean chipDisplayed = bucketSelectorFilter.isChipDisplayed(optionToTest);
        Assert.assertTrue(chipDisplayed, "The chip for the option '" + optionToTest + "' is not displayed.");

        // Search input returns the expected results
        optionToTest = bucketsToShow.get(1);
        bucketSelectorFilter.searchForOption(optionToTest);
        boolean optionDisplayed = bucketSelectorFilter.isOptionDisplayed(optionToTest);
        Assert.assertTrue(optionDisplayed, "The option '" + optionToTest + "' is not displayed after searching for it.");

        // Selected options are not deselected after clearing search input
        bucketSelectorFilter.selectOption(optionToTest);
        bucketSelectorFilter.searchForOption("");
        List<String> selectedOptions = bucketSelectorFilter.getAllSelectedOptions();
        Assert.assertEquals(selectedOptions, bucketsToShow, "The options selected are not the same as the options expected.");

        // Applying filter displays the correct buckets
        bucketSelectorFilter.applyFilter();

        List<String> bucketsDisplayed = keywordsTab.expandAllPanels().getBucketsDisplayed();
        Assert.assertEquals(
                bucketsDisplayed,
                bucketsToShow,
                "The buckets displayed after filtering for '" + bucketsToShow + "' are not the same as the buckets expected."
        );

        // Deselecting an option removes it from the selected options
        bucketSelectorFilter.openFilter(keywordsTab.KEYWORD_BUCKETS_FILTER);
        bucketSelectorFilter.deselectOption(optionToTest);
        bucketSelectorFilter.applyFilter();

        // The buckets displayed are the same as the buckets expected after removing option
        bucketsDisplayed = keywordsTab.expandAllPanels().getBucketsDisplayed();
        bucketsToShow.remove(optionToTest);
        Assert.assertEquals(
                bucketsDisplayed,
                bucketsToShow,
                "The buckets displayed after deselecting for '" + optionToTest + "' are not the same as the buckets expected."
        );
    }

    @Test(priority = 3, description = "C243995. Save button updates keywords in product details - keyword tab")
    public void CGEN_KeywordsTab_CanAddEditAndDeleteKeywords() throws InterruptedException {
        var keywordToTest = "testKeyword";

        MultiselectFilterWithSearchInput bucketSelectorFilter = keywordsTab.bucketSelectorFilter;
        bucketSelectorFilter.openFilter(keywordsTab.KEYWORD_BUCKETS_FILTER);
        bucketSelectorFilter.deselectAllOptions();
        bucketSelectorFilter.applyFilter();

        keywordsTab.deleteAllKeywordOccurrencesContainsText(keywordToTest);

        // Add keyword
        Enums.KeywordBucketType bucket = Enums.KeywordBucketType.TITLE;
        Enums.KeywordBucketType secondBucket = Enums.KeywordBucketType.ON_PAGE;

        String productVersion = RETAILER_TO_TEST + " " + CAMPAIGN_TO_TEST;
        List<String> keywordsBeforeInVersionToTest = keywordsTab.getKeywordsInBucket(RETAILER_TO_TEST, LOCALE_TO_TEST, bucket);
        List<String> keywordsBeforeInSecondVersionToTest = keywordsTab.getKeywordsInBucket(productVersion, LOCALE_TO_TEST, secondBucket);

        keywordsTab.addKeywordToBucket(RETAILER_TO_TEST, LOCALE_TO_TEST, keywordToTest, bucket);
        keywordsTab.addKeywordToBucket(productVersion, LOCALE_TO_TEST, keywordToTest, secondBucket);
        keywordsTab.clickApplyChangesButtonAndWaitForInvisibility();
        List<String> currentKeywordsInVersionToTest = verifyKeywordWasAddedToBucket(keywordToTest, bucket, keywordsBeforeInVersionToTest, RETAILER_TO_TEST, LOCALE_TO_TEST);
        List<String> currentKeywordsInSecondVersionToTest = verifyKeywordWasAddedToBucket(keywordToTest, secondBucket, keywordsBeforeInSecondVersionToTest, productVersion, LOCALE_TO_TEST);

        // Edit keyword
        String newKeyword = keywordToTest + "-NEW";
        keywordsTab.editKeyword(RETAILER_TO_TEST, LOCALE_TO_TEST, keywordToTest, bucket, newKeyword);
        keywordsTab.editKeyword(productVersion, LOCALE_TO_TEST, keywordToTest, secondBucket, newKeyword);
        keywordsTab.clickApplyChangesButtonAndWaitForInvisibility();
        currentKeywordsInVersionToTest = verifyKeywordWasUpdated(keywordToTest, newKeyword, bucket, currentKeywordsInVersionToTest, RETAILER_TO_TEST, LOCALE_TO_TEST);
        currentKeywordsInSecondVersionToTest = verifyKeywordWasUpdated(keywordToTest, newKeyword, secondBucket, currentKeywordsInSecondVersionToTest, productVersion, LOCALE_TO_TEST);

        // Delete keyword
        keywordsTab.deleteKeywordFromBucket(RETAILER_TO_TEST, LOCALE_TO_TEST, newKeyword, bucket);
        keywordsTab.deleteKeywordFromBucket(productVersion, LOCALE_TO_TEST, newKeyword, secondBucket);
        keywordsTab.clickApplyChangesButtonAndWaitForInvisibility();
        verifyKeywordWasRemovedFromBucket(newKeyword, bucket, currentKeywordsInVersionToTest, RETAILER_TO_TEST, LOCALE_TO_TEST);
        verifyKeywordWasRemovedFromBucket(newKeyword, secondBucket, currentKeywordsInSecondVersionToTest, productVersion, LOCALE_TO_TEST);
    }

    @Test(priority = 4, description = "C244431. Add Multiple pipe separated Keywords in Add Keyword field")
    public void CGEN_KeywordsTab_CanAddPipeSeparatedKeywords() throws InterruptedException {

        Enums.KeywordBucketType bucketToTest = Enums.KeywordBucketType.OPTIONAL;
        String productVersion = RETAILER_TO_TEST + " " + CAMPAIGN_TO_TEST;

        List<String> keywordsBefore = keywordsTab.getKeywordsInBucket(productVersion, LOCALE_TO_TEST, bucketToTest);
        String pipeSeparatedKeyWords = "optional1|optional2|optional3";
        keywordsTab.addKeywordToBucket(productVersion, LOCALE_TO_TEST, pipeSeparatedKeyWords, bucketToTest);

        List<String> keywordsAfter = keywordsTab.getKeywordsInBucket(productVersion, LOCALE_TO_TEST, bucketToTest);
        Assert.assertNotEquals(keywordsBefore, keywordsAfter, "Keywords are missing");
        List<String> expectedKeywords = Arrays.stream(pipeSeparatedKeyWords.split("\\|")).collect(Collectors.toList());

        expectedKeywords.addAll(keywordsBefore);

        Assert.assertEqualsNoOrder(keywordsAfter.toArray(), expectedKeywords.toArray(), "Keywords were not added correctly");
        keywordsTab.clickCancelChangesButton();
    }

    @Test(priority = 5, description = "C244432. Left side filter works correctly - keyword tab")
    public void CGEN_KeywordsTab_LeftSideFilterWorksCorrectly() throws InterruptedException {
        keywordsTab.searchForKeyword("");

        // FILTER BY LOCALE
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, List.of("en-US")));
        performLeftSideFilterTest(filtersAndPathsToApply);

        // FILTER BY RETAILER
        filtersAndPathsToApply.clear();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER, List.of("en-US|Amazon.com")));
        performLeftSideFilterTest(filtersAndPathsToApply);

        // FILTER BY CAMPAIGN
        filtersAndPathsToApply.clear();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.CAMPAIGN, List.of("es-MX|Halloween")));
        performLeftSideFilterTest(filtersAndPathsToApply);

        // FILTER BY RETAILER-CAMPAIGN
        filtersAndPathsToApply.clear();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, List.of("es-MX|Amazon.com|Halloween")));
        performLeftSideFilterTest(filtersAndPathsToApply);

        // FILTER ALL LEVELS
        filtersAndPathsToApply.clear();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, List.of("es-MX")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER, List.of("es-MX|Amazon.com")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.CAMPAIGN, List.of("es-MX|Halloween")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, List.of("es-MX|Amazon.com|Halloween")));
        performLeftSideFilterTest(filtersAndPathsToApply);

        // NEGATIVE CASE SCENARIO
        var urlBefore = driver.getCurrentUrl();
        keywordsTab.productsLeftSideFilter.clearAllAndApply();
        keywordsTab.productsLeftSideFilter.selectFilter(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, List.of("fr-FR|WalmartGrocery.com|Rewarding customers"));
        keywordsTab.productsLeftSideFilter.applyFilter(KeywordsTab.class);
        keywordsTab.getWait(Duration.ofSeconds(2)).until(driver -> !driver.getCurrentUrl().equals(urlBefore));
        keywordsTab.waitForDOMStabilization();

        List<Map.Entry<String, String>> currentVersionsAndLocales = keywordsTab.getProductVersionsAndLocaleDisplayed();
        Assert.assertTrue(currentVersionsAndLocales.isEmpty(), "Some panels are displayed after applying a filter that should not display any panels.");

        var isPageDisplayed = keywordsTab.productsLeftSideFilter.isLeftSideFilterDisplayed();
        Assert.assertTrue(isPageDisplayed, "Left side filter is not displayed after applying a filter that should not display any panels.");
    }

    @Test(priority = 6, description = "C243973. Move keywords to another bucket")
    public void CGEN_KeywordsTab_CanMoveBucketKeywordsToAnotherBucket() throws InterruptedException {
        var bucketToCopy = Enums.KeywordBucketType.RESERVED;
        var targetBucket = Enums.KeywordBucketType.BRANDED;

        keywordsTab.clickCancelChangesButtonIfDisplayed();
        int numberOfHighlightedKeywordsBefore = keywordsTab.getNumberOfHighlightedKeywords();
        List<String> keywordsInSourceBucket = keywordsTab.getKeywordsInBucket(VERSION_TO_TEST, LOCALE_TO_TEST, bucketToCopy);
        List<String> keywordsInTargetBucketBefore = keywordsTab.getKeywordsInBucket(VERSION_TO_TEST, LOCALE_TO_TEST, targetBucket);

        keywordsTab.moveKeywordsToAnotherBucket(VERSION_TO_TEST, LOCALE_TO_TEST, bucketToCopy, targetBucket);

        List<String> keywordsInTargetBucketAfterCopying = keywordsTab.getKeywordsInBucket(VERSION_TO_TEST, LOCALE_TO_TEST, targetBucket);
        verifyKeywordsWereHighlighted(keywordsInSourceBucket, numberOfHighlightedKeywordsBefore);
        verifyKeywordsWereCopied(keywordsInTargetBucketBefore, keywordsInSourceBucket, keywordsInTargetBucketAfterCopying, false);
        verifyKeywordsHaveStrikethrough(VERSION_TO_TEST, LOCALE_TO_TEST, bucketToCopy, keywordsInSourceBucket);

        var keywordToTest = keywordsInSourceBucket.get(0);
        keywordsTab.clickUndoButtonOfKeyword(VERSION_TO_TEST, LOCALE_TO_TEST, keywordToTest, bucketToCopy);
        var keywordHasStrikeThrough = keywordsTab.isKeywordWithStrikeThrough(VERSION_TO_TEST, LOCALE_TO_TEST, keywordToTest, bucketToCopy);
        Assert.assertFalse(keywordHasStrikeThrough, "Keyword " + keywordToTest + "still has strikethrough style");

        keywordsTab.clickApplyChangesButton();
        var expectedMessage = String.format("Version keywords must be unique. Duplicate keywords found: %s %s: \"%s\".", LOCALE_TO_TEST, VERSION_TO_TEST, keywordToTest);

        var infoMessageDisplayed = keywordsTab.isNoteDisplayedWithMessage(Enums.NoteType.INFO, expectedMessage);
        Assert.assertTrue(infoMessageDisplayed, "The info message was not displayed after clicking the apply changes button with duplicate keyword.");

        keywordsTab.deleteKeywordFromBucket(VERSION_TO_TEST, LOCALE_TO_TEST, keywordToTest, targetBucket);
        keywordsTab.clickApplyChangesButtonAndWaitForInvisibility();
        var successMessageDisplayed = keywordsTab.isNoteDisplayed(Enums.NoteType.SUCCESS);
        Assert.assertTrue(successMessageDisplayed, "The success message was not displayed after applying changes.");

        keywordsInTargetBucketAfterCopying = keywordsTab.getKeywordsInBucket(VERSION_TO_TEST, LOCALE_TO_TEST, targetBucket);
        keywordsInSourceBucket.remove(keywordToTest);
        verifyKeywordsWereCopied(keywordsInTargetBucketBefore, keywordsInSourceBucket, keywordsInTargetBucketAfterCopying, true);
    }

    @Test(priority = 7, description = "C243974. Can copy bucket keywords to another version")
    public void CGEN_KeywordsTab_CanCopyBucketKeywordsToAnotherVersion() throws Exception {
        UserFriendlyInstancePath instancePath = new UserFriendlyInstancePath(UNIQUE_ID_OF_PRODUCT_TO_TEST, "en-US", RETAILER_TO_TEST, null);
        String targetVersion = instancePath.getProductVersion();

        List<String> keywordsInSourceBucket = keywordsTab.getKeywordsInBucket(VERSION_TO_TEST, LOCALE_TO_TEST, BUCKET_TO_COPY);

        String localeIdOfTargetVersion = company.getLocaleId(instancePath.localeName);
        String targetProductUrl = InsightsConstants.getProductDetailsUrl(productMasterId) + "?l=" + localeIdOfTargetVersion + "&r=null&c=null";
        keywordsTab = keywordsTab.navigateToUrl(targetProductUrl, PropertiesTab.class).clickKeywordsTab();
        List<String> keywordsInTargetBucketBefore = keywordsTab.getKeywordsInBucket(targetVersion, instancePath.localeName, BUCKET_TO_COPY);

        keywordsTab.navigateBack();
        keywordsTab.clickKeywordsTab();

        keywordsTab.copyKeywordsToAnotherVersion(VERSION_TO_TEST, LOCALE_TO_TEST, BUCKET_TO_COPY, targetVersion, instancePath.localeName);
        keywordsTab.clickApplyChangesButtonAndWaitForInvisibility();

        keywordsTab = keywordsTab.navigateToUrl(targetProductUrl, PropertiesTab.class).clickKeywordsTab();
        List<String> keywordsInTargetBucketAfter = keywordsTab.getKeywordsInBucket(targetVersion, instancePath.localeName, BUCKET_TO_COPY);
        verifyKeywordsWereCopied(keywordsInTargetBucketBefore, keywordsInSourceBucket, keywordsInTargetBucketAfter, true);

        keywordsTab = keywordsTab.navigateToUrl(productToTestUrl, PropertiesTab.class).clickKeywordsTab();
    }

    @Test(priority = 8, description = "C243975. Can copy bucket keywords to another product")
    public void CGEN_KeywordsTab_CanCopyBucketKeywordsToAnotherProduct() throws Exception {
        UserFriendlyInstancePath pathOfTargetProduct = new UserFriendlyInstancePath(PREFIX_PRODUCTS_TO_TEST + "002", "fr-FR", null, null);
        String targetVersion = pathOfTargetProduct.getProductVersion();

        SoftAssert softAssert = new SoftAssert();

        List<String> keywordsInSourceBucket = keywordsTab.getKeywordsInBucket(VERSION_TO_TEST, LOCALE_TO_TEST, BUCKET_TO_COPY);

        String localeIdOfTargetVersion = company.getLocaleId(pathOfTargetProduct.localeName);
        String productIdOfTargetProduct = ProductVersioningApiService.getProductMasterByUniqueId(pathOfTargetProduct.productIdentifier, jwt)._id;

        // Getting keywords in target bucket of target product before copying keywords
        String targetProductUrl = InsightsConstants.getProductDetailsUrl(productIdOfTargetProduct) + "?l=" + localeIdOfTargetVersion + "&r=null&c=null";
        keywordsTab = keywordsTab.navigateToUrl(targetProductUrl, PropertiesTab.class).clickKeywordsTab();
        List<String> keywordsInTargetBucketBefore = keywordsTab.getKeywordsInBucket(targetVersion, pathOfTargetProduct.localeName, BUCKET_TO_COPY);
        
        keywordsTab = keywordsTab.navigateToUrl(productToTestUrl, PropertiesTab.class).clickKeywordsTab();

        // Starting process to copy keywords to another product
        BulkEditSelectProductsPage bulkEditSelectProductsPage = keywordsTab.copyKeywordsToOtherProducts(VERSION_TO_TEST, LOCALE_TO_TEST, BUCKET_TO_COPY);
        Thread.sleep(1000);
        bulkEditSelectProductsPage.removeAllAdvancedFilters();
        int productCountBeforeSearch = bulkEditSelectProductsPage.getNumberOfProductsDisplayedNextToSearchInput();
        bulkEditSelectProductsPage.searchByProductIdentifier(pathOfTargetProduct.productIdentifier);
        bulkEditSelectProductsPage.waitForNumberOfProductsToChange(productCountBeforeSearch);
        bulkEditSelectProductsPage.selectProduct(pathOfTargetProduct);
        BulkAddDeleteKeywordsModal bulkAddDeleteKeywordsModal = bulkEditSelectProductsPage.clickContinueButton();

        // Verifying that only one keyword bucket section is displayed and that the keywords in the modal are the same as the keywords in the source bucket
        int bucketSectionsDisplayed = bulkAddDeleteKeywordsModal.getNumberOfKeywordBucketSections();
        softAssert.assertEquals(bucketSectionsDisplayed, 1, "Only one keyword bucket section should be displayed");
        List<String> keywordsInBucket = bulkAddDeleteKeywordsModal.getKeywordsInBucket(BUCKET_TO_COPY.getBucketTypeForUI());
        softAssert.assertEquals(keywordsInBucket, keywordsInSourceBucket, "Keywords in modal are not the same as the keywords in pdp page");

        // Verifying that correct cell is highlighted and that keywords were copied to target bucket
        BulkEditKeywordsPage bulkEditKeywordsPage = bulkAddDeleteKeywordsModal.clickSaveAndExitButton(BulkEditKeywordsPage.class);
        int highlightedCells = bulkEditKeywordsPage.getNumberOfHighlightedCells();
        softAssert.assertEquals(highlightedCells, 1, "Only one cell should be highlighted");
        String instanceUniqueIdOfTargetProduct = bulkEditKeywordsPage.getInstanceUniqueIdsDisplayed().get(0);
        boolean correctCellIsHighlighted = bulkEditKeywordsPage.isCellHighlighted(instanceUniqueIdOfTargetProduct, BUCKET_TO_COPY.getBucketTypeForUI());
        softAssert.assertTrue(correctCellIsHighlighted, BUCKET_TO_COPY + " cell is not highlighted");

        List<String> keywordsInTargetBucketAfter = bulkEditKeywordsPage.getKeywordSetDisplayed(instanceUniqueIdOfTargetProduct).title;
        verifyKeywordsWereCopied(keywordsInTargetBucketBefore, keywordsInSourceBucket, keywordsInTargetBucketAfter, false);

        // Verifying that keywords were copied to target product (PDP page)
        keywordsTab = bulkEditKeywordsPage.clickSubmitButton(PropertiesTab.class)
                .navigateToUrl(targetProductUrl, PropertiesTab.class)
                .clickKeywordsTab();
        keywordsInTargetBucketAfter = keywordsTab.getKeywordsInBucket(targetVersion, pathOfTargetProduct.localeName, BUCKET_TO_COPY);
        verifyKeywordsWereCopied(keywordsInTargetBucketBefore, keywordsInSourceBucket, keywordsInTargetBucketAfter, true);

        keywordsTab = keywordsTab.navigateToUrl(productToTestUrl, PropertiesTab.class).clickKeywordsTab();
        softAssert.assertAll();
    }

    @Test(priority = 9, description = "C245078. Can copy entire version keywords to another version")
    public void CGEN_KeywordsTab_CanCopyEntireVersionKeywordsToAnotherVersion() throws Exception {
        Map<Enums.KeywordBucketType, List<String>> allKeywordsInSourceVersion = keywordsTab.getAllKeywordsOfVersionGroupedByBucket(VERSION_TO_TEST, LOCALE_TO_TEST);

        var instancePathOfTarget = new UserFriendlyInstancePath(UNIQUE_ID_OF_PRODUCT_TO_TEST, "en-US", RETAILER_TO_TEST, null);
        var targetVersion = instancePathOfTarget.getProductVersion();

        var localeIdOfTargetVersion = company.getLocaleId(instancePathOfTarget.localeName);

        var targetProductUrl = InsightsConstants.getProductDetailsUrl(productMasterId) + "?l=" + localeIdOfTargetVersion + "&r=null&c=null";
        keywordsTab = keywordsTab.navigateToUrl(targetProductUrl, PropertiesTab.class).clickKeywordsTab();

        var applyChanges = false;
        for (Enums.KeywordBucketType bucket : allKeywordsInSourceVersion.keySet()) {
            List<String> keywordsInSourceBucket = allKeywordsInSourceVersion.get(bucket);
            var keywordsInTargetBucket = keywordsTab.getKeywordsInBucket(targetVersion, instancePathOfTarget.localeName, bucket);
            for (String keyword : keywordsInSourceBucket) {
                if (keywordsInTargetBucket.contains(keyword)) {
                    keywordsTab.deleteKeywordFromBucket(targetVersion, instancePathOfTarget.localeName, keyword, bucket);
                    applyChanges = true;
                }
            }
        }

        if (applyChanges) {
            keywordsTab.clickApplyChangesButtonAndWaitForInvisibility();
        }

        Map<Enums.KeywordBucketType, List<String>> allKeywordsInTargetVersionBefore = keywordsTab.getAllKeywordsOfVersionGroupedByBucket(targetVersion, instancePathOfTarget.localeName);

        keywordsTab = keywordsTab.navigateToUrl(productToTestUrl, PropertiesTab.class).clickKeywordsTab();

        keywordsTab.copyEntireVersionKeywordsToAnotherVersion(VERSION_TO_TEST, LOCALE_TO_TEST, targetVersion, instancePathOfTarget.localeName);
        keywordsTab.clickApplyChangesButtonAndWaitForInvisibility();

        keywordsTab = keywordsTab.navigateToUrl(targetProductUrl, PropertiesTab.class).clickKeywordsTab();

        Map<Enums.KeywordBucketType, List<String>> allKeywordsInTargetVersionAfter = keywordsTab.getAllKeywordsOfVersionGroupedByBucket(targetVersion, instancePathOfTarget.localeName);

        for (Enums.KeywordBucketType bucket : allKeywordsInSourceVersion.keySet()) {
            var keywordsInSourceBucket = allKeywordsInSourceVersion.get(bucket);
            var keywordsInTargetBucketAfter = allKeywordsInTargetVersionAfter.get(bucket);
            verifyKeywordsWereCopied(allKeywordsInTargetVersionBefore.get(bucket), keywordsInSourceBucket, keywordsInTargetBucketAfter, true);
        }

        keywordsTab = keywordsTab.navigateToUrl(productToTestUrl, PropertiesTab.class).clickKeywordsTab();
    }

    @Test(priority = 10, description = "C245079. Can copy entire version keywords to another product. This test DOES NOT apply changes to the target product because this is tested in CGEN_KeywordsTab_CanCopyKeywordsToAnotherProduct")
    public void CGEN_KeywordsTab_CanCopyEntireVersionKeywordsToAnotherProduct() throws Exception {
        Map<Enums.KeywordBucketType, List<String>> expectedKeywordsFromSourceVersion = keywordsTab.getAllKeywordsOfVersionGroupedByBucket(VERSION_TO_TEST, LOCALE_TO_TEST);
        expectedKeywordsFromSourceVersion.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        var bulkEditSelectProductsPage = keywordsTab.copyEntireVersionKeywordsToOtherProduct(VERSION_TO_TEST, LOCALE_TO_TEST);
        var pathOfTargetProduct = new UserFriendlyInstancePath(PREFIX_PRODUCTS_TO_TEST + "002", "fr-FR", null, null);

        bulkEditSelectProductsPage.removeAllAdvancedFilters();
        var productCountBeforeSearch = bulkEditSelectProductsPage.getNumberOfProductsDisplayedNextToSearchInput();
        bulkEditSelectProductsPage.searchByProductIdentifier(pathOfTargetProduct.productIdentifier);
        bulkEditSelectProductsPage.waitForNumberOfProductsToChange(productCountBeforeSearch);
        bulkEditSelectProductsPage.selectProduct(pathOfTargetProduct);
        var bulkModal = bulkEditSelectProductsPage.clickContinueButton();

        // Verifying only buckets with keywords are displayed in the modal
        var bucketSectionsDisplayed = bulkModal.getNumberOfKeywordBucketSections();
        Assert.assertEquals(bucketSectionsDisplayed, expectedKeywordsFromSourceVersion.size(), "The number of keyword bucket sections displayed is not the same as the number of keyword bucket sections expected.");

        for (var bucket : expectedKeywordsFromSourceVersion.keySet()) {
            var keywordsInBucket = bulkModal.getKeywordsInBucket(bucket.getBucketTypeForUI());
            Assert.assertEquals(keywordsInBucket, expectedKeywordsFromSourceVersion.get(bucket), "Keywords of bucket " + bucket + " in modal are not the same as the keywords in pdp page");
        }

        keywordsTab = bulkModal.clickCancelButton(BulkEditKeywordsPage.class).clickCancelButton(PropertiesTab.class).clickKeywordsTab();
    }

    @Test(priority = 11, description = "C258656. Cannot have duplicate keyword in same product version")
    public void CGEN_KeywordsTab_CannotHaveDuplicateKeywordsAcrossBuckets() throws InterruptedException {
        var bucketToCopy = Enums.KeywordBucketType.RESERVED;
        var targetBucket = Enums.KeywordBucketType.BRANDED;

        keywordsTab.clickCancelChangesButtonIfDisplayed();
        var keywordsInSourceBucketToCopy = keywordsTab.getKeywordsInBucket(VERSION_TO_TEST, LOCALE_TO_TEST, bucketToCopy).get(0);

        keywordsTab.addKeywordToBucket(VERSION_TO_TEST, LOCALE_TO_TEST, keywordsInSourceBucketToCopy.toUpperCase(), targetBucket);

        keywordsTab.clickApplyChangesButton();
        var expectedMessage = String.format("Version keywords must be unique. Duplicate keywords found: %s %s: \"%s\".", LOCALE_TO_TEST, VERSION_TO_TEST, keywordsInSourceBucketToCopy.toLowerCase());

        var infoMessageDisplayed = keywordsTab.isNoteDisplayedWithMessage(Enums.NoteType.INFO, expectedMessage);
        Assert.assertTrue(infoMessageDisplayed, "The info message was not displayed after clicking the apply changes button with duplicate keyword.");
    }

    private List<String> verifyKeywordWasAddedToBucket(String keyword, Enums.KeywordBucketType bucket, List<String> keywordsBefore, String productVersion, String locale) {
        List<String> keywordsAfter = keywordsTab.getKeywordsInBucket(productVersion, locale, bucket);
        Assert.assertNotEquals(
                keywordsAfter,
                keywordsBefore,
                "The keywords in the bucket '" + bucket + "' of product version '" + productVersion + "' and locale '" + locale + "' " +
                        "are the same as the keywords before adding a new keyword."
        );

        Assert.assertTrue(
                keywordsAfter.contains(keyword),
                "The keyword '" + keyword +
                        "' was not added to the bucket '" + bucket + "' of product version '" + productVersion + "' and locale '" + locale + "'."
        );

        return keywordsAfter;
    }

    private List<String> verifyKeywordWasUpdated(String oldKeyword, String newKeyword, Enums.KeywordBucketType bucket, List<String> keywordsBefore, String productVersion, String locale) {
        List<String> keywordsAfter = keywordsTab.getKeywordsInBucket(productVersion, locale, bucket);
        Assert.assertNotEquals(
                keywordsAfter,
                keywordsBefore,
                "The keywords in the bucket '" + bucket + "' of product version '" + productVersion + "' and locale '" + locale + "' " +
                        "are the same as the keywords before updating keyword."
        );

        Assert.assertFalse(
                keywordsAfter.contains(oldKeyword),
                "The keyword '" + oldKeyword +
                        "' was not updated in bucket '" + bucket + "' of product version '" + productVersion + "' and locale '" + locale + "'."
        );

        Assert.assertTrue(
                keywordsAfter.contains(newKeyword),
                "The keyword '" + newKeyword +
                        "' was not updated in bucket '" + bucket + "' of product version '" + productVersion + "' and locale '" + locale + "'."
        );

        return keywordsAfter;
    }

    private void verifyKeywordWasRemovedFromBucket(String keyword, Enums.KeywordBucketType bucket, List<String> keywordsBefore, String productVersion, String locale) {
        List<String> keywordsAfter = keywordsTab.getKeywordsInBucket(productVersion, locale, bucket);
        Assert.assertNotEquals(
                keywordsAfter,
                keywordsBefore,
                "The keywords in the bucket '" + bucket + "' of product version '" + productVersion + "' and locale '" + locale + "' " +
                        "are the same as the keywords before deleting keyword."
        );

        Assert.assertFalse(
                keywordsAfter.contains(keyword),
                "The keyword '" + keyword +
                        "' was not deleted from bucket '" + bucket + "' of product version '" + productVersion + "' and locale '" + locale + "'."
        );
    }

    private void performLeftSideFilterTest(List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPaths) throws InterruptedException {
        keywordsTab.productsLeftSideFilter.clearAllAndApply();
        keywordsTab.waitForDOMStabilization();
        Thread.sleep(1000);

        List<Map.Entry<String, String>> expectedVersionsAndLocales = keywordsTab.getProductVersionsAndLocaleDisplayed();

        LOGGER.info("expectedVersionsAndLocales: " + expectedVersionsAndLocales);

        for (var filterToApply : filtersAndPaths) {
            keywordsTab.productsLeftSideFilter.selectFilter(filterToApply.getKey(), filterToApply.getValue());
        }

        var urlBeforeApplyingFilters = driver.getCurrentUrl();
        keywordsTab.productsLeftSideFilter.applyFilter(KeywordsTab.class);
        keywordsTab.getWait(Duration.ofSeconds(2)).until(driver -> !driver.getCurrentUrl().equals(urlBeforeApplyingFilters));

        expectedVersionsAndLocales.removeIf(versionAndLocale -> {
            for (var filterToApply : filtersAndPaths) {
                var filterType = filterToApply.getKey();
                var paths = filterToApply.getValue();
                for (var path : paths) {
                    var pathParts = path.split("\\|");
                    String locale = pathParts[0];
                    String version;

                    if (filterType == ProductsLeftSideFilter.FilterType.RETAILER || filterType == ProductsLeftSideFilter.FilterType.CAMPAIGN) {
                        version = pathParts[1];
                    } else if (filterType == ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN) {
                        version = pathParts[1] + " " + pathParts[2];
                    } else {
                        version = "Base";
                    }

                    if (versionAndLocale.getKey().equals(version) && versionAndLocale.getValue().equals(locale)) {
                        return false;
                    }
                }
            }
            return true;
        });

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(
                () ->
                {
                    List<Map.Entry<String, String>> currentVersionsAndLocales = keywordsTab.getProductVersionsAndLocaleDisplayed();
                    Assert.assertEquals(
                            currentVersionsAndLocales,
                            expectedVersionsAndLocales,
                            "The panels displayed after filtering are not the same as the panels expected." +
                                    "\nExpected: " + expectedVersionsAndLocales +
                                    "\nActual: " + currentVersionsAndLocales
                    );
                }
        );
    }

    private void verifyKeywordsWereHighlighted(List<String> keywordsInSourceBucket, int numberOfHighlightedKeywordsBefore) {
        int numberOfHighlightedKeywordsAfter = keywordsTab.getNumberOfHighlightedKeywords();
        Assert.assertEquals(
                numberOfHighlightedKeywordsAfter,
                numberOfHighlightedKeywordsBefore + keywordsInSourceBucket.size(),
                "No. of highlighted keywords doesn't match the number of keywords copied to the target bucket + the number of highlighted keywords before moving"
        );
    }

    private void verifyKeywordsWereCopied(List<String> keywordsInTargetBucketBefore, List<String> keywordsFromSourceBucket, List<String> keywordsInTargetBucketAfter, boolean checkDuplicates) {
        List<String> expectedKeywordsInTargetBucket = new ArrayList<>(keywordsInTargetBucketBefore);
        expectedKeywordsInTargetBucket.addAll(keywordsFromSourceBucket);

        if (checkDuplicates) {
            expectedKeywordsInTargetBucket = expectedKeywordsInTargetBucket.stream().distinct().collect(Collectors.toList());
        }

        Assert.assertEquals(
                keywordsInTargetBucketAfter,
                expectedKeywordsInTargetBucket,
                "Keywords from source bucket were not copied to target bucket"
        );
    }

    private void verifyKeywordsHaveStrikethrough(String productVersion, String locale, Enums.KeywordBucketType bucketName, List<String> keywordsInSourceBucket) {
        for (var keyword : keywordsInSourceBucket) {
            var keywordHasStrikeThrough = keywordsTab.isKeywordWithStrikeThrough(productVersion, locale, keyword, bucketName);
            Assert.assertTrue(keywordHasStrikeThrough, "Keyword " + keyword + "doesn't have strikethrough style");
        }
    }

    private void importCleanupFile() {
        try {
            var cleanupImportFile = "https://os-media-service.s3.amazonaws.com/qa/imports/UIKeywordTestsCleanup.xlsx";
            SharedRequests.importProductKeywords(cleanupImportFile, Enums.KeywordFlag.REPLACE, jwt);
        } catch (Exception ignored) {
        }
    }
}