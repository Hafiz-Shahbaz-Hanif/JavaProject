package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

public abstract class ProductEditableInvariantDataSetBase extends ProductInvariantDataSetBase {

    public boolean isEditable;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public ProductDataSetTaskMeta taskMeta;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProductEditableInvariantDataSetBase that = (ProductEditableInvariantDataSetBase) o;
        return isEditable == that.isEditable && Objects.equals(taskMeta, that.taskMeta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isEditable, taskMeta);
    }

    @Override
    public String toString() {
        return "{" +
                "isEditable=" + isEditable +
                ", taskMeta=" + taskMeta +
                ", type=" + type +
                ", companyId='" + companyId + '\'' +
                ", productMasterId='" + productMasterId + '\'' +
                ", locale='" + locale + '\'' +
                ", meta=" + meta +
                '}';
    }
}
