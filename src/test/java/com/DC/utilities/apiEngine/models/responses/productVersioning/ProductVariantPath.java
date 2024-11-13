package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Objects;

public class ProductVariantPath {

    public String productMasterId;

    public String localeId;

    public String systemRetailerId;

    public String campaignId;

    public ProductVariantPath(String productMasterId, String localeId, String systemRetailerId, String campaignId) {
        this.productMasterId = productMasterId;
        this.localeId = localeId;
        this.systemRetailerId = systemRetailerId;
        this.campaignId = campaignId;
    }

    public ProductVariantPath() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariantPath that = (ProductVariantPath) o;
        return Objects.equals(productMasterId, that.productMasterId)
                && Objects.equals(localeId, that.localeId)
                && Objects.equals(systemRetailerId, that.systemRetailerId)
                && Objects.equals(campaignId, that.campaignId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productMasterId, localeId, systemRetailerId, campaignId);
    }

    @Override
    public String toString() {
        return "{" +
                "productMasterId='" + productMasterId + '\'' +
                ", localeId='" + localeId + '\'' +
                ", systemRetailerId='" + systemRetailerId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                '}';
    }
}
