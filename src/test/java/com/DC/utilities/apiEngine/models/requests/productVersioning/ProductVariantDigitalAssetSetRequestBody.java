package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.apiEngine.models.responses.productVersioning.DigitalAssetProperty;

import java.util.List;

public class ProductVariantDigitalAssetSetRequestBody extends ProductVariantRequestBodyBase {
    public List<DigitalAssetProperty> digitalAssets;

    public ProductVariantDigitalAssetSetRequestBody(String level, String localeId, String retailerId, String campaignId, List<DigitalAssetProperty> digitalAssets) {
        super(level, localeId, retailerId, campaignId);
        this.digitalAssets = digitalAssets;
    }

    public ProductVariantDigitalAssetSetRequestBody() {}
}
