package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.enums.Enums;

import java.util.List;

public class CommitOrClearStagedDataRequestBody {
    public Enums.ProductVariantLevel level;
    public String localeId;
    public String retailerId;
    public String campaignId;
    public List<String> propertyIds;
    public List<String> digitalAssetIds;
    public String chainItemId;

    public CommitOrClearStagedDataRequestBody(Enums.ProductVariantLevel level, String localeId, String retailerId, String campaignId, List<String> propertyIds, List<String> digitalAssetIds, String chainItemId) {
        this.level = level;
        this.localeId = localeId;
        this.retailerId = retailerId;
        this.campaignId = campaignId;
        this.propertyIds = propertyIds;
        this.digitalAssetIds = digitalAssetIds;
        this.chainItemId = chainItemId;
    }

}
