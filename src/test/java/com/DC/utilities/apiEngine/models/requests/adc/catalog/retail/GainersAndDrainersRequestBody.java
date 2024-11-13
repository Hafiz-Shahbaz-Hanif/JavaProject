package com.DC.utilities.apiEngine.models.requests.adc.catalog.retail;

public class GainersAndDrainersRequestBody {

    public PagingAttributes pagingAttributes;
    public boolean showDataStatus;
    public String interval;
    public String maxMonthlyDate;
    public DateRange dateRange;
    public String startDate;
    public String endDate;
    public boolean segmentationFiltersExist;
    public boolean isOnlyBusinessUnit;
    public int businessUnitId;
    public String clientCategory;
    public String clientAccountType;
    public boolean isClientsRequired;
    public int fytdStartDate;
    public int lfytdStartDate;
    public int obsoleteAsinTypeId;
    public boolean requiresMtd;
    public boolean requiresProfitability;
    public String retailerPlatform;
    public String distributorView;
    public String reportingMetric;
    public String calculationType;
    public int asinCount;
    public boolean segmentedUnsegmented;
    public String metricLabel;
    public String format;
    public String formatShort;

    public GainersAndDrainersRequestBody(PagingAttributes pagingAttributes,
                                         boolean showDataStatus,
                                         String interval,
                                         String maxMonthlyDate,
                                         DateRange dateRange,
                                         String startDate,
                                         String endDate,
                                         boolean segmentationFiltersExist,
                                         boolean isOnlyBusinessUnit,
                                         int businessUnitId,
                                         String clientCategory,
                                         String clientAccountType,
                                         boolean isClientsRequired,
                                         int obsoleteAsinTypeId,
                                         boolean requiresMtd,
                                         boolean requiresProfitability,
                                         String retailerPlatform,
                                         String distributorView,
                                         String reportingMetric,
                                         String calculationType,
                                         int asinCount,
                                         boolean segmentedUnsegmented,
                                         String metricLabel,
                                         String format,
                                         String formatShort)
    {
        this.pagingAttributes = pagingAttributes;
        this.showDataStatus = showDataStatus;
        this.interval = interval;
        this.maxMonthlyDate = maxMonthlyDate;
        this.dateRange = dateRange;
        this.startDate = startDate;
        this.endDate = endDate;
        this.segmentationFiltersExist = segmentationFiltersExist;
        this.isOnlyBusinessUnit = isOnlyBusinessUnit;
        this.businessUnitId = businessUnitId;
        this.clientCategory = clientCategory;
        this.clientAccountType =clientAccountType;
        this.isClientsRequired = isClientsRequired;
        this.obsoleteAsinTypeId = obsoleteAsinTypeId;
        this.requiresMtd = requiresMtd;
        this.requiresProfitability = requiresProfitability;
        this.retailerPlatform = retailerPlatform;
        this.distributorView = distributorView;
        this.reportingMetric = reportingMetric;
        this.calculationType = calculationType;
        this.asinCount = asinCount;
        this.segmentedUnsegmented = segmentedUnsegmented;
        this.metricLabel = metricLabel;
        this.format = format;
        this.formatShort = formatShort;

    }

    public GainersAndDrainersRequestBody(int businessUnitId, String distributorView, String interval, String clientCategory) {

        this.businessUnitId = businessUnitId;
        this.distributorView = distributorView;
        this.interval = interval;
        this.clientCategory = clientCategory;
    }


    public static class DateRange {

        public String label;

        public DateRange(String label) {
            this.label = label;
        }

    }

    public static class PagingAttributes {
        public int pageSize;
        public int page;


        public PagingAttributes(int pageSize, int page) {
            this.pageSize = pageSize;
            this.page = page;
        }
    }
}
