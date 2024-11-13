package com.DC.utilities.apiEngine.apiRequests.productVersioning;

import com.DC.utilities.apiEngine.routes.productVersioning.TaskUIRoutes;
import io.restassured.response.Response;
import org.json.JSONObject;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class TaskUIRequests {
    public static Response addTaskUIConfig(JSONObject payload, String jwt) throws Exception {
        return callEndpoint(TaskUIRoutes.TASK_UI_CONFIG_ROUTE, jwt, "POST", payload.toString(), "");
    }

    public static Response getTaskUIConfig(String taskUIConfigId, String jwt) throws Exception {
        return callEndpoint(TaskUIRoutes.getTaskUIConfigRoutePath(taskUIConfigId), jwt, "GET", "", "");
    }

    public static Response replaceTaskUIConfig(JSONObject payload, String taskUIConfigId, String jwt) throws Exception {
        return callEndpoint(TaskUIRoutes.getTaskUIConfigRoutePath(taskUIConfigId), jwt, "PUT", payload.toString(), "");
    }

    public static Response mergeTaskUIConfig(JSONObject payload, String taskUIConfigId, String jwt) throws Exception {
        return callEndpoint(TaskUIRoutes.getTaskUIConfigRoutePath(taskUIConfigId), jwt, "PATCH", payload.toString(), "");
    }

    public static Response deleteTaskUIConfig(String taskUIConfigId, String jwt) throws Exception {
        return callEndpoint(TaskUIRoutes.getTaskUIConfigRoutePath(taskUIConfigId), jwt, "DELETE", "", "");
    }

    public static Response getAllTaskUIConfigs(String jwt) throws Exception {
        return callEndpoint(TaskUIRoutes.TASK_UI_CONFIG_ROUTE, jwt, "GET", "", "");
    }

    public static Response getTaskHistoryFilterOptions(JSONObject payload, String jwt) throws Exception {
        return callEndpoint(TaskUIRoutes.TASK_UI_HISTORY_ROUTE + "/grid/filter-options", jwt, "POST", payload.toString(), "");
    }

    public static Response getTaskHistoryGrid(JSONObject payload, String jwt) throws Exception {
        return callEndpoint(TaskUIRoutes.TASK_UI_HISTORY_ROUTE + "/grid", jwt, "POST", payload.toString(), "");
    }

    public static Response getTaskHistoryActivityDetails(String activityId, String assignmentId, String jwt) throws Exception {
        var req = "{\n" +
                "\"assignmentId\" : \"" + assignmentId + "\"\n" +
                "}";
        return callEndpoint(TaskUIRoutes.TASK_UI_HISTORY_ROUTE + "/grid/activity/" + activityId, jwt, "POST", req, "");
    }
}
