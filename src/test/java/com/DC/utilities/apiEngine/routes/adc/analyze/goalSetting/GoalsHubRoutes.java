package com.DC.utilities.apiEngine.routes.adc.analyze.goalSetting;

import com.DC.utilities.ReadConfig;

public class GoalsHubRoutes {

    public static final String HUB_EXTERNAL_GATEWAY = ReadConfig.getInstance().getHubExternalGateway();
    public static final String GOALS_HUB = "/goals/goals-hub";
    public static final String GOALS_BULK_CREATE = "/goals/goals-hub/bulk";
    public static final String GOALS_BRANDS = "/segmentation/catalog/brand-manufacturer";
    public static final String GOALS_EXPORT = "/goals/goals-hub/export";
    public static final String GOALS_SEGMENTATION = "/segmentation/catalog";

    public static String getGoalsHubRoutePath() {
        return HUB_EXTERNAL_GATEWAY + GOALS_HUB;
    }

    public static String getGoalBrandsRoutePath() {
        return HUB_EXTERNAL_GATEWAY + GOALS_BRANDS;
    }

    public static String getGoalSegmentationRoutePath() {
        return HUB_EXTERNAL_GATEWAY + GOALS_SEGMENTATION;
    }

    public static String getGoalsHubDeleteRoutePath(String goalId) {
        return HUB_EXTERNAL_GATEWAY + GOALS_HUB + "/" + goalId;
    }

    public static String getGoalRoutePath(String goalId) {
        return HUB_EXTERNAL_GATEWAY + GOALS_HUB + "/fetch/" + goalId;
    }

    public static String getGoalsHubUpdateRoutePath() {
        return HUB_EXTERNAL_GATEWAY + GOALS_HUB;
    }

    public static String getAllGoalsHubRoutePath() {
        return HUB_EXTERNAL_GATEWAY + GOALS_HUB + "/all";
    }

    public static String getGoalMetricRoutePath() {
        return HUB_EXTERNAL_GATEWAY + GOALS_HUB + "/metric";
    }

    public static String goalBulkCreateRoutePath() {
        return HUB_EXTERNAL_GATEWAY + GOALS_BULK_CREATE;
    }

    public static String goalsExportRoutePath() {
        return HUB_EXTERNAL_GATEWAY + GOALS_EXPORT;
    }

}