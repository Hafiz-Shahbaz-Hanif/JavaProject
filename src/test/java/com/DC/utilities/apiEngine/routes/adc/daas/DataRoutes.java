package com.DC.utilities.apiEngine.routes.adc.daas;

import com.DC.utilities.ReadConfig;

public class DataRoutes {
	
    private static final String BASE_URI = ReadConfig.getInstance().getFilaBaseUri();
    private static final String HUB_EXTERNAL_GATEWAY = ReadConfig.getInstance().getHubExternalGateway();  
    private static final String DAAS_AUTOMATED_DATA_TEMPLATE = "/daas/v1/automatedDataDelivery/templates";
    private static final String DAAS_AUTOMATED_DATA_SOURCE_ORIGINS = "/daas/v1/automatedDataDelivery/dataSourceOrigins";

    public static String getDaasAutomatedDataTemplateRoutePath() {
        return BASE_URI + DAAS_AUTOMATED_DATA_TEMPLATE;
    }

    public static String getDaasAutomatedDataSourceOriginsRoutePath() {
        return BASE_URI + DAAS_AUTOMATED_DATA_SOURCE_ORIGINS;
    }
    
    public static String getDaasAutomatedDataSourceOriginsExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY + DAAS_AUTOMATED_DATA_SOURCE_ORIGINS;
    }


}