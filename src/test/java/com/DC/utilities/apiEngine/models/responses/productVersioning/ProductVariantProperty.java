package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.List;
import java.util.Objects;

public class ProductVariantProperty {

    public String id;

    public List<Object> values;

    public ProductVariantProperty(String id, List<Object> values) {
        this.id = id;
        this.values = values;
    }

    public ProductVariantProperty() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariantProperty)) return false;
        ProductVariantProperty property = (ProductVariantProperty) o;
        return id.equals(property.id) && values.equals(property.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, values);
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", values=" + values +
                '}';
    }
}
