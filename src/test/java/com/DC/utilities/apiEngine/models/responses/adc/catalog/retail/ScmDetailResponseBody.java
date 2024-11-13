package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScmDetailResponseBody {

    public Metrics metrics;
    public Metrics getMetrics() {
        return metrics;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metrics {
        @JsonProperty("Shipped Revenue")
        public List<ShippedRevenue> Shipped_Revenue;
        @JsonProperty("Conversion Rate")
        public List<ConversionRate> Conversion_Rate;
        @JsonProperty("Ordered Revenue")
        public List<OrderedRevenue> Ordered_Revenue;
        @JsonProperty("Average Sales Price")
        public List<AverageSalesPrice> Average_Sales_Price;
        @JsonProperty("Glance Views")
        public List<GlanceViews> Glance_Views;
        @JsonProperty("SP Spend")
        public List<SPSpend> SP_Spend;


        public static class ShippedRevenue {
            public String date;
            public String value;
            public String dataType;

            public List<String> getShippedRevenue() {
                return Arrays.asList(date, value, dataType);
            }
        }

        public static class ConversionRate{
            public String date;
            public String value;
            public String dataType;

            public List<String> getConversionRate(){
                return Arrays.asList(date,value,dataType);
            }
        }
        public static class OrderedRevenue{
            public String date;
            public String value;
            public String dataType;

            public List<String> getOrderedRevenue(){
                return Arrays.asList(date,value,dataType);
            }
        }
        public static class GlanceViews{
            public String date;
            public String value;
            public String dataType;

            public List<String> getGlanceViews(){
                return Arrays.asList(date,value,dataType);
            }
        }
        public static class AverageSalesPrice{
            public String date;
            public String value;
            public String dataType;

            public List<String> getAverageSalesPrice(){
                return Arrays.asList(date,value,dataType);
            }
        }
        public static class SPSpend{
            public String date;
            public String value;
            public String dataType;

            public List<String> getSPSpend(){
                return Arrays.asList(date,value,dataType);
            }
        }

    }

}
