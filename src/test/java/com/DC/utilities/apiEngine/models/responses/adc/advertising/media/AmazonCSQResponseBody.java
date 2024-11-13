package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AmazonCSQResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {
        public String spend;
        public String clicks;
        public String sales;
        public String impressions;
        public String roasGainersAndDrainers;
        public String cpcGainersAndDrainers;
        public String cvrGainersAndDrainers;
        public String cpaGainersAndDrainers;
        public String spc;
        public String attrSales14dSameSku;
        public String attrConversions14d;
        public String attrConversions14dSameSKU;
        public String attrUnitsOrdered14d;
        public String query;
        public List<CampaignKeywordDetails> campaignKeywordDetails;

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

        public List<String> getItemsCSQData() {
            return Arrays.asList(roasGainersAndDrainers, cpcGainersAndDrainers, cvrGainersAndDrainers, cpaGainersAndDrainers, spc, attrSales14dSameSku, attrConversions14d, attrConversions14dSameSKU, attrUnitsOrdered14d,
                    query);

        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CampaignKeywordDetails {
            public String state;
            public String spend;
            public String sales;
            public String clicks;
            public String impressions;
            public String attrConversions14d;
            public String campaignUnitType;
            public String bidLock;
            public String apiKeywordId;
            public String keyword;
            public String matchType;
            public String bid;
            public String topofSearchBid;
            public String productPageBid;
            public String apiCampaignId;
            public String campaignId;
            public String campaignType;
            public String campaignName;
            public String asin;
            public String campaignState;
            public String consumerAvailability;
            public String autoCampaign;

            public List<String> getItemsCampaignKeywordDetailsData() {
                return Arrays.asList(state, spend, sales, clicks, impressions, attrConversions14d, campaignUnitType, bidLock, apiKeywordId, keyword, matchType, bid, topofSearchBid, productPageBid,
                        campaignType, campaignName, asin, campaignState, consumerAvailability, autoCampaign);

            }

        }
    }
}