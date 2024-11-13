package com.DC.utilities.apiEngine.models.responses.hub.aggregation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RetailerPlatform {

    @JsonProperty("currencyCode")
    public String currencyCode;
    @JsonProperty("region")
    public String region;
    @JsonProperty("flywheelBusinessUnitIds")
    public List<Integer> flywheelBusinessUnitIds;
    @JsonProperty("currencySymbol")
    public String currencySymbol;
    @JsonProperty("mediaOffsiteEnabled")
    public boolean mediaOffsiteEnabled;
    @JsonProperty("domain")
    public String domain;
    @JsonProperty("adPlatforms")
    public List<AdPlatforms> adPlatforms;
    @JsonProperty("retailerPlatformId")
    public String retailerPlatformId;
    @JsonProperty("marketShareIds")
    public List<Integer> marketShareIds;
    @JsonProperty("mediaOnsiteEnabled")
    public boolean mediaOnsiteEnabled;
    @JsonProperty("retailEnabled")
    public boolean retailEnabled;
    @JsonProperty("flywheelContentStudioCompanyIds")
    public List<Object> flywheelContentStudioCompanyIds;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getRegion() {
        return region;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public boolean getMediaOffsiteEnabled() {
        return mediaOffsiteEnabled;
    }

    public String getDomain() {
        return domain;
    }

    public List<AdPlatforms> getAdPlatforms() {
        return adPlatforms;
    }

    public String getRetailerPlatformId() {
        return retailerPlatformId;
    }

    public boolean getMediaOnsiteEnabled() {
        return mediaOnsiteEnabled;
    }

    public boolean getRetailEnabled() {
        return retailEnabled;
    }

    public List<Integer> getFlywheelBusinessUnitId(){
        return flywheelBusinessUnitIds;
    }

    public List<Integer> getMarketShareId() {
        return marketShareIds;
    }

    public List<Object> getFlywheelContentStudioCompanyId() {
        return flywheelContentStudioCompanyIds;
    }

    public static class AdPlatforms {
        @JsonProperty("adPlatform")
        public String adPlatform;

        @JsonProperty("adPlatformId")
        public String adPlatformId;

    }


}
