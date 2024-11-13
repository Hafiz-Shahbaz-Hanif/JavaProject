package com.DC.pageobjects.adc.execute.mediaManagement;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.CommonFeatures;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import com.DC.utilities.CsvUtility;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AirTrafficControlPage extends NetNewNavigationMenu {

    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public CommonFeatures commonFeatures;
    private static final By ATC_HEADER = By.xpath("//a[text()='Air Traffic Control']");
    private static final By EXPORT_BUTTON = By.id("export-button");
    private static final By CLOSE_ALERT_BUTTON = By.xpath("//div[@role='alert']//button/following-sibling::button");
    private static final By CSQ_COLUMN_HEADER = By.xpath("//span[text()='CSQ']");
    private static final By CSQ_COLUMN_FILTER = By.xpath("//span[@class='ag-icon ag-icon-menu']");
    private static final By CSQ_COLUMN_FILTER_TEXTBOX = By.xpath("(//input[@placeholder='Filter...'])[1]");
    private static final By CSQ_COLUMN_FILTER_APPLY_BUTTON = By.xpath("//button[@type='submit']");
    private static final By ATC_REPORT_METRIC_SELECTOR = By.xpath("//label[text()='Metric']/following-sibling::div");
    private static final By FLYOUT_EXPORT_BUTTON = By.id("export-graph-select");
    private static final By ATC_REPORT_METRIC_CALLOUTS_CHECKBOX = By.xpath("//*[@data-testid='CheckBoxIcon']/preceding-sibling::input");
    private static final By CAMPAIGN_NAME_FILTER = By.xpath("//div[@id='sidebar-filter-campaign-name']//input[@placeholder='Type to Filter']");
    private static final By APPLY_BUTTON = By.xpath("//button[text()='Apply']");
    private static final By FIDO_BIDDING_ALERT = By.xpath("//div[contains(@class,'MuiAlert-message')]");

    public By getAutomateThisWithXpath(String automationType) {
        return By.xpath("//*[text()='" + automationType + "']");
    }

    public By getButtonXpath(String buttonName) {
        return By.xpath("//button[text()='" + buttonName + "']");
    }

    public AirTrafficControlPage(WebDriver driver) {
        super(driver);
        findElementVisible(ATC_HEADER);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
        commonFeatures = new CommonFeatures(driver);
    }

    public boolean isATCScreenDisplayed() {
        return isElementVisible(ATC_HEADER);
    }

    public boolean isExportButtonDisplayed() {
        return isElementVisible(EXPORT_BUTTON);
    }

    public void clickExportButton() throws InterruptedException {
        click(EXPORT_BUTTON);
    }

    public List<String> getExportOptions() {
        List<String> exportOptions = new ArrayList<>();
        List<WebElement> exportOptionsElements = findElementsVisible(By.xpath("//ul[@aria-labelledby='export-button']/li"));
        for (WebElement exportOption : exportOptionsElements) {
            exportOptions.add(exportOption.getText());
        }
        return exportOptions;
    }

    public void clickExportButtonOption(String exportButtonName) throws InterruptedException {
        By exportButton = By.xpath("//ul[@aria-labelledby='export-button']/li[text()='" + exportButtonName + "']");
        click(exportButton);
    }

    public boolean isExportSuccessMessageDisplayed() {
        String exportSuccessfulMessage = "Air Traffic Control export has been started. To download results, go to ";
        return isElementVisible(By.xpath("//div[text()='" + exportSuccessfulMessage + "']"));
    }

    public void closeAlert() throws InterruptedException {
        click(CLOSE_ALERT_BUTTON);
    }

    public boolean isExportErrorMessageDisplayed(String maxDaysForErrorMessage) {
        String exportErrorMessage = "Air Traffic Control export failed. Reason: Selected date range cannot exceed " + maxDaysForErrorMessage + "";
        return isElementVisible(By.xpath("//div[text()='" + exportErrorMessage + "']"));
    }

    public void clickDownloadManagerLink() throws InterruptedException {
        By downloadManagerLink = By.xpath("//div[text()='Air Traffic Control export has been started. To download results, go to ']//a[text()='Download Manager']");
        click(downloadManagerLink);
    }

    public boolean verifyCSVHasCorrectColumns(String path) {
        List<String> columnsFromExcel = CsvUtility.getAllColumnNames(path);
        List<String> expectedAddedColumns = List.of("Ensemble Spend", "Ensemble Sales", "Perpetua Spend", "Perpetua Sales", "FIDO Spend", "FIDO Sales", "Rule-Based Bidding Spend",
                "Rule-Based Bidding Sales", "Non-Automated Spend", "Non-Automated Sales", "Current Automation Applied", "Campaign Name", "Match Type", "Keyword");
        UI_LOGGER.info("Columns from excel are " + columnsFromExcel + " which should contain " + expectedAddedColumns);

        return columnsFromExcel.containsAll(expectedAddedColumns);
    }

    public void selectCSQuery(String queryName) throws InterruptedException {
        findElementVisible(CSQ_COLUMN_HEADER);
        click(CSQ_COLUMN_FILTER);
        findElementVisible(CSQ_COLUMN_FILTER_TEXTBOX);
        sendKeys(CSQ_COLUMN_FILTER_TEXTBOX, queryName);
        click(CSQ_COLUMN_FILTER_APPLY_BUTTON);
        findElementVisible(CSQ_COLUMN_HEADER, Duration.ofSeconds(15));
    }

    public double getTotalSumFromUI(String columnName) {
        By columnValue = By.xpath("//div[@role='gridcell' and @col-id='" + columnName + "']//span");
        String columnValueFromUI = getTextFromElement(columnValue);
        return Double.parseDouble(columnValueFromUI.replace("$", "").replace(",", ""));
    }

    public boolean isSumFromUIMatchingSumFromCSV(String path, String columnName, double totalSumFromUI) {
        double totalSumFromCSV = CsvUtility.getColumnSum(path, columnName);
        UI_LOGGER.info("Total sum from UI is " + totalSumFromUI + " and total sum from CSV is " + totalSumFromCSV);
        return totalSumFromUI == totalSumFromCSV;
    }

    public double getSumOfAllAutomationTypes(String filepath, List<String> columnNames, int rowNumber) {
        double sum = 0.0;
        try (CSVReader reader = new CSVReader(new FileReader(filepath))) {
            String[] header = reader.readNext();
            if (header != null) {
                String[] nextLine = new String[0];
                for (int currentRow = 1; currentRow <= rowNumber; currentRow++) {
                    nextLine = reader.readNext();
                    if (nextLine == null) {
                        Assert.fail("Row number " + rowNumber + " is not present in the file");
                        return sum;
                    }
                }

                int[] columnIndices = new int[columnNames.size()];
                for (int i = 0; i < columnNames.size(); i++) {
                    for (int j = 0; j < header.length; j++) {
                        if (columnNames.get(i).equalsIgnoreCase(header[j])) {
                            columnIndices[i] = j;
                            break;
                        }
                    }
                }

                for (int columnIndex : columnIndices) {
                    String columnValueStr = nextLine[columnIndex];
                    if (!columnValueStr.isEmpty()) {
                        try {
                            double columnValue = Double.parseDouble(columnValueStr);
                            sum += columnValue;
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Value is not a number: " + columnValueStr);
                        }
                    }
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return sum;
    }

    public boolean isAutomationTypeSumMatchesAggregated(String path, String columnToCheck, int rowNumber, List<String> columnNames) {
        String aggregatedValueFromCSVText = CsvUtility.getCellContent(path, rowNumber, columnToCheck);
        double aggregatedValueFromCSV = Double.parseDouble(aggregatedValueFromCSVText.replace("$", "").replace(",", ""));
        double sumOfAutomationTypes = getSumOfAllAutomationTypes(path, columnNames, rowNumber);
        UI_LOGGER.info("Aggregated value from CSV is " + aggregatedValueFromCSV + " and sum of automation types is " + sumOfAutomationTypes);
        return aggregatedValueFromCSV == sumOfAutomationTypes;
    }

    public boolean verifyCSVHasDifferentValuesInColumn(String path, String columnName) {
        List<String> columnValues = CsvUtility.getAllCellValuesInColumn(path, columnName);
        return columnValues.stream().distinct().count() > 1;
    }

    public boolean isReportMetricsSelectionDisplayed() {
        return isElementVisible(ATC_REPORT_METRIC_SELECTOR);
    }

    public List<String> getReportMetricsOptions() throws InterruptedException {
        click(ATC_REPORT_METRIC_SELECTOR);
        List<WebElement> reportMetricsOptionsElements = findElementsVisible(By.xpath("//ul[@role='listbox']/li"));
        return getTextFromElements(reportMetricsOptionsElements);
    }

    public boolean isSpendSelectedByDefault() {
        return findElementVisible(ATC_REPORT_METRIC_SELECTOR).getText().equals("Spend");
    }

    public void selectReportMetric(String metricToSelect) throws InterruptedException {
        waitForElementClickable(ATC_REPORT_METRIC_SELECTOR, Duration.ofSeconds(30));
        click(ATC_REPORT_METRIC_SELECTOR);
        click(By.xpath("//ul[@role='listbox']/li[text()='" + metricToSelect + "']"));
    }

    public String getSelectedReportMetricName() {
        return findElementVisible(ATC_REPORT_METRIC_SELECTOR).getText();
    }

    public void clickLegendLabel(List<String> labelsToClick, boolean select) {
        List<WebElement> legendLabelsElements = findElementsVisible(By.xpath("//*[@class='highcharts-legend highcharts-no-tooltip']//div[contains(@class,'highcharts-legend-item')]//span/.."));
        for (WebElement legendLabel : legendLabelsElements) {
            WebElement legendLabelText = legendLabel.findElement(By.xpath("./span"));
            boolean isLabelHidden = legendLabel.getAttribute("class").contains("highcharts-legend-item-hidden");
            String labelText = legendLabelText.getText();
            if (labelsToClick.contains(labelText)) {
                if ((select && isLabelHidden) || (!select && !isLabelHidden)) {
                    legendLabelText.click();
                }
            }
        }
    }

    public boolean isNoDataDisplayed() {
        return isElementVisible(By.xpath("//span[text()='No Data To Display']"));
    }

    public void clickATCChartExportIcon() throws InterruptedException {
        click(FLYOUT_EXPORT_BUTTON);
    }

    public void clickATCChartExportOption(String exportOption) throws InterruptedException {
        click(By.xpath("//ul[@role='listbox']/li[text()='" + exportOption + "']"));
    }

    public List<String> getDaysAndValuesFromFlyout() {
        List<WebElement> everyDayValues = findElementsVisible(By.xpath("//*[@class='highcharts-series-group']/following-sibling::*[contains(@class,'highcharts-data-labels') and not(@visibility='hidden')]//*[@class='highcharts-text-outline']"));
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        List<String> valuesFromFlyout = new ArrayList<>();
        for (WebElement everyDayValue : everyDayValues) {
            var value = SharedMethods.convertToNumber(everyDayValue.getText());
            String formattedValue = decimalFormat.format(value);
            valuesFromFlyout.add(formattedValue);
        }
        return valuesFromFlyout;
    }

    public Map<String, Double> getSumOfValuesFromFlyout(Map<String, Map<String, String>> datesAndValuesFromDB) {
        Map<String, Double> sumOfValues = new HashMap<>();

        for (Map.Entry<String, Map<String, String>> outerEntry : datesAndValuesFromDB.entrySet()) {
            Map<String, String> columnValues = outerEntry.getValue();

            for (Map.Entry<String, String> innerEntry : columnValues.entrySet()) {
                String columnName = innerEntry.getKey();
                String value = innerEntry.getValue();
                double parsedValue = Double.parseDouble(value);

                sumOfValues.put(columnName, sumOfValues.getOrDefault(columnName, 0.0) + parsedValue);
            }
        }

        return sumOfValues;
    }

    public boolean doValuesFromFlyoutMatchValuesFromDB(List<String> valuesFromFlyout, Map<String, Map<String, String>> valuesFromDB) {
        List<String> expectedValuesFromDB = valuesFromDB.values().stream()
                .flatMap(subMap -> subMap.values().stream())
                .filter(value -> !value.equals("0.00"))
                .collect(Collectors.toList());

        List<String> filteredDatesAndValuesFromLineChart = valuesFromFlyout.stream()
                .filter(value -> !value.equals("0.00"))
                .collect(Collectors.toList());

        Collections.sort(expectedValuesFromDB);
        Collections.sort(filteredDatesAndValuesFromLineChart);
        return expectedValuesFromDB.equals(filteredDatesAndValuesFromLineChart);
    }

    public void updateSumsForDate(Map<String, String> sumsForDate, String column, double valueToAdd) {
        if (!sumsForDate.containsKey(column)) {
            sumsForDate.put(column, String.format("%.2f", valueToAdd));
        } else {
            double existingValue = Double.parseDouble(sumsForDate.get(column));
            double updatedValue = existingValue + valueToAdd;
            sumsForDate.put(column, String.format("%.2f", updatedValue));
        }
    }

    public boolean isCalloutsDisplayed() {
        return isElementPresent(ATC_REPORT_METRIC_CALLOUTS_CHECKBOX);
    }

    public boolean isCalloutsCheckboxSelected() {
        return findElementPresent(ATC_REPORT_METRIC_CALLOUTS_CHECKBOX).getAttribute("value").equals("true");
    }

    public void clickCalloutsCheckbox() throws InterruptedException {
        click(ATC_REPORT_METRIC_CALLOUTS_CHECKBOX);
    }

    public boolean isCalloutsValuesDisplayed() {
        return isElementVisible(By.xpath("//*[contains(@class,'highcharts-data-labels')]//*[@class='highcharts-text-outline']")) &&
                !getTextFromElement(By.xpath("//*[contains(@class,'highcharts-data-labels')]//*[@class='highcharts-text-outline']"))
                        .isEmpty();
    }

    public boolean isValuesFromFlyoutMatchValuesFromDB(List<String> valuesFromFlyout, Map<String, Map<String, String>> valuesFromDB) {
        List<String> expectedValuesFromDB = valuesFromDB.values().stream()
                .flatMap(subMap -> subMap.values().stream())
                .filter(value -> !value.equals("0.00"))
                .map(value -> String.format("%.2f", Double.parseDouble(value)))
                .collect(Collectors.toList());

        List<String> filteredDatesAndValuesFromLineChart = valuesFromFlyout.stream()
                .filter(value -> !value.equals("0.00"))
                .map(value -> String.format("%.2f", Double.parseDouble(value)))
                .collect(Collectors.toList());

        Collections.sort(expectedValuesFromDB);
        Collections.sort(filteredDatesAndValuesFromLineChart);
        return expectedValuesFromDB.equals(filteredDatesAndValuesFromLineChart);
    }

    public List<String> getLegendLabels() {
        List<String> legendLabels = new ArrayList<>();
        List<WebElement> legendLabelsElements = findElementsVisible(By.xpath("//*[@class='highcharts-legend highcharts-no-tooltip']//div[contains(@class,'highcharts-legend-item')]//span/.."));
        for (WebElement legendLabel : legendLabelsElements) {
            boolean isLabelHidden = legendLabel.getAttribute("class").contains("highcharts-legend-item-hidden");
            if (!isLabelHidden) {
                legendLabels.add(legendLabel.getText());
            }
        }
        return legendLabels;
    }

    public List<WebElement> getLinesInChart() {
        return findElementsVisible(By.xpath("//*[@class='highcharts-series-group']//*[@class='highcharts-tracker-line' and not(@visibility='hidden')]"));
    }

    public void selectCampaign(String campaignName) throws InterruptedException {
        click(CAMPAIGN_NAME_FILTER);
        sendKeys(CAMPAIGN_NAME_FILTER, campaignName);
        var campaignItemLocator = By.xpath("//div[@id='sidebar-filter-campaign-name']//ul/span[contains(text(),'" + campaignName + "')]");
        clickElement(campaignItemLocator);
        waitForElementClickable(APPLY_BUTTON);
        click(APPLY_BUTTON);
    }

    public void clickAsin(String asin) {
        var asinLocator = By.xpath("//div[@role='rowgroup']//span[contains(@id,'cell-automatedUnits')]//div//p[contains(text(),'" + asin + "')]");
        try {
            clickElement(asinLocator);
        } catch (StaleElementReferenceException e) {
            clickElement(asinLocator);
        }
    }

    public void clickFidoBiddingButton() throws InterruptedException {
        By automateThisWithLocator = getAutomateThisWithXpath("With FIDO Bidding");
        waitForElementClickable(automateThisWithLocator);
        click(automateThisWithLocator);
    }

    public void launchFidoBidding() throws InterruptedException {
        By launchButtonLocator = getButtonXpath("Launch");
        clickFidoBiddingButton();
        waitForElementClickable(launchButtonLocator);
        click(launchButtonLocator);
    }

    public String getAlertMessage() {
        if (isElementVisible(FIDO_BIDDING_ALERT)) {
            String alertMessage = getTextFromElement(FIDO_BIDDING_ALERT);
            waitForElementToBeInvisible(FIDO_BIDDING_ALERT, Duration.ofSeconds(30));
            return alertMessage;
        }
        return null;
    }

    public String getAsinBackgroundColor(String asin) {
        WebElement asinElement;
        By asinsIconLocator = By.xpath("//div[@role='rowgroup']//span[contains(@id,'cell-automatedUnits')]//div//p[contains(text(),'" + asin + "')]/preceding-sibling::span");
        try {
            asinElement = findElementVisible(asinsIconLocator, Duration.ofSeconds(30));
        } catch (StaleElementReferenceException e) {
            asinElement = findElementVisible(asinsIconLocator, Duration.ofSeconds(30));
        }
        if (asinElement != null) {
            return asinElement.getCssValue("background-color");
        } else {
            return null;
        }
    }

    public void clickDisableFidoBiddingButton() throws InterruptedException {
        By disableButtonLocator = getButtonXpath("Disable");
        waitForElementClickable(disableButtonLocator);
        click(disableButtonLocator);
    }
}
