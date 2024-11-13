package com.DC.objects.insightslegacy;

import java.util.Objects;

public class LaunchFileAssociatedProducts {

    public String productName;
    public String uniqueID;
    public String ancestorUniqueID;
    public String productLevel;

    public LaunchFileAssociatedProducts(String productName, String uniqueID, String ancestorUniqueID, String productLevel) {
        this.productName = productName;
        this.uniqueID = uniqueID;
        this.ancestorUniqueID = ancestorUniqueID;
        this.productLevel = productLevel;
    }

    public LaunchFileAssociatedProducts() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LaunchFileAssociatedProducts)) return false;
        LaunchFileAssociatedProducts that = (LaunchFileAssociatedProducts) o;
        return productName == that.productName &&
                uniqueID == that.uniqueID &&
                ancestorUniqueID == that.ancestorUniqueID &&
                productLevel == that.productLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName, uniqueID, ancestorUniqueID, productLevel);
    }

    @Override
    public String toString() {
        return "LaunchFileAssociatedProducts{" +
                ", productName='" + productName + '\'' +
                ", uniqueID='" + uniqueID + '\'' +
                ", ancestorUniqueID='" + ancestorUniqueID + '\'' +
                ", productLevel='" + productLevel + '\'' +
                '}';
    }
}
