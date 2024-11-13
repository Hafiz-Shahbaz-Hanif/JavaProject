package com.DC.utilities.apiEngine.models.requests.adc.catalog.retail;

import org.apache.log4j.Logger;

import java.util.List;

public class AsinScratchpadRequestBody {

    public String interval;
    public boolean requiresAsinLevel;
    public String metric;
    public DateRange dateRange;
    public String startDate;
    public String endDate;
    public boolean segmentationFiltersExist;
    public int businessUnitId;
    public String clientCategory;
    public String clientAccountType;
    public List<String> segmentationFilters;
    public int obsoleteAsinTypeId;
    public String retailerPlatform;
    public String distributorView;
    public List<String> asinIds;

    public AsinScratchpadRequestBody(
            String interval,
            boolean requiresAsinLevel,
            String metric,
            DateRange dateRange,
            String startDate,
            String endDate,
            boolean segmentationFiltersExist,
            int businessUnitId,
            String clientCategory,
            String clientAccountType,
            List<String> segmentationFilters,
            int obsoleteAsinTypeId,
            String retailerPlatform,
            String distributorView,
            List<String> asinIds
    )
    {
        this.interval = interval;
        this.requiresAsinLevel = requiresAsinLevel;
        this.metric = metric;
        this.dateRange = dateRange;
        this.startDate = startDate;
        this.endDate = endDate;
        this.segmentationFiltersExist = segmentationFiltersExist;
        this.businessUnitId = businessUnitId;
        this.clientCategory = clientCategory;
        this.clientAccountType = clientAccountType;
        this.segmentationFilters = segmentationFilters;
        this.obsoleteAsinTypeId = obsoleteAsinTypeId;
        this.retailerPlatform = retailerPlatform;
        this.distributorView = distributorView;
        this.asinIds = asinIds;

        Logger.getLogger(AsinScratchpadRequestBody.class).info("** Serializing request body for Asin Scratchpad request body");
    }

    public static class DateRange {

        public String label;

        public DateRange(String label) {
            this.label = label;
        }

    }
}
