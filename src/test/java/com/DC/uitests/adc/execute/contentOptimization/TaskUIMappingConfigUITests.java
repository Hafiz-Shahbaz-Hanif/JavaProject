package com.DC.uitests.adc.execute.contentOptimization;

import com.DC.constants.InsightsConstants;
import com.DC.db.productVersioning.TaskUIConfigCollection;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.contentOptimization.TaskUIMappingPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.TaskUIRequests;
import com.DC.utilities.enums.Enums;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.stream.Collectors;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class TaskUIMappingConfigUITests extends BaseClass {

    private final String USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();
    private final String NAME_OF_MAPPING_TO_ADD = "QA Test Label For UI Tests";
    private final boolean IS_INTERNAL = false;
    private final JSONObject MAPPINGS = new JSONObject().put("mappings", new JSONArray());

    private TaskUIMappingPage taskUIMappingPage;
    private String jwt;
    private String taskUIConfigId;

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_TASK_UI_MAPPING_URL);
        taskUIMappingPage = new TaskUIMappingPage(driver);
        jwt = SecurityAPI.getJwtForInsightsUser(driver);
    }

    @AfterClass
    public void killDriver() {
        if (taskUIConfigId != null) {
            new TaskUIConfigCollection().deleteTaskUIConfig(taskUIConfigId);
        }
        quitBrowser();
    }

    @Test(priority = 1, description = "C255054 - Correct mapping configs are displayed")
    public void CGEN_TaskUIMappings_CorrectMappingConfigsAreDisplayed() throws Exception {
        var expectedMappingConfigsIds = TaskUIRequests.getAllTaskUIConfigs(jwt).jsonPath().getList("_id").stream().sorted().collect(Collectors.toList());
        var idsOfMappingConfigsDisplayed =  taskUIMappingPage.getIdsOfMappingConfigsDisplayed().stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(idsOfMappingConfigsDisplayed, expectedMappingConfigsIds, "Mapping configs displayed don't match the ones returned by the API");
    }

    @Test(priority = 2, description = "C243686 - Can create a new mapping config")
    public void CGEN_TaskUIMappings_CanCreateMappingConfig() {
        var createTaskUIMappingPage = taskUIMappingPage.clickCreateNewMappingConfigButton();
        createTaskUIMappingPage.fillFields(NAME_OF_MAPPING_TO_ADD, IS_INTERNAL, MAPPINGS);
        Assert.assertTrue(createTaskUIMappingPage.isCreateButtonEnabled(), "Create button is not enabled when all fields are filled");
        createTaskUIMappingPage.clickCreateButton();
        taskUIMappingPage = new TaskUIMappingPage(driver);

        var successNoteDisplayed = taskUIMappingPage.isNoteDisplayedWithMessage(Enums.NoteType.SUCCESS, "Configuration was successfully created.");
        Assert.assertTrue(successNoteDisplayed, "Success note is not displayed after creating a new mapping config");

        var mappingIsDisplayed = taskUIMappingPage.isMappingConfigDisplayed(NAME_OF_MAPPING_TO_ADD);
        Assert.assertTrue(mappingIsDisplayed, "Mapping config is not displayed after creating it");

        mappingIsDisplayed = taskUIMappingPage.refreshPage(TaskUIMappingPage.class).isMappingConfigDisplayed(NAME_OF_MAPPING_TO_ADD);
        Assert.assertTrue(mappingIsDisplayed, "Mapping config is not displayed after refreshing the page");

        taskUIConfigId = taskUIMappingPage.getIdOfMappingConfig(NAME_OF_MAPPING_TO_ADD);
    }

    @Test(priority = 3, description = "C243688 - Can delete a mapping config")
    public void CGEN_TaskUIMappings_CanDeleteAMappingConfig() {
        createMappingIfNotExistent();

        // Testing closing the modal
        taskUIMappingPage.searchMappingConfig(NAME_OF_MAPPING_TO_ADD);
        var deleteModal = taskUIMappingPage.clickDeleteIcon(NAME_OF_MAPPING_TO_ADD);
        taskUIMappingPage = deleteModal.clickNeverMindButton();
        var mappingIsDisplayed = taskUIMappingPage.isMappingConfigDisplayed(NAME_OF_MAPPING_TO_ADD);
        Assert.assertTrue(mappingIsDisplayed, "Mapping config was deleted after clicking on 'Never mind...' button");

        taskUIMappingPage.searchMappingConfig(NAME_OF_MAPPING_TO_ADD);
        deleteModal = taskUIMappingPage.clickDeleteIcon(NAME_OF_MAPPING_TO_ADD);
        taskUIMappingPage = deleteModal.clickCloseIcon();
        mappingIsDisplayed = taskUIMappingPage.isMappingConfigDisplayed(NAME_OF_MAPPING_TO_ADD);
        Assert.assertTrue(mappingIsDisplayed, "Mapping config was deleted after clicking on the close modal icon");

        // Testing actual deletion
        taskUIMappingPage.searchMappingConfig(NAME_OF_MAPPING_TO_ADD);
        deleteModal = taskUIMappingPage.clickDeleteIcon(NAME_OF_MAPPING_TO_ADD);
        taskUIMappingPage = deleteModal.clickDeleteButton();
        mappingIsDisplayed = taskUIMappingPage.isMappingConfigDisplayed(NAME_OF_MAPPING_TO_ADD);
        Assert.assertFalse(mappingIsDisplayed, "Mapping config was not deleted after confirming deletion");
    }

    private void createMappingIfNotExistent() {
        var mappingExists = taskUIMappingPage.isMappingConfigDisplayed(NAME_OF_MAPPING_TO_ADD);
        if (!mappingExists) {
            var createTaskUIMappingPage = taskUIMappingPage.clickCreateNewMappingConfigButton();
            createTaskUIMappingPage.fillFields(NAME_OF_MAPPING_TO_ADD, IS_INTERNAL, MAPPINGS);
            createTaskUIMappingPage.clickCreateButton();
            taskUIMappingPage = new TaskUIMappingPage(driver);
            taskUIMappingPage.closeNoteIfDisplayed(Enums.NoteType.SUCCESS);
        }
    }
}
