package com.DC.db.hubDbFunctions;

public class UserBuAggregation {

    private String platformName;
    private Object buAggregation;
    private String currencyCode;
    private String region;
    private String currencySymbol;
    private boolean mediaOffsiteEnabled;
    private String domain;
    private String retailerPlatformId;
    private boolean mediaOnsiteEnabled;
    private boolean retailEnabled;

    public UserBuAggregation(String platformName, Object buAggregation, String currencyCode, String region, String currencySymbol, boolean mediaOffsiteEnabled, String domain, String retailerPlatformId, boolean mediaOnsiteEnabled, boolean retailEnabled) {
        this.platformName = platformName;
        this.buAggregation = buAggregation;
        this.currencyCode = currencyCode;
        this.region = region;
        this.currencySymbol = currencySymbol;
        this.mediaOffsiteEnabled = mediaOffsiteEnabled;
        this.domain = domain;
        this.retailerPlatformId = retailerPlatformId;
        this.mediaOnsiteEnabled = mediaOnsiteEnabled;
        this.retailEnabled = retailEnabled;
    }

    public String getPlatformName() {
        return platformName;
    }

    public Object getBuAggregation() {
        return buAggregation;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getRegion() {
        return region;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public boolean isMediaOffsiteEnabled() {
        return mediaOffsiteEnabled;
    }

    public String getDomain() {
        return domain;
    }

    public String getRetailerPlatformId() {
        return retailerPlatformId;
    }

    public boolean isMediaOnsiteEnabled() {
        return mediaOnsiteEnabled;
    }

    public boolean isRetailEnabled() {
        return retailEnabled;
    }
}
