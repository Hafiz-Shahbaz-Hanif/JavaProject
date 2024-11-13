package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.enums.Enums;

import java.util.Objects;

public abstract class ProductInvariantDataSetBase {

    public Enums.ProductVariantType type;

    public String companyId;

    public String productMasterId;

    public String locale;

    public ProductDataSetMeta meta;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductInvariantDataSetBase that = (ProductInvariantDataSetBase) o;
        return type == that.type && companyId.equals(that.companyId) && productMasterId.equals(that.productMasterId) && locale.equals(that.locale) && meta.equals(that.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, companyId, productMasterId, locale, meta);
    }

    @Override
    public String toString() {
        return "{" +
                "type=" + type +
                ", companyId='" + companyId + '\'' +
                ", productMasterId='" + productMasterId + '\'' +
                ", locale='" + locale + '\'' +
                ", meta=" + meta +
                '}';
    }
}
