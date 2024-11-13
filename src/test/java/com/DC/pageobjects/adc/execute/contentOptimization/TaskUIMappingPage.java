package com.DC.pageobjects.adc.execute.contentOptimization;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;

public class TaskUIMappingPage extends InsightsNavigationMenu {
    private final String TABLE_XPATH = "//table[@class='config-table']";

    public TaskUIMappingPage(WebDriver driver) {
        super(driver);
        findElementVisible(By.xpath(TABLE_XPATH), Duration.ofSeconds(20));
    }

    public List<String> getIdsOfMappingConfigsDisplayed() {
        var idsLocator = By.xpath(TABLE_XPATH + "//tbody//tr//td[2]//span");
        return getTextFromElements(idsLocator);
    }

    public CreateTaskUIMappingPage clickCreateNewMappingConfigButton() {
        var createButtonLocator = By.xpath("//button[@data-qa='Button' and text()='Create Mapping']");
        clickElement(createButtonLocator);
        return new CreateTaskUIMappingPage(driver);
    }

    public void searchMappingConfig(String mappingConfigName) {
        var searchInputLocator = By.xpath("//input[@placeholder='Search...']");
        var searchButtonLocator = By.xpath("//div[@data-qa='TaskUIConfig']//i[@class='fa fa-search']");
        setText(searchInputLocator, mappingConfigName);
        clickElement(searchButtonLocator);
    }

    public boolean isMappingConfigDisplayed(String mappingConfigName) {
        var cellLocator = By.xpath("//td[1]//span[text()='" + mappingConfigName + "']");
        searchMappingConfig(mappingConfigName);
        return isElementVisible(cellLocator);
    }

    public String getIdOfMappingConfig(String mappingConfigName) {
        var cellLocator = By.xpath("//tr[child::td[1]//span[text()='" + mappingConfigName + "']]//td[2]//span");
        moveToElement(cellLocator);
        return getTextFromElement(cellLocator);
    }

    public DeleteTaskUIMappingPage clickDeleteIcon(String mappingConfigName) {
        var deleteIconLocator = By.xpath("//tr[child::td[1]//span[text()='" + mappingConfigName + "']]//td[3]//button");
        clickElement(deleteIconLocator);
        return new DeleteTaskUIMappingPage(driver);
    }

    public static class CreateTaskUIMappingPage extends InsightsNavigationMenu {
        private final By CREATE_BUTTON_LOCATOR = By.xpath("//button[@data-qa='Button' and text()='Create']");
        private final By LABEL_INPUT_LOCATOR = By.xpath("//div[child::span[text()='Label']]//input[@type='text']");
        private final By INTERNAL_ONLY_CHECKBOX_LOCATOR = By.xpath("//div[child::span[text()='Label']]//input[@type='checkbox']");
        private final By MAPPING_TEXTAREA_LOCATOR = By.tagName("textarea");

        public CreateTaskUIMappingPage(WebDriver driver) {
            super(driver);
            findElementVisibleMilliseconds(CREATE_BUTTON_LOCATOR);
        }

        public boolean isCreateButtonEnabled() {
            return isElementEnabled(CREATE_BUTTON_LOCATOR);
        }

        public void fillFields(String label, boolean isInternal, JSONObject mappings) {
            setText(LABEL_INPUT_LOCATOR, label);
            if (isInternal) {
                selectElement(INTERNAL_ONLY_CHECKBOX_LOCATOR);
            }
            setText(MAPPING_TEXTAREA_LOCATOR, mappings.toString());
        }

        public void clickCreateButton() {
            clickElement(CREATE_BUTTON_LOCATOR);
        }
    }

    public static class DeleteTaskUIMappingPage extends InsightsNavigationMenu {
        private final By DELETE_BUTTON_LOCATOR = By.xpath("//button[@data-qa='Button' and text()='Delete Configuration']");
        private final By NEVER_MIND_BUTTON_LOCATOR = By.xpath("//span[contains(text(),'Never mind!')]");

        public DeleteTaskUIMappingPage(WebDriver driver) {
            super(driver);
            findElementVisibleMilliseconds(DELETE_BUTTON_LOCATOR);
        }

        public TaskUIMappingPage clickDeleteButton() {
            clickElement(DELETE_BUTTON_LOCATOR);
            waitForElementToBeInvisible(DELETE_BUTTON_LOCATOR, Duration.ofSeconds(5));
            return new TaskUIMappingPage(driver);
        }

        public TaskUIMappingPage clickNeverMindButton() {
            clickElement(NEVER_MIND_BUTTON_LOCATOR);
            return new TaskUIMappingPage(driver);
        }

        public TaskUIMappingPage clickCloseIcon() {
            clickCloseIconFromReactModal();
            return new TaskUIMappingPage(driver);
        }
    }

}
