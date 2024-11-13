package com.DC.objects.productVersioning;

import java.util.Objects;

public class ProductInstanceUIData {
    public String productIdentifier;

    public String localeName;

    public String version;

    public ProductInstanceUIData(String productIdentifier, String localeName, String version) {
        this.productIdentifier = productIdentifier;
        this.localeName = localeName;
        this.version = version;
    }

    @Override
    public String toString() {
        return "{" +
                "productIdentifier='" + productIdentifier + '\'' +
                ", localeName='" + localeName + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductInstanceUIData)) return false;
        ProductInstanceUIData that = (ProductInstanceUIData) o;
        return Objects.equals(productIdentifier, that.productIdentifier) && Objects.equals(localeName, that.localeName) && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productIdentifier, localeName, version);
    }
}
