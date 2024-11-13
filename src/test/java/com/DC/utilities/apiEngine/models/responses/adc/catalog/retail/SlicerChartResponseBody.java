package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlicerChartResponseBody {

    public List<SlicerData> slicerData;

    public List<SlicerData> getslicerData() {
        return slicerData;
    }

    public static class SlicerData{
        public String segmentationValueId;
        public String segmentationValue;
        public List<Data> data;

        public String getSegmentationValueId() { return segmentationValueId;}
        public String getSegmentationValue() { return segmentationValue;}

        public List<Object> getAllChartData() {
            List<Object> DatesAndValuesData = new ArrayList<>();
            DatesAndValuesData.addAll(data);
            return DatesAndValuesData;
        }

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
