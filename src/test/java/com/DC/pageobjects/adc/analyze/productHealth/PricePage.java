package com.DC.pageobjects.adc.analyze.productHealth;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.CommonFeatures;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.*;

import java.text.ParseException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


public class PricePage extends NetNewNavigationMenu {

    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public CommonFeatures commonFeatures;
    public DCFilters dcFilters;

    private final By PRODUCT_WITH_PRICE_CHANGES_HEADER = By.xpath("//h4[text()='Products with Price Changes']");
    private final By VALUE_PERCENTAGE_TOGGLE = By.id("value-percentage-toggle");
    private final By PRICE_CHANGE_FIELD = By.id("price-change-input-field");
    public final By PRICE_CHANGE_GROUP_BY = By.id("price-change-groupby-selection");
    private final By AVERAGE_SELLING_PRICE_HEADER = By.xpath("//h4[text()='Average Selling Price']");
    public final By AVERAGE_SELLING_PRICE_GROUP_BY = By.id("asp-groupby-selection");
    public final By PRICE_TABLE_HEADER = By.xpath("//h4[text()='Price Table']");
    private final By SEARCH_INPUT_BOX = By.xpath("//span[text()='search']/preceding-sibling::input");
    private final By DOWNLOAD_ICON = By.id("price-table-export-button");
    private final By MORE_OPTIONS_ICON = By.xpath("//span[text()='more_vert']");
    private final By PRICE_TABLE_SORTING_ICON = By.xpath("//div[@class='ag-header-row ag-header-row-column']//span[text()='filter_alt']");
    private final By PRICE_CHANGE_CHART_EXPORT_ICON = By.xpath("//h4[text()='Products with Price Changes']/parent::*/following-sibling::div");
    private final By AVERAGE_SELLING_PRICE_CHART_EXPORT_ICON = By.xpath("//h4[text()='Average Selling Price']/parent::*/following-sibling::div");

    public PricePage(WebDriver driver) {
        super(driver);
        findElementVisible(PRODUCT_WITH_PRICE_CHANGES_HEADER);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
        commonFeatures = new CommonFeatures(driver);
        dcFilters = new DCFilters(driver);
    }

    public boolean isProductsWithPriceChangesHeaderDisplayed() {
        return isElementVisible(PRODUCT_WITH_PRICE_CHANGES_HEADER);
    }

    public boolean isValuePercentageToggleDisplayed() {
        return isElementVisible(VALUE_PERCENTAGE_TOGGLE);
    }

    public void selectValuePercentageToggle(String toggle) throws InterruptedException {
        By toggleLocator = By.xpath("//div[@id='value-percentage-toggle']/button[text()='" + toggle + "']");
        click(toggleLocator);
    }

    public boolean isTableDataValid(String toggle) {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR, Duration.ofSeconds(15));
        boolean isTableDataValid = true;
        By tableDataLocator = By.xpath("//*[name()='g'][@class='highcharts-label highcharts-data-label highcharts-data-label-color-0']");
        String tableData = getTextFromElement(tableDataLocator, Duration.ofSeconds(3));
        String[] tableDataArray = tableData.split("\n");

        for (String data : tableDataArray) {
            if (toggle.equals("#")) {
                if (!data.contains("")) {
                    isTableDataValid = false;
                    break;
                }
            } else if (toggle.equals("%")) {
                if (!data.contains("%")) {
                    isTableDataValid = false;
                    break;
                }
            }
        }
        return isTableDataValid;
    }

    public boolean isPriceChangeFieldDisplayed() {
        return isElementVisible(PRICE_CHANGE_FIELD);
    }

    public boolean isPriceChangeFieldDefaultValueZero() {
        return getAttribute(PRICE_CHANGE_FIELD, "value").equals("0");
    }

    public boolean isPriceChangeFieldEditable(String priceChangeValue) {
        setText(PRICE_CHANGE_FIELD, priceChangeValue);
        return getAttribute(PRICE_CHANGE_FIELD, "value").equals(priceChangeValue);
    }

    public boolean isPriceChangeGroupByDisplayed() {
        return isElementVisible(PRICE_CHANGE_GROUP_BY);
    }

    public String getGroupByDefaultValue(By groupByLocator) {
        return getTextFromElement(groupByLocator, Duration.ofSeconds(3));
    }

    public boolean isAverageSellingPriceHeaderDisplayed() {
        return isElementVisible(AVERAGE_SELLING_PRICE_HEADER);
    }

    public String getPriceScreenHeading(String heading) {
        By headingLocator = By.xpath("//h4[text()='" + heading + "']");
        return getTextFromElement(headingLocator, Duration.ofSeconds(3));
    }

    public boolean isAverageSellingPriceGroupByDisplayed() {
        return isElementVisible(AVERAGE_SELLING_PRICE_GROUP_BY);
    }

    public boolean isAllRequiredPriceTableButtonsDisplayed() {
        scrollIntoView(PRICE_TABLE_HEADER);
        return isElementVisible(PRICE_TABLE_HEADER) && isElementVisible(SEARCH_INPUT_BOX) && isElementVisible(DOWNLOAD_ICON) && isElementVisible(MORE_OPTIONS_ICON);
    }

    public List<String> getAllSortingDropdownOptions() throws InterruptedException {
        if (!isElementVisible(PRICE_TABLE_SORTING_ICON)) {
            scrollIntoViewAndClick(PRICE_TABLE_SORTING_ICON);
        } else {
            try {
                click(PRICE_TABLE_SORTING_ICON);
            } catch (StaleElementReferenceException e) {
                UI_LOGGER.info("Stale Element Exception occurred while clicking on Sorting Icon. Trying again...");
                click(PRICE_TABLE_SORTING_ICON);
            }
        }
        By sortingDropdownOption = By.xpath("//ul[@role='menu']//span[contains(@class, 'MuiTypography-root MuiTypography-body1')]");
        List<String> sortingDropdownOptionsList = getTextFromElementsMilliseconds(sortingDropdownOption);
        UI_LOGGER.info("Actual Options in Sorting Dropdown are " + sortingDropdownOptionsList);
        return sortingDropdownOptionsList;
    }

    public boolean isPriceChangeExportIconDisplayed() {
        try {
            WebElement exportIcon = findElementVisible(PRICE_CHANGE_CHART_EXPORT_ICON);
            if (!exportIcon.isDisplayed()) {
                scrollIntoView(exportIcon);
                exportIcon = findElementVisible(PRICE_CHANGE_CHART_EXPORT_ICON);
            }
            return exportIcon.isDisplayed();
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e) {
            return false;
        }
    }

    public boolean isAverageSellingPriceExportIconDisplayed() {
        try {
            WebElement exportIcon = findElementVisible(AVERAGE_SELLING_PRICE_CHART_EXPORT_ICON);
            if (!exportIcon.isDisplayed()) {
                scrollIntoView(exportIcon);
                exportIcon = findElementVisible(AVERAGE_SELLING_PRICE_CHART_EXPORT_ICON);
            }
            return exportIcon.isDisplayed();
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e) {
            return false;
        }
    }

    public void clickOnPriceChangeExportIcon() throws InterruptedException {
        click(PRICE_CHANGE_CHART_EXPORT_ICON);
    }

    public void clickOnAverageSellingPriceExportIcon() throws InterruptedException {
        click(AVERAGE_SELLING_PRICE_CHART_EXPORT_ICON);
    }

    public void clickExportButton(String exportButtonName) throws InterruptedException {
        click(By.xpath("//ul[@role='listbox']/li[text()='" + exportButtonName + "']"));
    }

    public List<String> isPriceProductDisplayedInSearchResults() {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR, Duration.ofSeconds(20));
        List<WebElement> listOfItemsFromTable = findElementsVisible(By.xpath("//div[@col-id='rpc']//a"));
        List<String> listOfItems = getTextFromElements(listOfItemsFromTable);
        listOfItems.sort(String::compareToIgnoreCase);
        UI_LOGGER.info("List of items from table is " + listOfItems);
        return listOfItems;
    }

    public Map<String, Map<String, String>> getItemsFromUITable() throws ParseException, InterruptedException {
        By rowLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index]");
        int numberOfRows = getElementCount(rowLocator);
        Map<String, Map<String, String>> statusMap = new HashMap<>();
        Thread.sleep(5000);
        scrollIntoView(rowLocator);

        for (int i = 0; i < numberOfRows; i++) {
            By rpcLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index='" + i + "']/div[@col-id='rpc']//span");
            By statusesLocator = By.xpath("//div[@class='ag-center-cols-container']/div[@row-index='" + i + "']//div[@id='price-table-obeserved-price-cell']/p[1]");
            By tilesInUI = By.xpath("//div[@class='ag-center-cols-container']/div[@row-index='" + i + "']//div[@id='price-table-obeserved-price-cell']/ancestor::div[@role='gridcell']");

            String rpc = null;
            List<String> statusesInUI = null;
            List<String> dateFromUI = null;

            int maxRetries = 3;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try {
                    rpc = getTextFromElement(rpcLocator);
                    List<WebElement> statuses = findElementsVisible(statusesLocator);
                    statusesInUI = getTextFromElements(statuses);
                    dateFromUI = findElementsVisible(tilesInUI).stream().map(element -> element.getAttribute("col-id")).collect(Collectors.toList());

                    break;
                } catch (StaleElementReferenceException e) {
                    UI_LOGGER.info("Stale element exception occurred. Retrying...");
                    retryCount++;
                }
            }

            if (retryCount >= maxRetries) {
                UI_LOGGER.error("Max retries reached. Could not resolve stale element issue.");
                continue;
            }

            for (int j = 0; j < dateFromUI.size(); j++) {
                String date = dateFromUI.get(j);
                String convertedDate = dateAndIntervalPickerPage.convertDateFormat(date);
                statusMap.putIfAbsent(convertedDate, new HashMap<>());
                String status = statusesInUI.get(j).replace("$", "");
                if (status.equals("No Data")) {
                    statusMap.remove(convertedDate).remove(status);
                } else {
                    statusMap.get(convertedDate).put(rpc, status);
                }
            }
        }

        UI_LOGGER.info("Status map is " + statusMap);
        return statusMap;
    }
}
