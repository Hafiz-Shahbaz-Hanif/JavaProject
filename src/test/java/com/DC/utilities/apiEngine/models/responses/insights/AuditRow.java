package com.DC.utilities.apiEngine.models.responses.insights;
import java.util.Objects;

public class AuditRow {
    public String mappingHeader;
    public String sotHeader;
    public String sotHasValue;
    public Boolean sotBooleanValue;
    public String pdpHasValue;
    public String sotValue;
    public Object pdpValue;
    public String auditResult;
    public Integer sotValueCount;
    public Integer pdpValueCount;
    public String valueCountMatch;
    public String dateValue;
    public String retailerValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditRow that = (AuditRow) o;
        return mappingHeader == that.mappingHeader
                && sotHeader.equals(that.sotHeader)
                && sotHasValue.equals(that.sotHasValue)
                && sotBooleanValue.equals(that.sotBooleanValue)
                && pdpHasValue.equals(that.pdpHasValue)
                && sotValue.equals(that.sotValue)
                && pdpValue.equals(that.pdpValue)
                && auditResult == that.auditResult
                && sotValueCount == that.sotValueCount
                && pdpValueCount == that.pdpValueCount
                && valueCountMatch == that.valueCountMatch
                && dateValue == that.dateValue
                && retailerValue.equals(that.retailerValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mappingHeader, sotHeader, sotHasValue, sotBooleanValue, pdpHasValue, sotValue, pdpValue,
                auditResult, sotValueCount, pdpValueCount, valueCountMatch, dateValue, retailerValue);
    }

    @Override
    public String toString() {
        return "{" +
                "mappingHeader'" + mappingHeader + '\'' +
                ", sotHeader:" + sotHeader +
                ", sotHasValue:" + sotHasValue +
                ", sotBooleanValue:" + sotBooleanValue +
                ", pdpHasValue:" + pdpHasValue + '\'' +
                ", sotValue:" + sotValue + '\'' +
                ", pdpValue:" + pdpValue + '\'' +
                ", auditResult:" + auditResult +
                ", sotValueCount:" + sotValueCount +
                ", pdpValueCount:" + pdpValueCount +
                ", valueCountMatch:" + valueCountMatch +
                ", dateValue:" + dateValue +
                ", retailerValue:" + retailerValue +
                '}';
    }
}
