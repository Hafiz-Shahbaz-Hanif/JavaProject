package com.DC.uitests.adc.execute.mediaManagement.budgetManager;

import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.mediaManagement.budgetManager.BudgetManagerPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SharedMethods;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InstacartBudgetPacingTest extends BaseClass {
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private BudgetManagerPage budgetManagerPage;

    @BeforeMethod()
    public void setupTests(ITestContext testContext) throws Exception {

        driver = initializeNonIncognitoBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("Hersheys US");
        AppHomepage appHomepage = new AppHomepage(driver);
        appHomepage.clickOnSection("Execute");
        appHomepage.clickLink("Budget Manager");
        budgetManagerPage = new BudgetManagerPage(driver);
        budgetManagerPage.selectBudgetTracker();
        budgetManagerPage.selectSinglePlatform("Amazon","Walmart","Criteo","Amazon DSP");
        budgetManagerPage.editPacingMethod();
        Thread.sleep(5000);
    }
    @Test(description = "EMP-775 - Verify that the selected Pacing Method should get applied.")
    public void Pacing_Method_Test() throws InterruptedException {
        LOGGER.info("Verify Pacing Detail Page is displayed");
        Assert.assertTrue(budgetManagerPage.isPacingDetailPageIsDisplayed(),"Pacing Detail Page is not displayed");

        LOGGER.info("Verify Pacing Method Dropdown Option");
        budgetManagerPage.verifyPacingMethodDropdownOption();
        Assert.assertTrue(budgetManagerPage.applyPacingMethod("Even Spend Allocated Daily"),"Selected Pacing Method is not Displayed");
    }
    @Test(description = "EMP-775 - Verify Budget Settings modal for Custom Daily method.")
    public void Budget_Setting_Model_Daily_Test() throws InterruptedException {

        budgetManagerPage.selectSpecificDate();

        LOGGER.info("Remove Pacing Method");
        Assert.assertTrue(budgetManagerPage.verifyAppliedPacingMethodDisplayed("None"), "Selected Pacing Method None is not Displayed");

        LOGGER.info("Select the Custom Daily Pacing Method");
        budgetManagerPage.verifyCustomPacingMethod("Custom Daily");
        Assert.assertTrue(budgetManagerPage.verifyBudgetAdjustmentDisplayed(),"Budget Adjustment is not Displayed");
        Assert.assertTrue(budgetManagerPage.verifyCreateBudgetRuleButtonDisplayed(),"Create Budget Rule button is not Displayed");
        Assert.assertTrue(budgetManagerPage.verifyCancelButtonDisplayed(),"Cancel button is not Displayed");

        LOGGER.info("Validate Custom Daily Budget Functionality");
        Assert.assertTrue(budgetManagerPage.validateCustomDailyBudgetRule(),"Custom Daily Budget is not correctly Displayed");
    }

    @Test(description = "EMP-775 - Verify User can create budget rules for custom daily pacing type")
    public void Budget_Rule_Custom_Daily_Test() throws InterruptedException {

        LOGGER.info("Verify Custom Daily Pacing Method");
        budgetManagerPage.verifyCustomPacingMethod("Custom Daily");
        Assert.assertTrue(budgetManagerPage.verifyCreateBudgetRuleButtonDisplayed(),"Create Budget Rule button is not Displayed");

        LOGGER.info("Validate Create Budget rule functionality");
        budgetManagerPage.validateCreateBudgetRule("Custom Daily");
    }

    @Test(description = "EMP-775 - Verify user can remove applied pacing methods.")
    public void Pacing_Method_Remove_Functionality_Test() throws InterruptedException {

        LOGGER.info("Verify Pacing Method Dropdown Option");
        budgetManagerPage.verifyPacingMethodDropdownOption();
        Assert.assertTrue(budgetManagerPage.applyPacingMethod("Even Spend Allocated Daily"),"Selected Pacing Method is not Displayed");

        LOGGER.info("Remove Pacing Method");
        Assert.assertTrue(budgetManagerPage.verifyAppliedPacingMethodDisplayed("None"),"Selected Pacing Method None is not Displayed");

    }

    @Test(description = "EMP-775 - Verify that user should be able to apply pacing methods for selected IO Segment")
    public void PAcing_Method_Selected_IO_Segment_Test() throws InterruptedException {

        LOGGER.info("Verify Pacing Method Dropdown Option");
        budgetManagerPage.verifyPacingMethodDropdownOption();
        Assert.assertTrue(budgetManagerPage.applyPacingMethod("Even Spend Allocated Daily"),"Selected Pacing Method is not Displayed");

        LOGGER.info("Move back to Budget Tracker Page");
        driver.navigate().back();
        driver.navigate().refresh();
        budgetManagerPage.selectBudgetTracker();
        budgetManagerPage.selectSinglePlatform("Amazon","Walmart","Criteo","Amazon DSP");

        LOGGER.info("Verify Applied Pacing Method displayed on Pacing details page");
        Assert.assertTrue(budgetManagerPage.verifyUpdatedPacingMethodColumn("EVEN_DAILY"),"Applied pacing Method not displayed on Pacing Method Column");
    }

    @Test(description = "EMP-775 - Verify Budget Settings modal for Custom Monthly pacing method.")
    public void Budget_Setting_Model_Custom_Monthly_Test() throws InterruptedException {

        LOGGER.info("Select the Custom Monthly Pacing Method");
        budgetManagerPage.verifyCustomPacingMethod("Custom Monthly");
        Assert.assertTrue(budgetManagerPage.verifyBudgetAdjustmentDisplayed(),"Budget Adjustment is not Displayed");
        Assert.assertTrue(budgetManagerPage.verifyCancelButtonDisplayed(),"Cancel button is not Displayed");

        LOGGER.info("Validate Custom Monthly Budget Functionality");
        Assert.assertTrue(budgetManagerPage.validateCustomMonthlyBudgetRule(),"Custom Monthly Budget is not correctly Displayed");
    }

    @Test(description = "EMP-775 - Verify the daily breakdown functionality.")
    public void Daily_Breakdown_functionality_Test() throws InterruptedException {

        LOGGER.info("Hover and Click on Daily Breakdown");
        budgetManagerPage.Daily_Breakdown_Functionality();

    }
    @Test(description = "EMP-775 - Verify the export functionality. for daily breakdown.")
    public void Daily_Breakdown_functionality_Export_Test() throws Exception {

        LOGGER.info("Hover and Click on Daily Breakdown");
        budgetManagerPage.Daily_Breakdown_Functionality();

        LOGGER.info("Verify Export Button Displayed");
        Assert.assertTrue(budgetManagerPage.isExportButtonDisplayed(),"Export button is not Displayed");

        LOGGER.info("Verify Spend Column Header Displayed");
        Assert.assertTrue(budgetManagerPage.isSpendColumnDisplayed(),"Spend Column Header is not Displayed");

        LOGGER.info("Verify Daily Breakdown Export Functionality");
        budgetManagerPage.verifyDailyBreakdownExportFunctionality();

        LOGGER.info("Verifying new columns are added to excel file");
        String downloadPath = SharedMethods.isFileDownloaded(".csv", "Pacing Daily Breakdown", 60, downloadFolder);
        Assert.assertNotNull(downloadPath, "File is not downloaded");
        Assert.assertTrue(budgetManagerPage.verifyCSVHasCorrectColumns(downloadPath), "CSV file does not have correct columns");

    }


}

