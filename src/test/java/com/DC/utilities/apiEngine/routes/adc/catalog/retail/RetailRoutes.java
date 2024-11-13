package com.DC.utilities.apiEngine.routes.adc.catalog.retail;

import com.DC.utilities.ReadConfig;

public class RetailRoutes {

    private static final String BASE_URI = ReadConfig.getInstance().getFilaBaseUri();
    public static final String RETAIL_CATALOG = "/catalog";
    public static final String RETAIL_SCRATCHPAD_ENDPOINT = "/scratchpad";
    public static final String RETAIL_SCRATCHPAD_SUMMARY_ENDPOINT ="/scratchpad/summary";
    public static final String SLICER_CHART ="/slicer/chart";
    public static final String SLICER_GRID ="/slicer/grid";
    public static final String RETAIL_SCRATCHPAD = "/catalog/scratchpad";
    public static final String SLICER_CHART_AGG_BU = "/scratchpad/slicer/chart/aggbus";
    public static final String SLICER_GRID_AGG_BU = "/scratchpad/slicer/grid/aggbus";
    public static final String SUMMARY_AGG_BU = "/scratchpad/summary/aggbus";
    public static final String SCRATCHPAD_AGG_BU = "/scratchpad/aggbus";
    public static final String ASIN_SEGMENTATION = "/segmentation/asin/all";
    public static final String ASIN_SEGMENTATION_PO_GOLDEN_DATA = "/po/golden/data/all";
    public static final String ASIN_SEGMENTATION_MASTER_DATA = "/master/data/all";
    public static final String CREATE_ASIN = "/segmentation/value/asin";
    public static final String RETAIL_SALESCORRELATION_DETAIL = "/scm/detail";
    public static final String RETAIL_SALESCORRELATION_SUMMARY = "/scm/summary";
    public static final String RETAIL_SALESCORRELATION_ALL = "/scm/all";
    public static final String RETAIL_REPORT = "/report";
    public static final String RETAIL_ROUNDUP_ALL_ENDPOINT = "/roundup/all";
    public static final String RETAIL_ROUNDUP_PRODUCT_ENDPOINT = "/roundup/product";
    public static final String RETAIL_ROUNDUP_SEGMENTATION_ENDPOINT = "/roundup/segmentation";
    public static final String RETAIL_ROUNDUP_AGGBUS_ENDPOINT = "/roundup/aggbus/";
    public static final String RETAIL_GAINERS_DRAINERS_ALL = "/retail/gd/all";
    public static final String RETAIL_GAINERS_DRAINERS_INTERVALS = "/report/data/health";
    private static String SEGMENTATIONS_GROUPED = "/catalog/segmentation/value/asin/grouped/";
    private static String ROUND_UP_EXPORT = "/roundup/export";
    private static String ROUND_UP_MULTI_BU_EXPORT = "/roundup/aggbus/export";


    public static String getRetailScratchpadRoutePath() {
        return BASE_URI + RETAIL_SCRATCHPAD;
    }

    public static String getScratchpadRoutePath(String endpoint) {
        return BASE_URI + RETAIL_CATALOG + endpoint;
    }

    public static String getRetailScratchpadRoutePath(String endpoint) {
        return BASE_URI + RETAIL_SCRATCHPAD + endpoint;
    }
    public static String getAsinSegmentationRoutePath(String endpoint) {
        return BASE_URI + RETAIL_CATALOG + endpoint;
    }

    public static String getSalesCorrelationRoutePath(String endpoint) {
        return BASE_URI + RETAIL_CATALOG + endpoint;
    }

    public static String getGainerAndDrainersRoutePath() { return BASE_URI + RETAIL_CATALOG + RETAIL_GAINERS_DRAINERS_ALL;}

    public static String getGainerAndDrainersIntervalRoutePath() { return BASE_URI + RETAIL_CATALOG + RETAIL_GAINERS_DRAINERS_INTERVALS;}

    public static String getSegmentationGroupsForBusinessUnitRoutePath(int businessUnitId) {
        return BASE_URI + SEGMENTATIONS_GROUPED + businessUnitId;
    }

    public static String getRetailRoundupAllRoutePath() {
        return BASE_URI + RETAIL_CATALOG + RETAIL_REPORT + RETAIL_ROUNDUP_ALL_ENDPOINT;
    }

    public static String getRetailRoundupProductRoutePath() {
        return BASE_URI + RETAIL_CATALOG + RETAIL_REPORT + RETAIL_ROUNDUP_PRODUCT_ENDPOINT;
    }

    public static String getRetailRoundupSegmentationRoutePath() {
        return BASE_URI + RETAIL_CATALOG + RETAIL_REPORT + RETAIL_ROUNDUP_SEGMENTATION_ENDPOINT;
    }

    public static String getRetailRoundupAggBusRoutePath(String endpoint) {
        return BASE_URI + RETAIL_CATALOG + RETAIL_REPORT + RETAIL_ROUNDUP_AGGBUS_ENDPOINT + endpoint;
    }

    public static String getRetailRoundupExportRoutePath() {
        return BASE_URI + RETAIL_CATALOG + RETAIL_REPORT + ROUND_UP_EXPORT;
    }

    public static String getRetailRoundupMultiBuExportRoutePath() {
        return BASE_URI + RETAIL_CATALOG + RETAIL_REPORT + ROUND_UP_MULTI_BU_EXPORT;
    }
}