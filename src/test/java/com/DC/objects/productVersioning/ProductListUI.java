package com.DC.objects.productVersioning;

import com.DC.utilities.enums.Enums;

import java.util.Objects;

public class ProductListUI {
    public String name;
    public int numberOfProducts;
    public String ownerName;
    public Enums.ProductListPermission permission;
    public String lastActivity;

    public ProductListUI(String name, int numberOfProducts, String ownerName, Enums.ProductListPermission permission, String lastActivity) {
        this.name = name;
        this.numberOfProducts = numberOfProducts;
        this.ownerName = ownerName;
        this.permission = permission;
        this.lastActivity = lastActivity;
    }

    public ProductListUI() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductListUI)) return false;
        ProductListUI that = (ProductListUI) o;
        return numberOfProducts == that.numberOfProducts && name.equals(that.name) && ownerName.equals(that.ownerName) && permission == that.permission && lastActivity.equals(that.lastActivity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, numberOfProducts, ownerName, permission, lastActivity);
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", numberOfProducts=" + numberOfProducts +
                ", ownerName='" + ownerName + '\'' +
                ", permission=" + permission +
                ", lastActivity='" + lastActivity + '\'' +
                '}';
    }
}
