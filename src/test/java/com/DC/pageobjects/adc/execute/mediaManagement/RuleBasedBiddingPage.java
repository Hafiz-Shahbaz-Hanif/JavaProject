package com.DC.pageobjects.adc.execute.mediaManagement;

import com.DC.db.execute.MediaManagementQueries;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.utilities.SQLUtility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RuleBasedBiddingPage extends NetNewNavigationMenu {

    public DCFilters dcFilters;
    private static final By RBB_HEADER = By.xpath("//span[text()='Rule Based Bidding']");
    private static final By CREATE_RBB_BUTTON = By.xpath("//span[text()='Create']");
    private static final By CREATE_CONFIG_POPUP = By.xpath("//h5[text()='Create Rule-Based Bidding Configurations']");
    private static final By ELIGIBLE_CAMPAIGNS_FIELD = By.xpath("//label[contains(text(),'Eligible Campaigns')]");
    private static final By GOAL_TYPE_FIELD = By.xpath("//label[contains(text(),'Goal Type')]");
    private static final By GOAL_VALUE_FIELD = By.xpath("//label[contains(text(),'Goal value')]");
    private static final By CONSTRAINT_TYPE_FIELD = By.xpath("//label[contains(text(),'Constraint Type')]");
    private static final By CONSTRAINT_VALUE_FIELD = By.xpath("//label[contains(text(),'Constraint value')]");
    private static final By SAVE_BUTTON = By.xpath("//div[@class='modal-footer']/button[contains(text(),'Save')]");
    private static final By CANCEL_BUTTON = By.xpath("//div[@class='modal-footer']/button[contains(text(),'Cancel')]");

    public RuleBasedBiddingPage(WebDriver driver) {
        super(driver);
        findElementVisible(RBB_HEADER);
        dcFilters = new DCFilters(driver);
    }

    public boolean isRuleBasedBiddingScreenDisplayed() {
        return isElementVisible(RBB_HEADER);
    }

    public boolean isCreateRulePopupBoxDisplayed() throws InterruptedException {
        click(CREATE_RBB_BUTTON);
        return isElementVisible(CREATE_CONFIG_POPUP);
    }

    public boolean isAllElementsDisplayed() {
        return isElementVisible(ELIGIBLE_CAMPAIGNS_FIELD) &&
                isElementVisible(GOAL_TYPE_FIELD) &&
                isElementVisible(GOAL_VALUE_FIELD) &&
                isElementVisible(CONSTRAINT_TYPE_FIELD) &&
                isElementVisible(CONSTRAINT_VALUE_FIELD) &&
                isElementVisible(SAVE_BUTTON) &&
                isElementVisible(CANCEL_BUTTON);
    }

    public List<String> getEligibleCampaignsForBU() throws InterruptedException {
        List<String> listOfCampaignsInDropdown = new ArrayList<>();
        click(CREATE_RBB_BUTTON);
        By campaignNameLocator = By.xpath("//div[@class='selectize-dropdown multi plugin-dropdown_direction plugin-remove_button direction-down']//div[contains(@class,'option')]");
        List<WebElement> campaignNames = findElementsVisible(campaignNameLocator);
        for (WebElement campaignName : campaignNames) {
            String campaignNameText = campaignName.getText();
            listOfCampaignsInDropdown.add(campaignNameText);
        }
        UI_LOGGER.info("Number of Campaigns in Dropdown: " + listOfCampaignsInDropdown.size());
        return listOfCampaignsInDropdown;
    }

    public List<String> getEligibleCampaignsFromDB(int businessUnitId, String campaignStatus) throws SQLException {
        List<String> listOfCampaignsFromDB = new ArrayList<>();
        SQLUtility.connectToServer();
        ResultSet rs = SQLUtility.executeQuery(MediaManagementQueries.getEligibleCampaigns(businessUnitId, campaignStatus));
        while (rs.next()) {
            listOfCampaignsFromDB.add(rs.getString("campaignName"));
        }
        SQLUtility.closeConnections();
        UI_LOGGER.info("Number of Campaigns in DB: " + listOfCampaignsFromDB.size());
        return listOfCampaignsFromDB;
    }

    public boolean compareEligibleCampaigns(int businessUnitId, String campaignStatus) throws SQLException, InterruptedException {
        dcFilters.selectStatus(campaignStatus);
        List<String> listOfCampaignsFromDB = getEligibleCampaignsFromDB(businessUnitId, campaignStatus);
        List<String> listOfCampaignsInDropdown = getEligibleCampaignsForBU();
        List<String> extraCampaigns = new ArrayList<>();
        List<String> missingCampaigns = new ArrayList<>();
        for (String campaign : listOfCampaignsFromDB) {
            if (!listOfCampaignsInDropdown.contains(campaign)) {
                extraCampaigns.add(campaign);
            }
        }
        for (String campaign : listOfCampaignsInDropdown) {
            if (!listOfCampaignsFromDB.contains(campaign)) {
                missingCampaigns.add(campaign);
            }
        }
        if (extraCampaigns.isEmpty() && missingCampaigns.isEmpty()) {
            return true;
        } else {
            UI_LOGGER.info("Extra Campaigns: " + extraCampaigns);
            UI_LOGGER.info("Missing Campaigns: " + missingCampaigns);
            return false;
        }
    }
}
