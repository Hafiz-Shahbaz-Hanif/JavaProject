package com.DC.utilities.apiEngine.models.requests.adc.advertisig.media;

import org.apache.log4j.Logger;

import java.util.List;

public class ScratchpadRequestBody {

    public String interval;
    public String projection;
    public String metric;
    public boolean isSlicerView;
    public List<String> saleMetrics;
    public String startDate;
    public String endDate;
    public int businessUnitId;
    public String platform;
    public String attribution;
    public String periodComparison;
    public String sliceByType;

    public ScratchpadRequestBody(
            String interval,
            String projection,
            String metric,
            boolean isSlicerView,
            List<String> saleMetrics,
            String startDate,
            String endDate,
            int businessUnitId,
            String platform,
            String attribution,
            String periodComparison,
            String sliceByType
    )
    {
        this.interval = interval;
        this.projection = projection;
        this.metric = metric;
        this.isSlicerView = isSlicerView;
        this.saleMetrics = saleMetrics;
        this.startDate = startDate;
        this.endDate = endDate;
        this.businessUnitId = businessUnitId;
        this.platform = platform;
        this.attribution = attribution;
        this.periodComparison = periodComparison;
        this.sliceByType = sliceByType;
        Logger.getLogger(ScratchpadRequestBody.class).info("** Serializing request body for Scratchpad");
    }

    /*public ScratchpadRequestModel(
            String interval,
            String projection,
            String metric,
            boolean isSlicerView,
            List<String> saleMetrics,
            String startDate,
            String endDate,
            int businessUnitId,
            String platform,
            String attribution
    )
    {
        this.interval = interval;
        this.projection = projection;
        this.metric = metric;
        this.isSlicerView = isSlicerView;
        this.saleMetrics = saleMetrics;
        this.startDate = startDate;
        this.endDate = endDate;
        this.businessUnitId = businessUnitId;
        this.platform = platform;
        this.attribution = attribution;
        Logger.getLogger(ScratchpadRequestModel.class).info("** Serializing request body for Scratchpad");
    }*/
}
