package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class ProductVariantListProduct extends InstancePathBase {

    public String productMasterUniqueId;

    public String instanceId;

    public ProductVariantListProduct(String productMasterId, String localeId, String retailerId, String campaignId, String productMasterUniqueId, String instanceId) {
        super(productMasterId, localeId, retailerId, campaignId);
        this.productMasterUniqueId = productMasterUniqueId;
        this.instanceId = instanceId;
    }

    public ProductVariantListProduct(InstancePathBase instancePath, String productMasterUniqueId, String instanceId) {
        super(instancePath.productMasterId, instancePath.localeId, instancePath.retailerId, instancePath.campaignId);
        this.productMasterUniqueId = productMasterUniqueId;
        this.instanceId = instanceId;
    }

    public ProductVariantListProduct() {}

    @JsonIgnore
    public InstancePathBase getInstancePathBase() {
        return new InstancePathBase(this.productMasterId, this.localeId, this.retailerId, this.campaignId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariantListProduct)) return false;
        if (!super.equals(o)) return false;
        ProductVariantListProduct that = (ProductVariantListProduct) o;
        return productMasterUniqueId.equals(that.productMasterUniqueId) && instanceId.equals(that.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), productMasterUniqueId, instanceId);
    }

    @Override
    public String toString() {
        return "{" +
                "productMasterUniqueId='" + productMasterUniqueId + '\'' +
                ", productMasterId='" + productMasterId + '\'' +
                ", localeId='" + localeId + '\'' +
                ", retailerId='" + retailerId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}
