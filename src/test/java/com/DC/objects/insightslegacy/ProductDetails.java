package com.DC.objects.insightslegacy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDetails {
    public ProductDetails() {
    }

    public ProductProperties[] properties;
    public ProductProperties[] stagedChanges;

    @Override
    public String toString() {
        return "{" +
                "properties:" + properties +
                ", stagedChanges:" + stagedChanges +
                '}';
    }
}
