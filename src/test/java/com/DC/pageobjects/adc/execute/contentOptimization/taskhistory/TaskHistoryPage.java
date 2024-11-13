package com.DC.pageobjects.adc.execute.contentOptimization.taskhistory;

import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.pageobjects.filters.InsightsTimestampFilter;
import com.DC.pageobjects.filters.MultiselectWithSortFilter;
import com.DC.utilities.DiffUtility;
import com.DC.utilities.SharedMethods;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;

import java.time.Duration;
import java.util.*;

public class TaskHistoryPage extends InsightsNavigationMenu {
    public final String HEADER_XPATH = "//div[contains(@class,'MuiCardHeader')]";
    public final By TABLE_XPATH = By.xpath("//div[@data-qa='TaskHistory']");
    public final By COUNTER_LOCATOR = By.xpath(HEADER_XPATH + "//h6");
    public final By SEARCH_INPUT_LOCATOR = By.xpath(HEADER_XPATH + "//input");
    public final By SEARCH_BUTTON_LOCATOR = By.xpath("//button[text()='search']");
    public final By APPLIED_FILTERS_LOCATOR = By.xpath(HEADER_XPATH + "//div[@data-qa='InlineList']//span");
    public final By NO_DATA_LOCATOR = By.xpath("//big[text()='No data']");

    public TaskHistoryPage(WebDriver driver) {
        super(driver);
        findElementVisible(TABLE_XPATH);
    }

    public TaskHistoryPage searchRecord(String searchTerm) {
        setText(SEARCH_INPUT_LOCATOR, searchTerm);
        clickElement(SEARCH_BUTTON_LOCATOR);
        return new TaskHistoryPage(driver);
    }

    public List<String> getFiltersApplied() {
        return getTextFromElements(APPLIED_FILTERS_LOCATOR);
    }

    public int getNumberOfRecordsDisplayed() {
        var counterText = getTextFromElement(COUNTER_LOCATOR);
        return SharedMethods.extractIntegerFromString(counterText);
    }

    public InsightsTimestampFilter openTimestampFilter() {
        var timestampLocator = By.xpath("//th//h6[text()='Timestamp']");
        clickElement(timestampLocator);
        return new InsightsTimestampFilter(driver);
    }

    public MultiselectWithSortFilter openDropdownFilter(String columnName) {
        var columnLocator = By.xpath("//th//h6[text()='" + columnName + "']");
        clickElement(columnLocator);
        return new MultiselectWithSortFilter(driver);
    }

    public ArrayList<LinkedHashMap<String, String>> getTableData() {
        ArrayList<LinkedHashMap<String, String>> tasksData = new ArrayList<>();

        var tableLocator = By.xpath("//div[@class='table-responsive dx-g-bs4-table-container']");
        var tableBody = By.xpath("//div[@data-qa='TaskHistory']//tbody");

        scrollToCenterIfTableScrollable(tableLocator);

        double currentPosition;
        double pixelsToScroll = getPixelsToScroll(tableLocator, 15);

        do {
            waitForDOMStabilization();
            currentPosition = getVerticalScrollPosition(tableLocator);

            WebElement tableElement;
            List<WebElement> tableRows;
            try {
                tableElement = findElementVisible(tableBody);
                tableRows = new ArrayList<>(tableElement.findElements(By.tagName("tr")));
            } catch (StaleElementReferenceException e) {
                tableElement = findElementVisible(tableBody);
                tableRows = new ArrayList<>(tableElement.findElements(By.tagName("tr")));
            }

            for (var row : tableRows) {
                List<WebElement> rowCells;
                try {
                    rowCells = new ArrayList<>(row.findElements(By.tagName("td")));
                } catch (StaleElementReferenceException e) {
                    rowCells = new ArrayList<>(row.findElements(By.tagName("td")));
                }

                List<WebElement> finalRowCells = rowCells;
                var timestampInList = tasksData.stream().anyMatch(map -> map.get("Timestamp").equals(finalRowCells.get(0).getText()));
                LinkedHashMap<String, String> rowData = null;
                if (!timestampInList) {
                    try {
                        rowData = getRowData(rowCells);
                    } catch (StaleElementReferenceException e) {
                        rowData = getRowData(rowCells);
                    }
                }

                if (rowData != null) {
                    var isEmptyRow = rowData.values().stream().allMatch(String::isEmpty);
                    if (!isEmptyRow) {
                        tasksData.add(rowData);
                    }
                }
            }

            scrollElementVertically(pixelsToScroll, tableLocator);

        } while (currentPosition != getVerticalScrollPosition(tableLocator));
        return tasksData;
    }

    public LinkedHashMap<String, String> getFirstRowData() {
        var firstRowLocator = By.xpath("//tbody//tr[1]");
        var firstRow = findElementVisible(firstRowLocator);
        List<WebElement> rowCells = new ArrayList<>(firstRow.findElements(By.tagName("td")));
        return getRowData(rowCells);
    }

    public boolean isNoDataRowDisplayed() {
        return isElementVisible(NO_DATA_LOCATOR, Duration.ofSeconds(3));
    }

    public TaskDetails clickFirstTimestampLink() {
        var firstTimestampLink = By.xpath("//tbody//tr[1]//span");
        clickElement(firstTimestampLink);
        return new TaskDetails(driver);
    }

    private LinkedHashMap<String, String> getRowData(List<WebElement> rowCells) {
        LinkedHashMap<String, String> rowData = new LinkedHashMap<>();
        rowData.put("Timestamp", rowCells.get(0).getText());
        rowData.put("Task Title", rowCells.get(1).getText());
        rowData.put("Product Versions", rowCells.get(2).getText());
        rowData.put("Assignment Name", rowCells.get(3).getText());
        rowData.put("User", rowCells.get(4).getText());
        rowData.put("Decision", rowCells.get(5).getText());
        return rowData;
    }

    public static class TaskDetails extends InsightsNavigationMenu {
        private final By TABLE_ROWS = By.xpath("//table[@class='product-history-flyout-table']//tbody/tr");
        private final By CLOSE_ICON_LOCATOR = By.xpath("//button[child::*[@data-testid='CloseIcon']]");
        private final By PRODUCTS_LOCATOR = By.xpath("//div[child::div[@data-qa='DetailsOverlayTable']]/preceding-sibling::div//span");
        private final By TIMESTAMP_LOCATOR = By.xpath("//div[contains(@class,'MuiDialog-container')]//h2");
        private final By ASSIGNMENT_NAME_LOCATOR = By.xpath("(//div[contains(@class,'MuiDialogContent-root')]//h4)[1]");
        private final By TASK_TITLE_LOCATOR = By.xpath("(//div[contains(@class,'MuiDialogContent-root')]//span)[1]");
        private final By SUBMITTED_BY_LOCATOR = By.xpath("(//div[contains(@class,'MuiDialogContent-root')]//span)[2]");

        public TaskDetails(WebDriver driver) {
            super(driver);
            findElementVisible(TABLE_ROWS);
        }

        public void clickFirstTimestampLink() {
            var firstTimestampLink = By.xpath("//tbody//tr[1]//span");
            clickElement(firstTimestampLink);
        }

        public TaskHistoryPage closeModal() {
            clickElement(CLOSE_ICON_LOCATOR);
            return new TaskHistoryPage(driver);
        }

        public TaskDetails clickOnProduct(String productToClick) {
            var productLink = By.xpath("//div[child::div[@data-qa='DetailsOverlayTable']]/preceding-sibling::div//span[text()='" + productToClick + "']");
            clickElement(productLink);
            return this;
        }

        public List<TaskHistoryObjects.GeneralFeedbackBox> getGeneralFeedback() {
            var feedbackBoxes = new ArrayList<TaskHistoryObjects.GeneralFeedbackBox>();
            var feedbackBoxXPath = "(//div[@data-qa='GeneralFeedbackBox'])";
            var feedbackBoxTotal = getElementCount(By.xpath(feedbackBoxXPath));
            for (int i = 1; i <= feedbackBoxTotal; i++) {
                var senderLocator = By.xpath(feedbackBoxXPath + "[" + i + "]/div[1]/span[1]");
                var timestampLocator = By.xpath(feedbackBoxXPath + "[" + i + "]/div[1]/span[2]");
                var feedbackLocator = By.xpath(feedbackBoxXPath + "[" + i + "]/div[2]/span");
                var sender = getTextFromElement(senderLocator);
                var timestamp = getTextFromElement(timestampLocator);
                var feedback = getTextFromElement(feedbackLocator);

                var spaceIndex = timestamp.indexOf(" ");
                timestamp = (spaceIndex != -1) ? timestamp.substring(0, spaceIndex) : timestamp;

                var feedbackBox = new TaskHistoryObjects.GeneralFeedbackBox(sender, timestamp, feedback);
                feedbackBoxes.add(feedbackBox);
            }
            return feedbackBoxes;
        }

        public List<String> getProductsDisplayed() {
            return getTextFromElements(PRODUCTS_LOCATOR);
        }

        public TaskHistoryObjects.TaskHistoryDetailsData getTaskHistoryDetails() {
            var timestamp = getTextFromElement(TIMESTAMP_LOCATOR).replace("Task Details: ", "");
            var assignmentName = getTextFromElement(ASSIGNMENT_NAME_LOCATOR);
            var taskTitle = getTextFromElement(TASK_TITLE_LOCATOR);
            var submittedBy = getTextFromElement(SUBMITTED_BY_LOCATOR);
            var products = getProductsDisplayed();
            var feedbackBoxes = getGeneralFeedback();
            return new TaskHistoryObjects.TaskHistoryDetailsData(timestamp, assignmentName, taskTitle, submittedBy, products, feedbackBoxes);
        }

        public List<TaskHistoryObjects.TaskDetailsProperties> getTableData() throws Exception {
            List<TaskHistoryObjects.TaskDetailsProperties> tableData = new ArrayList<>();
            var rowsList = new ArrayList<>(findElementsVisibleMilliseconds(TABLE_ROWS));
            for (var row : rowsList) {
                var cellsList = new ArrayList<>(findElementsWithinAnotherElement(row, By.tagName("td")));
                var rowData = new TaskHistoryObjects.TaskDetailsProperties(
                        getValueAndMarkup(cellsList.get(0)),
                        getValueAndMarkup(cellsList.get(1)),
                        getValueAndMarkup(cellsList.get(2)),
                        getValueMarkupDiff(cellsList.get(3))
                );

                tableData.add(rowData);
            }
            return tableData;
        }

        private DiffUtility.Diff getValueAndMarkup(WebElement cell) throws Exception {
            String value;
            var cellText = cell.getText();
            if (cellText.isEmpty()) {
                try {
                    value = cell.findElement(By.tagName("a")).getAttribute("href");
                    return new DiffUtility.Diff(DiffUtility.Operation.EQUAL, value);
                } catch (Exception e) {
                    return null;
                }
            } else {
                return new DiffUtility.Diff(getValueOperation(cell), cellText);
            }
        }

        private DiffUtility.Operation getValueOperation(WebElement cell) throws Exception {
            var cssProperty = "text-decoration";
            WebElement cellWordElement;
            try {
                cellWordElement = cell.findElement(By.xpath("./span"));
            } catch (Exception e) {
                return DiffUtility.Operation.EQUAL;
            }
            var color = cellWordElement.getCssValue(cssProperty);

            switch (color) {
                case "none solid rgb(0, 0, 0)":
                    return DiffUtility.Operation.EQUAL;
                case "line-through solid rgb(0, 0, 0)":
                    return DiffUtility.Operation.DELETE;
                case "none solid rgb(2, 64, 132)":
                    return DiffUtility.Operation.INSERT;
                default:
                    throw new Exception("Unknown color: " + color);
            }
        }

        private LinkedList<DiffUtility.Diff> getValueMarkupDiff(WebElement parent) {
            WebElement differenceSpan;
            try {
                differenceSpan = parent.findElement(By.xpath("descendant::span[@class='textDiffs']"));
            } catch (NoSuchElementException e) {
                try {
                    var url = parent.findElement(By.xpath("descendant::a")).getAttribute("href");
                    LinkedList<DiffUtility.Diff> diffAndMarkupList = new LinkedList<>();
                    diffAndMarkupList.add(new DiffUtility.Diff(DiffUtility.Operation.EQUAL, url));
                    return diffAndMarkupList;
                } catch (NoSuchElementException exception) {
                    return null;
                }
            }

            // Get the inner HTML of the span element
            String innerHTML = differenceSpan.getAttribute("innerHTML");

            LinkedList<DiffUtility.Diff> diffAndMarkupList = new LinkedList<>();

            // Parse the HTML using Jsoup
            var doc = Jsoup.parse(innerHTML);

            List<org.jsoup.nodes.Node> childNodes = doc.body().childNodes();
            for (var node : childNodes) {
                if (node instanceof org.jsoup.nodes.TextNode) {
                    var textNode = (org.jsoup.nodes.TextNode) node;
                    var text = textNode.text();
                    if (!text.isEmpty()) {
                        var chunks = text.split(" ");
                        Arrays.stream(chunks).map(chunk -> new DiffUtility.Diff(DiffUtility.Operation.EQUAL, chunk)).forEach(diffAndMarkupList::add);
                    }
                } else {
                    var element = (org.jsoup.nodes.Element) node;
                    var tagName = element.tagName();
                    var text = element.text();
                    var markupType = tagName.equals("del") ? DiffUtility.Operation.DELETE : DiffUtility.Operation.INSERT;
                    var chunks = text.split(" ");
                    Arrays.stream(chunks).map(chunk -> new DiffUtility.Diff(markupType, chunk)).forEach(diffAndMarkupList::add);
                }
            }
            return diffAndMarkupList.isEmpty() ? null : diffAndMarkupList;
        }
    }
}
