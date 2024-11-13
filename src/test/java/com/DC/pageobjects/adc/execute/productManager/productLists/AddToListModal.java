package com.DC.pageobjects.adc.execute.productManager.productLists;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddToListModal extends InsightsNavigationMenu {
    protected final String ADD_TO_LIST_MODAL_XPATH = "//div[@data-qa='AddProductInstancesToList']";
    protected final By ADD_BUTTON_LOCATOR = By.xpath(ADD_TO_LIST_MODAL_XPATH + "//button[@data-qa='Button' and contains(text(),'Add')]");
    protected final By SEARCH_LIST_INPUT_LOCATOR = By.xpath(ADD_TO_LIST_MODAL_XPATH + "//input[@placeholder='Search lists']");
    protected final By MODAL_INFO_LOCATOR = By.xpath(ADD_TO_LIST_MODAL_XPATH + "/div/span");
    protected final By APPLY_BUTTON_LOCATOR = By.xpath(ADD_TO_LIST_MODAL_XPATH + "//button[text()='Apply']");
    protected final By FILTER_CHIPS_LOCATOR = By.xpath(ADD_TO_LIST_MODAL_XPATH + "//span[contains(@class,'Chip')]");
    protected final By NEW_LIST_INPUT_LOCATOR = By.xpath(ADD_TO_LIST_MODAL_XPATH + "//input[contains(@placeholder,'Enter name of new list')]");
    protected final By SAVE_BUTTON_LOCATOR = By.xpath(ADD_TO_LIST_MODAL_XPATH + "//button[text()='save']");

    public AddToListModal(WebDriver driver) {
        super(driver);
        findElementVisibleMilliseconds(ADD_BUTTON_LOCATOR);
        String modalInfo = getTextFromElementMilliseconds(MODAL_INFO_LOCATOR);
        if (modalInfo.contains("0 item")) {
            throw new NoSuchElementException("Unable to continue; 0 items were going to be added to the list");
        }
    }

    public AddToListModal searchList(String listName) {
        clickElement(SEARCH_LIST_INPUT_LOCATOR);
        setText(SEARCH_LIST_INPUT_LOCATOR, listName);
        return this;
    }

    public AddToListModal selectList(String listName) {
        searchList(listName);
        By listElementLocator = By.xpath(ADD_TO_LIST_MODAL_XPATH + "//li[child::span[text()='" + listName + "']]");
        boolean listExists = isElementVisibleMilliseconds(listElementLocator);
        if (!listExists) {
            throw new NoSuchElementException("Unable to find list " + listName + " or dropdown menu is not visible");
        }
        selectElement(listElementLocator);
        return this;
    }

    public AddToListModal clickApplyButton() {
        clickElement(APPLY_BUTTON_LOCATOR);
        return this;
    }

    public List<String> getFilterChipsDisplayed() {
        return getTextFromElementsMilliseconds(FILTER_CHIPS_LOCATOR);
    }

    public List<String> getListsInSearchInputValue() {
        return Arrays.stream(getAttribute(SEARCH_LIST_INPUT_LOCATOR, "value").split(",")).map(String::trim).collect(Collectors.toList());
    }

    public AddToListModal addNewList(AbstractMap.SimpleEntry<String, Enums.ProductListPermission> listToAdd) {
        By permissionRadioCheckboxLocator = By.xpath(ADD_TO_LIST_MODAL_XPATH + "//span[following-sibling::span[text()='" + listToAdd.getValue().getPermissionTypeForUI() + "']]/input");
        setText(NEW_LIST_INPUT_LOCATOR, listToAdd.getKey());
        clickElement(permissionRadioCheckboxLocator);
        clickElement(SAVE_BUTTON_LOCATOR);
        return this;
    }

    public void clickAddButton() {
        clickElement(ADD_BUTTON_LOCATOR);
        waitForElementToBeInvisible(ADD_BUTTON_LOCATOR, Duration.ofSeconds(3));
    }
}
