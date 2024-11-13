//TODO - Class not used as of 7/17/23 - Will revisit once this page makes it to the merge app
// Class uses page factory - when revisit, will need to update to use PageHandler

package com.DC.pageobjects.retail.salesTrends;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.DC.pageobjects.PageHandler;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

/*
public class RoundUpPage extends PageHandler {

    WebDriver FDdriver;
    Actions actions;
    WebDriverWait wdWait;
    public Logger logger;
    CommonMethods commonMethods;
    Filters filters;
    JavascriptExecutor js;
    SoftAssert softAssert;
    Select select;

    By loadingIcon = By.xpath("//div[@class='highcharts-loading']/span");

    public RoundUpPage(WebDriver rdriver) {
        super(rdriver);
        FDdriver = rdriver;
        actions = new Actions(FDdriver);
        PageFactory.initElements(rdriver, this);
        wdWait = new WebDriverWait(FDdriver, 30);
        logger = Logger.getLogger("fila");
        commonMethods = new CommonMethods(FDdriver);
        filters = new Filters(FDdriver);
        js = (JavascriptExecutor) FDdriver;
        softAssert = new SoftAssert();
    }

    @FindBy(id = "tilePercentVal_11")
    WebElement shippedCOGSMonthPeriodTilePercentageValue;

    @FindBy(id = "tilePercentVal_13")
    WebElement shippedCOGSLastYearTilePercentageValue;

    @FindBy(id = "tilePercentVal_21")
    WebElement aspMonthPeriodTilePercentageValue;

    @FindBy(id = "tilePercentVal_23")
    WebElement aspLastYearTilePercentageValue;

    @FindBy(xpath = "(//span[@title = 'Filter By ASIN']/child::*[@class = 'fa fa-filter'])[1]")
    WebElement firstFilterByAsinIcon;

    WebElement firstAsinValueInTheProductTable;

    @FindBy(xpath = "//div[@col-id = 'asin' and contains(@class, 'ag-header-cell')]")
    WebElement asinColumnHeader;

    @FindBy(xpath = "//div[@col-id='asin']//span[@id='filterMenu']")
    WebElement asinColumnFilterIcon;

    @FindBy(name = "filterType")
    WebElement filterTypeDropdown;

    @FindBy(xpath = "//input[@name = \"filterText\" and @type = \"text\"]")
    WebElement filterValueTextField;

    @FindBy(xpath = "//button[@type = \"submit\"]/child::i[@class = \"fa fa-check pr-2\"]")
    WebElement applyButton;

    List<WebElement> asinValuesInTheProductTable;

    @FindBy(xpath = "//button[@type = \"button\"]/child::i[@class = \"fa fa-undo pr-2\"]")
    WebElement resetButton;

    @FindBy(id = "shipped-cogs-by-category")
    WebElement shippedCOGSByCategoryTable;

    @FindBy(id = "shipped-cogs-by-sub-category")
    WebElement shippedCOGSBySubCategoryTable;

    @FindBy(id = "shipped-cogs-by-segment")
    WebElement shippedCOGSBySegmentTable;

    @FindBy(id = "shipped-cogs-by-product")
    WebElement shippedCOGSByProductTable;

    @FindBy(xpath = "//div[./div/h6[@id='shipped-cogs-by-sub-category']]/div/button[1]")
    WebElement shippedCOGSBySubCategoryExpandIcon;

    @FindBy(xpath = "//div[./div/h6[@id='shipped-cogs-by-segment']]/div/button[1]")
    WebElement shippedCOGSBySegmentExpandIcon;

    @FindBy(xpath = "//div[@aria-labelledby='modal-modal-title']/div/div/div/span")
    WebElement closeIcon;

    @FindBy(id = "interval-selector")
    WebElement intervalDropDown;

    @FindBy(xpath = "//*[local-name()='div' and ./div/div/h6[@id='shipped-cogs-by-category']]//*[name()='g']//*[name()='g']//*[name()='text']")
    List<WebElement> listOfShippedCOGSByCategoryMonthsData;

    @FindBy(xpath = "//*[local-name()='div' and ./div/div/h6[@id='shipped-cogs-by-sub-category']]//*[name()='g']//*[name()='g']//*[name()='text']")
    List<WebElement> listOfShippedCOGSBySubCategoryMonthsData;

    @FindBy(xpath = "//*[local-name()='div' and ./div/div/h6[@id='shipped-cogs-by-segment']]//*[name()='g']//*[name()='g']//*[name()='text']")
    List<WebElement> listOfShippedCOGSBySegmentMonthsData;

    @FindBy(xpath = "//*[local-name()='div' and ./div/div/h6[@id='shipped-cogs-by-product']]//*[name()='g']//*[name()='g']//*[name()='text']")
    List<WebElement> listOfShippedCOGSByProductMonthsData;

    private String cogsUnitRevenueFilter = "//app-selectize[select[@id='COGS, Units, Revenue']]";
    private String cogsUnitRevenueFilterValues = "//div[@class='selectize-dropdown single plugin-dropdown_direction direction-down' and contains(@style,'visible')]/div/div";
    private String distributorViewDefaultValue = "//div[@id='sidebar-filter-distributor-view']//input";
    private String btnSave = "//button[text()='Save']";
    private String ChkBoxCallouts = "//div[text()='Callouts']/span";
    private String calloutValues = "//*[local-name()='div' and ./div/div/h6[@id='shipped-cogs-yoy-comparison-arap']]//*[name()='g']//*[name()='text']//*[name()='tspan' and @class='highcharts-text-outline']";
    private String avgSellingPrice = "//*[local-name()='svg']//*[name()='g' and @class='highcharts-legend-item highcharts-spline-series highcharts-color-undefined highcharts-series-3']//*[name()='text' and text()='Avg. Selling Price']";
    private String firstSegmentShippedCogsBySegment = "//div[./div/div/h6[@id='shipped-cogs-by-segment']]//div[@aria-rowindex='2']/div[@col-id='segmentationValue']";
    private String firstSegmentFilterShippedCogsBySegment = "//div[./div/div/h6[@id='shipped-cogs-by-segment']]//div[@aria-rowindex='2']/div[@col-id='filter']//button[1]";
    private String firstSubCategoryShippedCogsBySubCategory = "//div[./div/div/h6[@id='shipped-cogs-by-sub-category']]//div[@aria-rowindex='2']/div[@col-id='segmentationValue']";
    private String firstSubCategoryFilterShippedCogsBySubCategory = "//div[./div/div/h6[@id='shipped-cogs-by-sub-category']]//div[@aria-rowindex='2']/div[@col-id='filter']//button[1]";
    private String segmentFilterValue = "//div[@id='sidebar-filter-segment']//div[@role='button']/span";
    private String subCategoryFilterValue = "//div[@id='sidebar-filter-sub-category']//div[@role='button']/span";
    private String asinsFilterValue = "//div[@id='sidebar-filter-asins']//div[@role='button']/span";
    private String highChartsLoading = "//span[@role='progressbar']";
    private String firstAsinShippedCogsByProduct = "//div[./div/div/h6[@id='shipped-cogs-by-product']]//div[@aria-rowindex='2']/div[@col-id='asin']/div/span";
    private String firstAsinShippedCogsByProductFilter = "//div[./div/div/h6[@id='shipped-cogs-by-product']]//div[@aria-rowindex='2']/div[@col-id='filter']//button[1]";
    private String firstAsinShippedCogsByProductDetails = "//div[./div/div/h6[@id='shipped-cogs-by-product']]//div[@aria-rowindex='2']/div[@col-id='filter']//button[2]";
    private String firstAsinShippedCogsByProductGainersDrainers = "//div[./div/div/h6[@id='shipped-cogs-by-product']]//div[@aria-rowindex='2']/div[@col-id='filter']//button[3]";
    private String aspInfo = "//p[text()='ASP']/*[@data-testid='InfoIcon']";
    private String dateRangeDefault = "//div[@id='daterange-selector']//div/ul/div/div/span[contains(@style,'bold')]";
    private String dateRangeField = "//button[@id='date-range-selector']";
    private String intervalFilterWeeklyValue = "//div[@id='menu-']/div/ul/li[@data-value='Weekly']";
    private String asinDetailsPageAsinsVal = "//app-selectize[./select[@id='ASINs']]/div/div/div";
    private String shippedCOGSTileCurrencyValues = "//div[./div/div/p[text()='Shipped COGS']]/div/h6";
    private String aspTileCurrencyValues = "//div[./div/div/p[text()='ASP']]/div/h6";
    private String mtdFtdTileCurrencyValues = "//div[./div/div/p[text()='MTD - FYTD']]/div/h6";
    private String shippedCOGSTilePercentage = "//div[./div/div/p[text()='Shipped COGS']]/div/span[2]";
    private String aspTilePercentage = "//div[./div/div/p[text()='ASP']]/div/span[2]";
    private String priorityAsinsOnlyToggle = "//div[@id='sidebar-filter-priority-asi-ns-only']/span/span/span";
    private String headerShippedCogsByCategory = "//div[./div/div/h6[@id='shipped-cogs-by-category']]//div[@class='ag-header-container']/div/div/div/div/div/span[@ref='eText']";
    private String headerShippedCogsBySubCategory = "//div[./div/div/h6[@id='shipped-cogs-by-sub-category']]//div[@class='ag-header-container']/div/div/div/div/div/span[@ref='eText']";
    private String headerShippedCogsBySegment = "//div[./div/div/h6[@id='shipped-cogs-by-segment']]//div[@class='ag-header-container']/div/div/div/div/div/span[@ref='eText']";
    private String headerShippedCogsByProduct = "//div[./div/div/h6[@id='shipped-cogs-by-product']]//div[@class='ag-header-container']/div/div/div/div/div/span[@ref='eText']";
    private String modalHeaders = "//div[@aria-labelledby='modal-modal-title']//div[@class='ag-header-container']/div/div//div/span[@ref='eText']";
    private String shippedCogsByProductExpandIcon = "//div[@id='roundup-product']//*[name()='svg' and @data-testid='FullscreenIcon']";
    public static String shippedCogsByCategoryProgressBar = "//h6[@id='shipped-cogs-by-category']/../../..//span[@role='progressbar']";
    private String stackIconShippedCogsByCategory = "//h6[@id='shipped-cogs-by-category']/../..//*[@data-testid='MoreVertIcon']";
    private String shippedCogsByCategoryColumnHeaders = "//h6[@id='shipped-cogs-by-category']/../../..//div[@role='columnheader']";
    private String shippedCogsByCategoryCheckedColumnInStack = "//li[text()='Filter']/..//input[@type='checkbox' and @checked]";
    private String shippedCogsByCategoryColumnHeadersInStack = "//li[text()='Filter']/../li";
    private String stackIconShippedCogsBySegment = "//h6[@id='shipped-cogs-by-segment']/../..//*[@data-testid='MoreVertIcon']";
    private String shippedCogsBySegmentColumnHeaders = "//h6[@id='shipped-cogs-by-segment']/../../..//div[@role='columnheader']";
    private String shippedCogsBySegmentCheckedColumnInStack = "//li[text()='Filter']/..//input[@type='checkbox' and @checked]";
    private String shippedCogsBySegmentColumnHeadersInStack = "//li[text()='Filter']/../li";
    private String stackIconShippedCogsBySubCategory = "//h6[@id='shipped-cogs-by-sub-category']/../..//*[@data-testid='MoreVertIcon']";
    private String shippedCogsBySubCategoryColumnHeaders = "//h6[@id='shipped-cogs-by-sub-category']/../../..//div[@role='columnheader']";
    private String shippedCogsBySubCategoryCheckedColumnInStack = "//li[text()='Filter']/..//input[@type='checkbox' and @checked]";
    private String shippedCogsBySubCategoryColumnHeadersInStack = "//li[text()='Filter']/../li";
    private String stackIconShippedCogsByProduct = "//h6[@id='shipped-cogs-by-product']/../..//*[@data-testid='MoreVertIcon']";
    private String shippedCogsByProductColumnHeaders = "//h6[@id='shipped-cogs-by-product']/../../..//div[@role='columnheader']";
    private String shippedCogsByProductCheckedColumnInStack = "//li[text()='Filter']/..//input[@type='checkbox' and @checked]";
    private String shippedCogsByProductColumnHeadersInStack = "//li[text()='Filter']/../li";

    public void clickApply() {
        click(findElement(btnSave));
        wdWait.until(ExpectedConditions.invisibilityOfAllElements(FDdriver.findElements(By.xpath(highChartsLoading))));
    }

    public void shippedCogsTilesContainsCurrency() {
        logger.info("Verifying Currency Symbol in Shipped COGS tiles");
        List<WebElement> curremcyValueEle = FDdriver.findElements(By.xpath(shippedCOGSTileCurrencyValues));
        for (WebElement ele : curremcyValueEle) {
            if (!ele.getText().equalsIgnoreCase("-"))
                softAssert.assertTrue(ele.getText().contains("$"));
        }
    }

    public void aspTilesContainsCurrency() {
        logger.info("Verifying Currency Symbol in ASP tiles");
        List<WebElement> curremcyValueEle = FDdriver.findElements(By.xpath(aspTileCurrencyValues));
        for (WebElement ele : curremcyValueEle) {
            if (!ele.getText().equalsIgnoreCase("-"))
                softAssert.assertTrue(ele.getText().contains("$"));
        }
    }

    public void mtdTilesContainsCurrency() {
        logger.info("Verifying Currency Symbol in MTD-FYTD tiles");
        List<WebElement> curremcyValueEle = FDdriver.findElements(By.xpath(mtdFtdTileCurrencyValues));
        for (WebElement ele : curremcyValueEle) {
            if (!ele.getText().equalsIgnoreCase("-"))
                softAssert.assertTrue(ele.getText().contains("$"));
        }
    }

    public void shippedCogsTilesContainsPercentage() {
        logger.info("Verifying Percentage Symbol in Shipped COGS tiles");
        List<WebElement> percentEle = FDdriver.findElements(By.xpath(shippedCOGSTilePercentage));
        for (WebElement ele : percentEle) {
            softAssert.assertTrue(ele.getText().contains("%"));
        }
    }

    public void aspTilesContainsPercentage() {
        logger.info("Verifying Percentage Symbol in ASP tiles");
        List<WebElement> percentEle = FDdriver.findElements(By.xpath(aspTilePercentage));
        for (WebElement ele : percentEle) {
            softAssert.assertTrue(ele.getText().contains("%"));
        }
    }

    public boolean roundUpLoadsSuccessfully() throws InterruptedException {
        sideBarExpand();
        logger.info("roundUpLoadsSuccessfully: Retail->Sales Trend->RoundUp page load successfully ");
        return true;
    }

    public void sideBarExpand() throws InterruptedException {
        // If side bar is closed, expand it
        filters.expandFilters();
    }

    public boolean basicClientCogsUnitsRevenueFilterValidation() throws InterruptedException {
        click(findElement(cogsUnitRevenueFilter));
        Thread.sleep(2000);

        List<WebElement> values = findElements(cogsUnitRevenueFilterValues);

        if (values.size() == 0) {
            logger.error("basicClientCogsUnitsRevenueFilterValidation: No options found in \"COGS, UNITS, REVENUE\" filter");
            Assert.fail("basicClientCogsUnitsRevenueFilterValidation: No options found in \"COGS, UNITS, REVENUE\" filter");
        }
        for (int i = 0; i < values.size(); i++) {
            String optionText = values.get(i).getText();
            if (!optionText.equalsIgnoreCase("Shipped COGS") && !optionText.equalsIgnoreCase("Shipped Units")) {
                logger.error("basicClientCogsUnitsRevenueFilterValidation: Invalid option for Basic client: " + optionText);
                Assert.fail("basicClientCogsUnitsRevenueFilterValidation: Invalid option for Basic client: " + optionText);
            }
        }

        return true;
    }

    public boolean defaultDistributorViewValidationPremiumClients() throws InterruptedException {
        WebElement distViewFilterDefaultValue = findElement(distributorViewDefaultValue);
        String val = distViewFilterDefaultValue.getAttribute("value");
        if (!val.equalsIgnoreCase("Manufacturing")) {
            logger.error("defaultDistributorViewValidationPremiumClients: Default option for Premium client in Distributor View filter is: " + distViewFilterDefaultValue.getText());
            Assert.fail("defaultDistributorViewValidationPremiumClients: Default option for Premium client in Distributor View filter is: " + distViewFilterDefaultValue.getText());
        }
        return true;
    }

    public boolean calloutsToggleValidation() {
        clickApply();
        // Call out is checked and values should be present on COGS and YOY % Comparison graph
        logger.info("calloutsToggleValidation: Callouts Checkbox is checked");
        List<WebElement> calloutvalues = findElements(calloutValues);

        if (calloutvalues.size() == 0) {
            logger.error("calloutsToggleValidation: Call out is checked and values is not present on COGS and YOY % Comparison graph");
            Assert.fail("calloutsToggleValidation: Call out is checked and values is not present on COGS and YOY % Comparison graph");
        }

        findElement(ChkBoxCallouts).click();
        logger.info("calloutsToggleValidation: Callouts Checkbox is unchecked");
        calloutvalues = FDdriver.findElements(By.xpath(calloutValues));

        if (calloutvalues.size() > 0) {
            logger.error("calloutsToggleValidation: Call out is unchecked and values is still present on COGS and YOY % Comparison graph");
            Assert.fail("calloutsToggleValidation: Call out is unchecked and values is still present on COGS and YOY % Comparison graph");
        }

        return true;
    }

    public boolean averageSellingPriceBarChartsValidation() {
        clickApply();
        String avgSellingPriceText = findElement(avgSellingPrice).getText();
        if (!avgSellingPriceText.equalsIgnoreCase("Avg. Selling Price")) {
            logger.error("averageSellingPriceBarChartsValidation: Average selling price is not shown in bar charts for COGS & YOY % Comparison.");
            Assert.fail("averageSellingPriceBarChartsValidation: Average selling price is not shown in bar charts for COGS & YOY % Comparison.");
        }

        return true;
    }

    public boolean cogsBySegmentChartFilterValidation() {
        clickApply();
        scrollToElement(shippedCOGSByProductTable);
        String segmentValue = findElement(firstSegmentShippedCogsBySegment).getText();

        logger.info("cogsBySegmentChartFilterValidation: First segment value in \"Shipped COGS by Segment\" chart is: " + segmentValue);
        click(findElement(firstSegmentFilterShippedCogsBySegment));
        wdWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(highChartsLoading)));
        String segmentFiltValue = findElement(segmentFilterValue).getText();
        if (!segmentFiltValue.split("\n")[0].equalsIgnoreCase(segmentValue)) {
            logger.error("cogsBySegmentChartFilterValidation: Segment filter value '" + segmentFiltValue + "' doesn't match the Segment value '" + segmentValue + "' in \"Shipped COGS by Segment\" chart");
            Assert.fail("cogsBySegmentChartFilterValidation: Segment filter value '" + segmentFiltValue + "' doesn't match the Segment value '" + segmentValue + "' in \"Shipped COGS by Segment\" chart");
        }
        return true;
    }

    public boolean cogsByProductChartValidation() {
        clickApply();
        scrollPageToBottom();
        String asinValue = findElement(firstAsinShippedCogsByProduct).getText();

        logger.info("cogsByProductChartValidation: First ASIN value in \"Shipped COGS by Product\" chart is: " + asinValue);
        click(findElement(firstAsinShippedCogsByProductFilter));
        wdWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(highChartsLoading)));
        logger.info("cogsByProductChartValidation: Selected filter \"Shipped COGS by Product\" ASIN value " + asinValue);
        String asinFiltValue = findElement(asinsFilterValue).getText();
        if (!asinFiltValue.contains(asinValue)) {
            logger.error("cogsByProductChartValidation: ASINs filter value '" + asinFiltValue + "' doesn't match the ASIN value '" + asinValue + "' in \"Shipped COGS by Product\" chart");
            Assert.fail("cogsByProductChartValidation: ASINs filter value '" + asinFiltValue + "' doesn't match the ASIN value '" + asinValue + "' in \"Shipped COGS by Product\" chart");
        }

        return true;
    }

    private void scrollPageToBottom() {
        ((JavascriptExecutor) FDdriver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public void applyAsinFilterInShippedCogsBytFilterTable() {
        clickApply();
        scrollToElement(shippedCOGSByProductTable);
        String asinValue = getText(firstAsinShippedCogsByProduct);
        logger.info("applyAsinFilterInShippedCogsBytFilterTable: First ASIN value in \"Shipped COGS by Product\" chart is: " + asinValue);

        click(findElement(firstAsinShippedCogsByProductFilter));
        wdWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(highChartsLoading)));

        String asinFilterValue = findElement(asinsFilterValue).getText();
        if (!asinFilterValue.contains(asinValue)) {
            logger.error("applyAsinFilterInShippedCogsBytFilterTable: ASINS filter value '" + asinFilterValue + "' doesn't match the ASIN value '" + asinValue + "' in \"Shipped COGS by Product\" chart");
            Assert.fail("applyAsinFilterInShippedCogsBytFilterTable: ASINS filter value '" + asinFilterValue + "' doesn't match the ASIN value '" + asinValue + "' in \"Shipped COGS by Product\" chart");
        }
    }

    public boolean verifyFilterAppliesSuccessfullyOnAsinColumn(String filteredAsinValue) {
        boolean f = true;
        for (int i = 0; i < asinValuesInTheProductTable.size(); i++) {
            String asinValue = asinValuesInTheProductTable.get(i).getText();
            if (asinValue.equalsIgnoreCase(filteredAsinValue)) {
                f = true;
            } else {
                f = false;
                logger.info("Filter on asin column is not working");
                break;
            }
        }
        return f;
    }

    public void resetFilterInAsinColumn() {
        moveToTheElement(asinColumnHeader);
        isDisplayed(asinColumnFilterIcon);
        logger.info("Click on filter column on asin icon");
        click(asinColumnFilterIcon);
        logger.info("Reset the filter");
        click(resetButton);
    }

    public void verifyTheColumnsInShippedCOGSByCategoryTable() {
        logger.info("Verify columns header in Shipped COGS By Category Table");
        scrollToElement(shippedCOGSByCategoryTable);
        List<WebElement> headerEle = FDdriver.findElements(By.xpath(headerShippedCogsByCategory));
        List<String> headers = new ArrayList<String>();
        headerEle.forEach(x -> headers.add(x.getText()));
        Assert.assertTrue(headers.contains("Last 6 Months"), "Error: 'Last 6 Months' is not visible in Shipped COGS By Category Table");
        Assert.assertTrue(headers.contains("Change From Previous Period"), "Error: 'Change From Previous Period' is not visible in Shipped COGS By Category Table");
        Assert.assertTrue(headers.contains("Change From Last Year"), "Error: 'Change From Last Year' is not visible in Shipped COGS By Category Table");
    }

    public void verifyTheColumnsInShippedCOGSBySubCategoryTable() {
        logger.info("Verify columns header in Shipped COGS By SubCategory Table");
        click(shippedCOGSBySubCategoryExpandIcon);
        List<WebElement> headerEle = FDdriver.findElements(By.xpath(modalHeaders));
        List<String> headers = new ArrayList<String>();
        headerEle.forEach(x -> headers.add(x.getText()));
        Assert.assertTrue(headers.contains("Last 6 Months"), "Error: 'Last 6 Months' is not visible in Shipped COGS By SubCategory Table");
        Assert.assertTrue(headers.contains("Change From Previous Period"), "Error: 'Change From Previous Period' is not visible in Shipped COGS By SubCategory Table");
        Assert.assertTrue(headers.contains("Change From Last Year"), "Error: 'Change From Last Year' is not visible in Shipped COGS By SubCategory Table");
        click(closeIcon);
    }

    public void verifyTheColumnsInShippedCOGSBySegmentTable() {

        logger.info("Verify columns header in Shipped COGS By Segment Table");
        click(shippedCOGSBySegmentExpandIcon);
        List<WebElement> headerEle = FDdriver.findElements(By.xpath(modalHeaders));
        List<String> headers = new ArrayList<String>();
        headerEle.forEach(x -> headers.add(x.getText()));
        Assert.assertTrue(headers.contains("Last 6 Months"), "Error: 'Last 6 Months' is not visible in Shipped COGS By Segment Table");
        Assert.assertTrue(headers.contains("Change From Previous Period"), "Error: 'Change From Previous Period' is not visible in Shipped COGS By Segment Table");
        Assert.assertTrue(headers.contains("Change From Last Year"), "Error: 'Change From Last Year' is not visible in Shipped COGS By Segment Table");
        click(closeIcon);
    }

    public boolean priorityAsinToggleValidation() {
        if (findElement(priorityAsinsOnlyToggle).getAttribute("class").contains("checked")) {
            logger.error("priorityAsinToggleValidation: \"Priority ASINs Only\" toggle is turned on by default");
            Assert.fail("priorityAsinToggleValidation: \"Priority ASINs Only\" toggle is turned on by default");
        } else {
            logger.info("priorityAsinToggleValidation: \"Priority ASINs Only\" toggle is turned off by default");
        }
        return true;
    }

    public boolean aspInfoArapClientValidation() {
        clickApply();
        WebElement aspInfoEle = findElement(aspInfo);
        actions.moveToElement(aspInfoEle).build().perform();
        aspInfoEle = findElement(aspInfo);
        String toolTipId = aspInfoEle.getAttribute("aria-labelledby");


        String toolTipvalue = findElement("//div[@id='" + toolTipId + "']/div/div").getText();
        if (!toolTipvalue.equalsIgnoreCase("Average Selling Price")) {
            logger.error("aspInfoArapClientValidation: \"ASP\" info tool tip doesn't show \"Average Selling Price\". Value shown is \"" + toolTipvalue + "\"");
            Assert.fail("aspInfoArapClientValidation: \"ASP\" info tool tip doesn't show \"Average Selling Price\". Value shown is \"" + toolTipvalue + "\"");
        } else {
            logger.info("aspInfoArapClientValidation: \"ASP\" info tool tip show \"Average Selling Price\"");
        }
        return true;
    }

    public boolean monthlyDropDownValidation() {
        click(findElement(dateRangeField));
        String dateRange = findElement(dateRangeDefault).getText();
        if (!dateRange.equalsIgnoreCase("Last 6 Months")) {
            logger.error("monthlyDropDownValidation: Default Date range is not \"Last 6 Months\". The value selected is:" + dateRange);
            Assert.fail("monthlyDropDownValidation: Default Date range is not \"Last 6 Months\". The value selected is:" + dateRange);
        } else {
            logger.info("monthlyDropDownValidation: Default Date range is \"Last 6 Months\"");
        }

        return true;
    }

    public boolean weeklyIntervalValidation() {
        click(intervalDropDown);
        click(findElement(intervalFilterWeeklyValue));
        click(findElement(dateRangeField));
        String dateRange = findElement(dateRangeDefault).getText();
        if (!dateRange.equalsIgnoreCase("Last 13 Weeks")) {
            logger.error("weeklyIntervalValidation: Default Date range is not \"Last 13 Weeks\". The value selected is:" + dateRange);
            Assert.fail("weeklyIntervalValidation: Default Date range is not \"Last 13 Weeks\". The value selected is:" + dateRange);
        } else {
            logger.info("weeklyIntervalValidation: Default Date range is \"Last 13 Weeks\"");
        }

        return true;
    }

    public boolean asinDetailsValidation() {
        clickApply();
        scrollPageToBottom();
        String asinValue = findElement(firstAsinShippedCogsByProduct).getText();

        logger.info("asinDetailsValidation: First ASIN value in \"Shipped COGS by Product\" chart is: " + asinValue);
        WebElement asinDetails = findElement(firstAsinShippedCogsByProductDetails);
        click(asinDetails);
        ArrayList<String> tabs = new ArrayList<String>(FDdriver.getWindowHandles());
        FDdriver.switchTo().window(tabs.get(1));
        wdWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(highChartsLoading)));
        String asinDetailsValue = findElement(asinDetailsPageAsinsVal).getText();
        if (!asinDetailsValue.contains(asinValue)) {
            logger.error("asinDetailsValidation: ASINs value '" + asinValue + "' doesn't match the ASIN details page value of '" + asinDetailsValue + "'");
            Assert.fail("asinDetailsValidation: ASINs value '" + asinValue + "' doesn't match the ASIN details page value of '" + asinDetailsValue + "'");
        } else {
            logger.info("asinDetailsValidation: ASINs value '" + asinValue + "' detail page " + FDdriver.getCurrentUrl() + " loaded successfully");
        }


        return true;
    }

    public boolean asinGainersDrainersValidation() {
        clickApply();
        scrollPageToBottom();
        String asinValue = findElement(firstAsinShippedCogsByProduct).getText();

        logger.info("asinGainersDrainersValidation: First ASIN value in \"Shipped COGS by Product\" chart is: " + asinValue);
        WebElement asinGainersDrainers = findElement(firstAsinShippedCogsByProductGainersDrainers);
        click(asinGainersDrainers);
        ArrayList<String> tabs = new ArrayList<String>(FDdriver.getWindowHandles());
        FDdriver.switchTo().window(tabs.get(1));
        if (!FDdriver.getCurrentUrl().contains(asinValue)) {
            logger.error("asinGainersDrainersValidation: ASINs value '" + asinValue + "' doesn't match the ASIN Gainers and Drainers page");
            Assert.fail("asinGainersDrainersValidation: ASINs value '" + asinValue + "' doesn't match the ASIN Gainers and Drainers page");
        } else {
            logger.info("asinGainersDrainersValidation: ASINs value '" + asinValue + "' Gainers and Drainers page " + FDdriver.getCurrentUrl() + " loaded successfully");
        }
        return true;
    }

    public boolean asinAmazonDetailsValidation() {
        clickApply();
        scrollPageToBottom();
        WebElement asinElement = findElement(firstAsinShippedCogsByProduct);
        String asinValue = asinElement.getText();
        logger.info("asinAmazonDetailsValidation: First ASIN value in \"Shipped COGS by Product\" chart is: " + asinValue);
        WebElement asinDetails = findElement(firstAsinShippedCogsByProductDetails);
        click(asinDetails);
        ArrayList<String> tabs = new ArrayList<String>(FDdriver.getWindowHandles());
        FDdriver.switchTo().window(tabs.get(1));
        if (!FDdriver.getCurrentUrl().contains(asinValue)) {
            logger.error("asinAmazonDetailsValidation: ASINs value '" + asinValue + "' doesn't match the Amazon ASIN details page value");
            Assert.fail("asinAmazonDetailsValidation: ASINs value '" + asinValue + "' doesn't match the Amazon ASIN details page value");
        } else {
            logger.info("asinAmazonDetailsValidation: ASINs value '" + asinValue + "' Amazon detail page loaded successfully. Page Title: " + FDdriver.getCurrentUrl());
        }
        return true;
    }

    public boolean isShippedCogsByCategoryContainsTheRespectiveCurrencySymbol() {
        logger.info("Check the respective currency symbol value");
        scrollToElement(shippedCOGSByCategoryTable);
        boolean f = false;
        for (WebElement listOfShippedCOGSByCategoryMonthsDatum : listOfShippedCOGSByCategoryMonthsData) {
            String data = listOfShippedCOGSByCategoryMonthsDatum.getText();
            if (data.contains("$")) {
                f = true;
            } else {
                break;
            }
        }
        return f;
    }

    public boolean isShippedCogsBySubCategoryContainsTheRespectiveCurrencySymbol() {
        logger.info("Check the respective currency symbol value in Shipped Cogs By SubCategory");
        scrollToElement(shippedCOGSBySubCategoryTable);
        boolean f = false;
        for (WebElement listOfShippedCOGSBySubCategoryMonthsDatum : listOfShippedCOGSBySubCategoryMonthsData) {
            String data = listOfShippedCOGSBySubCategoryMonthsDatum.getText();
            if (data.contains("$")) {
                f = true;
            } else {
                break;
            }
        }
        return f;
    }

    public boolean isShippedCogsBySegmentContainsTheRespectiveCurrencySymbol() {
        logger.info("Check the respective currency symbol value in Shipped Cogs By Segment");
        scrollToElement(shippedCOGSBySegmentTable);
        boolean f = false;
        for (WebElement listOfShippedCOGSBySegmentMonthsDatum : listOfShippedCOGSBySegmentMonthsData) {
            String data = listOfShippedCOGSBySegmentMonthsDatum.getText();
            if (data.contains("$")) {
                f = true;
            } else {
                break;
            }
        }
        return f;
    }

    public boolean isShippedCogsByProductContainsTheRespectiveCurrencySymbol() {
        logger.info("Check the respective currency symbol value in Shipped Cogs By Product");
        scrollToElement(shippedCOGSByProductTable);
        boolean f = false;
        for (WebElement listOfShippedCOGSByProductMonthsDatum : listOfShippedCOGSByProductMonthsData) {
            String data = listOfShippedCOGSByProductMonthsDatum.getText();
            if (data.contains("$")) {
                f = true;
            } else {
                break;
            }
        }
        return f;
    }

    public boolean cogsBySubCategoryChartFilterValidation() {
        clickApply();
        scrollPageToBottom();
        String subCategoryValue = findElement(firstSubCategoryShippedCogsBySubCategory).getText();

        logger.info("cogsBySubCategoryChartFilterValidation: First Sub Category value in \"Shipped COGS by SubCategory\" chart is: " + subCategoryValue);
        click(findElement(firstSubCategoryFilterShippedCogsBySubCategory));
        wdWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(highChartsLoading)));
        //		scrollToElement(FDdriver.findElement(By.xpath("//div[@id='sidebar-filter-sub-category']")));
        //		logger.info("cogsBySubCategoryChartFilterValidation: Selected filter \"Shipped COGS by SubCategory\" chart value is " + subCategoryValue);
        //		if (FDdriver.findElements(By.xpath("//div[@id='sidebar-filter-sub-category']//button")).size() > 0)
        //			click(FDdriver.findElement(By.xpath("//div[@id='sidebar-filter-sub-category']//button")));
        String subCategoryFiltValue = findElement(subCategoryFilterValue).getText();
        if (!subCategoryFiltValue.split("\n")[0].equalsIgnoreCase(subCategoryValue)) {
            logger.error("cogsBySubCategoryChartFilterValidation: SubCategory filter value '" + subCategoryFiltValue + "' doesn't match the Segment value '" + subCategoryValue + "' in \"Shipped COGS by SubCategory\" chart");
            Assert.fail("cogsBySubCategoryChartFilterValidation: SubCategory filter value '" + subCategoryFiltValue + "' doesn't match the Segment value '" + subCategoryValue + "' in \"Shipped COGS by SubCategory\" chart");
        }
        return true;
    }

    public void verifyShippedCogsByProductGainersDrainersIcon(SoftAssert softAssert) {
        logger.info("** Clicking on Gainers&Drainers icon in Shipped Cogs By Product");
        logger.info("** Getting a random number");
        int index = new Random().nextInt(5);
        String asinForIconClicked = getText("//div[@id='roundup-product']//div[@class='ag-center-cols-container']//div[@row-index='" + index + "']/div[@aria-colindex='3']", "Round Up - Shipped Cogs By Product - ASIN number in row: " + index);
        click("//div[@id='roundup-product']//div[@row-index='" + index + "']//div[@col-id='filter' and @aria-colindex='2']//*[name()='svg' and @data-testid='BarChartOutlinedIcon']/..", "Round Up - Shipped Cogs By Product - Gainers Drainers icon in row: " + index);

        logger.info("** Switching tab to Gainers & Drainers");
        switchToTab(2, 1);

        softAssert.assertTrue(elementVisible(GainersDrainersPage.chartTitle, 30), "** Gainers & Drainers tab may not have opened. Chart title 'Gainers & Drainers' could not be located.");
        softAssert.assertTrue(textContainedInElement(GainersDrainersPage.itemInAsinTextBox, asinForIconClicked, 15), "** Asin selected on Round Up did not populate Asin textbox on Grainers & Drainers.");

        logger.info("** Closing Gainers & Drainers tab");
        FDdriver.close();

        switchToTab(1, 0);

        logger.info("** Getting a new random number");
        index = new Random().nextInt(5);

        logger.info("** Clicking on Gainers&Drainers icon in Shipped Cogs By Product expanded view");
        click(shippedCogsByProductExpandIcon, "Round Up - Shipped Cogs By Product - Expand icon");
        asinForIconClicked = getText("//h2[@id='modal-modal-title']/../../..//div[@class='ag-center-cols-container']/div[@row-index='" + index + "']/div[@aria-colindex='3']", "Round Up - Shipped Cogs By Product (Expanded) - ASIN number in row: " + index);
        click("//h2[@id='modal-modal-title']/../../..//ancestor::div[@row-index ='" + index + "']//*[name()='svg' and @data-testid='BarChartOutlinedIcon']/..", "Round Up - Shipped Cogs By Product (Expanded) - Gainers Drainers icon in row: " + index);

        logger.info("** Switching tab to Gainers & Drainers");
        switchToTab(2, 1);

        softAssert.assertTrue(elementVisible(GainersDrainersPage.chartTitle, 30), "** Gainers & Drainers tab may not have opened. Chart title 'Gainers & Drainers' could not be located.");
        softAssert.assertTrue(textContainedInElement(GainersDrainersPage.itemInAsinTextBox, asinForIconClicked, 15), "** Asin selected on Round Up did not populate Asin textbox on Grainers & Drainers.");
    }

    public void verifyShippedCogsByProductAsinDetailsIcon(SoftAssert softAssert) {
        logger.info("** Clicking on Asin Detail icon in Shipped Cogs By Product");
        logger.info("** Getting a random number");
        int index = new Random().nextInt(5);
        String asinForIconClicked = getText("//div[@id='roundup-product']//div[@class='ag-center-cols-container']//div[@row-index='" + index + "']/div[@aria-colindex='3']", "Round Up - Shipped Cogs By Product - ASIN number in row: " + index);
        click("//div[@id='roundup-product']//div[@row-index='" + index + "']//div[@col-id='filter' and @aria-colindex='2']//*[name()='svg' and @data-testid='SearchIcon']/..", "Round Up - Shipped Cogs By Product - Asin Detail icon in row: " + index);

        logger.info("** Switching tab to Asin Detail");
        switchToTab(2, 1);

        softAssert.assertTrue(elementVisible("//a[@id='ASIN Detail']", 15), "** Asin Detail tab may not have opened. Chart title 'Asin Detail' could not be located.");
        softAssert.assertTrue(textContainedInElement(GainersDrainersPage.itemInAsinTextBox, asinForIconClicked, 15), "** Asin selected on Round Up did not populate Asin textbox on Asin Detail.");

        logger.info("** Closing Asin Detail tab");
        FDdriver.close();

        switchToTab(1, 0);

        logger.info("** Getting a new random number");
        index = new Random().nextInt(5);

        logger.info("** Clicking on Asin Detail icon in Shipped Cogs By Product expanded view");
        click(shippedCogsByProductExpandIcon, "Round Up - Shipped Cogs By Product - Expand icon");
        asinForIconClicked = getText("//h2[@id='modal-modal-title']/../../..//div[@class='ag-center-cols-container']/div[@row-index ='" + index + "']/div[@aria-colindex='3']", "Round Up - Shipped Cogs By Product (Expanded) - ASIN number in row: " + index);
        click("//h2[@id='modal-modal-title']/../../..//ancestor::div[@row-index ='" + index + "']//*[name()='svg' and @data-testid='SearchIcon']/..", "Round Up - Shipped Cogs By Product (Expanded)- Asin Detail  icon in row: " + index);

        logger.info("** Switching tab to Asin Detail");
        switchToTab(2, 1);

        softAssert.assertTrue(elementVisible("//a[@id='ASIN Detail']", 15), "** Asin Detail tab may not have opened. Chart title 'Retail Scratchpad' could not be located.");
        softAssert.assertTrue(textContainedInElement(GainersDrainersPage.itemInAsinTextBox, asinForIconClicked, 15), "** Asin selected on Round Up did not populate Asin textbox on Asin Detail.");

    }

    public void verifyShippedCogsByProductRetailScratchpedIcon(SoftAssert softAssert) {
        logger.info("** Clicking on Retail Scratchpad icon in Shipped Cogs By Product");
        logger.info("** Getting a random number");
        int index = new Random().nextInt(4);
        String asinForIconClicked = getText("//div[@id='roundup-product']//div[@class='ag-center-cols-container']//div[@row-id='" + index + "']/div[@aria-colindex='3']", "Round Up - Shipped Cogs By Product - ASIN number in row: " + index);
        click("//div[@id='roundup-product']//div[@row-id='" + index + "']//div[@col-id='filter' and @aria-colindex='2']//*[name()='svg' and @data-testid='QueryStatsOutlinedIcon']/..", "Round Up - Shipped Cogs By Product - Retail Scratchpad icon in row: " + index);

        logger.info("** Switching tab to Retail Scratchpad");
        switchToTab(2, 1);

        softAssert.assertTrue(elementVisible("//a[@id='Retail Scratchpad']", 15), "** Retail Scratchpad tab may not have opened. Chart title 'Asin Detail' could not be located.");
        softAssert.assertTrue(textContainedInElement(GainersDrainersPage.itemInAsinTextBox, asinForIconClicked, 10), "** Asin selected on Round Up did not populate Asin textbox on Retail Scratchpad.");

        logger.info("** Closing Retail Scratchpad tab");
        FDdriver.close();

        switchToTab(1, 0);

        logger.info("** Getting a new random number");
        index = new Random().nextInt(5);

        logger.info("** Clicking on Retail Scratchpad icon in Shipped Cogs By Product expanded view");
        click(shippedCogsByProductExpandIcon, "Round Up - Shipped Cogs By Product - Expand icon");
        asinForIconClicked = getText("//h2[@id='modal-modal-title']/../../..//div[@class='ag-center-cols-container']/div[@row-id='" + index + "']/div[@aria-colindex='3']", "Round Up - Shipped Cogs By Product (Expanded) - ASIN number in row: " + index);
        click("//h2[@id='modal-modal-title']/../../..//ancestor::div[@row-id ='" + index + "']//*[name()='svg' and @data-testid='QueryStatsOutlinedIcon']/..", "Round Up - Shipped Cogs By Product (Expanded)- Retail Scratchpad  icon in row: " + index);

        logger.info("** Switching tab to Retail Scratchpad");
        switchToTab(2, 1);

        softAssert.assertTrue(elementVisible("//a[@id='Retail Scratchpad']", 15), "** Retail Scratchpad tab may not have opened. Chart title 'Retail Scratchpad' could not be located.");
        softAssert.assertTrue(textContainedInElement(GainersDrainersPage.itemInAsinTextBox, asinForIconClicked, 15), "** Asin selected on Round Up did not populate Asin textbox on Retail Scratchpad.");

    }

    public void verifyShippedCogsBySubCategoryRetailScratchpedIcon(SoftAssert softAssert) {
        logger.info("** Clicking on Retail Scratchpad icon in Shipped Cogs By SubCategory");
        logger.info("** Getting a new random number");
        int index = new Random().nextInt(4);
        click("//h6[@id='shipped-cogs-by-sub-category']/../../..//div[@row-index='" + index + "']/div[@aria-colindex='1']//*[name()='svg' and @data-testid='QueryStatsOutlinedIcon']/..", "Round Up - Shipped Cogs By SubCategory - Retail Scratchpad icon in row: " + index);

        logger.info("** Switching tab to Retail Scratchpad");
        switchToTab(2, 1);
        softAssert.assertTrue(elementVisible("//a[@id='Retail Scratchpad']", 15), "** Retail Scratchpad tab may not have opened for Shipped Cogs by SubCategory. Chart title 'Retail Scratchpad' could not be located.");

        logger.info("** Closing Retail Scratchpad tab");
        FDdriver.close();
        switchToTab(1, 0);

        logger.info("** Getting a new random number");
        index = new Random().nextInt(4);

        logger.info("** Clicking on Retail Scratchpad icon in Shipped Cogs By SubCategory expanded view");
        click("//h6[@id='shipped-cogs-by-sub-category']/../..//*[name()='svg' and @data-testid='FullscreenIcon']", "Round Up - Shipped Cogs By SubCategory - Expand icon");
        click("//h2[@id='modal-modal-title']/../../..//ancestor::div[@row-index ='" + index + "']//*[name()='svg' and @data-testid='QueryStatsOutlinedIcon']/..", "Round Up - Shipped Cogs By SubCategory (Expanded) - Retail Scratchpad icon in row: " + index);

        logger.info("** Switching tab to Retail Scratchpad");
        switchToTab(2, 1);
        softAssert.assertTrue(elementVisible("//a[@id='Retail Scratchpad']", 15), "** Retail Scratchpad tab may not have opened for Shipped Cogs by SubCategory (expanded view). Chart title 'Retail Scratchpad' could not be located.");

        logger.info("** Closing Retail Scratchpad tab");
        FDdriver.close();

        switchToTab(1, 0);
        click("//span[@aria-label='close button']//*[name()='svg' and @data-testid='CloseIcon' ]", "");

    }

    public void verifyShippedCogsBySegmentRetailScratchpedIcon(SoftAssert softAssert) {
        logger.info("** Clicking on Retail Scratchpad icon in Shipped Cogs By Segment");
        logger.info("** Getting a new random number");
        int index = new Random().nextInt(4);
        click("//h6[@id='shipped-cogs-by-segment']/../../..//div[@row-index='" + index + "']/div[@aria-colindex='1']//*[name()='svg' and @data-testid='QueryStatsOutlinedIcon']/..", "Round Up - Shipped Cogs By Segment - Retail Scratchpad icon in row: " + index);

        logger.info("** Switching tab to Retail Scratchpad");
        switchToTab(2, 1);
        softAssert.assertTrue(elementVisible("//a[@id='Retail Scratchpad']", 15), "** Retail Scratchpad tab may not have opened for Shipped Cogs by Segment. Chart title 'Retail Scratchpad' could not be located.");

        logger.info("** Closing Retail Scratchpad tab");
        FDdriver.close();
        switchToTab(1, 0);

        logger.info("** Getting a new random number");
        index = new Random().nextInt(4);

        logger.info("** Clicking on Retail Scratchpad icon in Shipped Cogs By Segment expanded view");
        click("//h6[@id='shipped-cogs-by-segment']/../..//*[name()='svg' and @data-testid='FullscreenIcon']", "Round Up - Shipped Cogs By Segment - Expand icon");
        click("//h2[@id='modal-modal-title']/../../..//ancestor::div[@row-index ='" + index + "']//*[name()='svg' and @data-testid='QueryStatsOutlinedIcon']/..", "Round Up - Shipped Cogs By Segment (Expanded) - Retail Scratchpad icon in row: " + index);

        logger.info("** Switching tab to Retail Scratchpad");
        switchToTab(2, 1);
        softAssert.assertTrue(elementVisible("//a[@id='Retail Scratchpad']", 15), "** Retail Scratchpad tab may not have opened for Shipped Cogs by Segment (expanded view). Chart title 'Retail Scratchpad' could not be located.");

        logger.info("** Closing Retail Scratchpad tab");
        FDdriver.close();

        switchToTab(1, 0);
        click("//span[@aria-label='close button']//*[name()='svg' and @data-testid='CloseIcon' ]", "");
    }

    public void verifyShippedCogsByCategoryRetailScratchpedIcon(SoftAssert softAssert) {
        logger.info("** Clicking on Retail Scratchpad icon in Shipped Cogs By Category");
        logger.info("** Getting a new random number");
        int index = new Random().nextInt(4);
        click("//h6[@id='shipped-cogs-by-category']/../../..//div[@row-index='" + index + "']/div[@aria-colindex='1']//*[name()='svg' and @data-testid='QueryStatsOutlinedIcon']/..", "Round Up - Shipped Cogs By Category - Retail Scratchpad icon in row: " + index);

        logger.info("** Switching tab to Retail Scratchpad");
        switchToTab(2, 1);
        softAssert.assertTrue(elementVisible("//a[@id='Retail Scratchpad']", 15), "** Retail Scratchpad tab may not have opened for Shipped Cogs by Category. Chart title 'Retail Scratchpad' could not be located.");

        logger.info("** Closing Retail Scratchpad tab");
        FDdriver.close();
        switchToTab(1, 0);

        logger.info("** Getting a new random number");
        index = new Random().nextInt(4);

        logger.info("** Clicking on Retail Scratchpad icon in Shipped Cogs By Category expanded view");
        click("//h6[@id='shipped-cogs-by-category']/../..//*[name()='svg' and @data-testid='FullscreenIcon']", "Round Up - Shipped Cogs By Category - Expand icon");
        click("//h2[@id='modal-modal-title']/../../..//ancestor::div[@row-index ='" + index + "']//*[name()='svg' and @data-testid='QueryStatsOutlinedIcon']/..", "Round Up - Shipped Cogs By Category (Expanded) - Retail Scratchpad icon in row: " + index);

        logger.info("** Switching tab to Retail Scratchpad");
        switchToTab(2, 1);
        softAssert.assertTrue(elementVisible("//a[@id='Retail Scratchpad']", 15), "** Retail Scratchpad tab may not have opened for Shipped Cogs by Category (expanded view). Chart title 'Retail Scratchpad' could not be located.");

        logger.info("** Closing Retail Scratchpad tab");
        FDdriver.close();

        switchToTab(1, 0);
        click("//span[@aria-label='close button']//*[name()='svg' and @data-testid='CloseIcon' ]", "");

    }

    public void verifyColumnSelectionShippedCogsByCategory(SoftAssert softAssert) throws InterruptedException {
        logger.info("** Verifying column selection from stack menu in Shipped Cogs By Category chart");
        click(stackIconShippedCogsByCategory, "Shipped Cogs By Category - Stack Icon");

        List<WebElement> columnHeadersSelected = findElementsPresent(shippedCogsByCategoryCheckedColumnInStack, "Shipped Cogs By Category - Checked boxes in stack");

        for (WebElement header : columnHeadersSelected) {
            Thread.sleep(200);
            header.click();
        }

        hitEscKey();

        List<String> columnHeadersUi = new ArrayList<>();
        List<WebElement> columnHeaders = findElementsVisible(shippedCogsByCategoryColumnHeaders, 1, "Shipped Cogs By Category - Column Headers");

        for (WebElement header : columnHeaders) {
            columnHeadersUi.add(header.getText());
        }

        softAssert.assertTrue(columnHeadersUi.isEmpty(), "** Already selected Category columns unselected. No column headers shoud display, but following are displaying: " + columnHeadersUi);

        click(stackIconShippedCogsByCategory, "Shipped Cogs By Category - Stack Icon");

        List<WebElement> columnHeadersUnselected = findElementsPresent(shippedCogsByCategoryColumnHeadersInStack, "Shipped Cogs By Category - Column Headers in Stack");
        int size = columnHeadersUnselected.size();

        List<String> columnHeadersRandomlySelected = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Thread.sleep(200);
            size = size - 1;
            int index = new Random().nextInt(size);
            if (columnHeadersUnselected.get(index).getText().equals("Filter")) {
                columnHeadersRandomlySelected.add("");
            } else {
                columnHeadersRandomlySelected.add(columnHeadersUnselected.get(index).getText());
            }
            columnHeadersUnselected.get(index).click();
            columnHeadersUnselected.remove(index);
        }

        hitEscKey();

        columnHeadersUi = new ArrayList<>();
        columnHeaders = findElementsPresent(shippedCogsByCategoryColumnHeaders, "Shipped Cogs By Category - Column Headers");

        for (WebElement header : columnHeaders) {
            columnHeadersUi.add(header.getText());
        }

        softAssert.assertEquals(columnHeadersRandomlySelected, columnHeadersUi, "** Category columns to display randomly selected. What display do not match what were selected. Selected: " + columnHeadersRandomlySelected + " - Display: " + columnHeadersUi);
    }

    public void verifyColumnSelectionShippedCogsBySegment(SoftAssert softAssert) throws InterruptedException {
        logger.info("** Verifying column selection from stack menu in Shipped Cogs By Segment chart");
        click(stackIconShippedCogsBySegment, "Shipped Cogs By Segment - Stack Icon");

        List<WebElement> columnHeadersSelected = findElementsPresent(shippedCogsBySegmentCheckedColumnInStack, "Shipped Cogs By Segment - Checked boxes in stack");

        for (WebElement header : columnHeadersSelected) {
            Thread.sleep(200);
            header.click();
        }

        hitEscKey();

        List<String> columnHeadersUi = new ArrayList<>();
        List<WebElement> columnHeaders = findElementsVisible(shippedCogsBySegmentColumnHeaders, 1, "Shipped Cogs By Segment - Column Headers");

        for (WebElement header : columnHeaders) {
            columnHeadersUi.add(header.getText());
        }

        softAssert.assertTrue(columnHeadersUi.isEmpty(), "** Already selected Segment columns unselected. No column headers shoud display, but following are displaying: " + columnHeadersUi);

        click(stackIconShippedCogsBySegment, "Shipped Cogs By Segment - Stack Icon");

        List<WebElement> columnHeadersUnselected = findElementsPresent(shippedCogsBySegmentColumnHeadersInStack, "Shipped Cogs By Segment - Column Headers in Stack");
        int size = columnHeadersUnselected.size();

        List<String> columnHeadersRandomlySelected = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Thread.sleep(200);
            size = size - 1;
            int index = new Random().nextInt(size);
            if (columnHeadersUnselected.get(index).getText().equals("Filter")) {
                columnHeadersRandomlySelected.add("");
            } else {
                columnHeadersRandomlySelected.add(columnHeadersUnselected.get(index).getText());
            }
            Thread.sleep(200);

            columnHeadersUnselected.get(index).click();
            columnHeadersUnselected.remove(index);
        }

        hitEscKey();

        columnHeadersUi = new ArrayList<>();
        columnHeaders = findElementsPresent(shippedCogsBySegmentColumnHeaders, "Shipped Cogs By Segment - Column Headers");

        for (WebElement header : columnHeaders) {
            columnHeadersUi.add(header.getText());
        }

        softAssert.assertEquals(columnHeadersRandomlySelected, columnHeadersUi, "** Segment columns to display randomly selected. What display do not match what were selected. Selected: " + columnHeadersRandomlySelected + " - Display: " + columnHeadersUi);
    }

    public void verifyColumnSelectionShippedCogsBySubCategory(SoftAssert softAssert) throws InterruptedException {
        logger.info("** Verifying column selection from stack menu in Shipped Cogs By SubCategory chart");
        click(stackIconShippedCogsBySubCategory, "Shipped Cogs By SubCategory - Stack Icon");

        List<WebElement> columnHeadersSelected = findElementsPresent(shippedCogsBySubCategoryCheckedColumnInStack, "Shipped Cogs By SubCategory - Checked boxes in stack");

        for (WebElement header : columnHeadersSelected) {
            Thread.sleep(200);
            header.click();
        }

        hitEscKey();

        List<String> columnHeadersUi = new ArrayList<>();
        List<WebElement> columnHeaders = findElementsVisible(shippedCogsBySubCategoryColumnHeaders, 1, "Shipped Cogs By SubCategory - Column Headers");

        for (WebElement header : columnHeaders) {
            columnHeadersUi.add(header.getText());
        }

        softAssert.assertTrue(columnHeadersUi.isEmpty(), "** Already selected SubCategory columns unselected. No column headers shoud display, but following are displaying: " + columnHeadersUi);

        click(stackIconShippedCogsBySubCategory, "Shipped Cogs By SubCategory - Stack Icon");

        List<WebElement> columnHeadersUnselected = findElementsPresent(shippedCogsBySubCategoryColumnHeadersInStack, "Shipped Cogs By SubCategory - Column Headers in Stack");
        int size = columnHeadersUnselected.size();

        List<String> columnHeadersRandomlySelected = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Thread.sleep(200);
            size = size - 1;
            int index = new Random().nextInt(size);
            if (columnHeadersUnselected.get(index).getText().equals("Filter")) {
                columnHeadersRandomlySelected.add("");
            } else {
                columnHeadersRandomlySelected.add(columnHeadersUnselected.get(index).getText());
            }
            columnHeadersUnselected.get(index).click();
            columnHeadersUnselected.remove(index);
        }

        hitEscKey();

        columnHeadersUi = new ArrayList<>();
        columnHeaders = findElementsPresent(shippedCogsBySubCategoryColumnHeaders, "Shipped Cogs By SubCategory - Column Headers");

        for (WebElement header : columnHeaders) {
            columnHeadersUi.add(header.getText());
        }

        softAssert.assertEquals(columnHeadersRandomlySelected, columnHeadersUi, "** SubCategory columns to display randomly selected. What display do not match what were selected. Selected: " + columnHeadersRandomlySelected + " - Display: " + columnHeadersUi);
    }

    public void verifyColumnSelectionShippedCogsByProduct(SoftAssert softAssert) throws InterruptedException {
        logger.info("** Verifying column selection from stack menu in Shipped Cogs By Product chart");
        click(stackIconShippedCogsByProduct, "Shipped Cogs By Product - Stack Icon");

        List<WebElement> columnHeadersSelected = findElementsPresent(shippedCogsByProductCheckedColumnInStack, "Shipped Cogs By Product - Checked boxes in stack");

        for (WebElement header : columnHeadersSelected) {
            Thread.sleep(200);
            header.click();
        }

        hitEscKey();

        List<String> columnHeadersUi = new ArrayList<>();
        List<WebElement> columnHeaders = findElementsVisible(shippedCogsByProductColumnHeaders, 1, "Shipped Cogs By Product - Column Headers");

        for (WebElement header : columnHeaders) {
            columnHeadersUi.add(header.getText());
        }

        softAssert.assertTrue(columnHeadersUi.isEmpty(), "** Already selected Product columns unselected. No column headers shoud display, but following are displaying: " + columnHeadersUi);

        click(stackIconShippedCogsByProduct, "Shipped Cogs By Product - Stack Icon");

        List<WebElement> columnHeadersUnselected = findElementsPresent(shippedCogsByProductColumnHeadersInStack, "Shipped Cogs By Product - Column Headers in Stack");
        int size = columnHeadersUnselected.size();

        List<String> columnHeadersRandomlySelected = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Thread.sleep(200);
            size = size - 1;
            int index = new Random().nextInt(size);
            if (columnHeadersUnselected.get(index).getText().equals("Filter") || columnHeadersUnselected.get(index).getText().equals("Priority ASIN")) {
                columnHeadersRandomlySelected.add("");
            } else {
                columnHeadersRandomlySelected.add(columnHeadersUnselected.get(index).getText());
            }
            columnHeadersUnselected.get(index).click();
            columnHeadersUnselected.remove(index);
        }

        hitEscKey();

        columnHeadersUi = new ArrayList<>();
        columnHeaders = findElementsPresent(shippedCogsByProductColumnHeaders, "Shipped Cogs By Product - Column Headers");

        for (WebElement header : columnHeaders) {
            columnHeadersUi.add(header.getText());
        }

        softAssert.assertEquals(columnHeadersRandomlySelected, columnHeadersUi, "** Product columns to display randomly selected. What display do not match what were selected. Selected: " + columnHeadersRandomlySelected + " - Display: " + columnHeadersUi);
    }

}*/

