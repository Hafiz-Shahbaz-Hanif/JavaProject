package com.DC.utilities.apiEngine.models.responses.adc.catalog.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShareOfVoiceResponseBody {

    public BrandSov brandSov;
    public String aggregationInterval;
    String dataAsOf;
    String mostRecentScrapeAsOf;

    public List<String> getAggregationIntervalForAmazonPlatform() {return Arrays.asList(aggregationInterval,
            dataAsOf,mostRecentScrapeAsOf); }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BrandSov {

        public List<SovData> sovData;

        public static class SovData {
            public String searchQuery;
            public String placement;
            public List<BrandSovData> brandSovData;

            public List<String> getSovDataParameters() {
                return Arrays.asList(searchQuery, placement);
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class BrandSovData{
                String brand;
                String brandId;

                public List<String> getbrandSovData() {
                    return Arrays.asList(brand, brandId);
                }
            }
        }

    }
}
