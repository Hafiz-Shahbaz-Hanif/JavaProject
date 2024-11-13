package com.DC.pageobjects.adc.identify.executiveDashboard;

import com.DC.db.identify.SOVQueries;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.CommonFeatures;
import com.DC.utilities.SnowflakeUtility;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.openqa.selenium.*;
import org.testng.Assert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.*;

import static com.DC.testcases.BaseClass.LOGGER;

public class ShareOfVoicePage extends NetNewNavigationMenu {

    public DateAndIntervalPickerPage dateAndIntervalPickerPage;
    public DCFilters dcFilters;
    public CommonFeatures commonFeatures;
    private static final By SOV_HEADER = By.xpath("//a[text()='Share of Voice']");
    private static final By APPLY_BUTTON = By.xpath("//button[text()='Apply']");
    private static final By ALL_TERMS_COLUMN_HEADER = By.xpath("//h3[text()='All Terms']");
    private static final By AMAZON_SFR_COLUMN_HEADER = By.xpath("//h3[text()='Amazon SFR']");
    private static final By AVERAGE_SCORE_COLUMN_HEADER = By.xpath("//h3[text()='Average Score']");
    private static final By EXPORT_ICON = By.xpath("//span[text()='download']");
    private static final By SHADING_SELECTOR = By.id("shading-selector-selector");
    private static final By PASS_SHADING_VALUE = By.xpath("//ul[@role='listbox']//div[@class='MuiBox-root css-32tonw']/div//input");
    private static final By FAIL_SHADING_VALUE = By.xpath("//ul[@role='listbox']//div[@class='MuiBox-root css-j7y42f']/div//input");
    private static final By WEIGHT_SEARCH_TERM_TOGGLE = By.xpath("//div[@id='weight-search-term-box']//input/parent::span");
    private static final By WEIGHT_RANK_TOGGLE = By.xpath("//div[@id='weight-rank-box']//input/parent::span");
    private static final By SEARCH_TERM = By.xpath("(//div[@class='ag-center-cols-container']//div[@class='MuiBox-root css-j4fxw8'])[1]");
    private static final By SHOW_COMPETITORS_TOGGLE = By.xpath("(//input[@class='PrivateSwitchBase-input MuiSwitch-input css-1m9pwf3'])[1]/parent::span");
    private static final By VIEW_BY_DROPDOWN = By.id("view-by-select");
    private static final By REPORT_PAGE_TITLE = By.xpath("//h4[text()='Share of Voice by Retailer']");
    private static final By CLOSE_ICON = By.xpath("//span[text()='close']");
    private static final By RETAILERS_IN_DATA = By.id("price-table-selected-retailer");
    private static final By NO_RETAILERS_SELECTED = By.xpath("//p[text()='No Retailers Selected']");
    private static final By SEARCH_TERM_BAR = By.id("searchTermPicker");
    private static final By CLEAR_SEARCH_TERMS_BUTTON = By.xpath("//div[@id='custom-popup']//preceding-sibling::div[@id='selectedSearchTermsContainer']//*[@data-testid='CancelIcon']");
    private static final By SEARCH_TERM_APPLY_BUTTON = By.id("searchTermApplyButton");
    private static final By CLEAR_ALL_BUTTON_SEARCH_TERMS = By.id("searchTermClearAllButton");
    private static final By HOUR_SELECTOR = By.id("hour-selector");

    public ShareOfVoicePage(WebDriver driver) {
        super(driver);
        findElementVisible(SOV_HEADER);
        dateAndIntervalPickerPage = new DateAndIntervalPickerPage(driver);
        dcFilters = new DCFilters(driver);
        commonFeatures = new CommonFeatures(driver);
    }

    public boolean verifyDisplayOfCentralSOVScreen() {
        return isElementVisible(SOV_HEADER);
    }

    public List<String> getDataRetailers() {
        WebElement dataRetailersElement = findElementVisible(RETAILERS_IN_DATA);
        String[] retailers = dataRetailersElement.getText().split(": ");
        String[] retailerNames = retailers[1].split(",");
        List<String> retailerList = new ArrayList<>();

        for (String retailer : retailerNames) {
            retailerList.add(retailer.trim());
        }

        return retailerList;
    }

    public boolean verifyDisplayOfAllTermsColumnHeader() {
        return isElementVisible(ALL_TERMS_COLUMN_HEADER);
    }

    public boolean verifyDisplayOfAmazonSFRColumnHeader() {
        return isElementVisible(AMAZON_SFR_COLUMN_HEADER);
    }

    public boolean verifyDisplayOfAverageScoreColumnHeader() {
        return isElementVisible(AVERAGE_SCORE_COLUMN_HEADER);
    }

    public int getAverageScoreColumnValueFromUI() {
        findElementVisible(AVERAGE_SCORE_COLUMN_HEADER);
        WebElement averageScoreColumn = findElementVisible(By.xpath("//span[@id='cell-pivot.averageScore-816']"));
        String scoreText = averageScoreColumn.getText();
        String scoreWithoutPercent = scoreText.replaceAll("%", "");

        return Integer.parseInt(scoreWithoutPercent);
    }

    public int getOverallScoresValueFromUI() {
        WebElement scoreElement = findElementVisible(By.xpath("(//div[@class='ag-floating-top-viewport']//p)[1]"));

        String scoreElementText = scoreElement.getText();
        String scoreWithoutPercent = scoreElementText.replaceAll("%", "");

        return Integer.parseInt(scoreWithoutPercent);
    }


    public int calculateAverageScoreAmongColumns() {
        List<WebElement> averageScoreColumn = findElementsVisible(By.xpath("//div[@class='ag-pinned-left-cols-container']//div[@col-id='averageScore']"));
        int sum = 0;
        for (WebElement score : averageScoreColumn) {
            String scoreText = score.getText();
            String scoreWithoutPercent = scoreText.replaceAll("%", "");
            int scoreValue = Integer.parseInt(scoreWithoutPercent);

            sum += scoreValue;
        }
        return sum / averageScoreColumn.size();
    }

    public int calculateAverageOverallAmongColumns() {
        List<WebElement> averageOverallColumn = findElementsVisible(By.xpath("//div[@class='ag-center-cols-clipper']//div[@col-id='amazon.com']"));
        int sum = 0;
        for (WebElement score : averageOverallColumn) {
            String scoreText = score.getText();
            String scoreWithoutPercent = scoreText.replaceAll("%", "");
            int scoreValue = Integer.parseInt(scoreWithoutPercent);

            sum += scoreValue;
        }
        return sum / averageOverallColumn.size();
    }

    public boolean verifyDisplayOfDownloadIcon() {
        return isElementVisible(EXPORT_ICON);
    }

    public String getReportPageTitle() {

        return findElementVisible(REPORT_PAGE_TITLE).getText();
    }

    public boolean verifyDisplayOfShadingSelector() {
        return isElementVisible(SHADING_SELECTOR);
    }

    public void clickShadingSelector() throws InterruptedException {
        click(SHADING_SELECTOR);
    }

    public boolean verifyDefaultShading() throws InterruptedException {

        clickShadingSelector();
        WebElement passShadingValue = findElementVisible(PASS_SHADING_VALUE);
        WebElement failShadingValue = findElementVisible(FAIL_SHADING_VALUE);
        return passShadingValue.getAttribute("value").equals("50") && failShadingValue.getAttribute("value").equals("25");
    }

    public boolean verifyDisplayOfWeightSearchTermToggle() {
        return isElementVisible(WEIGHT_SEARCH_TERM_TOGGLE);
    }

    public boolean verifyDisplayOfWeightRankToggle() {
        return isElementVisible(WEIGHT_RANK_TOGGLE);
    }

    public By getWeightSearchTermToggle() {
        return WEIGHT_SEARCH_TERM_TOGGLE;
    }

    public By getWeightRankToggle() {
        return WEIGHT_RANK_TOGGLE;
    }

    public void toggleValidation(By toggleLocator, String toggleName) throws InterruptedException {
        WebElement toggle = findElementVisible(toggleLocator);
        String classAttribute = toggle.getAttribute("class");
        if (classAttribute.contains("Mui-checked")) {
            LOGGER.info(toggleName + " toggle ON");
            click(toggleLocator);
            classAttribute = toggle.getAttribute("class");
            if (classAttribute.contains("Mui-checked")) {
                LOGGER.error(toggleName + " toggle ON");
                Assert.fail(toggleName + " toggle ON");
            } else {
                LOGGER.info(toggleName + " toggle OFF");
            }
        } else {
            LOGGER.error(toggleName + " toggle OFF");
            Assert.fail(toggleName + " toggle OFF");
        }
    }

    public void verifyTableInDetailsPageIncludesSpecifiedColumns() throws InterruptedException {
        click(SEARCH_TERM);
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR, Duration.ofSeconds(20));
        List<WebElement> columns = findElementsVisible(By.xpath("//h3[text()='Rank']/ancestor::*[@class='ag-header-row ag-header-row-column']/div//h3 | //h3[text()='Rank']/ancestor::*[@class='ag-header-row ag-header-row-column']//div[@id='rank-change-box']/div[1]/span[1]"));

        List<String> columnTexts = new ArrayList<>();
        for (WebElement column : columns) {
            columnTexts.add(column.getText());
        }

        UI_LOGGER.info("Columns in the table in the details page: " + columnTexts);
        Assert.assertEquals(Arrays.asList("Rank", "Image", "Product Title", "Change in Rank", "Brand", "Manufacturer", "Price", "Availability"), columnTexts, "Table in the details page does not include the specified columns.");
    }

    public void closeDetailsPage() throws InterruptedException {
        click(CLOSE_ICON);
    }

    public void clickCompetitorToggle() throws InterruptedException {
        click(SHOW_COMPETITORS_TOGGLE);
        click(APPLY_BUTTON);
    }

    public void verifyCompetitorOptionEnabled(String searchTermName) throws InterruptedException {

        dateAndIntervalPickerPage.selectDateRange("Last Month");
        Thread.sleep(5000);
        findElementVisible(By.xpath("//span[text()='Average Overall']"));
        WebElement headerBeforeCompetitorsToggle = findElementVisible(By.xpath("//h4[@class='MuiTypography-root MuiTypography-h4 css-5tv2zv']"));
        String headerBeforeCompetitorsToggleText = headerBeforeCompetitorsToggle.getText();
        LOGGER.info("Header before competitors toggle: " + headerBeforeCompetitorsToggleText);
        clickCompetitorToggle();
        clearSearchTermBarAndEnterNewSearchTerm(searchTermName);
        WebElement headerAfterCompetitorsToggle = findElementVisible(By.xpath("//h4[@class='MuiTypography-root MuiTypography-h4 css-5tv2zv']"));
        String headerAfterCompetitorsToggleText = headerAfterCompetitorsToggle.getText();
        LOGGER.info("Header after competitors toggle: " + headerAfterCompetitorsToggleText);
        Assert.assertNotEquals(headerBeforeCompetitorsToggleText, headerAfterCompetitorsToggleText, "Share of Voice by Retailer header does not change to Share of Voice by Term.");

    }

    public String getDefaultViewByOption() {
        WebElement defaultViewByOption = findElementVisible(VIEW_BY_DROPDOWN);
        return defaultViewByOption.getText();
    }

    public void clickExportIcon() throws InterruptedException {
        click(EXPORT_ICON);
    }

    public boolean isToggleDisabled() {
        WebElement toggle = findElementVisible(By.xpath("//p[text()='Average Search Term Scores']/preceding-sibling::span/span[1]"));
        String disabledAttribute = toggle.getAttribute("aria-disabled");
        LOGGER.info("Toggle disabled attribute: " + disabledAttribute);
        return disabledAttribute.equalsIgnoreCase("true");
    }

    public boolean isToggleEnabled() {
        WebElement toggle = findElementVisible(By.xpath("//p[text()='Average Search Term Scores']/preceding-sibling::span/span[1]"));
        if (toggle.getAttribute("aria-disabled") == null) {
            LOGGER.info("Toggle is enabled");
            return true;
        } else {
            LOGGER.info("Toggle is disabled");
            return false;
        }
    }

    public boolean verifyCancelButtonIsWorking() {
        return isElementPresent(NO_RETAILERS_SELECTED);
    }

    public boolean verifyShadingColor() throws InterruptedException {
        By cellXpath = By.xpath("//div[@class='ag-center-cols-container']//div//div[@class='ag-cell-wrapper']//div");
        clickShadingSelector();
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR, Duration.ofSeconds(10));
        return commonFeatures.verifyShadingColor(cellXpath);
    }

    public void clearSearchTermBarAndEnterNewSearchTerm(String searchTerm) throws InterruptedException {

        click(SEARCH_TERM_BAR);
        if (isElementPresent(CLEAR_SEARCH_TERMS_BUTTON)) {
            click(CLEAR_SEARCH_TERMS_BUTTON);
            sendKeys(SEARCH_TERM_BAR, searchTerm);
            WebElement selectedSearchTerm = findElementVisible(By.xpath("//div[@id='custom-popup']//li//h6[text()='" + searchTerm + "']"));
            selectedSearchTerm.click();
            click(SEARCH_TERM_APPLY_BUTTON);
        } else {
            sendKeys(SEARCH_TERM_BAR, searchTerm);
            WebElement selectedSearchTerm = findElementVisible(By.xpath("//div[@id='custom-popup']//li//h6[text()='" + searchTerm + "']"));
            selectedSearchTerm.click();
            click(SEARCH_TERM_APPLY_BUTTON);
        }
    }

    public void searchForSearchTerm(List<String> searchTerms, String placementType, String... retailer) throws InterruptedException {
        dcFilters.selectMultipleRetailers(retailer);
        selectSearchTermFromList(searchTerms);
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR, Duration.ofSeconds(20));
        commonFeatures.selectPlacementType(placementType);
    }

    public boolean getToggleStatus(By toggleLocator) {
        WebElement toggle = findElementVisible(toggleLocator);
        String classAttribute = toggle.getAttribute("class");
        if (classAttribute.contains("Mui-checked")) {
            LOGGER.info("Toggle is ON");
            return true;
        } else {
            LOGGER.info("Toggle is OFF");
            return false;
        }
    }

    public List<String> getRanksFromUI() {
        By rowLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index]");
        int numberOfRows = getElementCount(rowLocator);
        List<String> statusSet = new ArrayList<>();

        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);

        for (int i = 0; i < numberOfRows; i++) {
            scrollIntoView(rowLocator);
            By itemLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index='" + i + "']/div[@col-id='pivot.searchTerm']//span");
            By sovValuesInUI = By.xpath("//div[@class='ag-center-cols-container']//div[@row-index='" + i + "']//div[contains(@id,'variation-cell-box')]/ancestor::div[@role='gridcell']//span/div");

            int maxRetries = 3;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try {
                    getTextFromElement(itemLocator);
                    List<WebElement> statuses = findElementsVisible(sovValuesInUI);
                    getTextFromElements(statuses);

                    for (WebElement statusElement : statuses) {
                        String statusText = statusElement.getText();
                        String modifiedStatus = statusText.replaceAll("%", "");
                        statusSet.add(modifiedStatus);
                    }
                    break;

                } catch (StaleElementReferenceException e) {
                    UI_LOGGER.info("Stale element exception occurred. Retrying...");
                    retryCount++;
                }
            }
        }
        Collections.sort(statusSet);
        return statusSet;
    }

    public void selectSearchTermFromList(List<String> searchTerm) throws InterruptedException {
        if (isElementPresent(CLEAR_ALL_BUTTON_SEARCH_TERMS)) {
            click(CLEAR_ALL_BUTTON_SEARCH_TERMS);
        }
        for (String term : searchTerm) {
            click(SEARCH_TERM_BAR);
            sendKeys(SEARCH_TERM_BAR, term);
            List<WebElement> selectedSearchTerm = findElementsVisible(By.xpath("//div[@id='custom-popup']//li//h6[text()='" + term + "']"));
            for (WebElement termElement : selectedSearchTerm) {
                termElement.click();
            }

            try {
                click(SEARCH_TERM_APPLY_BUTTON);
            } catch (StaleElementReferenceException e) {
                UI_LOGGER.info("Stale element exception occurred. Retrying...");
                click(SEARCH_TERM_APPLY_BUTTON);
            }
        }
    }

    public void clickToggle(By toggleLocator) {
        WebElement toggle = findElementVisible(toggleLocator);
        toggle.click();
        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);
    }

    public Map<String, String> getValuesFromUI(String colId) {
        Map<String, String> sfrValues = new HashMap<>();

        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);

        List<WebElement> itemElements = findElementsVisible(By.xpath("//div[@class='ag-pinned-left-cols-container']//a"));
        List<WebElement> valueElements = findElementsVisible(By.xpath("//div[@class='ag-pinned-left-cols-container']//p[@id='" + colId + "'] | //div[@class='ag-center-cols-container']//p[@id='" + colId + "'] "));

        if (itemElements.size() == valueElements.size()) {
            for (int i = 0; i < itemElements.size(); i++) {
                String itemName = itemElements.get(i).getText();
                String itemValue = valueElements.get(i).getText();
                if (itemValue.contains(",")) {
                    itemValue = itemValue.replaceAll(",", "");
                } else if (itemValue.contains("%")) {
                    itemValue = itemValue.replaceAll("%", "");
                }
                sfrValues.put(itemName, itemValue);
            }
        } else {
            Assert.fail("Item and value elements are not equal in size");
        }

        UI_LOGGER.info("Values from UI: " + sfrValues);
        return sfrValues;
    }

    public Map<String, List<String>> getSOVsFromUI() {
        By rowLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index]");
        int numberOfRows = getElementCount(rowLocator);
        Map<String, List<String>> statusSet = new HashMap<>();

        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);

        for (int i = 0; i < numberOfRows; i++) {
            Map<String, List<String>> valuesMap = new HashMap<>();
            scrollIntoView(rowLocator);
            By itemLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index='" + i + "']/div[@col-id='pivot.searchTerm']//span");
            By sovValuesInUI = By.xpath("//div[@class='ag-center-cols-container']//div[@row-index='" + i + "']//div[contains(@id,'variation-cell-box')]/ancestor::div[@role='gridcell']//span/div");

            int maxRetries = 3;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try {
                    String searchTerm = getTextFromElement(itemLocator);
                    List<WebElement> statuses = findElementsVisible(sovValuesInUI);
                    getTextFromElements(statuses);

                    List<String> statusValues = new ArrayList<>();
                    for (WebElement statusElement : statuses) {
                        String statusText = statusElement.getText();
                        if (!statusText.equalsIgnoreCase("N/A")) {
                            String modifiedStatus = statusText.replaceAll("%", "");
                            statusValues.add(modifiedStatus);
                        }
                    }
                    valuesMap.put(searchTerm, statusValues);
                    break;

                } catch (StaleElementReferenceException e) {
                    UI_LOGGER.info("Stale element exception occurred. Retrying...");
                    retryCount++;
                }
            }
            statusSet.putAll(valuesMap);
        }
        LOGGER.info("Ranks from UI: " + statusSet);
        return statusSet;
    }

    public void verifyDetailFlyoutValuesMirrorMainPage(String placement) throws InterruptedException {
        Map<String, List<String>> ranks = getSOVsFromUI();
        for (Map.Entry<String, List<String>> entry : ranks.entrySet()) {
            List<String> value = entry.getValue();
            for (String rank : value) {
                click(By.xpath("//div[@class='ag-center-cols-container']//span[@class='ag-cell-value']//p[contains(text(), '" + rank + "')]"));
                String informationFromFlyout = getTextFromElement(By.xpath("//div[@id='detail-page-box-upper']"));
                String[] parts = informationFromFlyout.split("\n");

                String searchTerm = parts[0].replace("\"", "").trim();
                String rankValue = parts[1].replace("%", "");
                String placementType = parts[2];

                Assert.assertEquals(rank, rankValue, "Rank value is not matching Main page and Detail flyout");
                Assert.assertEquals(searchTerm, entry.getKey(), "Search term is not matching Main page and Detail flyout");
                Assert.assertEquals(placementType, placement, "Placement type is not matching Main page and Detail flyout");
            }
        }
        LOGGER.info("Detail flyout values are verified");
    }

    public Map<String, List<String>> getItemsAndRanksFromUI() {
        By rowLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index]");
        int numberOfRows = getElementCount(rowLocator);
        Map<String, List<String>> statusSet = new HashMap<>();

        waitForElementToBeInvisible(CommonFeatures.LOADING_BAR);

        for (int i = 0; i < numberOfRows; i++) {
            Map<String, List<String>> valuesMap = new HashMap<>();
            scrollIntoView(rowLocator);
            By itemLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index='" + i + "']/div[@col-id='pivot.searchTerm']//span");
            By sovValuesInUI = By.xpath("//div[@class='ag-center-cols-container']//div[@row-index='" + i + "']//div[contains(@id,'variation-cell-box')]/ancestor::div[@role='gridcell']//span/div");

            int maxRetries = 3;
            int retryCount = 0;

            while (retryCount < maxRetries) {
                try {
                    String searchTerm = getTextFromElement(itemLocator);
                    List<WebElement> statuses = findElementsVisible(sovValuesInUI);
                    getTextFromElements(statuses);

                    List<String> statusValues = new ArrayList<>();
                    for (WebElement statusElement : statuses) {
                        String statusText = statusElement.getText();
                        if (!statusText.equalsIgnoreCase("N/A")) {
                            String modifiedStatus = statusText.replaceAll("%", "");
                            statusValues.add(modifiedStatus);
                        }
                    }
                    valuesMap.put(searchTerm, statusValues);
                    break;

                } catch (StaleElementReferenceException e) {
                    UI_LOGGER.info("Stale element exception occurred. Retrying...");
                    retryCount++;
                }
            }
            statusSet.putAll(valuesMap);
        }
        return statusSet;
    }

    public String getAverageOverallRankFromUI() {
        By averageOverallRankLocator = By.xpath("//div[@class='ag-floating-top-container']//p[@id='label-avf-score']");
        return getTextFromElement(averageOverallRankLocator).replaceAll("%", "");
    }

    public double getWeightFromDB(int SFRNumber, String date) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        double weight = 0;

        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SOVQueries.queryToCalculateWeight(SFRNumber, date))) {

                while (resultSet.next()) {
                    weight = resultSet.getDouble("WEIGHT");
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return weight;
    }

    public Map<String, List<String>> getSOVValuesFromDB(String BU, String startDate, String endDate, String retailerPlatform, String placementType, List<String> searchTerms) {
        SnowflakeUtility su = new SnowflakeUtility();
        Connection con;
        Map<String, List<String>> valuesMap = new HashMap<>();

        try {
            con = su.getConnection();
            try (Statement statement = con.createStatement();
                 ResultSet resultSet = statement.executeQuery(SOVQueries.queryToFetchSOVStatuses(BU, startDate, endDate, retailerPlatform, placementType, searchTerms))) {

                while (resultSet.next()) {
                    String searchTermFromDB = resultSet.getString("SEARCH_TERM");
                    double weightedValueInDB = Double.parseDouble(resultSet.getString("WEIGHTED_SHARE"));

                    if (valuesMap.containsKey(searchTermFromDB)) {
                        valuesMap.get(searchTermFromDB).add(String.valueOf(weightedValueInDB));
                    } else {
                        List<String> valuesList = new ArrayList<>();
                        valuesList.add(String.valueOf(weightedValueInDB));
                        valuesMap.put(searchTermFromDB, valuesList);
                    }
                }
            } catch (SQLException e) {
                Assert.fail("Exception running the query. Exception: " + e.getMessage());
            } finally {
                su.closeConnection(con);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Values from DB: " + valuesMap);
        return valuesMap;
    }

    public String calculateAverageOverallUnweighted() {
        By rowLocator = By.xpath("//div[@class='ag-pinned-left-cols-container']/div[@row-index]");
        int numberOfRows = getElementCount(rowLocator);
        int sumOfRanks = 0;
        List<String> rankList = new ArrayList<>();

        Map<String, List<String>> rankValues = getItemsAndRanksFromUI();
        for (String key : rankValues.keySet()) {
            List<String> rankValue = rankValues.get(key);
            rankList.add(rankValue.get(0));
        }
        for (String rank : rankList) {
            sumOfRanks += Integer.parseInt(rank);
        }

        return String.valueOf(sumOfRanks / numberOfRows);
    }

    public String calculateAverageOverallWeighted(String BU, String startDate, String endDate, String retailerPlatform, String placementType, List<String> searchTerms) {
        Map<String, String> sfrValues = getValuesFromUI("sfr-cell-renderer");
        Map<String, List<String>> rankValuesFromDB = getSOVValuesFromDB(BU, startDate, endDate, retailerPlatform, placementType, searchTerms);

        List<Double> rankTimesWeightList = new ArrayList<>();
        List<Double> weightCalculatedList = new ArrayList<>();

        for (String key : sfrValues.keySet()) {
            String sfrValue = sfrValues.get(key);
            List<String> rankList = rankValuesFromDB.get(key);

            if (rankList != null && !rankList.isEmpty()) {
                int sfrValueInt = Integer.parseInt(sfrValue);
                double weightCalculated = getWeightFromDB(sfrValueInt, startDate);
                weightCalculatedList.add(weightCalculated);

                double rankTimesWeight = Double.parseDouble(rankList.get(0)) * weightCalculated;
                rankTimesWeightList.add(rankTimesWeight);
            }
        }

        double sumOfWeightCalculated = calculateSumOfWeight(weightCalculatedList);
        double sumOfRankTimesWeight = calculateSumOfRankTimesWeight(rankTimesWeightList);
        double weightedCombinedAverageTimes100 = calculateWeightedCombinedAverage(sumOfWeightCalculated, sumOfRankTimesWeight);
        int weightedCombinedAverageTimes100Int = (int) Math.round(weightedCombinedAverageTimes100);
        return String.valueOf(weightedCombinedAverageTimes100Int);
    }

    private double calculateSumOfWeight(List<Double> weightCalculatedList) {
        double sumOfWeightCalculated = 0;
        for (double weight : weightCalculatedList) {
            sumOfWeightCalculated += weight;
        }
        return sumOfWeightCalculated;
    }

    private double calculateSumOfRankTimesWeight(List<Double> rankTimesWeightList) {
        double sumOfRankTimesWeight = 0;
        for (double rankTimesWeightValue : rankTimesWeightList) {
            sumOfRankTimesWeight += rankTimesWeightValue;
        }
        return sumOfRankTimesWeight;
    }

    private double calculateWeightedCombinedAverage(double sumOfWeightCalculated, double sumOfRankTimesWeight) {
        return sumOfRankTimesWeight / sumOfWeightCalculated * 100;
    }

    public String getHourValue() throws InterruptedException {
        Thread.sleep(4000);
        return getTextFromElement(HOUR_SELECTOR);
    }

    public WebElement findRowElement(int rowNumber) {
        By rowLocator = By.xpath("(//div[@class='ag-center-cols-container'])[2]/div[@row-index='" + rowNumber + "']");
        return findElementVisible(rowLocator);
    }

    public String getRPCValueFromUI(int rowNumber) {
        WebElement rowLocatorInDetailPage = findRowElement(rowNumber);
        return rowLocatorInDetailPage.findElement(By.xpath(".//div[@col-id='rpc']//a")).getText();
    }

    public String getRankValueFromUI(int rowNumber) {
        WebElement rowLocatorInDetailPage = findRowElement(rowNumber);
        String rankValue = rowLocatorInDetailPage.findElement(By.xpath(".//div[@col-id='rank']//span/div")).getText();
        return rankValue.substring(0, 1);
    }

    public String getRankChangeValueFromUI(int rowNumber) {
        WebElement rowLocatorInDetailPage = findRowElement(rowNumber);
        return rowLocatorInDetailPage.findElement(By.xpath(".//div[@col-id='rankChange']//p")).getText();
    }
}