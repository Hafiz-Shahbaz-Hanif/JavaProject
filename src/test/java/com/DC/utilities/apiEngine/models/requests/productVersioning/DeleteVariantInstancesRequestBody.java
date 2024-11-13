package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.enums.Enums;

import java.util.List;

public class DeleteVariantInstancesRequestBody {

    public Enums.DeleteProductVariantLevel level;

    public String retailerToDelete;

    public String campaignToDelete;

    public List<String> localeIdsToDelete;

    public DeleteVariantInstancesRequestBody(Enums.DeleteProductVariantLevel level, String retailerIdToDelete, String campaignIdToDelete, List<String> localeIdsToDelete) {
        this.level = level;
        this.retailerToDelete = retailerIdToDelete;
        this.campaignToDelete = campaignIdToDelete;
        this.localeIdsToDelete = localeIdsToDelete;
    }

    public DeleteVariantInstancesRequestBody() {}
}
