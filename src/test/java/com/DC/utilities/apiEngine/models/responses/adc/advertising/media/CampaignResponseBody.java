package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CampaignResponseBody extends BaseClassFlightdeckResponseBody {
    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {
        public String spend;
        public String clicks;
        public String sales;
        public String impressions;
        public String state;
        public String attrSales14dSameSku;
        public String attrConversions14d;
        public String attrConversions14dSameSKU;
        public String attrUnitsOrdered14d;
        public String attrSales1d;
        public String attrSales1dSameSku;
        public String attrConversions1d;
        public String attrConversions1dSameSKU;
        public String attrUnitsOrdered1d;
        public String attrSales7d;
        public String attrSales7dSameSku;
        public String attrConversions7d;
        public String attrConversions7dSameSKU;
        public String attrUnitsOrdered7d;
        public String attrSales30d;
        public String attrSales30dSameSku;
        public String attrConversions30d;
        public String attrConversions30dSameSKU;
        public String attrUnitsOrdered30d;
        public String bidLock;
        public String automatedBiddingStrategy;
        public String apiCampaignId;
        public String campaignId;
        public String campaignName;
        public String placement;
        public String campaignType;
        public String startDate;
        public String budget;
        public String defaultBid;
        public String isAutoCampaign;
        public String dayPartingConfigurationId;
        public String dayPartingConfigurationName;
        public String biddingStrategy;
        public String isDefaultBidEditable;
        public String ppuLastWeek;
        public String consumerAvailability;
        public String syncNegativeKeywords;
        public String syncNegativeKeywordsEditable;
        public String autoCampaign;

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

        public List<CampaignDetailPlacements> campaignDetailPlacements;

        public List<String> getItemsDataForAmazon() {
            return Arrays.asList(spend, clicks, impressions, sales, state, attrSales14dSameSku, attrConversions14d, attrConversions14dSameSKU,
                    attrUnitsOrdered14d, attrSales1d, attrSales1dSameSku, attrConversions1d, attrConversions1dSameSKU, attrUnitsOrdered1d,
                    attrSales7d, attrSales7dSameSku, attrConversions7d, attrConversions7dSameSKU, attrUnitsOrdered7d, attrSales30d,
                    attrSales30dSameSku, attrConversions30d, attrConversions30dSameSKU, attrUnitsOrdered30d, bidLock, apiCampaignId,
                    campaignId, campaignName, campaignType, startDate, budget, defaultBid, automatedBiddingStrategy, isAutoCampaign, dayPartingConfigurationId,
                    dayPartingConfigurationName, biddingStrategy, isDefaultBidEditable, ppuLastWeek, consumerAvailability, syncNegativeKeywords,
                    autoCampaign, syncNegativeKeywordsEditable);
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CampaignDetailPlacements {
            public String campaignId;
            public String placement;
            public String placementSpend;
            public String placementSales;
            public String placementClicks;
            public String placementRoas;
            public String placementCpc;
            public String placementCpa;
            public String placementCvr;
            public String placementRoasGainersAndDrainers;
            public String placementCpcGainersAndDrainers;
            public String placementCpaGainersAndDrainers;
            public String placementCvrGainersAndDrainers;
            public String placementImpressions;
            public String placementCtr;
            public String placementSpc;
            public String placementAttrSales14dSameSku;
            public String placementAttrConversions14d;
            public String placementAttrConversions14dSameSKU;
            public String placementAttrUnitsOrdered14d;
            public String placementAttrSales1d;
            public String placementAttrSales1dSameSku;
            public String placementAttrConversions1d;
            public String placementAttrConversions1dSameSKU;
            public String placementAttrUnitsOrdered1d;
            public String placementAttrSales7d;
            public String placementAttrSales7dSameSku;
            public String placementAttrConversions7d;
            public String placementAttrConversions7dSameSKU;
            public String placementAttrUnitsOrdered7d;
            public String placementAttrSales30d;
            public String placementAttrSales30dSameSku;
            public String placementAttrConversions30d;
            public String placementAttrConversions30dSameSKU;
            public String placementAttrUnitsOrdered30d;

            public List<String> getCampaignDetailPlacementData() {
                return Arrays.asList(campaignId, placement, placementSpend, placementSales, placementClicks, placementRoas, placementCpc,
                        placementCpa, placementCvr, placementRoasGainersAndDrainers, placementCpcGainersAndDrainers, placementCpaGainersAndDrainers,
                        placementCvrGainersAndDrainers, placementImpressions, placementCtr, placementSpc, placementAttrSales14dSameSku, placementAttrConversions14d,
                        placementAttrConversions14dSameSKU, placementAttrUnitsOrdered14d, placementAttrSales1d, placementAttrSales1dSameSku,
                        placementAttrConversions1d, placementAttrConversions1dSameSKU, placementAttrUnitsOrdered1d, placementAttrSales7d,
                        placementAttrSales7dSameSku, placementAttrConversions7d, placementAttrConversions7dSameSKU, placementAttrUnitsOrdered7d,
                        placementAttrSales30d, placementAttrSales30dSameSku, placementAttrConversions30d, placementAttrConversions30dSameSKU,
                        placementAttrUnitsOrdered30d);
            }
        }
    }
}
