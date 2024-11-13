package com.DC.utilities.apiEngine.models.responses.adc.catalog.retail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GainersAndDrainersIntervalResponseBody {

    public DataHealthItemInfo dataHealthItemInfo;
    public List<DataHealthIntervalDates> dataHealthIntervalDates;

    public DataHealthItemInfo getDataHealthItemInfo() {return dataHealthItemInfo;}
    public List<DataHealthIntervalDates> getDataHealthIntervalDates(){ return dataHealthIntervalDates;}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataHealthItemInfo{
        public String clientAccountName;
        public String dataSource;
        public String distributorView;
        public String metric;
        public String issueIdentified;

        public List<String> getDataHealthItemInfoValues() {
            return Arrays.asList(clientAccountName,dataSource,distributorView,metric,issueIdentified);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataHealthIntervalDates {
        public String interval;
        public List<String> dates;

        public String getInterval(){ return interval;}

        public List<String> getDates() { return dates;}
    }
}
