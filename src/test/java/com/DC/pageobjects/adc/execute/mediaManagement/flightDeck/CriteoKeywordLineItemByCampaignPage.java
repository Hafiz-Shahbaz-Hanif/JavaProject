package com.DC.pageobjects.adc.execute.mediaManagement.flightDeck;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class CriteoKeywordLineItemByCampaignPage extends NetNewNavigationMenu {

    private final By CLEAR_PLATFORM = By.xpath("//a[@id='clearplatform']");
    private final By PLATFORM_FIELD = By.id("Platform-selectized");
    private final By SELECT_CRITEO = By.xpath("//div[@data-value='CRITEO'][contains(text(),'Criteo')]");
    private final By APPLY_BUTTON = By.xpath("//button[@title='Apply']");
    private final By ADD_KEYWORD_OPTION = By.xpath("//a[@id='Add Keywords']");
    private final By LINE_ITEM_BY_CAMPAIGN_CHECKBOX = By.xpath("//div[@row-id=0]//span[@class='ag-icon ag-icon-checkbox-unchecked']");
    private final By EDIT_BUTTON = By.id("btnEdit");
    private final By ADD_KEYWORD_MODAL = By.xpath("//h5[normalize-space()='Add Keywords']");
    private final By ADD_KEYWORD_ADD_ROW_BUTTON = By.xpath("//button[normalize-space()='Add Row']");
    private final By ADD_KEYWORD_UPLOAD_BUTTON = By.xpath("//button[normalize-space()='Upload']");
    private final By ADD_KEYWORD_MODAL_CLOSED_BUTTON = By.xpath("//a[@title='Close']//i[@class='fa fa-times']");
    private final By ADD_KEYWORD_SAVE_BUTTON = By.xpath("//button[@class='btn btn-xs btn-success']");
    private final By ADD_KEYWORD_TEXT_FIELD = By.xpath("//div[@col-id='keyword']//input[@type='text']");
    private final By MATCH_TYPE_DROPDOWN = By.xpath("//select[@class='ag-cell-edit-input']");
    private final By ADD_MATCH_TYPE = By.xpath("//div[@col-id='matchType']//select/option[1]");
    private final By ADD_BID = By.xpath("//div[@col-id='bid']//input[@type='text']");
    private final By REQUEST_SUCCESS_MESSAGE = By.xpath("//div[@id='noty_layout__topRight']//div[contains(text(),'The request has been successfully processed')]");


    public CriteoKeywordLineItemByCampaignPage(WebDriver driver) {
        super(driver);
    }

    public void criteoPlatform() throws InterruptedException {
        UI_LOGGER.info("Clear the selected platform");
        click(CLEAR_PLATFORM);
        UI_LOGGER.info("Click on the Platform field");
        click(PLATFORM_FIELD);
        UI_LOGGER.info("Select Platform");
        if(isElementVisible(SELECT_CRITEO)) {
            click(SELECT_CRITEO);
            UI_LOGGER.info("Click Apply button");
            click(APPLY_BUTTON);
        }
        else {
            Assert.fail("Criteo is not displayed");
        }
    }

    public void editAddKeyword() throws InterruptedException {
        UI_LOGGER.info("Select the Line Item By Campaign checkbox");
        click(LINE_ITEM_BY_CAMPAIGN_CHECKBOX);
        UI_LOGGER.info("Click on Edit Button dropdown");
        click(EDIT_BUTTON);
        UI_LOGGER.info("Select Add Keyword option");
        click(ADD_KEYWORD_OPTION);
    }

    public boolean addKeywordModalDisplayed(){
        return isElementVisible(ADD_KEYWORD_MODAL);
    }
    public boolean addKeywordAddRowButtonDisplayed(){
        return isElementVisible(ADD_KEYWORD_ADD_ROW_BUTTON);
    }
    public boolean addKeywordUploadButtonDisplayed(){
        return isElementVisible(ADD_KEYWORD_UPLOAD_BUTTON);
    }
    public boolean addKeywordModalClosedButtonDisplayed(){
        return isElementVisible(ADD_KEYWORD_MODAL_CLOSED_BUTTON);
    }
    public boolean addKeywordModalSaveButtonDisplayed(){
        return isElementVisible(ADD_KEYWORD_SAVE_BUTTON);
    }

    public void addKeywordModalFunctionality() throws InterruptedException {
        findElementPresent(ADD_KEYWORD_TEXT_FIELD);
        UI_LOGGER.info("Add keyword in the Field");
        setText(ADD_KEYWORD_TEXT_FIELD,"TestKeyword" + Keys.TAB + Keys.TAB);
        UI_LOGGER.info("ADD Bid");
        setText(ADD_BID,"0.2" + Keys.SHIFT+ Keys.TAB);
        UI_LOGGER.info("Add Match Type");
        click(MATCH_TYPE_DROPDOWN);
        click(ADD_MATCH_TYPE);
        UI_LOGGER.info("Click on Save Button");
        click(ADD_KEYWORD_SAVE_BUTTON);
    }
    public boolean addKeywordSuccessMessage(){
        return isElementVisible(REQUEST_SUCCESS_MESSAGE);
    }
}
