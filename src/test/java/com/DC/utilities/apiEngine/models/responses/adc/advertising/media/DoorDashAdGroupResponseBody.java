package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DoorDashAdGroupResponseBody extends BaseClassFlightdeckResponseBody {
    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {
        public String internalId;
        public String apiId;
        public String accountId;
        public String apiAccountId;
        public String apiCampaignId;
        public String campaignName;
        public String campaignType;
        public String adGroupName;
        public String startDate;
        public String endDate;
        public String statusId;
        public String status;
        public String bidStrategy;
        public String orders;
        public String conversions;
        public String adgroupName;
        public List<BidPlacements> bidPlacements;

        public List<String> getDoorDashAdGroupData() {
            return Arrays.asList(internalId, apiId, accountId, apiAccountId, apiCampaignId, campaignName, campaignType, adGroupName, startDate, endDate, statusId, status, bidStrategy,
                    orders, conversions, adgroupName);
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class BidPlacements {
            public String placementType;
            public String placementBidValue;
            public String currency;
            public String internalId;
            public String spend;
            public String sales;
            public String clicks;
            public String ctr;
            public String impressions;
            public String orders;
            public String conversions;

            public List<String> getBidPlacementsData() {
                return Arrays.asList(placementType, placementBidValue, currency, internalId, spend, sales, clicks, ctr, impressions, orders, conversions);
            }

        }
    }
}