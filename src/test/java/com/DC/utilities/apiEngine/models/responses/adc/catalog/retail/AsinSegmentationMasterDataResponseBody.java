package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsinSegmentationMasterDataResponseBody extends BaseClassAsinSegmentationResponseBody {

    public List<Items> items;

    public List<Items> getItems() { return items; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        public String asinId;
        public String asin;
        public String asinUrl;
        public String latestTitle;
        public String clientAccountName;
        public String countryOfOrigin;
        public String asinCreatedOn;
        public String preparationType;
        public String asinUpdatedOn;

        public List<String> getItemsData (){
            return Arrays.asList(asinId,asin,latestTitle,clientAccountName,countryOfOrigin
            ,asinCreatedOn,preparationType,asinUpdatedOn);
        }
    }
}
