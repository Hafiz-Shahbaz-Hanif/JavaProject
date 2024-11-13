package com.DC.pageobjects.adc.analyze.marketingCloudAnalytics;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.XLUtils.XLUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LongTermValuePage extends NetNewNavigationMenu {

    private static final By APPLY_BUTTON = By.xpath("//button[text()='Apply']");
    private static final By LOYALTY_CHECKBOX = By.xpath("//span[contains(text(),'Loyalty')]");
    private static final By NTB_CHECKBOX = By.xpath("//span[contains(text(),'NTB')]");
    private static final By PERCENTAGE_RETURN_RADIO_BUTTON = By.xpath("//input[@value='Percentage Return']");
    private static final By CUSTOMER_VALUE_RADIO_BUTTON = By.xpath("//input[@value='Customer Value']");
    private static final By DOWNLOAD_BUTTON = By.xpath("//button[contains(text(),'Download')]");
    private static final By FIRST_COLUMN = By.xpath("//div[@col-id='periodDate']");
    private static final By TABLE_HEADER = By.xpath("//div[@class='ag-header-cell-label']");
    private static final By TABLE_SCROLL_BAR = By.xpath("//div[@class='ag-body-horizontal-scroll-viewport']");
    private static final By FILTER_COLLAPSE_ICON = By.xpath("//button[@aria-label='filter-collapse']");

    public LongTermValuePage(WebDriver driver) {
        super(driver);
    }

    public void clickLoyaltyCheckbox() throws InterruptedException {
        click(LOYALTY_CHECKBOX);
    }

    public void clickApplyButton() throws InterruptedException {
        click(APPLY_BUTTON);
    }

    public void clickCustomerValueRadioButton() throws InterruptedException {
        click(CUSTOMER_VALUE_RADIO_BUTTON);
    }

    public void clickPercentageReturnRadioButton() throws InterruptedException {
        click(PERCENTAGE_RETURN_RADIO_BUTTON);
    }

    public void selectOptionFromDownloadButton(String optionToSelect) throws InterruptedException {
        scrollIntoView(DOWNLOAD_BUTTON);
        selectItemFromDropdownForListElements(DOWNLOAD_BUTTON, optionToSelect);
    }

    public List<String> loadTableHeaderValues() {
        return getTextFromElementsMilliseconds(TABLE_HEADER);
    }

    public List<String> loadColumnValues(String columnName) {
        return getTextFromElementsMilliseconds(By.xpath("//div[@col-id='" + columnName + "']"));
    }

    public List<List<String>> loadTableData(boolean formatFirstRowOnly) throws InterruptedException {
        var tableData = new ArrayList<List<String>>();
        tableData.add(getTextFromElementsMilliseconds(FIRST_COLUMN));
        scrollIntoView(DOWNLOAD_BUTTON);
        click(FILTER_COLLAPSE_ICON);
        Thread.sleep(1000);
        var headerValues = loadTableHeaderValues();
        for (int i = 1; i < headerValues.size(); i++) {
            tableData.add(loadColumnValues(headerValues.get(i)));
        }
        for (int i = 1; i < tableData.size(); i++) {
            List<String> columnData = tableData.get(i);
            if (formatFirstRowOnly) {
                stripNonNumericValues(columnData, 2);
            } else {
                stripNonNumericValues(columnData, columnData.size());
            }
        }
        click(FILTER_COLLAPSE_ICON);
        return tableData;
    }

    private void stripNonNumericValues(List<String> columnData, int lastColumnIndexToStrip) {
        for (int j = 1; j < Math.min(columnData.size(), lastColumnIndexToStrip); j++) {
            var strippedValue = columnData.get(j).replaceAll("[^0-9.]", "");
            columnData.set(j, strippedValue);
        }
    }

    public List<List<String>> loadExcelData(String filePath, boolean formatToPercent) throws IOException {
        List<List<String>> sheetData = XLUtils.getSheetColumnData(filePath, false);

        int rowSize = sheetData.size();
        for (int i = 1; i < rowSize; i++) {
            List<String> columnData = sheetData.get(i);
            int columnSize = columnData.size();
            for (int j = 2; j < columnSize; j++) {
                var decimalValue = SharedMethods.convertToNumber(columnData.get(j));
                if (decimalValue != null) {
                    if (formatToPercent) {
                        int percentNoDecimals = (int) Math.round(decimalValue * 100);
                        columnData.set(j, percentNoDecimals + "%");
                    } else {
                        columnData.set(j, String.format("%.2f", decimalValue));
                    }
                }
            }
        }
        return sheetData;
    }

    public boolean isTablePresent(String category) {
        return isElementPresent(By.xpath("//h4[contains(text(),'" + category + "')]"));
    }

    public void clickNTBCheckbox() throws InterruptedException {
        click(NTB_CHECKBOX);
    }
}