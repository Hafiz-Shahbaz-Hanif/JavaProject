package com.DC.utilities.apiEngine.routes.productVersioning;

import com.DC.utilities.ReadConfig;

public class CompanyRoutes {

    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    public static final String COMPANY_HOST = READ_CONFIG.getProductVariantRepoEndpoint() + "/api";
    private static final String COMPANY_PROPERTIES_TEMPLATE = READ_CONFIG.getProductVariantRepoEndpoint() + "/api/company-properties-template";

    public static String getCompanyHost() {
        return COMPANY_HOST + "/company";
    }

    public static String getCompanyRetailersHost() {
        return COMPANY_HOST + "/company-retailers";
    }

    public static String getCompanyLocalesHost() {
        return COMPANY_HOST + "/company-locales";
    }

    public static String getCompanyPropertiesRoutePath() {
        return getCompanyHost() + "/company-properties";
    }

    public static String getCompanyPropertiesRoutePath(String companyPropertiesId) {
        return getCompanyPropertiesRoutePath() + "/" + companyPropertiesId;
    }

    public static String getCompanyRetailersRoutePath() {
        return getCompanyHost() + "/retailers";
    }

    public static String getCompanyLocalesRoutePath() {
        return getCompanyHost() + "/locales";
    }

    public static String getCompanyCampaignsRoutePath() {
        return getCompanyHost() + "/campaigns";
    }

    public static String getCompanyPropertySchemaRoutePath() {
        return getCompanyPropertiesRoutePath() + "/properties";
    }

    public static String getCompanyDigitalAssetsSchemaRoutePath() {
        return getCompanyPropertiesRoutePath() + "/digital-assets";
    }

    public static String getCompanyPropertiesTemplateHost() {
        return COMPANY_PROPERTIES_TEMPLATE;
    }

    public static String getCompanyPropertiesGroupsRoutePath() {
        return getCompanyPropertiesRoutePath() + "/groups";
    }

    public static String getCompanyPropertiesDigitalAssetGroupsRoutePath() {
        return getCompanyPropertiesRoutePath() + "/groups-digital-assets";
    }
}
