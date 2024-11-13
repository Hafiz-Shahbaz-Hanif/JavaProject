package com.DC.pageobjects.adc.execute.mediaManagement.flightDeck;

import com.DC.utilities.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.List;

public class ShowMeASIN extends FlightDeck {

    private By ASIN_DROPDOWN = By.xpath("//app-selectize[@name='showMe']//div[@class='item' and text()='ASIN']");
    private By ASIN_TITLE_COLUMN_VALUES = By.xpath("//div[@col-id ='asinTitle']/*/child::span[text()]");
    private By ASIN_COLUMN_VALUES = By.xpath("//div[@col-id ='asin']/*/child::span/*/child::a[text()]");
    private By AUTO_SALES_PAUSE_VALUES = By.xpath("//div[@col-id ='aspState']/*/child::span/*/child::div/child::span[text()]");
    private By AVAILABILITY_STATE_VALUES = By.xpath("//div[@col-id ='availabilityState']/*/child::span/*/child::div/child::span[text()]");
    private By SPEND_COLUMN_VALUES = By.xpath("//div[@col-id ='spend']/*/child::span[text()]");
    private By CAMPAIGN_STATE_VALUES = By.xpath("//div[@col-id ='state']/*/child::span/*/child::div/child::span[text()]");
    private By FILTERS_ASIN_TEXTBOX = By.xpath("//input[contains(@id, 'ASIN') and contains(@id, '-selectized')]");
    private By CLEAR_ASIN_FILTER_BTTN = By.xpath("//a[@id='clearASIN']");
    private By BUY_BOX_PRICE_AUTO_PAUSE_STATE_FILTER_DROPDOWN = By.xpath("//input[@id='Buy Box Price Auto-Pause State-selectized']");
    private By DELAYED_MESSAGING_AUTO_PAUSE_STATE_FILTER_DROPDOWN = By.xpath("//input[@id='Delayed Messaging Auto-Pause State-selectized']");
    private By WEEKS_ON_HAND_AUTO_PAUSE_STATE_FILTER_DROPDOWN = By.xpath("//input[@id='Weeks on Hand Auto-Pause State-selectized']");
    private By UPLOAD_FILE_BUTTON = By.xpath("//i[@class='fa fa-upload']");
    private By EXPORT_FILE_BUTTON = By.xpath("//i[@class='fa fa-download']");
    private By AUTO_STATE_PAUSE_COLUMN_HEADER = By.xpath("//div[@col-id = \"aspState\" and contains(@class, \"ag-header-cell\")]");
    private By AUTO_PAUSE_STATE_COLUMN_FILTER = By.xpath("//div[@col-id = \"aspState\"]/*/child::div/*/child::span[@class=\"ag-icon ag-icon-menu\"]");
    private By AUTO_PAUSE_FILTER_DROPDOWN = By.xpath("//select[@name='filter-dropdown']");

    public ShowMeASIN(WebDriver driver) {
        super(driver);
        findElementVisible(ASIN_DROPDOWN);
    }

    public void filterASINS(String asinToFilter) throws InterruptedException {
        clearAsinsFilterIfVisible();
        click(FILTERS_ASIN_TEXTBOX);
        sendKeysAndHitEnter(FILTERS_ASIN_TEXTBOX, asinToFilter);
        click(FILTER_APPLY_BUTTON);
        waitForElementClickable(FILTER_APPLY_BUTTON);
    }

    public void filterMultipleASINS(List<String> asinToFilter) throws InterruptedException {
        clearAsinsFilterIfVisible();
        for (String asin : asinToFilter) {
            click(FILTERS_ASIN_TEXTBOX);
            sendKeysAndHitEnter(FILTERS_ASIN_TEXTBOX, asin);
        }
        click(FILTER_APPLY_BUTTON);
        waitForElementClickable(FILTER_APPLY_BUTTON);
        waitForElementClickable(FILTER_CLEAR_BUTTON);
    }

    public void clearAsinsFilterIfVisible() throws InterruptedException {
        if (isElementVisible(CLEAR_ASIN_FILTER_BTTN)) {
            click(CLEAR_ASIN_FILTER_BTTN);
        }
    }

    public void selectAutoPauseFilter(Enums.AutoPauseState state) throws InterruptedException {
        click(By.xpath("//div[@class='option selected active' and text()='" + state + "']"));
    }

    public void selectBuyBoxPriceAutoPauseStateFilter(Enums.AutoPauseState state) throws InterruptedException {
        click(BUY_BOX_PRICE_AUTO_PAUSE_STATE_FILTER_DROPDOWN);
        selectAutoPauseFilter(state);
    }

    public void selectDelayedMessagingAutoPauseStateFilter(Enums.AutoPauseState state) throws InterruptedException {
        click(DELAYED_MESSAGING_AUTO_PAUSE_STATE_FILTER_DROPDOWN);
        selectAutoPauseFilter(state);
    }

    public void selectWeeksOnHandAutoPauseStateFilter(Enums.AutoPauseState state) throws InterruptedException {
        click(WEEKS_ON_HAND_AUTO_PAUSE_STATE_FILTER_DROPDOWN);
        selectAutoPauseFilter(state);
    }

    public List<String> getAsinTitleColumnValues() {
        return getAllValuesInSpecificColumn(ASIN_TITLE_COLUMN_VALUES);
    }

    public List<String> getAsinColumnValues() {
        return getAllValuesInSpecificColumn(ASIN_COLUMN_VALUES);
    }

    public List<String> getAutoSalesPauseColumnValues() {
        return getAllValuesInSpecificColumn(AUTO_SALES_PAUSE_VALUES);
    }

    public boolean isUploadFileButtonVisible() {
        return isElementVisible(UPLOAD_FILE_BUTTON);
    }

    public boolean isExportFileButtonVisible() {
        return isElementVisible(EXPORT_FILE_BUTTON);
    }

    public List<String> getAvailabilityStateColumnValues() {
        return getAllValuesInSpecificColumn(AVAILABILITY_STATE_VALUES);
    }

    public boolean isAutoPauseColumnFilterVisible() {
        hoverOverElement(AUTO_STATE_PAUSE_COLUMN_HEADER);
        return isElementVisible(AUTO_PAUSE_STATE_COLUMN_FILTER);
    }

    public void sortAsinsByAutoPauseStateColumnFilter(Enums.AutoPauseState state) throws InterruptedException {
        clickAutoPauseColumnFilter();
        click(AUTO_PAUSE_FILTER_DROPDOWN);
        selectAutoPauseColumnFilterFromDropdown(state);
        click(COLUMN_FILTER_DROPDOWN_APPLY_BTTN);
        waitForElementClickable(FILTER_APPLY_BUTTON);
        waitForElementClickable(FILTER_CLEAR_BUTTON);
    }

    public void clickAutoPauseColumnFilter() throws InterruptedException {
        hoverOverElement(AUTO_STATE_PAUSE_COLUMN_HEADER);
        click(AUTO_PAUSE_STATE_COLUMN_FILTER);
    }

    public void selectAutoPauseColumnFilterFromDropdown(Enums.AutoPauseState state) throws InterruptedException {
        By dropdownSelection = By.xpath("//option[text()='" + state + "']");
        click(dropdownSelection);
    }

    public List<String> getSpendColumnValues() {
        return getAllValuesInSpecificColumn(SPEND_COLUMN_VALUES);
    }

    public List<String> getCampaignStateColumnValues() {
        return getAllValuesInSpecificColumn(CAMPAIGN_STATE_VALUES);
    }
}
