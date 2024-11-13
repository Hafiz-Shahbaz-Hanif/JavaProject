package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScmSummaryForBrandResponseBody {

    public DateRange dateRange;
    public DateRange getDateRange(){
        return dateRange;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DateRange{
        public String startDate;
        public String endDate;

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

    }

    public Metrics metrics;
    public Metrics getMetrics(){
        return metrics;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metrics {
        @JsonProperty("Shipped Revenue")
        public ShippedRevenue Shipped_Revenue;
        @JsonProperty("Conversion Rate")
        public ConversionRate Conversion_Rate;
        @JsonProperty("Ordered Revenue")
        public OrderedRevenue Ordered_Revenue;
        @JsonProperty("Glance Views")
        public GlanceViews Glance_Views;

        public static class ShippedRevenue {
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
        public static class OrderedRevenue{
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
    }

    public SegmentedSales segmentedSales;
    public SegmentedSales getSegmentedSales(){
        return segmentedSales;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SegmentedSales{
        public List<Brand> BRAND;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Brand{
            String name;
            String value;

            public List<String> getBrand(){
                return Arrays.asList(name,value);
            }
        }
    }

    public SegmentedOrderedRevenues segmentedOrderedRevenues;
    public SegmentedOrderedRevenues getSegmentedOrderedRevenues(){
        return segmentedOrderedRevenues;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SegmentedOrderedRevenues{
        public List<Brand> BRAND;

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Brand{
            String name;
            String value;

            public List<String> getBrand(){
                return Arrays.asList(name,value);
            }
        }
    }
}
