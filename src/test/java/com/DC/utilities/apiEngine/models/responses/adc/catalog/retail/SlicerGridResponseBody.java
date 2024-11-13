package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlicerGridResponseBody {

    public Meta meta;

    public Meta getMeta() {
        return meta;
    }
@JsonIgnoreProperties(ignoreUnknown = true)
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

        public String getTotalDisplayCount() {return totalDisplayCount;}

        public String getSortAttribute() {
            return sortAttribute;
        }

        public String isSortAscending() {
            return sortAscending;
        }

    }

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        public String asinId;
        public String asin;
        public String asinTitle;
        public String clientAccountName;
        public List<Data> data;

        public List<String> getItemsData (){
            return Arrays.asList(asinId,asin,asinTitle,clientAccountName);
        }

        public static class Data {
            public String date;
            public String value;

            public List<String> getData() {
                return Arrays.asList(date,value);
            }
        }

    }

}
