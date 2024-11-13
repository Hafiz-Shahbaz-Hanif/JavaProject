package com.DC.pageobjects.adc.analyze.goalSetting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.CommonFeatures;
import com.DC.utilities.CsvUtility;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public class GoalsHubPage extends NetNewNavigationMenu {

    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public CommonFeatures commonFeatures;
    public DCFilters dcFilters;

    public static final By DELETE_ICON = By.xpath("//span[text()='delete']/..");
    public static final By EDIT_ICON = By.xpath("//span[text()='edit']/..");
    public static final By DELETE_GOAL = By.xpath("//button[text()='Delete Goal']");
    public static final By CANCEL_DELETE = By.xpath("//button[text()='Delete Goal']/..//button[text()='Cancel']");
    public static final By PERIOD_DROPDOWN = By.xpath("//div[@id='goals-timeframe-selector']/..");
    public static final By INTERVAL_DROPDOWN = By.xpath("//div[@id='goals-timeframe-selector']/..");
    public static final By CREATE_GOAL = By.xpath("//button[text()='Create Goal']");
    public static final By CREATE_METRIC = By.xpath("//input[@id='Metric_field']");
    public static final By METRIC_OPTIONS = By.xpath("//ul[@id='Metric_field-listbox']/li");
    public static final By CREATE_INTERVAL = By.xpath("//input[@id='Interval_field']");
    public static final By INTERVAL_OPTIONS = By.xpath("//ul[@id='Interval_field-listbox']/li");
    public static final By CREATE_TIME_PERIOD = By.xpath("//p[text()='Time Period']/../..//input/../div");
    public static final By CREATE_TIME_PERIOD_INPUT = By.xpath("//p[text()='Time Period']/../..//input");
    public static final By TIME_PERIOD_DATES = By.xpath("//button[contains(@class, 'materialui-daterange-picker-makeStyles-button') and not(contains(@class, 'disabled'))]");
    public static final By CREATE_SEGMENTATION_TYPE = By.xpath("//input[@id='Segmentation Type_field']");
    public static final By SEGMENTATION_TYPE_OPTIONS = By.xpath("//ul[@id='Segmentation Type_field-listbox']/li");
    public static final By CREATE_SEGMENTATION_VALUE = By.xpath("//input[@id='Segmentation value_field']");
    public static final By SEGMENTATION_VALUE_OPTIONS = By.xpath("//ul[@id='Segmentation value_field-listbox']/li");
    public static final By GOAL_MIN = By.xpath("(//input[@placeholder='Do not include decimals'])[1]");
    public static final By GOAL_MAX = By.xpath("(//input[@placeholder='Do not include decimals'])[2]");
    private static final By CHANGE_LOGS = By.xpath("//button[normalize-space()='Change Logs']");
    private static final By VERSION_HISTORY_TAB = By.xpath("//button[normalize-space()='Version History']");
    private static final By CHOOSE_FILE = By.xpath("//label[@id='upload-bulk-edit-delete-goals']/input");
    private static final By UPLOAD_BUTTON = By.xpath("//button[contains(text(),'Upload')]");

    private static final By DISTRIBUTOR_VIEW_DROPDOWN = By.xpath("//div[@id='sidebar-filter-distributor-view']//button[@title='Open']//*[name()='svg']//*[name()='path' and contains(@d,'M7 10l5 5 ')]");
    public static final By CREATE_DATE_APPLY_BTN = By.xpath("//div[contains(@class, 'materialui-daterange-picker')]/..//button[text()='Apply']");
    public static final By CREATE_TITLE = By.xpath("//input[@id='title']");
    public static final By CREATE_SAVE_BTN = By.xpath("//button[text()='Save']");
    public static final By CREATE_CANCEL_BTN = By.xpath("//h4/../../..//button[text()='Cancel']");
    public static final By FILTER_METRIC_DROPDOWN = By.xpath("//div[@id='sidebar-filter-metrics']//input");
    public static final By FILTER_APPLY_BTN = By.xpath("//button[text()='Apply']");
    public static final String GOALS_REACHED_PERCENTAGE = "//*[name()='text' and @x='15' and @y='140']";
    public static final String GOALS_REMAINING_PERCENTAGE = "//*[name()='text' and @y='140' and contains(text(), 'Goal')]";
    private static final By INTERVAL_SELECTION = By.id("interval-selector");
    private static final By GOALS_SELECTION = By.id("goals-timeframe-selector");
    private static final By GOAL_EXPORT_ICON = By.id("download-png-pdf-menu");
    private static final By GOAL_IMPORT_ICON = By.id("bulk-edit-delete-btn");
    private static final By GOALS_HUB_CHART = By.id("GoalsHubCharts");
    public static final By SELECT_METRICS = By.xpath("//input[@id='Metric_field']");
    public static final By SELECT_SHIPPED_COGS = By.xpath("//li[contains(text(),'SHIPPED COGS')]");
    public static final By SELECT_INTERVAL = By.xpath("//input[@id='Interval_field']");
    public static final By SELECT_MONTHLY = By.xpath("//li[contains(text(),'MONTHLY')]");
    public static final By SELECT_DISTRIBUTOR_VIEW = By.xpath("//input[@id='Distributor View_field']");
    public static final By SELECT_MANUFACTURING = By.xpath("//li[contains(text(),'MANUFACTURING')]");
    public static final By SELECT_SEGMENTATION_TYPE = By.xpath("//input[@id='Segmentation Type_field']");
    public static final By SELECT_BRAND = By.xpath("//li[contains(text(),'BRAND')]");
    public static final By SELECT_SEGMENTATION_VALUE = By.xpath("//input[@id='Segmentation value_field']");
    public static final By SELECT_ADOLPHS = By.xpath("//li[contains(text(),'ADOLPHS')]");
    public static final By DOUBLE_DASH_LAST_MODIFIED_DATE = By.xpath("//span[contains(text(),'--')]");
    public static final By CARROT_ICON = By.xpath("//span[@class='ag-icon ag-icon-tree-closed']");
    private static final By CREATE_GOALS_SELECTION = By.xpath("//button[normalize-space()='Create Goal']");
    private static final By METRICS_FILTER = By.xpath("//div[@id='sidebar-filter-metrics']//input");
    private static final By BRAND_FILTER = By.xpath("//div[@id='sidebar-filter-brand']//input");
    private static final By CATEGORY_FILTER = By.xpath("//div[@id='sidebar-filter-category']//input");
    public static final By APPLY_BUTTON = By.xpath("//button[@type='button' and text()='Apply']");
    private static final By CREATE_DISTRIBUTOR_VIEW = By.xpath("//input[@id='Distributor View_field']");
    public static final By DISTRIBUTOR_VIEW_OPTIONS = By.xpath("//ul[@id='Distributor View_field-listbox']/li");
    public static final By CREATE_GOAL_POP_UP_TITLE = By.xpath("//h4[text()='Create Goal']");
    public static final By INTERVAL_SELECTOR = By.xpath("//div[@id='interval-selector']/..");
    public static final By METRIC_SIGN = By.xpath("//input[@placeholder='Do not include decimals']/..//p");
    public static final String SALES_GOALS_REACHED_PERCENTAGE = "//span[not(@data-z-index) and contains(@style, 'weight')]";
    public static final String SALES_GOALS_REACHED_AMOUNT = "//span[not(contains(@style, 'weight')) and not(@class)]";
    public static final By SIDEBAR_FILTER_METRIC_FIELD = By.xpath("//div[@id='sidebar-filter-metrics']//input");
    public static final By SIDEBAR_FILTER_METRIC_OPTION = By.xpath("//div[@id='sidebar-filter-metrics']//ul/span");
    public static final By SIDEBAR_FILTER_BRAND_FIELD = By.xpath("//div[@id='sidebar-filter-brand']//input");
    public static final By SIDEBAR_FILTER_BRAND_OPTION = By.xpath("//div[@id='sidebar-filter-brand']//ul/span");
    private static final By TOOLTIP = By.xpath("//div[@role='tooltip']/div");
    public static final By SAMPLE_BTN = By.id("bulk-create-goal-sample");
    public static final By DELETE_CREATED_GOAL = By.xpath("//p[contains(text(),'Goal $102')]/../div//span[contains(text(),'delete')]");
    public static final By FILE_NAME_TEXT = By.xpath("//div[@row-id=0]/div[@col-id='fileName']//span[contains(@id,'cell-fileName')]");
    public static final By All_AVAILABLITY_GOAL = By.xpath("//div[@id='GoalsHubCharts']/div//p[contains(text(),'Availability')]");
    public static final By BULK_CREATED_GOAL_EXPORT = By.id("upload-bulk-create-goals");
    public static final By CLOSE_BTN = By.xpath("//span[contains(text(),'close')]");
    public static final By GOAL_TITLE = By.xpath("//input[@id='title']");
    private static final By SALES_GOAL_REACHED_AMOUNT = By.xpath("//div[contains(@class, 'highcharts-label highcharts')]//div//span[not(contains(text(), '%'))]");
    private static final By SALES_GOAL_PROGRESS_BAR = By.xpath("//*[name()='g' and @class='highcharts-series-group']");
    private static final By SALES_GOAL_REMAINING_AMOUNT = By.xpath("//p[contains(text(), 'Remaining')]");

    public static List<String> goalMetrics = List.of("AVAILABILITY", "ORDERED REVENUE", "ORDERED UNITS", "SHIPPED COGS", "SHIPPED REVENUE", "SHIPPED UNITS");
    public static List<String> goalIntervals = List.of("CUSTOM", "MONTHLY", "QUARTERLY", "YEARLY");
    public static List<String> goalSegmentationTypes = List.of("BRAND", "CATEGORY", "SEGMENT", "SUBCATEGORY");

    public static String[] createHeaderCsv = {"Metric", "Interval", "Goal Title (for custom interval only)", "Start Date", "End Date", "Segmentation Type", "Segmentation Value", "Goal Value (for non-range goals)", "Goal Min Value (for range goals)", "Goal Max Value (for range goals)", "Distributor View (not required for Availability)", "Campaign Type (required for media metrics)"};
    public static String[] editHeaderCvs = {"Goal Id", "Metric", "Interval", "Goal Title (for custom interval only)", "Start Date", "End Date", "Segmentation Type", "Segmentation Value", "Goal Value (for non-range goals)", "Goal Min Value (for range goals)", "Goal Max Value (for range goals)", "Distributor View (not required for Availability)", "Delete Goal"};

    public static final String brand = "lawrys"; //"billy bee, stubbs, zatarains"

    public GoalsHubPage(WebDriver driver) {
        super(driver);
    }

    public boolean verifyGoalCreated(int min, int max) {
        return isElementVisible(By.xpath("//p[contains(text(), '" + min + " - " + max + "')]/..//span[text()='edit']"));
    }

    public boolean isDeleteButtonDisplayed() {
        return isElementVisible(DELETE_GOAL);
    }

    public boolean metricSignDisplayed(String sign) {
        if (sign.isEmpty()) {
            return isElementVisible(METRIC_SIGN, java.time.Duration.ofSeconds(1));
        } else {
            return isElementVisible(By.xpath("//input[@placeholder='Do not include decimals']/..//p[text()='" + sign + "']"));
        }
    }

    public void setGoalTitle(String title) {
        setText(GOAL_TITLE, title);
    }

    public void clearMetric() throws InterruptedException {
        clearInput(CREATE_METRIC);
    }

    public String setRandomSalesMetric() throws InterruptedException {
        String metric = getRandomSalesMetric();
        setMetric(metric);
        return metric;
    }

    public String getRandomSalesMetric() throws InterruptedException {
        List<String> metrics = getMetrics();
        metrics.remove("AVAILABILITY");
        return (String) SharedMethods.getRandomItemFromList(metrics);
    }

    public boolean isCancelDeleteButtonDisplayed() {
        return isElementVisible(CANCEL_DELETE);
    }

    public void clickDeleteGoalIcon() throws InterruptedException {
        click(DELETE_ICON);
    }

    public void clickEditGoalIcon() throws InterruptedException {
        click(EDIT_ICON);
    }

    public void clickCancelDelete() throws InterruptedException {
        click(CANCEL_DELETE);
    }

    public void clickCreateGoal() throws InterruptedException {
        click(CREATE_GOAL);
    }

    public List<String> getMetrics() throws InterruptedException {
        click(CREATE_METRIC);
        List<String> metrics = getTextFromElements(findElementsVisible(METRIC_OPTIONS));
        Collections.sort(metrics);
        click(CREATE_GOAL_POP_UP_TITLE);
        return metrics;
    }

    public String getMetricSelected() {
        return getTextFromElement(CREATE_METRIC);
    }

    public List<String> getIntervals() throws InterruptedException {
        click(CREATE_INTERVAL);
        List<String> intervals = getTextFromElements(findElementsVisible(INTERVAL_OPTIONS));
        Collections.sort(intervals);
        click(CREATE_GOAL_POP_UP_TITLE);
        return intervals;
    }

    public void selectPeriod(String period) throws InterruptedException {
        click(PERIOD_DROPDOWN);
        click(By.xpath("//ul/li[text()='" + period + "']"));
    }

    public void selectInterval(String interval) throws InterruptedException {
        click(INTERVAL_SELECTOR);
        click(By.xpath("//ul/li[text()='" + interval + "']"));
    }

    public List<String> getSegmentationTypes() throws InterruptedException {
        click(CREATE_SEGMENTATION_TYPE);
        List<String> segmentationTypes = getTextFromElements(findElementsVisible(SEGMENTATION_TYPE_OPTIONS));
        Collections.sort(segmentationTypes);
        click(CREATE_GOAL_POP_UP_TITLE);
        return segmentationTypes;
    }

    public List<String> getSegmentationValues() throws InterruptedException {
        click(CREATE_SEGMENTATION_VALUE);
        return getTextFromElements(findElementsVisible(SEGMENTATION_VALUE_OPTIONS));
    }

    public void selectCustomDate() throws InterruptedException {
        click(CREATE_TIME_PERIOD);
        List<WebElement> dates = findElementsVisible(TIME_PERIOD_DATES);
        dates.get(1).click();
        dates.get(10).click();
        click(CREATE_DATE_APPLY_BTN);
    }

    public void setMinGoalValue(String min) {
        setText(GOAL_MIN, min);
    }

    public void setSalesGoalValue(String min) {
        setMinGoalValue(min);
    }

    public void setMaxGoalValue(String max) {
        setText(GOAL_MAX, max);
    }

    public void setMetric(String metric) throws InterruptedException {
        setText(CREATE_METRIC, metric);
        click(METRIC_OPTIONS);
    }

    public void setFilterMetric(String metric) throws InterruptedException {
        setText(SIDEBAR_FILTER_METRIC_FIELD, metric);
        click(SIDEBAR_FILTER_METRIC_OPTION);
    }

    public void setFilterBrand(String brand) throws InterruptedException {
        setText(SIDEBAR_FILTER_BRAND_FIELD, brand);
        click(SIDEBAR_FILTER_BRAND_OPTION);
    }

    public String setRandomMetric() throws InterruptedException {
        List<String> metrics = getMetrics();
        String metric = (String) SharedMethods.getRandomItemFromList(metrics);
        setMetric(metric);
        return metric;
    }

    public void setInterval(String interval) throws InterruptedException {
        setText(CREATE_INTERVAL, interval);
        click(INTERVAL_OPTIONS);
    }

    public String setRandomInterval() throws InterruptedException {
        List<String> intervals = getIntervals();
        String interval = (String) SharedMethods.getRandomItemFromList(intervals);
        setInterval(interval);
        return interval;
    }

    public void setSegmentationType(String segmentationType) throws InterruptedException {
        setText(CREATE_SEGMENTATION_TYPE, segmentationType);
        click(SEGMENTATION_TYPE_OPTIONS);
    }

    public String setRandomSegmentationType() throws InterruptedException {
        List<String> segTypes = getSegmentationTypes();
        String segType = (String) SharedMethods.getRandomItemFromList(segTypes);
        setSegmentationType(segType);
        return segType;
    }

    public List<String> getDistributorViews() throws InterruptedException {
        click(CREATE_DISTRIBUTOR_VIEW);
        List<String> distributorViews = getTextFromElements(findElementsVisible(DISTRIBUTOR_VIEW_OPTIONS));
        Collections.sort(distributorViews);
        click(CREATE_GOAL_POP_UP_TITLE);
        return distributorViews;
    }

    public String setRandomDistributorView() throws InterruptedException {
        List<String> views = getDistributorViews();
        String view = (String) SharedMethods.getRandomItemFromList(views);
        setDistributorView(view);
        return view;
    }

    public void setDistributorView(String segmentationType) throws InterruptedException {
        setText(CREATE_DISTRIBUTOR_VIEW, segmentationType);
        click(DISTRIBUTOR_VIEW_OPTIONS);
    }

    public String setRandomSegmentationValue() throws InterruptedException {
        List<String> segValues = getSegmentationValues();
        String segValue = (String) SharedMethods.getRandomItemFromList(segValues);
        setSegmentationValue(segValue);
        return segValue;
    }

    public void setSegmentationValue(String segmentationValue) throws InterruptedException {
        setText(CREATE_SEGMENTATION_VALUE, segmentationValue);
        click(SEGMENTATION_VALUE_OPTIONS);
    }

    public boolean isTitleFieldDisplayed() {
        return isElementVisible(CREATE_TITLE);
    }

    public boolean isSaveButtonEnabled() {
        return isElementEnabled(CREATE_SAVE_BTN);
    }

    public boolean isIntervalDropdownEnabled() {
        return isElementEnabled(CREATE_INTERVAL);
    }

    public boolean isMetricDropdownEnabled() {
        return isElementEnabled(CREATE_METRIC);
    }

    public boolean isSegmentationTypeDropdownEnabled() {
        return isElementEnabled(CREATE_SEGMENTATION_TYPE);
    }

    public boolean isDistributorViewEnabled() {
        return isElementEnabled(CREATE_DISTRIBUTOR_VIEW);
    }

    public boolean isSegmentationValueDropdownEnabled() {
        return isElementEnabled(CREATE_SEGMENTATION_VALUE);
    }

    public boolean isTimePeriodFieldEnabled() {
        return isElementEnabled(CREATE_TIME_PERIOD_INPUT);
    }

    public boolean isGoalMinFieldFieldEnabled() {
        return isElementEnabled(GOAL_MIN);
    }

    public boolean isGoalMaxFieldFieldEnabled() {
        return isElementEnabled(GOAL_MAX);
    }

    public void clickGoalCreateCancelBtn() throws InterruptedException {
        click(CREATE_CANCEL_BTN);
    }

    public String getGoal(String range) {
        return "//p[contains(text(), '" + range + "')]/../../..";
    }

    public String getGoalReachedPercentage(String range) {
        return getTextFromPresentElement(By.xpath(getGoal(range) + GOALS_REACHED_PERCENTAGE));
    }

    public String getGoalRemainingPercentage(String range) {
        return getTextFromElement(By.xpath(getGoal(range) + GOALS_REMAINING_PERCENTAGE));
    }

    public String getSalesGoalReachedPercentage(String percentageValue) {
        return getTextFromElement(By.xpath("//span[contains(text(), '" + percentageValue + "')]"));
    }

    public String getSalesGoalSet(String goal) {
        return getTextFromElement(By.xpath("//p[contains(text(), '" + goal + "')]"));
    }

    public String getSalesGoalReachedAmount() {
        return getTextFromElement(SALES_GOAL_REACHED_AMOUNT);
    }

    public int getSalesGoalProgress() throws InterruptedException {
        var dimension = findElementVisible(SALES_GOAL_PROGRESS_BAR);
        Thread.sleep(1500);
        return dimension.getSize().getWidth();
    }

    public String getSalesGoalRemainingAmount() {
        return getTextFromElement(SALES_GOAL_REMAINING_AMOUNT);
    }

    public String getYoyValue(String goal) {
        String yoy = getYoy(goal).getText();
        return yoy.split(" ")[0];
    }

    public WebElement getYoy(String goal) {
        return findElementVisible(By.xpath("//p[contains(text(), '" + goal + "')]/../..//p[contains(text(), 'YoY')]"));
    }

    public String calculateYoyPop(BigDecimal currentValue, BigDecimal lastYearValue) {
        String percentage = null;
        if (lastYearValue != null && lastYearValue.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal difference = currentValue.subtract(lastYearValue);
            percentage = difference.divide(lastYearValue, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString();
        } else {
            percentage = "0";
        }
        return percentage + "%";
    }

    public String getPopValue(String goal) {
        String pop = getPop(goal).getText();
        return pop.split(" ")[0];
    }

    public WebElement getPop(String goal) {
        return findElementVisible(By.xpath("//p[contains(text(), '" + goal + "')]/../..//p[contains(text(), 'PoP')]"));
    }

    public String getGoalMetricColor(String metric, String range, String colorCode) {
        return getGoal(range) + "//*[name()='g' and contains(@class, 'highcharts-plot')]/*[name()='path' and @fill='" + colorCode + "']";
    }

    public void selectMetric(String metric) throws InterruptedException {
        setText(FILTER_METRIC_DROPDOWN, metric);
        click(By.xpath("//ul/span[text()='" + metric + "'] | //ul/p[text()='" + metric + "']"));
    }

    public void displayGoalsBy(String metric, String period, String interval) throws InterruptedException {
        selectMetric(metric);
        selectPeriod(period);
        selectInterval(interval);
    }

    public void sideBarApply() throws InterruptedException {
        click(FILTER_APPLY_BTN);
    }

    public By createPageLocator(String PageName) {
        isElementVisible(By.xpath("//div[@class='MuiGrid-root MuiGrid-item MuiGrid-grid-xs-3 css-e6lmdt']//p[text()='" + PageName + "']"));
        return By.xpath("//div[@class='MuiGrid-root MuiGrid-item MuiGrid-grid-xs-3 css-e6lmdt']//p[text()='" + PageName + "']");
    }

    public void clickOnPage(String PageName) throws InterruptedException {
        click(createPageLocator(PageName));
    }

    public By createSectionLocator(String sectionName) {
        return By.xpath("//div[@class='MuiGrid-root MuiGrid-container MuiGrid-spacing-xs-2 css-1y9fiox']//p[text()='" + sectionName + "']");
    }

    public void clickOnSection(String sectionName) throws InterruptedException {
        click(createSectionLocator(sectionName));
    }

    public boolean verifyPresenceOfAllLeftSideFiltersGoalsHub(String filter, String filter1, String filter2, String filter3, String filter4) {
        return isElementVisible(By.id("sidebar-filter-" + filter)) && isElementVisible(By.id("sidebar-filter-" + filter1)) && isElementVisible(By.id("sidebar-filter-" + filter2)) && isElementVisible(By.id("sidebar-filter-" + filter3)) && isElementVisible(By.id("sidebar-filter-" + filter4));
    }

    public boolean isAvailabilityValueIsDisplayedInMetricsDropdown(String availability) throws InterruptedException {
        click(METRICS_FILTER);
        return isElementVisible(By.xpath("//ul/span[text()='" + availability + "'] | //ul/p[text()='" + availability + "']"));
    }

    public boolean isIntervalButtonIsDisplayed() throws InterruptedException {
        click(INTERVAL_SELECTION);
        return isElementVisible(INTERVAL_SELECTION);
    }

    public boolean isDistributorViewDisplayed() {
        return isElementVisible(CREATE_DISTRIBUTOR_VIEW);
    }

    public boolean isYearlyIntervalIsDisplayedAndClickable(String Yearly) {
        return isElementClickable(By.xpath("//ul[@role='listbox']/li[contains(text(),'" + Yearly + "')]"));
    }

    public boolean isCustomIntervalIsDisplayedAndClickable(String Custom) throws InterruptedException {
        boolean f = isElementClickable(By.xpath("//ul[@role='listbox']/li[contains(text(),'" + Custom + "')]"));
        click(By.xpath("//ul[@role='listbox']/li[contains(text(),'" + Custom + "')]"));
        return f;
    }

    public boolean isMonthlyIntervalIsDisplayedAndClickable(String Monthly) {
        return isElementClickable(By.xpath("//ul[@role='listbox']/li[contains(text(),'" + Monthly + "')]"));
    }

    public boolean isQuarterlyIntervalIsDisplayedAndClickable(String Quarterly) {
        return isElementClickable(By.xpath("//ul[@role='listbox']/li[contains(text(),'" + Quarterly + "')]"));
    }

    public boolean isGoalsButtonIsDisplayed() throws InterruptedException {
        click(GOALS_SELECTION);
        return isElementVisible(GOALS_SELECTION);
    }

    public boolean isPresentGoalIsDisplayedAndClickable(String Present_Goal) {
        return isElementClickable(By.xpath("//ul[@role='listbox']/li[normalize-space()='" + Present_Goal + "']"));
    }

    public boolean isPastGoalIsDisplayedAndClickable(String Past_Goal) {
        return isElementClickable(By.xpath("//ul[@role='listbox']/li[normalize-space()='" + Past_Goal + "']"));
    }

    public boolean isFuturGoalIsDisplayedAndClickable(String Future_Goal) {
        return isElementClickable(By.xpath("//ul[@role='listbox']/li[normalize-space()='" + Future_Goal + "']"));
    }

    public boolean isAllGoalIsDisplayedAndClickable(String All_Goal) throws InterruptedException {
        boolean f = isElementClickable(By.xpath("//ul[@role='listbox']/li[normalize-space()='" + All_Goal + "']"));
        click(By.xpath("//ul[@role='listbox']/li[normalize-space()='" + All_Goal + "']"));
        return f;
    }

    public void isCategoryOptionsAreDisplayed() throws InterruptedException {
        click(CATEGORY_FILTER);
        String[] expectedOptions = {"CONDIMENTS & SAUCES", "STOCK, BROTH & BOUILLON", "COATINGS & BREADCRUMBS", "RECIPE MIXES AND GRAVY", "HONEY",
                "ETHNIC CATEGORIES", "SALAD TOPPINGS", "RICE & HEALTHY GRAINS", "SPICES, EXTRACTS & SALT"};
        List<WebElement> values = driver.findElements(By.xpath("//ul[@id='mui-11-listbox']/span"));
        for (int i = 0; i < values.size(); i++) {

            if (values.get(i).getText().contains(expectedOptions[i])) {
                UI_LOGGER.info("Category Options are matched");
            } else {
                UI_LOGGER.error("Category options are not matched");
            }
        }
    }

    public boolean isCreateGoalButtonIsDisplayed() {
        return isElementVisible(CREATE_GOALS_SELECTION);
    }

    public boolean isDownloadsButtonIsDisplayed() {
        return isElementVisible(GOAL_EXPORT_ICON);
    }

    public boolean isGoalHubChartIsDisplayed() {
        return isElementVisible(GOALS_HUB_CHART);
    }

    public boolean isPngOptionIsDisplayed(String PNG) throws InterruptedException {
        click(GOAL_EXPORT_ICON);
        UI_LOGGER.info("Verify that PNG option is displayed");
        return isElementVisible(By.xpath("//div[@id='download-png-pdf-menu']//li/span[contains(text(),'" + PNG + "')]"));
    }

    public boolean verifyDistributorViewLeftSideFiltersGoalsHub(String filter) {
        return isElementVisible(By.id("sidebar-filter-" + filter));
    }

    public void clickDownloadCSV(String downloadCSV) throws InterruptedException {
        click(GOAL_EXPORT_ICON);
        UI_LOGGER.info("Verify that Download CSV option is displayed");
        isElementVisible(By.xpath("//div[@id='download-png-pdf-menu']//li/span[contains(text(),'" + downloadCSV + "')]"));
        click(By.xpath("//div[@id='download-png-pdf-menu']//li/span[contains(text(),'" + downloadCSV + "')]"));
    }

    public boolean verifyCSVHasCorrectColumns(String path) {
        List<String> columnsFromExcel = CsvUtility.getAllColumnNames(path);
        List<String> expectedAddedColumns = List.of("Goal Id", "Metric", "Interval", "Goal Title (for custom interval only)", "Start Date",
                "End Date", "Segmentation Type", "Goal Value (for non-range goals)", "Goal Min Value (for range goals)", "Goal Max Value (for range goals)",
                "Distributor View (not required for Availability)", "Delete Goal");
        UI_LOGGER.info("Columns from excel are " + columnsFromExcel + " which should contain " + expectedAddedColumns);

        return columnsFromExcel.containsAll(expectedAddedColumns);
    }


    public boolean isPdfOptionIsDisplayed(String PDF) throws InterruptedException {
        UI_LOGGER.info("Verify that PDF option is displayed");
        return isElementVisible(By.xpath("//div[@id='download-png-pdf-menu']//li/span[contains(text(),'" + PDF + "')]"));
    }

    public void selectAllGoalOption(String GoalOption) throws InterruptedException {
        click(GOALS_SELECTION);
        UI_LOGGER.info("Select All Goal Option");
        click(By.xpath("//ul[@role='listbox']/li[normalize-space()='" + GoalOption + "']"));
    }

    public String formatAvailabilityGoal(BigDecimal minValue, BigDecimal maxValue) {
        String goal = null;
        if (maxValue.compareTo(new BigDecimal(1000)) >= 0) {
            goal = "Goal " + minValue.toPlainString() + " - " + maxValue.divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "K" + "%";
        } else {
            goal = "Goal " + minValue.toPlainString() + " - " + maxValue.toPlainString() + "%";
        }
        return goal;
    }

    public String formatSalesGoals(BigDecimal value, boolean addCurrencySign) {
        String goalApiFormatted = null;
        String prefix = "Goal ";

        if (value.compareTo(new BigDecimal(1000000)) >= 0) {
            goalApiFormatted = value.divide(new BigDecimal(1000000)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "M";
        } else if (value.compareTo(new BigDecimal(1000)) >= 0 && value.compareTo(new BigDecimal(1000000)) < 0) {
            goalApiFormatted = value.divide(new BigDecimal(1000)).setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + "K";
        } else {
            goalApiFormatted = value.toPlainString();
        }
        if (addCurrencySign) {
            prefix = prefix + "$";
        }

        return prefix + goalApiFormatted;
    }

    public BigDecimal formatGoalReachedPercentageForAvailability(BigDecimal goalReached, BigDecimal minValue, BigDecimal maxValue) {
        BigDecimal goalReachedPercentageApi = null;
        if (goalReached.compareTo(minValue) >= 0 && goalReached.compareTo(maxValue) <= 0) {
            goalReachedPercentageApi = new BigDecimal(100);
        } else if (goalReached.compareTo(minValue) < 0) {
            goalReachedPercentageApi = goalReached.divide(minValue, 5, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).stripTrailingZeros();
        } else if (goalReached.compareTo(maxValue) > 0) {
            goalReachedPercentageApi = goalReached.divide(maxValue, 5, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).stripTrailingZeros();
        }
        return goalReachedPercentageApi;
    }

    public BigDecimal formatGoalReachedPercentageForSales(BigDecimal goalReached, BigDecimal value) {
        BigDecimal goalReachedPercentageApi = null;
        if (goalReached.compareTo(value) == 0) {
            goalReachedPercentageApi = new BigDecimal(100);
        } else {
            goalReachedPercentageApi = goalReached.divide(value, 5, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).stripTrailingZeros();
        }
        return goalReachedPercentageApi;
    }

    public void selectMetrics(String metrics) throws InterruptedException {
        UI_LOGGER.info("Select Metrics");
        click(METRICS_FILTER);
        WebElement metricsSelected = findElementVisible(By.xpath("//ul/span[text()='" + metrics + "'] | //ul/p[text()='" + metrics + "']"));
        metricsSelected.click();
    }

    public void selectBrand(String brand) throws InterruptedException {
        UI_LOGGER.info("Select Brand");
        click(BRAND_FILTER);
        WebElement brandSelected = findElementVisible(By.xpath("//ul/span[text()='" + brand + "'] | //ul/p[text()='" + brand + "']"));
        brandSelected.click();
    }

    public void clickApplyButton() throws InterruptedException {
        UI_LOGGER.info("Click on Apply Button");
        click(APPLY_BUTTON);
    }

    public String getTooltipText(String tootTip) {
        if (tootTip.equalsIgnoreCase("import")) {
            hoverOverElement(GOAL_IMPORT_ICON);
        } else if (tootTip.equalsIgnoreCase("export")) {
            hoverOverElement(GOAL_EXPORT_ICON);
        } else if (tootTip.equalsIgnoreCase("sample")) {
            hoverOverElement(SAMPLE_BTN);
        } else {
            Assert.fail("Invalid tooltip input.");
        }
        return getTextFromElement(TOOLTIP);
    }

    public void distributorViewDropdownValues() throws InterruptedException {
        click(DISTRIBUTOR_VIEW_DROPDOWN);
        List<WebElement> distributorViewValues = driver.findElements(By.xpath("//ul[@id='mui-6-listbox']/span"));
        for (int i = 0; i < distributorViewValues.size(); i++) {
            if (distributorViewValues.get(i).getText().contains("Sourcing") || distributorViewValues.get(i).getText().contains("Manufacturing")) {
                UI_LOGGER.info("Correct Dropdown values are displayed");
            } else {
                Assert.fail("Error: Incorrect Dropdown values are displayed");

            }
        }
    }

    public boolean isSamplePDFDisplayed() throws InterruptedException {
        click(CREATE_GOAL);
        return isElementVisible(SAMPLE_BTN);
    }

    public boolean allFilteredGoals() {
        return isElementVisible(All_AVAILABLITY_GOAL);
    }

    public boolean isExportDisplayedOnCreateGoal() {
        return isElementVisible(BULK_CREATED_GOAL_EXPORT);
    }

    public void clickCloseButton() throws InterruptedException {
        click(CLOSE_BTN);
    }

    public boolean verifyApplyButton(String apply) {
        return isElementVisible(By.xpath("//button[normalize-space()='" + apply + "']"));
    }

    public boolean verifyCancelButton(String clear) {
        return isElementVisible(By.xpath("//button[normalize-space()='" + clear + "']"));
    }

    public boolean verifyChangeLogButton() {
        return isElementVisible(CHANGE_LOGS);
    }

    public boolean verifyChangeLogFuntionality(String changelog) throws InterruptedException {
        UI_LOGGER.info("Click on Change log button");
        click(CHANGE_LOGS);
        return isElementVisible(By.xpath("//div[@role='tablist']/button[contains(text(),'" + changelog + "')]"));
    }

    public boolean verifyBusinessUnitHeader(String Businessunit) {
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + Businessunit + "')]"));
    }

    public boolean verifySegmentationTypeHeader(String Segmentationtype) {
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + Segmentationtype + "')]"));
    }

    public boolean verifySegmentationValueHeader(String Segmentationvalue) {
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + Segmentationvalue + "')]"));
    }

    public boolean verifyCurrentValueHeader(String Currentvalue) {
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + Currentvalue + "')]"));
    }

    public boolean verifyDateRangeHeader(String Daterange) {
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + Daterange + "')]"));
    }

    public boolean verifyLastModifiedHeader(String Lastmodified) {
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + Lastmodified + "')]"));
    }

    public boolean verifyFileNameHeader(String Filename) throws InterruptedException {
        UI_LOGGER.info("Click on Version History button");
        click(VERSION_HISTORY_TAB);
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + Filename + "')]"));
    }

    public boolean verifyUploadedByHeader(String Uploadedby) {
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + Uploadedby + "')]"));
    }

    public boolean verifyUploadedOnHeader(String UploadedOn) {
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + UploadedOn + "')]"));
    }

    public boolean verifyUploadedFileHeader(String Uploadedfile) {
        return isElementVisible(By.xpath("//div[@class='ag-root ag-unselectable ag-layout-auto-height']/div/div[2]//div[@role='row']/div//span[contains(text(),'" + Uploadedfile + "')]"));
    }

    public boolean verifyCloseButtonFunctionality(String close) throws InterruptedException {
        UI_LOGGER.info("Click on Close Button");
        boolean f = isElementClickable(By.xpath("//button[@type='button']/span[contains(text(),'" + close + "')]"));
        click(By.xpath("//button[@type='button']/span[contains(text(),'" + close + "')]"));
        isElementNotVisible(By.id("ModalContent"));
        return f;
    }

    public boolean versionHistoryTab() throws InterruptedException {
        click(CHANGE_LOGS);
        return isElementVisible(VERSION_HISTORY_TAB);
    }

    public void fileUpload(String filePath) throws InterruptedException {
        UI_LOGGER.info("Verify Bulk Edit/Delete Upload Option");
        isElementVisible(GOAL_IMPORT_ICON);
        click(GOAL_IMPORT_ICON);
        driver.findElement(CHOOSE_FILE).sendKeys(filePath);
        UI_LOGGER.info("File being uploaded: " + filePath);
        click(UPLOAD_BUTTON);
    }

    public String getFileNameText() {
        return getTextFromElement(FILE_NAME_TEXT);
    }

    public void createGOAL(String value) throws InterruptedException {
        UI_LOGGER.info("Click on Create GOAL");
        click(CREATE_GOAL);
        UI_LOGGER.info("Select Metrics");
        click(SELECT_METRICS);
        click(SELECT_SHIPPED_COGS);
        UI_LOGGER.info("Select interval");
        click(SELECT_INTERVAL);
        click(SELECT_MONTHLY);
        UI_LOGGER.info("Select Distributor View");
        click(SELECT_DISTRIBUTOR_VIEW);
        click(SELECT_MANUFACTURING);
        UI_LOGGER.info("Select Segmentation Type");
        click(SELECT_SEGMENTATION_TYPE);
        click(SELECT_BRAND);
        UI_LOGGER.info("Select Segmentation Value");
        click(SELECT_SEGMENTATION_VALUE);
        driver.findElement(SELECT_SEGMENTATION_VALUE).sendKeys("ADOLPHS");
        click(SELECT_ADOLPHS);
        UI_LOGGER.info("Set GOALS");
        driver.findElement(GOAL_MIN).sendKeys(value);
        UI_LOGGER.info("Click on Save Button");
        click(CREATE_SAVE_BTN);
    }

    public void editNewlyCreatedGoal() throws InterruptedException {
        UI_LOGGER.info("Click on Edit button");
        click(EDIT_ICON);
        UI_LOGGER.info("Update the Goal Value");
        isElementVisible(GOAL_MIN);
        driver.findElement(GOAL_MIN).sendKeys(Keys.BACK_SPACE, "2");
        UI_LOGGER.info("Click on Save Button");
        click(CREATE_SAVE_BTN);
    }

    public void verifyCarrotHeaderValues() throws InterruptedException {
        UI_LOGGER.info("Click on Change Logs Button");
        click(CHANGE_LOGS);
        UI_LOGGER.info("Verify Last Modified Date");
        if (isElementVisible(CARROT_ICON)) {
            UI_LOGGER.info("click on Carrot Icon");
            click(CARROT_ICON);
            Assert.assertTrue(isElementVisible(By.xpath("//div[@class='ag-header-row ag-header-row-column']//span[contains(text(),'Original Value')]")), "Original Value is not displayed");
            Assert.assertTrue(isElementVisible(By.xpath("//div[@class='ag-header-row ag-header-row-column']//span[contains(text(),'New Value')]")), "Original Value is not displayed");
            Assert.assertTrue(isElementVisible(By.xpath("//div[@class='ag-header-row ag-header-row-column']//span[contains(text(),'Modified Date')]")), "Original Value is not displayed");
            Assert.assertTrue(isElementVisible(By.xpath("//div[@class='ag-header-row ag-header-row-column']//span[contains(text(),'Modified By')]")), "Original Value is not displayed");
            click(CLOSE_BTN);
        }
    }

    public void verfiyLastModifiedDate() throws InterruptedException {
        UI_LOGGER.info("Click on Change Logs Button");
        click(CHANGE_LOGS);
        UI_LOGGER.info("Verify Last Modified Date");
        if (isElementVisible(DOUBLE_DASH_LAST_MODIFIED_DATE)) {
            UI_LOGGER.info("Double Dash is displayed and Goal in not updated");
            UI_LOGGER.info("Carrot icon is not displayed");
            click(CLOSE_BTN);
        } else {
            Assert.fail("Error: Element is not visible");
        }
    }

    public void deleteGoal() throws InterruptedException {
        UI_LOGGER.info("Click on the Goal Delete Icon");
        isElementVisible(DELETE_CREATED_GOAL);
        click(DELETE_ICON);
        UI_LOGGER.info("Click on Delete Goal");
        click(DELETE_GOAL);
        UI_LOGGER.info("Verify Deleted goal is not displayed Anymore");
        isElementNotVisible(DELETE_CREATED_GOAL);
    }

    public void setSalesMetric(boolean setMetric, boolean selectDistributorView) throws Exception {
        if (setMetric)
            setRandomSalesMetric();
        if (selectDistributorView)
            setRandomDistributorView();
    }

    public void setDateInterval(boolean setInterval, boolean selectDate, boolean setGoalTitle) throws Exception {
        if (setInterval)
            setInterval("CUSTOM");
        if (selectDate)
            selectCustomDate();
        if (setGoalTitle)
            setGoalTitle(SharedMethods.generateRandomString());
    }

    public void setSegmentation(boolean selectSegmentationType, boolean selectSegmentationValue) throws Exception {
        if (selectSegmentationType)
            setRandomSegmentationType();
        if (selectSegmentationValue)
            setRandomSegmentationValue();
    }


}