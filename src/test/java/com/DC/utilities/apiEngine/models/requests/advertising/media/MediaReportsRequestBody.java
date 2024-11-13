package com.DC.utilities.apiEngine.models.requests.advertising.media;

import org.apache.log4j.Logger;

import java.util.List;

public class MediaReportsRequestBody {

    public String startDate;
    public String endDate;
    public List<String> segmentationFilters;
    public List<StaticFilter> staticFilters;
    public String interval;
    public List<String> metrics;
    public String projection;
    public List<String> comparisons;
    public boolean splitByBusinessUnit;
    public String segmentationType;

    public MediaReportsRequestBody(
            String startDate,
            String endDate,
            List<String> segmentationFilters,
            List<StaticFilter> staticFilters,
            String interval,
            List<String> metrics,
            String projection,
            List<String> comparisons,
            boolean splitByBusinessUnit,
            String segmentationType
    ) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.segmentationFilters = segmentationFilters;
        this.staticFilters = staticFilters;
        this.interval = interval;
        this.metrics = metrics;
        this.projection = projection;
        this.comparisons = comparisons;
        this.splitByBusinessUnit = splitByBusinessUnit;
        this.segmentationType = segmentationType;
        Logger.getLogger(MediaReportsRequestBody.class).info("** Serializing request body for MediaScratchpad");
    }
}