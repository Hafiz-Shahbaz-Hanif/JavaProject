package com.DC.objects.productVersioning;

import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public abstract class ImportExportBase {

    public String _id;

    public int _version;

    public String createdBy;

    public Date startedOn;

    public Date completedOn;

    public String companyId;

    public Enums.ProcessStatus status;

    public String source;

    public ExportRecord.ExportWorkbook errorReport;

    public ImportRecord.StageStatusCounts stageStatusCounts;

    public ImportRecord.ProcessingIds processingIds;

    public List<String> standardizedDataIds;

    public List<String> transformedDataIds;

    public List<String> publishedIds;

    public List<String> errors;

    public Object meta;

    public SlackConfig slackConfig;

    public boolean allowPartialFailure;

    public boolean useQueues;

    public boolean cleanUpStandardData;

    public boolean cleanUpTranslatedData;

    public boolean cleanUpTransformedData;

    public String message;

    public int totalErrorCount;

    public Object failedJobCount;

    public List<String> standardMessageIds;

    public String pairedTrackingId;

    public static class StageStatusCounts {

        public StageStatusProperties standardize;

        public StageStatusProperties transform;

        public StageStatusProperties publish;

        @Override
        public String toString() {
            return "{" +
                    "standardize=" + standardize +
                    ", transform=" + transform +
                    ", publish=" + publish +
                    '}';
        }

        public static class StageStatusProperties {

            public int completed;

            public int failed;

            public int unknown;

            public int excluded;

            @Override
            public String toString() {
                return "{" +
                        "completed=" + completed +
                        ", failed=" + failed +
                        ", unknown=" + unknown +
                        ", excluded=" + excluded +
                        '}';
            }
        }
    }

    public static class ProcessingIds {
        public List<String> transform;
        public List<String> publish;
    }

    public String getImportFailedJobCountErrorMessage(String jobId) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.convertValue(failedJobCount, JsonNode.class);
        dataNode = dataNode.get(jobId).get("errors").get(0);
        dataNode = objectMapper.readTree(dataNode.asText());
        return dataNode.get("errorMessage").asText();
    }

    public String getImportFailedJobCountErrorMessage() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode dataNode = objectMapper.convertValue(failedJobCount, JsonNode.class);
        dataNode = dataNode.fields().next().getValue().get("errors").get(0);
        dataNode = objectMapper.readTree(dataNode.asText());
        return dataNode.get("errorMessage").asText();
    }
}
