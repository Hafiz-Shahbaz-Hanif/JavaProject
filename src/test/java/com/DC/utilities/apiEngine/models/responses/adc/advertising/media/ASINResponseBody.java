package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ASINResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {
        public String spend;
        public String clicks;
        public String sales;
        public String impressions;
        public String attrConversions14d;
        public String asin;
        public String asinSegmentedValueId;
        public String isAsinSegmented;
        public String aspState;
        public String availabilityState;
        public String wohState;
        public List<CampaignsList> campaignsList;

        public String getSpend() {
            return this.spend;
        }

        public String getClicks() {
            return this.clicks;
        }

        public String getImpressions() {
            return this.impressions;
        }

        public String getSales() {
            return this.sales;
        }

        public List<String> getItemsASINData() {
            return Arrays.asList(attrConversions14d, asin, asinSegmentedValueId, attrConversions14d, isAsinSegmented,
                    aspState, availabilityState,
                    wohState);

        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CampaignsList {
            public String campaignId;
            public String campaignName;
            public String campaignStatus;
            public String campaignSegmentationValueId;
            public String targetingType;
            public String apiCampaignId;

            public List<String> getItemsCampaignsListData() {
                return Arrays.asList(campaignId, campaignName,
                        campaignStatus, campaignSegmentationValueId, targetingType, apiCampaignId);

            }

        }
    }
}
