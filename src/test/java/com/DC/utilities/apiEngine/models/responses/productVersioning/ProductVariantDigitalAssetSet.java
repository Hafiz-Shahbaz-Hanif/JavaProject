package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ProductVariantDigitalAssetSet extends ProductEditableVariantDataSetBase {

    public String _id;

    public int _version;

    public Date dateCreated;

    public Date dateUpdated;

    public String companyPropertiesId;

    public List<DigitalAssetProperty> digitalAssets;

    public ProductVariantDigitalAssetSet() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariantDigitalAssetSet that = (ProductVariantDigitalAssetSet) o;
        return _version == that._version &&
                _id.equals(that._id) &&
                dateCreated.equals(that.dateCreated) &&
                dateUpdated.equals(that.dateUpdated) &&
                companyPropertiesId.equals(that.companyPropertiesId) &&
                digitalAssets.equals(that.digitalAssets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _version, dateCreated, dateUpdated, companyPropertiesId, digitalAssets);
    }
}
