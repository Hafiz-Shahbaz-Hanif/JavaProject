package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstacartKeywordsRolledUpResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {

        public String spend;
        public String clicks;
        public String sales;
        public String impressions;
        public String dateKey;
        public String keyword;
        public String campaignType;
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
        public String roasGainersAndDrainers;
        public String cpcGainersAndDrainers;
        public String cvrGainersAndDrainers;
        public String cpaGainersAndDrainers;
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

        public List<String> getItemsDataForInstacartKeywordsRolledUp() {
            return Arrays.asList(
                    spend, clicks, impressions, sales, dateKey, keyword, campaignType, apiAccountId, ioSegment, category, brand, subBrand, customSegment,
                    targetingType, accountId, directSales, haloSales, quantity,
                    directRoas, haloRoas, roasGainersAndDrainers, cpcGainersAndDrainers, cvrGainersAndDrainers, cpaGainersAndDrainers, cpm, totalDisplayRecords
            );
        }
    }
}