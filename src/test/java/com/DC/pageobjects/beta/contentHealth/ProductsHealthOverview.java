package com.DC.pageobjects.beta.contentHealth;

import com.DC.pageobjects.PageHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.List;

public class ProductsHealthOverview extends PageHandler {

    private final By PRODUCTS_HEALTH_OVERVIEW_HEADER = By.xpath("//div[text()='Products Health Overview']");
    private final By CONTENT_HEALTH_DASHBOARD_BREADCRUMB = By.xpath("//div[text()='Content Health Dashboard']");
    private final By PRODUCTS_OVERVIEW_BREADCRUMB = By.xpath("//span[text()='Products Overview']");
    private final By PRODUCTS_OVERVIEW_EXPORT_BUTTON = By.xpath("//span[@data-cy='download']");
    private final By PRODUCTS_OVERVIEW_TABLE_COLUMN_HEADERS = By.xpath("//div[contains(@data-cy, 'ui-table-header')]");

    public ProductsHealthOverview(WebDriver driver) {
        super(driver);
        findElementVisible(PRODUCTS_HEALTH_OVERVIEW_HEADER);
    }

    public boolean isContentHealthDashboardBreadcrumbVisible() {
        return isElementVisible(CONTENT_HEALTH_DASHBOARD_BREADCRUMB);
    }

    public boolean isProductsOverviewBreadcrumbVisible() {
        return isElementVisible(PRODUCTS_OVERVIEW_BREADCRUMB);
    }

    public boolean isProductsOverviewExportButtonVisible() {
        return isElementVisible(PRODUCTS_OVERVIEW_EXPORT_BUTTON);
    }

    public List<String> getProductOverviewTableColumnHeaders() {
        return getTextFromElements(PRODUCTS_OVERVIEW_TABLE_COLUMN_HEADERS);
    }
}
