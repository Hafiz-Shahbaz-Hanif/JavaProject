package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeywordByAdGroupResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {
        public String spend;
        public String clicks;
        public String impressions;
        public String sales;
        public String keywordId;
        public String walmartCampaignId;
        public String apiCampaignId;
        public String apiAdgroupId;
        public String apiKeywordId;
        public String campaignType;
        public String campaignName;
        public String keyword;
        public String matchType;
        public String bid;
        public String state;
        public String advertiserId;
        public String adGroupName;

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

        public List<String> getItemsDataForWalmartKeywordByAdGroup() {
            return Arrays.asList(spend, sales, clicks, roas, cpc, cvr, cpa,
                    impressions, ctr, keywordId, walmartCampaignId, apiCampaignId, campaignType,
                    apiAdgroupId, apiKeywordId, campaignName, keyword,
                    matchType, bid, state, advertiserId, adGroupName);
        }
    }
}