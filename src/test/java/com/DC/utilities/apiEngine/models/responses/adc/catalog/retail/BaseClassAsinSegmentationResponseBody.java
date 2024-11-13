package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

public class BaseClassAsinSegmentationResponseBody {

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

}
