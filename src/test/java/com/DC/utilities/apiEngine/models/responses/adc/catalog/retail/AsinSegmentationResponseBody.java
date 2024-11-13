package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsinSegmentationResponseBody extends BaseClassAsinSegmentationResponseBody{

    public List<Items> items;

    public List<Items> getItems() { return items; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        public String asinId;
        public String asin;
        public String clientAccountName;
        public String latestTitle;
        public String platform;
        public String asinUrl;

        public List<String> getItemsData (){
            return Arrays.asList(asinId,asin,clientAccountName,latestTitle,platform);
        }
        }
}
