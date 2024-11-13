package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import java.util.List;

public class AsinScratchpadResponseBody {

    public String asinId;
    public String asin;
    public String title;
    public String clientAccountName;
    public AsinScratchpadData asinScratchpadData;

    public static class AsinScratchpadData{
        public List<String> pcogs;
        public List<String> avgSellingOrObservedPrice;
        public List<String> downloadDate;
        public List<String> attributedSales14dSameSku;
        public List<String> attributedSales14dOtherSku;
        public List<String> attributedPercentRetail;
        public List<String> orderedRevenue;
        public List<String> orderedUnit;
        public List<String> shippedUnit;
        public List<String> changeGlanceViews;
        public List<String> repOOS;
        public List<String> changeConversion;
        public List<String> lbbPrice;
        public List<String> clicks;
        public List<String> spend;
        public List<String> cpa;
        public List<String> cpc;
        public List<String> attributedUnits14d;
        public List<String> shippedRevenue;
        public List<String> unitsInStock;
        public List<String> glanceViews;
        public List<String> lostOpportunity;
        public List<String> weightedRepOos;
        public List<String> lbbLostOpportunity;
        public List<String> procurableOOS;
    }
}
