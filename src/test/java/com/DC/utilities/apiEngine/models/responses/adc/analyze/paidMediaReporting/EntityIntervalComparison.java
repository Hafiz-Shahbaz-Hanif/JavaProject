package com.DC.utilities.apiEngine.models.responses.adc.analyze.paidMediaReporting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityIntervalComparison {
    private Total total;
    private List<EntityComparison> entityData = new ArrayList<>();
    private String interval;

    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

    public List<EntityComparison> getEntityData() {
        return entityData;
    }

    public void setEntityData(List<EntityComparison> entityData) {
        this.entityData = entityData;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }
}