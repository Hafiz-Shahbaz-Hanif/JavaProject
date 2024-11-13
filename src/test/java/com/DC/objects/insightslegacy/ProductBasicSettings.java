package com.DC.objects.insightslegacy;

import java.util.Objects;

public class ProductBasicSettings {

    public String uniqueID;
    public String name;
    public String productLevel;

    public ProductBasicSettings(String uniqueID, String name, String productLevel) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.productLevel = productLevel;
    }

    public ProductBasicSettings() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductBasicSettings)) return false;
        ProductBasicSettings that = (ProductBasicSettings) o;
        return uniqueID == that.uniqueID &&
                name == that.name &&
                productLevel == that.productLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueID, name, productLevel);
    }

    @Override
    public String toString() {
        return "ProductBasicSettings{" +
                ", uniqueID='" + uniqueID + '\'' +
                ", name='" + name + '\'' +
                ", productLevel='" + productLevel + '\'' +
                '}';
    }
}
