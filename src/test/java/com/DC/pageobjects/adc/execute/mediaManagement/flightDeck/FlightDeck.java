package com.DC.pageobjects.adc.execute.mediaManagement.flightDeck;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightDeck extends NetNewNavigationMenu {
    private static final By DATE_SELECTION = By.xpath("//div[@id='ddlDateRange']");
    private static final By FLIGHT_DECK_TABLE_SPEND_COLUMN = By.xpath("//span[@aria-colindex='8']");
    private static final By FLIGHT_DECK_TABLE_SCROLL_BAR = By.xpath("//div[@class='ag-body-horizontal-scroll-viewport']");
    private By FLIGHT_DECK_HEADER = By.xpath("//h5//span[text()='FlightDeck']");
    private By SHOW_ME_DROPDOWN = By.xpath("//app-selectize[@name='showMe']");
    private By SHOW_ME_DROPDOWN_OPEN = By.xpath("//div[@class='ng-tns-c12-1 ng-trigger ng-trigger-transformPanel mat-select-panel myPanelClass']");
    private By EDIT_DROPDOWN = By.xpath("//button[@id ='editDropdown']");
    private By UPLOAD_FILE_BUTTON = By.xpath("//a[@title='Upload File']");
    private By EXPORT_FILE_BUTTON = By.xpath("//button[@title='Export']");
    public By FILTER_APPLY_BUTTON = By.xpath("//button[@title='Apply']");
    public By FILTER_CLEAR_BUTTON = By.xpath("//button[@title='Clear']");
    public By COLUMN_FILTER_DROPDOWN_APPLY_BTTN = By.xpath("//button[@type='submit']//i");
    public By SELECT_ALL_CHECKBOX = By.xpath("//app-header-checkbox-renderer");
    public By TABLE_HORIZONTAL_SCROLLBAR = By.xpath("//div[@ref='eBodyHorizontalScrollViewport']");
    public By PLATFORM_SELECT_FILTER = By.xpath("//select[@id='Platform']/..");
    private static final By FILTER_COLLAPSE_ICON = By.xpath("//button[@class='btn']");
    private By CAMPAIGN_NAME_FILTER = By.id("Campaign Name-selectized");
    private By LOCKED_BID_IN_TABLE = By.xpath("//div[@col-id='bid']//span[@class='badge']");

    Map<Enums.FlightDeckShowMe, String> flightDeckShowMeOptions = new HashMap<>() {{
        put(Enums.FlightDeckShowMe.CAMPAIGNS, "Campaigns");
        put(Enums.FlightDeckShowMe.KEYWORDS, "Keywords/Targets (by campaign)");
        put(Enums.FlightDeckShowMe.KEYWORDS_ROLLED_UP, "Keywords/Targets (rolled up)");
        put(Enums.FlightDeckShowMe.ASIN, "ASIN");
        put(Enums.FlightDeckShowMe.CUSTOMER_SEARCH_QUERY, "Customer Service Query");
        put(Enums.FlightDeckShowMe.AD_GROUPS_BY_CAMPAIGN, "Adgroup (by Campaign)");
    }};

    public FlightDeck(WebDriver driver) {
        super(driver);
        //findElementVisible(FLIGHT_DECK_HEADER);
    }

    public By createSectionLocator(String sectionName) {
        return By.xpath("//div[@class='MuiGrid-root MuiGrid-container MuiGrid-spacing-xs-2 css-1y9fiox']//p[text()='" + sectionName + "']");
    }

    public void clickOnSection(String sectionName) throws InterruptedException {
        click(createSectionLocator(sectionName));
    }

    public By createPageLocator(String PageName) {
        isElementVisible(By.xpath("//div[@class='MuiGrid-root MuiGrid-item MuiGrid-grid-xs-3 css-e6lmdt']//p[text()='" + PageName + "']"));
        return By.xpath("//div[@class='MuiGrid-root MuiGrid-item MuiGrid-grid-xs-3 css-e6lmdt']//p[text()='" + PageName + "']");
    }

    public void clickOnPage(String PageName) throws InterruptedException {
        click(createPageLocator(PageName));
    }

    public boolean isEditButtonEnabled() {
        return findElementVisible(EDIT_DROPDOWN).isEnabled();
    }

    public boolean isUploadFileButtonVisible() {
        return isElementVisible(UPLOAD_FILE_BUTTON);
    }

    public boolean isExportFileButtonVisible() {
        return isElementVisible(EXPORT_FILE_BUTTON);
    }

    public List<String> getAllValuesInSpecificColumn(By column) {
        return findElementsVisible(column).stream().map(columnValue -> columnValue.getText()).collect(Collectors.toList());
    }

    public void openShowMeDropdown() throws InterruptedException {
        if (!isElementVisibleMilliseconds(SHOW_ME_DROPDOWN_OPEN)) {
            click(SHOW_ME_DROPDOWN);
        }
    }

    public boolean isOptionInShowMeDropdownVisible(Enums.FlightDeckShowMe option) {
        return isElementVisibleMilliseconds(By.xpath("//app-selectize[@name='showMe']//div[@class='item' and text()='" + flightDeckShowMeOptions.get(option) + "']"));
    }

    public FlightDeck selectShowMeOption(Enums.FlightDeckShowMe option) throws Exception {
        openShowMeDropdown();
        click(By.xpath("//app-selectize[@name='showMe']//div[@class='selectize-dropdown-content']//div[contains(@class,'option') and text()='" + flightDeckShowMeOptions.get(option) + "']"));
        switch (option) {
            case CAMPAIGNS:
                return new ShowMeCampaigns(driver);
            case KEYWORDS:
                return new ShowMeKeywords(driver);
            case KEYWORDS_ROLLED_UP:
                return new ShowMeKeywordsRolledUp(driver);
            case ASIN:
                return new ShowMeASIN(driver);
            case CUSTOMER_SEARCH_QUERY:
                return new ShowMeCustomerSearchQuery(driver);
            case AD_GROUPS_BY_CAMPAIGN:
                return new ShowMeAdGroupsByCampaignPage(driver);
        }
        throw new Exception("Unable to select Show Me dropdown " + option);
    }

    public void selectLastMonth() throws InterruptedException {
        selectDateRange("Last Month");
    }
    
    public void selectDateRange(String dateRange) throws InterruptedException {
        click(DATE_SELECTION);
        doubleClick(By.xpath("//li[text()='" + dateRange + "']"));
    }

    public List<Double> getFlightDeckSpendColumnValues() {
        scrollIntoView(FLIGHT_DECK_TABLE_SCROLL_BAR);
        scrollElementHorizontally(1000, FLIGHT_DECK_TABLE_SCROLL_BAR);
        List<String> columnValues = getTextFromElements(findElementsVisible(FLIGHT_DECK_TABLE_SPEND_COLUMN));
        return columnValues.stream().map(SharedMethods::convertToNumber).collect(Collectors.toList());
    }

    public void selectColumnFilterToApply(String filter) throws InterruptedException {
        By dropdownSelection = By.xpath("//option[text()='" + filter + "']");
        click(dropdownSelection);
    }

    public boolean isSelectAllCheckBoxSelected() {
        return isElementVisibleMilliseconds(By.xpath(SELECT_ALL_CHECKBOX + "//span[contains(@class, \"checkbox-checked\")]"));
    }

    public void checkSelectAllCheckBox() throws InterruptedException {
        if (!isSelectAllCheckBoxSelected()) {
            click(SELECT_ALL_CHECKBOX);
        }
    }

    public void openEditDropdown() throws InterruptedException {
        click(EDIT_DROPDOWN);
    }

    public void clickApplyButton() throws InterruptedException {
        click(FILTER_APPLY_BUTTON);
        waitForElementClickable(FILTER_APPLY_BUTTON);
        waitForElementClickable(FILTER_CLEAR_BUTTON);
    }

    public void selectPlatform(String platformName) throws InterruptedException {
        selectItemFromDropdown(PLATFORM_SELECT_FILTER, platformName);
        waitForElementNonClickable(FILTER_CLEAR_BUTTON);
        waitForElementClickable(FILTER_CLEAR_BUTTON);
    }

    public void collapseFilter() throws InterruptedException {
        click(FILTER_COLLAPSE_ICON);
    }

    public void selectCampaignName(String campaignName) throws InterruptedException {
        scrollIntoViewAndClick(CAMPAIGN_NAME_FILTER);
        sendKeys(CAMPAIGN_NAME_FILTER, campaignName);
        waitForElementClickable(FILTER_APPLY_BUTTON);
        click(FILTER_APPLY_BUTTON);
    }

    public boolean isBidLockedVisible() {
        return isElementVisible(LOCKED_BID_IN_TABLE);
    }
}