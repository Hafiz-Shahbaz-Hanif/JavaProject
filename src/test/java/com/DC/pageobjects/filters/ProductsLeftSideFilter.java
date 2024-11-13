package com.DC.pageobjects.filters;

import com.DC.pageobjects.PageHandler;
import com.DC.utilities.sharedElements.SingleSelectDropdown;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductsLeftSideFilter extends PageHandler {
    public final By OPEN_FILTER_ICON = By.xpath("//button[@aria-label='filterlist']//*[@data-testid='FilterAltOutlinedIcon']");
    public final By CLOSE_FILTER_ICON = By.xpath("//button[@aria-label='filterlist']//*[@data-testid='FirstPageOutlinedIcon']");
    public final By APPLY_BUTTON = By.xpath("//div[contains(@class,'Container-root')]//button[text()='Apply' and not(ancestor::div[contains(@class,'menu')])]");
    public final By CLEAR_BUTTON = By.xpath("//div[contains(@class,'Container-root')]//button[text()='Clear']");
    public final By DROPDOWN_APPLY_BUTTON = By.xpath("//div[contains(@class,'menu')]//button[text()='Apply']");

    public enum FilterType {
        BASE("Base"),
        RETAILER("Retailer"),
        CAMPAIGN("Campaign"),
        RETAILER_CAMPAIGN("Retailer Campaign");

        private final String filterType;

        FilterType(String filterType) {
            this.filterType = filterType;
        }
    }

    public ProductsLeftSideFilter(WebDriver driver) {
        super(driver);
    }

    public void selectFilter(FilterType filterType, List<String> pathsToSelect) throws InterruptedException {
        if (filterType.equals(FilterType.BASE)) {
            openDropdownMenu(filterType);
            searchInsideDropdown("");
            pathsToSelect.forEach(this::selectItemFromDropdownMenu);
            clickApplyButtonInDropdown();
            pathsToSelect.forEach(filterPath -> waitForFilterChip(filterType, filterPath));
        } else {
            for (var filterPath : pathsToSelect) {
                openDropdownMenu(filterType);
                searchInsideDropdown("");
                var singleSelect = new SingleSelectDropdown(driver);
                var pathParts = filterPath.split("\\|");
                Arrays.stream(pathParts).forEach(singleSelect::selectOption);
                waitForFilterChip(filterType, filterPath);
            }
        }
    }

    public void waitForFilterChip(FilterType filterType, String pathOfFilter) {
        var locator = getDropdownLocator(filterType) + "//span[text()='" + pathOfFilter + "']";
        findElementVisible(By.xpath(locator));
    }

    public void openDropdownMenu(FilterType filterType) throws InterruptedException {
        var locator = getDropdownLocator(filterType);
        var expandMoreButton = By.xpath(locator + "//button[text()='expand_more']");
        openDropdownMenu(expandMoreButton);
    }

    public void openDropdownMenu(By dropdown) throws InterruptedException {
        if (isElementVisibleMilliseconds(dropdown)) {
            click(dropdown);
        }
    }

    public void selectItemFromDropdownMenu(String itemToSelect) {
        By dropdownTextXpath = By.xpath("//li[descendant::span[text()='" + itemToSelect + "']]//input");
        selectElement(dropdownTextXpath);
    }

    public void clearAll() throws InterruptedException {
        click(CLEAR_BUTTON);
    }

    public void clearAllAndApply() throws InterruptedException {
        clearAll();
        applyFilter();
    }

    public void applyFilter() throws InterruptedException {
        click(APPLY_BUTTON);
        waitForDOMStabilization();
    }

    public <T> T applyFilter(Class<T> pageClass) throws InterruptedException {
        applyFilter();
        return getPage(pageClass);
    }

    public void clickApplyButtonInDropdown() throws InterruptedException {
        click(DROPDOWN_APPLY_BUTTON);
        waitForDOMStabilization();
    }

    public boolean isLeftSideFilterDisplayed() {
        return isElementVisibleMilliseconds(CLOSE_FILTER_ICON);
    }

    public void closeFilter() throws InterruptedException {
        if (isLeftSideFilterDisplayed()) {
            moveToElementAndClick(CLOSE_FILTER_ICON);
        }
    }

    public void openFilter() throws InterruptedException {
        if (!isLeftSideFilterDisplayed()) {
            moveToElementAndClick(OPEN_FILTER_ICON);
        }
    }

    public List<String> getFilterValues(FilterType filterType) {
        var filterChipsXPath = getDropdownLocator(filterType) + "//span";
        return getTextFromElementsMilliseconds(By.xpath(filterChipsXPath));
    }

    public Map<FilterType, List<String>> getAllAppliedFilters() {
        Map<FilterType, List<String>> filterValues = new HashMap<>();
        for (FilterType filterType : FilterType.values()) {
            filterValues.put(filterType, getFilterValues(filterType));
        }
        return filterValues;
    }

    public void searchInsideDropdown(String searchValue) {
        var searchInputLocator = By.xpath("//div[contains(@class,'menu')]//input");
        setText(searchInputLocator, searchValue);
    }

    public List<String> getDropdownOptions() {
        var menuItemsLocator = By.xpath("//div[contains(@class,'menu')]//li[not(child::button)]");
        var elements = findElementsPresentMilliseconds(menuItemsLocator);
        return elements.stream()
                .map(element -> element.getAttribute("textContent"))
                .collect(Collectors.toList());
    }

    public void clickBackToPreviousMenuButton() {
        var backArrowLocator = By.xpath("//li[text()='Back to previous menu']");
        clickElement(backArrowLocator);
    }

    public void removeFilterChip(String filterChip) {
        var removeFilterChipLocator = By.xpath("//span[text()='" + filterChip + "']/following-sibling::i");
        clickElement(removeFilterChipLocator);
        waitForElementToBeInvisible(removeFilterChipLocator);
    }

    public int getFiltersAppliedCount() {
        var filterChipLocator = By.xpath("//div[child::div[contains(@data-qa,'version-filter')]]//span");
        return getElementCountMilliseconds(filterChipLocator);
    }

    private String getDropdownLocator(FilterType filterType) {
        var dataQA = "";
        switch (filterType) {
            case BASE:
                dataQA = "version-filter-locale-locale-selection";
                break;
            case RETAILER:
                dataQA = "version-filter-retailer-locale-selection";
                break;
            case CAMPAIGN:
                dataQA = "version-filter-campaign-locale-selection";
                break;
            case RETAILER_CAMPAIGN:
                dataQA = "version-filter-retailer-campaign-locale-selection";
                break;
            default:
                throw new IllegalArgumentException("Invalid filter type: " + filterType);
        }
        return "//div[child::div[@data-qa='" + dataQA + "']]";
    }
}
