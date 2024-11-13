package com.DC.utilities.apiEngine.models.requests.adc.analyze.goalSetting;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllGoalsHubRequestBody {

    public String timeline;
    public String goalInterval;
    public List<String> metricList;
    public List<String> brand;

    public AllGoalsHubRequestBody(String timeline, String goalInterval, List<String> metricList, List<String> brandList) {
        this.timeline = timeline;
        this.goalInterval = goalInterval;
        this.metricList = metricList;
        this.brand = brandList;
    }
}