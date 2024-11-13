package com.DC.utilities.apiEngine.models.requests.hub.aggregation;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HubOrganizationAggregationRequestBody {

    public Object legacyPlatformOrganizationId;
    public String organizationId;
    public List<String> organizationIds;
    public String legacyPlatformName;

    public HubOrganizationAggregationRequestBody(Object legacyPlatformOrganizationId, String organizationId, String legacyPlatformName){
        this.legacyPlatformOrganizationId = legacyPlatformOrganizationId;
        this.organizationId = organizationId;
        this.legacyPlatformName = legacyPlatformName;
    }

    public HubOrganizationAggregationRequestBody(List<String> organizationIds){
        this.organizationIds = organizationIds;
    }

    public Object getLegacyPlatformOrganizationId() {
        return legacyPlatformOrganizationId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getLegacyPlatformName() {
        return legacyPlatformName;
    }
}