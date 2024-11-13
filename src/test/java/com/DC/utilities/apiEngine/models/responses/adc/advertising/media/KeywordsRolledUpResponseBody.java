package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeywordsRolledUpResponseBody extends BaseClassFlightdeckResponseBody {

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
        public String attrSales14dSameSku;
        public String attrConversions14d;
        public String attrConversions14dSameSKU;
        public String campaignUnitType;

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

        public List<String> getKeywordsRolledUpItemsData() {
            return Arrays.asList(roasGainersAndDrainers, cpcGainersAndDrainers, cvrGainersAndDrainers, cpaGainersAndDrainers, attrSales14dSameSku, attrConversions14d, attrConversions14dSameSKU,
                    campaignUnitType);

        }
    }
}
