package com.DC.objects.productVersioning;

import com.DC.utilities.enums.Enums;

import java.util.Objects;

public class ImportsTableData {
    public String name;
    public Enums.ImportType type;
    public int versionsUpdated;
    public String createdBy;
    public String dateCompleted;
    public String status;

    public ImportsTableData(String name, Enums.ImportType type, int versionsUpdated, String createdBy, String dateCompleted, String status) {
        this.name = name;
        this.type = type;
        this.versionsUpdated = versionsUpdated;
        this.createdBy = createdBy;
        this.dateCompleted = dateCompleted;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImportsTableData)) return false;
        ImportsTableData that = (ImportsTableData) o;
        return versionsUpdated == that.versionsUpdated &&
                Objects.equals(name, that.name) &&
                type == that.type &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(dateCompleted, that.dateCompleted) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, versionsUpdated, createdBy, dateCompleted, status);
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", versionsUpdated=" + versionsUpdated +
                ", createdBy='" + createdBy + '\'' +
                ", dateCompleted='" + dateCompleted + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
