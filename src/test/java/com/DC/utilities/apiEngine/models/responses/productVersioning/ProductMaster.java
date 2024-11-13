package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ProductMaster {

    public String _id;

    public int _version;

    public Date dateCreated;

    public Date dateUpdated;

    public String companyId;

    public String uniqueId;

    public String name;
    public String thumbnail;

    public VariantSets variantSets;

    public ProductMaster(String _id, int _version, Date dateCreated, Date dateUpdated, String companyId, String uniqueId, String name, String thumbnail, VariantSets variantSets) {
        this._id = _id;
        this._version = _version;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.companyId = companyId;
        this.uniqueId = uniqueId;
        this.name = name;
        this.thumbnail = thumbnail;
        this.variantSets = variantSets;
    }

    public ProductMaster() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductMaster that = (ProductMaster) o;
        return _version == that._version && _id.equals(that._id) && dateCreated.equals(that.dateCreated) && dateUpdated.equals(that.dateUpdated) && companyId.equals(that.companyId) && uniqueId.equals(that.uniqueId) && name.equals(that.name) && Objects.equals(thumbnail, that.thumbnail) && variantSets.equals(that.variantSets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _version, dateCreated, dateUpdated, companyId, uniqueId, name, thumbnail, variantSets);
    }

    @Override
    public String toString() {
        return "{" + "_id='" + _id + '\'' + ", _version=" + _version + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + ", companyId='" + companyId + '\'' + ", uniqueId='" + uniqueId + '\'' + ", name='" + name + '\'' + ", thumbnail='" + thumbnail + '\'' + ", variantSets=" + variantSets + '}';
    }

    public static class VariantSets {

        public List<Live> live;
        public List<Staged> staged;

        public VariantSets() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VariantSets that = (VariantSets) o;
            return live.equals(that.live) && staged.equals(that.staged);
        }

        @Override
        public int hashCode() {
            return Objects.hash(live, staged);
        }

        @Override
        public String toString() {
            return "{" + "live=" + live + ", staged=" + staged + '}';
        }

        public static class Live extends VariantSetsBase {

            public String name;

            public String uniqueId;

            public InvariantData invariantData;

            public ProductVariantInstances instances;

            public Live() {
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Live live = (Live) o;
                return name.equals(live.name) && uniqueId.equals(live.uniqueId) && invariantData.equals(live.invariantData) && instances.equals(live.instances);
            }

            @Override
            public int hashCode() {
                return Objects.hash(name, uniqueId, invariantData, instances);
            }

            @Override
            public String toString() {
                return "{" + "name='" + name + '\'' + ", uniqueId='" + uniqueId + '\'' + ", invariantData=" + invariantData + ", instances=" + instances + ", id='" + id + '\'' + ", localeId='" + localeId + '\'' + '}';
            }

            public static class ProductVariantInstances {

                public ProductInstanceGlobal global;

                public List<ProductInstanceRetailer> retailer;

                public List<ProductInstanceCampaign> globalCampaign;

                public List<ProductInstanceRetailerCampaign> retailerCampaign;

                public ProductVariantInstances() {
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    ProductVariantInstances that = (ProductVariantInstances) o;
                    return global.equals(that.global) && retailer.equals(that.retailer) && globalCampaign.equals(that.globalCampaign) && retailerCampaign.equals(that.retailerCampaign);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(global, retailer, globalCampaign, retailerCampaign);
                }

                @Override
                public String toString() {
                    return "{" + "global=" + global + ", retailer=" + retailer + ", globalCampaign=" + globalCampaign + ", retailerCampaign=" + retailerCampaign + '}';
                }

                public static class ProductInstanceGlobal {

                    public String id;

                    public Date dateCreated;

                    public Date dateUpdated;

                    public String uniqueId;


                    public String name;

                    public String thumbnail;

                    public String propertySetId;

                    public String keywordSetId;

                    public String digitalAssetSetId;

                    public ProductInstanceGlobal() {
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) return true;
                        if (o == null || getClass() != o.getClass()) return false;
                        ProductInstanceGlobal that = (ProductInstanceGlobal) o;
                        return id.equals(that.id) && dateCreated.equals(that.dateCreated) && dateUpdated.equals(that.dateUpdated) && uniqueId.equals(that.uniqueId) && name.equals(that.name) && Objects.equals(thumbnail, that.thumbnail) && Objects.equals(propertySetId, that.propertySetId) && Objects.equals(keywordSetId, that.keywordSetId) && Objects.equals(digitalAssetSetId, that.digitalAssetSetId);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(id, dateCreated, dateUpdated, uniqueId, name, thumbnail, propertySetId, keywordSetId, digitalAssetSetId);
                    }

                    @Override
                    public String toString() {
                        return "{" + "id='" + id + '\'' + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + ", uniqueId='" + uniqueId + '\'' + ", name='" + name + '\'' + ", thumbnail='" + thumbnail + '\'' + ", propertySetId='" + propertySetId + '\'' + ", keywordSetId='" + keywordSetId + '\'' + ", digitalAssetSetId='" + digitalAssetSetId + '\'' + '}';
                    }
                }

                public static class ProductInstanceRetailer extends ProductInstanceGlobal {

                    public String retailerId;

                    public String rpc;
                    public List<String> previousRpcs;
                    public List<String> businessUnits;

                    public ProductInstanceRetailer() {
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) return true;
                        if (!(o instanceof ProductInstanceRetailer)) return false;
                        if (!super.equals(o)) return false;
                        ProductInstanceRetailer that = (ProductInstanceRetailer) o;
                        return retailerId.equals(that.retailerId) && Objects.equals(rpc, that.rpc) && previousRpcs.equals(that.previousRpcs) && businessUnits.equals(that.businessUnits);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(super.hashCode(), retailerId, rpc, previousRpcs, businessUnits);
                    }

                    @Override
                    public String toString() {
                        return "{" + "id='" + id + '\'' + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + ", uniqueId='" + uniqueId + '\'' + ", name='" + name + '\'' + ", thumbnail='" + thumbnail + '\'' + ", propertySetId='" + propertySetId + '\'' + ", keywordSetId='" + keywordSetId + '\'' + ", digitalAssetSetId='" + digitalAssetSetId + '\'' + ", retailerId='" + retailerId + '\'' + ", rpc='" + rpc + '\'' + ", previousRpcs=" + previousRpcs + ", businessUnits=" + businessUnits + '}';
                    }
                }

                public static class ProductInstanceCampaign extends ProductInstanceGlobal {

                    public String campaignId;

                    public ProductInstanceCampaign() {
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) return true;
                        if (o == null || getClass() != o.getClass()) return false;
                        if (!super.equals(o)) return false;
                        ProductInstanceCampaign that = (ProductInstanceCampaign) o;
                        return campaignId.equals(that.campaignId);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(super.hashCode(), campaignId);
                    }

                    @Override
                    public String toString() {
                        return "{" + "campaignId='" + campaignId + '\'' + ", id='" + id + '\'' + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + ", uniqueId='" + uniqueId + '\'' + ", name='" + name + '\'' + ", thumbnail='" + thumbnail + '\'' + ", propertySetId='" + propertySetId + '\'' + ", keywordSetId='" + keywordSetId + '\'' + ", digitalAssetSetId='" + digitalAssetSetId + '\'' + '}';
                    }
                }

                public static class ProductInstanceRetailerCampaign extends ProductInstanceRetailer {

                    public String campaignId;

                    public ProductInstanceRetailerCampaign() {
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) return true;
                        if (!(o instanceof ProductInstanceRetailerCampaign)) return false;
                        if (!super.equals(o)) return false;
                        ProductInstanceRetailerCampaign that = (ProductInstanceRetailerCampaign) o;
                        return campaignId.equals(that.campaignId);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(super.hashCode(), campaignId);
                    }

                    @Override
                    public String toString() {
                        return "{" + "id='" + id + '\'' + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + ", uniqueId='" + uniqueId + '\'' + ", name='" + name + '\'' + ", thumbnail='" + thumbnail + '\'' + ", propertySetId='" + propertySetId + '\'' + ", keywordSetId='" + keywordSetId + '\'' + ", digitalAssetSetId='" + digitalAssetSetId + '\'' + ", retailerId='" + retailerId + '\'' + ", rpc='" + rpc + '\'' + ", previousRpcs=" + previousRpcs + ", businessUnits=" + businessUnits + ", campaignId='" + campaignId + '\'' + '}';
                    }
                }
            }
        }

        public static class Staged extends VariantSetsBase {

            public InvariantData invariantData;

            public ProductVariantInstancesStaged instances;

            public String chainItemId;

            public Staged() {
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                if (!super.equals(o)) return false;
                Staged staged = (Staged) o;
                return Objects.equals(invariantData, staged.invariantData) && instances.equals(staged.instances) && chainItemId.equals(staged.chainItemId);
            }

            @Override
            public int hashCode() {
                return Objects.hash(super.hashCode(), invariantData, instances, chainItemId);
            }

            @Override
            public String toString() {
                return "{" +
                        "invariantData=" + invariantData +
                        ", instances=" + instances +
                        ", chainItemId='" + chainItemId + '\'' +
                        ", id='" + id + '\'' +
                        ", localeId='" + localeId + '\'' +
                        '}';
            }

            public static class ProductVariantInstancesStaged {

                public ProductInstanceStagedGlobal global;

                public List<ProductInstanceStagedRetailer> retailer;

                public List<ProductInstanceStagedCampaign> globalCampaign;

                public List<ProductInstanceStagedRetailerCampaign> retailerCampaign;

                public ProductVariantInstancesStaged() {
                }

                @Override
                public boolean equals(Object o) {
                    if (this == o) return true;
                    if (o == null || getClass() != o.getClass()) return false;
                    ProductVariantInstancesStaged that = (ProductVariantInstancesStaged) o;
                    return global.equals(that.global) && retailer.equals(that.retailer) && globalCampaign.equals(that.globalCampaign) && retailerCampaign.equals(that.retailerCampaign);
                }

                @Override
                public int hashCode() {
                    return Objects.hash(global, retailer, globalCampaign, retailerCampaign);
                }

                @Override
                public String toString() {
                    return "{" + "global=" + global + ", retailer=" + retailer + ", globalCampaign=" + globalCampaign + ", retailerCampaign=" + retailerCampaign + '}';
                }

                public static class ProductInstanceStagedGlobal {

                    public String id;

                    public Date dateCreated;

                    public Date dateUpdated;

                    public String propertySetId;

                    public String keywordSetId;

                    public String digitalAssetSetId;

                    public ProductInstanceStagedGlobal() {
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) return true;
                        if (o == null || getClass() != o.getClass()) return false;
                        ProductInstanceStagedGlobal that = (ProductInstanceStagedGlobal) o;
                        return id.equals(that.id) && dateCreated.equals(that.dateCreated) && dateUpdated.equals(that.dateUpdated) && Objects.equals(propertySetId, that.propertySetId) && Objects.equals(keywordSetId, that.keywordSetId) && Objects.equals(digitalAssetSetId, that.digitalAssetSetId);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(id, dateCreated, dateUpdated, propertySetId, keywordSetId, digitalAssetSetId);
                    }

                    @Override
                    public String toString() {
                        return "{" + "id='" + id + '\'' + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + ", propertySetId='" + propertySetId + '\'' + ", keywordSetId='" + keywordSetId + '\'' + ", digitalAssetSetId='" + digitalAssetSetId + '\'' + '}';
                    }
                }

                public static class ProductInstanceStagedRetailer extends ProductInstanceStagedGlobal {

                    public String retailerId;
                    public String rpc;
                    public List<String> previousRpcs;
                    public List<String> businessUnits;

                    public ProductInstanceStagedRetailer() {
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) return true;
                        if (!(o instanceof ProductInstanceStagedRetailer)) return false;
                        if (!super.equals(o)) return false;
                        ProductInstanceStagedRetailer that = (ProductInstanceStagedRetailer) o;
                        return retailerId.equals(that.retailerId) && Objects.equals(rpc, that.rpc) && previousRpcs.equals(that.previousRpcs) && businessUnits.equals(that.businessUnits);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(super.hashCode(), retailerId, rpc, previousRpcs, businessUnits);
                    }

                    @Override
                    public String toString() {
                        return "{" + "id='" + id + '\'' + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + ", propertySetId='" + propertySetId + '\'' + ", keywordSetId='" + keywordSetId + '\'' + ", digitalAssetSetId='" + digitalAssetSetId + '\'' + ", retailerId='" + retailerId + '\'' + ", rpc='" + rpc + '\'' + ", previousRpcs=" + previousRpcs + ", businessUnits=" + businessUnits + '}';
                    }
                }

                public static class ProductInstanceStagedCampaign extends ProductInstanceStagedGlobal {

                    public String campaignId;

                    public ProductInstanceStagedCampaign() {
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) return true;
                        if (o == null || getClass() != o.getClass()) return false;
                        if (!super.equals(o)) return false;
                        ProductInstanceStagedCampaign that = (ProductInstanceStagedCampaign) o;
                        return campaignId.equals(that.campaignId);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(super.hashCode(), campaignId);
                    }

                    @Override
                    public String toString() {
                        return "{" + "campaignId='" + campaignId + '\'' + ", id='" + id + '\'' + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + ", propertySetId='" + propertySetId + '\'' + ", keywordSetId='" + keywordSetId + '\'' + ", digitalAssetSetId='" + digitalAssetSetId + '\'' + '}';
                    }
                }

                public static class ProductInstanceStagedRetailerCampaign extends ProductInstanceStagedRetailer {

                    public String campaignId;

                    public ProductInstanceStagedRetailerCampaign() {
                    }

                    @Override
                    public boolean equals(Object o) {
                        if (this == o) return true;
                        if (!(o instanceof ProductInstanceStagedRetailerCampaign)) return false;
                        if (!super.equals(o)) return false;
                        ProductInstanceStagedRetailerCampaign that = (ProductInstanceStagedRetailerCampaign) o;
                        return campaignId.equals(that.campaignId);
                    }

                    @Override
                    public int hashCode() {
                        return Objects.hash(super.hashCode(), campaignId);
                    }

                    @Override
                    public String toString() {
                        return "{" + "id='" + id + '\'' + ", dateCreated=" + dateCreated + ", dateUpdated=" + dateUpdated + ", propertySetId='" + propertySetId + '\'' + ", keywordSetId='" + keywordSetId + '\'' + ", digitalAssetSetId='" + digitalAssetSetId + '\'' + ", campaignId='" + campaignId + '\'' + ", retailerId='" + retailerId + '\'' + ", rpc='" + rpc + '\'' + ", previousRpcs=" + previousRpcs + ", businessUnits=" + businessUnits + '}';
                    }
                }
            }
        }

        public static class InvariantData {

            public String attributeSetId;

            public List<Integer> brand;

            public InvariantData() {
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                InvariantData that = (InvariantData) o;
                return Objects.equals(attributeSetId, that.attributeSetId) && Objects.equals(brand, that.brand);
            }

            @Override
            public int hashCode() {
                return Objects.hash(attributeSetId, brand);
            }

            @Override
            public String toString() {
                return "{" + "attributeSetId='" + attributeSetId + '\'' + ", brand=" + brand + '}';
            }
        }

        public static class VariantSetsBase {

            public String id;

            public String localeId;

            public VariantSetsBase() {
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                VariantSetsBase that = (VariantSetsBase) o;
                return id.equals(that.id) && localeId.equals(that.localeId);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id, localeId);
            }

            @Override
            public String toString() {
                return "{" + "id='" + id + '\'' + ", localeId='" + localeId + '\'' + '}';
            }
        }
    }

    public VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal getInstanceBaseData(ProductVariantInstancePath variantInstancePath) {
        ProductMaster.VariantSets.Live variantSet = this.variantSets.live
                .stream()
                .filter(variant -> variant.localeId.equals(variantInstancePath.localeId))
                .findFirst()
                .orElseThrow();

        switch (variantInstancePath.getProductLevel()) {
            case GLOBAL:
                return variantSet.instances.global;

            case RETAILER:
                return variantSet.instances.retailer
                        .stream().filter(instance -> instance.retailerId.equals(variantInstancePath.retailerId))
                        .findFirst()
                        .orElseThrow();

            case GLOBAL_CAMPAIGN:
                return variantSet.instances.globalCampaign
                        .stream().filter(instance -> instance.campaignId.equals(variantInstancePath.campaignId))
                        .findFirst()
                        .orElseThrow();
            case RETAILER_CAMPAIGN:
                return variantSet.instances.retailerCampaign
                        .stream().filter(instance ->
                                instance.retailerId.equals(variantInstancePath.retailerId) && instance.campaignId.equals(variantInstancePath.campaignId)
                        )
                        .findFirst()
                        .orElseThrow();
        }
        return null;
    }

    public VariantSets.Staged.ProductVariantInstancesStaged.ProductInstanceStagedGlobal getStagedInstanceBaseData(ProductVariantInstancePath variantInstancePath) {
        if (this.variantSets.staged == null || this.variantSets.staged.isEmpty()) {
            return null;
        }

        var variantSet = this.variantSets.staged
                .stream()
                .filter(variant -> variant.localeId.equals(variantInstancePath.localeId))
                .findFirst()
                .orElse(null);

        if (variantSet != null) {
            switch (variantInstancePath.getProductLevel()) {
                case GLOBAL:
                    return variantSet.instances.global;

                case RETAILER:
                    return variantSet.instances.retailer
                            .stream().filter(instance -> instance.retailerId.equals(variantInstancePath.retailerId))
                            .findFirst()
                            .orElse(null);

                case GLOBAL_CAMPAIGN:
                    return variantSet.instances.globalCampaign
                            .stream().filter(instance -> instance.campaignId.equals(variantInstancePath.campaignId))
                            .findFirst()
                            .orElse(null);
                case RETAILER_CAMPAIGN:
                    return variantSet.instances.retailerCampaign
                            .stream().filter(instance ->
                                    instance.retailerId.equals(variantInstancePath.retailerId) && instance.campaignId.equals(variantInstancePath.campaignId)
                            )
                            .findFirst()
                            .orElse(null);
            }
        }
        return null;
    }

}

