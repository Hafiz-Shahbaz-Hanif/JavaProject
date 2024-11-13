package com.DC.pageobjects.adc.execute.productManager.products;

import com.DC.utilities.SharedMethods;
import com.DC.utilities.sharedElements.GenericMultiListModal;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class BulkEditTableBase extends ProductsTableBase {
    protected final String PAGE_BODY_XPATH = "//div[@data-qa='BulkEditHeader']";
    protected final String BOTTOM_ACTION_BAR_XPATH = "//div[@data-qa='BottomActionBarPV']";
    protected final By SEARCH_INPUT_LOCATOR = By.xpath(PAGE_BODY_XPATH + "//input");
    protected final By MANAGE_COLUMNS_BUTTON_LOCATOR = By.xpath("//button[text()='Manage Columns']");
    protected final By SUBMIT_BUTTON_LOCATOR = By.xpath(BOTTOM_ACTION_BAR_XPATH + "//button[contains(text(),'Submit')]");
    protected final By CANCEL_BUTTON_LOCATOR = By.xpath(BOTTOM_ACTION_BAR_XPATH + "//button[contains(text(),'Cancel') or text()='Cancel']");
    protected final By CELLS_IN_EDIT_MODE_LOCATOR = By.xpath("//div[@role='gridcell' and descendant::*[self::input or self::textarea[not(@readonly)]]]");
    protected final By PRODUCTS_DISPLAYED_COUNT_LOCATOR = By.xpath(PAGE_BODY_XPATH + "//h6");

    protected final Duration MAX_WAIT_TIME_SECS = Duration.ofSeconds(3);

    public BulkEditTableBase(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(PAGE_BODY_XPATH), MAX_WAIT_TIME_SECS);
    }

    public GenericMultiListModal clickManageColumnsButton() throws InterruptedException {
        scrollIntoViewAndClick(MANAGE_COLUMNS_BUTTON_LOCATOR);
        return new GenericMultiListModal(driver);
    }

    public boolean isSubmitButtonEnabled() {
        return isElementEnabledMilliseconds(SUBMIT_BUTTON_LOCATOR);
    }

    public void clickSubmitButton() throws InterruptedException {
        scrollIntoViewAndClick(SUBMIT_BUTTON_LOCATOR);
    }

    public <T> T clickSubmitButton(Class<T> expectedPage) throws InterruptedException {
        scrollIntoViewAndClick(SUBMIT_BUTTON_LOCATOR);
        waitForElementToBeInvisible(SUBMIT_BUTTON_LOCATOR);
        return getPage(expectedPage);
    }

    public <T> T clickCancelButton(Class<T> expectedPage) throws InterruptedException {
        scrollIntoViewAndClick(CANCEL_BUTTON_LOCATOR);
        return getPage(expectedPage);
    }

    public void clickEditIcon(String instanceUniqueId, String columnName) throws InterruptedException {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By editButtonLocator = By.xpath(cellLocatorXpath + "//button[text()='edit']");
        scrollIntoViewAndClick(editButtonLocator);
    }

    public void clickEditIconIfDisplayed(String instanceUniqueId, String columnName) throws InterruptedException {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By editButtonLocator = By.xpath(cellLocatorXpath + "//button[text()='edit']");
        if (isElementVisibleMilliseconds(editButtonLocator)) {
            scrollIntoViewAndClick(editButtonLocator);
        }
    }

    public void clickEditIconsIfDisplayed(Map<String, List<String>> instanceAndColumnNames) throws InterruptedException {
        var entrySet = instanceAndColumnNames.entrySet();
        for (var entry : entrySet) {
            var columnNames = entry.getValue();
            for (var columnName : columnNames) {
                clickEditIconIfDisplayed(entry.getKey(), columnName);
            }
        }
    }

    public void clickSaveValueIcon(String instanceUniqueId, String columnName) throws InterruptedException {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By saveValueIconLocator = By.xpath(cellLocatorXpath + "//button[text()='save']");
        scrollIntoViewAndClick(saveValueIconLocator);
    }

    public void clickCloseIcon(String instanceUniqueId, String columnName) throws InterruptedException {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By closeIconLocator = By.xpath(cellLocatorXpath + "//button[text()='close']");
        scrollIntoViewAndClick(closeIconLocator);
    }

    public void clickAddValueIcon(String instanceUniqueId, String columnName) throws InterruptedException {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By addValueIconLocator = By.xpath(cellLocatorXpath + "//button[text()='add']");
        scrollIntoViewAndClick(addValueIconLocator);
    }

    public void addNewValueToCell(String instanceUniqueId, String columnName, String newValue) throws InterruptedException {
        int numberOfInputsBefore = getNumberOfInputsInCell(instanceUniqueId, columnName);
        clickAddValueIcon(instanceUniqueId, columnName);
        editInputValue(instanceUniqueId, columnName, newValue, numberOfInputsBefore + 1);
    }

    public void editCellValue(String instanceUniqueId, String columnName, String newValue) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By inputLocator = By.xpath(cellLocatorXpath + "//descendant::*[self::input or self::textarea[not(@readonly)]]");
        setText(inputLocator, newValue);
    }

    public void editInputValue(String instanceUniqueId, String columnName, String newValue, int inputNumber) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By inputLocator = By.xpath("(" + cellLocatorXpath + "//textarea[not(@readonly)])[" + inputNumber + "]");
        setText(inputLocator, newValue);
    }

    public void removeInputValue(String instanceUniqueId, String columnName, String valueToRemove) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By inputLocator = By.xpath(cellLocatorXpath + "//textarea[not(@readonly) and text()='" + valueToRemove + "']");
        clearInput(inputLocator);
    }

    public int getNumberOfInputsInCell(String instanceUniqueId, String columnName) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By inputLocator = By.xpath(cellLocatorXpath + "//textarea[not(@readonly)]");
        return getElementCountMilliseconds(inputLocator);
    }

    public void clickEditIconAndEditCellValue(String instanceUniqueId, String columnName, String newValue) throws InterruptedException {
        clickEditIconIfDisplayed(instanceUniqueId, columnName);
        editCellValue(instanceUniqueId, columnName, newValue);
    }

    public void selectValueFromDropdown(String instanceUniqueId, String columnName, String valueToSelect) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By inputLocator = By.xpath(cellLocatorXpath + "//input");
        var dropdown = new SingleSelectDropdown(driver);
        dropdown.openDropdownMenu(inputLocator);
        dropdown.selectOption(valueToSelect);
    }

    public void clickEditIconAndSelectValueFromDropdown(String instanceUniqueId, String columnName, String valueToSelect) throws InterruptedException {
        clickEditIconIfDisplayed(instanceUniqueId, columnName);
        selectValueFromDropdown(instanceUniqueId, columnName, valueToSelect);
    }

    public List<String> getInputValues(String instanceUniqueId, String columnName) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By cellValueLocator = By.xpath(cellLocatorXpath + "//textarea[not(@readonly)]");
        return getTextFromElementsMilliseconds(cellValueLocator);
    }

    public boolean isPlusIconDisplayed(String instanceUniqueId, String columnName) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By plusIconLocator = By.xpath(cellLocatorXpath + "//button[text()='add']");
        return isElementVisibleMilliseconds(plusIconLocator);
    }

    public boolean isEditIconDisplayed(String instanceUniqueId, String columnName) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By editIconLocator = By.xpath(cellLocatorXpath + "//button[text()='edit']");
        return isElementVisibleMilliseconds(editIconLocator);
    }

    public boolean isCloseIconDisplayed(String instanceUniqueId, String columnName) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By closeIconLocator = By.xpath(cellLocatorXpath + "//button[text()='close']");
        return isElementVisibleMilliseconds(closeIconLocator);
    }

    public boolean isSaveIconDisplayed(String instanceUniqueId, String columnName) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By saveIconLocator = By.xpath(cellLocatorXpath + "//button[text()='save']");
        return isElementVisibleMilliseconds(saveIconLocator);
    }

    public boolean isEditIconEnabled(String instanceUniqueId, String columnName) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By editIconLocator = By.xpath(cellLocatorXpath + "//button[text()='edit']");
        return isElementEnabled(editIconLocator);
    }

    public boolean isCellHighlighted(String instanceUniqueId, String columnName) {
        String cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        By cellLocator = By.xpath(cellLocatorXpath + "//div[contains(@style,'background-color: rgb(255, 208, 103)')]");
        return isElementVisibleMilliseconds(cellLocator);
    }

    public int getNumberOfCellsInEditMode() {
        return getElementCountMilliseconds(CELLS_IN_EDIT_MODE_LOCATOR);
    }

    public int getNumberOfHighlightedCells() {
        return getElementCountMilliseconds(By.xpath("//div[@role='gridcell' and descendant::div[contains(@style,'background-color: rgb(255, 208, 103)')]]"));
    }

    public int getNumberDisplayedNextToSearchInput() {
        String textInElement = getTextFromElementMilliseconds(PRODUCTS_DISPLAYED_COUNT_LOCATOR);
        return SharedMethods.extractIntegerFromString(textInElement);
    }

    public void clearSearchInput() {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, "");
    }

    public void uploadDigitalAsset(String instanceUniqueId, String columnName, String filePath) throws InterruptedException {
        var cellLocatorXpath = getCellLocator(instanceUniqueId, columnName);
        var editButtonLocator = By.xpath(cellLocatorXpath + "//button[text()='edit']");
        var addButtonLocator = By.xpath(cellLocatorXpath + "//button[text()='add']");
        var inputLocator = By.xpath("//div[@data-qa='DigitalAsset']//input");

        if (!isElementVisibleMilliseconds(editButtonLocator)) {
            scrollIntoViewAndClick(addButtonLocator);
        } else {
            scrollIntoViewAndClick(editButtonLocator);
        }

        uploadFile(inputLocator, filePath);
        waitForElementToBeInvisible(inputLocator);
    }
}
