package com.DC.utilities.apiEngine.routes.insights;
import com.DC.utilities.ReadConfig;

public class ProductAuditorRoutes {

    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();
    
    private static final String PRODUCT_AUDITOR_HOST = READ_CONFIG.getInsightsDigitalShelfAuditEndpoint();

    public static String getProductAuditorHost() {return PRODUCT_AUDITOR_HOST;}

    public static String getProductAuditorExportHost() {return getProductAuditorHost() + "/export";}
}
