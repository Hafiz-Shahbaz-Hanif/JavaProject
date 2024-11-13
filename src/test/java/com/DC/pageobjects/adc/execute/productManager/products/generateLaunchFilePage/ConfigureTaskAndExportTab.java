package com.DC.pageobjects.adc.execute.productManager.products.generateLaunchFilePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.HashMap;

public class ConfigureTaskAndExportTab extends GenerateLaunchFilePage {
    protected final By CONFIGURE_TASK_AND_EXPORT_BODY = By.xpath("//div[@data-qa='ConfigureTaskAndExport']");
    protected final By LAUNCH_BUTTON = By.xpath("//div[@data-qa='BottomActionBar']//button[text()='Launch']");
    protected final By CONFIRM_LAUNCH_BUTTON = By.xpath("//div[@data-qa='ModalWrapper']//button[text()='Launch']");

    public ConfigureTaskAndExportTab(WebDriver driver) {
        super(driver);
        findElementVisible(CONFIGURE_TASK_AND_EXPORT_BODY);
    }

    public void selectAnAssociatedProduct(String instanceId) {
        By checkboxToSelect = By.xpath("//div[@row-id='" + instanceId + "']//input[@type='radio']");
        selectElement(checkboxToSelect);
    }

    public void fillDataField(String fieldLabel, String inputValue) {
        By fieldInput = By.xpath("//div[child::p[text()='" + fieldLabel + "']]//input");
        sendKeys(fieldInput, inputValue);
    }

    public void fillOutAllFields(HashMap<String, String> fieldsAndValues) {
        fieldsAndValues.forEach(this::fillDataField);
    }

    public ConfigureTaskAndExportTab configureAndLaunchBatch(String instanceId, HashMap<String, String> fieldsAndValues) throws Exception {
        selectAnAssociatedProduct(instanceId);
        fillOutAllFields(fieldsAndValues);
        return launchBatch();
    }

    public ConfigureTaskAndExportTab launchBatch() throws Exception {
        clickElement(LAUNCH_BUTTON);
        clickElement(CONFIRM_LAUNCH_BUTTON);
        waitForElementToBeEnabled(LAUNCH_BUTTON, Duration.ofSeconds(30));
        return new ConfigureTaskAndExportTab(driver);
    }
}
