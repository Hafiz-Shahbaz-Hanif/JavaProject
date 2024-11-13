package com.DC.apitests.productversioning.taskui;

import com.DC.apitests.ApiValidations;
import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.TaskUIConfigCollection;
import com.DC.testcases.BaseClass;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.TaskUIRequests;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.DC.utilities.SecurityAPI.loginAndGetJwt;

public class TaskUIApiTests extends BaseClass {
    private static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    private final JSONObject ORIGINAL_MAPPING = new JSONObject() {{
        put("writeable", true);
        put("property", "test_property");
        put("assignments", new JSONArray());
    }};

    private final JSONObject TASK_UI_CONFIG_PAYLOAD = new JSONObject() {{
        put("label", "Task UI Config From API Test");
        put("mappings", new JSONArray().put(ORIGINAL_MAPPING));
        put("internal", false);
    }};

    private String jwt;
    private String taskUIConfigId;

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.username, TEST_CONFIG.password);
    }

    @AfterClass()
    public void cleanupTests() {
        if (taskUIConfigId != null) {
            new TaskUIConfigCollection().deleteTaskUIConfig(taskUIConfigId);
        }
    }

    @Test(priority = 1, description = "Verify Task UI Config can be created")
    public void Api_TaskUIConfig_CanCreateATaskUIConfig() throws Exception {
        var response = TaskUIRequests.addTaskUIConfig(TASK_UI_CONFIG_PAYLOAD, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);
        taskUIConfigId = verifyTaskUIData(response, null, TASK_UI_CONFIG_PAYLOAD.getString("label"), TASK_UI_CONFIG_PAYLOAD.getJSONArray("mappings"), false);
    }

    @Test(priority = 2, description = "Verify GET method returns correct Task UI Config")
    public void Api_TaskUIConfig_CanGetTaskUIConfig() throws Exception {
        addTaskUIConfigIfNeeded();
        var response = TaskUIRequests.getTaskUIConfig(taskUIConfigId, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);
        verifyTaskUIData(response, taskUIConfigId, TASK_UI_CONFIG_PAYLOAD.getString("label"), TASK_UI_CONFIG_PAYLOAD.getJSONArray("mappings"), true);
    }

    @Test(priority = 3, description = "Verify PUT/PATCH methods replace/merge data in Task UI Config")
    public void Api_TaskUIConfig_CanReplaceAndMergeDataOfTaskUIConfig() throws Exception {
        // PUT
        addTaskUIConfigIfNeeded();
        JSONObject mapping = new JSONObject() {{
            put("writeable", false);
            put("property", "new_property");
            put("assignments", new JSONArray());
        }};

        TASK_UI_CONFIG_PAYLOAD.put("label", "Task UI Config From API Test - Updated");
        TASK_UI_CONFIG_PAYLOAD.put("mappings", new JSONArray().put(mapping));

        var response = TaskUIRequests.replaceTaskUIConfig(TASK_UI_CONFIG_PAYLOAD, taskUIConfigId, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);
        verifyTaskUIData(response, taskUIConfigId, TASK_UI_CONFIG_PAYLOAD.getString("label"), TASK_UI_CONFIG_PAYLOAD.getJSONArray("mappings"), true);

        // PATCH
        var secondMapping = new JSONObject() {{
            put("writeable", true);
            put("property", "merged_property");
            put("assignments", new JSONArray());
        }};

        var taskUIConfigPayload = new JSONObject() {{
            put("label", TASK_UI_CONFIG_PAYLOAD.getString("label"));
            put("mappings", new JSONArray().put(secondMapping));
            put("internal", TASK_UI_CONFIG_PAYLOAD.getBoolean("internal"));
        }};

        var expectedMappings = new JSONArray();
        expectedMappings.put(mapping).put(secondMapping);

        response = TaskUIRequests.mergeTaskUIConfig(taskUIConfigPayload, taskUIConfigId, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);
        verifyTaskUIData(response, taskUIConfigId, taskUIConfigPayload.getString("label"), expectedMappings, true);
    }

    @Test(priority = 4, description = "Verify user cannot create duplicate Task UI Configs", enabled = false)
    public void Api_TaskUIConfig_CannotCreateATaskUIConfig_DuplicateLabel() throws Exception {
        // TODO - IN PRODUCT VARIANT THERE IS NO DUPLICATE LABEL VALIDATION
        addTaskUIConfigIfNeeded();

        var response = TaskUIRequests.getTaskUIConfig(taskUIConfigId, jwt);
        var taskUIConfig = response.jsonPath();
        var taskUIConfigPayload = new JSONObject() {{
            put("label", taskUIConfig.getString("label"));
            put("mappings", taskUIConfig.getList("mappings"));
            put("internal", taskUIConfig.getBoolean("internal"));
        }};

        response = TaskUIRequests.addTaskUIConfig(taskUIConfigPayload, jwt);
        Assert.assertEquals(response.statusCode(), 400, "Incorrect status code");
        // TODO - FINISH THIS
    }

    @Test(priority = 5, description = "Verify user cannot replace/merge data of Task UI Config if label already exists")
    public void Api_TaskUIConfig_CannotReplaceOrMergeDataOfTaskUIConfig_DuplicateLabel() throws Exception {
        addTaskUIConfigIfNeeded();

        var response = TaskUIRequests.getTaskUIConfig(taskUIConfigId, jwt);
        var taskUIConfig = response.jsonPath();
        var taskUIConfigPayload = new JSONObject() {{
            put("label", "Automated Test Company Mapping");
            put("mappings", taskUIConfig.getList("mappings"));
            put("internal", taskUIConfig.getBoolean("internal"));
        }};

        var expectedErrorMsg = "Type: TaskUIConfigError. Subtype: TaskUIConfigUpdateError. Message: Label already exists..";
        response = TaskUIRequests.replaceTaskUIConfig(taskUIConfigPayload, taskUIConfigId, jwt);
        ApiValidations.validateUnprocessableEntityError(response, expectedErrorMsg);

        response = TaskUIRequests.mergeTaskUIConfig(taskUIConfigPayload, taskUIConfigId, jwt);
        ApiValidations.validateUnprocessableEntityError(response, expectedErrorMsg);
    }

    @Test(priority = 6, description = "Verify mappings in request cannot have duplicate properties")
    public void Api_TaskUIConfig_CannotReplaceDataOfTaskUIConfig_DuplicatePropertiesInMapping() throws Exception {
        addTaskUIConfigIfNeeded();
        var response = TaskUIRequests.getTaskUIConfig(taskUIConfigId, jwt);
        var taskUIConfig = response.jsonPath();

        var taskUIConfigPayload = new JSONObject() {{
            put("label", taskUIConfig.getString("label"));
            put("mappings", new JSONArray().put(ORIGINAL_MAPPING).put(ORIGINAL_MAPPING));
            put("internal", taskUIConfig.getBoolean("internal"));
        }};

        response = TaskUIRequests.replaceTaskUIConfig(taskUIConfigPayload, taskUIConfigId, jwt);
        ApiValidations.validateUnprocessableEntityError(response, "Type: TaskUIConfigError. Subtype: TaskUIConfigValidationError. Message: Duplicate mappings properties detected.");
    }

    @Test(priority = 7, description = "Verify duplicate properties are removed when using PATCH method")
    public void Api_TaskUIConfig_DuplicatePropertiesInMappingAreRemovedWhenMergingData() throws Exception {
        addTaskUIConfigIfNeeded();
        var response = TaskUIRequests.getTaskUIConfig(taskUIConfigId, jwt);
        var taskUIConfig = response.jsonPath();

        var newMapping = new JSONObject() {{
            put("writeable", false);
            put("property", "another_property");
            put("assignments", new JSONArray());
        }};

        JSONArray array = new JSONArray().put(ORIGINAL_MAPPING).put(ORIGINAL_MAPPING).put(newMapping);
        var taskUIConfigPayload = new JSONObject() {{
            put("label", taskUIConfig.getString("label"));
            put("mappings", array);
            put("internal", taskUIConfig.getBoolean("internal"));
        }};

        List<String> expectedMappingProperties = new ArrayList<>(taskUIConfig.getList("mappings.property"));
        expectedMappingProperties.add(newMapping.getString("property"));
        expectedMappingProperties.add(ORIGINAL_MAPPING.getString("property"));

        expectedMappingProperties = expectedMappingProperties.stream().distinct().collect(Collectors.toList());

        response = TaskUIRequests.mergeTaskUIConfig(taskUIConfigPayload, taskUIConfigId, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);
        var responseJsonPath = response.jsonPath();
        Assert.assertEquals(
                responseJsonPath.getList("mappings.property").stream().sorted().collect(Collectors.toList()),
                expectedMappingProperties.stream().sorted().collect(Collectors.toList()),
                "Duplicate properties were not removed"
        );
    }

    @Test(priority = 8, description = "Verify DELETE method deletes a Task UI Config")
    public void Api_TaskUIConfig_CanDeleteTaskUIConfig() throws Exception {
        addTaskUIConfigIfNeeded();
        var response = TaskUIRequests.deleteTaskUIConfig(taskUIConfigId, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);

        response = TaskUIRequests.getTaskUIConfig(taskUIConfigId, jwt);
        ApiValidations.validateUnprocessableEntityError(response, "Could not find config");
    }

    @Test(priority = 9, description = "Verify user can get all Task UI Configs")
    public void Api_TaskUIConfig_CanGetAllTaskUIConfigs() throws Exception {
        var response = TaskUIRequests.getAllTaskUIConfigs(jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);
        var responseJsonPath = response.jsonPath();
        Assert.assertFalse(responseJsonPath.getList("_id").isEmpty(), "No Task UI Configs returned");

        var firstTaskUIConfig = new JSONArray(response.asString()).getJSONObject(0);

        Assert.assertTrue(firstTaskUIConfig.has("_id"), "Returned Task UI Config doesn't have an id");
        Assert.assertTrue(firstTaskUIConfig.has("label"), "Returned Task UI Config doesn't have a label");
        Assert.assertTrue(firstTaskUIConfig.has("mappings"), "Returned Task UI Config doesn't have mappings");
        Assert.assertTrue(firstTaskUIConfig.has("internal"), "Returned Task UI Config doesn't have an internal flag");
    }

    private void addTaskUIConfigIfNeeded() throws Exception {
        if (taskUIConfigId == null) {
            var response = TaskUIRequests.addTaskUIConfig(TASK_UI_CONFIG_PAYLOAD, jwt);
            taskUIConfigId = response.jsonPath().getString("_id");
        }
    }

    private String verifyTaskUIData(Response response, String taskUIConfigId, String expectedLabel, JSONArray expectedMappings, boolean checkId) {
        var responseJsonPath = response.jsonPath();
        var configId = responseJsonPath.getString("_id");
        if (checkId) {
            Assert.assertEquals(configId, taskUIConfigId, "Returned id doesn't match with the expected id");
        }
        Assert.assertEquals(responseJsonPath.getString("label"), expectedLabel, "Returned label doesn't match with the expected label");
        Assert.assertEquals(responseJsonPath.getList("mappings"), expectedMappings.toList(), "Returned mappings don't match with the expected mappings");
        return configId;
    }
}
