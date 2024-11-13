package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.enums.Enums;

import java.util.Objects;

public class ProductVariantInstancePath extends InstancePathBase {

    public Enums.ProductVariantType type;

    public ProductVariantInstancePath(String productMasterId, String localeId, Enums.ProductVariantType type, String retailerId, String campaignId) {
        super(productMasterId, localeId, retailerId, campaignId);
        this.type = type;
    }

    public ProductVariantInstancePath() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariantInstancePath that = (ProductVariantInstancePath) o;
        return Objects.equals(productMasterId, that.productMasterId)
                && Objects.equals(localeId, that.localeId)
                && Objects.equals(type, that.type)
                && Objects.equals(retailerId, that.retailerId)
                && Objects.equals(campaignId, that.campaignId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productMasterId, localeId, type, retailerId, campaignId);
    }

    @Override
    public String toString() {
        return "{" +
                "productMasterId='" + productMasterId + '\'' +
                ", localeId='" + localeId + '\'' +
                ", type='" + type + '\'' +
                ", retailerId='" + retailerId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                '}';
    }
}
