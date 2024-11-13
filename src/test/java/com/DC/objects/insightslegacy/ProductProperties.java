package com.DC.objects.insightslegacy;

import java.util.Objects;

public class ProductProperties {
    public ProductProperties() {
    }

    public String id;
    public String[] values;

    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", values:" + values +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductProperties)) return false;
        ProductProperties that = (ProductProperties) o;
        return id.equals(that.id) &&
                values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, values);
    }
}
