package com.DC.pageobjects.adc.execute.productManager.imports;

import com.DC.objects.productVersioning.ImportsTableData;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.*;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.awaitility.Awaitility.await;

public class ImportsPage extends InsightsNavigationMenu {
    private final By NEW_IMPORT_BUTTON_LOCATOR = By.xpath("//button[text()='New Import']");
    private final By SKELETON_LOCATOR = By.xpath("//span[contains(@class,'MuiSkeleton')]");
    private final By SEARCH_INPUT_LOCATOR = By.xpath("//div[@data-qa='ActionBar']//input");
    private final By IMPORTS_DISPLAYED_COUNT_LOCATOR = By.xpath("//div[@data-qa='ActionBar']/div/span");

    public Paginator paginator;
    public AGTableCommonFeatures tableCommonFeatures;

    public ImportsPage(WebDriver driver) {
        super(driver);
        waitForPageToFullyLoad();
        paginator = new Paginator(driver);
        tableCommonFeatures = new AGTableCommonFeatures(driver);
    }

    public ImportsPage waitForPageToFullyLoad() {
        findElementVisible(NEW_IMPORT_BUTTON_LOCATOR, MAX_WAIT_TIME_SECS);
        waitForElementToBeInvisible(SKELETON_LOCATOR, Duration.ofSeconds(30));
        return this;
    }

    public ImportsPage searchForImport(String searchTerm) {
        setTextAndHitEnter(SEARCH_INPUT_LOCATOR, searchTerm);
        return new ImportsPage(driver);
    }

    public int getNumberDisplayedNextToSearchInput() {
        String textInElement = getTextFromElementMilliseconds(IMPORTS_DISPLAYED_COUNT_LOCATOR);
        return SharedMethods.extractIntegerFromString(textInElement);
    }

    public ImportsTableData getDataOfLatestImport() {
        var cellsInRequestedRow = By.xpath("(//div[@class='ag-center-cols-viewport']//div[@role='row'])[1]//span");
        var rowData = getTextFromElementsMilliseconds(cellsInRequestedRow);
        return new ImportsTableData(rowData.get(0), Enums.ImportType.fromText(rowData.get(1)), Integer.parseInt(rowData.get(2)), rowData.get(3), rowData.get(4), rowData.get(5));
    }

    public List<ImportsTableData> getImportsDisplayed() {
        List<ImportsTableData> imports = new ArrayList<>();
        List<String> importIds = new ArrayList<>();

        scrollToCenterIfTableScrollable(tableCommonFeatures.TABLE_LOCATOR);

        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableCommonFeatures.TABLE_LOCATOR, 15);
        int totalRowsCount = 0;
        int latestRow;
        do {
            waitForDOMStabilization();
            currentPosition = getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR);
            int rowsDisplayedCount = getElementCount(tableCommonFeatures.ROWS_LOCATOR, Duration.ofSeconds(2));
            latestRow = totalRowsCount;
            totalRowsCount += rowsDisplayedCount;

            for (int i = latestRow; i <= totalRowsCount; i++) {
                By rowLocator = By.xpath("//div[@role='row' and ancestor::div[@class='ag-center-cols-container'] and @row-index=" + i + "]");

                boolean isRowPresent = isElementPresentMilliseconds(rowLocator);
                if (!isRowPresent) {
                    continue;
                }

                try {
                    getImportInfoAndAddItToList(rowLocator, imports, importIds);
                } catch (StaleElementReferenceException ex) {
                    getImportInfoAndAddItToList(rowLocator, imports, importIds);
                }
            }
            scrollElementVertically(pixelsToScroll, tableCommonFeatures.TABLE_LOCATOR);

        } while (currentPosition != getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR));
        return imports;
    }

    public boolean doAllImportsMatchCriteria(String searchTerm) {
        List<ImportsTableData> imports = new ArrayList<>();
        List<String> importIds = new ArrayList<>();

        scrollToCenterIfTableScrollable(tableCommonFeatures.TABLE_LOCATOR);

        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableCommonFeatures.TABLE_LOCATOR, 15);
        int totalRowsCount = 0;
        int latestRow;
        do {
            waitForDOMStabilization();
            currentPosition = getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR);
            int rowsDisplayedCount = getElementCount(tableCommonFeatures.ROWS_LOCATOR, Duration.ofSeconds(2));
            latestRow = totalRowsCount;
            totalRowsCount += rowsDisplayedCount;

            for (int i = latestRow; i <= totalRowsCount; i++) {
                By rowLocator = By.xpath("//div[@role='row' and ancestor::div[@class='ag-center-cols-container'] and @row-index=" + i + "]");

                boolean isRowPresent = isElementPresentMilliseconds(rowLocator);
                if (!isRowPresent) {
                    continue;
                }

                try {
                    getImportInfoAndAddItToList(rowLocator, imports, importIds);
                } catch (StaleElementReferenceException ex) {
                    getImportInfoAndAddItToList(rowLocator, imports, importIds);
                }

                var latestImport = imports.get(imports.size() - 1);
                var rowMatchCriteria = latestImport.name.contains(searchTerm) ||
                        latestImport.type.toString().toLowerCase().contains(searchTerm) ||
                        latestImport.status.contains(searchTerm);

                UI_LOGGER.info(latestImport + " matches criteria: " + rowMatchCriteria + " for search term: " + searchTerm);

                if (!rowMatchCriteria) {
                    return false;
                }
            }
            scrollElementVertically(pixelsToScroll, tableCommonFeatures.TABLE_LOCATOR);

        } while (currentPosition != getVerticalScrollPosition(tableCommonFeatures.TABLE_LOCATOR));
        return true;
    }

    public File downloadImportedFile(String fileNameWithExtension, String downloadsDirectory) {
        searchForImport(fileNameWithExtension);
        var cellToClick = By.xpath("(//div[@class='ag-center-cols-viewport']//div[@role='row'])[1]//span[ancestor::div[@col-id='filePath']]/a");

        var filesBeforeDownload = SharedMethods.getFilesInDirectory(downloadsDirectory, fileNameWithExtension);

        clickElement(cellToClick);

        try {
            await().atMost(Duration.ofSeconds(10))
                    .pollInterval(Duration.ofMillis(500))
                    .until(() -> {
                        List<File> filesInDirectory = SharedMethods.getFilesInDirectory(downloadsDirectory, fileNameWithExtension);
                        int filesAfterExporting = filesInDirectory.size();
                        return filesAfterExporting != filesBeforeDownload.size();
                    });
            return SharedMethods.getFilesInDirectory(downloadsDirectory, fileNameWithExtension).get(0);
        } catch (ConditionTimeoutException exception) {
            Assert.fail("File : " + fileNameWithExtension + " was not downloaded in the expected time");
        }
        return null;
    }

    public ImportModal openModalToImportCompanyProperties() throws InterruptedException {
        var singleSelectDropdown = new SingleSelectDropdown(driver);
        singleSelectDropdown.openDropdownMenu("New Import");
        singleSelectDropdown.selectOption("Import Company Property Data");
        return new ImportModal(driver);
    }

    public ProductsImportModal openModalToImportProductData() throws InterruptedException {
        var singleSelectDropdown = new SingleSelectDropdown(driver);
        singleSelectDropdown.openDropdownMenu("New Import");
        singleSelectDropdown.selectOption("Import Product Data");
        return new ProductsImportModal(driver);
    }

    public void clickOnFailedImport(String importNameAndExtension) {
        var failedImportLocator = By.xpath("//div[@col-id='filePath' and descendant::a[text()='" + importNameAndExtension + "']]//following-sibling::div[@col-id='status']//a");
        clickElement(failedImportLocator);
    }

    public ProductsPage clickOnVersionsUpdated(String importNameAndExtension) {
        var versionsUpdatedLocator = By.xpath("(//div[@col-id='filePath' and descendant::a[text()='" + importNameAndExtension + "']]//following-sibling::div[@col-id='versionsUpdated']//a)[1]");
        clickElement(versionsUpdatedLocator);
        return new ProductsPage(driver);
    }

    public String getIdOfFirstImport() {
        var cellToClick = By.xpath("(//div[@class='ag-center-cols-viewport']//div[@role='row'])[1]");
        return getAttribute(cellToClick, "row-id");
    }

    private void scrollUntilImportIsPresent(String importId) {
        By rowLocatorXPath = By.xpath(tableCommonFeatures.TABLE_VIEWPORT_XPATH + "//div[@row-id='" + importId + "']");
        String errorMsg = "Row  '" + importId + "' was not found in the table";
        scrollDownToElement(tableCommonFeatures.TABLE_LOCATOR, rowLocatorXPath, 10, errorMsg);
    }

    private ImportsTableData getImport(String importId) {
        scrollUntilImportIsPresent(importId);
        By cellsInRow = By.xpath(tableCommonFeatures.TABLE_VIEWPORT_XPATH + "//div[@row-id='" + importId + "']//span");
        var rowData = getTextFromElementsMilliseconds(cellsInRow);
        return new ImportsTableData(rowData.get(0), Enums.ImportType.fromText(rowData.get(1)), Integer.parseInt(rowData.get(2)), rowData.get(3), rowData.get(4), rowData.get(5));
    }

    private void getImportInfoAndAddItToList(By rowLocator, List<ImportsTableData> imports, List<String> importIds) {
        WebElement row = findElementPresentMilliseconds(rowLocator);
        String importId = row.getAttribute("row-id");
        boolean importAlreadyAdded = importIds.stream().anyMatch(i -> i.equals(importId));
        if (!importAlreadyAdded) {
            ImportsTableData importData;
            try {
                importData = getImport(importId);
            } catch (StaleElementReferenceException ex) {
                importData = getImport(importId);
            }
            imports.add(importData);
            importIds.add(importId);
        }
    }
}
