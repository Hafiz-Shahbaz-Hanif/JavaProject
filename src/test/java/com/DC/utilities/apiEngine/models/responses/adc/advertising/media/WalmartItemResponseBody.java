package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WalmartItemResponseBody extends BaseClassFlightdeckResponseBody {

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
        public String advertiserId;
        public String apiItemId;
        public String availabilityState;
        public String currentAvailability;
        public String availabilityDate;

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

        public List<String> getItemsWalmartItemData() {
            return Arrays.asList(roasGainersAndDrainers, cpcGainersAndDrainers, cvrGainersAndDrainers, cpaGainersAndDrainers, advertiserId, apiItemId, availabilityState, currentAvailability, availabilityDate);

        }
    }
}
