package com.DC.utilities.apiEngine.models.requests.adc.catalog.retail;

public class AsinSegmentationRequestbody {

    public PagingAttributes pagingAttributes;
    public int businessUnitId;
    public String clientCategory;
    public boolean isClientsRequired;
    public boolean initialLoad;
    public int obsoleteAsinTypeId;
    public boolean requiresProfitability;

    public AsinSegmentationRequestbody(PagingAttributes pagingAttributes,
                                       int businessUnitId,
                                       String clientCategory,
                                       boolean isClientsRequired,
                                       boolean initialLoad,
                                       int obsoleteAsinTypeId)
    {
        this.pagingAttributes = pagingAttributes;
        this.businessUnitId = businessUnitId;
        this.clientCategory = clientCategory;
        this.isClientsRequired = isClientsRequired;
        this.initialLoad = initialLoad;
        this.obsoleteAsinTypeId = obsoleteAsinTypeId;
    }

    public AsinSegmentationRequestbody(PagingAttributes pagingAttributes,
                                       int businessUnitId,
                                       String clientCategory,
                                       boolean isClientsRequired,
                                       boolean initialLoad,
                                       int obsoleteAsinTypeId,
                                       boolean requiresProfitability)
    {
        this.pagingAttributes = pagingAttributes;
        this.businessUnitId = businessUnitId;
        this.clientCategory = clientCategory;
        this.isClientsRequired = isClientsRequired;
        this.initialLoad = initialLoad;
        this.obsoleteAsinTypeId = obsoleteAsinTypeId;
        this.requiresProfitability = requiresProfitability;
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
