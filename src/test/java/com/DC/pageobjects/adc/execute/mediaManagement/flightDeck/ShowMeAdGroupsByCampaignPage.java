package com.DC.pageobjects.adc.execute.mediaManagement.flightDeck;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

public class ShowMeAdGroupsByCampaignPage extends FlightDeck {

    private static final By AD_GROUP_NAME_COLUMN = By.xpath("//div[@col-id='adGroupName']");
    private static final By STATE_COLUMN = By.xpath("//div[@col-id='status']");
    private static final By INTRA_DAY_MULTIPLIER_COLUMN = By.xpath("//div[@col-id='dayPartingConfigurationId']");
    private static final By INTRA_DAY_POPUP = By.xpath("//div[@id='noty_layout__topRight']");
    private static final By INTRADAY_MULTIPLIER_APPLY_BUTTON = By.xpath("//div[@class='modal-footer']//button[@type='submit']");
    private static final By INTRADAY_MULTIPLIER_ICON_MENU = By.xpath("//div[@col-id='dayPartingConfigurationId']//span[@class='ag-icon ag-icon-menu']");
    private static final By INTRADAY_MULTIPLIER_COLUMN_APPLY_BUTTON = By.xpath("//div[@class='ag-tab-body']//button[@type='submit']");
    private static final By INTRADAY_MULTIPLIER_COLUMN_FILTER_SEARCH_BAR = By.xpath("//select[@name='filter-dropdown']");
    private static final By EDIT_DROPDOWN = By.xpath("//button[@id ='editDropdown']");
    private static final By SHOW_ME_CAMPAIGNS_DROPDOWN = By.xpath("//app-selectize[@name='showMe']//div[@class='item' and text()='Adgroup (by Campaign)']");
    private static final By LOADING_POP_UP = By.xpath("//span[text()='Loading...']");
    private static final By REQUEST_IN_PROGRESS_POP_UP = By.xpath("//div[contains(text(),'Your request is in progress')]");


    public ShowMeAdGroupsByCampaignPage(WebDriver driver) throws InterruptedException {
        super(driver);
        findElementVisible(SHOW_ME_CAMPAIGNS_DROPDOWN);
    }

    public List<String> loadAdGroupNameColumnValues() {
        waitForDataToLoad();
        return loadColumnValues(AD_GROUP_NAME_COLUMN);
    }

    public List<String> loadStateColumnValues() {
        waitForDataToLoad();
        return loadColumnValues(STATE_COLUMN);
    }

    private void waitForDataToLoad() {
        findElementVisible(By.xpath("//div[@row-id='0']"));
    }

    public void selectCheckBoxElementByAdGroupName(String addGroupName) {
        var checkBoxElement = loadElementByColIdAndAdGroupName("0", addGroupName);
        checkBoxElement.click();
    }

    public void selectValueFromIntraDayMultiplierDropdown(String value) throws InterruptedException {
        click(By.xpath("//a[contains(text(),'" + value + "')]"));
    }

    public void selectValueFromBulkIntradayMultiplier(String value) throws InterruptedException {
        click(By.xpath("//div[contains(text(),'" + value + "')]"));
    }

    public void clickIntraDayMultiplierApplyButton() throws InterruptedException {
        click(INTRADAY_MULTIPLIER_APPLY_BUTTON);
        findElementVisible(REQUEST_IN_PROGRESS_POP_UP);
        waitForElementToBeInvisible(REQUEST_IN_PROGRESS_POP_UP);
    }

    public WebElement loadIntraDayElementByAdGroupName(String addGroupName) {
        return loadElementByColIdAndAdGroupName("dayPartingConfigurationId", addGroupName);
    }

    public WebElement loadStateElementByAdGroupName(String addGroupName) {
        return loadElementByColIdAndAdGroupName("status", addGroupName);
    }

    public WebElement loadElementByColIdAndAdGroupName(String colId, String adGroupName) {
        String rowXpath = String.format("//span[contains(text(),'%s')]/ancestor::div[@role='row']", adGroupName);
        WebElement rowElement = findElementVisible(By.xpath(rowXpath));
        return findElementWithinAnotherElement(rowElement, By.xpath(".//div[@col-id='" + colId + "']"), Duration.ofSeconds(5));
    }

    public void selectIntraDayMultiplierFilterOption(String optionToSelect) throws InterruptedException {
        click(INTRADAY_MULTIPLIER_ICON_MENU);
        click(INTRADAY_MULTIPLIER_COLUMN_FILTER_SEARCH_BAR);
        var xpath = "//option[contains(text(),'" + optionToSelect + "')]";
        findElementVisible(By.xpath(xpath)).click();
        click(INTRADAY_MULTIPLIER_COLUMN_APPLY_BUTTON);
        findElementVisible(LOADING_POP_UP);
        waitForElementToBeInvisible(LOADING_POP_UP);
    }

    public List<String> loadIntraDayMultiplierColumnValues() {
        return loadColumnValues(INTRA_DAY_MULTIPLIER_COLUMN);
    }

    public List<String> loadColumnValues(By columnHeader) {
        var columnValues = getTextFromElementsMilliseconds(columnHeader);
        return columnValues.subList(1, columnValues.size());
    }

    public void selectOptionForStateElement(WebElement stateElement, String optionToSelect) throws InterruptedException {
        var xpath = "//option[@value='" + optionToSelect + "']";
        selectOptionForCellElement(stateElement, xpath);
    }

    public void selectOptionForIntraDayElement(WebElement intraDayElement, String optionToSelect) throws InterruptedException {
        var xpath = "//option[contains(text(),'" + optionToSelect + "')]";
        selectOptionForCellElement(intraDayElement, xpath);
        findElementPresent(INTRA_DAY_POPUP);
    }

    private void selectOptionForCellElement(WebElement element, String xpath) throws InterruptedException {
        element.click();
        element.click();
        findElementVisible(By.xpath(xpath)).click();
        Thread.sleep(2000);
    }

    public String getIntraDayPopUpMessage(String popUpMessagePattern) {
        Pattern pattern = Pattern.compile(popUpMessagePattern);
        waitTextInElementToMatch(INTRA_DAY_POPUP, pattern, Duration.ofSeconds(8));
        return getTextFromElement(INTRA_DAY_POPUP);
    }
}

