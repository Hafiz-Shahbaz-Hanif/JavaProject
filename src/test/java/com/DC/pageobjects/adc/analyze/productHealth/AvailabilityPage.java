package com.DC.pageobjects.adc.analyze.productHealth;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.CommonFeatures;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


public class AvailabilityPage extends NetNewNavigationMenu {
    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public CommonFeatures commonFeatures;
    public DCFilters dcFilters;
    private final By AVAILABILITY_HEADER = By.xpath("//h4[text()='Available For Purchase']");
    private final By AVAILABLE_FOR_PURCHASE_HEADER = By.xpath("//h4[text()='Available For Purchase']");
    private final By LIMITED_AVAILABILITY_HEADER = By.xpath("//h4[text()='Limited Availability']");
    private final By UNAVAILABLE_FOR_PURCHASE_HEADER = By.xpath("//h4[text()='Unavailable For Purchase']");
    private final By AVAILABLE_FOR_PURCHASE_CHART = By.id("availability-scorePercentage-widget-scorePercentage-text");
    private final By LIMITED_AVAILABILITY_CHART = By.xpath("//h4[text()='Limited Availability']/parent::div/following-sibling::div//h2");
    private final By UNAVAILABLE_FOR_PURCHASE_CHART = By.xpath("//h4[text()='Unavailable For Purchase']/parent::div/following-sibling::div//h2");
    private final By EDIT_GOAL_ICON = By.xpath("//span[text()='edit']");
    private final By GOAL_VALUE = By.xpath("//span[text()='edit']/../parent::div/h6");
    private final By AVAILABILITY_BY_STATUS_HEADER = By.xpath("//h4[text()='Availability by Status']");
    private final By GROUP_BY_DROPDOWN = By.id("graph-buttons-box-1");
    private final By NUMBER_PERCENTAGE_TOGGLE = By.id("graph-buttons-toggle");
    private final By CHART_LABELS_TOGGLE = By.xpath("//p[text()='Chart Labels']/following-sibling::span");
    private final By EXPORT_ICON_IN_AVAILABILITY_TABLE = By.id("export-graph-form");
    public By PRODUCTS_HEADER = By.xpath("//h4[text()='Products']");
    private final By SEARCH_INPUT_BOX = By.xpath("//input[@placeholder='Search by Product Title or RPC']");
    private final By DOWNLOAD_ICON = By.xpath("//button[contains(@type,'button')]//span[contains(@class,'material-symbols-rounded')][text()='download']");
    private final By MORE_OPTIONS_ICON = By.xpath("//span[text()='more_vert']");
    public final By SORTING_AVAILABILITY_ICON = By.xpath("//div[@role='columnheader']//span[text()='filter_alt'][1]");
    public final By EXPORT_ICON_IN_PRODUCT_TABLE = By.xpath("//button[@type='button']//span[@class='material-symbols-rounded'][text()='download']");


    public AvailabilityPage(WebDriver driver) {
        super(driver);
        findElementVisible(AVAILABILITY_HEADER);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
        commonFeatures = new CommonFeatures(driver);
        dcFilters = new DCFilters(driver);
    }

    public boolean isAvailabilityScreenDisplayed() {
        return isElementVisible(AVAILABILITY_HEADER);
    }

    public boolean isAllThreeHeadersDisplayed() {
        return isElementVisible(AVAILABLE_FOR_PURCHASE_HEADER) && isElementVisible(LIMITED_AVAILABILITY_HEADER) && isElementVisible(UNAVAILABLE_FOR_PURCHASE_HEADER);
    }

    public boolean isAllTheChartsDisplayed() {
        return isElementVisible(AVAILABLE_FOR_PURCHASE_CHART) && isElementVisible(LIMITED_AVAILABILITY_CHART) && isElementVisible(UNAVAILABLE_FOR_PURCHASE_CHART);
    }

    public String getValueOfAvailableForPurchaseChart() {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        WebElement availableForPurchaseElement = findElementVisible(AVAILABLE_FOR_PURCHASE_CHART);
        UI_LOGGER.info("Available for purchase value is: " + availableForPurchaseElement.getText());
        return availableForPurchaseElement.getText();
    }

    public String getValueOfLimitedAvailabilityChart() {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        WebElement limitedAvailabilityElement = findElementVisible(LIMITED_AVAILABILITY_CHART);
        UI_LOGGER.info("Limited availability value is: " + limitedAvailabilityElement.getText());
        return limitedAvailabilityElement.getText();
    }

    public String getValueOfUnavailableForPurchaseChart() {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        WebElement unavailableForPurchaseElement = findElementVisible(UNAVAILABLE_FOR_PURCHASE_CHART);
        UI_LOGGER.info("Unavailable for purchase value is: " + unavailableForPurchaseElement.getText());
        return unavailableForPurchaseElement.getText();
    }

    public int isAllTheChartsSumUpTo100() {
        int sum;
        int availableForPurchaseValue = Integer.parseInt(getValueOfAvailableForPurchaseChart().replaceAll("%", ""));
        UI_LOGGER.info("Available for purchase value is: " + availableForPurchaseValue);
        int limitedAvailabilityValue = Integer.parseInt(getValueOfLimitedAvailabilityChart().replaceAll("%", ""));
        UI_LOGGER.info("Limited availability value is: " + limitedAvailabilityValue);
        int unavailableForPurchaseValue = Integer.parseInt(getValueOfUnavailableForPurchaseChart().replaceAll("%", ""));
        UI_LOGGER.info("Unavailable for purchase value is: " + unavailableForPurchaseValue);

        sum = availableForPurchaseValue + limitedAvailabilityValue + unavailableForPurchaseValue;
        UI_LOGGER.info("Sum of all the buckets is: " + sum);
        return sum;
    }

    public boolean isAvailableForPurchaseChartColorChangeBasedOnPerformance(int failurePercentage, int successPercentage) {
        int availableForPurchaseValue = Integer.parseInt(getValueOfAvailableForPurchaseChart().replace("%", ""));

        WebElement availableForPurchaseElement = findElementVisible(By.xpath("//h4[text()='Available For Purchase']/parent::div/following-sibling::div/div"));

        String bucketColor = availableForPurchaseElement.getCssValue("background-color");

        String expectedColor;
        String colorName;
        if (availableForPurchaseValue <= failurePercentage) {
            expectedColor = "rgba(244, 110, 110, 1)"; // Red color for failure
            colorName = "Red";
        } else if (availableForPurchaseValue >= successPercentage) {
            expectedColor = "rgba(103, 192, 101, 1)"; // Green color for pass
            colorName = "Green";
        } else {
            expectedColor = "rgba(255, 204, 72, 1)"; // Yellow color for in-between
            colorName = "Yellow";
        }

        UI_LOGGER.info("Color of the bucket is: " + colorName + ", for Success percentage: " + availableForPurchaseValue);
        Assert.assertEquals(bucketColor, expectedColor, "Incorrect color for percentage: " + availableForPurchaseValue);
        return true;
    }

    public boolean isEditGoalIconDisplayed() {
        return isElementVisible(EDIT_GOAL_ICON);
    }

    public void changeGoalSettings(String field, String newGoal) throws InterruptedException {
        click(EDIT_GOAL_ICON);

        By goalValueInputBox = By.xpath("//span[text()='" + field + "']/following-sibling::div//input");
        clearInput(goalValueInputBox);
        setText(goalValueInputBox, newGoal);
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        hitEscKey();
    }

    public boolean isSuccessGoalChanged() {
        WebElement setGoalValue = findElementVisible(GOAL_VALUE);

        int newGoal = getNewGoalValue();
        UI_LOGGER.info("Success goal that user enters is: " + newGoal + "%");
        UI_LOGGER.info("Success goal in UI is: " + setGoalValue.getText().replace("Goal ", "").trim());

        return setGoalValue.getText().equalsIgnoreCase("Goal " + newGoal + "%");
    }

    public int getNewGoalValue() {
        WebElement goalValue = findElementVisible(GOAL_VALUE);
        String goalValueText = goalValue.getText();
        String goalValueNumber = goalValueText.replace("Goal ", "").trim();
        String goalValueNumberWithoutPercentage = goalValueNumber.replace("%", "");
        return Integer.parseInt(goalValueNumberWithoutPercentage);
    }

    public void clickEditGoalIcon() throws InterruptedException {
        click(EDIT_GOAL_ICON);
    }

    public boolean isAllRequiredFieldsDisplayed() {
        return isElementVisible(AVAILABILITY_BY_STATUS_HEADER) && isElementVisible(GROUP_BY_DROPDOWN) && isElementVisible(NUMBER_PERCENTAGE_TOGGLE) && isElementVisible(CHART_LABELS_TOGGLE)
                && isElementVisible(EXPORT_ICON_IN_AVAILABILITY_TABLE);
    }

    public boolean isAllTheFieldsInGroupByDropdownDisplayed() throws InterruptedException {
        click(GROUP_BY_DROPDOWN);
        List<WebElement> groupByDropdownOptions = findElementsVisible(By.xpath("//ul[@class='MuiList-root MuiList-padding MuiMenu-list css-r8u8y9']/li"));
        for (WebElement groupByDropdownOption : groupByDropdownOptions) {
            String groupByDropdownOptionText = groupByDropdownOption.getText();
            if (groupByDropdownOptionText.equalsIgnoreCase("Total Catalog")) {
                Assert.assertTrue(groupByDropdownOption.getAttribute("aria-selected").equalsIgnoreCase("true"), "Total Catalog is not selected by default");
            }
        }
        return true;
    }

    public void clickByCategoryInGroupByDropdown() throws InterruptedException {
        click(By.xpath("//ul[@class='MuiList-root MuiList-padding MuiMenu-list css-r8u8y9']/li[text()='By Category']"));
    }

    public boolean isAllRequiredSKUFieldsDisplayed() {
        scrollIntoView(PRODUCTS_HEADER);
        return isElementVisible(PRODUCTS_HEADER) && isElementVisible(SEARCH_INPUT_BOX) && isElementVisible(DOWNLOAD_ICON) && isElementVisible(MORE_OPTIONS_ICON);
    }

    public List getAllOptionsInMoreOptionsDropdown() throws InterruptedException {
        click(MORE_OPTIONS_ICON);
        List<WebElement> options = findElementsVisible(By.xpath("//ul[@class='MuiList-root MuiList-padding MuiMenu-list css-r8u8y9']/li//span/following-sibling::span"));
        List<String> optionsList = new ArrayList<>();

        for (WebElement option : options) {
            optionsList.add(option.getText());
        }
        return optionsList;
    }

    public boolean isAllOptionsInMoreOptionsDropdownCheckedByDefault() {
        List<WebElement> options = findElementsVisible(By.xpath("//ul[@role='menu']/li//input"));
        return options.stream().allMatch(WebElement::isSelected);
    }

    public void isColorOfTheBucketCorrect(String status, WebElement statusElement) {

        List<String> greenTextValues = Collections.singletonList("In Stock");
        List<String> yellowTextValues = Arrays.asList("Limited Availability", "Delayed Fulfillment", "In Stores Only", "Pre Order", "Not Released Yet");
        List<String> redTextValues = Arrays.asList("Discontinued", "Lost Buy Box", "No Buy Box Offers", "Marketplace Only", "Out of Stock", "Void", "Andon Cord");

        String expectedColor;
        if (greenTextValues.contains(status)) {
            expectedColor = "rgba(103, 192, 101, 1)";
        } else if (yellowTextValues.contains(status)) {
            expectedColor = "rgba(255, 204, 72, 1)";
        } else if (redTextValues.contains(status)) {
            expectedColor = "rgba(244, 110, 110, 1)";
        } else {
            expectedColor = "rgba(237, 239, 241, 1)";
        }

        WebElement parentElement = statusElement.findElement(By.xpath(".."));
        String backgroundColor = parentElement.getCssValue("background-color");

        UI_LOGGER.info("Column text is: " + status + " and column color is: " + backgroundColor + " and expected color is: " + expectedColor);
        Assert.assertEquals(backgroundColor, expectedColor, "Column text is: " + status + " and column color is: " + backgroundColor + " and expected color is: " + expectedColor);

    }

    public List<String> getAllSortingDropdownOptions() throws InterruptedException {
        click(SORTING_AVAILABILITY_ICON);
        By sortingDropdownOption = By.xpath("//ul[@role='menu']//span[contains(@class, 'MuiTypography-root MuiTypography-body1')]");
        List<String> sortingDropdownOptionsList = getTextFromElementsMilliseconds(sortingDropdownOption);
        UI_LOGGER.info("Actual Options in Sorting Dropdown are " + sortingDropdownOptionsList);
        return sortingDropdownOptionsList;
    }

    public void clickAvailabilityExportIcon() throws InterruptedException {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        scrollToTopOfPage();
        click(EXPORT_ICON_IN_AVAILABILITY_TABLE);
    }

    public void clickProductsExportIcon() throws InterruptedException {
        scrollIntoView(EXPORT_ICON_IN_PRODUCT_TABLE);
        click(EXPORT_ICON_IN_PRODUCT_TABLE);
    }

    public List<String> getAllOptionsInExportDropdown() {
        By exportDropdownOption = By.xpath("//ul[@role='listbox']/li");
        List<String> exportDropdownOptionsList = getTextFromElementsMilliseconds(exportDropdownOption);
        UI_LOGGER.info("Actual Options in Export Dropdown are " + exportDropdownOptionsList);
        return exportDropdownOptionsList;
    }

    public void clickExportButton(String exportButtonName) throws InterruptedException {
        click(By.xpath("//ul[@role='listbox']/li[text()='" + exportButtonName + "']"));
    }

    public boolean isCategoryFilterWorking(String filterName) throws InterruptedException {
        List<String> listOfItemsFromTable = commonFeatures.getListOfItemsFromTable("cell-productTitle");
        return listOfItemsFromTable.stream().allMatch(item -> item.equalsIgnoreCase(filterName));
    }

    public Map<String, Map<String, String>> getItemsFromUITable() throws ParseException {
        By rowLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index]");
        int numberOfRows = getElementCount(rowLocator);
        Map<String, Map<String, String>> statusMap = new HashMap<>();

        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        scrollIntoView(rowLocator);

        for (int i = 0; i < numberOfRows; i++) {
            By rpcLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index='" + i + "']/div[@col-id='rpc']//span");
            By statusesLocator = By.xpath("//div[@class='ag-center-cols-container']/div[@row-index='" + i + "']//div[@id='variation-cell-box']/p[1]");
            By tilesInUI = By.xpath("//div[@class='ag-center-cols-container']/div[@row-index='" + i + "']//div[@id='variation-cell-box']/ancestor::div[@role='gridcell']");

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

                    UI_LOGGER.info("Checking color of the statuses");
                    for (WebElement statusElement : statuses) {
                        String statusText = statusElement.getText();
                        isColorOfTheBucketCorrect(statusText, statusElement);
                    }

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
                String status = statusesInUI.get(j);
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

    public List<String> isProductDisplayedInSearchResults() {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        List<WebElement> listOfItemsFromTable = findElementsVisible(By.xpath("//a[@id='rpc-col-link']"));
        List<String> listOfItems = getTextFromElements(listOfItemsFromTable);
        listOfItems.sort(String::compareToIgnoreCase);
        UI_LOGGER.info("List of items from table is " + listOfItems);
        return listOfItems;
    }
}
