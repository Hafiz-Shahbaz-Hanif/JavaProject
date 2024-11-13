package com.DC.utilities.apiEngine.routes.advertising;

import com.DC.utilities.ReadConfig;

public class MediaRoutes {

    private static final String BASE_URI = ReadConfig.getInstance().getFilaBaseUri();
    private static final String EXTERNAL_GATEWAY_URI = ReadConfig.getInstance().getHubExternalGateway();
    private static final String ADVERTISING_ENDPOINT = "/advertising";

    public static String getScratchpadSlicerRoutePath() {
        return BASE_URI + ADVERTISING_ENDPOINT + "/slicer";
    }

    public static String getScratchpadSlicerYoyRoutePath() {
        return BASE_URI + ADVERTISING_ENDPOINT + "/slicer" + "/yoy";
    }

    public static String getScratchpadSlicerSummaryRoutePath() {
        return BASE_URI + ADVERTISING_ENDPOINT + "/slicer" + "/summary";
    }

    public static String getScratchpadSlicerYoyAggbus() {
        return BASE_URI + ADVERTISING_ENDPOINT + "/slicer" + "/yoy/aggbus";
    }

    public static String getMediaReportsRoutePath() {
        return EXTERNAL_GATEWAY_URI + ADVERTISING_ENDPOINT + "/media-reports";
    }

    public static String getMediaReportsSlicerRoutePath() {
        return getMediaReportsRoutePath() + "/slicer";
    }

    public static String getMediaReportsSummaryRoutePath() {
        return getMediaReportsRoutePath() + "/summary";
    }

    public static String getMediaReportsMetricOverviewRoutePath() {
        return getMediaReportsRoutePath() + "/metricoverview?platform=AMAZON";
    }

    public static String getMediaReportsMultiMetricRoutePath() {
        return getMediaReportsRoutePath() + "/multi-metric?platform=AMAZON";
    }

    public static String getMediaReportsSummaryCampaignTypeRoutePath() {
        return getMediaReportsSummaryRoutePath() + "/campaigntype?platform=AMAZON";
    }

    public static String getMediaReportsSummaryDateRoutePath() {
        return getMediaReportsSummaryRoutePath() + "/date?platform=AMAZON";
    }

    public static String getMediaReportsSummarySegmentationRoutePath() {
        return getMediaReportsSummaryRoutePath() + "/segmentation?platform=AMAZON";
    }

    public static String getMediaReportsYoyRoutePath() {
        return getMediaReportsRoutePath() + "/yoy?platform=AMAZON";
    }

    public static String getMediaReportsSlicerCampaignTypeRoutePath() {
        return getMediaReportsSlicerRoutePath() + "/campaigntype?platform=AMAZON";
    }

    public static String getMediaReportsSlicerSegmentationRoutePath() {
        return getMediaReportsSlicerRoutePath() + "/segmentation?platform=AMAZON";
    }
}