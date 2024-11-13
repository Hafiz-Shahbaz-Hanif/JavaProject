package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.*;

import java.util.List;

public class GenericMultiListModal extends PageHandler {
    public final String MODAL_XPATH = "//div[@data-qa='GenericMultiListModal']";
    public final By MODAL_LOCATOR = By.xpath(MODAL_XPATH);
    public final By SEARCH_INPUT = By.xpath(MODAL_XPATH + "//input[@type='text']");
    public final By MOVE_ALL_TO_LEFT = By.xpath(MODAL_XPATH + "//span[text()='chevron_left']");
    public final By MOVE_ALL_TO_RIGHT = By.xpath(MODAL_XPATH + "//span[text()='chevron_right']");
    public final By MOVE_SELECTED_TO_LEFT = By.xpath(MODAL_XPATH + "//button[text()='chevron_left' and not(child::span)]");
    public final By MOVE_SELECTED_TO_RIGHT = By.xpath(MODAL_XPATH + "//button[text()='chevron_right' and not(child::span)]");
    public final By UPDATE_BUTTON_LOCATOR = By.xpath(MODAL_XPATH + "//button[contains(text(),'Update')]");
    public final By BUTTON_TO_APPLY_CHANGES_LOCATOR = By.xpath("(//div[@data-qa='GenericMultiListModal']//div[@data-qa='InlineList'])[2]//button[not(text()='Cancel')]");
    public final By CANCEL_BUTTON_LOCATOR = By.xpath(MODAL_XPATH + "//button[contains(text(),'Cancel')]");
    public final By UNSELECTED_OPTIONS_LOCATOR = By.xpath(MODAL_XPATH + "//div[@role='grid']//li//span[text()]");
    public final By SELECTED_OPTIONS_LOCATOR = By.xpath(MODAL_XPATH + "//div[@draggable]//li//span[text() and not(text()='open_with')]");

    public GenericMultiListModal(WebDriver driver) {
        super(driver);
        findElementVisibleMilliseconds(MODAL_LOCATOR);
    }

    public void selectOptionOnTheLeft(String option) {
        By optionLocator = By.xpath("//div[@role='rowgroup']//li[child::span[text()='" + option + "']]//input");
        selectElement(optionLocator);
    }

    public void selectOptionOnTheRight(String option) {
        By optionLocator = By.xpath("//div[@draggable]/li[descendant::span[text()='" + option + "']]//input");
        selectElement(optionLocator);
    }

    public void moveAllOptionsToTheLeft() throws InterruptedException {
        try {
            scrollIntoViewAndClick(MOVE_ALL_TO_LEFT);
        } catch (ElementClickInterceptedException e) {
            clickElementUsingJavascriptExecutor(MOVE_ALL_TO_LEFT);
        }
    }

    public void moveAllOptionsToTheRight() throws InterruptedException {
        try {
            scrollIntoViewAndClick(MOVE_ALL_TO_RIGHT);
        } catch (ElementClickInterceptedException e) {
            clickElementUsingJavascriptExecutor(MOVE_ALL_TO_RIGHT);
        }
    }

    public void moveOptionsToTheRight(List<String> optionsToMove) {
        for (String option : optionsToMove) {
            searchForOption(option);
            selectOptionOnTheLeft(option);
        }
        clickElement(MOVE_SELECTED_TO_RIGHT);
    }

    public void moveOptionsToTheLeft(List<String> optionsToMove) {
        for (String option : optionsToMove) {
            selectOptionOnTheRight(option);
        }
        clickElement(MOVE_SELECTED_TO_LEFT);
    }

    public void reorderOption(String optionToDrag, int position) {
        By optionLocator = By.xpath("//div[child::li[descendant::span[text()='" + optionToDrag + "']]]//span[text()='open_with']");
        By targetLocator = By.xpath("//div[@draggable and position()=" + position + "]//li");
        dragAndDrop_SecondAlternative(optionLocator, targetLocator);
    }

    public void searchForOption(String option) {
        setText(SEARCH_INPUT, option);
    }

    public void clearSearch() {
        clearInput(SEARCH_INPUT);
    }

    public List<String> getUnselectedOptions() {
        return getTextFromElementsMilliseconds(UNSELECTED_OPTIONS_LOCATOR);
    }

    public List<String> getSelectedOptions() {
        return getTextFromElementsMilliseconds(SELECTED_OPTIONS_LOCATOR);
    }

    public boolean isUpdateButtonEnabled() {
        return isElementEnabled(UPDATE_BUTTON_LOCATOR);
    }

    public boolean isButtonToApplyChangesEnabled() {
        return isElementEnabled(BUTTON_TO_APPLY_CHANGES_LOCATOR);
    }

    public String getTextOfButtonToApplyChanges() {
        return getTextFromElement(BUTTON_TO_APPLY_CHANGES_LOCATOR);
    }

    public void updateChanges() throws InterruptedException {
        click(UPDATE_BUTTON_LOCATOR);
    }

    public <T> T updateChanges(Class<T> clazz) throws InterruptedException {
        click(UPDATE_BUTTON_LOCATOR);
        return getPage(clazz);
    }

    public void applyChanges() throws InterruptedException {
        click(BUTTON_TO_APPLY_CHANGES_LOCATOR);
    }

    public <T> T applyChanges(Class<T> clazz) throws InterruptedException {
        click(BUTTON_TO_APPLY_CHANGES_LOCATOR);
        return getPage(clazz);
    }

    public void cancelChanges() throws InterruptedException {
        click(CANCEL_BUTTON_LOCATOR);
    }

    public <T> T cancelChanges(Class<T> clazz) throws InterruptedException {
        click(CANCEL_BUTTON_LOCATOR);
        return getPage(clazz);
    }


}
