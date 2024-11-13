package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Date;
import java.util.Objects;

public class CompanyProperties extends CompanyPropertiesBase {

    public String _id;

    public int _version;

    public Date dateCreated;

    public Date dateUpdated;

    public String name;

    public String companyId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyProperties that = (CompanyProperties) o;
        return _version == that._version && _id.equals(that._id) &&
                dateCreated.equals(that.dateCreated) &&
                dateUpdated.equals(that.dateUpdated) &&
                name.equals(that.name) &&
                companyId.equals(that.companyId) &&
                digitalAssetPropertySchema.equals(that.digitalAssetPropertySchema) &&
                propertySchema.equals(that.propertySchema) &&
                groups.equals(that.groups) &&
                groupsDigitalAssets.equals(that.groupsDigitalAssets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _version, dateCreated, dateUpdated, name, companyId, digitalAssetPropertySchema, propertySchema, groups, groupsDigitalAssets);
    }

    @Override
    public String toString() {
        return "{" +
                "_id='" + _id + '\'' +
                ", _version=" + _version +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                ", name='" + name + '\'' +
                ", companyId='" + companyId + '\'' +
                ", digitalAssetCompanyPropertySchema=" + digitalAssetPropertySchema +
                ", propertySchema=" + propertySchema +
                ", groups=" + groups +
                ", groupsDigitalAssets=" + groupsDigitalAssets +
                ", templateIds=" + templateIds +
                '}';
    }
}
