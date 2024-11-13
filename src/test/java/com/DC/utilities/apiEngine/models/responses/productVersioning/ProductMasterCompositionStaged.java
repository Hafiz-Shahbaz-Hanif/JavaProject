package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.List;
import java.util.Objects;

public class ProductMasterCompositionStaged  extends ProductMasterComposition{

    public String chainItemId;

    public ProductMasterCompositionStaged(String localeId, ProductMasterInstanceComposition global, List<ProductMasterRetailerInstanceComposition> retailer, List<ProductMasterInstanceComposition> globalCampaign, List<ProductMasterRetailerInstanceComposition> retailerCampaign, String chainItemId) {
        super(localeId, global, retailer, globalCampaign, retailerCampaign);
        this.chainItemId = chainItemId;
    }

    public ProductMasterCompositionStaged() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMasterCompositionStaged)) return false;
        if (!super.equals(o)) return false;
        ProductMasterCompositionStaged that = (ProductMasterCompositionStaged) o;
        return Objects.equals(chainItemId, that.chainItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chainItemId);
    }

    @Override
    public String toString() {
        return "{" +
                "chainItemId='" + chainItemId + '\'' +
                ", localeId='" + localeId + '\'' +
                ", global=" + global +
                ", retailer=" + retailer +
                ", globalCampaign=" + globalCampaign +
                ", retailerCampaign=" + retailerCampaign +
                '}';
    }
}
