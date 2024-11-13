package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Objects;

public class FriendlyProductVariantList extends ProductVariantList {

    public String ownerName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendlyProductVariantList)) return false;
        if (!super.equals(o)) return false;
        FriendlyProductVariantList that = (FriendlyProductVariantList) o;
        return ownerName.equals(that.ownerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ownerName);
    }

    @Override
    public String toString() {
        return "{" +
                "_id='" + _id + '\'' +
                ", _version=" + _version + '\'' +
                ", dateCreated=" + dateCreated + '\'' +
                ", dateUpdated=" + dateUpdated + '\'' +
                ", companyId='" + companyId + '\'' +
                ", name='" + name + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", permission=" + permission + '\'' +
                ", products=" + products + '\'' +
                '}';
    }
}
