package com.DC.utilities.apiEngine.models.responses.productVersioning;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Company {

    public String _id;

    public int _version;

    public Date dateCreated;

    public Date dateUpdated;

    public String name;

    public String companyPropertiesId;

    public List<CompanyRetailers> retailers;

    public List<CompanyLocales> locales;

    public List<CompanyCampaign> campaigns;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return _version == company._version &&
                _id.equals(company._id) &&
                dateCreated.equals(company.dateCreated) &&
                dateUpdated.equals(company.dateUpdated) &&
                name.equals(company.name) &&
                Objects.equals(companyPropertiesId, company.companyPropertiesId) &&
                retailers.equals(company.retailers) &&
                locales.equals(company.locales) &&
                campaigns.equals(company.campaigns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, _version, dateCreated, dateUpdated, name, companyPropertiesId, retailers, locales, campaigns);
    }

    @Override
    public String toString() {
        return "{" +
                "_id='" + _id + '\'' +
                ", _version=" + _version +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                ", name='" + name + '\'' +
                ", companyPropertiesId='" + companyPropertiesId + '\'' +
                ", retailers=" + retailers +
                ", locales=" + locales +
                ", campaigns=" + campaigns +
                '}';
    }

    public static class CompanyRetailers {

        public String systemRetailerId;

        public int retailerDomainId;

        public String clientRetailerName;

        public CompanyRetailers(String systemRetailerId, int retailerDomainId, String clientRetailerName) {
            this.systemRetailerId = systemRetailerId;
            this.retailerDomainId = retailerDomainId;
            this.clientRetailerName = clientRetailerName;
        }

        public CompanyRetailers() {}

        @Override
        public String toString() {
            return "{" +
                    "systemRetailerId='" + systemRetailerId + '\'' +
                    ", retailerDomainId=" + retailerDomainId +
                    ", clientRetailerName='" + clientRetailerName + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompanyRetailers that = (CompanyRetailers) o;
            return retailerDomainId == that.retailerDomainId && systemRetailerId.equals(that.systemRetailerId) && clientRetailerName.equals(that.clientRetailerName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(systemRetailerId, retailerDomainId, clientRetailerName);
        }
    }

    public static class CompanyLocales {

        public String localeId;
        public String localeName;
        public CompanyLocales(String localeId, String localeName) {
            this.localeId = localeId;
            this.localeName = localeName;
        }
        public CompanyLocales() {}
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompanyLocales that = (CompanyLocales) o;
            return localeId.equals(that.localeId) && localeName.equals(that.localeName);
        }
        @Override
        public int hashCode() {
            return Objects.hash(localeId, localeName);
        }
        @Override
        public String toString() {
            return "{" +
                    "localeId='" + localeId + '\'' +
                    ", localeName='" + localeName + '\'' +
                    '}';
        }
    }
    public static class CompanyCampaign {

        public String id;

        public String name;

        public Date startDate;

        public Date endDate;

        public CompanyCampaign(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public CompanyCampaign(String id, String name, Date startDate, Date endDate) {
            this.id = id;
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
        }
        public CompanyCampaign() {}
        @Override
        public String toString() {
            return "{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    '}';
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CompanyCampaign that = (CompanyCampaign) o;
            return id.equals(that.id) && name.equals(that.name) && Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
        }
        @Override
        public int hashCode() {
            return Objects.hash(id, name, startDate, endDate);
        }
    }

    public String getLocaleId(String localeName) throws Exception {
        return isBlank(localeName) | Objects.equals(localeName, "null") ? null : this.locales.stream()
                .filter(locale -> locale.localeName.equals(localeName))
                .findFirst()
                .orElseThrow(() -> new Exception("Locale not found"))
                .localeId;
    }
    public String getRetailerId(String retailerName) throws Exception {
        return isBlank(retailerName) | Objects.equals(retailerName, "null") ? null : this.retailers.stream()
                .filter(retailer -> retailer.clientRetailerName.equals(retailerName))
                .findFirst()
                .orElseThrow(() -> new Exception("Retailer not found"))
                .systemRetailerId;
    }

    public String getCampaignId(String campaignName) throws Exception {
        return isBlank(campaignName) | Objects.equals(campaignName, "null") ? null : this.campaigns.stream()
                .filter(campaign -> campaign.name.equals(campaignName))
                .findFirst()
                .orElseThrow(() -> new Exception("Campaign not found"))
                .id;
    }

    public String getLocaleName(String localeId) throws Exception {
        return isBlank(localeId) ? "" : this.locales.stream()
                .filter(locale -> locale.localeId.equals(localeId))
                .findFirst()
                .orElseThrow(() -> new Exception("Locale not found"))
                .localeName;
    }

    public String getRetailerName(String retailerId) throws Exception {
        return isBlank(retailerId) ? "" : this.retailers.stream()
                .filter(retailer -> retailer.systemRetailerId.equals(retailerId))
                .findFirst()
                .orElseThrow(() -> new Exception("Retailer not found"))
                .clientRetailerName;
    }

    public String getCampaignName(String campaignId) throws Exception {
        return isBlank(campaignId) ? "" : this.campaigns.stream()
                .filter(campaign -> campaign.id.equals(campaignId))
                .findFirst()
                .orElseThrow(() -> new Exception("Campaign not found"))
                .name;

    }
}
