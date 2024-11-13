package com.DC.pageobjects.filters;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class InsightsTimestampFilter extends PageHandler {
    public final String DROPDOWN_MENU_XPATH = "//div[@data-qa='DropdownMenu']";
    public final By UPDATE_BUTTON_LOCATOR = By.xpath(DROPDOWN_MENU_XPATH + "//button");

    public InsightsTimestampFilter(WebDriver driver) {
        super(driver);
        findElementVisible(UPDATE_BUTTON_LOCATOR, Duration.ofSeconds(3));
    }

    public String getSelectedSortOption() {
        var selectedSortOptionLocator = By.xpath("//div[@data-qa='Sort']//div[@class='_1vh6axjj']//span");
        var isVisible = isElementVisibleMilliseconds(selectedSortOptionLocator);
        return !isVisible ? null : getTextFromElement(selectedSortOptionLocator).replace("Sort ", "");
    }

    public String getSelectedDateFilterOption() {
        var selectedSortOptionLocator = By.xpath("//div[@data-qa='DateFilterOptions']//div[contains(@class,'_1ptvpppy')]/span[1]");
        var isVisible = isElementVisibleMilliseconds(selectedSortOptionLocator);
        return !isVisible ? "Date Range" : getTextFromElement(selectedSortOptionLocator);
    }

    public void clickCancelButton() {
        var cancelButtonLocator = By.xpath(DROPDOWN_MENU_XPATH + "//span[text()='Cancel']");
        clickElement(cancelButtonLocator);
    }

    public void selectSortOption(String sortOption) {
        var sortOptionLocator = By.xpath("//div[@data-qa='Sort']//span[contains(text(),'" + sortOption + "')]");
        clickElement(sortOptionLocator);
    }

    public void selectDateFilterOption(String dateFilterOption) {
        var sortOptionLocator = By.xpath("//div[@data-qa='DateFilterOptions']//span[contains(text(),'" + dateFilterOption + "')]");
        clickElement(sortOptionLocator);
    }

    public void selectDateRange(String startDate, String endDate) {
        var dateInputBaseLocator = "(//div[@data-qa='DatePickerComponent']//input)";
        var startDateLocator = By.xpath(dateInputBaseLocator + "[1]");
        var endDateLocator = By.xpath(dateInputBaseLocator + "[2]");
        setTextAndHitEnter(startDateLocator, startDate);
        setTextAndHitEnter(endDateLocator, endDate);
    }

    public <T> T clickUpdateButton(Class<T> parentPage) {
        clickElement(UPDATE_BUTTON_LOCATOR);
        waitForElementToBeInvisible(UPDATE_BUTTON_LOCATOR, Duration.ofSeconds(3));
        return getPage(parentPage);
    }
}
