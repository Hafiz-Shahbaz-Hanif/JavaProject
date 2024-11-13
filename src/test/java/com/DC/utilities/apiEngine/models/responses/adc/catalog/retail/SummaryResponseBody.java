package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryResponseBody {

    public Summary summary;
    public Summary previousSummary;

    public Summary getSummary() {return summary;}
    public Summary getPreviousSummary() {return previousSummary;}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summary{
         String groupBy;
         String segmentationValueId;
         String businessUnitId;
         String businessUnitName;
         String glanceViews;
         String conversionRate;
         String shippedCogs;
         String orderedRevenue;
         String orderedUnits;
         String averageSellingPrice;
         String clicks;
         String weightedRepOos;
         String lostOpportunity;
         String lbbLostOpportunity;
         String spSpend;
         String cpc;
         String shippedUnits;
         String shippedRevenue;
         String unitsInStock;
         String attributedSales14d;
         String attributedUnits14d;
         String salesPercent;
         String cpa;
         String weightedRepOosNumerator;
         String weightedRepOosDenominator;
         String attributedConversion;
         String totalOrderItems;
         String totalSessions;
         String totalPageviews;
         String buyBoxPercentage;

        public List<String> getSummary(){
            return Arrays.asList(groupBy, segmentationValueId, businessUnitId, businessUnitName, glanceViews, conversionRate, shippedCogs,
                    orderedRevenue, orderedUnits, averageSellingPrice, clicks, weightedRepOos, lostOpportunity, lbbLostOpportunity, spSpend, cpc,
                    shippedUnits, shippedRevenue,unitsInStock,attributedSales14d,attributedUnits14d,salesPercent,cpa,weightedRepOosNumerator,weightedRepOosDenominator,
                    attributedConversion,totalOrderItems,totalSessions,totalPageviews, buyBoxPercentage);
        }

        public List<String> getPreviousSummary(){
            return Arrays.asList(groupBy, segmentationValueId, businessUnitId, businessUnitName, glanceViews, conversionRate, shippedCogs,
                    orderedRevenue, orderedUnits, averageSellingPrice, clicks, weightedRepOos, lostOpportunity, lbbLostOpportunity, spSpend, cpc,
                    shippedUnits, shippedRevenue,unitsInStock,attributedSales14d,attributedUnits14d,salesPercent,cpa,weightedRepOosNumerator,weightedRepOosDenominator,
                    attributedConversion,totalOrderItems,totalSessions,totalPageviews, buyBoxPercentage);
        }

    }
}
