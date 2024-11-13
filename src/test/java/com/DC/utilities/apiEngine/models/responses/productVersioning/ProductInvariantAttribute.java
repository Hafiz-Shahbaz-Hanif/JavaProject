package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Objects;

public class ProductInvariantAttribute {

    public Number segmentId;

    public Number segmentValueId;

    public ProductInvariantAttribute(Number segmentId, Number segmentValueId) {
        this.segmentId = segmentId;
        this.segmentValueId = segmentValueId;
    }

    public ProductInvariantAttribute() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductInvariantAttribute)) return false;
        ProductInvariantAttribute that = (ProductInvariantAttribute) o;
        return segmentId.equals(that.segmentId) && segmentValueId.equals(that.segmentValueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segmentId, segmentValueId);
    }

    @Override
    public String toString() {
        return "{" +
                "segmentId=" + segmentId +
                ", segmentValueId=" + segmentValueId +
                '}';
    }
}
