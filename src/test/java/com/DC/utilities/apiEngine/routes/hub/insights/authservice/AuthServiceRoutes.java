package com.DC.utilities.apiEngine.routes.hub.insights.authservice;

import com.DC.utilities.ReadConfig;

public class AuthServiceRoutes {

    public static final String BASE_AUTH_URI = ReadConfig.getInstance().getInsightsAuthServiceEndpoint();
    public static final String HUB_EXTERNAL_GATEWAY = ReadConfig.getInstance().getHubExternalGateway() + "/insights";
    public static final String JWT = ReadConfig.getInstance().getInsightsJwtEndpoint();
    public static final String COMPANY_SCHEMA_URI = ReadConfig.getInstance().getInsightsCompanySchemaEndpoint(); 
    public static final String COMPANY_All_COUNTRIES_URI = ReadConfig.getInstance().getInsightsCompanyAllCountriesEndpoint();
    public static final String CPG_ACCOUNT_URL = ReadConfig.getInstance().getCpgAccountUrl();
    public static final String AUTH_SERVICE = "/api/authenticate/auth0/login";
    public static final String COMPANY_SCHEMA = "/api/company-schema";
    public static final String COMPANY_SCHEMA_PROPERTIES_EXTERNALGATEWAY ="/api/cpg/product-repository/company-schema/properties";
    public static final String COMPANY_SCHEMA_PROPERTIES ="/api/company-schema/properties";
    public static final String COMPANY_ALL_COUNTRIES_EXTERNALGATEWAY ="/api/cpg/company/getallcountries";
    public static final String COMPANY_ALL_COUNTRIES ="/api/company/getallcountries";
    public static final String CATEGORIES_FAVORITE ="/api/cpg/categories/favorite";
    public static final String COMPANY_SCHEMA_EXTERNALGATEWAY = "/api/product-repository/company-schema";
    public static final String SUPPORT_LOGIN ="/api/cpg/support-login";
    public static final String COMPANIES ="/api/cpg/company";
    public static final String USER_ROLE_UPDATE ="/api/company-account/";


    public static String getAuthServiceRoutePath() {
    	return BASE_AUTH_URI + AUTH_SERVICE;
    }

    public static String getJwtRoutePath() {
    	return JWT;
    }

    public static String getCompanySchemaRoutePath() {
    	return COMPANY_SCHEMA_URI + COMPANY_SCHEMA;
    }
    
    public static String getCompanySchemaRouteExternalGatewayPath() {
    	return HUB_EXTERNAL_GATEWAY + COMPANY_SCHEMA_EXTERNALGATEWAY;
    }
    
    public static String getCompanySchemaPropertiesPath() {
    	return COMPANY_SCHEMA_URI + COMPANY_SCHEMA_PROPERTIES;
    }

    public static String getCompanySchemaPropertiesRouteExternalGatewayPath() {
    	return HUB_EXTERNAL_GATEWAY + COMPANY_SCHEMA_PROPERTIES_EXTERNALGATEWAY;
    }
    
    public static String getCompanyAllCountriesRoutePath() {
    	return COMPANY_All_COUNTRIES_URI + COMPANY_ALL_COUNTRIES;
    }

    public static String getCompanyAllCountriesRouteExternalGatewayPath() {
    	return HUB_EXTERNAL_GATEWAY + COMPANY_ALL_COUNTRIES_EXTERNALGATEWAY;
    }

    public static String getCategoriesFavoriteRouteExternalGatewayPath() {
    	return HUB_EXTERNAL_GATEWAY + CATEGORIES_FAVORITE;
    }
    
    public static String getSupportLoginRouteExternalGatewayPath() {
    	return HUB_EXTERNAL_GATEWAY + SUPPORT_LOGIN;
    }

    public static String getCompaniesRouteExternalGatewayPath() {
        return HUB_EXTERNAL_GATEWAY + COMPANIES;
    }

    public static String getUserRoleUpdateRoutePath(String userId) {
        return CPG_ACCOUNT_URL + USER_ROLE_UPDATE + userId;
    }

}