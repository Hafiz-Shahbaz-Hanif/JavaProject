package com.DC.utilities.apiEngine.routes.productVersioning;

import com.DC.utilities.ReadConfig;

public class TaskUIRoutes {
    public static final ReadConfig READ_CONFIG = ReadConfig.getInstance();
    public static final String TASK_UI_HOST = READ_CONFIG.getProductVariantRepoEndpoint() + "/api/task-ui";
    public static final String TASK_UI_CONFIG_ROUTE = TASK_UI_HOST + "/config";
    public static final String TASK_UI_HISTORY_ROUTE = READ_CONFIG.getProductVariantRepoEndpoint() + "/api/task-version-history";

    public static String getTaskUIConfigRoutePath(String taskUIConfigId) {
        return TASK_UI_CONFIG_ROUTE + "/" + taskUIConfigId;
    }

}
