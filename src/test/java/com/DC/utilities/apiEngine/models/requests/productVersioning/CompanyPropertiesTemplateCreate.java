package com.DC.utilities.apiEngine.models.requests.productVersioning;

import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyProperties;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyPropertiesBase;
import com.DC.utilities.enums.Enums;

import java.util.List;

public class CompanyPropertiesTemplateCreate {
    public String name;
    public Enums.CompanyPropertiesTemplateType templateType;
    public String templateSubType;
    public List<CompanyPropertiesBase.DigitalAssetCompanyProperty> digitalAssetPropertySchema;
    public List<CompanyProperties.Property> propertySchema;
    public List<CompanyProperties.GroupCreate> groups;
    public List<CompanyProperties.GroupCreate> groupsDigitalAssets;

    public CompanyPropertiesTemplateCreate() {
    }
}
