package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.*;

import java.util.ArrayList;
import java.util.List;

public class AGTableCommonFeatures extends PageHandler {
    public final String TABLE_VIEWPORT_XPATH = "//div[@class='ag-center-cols-viewport']";
    public final String TABLE_FULL_WIDTH_XPATH = "//div[@class='ag-full-width-container']";
    public final String SELECT_ALL_CHECKBOX_XPATH = "//div[@col-id='selectionCheckbox' and @role='columnheader']//input";
    public final By COLUMN_HEADERS_LOCATOR = By.xpath("//div[@role='columnheader']//h6 | //div[@role='columnheader']//span[(@ref='eText' or @data-ref='eText')  and text()]");
    public final By NO_DATA_MESSAGE_LOCATOR = By.xpath("//div[@class='ag-overlay-panel']/div[text()='No Data To Display'] | //div[@class='ag-overlay-panel']//span[text()='No Rows To Show']");
    public final By CHECKED_ITEMS_LOCATOR = By.xpath("//div[@col-id='selectionCheckbox' and @role='gridcell']//input/parent::div[contains(@class,'ag-checked')]");
    public final By SELECT_ALL_CHECKBOX_LOCATOR = By.xpath(SELECT_ALL_CHECKBOX_XPATH);
    public final By TABLE_LOCATOR = By.xpath("//div[contains(@class,'ag-body-viewport')]");
    public final By ROWS_LOCATOR = By.xpath("//div[@role='row' and ancestor::div[@class='ag-center-cols-container']]");

    public AGTableCommonFeatures(WebDriver driver) {
        super(driver);
    }

    public boolean isNoDataMessageDisplayed() {
        return isElementVisibleMilliseconds(NO_DATA_MESSAGE_LOCATOR);
    }

    public List<String> getColumnsDisplayed() {
        return getTextFromElementsMilliseconds(COLUMN_HEADERS_LOCATOR);
    }

    public String getColumnId(String columnName) {
        By columnLocator = By.xpath("//div[@role='columnheader' and descendant::span[(@ref='eText' or @data-ref='eText') and text()='" + columnName + "']]");
        return getAttribute(columnLocator, "col-id");
    }

    public boolean isSelectAllCheckboxSelected() {
        By checkboxParentElement = By.xpath(SELECT_ALL_CHECKBOX_XPATH + "/..");
        return getAttribute(checkboxParentElement, "class").contains("ag-checked");
    }

    public int getSelectedItemsCount() {
        return getElementCountMilliseconds(CHECKED_ITEMS_LOCATOR);
    }

    public void deselectAll() {
        int selectedItemsCount = getSelectedItemsCount();

        if (isSelectAllCheckboxSelected()) {
            clickElement(SELECT_ALL_CHECKBOX_LOCATOR);
        } else if (selectedItemsCount > 0) {
            clickElement(SELECT_ALL_CHECKBOX_LOCATOR);
            clickElement(SELECT_ALL_CHECKBOX_LOCATOR);
        }
    }

    public void selectAll() throws InterruptedException {
        if (!isSelectAllCheckboxSelected()) {
            click(SELECT_ALL_CHECKBOX_LOCATOR);
        }
    }

    public WebElement scrollHorizontallyToElementInTable(By elementLocator, String cssOfRelativeElement, By scrollBarLocator) {
        WebElement element = findElementPresentMilliseconds(elementLocator);
        if (!element.isDisplayed()) {
            // Calculate the location of the element relative to table
            var script = "var div = arguments[0].closest(arguments[1]); " +
                    "var rect = arguments[0].getBoundingClientRect(); " +
                    "var tableRect = div.getBoundingClientRect(); " +
                    "var x = rect.left - tableRect.left; " +
                    "var y = rect.top - tableRect.top; " +
                    "return [x, y];";
            var result = ((JavascriptExecutor) driver).executeScript(script, element, cssOfRelativeElement);
            var pixelsToScroll = (Long) ((ArrayList<?>) result).get(0);
            scrollElementHorizontally(pixelsToScroll, scrollBarLocator);
            waitForDOMStabilization();
        }
        return findElementVisibleMilliseconds(elementLocator);
    }

    public String getColumnSortStatus(String columnName) {
        var indicatorIconLocator = By.xpath("//div[@role='columnheader' and descendant::span[text()='" + columnName + "']]//span//span[contains(@ref,'eSort') and not(contains(@class,'hidden'))]");
        var currentStatus = getAttribute(indicatorIconLocator, "ref");
        return currentStatus.replace("eSort", "");
    }

    public List<String> getSortableColumns() {
        var sortableColumnsLocator = By.xpath("//div[@role='columnheader' and descendant::i[contains(@class,'fa-sort')]]");
        var sortableColumns = findElementsPresentMilliseconds(sortableColumnsLocator);
        var columnsInOrder = new String[sortableColumns.size()];
        for (var col : sortableColumns) {
            var colIndex = col.getAttribute("aria-colindex");
            var colName = getTextFromElementMilliseconds(By.xpath("//div[@role='columnheader' and @aria-colindex='" + colIndex + "']//span[@ref='eText']"));
            columnsInOrder[Integer.parseInt(colIndex) - 2] = colName;
        }
        return new ArrayList<>(List.of(columnsInOrder));
    }

    public void sortColumn(String columnName, boolean ascending) throws Exception {
        var columnLocator = By.xpath("//div[@role='columnheader' and descendant::span[@ref='eText' and text()='" + columnName + "']]");
        var currentStatus = getColumnSortStatus(columnName);
        var sortText = ascending ? "Asc" : "Desc";

        var maxAttempts = 3;
        while (!currentStatus.equals(sortText)) {
            clickElement(columnLocator);
            currentStatus = getColumnSortStatus(columnName);
            maxAttempts--;
            if (maxAttempts == 0) {
                throw new Exception("Unable to sort column " + columnName + " in " + sortText + " order");
            }
        }
        waitForDOMStabilization();
    }

    public void rearrangeColumnPosition(String columnToMove, String rearrangementColumn) {
        var sourceColumnLocator = By.xpath("//div[@role='columnheader' and descendant::span[text()='" + columnToMove + "']]");
        var targetColumnLocator = By.xpath("//div[@role='columnheader' and descendant::span[text()='" + rearrangementColumn + "']]");

        waitForDOMStabilization();
        dragAndDrop(sourceColumnLocator, targetColumnLocator);
    }
}
