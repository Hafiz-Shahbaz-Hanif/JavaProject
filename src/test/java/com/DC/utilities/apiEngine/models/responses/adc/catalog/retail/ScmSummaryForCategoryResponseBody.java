package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScmSummaryForCategoryResponseBody extends ScmSummaryForBrandResponseBody {

    public MetricsCategory metrics;
    public MetricsCategory getMetricsCategory(){
        return metrics;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetricsCategory{
        @JsonProperty("Shipped Revenue")
        public ShippedRevenue Shipped_Revenue;
        @JsonProperty("Glance Views")
        public GlanceViews Glance_Views;
        @JsonProperty("Ordered Revenue")
        public OrderedRevenue Ordered_Revenue;
        @JsonProperty("Conversion Rate")
        public ConversionRate Conversion_Rate;

        public static class ShippedRevenue {
            public String current;
            public String previous;

            public String getCurrent(){return current;}
            public String getPrevious(){return previous;}
        }

        public static class GlanceViews{
            public String current;
            public String previous;

            public String getCurrent(){return current;}
            public String getPrevious(){return previous;}
        }

        public static class OrderedRevenue{
            public String current;
            public String previous;

            public String getCurrent(){return current;}
            public String getPrevious(){return previous;}
        }

        public static class ConversionRate{
            public String current;
            public String previous;

            public String getCurrent(){return current;}
            public String getPrevious(){return previous;}
        }
    }

    public SegmentedSales segmentedSales;
    public SegmentedSales getsegmentedSales(){
        return segmentedSales;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SegmentedSales{
        public List<Category> CATEGORY;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Category{
            String name;
            String value;

            public List<String> getCategory(){
                return Arrays.asList(name,value);
            }

        }

    }
    public SegmentedOrderedRevenues segmentedOrderedRevenues;
    public SegmentedOrderedRevenues getsegmentedOrderedRevenues() {
        return segmentedOrderedRevenues;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SegmentedOrderedRevenues{
        public List<Category> CATEGORY;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Category{
            String name;
            String value;

            public List<String> getCategory(){
                return Arrays.asList(name,value);
            }

        }


    }

}
