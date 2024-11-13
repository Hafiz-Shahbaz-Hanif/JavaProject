package com.DC.objects.productVersioning;

import java.util.List;
import java.util.Objects;

public class AttributesTableData {
    public String attribute;
    public List<String> taggedValues;

    public double taggedVolume;

    public AttributesTableData(String attribute, List<String> taggedValues, double taggedVolume) {
        this.attribute = attribute;
        this.taggedValues = taggedValues;
        this.taggedVolume = taggedVolume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttributesTableData)) return false;
        AttributesTableData that = (AttributesTableData) o;
        return Double.compare(taggedVolume, that.taggedVolume) == 0 && Objects.equals(attribute, that.attribute) && Objects.equals(taggedValues, that.taggedValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attribute, taggedValues, taggedVolume);
    }
}
