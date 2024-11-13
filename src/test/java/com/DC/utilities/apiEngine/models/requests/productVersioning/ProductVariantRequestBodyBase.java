package com.DC.utilities.apiEngine.models.requests.productVersioning;

public abstract class ProductVariantRequestBodyBase {
    public String level;
    public String localeId;
    public String retailerId;
    public String campaignId;

    public ProductVariantRequestBodyBase(String level, String localeId, String retailerId, String campaignId) {
        this.level = level;
        this.localeId = localeId;
        this.retailerId = retailerId;
        this.campaignId = campaignId;
    }

    public ProductVariantRequestBodyBase() {}
}
