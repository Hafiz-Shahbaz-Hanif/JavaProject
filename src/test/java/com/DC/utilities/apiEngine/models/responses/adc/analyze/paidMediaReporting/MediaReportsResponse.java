package com.DC.utilities.apiEngine.models.responses.adc.analyze.paidMediaReporting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaReportsResponse {
    private List<MediaReportsData> data = new ArrayList<>();

    public List<MediaReportsData> getData() {
        return data;
    }

    public void setData(List<MediaReportsData> data) {
        this.data = data;
    }
}
