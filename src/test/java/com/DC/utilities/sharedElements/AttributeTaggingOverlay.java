package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class AttributeTaggingOverlay extends PageHandler {
    private static final String MODAL_BODY_XPATH = "//div[@data-qa='AttributeTaggingOverlay']";
    private static final By SAVE_BUTTON_LOCATOR = By.xpath(MODAL_BODY_XPATH + "//button[text()='Save Attributes']");
    private static final By SELECT_TAG_TO_APPLY_DROPDOWN = By.xpath(MODAL_BODY_XPATH + "//button[text()='Select tag to apply...']");

    public AttributeTaggingOverlay(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(MODAL_BODY_XPATH));
    }

    public <T> T clickSaveAttributesButton(Class<T> clazz) {
        clickElement(SAVE_BUTTON_LOCATOR);
        waitForElementToBeInvisible(SAVE_BUTTON_LOCATOR, Duration.ofSeconds(15));
        return getPage(clazz);
    }

    public AttributeTaggingOverlay selectTagToApply(String tagName) {
        var singleSelect = new SingleSelectDropdown(driver);
        singleSelect.openDropdownMenu(SELECT_TAG_TO_APPLY_DROPDOWN);
        singleSelect.selectOption(tagName);
        return this;
    }

    public AttributeTaggingOverlay selectOrDeselectValueFromCheckbox(String tagName, boolean selectValue) {
        var inputLocator = By.xpath(MODAL_BODY_XPATH + "//label[descendant::span[text()='" + tagName + "']]//input");
        if (selectValue) {
            selectElement(inputLocator);
        } else {
            deselectElement(inputLocator);
        }
        return this;
    }
}
