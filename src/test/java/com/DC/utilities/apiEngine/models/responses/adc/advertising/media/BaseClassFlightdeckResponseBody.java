package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

public class BaseClassFlightdeckResponseBody {

    public Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {
        public String currentPage;
        public String pageCount;
        public String pageSize;
        public String totalCount;
        public String totalDisplayCount;
        public String sortAttribute;
        public String sortAscending;

        public String getCurrentPage() {
            return currentPage;
        }

        public String getPageCount() {
            return pageCount;
        }

        public String getPageSize() {
            return pageSize;
        }

        public String getTotalCount() {
            return totalCount;
        }

        public String getTotalDisplayCount() {
            return totalDisplayCount;
        }

        public String getSortAttribute() {
            return sortAttribute;
        }

        public String isSortAscending() {
            return sortAscending;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {

        public String spend;
        public String sales;
        public String clicks;
        public String roas;
        public String cpc;
        public String cvr;
        public String cpa;
        public String impressions;
        public String ctr;

        public List<String> getItemsDataBaseClass() {
            return Arrays.asList(spend, sales, clicks, roas, cpc, cvr, cpa,
                    impressions, ctr);

        }
    }
}
