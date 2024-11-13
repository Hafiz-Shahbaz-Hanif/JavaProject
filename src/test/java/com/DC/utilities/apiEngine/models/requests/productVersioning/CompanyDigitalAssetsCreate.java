package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyPropertiesBase;
import com.fasterxml.jackson.annotation.JsonInclude;

public class CompanyDigitalAssetsCreate extends CompanyPropertiesBase.DigitalAssetCompanyProperty {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public boolean addImageMappingSpecs;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public boolean removeImageMappingSpecs;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String imageType;

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", helpText='" + helpText + '\'' +
                ", dropdownValues=" + dropdownValues +
                ", allowMultipleValues=" + allowMultipleValues +
                ", group='" + group + '\'' +
                ", addImageMappingSpecs=" + addImageMappingSpecs + '\'' +
                ", removeImageMappingSpecs=" + removeImageMappingSpecs + '\'' +
                ", imageType='" + imageType + '\'' +
                '}';
    }
}
