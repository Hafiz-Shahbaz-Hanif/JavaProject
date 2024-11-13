package com.DC.pageobjects.adc.execute.dspControlPanel;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.sharedElements.AGTableCommonFeatures;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DSPControlPanelPage extends NetNewNavigationMenu {

    private static final By DSP_CONTROL_PANEL_BREADCRUMB = By.xpath("//a[text()='DSP Control Panel']");
    private static final By DSP_CONTROL_PANEL_GRID = By.id("dsp-control-panel");
    private static final By DSP_CONTROL_PANEL_GRID_SIDEBAR_BUTTON = By.cssSelector("[ref = 'sideBarButtons'] [type = 'button']");
    private static final By DSP_CONTROL_PANEL_GRID_ROW_CHECKBOX = By.cssSelector("[id = 'dsp-control-panel' ] div[col-id = 'checkbox'][role = 'gridcell'] [ref = 'eCheckbox']");
    private static final By DSP_CONTROL_PANEL_GRID_EDIT_BUTTON = By.xpath("//button[@type = 'button' and text() = \"Edit\"]");
    private static final By DSP_CONTROL_PANEL_GRID_BULK_EDIT_BUTTON = By.xpath("//button[@type = 'button' and text() = \"Bulk Edit\"]");
    private static final By AUTOMATION_BULK_STATUS_ENABLE_FREQUENCY = By.id("enable-frequency");
    private static final By AUTOMATION_BULK_STATUS_DISABLE_FREQUENCY = By.id("disable-frequency");
    private static final By AUTOMATION_BULK_STATUS_POPUP_FREQUENCY_CAP_SECTION = By.xpath("//button[text() = 'Frequency Cap']");
    private static final By AUTOMATION_BULK_STATUS_POPUP_LBB_SECTION = By.xpath("//button[text() = 'LBB']");
    private static final By AUTOMATION_BULK_STATUS_POPUP_OOS_SECTION = By.xpath("//button[text() = 'OOS']");
    private static final By AUTOMATION_BULK_STATUS_POPUP_CROSS_ICON = By.xpath("//button/span[text() = 'close']");
    private static final By DSP_CONTROL_PANEL_GRID_ALL_CHECKBOX = By.id("custom-header-checkbox");
    private static final By AUTOMATION_FILTER_ENABLED_VALUE = By.xpath("//ul/child::span[text()='ENABLED']");
    private static final By AUTOMATION_FILTER_PAUSED_VALUE = By.xpath("//ul/child::span[text()='PAUSED']");
    private static final By AUTOMATION_FILTER_PENDING_VALUE = By.xpath("//ul/child::span[text()='PENDING']");
    private static final By AUTOMATION_FILTER_NON_COMPLIANT_VALUE = By.xpath("//ul/child::span[text()='NON_COMPLIANT']");
    private static final By DSP_CONTROL_PANEL_GRID_LINE_ITEM_NAME_COLUMN = By.cssSelector("[id = 'dsp-control-panel' ] div[col-id = 'name']");
    private static final By DSP_CONTROL_PANEL_GRID_AUTOMATION_STATUS_DATA = By.cssSelector("[id = 'dsp-control-panel' ] div[col-id = 'automation'][role = 'gridcell'] span[aria-label]");
    private static final By AUTOMATION_COLUMN_TOOLTIP = By.cssSelector("[role = 'tooltip'] div");

    public final DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public final DCFilters dcFilters;
    public AGTableCommonFeatures tableCommonFeatures;

    public DSPControlPanelPage(WebDriver driver) {
        super(driver);
        findElementVisible(DSP_CONTROL_PANEL_BREADCRUMB);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
        dcFilters = new DCFilters(driver);
        tableCommonFeatures = new AGTableCommonFeatures(driver);
    }

    public void moveToDspControlPanelGrid() throws InterruptedException {
        waitForDOMStabilization();
        findElementPresent(DSP_CONTROL_PANEL_GRID);
        scrollIntoView(DSP_CONTROL_PANEL_GRID_SIDEBAR_BUTTON);
        clickElementUsingJavascriptExecutor(DSP_CONTROL_PANEL_GRID_SIDEBAR_BUTTON);
    }

    public void clickBulkEditButtonOnDspControlPanelGrid() throws InterruptedException {
        clickElement(DSP_CONTROL_PANEL_GRID_ALL_CHECKBOX);
        clickElement(DSP_CONTROL_PANEL_GRID_BULK_EDIT_BUTTON);
    }

    public boolean verifyAutomationBulkSelectPopupElements() {
        return isElementVisible(AUTOMATION_BULK_STATUS_POPUP_FREQUENCY_CAP_SECTION) & isElementVisible(AUTOMATION_BULK_STATUS_POPUP_LBB_SECTION)
                & isElementVisible(AUTOMATION_BULK_STATUS_POPUP_OOS_SECTION) & isElementVisible(AUTOMATION_BULK_STATUS_ENABLE_FREQUENCY)
                & isElementVisible(AUTOMATION_BULK_STATUS_DISABLE_FREQUENCY);
    }

    public void closeAutomationsBulkSelectPopup() {
        clickElement(AUTOMATION_BULK_STATUS_POPUP_CROSS_ICON);
    }

    public void selectAutomationFilterValue(String filterValue) throws InterruptedException {
        switch (filterValue) {
            case "ENABLED":
                click(AUTOMATION_FILTER_ENABLED_VALUE);
                break;
            case "PAUSED":
                click(AUTOMATION_FILTER_PAUSED_VALUE);
                break;
            case "PENDING":
                click(AUTOMATION_FILTER_PENDING_VALUE);
                break;
            case "NON_COMPLIANT":
                click(AUTOMATION_FILTER_NON_COMPLIANT_VALUE);
                break;
        }
    }

    public void selectAutomationFilter(String automationFilter) throws InterruptedException {
        selectAutomationFilterValue(automationFilter);
        dcFilters.apply();
    }

    public boolean verifyDspControlPanelGridDataAgainstPausedAutomationStatus() throws InterruptedException {
        List<WebElement> elementList = driver.findElements(DSP_CONTROL_PANEL_GRID_AUTOMATION_STATUS_DATA);
        boolean f = false;
        for (WebElement element : elementList) {
            hoverOverElement(element);
            String text = getTextFromElement(AUTOMATION_COLUMN_TOOLTIP);
            if (text.contains("Automation on this Line Item is inactive")) {
                hoverOverElement(DSP_CONTROL_PANEL_GRID_LINE_ITEM_NAME_COLUMN);
                f = true;
            } else {
                f = false;
                break;
            }
        }
        return f;
    }
}
