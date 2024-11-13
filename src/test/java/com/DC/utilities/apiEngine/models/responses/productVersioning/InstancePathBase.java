package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class InstancePathBase {

    public String productMasterId;

    public String localeId;

    public String retailerId;

    public String campaignId;

    public InstancePathBase(String productMasterId, String localeId, String retailerId, String campaignId) {
        this.productMasterId = productMasterId;
        this.localeId = localeId;
        this.retailerId = retailerId;
        this.campaignId = campaignId;
    }

    public InstancePathBase() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstancePathBase)) return false;
        InstancePathBase that = (InstancePathBase) o;
        return productMasterId.equals(that.productMasterId) &&
                localeId.equals(that.localeId) &&
                Objects.equals(retailerId, that.retailerId) &&
                Objects.equals(campaignId, that.campaignId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productMasterId, localeId, retailerId, campaignId);
    }

    @Override
    public String toString() {
        return "{" +
                "productMasterId='" + productMasterId + '\'' +
                ", localeId='" + localeId + '\'' +
                ", retailerId='" + retailerId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                '}';
    }

    @JsonIgnore
    public com.DC.utilities.enums.Enums.ProductVariantLevel getProductLevel() {
        if (isBlank(this.retailerId) && isBlank(this.campaignId)) {
            return Enums.ProductVariantLevel.GLOBAL;
        }
        if (!isBlank(this.retailerId) && isBlank(this.campaignId)) {
            return Enums.ProductVariantLevel.RETAILER;
        }
        if (!isBlank(this.retailerId) && !isBlank(this.campaignId)) {
            return Enums.ProductVariantLevel.RETAILER_CAMPAIGN;
        }
        if (isBlank(this.retailerId) && !isBlank(this.campaignId)) {
            return Enums.ProductVariantLevel.GLOBAL_CAMPAIGN;
        }
        return null;
    }
}
