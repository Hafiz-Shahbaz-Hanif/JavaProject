package com.DC.pageobjects.adc.analyze.retailReporting;

import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.utilities.SharedMethods;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.List;

public class RetailScratchpadPage extends NetNewNavigationMenu {

    public static final By PRODUCT_TITLE_HEADER = By.xpath("//span[text()='Product Title']/ancestor::div[@col-id='asinTitle']");
    public static final By SUMMARY_TABLE_ROWS = By.xpath("//span[text()='ASIN']/ancestor::div[contains(@class, 'ag-unselectable ag-layout-normal')]//div[contains(@class, 'center-cols-container')]/div");
    public static final By PRODUCT_TITLE_WITHIN_ROW = By.xpath("./div[@col-id='asinTitle']//span");
    public static final By SUMMARY_SLICEBY_DROPDOWN = By.xpath("//button[@id='basic-button']/preceding-sibling::div[1]");
    public static final By INTERVAL_SELECTOR = By.xpath("//div[@id='interval-selector']");
    public static final By EXPORT_SELECTOR = By.xpath("//span[text()='download']/..");
    public static final By PRODUCT_TITLE_FILTER_ICON = By.xpath("//span[text()='Product Title']/ancestor::div[@col-id='asinTitle']//span[@ref='eMenu']");
    public static final By FILTER_OPTIONS = By.xpath("//div[@ref='eOptions1']//div[@ref='eIcon']");
    public static final By FILTER_INPUT_FIELD = By.xpath("//input[@placeholder='Filter...' and not(@disabled)]");
    public static final By FILTER_APPLY_BUTTON = By.xpath("//button[contains(text(), 'Reset')]/../button[@type='submit']");
    public static final Duration TIMEOUT = Duration.ofSeconds(60);

    public RetailScratchpadPage (WebDriver driver) {
        super(driver);
    }

    public void selectSummarySliceByOption(String option) throws InterruptedException {
        click(SUMMARY_SLICEBY_DROPDOWN);
        click(By.xpath("//ul[contains(@class, 'css-r8u8y9')]/li[text()='" + option + "']"));
    }

    public void selectInterval(String interval) throws InterruptedException {
        click(INTERVAL_SELECTOR);
        click(By.xpath("//ul[contains(@class, 'css-r8u8y9')]/li[text()='" + interval + "']"));
    }

    public void exportAs(String type) throws InterruptedException {
        click(EXPORT_SELECTOR);
        click(By.xpath("//li[text()='Export to " + type + "']"));
    }

    public void openProductTitleFilter(String filter) throws InterruptedException {
        hoverOverElement(RetailScratchpadPage.PRODUCT_TITLE_HEADER);
        click(PRODUCT_TITLE_FILTER_ICON);
        click(FILTER_OPTIONS);
        click(By.xpath("//span[text()='"+filter+"']/.."));
    }

    public void setProductTitleFilter(String filterValue) throws InterruptedException {
        setText(FILTER_INPUT_FIELD, filterValue);
        click(FILTER_APPLY_BUTTON);
    }

    public void verifyProductTitleHeaderFilter(SoftAssert softAssert, List<WebElement> summaryTableRows, String filter, String title) throws InterruptedException {
        String filterValue = title;
        openProductTitleFilter(filter);

        if (filter.equalsIgnoreCase("Equals")){
            setProductTitleFilter(filterValue);
            summaryTableRows = findElementsVisible(RetailScratchpadPage.SUMMARY_TABLE_ROWS, TIMEOUT);

            for (WebElement row : summaryTableRows) {
                String productTitleUi = row.findElement(RetailScratchpadPage.PRODUCT_TITLE_WITHIN_ROW).getText();
                softAssert.assertTrue(productTitleUi.equals(filterValue), "Product title error for 'Equals' filter.");
            }
        }

        if (filter.equalsIgnoreCase("Contains")){
            filterValue = filterValue.substring(0, 6);
            setProductTitleFilter(filterValue);
            summaryTableRows = findElementsVisible(RetailScratchpadPage.SUMMARY_TABLE_ROWS, TIMEOUT);

            for (WebElement row : summaryTableRows) {
                String productTitleUi = row.findElement(RetailScratchpadPage.PRODUCT_TITLE_WITHIN_ROW).getText();
                softAssert.assertTrue(productTitleUi.contains(filterValue), "Product title error for 'Contains' filter.");
            }
        }

        if (filter.equalsIgnoreCase("Starts with")){
            filterValue = filterValue.substring(0, 6);
            setProductTitleFilter(filterValue);
            summaryTableRows = findElementsVisible(RetailScratchpadPage.SUMMARY_TABLE_ROWS, TIMEOUT);

            for (WebElement row : summaryTableRows) {
                String productTitleUi = row.findElement(RetailScratchpadPage.PRODUCT_TITLE_WITHIN_ROW).getText();
                softAssert.assertTrue(productTitleUi.startsWith(filterValue), "Product title error for 'Starts with' filter.");
            }
        }

        if (filter.equalsIgnoreCase("Ends with")){
            filterValue = filterValue.substring(filterValue.length() - 6);
            setProductTitleFilter(filterValue);
            summaryTableRows = findElementsVisible(RetailScratchpadPage.SUMMARY_TABLE_ROWS, TIMEOUT);

            for (WebElement row : summaryTableRows) {
                String productTitleUi = row.findElement(RetailScratchpadPage.PRODUCT_TITLE_WITHIN_ROW).getText();
                softAssert.assertTrue(productTitleUi.endsWith(filterValue), "Product title error for 'Ends with' filter.");
            }
        }

        if (filter.equalsIgnoreCase("Not contains")){
            filterValue = filterValue.substring(0, 6);
            setProductTitleFilter(filterValue);
            summaryTableRows = findElementsVisible(RetailScratchpadPage.SUMMARY_TABLE_ROWS, TIMEOUT);

            for (WebElement row : summaryTableRows) {
                String productTitleUi = row.findElement(RetailScratchpadPage.PRODUCT_TITLE_WITHIN_ROW).getText();
                softAssert.assertFalse(productTitleUi.contains(filterValue), "Product title error for 'Not contains' filter.");
            }
        }
    }

    public List<WebElement> getSummaryTableRows() {
        return findElementsVisible(RetailScratchpadPage.SUMMARY_TABLE_ROWS);
    }

    public boolean waitProductTitleVisibleOnUi() {
        return isElementVisible(RetailScratchpadPage.PRODUCT_TITLE_HEADER, RetailScratchpadPage.TIMEOUT);
    }

    public String getProductTitleOnUi (WebElement row) {
        return row.findElement(RetailScratchpadPage.PRODUCT_TITLE_WITHIN_ROW).getText();
    }

    public String getRandomProductTitle (List<WebElement> summaryTableRows) {
        WebElement row = (WebElement) SharedMethods.getRandomItemFromList(summaryTableRows);
        return row.findElement(RetailScratchpadPage.PRODUCT_TITLE_WITHIN_ROW).getText();
    }








}