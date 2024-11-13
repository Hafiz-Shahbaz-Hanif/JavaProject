package com.DC.pageobjects.adc.execute.mediaManagement.campaignSegmentation;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CampaignSegmentationManagerPage extends NetNewNavigationMenu {

    private static final By EXISTING_FILTER_ROWS = By.xpath("//div[@role='rowgroup']/div[@role='row' and @row-index]");
    private static final By PLATFORMS_ON_FILTER = By.xpath("//span[@ref='eText' and not(contains(text(),'Multi Platform'))]");
    private static final By CAMPAIGN_SEGMENTATION_MANAGER_TITLE = By.xpath("//a[text()='Manager']");
    private static final By CAMPAIGN_SEGMENTATION_MANAGER_BREADCRUMB = By.xpath("//a[text()='Manager']");
    private static final By MPV_MAPPINGS_TAB = By.id("mpv-mapping");
    private static final By EXISTING_MPV_FILTER_NAMES = By.xpath("//div[@role='rowgroup']/div[@role='row' and @row-index]//span[@aria-colindex='2' and @role='gridcell']");
    private static final By CHECKBOX_FOR_FILTER = By.xpath("//span[@aria-colindex='1' and @role='gridcell']/preceding-sibling::span");
    private static final By BTN_DELETE_RECORD = By.xpath("//a[@title='Delete Record']");
    private static final By BTN_CONFIRM_DELETE = By.xpath("//div[@class='noty_buttons']/button[contains(text(),'Confirm')]");
    private static final By CREATE_BUTTON_TO_DISPLAY_POPUP = By.xpath("//button[@title='Create Configuration']");
    private static final By TOP_RIGHT_ALERT = By.xpath("//div[@id='noty_layout__topRight' and @role='alert']");
    private static final By TOP_RIGHT_POPUP = By.xpath("//div[@class='noty_body']");
    private static final By BTN_EDIT = By.xpath("//button[@title='Edit Configuration']");
    private static final By IN_LINE_SELECTION = By.xpath("//select[@class='ag-cell-edit-input']");
    private static final By BTN_CREATE = By.xpath("//div[@class='modal-footer']//button[contains(@class, 'btn-success')]");
    private static final By MPV_FILTER_NAME = By.xpath("//input[@formcontrolname= 'linkTitle']");
    private static final By MPV_FILTER_TYPE_AMAZON = By.xpath("//label[contains(text(), 'AMAZON') and not(contains(text(), 'DSP'))]/following-sibling::div/select");

    public CampaignSegmentationManagerPage(WebDriver driver) {
        super(driver);
        findElementVisible(CAMPAIGN_SEGMENTATION_MANAGER_BREADCRUMB);
    }

    public void clickMultiPlatformViewMappingsTab() throws InterruptedException {
        click(MPV_MAPPINGS_TAB);
    }

    public boolean isCampaignSegmentationManagerScreenDisplayed() {
        return isElementVisible(CAMPAIGN_SEGMENTATION_MANAGER_TITLE);
    }

    public void createQuickMultiPlatformViewFilter(String filterName, String filterType) throws InterruptedException {
        deleteFilterIfExists(filterName);
        clickCreateButtonToDisplayPopup();
        setText(MPV_FILTER_NAME, filterName);
        selectMultiPlatformViewFilterTypeForAmazon(filterType);
        click(BTN_CREATE);
        waitForElementToBeInvisible(TOP_RIGHT_ALERT);
    }

    public void quickUpdateMultiPlatformViewFilter(String newFilterName, String newFilterType) throws InterruptedException {
        deleteFilterIfExists(newFilterName);
        setText(MPV_FILTER_NAME, newFilterName);
        selectMultiPlatformViewFilterTypeForAmazon(newFilterType);
        click(BTN_CREATE);
        waitTextInElementToBe(TOP_RIGHT_POPUP, "Link detail has been Updated", Duration.ofSeconds(5));
        waitForTextOfElementToChange(TOP_RIGHT_POPUP, "Link detail has been Updated", Duration.ofSeconds(5));
    }

    public boolean isFilterNotPresent(String filterName) {
        return isElementNotVisible(getFilterXpath(filterName));
    }

    public boolean isFilterPresent(String filterName) {
        return isElementVisible(getFilterXpath(filterName));
    }

    private By getFilterXpath(String filterName) {
        return By.xpath("//span[@aria-colindex='2' and @role='gridcell' and text()='" + filterName + "']");
    }

    public void clickCreateButtonToDisplayPopup() throws InterruptedException {
        click(CREATE_BUTTON_TO_DISPLAY_POPUP);
    }

    public void selectMultiPlatformViewFilterTypeForAmazon(String filterType) {
        selectOptionFromDropdownByText(MPV_FILTER_TYPE_AMAZON, filterType);
    }

    public void openEditModal(String filterName) throws InterruptedException {
        selectFilterCheckBoxByFilterName(filterName);
        waitForElementToBeInvisible(TOP_RIGHT_POPUP);
        click(BTN_EDIT);
    }

    private void selectFilterCheckBoxByFilterName(String filterName) {
        List<WebElement> existingMultiPlatformViewFilterNames = findElementsVisible(EXISTING_MPV_FILTER_NAMES);
        List<WebElement> checkBoxForFilter = findElementsVisible(CHECKBOX_FOR_FILTER);
        for (int i = 0; i < existingMultiPlatformViewFilterNames.size(); i++) {
            if (existingMultiPlatformViewFilterNames.get(i).getText().equals(filterName)) {
                checkBoxForFilter.get(i).click();
            }
        }
    }

    public void deleteFilterIfExists(String filterName) throws InterruptedException {
        if (isFilterPresent(filterName)) {
            List<WebElement> existingMultiPlatformViewFilterNames = findElementsVisible(EXISTING_MPV_FILTER_NAMES);
            List<WebElement> checkBoxForFilter = findElementsVisible(CHECKBOX_FOR_FILTER);
            List<WebElement> btnDeleteRecord = findElementsVisible(BTN_DELETE_RECORD);

            for (int i = 0; i < existingMultiPlatformViewFilterNames.size(); i++) {
                if (existingMultiPlatformViewFilterNames.get(i).getText().equals(filterName)) {
                    checkBoxForFilter.get(i).click();
                    btnDeleteRecord.get(i).click();

                    click(BTN_CONFIRM_DELETE);
                }
            }
        }
        waitForElementToBeInvisible(TOP_RIGHT_ALERT);
    }

    public List<String> getPlatformNamesOnFilter(String filterName) {
        List<String> platforms = new ArrayList<>();
        List<WebElement> existingMultiPlatformViewFilterNames = findElementsVisible(EXISTING_MPV_FILTER_NAMES);
        List<WebElement> existingFilterRows = findElementsVisible(EXISTING_FILTER_ROWS);
        List<WebElement> platformsOnFilter = findElementsVisible(PLATFORMS_ON_FILTER);

        for (int i = 0; i < existingMultiPlatformViewFilterNames.size(); i++) {
            if (existingMultiPlatformViewFilterNames.get(i).getText().equals(filterName)) {
                String rowIndex = existingFilterRows.get(i).getAttribute("aria-rowindex");
                for (int j = 0; j < platformsOnFilter.size(); j++) {
                    String colIndex = platformsOnFilter.get(j).getAttribute("aria-colindex");
                    WebElement el = findElementVisible(By.xpath("//div[@aria-rowindex='" + rowIndex + "']//span[@aria-colindex='" + colIndex + "' and @role='gridcell']"));
                    if (!el.getText().equals("None")) {
                        String text = platformsOnFilter.get(j).getText();
                        platforms.add(text);
                    }
                }
            }
        }
        return platforms;
    }

    public List<String> getPlatformValuesOnFilter(String filterName) {
        List<String> platformValues = new ArrayList<>();
        List<WebElement> existingMultiPlatformViewFilterNames = findElementsVisible(EXISTING_MPV_FILTER_NAMES);
        List<WebElement> existingFilterRows = findElementsVisible(EXISTING_FILTER_ROWS);
        List<WebElement> platformsOnFilter = findElementsVisible(PLATFORMS_ON_FILTER);

        for (int i = 0; i < existingMultiPlatformViewFilterNames.size(); i++) {
            if (existingMultiPlatformViewFilterNames.get(i).getText().equals(filterName)) {
                String rowIndex = existingFilterRows.get(i).getAttribute("aria-rowindex");
                for (WebElement webElement : platformsOnFilter) {
                    String colIndex = webElement.getAttribute("aria-colindex");
                    WebElement el = findElementVisible(By.xpath("//div[@aria-rowindex='" + rowIndex + "']//span[@aria-colindex='" + colIndex + "' and @role='gridcell']"));
                    if (!el.getText().equals("None")) {
                        String text = el.getText();
                        platformValues.add(text);
                    }
                }
            }
        }
        return platformValues;
    }

    public void updateInLineFilterType(String filterName, String platform, String value) {
        List<WebElement> existingMultiPlatformViewFilterNames = findElementsVisible(EXISTING_MPV_FILTER_NAMES);
        List<WebElement> existingFilterRows = findElementsVisible(EXISTING_FILTER_ROWS);
        List<WebElement> platformsOnFilter = findElementsVisible(PLATFORMS_ON_FILTER);
        for (int i = 0; i < existingMultiPlatformViewFilterNames.size(); i++) {
            if (existingMultiPlatformViewFilterNames.get(i).getText().equals(filterName)) {
                String rowIndex = existingFilterRows.get(i).getAttribute("aria-rowindex");
                for (WebElement webElement : platformsOnFilter) {
                    if (webElement.getText().equalsIgnoreCase(platform)) {
                        String colIndex = webElement.getAttribute("aria-colindex");
                        WebElement el = findElementVisible(By.xpath("//div[@aria-rowindex='" + rowIndex + "']//span[@aria-colindex='" + colIndex + "' and @role='gridcell']"));
                        el.click();
                        selectOptionFromDropdownByText(IN_LINE_SELECTION, value);
                    }
                }
            }
        }

    }

    public String getFilterType(String filterName, String platform) {
        String filterType = null;
        List<WebElement> existingMultiPlatformViewFilterNames = findElementsVisible(EXISTING_MPV_FILTER_NAMES);
        List<WebElement> existingFilterRows = findElementsVisible(EXISTING_FILTER_ROWS);
        List<WebElement> platformsOnFilter = findElementsVisible(PLATFORMS_ON_FILTER);

        for (int i = 0; i < existingMultiPlatformViewFilterNames.size(); i++) {
            if (existingMultiPlatformViewFilterNames.get(i).getText().equals(filterName)) {
                String rowIndex = existingFilterRows.get(i).getAttribute("aria-rowindex");
                for (WebElement webElement : platformsOnFilter) {
                    if (webElement.getText().equalsIgnoreCase(platform)) {
                        String colIndex = webElement.getAttribute("aria-colindex");
                        filterType = getTextFromElement(By.xpath("//div[@aria-rowindex='" + rowIndex + "']//span[@aria-colindex='" + colIndex + "' and @role='gridcell']"));
                    }
                }
            }
        }
        return filterType;
    }

    public List<String> getCurrentValuesInModal(List<String> platforms) {
        List<WebElement> platformDropdowns = new ArrayList<>();
        List<String> prepopulatedValues = new ArrayList<>();
        for (String platform : platforms) {
            By platformDropdownXpath = By.xpath("//label[contains(text(), '" + platform + "') and not(contains(text(), 'DSP'))]/..//select[@formcontrolname='segmentationTypeId']");
            platformDropdowns.add(findElementVisible(platformDropdownXpath));
        }
        for (WebElement platformDropdown : platformDropdowns) {
            Select select = new Select(platformDropdown);
            List<WebElement> options = select.getOptions();
            for (WebElement option : options) {
                if (option.isSelected()) {
                    prepopulatedValues.add(option.getText().trim());
                }
            }
        }
        return prepopulatedValues;
    }
}