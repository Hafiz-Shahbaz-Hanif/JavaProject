package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.time.OffsetDateTime;

public class CompanyRetailerStandardized {
    public String _id;
    public int _version;
    public OffsetDateTime dateCreated;
    public OffsetDateTime dateUpdated;
    public int domainId;
    public String retailerName;
    public boolean isAIWhitelisted;

    public CompanyRetailerStandardized() {}
}
