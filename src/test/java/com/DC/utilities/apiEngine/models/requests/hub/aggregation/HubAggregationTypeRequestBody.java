package com.DC.utilities.apiEngine.models.requests.hub.aggregation;

public class HubAggregationTypeRequestBody {

    public String legacyPlatformId;
    public String name;
    public String slug;

    public HubAggregationTypeRequestBody(String legacyPlatformId, String name, String slug){
        this.legacyPlatformId = legacyPlatformId;
        this.name = name;
        this.slug = slug;
    }
}