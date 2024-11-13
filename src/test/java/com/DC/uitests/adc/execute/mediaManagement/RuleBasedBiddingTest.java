package com.DC.uitests.adc.execute.mediaManagement;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.mediaManagement.RuleBasedBiddingPage;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;

public class RuleBasedBiddingTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private RuleBasedBiddingPage ruleBasedBiddingPage;
    private AppHomepage appHomepage;
    int buIdPerformanceHealthEast = 113;

    @BeforeMethod
    public void setupTestMethodForNetNew(final ITestContext testContext, ITestResult tr) throws InterruptedException {
        testMethodName.set(tr.getMethod().getMethodName());
        LOGGER.info("************* STARTED TEST METHOD " + testMethodName + " ***************");
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        appHomepage = new AppHomepage(driver);
        appHomepage.selectBU("Performance Health East");
        appHomepage.clickOnSection("Execute");
        appHomepage.clickLink("Rule-Based Bidding");

        ruleBasedBiddingPage = new RuleBasedBiddingPage(driver);
    }

    @AfterMethod()
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "Verify Display of Rule-Based Bidding Screen")
    public void RBB_RBBScreenIsDisplayed() {
        Assert.assertTrue(ruleBasedBiddingPage.isRuleBasedBiddingScreenDisplayed(), "Rule-Based Bidding Screen is not displayed");
        String currentUrl = ruleBasedBiddingPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("rule-based-bidding"), "Page url does not contain 'rule-based-bidding'");
    }

    @Test(description = "C207853 - Verify that on clicking Create button popup box is shown")
    public void RBB_CreateButtonPopupBoxIsShown() throws InterruptedException {
        Assert.assertTrue(ruleBasedBiddingPage.isCreateRulePopupBoxDisplayed(), "Create Rule popup box is not displayed");
        Assert.assertTrue(ruleBasedBiddingPage.isAllElementsDisplayed(), "All elements are not displayed");
    }

    @Test(description = "C207854 - Verify that Eligible Campaigns dropdown shows list of eligible campaigns with respect to selected BU")
    public void RBB_EligibleCampaignsDropdownShowsListOfEligibleCampaigns() throws InterruptedException, SQLException {
        String statusToSelect = "Eligible";
        Assert.assertTrue(ruleBasedBiddingPage.compareEligibleCampaigns(buIdPerformanceHealthEast, statusToSelect), "Eligible campaigns for BU are not equal to eligible campaigns for BU in DB");
        LOGGER.info("Eligible campaigns for BU are equal to eligible campaigns for BU in DB");
    }
}
