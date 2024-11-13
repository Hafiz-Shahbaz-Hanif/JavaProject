package com.DC.utilities.apiEngine.models.responses.adc.analyze.paidMediaReporting;

import java.util.ArrayList;
import java.util.List;

public class MultiMetricData {

    private String metric;
    private Total total;
    private List<IntervalComparison> intervalComparisons = new ArrayList<>();

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

    public List<IntervalComparison> getIntervalComparisons() {
        return intervalComparisons;
    }

    public void setIntervalComparisons(List<IntervalComparison> intervalComparisons) {
        this.intervalComparisons = intervalComparisons;
    }
}