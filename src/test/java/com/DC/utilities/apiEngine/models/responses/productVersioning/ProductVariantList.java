package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.enums.Enums;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class ProductVariantList {
    public String _id;

    public int _version;

    public OffsetDateTime dateCreated;

    public OffsetDateTime dateUpdated;

    public String companyId;

    public String name;

    public String ownerId;

    public Enums.ProductListPermission permission;

    public List<ProductVariantListProduct> products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariantList)) return false;
        ProductVariantList that = (ProductVariantList) o;
        return _version == that._version
                && _id.equals(that._id)
                && dateCreated.equals(that.dateCreated)
                && dateUpdated.equals(that.dateUpdated)
                && companyId.equals(that.companyId)
                && name.equals(that.name)
                && ownerId.equals(that.ownerId)
                && permission == that.permission
                && products.equals(that.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _version, dateCreated, dateUpdated, companyId, name, ownerId, permission, products);
    }

    @Override
    public String toString() {
        return "{" +
                "_id='" + _id + '\'' +
                ", _version=" + _version +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                ", companyId='" + companyId + '\'' +
                ", name='" + name + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", permission=" + permission +
                ", products=" + products +
                '}';
    }
}
