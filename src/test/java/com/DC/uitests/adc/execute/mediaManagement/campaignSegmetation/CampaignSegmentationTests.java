package com.DC.uitests.adc.execute.mediaManagement.campaignSegmetation;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.mediaManagement.campaignSegmentation.CampaignSegmentationManagerPage;
import com.DC.pageobjects.adc.execute.mediaManagement.campaignSegmentation.CampaignSegmentationPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.DC.utilities.SharedMethods.createList;

public class CampaignSegmentationTests extends BaseClass {

    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppFilaLegacyUrl();
    private static final String ADVERTISING_CAMPAIGN_SEGMENTATION_AMAZON_PATH = "/advertising/campaign-segmentation/AMAZON";
    private static final String CAMPAIGN_SEGMENTATION_URL = LOGIN_ENDPOINT + ADVERTISING_CAMPAIGN_SEGMENTATION_AMAZON_PATH;
    private CampaignSegmentationPage campaignSegmentationPage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("Salomon");
        driver.get(CAMPAIGN_SEGMENTATION_URL);
        campaignSegmentationPage = new CampaignSegmentationPage(driver);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @BeforeMethod
    public void setupMethod() {
        if (!driver.getTitle().contains("Campaign Segmentation - Flywheel")) {
            driver.navigate().to(CAMPAIGN_SEGMENTATION_URL);
        }
    }

    @Test(priority = 1, description = "Verify the Campaign Segmentation Page and Filter are displayed")
    public void CampaignSegmentation_VerificationOfCampaignSegmentationScreenAndFilterDisplayed() {
        Assert.assertTrue(campaignSegmentationPage.isCampaignSegmentationScreenDisplayed(), "Campaign Segmentation screen was not displayed");
        Assert.assertTrue(campaignSegmentationPage.isFilterFormDisplayed(), "Required filter form was not displayed");
        Assert.assertTrue(campaignSegmentationPage.isRequiredFilterDisplayed(), "Required filter was not displayed");
        Assert.assertTrue(campaignSegmentationPage.isRequiredFilterValueDisplayed(), "Required filter value was not displayed");
    }

    @Test(priority = 2, description = "Verify that Campaign Manager Segmentation Page and MPVTab is Displayed")
    public void CampaignSegmentationManager_MPVTabDisplayed() throws InterruptedException {
        String filterName = "TestFilterName";
        CampaignSegmentationManagerPage campaignSegmentationManagerPage = campaignSegmentationPage.clickSegmentationManagerButton();
        Assert.assertTrue(campaignSegmentationManagerPage.isCampaignSegmentationManagerScreenDisplayed(), "Manager page was not displayed");
        campaignSegmentationManagerPage.clickMultiPlatformViewMappingsTab();
        campaignSegmentationManagerPage.createQuickMultiPlatformViewFilter(filterName, "Category");
        campaignSegmentationManagerPage.refreshPage();
        campaignSegmentationManagerPage.clickMultiPlatformViewMappingsTab();
        Assert.assertTrue(campaignSegmentationManagerPage.isFilterPresent(filterName), "Filter could not be created");

        driver.get(CAMPAIGN_SEGMENTATION_URL);
        campaignSegmentationManagerPage = campaignSegmentationPage.clickSegmentationManagerButton();
        campaignSegmentationManagerPage.clickMultiPlatformViewMappingsTab();
        campaignSegmentationManagerPage.openEditModal(filterName);
        String platformName = "AMAZON";
        Assert.assertEquals(campaignSegmentationManagerPage.getCurrentValuesInModal(List.of(platformName)), createList("Category"), "Current values for selected platforms do not prepopulate " + campaignSegmentationManagerPage.getCurrentValuesInModal(List.of(platformName)) + " : " + List.of("Category"));

        String filterNameForUpdate = "TestFilterNameUpdated";
        String newFilterType = "Targeting Type";
        campaignSegmentationManagerPage.quickUpdateMultiPlatformViewFilter(filterNameForUpdate, newFilterType);
        Assert.assertTrue(campaignSegmentationManagerPage.isFilterPresent(filterNameForUpdate), "Filter could not be updated");
        Assert.assertTrue(campaignSegmentationManagerPage.getPlatformNamesOnFilter(filterNameForUpdate).contains(platformName), "Platform does not contain expected value after update " + createList(platformName));
        Assert.assertTrue(campaignSegmentationManagerPage.getPlatformValuesOnFilter(filterNameForUpdate).contains(newFilterType), "Platform does not contain expected value after update " + createList(newFilterType));

        campaignSegmentationManagerPage.updateInLineFilterType(filterNameForUpdate, "amazon", "ASIN");
        Assert.assertEquals(campaignSegmentationManagerPage.getFilterType(filterNameForUpdate, "amazon"), "ASIN", "Inline filter not updated");
        campaignSegmentationManagerPage.refreshPage();
        campaignSegmentationManagerPage.clickMultiPlatformViewMappingsTab();
        campaignSegmentationManagerPage.deleteFilterIfExists(filterNameForUpdate);
        Assert.assertTrue(campaignSegmentationManagerPage.isFilterNotPresent(filterNameForUpdate), "Filter could not be deleted");
    }

    @Test(priority = 3, description = "Verify that 'Sponsored TV' option in the Campaign Type filter for the Amazon Platform is added.")
    public void CampaignSegmentation_SponsoredTV() throws InterruptedException {
        String filterName = "Sponsored TV";
        campaignSegmentationPage.applyCampaignTypeFilterByValue(filterName);
        Assert.assertTrue(campaignSegmentationPage.isCampaignColumnContainsValue(filterName), String.format("%s is not displayed in Campaign Type", filterName));
    }
}