package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Objects;

public class ProductDataSetMeta {

    public String sourceSetId;

    public ProductDataSetMeta() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDataSetMeta that = (ProductDataSetMeta) o;
        return Objects.equals(sourceSetId, that.sourceSetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceSetId);
    }

    @Override
    public String toString() {
        return "{" +
                "sourceSetId='" + sourceSetId + '\'' +
                '}';
    }
}
