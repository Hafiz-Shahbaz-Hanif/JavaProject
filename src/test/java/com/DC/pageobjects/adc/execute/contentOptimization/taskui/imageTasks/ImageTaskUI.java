package com.DC.pageobjects.adc.execute.contentOptimization.taskui.imageTasks;

import com.DC.pageobjects.adc.execute.contentOptimization.taskui.TaskUIBase;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;

/**
 * Includes image creation and image revisions since both tasks are very similar
 */
public class ImageTaskUI extends TaskUIBase {
    protected final String MEDIA_UPLOAD_SECTION_XPATH = "//div[@data-qa='MediaUploadSection']";
    protected final By MEDIA_UPLOAD_SECTIONS = By.xpath("//div[@data-qa='MediaUpload']/div[1]");
    protected final Duration MAX_WAIT_TIME = Duration.ofSeconds(3);
    protected By deleteMediaIcon;

    public ImageTaskUI(WebDriver driver) throws Exception {
        super(driver);
    }

    public List<String> getMediaUploadSectionsDisplayed() {
        return getTextFromElementsMilliseconds(MEDIA_UPLOAD_SECTIONS);
    }

    public void uploadMediaFileToSection(String filePath, String sectionName) throws InterruptedException {
        var uploadInput = By.xpath(getSectionXPath(sectionName) + "//input[@type='file']");
        uploadFile(uploadInput, filePath);
    }

    public Boolean doesProductHaveMediaAssigned(String sectionName) {
        deleteMediaIcon = By.xpath(getSectionXPath(sectionName) + "//i[@class='fa fa-trash-alt']");
        return isElementVisible(deleteMediaIcon, MAX_WAIT_TIME);
    }

    public void removeProductMediaIfExistent(String sectionName) {
        if (doesProductHaveMediaAssigned(sectionName)) {
            moveToElementAndClick(deleteMediaIcon);
            waitForElementToBeInvisible(deleteMediaIcon);
        }
    }

    public String getMediaUrl(String sectionName) {
        var mediaLocator = By.xpath(getSectionXPath(sectionName) + "//a");
        return getAttribute(mediaLocator, "href");
    }

    public void assignNewMediaFileToProduct(int productIndex, String sectionName, String mediaPath) throws InterruptedException {
        clickOnSpecificProduct(productIndex);
        removeProductMediaIfExistent(sectionName);
        try {
            uploadMediaFileToSection(mediaPath, sectionName);
        } catch (StaleElementReferenceException e) {
            clickElement(deleteMediaIcon);
            uploadMediaFileToSection(mediaPath, sectionName);
        }
    }

    private String getSectionXPath(String sectionName) {
        return "//div[@data-qa='MediaUpload' and child::div[text()='" + sectionName + "']]";
    }

}
