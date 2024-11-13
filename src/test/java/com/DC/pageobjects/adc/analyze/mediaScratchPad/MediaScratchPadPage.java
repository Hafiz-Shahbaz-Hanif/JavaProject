package com.DC.pageobjects.adc.analyze.mediaScratchPad;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

public class MediaScratchPadPage extends NetNewNavigationMenu {
    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public DCFilters dcFilters;
    private static final By MEDIA_SCRATCHPAD_BREADCRUMB = By.xpath("//a[text()='Media Scratchpad']");
    private static final By PERIOD_COMPARISON_DROPDOWN = By.xpath("//app-selectize[@class='yoy-selectize ng-untouched ng-pristine ng-valid']");
    private static final By SLICE_BY_DROPDOWN = By.xpath("//app-selectize[@class='yoy-selectize ng-untouched ng-pristine ng-valid']//following::app-selectize[@class='ng-untouched ng-pristine ng-valid']");
    private static final By SLICE_BY_DROPDOWN_VALUE = By.xpath("(//div[@class='selectize-dropdown-content'])[2]//div[contains(@class, 'option')]");
    private static final By PERIOD_COMPARISON_DROPDOWN_VALUE = By.xpath("(//div[@class='selectize-dropdown-content'])[1]//div[contains(@class, 'option')]");
    private static final By APPLY_BUTTON = By.xpath("//button[@title='Apply']");
    private static final By PERIOD_COMPARISON_TEXT = By.xpath("//b[contains(text(), 'Period Comparison')]//following::p[1]");
    private static final By SLICE_BY_TEXT = By.xpath("//b[contains(text(), 'Slice By')]//following::p[1]");
    private static final By DSP_CREATIVE_WIDGET = By.xpath("//app-dsp-creative-widget");
    private static final By DSP_INVENTORY_WIDGET_BUTTON = By.xpath("//button[@type = 'button']/*[text() = 'Inventory']");
    private static final By DSP_GEOGRAPHY_WIDGET_BUTTON = By.xpath("//button[@type = 'button']/*[text() = 'Geography']");
    private static final By DSP_INVENTORY_WIDGET_SITE_DATA = By.cssSelector("app-dsp-inventory-widget [col-id = 'siteName_inv']  [role = 'gridcell']");
    private static final By DSP_GEOGRAPHY_WIDGET_SITE_DATA = By.cssSelector("app-dsp-geography-widget [col-id = 'lineItemName_geography']  [role = 'gridcell']");

    public MediaScratchPadPage(WebDriver driver) {
        super(driver);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
        dcFilters = new DCFilters(driver);
        findElementVisible(MEDIA_SCRATCHPAD_BREADCRUMB);
    }

    public List<String> getPeriodComparisonDropdownValues() throws InterruptedException {
        click(PERIOD_COMPARISON_DROPDOWN);
        List<WebElement> linkElements = findElementsVisible(PERIOD_COMPARISON_DROPDOWN_VALUE);
        return getTextFromElements(linkElements);
    }

    public List<String> getSliceByDropdownValues() throws InterruptedException {
        click(SLICE_BY_DROPDOWN);
        List<WebElement> linkElements = findElementsVisible(SLICE_BY_DROPDOWN_VALUE);
        return getTextFromElements(linkElements);
    }

    public void selectValuePeriodComparisonDropDown(String periodComparisonDropdownValue) {
        selectOptionFromDropdownByText(PERIOD_COMPARISON_DROPDOWN, periodComparisonDropdownValue);
    }

    public void selectValueSliceByDropDownAndApplySearch(String value) throws InterruptedException {
        selectOptionFromDropdownByText(SLICE_BY_DROPDOWN, value);
        click(APPLY_BUTTON);
        waitForElementClickable(APPLY_BUTTON);
    }

    public String getDynamicPeriodComparisonText() {
        return getTextFromElement(PERIOD_COMPARISON_TEXT);
    }

    public String getDynamicSliceByText() {
        return getTextFromElement(SLICE_BY_TEXT);
    }

    public void clickOnInventoryWidget() throws InterruptedException {
        scrollIntoView(DSP_CREATIVE_WIDGET);
        if(isElementVisible(DSP_INVENTORY_WIDGET_BUTTON)) {
            click(DSP_INVENTORY_WIDGET_BUTTON);
        }
    }

    public void clickOnGeographyWidget() throws InterruptedException {
        scrollIntoView(DSP_CREATIVE_WIDGET);
        if(isElementVisible(DSP_GEOGRAPHY_WIDGET_BUTTON)) {
            click(DSP_GEOGRAPHY_WIDGET_BUTTON);
        }
    }

    public boolean isInventoryWidgetDataLoaded() {
        return isElementVisible(DSP_INVENTORY_WIDGET_SITE_DATA, Duration.ofSeconds(120));
    }

    public boolean isGeographWidgetDataLoaded() {
        return isElementVisible(DSP_GEOGRAPHY_WIDGET_SITE_DATA, Duration.ofSeconds(120));
    }
}