package com.DC.utilities.apiEngine.routes.adc.advertising.media;

import com.DC.utilities.ReadConfig;

public class MediaRoutes {

    private static final String BASE_URI = ReadConfig.getInstance().getFilaBaseUri();
    private static final String ADVERTISING_ENDPOINT = "/advertising";
    private static final String HUB_EXTERNAL_GATEWAY = ReadConfig.getInstance().getHubExternalGateway(); 
    private static final String REPORTING_DASHBOARD_ENDPOINT = "/advertising/reporting/dashboard";
    private static final String MEDIA_FLIGHTDECK = "/advertising/flightdeck/";
    private static final String MPV_FILTERS = "/advertising/segmentationLink";
    private static String segmentationTypeEntity = "/admin/segmentation/type/entity/all/";
    public static String campaignEndpoint = "/campaign/all";
    public static String keywordsByCampaignEndpoint = "/keyword/bycampaign/all";
    public static String keywordsRolledUpEndpoint = "/keyword/rolledup/all";
    public static String asinEndpoint = "/asin/all";
    public static String keywordsByAdGroupEndpoint = "/keyword/byadgroup/all";
    public static String csqEndpoint = "/csq/all";
    public static String itemEndpoint = "/item/rolledup/all";
    public static String lineItemEndpoint = "/lineitem/all";
    public static String productEndpoint = "/product/all";
    public static String adGroupEndpoint = "/adgroup/all";

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
    
    public static String getReportingDashboardRoutePath() {
        return BASE_URI + REPORTING_DASHBOARD_ENDPOINT;
    }

    public static String getMediaFlightDeckRoutePath(String platform) {
        return BASE_URI + MEDIA_FLIGHTDECK + platform + "/campaign/all";
    }

    public static String getMediaFlightDeckRoutePath(String platform, String endpoint) {
        return BASE_URI + MEDIA_FLIGHTDECK + platform + endpoint;
    }

    public static String getSegmentationTypeEntityForBusinessUnitRoutePath(int businessUnitId) {
        return BASE_URI + segmentationTypeEntity + businessUnitId;
    }

    public static String getMultiPlatformViewFiltersRoutePath() {
        return BASE_URI + MPV_FILTERS;
    }

    public static String getMultiPlatformViewFilterCreationRoutePath() {
        return BASE_URI + MPV_FILTERS;
    }

    public static String getMultiPlatformViewFilterDeleteRoutePath(String linkId) {
        return BASE_URI + MPV_FILTERS + "/" + linkId;
    }
    
    public static String getReportingDashboardExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY + REPORTING_DASHBOARD_ENDPOINT;
    }

	public static String getMultiPlatformViewFiltersExternalGatewayRoutePath() {
		return HUB_EXTERNAL_GATEWAY + MPV_FILTERS;
	}

	public static String getSegmentationTypeEntityForBusinessUnitExternalGatewayRoutePath(int businessUnitId) {
		return HUB_EXTERNAL_GATEWAY + segmentationTypeEntity + businessUnitId;
	}

	public static String getMultiPlatformViewFilterCreationExternalGatewayRoutePath() {
		return HUB_EXTERNAL_GATEWAY + MPV_FILTERS;
	}

	public static String getMultiPlatformViewFilterDeleteExternalGatewayRoutePath(String linkId) {
		return HUB_EXTERNAL_GATEWAY + MPV_FILTERS + "/" + linkId;
	}
}
