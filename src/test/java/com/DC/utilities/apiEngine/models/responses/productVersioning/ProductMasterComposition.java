package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.List;
import java.util.Objects;

public class ProductMasterComposition {

    public String localeId;

    public ProductMasterInstanceComposition global;

    public List<ProductMasterRetailerInstanceComposition> retailer;

    public List<ProductMasterInstanceComposition> globalCampaign;

    public List<ProductMasterRetailerInstanceComposition> retailerCampaign;

    public ProductMasterComposition(String localeId,
                                    ProductMasterInstanceComposition global,
                                    List<ProductMasterRetailerInstanceComposition> retailer,
                                    List<ProductMasterInstanceComposition> globalCampaign,
                                    List<ProductMasterRetailerInstanceComposition> retailerCampaign
    ) {
        this.localeId = localeId;
        this.global = global;
        this.retailer = retailer;
        this.globalCampaign = globalCampaign;
        this.retailerCampaign = retailerCampaign;
    }

    public ProductMasterComposition() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMasterComposition)) return false;
        ProductMasterComposition that = (ProductMasterComposition) o;
        return localeId.equals(that.localeId) &&
                global.equals(that.global) &&
                retailer.equals(that.retailer) &&
                globalCampaign.equals(that.globalCampaign) &&
                retailerCampaign.equals(that.retailerCampaign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localeId, global, retailer, globalCampaign, retailerCampaign);
    }

    @Override
    public String toString() {
        return "{" +
                "localeId='" + localeId + '\'' +
                ", global=" + global +
                ", retailer=" + retailer +
                ", globalCampaign=" + globalCampaign +
                ", retailerCampaign=" + retailerCampaign +
                '}';
    }
}
