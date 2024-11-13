package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

public class InstacartKeywordByAdGroupResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items extends BaseClassFlightdeckResponseBody.Items {

        public String spend;
        public String clicks;
        public String impressions;
        public String sales;
        public String campaignId;
        public String adGroupId;
        public String keywordId;
        public String dateKey;
        public String apiCampaignId;
        public String apiAdgroupId;
        public String apiKeywordId;
        public String campaignName;
        public String campaignType;
        public String adGroupName;
        public String keyword;
        public String matchType;
        public String cpcBid;
        public String minBid;
        public String suggestedBid;
        public String apiAccountId;
        public String ioSegment;
        public String category;
        public String brand;
        public String subBrand;
        public String customSegment;
        public String targetingType;
        public String accountId;
        public String directSales;
        public String haloSales;
        public String quantity;
        public String directRoas;
        public String haloRoas;
        public String yesterdaySpentBudget;
        public String cpm;
        public String totalDisplayRecords;

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

        public List<String> getItemsDataForInstacartKeywordByAdGroup() {
            return Arrays.asList(
                    spend, clicks, impressions, sales, campaignId, adGroupId, keywordId, dateKey, apiCampaignId, apiAdgroupId, apiKeywordId, campaignName,
                    campaignType, adGroupName, keyword, matchType, cpcBid, minBid, suggestedBid, apiAccountId, ioSegment,
                    category, brand, subBrand, customSegment, targetingType, accountId, directSales, haloSales, quantity,
                    directRoas, haloRoas, yesterdaySpentBudget, cpm, totalDisplayRecords
            );
        }
    }
}