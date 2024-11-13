package com.DC.pageobjects.adc.execute.mediaManagement.campaignSegmentation;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

public class CampaignSegmentationPage extends NetNewNavigationMenu {

    private static final By CAMPAIGN_SEGMENTATION_TITLE = By.xpath("//a[text()='Campaign Segmentation']");
    private static final By SEGMENTATION_MANAGER_BUTTON = By.xpath("//a[text()='Segmentation Manager']");
    private static final By FILTER_FORM = By.xpath("//form[@class=contains(@class,'filter-form')]");
    private static final By REQUIRED_FILTER_PLATFORM = By.xpath("//select[@id='Platform']/..");
    private static final By REQUIRED_FILTER_VALUE_AMAZON = By.xpath("//div[@class='item' and @data-value='AMAZON']");
    private static final By CAMPAIGN_TYPE_FILTER = By.xpath("//select[@id='Campaign Type']/..");
    private static final By SPONSORED_TV_FILTER_VALUE = By.xpath("//option[@value='Sponsored TV']");
    private static final By APPLY_BUTTON = By.xpath("//button[@title='Apply']");
    private static final By CAMPAIGN_TYPE_LINE_VALUE = By.xpath("//span[contains(@class, 'ag-cell-value') and @aria-colindex='5']");
    
    public CampaignSegmentationPage(WebDriver driver) {
        super(driver);
        findElementVisible(CAMPAIGN_SEGMENTATION_TITLE);
    }

    public CampaignSegmentationManagerPage clickSegmentationManagerButton() throws InterruptedException {
        click(SEGMENTATION_MANAGER_BUTTON);
        return new CampaignSegmentationManagerPage(driver);
    }

    public boolean isFilterFormDisplayed() {
        return isElementVisible(FILTER_FORM);
    }

    public boolean isRequiredFilterDisplayed() {
        return isElementVisible(REQUIRED_FILTER_PLATFORM);
    }

    public boolean isRequiredFilterValueDisplayed() {
        return isElementVisible(REQUIRED_FILTER_VALUE_AMAZON);
    }

    public boolean isCampaignSegmentationScreenDisplayed() {
        return isElementVisible(CAMPAIGN_SEGMENTATION_TITLE);
    }

    public void applyCampaignTypeFilterByValue(String value) throws InterruptedException {
        selectItemFromDropdown(CAMPAIGN_TYPE_FILTER, value);
        click(APPLY_BUTTON);
        waitForElementClickable(APPLY_BUTTON);
    }

    public boolean isCampaignColumnContainsValue(String value) {
        List<String> lines = getTextFromElementsMilliseconds(CAMPAIGN_TYPE_LINE_VALUE);
        return lines.stream().allMatch(line -> line.equals(value));
    }
}
