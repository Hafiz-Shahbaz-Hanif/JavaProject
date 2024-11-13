package com.DC.pageobjects.adc.analyze.searchReporting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.CommonFeatures;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.text.ParseException;
import java.time.Duration;
import java.util.*;

import static org.testng.Assert.fail;

public class SearchRankPage extends NetNewNavigationMenu {

    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public CommonFeatures commonFeatures;
    public DCFilters dcFilters;

    private static final By SEARCH_RANK_HEADER = By.xpath("//a[text()='Search Rank']");
    private static final By SHOW_MORE_LESS_BUTTON = By.id("see-more-less");
    private static final By CLEAR_ALL_TERMS_BUTTON = By.id("searchTermClearAllButton");
    private static final By SEARCH_ALL_TERMS_FIELD = By.id("searchTermPicker");
    private static final By SEARCH_TERMS_APPLY_BUTTON = By.id("searchTermApplyButton");
    private static final By SEARCH_TERMS_TAG = By.id("chip");
    private static final By SEARCH_RANK_EXPORT_ICON = By.xpath("//span[text()='download']");
    private static final By ROWS_NUMBER_LINE = By.id("rows-text");
    private static final By CLEAR_SELECTION_AND_CLOSE_BUTTON = By.id("deselect rows");
    private static final By COMPARE_SEARCH_RANK_BUTTON = By.id("open-graph");
    private static final By DATE_RANGE_SELECTOR_CHART = By.xpath("//div[@id='search-rank-graph-widget']//button[@id='date-range-selector']");
    private static final By INTERVAL_SELECTOR_CHART = By.xpath("//div[@id='search-rank-graph-widget']//div[@id='interval-selector']");
    private static final By CLOSE_ICON_IN_CHART = By.xpath("//span[text()='close']");
    private static final By LOADING_BAR = By.xpath("//span[@role='progressbar']");
    private static final By SEARCH_ITEM_TAGS = By.xpath("//div[@id='searchTermPopup']//div[@id='chip']//*[@data-testid='CancelIcon']");
    private static final By SEARCH_DROPDOWN_OPTIONS = By.xpath("//div[@id='searchTermPopup']//li//h6");
    private static final By ROWS_IN_TABLE = By.xpath("//div[@ref='eCheckbox']/ancestor::div[@col-id='productImageUrl']/following-sibling::div[@col-id='rpc']//a");
    private static final By SEARCH_ITEM_TAG_LOCATOR = By.xpath("//div[@id='selectedSearchTermsContainer']//div[@id='chip']");
    private static final By ROW_LOCATOR = By.xpath("//div[@class='ag-center-cols-container']");
    private static final By AVERAGE_RANK_CELLS = By.xpath("//div[@col-id='averageRank']//span");
    private static final By BEST_RANK_IN_UI = By.xpath("//div[@col-id='bestRank']//span");
    private List<String> searchRankStatuses = Arrays.asList("Sponsored Only", "Top rated from our brands", "Sponsored Brands", "Black Friday deals", "Prime Day deals", "Featured from Amazon brands",
            "SP BTF", "SP ATF", "Organic", "SP Carousel", "Brand Amplifier", "Cyber Monday deals", "Highly rated", "Shop by features from reviews", "SP MTF", "Organic Only", "SB Video",
            "All 1st Page results", "Featured from the store", "Climate Pledge Friendly");

    public SearchRankPage(WebDriver driver) {
        super(driver);
        findElementVisible(SEARCH_RANK_HEADER);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
        commonFeatures = new CommonFeatures(driver);
        dcFilters = new DCFilters(driver);
    }

    public boolean isSearchRankHeaderDisplayed() {
        findElementVisible(SEARCH_RANK_HEADER);
        return isElementVisible(SEARCH_RANK_HEADER);
    }

    public void isShowMoreLessButtonWorksCorrectly() throws InterruptedException {
        WebElement showMoreLessButton = findElementVisible(By.xpath("(//button[@id='see-more-less'])[1]"));
        List<WebElement> precedingSibling = showMoreLessButton.findElements(By.xpath("preceding-sibling::*"));

        if (precedingSibling.size() == 2 && showMoreLessButton.getText().equals("+Show More")) {
            UI_LOGGER.info("Show more button is displayed");
            scrollIntoViewAndClick(SHOW_MORE_LESS_BUTTON);

            List<WebElement> searchRankTiles = findElementsVisible(By.xpath("(//button[@id='see-more-less'])[1]/preceding-sibling::*"));

            if (searchRankTiles.size() > precedingSibling.size()) {
                UI_LOGGER.info("All Search Rank tiles are visible");
            } else {
                fail("Not all Search Rank tiles are visible");
            }
        } else {
            fail("Show more button is not displayed or does not match the expected conditions");
        }

        precedingSibling = showMoreLessButton.findElements(By.xpath("preceding-sibling::*"));

        if (precedingSibling.size() > 2 && showMoreLessButton.getText().equals("-Show Less")) {
            UI_LOGGER.info("Show less button is displayed");
            click(SHOW_MORE_LESS_BUTTON);

            List<WebElement> searchRankTiles = findElementsVisible(By.xpath("(//button[@id='see-more-less'])[1]/preceding-sibling::*"));

            if (searchRankTiles.size() == 2) {
                UI_LOGGER.info("Collapsed to 2 Search Rank tiles again");
            } else {
                fail("Failed to collapse to 2 Search Rank tiles again");
            }
        } else {
            fail("Show less button is not displayed or does not match the expected conditions");
        }
    }

    public void selectTermInSearchAllTerms(String... termsToSelect) throws InterruptedException {
        if (isElementVisible(CLEAR_ALL_TERMS_BUTTON)) {
            click(CLEAR_ALL_TERMS_BUTTON);
        }

        click(SEARCH_ALL_TERMS_FIELD);

        for (String termToSelect : termsToSelect) {
            clearInput(SEARCH_ALL_TERMS_FIELD);

            sendKeys(SEARCH_ALL_TERMS_FIELD, termToSelect);
            List<WebElement> selectedSearchItemList = findElementsVisible(SEARCH_DROPDOWN_OPTIONS);

            for (WebElement selectedSearchItem : selectedSearchItemList) {
                if (selectedSearchItem.getText().equals(termToSelect)) {
                    selectedSearchItem.click();
                    break;
                }
            }
        }
        click(SEARCH_TERMS_APPLY_BUTTON);
        waitForElementToBeInvisible(LOADING_BAR, Duration.ofSeconds(15));
    }

    public boolean removeTermFromSearchAllTerms(String termToRemove) {
        List<WebElement> searchTermsTags = findElementsVisible(SEARCH_TERMS_TAG);
        boolean termRemoved = false;

        for (WebElement searchTermTag : searchTermsTags) {
            if (searchTermTag.getText().equals(termToRemove)) {
                searchTermTag.click();
                termRemoved = true;
                break;
            }
        }

        if (termRemoved) {
            UI_LOGGER.info("Term " + termToRemove + " was removed from the search terms tag");
        } else {
            fail("Term " + termToRemove + " was not removed from the search terms tag");
        }

        return true;
    }

    public boolean resetAllTerms() throws InterruptedException {
        if (isElementVisible(CLEAR_ALL_TERMS_BUTTON)) {
            click(CLEAR_ALL_TERMS_BUTTON);
        }

        List<WebElement> searchTermsTags = findElementsVisible(SEARCH_TERMS_TAG);

        if (searchTermsTags.size() == 0) {
            UI_LOGGER.info("All terms were removed from the search terms tag");
        } else {
            fail("Not all terms were removed from the search terms tag");
        }

        clearInput(SEARCH_ALL_TERMS_FIELD);
        return true;
    }

    public boolean isSearchRankExportIconDisplayed() {
        return isElementVisible(SEARCH_RANK_EXPORT_ICON);
    }

    public void clickOnExportIcon() throws InterruptedException {
        click(SEARCH_RANK_EXPORT_ICON);
    }

    public void selectRowsFromTable(List<Integer> indices) throws InterruptedException {
        Thread.sleep(3000);
        List<WebElement> checkboxes = findElementsVisible(By.xpath("//div[@ref='eCheckbox']"));
        if (checkboxes.size() == 0) {
            fail("No checkboxes found");
        }

        for (int index : indices) {
            if (index < checkboxes.size()) {
                scrollIntoView(checkboxes.get(index));
                WebElement checkbox = checkboxes.get(index);
                checkbox.click();
            } else {
                fail("Index " + index + " is out of range.");
            }
        }
    }

    public List<String> getTextFromSelectedRows(List<Integer> indices) {
        List<String> textFromSelectedRows = new ArrayList<>();
        List<WebElement> rows = findElementsVisible(ROWS_IN_TABLE);

        for (int index : indices) {
            if (index < rows.size()) {
                WebElement row = rows.get(index);
                textFromSelectedRows.add(row.getText());
            } else {
                fail("Index " + index + " is out of range.");
            }
        }

        return textFromSelectedRows;
    }

    public boolean isRowsNumberLineDisplayed() {
        return isElementVisible(ROWS_NUMBER_LINE);
    }

    public boolean isNumberOfSelectedRowsCorrect(List<Integer> indices) {
        String rowsNumberLineText = findElementVisible(ROWS_NUMBER_LINE).getText();
        int actualNumberOfSelectedRows = Integer.parseInt(rowsNumberLineText.split(" ")[3]);

        if (actualNumberOfSelectedRows == indices.size()) {
            UI_LOGGER.info("Number of selected rows is correct. Number from result line is " + actualNumberOfSelectedRows + " = number of selected checkboxes " + indices.size());
            return true;
        } else {
            UI_LOGGER.info("Number of selected rows is incorrect");
            return false;
        }
    }

    public boolean areRowButtonsDisplayed() {
        return isElementVisible(CLEAR_SELECTION_AND_CLOSE_BUTTON) && isElementVisible(COMPARE_SEARCH_RANK_BUTTON);
    }

    public void clickOnClearSelectionAndCloseButton() throws InterruptedException {
        click(CLEAR_SELECTION_AND_CLOSE_BUTTON);
        scrollToTopOfPage();
    }

    public boolean isAnyRowSelected() {
        List<WebElement> checkboxes = findElementsVisible(By.xpath("//div[@ref='eCheckbox']//div[@ref='eWrapper']"));
        for (WebElement checkbox : checkboxes) {
            if (checkbox.getAttribute("class").contains("ag-checked") && isRowsNumberLineDisplayed() && areRowButtonsDisplayed()) {
                UI_LOGGER.info("There is at least one row selected");
                return false;
            }
        }
        UI_LOGGER.info("There is no rows selected");
        return true;
    }

    public void clickOnCompareSearchRankButton() throws InterruptedException {
        click(COMPARE_SEARCH_RANK_BUTTON);
    }

    public boolean isDateRangeSelectorChartDisplayed() {
        return isElementVisible(DATE_RANGE_SELECTOR_CHART);
    }

    public boolean isIntervalSelectorChartDisplayed() {
        return isElementVisible(INTERVAL_SELECTOR_CHART);
    }

    public boolean isDateRangeSelectorChartCorrect() {
        String dateRangeSelectorChartText = findElementVisible(By.xpath("//div[@id='search-rank-graph-widget']//button[@id='date-range-selector']/span[2]")).getText();
        String selectedDateText = dateAndIntervalPickerPage.getSelectedDate();
        if (dateRangeSelectorChartText.equals(selectedDateText)) {
            UI_LOGGER.info("Date range selector chart is correct. Date from chart is " + dateRangeSelectorChartText + " = selected date " + selectedDateText);
            return true;
        } else {
            UI_LOGGER.info("Date range selector chart is incorrect");
            return false;
        }
    }

    public boolean isIntervalSelectorChartCorrect() {
        String intervalSelectorChartText = findElementVisible(INTERVAL_SELECTOR_CHART).getText();
        String selectedIntervalText = dateAndIntervalPickerPage.getSelectedInterval();
        if (intervalSelectorChartText.equals(selectedIntervalText)) {
            UI_LOGGER.info("Interval selector chart is correct. Interval from chart is " + intervalSelectorChartText + " = selected interval " + selectedIntervalText);
            return true;
        } else {
            UI_LOGGER.info("Interval selector chart is incorrect");
            return false;
        }
    }

    public void clickCloseIconInChart() throws InterruptedException {
        findElementVisible(CLOSE_ICON_IN_CHART);
        click(CLOSE_ICON_IN_CHART);
    }

    public boolean areRpcsInChartCorrect(List<String> rpcsFromTable) {
        List<WebElement> rpcsInChart = findElementsVisible(By.xpath("//div[@class='highcharts-legend highcharts-no-tooltip']//span/div"));

        List<String> rpcsInChartText = new ArrayList<>();

        for (WebElement rpc : rpcsInChart) {
            String fullText = rpc.getText();
            String rpcText = fullText.split(" ")[0];
            rpcsInChartText.add(rpcText);
        }

        if (rpcsInChartText.equals(rpcsFromTable)) {
            UI_LOGGER.info("RPCs in chart are correct. RPC from chart are " + rpcsInChartText + " = RPC from table " + rpcsFromTable);
            return true;
        } else {
            UI_LOGGER.info("RPCs in chart are incorrect. RPC from chart are " + rpcsInChartText + " = RPC from table " + rpcsFromTable);
            return false;
        }
    }

    public boolean areSelectedSearchTermsDisplayedAsTags(String... termsToSelect) {
        List<String> searchItemTagsText = new ArrayList<>(Arrays.asList(termsToSelect));
        List<WebElement> searchItemTags = findElementsVisible(SEARCH_ITEM_TAG_LOCATOR);
        List<String> searchItemTagsTextFromUI = new ArrayList<>();
        for (WebElement searchItemTag : searchItemTags) {
            searchItemTagsTextFromUI.add(searchItemTag.getText());
        }
        if (searchItemTagsText.equals(searchItemTagsTextFromUI)) {
            UI_LOGGER.info("Selected search terms are displayed as tags. Selected search terms are " + searchItemTagsText + " = search terms from UI " + searchItemTagsTextFromUI);
            return true;
        } else {
            UI_LOGGER.error("Selected search terms are not displayed as tags");
            return false;
        }
    }

    public boolean isShadingRedGreenCorrect() {
        List<WebElement> shading = findElementsVisible(By.xpath("//div[@class='ag-center-cols-viewport']//div[@id='variation-cells-box-main']"));
        zoomInOrOutTo("70");
        for (int i = 0; i < shading.size(); i++) {
            String textFromCell = shading.get(i).getText();
            if (textFromCell.contains("\n")) {
                String[] textParts = textFromCell.split("\n");
                textFromCell = textParts[1];
            }
            String colorFromCell = shading.get(i).getCssValue("background-color");
            if (searchRankStatuses.contains(textFromCell) || textFromCell.equalsIgnoreCase("Sponsored") && colorFromCell.equals("rgba(103, 192, 101, 1)")) {
                UI_LOGGER.info("Shading is correct. Text is " + textFromCell + " and color is " + colorFromCell);
            } else if (textFromCell.equalsIgnoreCase("Not on first page") && colorFromCell.equals("rgba(244, 110, 110, 1)")) {
                UI_LOGGER.info("Shading is correct. Text is " + textFromCell + " and color is " + colorFromCell);
            } else if (textFromCell.equalsIgnoreCase("No data") && colorFromCell.equals("rgba(237, 239, 241, 1)")) {
                UI_LOGGER.info("Shading is correct. Text is " + textFromCell + " and color is " + colorFromCell);
            } else {
                fail("Shading is incorrect. Text is " + textFromCell + " and color is " + colorFromCell);
            }
        }
        zoomInOrOutTo("100");
        return true;
    }

    public void verifyUniquenessOfDaySells() throws ParseException {
        String[] dateParts = commonFeatures.processDatesInTable();
        Set<String> uniqueValuesSet = new HashSet<>();
        List<String> valuesForSet = new ArrayList<>();
        List<String> rowCellValues;
        for (String datePart : dateParts) {
            WebElement rowContainer = findElementVisible(ROW_LOCATOR);
            List<WebElement> rows = rowContainer.findElements(By.xpath("./div"));

            for (int j = 0; j < rows.size(); j++) {
                By specificRowLocator = By.xpath("//div[@class='ag-center-cols-container']/div[@row-id='" + j + "']");
                WebElement specificRow = findElementVisible(specificRowLocator);

                List<WebElement> cells = specificRow.findElements(By.xpath(".//div[@col-id='" + datePart + "']//span/*"));
                if (cells.size() > 2) {
                    commonFeatures.expandRow(specificRow, datePart);
                    cells = specificRow.findElements(By.xpath(".//div[@col-id='" + datePart + "']//span"));
                }
                rowCellValues = commonFeatures.extractCellValues(cells);
                valuesForSet.addAll(rowCellValues);
                uniqueValuesSet.addAll(rowCellValues);
                UI_LOGGER.info("Values are for specific product and specific date " + valuesForSet + " size is " + valuesForSet.size());
                UI_LOGGER.info("Unique values are for specific product and specific date " + uniqueValuesSet + " size is " + uniqueValuesSet.size());
                Assert.assertEquals(valuesForSet.size(), uniqueValuesSet.size(), "Values in day cells are not unique");

                valuesForSet.clear();
                uniqueValuesSet.clear();
            }
        }
    }

    public Integer findLowestRank(List<String> values) {
        int lowestRank = Integer.MAX_VALUE;
        for (String value : values) {
            int rank = Integer.parseInt(value);
            if (rank < lowestRank) {
                lowestRank = rank;
            }
        }
        return lowestRank;
    }


    public String getLowestValueFromUI(int cellIndex) {
        List<WebElement> bestRanks = findElementsVisible(BEST_RANK_IN_UI);
        return bestRanks.get(cellIndex).getText();
    }

    public void verifyBestRankInTable() throws ParseException, InterruptedException {
        int numberOfValuesToCheck = 2;
        String[] dateParts = commonFeatures.processDatesInTable();
        List<String> allCellValues = new ArrayList<>();
        List<String> rowCellValues;
        for (int i = 0; i < numberOfValuesToCheck; i++) {
            By specificRowLocator = By.xpath("//div[@class='ag-center-cols-container']/div[@row-id='" + i + "']");
            WebElement specificRow = findElementVisible(specificRowLocator);
            for (int j = 0; j < dateParts.length; j++) {
                List<WebElement> cells = specificRow.findElements(By.xpath(".//div[@col-id='" + dateParts[j] + "']//span/*"));
                if (cells.size() > numberOfValuesToCheck) {
                    commonFeatures.expandRow(specificRow, dateParts[j]);
                    cells = specificRow.findElements(By.xpath(".//div[@col-id='" + dateParts[j] + "']//span"));
                }
                rowCellValues = commonFeatures.extractCellValues(cells);
                allCellValues.addAll(rowCellValues);
                Thread.sleep(500);
            }

            UI_LOGGER.info("All cell values are " + allCellValues);
            String lowestValue = String.valueOf(findLowestRank(allCellValues));
            UI_LOGGER.info("Lowest value is: " + lowestValue);
            String lowestValueFromUI = getLowestValueFromUI(i);
            lowestValueFromUI = lowestValueFromUI.replaceAll("[^0-9]", "");
            UI_LOGGER.info("Lowest value from UI is: " + lowestValueFromUI);
            Assert.assertEquals(lowestValue, lowestValueFromUI, "Lowest value is not the same");

            allCellValues.clear();
        }
    }

    public void isColorOfTheBucketCorrect(String status, WebElement statusElement) {
        List<String> redTextValues = Collections.singletonList("Not on first page");

        String expectedColor;
        if (searchRankStatuses.contains(status) || status.equalsIgnoreCase("Sponsored")) {
            expectedColor = "rgba(103, 192, 101, 1)";
        } else if (redTextValues.contains(status)) {
            expectedColor = "rgba(244, 110, 110, 1)";
        } else {
            expectedColor = "rgba(237, 239, 241, 1)";
        }

        WebElement childElement = statusElement.findElement(By.xpath("./div"));
        String backgroundColor = childElement.getCssValue("background-color");

        Assert.assertEquals(backgroundColor, expectedColor, "Column text is: " + status + " and column color is: " + backgroundColor + " and expected color is: " + expectedColor);

    }

    public Set<String> getStatusesFromUI() {
        By rowLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index]");
        int numberOfRows = getElementCount(rowLocator);
        Set<String> statusSet = new HashSet<>();

        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);

        for (int i = 0; i < numberOfRows; i++) {
            scrollIntoView(rowLocator);
            By rpcLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index='" + i + "']/div[@col-id='rpc']//span");
            By placementsInUI = By.xpath("//div[@class='ag-center-cols-container']//div[@row-index='" + i + "']//div[contains(@id,'variation-cell-box')]/ancestor::div[@role='gridcell']//span/div");

            int maxRetries = 3;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try {
                    getTextFromElement(rpcLocator);
                    if (isElementVisible(SHOW_MORE_LESS_BUTTON)) {
                        click(SHOW_MORE_LESS_BUTTON);
                    }
                    List<WebElement> statuses = findElementsVisible(placementsInUI);
                    getTextFromElements(statuses);

                    UI_LOGGER.info("Checking color of the statuses");

                    for (WebElement statusElement : statuses) {
                        String statusText = statusElement.getText();
                        String modifiedStatus = statusText.replaceAll("#\\d+", "").trim();
                        statusSet.add(modifiedStatus);

                        if (statusText.equals("No Data")) {
                            statusSet.remove(modifiedStatus);
                        }

                        isColorOfTheBucketCorrect(modifiedStatus, statusElement);
                    }
                    click(SHOW_MORE_LESS_BUTTON);
                    Thread.sleep(1000);
                    break;

                } catch (StaleElementReferenceException e) {
                    UI_LOGGER.info("Stale element exception occurred. Retrying...");
                    retryCount++;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (retryCount >= maxRetries) {
                UI_LOGGER.error("Max retries reached. Could not resolve stale element issue.");
            }
        }

        return statusSet;
    }

    public String getRankTextFromUI(String rankType) {
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR, Duration.ofSeconds(15));
        By averageRankLocator = By.xpath("//div[@col-id='" + rankType + "' and @role='gridcell']//span");
        return getTextFromElement(averageRankLocator).replaceAll("[^0-9]", "");
    }

    public void performSearch(String searchTerm, String rpc, String retailer, String placementType) throws InterruptedException {
        dcFilters.selectRetailer(retailer);
        selectTermInSearchAllTerms(searchTerm);
        commonFeatures.searchForText(rpc);
        commonFeatures.selectPlacementType(placementType);
    }
}



