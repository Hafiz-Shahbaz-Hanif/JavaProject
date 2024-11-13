package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlicerChartAggbusResponseBody {
    public List<SlicerData> slicerData;

    public List<SlicerData> getslicerData() {
        return slicerData;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SlicerData{
        public String segmentationValueId;
        public String segmentationValue;
        public String businessUnitId;
        public String businessUnitName;
        public List<Data> data;

        public String getSegmentationValueId() { return segmentationValueId;}
        public String getSegmentationValue() { return segmentationValue;}
        public String getBusinessUnitId(){ return  businessUnitId;}
        public String getBusinessUnitName(){ return businessUnitName;}

        @JsonIgnoreProperties(ignoreUnknown = true)
        public List<Object> getAllChartData() {
            List<Object> DatesAndValuesData = new ArrayList<>();
            DatesAndValuesData.addAll(data);
            return DatesAndValuesData;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Data{
            public String date;
            public String value;
            public String percentage;

            public List<String> getDatesAndValues() {
                return Arrays.asList(date,value,percentage);
            }

        }
    }
}
