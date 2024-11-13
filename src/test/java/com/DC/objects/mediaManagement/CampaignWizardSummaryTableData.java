package com.DC.objects.mediaManagement;

import java.util.List;
import java.util.Objects;

public class CampaignWizardSummaryTableData {

    public String claim;
    public String brand;
    public String ioSegment;
    public String campaignBudget;
    public String campaignName;

    public CampaignWizardSummaryTableData( String claim, String brand, String ioSegment, String campaignBudget, String campaignName) {
        this.claim = claim;
        this.brand = brand;
        this.ioSegment = ioSegment;
        this.campaignBudget = campaignBudget;
        this.campaignName = campaignName;
    }

    public CampaignWizardSummaryTableData() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampaignWizardSummaryTableData)) return false;
        CampaignWizardSummaryTableData that = (CampaignWizardSummaryTableData) o;
        return claim.equals(that.claim) &&
                brand.equals(that.brand) &&
                ioSegment.equals(that.ioSegment) &&
                campaignBudget.equals(that.campaignBudget) &&
                campaignName.equals(that.campaignName);
    }

    @Override
    public int hashCode() {
        return Objects.hash( claim, brand, ioSegment, campaignBudget, campaignName);
    }

    @Override
    public String toString() {
        return "{" +
                ", claim='" + claim + '\'' +
                ", brand='" + brand + '\'' +
                ", ioSegment='" + ioSegment + '\'' +
                ", budget='" + campaignBudget + '\'' +
                ", campaignName='" + campaignName + '\'' +
                '}';
    }
}
