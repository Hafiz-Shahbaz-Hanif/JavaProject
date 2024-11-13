package com.DC.pageobjects.adc.execute.mediaManagement;

import com.DC.db.execute.MediaManagementQueries;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.CommonFeatures;
import com.DC.utilities.SQLUtility;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

public class EnsemblePage extends NetNewNavigationMenu {

    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public DCFilters dcFilters;
    public CommonFeatures commonFeatures;

    private static final By ENSEMBLE_HEADER = By.xpath("//span[text()='Ensemble']");
    private static final By ENSEMBLE_APPLY_BUTTON = By.xpath("//button[@title='Apply']");
    private static final By BULK_EDIT_BUTTON = By.xpath("//button[@title='Edit Status']");
    private static final By ADJUST_BID_CANCEL_BUTTON = By.xpath("//div[@class='modal-footer']/button[text()='Cancel']");
    private static final By ADJUST_BID_UPDATE_BUTTON = By.xpath("//div[@class='modal-footer']//span[text()='Update']");
    private static final By BID_VALUE_BOX = By.id("bid-value");
    private static final By ADJUST_BID_HEADER = By.xpath("//span[contains(text(),'Adjust Bid')]");
    private static final By ENSEMBLE_DAYPARTING_MANAGER_BUTTON = By.xpath("//button[@title='Ensemble Dayparting Manager']");
    private static final By CREATE_BUTTON = By.xpath("//button[@title='Create Configuration']");
    private static final By COPY_CONFIGURATION_BUTTON = By.xpath("//button[@title='Copy Configuration']");
    private static final By EDIT_CONFIGURATION_BUTTON = By.xpath("//button[@title='Edit Configuration']");
    private static final By UPLOAD_BUTTON = By.xpath("//button[@class='btn btn-xs btn-icon']/i[@class='fa fa-upload']");
    private static final By EXPORT_BUTTON = By.xpath("//button[@title='Export to CSV']");
    private static final By DATE_RANGE_PICKER = By.id("ddlDateRange");
    private static final By CREATE_CONFIGURATION_HEADER = By.xpath("//span[text()='Create Configuration']");
    private static final By QUERY_SELECTION = By.xpath("//form[not(@hidden)]//input[@name='queryText']");
    private static final By DEFAULT_BID_SELECTION = By.name("defaultBid");
    private static final By SLOT_DETAIL_SELECTION = By.xpath("//span[text()='Click to change slots +']");
    private static final By ASIN_PRIORITY_SELECTION = By.id("asinSelectize-selectized");
    private static final By SEND_FOR_APPROVAL_BUTTON = By.xpath("//span[text()='Send for Approval']");
    private static final By PRESSURE_SELECTION = By.xpath("//form[not(@hidden)]//input[contains(@placeholder,'Pressure')]");
    private static final By BID_FLOOR_SELECTION = By.xpath("//form[not(@hidden)]//input[contains(@placeholder,'Bid Floor')]");
    private static final By BID_CEILING_SELECTION = By.xpath("//form[not(@hidden)]//input[contains(@placeholder,'Bid Ceiling')]");
    private static final By DAYPART_SELECTION = By.xpath("//form[not(@hidden)]//input[contains(@placeholder,'Daypart')]");
    private static final By PRESSURE_DROPDOWN_OPTIONS = By.xpath("//div[@class='selectize-dropdown-content']/div[contains(@data-value,'PRESSURE')]");
    private static final By DAYPART_DROPDOWN_OPTIONS = By.xpath("//form[not(@hidden)]//app-selectize[contains(@placeholder,'Daypart')]//div[@class='selectize-dropdown-content']//div[contains(@class,'option')]");
    private static final By PLACEMENT_SELECTION = By.name("placement");
    private static final By SLOT_RANK_SELECTION = By.name("slotRank");
    private static final By SEGMENTATION_TYPE_SELECTION = By.xpath("//app-selectize[@placeholder='Segmentation Type']");
    private static final By SEGMENTATION_VALUE_SELECTION = By.xpath("//app-selectize[@placeholder='Segmentation Value']//input");
    private static final By PLACEMENT_DROPDOWN_OPTIONS = By.xpath("//app-selectize[@name='placement']//div[@class='selectize-dropdown-content']/div[contains(@class,'option')]");
    private static final By SEGMENTATION_TYPE_DROPDOWN_OPTIONS = By.xpath("//app-selectize[@placeholder='Segmentation Type']//div[@class='selectize-dropdown-content']/div");

    private final Duration TIMEOUT = Duration.ofSeconds(2);

    public EnsemblePage(WebDriver driver) {
        super(driver);
        findElementVisible(ENSEMBLE_HEADER);
        dcFilters = new DCFilters(driver);
    }

    public boolean isEnsembleScreenDisplayed() {
        return isElementVisible(ENSEMBLE_HEADER);
    }

    public boolean checkEnsembleStatuses(String statusToCheck) throws InterruptedException {
        ArrayList<Map<String, String>> statusesAndTypesInTable = getEnsembleConfiguration(statusToCheck);
        UI_LOGGER.info("statusesAndTypesInTable: " + statusesAndTypesInTable + " statusToCheck: " + statusToCheck);
        return statusesAndTypesInTable.stream().allMatch(map -> map.get("status").equals(statusToCheck));
    }

    public Map<String, List<String>> getStatusesFromKeywordFlyout() {
        By rowLocator = By.xpath("(//div[@class='ag-center-cols-container'])[2]/div[@row-index]");
        int numberOfRows = getElementCount(rowLocator);
        Map<String, List<String>> statusAsinValues = new HashMap<>();

        for (int i = 0; i < numberOfRows; i++) {
            By statusLocator = By.xpath("(//div[@class='ag-center-cols-container'])[2]/div[@row-id='" + i + "']/div[@col-id='status']");
            By asinLocator = By.xpath("(//div[@class='ag-center-cols-container'])[2]/div[@row-id='" + i + "']/div[@col-id='asin']");

            try {
                String status = getTextFromElement(statusLocator);
                String asin = getTextFromElement(asinLocator);

                if (statusAsinValues.containsKey(status)) {
                    statusAsinValues.get(status).add(asin);
                } else {
                    List<String> asinList = new ArrayList<>();
                    asinList.add(asin);
                    statusAsinValues.put(status, asinList);
                }
            } catch (StaleElementReferenceException e) {
                UI_LOGGER.info("Stale element exception occurred. Retrying...");
                String status = getTextFromElement(statusLocator);
                String asin = getTextFromElement(asinLocator);

                if (statusAsinValues.containsKey(status)) {
                    statusAsinValues.get(status).add(asin);
                } else {
                    List<String> asinList = new ArrayList<>();
                    asinList.add(asin);
                    statusAsinValues.put(status, asinList);
                }
            }
        }
        UI_LOGGER.info("Statuses and ASINs in flyout: " + statusAsinValues);
        return statusAsinValues;
    }

    public ArrayList<Map<String, String>> getEnsembleConfiguration(String statusToCheck) throws InterruptedException {
        dcFilters.selectStatus(statusToCheck);
        waitForElementClickable(ENSEMBLE_APPLY_BUTTON);
        By tableLocator = By.xpath("//div[contains(@class,'ag-body-viewport')]");
        By rowsLocator = By.xpath("//div[@class='ag-center-cols-container']/div");
        Map<String, Map<String, String>> statusesAndTypesInTable = new LinkedHashMap<>();
        scrollToCenterIfTableScrollable(tableLocator);
        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableLocator, 15);
        do {
            currentPosition = getVerticalScrollPosition(tableLocator);
            var rowsDisplayed = findElementsVisible(rowsLocator, TIMEOUT);
            for (var row : rowsDisplayed) {
                try {
                    getData(row, statusesAndTypesInTable, statusToCheck);
                } catch (StaleElementReferenceException | org.openqa.selenium.NoSuchElementException ex) {
                    break;
                }
            }
            scrollElementVertically(pixelsToScroll, tableLocator);
        } while (currentPosition != getVerticalScrollPosition(tableLocator));
        var sortedMap = new TreeMap<>(statusesAndTypesInTable);
        return new ArrayList<>(sortedMap.values());
    }

    public void getData(WebElement row, Map<String, Map<String, String>> statusesAndTypesInTable, String statusToCheck) {
        var rowId = row.getAttribute("row-id");
        boolean rowAlreadyAdded = statusesAndTypesInTable.containsKey(rowId);
        By statusLocator = By.xpath("./div[@col-id='status']");
        By keywordFlyoutHeaderLocator = By.xpath("//span[@role='columnheader' and text()='Bid Details']");
        By typeLocator = By.xpath("./div[@col-id='configurationType']");
        By keywordExpandLocator = By.xpath(".//div[@col-id='configurationType']//span[@ref='eContracted']");
        By keywordCollapseLocator = By.xpath(".//div[@col-id='configurationType']//span[@ref='eExpanded']");
        if (!rowAlreadyAdded) {
            String status;
            String type;
            try {
                status = row.findElement(statusLocator).getText();
                type = row.findElement(typeLocator).getText();

                if (type.equalsIgnoreCase("Keyword")) {
                    if (!isElementVisible(keywordFlyoutHeaderLocator)) {
                        row.findElement(keywordExpandLocator).click();
                        findElementVisible(keywordFlyoutHeaderLocator);
                        UI_LOGGER.info("Keyword flyout is expanded");
                    }
                    Map<String, List<String>> statusesAndTypesInFlyout = getStatusesFromKeywordFlyout();
                    if (statusToCheck.equalsIgnoreCase("Enabled")) {
                        for (Map.Entry<String, List<String>> entry : statusesAndTypesInFlyout.entrySet()) {
                            if (entry.getKey().equalsIgnoreCase("Enabled")) {
                                break;
                            }
                        }
                    } else {
                        for (Map.Entry<String, List<String>> entry : statusesAndTypesInFlyout.entrySet()) {
                            if (!entry.getKey().equalsIgnoreCase("Disabled")) {
                                Assert.fail("Keyword status is not disabled");
                            }
                        }
                    }
                    if (isElementVisible(keywordFlyoutHeaderLocator)) {
                        row.findElement(keywordCollapseLocator).click();
                        waitForElementToBeInvisible(keywordFlyoutHeaderLocator);
                        UI_LOGGER.info("Keyword flyout is collapsed");
                    }
                }
                statusesAndTypesInTable.put(rowId, Map.of("status", status, "type", type));
            } catch (StaleElementReferenceException ignored) {
                UI_LOGGER.info("Stale element exception occurred INSIDE METHOD. Retrying...");
            }
        }
    }

    public void selectCheckboxToEdit(int rowIndexToCheck) {
        List<WebElement> rows = findElementsVisible(By.xpath("//div[@ref='eCenterContainer']/div[@row-id]"), TIMEOUT);
        WebElement rowToCheck = rows.get(rowIndexToCheck);
        WebElement checkbox = rowToCheck.findElement(By.xpath(".//span[@class='ag-icon ag-icon-checkbox-unchecked']"));
        checkbox.click();
    }

    public void selectBulkEditFunction(String function) throws InterruptedException {
        click(BULK_EDIT_BUTTON);
        click(By.xpath("//div[@id='editDropdownMenu']/a[text()='" + function + "']"));
    }

    public String getBidValue(int rowIndexToCheck, String bidValueID) throws InterruptedException {
        String bidValue;
        Thread.sleep(2000);
        try {
            List<WebElement> rows = findElementsVisible(By.xpath("//div[@ref='eCenterContainer']/div[@row-id]"), TIMEOUT);
            WebElement rowToCheck = rows.get(rowIndexToCheck);
            WebElement bid = rowToCheck.findElement(By.xpath(".//div[@col-id='" + bidValueID + "']//span[@role='gridcell']/span"));
            bidValue = bid.getText();
        } catch (StaleElementReferenceException e) {
            UI_LOGGER.info("Stale element exception occurred INSIDE METHOD. Retrying...");
            List<WebElement> rows = findElementsVisible(By.xpath("//div[@ref='eCenterContainer']/div[@row-id]"), TIMEOUT);
            WebElement rowToCheck = rows.get(rowIndexToCheck);
            WebElement bid = rowToCheck.findElement(By.xpath(".//div[@col-id='" + bidValueID + "']//span[@role='gridcell']/span"));
            bidValue = bid.getText();
        }
        return bidValue;
    }

    public void editBidValue(double bidValue) throws InterruptedException {
        sendKeys(BID_VALUE_BOX, String.valueOf(bidValue));
        waitForElementClickable(ADJUST_BID_UPDATE_BUTTON);
        click(ADJUST_BID_UPDATE_BUTTON);
        waitForElementToBeInvisible(ADJUST_BID_HEADER);
    }

    public String getSegmentationFromRow(int rowIndexToCheck) throws InterruptedException {
        String segmentationValue;
        Thread.sleep(2000);
        try {
            List<WebElement> rows = findElementsVisible(By.xpath("//div[@ref='eCenterContainer']/div[@row-id]"), TIMEOUT);
            WebElement rowToCheck = rows.get(rowIndexToCheck);
            WebElement segmentationElement = rowToCheck.findElement(By.xpath(".//div[@col-id='segmentationValue']//span[@role='gridcell']/span"));
            segmentationValue = segmentationElement.getText();
        } catch (StaleElementReferenceException e) {
            UI_LOGGER.info("Stale element exception occurred INSIDE METHOD. Retrying...");
            List<WebElement> rows = findElementsVisible(By.xpath("//div[@ref='eCenterContainer']/div[@row-id]"), TIMEOUT);
            WebElement rowToCheck = rows.get(rowIndexToCheck);
            WebElement segmentationElement = rowToCheck.findElement(By.xpath(".//div[@col-id='segmentationValue']//span[@role='gridcell']/span"));
            segmentationValue = segmentationElement.getText();
        }
        return segmentationValue;
    }

    public int findRowWithSegmentation(String desiredSegmentation) {
        String segmentationValue;
        int rowIndex = 0;
        try {
            List<WebElement> rows = findElementsVisible(By.xpath("//div[@ref='eCenterContainer']/div[@row-id]"), TIMEOUT);
            for (WebElement row : rows) {
                WebElement segmentationElement = row.findElement(By.xpath(".//div[@col-id='segmentationValue']//span[@role='gridcell']/span"));
                segmentationValue = segmentationElement.getText();
                if (segmentationValue.equalsIgnoreCase(desiredSegmentation)) {
                    break;
                }
                rowIndex++;
            }
        } catch (StaleElementReferenceException e) {
            UI_LOGGER.info("Stale element exception occurred INSIDE METHOD. Retrying...");
            List<WebElement> rows = findElementsVisible(By.xpath("//div[@ref='eCenterContainer']/div[@row-id]"), TIMEOUT);
            for (WebElement row : rows) {
                WebElement segmentationElement = row.findElement(By.xpath(".//div[@col-id='segmentationValue']//span[@role='gridcell']/span"));
                segmentationValue = segmentationElement.getText();
                if (segmentationValue.equalsIgnoreCase(desiredSegmentation)) {
                    break;
                }
                rowIndex++;
            }
        }
        return rowIndex;
    }

    public void selectRowToEdit(int rowIndexToCheck, String function) throws InterruptedException {
        selectCheckboxToEdit(rowIndexToCheck);
        selectBulkEditFunction(function);
        findElementVisible(ADJUST_BID_HEADER);
    }

    public boolean isCancelButtonWorking() throws InterruptedException {
        click(ADJUST_BID_CANCEL_BUTTON);
        waitForElementToBeInvisible(ADJUST_BID_HEADER);
        return !isElementVisible(ADJUST_BID_HEADER);
    }

    public boolean verifyPresenceOfButtons() {
        if (isElementVisible(BULK_EDIT_BUTTON) && isElementVisible(ENSEMBLE_DAYPARTING_MANAGER_BUTTON) && isElementVisible(CREATE_BUTTON) && isElementVisible(COPY_CONFIGURATION_BUTTON) && isElementVisible(EDIT_CONFIGURATION_BUTTON) && isElementVisible(UPLOAD_BUTTON)) {
            isElementVisible(EXPORT_BUTTON);
            return true;
        }
        return false;
    }

    public String getDefaultDateRange() {
        return getTextFromElement(DATE_RANGE_PICKER);
    }

    public boolean isDateRangeEditable() {
        WebElement dateRange = findElementVisible(By.xpath("//div[@id='ddlDateRange']/span"));
        return dateRange.getAttribute("class").contains("disabled-date-picker");
    }

    public void createConfig(Map<String, String> configValues) throws InterruptedException {
        String slotOrKeywordLevel = configValues.get("slotOrKeywordLevel");
        String pressureValue = configValues.get("pressureValue");
        String bidFloorValue = configValues.get("bidFloorValue");
        String bidCeilingValue = configValues.get("bidCeilingValue");
        String keywordFromDB = configValues.get("keywordFromDB");
        String asinFromDB = configValues.get("asinFromDB");
        String defaultBid = configValues.get("defaultBid");
        String slotDetail = configValues.get("slotDetail");
        String slotRank = configValues.get("slotRank");
        String segmentationType = configValues.get("segmentationType");
        click(CREATE_BUTTON);
        findElementVisible(CREATE_CONFIGURATION_HEADER);
        WebElement slotOrKeywordToggle = findElementVisible(By.xpath("//div[@class='mat-button-toggle-label-content' and text()='" + slotOrKeywordLevel + "']"));
        slotOrKeywordToggle.click();
        Thread.sleep(1000);

        sendKeys(QUERY_SELECTION, keywordFromDB);
        if (slotOrKeywordLevel.equalsIgnoreCase("Keyword")) {
            sendKeys(DEFAULT_BID_SELECTION, defaultBid);
            selectSlotDetail(slotDetail);
            selectPressureAndBids(pressureValue, bidFloorValue, bidCeilingValue);
            sendKeys(ASIN_PRIORITY_SELECTION, asinFromDB);
            selectAsin(asinFromDB);
        } else {
            click(PLACEMENT_SELECTION);
            click(PLACEMENT_DROPDOWN_OPTIONS);
            sendKeys(SLOT_RANK_SELECTION, slotRank);
            selectSegmentationType(segmentationType);
            Thread.sleep(1000);
            sendKeys(SEGMENTATION_VALUE_SELECTION, asinFromDB);
            selectSegmentationValue(asinFromDB);
            selectPressureAndBids(pressureValue, bidFloorValue, bidCeilingValue);
        }

        click(SEND_FOR_APPROVAL_BUTTON);
        findElementVisible(ENSEMBLE_HEADER);
    }

    public Map<String, String> getKeywordsAndAsinsEligibleForCreation(int businessUnitId) throws SQLException {
        Map<String, String> keywordsAndAsins = new HashMap<>();
        SQLUtility.connectToServer();
        ResultSet resultSet = SQLUtility.executeQuery(MediaManagementQueries.getKeywordsAndAsinsEligibleForCreation(businessUnitId));
        while (resultSet.next()) {
            keywordsAndAsins.put(resultSet.getString("keyword"), resultSet.getString("ASIN"));
            break;
        }
        SQLUtility.closeConnections();
        UI_LOGGER.info("Keywords and ASINs eligible for creation: " + keywordsAndAsins);
        return keywordsAndAsins;
    }

    public void selectSlotDetail(String slotDetail) throws InterruptedException {
        click(SLOT_DETAIL_SELECTION);
        click(By.xpath("//mat-option/span[text()='" + slotDetail + "']"));

        // workaround until devs fix UX issue
        By tempSlotButton = By.id("slotSelect");
        click(tempSlotButton);

        By slotHeader = By.xpath("//h5[@class='configSectionHeader' and text()='" + slotDetail + "']");
        findElementVisible(slotHeader);
    }

    public void selectPressureAndBids(String pressureValue, String bidFloorValue, String bidCeilingValue) throws InterruptedException {
        click(PRESSURE_SELECTION);

        for (WebElement pressureOption : findElementsVisible(PRESSURE_DROPDOWN_OPTIONS, TIMEOUT)) {
            if (pressureOption.getText().equalsIgnoreCase(pressureValue)) {
                pressureOption.click();
                break;
            }
        }

        sendKeys(BID_FLOOR_SELECTION, bidFloorValue);
        sendKeys(BID_CEILING_SELECTION, bidCeilingValue);
    }

    public void selectAsin(String asinPriority) {
        List<WebElement> asinOptions = findElementsVisible(By.xpath("//app-selectize[@id='asinSelectize']//div[@class='option']"), TIMEOUT);
        for (WebElement asinOption : asinOptions) {
            if (asinOption.getText().equalsIgnoreCase(asinPriority)) {
                asinOption.click();
                break;
            }
        }
    }

    public void selectSegmentationType(String segmentationType) throws InterruptedException {
        click(SEGMENTATION_TYPE_SELECTION);
        List<WebElement> segmentationTypeOptions = findElementsVisible(SEGMENTATION_TYPE_DROPDOWN_OPTIONS, TIMEOUT);
        for (WebElement segmentationTypeOption : segmentationTypeOptions) {
            if (segmentationTypeOption.getText().equalsIgnoreCase(segmentationType)) {
                segmentationTypeOption.click();
                break;
            }
        }
    }

    public void selectSegmentationValue(String segmentationValue) {
        List<WebElement> segmentationValueOptions = findElementsVisible(By.xpath("//app-selectize[@name='segmentationValues']/div//div[@class='selectize-dropdown-content']/div"), TIMEOUT);
        for (WebElement segmentationValueOption : segmentationValueOptions) {
            if (segmentationValueOption.getText().equalsIgnoreCase(segmentationValue)) {
                segmentationValueOption.click();
                break;
            }
        }
    }

    public boolean isEnsembleCreated(String query, String bidFloor, String bidCeiling) throws InterruptedException, SQLException {
        dcFilters.enterQuery(query);
        waitForElementClickable(ENSEMBLE_APPLY_BUTTON);
        List<String> configDetailsFromDB = getCreatedConfigFromDB(query, bidFloor, bidCeiling);
        List<String> configDetailsFromUI = getCreatedConfigFromUI(query, bidFloor, bidCeiling);
        return configDetailsFromDB.equals(configDetailsFromUI);
    }

    public List<String> getCreatedConfigFromUI(String query, String bidFloor, String bidCeiling) {
        List<String> configDetailsFromUI = new ArrayList<>();
        By rowsLocator = By.xpath("//div[@class='ag-center-cols-container']/div");
        List<WebElement> rows = findElementsVisible(rowsLocator, TIMEOUT);
        for (WebElement row : rows) {
            WebElement queryElement = row.findElement(By.xpath(".//div[@col-id='query']"));
            WebElement bidFloorElement = row.findElement(By.xpath(".//div[@col-id='minimumBid']"));
            WebElement bidCeilingElement = row.findElement(By.xpath(".//div[@col-id='bidCeiling']"));
            if (queryElement.getText().equalsIgnoreCase(query) && bidFloorElement.getText().replace("$", "").equalsIgnoreCase(bidFloor) && bidCeilingElement.getText().replace("$", "").equalsIgnoreCase(bidCeiling)) {
                configDetailsFromUI.add(queryElement.getText());
                configDetailsFromUI.add(bidFloorElement.getText().replace("$", ""));
                configDetailsFromUI.add(bidCeilingElement.getText().replace("$", ""));
                break;
            }
        }
        UI_LOGGER.info("Elements from config created in UI: " + configDetailsFromUI);
        return configDetailsFromUI;
    }

    public List<String> getCreatedConfigFromDB(String queryText, String bidFloor, String bidCeiling) throws SQLException {
        List<String> configDetailsFromDB = new ArrayList<>();
        SQLUtility.connectToServer();
        ResultSet resultSet = SQLUtility.executeQuery(MediaManagementQueries.getCreatedConfig(queryText, bidFloor, bidCeiling));

        while (resultSet.next()) {
            configDetailsFromDB.add(resultSet.getString("QUERY_TEXT"));
            configDetailsFromDB.add(resultSet.getString("MIN_CPC"));
            configDetailsFromDB.add(resultSet.getString("BID_CEILING"));
            break;
        }
        SQLUtility.closeConnections();
        UI_LOGGER.info("Elements from config created in DB: " + configDetailsFromDB);
        return configDetailsFromDB;
    }
}