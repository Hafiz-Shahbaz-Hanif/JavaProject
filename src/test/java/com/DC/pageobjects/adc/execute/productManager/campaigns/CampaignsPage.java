package com.DC.pageobjects.adc.execute.productManager.campaigns;

import java.util.List;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CampaignsPage extends InsightsNavigationMenu {
    private final By ADD_CAMPAIGN_BUTTON = By.xpath("//button[contains(text(),'Add Campaign')]");
    private final By ADD_BUTTON = By.xpath("//button[text()='Add']");
    private final By ADD_CLOSE_BUTTON = By.xpath("//button[text()='Add & Close']");
    private final By NAME_ATTRIBUTE = By.xpath("//input[@class='MuiInputBase-input MuiOutlinedInput-input MuiInputBase-inputSizeSmall css-ketj3n']");
    private final By SET_DATE_CHECKBOX = By.xpath("//input[@class='PrivateSwitchBase-input css-1m9pwf3']");
    private final By START_SET_DATE_BUTTON = By.xpath("(//input[@placeholder='MM-DD-YYYY'])[1]");
    private final By END_SET_DATE_BUTTON = By.xpath("(//input[@placeholder='MM-DD-YYYY'])[2]");
    private final By SEARCH_CAMPAIGN_INPUT = By.xpath("//input[@placeholder='Search Campaigns']");
    private final By CAMPAIGN_NAME_LIST = By.xpath("//div[@col-id='name']/div/span[contains(@id,'cell')]/div/span");
    private final By CAMPAIGN_DATE_RANGE_LIST = By.xpath("//div[@col-id='dateRange']//span[contains(@id,'cell')]//span");
    private final By DUPLICATE_CAMPAIGN_NAME_MESSAGE = By.xpath("//span[contains(text(),'There was an error adding the campaign. Campaign with this name already exists.')]");
    private final By CAMPAIGN_COLUMN = By.xpath("//span[text()='Campaign']");
    private final By DATE_RANGE_COLUMN = By.xpath("//span[text()='Date Range']");
    private final By SAVE_BUTTON = By.xpath("//button[contains(text(),'save')]");
    private final By SUBMIT_BUTTON = By.xpath("//button[contains(text(),'Submit')]");
    private final By DELETE_CAMPAIGN_BUTTON_LINK = By.xpath("//button[contains(text(),'Delete Campaign')]");
    private final By NO_DELETE_CAMPAIGN_BUTTON = By.xpath("//button[contains(text(),'No')]");
    private final By YES_DELETE_CAMPAIGN_BUTTON = By.xpath("//button[contains(text(),'Yes, delete.')]");
    private final By CLOSE_ADD_CAMPAIGN = By.xpath("//button[contains(text(),'close')]");
    private final By NEW_UPDATED_CAMPAIGN = By.xpath("//span[contains(text(),'New Rewarding customers Updated')]");
    private final By ADD_CAMPAIGN_CANCEL_BUTTON = By.xpath("//button[contains(text(),'Cancel')]");
    private final By TOTAL_CAMPAIGNS = By.xpath("//div[contains(@class,'Header')]//span[contains(@class,'Label')]");

    public CampaignsPage(WebDriver driver) {
        super(driver);
        findElementVisible(ADD_CAMPAIGN_BUTTON);
    }

    public boolean doesCampaignExist(String campaignToSearch) {
        By existingCampaign = By.xpath("//span[text()='" + campaignToSearch + "']");
        clearInput(SEARCH_CAMPAIGN_INPUT);
        searchForCampaign(campaignToSearch);
        boolean campaignAvailability = isElementVisible(existingCampaign, MAX_WAIT_TIME_SECS);
        clearInput(SEARCH_CAMPAIGN_INPUT);
        return campaignAvailability;
    }

    public String getTotalCampaigns() {
        return getTextFromElement(TOTAL_CAMPAIGNS);
    }

    public boolean areAddAndCloseButtonEnabled() throws InterruptedException {
        click(ADD_CAMPAIGN_BUTTON);
        String getNameAttribute = getAttribute(NAME_ATTRIBUTE, "value");
        boolean isAddButtonEnabled = false;
        boolean areAddAndCloseButtonEnabled = false;
        if (getNameAttribute.equals(null)) {
            isAddButtonEnabled = isElementEnabled(ADD_BUTTON);
            areAddAndCloseButtonEnabled = isElementEnabled(ADD_CLOSE_BUTTON);
        }
        return isAddButtonEnabled && areAddAndCloseButtonEnabled;
    }

    public void enterCampaignNameAddAndClose(String text) throws InterruptedException {
        sendKeys(NAME_ATTRIBUTE, text);
        click(ADD_CLOSE_BUTTON);
    }

    public void clickAddButtonAndAddNewCampaign(String text) throws InterruptedException {
        click(ADD_CAMPAIGN_BUTTON);
        sendKeys(NAME_ATTRIBUTE, text);
        click(ADD_CLOSE_BUTTON);
    }

    public boolean isSetADateRangeSelected() {
        return isElementSelected(SET_DATE_CHECKBOX);
    }

    public boolean isSelectDateButtonDisplayed() throws InterruptedException {
        click(SET_DATE_CHECKBOX);
        boolean startDateSelector = isElementEnabled(START_SET_DATE_BUTTON);
        boolean EndDateSelector = isElementEnabled(END_SET_DATE_BUTTON);
        return startDateSelector && EndDateSelector;
    }

    public boolean isDuplicateCampaignMessageDisplayed(String text) throws InterruptedException {
        click(ADD_CAMPAIGN_BUTTON);
        sendKeys(NAME_ATTRIBUTE, text);
        click(ADD_BUTTON);
        boolean messagePresent = isElementPresent(DUPLICATE_CAMPAIGN_NAME_MESSAGE);
        click(CLOSE_ADD_CAMPAIGN);
        return messagePresent;
    }

    public boolean checkCancelButtonOnAddCampaignPage(String text) throws InterruptedException {
        String totalCampaignBeforeAddingNewCampaign = getTotalCampaigns();
        click(ADD_CAMPAIGN_BUTTON);
        sendKeys(NAME_ATTRIBUTE, text);
        click(ADD_CAMPAIGN_CANCEL_BUTTON);
        String totalCampaignsAfterAddingNewCampaign = getTotalCampaigns();
        return totalCampaignBeforeAddingNewCampaign.equals(totalCampaignsAfterAddingNewCampaign);
    }

    public boolean isCampaignColumnAvailable() {
        return isElementPresent(CAMPAIGN_COLUMN);
    }

    public boolean isDateRangeColumnAvailable() {
        return isElementPresent(DATE_RANGE_COLUMN);
    }

    public String areCampaignNamesUpdated(String newCampaignName, String campaignToEdit) throws InterruptedException {
        sendKeysAndHitEnter(SEARCH_CAMPAIGN_INPUT, campaignToEdit);
        By editCampaignInput = By.xpath("/div[@role='gridcell']//input[@type='text']");
        By campaignEditButton = By.xpath("//span[text()='" + campaignToEdit + "']/following-sibling::button");
        clickElement(campaignEditButton);
        setText(editCampaignInput, newCampaignName);
        click(SAVE_BUTTON);
        clickElement(SUBMIT_BUTTON);
        return getTextFromElement(NEW_UPDATED_CAMPAIGN);
    }

    public boolean deleteCampaign(String campaignName, boolean confirmDelete) throws InterruptedException {
        By selectCampaign = By.xpath("//span[contains(text(),'" + campaignName + "')]");

        boolean campaignExists = doesCampaignExist(campaignName);
        if (!campaignExists) {
            clickAddButtonAndAddNewCampaign(campaignName);
        }

        By checkbox = By.xpath("//div[@role='row' and descendant::div[@col-id='name' and descendant::span[text()='" + campaignName + "']]]//input");
        if (!confirmDelete) {
            searchForCampaign(campaignName);
            click(checkbox);
            click(DELETE_CAMPAIGN_BUTTON_LINK);
            click(NO_DELETE_CAMPAIGN_BUTTON);
            searchForCampaign(campaignName);
            boolean undeletedSelectedElement = isElementPresent(selectCampaign);
            clearInput(SEARCH_CAMPAIGN_INPUT);
            deselectElement(checkbox);
            return undeletedSelectedElement;
        } else {
            searchForCampaign(campaignName);
            click(checkbox);
            click(DELETE_CAMPAIGN_BUTTON_LINK);
            click(YES_DELETE_CAMPAIGN_BUTTON);
            waitForElementToBeInvisible(YES_DELETE_CAMPAIGN_BUTTON, MAX_WAIT_TIME_SECS);
            searchForCampaign(campaignName);
            waitForDOMStabilization();
            return isElementVisible(selectCampaign, MAX_WAIT_TIME_SECS);
        }
    }

    public List<String> getCampaignsName() {
        List<WebElement> list = findElementsVisible(CAMPAIGN_NAME_LIST);
        return getTextFromElements(list);
    }

    public List<String> getCampaignDateRange() {
        List<WebElement> list = findElementsPresent(CAMPAIGN_DATE_RANGE_LIST);
        return getTextFromElements(list);
    }

    public void searchForCampaign(String campaignToSearch) {
        setTextAndHitEnter(SEARCH_CAMPAIGN_INPUT, campaignToSearch);
    }
}
