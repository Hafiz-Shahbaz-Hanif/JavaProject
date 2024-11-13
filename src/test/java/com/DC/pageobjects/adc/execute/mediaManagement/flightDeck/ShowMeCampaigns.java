package com.DC.pageobjects.adc.execute.mediaManagement.flightDeck;

import com.DC.utilities.SharedMethods;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShowMeCampaigns extends FlightDeck {

    private By SPEND_COLUMN_HEADER = By.xpath("//div[@col-id = \"spend\" and contains(@class, \"ag-header-cell\")]");
    private By SPEND_COLUMN_HEADER_FILTER = By.xpath("//div[@col-id= 'spend']//span[@class='ag-icon ag-icon-menu']");
    private By COLUMN_HEADER_FILTER_DROPDOWN = By.xpath("//select[@name='filterType']");
    private By CAMPAIGN_NAME_ACTIVE_FILTERS = By.xpath("//select[@id='Campaign Name']/..//div[@class='item']/text()");
    private By CAMPAIGN_NAME_COLUMN_VALUES = By.xpath("//div[@col-id ='campaignName']/*/child::span//span[@class='text-primary']");
    private By CAMPAIGN_BUDGET_VALUES = By.xpath("//div[@col-id ='dailyBudget']//span[@role='gridcell']//span[text()]");
    private By COLUMN_FILTER_INPUT = By.xpath("//div[@class='ag-filter']//input");
    private By EDIT_DROPDOWN_KEYWORDS_TARGETS_OPTION = By.xpath("//a[@id='editDropdownAnchor']//a[text()='View Keywords/Targets']");
    private By EDIT_DROPDOWN_ADJUST_CAMPAIGN_BUDGETS_OPTION = By.xpath("//a[@id='editDropdownAnchor']//a[text()='Adjust Campaign Daily Budgets']");
    private By SHOW_ME_CAMPAIGNS_DROPDOWN = By.xpath("//app-selectize[@name='showMe']//div[@class='item' and text()='Campaigns']");
    private By CAMPAIGN_BUDGET_UPDATE_BUTTON = By.xpath("//button/span[text()='Update']");
    private By CAMPAIGN_BUDGET_VALUE_FIELD = By.xpath("//input[@id='bid-value']");
    private By CAMPAIGN_EDIT_LOADING_POPUP = By.xpath("//div[@class='mb-3']");
    private By CAMPAIGN_EDIT_LOADING_BAR = By.xpath("//div[@class='noty_progressbar']");
    private By CAMPAIGN_TYPE_FILTER_DROPDOWN = By.xpath("//input[@id='Campaign Type-selectized']");
    private By CAMPAIGN_TYPE_FILTER_DROPDOWN_OPEN = By.xpath("//div[@class='selectize-input items not-full has-options focus input-active dropdown-active']");
    private By FIDO_BIDDING_STATUS_FILTER = By.xpath("//label[contains(text(),'FIDO Bidding Status')]");
    private By FIDO_BIDDING_STATUS_DROPDOWN = By.id("FIDO Bidding Status-selectized");
    private By CREATE_CAMPAIGN_BUTTON = By.xpath("//a[text()='Create Campaign ']");
    private By BLUE_DOT_TOOLTIP = By.xpath("//div[@class='tooltip-inner']");
    private By ALERT = By.xpath("//div[@class='noty_body']");
    private By CAMPAIGNS_EDIT_BUTTON = By.id("editDropdownAnchor");
    private By FIDO_STATUS_CLEAR_BUTTON = By.id("clearintraDayBiddingEligibilityStatus");

    public ShowMeCampaigns(WebDriver driver) {
        super(driver);
        findElementVisible(SHOW_ME_CAMPAIGNS_DROPDOWN);
    }

    public void clickSpendColumnFilter() throws InterruptedException {
        scrollElementHorizontally(600, TABLE_HORIZONTAL_SCROLLBAR);
        hoverOverElement(SPEND_COLUMN_HEADER);
        click(SPEND_COLUMN_HEADER_FILTER);
    }

    public void filterCampaignType(String type) throws InterruptedException {
        if (!isElementVisible(CAMPAIGN_TYPE_FILTER_DROPDOWN_OPEN)) {
            click(CAMPAIGN_TYPE_FILTER_DROPDOWN);
        }
        click(By.xpath("//div[@data-value='" + type + "']"));
        click(FILTER_APPLY_BUTTON);
        isElementClickable(FILTER_APPLY_BUTTON);
        isElementClickable(FILTER_CLEAR_BUTTON);
        Thread.sleep(2000);
    }

    public CampaignWizardModal clickCreateCampaignButton() throws InterruptedException {
        click(CREATE_CAMPAIGN_BUTTON);
        return new CampaignWizardModal(driver);
    }

    public void clickCheckBoxForSpecificRow(int rowNumber) throws InterruptedException {
        click(By.xpath("(//div[@ref='eLeftContainer']//div[@ref='eCellWrapper']//span[@class='ag-selection-checkbox'])[" + rowNumber + "]"));
    }

    public boolean isCreateCampaignsButtonVisible() {
        return isElementVisible(CREATE_CAMPAIGN_BUTTON);
    }

    public void sortCampaignsBySpendColumn(String filter, String valueToFilter) throws InterruptedException {
        clickSpendColumnFilter();
        click(COLUMN_HEADER_FILTER_DROPDOWN);
        selectColumnFilterToApply(filter);
        sendKeys(COLUMN_FILTER_INPUT, valueToFilter);
        click(COLUMN_FILTER_DROPDOWN_APPLY_BTTN);
        waitForElementClickable(FILTER_APPLY_BUTTON);
        waitForElementClickable(FILTER_CLEAR_BUTTON);
    }

    public List<String> getActiveCampaignNameFilters() {
        return findElementsVisible(CAMPAIGN_NAME_ACTIVE_FILTERS).stream().map(filter -> filter.getText()).collect(Collectors.toList());
    }

    public List<String> getCampaignNameColumnValues() {
        return getAllValuesInSpecificColumn(CAMPAIGN_NAME_COLUMN_VALUES);
    }

    public List<Double> getCampaignBudgetColumnValues() {
        List<String> columnValues = getAllValuesInSpecificColumn(CAMPAIGN_BUDGET_VALUES);
        List<Double> columnValuesConverted = new ArrayList<>();
        for (var column : columnValues) {
            columnValuesConverted.add(SharedMethods.convertToNumber(column));
        }
        return columnValuesConverted;
    }

    public void openEditDropdownAndSelectViewKeywordsTargets() throws InterruptedException {
        openEditDropdown();
        click(EDIT_DROPDOWN_KEYWORDS_TARGETS_OPTION);
    }

    public void openEditDropdownAndSelectAdjustCampaignDailyBudgets(String budgetDropdown, String value) throws InterruptedException {
        openEditDropdown();
        click(EDIT_DROPDOWN_ADJUST_CAMPAIGN_BUDGETS_OPTION);
        click(By.xpath("//app-selectize[@id='bid-type']//div[@class='selectize-dropdown-content']/div[@data-value='" + budgetDropdown + "']"));
        sendKeys(CAMPAIGN_BUDGET_VALUE_FIELD, value);
        click(CAMPAIGN_BUDGET_UPDATE_BUTTON);
        waitForElementToBeInvisible(CAMPAIGN_EDIT_LOADING_POPUP);
        waitForElementToBeEnabled(CAMPAIGN_EDIT_LOADING_BAR, Duration.ofMillis(3000));
        waitForElementToBeInvisible(CAMPAIGN_EDIT_LOADING_BAR);
        waitForElementToBeEnabled(FILTER_APPLY_BUTTON, Duration.ofMillis(3000));
        waitForElementToBeEnabled(FILTER_CLEAR_BUTTON, Duration.ofMillis(3000));
    }

    public boolean isFidoBiddingStatusFilterVisible() {
        return isElementPresent(FIDO_BIDDING_STATUS_FILTER);
    }

    public List<String> getAllFidoBiddingStatusOptions() throws InterruptedException {
        Thread.sleep(2000);
        scrollIntoViewAndClick(FIDO_BIDDING_STATUS_DROPDOWN);
        return getTextFromElements(findElementsVisible(By.xpath("(//div[@class='selectize-dropdown-content'])[8]//div[contains(@class,'option')]")));
    }

    public void selectFidoBiddingStatusFilterOption(String option) throws InterruptedException {
        Thread.sleep(2000);
        if (isElementVisible(FIDO_STATUS_CLEAR_BUTTON)) {
            click(FIDO_STATUS_CLEAR_BUTTON);
        }
        scrollIntoViewAndClick(FIDO_BIDDING_STATUS_DROPDOWN);
        By optionToSelect = By.xpath("(//div[@class='selectize-dropdown-content'])[8]//div[contains(@class,'option') and text()='" + option + "']");
        waitForElementClickable(optionToSelect);
        click(optionToSelect);
        click(FILTER_APPLY_BUTTON);
        Thread.sleep(2000);
    }

    public static By getBlueDotXPath(String campaignThatWasUpdated) {
        return By.xpath("//div[@class='ag-pinned-left-cols-container']//span[contains(text(),'" + campaignThatWasUpdated + "')]/following-sibling::div/img");
    }

    public boolean isFidoBiddingStatusBlueDotDisplayed(String campaignThatWasUpdated) {
        return isElementVisible(getBlueDotXPath(campaignThatWasUpdated));
    }

    public String getToolTipText(String campaignThatWasUpdated) {
        hoverOverElement(getBlueDotXPath(campaignThatWasUpdated));
        if (!isElementVisible(BLUE_DOT_TOOLTIP)) {
            Assert.fail("Tooltip is not visible");
        }
        return getTextFromElement(BLUE_DOT_TOOLTIP);
    }

    public void clickFidoBiddingStatusBlueDot(String campaignThatWasUpdated) throws InterruptedException {
        Thread.sleep(2000);
        clickElement(getBlueDotXPath(campaignThatWasUpdated));
        waitForElementClickable(FILTER_APPLY_BUTTON);
    }

    public String getBlueDotImage(String campaignThatWasUpdated) {
        waitForElementClickable(FILTER_APPLY_BUTTON);
        return getAttribute(getBlueDotXPath(campaignThatWasUpdated), "src");
    }

    public void clickEditButton() throws InterruptedException {
        click(CAMPAIGNS_EDIT_BUTTON);
    }

    public void selectEnableFIDOFromEditDropdown() throws InterruptedException {
        By enableFidoBidding = By.id("EnableFIDO Bidding");
        click(enableFidoBidding);
        waitForElementClickable(FILTER_APPLY_BUTTON);
    }

    public void selectDisableFIDOFromEditDropdown() throws InterruptedException {
        By disableFidoBidding = By.xpath("//a[@id='editDropdownAnchor']//a[4]");
        click(disableFidoBidding);
        waitForElementClickable(FILTER_APPLY_BUTTON);
    }

    public String getAlertText() {
        String alertText = getTextFromElement(ALERT);
        waitForElementToBeInvisible(ALERT);
        return alertText;
    }

    public void clickApplyButton() throws InterruptedException {
        click(FILTER_APPLY_BUTTON);
        waitForElementClickable(FILTER_APPLY_BUTTON);
    }
}
