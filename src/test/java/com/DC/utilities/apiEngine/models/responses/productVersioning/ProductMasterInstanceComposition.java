package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.apiEngine.models.responses.insights.CPGData.SearchPhrases.SearchPhraseVolume;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ProductMasterInstanceComposition {

    public ProductMasterInfo productMasterInfo;

    public String instanceUniqueId;

    public String instanceName;

    public String instanceThumbnail;
    public String instanceId;

    public String masterUniqueId;

    public String masterName;

    public String masterThumbnail;

    public List<Integer> brands;

    public ProductInvariantAttributeSetCore attributes;

    public List<ProductVariantProperty> properties;

    public ProductVariantKeywords keywords;

    public List<SearchPhraseVolume> keywordVolumes;

    public List<DigitalAssetProperty> digitalAssets;

    public ProductMasterInstanceComposition(ProductMasterInfo productMasterInfo,
                                            String instanceUniqueId,
                                            String instanceName,
                                            String instanceThumbnail,
                                            String instanceId,
                                            String masterUniqueId,
                                            String masterName,
                                            String masterThumbnail,
                                            List<Integer> brands,
                                            ProductInvariantAttributeSetCore attributes,
                                            List<ProductVariantProperty> properties,
                                            ProductVariantKeywords keywords,
                                            List<SearchPhraseVolume> keywordVolumes,
                                            List<DigitalAssetProperty> digitalAssets) {
        this.productMasterInfo = productMasterInfo;
        this.instanceUniqueId = instanceUniqueId;
        this.instanceName = instanceName;
        this.instanceThumbnail = instanceThumbnail;
        this.instanceId = instanceId;
        this.masterUniqueId = masterUniqueId;
        this.masterName = masterName;
        this.masterThumbnail = masterThumbnail;
        this.brands = brands;
        this.attributes = attributes;
        this.properties = properties;
        this.keywords = keywords;
        this.keywordVolumes = keywordVolumes;
        this.digitalAssets = digitalAssets;
    }

    public ProductMasterInstanceComposition() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductMasterInstanceComposition)) return false;
        ProductMasterInstanceComposition that = (ProductMasterInstanceComposition) o;
        return productMasterInfo.equals(that.productMasterInfo) &&
                instanceUniqueId.equals(that.instanceUniqueId) &&
                instanceName.equals(that.instanceName) &&
                Objects.equals(instanceThumbnail, that.instanceThumbnail) &&
                Objects.equals(instanceId, that.instanceId) &&
                masterUniqueId.equals(that.masterUniqueId) &&
                masterName.equals(that.masterName) &&
                Objects.equals(masterThumbnail, that.masterThumbnail) &&
                brands.equals(that.brands) &&
                Objects.equals(attributes, that.attributes) &&
                properties.equals(that.properties) &&
                keywords.equals(that.keywords) &&
                keywordVolumes.equals(that.keywordVolumes) &&
                digitalAssets.equals(that.digitalAssets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productMasterInfo, instanceUniqueId, instanceName, instanceThumbnail, instanceId, masterUniqueId, masterName, masterThumbnail, brands, attributes, properties, keywords, keywordVolumes, digitalAssets);
    }

    @Override
    public String toString() {
        return "{" +
                "productMasterInfo=" + productMasterInfo +
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

    public void sortSearchPhraseVolumes() {
        this.keywordVolumes.sort(Comparator.comparing(o -> o.searchPhrase));
    }
}
