package com.DC.pageobjects.adc.analyze.paidMediaReporting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class ExecutiveDashboardPage extends NetNewNavigationMenu {
    private static final By EXECUTIVE_DASHBOARD_BREADCRUMB = By.xpath("//a[text()='Executive Dashboard']");
    private static final By FILTER_ASIN = By.xpath("(//div[contains(@class,'MuiInputBase-root MuiOutlinedInput-root MuiInputBase-colorPrimary MuiInputBase-formControl MuiInputBase-sizeSmall')])[3]");
    private static final By FILTER_PERIOD = By.xpath("(//div[contains(@class,'MuiInputBase-root MuiOutlinedInput-root MuiInputBase-colorPrimary MuiInputBase-formControl MuiInputBase-sizeSmall')])[2]");
    private static final By SOURCE_OF_CHANGE_COLUMN = By.xpath("(//table[contains(@class,'MuiTable-root MuiTable-stickyHeader')])[2]/tbody/tr/td[3]");
    private static final By SOURCE_OF_CHANGE_WIDGET = By.cssSelector("[id = 'source-of-change']");
    private static final By SOURCE_OF_CHANGE_WIDGET_LISTBOX = By.xpath("//*[@aria-haspopup = 'listbox' and text() = 'Campaign Type']");
    private static final By SOURCE_OF_CHANGE_PLATFORM = By.cssSelector("[data-value = 'Platform']");
    private static final By SOURCE_OF_CHANGE_MULTIPLE_BU = By.cssSelector("[data-value = 'Business Unit']");
    private static final By AMAZON_PLATFORM = By.cssSelector("button img[alt = 'Amazon Logo']");
    private static final By AMAZON_DSP_LOGO = By.cssSelector("img[alt = 'Amazon DSP Logo']");
    private static final By COMPARISON_PERIOD_SELECTOR = By.id("comparison-period-selector");
    private static final By INTERVAL_SELECTOR = By.id("interval-selector");
    private static final By PLATFORM_SELECTION_WIDGET = By.cssSelector("button[type = 'button']:first-of-type  div + span[class = 'material-symbols-outlined'] ");
    private static final By PROGRESS_BAR = By.cssSelector("[role = 'progressbar']");
    private static final By SOURCE_OF_CHANGE_WIDGET_COLUMN = By.cssSelector("[data-rbd-draggable-id = 'aggTypeValue'] span");
    private static final By SOURCE_OF_CHANGE_WIDGET_PLATFORM_COLUMN_VALUES = By.cssSelector("tr:not(:last-child) :first-of-type[id ^= 'source-of-change-table-row'] span:first-child");

    public final DateAndIntervalPickerPage dateAndIntervalPickerPage;

    public ExecutiveDashboardPage(WebDriver driver) {
        super(driver);
        findElementVisible(EXECUTIVE_DASHBOARD_BREADCRUMB);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
    }

    public void filterByASIN() throws InterruptedException {
        selectItemFromDropdownForListElements(FILTER_ASIN, "ASIN");
    }

    public void selectMonthlyPeriod() throws InterruptedException {
        selectItemFromDropdownForListElements(FILTER_PERIOD, "Monthly");
    }

    public List<Double> getEDPSpendColumnValues() {
        List<String> columnValues = getTextFromElements(findElementsVisible(SOURCE_OF_CHANGE_COLUMN));
        return columnValues.stream().map(SharedMethods::convertToNumber).collect(Collectors.toList())
                .subList(0, columnValues.size() - 1);
    }

    private void moveToSourceOfChangeWidget() { scrollIntoView(SOURCE_OF_CHANGE_WIDGET);}

    public boolean selectMultiplePlatforms() throws InterruptedException {
        boolean f = false;
        if(isElementClickable(AMAZON_PLATFORM)) {
            click(AMAZON_PLATFORM);
            f = isElementPresent(AMAZON_DSP_LOGO);
            click(AMAZON_DSP_LOGO);
            click(PLATFORM_SELECTION_WIDGET);
        }
        return f;
    }

    public boolean verifySourceOfChangeWidgetListBox(String listBoxOption) throws InterruptedException {
        moveToSourceOfChangeWidget();
        boolean f = false;
        if(isElementVisible(SOURCE_OF_CHANGE_WIDGET_LISTBOX)) {
            click(SOURCE_OF_CHANGE_WIDGET_LISTBOX);
             switch(listBoxOption) {
                 case "Platform":
                     f = isElementPresent(SOURCE_OF_CHANGE_PLATFORM);
                     click(SOURCE_OF_CHANGE_PLATFORM);
                     break;
                 case "Business Unit":
                     f = isElementPresent(SOURCE_OF_CHANGE_MULTIPLE_BU);
                     click(SOURCE_OF_CHANGE_MULTIPLE_BU);
                     break;
             }
        }
        return f;
    }

    public boolean verifySourceOfChangeWidgetTable(String sourceOfChangeWidgetColumn, List<String> list) {
        waitForElementToBeInvisible(PROGRESS_BAR);
        boolean f = false;
        f = getTextFromElement(SOURCE_OF_CHANGE_WIDGET_COLUMN).equalsIgnoreCase(sourceOfChangeWidgetColumn);
        List<WebElement> element = driver.findElements(SOURCE_OF_CHANGE_WIDGET_PLATFORM_COLUMN_VALUES);
        for (int i = 0; i < getElementCount(SOURCE_OF_CHANGE_WIDGET_PLATFORM_COLUMN_VALUES); i++) {
            f = list.contains(getTextFromElements(element).get(i));
        }
        return f;
    }
}