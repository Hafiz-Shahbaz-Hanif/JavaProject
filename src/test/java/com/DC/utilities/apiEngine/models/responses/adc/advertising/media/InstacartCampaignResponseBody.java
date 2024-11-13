package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstacartCampaignResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {

        public String spend;
        public String clicks;
        public String impressions;
        public String sales;
        public String dateKey;
        public String instacartCampaignId;
        public String apiAdgroupId;
        public String campaignType;
        public String campaignName;
        public String yesterdaysSpendBudget;
        public String ioSegment;
        public String category;
        public String brand;
        public String subBrand;
        public String customSegment;
        public String targetingType;
        public String adGroupId;
        public String directSales;
        public String haloSales;
        public String directRoas;
        public String haloRoas;
        public String roasGainersAndDrainers;
        public String cpcGainersAndDrainers;
        public String cvrGainersAndDrainers;
        public String cpaGainersAndDrainers;
        public String quantities;
        public String campaignState;
        public String startDate;
        public String endDate;
        public String totalBudget;
        public String dailyBudget;
        public String budgetType;
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

        public List<String> getItemsDataForInstacartCampaign() {
            return Arrays.asList(spend, clicks, impressions, sales, dateKey, instacartCampaignId, apiAdgroupId, campaignType, campaignName, yesterdaysSpendBudget, ioSegment,
                    category, brand, subBrand, customSegment, targetingType, adGroupId, directSales,
                    haloSales, directRoas, haloRoas, roasGainersAndDrainers, cpcGainersAndDrainers, cvrGainersAndDrainers, cpaGainersAndDrainers, quantities, campaignState, totalBudget,
                    dailyBudget, budgetType, cpm, totalDisplayRecords);
        }
    }
}