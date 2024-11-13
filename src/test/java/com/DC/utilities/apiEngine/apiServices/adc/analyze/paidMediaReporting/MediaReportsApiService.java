package com.DC.utilities.apiEngine.apiServices.adc.analyze.paidMediaReporting;

import com.DC.utilities.apiEngine.models.requests.advertising.media.MediaReportsRequestBody;
import com.DC.utilities.apiEngine.models.requests.advertising.media.StaticFilter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MediaReportsApiService {

    public static MediaReportsRequestBody metricOverviewWithActualDataRequestBody(String campaignType) {
        var staticFilters = getStaticFilters(campaignType);
        var metrics = List.of("Spend", "Sales", "ROAS", "Impressions", "Clicks", "CTR", "CVR", "CPC", "SPC", "CPA");
        return new MediaReportsRequestBody(getStartDate(), getEndDate(), List.of(),
                staticFilters,
                "DAILY", metrics,
                "Actual",
                List.of(), false, null);
    }

    public static MediaReportsRequestBody mMGraphWithActualDataRequestBody(String campaignType) {
        var staticFilters = getStaticFilters(campaignType);
        var metrics = List.of("Spend", "Sales", "ROAS", "Impressions", "Clicks", "Conversions", "Conversion Rate",
                "CTR", "CVR", "CPC", "SPC", "CPA", "SPM", "CPM", "DPV", "Branded Searches", "VCR", "NTB DPV", "NTB Sales",
                "NTB Order Rate");
        return new MediaReportsRequestBody(getStartDate(), getEndDate(),
                List.of(), staticFilters, "DAILY", metrics,
                "Actual", List.of(), false, null);
    }

    public static MediaReportsRequestBody summaryTableSliceByCampaignTypeRequestBody(String campaignType) {
        var staticFilters = getStaticFilters(campaignType);
        var metrics = List.of("Spend", "Sales", "ROAS", "Impressions", "Clicks", "Conversions",
                "Conversion Rate", "CTR", "CVR", "CPC", "SPC", "CPA", "SPM", "CPM", "DPV", "Branded Searches",
                "VCR", "NTB DPV", "NTB Sales", "NTB Order Rate");

        return new MediaReportsRequestBody("2023-09-01", "2023-09-30", List.of(),
                staticFilters, "DAILY", metrics, "Actual",
                List.of("Last Year"), false, null);
    }

    public static MediaReportsRequestBody summaryTableSliceByDateRequestBody(String campaignType) {
        var staticFilters = getStaticFilters(campaignType);
        var metrics = List.of("Spend", "Sales", "ROAS", "Impressions", "Clicks", "Conversions",
                "Conversion Rate", "CTR", "CVR", "CPC", "SPC", "CPA", "SPM", "CPM", "DPV", "Branded Searches",
                "VCR", "NTB DPV", "NTB Sales", "NTB Order Rate");
        return new MediaReportsRequestBody("2023-09-01", "2023-09-30", List.of(), staticFilters,
                "DAILY", metrics, "Actual", List.of("Last Year"), false, null);
    }

    public static MediaReportsRequestBody summaryTableSliceBySegmentationRequestBody(String campaignType) {
        var staticFilters = getStaticFilters(campaignType);
        var metrics = List.of("Spend", "Sales", "ROAS", "Impressions", "Clicks", "Conversions",
                "Conversion Rate", "CTR", "CVR", "CPC", "SPC", "CPA", "SPM", "CPM", "DPV", "Branded Searches",
                "VCR", "NTB DPV", "NTB Sales", "NTB Order Rate");
        return new MediaReportsRequestBody("2023-09-01", "2023-09-30", List.of(),
                staticFilters, "DAILY", metrics, "Actual",
                List.of("Last Year"), false, "Brand");
    }

    public static MediaReportsRequestBody yoyRequestBody(String campaignType) {
        var staticFilters = getStaticFilters(campaignType);
        return new MediaReportsRequestBody("2023-09-01", "2023-09-30", List.of(), staticFilters,
                "DAILY", List.of("DPV", "NTB DPV", "VCR", "Branded Searches"), "Actual",
                List.of("Last Year"), false, null);
    }

    public static MediaReportsRequestBody slicerSliceByCampaignTypeRequestBody(String campaignType) {
        var staticFilters = getStaticFilters(campaignType);
        return new MediaReportsRequestBody("2023-09-01", "2023-09-30", List.of(), staticFilters,
                "DAILY", List.of("DPV", "NTB DPV", "VCR", "Branded Searches"), "Actual",
                List.of(), false, null);
    }

    public static MediaReportsRequestBody slicerSliceBySegmentationRequestBody(String campaignType) {
        var staticFilters = getStaticFilters(campaignType);
        return new MediaReportsRequestBody("2023-09-01", "2023-09-30", List.of(), staticFilters,
                "DAILY", List.of("DPV", "NTB DPV", "VCR", "Branded Searches"), "Actual",
                List.of(), false, "Brand");
    }

    private static String getStartDate() {
        return LocalDate.now().minusMonths(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private static String getEndDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private static List<StaticFilter> getStaticFilters(String campaignType) {
        var campaignTypeFilter = new StaticFilter("Campaign Type", campaignType);
        var staticFilters = new ArrayList<StaticFilter>();
        staticFilters.add(new StaticFilter("Platform", "Amazon"));
        staticFilters.add(new StaticFilter("Attribution", "ATTR_14D"));
        if (StringUtils.isNotBlank(campaignType)) {
            staticFilters.add(campaignTypeFilter);
        }
        return staticFilters;
    }
}