package com.DC.objects.productVersioning;

import java.util.Objects;

public class ProductAttributesDataInExcel {

    public String productIdentifier;

    public String locale;
    public String productName;
    public String categoryName;

    public String path;

    public long categoryId;

    public String attribute;
    public long segmentId;
    public String taggedValue;
    public long segmentValueId;
    public long volume;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductAttributesDataInExcel that = (ProductAttributesDataInExcel) o;
        return categoryId == that.categoryId && segmentId == that.segmentId && segmentValueId == that.segmentValueId && volume == that.volume && Objects.equals(productIdentifier, that.productIdentifier) && Objects.equals(locale, that.locale) && Objects.equals(productName, that.productName) && Objects.equals(categoryName, that.categoryName) && Objects.equals(path, that.path) && Objects.equals(attribute, that.attribute) && Objects.equals(taggedValue, that.taggedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productIdentifier, locale, productName, categoryName, path, categoryId, attribute, segmentId, taggedValue, segmentValueId, volume);
    }


    public String toString() {
        return "{" + "productIdentifier='" + productIdentifier + '\'' + ", locale='" + locale + '\'' + ", productName='" + productName + '\'' + ", categoryName='" + categoryName + '\'' + ", path='" + path + '\'' + ", categoryId=" + categoryId + ", attribute='" + attribute + '\'' + ", segmentId='" + segmentId + '\'' + ", taggedValue='" + taggedValue + '\'' + ", segmentValueId='" + segmentValueId + '\'' + ", volume=" + volume + '}';
    }
}