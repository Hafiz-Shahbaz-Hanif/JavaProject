package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantKeywords;

public class ProductVariantKeywordSetRequestBody extends ProductVariantRequestBodyBase {

    public ProductVariantKeywords keywords;

    public ProductVariantKeywordSetRequestBody(
            String level,
            String localeId,
            String retailerId,
            String campaignId,
            ProductVariantKeywords keywords) {
        super(level, localeId, retailerId, campaignId);
        this.keywords = keywords;
    }

    public ProductVariantKeywordSetRequestBody() {}
}
