package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RetailScratchpadResponseBody {

    public ChartData chartData;

    public ChartData getchartData() {
        return chartData;
    }

    public static class ChartData  {
        public String downloadDate;
        public String orderedRevenue;
        public String orderedUnits;
        public String shippedCogs;
        public String shippedUnits;
        public String shippedRevenue;
        public String averageSellingPrice;
        public String unitsInStock;
        public String attributedSales14d;
        public String attributedUnits14d;
        public String salesPercent;
        public String clicks;
        public String spend;
        public String cpc;
        public String cpa;
        public String glanceViews;
        public String lostOpportunity;
        public String weightedRepOos;
        public String sbSales;
        public String sbSpend;
        public String sdSales;
        public String sdSpend;
        public String lbbLostOpportunity;
        public String conversionRate;
        public String totalOrderItems;
        public String totalSessions;
        public String totalPageviews;
        public String buyBoxPercentage;

        public String getdownloadDate() {
            return downloadDate;
        }
        public String getorderedRevenue() {
            return orderedRevenue;
        }
        public String getorderedUnits() {
            return orderedUnits;
        }
        public String getshippedCogs() {
            return shippedCogs;
        }
        public String getshippedUnits() {
            return shippedUnits;
        }
        public String getshippedRevenue() {
            return shippedRevenue;
        }
        public String getaverageSellingPrice() {
            return averageSellingPrice;
        }
        public String getunitsInStock() {
            return unitsInStock;
        }
        public String getattributedSales14d() {
            return attributedSales14d;
        }
        public String getattributedUnits14d() {
            return attributedUnits14d;
        }
        public String getsalesPercent() {
            return salesPercent;
        }
        public String getclicks() {
            return clicks;
        }
        public String getspend() {
            return spend;
        }
        public String getcpc() {
            return cpc;
        }
        public String getcpa() {
            return cpa;
        }
        public String getglanceViews() {
            return glanceViews;
        }
        public String getlostOpportunity() {
            return lostOpportunity;
        }
        public String getweightedRepOos() {
            return weightedRepOos;
        }
        public String getsbSales() {
            return sbSales;
        }
        public String getsbSpend() {
            return sbSpend;
        }
        public String getsdSales() {
            return sdSales;
        }
        public String getsdSpend() {
            return sdSpend;
        }
        public String getlbbLostOpportunity() {
            return lbbLostOpportunity;
        }
        public String getconversionRate() {
            return conversionRate;
        }
        public String gettotalOrderItems() {
            return totalOrderItems;
        }
        public String gettotalSessions() {
            return totalSessions;
        }
        public String gettotalPageviews() {
            return totalPageviews;
        }
        public String getbuyBoxPercentage() {
            return buyBoxPercentage;
        }

    }

    public List<MetricDateValues> metricDateValues;

    public List<MetricDateValues> getmetricDateValues() {
        return metricDateValues;
    }

    public static class MetricDateValues{

        public String metric;
        public List<DatesAndValues> datesAndValues;


        public String getmetric() { return metric;}

        public List<Object> getAllDatesAndValues() {
            List<Object> DatesAndValuesData = new ArrayList<>();
            DatesAndValuesData.addAll(datesAndValues);
            return DatesAndValuesData;
        }

        public static class DatesAndValues{
            public String date;
            public String value;

            public List<String> getDatesAndValues() {
                return Arrays.asList(date,value);
            }
        }
    }







}
