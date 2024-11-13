package com.DC.objects.mediaManagement;

import com.DC.utilities.enums.Enums;

import java.util.Objects;

public class CampaignWizardKeywordTableData {
    String keyword;
    String matchType;
    String bid;

    public CampaignWizardKeywordTableData(String keyword, String matchType, String bid) {
        this.keyword = keyword;
        this.matchType = matchType;
        this.bid = bid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CampaignWizardKeywordTableData)) return false;
        CampaignWizardKeywordTableData that = (CampaignWizardKeywordTableData) o;
        return keyword.equals(that.keyword) &&
                matchType.equals(that.matchType) &&
                bid.equals(that.bid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, matchType, bid);
    }

    @Override
    public String toString() {
        return "{" +
                "keyword='" + keyword + '\'' +
                ", matchType='" + matchType + '\'' +
                ", bid='" + bid + '\'' +
                '}';
    }

    public String getKeyword() {
        return keyword;
    }

    public String getMatchType() {
        return matchType;
    }

    public String getBid() {
        return bid;
    }
}
