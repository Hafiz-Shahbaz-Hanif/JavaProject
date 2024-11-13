package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.apiEngine.models.requests.productVersioning.CompanyPropertiesTemplateCreate;

import java.time.OffsetDateTime;

public class CompanyPropertiesTemplate extends CompanyPropertiesTemplateCreate {
    public String _id;
    public int _version;
    public OffsetDateTime dateCreated;

    public OffsetDateTime dateUpdated;
    public boolean enabled;

    public CompanyPropertiesTemplate() {}
}
