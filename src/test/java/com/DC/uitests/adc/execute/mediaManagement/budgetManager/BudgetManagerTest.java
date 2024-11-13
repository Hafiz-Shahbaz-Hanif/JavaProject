package com.DC.uitests.adc.execute.mediaManagement.budgetManager;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.mediaManagement.budgetManager.BudgetManagerPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.DC.utilities.CsvUtility.readBulkUploadCsvFileByColumnsAndSkipEqualDates;
import static com.DC.utilities.XLUtils.XLUtils.createExcelFileFromHeaderColumns;

public class BudgetManagerTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private BudgetManagerPage budgetManagerPage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        AppHomepage appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Execute");
        appHomepage.clickLink("Budget Manager");
        budgetManagerPage = new BudgetManagerPage(driver);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("3M CA", "3M", "3M AU");
        budgetManagerPage.selectPlatforms(List.of("Amazon DSP", "Walmart"));
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify that User is able to select multiple options from IO Segment filter")
    public void MDR_BudgetManager_IoSegmentFilterVerification() throws InterruptedException {
        budgetManagerPage.clickIOSegmentButton();
        List<String> iOSegmentFilterOptions = budgetManagerPage.getAvailableOptionsInIoSegmentFilter();

        performTest(iOSegmentFilterOptions.get(0));

        budgetManagerPage.clearIoSegmentFilter();

        performTest(iOSegmentFilterOptions.get(0), iOSegmentFilterOptions.get(1), iOSegmentFilterOptions.get(2));
    }

    private void performTest(String... optionsToSelect) throws InterruptedException {
        budgetManagerPage.selectIoSegmentFilterOptions(optionsToSelect);
        Thread.sleep(5000);
        var ioSegmentColumnValues = budgetManagerPage.getIoSegmentColumnValues();
        var containsOnlySelectedOptionsOrEmpty = ioSegmentColumnValues.isEmpty()
                || List.of(optionsToSelect).containsAll(ioSegmentColumnValues);
        var invalidColumnValues = ioSegmentColumnValues.stream()
                .filter(element -> !List.of(optionsToSelect).contains(element))
                .collect(Collectors.toList());
        Assert.assertTrue(containsOnlySelectedOptionsOrEmpty, "Unexpected IO Segment column values:" + invalidColumnValues);
    }

    @Test(priority = 2, description = "Verify Bulk Budget Upload functionality")
    public void MDR_BudgetManager_BulkUpload_Verification() throws Exception {
        budgetManagerPage.clickDownloadButton();
        String downloadedFileExtension = ".csv";
        String downloadedFilePath = SharedMethods.isFileDownloaded(downloadedFileExtension, "budgetsubmission", 60, downloadFolder);

        String xlsxFilePath = downloadFolder + File.separator + "budgetsubmission.xlsx";
        List<String> dateColumns = List.of("Start Date", "End Date");

        var incompleteHeaderList = "Platform,Business Unit,IO Segment,Start Date,End Date";
        Map<String, List<Object>> incompleteColumns = readBulkUploadCsvFileByColumnsAndSkipEqualDates(downloadedFilePath, incompleteHeaderList, dateColumns, 5);
        var invalidFilePath = createExcelFileFromHeaderColumns(incompleteColumns, xlsxFilePath, dateColumns);
        String errorPopUpMessage = "Excel Parsing Exception: Missing headers: Requested Budget, Supporting Documents (insert URL to MAF)";
        performTest(invalidFilePath, errorPopUpMessage);

        var requiredHeaderList = "Platform,Business Unit,IO Segment,Start Date,End Date,Requested Budget";
        Map<String, List<Object>> requiredColumns = readBulkUploadCsvFileByColumnsAndSkipEqualDates(downloadedFilePath, requiredHeaderList, dateColumns, 5);

        addSupportingDocumentsColumn(requiredColumns, "Invalid data");
        var warningFilePath = createExcelFileFromHeaderColumns(requiredColumns, xlsxFilePath, dateColumns);
        String warningPopUpMessage = "Request has been processed. Row level validation failures encountered. Please check the errors file.";
        performTest(warningFilePath, warningPopUpMessage);
        String warningDownloadedFilePath = SharedMethods.isFileDownloaded(downloadedFileExtension, "BULK_UPLOAD_ERRORS", 60, downloadFolder);

        addSupportingDocumentsColumn(requiredColumns, "https://cloud.qa.dc.flywheeldigital.com/app/budget/manager");
        var validFilePath = createExcelFileFromHeaderColumns(requiredColumns, xlsxFilePath, dateColumns);
        String successPopUpMessage = "Bulk upload completed successfully";
        performTest(validFilePath, successPopUpMessage);

        SharedMethods.deletePath(Path.of(downloadedFilePath));
        SharedMethods.deletePath(Path.of(xlsxFilePath));
        SharedMethods.deletePath(Path.of(warningDownloadedFilePath));
    }

    private void addSupportingDocumentsColumn(Map<String, List<Object>> data, String valueToFill) {
        var supportingDocumentsHeader = "Supporting Documents (insert URL to MAF)";
        var columnSize = data.get("Platform").size();
        List<Object> supportingDocumentsUrl = new ArrayList<>();
        for (int i = 0; i < columnSize; i++) {
            supportingDocumentsUrl.add(valueToFill);
        }
        data.put(supportingDocumentsHeader, supportingDocumentsUrl);
    }

    private void performTest(String relativeFilePath, String popUpMessage) throws InterruptedException {
        budgetManagerPage.clickBulkUploadButton();
        budgetManagerPage.uploadBulkFile(relativeFilePath);
        budgetManagerPage.clickUploadButton();
        budgetManagerPage.clickBulkUploadConfirmButton();
        Assert.assertTrue(budgetManagerPage.getPopUpText(popUpMessage).contains(popUpMessage), "Pop-up message is not as expected");
    }
}