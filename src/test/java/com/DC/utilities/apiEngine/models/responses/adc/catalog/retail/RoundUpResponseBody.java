package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import java.util.Arrays;
import java.util.List;

public class RoundUpResponseBody {

    public MtdAndFiscalWidget mtdAndFiscalWidget;
    public MetricAndAspWidget metricAndAspWidget;
    public List<PopWidget> popWidget;
    public List<YoyWidget> yoyWidget;

    public static class MtdAndFiscalWidget {
        String monthToDate;
        String fiscalYearToDate;
        String lastFiscalYearToDate;

        public List<String> getMtdAndFiscalWidgetValues() {
            return Arrays.asList(monthToDate, fiscalYearToDate, lastFiscalYearToDate);
        }
    }

    public static class MetricAndAspWidget {
        public Metric metric;
        public AverageSellingPrice averageSellingPrice;

        public static class Metric {
            String current;
            String previous;
            String last;
            String iya;

            public List<String> getMetricValues() {
                return Arrays.asList(current, previous, last, iya);
            }
        }

        public static class AverageSellingPrice {
            String current;
            String previous;
            String last;
            String iya;

            public List<String> averageSellingPriceValues() {
                return Arrays.asList(current, previous, last, iya);
            }
        }
    }

    public static class PopWidget {
        public String day;
        public String month;
        public String year;
        public String current;
        public String previous;
        public String percentageChange;
        public String averageSellingPrice;
        public String changeFromLastYear;
        public String changeFromPreviousPeriod;

        public List<String> getPopWidgetValues() {
            return Arrays.asList(day, month, year, current, previous, percentageChange, averageSellingPrice,
                    changeFromLastYear, changeFromPreviousPeriod);
        }
    }

    public static class YoyWidget {
        public String day;
        public String month;
        public String year;
        public String current;
        public String previous;
        public String percentageChange;
        public String averageSellingPrice;
        public String iya;
        public String changeFromLastYear;
        public String changeFromPreviousPeriod;

        public List<String> getYoyWidgetValues() {
            return Arrays.asList(day, month, year, current, previous, percentageChange, averageSellingPrice, iya,
                    changeFromLastYear, changeFromPreviousPeriod);
        }
    }
}
