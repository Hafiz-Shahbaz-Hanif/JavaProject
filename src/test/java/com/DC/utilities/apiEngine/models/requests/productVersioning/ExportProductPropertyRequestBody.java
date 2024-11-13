package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantInstancePath;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

public class ExportProductPropertyRequestBody {

    public List<ProductVariantInstancePath> products;

    public List<String> propertyIds;

    public List<String> digitalAssetIds;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String type;

    public ExportProductPropertyRequestBody(List<ProductVariantInstancePath> products, List<String> propertyIds) {
        this.products = products;
        this.propertyIds = propertyIds;
    }

    public ExportProductPropertyRequestBody(List<ProductVariantInstancePath> products, List<String> propertyIds, List<String> digitalAssetIds) {
        this.products = products;
        this.propertyIds = propertyIds;
        this.digitalAssetIds = digitalAssetIds;
    }

    public ExportProductPropertyRequestBody(List<ProductVariantInstancePath> products, List<String> propertyIds, List<String> digitalAssetIds, String type) {
        this.products = products;
        this.propertyIds = propertyIds;
        this.digitalAssetIds = digitalAssetIds;
        this.type = type;
    }

    public ExportProductPropertyRequestBody(List<ProductVariantInstancePath> products) {
        this.products = products;
    }

    public ExportProductPropertyRequestBody() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExportProductPropertyRequestBody)) return false;
        ExportProductPropertyRequestBody that = (ExportProductPropertyRequestBody) o;
        return Objects.equals(products, that.products)
                && Objects.equals(propertyIds, that.propertyIds)
                && Objects.equals(digitalAssetIds, that.digitalAssetIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(products, propertyIds, digitalAssetIds);
    }

    @Override
    public String toString() {
        return "{" +
                "products=" + products +
                ", propertyIds=" + propertyIds +
                ", digitalAssetIds=" + digitalAssetIds +
                '}';
    }
}
