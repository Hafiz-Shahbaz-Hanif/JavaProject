package com.DC.utilities.apiEngine.apiRequests.adc.analyze.caseManagementReporting;

import java.util.Collections;
import java.util.List;

public class CaseManagementReportingRequestBody {
    public String startDate;
    public String endDate;
    public PagingAttributes pagingAttributes;
    public List<String> segmentationFilters;
    public String interval;
    public List<Integer> businessUnitId;
    public String search;
    public List<String> asinIds;
    public List<Integer> clientAccountIds;
    public String obsoleteAsinTypeId;
    public String graphType;

    public CaseManagementReportingRequestBody(String startDate, String endDate, PagingAttributes pagingAttributes, List<String> segmentationFilters, String interval,
                                              int businessUnitId, String search, List<String> asinIds, Integer[] clientAccountIds) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.pagingAttributes = pagingAttributes;
        this.segmentationFilters = segmentationFilters;
        this.interval = interval;
        this.businessUnitId = Collections.singletonList(businessUnitId);
        this.search = search;
        this.asinIds = asinIds;
        this.clientAccountIds = List.of(clientAccountIds);
    }

    public CaseManagementReportingRequestBody(int businessUnitId, String interval, String graphType, String startDate, String endDate, List<String> segmentationFilters, String obsoleteAsinTypeId, List<String> asinIds, Integer[] clientAccountIds) {
        this.businessUnitId = Collections.singletonList(businessUnitId);
        this.interval = interval;
        this.graphType = graphType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.segmentationFilters = segmentationFilters;
        this.obsoleteAsinTypeId = obsoleteAsinTypeId;
        this.asinIds = asinIds;
        this.clientAccountIds = List.of(clientAccountIds);
    }

    public CaseManagementReportingRequestBody(String startDate, String endDate, List<String> segmentationFilters, String interval, int businessUnitId, List<String> asinIds, String obsoleteAsinTypeId, Integer[] clientAccountIds) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.pagingAttributes = new PagingAttributes(1, 100, 0, false, "createdOn");
        this.segmentationFilters = segmentationFilters;
        this.interval = interval;
        this.businessUnitId = Collections.singletonList(businessUnitId);
        this.asinIds = asinIds;
        this.obsoleteAsinTypeId = obsoleteAsinTypeId;
        this.clientAccountIds = List.of(clientAccountIds);
    }

    public static class PagingAttributes {
        public int page;
        public int pageSize;
        public int totalRecords;
        public boolean orderAscending;
        public String orderAttribute;

        public PagingAttributes(int page, int pageSize, int totalRecords, boolean orderAscending, String orderAttribute) {
            this.page = page;
            this.pageSize = pageSize;
            this.totalRecords = totalRecords;
            this.orderAscending = orderAscending;
            this.orderAttribute = orderAttribute;
        }
    }
}

