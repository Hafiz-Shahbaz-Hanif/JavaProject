package com.DC.utilities.apiEngine.models.requests.adc.advertisig.media;

import java.util.List;

public class FlightdeckRequestBody {

    public PagingAttributes pagingAttributes;
    public String viewing;
    public DateRange dateRange;
    public int businessUnitId;
    public String platform;
    public List<String> metricFilters;
    public int userId;
    public List<String> saleMetrics;
    public String attribution;
    public String startDate;
    public String endDate;
    public List<String> segmentationFilters;

    public FlightdeckRequestBody(PagingAttributes pagingAttributes, String viewing, DateRange dateRange, int businessUnitId, String startDate, String endDate, String platform, List<String> metricFilters, int userId) {
        this.pagingAttributes = pagingAttributes;
        this.viewing = viewing;
        this.dateRange = dateRange;
        this.businessUnitId = businessUnitId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.platform = platform;
        this.metricFilters = metricFilters;
        this.userId = userId;
    }

    public FlightdeckRequestBody(PagingAttributes pagingAttributes, String viewing, DateRange dateRange, List<String> saleMetrics, int businessUnitId, String startDate, String endDate, List<String> metricFilters, String platform, String attribution) {
        this.pagingAttributes = pagingAttributes;
        this.viewing = viewing;
        this.dateRange = dateRange;
        this.saleMetrics = saleMetrics;
        this.businessUnitId = businessUnitId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.metricFilters = metricFilters;
        this.platform = platform;
        this.attribution = attribution;
    }

    public FlightdeckRequestBody(PagingAttributes pagingAttributes, String viewing, DateRange dateRange, int businessUnitId, String startDate, String endDate, List<String> metricFilters, String platform, int userId) {
        this.pagingAttributes = pagingAttributes;
        this.viewing = viewing;
        this.dateRange = dateRange;
        this.businessUnitId = businessUnitId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.metricFilters = metricFilters;
        this.platform = platform;
        this.userId = userId;
    }

    public FlightdeckRequestBody(PagingAttributes pagingAttributes, String viewing, DateRange dateRange, int businessUnitId, String startDate, String endDate, String platform, int userId, List<String> segmentationFilters) {
        this.pagingAttributes = pagingAttributes;
        this.viewing = viewing;
        this.dateRange = dateRange;
        this.businessUnitId = businessUnitId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.platform = platform;
        this.userId = userId;
        this.segmentationFilters = segmentationFilters;

    }

    public FlightdeckRequestBody(PagingAttributes pagingAttributes, String viewing, DateRange dateRange, int businessUnitId, String startDate, String endDate, String platform) {
        this.pagingAttributes = pagingAttributes;
        this.viewing = viewing;
        this.dateRange = dateRange;
        this.businessUnitId = businessUnitId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.platform = platform;

    }

    public FlightdeckRequestBody(PagingAttributes pagingAttributes, String viewing, DateRange dateRange, int businessUnitId, String startDate, String endDate, List<String> metricFilters, String platform) {
        this.pagingAttributes = pagingAttributes;
        this.viewing = viewing;
        this.dateRange = dateRange;
        this.businessUnitId = businessUnitId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.metricFilters = metricFilters;
        this.platform = platform;

    }

    public static class PagingAttributes {
        public int pageSize;
        public int page;
        public boolean orderAscending;
        public String orderAttribute;

        public PagingAttributes(int pageSize, int page) {
            this.pageSize = pageSize;
            this.page = page;
        }

        public PagingAttributes(int pageSize, int page, boolean orderAscending, String orderAttribute) {
            this.pageSize = pageSize;
            this.page = page;
            this.orderAscending = orderAscending;
            this.orderAttribute = orderAttribute;
        }
    }

    public static class DateRange {
        public String label;

        public DateRange(String label) {
            this.label = label;
        }
    }

}