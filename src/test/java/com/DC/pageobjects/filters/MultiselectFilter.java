package com.DC.pageobjects.filters;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class MultiselectFilter extends PageHandler {
    protected final String MENU_XPATH = "//div[contains(@class,'MuiPaper-menu')]";
    protected final By CHECKBOX = By.xpath(MENU_XPATH + "//li//input");
    protected final By APPLY_BUTTON = By.xpath(MENU_XPATH + "//button[text()='Apply']");
    protected final By CHECKED_OPTIONS = By.xpath(MENU_XPATH + "//li[descendant::*[@data-testid='CheckBoxIcon']]");

    protected final Duration MAX_WAIT_TIME_SECS = Duration.ofSeconds(3);
    protected final Duration MAX_WAIT_TIME_MILLIS = Duration.ofMillis(300);

    public MultiselectFilter(WebDriver driver) {
        super(driver);
    }

    public boolean isFilterMenuOpen() {
        return isElementVisibleMilliseconds(APPLY_BUTTON);
    }

    public void clickFilterButton(String filterTitle) throws InterruptedException {
        By filterButton = By.xpath("//button[text()='" + filterTitle + "']");
        click(filterButton);
    }

    public void openFilter(String filterTitle) throws InterruptedException {
        if (!isFilterMenuOpen()) {
            clickFilterButton(filterTitle);
        }
        findElementVisible(APPLY_BUTTON, MAX_WAIT_TIME_SECS);
    }

    public void closeFilter(String filterTitle) throws InterruptedException {
        if (isFilterMenuOpen()) {
            clickFilterButton(filterTitle);
        }
        waitForElementToBeInvisibleInMilliseconds(APPLY_BUTTON);
    }

    public void deselectAllOptions() {
        deselectAllElements(CHECKBOX, MAX_WAIT_TIME_MILLIS);
    }

    public void selectAllOptions() {
        selectAllElements(CHECKBOX, MAX_WAIT_TIME_MILLIS);
    }

    public void deselectAllAndSelectOption(String option) {
        deselectAllOptions();
        selectOption(option);
    }

    public void deselectOption(String option) {
        By optionLocator = getOptionCheckboxLocator(option);
        deselectElement(optionLocator);
    }

    public void selectOption(String option) {
        By optionLocator = getOptionCheckboxLocator(option);
        selectElement(optionLocator);
    }

    public void selectOptionAndApplyChanges(String option) throws InterruptedException {
        selectOption(option);
        applyFilter();
    }

    public void selectOptions(List<String> options) {
        deselectAllElements(CHECKBOX, MAX_WAIT_TIME_SECS);
        for (String option : options) {
            selectOption(option);
        }
    }

    public boolean isOptionDisplayed(String option) {
        By optionLocator = By.xpath(MENU_XPATH + "//li[span[following-sibling::span[text()='" + option + "']]]");
        return isElementVisible(optionLocator, MAX_WAIT_TIME_SECS);
    }

    public void applyFilter() throws InterruptedException {
        click(APPLY_BUTTON);
        waitForElementToBeInvisibleInMilliseconds(APPLY_BUTTON);
    }

    public <T> T applyFilter(Class<T> parentPage) throws InterruptedException {
        applyFilter();
        return (T) getPage(parentPage);
    }

    public void selectOptionsAndApplyChanges(List<String> options) throws InterruptedException {
        selectOptions(options);
        applyFilter();
    }

    public List<String> getAllSelectedOptions() {
        List<WebElement> selectedOptions = findElementsVisibleMilliseconds(CHECKED_OPTIONS);
        return getTextFromElements(selectedOptions);
    }

    protected By getOptionCheckboxLocator(String option) {
        return By.xpath("//div[contains(@class,'MuiPaper-menu')]//li//span[following-sibling::span[text()='" + option + "']]/input");
    }
}
