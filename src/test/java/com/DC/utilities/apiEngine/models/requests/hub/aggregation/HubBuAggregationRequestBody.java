package com.DC.utilities.apiEngine.models.requests.hub.aggregation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HubBuAggregationRequestBody {

    public Object legacyPlatformBusinessUnitId;
    public String businessUnitId;
    public List<String> businessUnitIds;
    public String retailerPlatformId;
    public String legacyPlatformName;

    public HubBuAggregationRequestBody(Object legacyPlatformBusinessUnitId, String businessUnitId, String retailerPlatformId, String legacyPlatformName){
        this.legacyPlatformBusinessUnitId = legacyPlatformBusinessUnitId;
        this.businessUnitId = businessUnitId;
        this.retailerPlatformId = retailerPlatformId;
        this.legacyPlatformName = legacyPlatformName;
    }

    public HubBuAggregationRequestBody(List<String> businessUnitIds){
        this.businessUnitIds = businessUnitIds;
    }

    public Object getLegacyPlatformBusinessUnitId() {
        return legacyPlatformBusinessUnitId;
    }

    public String getBusinessUnitId() {
        return businessUnitId;
    }

    public List<String> getBusinessUnitIds() {
        return businessUnitIds;
    }

    public String getRetailerPlatformId() {
        return retailerPlatformId;
    }

    public String getLegacyPlatformName() {
        return legacyPlatformName;
    }
}