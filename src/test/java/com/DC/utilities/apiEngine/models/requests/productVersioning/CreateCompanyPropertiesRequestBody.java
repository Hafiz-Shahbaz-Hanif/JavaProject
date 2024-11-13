package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateCompanyPropertiesRequestBody {

    public String name;

    public List<CompanyDigitalAssetsCreate> digitalAssetPropertySchema;

    public List<CompanyProperties.Property> propertySchema;

    public List<CompanyProperties.Group> groups;

    public List<CompanyProperties.Group> groupsDigitalAssets;

    public CreateCompanyPropertiesRequestBody(String name,
                                              List<CompanyDigitalAssetsCreate> digitalAssetPropertySchema,
                                              List<CompanyProperties.Property> propertySchema,
                                              List<CompanyProperties.Group> groups,
                                              List<CompanyProperties.Group> groupsInternal
    ) {
        this.name = name;
        this.digitalAssetPropertySchema = digitalAssetPropertySchema;
        this.propertySchema = propertySchema;
        this.groups = groups;
        this.groupsDigitalAssets = groupsInternal;
    }

    public CreateCompanyPropertiesRequestBody() {}
}
