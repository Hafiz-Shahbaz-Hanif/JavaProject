package com.DC.utilities.apiEngine.models.requests.adc.catalog.retail;

import java.util.List;

public class SalesCorrelationRequestBody {

    public String interval;
    public String comparisonType;
    public String clientCategory;
    public String clientAccountType;
    public boolean isClientsRequired;
    public String startDate;
    public String endDate;
    public List<String> segmentationFilters;
    public int obsoleteAsinTypeId;
    public boolean requiresProfitability;
    public PagingAttributes pagingAttributes;
    public boolean ShowCogs;
    public List<String> asinIds;
    public String distributorView;
    public String segmentedSales;
    public String maxMonthlyDate;
    public String format;
    public String formatShort;
    public boolean isLastWeekOrMonth;
    public  List<String> clientAccountIds;
    public String retailerPlatform;
    public int businessUnitId;
    public int priorityAsinSegmentId;

    public SalesCorrelationRequestBody(String interval,
                                String comparisonType,
                                String clientCategory,
                                String clientAccountType,
                                boolean isClientsRequired,
                                String startDate,
                                String endDate,
                                int obsoleteAsinTypeId,
                                boolean requiresProfitability,
                                String distributorView,
                                String segmentedSales,
                                String format,
                                String formatShort,
                                boolean isLastWeekOrMonth,
                                String retailerPlatform,
                                int businessUnitId,
                                int priorityAsinSegmentId)
    {
        this.interval = interval;
        this.comparisonType = comparisonType;
        this.clientCategory = clientCategory;
        this.clientAccountType = clientAccountType;
        this.isClientsRequired = isClientsRequired;
        this.startDate = startDate;
        this.endDate = endDate;
        this.obsoleteAsinTypeId = obsoleteAsinTypeId;
        this.requiresProfitability = requiresProfitability;
        this.distributorView = distributorView;
        this.segmentedSales = segmentedSales;
        this.format = format;
        this.formatShort = formatShort;
        this.isLastWeekOrMonth = isLastWeekOrMonth;
        this.retailerPlatform = retailerPlatform;
        this.businessUnitId = businessUnitId;
        this.priorityAsinSegmentId = priorityAsinSegmentId;

    }

    public SalesCorrelationRequestBody(boolean requiresProfitability,
                                       PagingAttributes pagingAttributes,
                                       boolean ShowCogs,
                                       int businessUnitId,
                                       String clientCategory,
                                       String clientAccountType,
                                       boolean isClientsRequired,
                                       int obsoleteAsinTypeId,
                                       int priorityAsinSegmentId,
                                       String distributorView,
                                       String startDate,
                                       String endDate,
                                       String interval,
                                       String retailerPlatform,
                                       String comparisonType)
    {
        this.requiresProfitability = requiresProfitability;
        this.pagingAttributes = pagingAttributes;
        this.ShowCogs = ShowCogs;
        this.businessUnitId = businessUnitId;
        this.clientCategory = clientCategory;
        this.clientAccountType = clientAccountType;
        this.isClientsRequired = isClientsRequired;
        this.obsoleteAsinTypeId = obsoleteAsinTypeId;
        this.priorityAsinSegmentId = priorityAsinSegmentId;
        this.distributorView = distributorView;
        this.startDate = startDate;
        this.endDate = endDate;
        this.interval = interval;
        this.retailerPlatform = retailerPlatform;
        this.comparisonType = comparisonType;
    }

    public static class PagingAttributes {
        public int pageSize;
        public int page;
        public String orderAttribute;


        public PagingAttributes(int pageSize, int page, String orderAttribute) {
            this.pageSize = pageSize;
            this.page = page;
            this.orderAttribute = orderAttribute;
        }
    }


}
