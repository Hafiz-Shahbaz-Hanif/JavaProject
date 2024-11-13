package com.DC.pageobjects.adc.execute.productManager.products;

import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage.PropertiesTab;
import com.DC.pageobjects.filters.ProductsLeftSideFilter;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.sharedElements.Paginator;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public abstract class ProductsPageBase extends ProductsTableBase {
    protected final String BOTTOM_ACTION_BAR_XPATH = "//div[@data-qa='BottomActionBarPV']";
    protected final String PRODUCTS_HEADER_LOCATOR = "//div[@data-qa='ProductsHeader']";
    protected final By BOTTOM_ACTION_BAR_LOCATOR = By.xpath(BOTTOM_ACTION_BAR_XPATH);
    protected final By SEARCH_INPUT_LOCATOR = By.xpath("//input[@placeholder='Search by Product Identifier ...']");
    protected final By SELECT_PRODUCTS_CHECKBOX_LOCATOR = By.xpath("//*[contains(@data-testid,'CheckBox')]/preceding-sibling::input");
    private final By PRODUCTS_DISPLAYED_COUNT_LOCATOR = By.xpath(PRODUCTS_HEADER_LOCATOR + "//h6");
    public Paginator paginator;
    public SingleSelectDropdown moreActionsDropdown;

    public ProductsPageBase(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(PRODUCTS_HEADER_LOCATOR));
        paginator = new Paginator(driver);
        moreActionsDropdown = new SingleSelectDropdown(driver);
    }

    public ProductsPageBase searchByProductIdentifier(String productIdentifier) {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, productIdentifier);
        waitForDOMStabilization();
        waitForSearchFilterChipToDisplay(productIdentifier);
        waitForDOMStabilization();
        return this;
    }

    public int getNumberOfProductsDisplayedNextToSearchInput() {
        String textInElement = getTextFromElement(PRODUCTS_DISPLAYED_COUNT_LOCATOR, MAX_WAIT_TIME_SECS);
        var productsPart = textInElement.substring(0, textInElement.indexOf("|") + 1).trim();
        return SharedMethods.extractIntegerFromString(productsPart);
    }

    public int getNumberOfVersionsDisplayedNextToSearchInput() {
        var textInElement = getTextFromElement(PRODUCTS_DISPLAYED_COUNT_LOCATOR, MAX_WAIT_TIME_SECS);
        var versionsPart = textInElement.substring(textInElement.indexOf("|") + 1).trim();
        return SharedMethods.extractIntegerFromString(versionsPart);
    }

    public int waitForNumberOfProductsToChange(int numberOfProductsBefore) {
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(
                () ->
                {
                    var currentNumberOfProducts = getNumberOfProductsDisplayedNextToSearchInput();
                    Assert.assertNotEquals(
                            currentNumberOfProducts,
                            numberOfProductsBefore,
                            "Number of products displayed didn't change from " + numberOfProductsBefore
                    );
                }
        );
        return getNumberOfProductsDisplayedNextToSearchInput();
    }

    // checkVersions is true if you want to check the number of versions displayed next to the search input instead of the number of products
    public int refreshUntilNumberNextToSearchInputEquals(int expectedNumber, int attempts, String errorMessage, boolean checkVersions) throws Exception {
        int currentNumber = checkVersions ? getNumberOfVersionsDisplayedNextToSearchInput() : getNumberOfProductsDisplayedNextToSearchInput();
        if (currentNumber == expectedNumber) {
            return currentNumber;
        }
        for (int i = 1; i <= attempts; i++) {
            refreshPage();
            waitForDOMStabilization();
            currentNumber = checkVersions ? getNumberOfVersionsDisplayedNextToSearchInput() : getNumberOfProductsDisplayedNextToSearchInput();
            if (currentNumber == expectedNumber) {
                break;
            } else if (i == attempts) {
                throw new Exception(errorMessage + " Expected: " + expectedNumber + " Actual: " + currentNumber);
            }
        }
        return currentNumber;
    }

    public ProductsPageBase deselectAllProducts() {
        clickElement(SELECT_PRODUCTS_CHECKBOX_LOCATOR);
        var deselectAllCheckbox = By.xpath("//li[text()='Deselect All']");
        clickElement(deselectAllCheckbox);
        return this;
    }

    public ProductsPageBase selectAllProductsOnPage() throws InterruptedException {
        clickElement(SELECT_PRODUCTS_CHECKBOX_LOCATOR);
        var selectAllOnPageCheckbox = By.xpath("//li[text()='Select All On Page']");
        clickElement(selectAllOnPageCheckbox);
        return this;
    }

    public ProductsPageBase selectProduct(UserFriendlyInstancePath product) {
        scrollToProduct(product);
        String instanceId = getInstanceId(product);
        selectProduct(instanceId);
        return this;
    }

    public ProductsPageBase selectProduct(String instanceUniqueId) {
        scrollToInstance(instanceUniqueId);
        String rowLocator = getRowLocator(instanceUniqueId);
        By checkboxLocator = By.xpath(rowLocator + "//input[@type='checkbox']");
        try {
        clickElement(checkboxLocator);
        } catch (ElementClickInterceptedException e) {
            UI_LOGGER.info("ElementClickInterceptedException caught. Clicking again using javascript.");
            clickElementUsingJavascriptExecutor(checkboxLocator);
        }
        return this;
    }

    public ProductsPageBase selectProducts(List<UserFriendlyInstancePath> products) {
        for (UserFriendlyInstancePath product : products) {
            selectProduct(product);
        }
        return this;
    }

    public ProductsPageBase selectProductsByInstanceUniqueId(List<String> instanceUniqueIds) {
        for (String instanceUniqueId : instanceUniqueIds) {
            selectProduct(instanceUniqueId);
        }
        return this;
    }

    public PropertiesTab goToPropertiesTab(UserFriendlyInstancePath product) {
        searchByProductIdentifier(product.productIdentifier);
        String rowLocator = getRowLocator(product);
        By versionLink = By.xpath(rowLocator + "//a");
        scrollIntoView(versionLink);
        clickElement(versionLink);
        return new PropertiesTab(driver);
    }

    public <T> T applyVersionsFilter(List<Map.Entry<ProductsLeftSideFilter.FilterType, List<String>>> filtersAndPaths, Class<T> pageClass) throws InterruptedException {
        leftSideFilter.openFilter();
        var textNextToInputSearchBefore = getTextFromElement(PRODUCTS_DISPLAYED_COUNT_LOCATOR, MAX_WAIT_TIME_SECS);

        for (var filterToApply : filtersAndPaths) {
            leftSideFilter.selectFilter(filterToApply.getKey(), filterToApply.getValue());
        }

        leftSideFilter.applyFilter(pageClass);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(
                () ->
                {
                    var currentTextNextToInputSearch = getTextFromElement(PRODUCTS_DISPLAYED_COUNT_LOCATOR, MAX_WAIT_TIME_SECS);
                    Assert.assertNotEquals(
                            currentTextNextToInputSearch,
                            textNextToInputSearchBefore,
                            "Number of products and versions displayed didn't change from " + textNextToInputSearchBefore
                    );
                }
        );
        return getPage(pageClass);
    }
}
