package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalmartCampaignResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {

        public String spend;
        public String clicks;
        public String impressions;
        public String startDate;
        public String endDate;
        public String walmartCampaignId;
        public String apiCampaignId;
        public String campaignType;
        public String campaignName;
        public String targetingType;
        public String state;
        public String advertiserId;
        public String dailyBudget;
        public List<CampaignDetailPlatforms> campaignDetailPlatforms;
        public List<CampaignDetailPageType> campaignDetailPageType;
        public List<CampaignDetailPlacement> campaignDetailPlacement;
        public List<CampaignDetailPlacementInclusion> campaignDetailPlacementInclusion;
        public List<CampaignDetailPlacementMultiplier> campaignDetailPlacementMultiplier;

        public String getSpend() {
            return this.spend;
        }

        public String getClicks() {
            return this.clicks;
        }

        public String getImpressions() {
            return this.impressions;
        }

        public List<Object> getAllItemsDataWalmartCampaign() {
            List<Object> itemsData = new ArrayList<>();
            itemsData.addAll(getItemsDataForWalmartCampaign());
            itemsData.addAll(campaignDetailPlatforms);
            itemsData.addAll(campaignDetailPageType);
            itemsData.addAll(campaignDetailPlacement);
            itemsData.addAll(campaignDetailPlacementInclusion);
            itemsData.addAll(campaignDetailPlacementMultiplier);
            return itemsData;
        }

        public List<String> getItemsDataForWalmartCampaign() {
            return Arrays.asList(spend, clicks, impressions, startDate, endDate, walmartCampaignId, apiCampaignId, campaignType, campaignName, targetingType, state, advertiserId, dailyBudget);
        }

        public static class CampaignDetailPlatforms {

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
            public String walmartCampaignId;
            public String apiCampaignId;
            public String multiplierType;
            public String multiplierCategory;
            public String editable;

            public List<String> getCampaignDetailPlatformsData() {
                return Arrays.asList(spend, sales, clicks, roas, cpc, cvr, cpa, roasGainersAndDrainers, cpcGainersAndDrainers, cvrGainersAndDrainers, cpaGainersAndDrainers, impressions, ctr,
                        spc, conversion, walmartCampaignId, apiCampaignId, multiplierType, multiplierCategory, editable);
            }

        }

        public List<CampaignDetailPageType> getCampaignDetailPageType() {
            return campaignDetailPageType;
        }

        public static class CampaignDetailPageType {

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
            public String walmartCampaignId;
            public String apiCampaignId;
            public String multiplierType;
            public String multiplierCategory;

            public List<String> getCampaignDetailPageTypeData() {
                return Arrays.asList(spend, sales, clicks, roas, cpc, cvr, cpa, roasGainersAndDrainers, cpcGainersAndDrainers, cvrGainersAndDrainers, cpaGainersAndDrainers, impressions,
                        ctr, spc, conversion, walmartCampaignId, apiCampaignId, multiplierType, multiplierCategory);
            }
        }

        public List<CampaignDetailPlacement> getCampaignDetailPlacement() {
            return campaignDetailPlacement;
        }

        public static class CampaignDetailPlacement {

            public String spend;
            public String sales;
            public String clicks;
            public String roas;
            public String cpc;
            public String roasGainersAndDrainers;
            public String cpcGainersAndDrainers;
            public String impressions;
            public String ctr;
            public String spc;
            public String conversion;
            public String walmartCampaignId;
            public String apiCampaignId;
            public String multiplierType;
            public String multiplierCategory;
            public String multiplierLabel;

            public List<String> getCampaignDetailPlacementData() {
                return Arrays.asList(spend, sales, clicks, roas, cpc, roasGainersAndDrainers, cpcGainersAndDrainers, impressions, ctr,
                        spc, conversion, walmartCampaignId, apiCampaignId, multiplierType, multiplierCategory, multiplierLabel);
            }
        }

        public List<CampaignDetailPlacementInclusion> getCampaignDetailPlacementInclusion() {
            return campaignDetailPlacementInclusion;
        }

        public static class CampaignDetailPlacementInclusion {
            public String walmartCampaignId;
            public String apiCampaignId;
            public String multiplierType;
            public String multiplierCategory;
            public String multiplierLabel;
            public String placementInclusion;
            public String editable;
            public String placementInclusionEditable;

            public List<String> getCampaignDetailPlacementInclusionData() {
                return Arrays.asList(walmartCampaignId, apiCampaignId, multiplierType, multiplierCategory, multiplierLabel, placementInclusion, editable, placementInclusionEditable);
            }
        }

        public List<CampaignDetailPlacementMultiplier> getCampaignDetailPlacementMultiplier() {
            return campaignDetailPlacementMultiplier;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CampaignDetailPlacementMultiplier {
            public String walmartCampaignId;
            public String apiCampaignId;
            public String multiplierType;
            public String multiplierCategory;
            public String multiplierLabel;
            public String editable;

            public List<String> getCampaignDetailPlacementMultiplierData() {
                return Arrays.asList(walmartCampaignId, apiCampaignId, multiplierType, multiplierCategory, multiplierLabel, editable);
            }
        }
    }
}
