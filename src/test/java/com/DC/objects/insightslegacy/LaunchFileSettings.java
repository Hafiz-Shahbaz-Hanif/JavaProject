package com.DC.objects.insightslegacy;

import java.util.HashMap;
import java.util.Objects;

public class LaunchFileSettings {

    public String batchName;
    public int numberOfTasks;
    public String chainName;
    public FormatBatchProductConfig formatBatchConfig;
    public HashMap<String, String> fieldIdsAndLabels;

    public LaunchFileSettings(String batchName, int numberOfTasks, String chainName, FormatBatchProductConfig formatBatchConfig, HashMap<String, String> fieldIdsAndLabels) {
        this.batchName = batchName;
        this.numberOfTasks = numberOfTasks;
        this.chainName = chainName;
        this.formatBatchConfig = formatBatchConfig;
        this.fieldIdsAndLabels = fieldIdsAndLabels;
    }

    public LaunchFileSettings() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LaunchFileSettings)) return false;
        LaunchFileSettings that = (LaunchFileSettings) o;
        return batchName == that.batchName &&
                numberOfTasks == that.numberOfTasks &&
                chainName == that.chainName &&
                formatBatchConfig == that.formatBatchConfig &&
                fieldIdsAndLabels == that.fieldIdsAndLabels;
    }

    @Override
    public int hashCode() {
        return Objects.hash(batchName, numberOfTasks, chainName, formatBatchConfig, fieldIdsAndLabels);
    }

    @Override
    public String toString() {
        return "LaunchFileSettings{" +
                ", batchName='" + batchName + '\'' +
                ", numberOfTasks='" + numberOfTasks + '\'' +
                ", chainName='" + chainName + '\'' +
                ", formatBatchConfig='" + formatBatchConfig + '\'' +
                ", fieldIdsAndLabels='" + fieldIdsAndLabels + '\'' +
                '}';
    }
}
