package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Objects;

public abstract class ProductEditableVariantDataSetBase extends ProductVariantDataSetBase {

    public boolean isEditable;

    public ProductDataSetTaskMeta taskMeta;

    public ProductEditableVariantDataSetBase() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductEditableVariantDataSetBase that = (ProductEditableVariantDataSetBase) o;
        return isEditable == that.isEditable && Objects.equals(taskMeta, that.taskMeta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isEditable, taskMeta);
    }

    @Override
    public String toString() {
        return "{" +
                "isEditable=" + isEditable +
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
}

