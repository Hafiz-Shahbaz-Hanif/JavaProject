package com.DC.utilities.apiEngine.models.responses.insights;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DVAExportData {
        @JsonProperty("file")
        public String file;
        @JsonProperty("url")
        public String url;
}
