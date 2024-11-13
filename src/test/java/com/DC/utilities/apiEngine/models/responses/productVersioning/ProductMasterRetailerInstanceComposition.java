package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.apiEngine.models.responses.insights.CPGData.SearchPhrases.SearchPhraseVolume;

import java.util.List;
import java.util.Objects;

public class ProductMasterRetailerInstanceComposition extends ProductMasterInstanceComposition {
    public String rpc;

    public List<String> previousRpcs;

    public List<String> businessUnits;

    public ProductMasterRetailerInstanceComposition(ProductMasterInfo productMasterInfo, String instanceUniqueId, String instanceName, String instanceThumbnail, String instanceId, String masterUniqueId, String masterName, String masterThumbnail, List<Integer> brands, ProductInvariantAttributeSetCore attributes, List<ProductVariantProperty> properties, ProductVariantKeywords keywords, List<SearchPhraseVolume> keywordVolumes, List<DigitalAssetProperty> digitalAssets, String rpc, List<String> previousRpcs, List<String> businessUnits) {
        super(productMasterInfo, instanceUniqueId, instanceName, instanceThumbnail, instanceId, masterUniqueId, masterName, masterThumbnail, brands, attributes, properties, keywords, keywordVolumes, digitalAssets);
        this.rpc = rpc;
        this.previousRpcs = previousRpcs;
        this.businessUnits = businessUnits;
    }

    public ProductMasterRetailerInstanceComposition() {}

    public ProductMasterRetailerInstanceComposition(ProductMasterInstanceComposition instanceComposition, String rpc, List<String> previousRpcs, List<String> businessUnits) {
        super(instanceComposition.productMasterInfo, instanceComposition.instanceUniqueId, instanceComposition.instanceName, instanceComposition.instanceThumbnail, instanceComposition.instanceId, instanceComposition.masterUniqueId, instanceComposition.masterName, instanceComposition.masterThumbnail, instanceComposition.brands, instanceComposition.attributes, instanceComposition.properties, instanceComposition.keywords, instanceComposition.keywordVolumes, instanceComposition.digitalAssets);
        this.rpc = rpc;
        this.previousRpcs = previousRpcs;
        this.businessUnits = businessUnits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMasterRetailerInstanceComposition)) return false;
        if (!super.equals(o)) return false;
        ProductMasterRetailerInstanceComposition that = (ProductMasterRetailerInstanceComposition) o;
        return Objects.equals(rpc, that.rpc) && previousRpcs.equals(that.previousRpcs) && businessUnits.equals(that.businessUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rpc, previousRpcs, businessUnits);
    }

    @Override
    public String toString() {
        return "{" +
                "rpc='" + rpc + '\'' +
                ", previousRpcs=" + previousRpcs +
                ", businessUnits=" + businessUnits +
                ", productMasterInfo=" + productMasterInfo +
                ", instanceUniqueId='" + instanceUniqueId + '\'' +
                ", instanceName='" + instanceName + '\'' +
                ", instanceThumbnail='" + instanceThumbnail + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", masterUniqueId='" + masterUniqueId + '\'' +
                ", masterName='" + masterName + '\'' +
                ", masterThumbnail='" + masterThumbnail + '\'' +
                ", brands=" + brands +
                ", attributes=" + attributes +
                ", properties=" + properties +
                ", keywords=" + keywords +
                ", keywordVolumes=" + keywordVolumes +
                ", digitalAssets=" + digitalAssets +
                '}';
    }
}
