package com.DC.pageobjects.adc.execute.productManager.products;

import com.DC.objects.productVersioning.ProductInstanceUIData;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.execute.productManager.products.productDetailsPage.PropertiesTab;
import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.pageobjects.filters.ProductsLeftSideFilter;
import com.DC.utilities.sharedElements.AGTableCommonFeatures;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ProductsTableBase extends InsightsNavigationMenu {
    protected final By PRODUCTS_GRID_LOCATOR = By.xpath("//div[@data-qa='ProductsGrid']");
    protected final By HORIZONTAL_SCROLLBAR_LOCATOR = By.xpath("//div[@data-qa='ProductsGrid']//div[@class='ag-body-horizontal-scroll-viewport']");
    protected final By FILTER_CHIPS_LOCATOR = By.xpath("//div[@data-qa='FilterChips']//span");
    protected final By FILTER_CHIPS_CLOSE_ICON_LOCATOR = By.xpath("//div[@data-qa='FilterChips']//i");

    public AGTableCommonFeatures tableCommonFeatures;
    public ProductsLeftSideFilter leftSideFilter;

    public ProductsTableBase(WebDriver driver) {
        super(driver);
        findElementVisible(PRODUCTS_GRID_LOCATOR);
        tableCommonFeatures = new AGTableCommonFeatures(driver);
        leftSideFilter = new ProductsLeftSideFilter(driver);
    }

    public List<String> getInstanceUniqueIdsDisplayed() {
        LinkedHashMap<Integer, String> instanceUniqueIdsMap = new LinkedHashMap<>();

        scrollToCenterIfTableScrollable(tableCommonFeatures.TABLE_LOCATOR);

        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableCommonFeatures.TABLE_LOCATOR, 15);

        do {
            waitForDOMStabilization();
            currentPosition = getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR);
            var rowsDisplayed = findElementsVisible(tableCommonFeatures.ROWS_LOCATOR, Duration.ofSeconds(2));

            for (var row : rowsDisplayed) {
                try {
                    int rowIndex = Integer.parseInt(row.getAttribute("row-index"));
                    boolean idAlreadyAdded = instanceUniqueIdsMap.containsKey(rowIndex);
                    if (!idAlreadyAdded) {
                        var name = row.findElement(By.xpath(".//div[@col-id='version']//span//span")).getText();
                        instanceUniqueIdsMap.put(rowIndex, name);
                    }
                } catch (StaleElementReferenceException | NoSuchElementException ex) {
                    break;
                }
            }
            scrollElementVertically(pixelsToScroll, tableCommonFeatures.TABLE_LOCATOR);

        } while (currentPosition != getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR));

        var sortedMap = new TreeMap<>(instanceUniqueIdsMap);
        return new ArrayList<>(sortedMap.values());
    }

    public void scrollToInstance(String instanceUniqueId) {
        String rowLocator = getRowLocator(instanceUniqueId);
        waitForDOMStabilization();
        String errorMsg = "Row with instanceUniqueId '" + instanceUniqueId + "' was not found in the table.";
        scrollDownToElement(tableCommonFeatures.TABLE_LOCATOR, By.xpath(rowLocator), 10, errorMsg);
    }

    public void scrollToProduct(UserFriendlyInstancePath product) {
        String rowLocator = getRowLocator(product);
        waitForDOMStabilization();
        String errorMsg = "Row for product '" + product + "' was not found in the table.";
        scrollDownToElement(tableCommonFeatures.TABLE_LOCATOR, By.xpath(rowLocator), 10, errorMsg);
    }

    public String getRowLocator(String instanceUniqueId) {
        return "//span[descendant::span[text()='" + instanceUniqueId + "']]//ancestor::div[@role='row']";
    }

    public String getRowId(String instanceUniqueId) {
        return findElementVisibleMilliseconds(By.xpath(getRowLocator(instanceUniqueId))).getAttribute("row-id");
    }

    public String getRowId(UserFriendlyInstancePath product) {
        return findElementVisibleMilliseconds(By.xpath(getRowLocator(product))).getAttribute("row-id");
    }

    public String getRowLocator(UserFriendlyInstancePath product) {
        String productVersion = product.getProductVersion();
        return String.format("//div[contains(@class,'locale_%1$s') and " +
                        "child::div[@col-id='masterUniqueId' and descendant::span[text()='%2$s']] and " +
                        "child::div[@col-id='version' and descendant::a[text()='%3$s']]]",
                product.localeName, product.productIdentifier, productVersion
        );
    }

    public List<String> getNonDefaultColumnsDisplayed() {
        Map<Integer, String> colIndexAndNames = new HashMap<>();
        var nonDefaultColumnHeadersXpath = "//div[@role='columnheader' and not(@col-id='masterUniqueId') and not(@col-id='version') and not(@col-id='selectionCheckbox')]";

        int nonDefaultColumnsCount = getElementCountMilliseconds(By.xpath(nonDefaultColumnHeadersXpath));

        boolean scrollBarDisplayed = isElementVisibleMilliseconds(HORIZONTAL_SCROLLBAR_LOCATOR);

        if (scrollBarDisplayed) {
            scrollBarToLeftEnd(HORIZONTAL_SCROLLBAR_LOCATOR);
        }

        for (int i = 0; i < nonDefaultColumnsCount; i++) {
            var colNameLocator = By.xpath(nonDefaultColumnHeadersXpath + "[" + (i + 1) + "]");
            var element = tableCommonFeatures.scrollHorizontallyToElementInTable(colNameLocator, "div[data-qa='ProductsGrid']", HORIZONTAL_SCROLLBAR_LOCATOR);
            var colIndex = element.getAttribute("aria-colindex");
            var colName = element.findElement(By.xpath(".//span[(@ref='eText' or @data-ref='eText') and text()]")).getText();
            colIndexAndNames.put(Integer.parseInt(colIndex), colName);
        }
        colIndexAndNames = new TreeMap<>(colIndexAndNames);
        return new ArrayList<>(colIndexAndNames.values());
    }

    public String getCellLocator(String instanceUniqueId, String columnName) {
        String columnId = tableCommonFeatures.getColumnId(columnName);
        return "//span[descendant::span[text()='" + instanceUniqueId + "']]" +
                "//ancestor::div[@role='row']//div[contains(@col-id,'" + columnId + "')]";
    }

    public String getCellLocator(UserFriendlyInstancePath product, String propertyName) {
        String columnId = tableCommonFeatures.getColumnId(propertyName);
        String rowLocator = getRowLocator(product);
        return rowLocator + "//div[contains(@col-id,'" + columnId + "')]";
    }

    public String getInstanceId(UserFriendlyInstancePath product) {
        String cellLocator = getCellLocator(product, "Versions");
        By fullCellLocator = By.xpath(cellLocator + "//span//span");
        scrollToProduct(product);
        return getTextFromElement(fullCellLocator);
    }

    public List<String> getCellValues(String instanceUniqueId, String columnName) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By cellValueLocator = By.xpath(cellLocatorXpath + "//span[text()]");
        By digitalAssetCellLocator = By.xpath(cellLocatorXpath + "//a");

        var elementPresent = isElementPresentMilliseconds(cellValueLocator);
        var digitalAssetPresent = isElementPresentMilliseconds(digitalAssetCellLocator);

        if (elementPresent) {
            return getTextFromElementsMilliseconds(cellValueLocator);
        } else if (digitalAssetPresent) {
            var digitalAssets = findElementsVisibleMilliseconds(digitalAssetCellLocator);
            return digitalAssets.stream().map(digitalAsset -> digitalAsset.getAttribute("href")).collect(Collectors.toCollection(ArrayList::new));
        } else {
            return new ArrayList<>();
        }
    }

    public LinkedHashMap<String, List<String>> getAllCellValuesFromInstance(String instanceUniqueId) {
        LinkedHashMap<String, List<String>> columnNameAndValues = new LinkedHashMap<>();

        List<String> nonDefaultColumnsDisplayed = getNonDefaultColumnsDisplayed();

        for (String columnName : nonDefaultColumnsDisplayed) {
            scrollToInstance(instanceUniqueId);
            List<String> cellValues = getCellValues(instanceUniqueId, columnName);
            columnNameAndValues.put(columnName, cellValues);
        }

        return columnNameAndValues;
    }

    public LinkedHashMap<String, List<String>> getColumnIdsAndCellValuesFromInstance(String instanceUniqueId) {
        LinkedHashMap<String, List<String>> columnIdAndValues = new LinkedHashMap<>();

        List<String> nonDefaultColumnsDisplayed = getNonDefaultColumnsDisplayed();

        for (String columnName : nonDefaultColumnsDisplayed) {
            List<String> cellValues = getCellValues(instanceUniqueId, columnName);
            String columnId = tableCommonFeatures.getColumnId(columnName);
            columnIdAndValues.put(columnId, cellValues);
        }

        return columnIdAndValues;
    }

    public boolean isPageFilteredByList(String listName) {
        By filterChipLocator = By.xpath("//div[@data-qa='FilterChips']//span[text()='Product List - " + listName + ": ']");
        return isElementVisible(filterChipLocator, MAX_WAIT_TIME_SECS);
    }

    public boolean isPageFilteredByImportId(String importId) {
        By filterChipLocator = By.xpath("//div[@data-qa='FilterChips']//span[text()='Import - " + importId + ": ']");
        return isElementVisible(filterChipLocator, MAX_WAIT_TIME_SECS);
    }

    public ProductsTableBase removeAllAdvancedFilters() throws InterruptedException {
        int totalFilters = getElementCount(FILTER_CHIPS_CLOSE_ICON_LOCATOR, MAX_WAIT_TIME_SECS);
        for (int index = 1; index <= totalFilters; index++) {
            click(FILTER_CHIPS_CLOSE_ICON_LOCATOR);
            waitForElementCountToEqual(FILTER_CHIPS_CLOSE_ICON_LOCATOR, totalFilters - index, MAX_WAIT_TIME_SECS);
            waitForDOMStabilization();
        }
        return this;
    }

    public int getNumberOfAdvancedFiltersApplied() {
        return getElementCount(FILTER_CHIPS_LOCATOR, MAX_WAIT_TIME_SECS);
    }

    public void waitForSearchFilterChipToDisplay(String productIdentifier) {
        By filterChipLocator = By.xpath("//div[@data-qa='FilterChips']//span[text()='Search - contains: " + productIdentifier + "']");
        findElementVisible(filterChipLocator, MAX_WAIT_TIME_SECS);
    }

    public List<ProductInstanceUIData> getProductsDisplayed() {
        LinkedHashMap<Integer, ProductInstanceUIData> productVersionsMap = new LinkedHashMap<>();

        scrollToCenterIfTableScrollable(tableCommonFeatures.TABLE_LOCATOR);

        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableCommonFeatures.TABLE_LOCATOR, 15);

        do {
            currentPosition = getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR);
            var rowsDisplayed = findElementsVisible(tableCommonFeatures.ROWS_LOCATOR, Duration.ofSeconds(2));

            for (var row : rowsDisplayed) {
                try {
                    getProductVersionInfoAndAddItToList(row, productVersionsMap);
                } catch (StaleElementReferenceException | NoSuchElementException ex) {
                    break;
                }
            }
            scrollElementVertically(pixelsToScroll, tableCommonFeatures.TABLE_LOCATOR);

        } while (currentPosition != getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR));

        var sortedMap = new TreeMap<>(productVersionsMap);
        UI_LOGGER.info("Product versions displayed: " + sortedMap.values());
        return new ArrayList<>(sortedMap.values());
    }

    public ProductInstanceUIData getProductInstanceUIData(String rowId) {
        waitForDOMStabilization();

        var rowXPath = tableCommonFeatures.TABLE_VIEWPORT_XPATH + "//div[@row-id='" + rowId + "']";
        var productIdentifierXPath = By.xpath(rowXPath + "//div[contains(@col-id,'masterUniqueId')]//span");
        var versionXPath = By.xpath(rowXPath + "//div[contains(@col-id,'version')]//span//descendant::*[self::p or self::a]");

        var productIdentifier = getTextFromElement(productIdentifierXPath);
        var version = getTextFromElement(versionXPath);
        var localeName = getAttribute(By.xpath(rowXPath), "class").split("locale_")[1].split(" ")[0];

        return new ProductInstanceUIData(productIdentifier, localeName, version);
    }

    public void scrollToProductIdentifier(String productIdentifier) {
        var rowLocator = By.xpath("//div[@role='rowgroup']//div[@data-qa='InlineList']//h6[text()='" + productIdentifier + "']");
        waitForDOMStabilization();
        var errorMsg = "Header with product identifier '" + productIdentifier + "' was not found in the table.";
        scrollDownToElement(tableCommonFeatures.TABLE_LOCATOR, rowLocator, 10, errorMsg);
    }

    public PropertiesTab clickOnProductIdentifierHeader(String productIdentifier) {
        var rowLocator = By.xpath("//div[@role='rowgroup']//div[@data-qa='InlineList']//a[text()='" + productIdentifier + "']");
        clickElement(rowLocator);
        return new PropertiesTab(driver);
    }

    private void getProductVersionInfoAndAddItToList(WebElement row, LinkedHashMap<Integer, ProductInstanceUIData> productVersions) {
        var rowId = row.getAttribute("row-id");
        var rowIndex = Integer.parseInt(row.getAttribute("row-index"));
        var productAlreadyAdded = productVersions.containsKey(rowIndex);

        if (!productAlreadyAdded) {
            var productInstanceUIData = getProductInstanceUIData(rowId);
            productVersions.put(rowIndex, productInstanceUIData);
        }
    }

}
