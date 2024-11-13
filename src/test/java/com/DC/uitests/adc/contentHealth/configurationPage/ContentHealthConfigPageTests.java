package com.DC.uitests.adc.contentHealth.configurationPage;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.beta.contentHealth.ContentHealthDashboardPage;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.List;
import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class ContentHealthConfigPageTests extends BaseClass {

    private final String USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private static final String FCC_BETA_URL = READ_CONFIG.getDcBetaUrl();
    public static final String INSIGHTS_CONTENT_HEALTH_URL = FCC_BETA_URL + "/content-health";
    public ContentHealthDashboardPage contentHealthDashboardPage;

    @BeforeClass(alwaysRun = true)
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USERNAME, PASSWORD);
        driver.get(INSIGHTS_CONTENT_HEALTH_URL);
        contentHealthDashboardPage = new ContentHealthDashboardPage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify basic features load when user enters content health dashboard")
    public void contentHealthDashboardSmokeTest(){
        Assert.assertTrue(contentHealthDashboardPage.isContentScoresOverTimeGraphVisible(), "Content Scores Over Time graph was not visible");
        Assert.assertTrue(contentHealthDashboardPage.isContentConfigurationButtonVisible(), "Content configuration button was not visible for support user");
        Assert.assertTrue(contentHealthDashboardPage.isContentScoresOverTimeRetailerTableVisible(), "Content scores retailer table was not visible");
        Assert.assertTrue(contentHealthDashboardPage.isEditScoresButtonVisible(), "Edit scores button was not visible");
        List<String> expectedFiltersInSideBar = Arrays.asList("Supply Chain", "Media", "Retail", "Sales Explorer", "All Reports", "AMC Tools", "Client Activation");
        List<String> actualFiltersInSideBar = contentHealthDashboardPage.getSideBarFilterOptions();
        actualFiltersInSideBar.removeAll(Arrays.asList("", null));
        Assert.assertEqualsNoOrder(actualFiltersInSideBar, expectedFiltersInSideBar, "Expected filters were not displayed in sidebar" +
                "\n EXPECTED:" + expectedFiltersInSideBar +
                "\n ACTUAL:" + actualFiltersInSideBar);
        List<String> expectedScoresOverTimeTabs = Arrays.asList("Retailers", "Categories", "Brands");
        List<String> actualScoresOverTimeTabs = contentHealthDashboardPage.getScoresOverTimeTabs();
        Assert.assertEqualsNoOrder(actualScoresOverTimeTabs, expectedScoresOverTimeTabs, "Expected scores over time filters were not displayed " +
                "\n EXPECTED:" + expectedScoresOverTimeTabs +
                "\n ACTUAL:" + actualScoresOverTimeTabs);
    }

    @Test(priority = 1, description = "Verify sidebar can be collapsed and expanded")
    public void verifySideBarOpenCloseButtonFunctionsCorrectly() throws InterruptedException {
        Assert.assertTrue(contentHealthDashboardPage.isSideBarExpanded(), "Sidebar was not expanded by default when navigating to content health page");
        contentHealthDashboardPage.closeSideBar();
        Assert.assertFalse(contentHealthDashboardPage.isSideBarExpanded(), "Sidebar was not closed after clicking on sidebar toggle button");
    }

    @Test(priority = 2, description = "Verify a question icon is displayed next to Content Health header and a tooltip appears on hover")
    public void verifyToolTipsAppearOnHoverInContentHealthAndScoresHeaders(){
        Assert.assertTrue(contentHealthDashboardPage.hoverOverContentHealthHeaderTooltipAndReturnPopupVisiblity(), "Content Health Header tool tip popup did not appear on hover");
        Assert.assertTrue(contentHealthDashboardPage.hoverOverCurrentContentHealthTooltipAndReturnPopupVisiblity(), "Current Content Health tool tip popup did not appear on hover");
        Assert.assertTrue(contentHealthDashboardPage.hoverOverContentScoresOverTimeTooltipAndReturnPopupVisiblity(), "Content Scores Over Time tool tip popup did not appear on hover");
    }
}


