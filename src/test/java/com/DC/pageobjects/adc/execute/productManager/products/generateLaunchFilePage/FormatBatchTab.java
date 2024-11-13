package com.DC.pageobjects.adc.execute.productManager.products.generateLaunchFilePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FormatBatchTab extends GenerateLaunchFilePage {
    protected final By FORMAT_BATCH_BODY = By.xpath("//div[@data-qa='FormatBatch']");

    public FormatBatchTab(WebDriver driver) throws Exception {
        super(driver);
        findElementVisible(FORMAT_BATCH_BODY);
    }

    public FormatBatchTab setTaskNumber(String instanceId, int taskNumber) {
        By taskNumberInput = By.xpath("//div[contains(@class,'ag-center-cols-container')]//div[@row-id='" + instanceId + "']//input");
        setText(taskNumberInput, String.valueOf(taskNumber));
        return this;
    }

    public ConfigureTaskAndExportTab clickContinue() throws Exception {
        clickElement(CONTINUE_BUTTON);
        return new ConfigureTaskAndExportTab(driver);
    }


}
