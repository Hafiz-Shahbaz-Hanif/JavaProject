package com.DC.utilities.apiEngine.routes.adc.catalog.search;

import com.DC.utilities.ReadConfig;

public class SearchRoutes {
	
    private static final String BASE_URI = ReadConfig.getInstance().getFilaBaseUri();
    private static final String HUB_EXTERNAL_GATEWAY = ReadConfig.getInstance().getHubExternalGateway();  
    private static final String SHARE_OF_VOICE_BRAND = "/catalog/report/sov/brand";
	private static final String ADMIN_MANAGE_QUERIES_EXPORT = "/admin/keyword/export";
	private static String ADMIN_MANAGE_QUERIES_IMPORT = "/admin/keyword/import/";
	private static final String SHARE_OF_VOICE_BRAND_AMAZON_PLATFORM = "/catalog/report/sov/sfr/AMAZON/US";


    public static String getShareOfVoiceBrandRoutePath() {
        return BASE_URI + SHARE_OF_VOICE_BRAND;
    }
	
	public static String getAdminManageQueriesExportRoutePath() {
		return BASE_URI + ADMIN_MANAGE_QUERIES_EXPORT;
	}
	
	public static String getAdminManageQueriesImportRoutePath(int businessUnitId) {
		return BASE_URI + ADMIN_MANAGE_QUERIES_IMPORT + businessUnitId;
	}
	
    public static String getShareOfVoiceBrandExternalGatewayRoutePath() {
        return HUB_EXTERNAL_GATEWAY + SHARE_OF_VOICE_BRAND;
    }
    
	public static String getAdminManageQueriesExportExternalGatewayRoutePath() {
		return HUB_EXTERNAL_GATEWAY + ADMIN_MANAGE_QUERIES_EXPORT;
	}
	
	public static String getAdminManageQueriesImportExternalGatewayRoutePath(int businessUnitId) {
		return HUB_EXTERNAL_GATEWAY + ADMIN_MANAGE_QUERIES_IMPORT + businessUnitId;
	}

	public static String getShareOfVoiceBrandAmazonRoutePath() {
		return BASE_URI + SHARE_OF_VOICE_BRAND_AMAZON_PLATFORM;
	}

}