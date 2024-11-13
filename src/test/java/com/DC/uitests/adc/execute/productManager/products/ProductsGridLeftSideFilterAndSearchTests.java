package com.DC.uitests.adc.execute.productManager.products;

import com.DC.constants.InsightsConstants;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.pageobjects.filters.ProductsLeftSideFilter;
import com.DC.testcases.BaseClass;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.*;
import java.util.stream.Collectors;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class ProductsGridLeftSideFilterAndSearchTests extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private ProductsPage productsPage;

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).loginDcApp(USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_PRODUCTS_URL);
        productsPage = new ProductsPage(driver);
        productsPage.paginator.selectNumberOfItemsPerPage(25);
        productsPage.closeContentSuggestionsBanner();
    }

    @AfterMethod
    public void cleanupTestMethod() throws Exception {
        productsPage.recoverPageIfBlankPageIsDisplayed(InsightsConstants.INSIGHTS_PRODUCTS_URL, productsPage);
        productsPage.leftSideFilter.clearAll();
        productsPage = productsPage.leftSideFilter.applyFilter(ProductsPage.class);
    }

    @AfterClass(alwaysRun = true)
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "C244883. Verify default status of left side filter.")
    public void CSCAT_ProductsPage_LeftSideFilterDefaultStatusIsCorrect() {
        var leftSideFilterDisplayed = productsPage.leftSideFilter.isLeftSideFilterDisplayed();
        Assert.assertTrue(leftSideFilterDisplayed, "Left side filter should be displayed by default");

        SoftAssert softAssert = new SoftAssert();
        verifyFilterIsEmptyByDefault(ProductsLeftSideFilter.FilterType.BASE, softAssert);
        verifyFilterIsEmptyByDefault(ProductsLeftSideFilter.FilterType.RETAILER, softAssert);
        verifyFilterIsEmptyByDefault(ProductsLeftSideFilter.FilterType.CAMPAIGN, softAssert);
        verifyFilterIsEmptyByDefault(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, softAssert);

        softAssert.assertAll();
    }

    @Test(priority = 2, description = "C244884. Verify left side filter can be opened and closed.")
    public void CSCAT_ProductsPage_CanOpenAndCloseLeftSideFilter() throws InterruptedException {
        productsPage.leftSideFilter.closeFilter();
        var leftSideFilterDisplayed = productsPage.leftSideFilter.isLeftSideFilterDisplayed();
        Assert.assertFalse(leftSideFilterDisplayed, "Left side filter didn't close");

        productsPage.leftSideFilter.openFilter();
        leftSideFilterDisplayed = productsPage.leftSideFilter.isLeftSideFilterDisplayed();
        Assert.assertTrue(leftSideFilterDisplayed, "Left side filter didn't open");
    }

    @Test(priority = 3, description = "C244885. Verify user can apply left side filter to show only base versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_OnlyBases() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, List.of("es-MX", "fr-FR")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 4, description = "C244886. Verify user can apply left side filter to show only retailer versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_OnlyRetailers() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER, List.of("es-MX|Amazon.com", "fr-FR|Walmart.com")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 5, description = "C244887. Verify user can apply left side filter to show only campaign versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_OnlyCampaigns() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.CAMPAIGN, List.of("en-US|Christmas", "es-MX|Halloween")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 6, description = "C244888. Verify user can apply left side filter to show only retailer-campaign versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_OnlyRetailerCampaigns() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, List.of("en-US|Amazon.com|Halloween", "fr-FR|Amazon.com|Christmas")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 7, description = "C244889. Verify user can apply left side filter to show base and retailer versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_BasesAndRetailers() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, List.of("en-US")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER, List.of("es-MX|Amazon.com")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 8, description = "C244890. Verify user can apply left side filter to show base and campaign versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_BasesAndCampaigns() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, List.of("en-US")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.CAMPAIGN, List.of("es-MX|Halloween")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 9, description = "C244891. Verify user can apply left side filter to show base and retailer-campaign versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_BasesAndRetailerCampaigns() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, List.of("es-MX")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, List.of("en-US|Amazon.com|Halloween", "fr-FR|Amazon.com|Christmas")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 10, description = "C244892. Verify user can apply left side filter to show retailer and campaign versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_RetailersAndCampaigns() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER, List.of("es-MX|Walmart.com")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.CAMPAIGN, List.of("en-US|Halloween", "fr-FR|Christmas")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 11, description = "C244893. Verify user can apply left side filter to show retailer and retailer-campaign versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_RetailersAndRetailerCampaigns() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER, List.of("fr-FR|Walmart.com")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, List.of("en-US|Amazon.com|Halloween")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 12, description = "C244894. Verify user can apply left side filter to show campaign and retailer-campaign versions.")
    public void CSCAT_ProductsPage_LeftSideFilter_CampaignsAndRetailerCampaigns() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.CAMPAIGN, List.of("es-MX|Halloween")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, List.of("fr-FR|Amazon.com|Christmas", "es-MX|Amazon.com|Halloween")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 13, description = "C244895. Verify user can apply left side filter to show versions of all levels.")
    public void CSCAT_ProductsPage_LeftSideFilter_AllLevels() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, List.of("en-US")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER, List.of("es-MX|Walmart.com")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.CAMPAIGN, List.of("es-MX|Halloween")));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, List.of("fr-FR|Amazon.com|Christmas")));
        performLeftSideFilterTest(filtersAndPathsToApply);
    }

    @Test(priority = 14, description = "C244896. Verify no data is displayed if user applies a non existent combination (0 products returned).")
    public void CSCAT_ProductsPage_LeftSideFilter_NonExistentCombination() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, List.of("fr-FR|WalmartGrocery.com|Rewarding customers")));

        productsPage.leftSideFilter.openFilter();

        for (var filterToApply : filtersAndPathsToApply) {
            productsPage.leftSideFilter.selectFilter(filterToApply.getKey(), filterToApply.getValue());
        }

        productsPage = productsPage.leftSideFilter.applyFilter(ProductsPage.class);

        var noProductsDisplayed = productsPage.getProductsDisplayed().isEmpty();
        Assert.assertTrue(noProductsDisplayed, "Products should not be displayed when a non-existent combination of filters is applied");
    }

    @Test(priority = 15, description = "C244897. Verify search inputs and back to previous menu button work inside left side filter dropdowns.")
    public void CSCAT_ProductsPage_LeftSideFilter_CanSearchInsideDropdowns() throws InterruptedException {
        productsPage.leftSideFilter.openFilter();
        verifySearchInputsWork(ProductsLeftSideFilter.FilterType.BASE, "en-US");
        verifySearchInputsWork(ProductsLeftSideFilter.FilterType.RETAILER, "en-US|Amazon.com");
        verifySearchInputsWork(ProductsLeftSideFilter.FilterType.CAMPAIGN, "en-US|Halloween");
        verifySearchInputsWork(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, "en-US|Amazon.com|Halloween");
    }

    @Test(priority = 16, description = "C244898. Verify user can remove filter chips from the left side filter dropdowns.")
    public void CSCAT_ProductsPage_LeftSideFilter_CanRemoveFilterChips() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, new ArrayList<>(List.of("es-MX", "en-US"))));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER, new ArrayList<>(List.of("es-MX|Walmart.com", "en-US|Amazon.com"))));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.CAMPAIGN, new ArrayList<>(List.of("es-MX|Halloween", "en-US|Christmas"))));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, new ArrayList<>((List.of("fr-FR|Amazon.com|Christmas", "es-MX|Amazon.com|Halloween")))));

        productsPage = productsPage.applyVersionsFilter(filtersAndPathsToApply, ProductsPage.class);

        for (var filterToApply : filtersAndPathsToApply) {
            productsPage.leftSideFilter.removeFilterChip(filterToApply.getValue().get(0));
            filterToApply.getValue().remove(0);
        }

        productsPage = productsPage.leftSideFilter.applyFilter(ProductsPage.class);

        var fullVersionsDisplayed = getFullVersionsDisplayed();
        var expectedVersions = filtersAndPathsToApply.stream().map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        var productsHaveCorrectVersion = expectedVersions.containsAll(fullVersionsDisplayed);
        Assert.assertTrue(productsHaveCorrectVersion, "Not all products displayed match the expected versions.\nAccepted versions: " + expectedVersions + "\nVersions displayed: " + fullVersionsDisplayed);
    }

    @Test(priority = 17, description = "C243823. Verify that if the user applies filters (left side) filters are kept after change page in pagination")
    public void CSCAT_ProductsPage_LeftSideFilter_FiltersAreKeptAfterChangingPage() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, new ArrayList<>(List.of("es-MX"))));

        SoftAssert softAssert = new SoftAssert();
        productsPage.paginator.selectNumberOfItemsPerPage(5);
        productsPage.paginator.goToSpecificPage(4);

        productsPage = productsPage.applyVersionsFilter(filtersAndPathsToApply, ProductsPage.class);

        var activePage = productsPage.paginator.getActivePageNumber();
        Assert.assertEquals(activePage, 1, "Active page should be 1 after applying filters");

        var numberDisplayedNextToSearchInput = productsPage.getNumberOfProductsDisplayedNextToSearchInput();

        if (numberDisplayedNextToSearchInput > 5) {
            productsPage.paginator.goToNextPage();

            for (var filterToApply : filtersAndPathsToApply) {
                var filterValues = productsPage.leftSideFilter.getFilterValues(filterToApply.getKey());
                softAssert.assertEquals(filterValues, filterToApply.getValue(), "Filter values should be the same after going to the next page");
            }

            var fullVersionsDisplayed = getFullVersionsDisplayed();
            var expectedVersions = filtersAndPathsToApply.stream().map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
            var productsHaveCorrectVersion = expectedVersions.containsAll(fullVersionsDisplayed);
            softAssert.assertTrue(productsHaveCorrectVersion,
                    "Not all products in the next page match the expected versions.\nAccepted versions: " + expectedVersions + "\nVersions displayed: " + fullVersionsDisplayed
            );
        }
        softAssert.assertAll();
    }

    @Test(priority = 18, description = "C243824. Verify that if the user applies filters (left side) filters are kept after change page size (amount of results per page)")
    public void CSCAT_ProductsPage_LeftSideFilter_FiltersAreKeptAfterChangingPageSize() throws InterruptedException {
        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPathsToApply = new ArrayList<>();
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, new ArrayList<>(List.of("es-MX", "en-US"))));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER, new ArrayList<>(List.of("es-MX|Walmart.com", "en-US|Amazon.com"))));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.CAMPAIGN, new ArrayList<>(List.of("es-MX|Halloween", "en-US|Christmas"))));
        filtersAndPathsToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.RETAILER_CAMPAIGN, new ArrayList<>((List.of("fr-FR|Amazon.com|Christmas", "es-MX|Amazon.com|Halloween")))));

        SoftAssert softAssert = new SoftAssert();
        productsPage.paginator.selectNumberOfItemsPerPage(5);
        productsPage = productsPage.applyVersionsFilter(filtersAndPathsToApply, ProductsPage.class);

        var fullVersionsDisplayed = getFullVersionsDisplayed();

        productsPage.paginator.selectNumberOfItemsPerPage(25);

        for (var filterToApply : filtersAndPathsToApply) {
            var filterValues = productsPage.leftSideFilter.getFilterValues(filterToApply.getKey());
            softAssert.assertEquals(filterValues, filterToApply.getValue(), "Filter values should be the same after changing the page size");
        }

        fullVersionsDisplayed = getFullVersionsDisplayed();
        var expectedVersions = filtersAndPathsToApply.stream().map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        var productsHaveCorrectVersion = expectedVersions.containsAll(fullVersionsDisplayed);
        softAssert.assertTrue(productsHaveCorrectVersion,
                "Not all products match the expected versions after changing page size.\nAccepted versions: " + expectedVersions + "\nVersions displayed: " + fullVersionsDisplayed
        );

        softAssert.assertAll();
    }

    @Test(priority = 31, description = "C244899. Verify user can search for a product and then apply left side filter")
    public void CSCAT_ProductsPage_LeftSideFilter_CanApplyVersionFilterAfterSearchingForProducts() throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();

        var numberOfProductsBeforeSearch = productsPage.getNumberOfProductsDisplayedNextToSearchInput();

        var searchTerm = "QA-BULK-EDIT-";
        productsPage.searchByProductIdentifier(searchTerm);
        productsPage.waitForNumberOfProductsToChange(numberOfProductsBeforeSearch);

        var productsDisplayed = productsPage.getProductsDisplayed();
        var productsHaveCorrectProductIdentifier = productsDisplayed.stream().allMatch(product -> product.productIdentifier.contains(searchTerm));
        softAssert.assertTrue(productsHaveCorrectProductIdentifier, "Not all products displayed contain the search term");

        List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filterToApply = new ArrayList<>();
        filterToApply.add(new AbstractMap.SimpleEntry<>(ProductsLeftSideFilter.FilterType.BASE, List.of("es-MX")));

        productsPage = productsPage.applyVersionsFilter(filterToApply, ProductsPage.class);

        var filterChips = productsPage.getNumberOfAdvancedFiltersApplied();
        softAssert.assertEquals(filterChips, 1, "Number of advanced filters changed after applying a version filter");

        var fullVersionsDisplayed = getFullVersionsDisplayed();
        var expectedVersions = filterToApply.stream().map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        var productsHaveCorrectVersion = expectedVersions.containsAll(fullVersionsDisplayed);
        softAssert.assertTrue(productsHaveCorrectVersion,
                "Not all products match the expected versions after applying a version filter with an advanced filter applied"
        );

        productsPage.removeAllAdvancedFilters();
        productsPage.waitForNumberOfProductsToChange(productsDisplayed.size());

        var filterValues = productsPage.leftSideFilter.getFilterValues(ProductsLeftSideFilter.FilterType.BASE);
        softAssert.assertEquals(filterValues, filterToApply.get(0).getValue(), "Filter values should be the same after removing the advanced filter");

        softAssert.assertAll();
    }

    private void performLeftSideFilterTest(List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPaths) throws InterruptedException {
        SoftAssert softAssert = new SoftAssert();

        productsPage = productsPage.applyVersionsFilter(filtersAndPaths, ProductsPage.class);

        var fullVersionsDisplayed = getFullVersionsDisplayed();
        var expectedVersions = filtersAndPaths.stream().map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());

        var productsHaveCorrectVersion = expectedVersions.containsAll(fullVersionsDisplayed);
        softAssert.assertTrue(productsHaveCorrectVersion, "Not all products displayed match the expected versions.\nAccepted versions: " + expectedVersions + "\nVersions displayed: " + fullVersionsDisplayed);

        softAssert.assertAll();
    }

    private ArrayList<String> getFullVersionsDisplayed() {
        var productsDisplayed = productsPage.getProductsDisplayed();
        var fullVersionsDisplayed = new ArrayList<String>();
        for (var product : productsDisplayed) {
            var filterChipFromProduct = product.version.replaceFirst("\\.com ", ".com|");
            if (!product.version.equals("Base")) {
                fullVersionsDisplayed.add(product.localeName + "|" + filterChipFromProduct);
            } else {
                fullVersionsDisplayed.add(product.localeName);
            }
        }
        return fullVersionsDisplayed;
    }

    private void verifyFilterIsEmptyByDefault(ProductsLeftSideFilter.FilterType filterType, SoftAssert softAssert) {
        var filterValue = productsPage.leftSideFilter.getFilterValues(ProductsLeftSideFilter.FilterType.BASE);
        softAssert.assertTrue(filterValue.isEmpty(), filterType + " filter values should be empty by default");
    }

    private void verifySearchInputsWork(ProductsLeftSideFilter.FilterType filterType, String path) throws InterruptedException {
        productsPage.leftSideFilter.openDropdownMenu(filterType);

        if (filterType.equals(ProductsLeftSideFilter.FilterType.BASE)) {
            productsPage.leftSideFilter.searchInsideDropdown("invalid");
            var dropdownItems = productsPage.leftSideFilter.getDropdownOptions();
            Assert.assertEquals(dropdownItems.size(), 1, "The only dropdown item should be the 'Select All' option when searching for an invalid term in the base dropdown");
            Assert.assertEquals(dropdownItems.get(0), "Select All", "The only dropdown item should be the 'Select All' option when searching for an invalid term in the base dropdown");

            productsPage.leftSideFilter.searchInsideDropdown(path);
            dropdownItems = productsPage.leftSideFilter.getDropdownOptions();
            Assert.assertFalse(dropdownItems.isEmpty(), "Dropdown items should not be empty when searching for a valid term");
            for (var item : dropdownItems) {
                if (!item.equals("Select All") && !item.contains(path)) {
                    Assert.fail("Dropdown items should contain the search term");
                }
            }

            productsPage.leftSideFilter.selectItemFromDropdownMenu(path);
            productsPage.leftSideFilter.clickApplyButtonInDropdown();

        } else {
            var pathParts = path.split("\\|");
            for (int i = 0; i < pathParts.length; i++) {
                var pathPart = pathParts[i];
                productsPage.leftSideFilter.searchInsideDropdown("invalid");
                var dropdownItems = productsPage.leftSideFilter.getDropdownOptions();
                Assert.assertTrue(dropdownItems.isEmpty(), "Dropdown items should be empty when searching for an invalid term");

                productsPage.leftSideFilter.searchInsideDropdown(pathPart);
                dropdownItems = productsPage.leftSideFilter.getDropdownOptions();
                Assert.assertFalse(dropdownItems.isEmpty(), "Dropdown items should not be empty when searching for a valid term");
                for (var item : dropdownItems) {
                    if (!item.contains(pathPart)) {
                        Assert.fail("Dropdown items should contain the search term");
                    }
                }

                var singleSelect = new SingleSelectDropdown(driver);
                singleSelect.selectOption(pathPart);

                if (i < pathParts.length - 1) {
                    productsPage.leftSideFilter.clickBackToPreviousMenuButton();
                    singleSelect.selectOption(pathPart);
                }
            }
        }

        var filterValues = productsPage.leftSideFilter.getFilterValues(filterType);
        Assert.assertFalse(filterValues.isEmpty(), "Filter values should not be empty after selecting a dropdown item");
        Assert.assertEquals(filterValues.get(0), path, "Filter value should match the selected dropdown item");
    }
}
