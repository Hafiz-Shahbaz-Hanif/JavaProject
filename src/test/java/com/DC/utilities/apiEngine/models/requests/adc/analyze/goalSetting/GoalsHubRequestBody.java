package com.DC.utilities.apiEngine.models.requests.adc.analyze.goalSetting;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoalsHubRequestBody {

    public String metricId;
    public String interval;
    public String startDate;
    public String endDate;
    public String pivotType;
    public String pivotValue;
    public GoalValueSpecification goalValueSpecification;
    public String goalType;
    public String goalTitle;
    public String goalId;

    public GoalsHubRequestBody(String metricId, String interval, String startDate, String endDate, String pivotType, String pivotValue, GoalValueSpecification goalValueSpecification, String goalType, String goalTitle) {
        this.metricId = metricId;
        this.interval = interval;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pivotType = pivotType;
        this.pivotValue = pivotValue;
        this.goalValueSpecification = goalValueSpecification;
        this.goalType = goalType;
        this.goalTitle = goalTitle;
    }

    public GoalsHubRequestBody(String goalId, GoalValueSpecification goalValueSpecification) {
        this.goalValueSpecification = goalValueSpecification;
        this.goalId = goalId;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class GoalValueSpecification {
        public String minValue;
        public String maxValue;
        public String value;
        public String distributorView;

        public GoalValueSpecification(String minValue, String maxValue, String value, String distributorView) {
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.value = value;
            this.distributorView = distributorView;

        }
    }
}