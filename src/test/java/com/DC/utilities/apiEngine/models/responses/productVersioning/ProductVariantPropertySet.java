package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ProductVariantPropertySet extends ProductEditableVariantDataSetBase {

    public String _id;

    public int _version;

    public Date dateCreated;

    public Date dateUpdated;

    public String companyPropertiesId;

    public List<ProductVariantProperty> properties;

    public ProductVariantPropertySet() {}

    @Override
    public String toString() {
        return "{" +
                "_id='" + _id + '\'' +
                ", _version=" + _version +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                ", companyPropertiesId='" + companyPropertiesId + '\'' +
                ", properties=" + properties +
                ", isEditable=" + isEditable +
                ", taskMeta=" + taskMeta +
                ", type=" + type +
                ", level=" + level +
                ", retailerId='" + retailerId + '\'' +
                ", campaignId='" + campaignId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", productMasterId='" + productMasterId + '\'' +
                ", locale='" + locale + '\'' +
                ", meta=" + meta +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariantPropertySet)) return false;
        if (!super.equals(o)) return false;
        ProductVariantPropertySet that = (ProductVariantPropertySet) o;
        return _version == that._version &&
                _id.equals(that._id) &&
                dateCreated.equals(that.dateCreated) &&
                dateUpdated.equals(that.dateUpdated) &&
                companyPropertiesId.equals(that.companyPropertiesId) &&
                properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _id, _version, dateCreated, dateUpdated, companyPropertiesId, properties);
    }
}
