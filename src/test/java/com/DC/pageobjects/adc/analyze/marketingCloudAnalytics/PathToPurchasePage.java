package com.DC.pageobjects.adc.analyze.marketingCloudAnalytics;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;

public class PathToPurchasePage extends NetNewNavigationMenu {
    private static final By BRAND_FILTER = By.xpath("//div[@id='sidebar-filter-brand']//input");
    private static final By CATEGORY_FILTER = By.xpath("//div[@id='sidebar-filter-category']//input");
    private static final By ITEMS_IN_CATEGORY_FILTER = By.xpath("//div[@id='sidebar-filter-category']//div[@role='button']//span[text()]");
    private static final By ITEMS_IN_BRAND_FILTER = By.xpath("//div[@id='sidebar-filter-brand']//div[@role='button']//span[text()]");
    private static final By APPLY_BUTTON = By.xpath("//button[text()='Apply']");
    private static final By CLEAR_BUTTON = By.xpath("//button[text()='Clear']");
    private static final By SLIDE_BUTTON_NTB_TOGGLE = By.xpath("//input[@aria-label='controlled']");
    private static final By PATH_TO_PURCHASE_BREADCRUMB = By.xpath("//a[text()='Path to Purchase']");
    private static final By FILTER_ELEMENTS = By.xpath("//div[@role='presentation']//span[text()]");

    private final Duration TIMEOUT = Duration.ofSeconds(2);

    public PathToPurchasePage(WebDriver driver) {
        super(driver);
        findElementVisible(PATH_TO_PURCHASE_BREADCRUMB);
    }

    public void selectItemsFromBrandDropdown(List<String> optionsToSelect) throws InterruptedException {
        selectItemsFromDropdownFilter(BRAND_FILTER, optionsToSelect);
        click(APPLY_BUTTON);
    }

    public void selectItemsFromCategoryDropdown(List<String> optionsToSelect) throws InterruptedException {
        selectItemsFromDropdownFilter(CATEGORY_FILTER, optionsToSelect);
        clickElement(APPLY_BUTTON);
    }

    public void clearFilters() {
        clickElement(CLEAR_BUTTON);
    }

    public int getSelectedBrandsCount() {
        return getElementCount(ITEMS_IN_BRAND_FILTER, TIMEOUT);
    }

    public List<String> getSelectedOptionsInBrandFilter() {
        return getTextFromElements(findElementsPresent(ITEMS_IN_BRAND_FILTER, TIMEOUT));
    }

    public List<String> getSelectedOptionsInCategoryFilter() {
        return getTextFromElements(findElementsPresent(ITEMS_IN_CATEGORY_FILTER, TIMEOUT));
    }

    public int getSelectedCategoriesCount() {
        return getElementCount(ITEMS_IN_CATEGORY_FILTER, TIMEOUT);
    }

    public void clickSlideButtonNTB() {
        clickElement(SLIDE_BUTTON_NTB_TOGGLE);
    }

    public List<String> getAvailableOptionsInBrandFilter() throws InterruptedException {
        return openSelectedFilterAndGetAllValues(BRAND_FILTER, FILTER_ELEMENTS);
    }

    public List<String> getAvailableOptionsInCategoryFilter() throws InterruptedException {
        return openSelectedFilterAndGetAllValues(CATEGORY_FILTER, FILTER_ELEMENTS);
    }
}