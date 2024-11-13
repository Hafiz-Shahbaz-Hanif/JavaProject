package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantProperty;

import java.util.List;

public class ProductVariantPropertySetRequestBody extends ProductVariantRequestBodyBase {
    public List<ProductVariantProperty> properties;

    public ProductVariantPropertySetRequestBody(String level, String localeId, String retailerId, String campaignId, List<ProductVariantProperty> properties) {
        super(level, localeId, retailerId, campaignId);
        this.properties = properties;
    }

    public ProductVariantPropertySetRequestBody() {}
}
