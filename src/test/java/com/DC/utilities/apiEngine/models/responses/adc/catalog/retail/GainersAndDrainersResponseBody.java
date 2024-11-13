package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GainersAndDrainersResponseBody {
    public String segmentationValue;
    public List<GainersAndDrainers> gainers;
    public List<GainersAndDrainers> drainers;

    public String getSegmentationValue(){return segmentationValue;}
    public List<GainersAndDrainers> getGainers(){return gainers;}
    public List<GainersAndDrainers>getDrainers(){return drainers;}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GainersAndDrainers {
        public String asinId;
        public String asin;
        public String asinTitle;
        public String clientAccountName;
        public String segmentationValue;
        public String productImage;
        public String currentMetric;
        public String previousMetric;
        public String lastMetric;
        public String currentAsp;
        public String previousAsp;
        public String lastAsp;
        public String segmentPercentShare;

        public List<String> getGainers(){
            return Arrays.asList(asinId,asin,asinTitle,clientAccountName,segmentationValue,productImage,
                    currentMetric,previousMetric,lastMetric,currentAsp,previousAsp,lastAsp,segmentPercentShare);
        }

        public List<String> getDrainers(){
            return Arrays.asList(asinId,asin,asinTitle,clientAccountName,segmentationValue,productImage,
                    currentMetric,previousMetric,lastMetric,currentAsp,previousAsp,lastAsp,segmentPercentShare);
        }
    }
}
