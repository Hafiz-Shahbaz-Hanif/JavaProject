package com.DC.utilities.apiEngine.models.requests.hub.aggregation;

import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.FlightdeckRequestBody;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HubAuthAggregationRequestBody {

    public Aggregation aggregation;

    public HubAuthAggregationRequestBody(Aggregation aggregation) {
        this.aggregation = aggregation;
    }

    public static class Aggregation {

        public Object data;
        public String type;
        public String legacyPlatformName;

        public Aggregation(Object data, String type, String legacyPlatformName) {
            this.data = data;
            this.type = type;
            this.legacyPlatformName = legacyPlatformName;
        }

    }
}