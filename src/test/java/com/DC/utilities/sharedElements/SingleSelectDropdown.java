package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class SingleSelectDropdown extends PageHandler {
    protected final String MENU_XPATH = "//div[contains(@class,'MuiPaper-menu')]";
    protected final By MENU_ITEMS_LOCATOR = By.xpath(MENU_XPATH + "//li");

    public SingleSelectDropdown(WebDriver driver) {
        super(driver);
    }

    public void openDropdownMenu(String dropdownTitle) throws InterruptedException {
        String dropdownXpath = "//button[text()='" + dropdownTitle + "']";
        By dropdownMenuLocator = By.xpath(dropdownXpath + "/../following-sibling::div");
        By dropdownLocator = By.xpath(dropdownXpath);

        if (!isElementVisibleMilliseconds(dropdownMenuLocator)) {
            scrollIntoViewAndClick(dropdownLocator);
        }
    }

    // USE THIS IF THERE ARE MULTIPLE DROPDOWNS WITH THE SAME TITLE
    public void openDropdownMenu(String dropdownTitle, int dropdownPosition) throws InterruptedException {
        String dropdownXpath = "(//button[text()='" + dropdownTitle + "'])[" + dropdownPosition + "]";
        By dropdownMenuLocator = By.xpath(dropdownXpath + "/../following-sibling::div");
        By dropdownLocator = By.xpath(dropdownXpath);

        if (!isElementVisibleMilliseconds(dropdownMenuLocator)) {
            scrollIntoViewAndClick(dropdownLocator);
        }
    }

    public void openDropdownMenu(By dropdownLocator) {
        By dropdownMenuLocator = By.xpath(MENU_XPATH + "//li");
        if (!isElementVisibleMilliseconds(dropdownMenuLocator)) {
            clickElement(dropdownLocator);
        }
        findElementVisible(dropdownMenuLocator, Duration.ofSeconds(3));
    }

    public void closeDropdownMenu(By closeIconLocator) {
        By dropdownMenuLocator = By.xpath(MENU_XPATH);
        if (isElementVisibleMilliseconds(dropdownMenuLocator)) {
            clickElement(closeIconLocator);
        }
        waitForElementToBeInvisible(dropdownMenuLocator);
    }

    public void openDropdownMenu(By dropdownLocator, By dropdownMenuLocator) {
        if (!isElementVisibleMilliseconds(dropdownMenuLocator)) {
            clickElement(dropdownLocator);
        }
        findElementVisibleMilliseconds(dropdownMenuLocator);
    }

    public void selectOption(String option) {
        By optionLocator = By.xpath(MENU_XPATH + "//li[text()=\"" + option + "\"]");
        selectElement(optionLocator);
        waitForElementToBeInvisibleInMilliseconds(optionLocator);
    }

    public <T> T selectOption(String option, Class<T> clazz) {
        selectOption(option);
        return getPage(clazz);
    }

    public List<String> getDropdownOptions() {
        var elements = findElementsPresentMilliseconds(MENU_ITEMS_LOCATOR);
        return elements.stream()
                .map(element -> element.getAttribute("textContent"))
                .collect(Collectors.toList());
    }
}
