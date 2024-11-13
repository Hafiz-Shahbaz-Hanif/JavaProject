package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstacartAdGroupResponseBody extends BaseClassFlightdeckResponseBody {

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
        public String dateKey;
        public String apiAdgroupId;
        public String adGroupName;
        public String performanceCategory;
        public String instacartCampaignId;
        public String adGroupId;
        public String apiCampaignId;
        public String campaignType;
        public String dayPartingConfigurationId;
        public String dayPartingConfigurationName;
        public String adGroupState;
        public String campaignName;
        public String adGroupDefaultBid;
        public String yesterdaysSpendBudget;
        public String ioSegment;
        public String category;
        public String brand;
        public String subBrand;
        public String customSegment;
        public String targetingType;
        public String accountId;
        public String directSales;
        public String haloSales;
        public String directRoas;
        public String haloRoas;
        public String roasGainersAndDrainers;
        public String cpcGainersAndDrainers;
        public String cvrGainersAndDrainers;
        public String cpaGainersAndDrainers;
        public String cpm;
        public String totalDisplayRecords;
        public String quantity;
        public List<AdGroupPerformanceMetrics> adGroupPerformanceMetrics;

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

        public List<String> getInstacartAdGroupData() {
            return Arrays.asList(spend, clicks, impressions, sales, dateKey, apiAdgroupId, adGroupName, performanceCategory, instacartCampaignId, adGroupId, apiCampaignId, campaignType, dayPartingConfigurationId, dayPartingConfigurationName,
                    adGroupState, campaignName, adGroupDefaultBid, yesterdaysSpendBudget, ioSegment, category, brand, subBrand, customSegment, targetingType, accountId, directSales, haloSales, directRoas,
                    haloRoas, roasGainersAndDrainers, cpcGainersAndDrainers, cvrGainersAndDrainers, cpaGainersAndDrainers, cpm, totalDisplayRecords, quantity);

        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class AdGroupPerformanceMetrics {
            public String adGroupId;
            public String performanceCategory;
            public String performanceSpend;
            public String performanceSales;
            public String performanceClicks;
            public String performanceRoas;
            public String performanceCpc;
            public String placementCpc;
            public String placementCvr;
            public String performanceImpressions;
            public String performanceCtr;
            public String performanceConversions;

            public List<String> getAdGroupPerformanceMetricsData() {
                return Arrays.asList(adGroupId, performanceCategory, performanceSpend, performanceSales, performanceClicks, performanceRoas, performanceCpc, placementCpc,
                        placementCvr, performanceImpressions, performanceCtr, performanceConversions);

            }
        }
    }
}