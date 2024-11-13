package com.DC.utilities.apiEngine.models.responses.adc.manage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchTermManagementResponseBody {
    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public static class Items {
        public String searchTermId;
        public String searchTerm;
        public String businessUnitSearchTermId;
        public String scrapeFrequency;
        public String visibility;
        public String scrapeDepth;
        public String priorityTerm;
        public List<Groups> groups;
        public List<RetailerPlatforms> retailerPlatforms;

        public static class Groups {
            public String groupId;
            public String groupName;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class RetailerPlatforms {
            public String retailerId;
            public String retailerName;
            public List<Platforms> platforms;

            public List<String> getRetailerPlatformsData() {
                return Arrays.asList(retailerId, retailerName);
            }
        }

        public static class Platforms {
            public String platformId;
            public String platformName;
            public String region;
            public String businessUnitProvisionId;
        }


    }
}
