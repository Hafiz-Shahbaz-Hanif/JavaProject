package com.DC.pageobjects.adc.analyze.retailReporting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.DateUtility;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FilenameFilter;
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class RoundUpPage extends NetNewNavigationMenu {

    public static final By ROUND_UP_BREADCRUMB = By.xpath("//a[text()='Round Up']");
    private final By BU_FILTER = By.xpath("(//span[contains(@class, 'css-1rzb3uu')]/../.. | //button[text()='Select a business unit'])[2]");
    private static final By ROUND_UP_HEADER = By.xpath("//a[text()='Round Up']");
    private final By BU_OPTIONS = By.xpath("//div[@class='MuiBox-root css-dvxtzn']");
    private final By SELECT_BU_RADIO_BUTTON = By.xpath("//span[contains(@class, 'css-7jwj1x')]/input[not(@checked)] | //span[contains(@class, 'css-17nlnql')]/input");
    private final By SEARCH_BAR_BU_SELECTOR = By.xpath("//input[@placeholder='Search Company Name'] | //input[@placeholder='Type to filter']");
    private final By BU_SELECTOR_SAVE_BUTTON = By.xpath("//div[@role='tooltip']//button[@type='button'][normalize-space()='Save']");
    private static final By APPLY_BUTTON = By.xpath("//button[text()='Apply']");
    private static final By DISTRIBUTOR_VIEW = By.cssSelector("[id = 'sidebar-filter-distributor-view'] input");
    private static final By AVERAGE_SELLING_PRICE_GRAPH_LEGEND = By.xpath("//*[contains(@style,'rgb(51, 51, 51)') and text() = 'Avg. Selling Price']");
    private static final By SHIPPED_COGS_BY_CATEGORY_FIRST_CATEGORY_FILTER = By.cssSelector("[id = 'cogs-category'] [row-index='0'] [col-id = 'filter'] [type = 'button'] span");
    private static final By SHIPPED_COGS_BY_CATEGORY_FIRST_CATEGORY_FILTER_VALUE = By.cssSelector("[id = 'cogs-category'] [row-index='0'] [col-id = 'segmentationValue'] span[id]");
    private static final By VALUE_IN_LEFT_SIDE_CATEGORY_FILTER = By.cssSelector("[id = 'sidebar-filter-category'] div[role='button'] span");
    private static final By SHIPPED_COGS_BY_SEGMENT_FIRST_ROW_FILTER = By.cssSelector("[id = 'cogs-segment'] [row-index='0'] [col-id = 'filter'] [type = 'button'] span");
    private static final By SHIPPED_COGS_BY_SEGMENT_FIRST_ROW_FILTER_VALUE = By.cssSelector("[id = 'cogs-segment'] [row-index='0'] [col-id = 'segmentationValue'] span[id]");
    private static final By VALUE_IN_LEFT_SIDE_SEGMENT_FILTER = By.cssSelector("[id = 'sidebar-filter-segment'] div[role='button'] span");
    private static final By SHIPPED_COGS_BY_PRODUCT_BLOCK = By.id("cogs-product");
    private static final By SHIPPED_COGS_BY_PRODUCT_FIRST_ROW_FILTER = By.cssSelector("[id = 'cogs-product'] [row-index='0'] [col-id = 'filter'] [aria-label = 'Filter By ASIN'] span");
    private static final By SHIPPED_COGS_BY_PRODUCT_FIRST_ROW_FILTER_VALUE = By.cssSelector("[id = 'cogs-product'] [row-index='0'] [col-id = 'asin'] span[id] a");
    private static final By VALUE_IN_LEFT_SIDE_PRODUCT_FILTER = By.cssSelector("[id = 'sidebar-filter-asins'] div[role='button'] span");
    private static final By CALLOUTS_CHECKBOX_CHECKED = By.cssSelector("span[class *= 'Mui-checked'] [class *= 'PrivateSwitchBase-input'][type = 'checkbox']");
    private static final By CALLOUTS_CHECKBOX_UNCHECKED = By.cssSelector("span [class *= 'PrivateSwitchBase-input'][type = 'checkbox'][checked]");
    private static final By PRIORITY_ASINS_ONLY_TOGGLE_CHECKED = By.cssSelector("[id = 'sidebar-filter-priority-asi-ns-only'] span[class *= 'Mui-checked'] [type = 'checkbox']");
    private static final By DURATION_COLUMN_HEADING_SHIPPED_COGS_BY_SUBCATEGORY = By.cssSelector("[id = 'cogs-subcategory'] [col-id = 'current'] span[ref = 'eText']");
    private static final By PREVIOUS_PERIOD_COLUMN_HEADING_SHIPPED_COGS_BY_SUBCATEGORY = By.cssSelector("[id = 'cogs-subcategory'] [col-id = 'previous'] span[ref = 'eText']");
    private static final By LAST_YEAR_COLUMN_HEADING_SHIPPED_COGS_BY_SUBCATEGORY = By.cssSelector("[id = 'cogs-subcategory'] [col-id = 'last'] span[ref = 'eText']");
    private static final By DURATION_COLUMN_HEADING_SHIPPED_COGS_BY_SEGMENT = By.cssSelector("[id = 'cogs-segment'] [col-id = 'current'] span[ref = 'eText']");
    private static final By PREVIOUS_PERIOD_COLUMN_HEADING_SHIPPED_COGS_BY_SEGMENT = By.cssSelector("[id = 'cogs-segment'] [col-id = 'previous'] span[ref = 'eText']");
    private static final By LAST_YEAR_COLUMN_HEADING_SHIPPED_COGS_BY_SEGMENT = By.cssSelector("[id = 'cogs-segment'] [col-id = 'last'] span[ref = 'eText']");
    private static final By DURATION_COLUMN_HEADING_SHIPPED_COGS_BY_PRODUCT = By.cssSelector("[id = 'cogs-product'] [col-id = 'current'] h3");
    private static final By PREVIOUS_PERIOD_COLUMN_HEADING_SHIPPED_COGS_BY_PRODUCT = By.cssSelector("[id = 'cogs-product'] [col-id = 'changeFromPreviousPeriod'] h3");
    private static final By LAST_YEAR_COLUMN_HEADING_SHIPPED_COGS_BY_PRODUCT = By.cssSelector("[id = 'cogs-product'] [col-id = 'changeFromLastYear'] h3");
    private static final By SHIPPED_COGS_BY_PRODUCT_FIRST_ROW_ASIN_MAGNIFYING_ICON = By.cssSelector("[id = 'cogs-product'] [row-index='0'] [col-id = 'filter'] [aria-label = 'View Details'] span");
    private static final By ASIN_DETAIL_BREADCRUMB = By.cssSelector("[class *= 'breadcrumb-title'] li a[id = 'ASIN Detail']");
    private static final By ASIN_VALUE_IN_ASIN_DETAIL_PAGE = By.cssSelector("[class *= 'asin-info-item']:first-of-type span[class]");
    private static final By ROUND_UP_START_YEAR_DD = By.xpath("(//div[contains(@class, 'divider')]//preceding-sibling::div//div[@aria-haspopup='listbox'])[2]");
    private static final By ROUND_UP_END_YEAR_DD = By.xpath("(//div[contains(@class, 'divider')]//following-sibling::div//div[@aria-haspopup='listbox'])[2]");
    private static final By ROUND_UP_START_MONTH_DD = By.xpath("(//div[contains(@class, 'divider')]//preceding-sibling::div//div[@aria-haspopup='listbox'])[1]");
    private static final By ROUND_UP_END_MONTH_DD = By.xpath("(//div[contains(@class, 'divider')]//following-sibling::div//div[@aria-haspopup='listbox'])[1]");
    private static final By APPLY_DATE_RANGE = By.xpath("//div[@id='daterange-selector']//div//div//div//button[@type='button'][text()='Apply']");
    private static final By DATE_SELECTION = By.xpath("//*[@id = 'date-range-selector' or @id = 'ddlDateRange']");
    private static final By INTERVAL_DROPDOWN = By.id("interval-selector");
    private static final By DATE_INTERVAL = By.id("date-range-selector");
    private static final By DATA_STATUS = By.xpath("//button[contains(@class,'MuiButton-contained MuiButton-containedError')][contains(text(),'Data Status')]");
    private static final By PROGRESS_BAR = By.xpath("//div[@class='MuiBox-root css-4bsr9w']/div[2]/span[@role='progressbar']");
    private static final By COGS_UNIT_DROPDOWN_LIST = By.xpath("//ul[@role='listbox']/span");
    private static final By COGS_UNIT_DROPDOWN = By.xpath("//div[@id='sidebar-filter-cogs-units-revenue']//button[@title='Open']//*[name()='svg']//*[name()='path' and contains(@d,'M7 10l5 5 ')]");
    private static final By DATA_HIGH_CHART = By.id("highcharts-gni3494-214");
    private static final By DATA_HIGH_CHART_EXPAND_ICON = By.xpath("//div[@class='MuiBox-root css-4bsr9w']/div[2]//span[@class='material-symbols-rounded'][normalize-space()='fullscreen']");
    private static final By MODEL_BOX = By.id("ModalContent");
    private static final By CLOSE_ICON = By.xpath("//span[text()='close']");
    private static final By HOURLY_HEADER = By.xpath("//span[normalize-space()='Hourly']");
    private static final By EXPORT_TO_ASIN = By.xpath("//button[contains(text(),'Export Asin to CSV')]");
    public static String[] createHeaderCsvSingleBu = {"Client Account Name", "Data Source", "Distributor View", "Metric", "Check Title", "Date", "Interval", "ASIN"};
    public static String[] createHeaderCsvMultiBu = {"Client Account Name", "Business Unit", "Data Source", "Distributor View", "Metric", "Check Title", "Date", "Interval", "ASIN"};
    private final By DATA_HIGH_CHART_EXPORT_ICON = By.xpath("//*[text()='Chart context menu']/following-sibling::*[@class='highcharts-button-symbol']");

    private static final Duration MAX_TIMEOUT = Duration.ofSeconds(30);

    public static Map<Integer, String> months = new HashMap<>() {{
        put(0, "Jan");
        put(1, "Feb");
        put(2, "Mar");
        put(3, "Apr");
        put(4, "May");
        put(5, "June");
        put(6, "July");
        put(7, "August");
        put(8, "Sept");
        put(9, "Oct");
        put(10, "Nov");
        put(11, "Dec");
    }};


    public RoundUpPage(WebDriver driver) {
        super(driver);
    }

    public boolean isRoundUpHeaderDisplayed() {
        UI_LOGGER.info("Verify that round up screen is loaded");
        return isElementVisible(ROUND_UP_HEADER);
    }

    public void clickApplyButton() throws InterruptedException {
        UI_LOGGER.info("Click on Apply button of the filters");
        click(APPLY_BUTTON);
    }

    public String getDistributorViewValue() {
        findElementVisible(DISTRIBUTOR_VIEW, MAX_TIMEOUT);
        return driver.findElement(DISTRIBUTOR_VIEW).getAttribute("value");
    }

    public boolean averageSellingPriceBarChartsValidation() {
        UI_LOGGER.info("Verify that Average Selling Price is available in the graph");
        return isElementVisible(AVERAGE_SELLING_PRICE_GRAPH_LEGEND);
    }

    public void clickAverageSellingPriceGraphLegend() throws InterruptedException {
        UI_LOGGER.info("Click on Average Selling Price graph legend");
        click(AVERAGE_SELLING_PRICE_GRAPH_LEGEND);
    }

    public void clickShippedCogsByCategoryFirstCategoryFilter() throws InterruptedException {
        UI_LOGGER.info("Click on SHIPPED COGS BY CATEGORY first category filter");
        isElementVisible(SHIPPED_COGS_BY_CATEGORY_FIRST_CATEGORY_FILTER, MAX_TIMEOUT);
        click(SHIPPED_COGS_BY_CATEGORY_FIRST_CATEGORY_FILTER);
    }

    public void clickShippedCogsBySegmentFirstCategoryFilter() throws InterruptedException {
        UI_LOGGER.info("Click on SHIPPED COGS BY SEGMENT first category filter");
        isElementVisible(SHIPPED_COGS_BY_SEGMENT_FIRST_ROW_FILTER, MAX_TIMEOUT);
        click(SHIPPED_COGS_BY_SEGMENT_FIRST_ROW_FILTER);
    }

    public void clickShippedCogsByProductFirstCategoryFilter() throws InterruptedException {
        UI_LOGGER.info("Click on SHIPPED COGS BY PRODUCT first category filter");
        isElementVisible(SHIPPED_COGS_BY_CATEGORY_FIRST_CATEGORY_FILTER, MAX_TIMEOUT);
        scrollIntoView(SHIPPED_COGS_BY_PRODUCT_BLOCK);
        isElementVisible(SHIPPED_COGS_BY_PRODUCT_FIRST_ROW_FILTER, MAX_TIMEOUT);
        click(SHIPPED_COGS_BY_PRODUCT_FIRST_ROW_FILTER);
    }

    public String getCategoryFilterValueFromShippedCogsByCategoryFilter() {
        UI_LOGGER.info("Get Category filter value from Shipped Cogs By Category filter");
        return findElementVisible(SHIPPED_COGS_BY_CATEGORY_FIRST_CATEGORY_FILTER_VALUE).getText();
    }

    public String getCategoryFilterValueFromLeftMenuFilter() {
        UI_LOGGER.info("Get Category Value from left side menu filter");
        return findElementVisible(VALUE_IN_LEFT_SIDE_CATEGORY_FILTER).getText();
    }

    public String getSegmentFilterValueFromShippedCogsBySegmentFilter() {
        UI_LOGGER.info("Get Segment filter value from Shipped Cogs By Segment filter");
        return findElementVisible(SHIPPED_COGS_BY_SEGMENT_FIRST_ROW_FILTER_VALUE).getText();
    }

    public String getSegmentFilterValueFromLeftMenuFilter() {
        UI_LOGGER.info("Get Segment Value from left side menu filter");
        return findElementVisible(VALUE_IN_LEFT_SIDE_SEGMENT_FILTER).getText();
    }

    public String getAsinFilterValueFromShippedCogsByProductFilter() {
        UI_LOGGER.info("Get Asin filter value from Shipped Cogs By Product filter");
        return findElementVisible(SHIPPED_COGS_BY_PRODUCT_FIRST_ROW_FILTER_VALUE).getText();
    }

    public String getAsinFilterValueFromLeftMenuFilter() {
        UI_LOGGER.info("Get Asin Value from left side menu filter");
        return findElementVisible(VALUE_IN_LEFT_SIDE_PRODUCT_FILTER).getText();
    }

    public boolean isCalloutsCheckboxToggleOff() throws InterruptedException {
        UI_LOGGER.info("Toggle off callouts checkbox");
        click(CALLOUTS_CHECKBOX_CHECKED);
        return isElementPresent(CALLOUTS_CHECKBOX_CHECKED);
    }

    public boolean isCalloutsCheckboxToggleOn() throws InterruptedException {
        UI_LOGGER.info("Toggle on callouts checkbox");
        click(CALLOUTS_CHECKBOX_UNCHECKED);
        return isElementPresent(CALLOUTS_CHECKBOX_CHECKED);
    }

    public boolean isPriorityAsinsOnlyToggleOffByDefault() {
        UI_LOGGER.info("is Priority Asins Only Toggle Off By Default");
        return isElementPresent(PRIORITY_ASINS_ONLY_TOGGLE_CHECKED);
    }

    public boolean isShippedCogsBySubCategoryContainsCorrectColumns() {
        UI_LOGGER.info("is Shipped Cogs By SubCategory Contains Correct Columns");
        return isElementPresent(DURATION_COLUMN_HEADING_SHIPPED_COGS_BY_SUBCATEGORY) &&
                isElementPresent(PREVIOUS_PERIOD_COLUMN_HEADING_SHIPPED_COGS_BY_SUBCATEGORY) &&
                isElementPresent(LAST_YEAR_COLUMN_HEADING_SHIPPED_COGS_BY_SUBCATEGORY);
    }

    public boolean isShippedCogsBySegmentContainsCorrectColumns() {
        UI_LOGGER.info("is Shipped Cogs By Segment Contains Correct Columns");
        return isElementPresent(DURATION_COLUMN_HEADING_SHIPPED_COGS_BY_SEGMENT) &&
                isElementPresent(PREVIOUS_PERIOD_COLUMN_HEADING_SHIPPED_COGS_BY_SEGMENT) &&
                isElementPresent(LAST_YEAR_COLUMN_HEADING_SHIPPED_COGS_BY_SEGMENT);
    }

    public boolean isShippedCogsByProductContainsCorrectColumns() {
        UI_LOGGER.info("is Shipped Cogs By Product Contains Correct Columns");
        return isElementPresent(DURATION_COLUMN_HEADING_SHIPPED_COGS_BY_PRODUCT) &&
                isElementPresent(PREVIOUS_PERIOD_COLUMN_HEADING_SHIPPED_COGS_BY_PRODUCT) &&
                isElementPresent(LAST_YEAR_COLUMN_HEADING_SHIPPED_COGS_BY_PRODUCT);
    }

    public void verifyDataIsLoadedInShippedCogsByProductBlock() {
        UI_LOGGER.info("Verify that data is loaded in the Shipped Cogs By Product Block");
        isElementVisible(SHIPPED_COGS_BY_CATEGORY_FIRST_CATEGORY_FILTER, MAX_TIMEOUT);
        scrollIntoView(SHIPPED_COGS_BY_PRODUCT_BLOCK);
        isElementVisible(SHIPPED_COGS_BY_PRODUCT_FIRST_ROW_FILTER, MAX_TIMEOUT);
    }


    public void clickShippedCogsBySegmentFirstAsinMagnifyingFilter() throws InterruptedException {
        UI_LOGGER.info("Click on SHIPPED COGS BY Product first Asin Magnifying View Detail filter");
        isElementVisible(SHIPPED_COGS_BY_PRODUCT_FIRST_ROW_ASIN_MAGNIFYING_ICON, MAX_TIMEOUT);
        click(SHIPPED_COGS_BY_PRODUCT_FIRST_ROW_ASIN_MAGNIFYING_ICON);
    }

    public boolean verifyAsinDetailTabOpens() {
        UI_LOGGER.info("Switching to Asin Detail Tab");
        switchToTab(2, 1);
        return isElementPresent(ASIN_DETAIL_BREADCRUMB);
    }

    public String getAsinValueFromAsinDetailPage() {
        UI_LOGGER.info("Getting Asin Value from the Asin Detail Page");
        return findElementVisible(ASIN_VALUE_IN_ASIN_DETAIL_PAGE).getText();
    }

    public boolean realTimeSalesHourlyGraphShowsInColor(String rgba) {
        return isElementPresent(By.xpath("//*[name()='g']/*[name()='path' and @fill='" + rgba + "']"));
    }

    public void selectPeriod(String period) throws InterruptedException {
        click(DATE_SELECTION);
        click(By.xpath("//ul//span[text()='" + period + "']"));
    }

    public void selectRoundUpDateRange(int startDate, int endDate) throws InterruptedException {
        int startYear = DateUtility.extractYear(startDate);
        int startMonth = DateUtility.extractMonth(startDate) - 1;
        int startDay = DateUtility.extractDay(startDate);
        int endYear = DateUtility.extractYear(endDate);
        int endMonth = DateUtility.extractMonth(endDate) - 1;
        int endDay = DateUtility.extractDay(endDate);

        String startMonthUi = getTextFromElement(ROUND_UP_START_MONTH_DD);
        String endMonthUi = getTextFromElement(ROUND_UP_END_MONTH_DD);

        if (months.get(startMonth).equals(startMonthUi) && months.get(endMonth).equals(startMonthUi)) {
            selectYearAndMonth(startYear, startMonth);
            click(generateXpathForDay(true, startDay));
            click(generateXpathForDay(true, endDay));
        }
        if (months.get(startMonth).equals(endMonthUi) && months.get(endMonth).equals(endMonthUi)) {
            selectYearAndMonth(startYear, startMonth);
            click(generateXpathForDay(false, startDay));
            click(generateXpathForDay(false, endDay));
        }
        if (months.get(startMonth).equals(startMonthUi) && months.get(endMonth).equals(endMonthUi)) {
            selectYearAndMonth(startYear, startMonth);
            click(generateXpathForDay(true, startDay));
            selectYearAndMonth(endYear, endMonth);
            click(generateXpathForDay(false, endDay));
        }
        click(APPLY_DATE_RANGE);
    }

    private By generateXpathForDay(boolean precedingMonth, int day) {
        String path = precedingMonth ? "preceding" : "following";
        return By.xpath("//div[contains(@class, 'divider')]//" + path + "-sibling::div//button[not(@disabled)]//p[text()='" + day + "']");
    }

    private void selectYearAndMonth(int year, int month) throws InterruptedException {
        click(ROUND_UP_START_YEAR_DD);
        click(By.xpath("//ul[@role='listbox']/li[text()='" + year + "']"));
        click(ROUND_UP_START_MONTH_DD);
        click(By.xpath("//ul[@role='listbox']/li[@data-value=" + month + "]"));
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

    public void selectInterval(String interval) throws InterruptedException {
        UI_LOGGER.info("Click on Interval Dropdown");
        click(INTERVAL_DROPDOWN);
        isElementVisible(By.xpath("//li[normalize-space()='" + interval + "']"));
        click(By.xpath("//li[normalize-space()='" + interval + "']"));
        UI_LOGGER.info("Click on Apply Button");
        click(APPLY_BUTTON);
        UI_LOGGER.info("Wait for the selected interval Data is displayed");
        isElementNotVisible(PROGRESS_BAR);
        UI_LOGGER.info("Verify Data Highcharts is Displayed");
        isElementVisible(DATA_HIGH_CHART);
    }

    public boolean verifyHourlyIntervalDisplayed(String interval) {
        return isElementVisible(By.xpath("//div[@id='interval-selector'][normalize-space()='" + interval + "']"));
    }

    public void clickDateRangeSelector() throws InterruptedException {
        UI_LOGGER.info("Click on date interval");
        click(DATE_INTERVAL);
    }

    public boolean verifyLastFiveDayDateIsDisplayed(String date) {
        return isElementVisible(By.xpath("//ul[contains(@class,'materialui-daterange-picker')]//span[normalize-space()='" + date + "']"));
    }

    public boolean verifyExpandIconfunctionality(String expandIcon) throws InterruptedException {
        UI_LOGGER.info("Verify Expand icon displayed");
        isElementVisible(By.xpath("//div[@id='" + expandIcon + "']//span[@class='material-symbols-rounded'][normalize-space()='fullscreen']"));
        click(By.xpath("//div[@id='" + expandIcon + "']//span[@class='material-symbols-rounded'][normalize-space()='fullscreen']"));
        boolean f = isElementVisible(MODEL_BOX);
        click(CLOSE_ICON);
        return f;
    }

    public boolean verifyDataHighChartExpandIconfunctionality() throws InterruptedException {
        UI_LOGGER.info("Verify Expand icon displayed");
        isElementVisible(DATA_HIGH_CHART_EXPAND_ICON);
        click(DATA_HIGH_CHART_EXPAND_ICON);
        boolean f = isElementVisible(MODEL_BOX);
        click(CLOSE_ICON);
        return f;
    }

    public void clickExportButton(String exportButtonName) throws InterruptedException {
        By exportButton = By.xpath("//ul[@class='highcharts-menu']/li[text()='" + exportButtonName + "']");
        click(exportButton);
    }

    public void clickOnDataHighChartExportIcon() throws InterruptedException {
        click(DATA_HIGH_CHART_EXPORT_ICON);
    }

    public boolean isDataHighChartExportIconDisplayed() {
        try {
            WebElement exportIcon = findElementVisible(DATA_HIGH_CHART_EXPORT_ICON);
            if (!exportIcon.isDisplayed()) {
                scrollIntoView(exportIcon);
                exportIcon = findElementVisible(DATA_HIGH_CHART_EXPORT_ICON);
            }
            return exportIcon.isDisplayed();
        } catch (NoSuchElementException | TimeoutException | StaleElementReferenceException e) {
            return false;
        }
    }

    @DataProvider
    public static Object[][] downloadOptions() {
        return new Object[][]{
                {"Download PNG image", ".png"},
                {"Download PDF document", ".pdf"},
                {"Download CSV", ".csv"},
                {"Download XLS", ".xls"},
        };
    }

    public static String isFileDownloaded(String extension, String fileName, int maxWaitTime, String downloadFolder) throws Exception {

        File file = new File(downloadFolder);
        String filePath = "";
        if (file.isDirectory()) {
            // use await() to make sure the directory has a file that ends with the extension and the name contains the fileName
            FilenameFilter fileFilter = (dir, name) -> (name.endsWith(extension) && name.toLowerCase().contains(fileName.toLowerCase()));
            try {
                await().atMost(maxWaitTime, TimeUnit.SECONDS).until(() -> {
                    File[] files = file.listFiles(fileFilter);
                    return files != null && files.length > 0;
                });
                return Objects.requireNonNull(file.listFiles(fileFilter))[0].getPath();
            } catch (ConditionTimeoutException e) {
                throw new Exception("File not found in " + maxWaitTime + " seconds");
            }
        }
        return filePath;
    }

    public boolean dataStausButtonForSingleAndMultiBu() {
        UI_LOGGER.info("Verify Data status is Enabled");
        return isElementEnabled(DATA_STATUS);
    }

    public boolean dataStatusHourlyIntervalIsDisplayed() throws InterruptedException {
        UI_LOGGER.info("Click Data Status");
        click(DATA_STATUS);
        UI_LOGGER.info("Verify that Data Status Grid hourly interval Displayed ");
        return isElementVisible(HOURLY_HEADER);
    }

    public boolean dataStutusExportButtonDisplayed() {
        UI_LOGGER.info("Verify that Export to Asin Button is displayed");
        return isElementVisible(EXPORT_TO_ASIN);
    }

    public void clickExportToAsinButton() throws InterruptedException {
        UI_LOGGER.info("Click on Export to Asin button");
        click(EXPORT_TO_ASIN);
    }

    public void selectMultiBU(String... bus) throws InterruptedException {
        UI_LOGGER.info("Click Bu Filter");
        click(BU_FILTER);

        for (String bu : bus) {
            setText(SEARCH_BAR_BU_SELECTOR, bu);
            List<WebElement> buOptions = findElementsVisible(BU_OPTIONS);
            for (WebElement buOption : buOptions) {
                if (buOption.getText().contains(bu)) {
                    click(SELECT_BU_RADIO_BUTTON);
                    UI_LOGGER.info("Selected BU: " + bu);
                    break;
                }
            }
        }
        click(BU_SELECTOR_SAVE_BUTTON);
    }

    public void verifyAllIntervalsCOGSValues(String button, String interval) throws InterruptedException {
        List<String> optionsForHourly = Arrays.asList("Ordered Units", "Ordered Revenue");
        List<String> optionsForOthers = Arrays.asList("Ordered Units", "Ordered Revenue", "Shipped COGS", "Shipped Units", "Shipped Revenue");
        UI_LOGGER.info("Click on clear Button");
        click(By.xpath("//div[@id='sidebar-filter-cogs-units-revenue']//a[normalize-space()='" + button + "']"));
        click(COGS_UNIT_DROPDOWN);
        List<WebElement> cogsDropdwnValues = findElementsVisible(COGS_UNIT_DROPDOWN_LIST);
        List<String> optionsOfCogs = new ArrayList<>();
        for (WebElement cogsDropdwnValue : cogsDropdwnValues) {
            String text = cogsDropdwnValue.getText();
            optionsOfCogs.add(text);
        }
        if (interval.equalsIgnoreCase("Hourly")) {
            Assert.assertEqualsNoOrder(optionsForHourly.toArray(), optionsOfCogs.toArray());
        } else {
            Assert.assertEqualsNoOrder(optionsForOthers.toArray(), optionsOfCogs.toArray());
        }
    }
}