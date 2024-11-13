package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CSQResponseBody extends BaseClassFlightdeckResponseBody {

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
        public String query;
        public List<AdGroupKeywordDetailModels> adGroupKeywordDetailModels;

        public String getSpend() {
            return this.spend;
        }

        public String getClicks() {
            return this.clicks;
        }

        public String getImpressions() {
            return this.impressions;
        }
        public String getSales() { return this.sales;}

        public static class AdGroupKeywordDetailModels {

            public String spend;
            public String sales;
            public String clicks;
            public String roas;
            public String cpc;
            public String cvr;
            public String cpa;
            public String roasGainersAndDrainers;
            public String cpcGainersAndDrainers;
            public String cvrGainersAndDrainers;
            public String cpaGainersAndDrainers;
            public String impressions;
            public String ctr;
            public String spc;
            public String conversion;
            public String keywordId;
            public String walmartCampaignId;
            public String apiCampaignId;
            public String apiAdgroupId;
            public String apiKeywordId;
            public String campaignName;
            public String keyword;
            public String matchType;
            public String bid;
            public String state;
            public String advertiserId;
            public String adGroupName;
            public String itemImageUrl;
            public String placementBuyBoxBid;
            public String placementSearchIngridBid;
            public String platformDesktopBid;
            public String platformMobileBid;
            public String platformAppBid;

            public List<String> getAdGroupKeywordDetailModelsData() {
                return Arrays.asList(spend, sales, clicks, roas, cpc, cvr, cpa,
                        roasGainersAndDrainers, cpcGainersAndDrainers, cvrGainersAndDrainers, cpaGainersAndDrainers, impressions,
                        ctr, spc, conversion, keywordId, walmartCampaignId, apiCampaignId,
                        apiAdgroupId, apiKeywordId, campaignName, keyword,
                        matchType, bid, state, advertiserId,
                        adGroupName, itemImageUrl, placementBuyBoxBid, placementSearchIngridBid,
                        platformDesktopBid, platformMobileBid, platformAppBid);
            }
        }
    }
}
