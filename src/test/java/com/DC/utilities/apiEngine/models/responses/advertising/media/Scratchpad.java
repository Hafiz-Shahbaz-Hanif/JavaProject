package com.DC.utilities.apiEngine.models.responses.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Scratchpad {

    public SummaryData summaryData;
    public List<SlicerData> slicerData;
    public List<Data> data;
    public Summary summary;
    public Summary previousSummary;

    public SummaryData getSummaryData() {
        return summaryData;
    }

    public List<SlicerData> getSlicerData() {
        return slicerData;
    }

    public Summary getSummary() {return summary;}

    public Summary getPreviousSummary() {return previousSummary;}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SummaryData {
        String impressions;
        String clicks;
        String sales;
        String spend;
        String conversions;
        String CTR;
        String CPC;
        String SPC;
        String ROAS;
        String CVR;
        String CPA;

        public List<String> getSummaryData() {
            return Arrays.asList(impressions, clicks, CTR, CPC, SPC, CVR, CPA, sales, spend, ROAS, conversions);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summary {
        String impressions;
        String clicks;
        String sales;
        String spend;
        String conversions;
        String CTR;
        String CPC;
        String SPC;
        String ROAS;
        String CVR;
        String CPA;
        String segmentationValueId;
        String segmentationValue;
        String newToBrandOrders;
        String newToBrandOrdersPercentage;
        String newToBrandSales;
        String newToBrandSalesPercentage;
        String newToBrandUnits;
        String newToBrandUnitsPercentage;
        String newToBrandOrderRate;
        String totalUnits;

        public List<String> getSlicerSummary(){
            return Arrays.asList(segmentationValueId, segmentationValue, impressions, clicks, CTR, CPC, SPC, CVR, CPA, sales, spend, ROAS, conversions,
                    newToBrandOrders, newToBrandOrdersPercentage, newToBrandSales, newToBrandSalesPercentage, newToBrandUnits,
                    newToBrandOrderRate);
        }

        public List<String> getSlicerPreviousSummary(){
            return Arrays.asList(segmentationValueId, segmentationValue, impressions, clicks, CTR, CPC, SPC, CVR, CPA, sales, spend, ROAS, conversions,
                    newToBrandOrders, newToBrandOrdersPercentage, newToBrandSales, newToBrandSalesPercentage, newToBrandUnits,
                    newToBrandOrderRate);
        }
    }

    public static class SlicerData {
        String segmentationValueId;
        String segmentationValue;
        List<Data> data;

        public List<String> getSlicerData() {
            return Arrays.asList(segmentationValueId, segmentationValue);
        }

        public List<Data> getData() {
            return data;
        }

        public static class Data{
            String date;
            String value;
            String percentage;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        String currentYearValue;
        String lastYearValue;
        String percentageChange;

        public List<String> getSlicerYoyData() {
            return Arrays.asList(currentYearValue, lastYearValue, percentageChange);
        }

        public List<String> checkLastYearValue() {
            return Arrays.asList(lastYearValue);
        }
    }
}
