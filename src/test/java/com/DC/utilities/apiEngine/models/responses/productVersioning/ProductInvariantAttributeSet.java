package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ProductInvariantAttributeSet extends ProductEditableInvariantDataSetBase {
    public String _id;

    public int _version;

    public Date dateCreated;

    public Date dateUpdated;

    public Number categoryId;

    public List<ProductInvariantAttribute> attributes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProductInvariantAttributeSet that = (ProductInvariantAttributeSet) o;
        return _version == that._version &&
                _id.equals(that._id) &&
                dateCreated.equals(that.dateCreated) &&
                dateUpdated.equals(that.dateUpdated) &&
                categoryId.equals(that.categoryId) &&
                attributes.equals(that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _id, _version, dateCreated, dateUpdated, categoryId, attributes);
    }

    @Override
    public String toString() {
        return "{" +
                "_id='" + _id + '\'' +
                ", _version=" + _version +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                ", categoryId=" + categoryId +
                ", attributes=" + attributes +
                ", isEditable=" + isEditable +
                ", taskMeta=" + taskMeta +
                ", type=" + type +
                ", companyId='" + companyId + '\'' +
                ", productMasterId='" + productMasterId + '\'' +
                ", locale='" + locale + '\'' +
                ", meta=" + meta +
                '}';
    }
}
