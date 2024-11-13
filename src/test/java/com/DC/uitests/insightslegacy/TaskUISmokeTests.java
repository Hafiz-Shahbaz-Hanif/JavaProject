package com.DC.uitests.insightslegacy;

import com.DC.objects.insightslegacy.AssignmentDetails;
import com.DC.pageobjects.legacy.legacyinsights.DashboardPage;
import com.DC.pageobjects.legacy.legacyinsights.InsightsLoginPage;
import com.DC.pageobjects.legacy.legacyinsights.TasksPage;
import com.DC.pageobjects.legacy.legacyinsights.taskpages.*;
import com.DC.testcases.BaseClass;
import com.DC.utilities.enums.Enums;
import com.DC.utilities.sharedElements.TaskLaunchFileSettings;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.DC.constants.InsightsConstants.INSIGHTS_LEGACY_ENDPOINT;

public class TaskUISmokeTests extends BaseClass {
    private DashboardPage dashboardPage;
    private TaskPage taskPage;
    private TasksPage tasksPage;
    private final String SUPPORT_USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();

    @BeforeClass()
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(INSIGHTS_LEGACY_ENDPOINT);
        new InsightsLoginPage(driver).login(SUPPORT_USERNAME, PASSWORD);
        dashboardPage = new DashboardPage(driver);
        tasksPage = dashboardPage.clickTasks().changeTasksPerPage("100");
    }

    @AfterMethod
    public void cleanUp() throws Exception {
        if (driver.getTitle().equals("Task | OneSpace")) {
            tasksPage = taskPage.discardTask();
        }
    }

    @AfterClass(alwaysRun = true)
    public void killDriver() {
        quitBrowser();
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_ContentCreateTaskLoads() throws Exception {
        String assignmentName = "Content Creation Workflow";
        tasksPage.releaseMoreTasksIfNeeded(assignmentName, TaskLaunchFileSettings.getContentCreateSmokeLaunchFileSettings(), 1);
        verifyContentTaskUILoads( assignmentName, Enums.TaskType.Content );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_ContentInternalReviewTaskLoads() throws Exception {
        String assignmentName = "Content Internal Review Workflow";
        tasksPage.releaseMoreTasksIfNeeded(assignmentName, TaskLaunchFileSettings.getContentCreateSmokeLaunchFileSettings(), 1);
        verifyContentTaskUILoads( assignmentName, Enums.TaskType.Content );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_ContentBrandReviewTaskLoads() throws Exception {
        String assignmentName = "Content Brand Review Workflow";
        tasksPage.releaseMoreTasksIfNeeded(assignmentName, TaskLaunchFileSettings.getContentCreateSmokeLaunchFileSettings(), 1);
        verifyContentTaskUILoads( assignmentName, Enums.TaskType.Content );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_ContentLegalReviewTaskLoads() throws Exception {
        String assignmentName = "Content Legal Review Workflow";
        tasksPage.releaseMoreTasksIfNeeded(assignmentName, TaskLaunchFileSettings.getContentCreateSmokeLaunchFileSettings(), 1);
        verifyContentTaskUILoads( assignmentName, Enums.TaskType.Content );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_ImageCreateTaskLoads() throws Exception {
        String assignmentName = "Image Creation Workflow";
        tasksPage.releaseMoreTasksIfNeeded(assignmentName, TaskLaunchFileSettings.getImageSmokeLaunchFileSettings(), 1);
        verifyImageTaskUILoads( assignmentName, Enums.TaskType.Image );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_ImageInternalReviewTaskLoads() throws Exception {
        String assignmentName = "Image Internal Review Workflow";
        verifyImageTaskUILoads( assignmentName, Enums.TaskType.Image );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_ImageBrandReviewTaskLoads() throws Exception {
        String assignmentName = "Image Brand Review Workflow";
        verifyImageTaskUILoads( assignmentName, Enums.TaskType.Image );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_ImageLegalReviewTaskLoads() throws Exception {
        String assignmentName = "Image Legal Review Workflow";
        verifyImageTaskUILoads( assignmentName, Enums.TaskType.Image );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_AttributeTaggingTaskLoads() throws Exception {
        String assignmentName = "Automated Test Company - Attribute Tagging";
        tasksPage.releaseMoreTasksIfNeeded(assignmentName, tasksPage.getLaunchFileSettings(Enums.TaskType.Attribute), 1);
        verifyAttributeTaskUILoads( assignmentName, Enums.TaskType.Attribute );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_AttributeTaggingInternalReviewTaskLoads() throws Exception {
        String assignmentName = "Automated Test Company - Attribute Tagging Internal Review";
        verifyAttributeTaskUILoads( assignmentName, Enums.TaskType.Attribute);
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_KeywordResearchTaskLoads() throws Exception {
        String assignmentName = "Automated Test Company - Keyword Research";
        tasksPage.releaseMoreTasksIfNeeded(assignmentName, tasksPage.getLaunchFileSettings(Enums.TaskType.Keyword), 1);
        verifyKeywordResearchTaskUILoads( assignmentName, Enums.TaskType.Keyword );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_KeywordResearchInternalReviewTaskLoads() throws Exception {
        String assignmentName = "Automated Test Company - Keyword Research Internal Review";
        verifyKeywordResearchTaskUILoads( assignmentName, Enums.TaskType.Keyword );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_RPCDiscoveryTaskLoads() throws Exception {
        String assignmentName = "RPC Discovery Collection";
        tasksPage.releaseMoreTasksIfNeeded(assignmentName, tasksPage.getLaunchFileSettings(Enums.TaskType.Rpc), 1);
        verifyRPCTaskUILoads( assignmentName, Enums.TaskType.Rpc );
    }

    @Test(groups = {"TaskUI Smoke Test"})
    public void UI_RPCDiscoveryInternalReviewTaskLoads() throws Exception {
        String assignmentName = "RPC Discovery Internal Review";
        verifyRPCTaskUILoads( assignmentName, Enums.TaskType.Rpc );
    }

    public void verifyContentTaskUILoads( String assignmentName, Enums.TaskType taskType ) throws Exception {
        AssignmentDetails assignmentDetails = getAssignmentDetails(assignmentName, taskType);
        TaskPage contentTask = tasksPage.claimRequiredAssignmentIfExists( assignmentDetails );
        taskPage = contentTask;
        contentTask.switchFeedbackNotificationsIndicator(Enums.ToggleAction.Hide);
        Assert.assertTrue( contentTask.clickAllProductsAndCheckTaskVisibility() );
    }

    public void verifyImageTaskUILoads( String assignmentName, Enums.TaskType taskType ) throws Exception {
        AssignmentDetails assignmentDetails = getAssignmentDetails(assignmentName, taskType);
        TaskPage imageTask = tasksPage.claimRequiredAssignmentIfExists( assignmentDetails );
        taskPage = imageTask;
        imageTask.switchFeedbackNotificationsIndicator(Enums.ToggleAction.Hide);
        Assert.assertTrue( imageTask.clickAllProductsAndCheckTaskVisibility() );
    }

    public void verifyAttributeTaskUILoads( String assignmentName, Enums.TaskType taskType ) throws Exception {
        AssignmentDetails assignmentDetails = getAssignmentDetails(assignmentName, taskType);
        TaskPage attributeTaggingTaskUIPage = tasksPage.claimRequiredAssignmentIfExists( assignmentDetails );
        taskPage = attributeTaggingTaskUIPage;
        attributeTaggingTaskUIPage.switchFeedbackNotificationsIndicator(Enums.ToggleAction.Hide);
        Assert.assertTrue( attributeTaggingTaskUIPage.clickAllProductsAndCheckTaskVisibility() );
    }
    public void verifyKeywordResearchTaskUILoads( String assignmentName, Enums.TaskType taskType ) throws Exception {
        AssignmentDetails assignmentDetails = getAssignmentDetails(assignmentName, taskType);
        TaskPage keywordResearchTaskUIPage = tasksPage.claimRequiredAssignmentIfExists( assignmentDetails );
        taskPage = keywordResearchTaskUIPage;
        keywordResearchTaskUIPage.switchFeedbackNotificationsIndicator(Enums.ToggleAction.Hide);
        Assert.assertTrue( keywordResearchTaskUIPage.clickAllProductsAndCheckTaskVisibility() );
    }

    public void verifyRPCTaskUILoads( String assignmentName, Enums.TaskType taskType ) throws Exception {
        AssignmentDetails assignmentDetails = getAssignmentDetails(assignmentName, taskType);
        TaskPage rpcDiscoveryTaskUIPage = tasksPage.claimRequiredAssignmentIfExists( assignmentDetails );
        taskPage = rpcDiscoveryTaskUIPage;
        rpcDiscoveryTaskUIPage.switchFeedbackNotificationsIndicator(Enums.ToggleAction.Hide);
        Assert.assertTrue( rpcDiscoveryTaskUIPage.clickAllProductsAndCheckTaskVisibility() );
    }

    public AssignmentDetails getAssignmentDetails(String assignmentName, Enums.TaskType taskType) {
        AssignmentDetails assignmentDetails = new AssignmentDetails();
        assignmentDetails.assignmentName = assignmentName;
        assignmentDetails.taskType = taskType;
        assignmentDetails.requiredStatus = Enums.TaskStatus.Available;
        return assignmentDetails;
    }
}
