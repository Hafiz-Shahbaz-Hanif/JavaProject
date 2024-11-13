package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.List;
import java.util.Objects;

public class ProductMasterInfo extends InstancePathBase {

    public String productMasterUniqueId;

    public String liveVariantId;

    public String liveInstanceId;

    public ProductMasterInfo() {}

    public ProductMasterInfo(String productMasterId, String productMasterUniqueId, String localeId, String retailerId, String campaignId, String liveVariantId, String liveInstanceId) {
        super(productMasterId, localeId, retailerId, campaignId);
        this.productMasterUniqueId = productMasterUniqueId;
        this.liveVariantId = liveVariantId;
        this.liveInstanceId = liveInstanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMasterInfo)) return false;
        if (!super.equals(o)) return false;
        ProductMasterInfo that = (ProductMasterInfo) o;
        return productMasterUniqueId.equals(that.productMasterUniqueId) &&
                liveVariantId.equals(that.liveVariantId) &&
                liveInstanceId.equals(that.liveInstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), productMasterUniqueId, liveVariantId, liveInstanceId);
    }

    @Override
    public String toString() {
        return "{" +
                "productMasterId='" + productMasterId + '\'' +
                ", productMasterUniqueId='" + productMasterUniqueId + '\'' +
                ", localeId='" + localeId + '\'' +
                ", retailerId='" + retailerId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                ", liveVariantId='" + liveVariantId + '\'' +
                ", liveInstanceId='" + liveInstanceId + '\'' +
                '}';
    }
}
