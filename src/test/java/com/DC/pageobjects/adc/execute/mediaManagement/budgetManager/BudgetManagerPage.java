package com.DC.pageobjects.adc.execute.mediaManagement.budgetManager;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.CsvUtility;

import com.DC.utilities.DateUtility;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.testng.Assert;


import java.io.File;

import java.time.Duration;
import java.util.*;

public class BudgetManagerPage extends NetNewNavigationMenu {
    public DateAndIntervalPickerPage dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
    private static final By BUDGET_SUBMISSION = By.xpath("//button[@id='budget-submission']");
    private static final By IO_SEGMENT_FILTER = By.xpath("//div[@id='sidebar-filter-io-segment']//input");
    private static final By APPLY_BUTTON = By.xpath("//button[text()='Apply']");
    private static final By CLEAR_BUTTON = By.xpath("//button[text()='Clear'] | //a[text()='Clear']");
    private static final By IO_SEGMENT_COLUMN = By.xpath("//span[contains(@id,'cell-agencySegment')]");
    private static final By IO_SEGMENT_TOGGLE_BUTTON = By.xpath("//input[contains(@class,'PrivateSwitchBase-input')]");
    private static final By FILE_UPLOAD_INPUT = By.xpath("//input[@id='file-upload']");
    private static final By FILTER_ELEMENTS = By.xpath("//span[@role='option']");
    private static final By PROGRESS_BAR = By.xpath("//span[@role='progressbar']");
    private final By MULTIPLATFORM_DROPDOWN = By.xpath("(//span[@class='material-symbols-outlined'])[5]");
    private final By B_M_UPLOAD_BUTTON = By.xpath("(//span[@class=contains(text(),'upload')])[2]");
    private final By B_M_DOWNLOAD_BUTTON = By.xpath("(//span[@class=contains(text(),'download')])[2]");
    private final By BULK_UPLOAD_BUTTON = By.xpath("//button[@id='bulk-upload-button']");
    private final By CONFIRM_UPLOAD_BUTTON = By.xpath("//button[@id='confirmation-dialogue-confirm-button']");
    private static final By POP_UP_UPLOAD_PROCESSING = By.xpath("//div[contains(@class,'MuiAlert-message')]");
    private static final By BUDGET_TRACKER_BUTTON = By.xpath("//button[contains(text(),'Budget Tracker')]");
    private static final By PACING_DETAILS_HEADER = By.xpath("//a[normalize-space()='Pacing Details']");
    private static final By PACING_METHOD_EDIT_ICON = By.xpath("//div[@row-id=0]//span[normalize-space()='edit'] | //span[normalize-space()='edit']");
    private static final By PACING_METHOD_DROPDOWN_ICON = By.xpath("//div[@role='button']");
    private static final By PACING_METHOD_DROPDOWN_LIST = By.xpath("//ul[@role='listbox']/li");
    private static final By SAVE_BUTTON = By.xpath("//button[normalize-space()='Save']");
    private static final By BUDGET_ADJUSTMENT_DISPLAYED = By.xpath("//span[contains(text(),'Budget Readjust')]");
    private static final By CREATE_BUDGET_RULE_BUTTON = By.xpath("//p[normalize-space()='Create Budget Rules']");
    private static final By INCREASE_BY_VALUE_INPUT_FIELD = By.xpath("//h6[contains(text(),'Increase By value')]/../..//input[@id='float-Two-decimal-input']");
    private static final By CUSTOM_BUDGET_INPUT_FIELD = By.xpath("//div[contains(@class,'MuiGrid-root MuiGrid-grid-md-12')][1]/div[2]//input[@id='float-Two-decimal-input']");
    private static final By MONTHLY_BUDGET_IN_GRAPH = By.xpath("//div[contains(normalize-space(), '$5')]");
    private static final By DAILY_ALLOCATION_VALUE = By.xpath("//div[contains(@class,'MuiGrid-root MuiGrid-item MuiGrid-grid-xs-5')]/h6");
    private static final By CREATED_BUDGET_RULE_CONFIRM_BUTTON = By.xpath("//div[@class='MuiBox-root css-138h70a']//button[normalize-space()='Confirm']");
    private static final By CONFIRM_BUTTON = By.xpath("//div[@class='MuiBox-root css-1mskdau']//button[normalize-space()='Confirm']");
    private static final By CANCEL_BUTTON = By.xpath("//div[@class='MuiBox-root css-1mskdau']//button[normalize-space()='Cancel']");
    private static final By CUSTOM_BUDJET_VALUE = By.xpath("//div[@id='ModalContent']/div[@id='stepAllocateBudgets']/div[@class='MuiGrid-root css-j72r26']//input[@id='float-Two-decimal-input']");
    private static final By DAILY_BUDGET_IN_GRAPH = By.xpath("//div[contains(normalize-space(), '$20')]");
    private static final By DAILY_BREAKDOWN = By.cssSelector("g[class*=highcharts-column-series] :first-child[class*=highcharts-point]");
    private static final By DAILY_BREAKDOWN_SPEND = By.xpath("//div[@class='ag-center-cols-container']/div/div[2]//span");
    private static final By DAILY_BREAKDOWN_EXPORT = By.xpath("//h4[contains(text(),'Daily Breakdown -')]/../../div[2]//span[normalize-space()='download']");
    private static final By SPEND_COLUMN_HEADER = By.xpath("//span[normalize-space()='Spend']");
    private static final By DOWNLOAD_SUCCESS_MESSAGE = By.xpath("//p[normalize-space()='Downloaded Successfully']");
    private static final By RESOURCE_ICON = By.xpath("//button[@id='hide-rc']");
    private static final By PACING_BAR_SPEND = By.cssSelector("g[class*=highcharts-tooltip] :nth-child(5) :first-child");
    private static final By PACING_METHOD_HORIZONTOL_SCROLL_BAR = By.xpath("//div[@class='ag-body-horizontal-scroll-viewport']");

    public BudgetManagerPage(WebDriver driver) {
        super(driver);
        findElementVisible(BUDGET_SUBMISSION);
    }

    public List<String> getAvailableOptionsInIoSegmentFilter() throws InterruptedException {
        waitForElementToBeInvisible(PROGRESS_BAR);
        return openSelectedFilterAndGetAllValues(IO_SEGMENT_FILTER, FILTER_ELEMENTS);
    }

    public void selectPlatforms(List<String> platforms) throws InterruptedException {
        click(MULTIPLATFORM_DROPDOWN);
        for (String platform : platforms) {
            click(By.xpath("//h6[text()='" + platform + "']"));
        }
        click(MULTIPLATFORM_DROPDOWN);
    }

    public void selectSinglePlatform(String UnSelectPlatform1,String UnSelectPlatform2,String UnSelectPlatform3,String UnSelectPlatform4) throws InterruptedException {
        UI_LOGGER.info("Click on Platform Button");
        click(MULTIPLATFORM_DROPDOWN);
        UI_LOGGER.info("Un Select Clients");
        click(By.xpath("//h6[text()='" + UnSelectPlatform1 + "']"));
        click(By.xpath("//h6[text()='" + UnSelectPlatform2 + "']"));
        click(By.xpath("//h6[text()='" + UnSelectPlatform3 + "']"));
        click(By.xpath("//h6[text()='" + UnSelectPlatform4 + "']"));
        click(MULTIPLATFORM_DROPDOWN);
    }

    public void clickIOSegmentButton() throws InterruptedException {
        click(IO_SEGMENT_TOGGLE_BUTTON);
    }

    public void selectIoSegmentFilterOptions(String... optionsToSelect) throws InterruptedException {
        selectItemsFromDropdownFilter(IO_SEGMENT_FILTER, Arrays.asList(optionsToSelect));
        click(APPLY_BUTTON);
        waitForElementToBeInvisible(PROGRESS_BAR);
    }

    public List<String> getIoSegmentColumnValues() {
        return getTextFromElements(findElementsVisible(IO_SEGMENT_COLUMN));
    }

    public void clearIoSegmentFilter() throws InterruptedException {
        click(CLEAR_BUTTON);
    }

    public void clickBulkUploadButton() throws InterruptedException {
        click(B_M_UPLOAD_BUTTON);
    }

    public void clickDownloadButton() throws InterruptedException {
        click(B_M_DOWNLOAD_BUTTON);
    }

    public void clickUploadButton() throws InterruptedException {
        click(BULK_UPLOAD_BUTTON);
    }

    public void clickBulkUploadConfirmButton() throws InterruptedException {
        click(CONFIRM_UPLOAD_BUTTON);
    }

    public String getPopUpText(String popUpMessage) {
        waitTextInElementToBe(POP_UP_UPLOAD_PROCESSING, popUpMessage, Duration.ofSeconds(20));
        return getTextFromElement(POP_UP_UPLOAD_PROCESSING);
    }

    public void uploadBulkFile(String relativeFilePath) throws InterruptedException {
        var fileToUpload = new File(relativeFilePath);
        uploadFile(FILE_UPLOAD_INPUT, fileToUpload.getAbsolutePath());
    }

    public void selectBudgetTracker() throws InterruptedException {
        UI_LOGGER.info("Click on budget Tracker");
        click(BUDGET_TRACKER_BUTTON);
    }

    public void ioSegmentToogleSwitchOn() throws InterruptedException {
        UI_LOGGER.info("Switch On IO Segment Toogle");
        click(IO_SEGMENT_TOGGLE_BUTTON);
    }

    public void editPacingMethod() throws InterruptedException {
        UI_LOGGER.info("Click on Edit Pacing Method Icon");
        waitForElementClickable(PACING_METHOD_EDIT_ICON);
        click(PACING_METHOD_EDIT_ICON);
        Thread.sleep(4000);
    }

    public boolean isPacingDetailPageIsDisplayed() {
        return isElementPresent(PACING_DETAILS_HEADER);
    }

    public void verifyPacingMethodDropdownOption() throws InterruptedException {
        UI_LOGGER.info("Click on Pacing Method Edit Icon");
        click(PACING_METHOD_EDIT_ICON);
        UI_LOGGER.info("Click Dropdown icon");
        click(PACING_METHOD_DROPDOWN_ICON);
        List<WebElement> pacingMethodDropdownValues = findElementsVisible(PACING_METHOD_DROPDOWN_LIST);
        for (WebElement pacingMethodDropdownValue : pacingMethodDropdownValues) {
            if (pacingMethodDropdownValue.getText().contains("None") || pacingMethodDropdownValue.getText().contains("Even Spend Allocated Daily") || pacingMethodDropdownValue.getText().contains("Spend Allocated Monthly")
                    || pacingMethodDropdownValue.getText().contains("Custom Daily") || pacingMethodDropdownValue.getText().contains("Custom Monthly")) {
                UI_LOGGER.info("Correct Dropdown values are displayed");
            } else {
                Assert.fail("Error: Incorrect Dropdown values are displayed");

            }
        }
    }

    public boolean applyPacingMethod(String pacingMethod) throws InterruptedException {
        UI_LOGGER.info("Select the Pacing Method");
        isElementVisible(By.xpath("//li[contains(text(),'" + pacingMethod + "')]"));
        click(By.xpath("//li[contains(text(),'" + pacingMethod + "')]"));
        UI_LOGGER.info("Click on Save Button");
        click(SAVE_BUTTON);
        UI_LOGGER.info("Verify Selected Pacing Method Displayed");
        return isElementPresent(By.xpath("//h4[contains(text(),'" + pacingMethod + "')]"));
    }

    public boolean validateCustomMonthlyBudgetRule() throws InterruptedException {
        UI_LOGGER.info("Enter the Custom Monthly Budget");
        click(CUSTOM_BUDGET_INPUT_FIELD);
        sendKeys(CUSTOM_BUDGET_INPUT_FIELD, "5" + Keys.TAB);
        WebElement Monthly_Allocation = findElementVisible(CUSTOM_BUDGET_INPUT_FIELD);
        Monthly_Allocation.getAttribute("value");
        UI_LOGGER.info(Monthly_Allocation.getAttribute("value"));
        UI_LOGGER.info("Click on Confirm");
        click(CONFIRM_BUTTON);
        Thread.sleep(2000);
        return isElementPresent(MONTHLY_BUDGET_IN_GRAPH);
    }

    public boolean verifyAppliedPacingMethodDisplayed(String pacingMethod) throws InterruptedException {
        UI_LOGGER.info("Click on Pacing Method Edit Icon");
        click(PACING_METHOD_EDIT_ICON);
        UI_LOGGER.info("Click Dropdown icon");
        click(PACING_METHOD_DROPDOWN_ICON);
        UI_LOGGER.info("Select the Pacing Method");
        waitForElementClickable(By.xpath("//li[contains(text(),'"+ pacingMethod +"')]"));
        click(By.xpath("//li[contains(text(),'"+ pacingMethod +"')]"));
        UI_LOGGER.info("Click on Save Button");
        click(SAVE_BUTTON);
        Thread.sleep(2000);
        UI_LOGGER.info("Verify Selected Pacing Method '"+ pacingMethod +"' ");
        return isElementPresent(By.xpath("//h4[contains(text(),'"+ pacingMethod +"')]"));
    }

    public boolean verifyUpdatedPacingMethodColumn(String pacingMethod) {
        scrollElementHorizontally(1000, PACING_METHOD_HORIZONTOL_SCROLL_BAR);
        return isElementVisible(By.xpath("//div[@row-id=0]//p[contains(text(),'"+ pacingMethod +"')]"));
    }

    public void verifyCustomPacingMethod(String pacingMethod) throws InterruptedException {
        UI_LOGGER.info("Click on Pacing Method Edit Icon");
        click(PACING_METHOD_EDIT_ICON);
        UI_LOGGER.info("Click Dropdown icon");
        click(PACING_METHOD_DROPDOWN_ICON);
        Thread.sleep(3000);
        UI_LOGGER.info("Select the Pacing Method");
        isElementVisible(By.xpath("//li[contains(text(),'" + pacingMethod + "')]"));
        click(By.xpath("//li[contains(text(),'" + pacingMethod + "')]"));
        Thread.sleep(3000);
    }

    public boolean verifyCreateBudgetRuleButtonDisplayed() {
        return isElementVisible(CREATE_BUDGET_RULE_BUTTON);
    }

    public boolean verifyCancelButtonDisplayed() {
        return isElementVisible(CANCEL_BUTTON);
    }

    public boolean verifyBudgetAdjustmentDisplayed() {
        return isElementVisible(BUDGET_ADJUSTMENT_DISPLAYED);
    }

    public void validateCreateBudgetRule(String pacingMethod) throws InterruptedException {
        UI_LOGGER.info("Click on the Create Budget rule Button");
        click(CREATE_BUDGET_RULE_BUTTON);
        isElementVisible((INCREASE_BY_VALUE_INPUT_FIELD));
        setText(INCREASE_BY_VALUE_INPUT_FIELD,"20");
        //wait added to extract the Daily allocation Value
        Thread.sleep(2000);
        UI_LOGGER.info("Extract the Daily Allocation");
        WebElement Daily_Allocation = findElementVisible(DAILY_ALLOCATION_VALUE);
        String DailyAllocationBudget = Daily_Allocation.getText().replace("$", "");
        String DailyAllocationBudgetWithout$ = DailyAllocationBudget.replaceAll("\\s", "");
        UI_LOGGER.info("Click on Confirm");
        click(CREATED_BUDGET_RULE_CONFIRM_BUTTON);
        Thread.sleep(3000);
        UI_LOGGER.info("Click on Pacing Method Edit Icon");
        click(PACING_METHOD_EDIT_ICON);
        UI_LOGGER.info("Click Dropdown icon");
        click(PACING_METHOD_DROPDOWN_ICON);
        UI_LOGGER.info("Select the Pacing Method");
        click(By.xpath("//li[contains(text(),'" + pacingMethod + "')]"));
        Thread.sleep(2000);
        List<WebElement> Custom_Allocated_Budget = findElementsPresent(CUSTOM_BUDJET_VALUE);
        List<String> valuesOfAllocatedBudget = new ArrayList<>();
        for (WebElement pacingMethodValue : Custom_Allocated_Budget) {
            String text = pacingMethodValue.getAttribute("value");
            double textWithtwodecimal = Double.parseDouble(text);
            textWithtwodecimal = Math.round(textWithtwodecimal * 100.00) / 100.00;
            String roundedNumber = String.valueOf(textWithtwodecimal);
            valuesOfAllocatedBudget.add(roundedNumber);
            UI_LOGGER.info(roundedNumber);
        }
        Assert.assertTrue(valuesOfAllocatedBudget.contains(DailyAllocationBudgetWithout$));
    }

    public boolean validateCustomDailyBudgetRule() throws InterruptedException {
        UI_LOGGER.info("Enter the Custom Daily Budget");
        click(CUSTOM_BUDGET_INPUT_FIELD);
        sendKeys(CUSTOM_BUDGET_INPUT_FIELD, "20" + Keys.TAB);
        WebElement Monthly_Allocation = findElementVisible(CUSTOM_BUDGET_INPUT_FIELD);
        Monthly_Allocation.getAttribute("value");
        UI_LOGGER.info(Monthly_Allocation.getAttribute("value"));
        UI_LOGGER.info("Click on Confirm");
        click(CONFIRM_BUTTON);
        Thread.sleep(2000);
        return isElementPresent(DAILY_BUDGET_IN_GRAPH);
    }

    public void Daily_Breakdown_Functionality() throws InterruptedException {
        UI_LOGGER.info("Hover over Daily Breakdown");
        Thread.sleep(3000);
        hoverOverElement(DAILY_BREAKDOWN);
        UI_LOGGER.info("Click on Daily Breakdown");
        click(DAILY_BREAKDOWN);
        WebElement breakdownSpend = findElementVisible(DAILY_BREAKDOWN_SPEND);
        String breakdownSpendExtractText = breakdownSpend.getText();
        WebElement pacingBarSpend = findElementVisible(PACING_BAR_SPEND);
        String pacingBarSpendExtractText = pacingBarSpend.getText();
        Thread.sleep(2000);
        Assert.assertTrue(pacingBarSpendExtractText.contains(breakdownSpendExtractText), "Pacing bar Spend and Daily Breakdown Spend are not matched");
    }

    public boolean isExportButtonDisplayed() {
        return isElementVisible(DAILY_BREAKDOWN_EXPORT);
    }

    public boolean isSpendColumnDisplayed() {
        return isElementVisible(SPEND_COLUMN_HEADER);
    }

    public void isDownloadSuccessMessageDisplayed() {
        isElementVisible(DOWNLOAD_SUCCESS_MESSAGE);
    }

    public void verifyDailyBreakdownExportFunctionality() throws InterruptedException {
        UI_LOGGER.info("Close Resource Center");
        click(RESOURCE_ICON);
        UI_LOGGER.info("Click on Export");
        click(DAILY_BREAKDOWN_EXPORT);
        isDownloadSuccessMessageDisplayed();
        Thread.sleep(3000);
    }

    public boolean verifyCSVHasCorrectColumns(String path) {
        List<String> columnsFromExcel = CsvUtility.getAllColumnNames(path);
        List<String> expectedAddedColumns = List.of("date", "spend");
        UI_LOGGER.info("Columns from excel are " + columnsFromExcel + " which should contain " + expectedAddedColumns);

        return columnsFromExcel.containsAll(expectedAddedColumns);
    }

    public void selectSpecificDate() throws InterruptedException {
        String startDateString = DateUtility.getYesterday();
        int startDate = DateUtility.convertDateToInt(startDateString);
        String endDateString = DateUtility.getLastDayOfThisMonth();
        int endDate = DateUtility.convertDateToInt(endDateString);

        String monthToSelect = DateUtility.retrieveMonth(startDate);
        int yearToSelect = DateUtility.extractYear(startDate);
        int startDayToSelect = DateUtility.extractDay(startDate);
        int endDayToSelect = DateUtility.extractDay(endDate);

        dateAndIntervalPickerPage.selectCustomDateRangeWithYear(String.valueOf(yearToSelect), monthToSelect, startDayToSelect, endDayToSelect);
    }
}