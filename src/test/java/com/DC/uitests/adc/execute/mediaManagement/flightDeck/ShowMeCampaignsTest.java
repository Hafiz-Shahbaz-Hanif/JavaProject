package com.DC.uitests.adc.execute.mediaManagement.flightDeck;

import com.DC.constants.NetNewConstants;
import com.DC.db.execute.SharedDBMethods;
import com.DC.objects.mediaManagement.CampaignWizardKeywordTableData;
import com.DC.objects.mediaManagement.CampaignWizardSummaryTableData;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.mediaManagement.flightDeck.*;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.enums.Enums;
import org.openqa.selenium.TimeoutException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ShowMeCampaignsTest extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();
    private FlightDeck flightDeck;
    private ShowMeCampaigns showMeCampaigns;
    private CampaignWizardModal campaignWizardModal;
    private String campaignThatWasUpdated;
    private String itemToCheck;
    private String mostRecentDateFromDB;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("McCormick");
        Thread.sleep(2000);
        flightDeck = new FlightDeck(driver);
        flightDeck.clickOnSection("Execute");
        flightDeck.clickOnPage("FlightDeck");
        showMeCampaigns = (ShowMeCampaigns) flightDeck.selectShowMeOption(Enums.FlightDeckShowMe.CAMPAIGNS);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify that using select all checkbox on Show Me Campaigns page and editing to view Keywords/Targets adds campaign names to filters correctly")
    public void MDM_ShowMeCampaigns_VerifySelectAllCampaignsEditKeywordsWorksCorrectly() throws InterruptedException {
        showMeCampaigns.sortCampaignsBySpendColumn("Greater than", "35");
        List<String> expectedCampaignNames = showMeCampaigns.getCampaignNameColumnValues();
        showMeCampaigns.checkSelectAllCheckBox();
        showMeCampaigns.openEditDropdownAndSelectViewKeywordsTargets();
        List<String> campaignNamesInFilter = showMeCampaigns.getActiveCampaignNameFilters();
        Assert.assertTrue(campaignNamesInFilter.containsAll(expectedCampaignNames), "After using select all checkbox and using edit to view keywords/targets, campaign names were not properly applied to filter" +
                "\n EXPECTED: " + expectedCampaignNames +
                "\n ACTUAL: " + campaignNamesInFilter);
    }

    @Test(priority = 2, description = "After using select all checkbox and editing campaign values all campaign's budget values should be updated’")
    public void MDM_VerifySelectAllCampaignEditOperationsFunctionsCorrectly() throws InterruptedException {
        showMeCampaigns.filterCampaignType("Sponsored Products");
        List<Double> budgetColumnValuesBeforeEdit = showMeCampaigns.getCampaignBudgetColumnValues();
        showMeCampaigns.checkSelectAllCheckBox();
        showMeCampaigns.openEditDropdownAndSelectAdjustCampaignDailyBudgets("Increase by", "2");
        List<Double> budgetColumnValuesAfterEdit = showMeCampaigns.getCampaignBudgetColumnValues();
        Assert.assertNotEquals(budgetColumnValuesAfterEdit, budgetColumnValuesBeforeEdit);
        for (int i = 0; i < budgetColumnValuesAfterEdit.size(); i++) {
            Double valueBeforeEdit = Double.sum(budgetColumnValuesBeforeEdit.get(i), 2.0);
            Assert.assertEquals(valueBeforeEdit, budgetColumnValuesAfterEdit.get(i));
        }

        showMeCampaigns.checkSelectAllCheckBox();
        showMeCampaigns.openEditDropdownAndSelectAdjustCampaignDailyBudgets("Decrease by", "2");
    }

    @Test(priority = 3, description = "After using multi select on campaigns and editing campaign values selected campaign's budget values should be updated")
    public void MDM_VerifyMultiSelectCampaignEditOperationsFunctionsCorrectly() throws InterruptedException {
        List<Integer> productsToSelect = List.of(1, 3, 5);
        showMeCampaigns.filterCampaignType("Sponsored Products");
        List<Double> budgetColumnValues = showMeCampaigns.getCampaignBudgetColumnValues();
        List<Double> budgetColumnValuesBeforeEdit = List.of(budgetColumnValues.get(0), budgetColumnValues.get(2), budgetColumnValues.get(4));
        for (int product : productsToSelect) {
            showMeCampaigns.clickCheckBoxForSpecificRow(product);
        }

        showMeCampaigns.openEditDropdownAndSelectAdjustCampaignDailyBudgets("Increase by", "2");
        budgetColumnValues = showMeCampaigns.getCampaignBudgetColumnValues();
        List<Double> budgetColumnValuesAfterEdit = List.of(budgetColumnValues.get(0), budgetColumnValues.get(2), budgetColumnValues.get(4));
        Assert.assertNotEquals(budgetColumnValuesAfterEdit, budgetColumnValuesBeforeEdit);
        for (int i = 0; i < budgetColumnValuesAfterEdit.size(); i++) {
            Double valueBeforeEdit = Double.sum(budgetColumnValuesBeforeEdit.get(i), 2.0);
            Assert.assertEquals(valueBeforeEdit, budgetColumnValuesAfterEdit.get(i));
        }

        for (int product : productsToSelect) {
            showMeCampaigns.clickCheckBoxForSpecificRow(product);
        }
        showMeCampaigns.openEditDropdownAndSelectAdjustCampaignDailyBudgets("Decrease by", "2");
    }

    @Test(priority = 4, description = "MDM-131 - Campaign wizard functions as expected")
    public void MDM_ShowMeCampaigns_VerifyCampaignWizardFunctionsCorrectly() throws InterruptedException {
        showMeCampaigns.selectBU("Hersheys - US");
        Assert.assertTrue(showMeCampaigns.isCreateCampaignsButtonVisible(), "Create Campaigns button is not visible on Show Me Campaigns screen");
        List<String> expectedCampaignSteps = List.of("Campaign Type", "Products", "Segmentation", "Targeting");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(campaignWizardModal = showMeCampaigns.clickCreateCampaignButton(), "Campaign Wizard modal was not opened after clicking create campaign button");
        campaignWizardModal.areCampaignWizardStepsVisible();
        List<String> actualCampaignSteps = campaignWizardModal.getCampaignWizardSteps();
        softAssert.assertEquals(actualCampaignSteps, expectedCampaignSteps, "Campaign Wizard steps are not displayed as expected");

        campaignWizardModal.clickCampaignWizardStep("Campaign Type");
        Assert.assertTrue(campaignWizardModal.isCampaignTypeSelectCardVisible(), "Campaign Type sponsored products card was not visible on Campaign Wizard modal");
        campaignWizardModal.clickSponsoredProductsCheckbox();
        Assert.assertTrue(campaignWizardModal.isSponsoredProductsCheckboxSelected(), "Sponsored products card was not selected by default on Campaign Wizard modal");

        campaignWizardModal.clickCampaignWizardStep("Products");
        Assert.assertTrue(campaignWizardModal.isProductsSelectorCardVisible(), "Product Selector card was not visible on Campaign Wizard modal");
        String asinToTest = "B01GEIMR2K";
        campaignWizardModal.searchProductSelectorByASIN(asinToTest);
        List<String> asinsDisplayedInTable = campaignWizardModal.getAllAsinsInTable();
        for (String asin : asinsDisplayedInTable) {
            Assert.assertTrue(asin.contains(asinToTest));
        }

        //Assert only one ASIN is able to be selected for any campaign
        campaignWizardModal.selectASINCheckbox(2);
        Assert.assertTrue(campaignWizardModal.isAsinCheckBoxSelected(2), "ASIN checkbox was not selected");
        //unselect
        campaignWizardModal.selectASINCheckbox(1);
        Assert.assertTrue(campaignWizardModal.isAsinCheckBoxSelected(1), "ASIN checkbox was not selected");
        Assert.assertFalse(campaignWizardModal.isAsinCheckBoxSelected(2), "ASIN checkbox still selected after another row's checkbox was selected");
        Assert.assertTrue(campaignWizardModal.doesAsinRedirectToSameASINUrl(asinToTest), asinToTest + "did not redirect to correct url in the PDP after clicking on it in the Product Selector card");

        campaignWizardModal.clickCampaignWizardStep("Segmentation");
        Assert.assertTrue(campaignWizardModal.isAddSegmentationCardVisible(), "Segmentation card was not visible on Campaign Wizard modal");
        campaignWizardModal.setSegmentationBriefAsinTitleField("Automated Test");
        List<String> segmentationFilters = List.of("Claim", "Brand", "IO Segment");
        List<String> segmentationFilterSelections = new ArrayList<>();
        for (String segmentationFilter : segmentationFilters) {
            String valueToSelect = campaignWizardModal.getAllValuesInSegmentationDropdownMenu(segmentationFilter).get(3);
            segmentationFilterSelections.add(valueToSelect);
            campaignWizardModal.openSegmentationDropdownAndSelectValue(segmentationFilter, valueToSelect);
            Assert.assertEquals(campaignWizardModal.getSelectedSegmentationValue(segmentationFilter), valueToSelect, "Correct segmentation filter was not displayed on dropdown after being selected");
        }

        campaignWizardModal.clickCampaignWizardStep("Targeting");
        Assert.assertTrue(campaignWizardModal.isTargetingSectionVisible(), "Targeting card was not visible on Campaign Wizard modal");
        campaignWizardModal.deselectTargetingType(Enums.TargetingType.Auto);
        campaignWizardModal.deselectTargetingType(Enums.TargetingType.Manual);
        Assert.assertFalse(campaignWizardModal.isAutoKeywordsBarVisible(), "Auto Keywords Targeting bar was visible when auto keywords checkbox was unselected");
        Assert.assertFalse(campaignWizardModal.isBrandedKeywordsBarVisible(), "Branded Keywords Targeting bar was visible when manual keywords checkbox was unselected");
        Assert.assertFalse(campaignWizardModal.isNonBrandedKeywordsBarVisible(), "Non Branded Keywords Targeting bar was visible when manual keywords checkbox was unselected");
        Assert.assertFalse(campaignWizardModal.isCompetitorKeywordsBarVisible(), "Competitor Keywords Targeting bar was visible when manual keywords checkbox was unselected");

        campaignWizardModal.selectTargetingType(Enums.TargetingType.Auto);
        Assert.assertTrue(campaignWizardModal.isAutoKeywordsBarVisible(), "Auto Keywords Targeting bar was not visible when auto keywords checkbox was selected");
        verifyManualTargetingTypeSectionsAreNotVisibleIfAutoIsSelected();
        campaignWizardModal.deselectTargetingType(Enums.TargetingType.Auto);
        campaignWizardModal.selectTargetingType(Enums.TargetingType.Manual);
        verifyManualTargetingTypeSectionsAreVisibleIfManualIsSelected();

        String campaignToTest = "Branded";
        campaignWizardModal.openKeywordTypeBar(campaignToTest);
        verifyKeywordCanBeAddedToCampaign(campaignToTest, "1", Enums.KeywordMatchType.Exact, "BrandTest");
        campaignToTest = "Non Branded";
        campaignWizardModal.openKeywordTypeBar(campaignToTest);
        verifyKeywordCanBeAddedToCampaign(campaignToTest, "2", Enums.KeywordMatchType.Broad, "NonBrandTest");
        campaignToTest = "Competitor";
        campaignWizardModal.openKeywordTypeBar(campaignToTest);
        verifyKeywordCanBeAddedToCampaign(campaignToTest, "3", Enums.KeywordMatchType.Phrase, "CompTest");

        String testCampaignBudget = "10";
        campaignWizardModal.setCampaignBudget(testCampaignBudget);
        Assert.assertEquals(campaignWizardModal.getCampaignBudgetFieldValue(), testCampaignBudget, "Budget value was not updated as expected in Campaign Wizard modal");

        // Leaving these commented out because I am not sure if these steps will be re-added to campaign wizard. Feel free to remove if it is confirmed they won't be.
         /*String expectedBiddingStrategy = "Dynamic Bids- down only";
        Assert.assertTrue(campaignWizardModal.isBiddingStrategySelected(expectedBiddingStrategy), expectedBiddingStrategy + "was not selected after clicking on checkbox");
        campaignWizardModal.selectBiddingStrategy(false);
        expectedBiddingStrategy = "Dynamic Bids - up & down";
        campaignWizardModal.selectBiddingStrategy(true);
        Assert.assertTrue(campaignWizardModal.isBiddingStrategySelected(expectedBiddingStrategy), expectedBiddingStrategy + "was not selected after clicking on checkbox");
*/
        //Assert.assertTrue(campaignWizardModal.isDayPartingFieldVisible(), "Day Parting field was not visible in Campaign Wizard modal");
        //String daypartingConfig = "HSY US Chocolate";
        //campaignWizardModal.selectDayPartingConfig(daypartingConfig);

        Assert.assertTrue(campaignWizardModal.isLaunchCampaignButtonEnabled(), "Launch Campaign button was not enabled on Campaign Wizard Launch Modal");
        campaignWizardModal.clickLaunchCampaignButton();
        Assert.assertTrue(campaignWizardModal.isCampaignWizardLaunchModalPopUpVisible(), "Campaign Wizard Launch modal pop up was not visible after clicking Next button on Campaign Wizard modal");
        CampaignWizardSummaryTableData campaignWizardSummaryData = campaignWizardModal.getCampaignWizardSelectedSettings();
        Assert.assertTrue(campaignWizardSummaryData.campaignName.contains(asinToTest), asinToTest + "was not integrated into autogenerated Campaign Name");
        Assert.assertEquals(campaignWizardSummaryData.campaignBudget, "$" + testCampaignBudget, "Campaign budget was not the expected value in campaign summary");
        Assert.assertEquals(campaignWizardSummaryData.brand, segmentationFilterSelections.get(1), "Brand Segmentation value was not the expected value in campaign summary");
        Assert.assertEquals(campaignWizardSummaryData.claim, segmentationFilterSelections.get(0), "Claim Segmentation value was not the expected value in campaign summary");
        Assert.assertEquals(campaignWizardSummaryData.ioSegment, segmentationFilterSelections.get(2), "IO Segmentation value was not the expected value in campaign summary");
        campaignWizardModal.closeLaunchCampaignModal();

        campaignWizardModal.clickCampaignWizardStep("Segmentation");
        List<String> updatedSegmentationFilterSelections = new ArrayList<>();

        for (String segmentationFilter : segmentationFilters) {
            String valueToSelect = campaignWizardModal.getAllValuesInSegmentationDropdownMenu(segmentationFilter).get(2);
            updatedSegmentationFilterSelections.add(valueToSelect);
            campaignWizardModal.openSegmentationDropdownAndSelectValue(segmentationFilter, valueToSelect);
        }
        campaignWizardModal.clickCampaignWizardStep("Targeting");
        testCampaignBudget = "22";
        campaignWizardModal.setCampaignBudget(testCampaignBudget);
        campaignWizardModal.clickLaunchCampaignButton();
        campaignWizardSummaryData = campaignWizardModal.getCampaignWizardSelectedSettings();
        Assert.assertEquals(campaignWizardSummaryData.campaignBudget, "$" + testCampaignBudget, "Campaign budget was not the expected value in campaign summary after making changes");
        Assert.assertEquals(campaignWizardSummaryData.brand, updatedSegmentationFilterSelections.get(1), "Brand Segmentation value was not the expected value in campaign summary after making changes");
        Assert.assertEquals(campaignWizardSummaryData.claim, updatedSegmentationFilterSelections.get(0), "Claim Segmentation value was not the expected value in campaign summary after making changes");
        Assert.assertEquals(campaignWizardSummaryData.ioSegment, updatedSegmentationFilterSelections.get(2), "IO Segmentation value was not the expected value in campaign summary after making changes");
    }

    @Test(priority = 5, description = "C244750 - Verify that left-hand FIDO Bidding Status filter was added to Show Me: Campaigns and has the following options: Eligible, Enabled, Ineligible")
    public void MDM_ShowMeCampaigns_VerifyFidoBiddingStatusFilterIsAdded() throws InterruptedException {
        showMeCampaigns.selectBU("Performance Health East");
        Assert.assertTrue(showMeCampaigns.isFidoBiddingStatusFilterVisible(), "FIDO Bidding Status filter was not added to Show Me Campaigns screen");
        List<String> expectedFidoBiddingStatusOptions = List.of("Eligible", "Enabled", "Ineligible");
        List<String> actualFidoBiddingStatusOptions = showMeCampaigns.getAllFidoBiddingStatusOptions();
        Assert.assertEquals(actualFidoBiddingStatusOptions, expectedFidoBiddingStatusOptions, "FIDO Bidding Status filter options were not displayed as expected");
    }

    @Test(priority = 6, description = "C244748 - Show the blue dots to enable FIDO within Flight Deck Show Me: Campaigns if the campaign is deemed eligible")
    public void MDM_ShowMeCampaigns_VerifyFidoBiddingStatusBlueDotIsDisplayed() throws InterruptedException, SQLException {
        showMeCampaigns.selectBU("Performance Health East");

        showMeCampaigns.selectFidoBiddingStatusFilterOption("Eligible");
        int numberOfExpectedCampaignNames = showMeCampaigns.getCampaignNameColumnValues().size();
        Assert.assertEquals(numberOfExpectedCampaignNames, 0, "More than 0 campaigns were displayed after selecting Eligible option from FIDO Bidding Status filter");

        int randomIndex = new Random().nextInt(NetNewConstants.someCampaignIds.size());
        int campaignToSelect = NetNewConstants.someCampaignIds.get(randomIndex);
        Map<String, String> itemAndCampaignName = SharedDBMethods.getItemToSelect(113, campaignToSelect);
        campaignThatWasUpdated = itemAndCampaignName.get("CAMPAIGN_NAME");
        itemToCheck = itemAndCampaignName.get("itemApiUnitId");
        mostRecentDateFromDB = SharedDBMethods.getLastDateFromDB(itemToCheck);
        SharedDBMethods.updateDownloadDate(itemToCheck, mostRecentDateFromDB);

        showMeCampaigns.refreshPage();
        showMeCampaigns.selectFidoBiddingStatusFilterOption("Eligible");
        List<String> campaignNamesEligible = showMeCampaigns.getCampaignNameColumnValues();
        Assert.assertEquals(campaignNamesEligible.size(), 1, "More than 1 campaign was displayed after selecting Eligible option from FIDO Bidding Status filter");
        Assert.assertTrue(campaignNamesEligible.contains(campaignThatWasUpdated), "Campaign " + campaignThatWasUpdated + " was not displayed after selecting Eligible option from FIDO Bidding Status filter");
        Assert.assertTrue(showMeCampaigns.isFidoBiddingStatusBlueDotDisplayed(campaignThatWasUpdated), "Blue dot was not displayed for campaign " + campaignThatWasUpdated);
    }

    @Test(priority = 7, description = "C244751/1 - Confirm blue circle icon, tooltip and enabling FIDO bidding thru clicking on the blue circle icon")
    public void MDM_ShowMeCampaigns_VerifyFidoBiddingStatusBlueDotTooltipAndEnablingFidoBidding() throws Exception {
        showMeCampaigns.selectBU("Performance Health East");

        showMeCampaigns.selectFidoBiddingStatusFilterOption("Eligible");
        changeFidoBidding(campaignThatWasUpdated, "Campaign Eligible for FIDO Bidding. Click to enable FIDO", "FIDO Bidding enabled", true, "blueDot");
        showMeCampaigns.clickApplyButton();
        changeFidoBidding(campaignThatWasUpdated, "FIDO-Enabled Campaign. Click to disable FIDO", "FIDO Bidding disabled", false, "blueDot");
    }

    @Test(priority = 8, description = "C244751/2 - Confirm blue circle icon, tooltip and enabling FIDO bidding thru Edit button")
    public void MDM_ShowMeCampaigns_VerifyFidoBiddingStatusBlueDotTooltipAndEnablingFidoBiddingThroughEditButton() throws Exception {
        showMeCampaigns.selectBU("Performance Health East");

        showMeCampaigns.selectFidoBiddingStatusFilterOption("Eligible");
        changeFidoBidding(campaignThatWasUpdated, "Campaign Eligible for FIDO Bidding. Click to enable FIDO", "FIDO Bidding enabled", true, "editButton");
        showMeCampaigns.clickApplyButton();
        changeFidoBidding(campaignThatWasUpdated, "FIDO-Enabled Campaign. Click to disable FIDO", "FIDO Bidding disabled", false, "editButton");

        SharedDBMethods.resetCampaignToOriginalValues(itemToCheck, mostRecentDateFromDB);
        showMeCampaigns.refreshPage();
        showMeCampaigns.selectFidoBiddingStatusFilterOption("Eligible");
        Assert.assertEquals(showMeCampaigns.getCampaignNameColumnValues().size(), 0, "Campaign budget was not reset to original value after test");
    }

    @Test(description = "EMP-595 - Campaign wizard Search functionality as expected")
    public void MDM_ShowMeCampaigns_VerifyCampaignWizardSearchFunctionality() throws InterruptedException {
        showMeCampaigns.selectBU("Hersheys - US");
        Assert.assertTrue(showMeCampaigns.isCreateCampaignsButtonVisible(), "Create Campaigns button is not visible on Show Me Campaigns screen");
        List<String> expectedCampaignSteps = List.of("Campaign Type", "Products", "Segmentation", "Targeting");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(campaignWizardModal = showMeCampaigns.clickCreateCampaignButton(), "Campaign Wizard modal was not opened after clicking create campaign button");
        campaignWizardModal.areCampaignWizardStepsVisible();
        List<String> actualCampaignSteps = campaignWizardModal.getCampaignWizardSteps();
        softAssert.assertEquals(actualCampaignSteps, expectedCampaignSteps, "Campaign Wizard steps are not displayed as expected");

        campaignWizardModal.clickCampaignWizardStep("Campaign Type");
        Assert.assertTrue(campaignWizardModal.isCampaignTypeSelectCardVisible(), "Campaign Type sponsored products card was not visible on Campaign Wizard modal");
        campaignWizardModal.clickSponsoredProductsCheckbox();
        Assert.assertTrue(campaignWizardModal.isSponsoredProductsCheckboxSelected(), "Sponsored products card was not selected by default on Campaign Wizard modal");

        campaignWizardModal.clickCampaignWizardStep("Products");
        Assert.assertTrue(campaignWizardModal.isProductsSelectorCardVisible(), "Product Selector card was not visible on Campaign Wizard modal");
        String asinToTest = "B01GEIMR2K";
        campaignWizardModal.searchProductSelectorByASIN(asinToTest);
        List<String> asinsDisplayedInTable = campaignWizardModal.getAllAsinsInTable();
        for (String asin : asinsDisplayedInTable) {
            Assert.assertTrue(asin.contains(asinToTest));
        }
        LOGGER.info("Verify Product Search field");
        campaignWizardModal.clickCrossIcon();
    }

    @Test(description = "EMP-595 - Campaign wizard Search functionality as expected")
    public void MDM_ShowMeCampaigns_VerifyCampaignWizardSearchFieldByRandomCharacters() throws InterruptedException {
        showMeCampaigns.selectBU("Hersheys - US");
        Assert.assertTrue(showMeCampaigns.isCreateCampaignsButtonVisible(), "Create Campaigns button is not visible on Show Me Campaigns screen");
        List<String> expectedCampaignSteps = List.of("Campaign Type", "Products", "Segmentation", "Targeting");

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(campaignWizardModal = showMeCampaigns.clickCreateCampaignButton(), "Campaign Wizard modal was not opened after clicking create campaign button");
        campaignWizardModal.areCampaignWizardStepsVisible();
        List<String> actualCampaignSteps = campaignWizardModal.getCampaignWizardSteps();
        softAssert.assertEquals(actualCampaignSteps, expectedCampaignSteps, "Campaign Wizard steps are not displayed as expected");

        campaignWizardModal.clickCampaignWizardStep("Campaign Type");
        Assert.assertTrue(campaignWizardModal.isCampaignTypeSelectCardVisible(), "Campaign Type sponsored products card was not visible on Campaign Wizard modal");
        campaignWizardModal.clickSponsoredProductsCheckbox();
        Assert.assertTrue(campaignWizardModal.isSponsoredProductsCheckboxSelected(), "Sponsored products card was not selected by default on Campaign Wizard modal");

        campaignWizardModal.clickCampaignWizardStep("Products");
        Assert.assertTrue(campaignWizardModal.isProductsSelectorCardVisible(), "Product Selector card was not visible on Campaign Wizard modal");
        LOGGER.info("Verify Random Character Search");
        String asinRandomChracter = "HGFDSA";
        campaignWizardModal.searchProductSelectorByASIN(asinRandomChracter);
        Assert.assertTrue(campaignWizardModal.verifyRandomCharacterSearched(),"Search field accepted the random character");

    }

    public void verifyKeywordCanBeAddedToCampaign(String campaign, String bid, Enums.KeywordMatchType matchType, String keyword) throws InterruptedException {
        campaignWizardModal.addKeywordToCampaign(campaign, bid, matchType, keyword);
        Assert.assertTrue(campaignWizardModal.isKeywordAddedSuccessMessageDisplayed(), "Keyword was added successful message was not displayed after adding keyword to " + campaign + "Keywords");
        List<CampaignWizardKeywordTableData> keywordTableData = campaignWizardModal.getAllKeywordDataFromTable(campaign);
        Assert.assertEquals(keywordTableData.get(0).getKeyword(), keyword, "Keyword was not displayed as expected in " + campaign + " table after being added");
        Assert.assertEquals(keywordTableData.get(0).getBid(), "$" + bid, "Bid was not displayed as expected in " + campaign + " table after being added");
        Assert.assertEquals(keywordTableData.get(0).getMatchType(), matchType.toString(), "Match type was not displayed as expected in " + campaign + " table after being added");
    }

    public void verifyManualTargetingTypeSectionsAreNotVisibleIfAutoIsSelected() {
        Assert.assertFalse(campaignWizardModal.isBrandTargetingSectionVisible(), "Brand Targeting section was visible after selecting Auto targeting type");
        Assert.assertFalse(campaignWizardModal.isNonBrandedTargetingSectionVisible(), "Non-Branded Targeting section was visible after selecting Auto targeting type");
        Assert.assertFalse(campaignWizardModal.isCompetitorTargetingSectionVisible(), "Competitor Targeting section was visible after selecting Auto targeting type");
    }

    public void verifyManualTargetingTypeSectionsAreVisibleIfManualIsSelected() {
        Assert.assertTrue(campaignWizardModal.isBrandTargetingSectionVisible(), "Brand Targeting section was not visible after selecting Manual targeting type");
        Assert.assertTrue(campaignWizardModal.isNonBrandedTargetingSectionVisible(), "Non-Branded Targeting section was not visible after selecting Manual targeting type");
        Assert.assertTrue(campaignWizardModal.isCompetitorTargetingSectionVisible(), "Competitor Targeting section was not visible after selecting Manual targeting type");
    }

    private void changeFidoBidding(String campaignName, String expectedTooltip, String expectedAlertText, boolean shouldBeBidLocked, String wayOfEditing) throws Exception {
        if (showMeCampaigns.isFidoBiddingStatusBlueDotDisplayed(campaignName)) {
            String originalBlueDotIcon = showMeCampaigns.getBlueDotImage(campaignName);
            Assert.assertEquals(showMeCampaigns.getToolTipText(campaignName), expectedTooltip, "Tooltip text was not displayed as expected for campaign " + campaignName);
            switch (wayOfEditing) {
                case "editButton":
                    showMeCampaigns.clickCheckBoxForSpecificRow(1);
                    showMeCampaigns.clickEditButton();
                    switch (expectedTooltip) {
                        case "Campaign Eligible for FIDO Bidding. Click to enable FIDO":
                            showMeCampaigns.selectEnableFIDOFromEditDropdown();
                            break;
                        case "FIDO-Enabled Campaign. Click to disable FIDO":
                            showMeCampaigns.selectDisableFIDOFromEditDropdown();
                            break;
                    }
                    break;
                case "blueDot":
                    showMeCampaigns.clickFidoBiddingStatusBlueDot(campaignName);
                    break;
            }
            Assert.assertEquals(showMeCampaigns.getAlertText(), expectedAlertText, "Enabled message was not displayed as expected after enabling campaign by clicking on blue dot");
            String enabledBlueDotIcon = showMeCampaigns.getBlueDotImage(campaignName);
            Assert.assertNotEquals(originalBlueDotIcon, enabledBlueDotIcon, "Blue dot icon was not updated after clicking on it");
            Assert.assertEquals(wasBidLocked(campaignName), shouldBeBidLocked, "Bid was not locked for campaign " + campaignName);
        } else {
            Assert.fail("Blue dot was not displayed for campaign " + campaignName);
        }
    }

    public boolean wasBidLocked(String campaignName) throws Exception {
        try {
            flightDeck.selectShowMeOption(Enums.FlightDeckShowMe.KEYWORDS);
        } catch (TimeoutException e) {
            throw new TimeoutException(e);
        }
        flightDeck.selectCampaignName(campaignName);
        boolean isBidLocked = flightDeck.isBidLockedVisible();
        flightDeck.selectShowMeOption(Enums.FlightDeckShowMe.CAMPAIGNS);
        return isBidLocked;
    }
}
