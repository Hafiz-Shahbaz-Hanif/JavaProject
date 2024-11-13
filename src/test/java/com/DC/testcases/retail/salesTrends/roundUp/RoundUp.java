//TODO - Class not used as of 7/17/23 - Will revisit once this page makes it to the merge app

package com.DC.testcases.retail.salesTrends.roundUp;

import com.DC.constants.NetNewConstants;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.retailReporting.RoundUpPage;

import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.lang.reflect.Method;

public class RoundUp extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private RoundUpPage roundUpPage;
    SoftAssert softAssert = new SoftAssert();

    @BeforeMethod
    public void setupTests(ITestContext testContext) throws Exception {
        String suiteName = testContext.getSuite().getName();
        if (suiteName.contains("Default Suite")) {
            String className = testContext.getAllTestMethods()[0].getTestClass().getName().toLowerCase();
            if (!className.contains("apitest")) {
                boolean headless = READ_CONFIG.getHeadlessMode();
                driver = initializeBrowser(testContext, headless);
            }
        }
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);

        String roundUpUrl = NetNewConstants.getReportsUrl("catalog", "reporting/round-up");
        driver.get(roundUpUrl);
        roundUpPage = new RoundUpPage(driver);
    }

    /*@TmsLink("147973")
    @Test(description = "COGS, ASP and MTD-FYTD tiles are displayed with perspective currency symbol.")
    public void Retail_RoundUp_C147973_Test(String TCID, String businessUnit, String client) throws Exception {
        LOGGER.info("RoundUp test case " + TCID + " started");

        softAssert.assertTrue(roundUpPage.isRoundUpHeaderDisplayed());
        roundUpPage.clickApplyButton();
        roundUpPage.shippedCogsTilesContainsCurrency();
        roundUpPage.aspTilesContainsCurrency();
        roundUpPage.mtdTilesContainsCurrency();

        LOGGER.info("RoundUp test case " + TCID + " completed");
    }

    /*@TmsLink("147974")
    @Test(dataProvider = "Retail_RoundUp_C147974", dataProviderClass = RoundUpDataProvider.class, description = "COGS, ASP and MTD-FYTD tiles show percentage for Last Week/Month and Last Year.")
    public void Retail_RoundUp_C147974_Test(String TCID, String businessUnit, String client) throws Exception {
        logger.info("RoundUp test case " + TCID + " started");

        LoginPage lp = new LoginPage(driver);
        lp.login("fila");
        RoundUpPage roundUpPage = new RoundUpPage(driver);

        if (lp.logoPresent()) {
            lp.selectBuisnessunit(businessUnit, client);
            lp.clickRetailRoundUp();
            roundUpPage.roundUpLoadsSuccessfully();
            roundUpPage.clickApply();
            roundUpPage.shippedCogsTilesContainsPercentage();
            roundUpPage.aspTilesContainsPercentage();
        }
        logger.info("RoundUp test case " + TCID + " completed");
    }*/
    
    //@TmsLink("147977")
    @Test(description = "Distributor view Manufacturing is shown by default for Premium clients (or as selected in Client Management). Sourcing for basic clients and sourcing for hybrid.")
    public void Retail_RoundUp_C147977_Test() {
        softAssert.assertTrue(roundUpPage.isRoundUpHeaderDisplayed());
        softAssert.assertTrue(roundUpPage.getDistributorViewValue().contains("Manufacturing"));

        softAssert.assertAll();
    }

    //@TmsLink("147978")
    @Test(description = "Average selling price is shown in bar charts for COGS & YOY % Comparison.")
    public void Retail_RoundUp_C147978_Test() throws InterruptedException {
        softAssert.assertTrue(roundUpPage.averageSellingPriceBarChartsValidation());
        roundUpPage.clickAverageSellingPriceGraphLegend();
        softAssert.assertFalse(roundUpPage.averageSellingPriceBarChartsValidation());

        softAssert.assertAll();
    }
    
    //@TmsLink("147979")
    @Test(description = "If user clicks on a bar chart in COGS by Sub cateogry, respective category is selected in left filter.")
    public void Retail_RoundUp_C147979_Test() throws InterruptedException {
        roundUpPage.clickShippedCogsByCategoryFirstCategoryFilter();
        softAssert.assertTrue(roundUpPage.getCategoryFilterValueFromShippedCogsByCategoryFilter().
                equalsIgnoreCase(roundUpPage.getCategoryFilterValueFromLeftMenuFilter()));

        softAssert.assertAll();
    }
    
    //@TmsLink("147980")
    @Test(description = "If user clicks on a bar chart in COGS by Segment, respective segment is selected in left filter.")
    public void Retail_RoundUp_C147980_Test() throws Exception
	{
        roundUpPage.clickShippedCogsBySegmentFirstCategoryFilter();
        softAssert.assertTrue(roundUpPage.getSegmentFilterValueFromShippedCogsBySegmentFilter().
                equalsIgnoreCase(roundUpPage.getSegmentFilterValueFromLeftMenuFilter()));

        softAssert.assertAll();
    }
        
    //@TmsLink("147981")
    @Test(description = "If user clicks on a bar chart in COGS by Product, respective ASIN is selected in left filter.")
    public void Retail_RoundUp_C147981_Test() throws Exception
	{
        roundUpPage.clickShippedCogsByProductFirstCategoryFilter();
        softAssert.assertTrue(roundUpPage.getAsinFilterValueFromLeftMenuFilter().
                contains(roundUpPage.getAsinFilterValueFromShippedCogsByProductFilter()));

        softAssert.assertAll();
    }
    
    /*@TmsLink("147982")
    @Test(dataProvider = "Retail_RoundUp_C147982", dataProviderClass = RoundUpDataProvider.class, description = "Respective Currency symbols are shown in COGS by Category, COGS by SubCategory, COGS by Segment and COGS by Product. Retail › Retail_Sales Trends_Round Up")
    public void Retail_RoundUp_C147982_Test(String TCID, String businessUnit, String client) throws Exception {
        logger.info("RoundUp test case " + TCID + " started");

        LoginPage lp = new LoginPage(driver);
        lp.login("fila");
        RoundUpPage roundUpPage = new RoundUpPage(driver);
        SoftAssert softAssert = new SoftAssert();

        if (lp.logoPresent()) {
            lp.selectBuisnessunit(businessUnit, client);
            lp.clickRetailRoundUp();
            roundUpPage.roundUpLoadsSuccessfully();
            roundUpPage.clickApply();
            softAssert.assertTrue(roundUpPage.isShippedCogsByCategoryContainsTheRespectiveCurrencySymbol(), "Error: Shipped COGS By Category doesn't contains the respective currency symbol");
            softAssert.assertTrue(roundUpPage.isShippedCogsBySubCategoryContainsTheRespectiveCurrencySymbol(), "Error: Shipped COGS By Sub-Category doesn't contains the respective currency symbol");
            softAssert.assertTrue(roundUpPage.isShippedCogsBySegmentContainsTheRespectiveCurrencySymbol(), "Error: Shipped COGS By Segment doesn't contains the respective currency symbol");
            softAssert.assertTrue(roundUpPage.isShippedCogsByProductContainsTheRespectiveCurrencySymbol(), "Error: Shipped COGS By Product doesn't contains the respective currency symbol");
        }
        
        logger.info("RoundUp test case " + TCID + " completed");
    }*/
    
    //@TmsLink("147983")
    @Test(description = "User can toggle on/off the callouts")
    public void Retail_RoundUp_C147983_Test() throws Exception
	{
        softAssert.assertFalse(roundUpPage.isCalloutsCheckboxToggleOff());
        softAssert.assertTrue(roundUpPage.isCalloutsCheckboxToggleOn());

        softAssert.assertAll();
    }
    
    //@TmsLink("147984")
    @Test(description = "Priority ASIN is toggled off by default.")
    public void Retail_RoundUp_C147984_Test() throws Exception
	{
        Assert.assertFalse(roundUpPage.isPriorityAsinsOnlyToggleOffByDefault());
    }
    
    //@TmsLink("147985")
    @Test(description = "Last Week/Month, Previous Period and Last Year are shown in COGS by SubCategory, COGS by Segment and COGS by Product")
    public void Retail_RoundUp_C147985_Test() {
        softAssert.assertTrue(roundUpPage.isShippedCogsBySubCategoryContainsCorrectColumns());
        softAssert.assertTrue(roundUpPage.isShippedCogsBySegmentContainsCorrectColumns());
        softAssert.assertTrue(roundUpPage.isShippedCogsByProductContainsCorrectColumns());

        softAssert.assertAll();
    }

    /*@TmsLink("147986")
    @Test(dataProvider = "Retail_RoundUp_C147986", dataProviderClass = RoundUpDataProvider.class, description = "For Monthly, Last 6 months data is to be shown in COGS & YOY % Comparison by default")
    public void Retail_RoundUp_C147986_Test(String TCID, String businessUnit, String client) throws Exception
	{
        logger.info("Retail_RoundUp_C147986 test case " + TCID + " started");
        LoginPage lp = new LoginPage(driver);
        lp.login("fila");
        RoundUpPage rs = new RoundUpPage(driver);
        if (lp.logoPresent()) {
            lp.selectBuisnessunit(businessUnit, client);
            lp.clickRetailRoundUp();
            if (rs.roundUpLoadsSuccessfully())
            {
            	if (rs.monthlyDropDownValidation())
            	{
            		logger.info("Retail_RoundUp_C147986 test case " + TCID + " completed successfully");
            		Assert.assertTrue(true, "Retail_RoundUp_C147986 test case " + TCID + " completed successfully");
            	}
            }
            else
            {
            	logger.error("Retail_RoundUp_C147986 test case " + TCID + ": roundUp page load failed");
            	Assert.fail("Retail_RoundUp_C147986 test case " + TCID + ": roundUp page load failed");
            }
        }
    }
    
    @TmsLink("147987")
    @Test(dataProvider = "Retail_RoundUp_C147987", dataProviderClass = RoundUpDataProvider.class, description = "For Weekly, Last 13 weeks data is to be shown in COGS & YOY % Comparison by default.")
    public void Retail_RoundUp_C147987_Test(String TCID, String businessUnit, String client) throws Exception
	{
        logger.info("Retail_RoundUp_C147987 test case " + TCID + " started");
        LoginPage lp = new LoginPage(driver);
        lp.login("fila");
        RoundUpPage rs = new RoundUpPage(driver);
        if (lp.logoPresent()) {
            lp.selectBuisnessunit(businessUnit, client);
            lp.clickRetailRoundUp();
            if (rs.roundUpLoadsSuccessfully())
            {
            	if (rs.weeklyIntervalValidation())
            	{
            		logger.info("Retail_RoundUp_C147987 test case " + TCID + " completed successfully");
            		Assert.assertTrue(true, "Retail_RoundUp_C147987 test case " + TCID + " completed successfully");
            	}
            }
            else
            {
            	logger.error("Retail_RoundUp_C147987 test case " + TCID + ": roundUp page load failed");
            	Assert.fail("Retail_RoundUp_C147987 test case " + TCID + ": roundUp page load failed");
            }
        }
    }
    
    @TmsLink("147989")
    @Test(dataProvider = "Retail_RoundUp_C147989", dataProviderClass = RoundUpDataProvider.class, description = "User can view details for an asin by clicking on view details.")
    public void Retail_RoundUp_C147989_Test(String TCID, String businessUnit, String client) throws Exception
	{
        logger.info("Retail_RoundUp_C147989 test case " + TCID + " started");
        LoginPage lp = new LoginPage(driver);
        lp.login("fila");
        RoundUpPage rs = new RoundUpPage(driver);
        if (lp.logoPresent()) {
            lp.selectBuisnessunit(businessUnit, client);
            lp.clickRetailRoundUp();
            if (rs.roundUpLoadsSuccessfully())
            {
            	if (rs.asinDetailsValidation())
            	{
            		logger.info("Retail_RoundUp_C147989 test case " + TCID + " completed successfully");
            		Assert.assertTrue(true, "Retail_RoundUp_C147989 test case " + TCID + " completed successfully");
            	}
            }
            else
            {
            	logger.error("Retail_RoundUp_C147989 test case " + TCID + ": roundUp page load failed");
            	Assert.fail("Retail_RoundUp_C147989 test case " + TCID + ": roundUp page load failed");
            }
        }
    }
    
    @TmsLink("147990")
    @Test(dataProvider = "Retail_RoundUp_C147990", dataProviderClass = RoundUpDataProvider.class, description = "User can view gains and drainers for an asin by clicking on gainers and drainers.")
    public void Retail_RoundUp_C147990_Test(String TCID, String businessUnit, String client) throws Exception
	{
        logger.info("Retail_RoundUp_C147990 test case " + TCID + " started");
        LoginPage lp = new LoginPage(driver);
        lp.login("fila");
        RoundUpPage rs = new RoundUpPage(driver);
        if (lp.logoPresent()) {
            lp.selectBuisnessunit(businessUnit, client);
            lp.clickRetailRoundUp();
            if (rs.roundUpLoadsSuccessfully())
            {
            	if (rs.asinGainersDrainersValidation())
            	{
            		logger.info("Retail_RoundUp_C147990 test case " + TCID + " completed successfully");
            		Assert.assertTrue(true, "Retail_RoundUp_C147990 test case " + TCID + " completed successfully");
            	}
            }
            else
            {
            	logger.error("Retail_RoundUp_C147990 test case " + TCID + ": roundUp page load failed");
            	Assert.fail("Retail_RoundUp_C147990 test case " + TCID + ": roundUp page load failed");
            }
        }
    }
    
    @TmsLink("147991")
    @Test(dataProvider = "Retail_RoundUp_C147991", dataProviderClass = RoundUpDataProvider.class, description = "User can view amazon details page for an asin by clicking on view amazon details.")
    public void Retail_RoundUp_C147991_Test(String TCID, String businessUnit, String client) throws Exception
	{
        logger.info("Retail_RoundUp_C147991 test case " + TCID + " started");
        LoginPage lp = new LoginPage(driver);
        lp.login("fila");
        RoundUpPage rs = new RoundUpPage(driver);
        if (lp.logoPresent()) {
            lp.selectBuisnessunit(businessUnit, client);
            lp.clickRetailRoundUp();
            if (rs.roundUpLoadsSuccessfully())
            {
            	if (rs.asinAmazonDetailsValidation())
            	{
            		logger.info("Retail_RoundUp_C147991 test case " + TCID + " completed successfully");
            		Assert.assertTrue(true, "Retail_RoundUp_C147991 test case " + TCID + " completed successfully");
            	}
            }
            else
            {
            	logger.error("Retail_RoundUp_C147991 test case " + TCID + ": roundUp page load failed");
            	Assert.fail("Retail_RoundUp_C147991 test case " + TCID + ": roundUp page load failed");
            }
        }
    }*/
    
    //@TmsLink("147992")
    @Test(description = "User can select an asin by either clicking on the filter or by clicking on the cogs by product")
    public void Retail_RoundUp_C147992_Test() throws Exception {
        roundUpPage.clickShippedCogsByProductFirstCategoryFilter();
        Assert.assertTrue(roundUpPage.getAsinFilterValueFromLeftMenuFilter().
                contains(roundUpPage.getAsinFilterValueFromShippedCogsByProductFilter()));
    }
    
    //@TmsLink("147993")
    @Test(description = "Verify that Average Selling Price is shown for ARAP Clients.")
    public void Retail_RoundUp_C147993_Test()
	{
        Assert.assertTrue(roundUpPage.averageSellingPriceBarChartsValidation());
    }
    
  /*@TmsLink("156536")
  @Test(dataProvider = "C156536", dataProviderClass = RoundUpDataProvider.class, description = "Verify that user should be able to open a new tab with Gainers and Drainers by clicking the bar chart icon in the table view or the expanded table view on Shipped COGS By Product Section")
  public void Retail_RoundUp_C156536_Test(String TCID, String businessUnit, String client) throws Exception {

		logger.info("** Query Trends test case "+TCID + " has started.");

		LoginPage lp = new LoginPage(driver);
		Filters fltr = new Filters(driver);
		RoundUpPage rup = new RoundUpPage(driver);
		SoftAssert softAssert = new SoftAssert();
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSalesTrends("Round Up");
		fltr.saveBtn();
		
		rup.verifyShippedCogsByProductGainersDrainersIcon(softAssert);

		softAssert.assertAll();
		logger.info("** Execution for test case " + TCID + " completed successfully");
  }*/
  
  //@TmsLink("156535")
  @Test(description = "Verify that user should be able to open a new tab with ASIN Details by clicking the magnifying glass icon in the table view or the expanded table view on Shipped COGS By Product Section")
  public void Retail_RoundUp_C156535_Test() throws Exception {
      roundUpPage.verifyDataIsLoadedInShippedCogsByProductBlock();
      String asinValue = roundUpPage.getAsinFilterValueFromShippedCogsByProductFilter();
      roundUpPage.clickShippedCogsBySegmentFirstAsinMagnifyingFilter();
      softAssert.assertTrue(roundUpPage.verifyAsinDetailTabOpens());
      softAssert.assertTrue(asinValue.contains(roundUpPage.getAsinValueFromAsinDetailPage()));

      softAssert.assertAll();
  }
  
  /*@TmsLink("156534")
  @Test(dataProvider = "C156534", dataProviderClass = RoundUpDataProvider.class, description = "Verify that user should be able to open a new tab with Retail Scratchpad by clicking the graph icon in the table view or the expanded table view on Sales by Category, Subcategory, Segment and Product sections")
  public void Retail_RoundUp_C156534_Test(String TCID, String businessUnit, String client) throws Exception {

		logger.info("** Query Trends test case "+TCID + " has started.");

		LoginPage lp = new LoginPage(driver);
		Filters fltr = new Filters(driver);
		RoundUpPage rup = new RoundUpPage(driver);
		SoftAssert softAssert = new SoftAssert();
		
		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSalesTrends("Round Up");
		fltr.saveBtn();
		
		rup.verifyShippedCogsByCategoryRetailScratchpedIcon(softAssert);
		rup.verifyShippedCogsBySubCategoryRetailScratchpedIcon(softAssert);
		rup.verifyShippedCogsBySegmentRetailScratchpedIcon(softAssert);
		rup.verifyShippedCogsByProductRetailScratchpedIcon(softAssert);
		
		softAssert.assertAll();
		logger.info("** Execution for test case " + TCID + " completed successfully");
  }
  
  @TmsLink("156532")
  @Test(dataProvider = "C156532", dataProviderClass = RoundUpDataProvider.class, description = "Verify that user should be able to choose which columns to display using stacked line icon from header filters on Sales by Category, Subcategory, Segment and Product sections")
  public void Retail_RoundUp_C156532_Test(String TCID, String businessUnit, String client) throws Exception {

		logger.info("** Query Trends test case "+TCID + " has started.");

		LoginPage lp = new LoginPage(driver);
		Filters fltr = new Filters(driver);
		RoundUpPage rup = new RoundUpPage(driver);
		Page pg = new Page(driver);
		SoftAssert softAssert = new SoftAssert();

		lp.loginTemp("fila");
		lp.selectDcBusinessUnit(businessUnit);
		lp.openSalesTrends("Round Up");
		fltr.saveBtn();
		
		Thread.sleep(500);
		pg.waitForElementToBeInvisible(RoundUpPage.shippedCogsByCategoryProgressBar, 120, "Shipped Cogs By Category Progress Bar");
		
		rup.verifyColumnSelectionShippedCogsByCategory(softAssert);
		rup.verifyColumnSelectionShippedCogsBySegment(softAssert);
		rup.verifyColumnSelectionShippedCogsBySubCategory(softAssert);
		rup.verifyColumnSelectionShippedCogsByProduct(softAssert);
		
		softAssert.assertAll();
		
		logger.info("** Execution for test case " + TCID + " completed successfully");
  }*/

    @AfterMethod
    public void tearDownTestMethod(final ITestContext testContext, ITestResult result) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        String testCaseId = getTestCaseIdValue(method);
        LOGGER.info("************* FINISHED TEST METHOD " + testMethodName + " ***************");
        LOGGER.info("TEST CASE ID: " + testCaseId);
        LOGGER.info("***** - QUITTING BROWSER - *****");
        driver.quit();
    }
}