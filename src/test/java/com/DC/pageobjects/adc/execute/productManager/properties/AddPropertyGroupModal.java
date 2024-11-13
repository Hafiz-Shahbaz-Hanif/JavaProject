package com.DC.pageobjects.adc.execute.productManager.properties;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.AbstractMap;

public class AddPropertyGroupModal extends InsightsNavigationMenu {
    private final String ADD_PROPERTY_GROUP_MODAL_XPATH = "//div[@data-qa='AddPropertyGroupPV']";
    private final By MODAL_LOCATOR = By.xpath(ADD_PROPERTY_GROUP_MODAL_XPATH);
    private final By GROUP_NAME_INPUT_LOCATOR = By.xpath(ADD_PROPERTY_GROUP_MODAL_XPATH + "//input[@type='text']");
    private final By CHECKBOX_LOCATOR = By.xpath(ADD_PROPERTY_GROUP_MODAL_XPATH + "//input[@type='checkbox']");
    private final By ADD_BUTTON_LOCATOR = By.xpath(ADD_PROPERTY_GROUP_MODAL_XPATH + "//button[text()='Add']");
    private final By CANCEL_BUTTON_LOCATOR = By.xpath(ADD_PROPERTY_GROUP_MODAL_XPATH + "//button[text()='Cancel']");
    private final By ADD_AND_CLOSE_BUTTON_LOCATOR = By.xpath(ADD_PROPERTY_GROUP_MODAL_XPATH + "//button[text()='Add & Close']");

    public AddPropertyGroupModal(WebDriver driver) {
        super(driver);
        findElementVisibleMilliseconds(MODAL_LOCATOR);
    }

    public void insertGroupInfo(AbstractMap.SimpleEntry<String, Boolean> groupInfo) {
        setText(GROUP_NAME_INPUT_LOCATOR, groupInfo.getKey());
        if (groupInfo.getValue()) {
            selectElement(CHECKBOX_LOCATOR);
        } else {
            deselectElement(CHECKBOX_LOCATOR);
        }
    }

    public void clickAddButton() {
        clickElement(ADD_BUTTON_LOCATOR);
    }

    public PropertiesPage clickCancelButton() {
        clickElement(CANCEL_BUTTON_LOCATOR);
        waitForElementToBeInvisible(MODAL_LOCATOR, MAX_WAIT_TIME_SECS);
        return new PropertiesPage(driver);
    }

    public PropertiesPage clickAddAndCloseButton() {
        clickElement(ADD_AND_CLOSE_BUTTON_LOCATOR);
        waitForElementToBeInvisible(MODAL_LOCATOR, MAX_WAIT_TIME_SECS);
        return new PropertiesPage(driver);
    }

    public boolean areAddButtonsEnabled() {
        return isElementEnabled(ADD_BUTTON_LOCATOR) && isElementEnabled(ADD_AND_CLOSE_BUTTON_LOCATOR);
    }
}
