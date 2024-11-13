package com.DC.uitests.adc.analyze.marketingCloudAnalytics;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.marketingCloudAnalytics.PathToPurchasePage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class PathToPurchaseTests extends BaseClass {
    private PathToPurchasePage pathToPurchasePage;
    private AppHomepage appHomepage;
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppFilaLegacyUrl();
    private static final String PATH_TO_PURCHASE_PATH = "app/amc/path-to-purchase-dashboard";
    private static final String PATH_TO_PURCHASE_URL = LOGIN_ENDPOINT.replace("#", "") + PATH_TO_PURCHASE_PATH;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("Hersheys");
        appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Analyze");
        appHomepage.clickLink("Path to Purchase");
        pathToPurchasePage = new PathToPurchasePage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @BeforeMethod
    public void setupMethod() {
        if (!driver.getTitle().contains("Path to Purchase")) {
            driver.get(PATH_TO_PURCHASE_URL);
        }
    }

    @Test(priority = 1, description = "Verify that Filter values are selected from one or other filter but not both ")
    public void pathToPurchase_CannotApplyBothFilters() throws InterruptedException {
        assertBrandAndCategoryFilters();

        pathToPurchasePage.clearFilters();
        pathToPurchasePage.clickSlideButtonNTB();

        assertBrandAndCategoryFilters();
    }

    private void assertBrandAndCategoryFilters() throws InterruptedException {
        Assert.assertEquals(pathToPurchasePage.getSelectedBrandsCount(), 0, "Brand Filter is not empty");
        Assert.assertEquals(pathToPurchasePage.getSelectedCategoriesCount(), 0, "Category Filter is not empty");

        List<String> availableOptionsInBrandFilter = pathToPurchasePage.getAvailableOptionsInBrandFilter();
        List<String> optionsToSelectInBrandFilter = availableOptionsInBrandFilter.subList(0, 2);
        pathToPurchasePage.selectItemsFromBrandDropdown(optionsToSelectInBrandFilter);
        Assert.assertEquals(pathToPurchasePage.getSelectedOptionsInBrandFilter(), optionsToSelectInBrandFilter, "Brand Filter is empty");
        Assert.assertEquals(pathToPurchasePage.getSelectedOptionsInCategoryFilter(), List.of(), "Category Filter is not empty");

        List<String> availableOptionsInCategoryFilter = pathToPurchasePage.getAvailableOptionsInCategoryFilter();
        List<String> optionsToSelectInCategoryFilter = availableOptionsInCategoryFilter.subList(0, 2);
        pathToPurchasePage.selectItemsFromCategoryDropdown(optionsToSelectInCategoryFilter);
        Assert.assertEquals(pathToPurchasePage.getSelectedOptionsInBrandFilter(), List.of(), "Brand Filter is not empty");
        Assert.assertEquals(pathToPurchasePage.getSelectedOptionsInCategoryFilter(), optionsToSelectInCategoryFilter, "Category Filter is empty");
    }
}