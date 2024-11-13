package com.DC.pageobjects.adc.analyze.productHealth;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.CommonFeatures;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;

import org.openqa.selenium.*;
import org.testng.Assert;

import java.util.List;
import java.util.NoSuchElementException;


public class SearchFrequencyRankPage extends NetNewNavigationMenu {

    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public DCFilters dcFilters;
    private final By SEARCH_FREQUENCY_HEADER = By.xpath("//a[normalize-space()='Search Frequency Rank']");
    private final By SEARCH_FREQUENCY_RANK_SECTION = By.xpath("//div[@class='MuiBox-root css-axw7ok']/h4[contains(text(),'Search Frequency Rank')]");
    private static final By EXPORT_ICON_EXPORT_GRAPH = By.id("export-graph-icon");
    private static final By EXPORT_ICON_SEARCH_VOLUME_GRAPH = By.id("export-graph-select");
    private static final By EXPORT_ICON_CLICK_CONVERSION = By.id("clickConversionGraph");
    private static final By FIRST_METRICS_TITLE = By.xpath("//h4[text()='Search Frequency Rank']");
    private static final By SECOND_METRICS_TITLE = By.xpath("//h4[text()='Click Share & Conversion Share']");
    private static final By CHART_LABELS_TOGGLE = By.xpath("//label[@id='chartLabels']//preceding-sibling::span/span[1]");
    private static final By SEARCH_BAR = By.id("sfr-search-term-filter");
    private static final By CLEAR_ALL_BTN = By.id("searchTermClearAllButton");
    private static final By PAPER_TOWEL_FILTER = By.xpath("//div[@id='searchTermPopup']//li[2]//input[1]");
    private static final By APPLY_BTN = By.xpath("//button[@id='searchTermApplyButton']");
    private static final By SFR_HEADER = By.xpath("//a[text()='Search Frequency Rank']");
    private final By SFR_CHART_EXPORT_ICON = By.xpath("//h4[text()='Search Frequency Rank']/parent::*/following-sibling::div");
    private final By CLICK_CONVERSION_EXPORT_ICON = By.xpath("//h4[text()='Click Share & Conversion Share']/parent::*/following-sibling::div");
    private static final By SEARCH_VOLUME_TAB = By.id("tab-1");
    private static final By SEARCH_VOLUME_HEADER = By.xpath("//h4[text()='Search Volume']");
    private final By SEARCH_VOLUME_EXPORT_ICON = By.xpath("//h4[text()='Search Volume']/parent::*/following-sibling::div");
    private static final By ADD_NEW_SEARCH_TERM_LOCATOR = By.xpath("//*[text()='Add new Search Term']");
    private static final By SEARCH_BAR_SEARCH_VOLUME = By.id("sfr-search-term-filter");
    private static final By SEARCH_TERM_FREQUENCY_CHART_DATE_LABELS = By.xpath("(//*[@class = \"highcharts-axis-labels highcharts-xaxis-labels\"])[1]/child::*[text()]");
    private static final By SEARCH_TERM_FREQUENCY_CHART_VALUES = By.xpath("//*[name()='g'][@class='highcharts-label highcharts-data-label highcharts-data-label-color-1']/*/*[text()]");
    private static final By SELECTED_SEARCH_TERM = By.xpath("//*[@id = 'selectedSearchTermsContainer']/*/child::span[text()]");
    private static final By APPLY_BUTTON_SEARCH = By.id("searchTermApplyButton");


    public SearchFrequencyRankPage(WebDriver driver) {
        super(driver);
        //waitForElementVisibility(SEARCH_FREQUENCY_HEADER);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
        dcFilters = new DCFilters(driver);
    }

    public boolean isSearchFrequencyScreenDisplayed() {
        return isElementVisible(SEARCH_FREQUENCY_HEADER);
    }

    public boolean isSearchFrequencyRankSectionNotDisplayed() {
        UI_LOGGER.info("Verify Search Frequency Rank section is not visible");
        return isElementNotVisible(SEARCH_FREQUENCY_RANK_SECTION);
    }

    public boolean verifyDisplayOfDownloadIconSFR() {
        return isElementVisible(EXPORT_ICON_EXPORT_GRAPH);
    }

    public boolean verifyDisplayOfDownloadIconSearchVolume() {
        return isElementVisible(EXPORT_ICON_SEARCH_VOLUME_GRAPH);
    }

    public boolean verifyDisplayOfDownloadIconClickConversion() {
        return isElementVisible(EXPORT_ICON_CLICK_CONVERSION);
    }

    public String getFirstMetricsTitle() {

        findElementVisible(FIRST_METRICS_TITLE);
        return getTextFromElement(FIRST_METRICS_TITLE);
    }

    public boolean verifyDisplayOfChartLabelsToggle() {
        return isElementVisible(CHART_LABELS_TOGGLE);
    }

    public By getChartLabelsToggle() {
        return CHART_LABELS_TOGGLE;
    }

    public String getSearchVolumeTitle() {
        findElementVisible(SEARCH_VOLUME_HEADER);
        return getTextFromElement(SEARCH_VOLUME_HEADER);
    }

    public void toggleValidation(By toggleLocator, String toggleName) throws InterruptedException {
        String classAttribute = getAttribute(toggleLocator, "class");
        if (classAttribute.contains("Mui-checked")) {
            UI_LOGGER.info(toggleName + " toggle ON");
            click(toggleLocator);
            classAttribute = getAttribute(toggleLocator, "class");
            if (classAttribute.contains("Mui-checked")) {
                UI_LOGGER.error(toggleName + " toggle ON");
                org.testng.Assert.fail(toggleName + " toggle ON");
            } else {
                UI_LOGGER.info(toggleName + " toggle OFF");
            }
        } else {
            UI_LOGGER.error(toggleName + " toggle OFF");
            Assert.fail(toggleName + " toggle OFF");
        }
    }

    public boolean verifySearchBarDisplayed() {
        return isElementVisible(SEARCH_BAR);
    }

    public String getSecondMetricsTitle() {
        findElementVisible(SECOND_METRICS_TITLE);
        return getTextFromElement(SECOND_METRICS_TITLE);
    }

    public void verifySearchFunctionality(String searchItem) throws InterruptedException {
        UI_LOGGER.info("Click on Click All Button");
        click(CLEAR_ALL_BTN);
        UI_LOGGER.info("All filter are Cleared");
        UI_LOGGER.info("Type " + searchItem + " in Search bar ");
        sendKeys(SEARCH_BAR, searchItem);
        List<WebElement> searchItemOptions = findElementsVisible(By.xpath("//ul[@id='sfr-search-term-filter-listbox']/li"));
        for (WebElement searchItemOption : searchItemOptions) {
            if (searchItemOption.getText().equals(searchItem)) {
                searchItemOption.click();
                break;
            }
        }
        click(APPLY_BUTTON_SEARCH);
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
    }

    public boolean isSFRExportIconDisplayed() {
        try {
            WebElement exportIcon = findElementVisible(SFR_CHART_EXPORT_ICON);
            if (!exportIcon.isDisplayed()) {
                scrollIntoView(exportIcon);
                exportIcon = findElementVisible(SFR_CHART_EXPORT_ICON);
            }
            return exportIcon.isDisplayed();
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e) {
            return false;
        }
    }

    public boolean isClickAndConversionExportIconDisplayed() {
        try {
            WebElement exportIcon = findElementVisible(CLICK_CONVERSION_EXPORT_ICON);
            if (!exportIcon.isDisplayed()) {
                scrollIntoView(exportIcon);
                exportIcon = findElementVisible(CLICK_CONVERSION_EXPORT_ICON);
            }
            return exportIcon.isDisplayed();
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e) {
            return false;
        }
    }

    public void clickOnSFRExportIcon() throws InterruptedException {
        click(SFR_CHART_EXPORT_ICON);
    }

    public void clickOnSearchVolumeExportIcon() throws InterruptedException {
        click(SEARCH_VOLUME_EXPORT_ICON);
    }

    public void clickOnClickConversionExportIcon() throws InterruptedException {
        scrollIntoViewAndClick(CLICK_CONVERSION_EXPORT_ICON);
    }

    public void clickExportButton(String exportButtonName) throws InterruptedException {
        By exportButton = By.xpath("//ul[@role='listbox']/li[text()='" + exportButtonName + "']");
        click(exportButton);
    }

    public boolean verifyDisplayOfCentralSFRScreen() {
        return isElementVisible(SEARCH_FREQUENCY_HEADER);
    }

    public boolean isSearchVolumeExportIconDisplayed() {
        try {
            WebElement exportIcon = findElementVisible(SEARCH_VOLUME_EXPORT_ICON);
            if (!exportIcon.isDisplayed()) {
                scrollIntoView(exportIcon);
                exportIcon = findElementVisible(SEARCH_VOLUME_EXPORT_ICON);
            }
            return exportIcon.isDisplayed();
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e) {
            return false;
        }
    }

    public void verifySearchVolumeTabIsCickable() throws InterruptedException {
        UI_LOGGER.info("Click on Search Volume Tab");
        click(SEARCH_VOLUME_TAB);
    }

    public boolean isSearchVolumeScreenDisplayed() {
        return isElementVisible(SEARCH_VOLUME_HEADER);
    }

    public void selectSearchTermInSearchBar(String searchItem) throws InterruptedException {
        if (isElementVisible(CLEAR_ALL_BTN)) {
            click(CLEAR_ALL_BTN);
        }
        click(SEARCH_BAR);
        sendKeys(SEARCH_BAR, searchItem);
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
        List<WebElement> searchItemOptions = findElementsVisible(By.xpath("//ul[@id='sfr-search-term-filter-listbox']/li"));
        for (WebElement searchItemOption : searchItemOptions) {
            if (searchItemOption.getText().equals(searchItem)) {
                searchItemOption.click();
                break;
            }
        }
        click(APPLY_BUTTON_SEARCH);
        By searchItemTagLocator = By.xpath("//div[@id='selectedSearchTermsContainer']/div/span");
        String searchItemTag = getTextFromElement(searchItemTagLocator);
        if (searchItemTag.equals(searchItem)) {
            UI_LOGGER.info("Correct Search Item is selected " + searchItem);
            By searchItemLocatorInTableTemp = By.xpath("//span[@data-z-index='2' and contains(text(), '" + searchItem + "')]");
            findElementVisible(searchItemLocatorInTableTemp);
        } else {
            Assert.fail("Incorrect Search Item is selected. Expected: " + searchItem + " Actual: " + searchItemTag);
        }
    }

    public boolean verifyVolumeTabSearchBarDisplayed() {
        return isElementVisible(SEARCH_BAR_SEARCH_VOLUME);
    }

    public List<String> getDateLabelsFromSFRChart() {
        UI_LOGGER.info("Get Date Labels From SFR Chart");
        List<WebElement> dateLabels = findElementsVisible(SEARCH_TERM_FREQUENCY_CHART_DATE_LABELS);
        UI_LOGGER.info(dateLabels);
        return getTextFromElements(dateLabels);
    }

    public List<String> getValuesFromSFRChart() {
        UI_LOGGER.info("Get Values From SFR Chart");
        List<WebElement> SFRValues = findElementsVisible(SEARCH_TERM_FREQUENCY_CHART_VALUES);
        return getTextFromElements(SFRValues);
    }

    public String getSelectedSearchTerm() {
        UI_LOGGER.info("Get selected search term");
        return getTextFromElement(SELECTED_SEARCH_TERM);
    }
}
