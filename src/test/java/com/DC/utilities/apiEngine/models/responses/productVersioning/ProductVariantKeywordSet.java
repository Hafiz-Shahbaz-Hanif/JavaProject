package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Date;
import java.util.Objects;

public class ProductVariantKeywordSet extends ProductEditableVariantDataSetBase {

    public String _id;

    public int _version;

    public Date dateCreated;

    public Date dateUpdated;

    public ProductVariantKeywords keywords;

    public ProductVariantKeywordSet() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVariantKeywordSet)) return false;
        if (!super.equals(o)) return false;
        ProductVariantKeywordSet that = (ProductVariantKeywordSet) o;
        return _version == that._version &&
                _id.equals(that._id) &&
                dateCreated.equals(that.dateCreated) &&
                dateUpdated.equals(that.dateUpdated) &&
                keywords.equals(that.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), _id, _version, dateCreated, dateUpdated, keywords);
    }
}