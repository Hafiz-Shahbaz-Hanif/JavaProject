package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CriteoProductResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items extends BaseClassFlightdeckResponseBody.Items {

        public String spend;
        public String clicks;
        public String impressions;
        public String internalId;
        public String apiId;
        public String productName;
        public String productCategory;
        public String brandName;
        public String lineitemId;
        public String apiLineitemId;
        public String lineitemName;
        public String retailer;
        public String availability;
        public String catalogMinBid;
        public String averageCpc;

        public String getSpend() {
            return this.spend;
        }

        public String getClicks() {
            return this.clicks;
        }

        public String getImpressions() {
            return this.impressions;
        }

        public List<String> getCriteoProductData() {
            return Arrays.asList(spend, clicks, impressions, internalId, apiId, productName, productCategory, brandName, lineitemId, apiLineitemId, lineitemName, retailer, availability, catalogMinBid, averageCpc);

        }

    }
}