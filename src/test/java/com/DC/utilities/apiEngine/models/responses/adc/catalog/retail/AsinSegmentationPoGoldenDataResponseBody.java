package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsinSegmentationPoGoldenDataResponseBody extends BaseClassAsinSegmentationResponseBody{

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        public String asinId;
        public String asin;
        public String asinUrl;
        public String latestTitle;
        public String clientAccountName;
        public String countryOfOrigin;
        public List<SegmentationDetailFragments> segmentationDetailFragments;

        public List<String> getItemsData (){
            return Arrays.asList(asinId,asin,latestTitle,clientAccountName,countryOfOrigin);
        }

        public static class SegmentationDetailFragments {
            public String id;
            public String segmentationTypeId;
            public String segmentationLabel;
            public SegmentationValue segmentationValue;

            public List<String> getSegmentationDetailFragments(){
                return Arrays.asList(id,segmentationTypeId,segmentationLabel);
            }

            public static class SegmentationValue{
                public String segmentationValueId;
                public String segmentationValue;

                public List<String> getSegmentationValue(){return Arrays.asList(segmentationValueId,segmentationValue);}

            }
        }
    }
}

