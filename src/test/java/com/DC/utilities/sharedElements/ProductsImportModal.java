package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.Objects;

public class ProductsImportModal extends PageHandler {
    private static final String MODAL_XPATH = "//div[@data-qa='ProductImports']";
    private static final String FILE_UPLOAD_BAR_XPATH = "//div[@data-qa='FileUploadBar']";
    private final By EXCEL_TEMPLATE_BUTTON_LOCATOR = By.xpath(MODAL_XPATH + "//a[text()='Download excel template']");
    private final By SAVE_AND_EXIT_BUTTON_LOCATOR = By.xpath(MODAL_XPATH + "//button[text()='Save & Exit']");

    public ProductsImportModal(WebDriver driver) {
        super(driver);
        findElementVisibleMilliseconds(By.xpath(MODAL_XPATH));
    }

    public ProductsImportModal selectTypeOfImport(Enums.ImportType importType) {
        if (Objects.equals(importType, Enums.ImportType.COMPANY)) {
            throw new IllegalArgumentException("Cannot import company from this modal");
        }

        var valueToSelect = Objects.equals(importType, null) ? "master" : importType.getImportType();
        var radioCheckboxLocator = By.xpath(MODAL_XPATH + "//input[@value='" + valueToSelect + "']");
        clickElement(radioCheckboxLocator);
        return this;
    }

    public ProductsImportModal selectFlagForKeywordImport(boolean overwriteExistingData) {
        var value = overwriteExistingData ? "replace" : "add";
        var radioCheckboxLocator = By.xpath(MODAL_XPATH + "//input[@value='" + value + "']");
        clickElement(radioCheckboxLocator);
        return this;
    }

    public void downloadExcelTemplate() {
        clickElement(EXCEL_TEMPLATE_BUTTON_LOCATOR);
    }

    public String getExcelTemplateNameWithExtension() {
        String hrefAttribute = getAttribute(EXCEL_TEMPLATE_BUTTON_LOCATOR, "href");
        if (hrefAttribute.contains("?")) {
            hrefAttribute = hrefAttribute.substring(0, hrefAttribute.lastIndexOf("?"));
        }
        return hrefAttribute.substring(hrefAttribute.lastIndexOf("/") + 1);
    }

    public boolean isSaveAndExitButtonEnabled() {
        return isElementEnabled(SAVE_AND_EXIT_BUTTON_LOCATOR);
    }

    public void uploadFile(String filePath) throws InterruptedException {
        var fileInputLocator = By.xpath(MODAL_XPATH + "//input[@type='file']");
        uploadFile(fileInputLocator, filePath);
    }

    public boolean isInvalidFileErrorDisplayed() {
        var errorLocator = By.xpath(MODAL_XPATH + "//span[contains(text(),'Please upload a .xlsx file')]");
        return isElementVisibleMilliseconds(errorLocator);
    }

    public ProductsImportModal waitForImportToUpload(){
        var filePreviewLinkLocator = By.xpath(FILE_UPLOAD_BAR_XPATH + "//a[text()='Preview']");
        findElementVisible(filePreviewLinkLocator);
        return this;
    }

    public ProductsImportModal downloadFilePreview(String fileNameWithExtension){
        var filePreviewLinkLocator = By.xpath(FILE_UPLOAD_BAR_XPATH + "//span[text()='" + fileNameWithExtension + "']/following-sibling::div/a[text()='Preview']");
        findElementVisible(filePreviewLinkLocator);
        clickElement(filePreviewLinkLocator);
        return this;
    }

    public String getLinkToPreview(String fileNameWithExtension){
        var filePreviewLinkLocator = By.xpath(FILE_UPLOAD_BAR_XPATH + "//span[text()='" + fileNameWithExtension + "']/following-sibling::div/a[text()='Preview']");
        waitForImportToUpload();
        return getAttribute(filePreviewLinkLocator, "href");
    }

    public void clickSaveAndExitButton() {
        clickElement(SAVE_AND_EXIT_BUTTON_LOCATOR);
        waitForElementToBeInvisible(SAVE_AND_EXIT_BUTTON_LOCATOR, Duration.ofSeconds(3));
    }

    public <T> T importKeywords(String validFilePath, boolean overwriteExistingData, Class<T> page) throws InterruptedException {
        selectTypeOfImport(Enums.ImportType.KEYWORD);
        selectFlagForKeywordImport(overwriteExistingData);
        uploadFile(validFilePath);
        waitForImportToUpload();
        clickSaveAndExitButton();
        return getPage(page);
    }

}
