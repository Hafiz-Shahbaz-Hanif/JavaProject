package com.DC.uitests.adc.execute.mediaManagement;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.mediaManagement.EnsemblePage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EnsembleTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private EnsemblePage ensemblePage;
    private AppHomepage appHomepage;

    @BeforeMethod
    public void setupTestMethodForNetNew(final ITestContext testContext, ITestResult tr) throws InterruptedException {
        testMethodName.set(tr.getMethod().getMethodName());
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName + " ***************");
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        appHomepage = new AppHomepage(driver);
        appHomepage.selectBU("Performance Health East");
        appHomepage.clickOnSection("Execute");
        appHomepage.clickLink("Ensemble");

        ensemblePage = new EnsemblePage(driver);
    }

    @AfterMethod()
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify Display of Ensemble Screen")
    public void MAU_Ensemble_EnsembleScreenIsDisplayed() {
        Assert.assertTrue(ensemblePage.isEnsembleScreenDisplayed(), "Ensemble Screen is not displayed");
        String currentUrl = ensemblePage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("ensemble"), "Page url does not contain 'ensemble'");
    }

    @Test(description = "C234949 - Verify Filtering by Status for Keyword Level")
    public void MAU_Ensemble_FilteringByStatusForKeywordLevel() throws InterruptedException {
        String statusToCheck = "ENABLED";
        Assert.assertTrue(ensemblePage.checkEnsembleStatuses(statusToCheck), "Status that is not " + statusToCheck + " is displayed in the grid.");
        LOGGER.info("Test with status " + statusToCheck + " passed. Now testing with status 'Disabled'");

        statusToCheck = "DISABLED";
        Assert.assertTrue(ensemblePage.checkEnsembleStatuses(statusToCheck), "Status that is not " + statusToCheck + " is displayed in the grid.");

        LOGGER.info("Test with status " + statusToCheck + " passed");
    }

    @Test(description = "C226241 - Verify Edit Bid Floor Functionality")
    public void MAU_Ensemble_EnsembleEditBidFloorFunctionality() throws InterruptedException {
        int rowNumberToEdit = (int) (Math.random() * 5);
        double bidFloorValue = Math.round(Math.random() * 0.99 * 100.00) / 100.00;
        String bidValueToCheck = "minimumBid";

        ensemblePage.selectRowToEdit(rowNumberToEdit, "Edit Bid Floor");

        String bidFloorBeforeEdit = ensemblePage.getBidValue(rowNumberToEdit, bidValueToCheck);
        LOGGER.info("Bid Floor value before edit is " + bidFloorBeforeEdit);
        String segmentationBeforeEdit = ensemblePage.getSegmentationFromRow(rowNumberToEdit);

        if (bidFloorBeforeEdit.equals(String.valueOf(bidFloorValue))) {
            LOGGER.info("Bid Floor value is same as before edit. Increasing bid floor value by 0.01");
            bidFloorValue += 0.01;
            ensemblePage.editBidValue(bidFloorValue);
            bidFloorBeforeEdit = ensemblePage.getBidValue(rowNumberToEdit, bidValueToCheck);
            LOGGER.info("Bid Floor value after edit is " + bidFloorBeforeEdit);
        }

        ensemblePage.editBidValue(bidFloorValue);

        String bidFloorAfterEdit = ensemblePage.getBidValue(rowNumberToEdit, bidValueToCheck);
        LOGGER.info("Bid Floor value after edit is " + bidFloorAfterEdit);
        String segmentationAfterEdit = ensemblePage.getSegmentationFromRow(rowNumberToEdit);

        if (segmentationBeforeEdit.equals(segmentationAfterEdit)) {
            Assert.assertNotEquals(bidFloorBeforeEdit, bidFloorAfterEdit, "Bid Floor value is not changed after edit");
            Assert.assertEquals(bidFloorAfterEdit, "$" + bidFloorValue, "Bid Floor value is not changed after edit");
        } else {
            int newRowNumber = ensemblePage.findRowWithSegmentation(segmentationBeforeEdit);
            String newBidFloorValue = ensemblePage.getBidValue(newRowNumber, bidValueToCheck);
            Assert.assertEquals(newBidFloorValue, "$" + bidFloorValue, "Bid Floor value is not changed after edit");
        }
    }

    @Test(description = "C226242 - Verify Edit Bid Ceiling Functionality")
    public void MAU_Ensemble_EnsembleEditBidCeilingFunctionality() throws InterruptedException {
        int rowNumberToEdit = (int) (Math.random() * 5);
        double bidCeilingValue = Math.round((Math.random() + 1) * 100.00) / 100.00;
        String bidValueToCheck = "bidCeiling";

        ensemblePage.selectRowToEdit(rowNumberToEdit, "Edit Bid Ceiling");

        String bidCeilingBeforeEdit = ensemblePage.getBidValue(rowNumberToEdit, bidValueToCheck);
        LOGGER.info("Bid Ceiling value before edit is " + bidCeilingBeforeEdit);
        String segmentationBeforeEdit = ensemblePage.getSegmentationFromRow(rowNumberToEdit);

        if (bidCeilingBeforeEdit.equals(String.valueOf(bidCeilingValue))) {
            LOGGER.info("Bid Ceiling value is same as before edit. Decreasing bid ceiling value by 0.01");
            bidCeilingValue -= 0.01;
            ensemblePage.editBidValue(bidCeilingValue);
            bidCeilingBeforeEdit = ensemblePage.getBidValue(rowNumberToEdit, bidValueToCheck);
            LOGGER.info("Bid Ceiling value after edit is " + bidCeilingBeforeEdit);
        }

        ensemblePage.editBidValue(bidCeilingValue);

        String bidCeilingAfterEdit = ensemblePage.getBidValue(rowNumberToEdit, bidValueToCheck);
        LOGGER.info("Bid Ceiling value after edit is " + bidCeilingAfterEdit);
        String segmentationAfterEdit = ensemblePage.getSegmentationFromRow(rowNumberToEdit);

        if (segmentationBeforeEdit.equals(segmentationAfterEdit)) {
            Assert.assertNotEquals(bidCeilingBeforeEdit, bidCeilingAfterEdit, "Bid Ceiling value is not changed after edit");
            Assert.assertEquals(bidCeilingAfterEdit, "$" + bidCeilingValue, "Bid Ceiling value is not changed after edit");
        } else {
            int newRowNumber = ensemblePage.findRowWithSegmentation(segmentationBeforeEdit);
            String newBidCeilingValue = ensemblePage.getBidValue(newRowNumber, bidValueToCheck);
            Assert.assertEquals(newBidCeilingValue, "$" + bidCeilingValue, "Bid Ceiling value is not changed after edit");
        }
    }

    @Test(description = "Verify Cancel Button Functionality")
    public void MAU_Ensemble_CancelButtonFunctionality() throws InterruptedException {
        int rowNumberToEdit = (int) (Math.random() * 5);
        String functionToCheck = "Edit Bid Floor";
        ensemblePage.selectRowToEdit(rowNumberToEdit, functionToCheck);
        Assert.assertTrue(ensemblePage.isCancelButtonWorking(), "Cancel button is not working");
        LOGGER.info("Cancel button for Edit Bid Floor is working");
        ensemblePage.refreshPage();
        LOGGER.info("Now testing Cancel button for Edit Bid Ceiling");
        rowNumberToEdit = (int) (Math.random() * 5);
        functionToCheck = "Edit Bid Ceiling";
        ensemblePage.selectRowToEdit(rowNumberToEdit, functionToCheck);
        Assert.assertTrue(ensemblePage.isCancelButtonWorking(), "Cancel button is not working");
        LOGGER.info("Cancel button for Edit Bid Ceiling is working");
    }

    @Test(description = "C226246 - Verify Ensemble screen buttons and default date range correct and not editable")
    public void MAU_Ensemble_ButtonsFunctionality() {
        Assert.assertTrue(ensemblePage.verifyPresenceOfButtons(), "Not all buttons are present on the page");
        LOGGER.info("All buttons are present on the page");
        String defaultDateRangeFromUI = ensemblePage.getDefaultDateRange();
        String expectedStartDate = DateUtility.formattingDate(DateUtility.getFirstDayOfLastTwoWeeks());
        String expectedEndDate = DateUtility.formattingDate(DateUtility.getTodayDate());
        Assert.assertEquals(defaultDateRangeFromUI, expectedStartDate + " - " + expectedEndDate, "Default date range is not correct");
        LOGGER.info("Default date range is correct");
        Assert.assertTrue(ensemblePage.isDateRangeEditable(), "Date range is editable");
        LOGGER.info("Date range is not editable");
    }

    @Test(description = "C226249/1 - Verify user can create new Ensemble configuration for Keyword level")
    public void MAU_Ensemble_EnsembleCreateNewConfigurationForSlotLevel() throws InterruptedException, SQLException {
        Map<String, String> configValues = new HashMap<>();
        int buIdToSelect = 113;
        String bidFloorValue = String.valueOf(Math.round(Math.random() * 0.99 * 100.00) / 100.00);
        String bidCeilingValue = String.valueOf(Math.round((Math.random() + 1) * 100.00) / 100.00);

        Map<String, String> keywordsAndAsins = ensemblePage.getKeywordsAndAsinsEligibleForCreation(buIdToSelect);
        String keywordFromDB = keywordsAndAsins.keySet().iterator().next();
        String asinFromDB = keywordsAndAsins.get(keywordFromDB);

        configValues.put("slotOrKeywordLevel", "Keyword");
        configValues.put("pressureValue", "Down");
        configValues.put("bidFloorValue", bidFloorValue);
        configValues.put("bidCeilingValue", bidCeilingValue);
        configValues.put("keywordFromDB", keywordFromDB);
        configValues.put("asinFromDB", asinFromDB);
        configValues.put("defaultBid", "0.01");
        configValues.put("slotDetail", "Slot 1");

        ensemblePage.createConfig(configValues);

        Assert.assertTrue(ensemblePage.isEnsembleCreated(keywordFromDB, bidFloorValue, bidCeilingValue), "Ensemble is not created");
        LOGGER.info("Ensemble for Query " + keywordFromDB + " level is created");
    }

    @Test(description = "C226249/2 - Verify user can create new Ensemble configuration for Slot level")
    public void MAU_Ensemble_EnsembleCreateNewConfigurationForKeywordLevel() throws InterruptedException, SQLException {
        Map<String, String> configValues = new HashMap<>();
        int buIdToSelect = 113;
        String bidFloorValue = String.valueOf(Math.round(Math.random() * 0.99 * 100.00) / 100.00);
        String bidCeilingValue = String.valueOf(Math.round((Math.random() + 1) * 100.00) / 100.00);

        Map<String, String> keywordsAndAsins = ensemblePage.getKeywordsAndAsinsEligibleForCreation(buIdToSelect);
        String keywordFromDB = keywordsAndAsins.keySet().iterator().next();
        String asinFromDB = keywordsAndAsins.get(keywordFromDB);

        configValues.put("slotOrKeywordLevel", "Slot");
        configValues.put("pressureValue", "Equal");
        configValues.put("bidFloorValue", bidFloorValue);
        configValues.put("bidCeilingValue", bidCeilingValue);
        configValues.put("keywordFromDB", keywordFromDB);
        configValues.put("asinFromDB", asinFromDB);
        configValues.put("slotRank", "1");
        configValues.put("segmentationType", "ASIN");

        ensemblePage.createConfig(configValues);

        Assert.assertTrue(ensemblePage.isEnsembleCreated(keywordFromDB, bidFloorValue, bidCeilingValue), "Ensemble is not created");
        LOGGER.info("Ensemble for Query " + keywordFromDB + " level is created");
    }
}