package com.DC.utilities.apiEngine.models.responses.adc.analyze.paidMediaReporting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaReportsData {
    private String startDate;
    private String endDate;
    private Double currentValue;
    private List<Object> platformValues = new ArrayList<>();
    private List<Comparison> comparisons = new ArrayList<>();
    private String metric;
    private String message;
    private Integer errorCode;
    private String errorDescription;
    private Total total;
    private List<IntervalComparison> intervalComparisons = new ArrayList<>();
    private Double percentageOfTotal;
    private List<MultiMetricData> data = new ArrayList<>();
    private Double value;
    private Double percentageChange;
    private String period;
    private Boolean preferableChange;
    private List<EntityComparison> entityComparisons = new ArrayList<>();
    private String entity;
    private List<EntityIntervalComparison> entityIntervalComparisons = new ArrayList<>();

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

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public List<Object> getPlatformValues() {
        return platformValues;
    }

    public void setPlatformValues(List<Object> platformValues) {
        this.platformValues = platformValues;
    }

    public List<Comparison> getComparisons() {
        return comparisons;
    }

    public void setComparisons(List<Comparison> comparisons) {
        this.comparisons = comparisons;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
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

    public Double getPercentageOfTotal() {
        return percentageOfTotal;
    }

    public void setPercentageOfTotal(Double percentageOfTotal) {
        this.percentageOfTotal = percentageOfTotal;
    }

    public List<MultiMetricData> getData() {
        return data;
    }

    public void setData(List<MultiMetricData> data) {
        this.data = data;
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

    public Boolean getPreferableChange() {
        return preferableChange;
    }

    public void setPreferableChange(Boolean preferableChange) {
        this.preferableChange = preferableChange;
    }

    public List<EntityComparison> getEntityComparisons() {
        return entityComparisons;
    }

    public void setEntityComparisons(List<EntityComparison> entityComparisons) {
        this.entityComparisons = entityComparisons;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public List<EntityIntervalComparison> getEntityIntervalComparisons() {
        return entityIntervalComparisons;
    }

    public void setEntityIntervalComparisons(List<EntityIntervalComparison> entityIntervalComparisons) {
        this.entityIntervalComparisons = entityIntervalComparisons;
    }
}