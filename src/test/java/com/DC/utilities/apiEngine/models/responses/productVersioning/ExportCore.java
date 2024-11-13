package com.DC.utilities.apiEngine.models.responses.productVersioning;

import com.DC.utilities.enums.Enums;

import java.util.Date;
import java.util.Objects;

public class ExportCore {

    public String _id;

    public int _version;

    public Date dateCreated;

    public Date dateUpdated;

    public Enums.ExportCoreType type;

    public Enums.ExportCoreStatus status;

    public String downloadLink;

    public String userId;

    public String companyId;

    public Object meta;

    public Object error;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExportCore that = (ExportCore) o;
        return _version == that._version &&
                _id.equals(that._id) &&
                dateCreated.equals(that.dateCreated) &&
                dateUpdated.equals(that.dateUpdated) &&
                type == that.type &&
                status == that.status &&
                Objects.equals(downloadLink, that.downloadLink) &&
                userId.equals(that.userId) &&
                companyId.equals(that.companyId) &&
                Objects.equals(meta, that.meta) &&
                Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _version, dateCreated, dateUpdated, type, status, downloadLink, userId, companyId, meta, error);
    }

    @Override
    public String toString() {
        return "{" +
                "_id='" + _id + '\'' +
                ", _version=" + _version +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                ", type=" + type +
                ", status=" + status +
                ", downloadLink='" + downloadLink + '\'' +
                ", userId='" + userId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", meta=" + meta +
                ", error=" + error +
                '}';
    }
}
