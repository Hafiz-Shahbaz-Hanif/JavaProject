package com.DC.pageobjects.filters;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class MultiselectWithSortFilter extends PageHandler {
    protected final String MENU_XPATH = "//div[@data-qa='DropdownMenu']";
    protected final By CHECKBOX = By.xpath(MENU_XPATH + "//li//input");
    protected final By UPDATE_BUTTON = By.xpath(MENU_XPATH + "//button[text()='Update']");
    protected final By CHECKED_OPTIONS = By.xpath("//li[descendant::*[@data-testid='CheckBoxIcon']]//span[text()]");
    protected final By DISPLAYED_OPTIONS = By.xpath("//div[@data-qa='DropdownMenu']/div//div//li/span[2]");
    protected final By SEARCH_INPUT_LOCATOR = By.xpath("//div[@data-qa='DropdownMenu']//input[@type='text']");

    protected final Duration MAX_WAIT_TIME_SECS = Duration.ofSeconds(3);

    public MultiselectWithSortFilter(WebDriver driver) {
        super(driver);
        findElementVisible(UPDATE_BUTTON, MAX_WAIT_TIME_SECS);
    }

    public void search(String searchTerm) {
        setText(SEARCH_INPUT_LOCATOR, searchTerm);
    }

    public void selectOption(String option) {
        By optionLocator = By.xpath("//li[descendant::span[text()='" + option + "']]//input");
        selectElement(optionLocator);
    }

    public <T> T selectOptionAndApplyChanges(String option, Class<T> parentPage) throws InterruptedException {
        selectOption(option);
        clickUpdateButton();
        return getPage(parentPage);
    }

    public void selectOptions(List<String> options) {
        deselectAllElements(CHECKBOX, MAX_WAIT_TIME_SECS);
        for (String option : options) {
            selectOption(option);
        }
    }

    public void clickUpdateButton() throws InterruptedException {
        click(UPDATE_BUTTON);
        waitForElementToBeInvisibleInMilliseconds(UPDATE_BUTTON);
    }

    public <T> T clickUpdateButton(Class<T> parentPage) throws InterruptedException {
        clickUpdateButton();
        return getPage(parentPage);
    }

    public List<String> getAllSelectedOptions() {
        List<WebElement> selectedOptions = findElementsVisibleMilliseconds(DISPLAYED_OPTIONS);
        return getTextFromElements(selectedOptions);
    }

    public void selectSortOption(String sortOption) {
        var sortOptionLocator = By.xpath("//div[@data-qa='Sort']//span[contains(text(),'" + sortOption + "')]");
        clickElement(sortOptionLocator);
    }

    public List<String> getAllOptions() {
        List<WebElement> options = findElementsVisibleMilliseconds(DISPLAYED_OPTIONS);
        return getTextFromElements(options);
    }
}
