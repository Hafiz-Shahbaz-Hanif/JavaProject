package com.DC.utilities.apiEngine.routes.hub.aggregation;

import com.DC.utilities.ReadConfig;

public class AggregationServiceRoutes {

    public static final String AGGREGATION_SERVICE_BASE_URI = ReadConfig.getInstance().getAggregationServiceBaseUri();
    public static final String AGGREGATION_TYPE = "/aggregation-type";
    public static final String AGGREGATION_ORGANIZATION = "/aggregation/organization";
    public static final String AGGREGATION_ORG_LIST = "/aggregation/organization/list";
    public static final String AGGREGATION_ORGANIZATION_ORG = "/aggregation/organization/org";
    public static final String AGGREGATION_BULK_ORGANIZATION = "/aggregation/organization/bulk";
    public static final String AGGREGATION_BULK_BU = "/aggregation/business-unit/bulk";
    public static final String AGGREGATION_BU_LIST = "/aggregation/business-unit/list";
    public static final String AGGREGATION_BU = "/aggregation/business-unit";
    public static final String AGGREGATION_BUSINESSES_BU = "/aggregation/business-unit/bu";
    public static final String AGGREGATION_AUTH = "/aggregation/authorization";


    public static String postAggregationTypeRoutePath() {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_TYPE;
    }

    public static String getAggregationTypeRoutePath(String aggregationTypeId) {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_TYPE + "/" + aggregationTypeId;
    }

    public static String postOrganizationAggregationRoutePath() {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_ORGANIZATION;
    }

    public static String postBuAggregationRoutePath() {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_BU;
    }

    public static String postBulkOrganizationAggregationRoutePath() {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_BULK_ORGANIZATION;
    }

    public static String putBulkBuAggregationRoutePath() {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_BULK_BU;
    }

    public static String getBuAggregationListRoutePath() {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_BU_LIST;
    }

    public static String getOrgAggregationListRoutePath() {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_ORG_LIST;
    }

    public static String patchOrganizationAggregationRoutePath(String aggregationId) {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_ORGANIZATION + "/" + aggregationId ;
    }

    public static String getOrganizationAggregationAggrIdRoutePath(String aggregationId) {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_ORGANIZATION + "/" + aggregationId;
    }

    public static String getBuAggregationAggrIdRoutePath(String aggregationId) {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_BU + "/" + aggregationId;
    }

    public static String getOrganizationAggregationOrgIdRoutePath(String orgId) {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_ORGANIZATION_ORG + "/" + orgId;
    }

    public static String getBuAggregationBuIdRoutePath(String buId) {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_BUSINESSES_BU + "/" + buId;
    }

    public static String patchBuAggregationRoutePath(String aggregationId) {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_BU + "/" + aggregationId;
    }

    public static String postAuthAggregationRoutePath(String userId) {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_AUTH + "/" + userId;
    }

    public static String getAuthAggregationRoutePath() {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_AUTH;
    }

    public static String deleteAuthAggregationRoutePath(String aggregationId) {
        return AGGREGATION_SERVICE_BASE_URI + AGGREGATION_AUTH + "/" + aggregationId;
    }


}