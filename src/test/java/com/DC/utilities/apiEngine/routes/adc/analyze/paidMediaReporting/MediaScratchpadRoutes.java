package com.DC.utilities.apiEngine.routes.adc.analyze.paidMediaReporting;

import com.DC.utilities.ReadConfig;

public class MediaScratchpadRoutes {

    public static final String HUB_EXTERNAL_GATEWAY = ReadConfig.getInstance().getHubExternalGateway();
    public static final String INVENTORY_DSP_SITES = "/advertising/inventory/filter/dsp/sites";
    public static final String INVENTORY_DSP_SUPPLY = "/advertising/inventory/filter/dsp/supply";
    public static final String DSP_REPORTING = "/advertising/dsp/reporting";
    public static final String GEOGRAPHY_DSP_CITY = "/advertising/flightdeck/filters/dsp/city";
    public static final String GEOGRAPHY_DSP_REGION = "/advertising/flightdeck/filters/dsp/region";
    public static final String GEOGRAPHY_DSP_COUNTRY = "advertising/flightdeck/filters/dsp/country";

    public static String getInventoryDSPSites() {
        return HUB_EXTERNAL_GATEWAY + INVENTORY_DSP_SITES;
    }
}
