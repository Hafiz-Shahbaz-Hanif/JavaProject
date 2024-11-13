package com.DC.utilities.apiEngine.models.requests.productVersioning;

import java.util.List;

public class DeleteVariantDigitalAssetsRequestBody extends ProductVariantRequestBodyBase {
    public List<String> digitalAssetIds;

    public DeleteVariantDigitalAssetsRequestBody(String level, String localeId, String retailerId, String campaignId, List<String> digitalAssetIds) {
        super(level, localeId, retailerId, campaignId);
        this.digitalAssetIds = digitalAssetIds;
    }

    public DeleteVariantDigitalAssetsRequestBody() {}
}
