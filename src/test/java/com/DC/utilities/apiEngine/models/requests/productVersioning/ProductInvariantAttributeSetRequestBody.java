package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductInvariantAttribute;

import java.util.List;

public class ProductInvariantAttributeSetRequestBody {

    public String localeId;

    public Number categoryId;

    public List<ProductInvariantAttribute> attributes;

    public ProductInvariantAttributeSetRequestBody(String localeId, Number categoryId, List<ProductInvariantAttribute> attributes) {
        this.localeId = localeId;
        this.categoryId = categoryId;
        this.attributes = attributes;
    }

    public ProductInvariantAttributeSetRequestBody() {}
}
