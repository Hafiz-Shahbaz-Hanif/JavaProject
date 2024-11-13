package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

public class RoundUpProductResponseBody extends BaseClassAsinSegmentationResponseBody {

    public List<Items> items;

    @JsonIgnoreProperties
    public static class Items {
        public String asin;
        public String asinTitle;
        public String clientAccountName;
        String current;
        String previous;
        String last;
        String isPriorityAsin;
        String asinId;
        String iya;
        String previousPercent;
        String lastPercent;
        public String businessUnitId;
        public String businessUnitName;
        public String platform;
        String changeFromLastYear;
        String changeFromPreviousPeriod;

        public List<String> getRoundUpProductItemsData() {
            return Arrays.asList(asin, asinTitle, clientAccountName,current,previous,last,asinId,changeFromLastYear,
                    changeFromPreviousPeriod);
        }

        public List<String> getRoundUpProductItemsAggBusData() {
            return Arrays.asList(asin, asinTitle, clientAccountName, current, previous, last, isPriorityAsin, asinId,
                    iya, previousPercent, lastPercent, businessUnitId, businessUnitName, platform, changeFromLastYear, changeFromPreviousPeriod);
        }
    }
}
