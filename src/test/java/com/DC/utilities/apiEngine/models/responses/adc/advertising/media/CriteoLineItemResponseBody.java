package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CriteoLineItemResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {
        public String spend;
        public String clicks;
        public String impressions;
        public String internalId;
        public String apiId;
        public String accountId;
        public String apiAccountId;
        public String apiCampaignId;
        public String campaignName;
        public String lineitemName;
        public String retailer;
        public String targetBid;
        public String bidStrategy;
        public String maxBid;
        public String startDate;
        public String remainingBudget;
        public String pacingMethod;
        public String dailyPacing;
        public String statusId;
        public String status;
        public String dayPartingConfigurationName;

        public String getSpend() {
            return this.spend;
        }

        public String getClicks() {
            return this.clicks;
        }

        public String getImpressions() {
            return this.impressions;
        }

        public List<String> getCriteoLineIteData() {
            return Arrays.asList(spend, clicks, sales, internalId, apiId, accountId, apiAccountId, apiCampaignId, campaignName, lineitemName, retailer, targetBid, bidStrategy, maxBid, startDate, remainingBudget,
                    pacingMethod, dailyPacing, statusId, status, dayPartingConfigurationName);

        }

    }
}