package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DoorDashCampaignsResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {
        public String campaignName;
        public String internalId;
        public String apiId;
        public String startDate;
        public String endDate;
        public String budgetType;
        public String lifeTimeBudget;
        public String dailyBudget;
        public String bidStrategy;
        public String status;
        public String orders;
        public String conversions;

        public List<String> getDoorDashCampaignsData() {
            return Arrays.asList(campaignName, internalId, apiId, startDate, endDate, budgetType, lifeTimeBudget, dailyBudget, bidStrategy, status, orders, conversions);

        }
    }
}