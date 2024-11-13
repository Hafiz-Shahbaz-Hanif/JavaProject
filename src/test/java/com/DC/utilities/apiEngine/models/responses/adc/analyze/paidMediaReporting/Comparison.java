package com.DC.utilities.apiEngine.models.responses.adc.analyze.paidMediaReporting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comparison {
    private String startDate;
    private String endDate;
    private Double value;
    private Double percentageChange;
    private String period;
    private List<Object> platformValues = new ArrayList<>();
    private Boolean preferableChange;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(Double percentageChange) {
        this.percentageChange = percentageChange;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<Object> getPlatformValues() {
        return platformValues;
    }

    public void setPlatformValues(List<Object> platformValues) {
        this.platformValues = platformValues;
    }

    public Boolean getPreferableChange() {
        return preferableChange;
    }

    public void setPreferableChange(Boolean preferableChange) {
        this.preferableChange = preferableChange;
    }
}
