package com.DC.utilities.apiEngine.models.requests.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.Logger;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoundupRequestBody {

    public PagingAttributes pagingAttributes;
    public int businessUnitId;
    public String interval;
    public String startDate;
    public String endDate;
    public List<SegmentationFilters> segmentationFilters;
    public List<String> segmentationByNameFilters;
    public int obsoleteAsinTypeId;
    public List<String> asinIds;
    public String platform;
    public String comparisonType;
    public String clientCategory;
    public String clientAccountType;
    public boolean isClientsRequired;
    public String fytdStartDate;
    public String lfytdStartDate;
    public String maxMonthlyDate;
    public String priorityAsinSegmentId;
    public boolean isLastWeekOrMonth;
    public boolean requiresProfitability;
    public String title;
    public String obsoleteAsinType;
    public String retailerPlatform;
    public String distributorView;
    public String reportingMetric;
    public String maxWeeklyDate;
    public String maxDailyDate;
    public String exportType;

    public RoundupRequestBody(
            PagingAttributes pagingAttributes,
            int businessUnitId,
            String interval,
            String startDate,
            String endDate,
            List<SegmentationFilters> segmentationFilters,
            List<String> segmentationByNameFilters,
            int obsoleteAsinTypeId,
            List<String> asinIds,
            String platform,
            String comparisonType,
            String clientCategory,
            String clientAccountType,
            boolean isClientsRequired,
            String fytdStartDate,
            String lfytdStartDate,
            String maxMonthlyDate,
            String priorityAsinSegmentId,
            boolean isLastWeekOrMonth,
            boolean requiresProfitability,
            String title,
            String obsoleteAsinType,
            String retailerPlatform,
            String distributorView,
            String reportingMetric,
            String maxWeeklyDate,
            String maxDailyDate
    ) {
        this.pagingAttributes = pagingAttributes;
        this.businessUnitId = businessUnitId;
        this.interval = interval;
        this.startDate = startDate;
        this.endDate = endDate;
        this.segmentationFilters = segmentationFilters;
        this.segmentationByNameFilters = segmentationByNameFilters;
        this.obsoleteAsinTypeId = obsoleteAsinTypeId;
        this.asinIds = asinIds;
        this.platform = platform;
        this.comparisonType = comparisonType;
        this.clientCategory = clientCategory;
        this.clientAccountType = clientAccountType;
        this.isClientsRequired = isClientsRequired;
        this.fytdStartDate = fytdStartDate;
        this.lfytdStartDate = lfytdStartDate;
        this.maxMonthlyDate = maxMonthlyDate;
        this.priorityAsinSegmentId = priorityAsinSegmentId;
        this.isLastWeekOrMonth = isLastWeekOrMonth;
        this.requiresProfitability = requiresProfitability;
        this.title = title;
        this.obsoleteAsinType = obsoleteAsinType;
        this.retailerPlatform = retailerPlatform;
        this.distributorView = distributorView;
        this.reportingMetric = reportingMetric;
        this.maxWeeklyDate = maxWeeklyDate;
        this.maxDailyDate = maxDailyDate;

        Logger.getLogger(RetailScratchpadRequestBody.class).info("** Serializing request body for Retail Scratchpad request body");
    }

    public RoundupRequestBody (
            PagingAttributes pagingAttributes,
            int businessUnitId,
            String interval,
            String startDate,
            String endDate,
            List<SegmentationFilters> segmentationFilters,
            List<String> asinIds,
            String platform,
            String clientCategory,
            String clientAccountType,
            boolean isClientsRequired,
            boolean isLastWeekOrMonth,
            boolean requiresProfitability,
            String obsoleteAsinType,
            String retailerPlatform,
            String distributorView,
            String reportingMetric
    ) {
        this.pagingAttributes = pagingAttributes;
        this.businessUnitId = businessUnitId;
        this.interval = interval;
        this.startDate = startDate;
        this.endDate = endDate;
        this.segmentationFilters = segmentationFilters;
        this.asinIds = asinIds = asinIds;
        this.platform = platform;
        this.clientCategory = clientCategory;
        this.clientAccountType = clientAccountType;
        this.isClientsRequired = isClientsRequired;
        this.isLastWeekOrMonth = isLastWeekOrMonth;
        this.requiresProfitability = requiresProfitability;
        this.obsoleteAsinType = obsoleteAsinType;
        this.retailerPlatform = retailerPlatform;
        this.distributorView = distributorView;
        this.reportingMetric = reportingMetric;

        Logger.getLogger(RetailScratchpadRequestBody.class).info("** Serializing request body for Retail Scratchpad request body");
    }

    public RoundupRequestBody (
            int businessUnitId,
            String interval,
            String startDate,
            String endDate,
            String platform,
            String clientCategory,
            String clientAccountType,
            boolean isClientsRequired,
            boolean isLastWeekOrMonth,
            boolean requiresProfitability,
            String title,
            String retailerPlatform,
            String distributorView,
            String reportingMetric,
            String exportType
    ) {
        this.businessUnitId = businessUnitId;
        this.interval = interval;
        this.startDate = startDate;
        this.endDate = endDate;
        this.platform = platform;
        this.clientCategory = clientCategory;
        this.clientAccountType = clientAccountType;
        this.isClientsRequired = isClientsRequired;
        this.isLastWeekOrMonth = isLastWeekOrMonth;
        this.requiresProfitability = requiresProfitability;
        this.title = title;
        this.retailerPlatform = retailerPlatform;
        this.distributorView = distributorView;
        this.reportingMetric = reportingMetric;
        this.exportType = exportType;

        Logger.getLogger(RetailScratchpadRequestBody.class).info("** Serializing request body for Retail Scratchpad request body");
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PagingAttributes {
        public int pageSize;
        public int page;
        public String orderAttribute;
        public boolean orderAscending;


        public PagingAttributes(int pageSize, int page, String orderAttribute, boolean orderAscending) {
            this.pageSize = pageSize;
            this.page = page;
            this.orderAttribute = orderAttribute;
            this.orderAscending = orderAscending;
        }

        public PagingAttributes() {

        }
    }

    public static class SegmentationFilters {
        public int segmentationTypeId;
        public List<Integer> segmentationValues;

        public SegmentationFilters(int segmentationTypeId, List<Integer> segmentationValues) {
            this.segmentationTypeId = segmentationTypeId;
            this.segmentationValues = segmentationValues;
        }
    }

}