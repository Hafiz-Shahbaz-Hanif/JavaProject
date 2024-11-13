package com.DC.utilities.sharedElements;

import com.DC.pageobjects.PageHandler;
import com.DC.utilities.DateUtility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateAndIntervalPickerPage extends PageHandler {

    private static final By DATE_SELECTION = By.xpath("//*[@id = 'date-range-selector' or @id = 'ddlDateRange']");
    private static final By INTERVAL_SELECTION = By.xpath("//*[@id = 'interval-selector' or @name = 'interval']");
    private static final By SINGLE_DATE_OPTION = By.xpath("(//p[contains(@class, 'materialui-daterange-picker') and (text() = '1')])[3]");
    private static final By APPLY_IN_DATE_RANGE_PICKER = By.xpath("//div[@id='daterange-selector']//div//div//div//button[@type='button'][text()='Apply']");
    private static final By DAILY_INTERVAL_SELECTION = By.xpath("//li[text()='Daily']");
    private static final By WEEKLY_INTERVAL_SELECTION = By.xpath("//*[text()='Weekly']");
    private static final By CLOSE_ICON = By.xpath("//span[text()='close']");
    private static final By MONTHLY_INTERVAL_SELECTION = By.xpath("//li[text()='Monthly']");
    private static final By DATE_RANGE_CANCEL_BUTTON = By.xpath("//div[@class='MuiBox-root css-1w2usl6']//button[normalize-space()='Cancel']");
    private static final Object[][] DATE_RANGE_FORMATTED_OPTIONS = {
            {"Last 7 Days", DateUtility.formattingDate(DateUtility.getFirstDayOfLastSevenDays()) + " - " + DateUtility.formattingDate(DateUtility.getYesterday())},
            {"Last 14 Days", DateUtility.formattingDate(DateUtility.getFirstDayOfLastFourteenDays()) + " - " + DateUtility.formattingDate(DateUtility.getYesterday())},
            {"Last 30 Days", DateUtility.formattingDate(DateUtility.getFirstDayOfLastThirtyDays()) + " - " + DateUtility.formattingDate(DateUtility.getYesterday())},
            {"Last 4 Weeks", DateUtility.formattingDate(DateUtility.getFirstDayOfLastFourWeeks()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastFourWeeks())},
            {"Last 13 Weeks", DateUtility.formattingDate(DateUtility.getFirstDayOfLastThirteenWeeks()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastThirteenWeeks())},
            {"Last 52 Weeks", DateUtility.formattingDate(DateUtility.getFirstDayOfLastFiftyTwoWeeks()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastFiftyTwoWeeks())},
            {"This Month", DateUtility.formattingDate(DateUtility.getFirstDayOfThisMonth()) + " - " + DateUtility.formattingDate(DateUtility.getTodayDate())},
            {"Last Month", DateUtility.formattingDate(DateUtility.getFirstDayOfLastMonth()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastMonth())},
            {"Last 6 Months", DateUtility.formattingDate(DateUtility.getFirstDayOfLastSixMonths()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastSixMonths())},
            {"Last 12 Months", DateUtility.formattingDate(DateUtility.getFirstDayOfLastTwelveMonths()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastTwelveMonths())},
            {"Year to date", DateUtility.formattingDate(DateUtility.getFirstDayOfTheYear()) + " - " + DateUtility.formattingDate(DateUtility.getTodayDate())}
    };
    private static final Object[][] DATE_RANGE_FORMATTED_OPTIONS_FOR_WEEKLY_INTERVAL = {
            {"Last Week", DateUtility.formattingDate(DateUtility.getFirstDayOfLastWeek()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastWeek())},
            {"Last 4 Weeks", DateUtility.formattingDate(DateUtility.getFirstDayOfLastFourWeeks()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastFourWeeks())},
            {"Last 13 Weeks", DateUtility.formattingDate(DateUtility.getFirstDayOfLastThirteenWeeks()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastThirteenWeeks())},
            {"Last 52 Weeks", DateUtility.formattingDate(DateUtility.getFirstDayOfLastFiftyTwoWeeks()) + " - " + DateUtility.formattingDate(DateUtility.getLastDayOfLastFiftyTwoWeeks())}
    };

    public DateAndIntervalPickerPage(WebDriver driver) {
        super(driver);
        findElementVisible(DATE_SELECTION);
    }

    public boolean isDateSelectionDisplayed() {
        return isElementVisible(DATE_SELECTION);
    }

    public boolean isIntervalSelectionDisplayed() {
        return isElementVisible(INTERVAL_SELECTION);
    }

    public String getDefaultDateSelection() {

        WebElement dateSelection = findElementVisible(By.xpath("//*[@id='date-range-selector']/span[2]"));

        return dateSelection.getText();
    }

    public void selectSingleDate() throws InterruptedException {
        click(DATE_SELECTION);
        Thread.sleep(1000);
        doubleClick(SINGLE_DATE_OPTION);
        Thread.sleep(1000);
        click(APPLY_IN_DATE_RANGE_PICKER);
    }

    public String getSelectedDate() {
        WebElement dateSelection = findElementVisible(By.xpath("//*[@id='date-range-selector']/span[2]"));
        return dateSelection.getText();
    }

    public String openDateRangeAndGetSelectedDate() throws InterruptedException {
        if (!isElementPresentMilliseconds(APPLY_IN_DATE_RANGE_PICKER)) {
            click(DATE_SELECTION);
        }
        return getTextFromElement(By.xpath("//span[@style='font-weight: bold;']"));
    }

    public List<String> getIntervalDropdownOptions() throws InterruptedException {

        click(INTERVAL_SELECTION);
        List<WebElement> intervalOptions = findElementsVisible(By.xpath("//ul[contains(@role,'listbox')]/li"));
        List<String> actualIntervalOptions = new ArrayList<>();

        for (WebElement option : intervalOptions) {
            actualIntervalOptions.add(option.getText());
        }

        return actualIntervalOptions;
    }

    public String getDefaultIntervalSelection() {
        WebElement intervalSelection = findElementVisible(INTERVAL_SELECTION);
        return intervalSelection.getText();
    }

    public void clickIntervalDropdown() throws InterruptedException {
        click(DAILY_INTERVAL_SELECTION);
    }

    public void clickMonthlyIntervalDropdown() throws InterruptedException {
        click(MONTHLY_INTERVAL_SELECTION);
    }

    public void clickWeeklyIntervalDropdown() throws InterruptedException {
        click(INTERVAL_SELECTION);
        click(WEEKLY_INTERVAL_SELECTION);
    }

    public List<String> selectDateRange() throws InterruptedException {
        List<String> actualSelectedDateRanges = new ArrayList<>();
        for (Object[] range : DATE_RANGE_FORMATTED_OPTIONS) {
            String dateRangeText = (String) range[0];
            By dateRangeOption = By.xpath("//span[text()='" + dateRangeText + "']");
            click(DATE_SELECTION);
            click(dateRangeOption);
            click(APPLY_IN_DATE_RANGE_PICKER);
            String actualDateRange = getSelectedDate();
            actualSelectedDateRanges.add(actualDateRange);
        }
        return actualSelectedDateRanges;
    }

    public List<String> getExpectedDateRanges() {
        List<String> expectedDateRanges = new ArrayList<>();
        for (Object[] range : DATE_RANGE_FORMATTED_OPTIONS) {
            String expectedDateRange = (String) range[1];
            expectedDateRanges.add(expectedDateRange);
        }
        return expectedDateRanges;
    }

    public boolean isExportDateSelectionMirrorUI() throws InterruptedException {
        String dateInExport = getExportDateSelection();
        clickCloseIcon();
        String dateInUI = getSelectedDate();
        UI_LOGGER.info("Export date selection: " + dateInExport);
        UI_LOGGER.info("UI date selection: " + dateInUI);
        return dateInExport.equals(dateInUI);
    }

    public String getExportDateSelection() {
        WebElement dateSelection = findElementVisible(By.xpath("//div[@id='ModalContent']//button[@id='date-range-selector']/span[2]"));
        return dateSelection.getText();
    }

    public void clickCloseIcon() throws InterruptedException {
        click(CLOSE_ICON);
    }

    public void clickDateRangeCancelButton() throws InterruptedException {
        click(DATE_RANGE_CANCEL_BUTTON);
    }

    public boolean isDateColumnSortedCorrectly() {
        String startingDateInDatePicker = getSelectedDate().substring(0, 10);
        String lastDateInDatePicker = getSelectedDate().substring(13, 23);
        String firstDateInTableXPath = "//h3[@class='ag-header-cell-text' and contains(., '" + lastDateInDatePicker + "')]";
        String lastDateInTableXPath = "//h3[@class='ag-header-cell-text' and contains(., '" + startingDateInDatePicker + "')]";
        String horizontalScrollViewportXPath = "//div[@class='ag-body-horizontal-scroll-viewport']";

        WebElement firstDateInTable;
        if (!isElementVisible(By.xpath(firstDateInTableXPath))) {
            scrollIntoView(By.xpath(firstDateInTableXPath));
        }
        firstDateInTable = findElementVisible(By.xpath(firstDateInTableXPath));
        String firstDateValue = extractDateFromText(firstDateInTable.getText());

        scrollIntoView(By.xpath(horizontalScrollViewportXPath));
        scrollElementHorizontally(2000, By.xpath(horizontalScrollViewportXPath));

        WebElement lastDateInTable;
        if (!isElementVisible(By.xpath(lastDateInTableXPath))) {
            scrollIntoView(By.xpath(lastDateInTableXPath));
        }
        lastDateInTable = findElementVisible(By.xpath(lastDateInTableXPath));
        String lastDateValue = extractDateFromText(lastDateInTable.getText());

        UI_LOGGER.info("First date value: " + firstDateValue + ", Selected most recent date: " + lastDateInDatePicker);
        UI_LOGGER.info("Last date value: " + lastDateValue + ", Selected latest date: " + startingDateInDatePicker);

        assert firstDateValue != null;
        if (!firstDateValue.equals(lastDateInDatePicker)) return false;
        assert lastDateValue != null;
        return lastDateValue.equals(startingDateInDatePicker);
    }

    public void selectDateRange(String dateRange) throws InterruptedException {
        click(DATE_SELECTION);
        Thread.sleep(1000);
        doubleClick(By.xpath("//span[text()='" + dateRange + "']"));
        Thread.sleep(1000);
        click(APPLY_IN_DATE_RANGE_PICKER);
    }

    public void selectInterval(String interval) throws InterruptedException {
        click(INTERVAL_SELECTION);
        Thread.sleep(1000);
        click(By.xpath("//li[text()='" + interval + "']"));
    }

    public List<String> getDateRangeDropdownOptions() throws InterruptedException {
        click(DATE_SELECTION);
        List<WebElement> dateRangeOptions = findElementsVisible(By.xpath("//ul[contains(@class,'materialui-daterange-picker')]/div/div/span"));
        List<String> actualDateRangeOptions = new ArrayList<>();

        for (WebElement option : dateRangeOptions) {
            actualDateRangeOptions.add(option.getText());
        }

        return actualDateRangeOptions;
    }

    private String extractDateFromText(String text) {
        String datePattern = "\\d{2}/\\d{2}/\\d{4}";
        Pattern pattern = Pattern.compile(datePattern);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    public String getSelectedInterval() {
        WebElement intervalSelection = findElementVisible(INTERVAL_SELECTION);
        return intervalSelection.getText();
    }

    public List<String> selectDateRangeForWeeklyInterval() throws InterruptedException {
        List<String> actualSelectedDateRanges = new ArrayList<>();
        for (Object[] range : DATE_RANGE_FORMATTED_OPTIONS_FOR_WEEKLY_INTERVAL) {
            String dateRangeText = (String) range[0];
            By dateRangeOption = By.xpath("//span[text()='" + dateRangeText + "']");
            click(DATE_SELECTION);
            click(dateRangeOption);
            click(APPLY_IN_DATE_RANGE_PICKER);
            String actualDateRange = getSelectedDate();
            actualSelectedDateRanges.add(actualDateRange);
        }
        return actualSelectedDateRanges;
    }

    public List<String> getExpectedDateRangesForWeeklyInterval() {
        List<String> expectedDateRanges = new ArrayList<>();
        for (Object[] range : DATE_RANGE_FORMATTED_OPTIONS_FOR_WEEKLY_INTERVAL) {
            String expectedDateRange = (String) range[1];
            expectedDateRanges.add(expectedDateRange);
        }
        return expectedDateRanges;
    }

    public int countDaysInDateRange(String dateRange) throws ParseException {
        String[] dates = dateRange.split(" - ");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = sdf.parse(dates[0]);
        Date endDate = sdf.parse(dates[1]);
        long diff = endDate.getTime() - startDate.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
    }

    public boolean compareDates(List<String> returnedDates) {
        String startingDateInDatePicker = getSelectedDate().substring(0, 10);
        String lastDateInDatePicker = getSelectedDate().substring(13, 23);

        String firstDateInTable = returnedDates.get(0);
        String lastDateInTable = returnedDates.get(returnedDates.size() - 1);

        return firstDateInTable.equals(lastDateInDatePicker) && lastDateInTable.equals(startingDateInDatePicker);
    }

    public void selectCustomDateRange(int startDay, int endDay) throws InterruptedException {
        By startDatePicker = By.xpath("//button[@type='button' and not(@disabled)]//p[contains(@class, 'materialui-daterange-picker') and (text() = '" + startDay + "')]");
        By endDatePicker = By.xpath("//button[@type='button' and not(@disabled)]//p[contains(@class, 'materialui-daterange-picker') and (text() = '" + endDay + "')]");
        click(DATE_SELECTION);
        click(startDatePicker);
        click(endDatePicker);
        click(APPLY_IN_DATE_RANGE_PICKER);
    }

    public static String transformDate(String date) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        String[] dateParts = date.split(" - ");
        if (dateParts.length == 2) {
            Date startDate = inputFormat.parse(dateParts[0].trim());
            Date endDate = inputFormat.parse(dateParts[1].trim());

            String transformedStartDate = outputFormat.format(startDate);
            String transformedEndDate = outputFormat.format(endDate);

            return transformedStartDate + " - " + transformedEndDate;
        }
        return date;
    }

    public String convertDateFormat(String inputDate) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
    }

    public void clickDateRangePicker(String monthToSelect, int startDay, int endDay) throws InterruptedException {
        By startDatePicker = By.xpath("//p[contains(@class, 'materialui-daterange-picker') and (text() = '" + startDay + "')]");
        By endDatePicker = By.xpath("//p[contains(@class, 'materialui-daterange-picker') and (text() = '" + endDay + "')]");
        click(DATE_SELECTION);
        WebElement month = findElementVisible(By.xpath("(//div[contains(@class, 'materialui-daterange-picker')]//div[@aria-haspopup='listbox'])[1]"));
        month.click();
        Thread.sleep(1000);

        List<WebElement> monthSelection = findElementsVisible(By.xpath("//ul[@role='listbox']/li"));
        for (WebElement monthOption : monthSelection) {
            if (monthOption.getText().equals(monthToSelect)) {
                monthOption.click();
                break;
            }
        }
        click(startDatePicker);
        click(endDatePicker);
        click(APPLY_IN_DATE_RANGE_PICKER);
    }

    public void selectCustomDateRangeWithYear(String yearToSelect, String monthToSelect, int startDay, int endDay) throws InterruptedException {
        By startDatePicker = By.xpath("//p[contains(@class, 'materialui-daterange-picker') and (text() = '" + startDay + "')]");
        By endDatePicker = By.xpath("//p[contains(@class, 'materialui-daterange-picker') and (text() = '" + endDay + "')]");
        click(DATE_SELECTION);
        clickElement(By.xpath("(//div[contains(@class, 'materialui-daterange-picker')]//div[@aria-haspopup='listbox'])[2]"));
        Thread.sleep(1000);

        List<WebElement> yearSelection = findElementsVisible(By.xpath("//ul[@role='listbox']/li"));
        for (WebElement yearOption : yearSelection) {
            if (yearOption.getText().equals(yearToSelect)) {
                yearOption.click();
                break;
            }
        }

        WebElement month = findElementVisible(By.xpath("(//div[contains(@class, 'materialui-daterange-picker')]//div[@aria-haspopup='listbox'])[1]"));
        month.click();
        Thread.sleep(1000);

        List<WebElement> monthSelection = findElementsVisible(By.xpath("//ul[@role='listbox']/li"));
        for (WebElement monthOption : monthSelection) {
            if (monthOption.getText().equals(monthToSelect)) {
                monthOption.click();
                break;
            }
        }
        click(startDatePicker);
        click(endDatePicker);
        click(APPLY_IN_DATE_RANGE_PICKER);
    }
}