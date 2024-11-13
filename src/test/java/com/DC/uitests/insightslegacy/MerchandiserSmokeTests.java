package com.DC.uitests.insightslegacy;

import com.DC.pageobjects.legacy.legacyinsights.*;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static com.DC.constants.InsightsConstants.*;

public class MerchandiserSmokeTests extends BaseClass {

    private DashboardPage dashboardPage;
    private final String SUPPORT_USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(INSIGHTS_LEGACY_ENDPOINT);
        new InsightsLoginPage(driver).login(SUPPORT_USERNAME, PASSWORD);
        dashboardPage = new DashboardPage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_DashboardPageLoads() throws InterruptedException {
        dashboardPage = dashboardPage.clickDashboard();
        Assert.assertEquals("My Dashboard", dashboardPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_KeywordSearchPageLoads() throws InterruptedException {
        KeywordSearchPage keywordSearchPage = dashboardPage.clickKeywordSearch();
        Assert.assertEquals("Keyword Search", keywordSearchPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_AttributeInsightsPageLoads() throws InterruptedException {
        AttributeInsightsPage attributeInsightsPage = dashboardPage.clickAttributeInsights();
        Assert.assertEquals("Attribute Insights", attributeInsightsPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_CategoryBrandsharePageLoads() throws InterruptedException {
        CategoryBrandsharePage categoryBrandsharePage = dashboardPage.clickCategoryBrandshare();
        Assert.assertEquals("Category Brandshare", categoryBrandsharePage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_WatchListsPageLoads() throws InterruptedException {
        WatchlistsPage watchlistsPage = dashboardPage.clickWatchlists();
        Assert.assertEquals("Watchlists", watchlistsPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_TasksPageLoads() throws Exception {
        TasksPage tasksPage = dashboardPage.clickTasks();
        Assert.assertEquals("Tasks", tasksPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_ProductsPageLoads() throws InterruptedException {
        ProductsPageLegacy productsPageLegacy = dashboardPage.clickProducts();
        Assert.assertEquals("Products", productsPageLegacy.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_ProductListsPageLoads() throws InterruptedException {
        ProductListsPage productListsPage = dashboardPage.clickProductLists();
        Assert.assertEquals("Product Lists", productListsPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_ProductEnhancementPageLoads() throws InterruptedException {
        ProductEnhancementPage productEnhancementPage = dashboardPage.clickProductEnhancement();
        Assert.assertEquals("Product Enhancement", productEnhancementPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_PropertiesPageLoads() throws InterruptedException {
        PropertiesPage propertiesPage = dashboardPage.clickPropertiesPage();
        Assert.assertEquals("Properties", propertiesPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_ImportsPageLoads() throws InterruptedException {
        ImportsPage importsPage = dashboardPage.clickImportsPage();
        Assert.assertEquals("Imports", importsPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_DestinationPageLoads() throws InterruptedException {
        DestinationManagerPage destinationManagerPage = dashboardPage.clickDestinationManager();
        Assert.assertEquals("Destination Manager", destinationManagerPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_APIAdministrationPageLoads() throws InterruptedException {
        APIAdministrationPage apiAdministrationPage = dashboardPage.clickAPIAdministration();
        Assert.assertEquals("API Administration", apiAdministrationPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_RequestsPageLoads() throws InterruptedException {
        RequestsPage requestsPage = dashboardPage.clickRequests();
        Assert.assertEquals("Requests Dashboard", requestsPage.getMainTitleText());
    }

    @Test(groups = {"InsightsSmokeTest"})
    public void UI_PropertyGroupsPageLoads() throws InterruptedException {
        PropertyGroupsPage propertyGroupsPage = dashboardPage.clickPropertyGroups();
        Assert.assertEquals("Property Groups", propertyGroupsPage.getMainTitleText());
    }
}
