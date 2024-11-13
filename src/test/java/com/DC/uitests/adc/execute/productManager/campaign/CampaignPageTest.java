package com.DC.uitests.adc.execute.productManager.campaign;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.DC.utilities.DateUtility;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.DC.constants.InsightsConstants;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.productManager.campaigns.CampaignsPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company.CompanyCampaign;

public class CampaignPageTest extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String CAMPAIGN_TO_TEST = "campaign to delete";
    private String jwt;
    CampaignsPage campaignsPage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_CAMPAIGNS_URL);
        campaignsPage = new CampaignsPage(driver);
        jwt = SecurityAPI.getJwtForInsightsUser(driver);
        CompanyApiService.getCompany(jwt);
    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify campaigns Details using backend Api's")
    public void PPV_CampaignsPage_CampaignsDetails() throws Exception {
        Company company = CompanyApiService.getCompany(jwt);
        List<CompanyCampaign> campaignsList = company.campaigns;
        List<String> campaignNames = new ArrayList<>();
        List<String> campaignsDateRange = new ArrayList<>();
        for (CompanyCampaign companyCampaign : campaignsList) {
            campaignNames.add(companyCampaign.name);

            if (companyCampaign.startDate == null || companyCampaign.endDate == null) {
                campaignsDateRange.add("");
                continue;
            }

            String startDate = companyCampaign.startDate.toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDate().toString();
            String endDate = companyCampaign.endDate.toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDate().toString();
            campaignsDateRange.add(DateUtility.formattingDate(startDate) + " - " + DateUtility.formattingDate(endDate));
        }
        Assert.assertEquals(campaignNames, campaignsPage.getCampaignsName(), "campaigns names are not matching");
        Assert.assertEquals(campaignsPage.getCampaignDateRange(), campaignsDateRange, "campaigns date ranges not matching");
    }

    @Test(priority = 2, description = "Verify column Header's Names")
    public void PPV_CampaignsPage_CampaignsPageColumns() {
        Assert.assertTrue(campaignsPage.isCampaignColumnAvailable(), "Campaigns campaign column Header is not available");
        Assert.assertTrue(campaignsPage.isDateRangeColumnAvailable(), "Campaigns Date Range column Header is not available");
    }

    @Test(priority = 3, description = "Verify Search for Campaigns")
    public void PPV_CampaignsPage_CanSearchCampaigns() throws InterruptedException {
        Assert.assertTrue(campaignsPage.checkCancelButtonOnAddCampaignPage("abc"), "cancel button not found");
        Assert.assertFalse(campaignsPage.doesCampaignExist("Aliba"), "campaign name exist");
        Assert.assertTrue(campaignsPage.doesCampaignExist("Halloween"), "campaign name does not exist");
    }

    @Test(priority = 4, description = "Verify Campaigns")
    public void PPV_CampaignsPage_CanAddCampaign() throws InterruptedException {
        Assert.assertFalse(campaignsPage.areAddAndCloseButtonEnabled(), "Add and close button enabled");
        Assert.assertFalse(campaignsPage.isSetADateRangeSelected(), "Set a Date Range checkbox selected");
        Assert.assertTrue(campaignsPage.isSelectDateButtonDisplayed(), "Select Date button not displayed");
        campaignsPage.enterCampaignNameAddAndClose(CAMPAIGN_TO_TEST);
        Assert.assertTrue(campaignsPage.isNoteDisplayed(Enums.NoteType.SUCCESS), "Success note not displayed after adding campaign");
        campaignsPage.closeNoteIfDisplayed(Enums.NoteType.SUCCESS);
        Assert.assertTrue(campaignsPage.isDuplicateCampaignMessageDisplayed(CAMPAIGN_TO_TEST), "message not displayed");
    }

    @Test(priority = 5, description = "Verify Campaign deletion")
    public void PPV_CampaignsPage_CanDeleteCampaigns() throws InterruptedException {
        Assert.assertTrue(campaignsPage.deleteCampaign(CAMPAIGN_TO_TEST, false), "Campaign was deleted");
        Assert.assertFalse(campaignsPage.deleteCampaign(CAMPAIGN_TO_TEST, true), "Campaign was not deleted");
    }
}
