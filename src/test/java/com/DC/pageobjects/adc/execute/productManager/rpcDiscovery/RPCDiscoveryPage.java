package com.DC.pageobjects.adc.execute.productManager.rpcDiscovery;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.sharedElements.AGTableCommonFeatures;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;
import java.util.*;

public class RPCDiscoveryPage extends InsightsNavigationMenu {
    private final By PAGE_BODY_XPATH = By.xpath("//div[@class='page-body']");
    private final By SEARCH_INPUT_LOCATOR = By.xpath("//input[@placeholder='Search RPC']");

    // TODO: Change data-qa to correct value
    private final By SEARCH_BUTTON_LOCATOR = By.xpath("//div[@data-qa='RPCGrid']//button[contains(text(),'search')]");

    public final String PRODUCT_IDENTIFIER_COL_NAME = "Product Identifier";
    public final String RPC_COL_NAME = "RPC";
    public final String LOCALE_COL_NAME = "Locale";
    public final String RETAILER_COL_NAME = "Retailer";
    public final String BUSINESS_UNIT_COL_NAME = "Business Unit";
    public final String DATE_UPDATED_COL_NAME = "Date Updated";

    public AGTableCommonFeatures tableCommonFeatures;

    public final List<String> COLUMNS_DISPLAYED = new ArrayList<>();

    public RPCDiscoveryPage(WebDriver driver) {
        super(driver);
        findElementVisible(PAGE_BODY_XPATH, MAX_WAIT_TIME_SECS);
        tableCommonFeatures = new AGTableCommonFeatures(driver);
        COLUMNS_DISPLAYED.addAll(tableCommonFeatures.getColumnsDisplayed());
    }

    public RPCDiscoveryPage searchForRPC(String searchTerm, boolean clickOnSearchIcon) {
        if (clickOnSearchIcon) {
            setText(SEARCH_INPUT_LOCATOR, searchTerm);
            clickElement(SEARCH_BUTTON_LOCATOR);
        } else {
            setTextAndHitEnter(SEARCH_INPUT_LOCATOR, searchTerm);
        }
        return this;
    }

    public BottomActionBar selectRPC(String rpc) throws InterruptedException {
        var checkboxLocator = By.xpath("//div[child::div[@col-id='rpc' and descendant::span[text()='" + rpc + "']]]//div[@col-id='selectionCheckbox']//input");
        scrollUntilRPCIsPresent(rpc);
        scrollIntoViewAndClick(checkboxLocator);
        return new BottomActionBar(driver);
    }

    public ArrayList<LinkedHashMap<String, String>> getTableData() {
        LinkedHashMap<Integer, LinkedHashMap<String, String>> rpcsMap = new LinkedHashMap<>();

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
                    boolean rpcAlreadyInMap = rpcsMap.containsKey(rowIndex);
                    if (!rpcAlreadyInMap) {
                        var rpc = row.findElement(By.xpath(".//div[@col-id='rpc']//span[text()]")).getText();
                        var rowData = getRowData(rpc);
                        rpcsMap.put(rowIndex, rowData);
                    }
                } catch (StaleElementReferenceException | NoSuchElementException ex) {
                    break;
                }
            }
            scrollElementVertically(pixelsToScroll, tableCommonFeatures.TABLE_LOCATOR);

        } while (currentPosition != getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR));

        var sortedMap = new TreeMap<>(rpcsMap);
        return new ArrayList<>(sortedMap.values());
    }

    public RPCDiscoveryPage scrollUntilRPCIsPresent(String rpc) {
        var rowLocatorXPath = By.xpath(getCellLocator(rpc, RPC_COL_NAME));
        var errorMsg = "RPC '" + rpc + "' was not found in the table";
        scrollDownToElement(tableCommonFeatures.TABLE_LOCATOR, rowLocatorXPath, 10, errorMsg);
        return this;
    }

    public String getCellValue(String rpc, String columnName) {
        var cellLocatorXPath = getCellLocator(rpc, columnName) + "//span";

        if (columnName.equals(PRODUCT_IDENTIFIER_COL_NAME) || columnName.equals(BUSINESS_UNIT_COL_NAME)) {
            cellLocatorXPath += "//span";
        }

        var cellValueLocator = By.xpath(cellLocatorXPath);
        return getTextFromElement(cellValueLocator, MAX_WAIT_TIME_SECS);
    }

    public LinkedHashMap<String, String> getRowData(String rpc) {
        scrollUntilRPCIsPresent(rpc);
        LinkedHashMap<String, String> rowData = new LinkedHashMap<>();

        for (String columnName : COLUMNS_DISPLAYED) {
            var cellValue = getCellValue(rpc, columnName);
            rowData.put(columnName, cellValue);
        }
        return rowData;
    }

    public void submitAndCreateProducts() {

    }

    public void clickIcon(String rpc, String columnName, String iconToClick) throws InterruptedException {
        var cellLocatorXpath = getCellLocator(rpc, columnName);
        var iconToClickLocator = By.xpath(cellLocatorXpath + "//button[text()='" + iconToClick + "']");
        scrollIntoViewAndClick(iconToClickLocator);
    }

    public void editProductIdentifierInput(String rpc, String newProductIdentifier) {
        var cellLocatorXpath = getCellLocator(rpc, PRODUCT_IDENTIFIER_COL_NAME);
        var inputLocator = By.xpath(cellLocatorXpath + "//input");
        var inputToEditProductIdentifier = findElementVisibleMilliseconds(inputLocator);
        setText(inputToEditProductIdentifier, newProductIdentifier);
    }

    public RPCDiscoveryPage editProductIdentifier(String rpc, String productIdentifier) throws InterruptedException {
        clickIcon(rpc, PRODUCT_IDENTIFIER_COL_NAME, "edit");
        editProductIdentifierInput(rpc, productIdentifier);
        clickIcon(rpc, PRODUCT_IDENTIFIER_COL_NAME, "save");
        return this;
    }

    public void editBusinessUnit(String rpc, String businessUnitToSelect) throws InterruptedException {
        clickIcon(rpc, BUSINESS_UNIT_COL_NAME, "edit");
        var cellLocatorXpath = getCellLocator(rpc, BUSINESS_UNIT_COL_NAME);
        By inputLocator = By.xpath(cellLocatorXpath + "//input");
        var dropdown = new SingleSelectDropdown(driver);
        dropdown.openDropdownMenu(inputLocator);
        dropdown.selectOption(businessUnitToSelect);
        clickIcon(rpc, BUSINESS_UNIT_COL_NAME, "save");
    }

    private String getCellLocator(String rpc, String columnName) {
        String columnId = tableCommonFeatures.getColumnId(columnName);
        return "//span[descendant::span[text()='" + rpc + "']]" +
                "//ancestor::div[@role='row']//div[contains(@col-id,'" + columnId + "')]";
    }

    public static class BottomActionBar extends RPCDiscoveryPage {

        public BottomActionBar(WebDriver driver) {
            super(driver);
        }
    }
}
