package com.DC.uitests.adc.execute.mediaManagement.flightDeck;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.mediaManagement.flightDeck.FlightDeck;
import com.DC.pageobjects.adc.execute.mediaManagement.flightDeck.ShowMeAdGroupsByCampaignPage;
import com.DC.pageobjects.adc.navigationMenus.NetNewNavigationMenu;
import com.DC.testcases.BaseClass;
import com.DC.utilities.enums.Enums;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ShowMeAdGroupTests extends BaseClass {
    private static final String USER_NAME = READ_CONFIG.getUsername();
    private static final String PASSWORD = READ_CONFIG.getPassword();
    private static final String LOGIN_ENDPOINT = READ_CONFIG.getDcAppFilaLegacyUrl();
    private static final String FLIGHT_DECK_URL = LOGIN_ENDPOINT + "/advertising/flightdeck/AMAZON";
    private static final int DIFFERENT_STATES_NUMBER = 3;
    private FlightDeck flightDeck;
    private ShowMeAdGroupsByCampaignPage showMeAdGroupsByCampaignPage;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USER_NAME, PASSWORD);
        NetNewNavigationMenu netNewNavigationMenu = new NetNewNavigationMenu(driver);
        netNewNavigationMenu.selectBU("McCormick");
        Thread.sleep(1000);
        driver.get(FLIGHT_DECK_URL);
        Thread.sleep(2000);
        flightDeck = new FlightDeck(driver);
        flightDeck.selectPlatform("DoorDash");
        showMeAdGroupsByCampaignPage = (ShowMeAdGroupsByCampaignPage) flightDeck.selectShowMeOption(Enums.FlightDeckShowMe.AD_GROUPS_BY_CAMPAIGN);
        flightDeck.clickApplyButton();
        flightDeck.collapseFilter();

    }

    @AfterClass
    public void killDriver() {
        quitBrowser();
    }

    @Test(priority = 1, description = "Verify that IntraDay Multiplier is working properly for DoorDash Platform")
    public void MDR_FlightDeck_DoorDashPlatform_VerifyDaypartingAssignments() throws InterruptedException {
        List<String> adGroupNameColumnValues = showMeAdGroupsByCampaignPage.loadAdGroupNameColumnValues();
        List<String> stateColumnValues = showMeAdGroupsByCampaignPage.loadStateColumnValues();
        var filteredColumnValues = selectAdGroupNamesWithDifferentStates(adGroupNameColumnValues, stateColumnValues);
        updateStateColumnIfNecessary(filteredColumnValues, "paused");
        updateStateColumnIfNecessary(filteredColumnValues, "active");

        for (int i = 0; i < filteredColumnValues.size(); i++) {
            performTest(adGroupNameColumnValues.get(i));
        }
    }

    @Test(priority = 2, description = "Verify that Multiselect from Campaign Table is working with  Bulk IntraDay Multiplier")
    public void MDR_FlightDeck_DoorDashMultiSelectIntraDayMultiplierEdit() throws InterruptedException {
        List<String> adGroupNameColumnValues = showMeAdGroupsByCampaignPage.loadAdGroupNameColumnValues();
        List<String> stateColumnValues = showMeAdGroupsByCampaignPage.loadStateColumnValues();
        var filteredColumnValues = selectAdGroupNamesWithDifferentStates(adGroupNameColumnValues, stateColumnValues);
        filteredColumnValues.remove("ended");
        updateStateColumnIfNecessary(filteredColumnValues, "paused");
        updateStateColumnIfNecessary(filteredColumnValues, "active");

        var adGroupColumnValues = new ArrayList<>(filteredColumnValues.values());
        showMeAdGroupsByCampaignPage.selectCheckBoxElementByAdGroupName(adGroupColumnValues.get(0));
        showMeAdGroupsByCampaignPage.selectCheckBoxElementByAdGroupName(adGroupColumnValues.get(1));
        var intraDayMultiplayerValue = "Test 1";
        showMeAdGroupsByCampaignPage.openEditDropdown();
        showMeAdGroupsByCampaignPage.selectValueFromIntraDayMultiplierDropdown("Adjust Dayparting Configurations");
        showMeAdGroupsByCampaignPage.selectValueFromBulkIntradayMultiplier(intraDayMultiplayerValue);
        showMeAdGroupsByCampaignPage.clickIntraDayMultiplierApplyButton();
        var intraDayElement1 = showMeAdGroupsByCampaignPage.loadIntraDayElementByAdGroupName(adGroupColumnValues.get(0));
        Assert.assertEquals(intraDayElement1.getText(), intraDayMultiplayerValue, "IntraDay Multiplier value is not as expected");
        var intraDayElement2 = showMeAdGroupsByCampaignPage.loadIntraDayElementByAdGroupName(adGroupColumnValues.get(1));
        Assert.assertEquals(intraDayElement2.getText(), intraDayMultiplayerValue, "IntraDay Multiplier value is not as expected");
    }

    @Test(priority = 3, description = "Verify that IntraDay Multiplier search filter is working properly for DoorDash platform")
    public void MDR_FlightDeck_DoorDash_Adjust_IntraDayMultiplierFilter() throws InterruptedException {
        var intraDayMultiplayerValue = "Test 1";
        showMeAdGroupsByCampaignPage.selectIntraDayMultiplierFilterOption(intraDayMultiplayerValue);
        List<String> intraDayMultiplierColumnValues = showMeAdGroupsByCampaignPage.loadIntraDayMultiplierColumnValues();
        intraDayMultiplierColumnValues.stream().filter(value -> !value.equals(intraDayMultiplayerValue))
                .forEach(value -> Assert.fail("IntraDay Multiplier value is not as expected"));
    }

    private void performTest(String adGroupColumnValue) throws InterruptedException {
        var stateElement = showMeAdGroupsByCampaignPage.loadStateElementByAdGroupName(adGroupColumnValue);
        var oldStateValue = stateElement.getText();
        var newStateValue = getNewStateValue(oldStateValue);
        if (!newStateValue.equals("ended")) {
            showMeAdGroupsByCampaignPage.selectOptionForStateElement(stateElement, newStateValue);
        }
        var intraDayElement = showMeAdGroupsByCampaignPage.loadIntraDayElementByAdGroupName(adGroupColumnValue);
        var expectedIntraDayValue = getExpectedIntraDayValue(newStateValue, intraDayElement.getText());
        Assert.assertEquals(intraDayElement.getText().toLowerCase(), expectedIntraDayValue, "IntraDay Multiplier value is not as expected");
        var expectedIntraDayMessagePattern = getExpectedIntraDayMessagePattern(newStateValue);
        showMeAdGroupsByCampaignPage.selectOptionForIntraDayElement(intraDayElement, "Test 1");
        var pattern = Pattern.compile(expectedIntraDayMessagePattern);
        var matcher = pattern.matcher(showMeAdGroupsByCampaignPage.getIntraDayPopUpMessage(expectedIntraDayMessagePattern));
        Assert.assertTrue(matcher.find(), "IntraDay Multiplier message is not as expected");
    }

    private String getNewStateValue(String oldStateValue) {
        if (oldStateValue.equalsIgnoreCase("ended")) {
            return "ended";
        }
        return oldStateValue.equalsIgnoreCase("active") ? "paused" : "active";
    }

    private String getExpectedIntraDayValue(String newStateValue, String oldIntraDayValue) {
        if (newStateValue.equals("active")) {
            return oldIntraDayValue.toLowerCase();
        }
        return "off";
    }

    private String getExpectedIntraDayMessagePattern(String stateValue) {
        if (stateValue.equals("ended")) {
            return "Cannot update Dayparting for 1 Ad Group\\(s\\) because status is not one of \\((paused,active|active,paused)\\)";
        }
        return "Record\\(s\\) have been updated\\.";
    }

    private Map<String, String> selectAdGroupNamesWithDifferentStates(List<String> adGroupNames, List<String> states) {
        Map<String, String> selectedElements = new HashMap<>();
        int endedStateIndex = 0;
        for (int i = 0; i < states.size(); i++) {
            String state = states.get(i).toLowerCase().trim();

            if (!selectedElements.containsKey(state)) {
                selectedElements.put(state, adGroupNames.get(i));
                if (state.equalsIgnoreCase("ended")) {
                    endedStateIndex = i;
                }
            }

            if (selectedElements.size() == DIFFERENT_STATES_NUMBER) {
                break;
            }
        }
        // Change states if necessary
        if (selectedElements.size() < DIFFERENT_STATES_NUMBER) {
            for (int i = endedStateIndex + 1; i < states.size(); i++) {
                String state = states.get(i).toLowerCase().trim();
                if (state.equalsIgnoreCase("ended")) {
                    if (!selectedElements.containsKey("paused")) {
                        selectedElements.put("paused", adGroupNames.get(i));
                        states.set(i, "paused");
                    } else if (!selectedElements.containsKey("active")) {
                        selectedElements.put("active", adGroupNames.get(i));
                        states.set(i, "active");
                    }
                }

                if (selectedElements.size() == DIFFERENT_STATES_NUMBER) {
                    break;
                }
            }
        }
        return selectedElements;
    }

    private void updateStateColumnIfNecessary(Map<String, String> filteredColumnValues, String state) throws InterruptedException {
        // we need to ensure that we have at least one of each state
        var stateElement = showMeAdGroupsByCampaignPage.loadStateElementByAdGroupName(filteredColumnValues.get(state));
        if (stateElement.getText().equalsIgnoreCase(state)) {
            showMeAdGroupsByCampaignPage.selectOptionForStateElement(stateElement, state);
        }
    }
}