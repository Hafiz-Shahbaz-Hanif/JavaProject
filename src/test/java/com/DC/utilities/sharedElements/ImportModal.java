package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ImportModal extends PageHandler {
    private final String MODAL_XPATH = "//div[@data-qa='ImportModal']";
    private final By EXCEL_TEMPLATE_BUTTON_LOCATOR = By.xpath(MODAL_XPATH + "//a[text()='Excel Template']");

    public ImportModal(WebDriver driver) {
        super(driver);
        findElementVisibleMilliseconds(By.xpath(MODAL_XPATH));
    }

    public void downloadExcelTemplate() {
        clickElement(EXCEL_TEMPLATE_BUTTON_LOCATOR);
    }

    public String getExcelTemplateNameWithExtension() {
        String hrefAttribute = getAttribute(EXCEL_TEMPLATE_BUTTON_LOCATOR, "href");
        return hrefAttribute.substring(hrefAttribute.lastIndexOf("/") + 1);
    }

    public void uploadFile(String filePath) throws InterruptedException {
        var fileInputLocator = By.xpath(MODAL_XPATH + "//input[@type='file']");
        uploadFile(fileInputLocator, filePath);
    }

    public boolean isInvalidFileErrorDisplayed() {
        var errorLocator = By.xpath(MODAL_XPATH + "//span[contains(text(),'Please upload a .xlsx file')]");
        return isElementVisibleMilliseconds(errorLocator);
    }
}
