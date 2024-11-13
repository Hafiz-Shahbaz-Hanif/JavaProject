package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class RoundUpSegmentationResponseBody {

    public List<SubCategory> subCategory;

    @JsonIgnoreProperties
    public static class SubCategory{
        public String segmentationType;
        public String segmentationValue;
        String current;
        String previous;
        String last;
        public String segmentationTypeId;
        public String segmentationValueId;
        String iya;
        String previousPercent;
        String lastPercent;
        public String businessUnitId;
        public String businessUnitName;
    }
}
