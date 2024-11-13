package com.DC.utilities;

import com.DC.pageobjects.PageHandler;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.*;
import org.testng.Assert;

import java.text.ParseException;
import java.time.Duration;
import java.util.*;

import static org.testng.Assert.fail;

public class CommonFeatures extends PageHandler {
    private static final By PASS_SHADING_VALUE = By.xpath("//span[text()='Success is']/following-sibling::div//input");
    private static final By FAIL_SHADING_VALUE = By.xpath("//span[text()='Failure is']/following-sibling::div//input");
    private static final By PAGINATION = By.xpath("//nav[@aria-label='pagination navigation']/following-sibling::div");
    private static final By TERMS_PER_PAGE = By.xpath("//nav[@aria-label='pagination navigation']/following-sibling::div//div/div");
    private static final By EXPORT_HEADER = By.xpath("//h4[text()='Export (.csv)']");
    private static final By EXPORT_BUTTON_IN_EXPORT_WINDOW = By.xpath("//button[text()='Export']");
    private final By MORE_OPTIONS_ICON = By.xpath("//span[text()='more_vert']");
    public final By TABLE_HEADERS = By.xpath("//div[@class='ag-header ag-pivot-off']//div[@role='columnheader']//div[@class='ag-header-cell-comp-wrapper']//h3");
    public final By RPC_XPATH = By.xpath("//div[@role='rowgroup']//div[@col-id='rpc']");
    private static final By PLACEMENT_TYPE_DROPDOWN = By.xpath("//*[@id='placement-type' or @id='placement-type-select']");
    private static final String SEARCH_TERM_WITH_DATES_LOCATOR = "//h3[contains(normalize-space(), 'Search') and contains(normalize-space(), 'Rank')]";
    private static final By HORIZONTAL_SCROLLBAR_LOCATOR = By.xpath("//div[contains(@class,'ag-body-horizontal-scroll-viewport')]");
    public static final By LOADING_BAR = By.xpath("//span[@role='progressbar']");
    private static final By NEXT_BUTTON = By.xpath("//button[@aria-label='Go to next page']");
    private static final By SEARCH_FIELD = By.xpath("//input[@placeholder='Search by Product Title or RPC']");

    public CommonFeatures(WebDriver driver) {
        super(driver);
        findElementVisible(TERMS_PER_PAGE);
    }

    public String getDefaultNumberOfTermsDisplayed() {
        return getTextFromElement(TERMS_PER_PAGE, Duration.ofSeconds(3));
    }

    public int getSetPassShadingValue() {
        WebElement passShadingValue = findElementVisible(PASS_SHADING_VALUE);
        return Integer.parseInt(passShadingValue.getAttribute("value"));
    }

    public int getSetFailShadingValue() {
        WebElement failShadingValue = findElementVisible(FAIL_SHADING_VALUE);
        return Integer.parseInt(failShadingValue.getAttribute("value"));
    }

    public boolean verifyShadingColor(By cellsXpath) {

        int setPassValue = getSetPassShadingValue();
        int setFailureValue = getSetFailShadingValue();

        List<WebElement> cells = findElementsVisible(cellsXpath);

        for (WebElement cell : cells) {

            String percentageText = cell.getText();

            int percentage;
            if (percentageText.equals("N/A")) {
                percentage = -1;
            } else {
                percentage = Integer.parseInt(percentageText.replaceAll("%", ""));
            }

            String cellColor = cell.getCssValue("background-color");

            String expectedColor;
            if (percentage == -1) {
                expectedColor = "rgba(237, 239, 241, 1)"; // Grey color for N/A
            } else if (percentage <= setFailureValue) {
                expectedColor = "rgba(244, 110, 110, 1)"; // Red color for failure
            } else if (percentage >= setPassValue) {
                expectedColor = "rgba(103, 192, 101, 1)"; // Green color for pass
            } else {
                expectedColor = "rgba(255, 204, 72, 1)"; // Yellow color for in-between
            }

            Assert.assertEquals(cellColor, expectedColor, "Incorrect color for percentage: " + percentageText);
        }

        return true;
    }

    public boolean verifyPaginationIsPresent() {
        scrollToBottomOfPage();
        return isElementVisible(PAGINATION);
    }

    public boolean isExportHeaderDisplayed() {
        return isElementVisible(EXPORT_HEADER);
    }

    public boolean isExportButtonInExportWindowDisplayed() {
        return isElementVisible(EXPORT_BUTTON_IN_EXPORT_WINDOW);
    }

    public void deselectOptionInMoreOptionsDropdown(String... optionsToDeselect) throws InterruptedException {
        scrollIntoViewAndClick(MORE_OPTIONS_ICON);

        By tableHeadersLocator = By.xpath("//div[@class='ag-header ag-pivot-off']//div[@role='columnheader']//div[@class='ag-header-cell-comp-wrapper']");

        for (String optionToDeselect : optionsToDeselect) {
            By option = By.xpath("//ul[@role='menu']/li[descendant::span[text()='" + optionToDeselect + "']]//input[@type='checkbox']");
            deselectElement(option);
        }

        List<String> updatedTableHeadersText = getTextFromElementsMilliseconds(tableHeadersLocator);

        for (String optionToDeselect : optionsToDeselect) {
            boolean isOptionPresentInTable = updatedTableHeadersText.stream().anyMatch(header -> header.contains(optionToDeselect));

            if (!isOptionPresentInTable) {
                UI_LOGGER.info("Deselected option " + optionToDeselect + " is not present in the table.");
            }

            Assert.assertFalse(isOptionPresentInTable, "Deselected option " + optionToDeselect + " is still present in the table");
            hitEscKey();
        }
    }

    public List<String> getActualTableColumnNames() {
        findElementVisible(TABLE_HEADERS);
        List<String> tableColumnNames = getTextFromElementsMilliseconds(TABLE_HEADERS);
        UI_LOGGER.info("Actual Options in Price Table are " + tableColumnNames);
        return tableColumnNames;
    }

    public boolean areOptionsRedirectedToSameRPCUrl() {
        if (!isElementVisible(RPC_XPATH)) {
            scrollIntoView(RPC_XPATH);
        }

        List<WebElement> rpcs = findElementsVisible(RPC_XPATH);
        for (int i = 0; i < rpcs.size(); i++) {
            WebElement rpc = findElementsVisible(RPC_XPATH).get(i);
            String rpcValue = rpc.getText();
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("window.open('https://www.amazon.com/dp/' + '" + rpcValue + "')");

            waitForNewTabToOpen(2);
            switchToTab(2, 1);
            UI_LOGGER.info("Current URL is " + getCurrentUrl() + " and it should contain " + rpcValue);

            boolean isOptionRedirectedToSameRPC;
            isOptionRedirectedToSameRPC = getCurrentUrl().contains(rpcValue);


            if (!isOptionRedirectedToSameRPC) {
                fail("Option " + rpcValue + " did not redirect to the same RPC");
            }

            closeCurrentTabAndSwitchToMainTab();
        }

        return true;
    }

    public boolean verifyDisplayOfPlacementTypeDropdown() {
        return isElementVisible(PLACEMENT_TYPE_DROPDOWN);
    }

    public void isDropdownSingleSelect() throws InterruptedException {

        click(PLACEMENT_TYPE_DROPDOWN);

        List<WebElement> placementTypeDropdownOptions = findElementsVisible(By.xpath("//ul[@role='listbox']/li"));

        int selectedOptions = 0;
        boolean isAll1stPageResultsDefault = false;
        for (WebElement option : placementTypeDropdownOptions) {
            String ariaSelected = option.getAttribute("aria-selected");
            if (ariaSelected != null && ariaSelected.equals("true")) {
                selectedOptions++;

                if (option.getText().equals("All 1st Page results")) {
                    isAll1stPageResultsDefault = true;
                }
            }
        }

        Assert.assertEquals(1, selectedOptions, "Only one option should be selected");

        Assert.assertTrue(isAll1stPageResultsDefault, "Selected option should be 'All 1st Page Results");
    }

    public boolean verifyPlacementTypeDropdownHasRequiredOptions() throws InterruptedException {
        click(PLACEMENT_TYPE_DROPDOWN);
        List<WebElement> placementTypeDropdownOptions = findElementsVisible(By.xpath("//ul[@role='listbox']/li"));

        List<String> placementTypeDropdownOptionTexts = new ArrayList<>();
        for (WebElement option : placementTypeDropdownOptions) {
            placementTypeDropdownOptionTexts.add(option.getText());
            if (option.getText().equalsIgnoreCase("All 1st Page Results"))
                option.click();
        }

        List<String> expectedOptions = Arrays.asList("Organic Only", "Sponsored Only", "All 1st Page results");

        return placementTypeDropdownOptionTexts.containsAll(expectedOptions);
    }

    public List<String> getDatesOrTimesFromTable(boolean extractDates) {
        zoomInOrOutTo("70");
        List<String> valuesInTable = new ArrayList<>();
        waitForElementToBeInvisible(LOADING_BAR);

        int datesOrTimesColumnsCount = findElementsPresent(By.xpath(SEARCH_TERM_WITH_DATES_LOCATOR)).size();

        boolean scrollBarDisplayed = isElementVisibleMilliseconds(HORIZONTAL_SCROLLBAR_LOCATOR);

        for (int i = 0; i < datesOrTimesColumnsCount; i++) {
            By dateLocator = By.xpath("(" + SEARCH_TERM_WITH_DATES_LOCATOR + ")[" + (i + 1) + "]");
            WebElement element;
            if (scrollBarDisplayed) {
                element = scrollRightToElement(HORIZONTAL_SCROLLBAR_LOCATOR, dateLocator, 10);
            } else {
                element = findElementVisibleMilliseconds(dateLocator);
            }

            String columnName = element.getText();
            String[] parts = columnName.split("\n");
            if (parts.length >= 2) {
                if (extractDates) {
                    valuesInTable.add(parts[1]);
                } else {
                    if (parts[2].contains("AM") || parts[2].contains("PM")) {
                        valuesInTable.add(parts[2]);
                    } else {
                        fail("Time is not displayed in the table");
                    }
                }
            }
        }
        zoomInOrOutTo("100");
        if (extractDates) {
            UI_LOGGER.info("Dates in the table are " + valuesInTable);
        } else {
            UI_LOGGER.info("Times in the table are " + valuesInTable);
        }
        return valuesInTable;
    }

    public List<String> getListOfItemsFromTable(String cellId) throws InterruptedException {
        waitForElementToBeInvisible(LOADING_BAR, Duration.ofSeconds(30));
        By tableLocator = By.xpath("//div[contains(@class,'ag-body-viewport')]");
        By elementsInTableLocator = By.xpath("//span[contains(@id,'" + cellId + "')] | //div[contains(@id,'" + cellId + "')]");
        List<String> elementsInTable = new ArrayList<>();
        List<String> elementsTexts = new ArrayList<>();
        Thread.sleep(2000);
        scrollMainBarToCenterAndInnerBarToTop(tableLocator);
        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableLocator, 10);
        do {
            currentPosition = getVerticalScrollPosition(tableLocator);
            List<WebElement> elements;
            try {
                elements = findElementsPresent(elementsInTableLocator);
            } catch (StaleElementReferenceException e) {
                elements = findElementsPresent(elementsInTableLocator);
            }
            for (WebElement element : elements) {
                String text = element.getText();
                if (!elementsTexts.contains(text)) {
                    elementsTexts.add(text);
                    try {
                        elementsInTable.add(element.getText());
                    } catch (StaleElementReferenceException e) {
                        elementsInTable.add(element.getText());
                    }
                }
                scrollElementVertically(pixelsToScroll, tableLocator);
            }
        }
        while (currentPosition != getVerticalScrollPosition(tableLocator));
        return elementsInTable;
    }

    public void changePageSize(String pageSize) throws InterruptedException {
        scrollIntoViewAndClick(PAGINATION);
        click(By.xpath("//ul[@role='listbox']/li[text()='" + pageSize + "']"));
        waitForElementToBeInvisible(LOADING_BAR);
        scrollToTopOfPage();
    }

    public void clickOnNextButton() throws InterruptedException {
        click(NEXT_BUTTON);
    }

    public void clickOnNextButtonIfEnabled() throws InterruptedException {
        waitForElementToBeInvisible(LOADING_BAR, Duration.ofSeconds(10));
        WebElement nextButton = findElementVisible(NEXT_BUTTON);
        findElementVisible(NEXT_BUTTON);

        String isDisabledValue = nextButton.getAttribute("disabled");
        if (isDisabledValue != null) {
            UI_LOGGER.info("Next button is disabled");
        } else {
            UI_LOGGER.info("Next button is enabled");
            clickOnNextButton();
        }
    }

    public boolean verifyNextButtonChangesDataInTable(String cellId, int numOfPagesToCheck) throws InterruptedException {
        Set<List<String>> visitedPages = new HashSet<>();

        for (int i = 0; i < numOfPagesToCheck; i++) {
            List<String> currentPageData = getListOfItemsFromTable(cellId);
            if (visitedPages.contains(currentPageData)) {
                UI_LOGGER.info("Data in the table is the same on page " + (i + 1) + " and a previous page.");
                return false;
            }
            visitedPages.add(currentPageData);

            if (i < numOfPagesToCheck - 1) {
                clickOnNextButtonIfEnabled();
            }
        }
        return true;
    }

    public String[] processDatesInTable() throws ParseException {
        DateAndIntervalPickerPage dp = new DateAndIntervalPickerPage(driver);
        String dateFromDateRange = dp.getSelectedDate();
        String formattedDate = DateAndIntervalPickerPage.transformDate(dateFromDateRange);

        return formattedDate.split(" - ");
    }

    public void expandRow(WebElement row, String datePart) {
        WebElement showMoreLessButton = row.findElement(By.xpath(".//div[@col-id='" + datePart + "']//button"));
        scrollIntoViewAndClick(showMoreLessButton);
    }

    public List<String> extractCellValues(List<WebElement> cells) {
        List<String> valuesForSet = new ArrayList<>();

        for (WebElement cell : cells) {
            String cellText = cell.getText();
            if (cellText.contains("#")) {
                String[] cellTextParts = cellText.split("\n");
                for (String cellTextPart : cellTextParts) {
                    String trimmedPart = cellTextPart.trim();
                    if (trimmedPart.startsWith("#")) {
                        String cellTextWithoutHashtag = trimmedPart.substring(1);
                        if (!cellTextWithoutHashtag.equals("")) {
                            valuesForSet.add(cellTextWithoutHashtag);
                        }
                    }
                }
            } else {
                if (!cellText.equals("")) {
                    valuesForSet.add(cellText);
                }
            }
        }
        return valuesForSet;
    }

    public void searchForText(String text) {
        scrollIntoView(SEARCH_FIELD);
        if (!findElementVisible(SEARCH_FIELD).getAttribute("value").equals("")) {
            clearInput(SEARCH_FIELD);
        }
        sendKeysAndHitEnter(SEARCH_FIELD, text);
    }

    public void selectPlacementType(String placementType) throws InterruptedException {
        click(PLACEMENT_TYPE_DROPDOWN);
        By placementTypeOptions = By.xpath("//ul[@aria-labelledby='placement-type-label']/li[text()='" + placementType + "']");
        clickElement(placementTypeOptions);
    }

    public List<String> getPlacementsFromUI() throws InterruptedException {
        List<String> placements = new ArrayList<>();
        click(PLACEMENT_TYPE_DROPDOWN);
        List<WebElement> placementElements = findElementsVisible(By.xpath("//ul[@role='listbox']/li"));
        for (WebElement placementElement : placementElements) {
            placements.add(placementElement.getText());
        }
        return placements;
    }
}