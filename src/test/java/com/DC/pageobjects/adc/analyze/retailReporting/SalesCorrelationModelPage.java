package com.DC.pageobjects.adc.analyze.retailReporting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesCorrelationModelPage extends NetNewNavigationMenu {
    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    private final By SALES_CORRELATION_HEADER = By.xpath("//a[text()='Sales Correlation Model']");
    private final By PRODUCTS_PAGE_SIZE = By.xpath("//div[@class='MuiPaper-root MuiPaper-elevation MuiPaper-rounded MuiPaper-elevation1 MuiCard-root css-1pnevbu']//div[@aria-haspopup='listbox']");
    private final By PRODUCTS_PAGE_SIZE_OPTIONS = By.xpath("//ul[@class='MuiList-root MuiList-padding MuiMenu-list css-r8u8y9']//li");
    private final By FILTER_DISTRIBUTOR_VIEW = By.xpath("//div[@id='sidebar-filter-distributor-view']//Input");
    private final By LEFT_FILTER_ICON = By.xpath("//span[text()='filter_alt']");
    private final By UNDO_FILTER = By.xpath("//span[text()='undo']");
    private final By REDO_FILTER = By.xpath("//span[text()='redo']");
    private By SALES_BY_WIDGET_DROPDOWN = By.xpath("//div[@class='MuiBox-root css-axw7ok']//h4[text()='Sales By']/parent::div/parent::div//div[@aria-haspopup='listbox']");
    private By SALES_BY_WIDGET_DROPDOWN_OPTIONS = By.xpath("//ul[@role='listbox']");
    private By MODAL_CLOSE_BUTTON   = By.xpath("//div[@class='MuiBox-root css-ewh8ib']//span[text()='close']");
    private By FILTERS_HEADER = By.xpath("//h6[text()='Filters']");
    private By CLOSE_FILTERS_BUTTON = By.xpath("//span[text()='first_page']");
    private By OPEN_FILTERS_BUTTON = By.xpath("//span[text()='filter_alt']");
    Map<Enums.SCTProductsColumns, String> productsColumns = new HashMap<>() {{
        put(Enums.SCTProductsColumns.Sales, "Sales");
        put(Enums.SCTProductsColumns.ChangeInSales, "% Change in sales");
        put(Enums.SCTProductsColumns.AdSpend, "Ad spend");
    }};

    public SalesCorrelationModelPage(WebDriver driver) {
        super(driver);
        findElementVisible(SALES_CORRELATION_HEADER);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
    }

    public String getDistributorViewValue() {
        return getAttribute(FILTER_DISTRIBUTOR_VIEW, "value");
    }

    public String getDefaultBrandInSalesWidget() {
        return getTextFromElement(SALES_BY_WIDGET_DROPDOWN);
    }

    public String getSalesByDropdownValue() {
        return getTextFromElement(SALES_BY_WIDGET_DROPDOWN);
    }

    public void openSalesByDropdown() throws InterruptedException {
        click(SALES_BY_WIDGET_DROPDOWN);
    }

    public boolean isSalesByDropdownOpen() {
        return isElementPresentMilliseconds(SALES_BY_WIDGET_DROPDOWN_OPTIONS);
    }

    public void selectSalesByDropdownValue(String value) throws InterruptedException {
        if (!isSalesByDropdownOpen()) {
            openSalesByDropdown();
        }
        By dropDownSelection = By.xpath("//li[@role='option' and text()='" + value + "']");
        click(dropDownSelection);
    }

    public void closeModal() throws InterruptedException {
        click(MODAL_CLOSE_BUTTON);
    }

    public boolean isDownloadButtonVisible(String section) {
        return isElementVisible(By.xpath("//div[@class='MuiBox-root css-axw7ok']//h4[text()='" + section + "']/parent::div/parent::div//span[text()='download']"));
    }

    public void clickDownloadButton(String section) throws InterruptedException {
        click(By.xpath("//h4[text()='" + section + "']/parent::div/parent::div//span[text()='download']"));
    }

    public boolean isModalExpanderButtonVisible(String section) {
        return isElementVisible(By.xpath("//div[@class='MuiBox-root css-axw7ok']//h4[text()='" + section + "']/parent::div/parent::div//span[text()='fullscreen']"));
    }

    public void clickModalExpanderButton(String section) throws InterruptedException {
        click(By.xpath("//div[@class='MuiBox-root css-axw7ok']//h4[text()='" + section + "']/parent::div/parent::div//span[text()='fullscreen']"));
    }

    public boolean isModalExpanderHeaderVisible(String section) {
        return isElementVisible(By.xpath("//div[@class='MuiBox-root css-ewh8ib']//h4[text()='" + section  + "']"));
    }

    public boolean isProductsColumnVisible(Enums.SCTProductsColumns column) {
        return isElementVisible(By.xpath("//h3[text()='" + productsColumns.get(column) + "']"));
    }

    public void clickProductsColumn(Enums.SCTProductsColumns column) throws InterruptedException {
        click(By.xpath("//h3[text()='" + productsColumns.get(column) + "']/parent::div//span"));
        waitForElementToBeInvisible(By.xpath(("//span[@role='progressbar']")), Duration.ofSeconds(30));
    }

    public Enums.ColumnArrowSorting getProductsColumnSortingArrowStatus(Enums.SCTProductsColumns column) {
        By columnHeader = By.xpath("//h3[text()='" + productsColumns.get(column) + "']/parent::div//span");
        if (isElementVisible(columnHeader)) {
            String arrowStatus = getTextFromElement(columnHeader);
            if (arrowStatus.equals("arrow_upward")) {
                return Enums.ColumnArrowSorting.Ascending;
            } if (arrowStatus.equals("arrow_downward")) {
                return Enums.ColumnArrowSorting.Descending;
            }
        }
        return null;
    }

    public Enums.SCTProductsColumns getActiveProductsColumn() {
        for (Enums.SCTProductsColumns column : productsColumns.keySet()) {
            Enums.ColumnArrowSorting arrowStatus = getProductsColumnSortingArrowStatus(column);
            if (arrowStatus != null) {
                if ( arrowStatus.equals(Enums.ColumnArrowSorting.Ascending) || arrowStatus.equals(Enums.ColumnArrowSorting.Descending)) {
                    return column;
                }
            }
        }
        return null;
    }

    public List<Integer> getAllPageSizeOptions() {
        List<WebElement> pageSizeListOptions = findElementsVisible(PRODUCTS_PAGE_SIZE_OPTIONS);
        List<Integer> pageSizeOptions = new ArrayList<>();
        for (WebElement option : pageSizeListOptions) {
            String pageSizeOption = option.getText();
            pageSizeOptions.add(Integer.parseInt(pageSizeOption));
        }
        return pageSizeOptions;
    }

    public void clickProductsPageSizeSelector() throws InterruptedException{
        click(PRODUCTS_PAGE_SIZE);
    }

    public void clickProductsPageSizeSelectionOption(int pageSize) throws InterruptedException {
        By optionToSelect = By.xpath("//ul[@class='MuiList-root MuiList-padding MuiMenu-list css-r8u8y9']//li"+ "[text()='" + pageSize + "']");
        click(optionToSelect);
    }

    public int getProductsPageSizeValue() {
        scrollIntoView(PRODUCTS_PAGE_SIZE);
        return Integer.parseInt(getTextFromElement(PRODUCTS_PAGE_SIZE));
    }

    public boolean isLeftFilterIconDisplayed() {
        return isElementVisible(LEFT_FILTER_ICON);
    }

    public boolean isUndoFilterDisplayed(){
        return isElementVisible(UNDO_FILTER);
    }

    public boolean isRedoFilterDisplayed() {
        return isElementVisible(REDO_FILTER);
    }

    public boolean isFiltersSectionOpen() {
        return isElementVisible(FILTERS_HEADER);
    }

    public void openFiltersSectionIfClosed() throws InterruptedException {
        if(!isFiltersSectionOpen()) {
            click(OPEN_FILTERS_BUTTON);
        }
    }
}
