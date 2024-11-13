package com.DC.apitests.adc.advertising.media;

import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.*;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class FlightdeckEnablePauseCampaignsTest extends BaseClass {
    private Map<String, String> headers;
    private String businessUnitId = "1545";
    private String currency = "GBR";
    private static final String BASE_URL = READ_CONFIG.getHubExternalGateway() + "/advertising";
    List<String> singleCampaignId = Collections.singletonList("2909847");
    List<String> multiCampaignIds = List.of("2909847", "2909871", "2909895");
    List<String> allCampaignIds = List.of("2909847", "2909871", "2909895", "2909919", "2909943", "2909967", "2909991", "2910015", "2910039", "2910063", "2910087", "2910111",
            "2910135", "2910159", "2910183", "2910207");
    String endDate = DateUtility.getYesterday();
    String startDate = LocalDate.now().minusDays(7).toString();

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, true);
        driver.get(READ_CONFIG.getDcAppUrl());
        new DCLoginPage(driver).login(READ_CONFIG.getUsername(), READ_CONFIG.getPassword());

        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SecurityAPI.getAuthToken(driver));
        headers.put("X-Businessunitcontext", businessUnitId);
        headers.put("X-Currencycontext", currency);
        headers.put("Content-Type", "application/json");
    }

    public Map<String, String> createAdjustmentOperation() {
        Map<String, String> adjustmentOperation = new HashMap<>();
        adjustmentOperation.put("operationType", "CHANGE_CAMPAIGN_STATE");
        adjustmentOperation.put("operationValue", "enabled");
        adjustmentOperation.put("adjustmentSource", "CAMPAIGNS");
        return adjustmentOperation;
    }

    public Map<String, Object> createFactFilterForAllCampaigns() {
        Map<String, Object> factFilter = new HashMap<>();
        factFilter.put("startDate", startDate);
        factFilter.put("endDate", endDate);
        factFilter.put("showMe", "CAMPAIGNS");
        return factFilter;
    }

    public Map<String, Object> createPaginationAttributesForAllCampaigns() {
        Map<String, Object> paginationAttributes = new HashMap<>();
        paginationAttributes.put("pageSize", 100);
        List<Integer> pageSizeList = Arrays.asList(5, 10, 25, 50, 100);
        paginationAttributes.put("pageSizeList", pageSizeList);
        paginationAttributes.put("totalRecords", 20);
        paginationAttributes.put("page", 1);
        paginationAttributes.put("orderAttribute", "spend");
        paginationAttributes.put("orderAscending", false);
        paginationAttributes.put("orderingAttribute", "_spend");
        paginationAttributes.put("allowSortingApproximation", true);
        return paginationAttributes;
    }

    public void Api_Flightdeck_PutEnabled(List<String> campaignIds) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        Map<String, String> adjustmentOperation = createAdjustmentOperation();
        Map<String, List<String>> filter = new HashMap<>();
        filter.put("dimensionIds", campaignIds);

        payload.put("adjustmentOperation", adjustmentOperation);
        payload.put("filter", filter);

        var response = callEndpoint(BASE_URL + "/adjustment/amazon/campaign", "PUT", new ObjectMapper().writeValueAsString(payload), headers, null);

        Assert.assertEquals(response.getStatusCode(), 200, "Response code was " + response.getStatusCode() + " instead of 200");
    }

    public void Api_Flightdeck_PutEnableAll() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        Map<String, String> adjustmentOperation = createAdjustmentOperation();
        Map<Object, Object> flightDeckFilter = new HashMap<>();
        Map<String, Object> factFilter = createFactFilterForAllCampaigns();
        Map<String, Object> paginationAttributes = createPaginationAttributesForAllCampaigns();

        payload.put("adjustmentOperation", adjustmentOperation);
        payload.put("flightDeckFilter", flightDeckFilter);
        flightDeckFilter.put("factFilter", factFilter);
        flightDeckFilter.put("paginationAttributes", paginationAttributes);

        var response = callEndpoint(BASE_URL + "/adjustment/amazon/campaign/flight-deck", "PUT", new ObjectMapper().writeValueAsString(payload), headers, null);

        Assert.assertEquals(response.getStatusCode(), 200, "Response code was " + response.getStatusCode() + " instead of 200");
    }

    @Test(description = "Flightdeck test: POST Amazon", dataProvider = "allCampaignIds")
    public void Api_Flightdeck_PostAmazon(List<String> campaignIds) throws Exception {
        List<String> payload = new ArrayList<>(campaignIds);

        var response = callEndpoint(BASE_URL + "/alert/AMAZON", "POST", new ObjectMapper().writeValueAsString(payload), headers, null);

        Assert.assertEquals(response.getStatusCode(), 200, "Response code was " + response.getStatusCode() + " instead of 200");
    }

    @Test(description = "Flightdeck test: POST campaigns", dataProvider = "allCampaignIds")
    public void Api_Flightdeck_PostCampaigns(List<String> campaignIds) throws Exception {
        List<String> payload = new ArrayList<>(campaignIds);

        var response = callEndpoint(BASE_URL + "/automated-bidding/CAMPAIGNS", "POST", new ObjectMapper().writeValueAsString(payload), headers, null);

        Assert.assertEquals(response.getStatusCode(), 200, "Response code was " + response.getStatusCode() + " instead of 200");
    }

    @Test(description = "Flightdeck test: POST all", dataProvider = "allCampaignIds")
    public void Api_Flightdeck_PostAll(List<String> campaignIds) throws Exception {
        LOGGER.info("Enabling campaigns..." + campaignIds);
        if(allCampaignIds.equals(singleCampaignId) || allCampaignIds.equals(multiCampaignIds)) {
            Api_Flightdeck_PutEnabled(campaignIds);
        } else {
            Api_Flightdeck_PutEnableAll();
        }

        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> factFilter = createFactFilterForAllCampaigns();
        Map<String, Object> paginationAttributes = createPaginationAttributesForAllCampaigns();

        payload.put("factFilter", factFilter);
        payload.put("paginationAttributes", paginationAttributes);

        var response = callEndpoint(BASE_URL + "/flight-deck/amazon/campaign/all", "POST", new ObjectMapper().writeValueAsString(payload), headers, null);

        Assert.assertEquals(response.getStatusCode(), 200, "Response code was " + response.getStatusCode() + " instead of 200");

        List<Map<String, Object>> items = response.jsonPath().getList("items");
        for (String campaignId : campaignIds) {
            boolean campaignFound = false;
            for (int i = 0; i < items.size(); i++) {
                String dimensionId = response.jsonPath().getString("items[" + i + "].dimensionModel.dimensionId");
                if (dimensionId.equals(campaignId)) {
                    campaignFound = true;
                    String state = response.jsonPath().getString("items[" + i + "].dimensionModel.state");
                    Assert.assertEquals(state, "enabled", "State is not 'enabled' for campaign id: " + campaignId);
                    break;
                }
            }

            if (!campaignFound) {
                Assert.fail("Campaign id " + campaignId + " is not present in the response.");
            }
        }
    }

    @DataProvider(name = "allCampaignIds")
    public Object[][] campaignIdsProviderForAll() {
        return new Object[][]{
                {singleCampaignId},
                {multiCampaignIds},
                {allCampaignIds}
        };
    }
}
