package com.DC.uitests.adc.contentHealth.configurationPage;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.beta.contentHealth.ProductsHealthOverview;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.List;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class ProductHealthOverviewTests extends BaseClass {

    private final String USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private static final String FCC_BETA_URL = READ_CONFIG.getDcBetaUrl();
    public static final String INSIGHTS_PRODUCTS_HEALTH_OVERVIEW_URL = FCC_BETA_URL + "/content-health/product-health-overview";
    public ProductsHealthOverview productsHealthOverview;

    @BeforeClass(alwaysRun = true)
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USERNAME, PASSWORD);
        driver.get(INSIGHTS_PRODUCTS_HEALTH_OVERVIEW_URL);
        productsHealthOverview = new ProductsHealthOverview(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify basic features load when user enters products health overview page")
    public void productsHealthOverviewSmokeTest() {
        Assert.assertTrue(productsHealthOverview.isContentHealthDashboardBreadcrumbVisible(), "Breadcrumbs for Content Health Dashboard were not visible");
        Assert.assertTrue(productsHealthOverview.isProductsOverviewBreadcrumbVisible(), "Breadcrumbs for Products Overview were not visible");
        Assert.assertTrue(productsHealthOverview.isProductsOverviewExportButtonVisible(), "Product Overview table export button was not visible");
        List<String> expectedProductsOverviewTableHeaders = Arrays.asList("Product RPCs", "Current Score", "Product Title", "Description", "Feature Bullets", "Digital Assets");
        List<String> actualProductsOverviewTableHeaders = productsHealthOverview.getProductOverviewTableColumnHeaders();
        Assert.assertEqualsNoOrder(actualProductsOverviewTableHeaders, expectedProductsOverviewTableHeaders,"Expected filters were not displayed in sidebar" +
                "\n EXPECTED:" + expectedProductsOverviewTableHeaders +
                "\n ACTUAL:" + actualProductsOverviewTableHeaders);
    }
}
