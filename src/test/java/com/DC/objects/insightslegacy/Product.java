package com.DC.objects.insightslegacy;

public class Product {

    public Product() {}

    public ProductDetails product;

    @Override
    public String toString() {
        return "{" +
                "productDetails:" + product +
                '}';
    }
}
