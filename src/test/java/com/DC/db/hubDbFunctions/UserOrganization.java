package com.DC.db.hubDbFunctions;

public class UserOrganization {

    private Object organizationId;
    private Object businessUnitId;
    private Object retailerPlatformId;

    public UserOrganization(Object organizationId, Object businessUnitId, Object retailerPlatformId) {
        this.organizationId = organizationId;
        this.businessUnitId = businessUnitId;
        this.retailerPlatformId = retailerPlatformId;
    }

    public Object getOrganizationId() {
        return organizationId;
    }

    public Object getBusinessUnitId() {
        return businessUnitId;
    }

    public Object getRetailerPlatformId() {
        return retailerPlatformId;
    }
}
