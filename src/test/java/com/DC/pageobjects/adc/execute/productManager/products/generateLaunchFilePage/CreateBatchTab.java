package com.DC.pageobjects.adc.execute.productManager.products.generateLaunchFilePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CreateBatchTab extends GenerateLaunchFilePage {
    private final By BATCH_NAME_INPUT_LOCATOR = By.xpath("//input[@type='text' and contains(@placeholder,'Enter a name')]");
    private final By TASKS_NUMBER_INPUT_LOCATOR = By.xpath("//input[@type='number' and contains(@placeholder,'Enter a number')]");
    private final By CHAINS_INPUT_LOCATOR = By.xpath("//span[contains(text(),'Select the chain')]/following-sibling::div//input");
    protected final By BACK_TO_PREVIOUS_STEP_BUTTON = By.xpath("//div[@data-qa='BottomActionBar']//div[text()='Back to Previous Step']");

    public CreateBatchTab(WebDriver driver) {
        super(driver);
        findElementVisible(BATCH_NAME_INPUT_LOCATOR);
    }

    public FormatBatchTab fillSectionFields(String batchName, int numberOfTasksForBatch, String chainName) throws Exception {
        setText(BATCH_NAME_INPUT_LOCATOR, batchName);
        setText(TASKS_NUMBER_INPUT_LOCATOR, String.valueOf(numberOfTasksForBatch));
        clickElement(CHAINS_INPUT_LOCATOR);
        setText(CHAINS_INPUT_LOCATOR, chainName);

        var dropdownItemToSelect = By.xpath("//div[@data-qa='CreateBatch']//li[text()='" + chainName + "']");
        clickElement(dropdownItemToSelect);
        clickElement(CONTINUE_BUTTON);
        return new FormatBatchTab(driver);
    }
}