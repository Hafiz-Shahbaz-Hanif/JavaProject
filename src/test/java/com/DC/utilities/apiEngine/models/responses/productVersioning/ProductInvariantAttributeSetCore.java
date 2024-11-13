package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.List;
import java.util.Objects;

public class ProductInvariantAttributeSetCore {

    public Number categoryId;

    public List<ProductInvariantAttribute> attributes;

    public ProductInvariantAttributeSetCore(Number categoryId, List<ProductInvariantAttribute> attributes) {
        this.categoryId = categoryId;
        this.attributes = attributes;
    }

    public ProductInvariantAttributeSetCore() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductInvariantAttributeSetCore)) return false;
        ProductInvariantAttributeSetCore that = (ProductInvariantAttributeSetCore) o;
        return categoryId.equals(that.categoryId) && attributes.equals(that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, attributes);
    }

    @Override
    public String toString() {
        return "{" +
                "categoryId=" + categoryId +
                ", attributes=" + attributes +
                '}';
    }
}
