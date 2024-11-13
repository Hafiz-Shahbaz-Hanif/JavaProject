package com.DC.utilities.apiEngine.models.responses.insights;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DVAData {

    @JsonProperty("All Retailers")
    public AuditResult[] allRetailers;

    @JsonProperty("Amazon")
    public AuditResult[] amazon;
}
