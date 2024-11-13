package com.DC.utilities.apiEngine.models.requests.productVersioning;

import java.util.List;

public class DeleteVariantPropertiesRequestBody extends ProductVariantRequestBodyBase {
    public List<String> propertyIds;

    public DeleteVariantPropertiesRequestBody(String level, String localeId, String retailerId, String campaignId, List<String> propertyIds) {
        super(level, localeId, retailerId, campaignId);
        this.propertyIds = propertyIds;
    }

    public DeleteVariantPropertiesRequestBody() {}
}
