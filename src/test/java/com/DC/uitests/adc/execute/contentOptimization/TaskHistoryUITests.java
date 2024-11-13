package com.DC.uitests.adc.execute.contentOptimization;

import com.DC.constants.InsightsConstants;
import com.DC.constants.ProductVersioningConstants;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.execute.contentOptimization.taskhistory.*;
import com.DC.pageobjects.adc.execute.contentOptimization.TasksPage;
import com.DC.pageobjects.adc.execute.contentOptimization.taskui.contentTasks.ContentClientReviewTaskUI;
import com.DC.pageobjects.adc.execute.contentOptimization.taskui.contentTasks.ContentTaskUI;
import com.DC.pageobjects.adc.execute.contentOptimization.taskui.imageTasks.ImageReviewTaskUI;
import com.DC.pageobjects.adc.execute.contentOptimization.taskui.imageTasks.ImageTaskUI;
import com.DC.pageobjects.adc.execute.productManager.products.ProductsPage;
import com.DC.testcases.BaseClass;
import com.DC.utilities.*;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.TaskUIRequests;
import com.DC.utilities.apiEngine.apiServices.insights.CPGAccount.CPGAccountService;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.DigitalAssetProperty;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductVariantProperty;
import com.DC.utilities.enums.Enums;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.DC.constants.NetNewConstants.DC_LOGIN_ENDPOINT;

public class TaskHistoryUITests extends BaseClass {
    private final String USERNAME = READ_CONFIG.getInsightsSupportUsername();
    private final String PASSWORD = READ_CONFIG.getInsightsPassword();

    private TaskHistoryPage taskHistoryPage;
    private String jwt;
    private String expectedUser;
    private Company company;

    @DataProvider
    public static Object[][] columnsToTest() {
        return new Object[][]{
                {"Assignment Name"},
                {"User"},
                {"Decision"}
        };
    }

    @BeforeClass
    public void setupTests(ITestContext testContext) throws Exception {
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        driver.get(DC_LOGIN_ENDPOINT);
        new DCLoginPage(driver).login(USERNAME, PASSWORD);
        driver.get(InsightsConstants.INSIGHTS_TASK_HISTORY_URL);
        taskHistoryPage = new TaskHistoryPage(driver);
        jwt = SecurityAPI.getJwtForInsightsUser(driver);
        var accountInfo = CPGAccountService.getAccountInfo(jwt);
        expectedUser = accountInfo.firstName + " " + accountInfo.lastName;
        company = CompanyApiService.getCompany(jwt);
    }

    @BeforeMethod
    public void navigateToTaskHistoryPage() {
        taskHistoryPage = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);
    }

    @AfterClass(alwaysRun = true)
    public void killDriver() {
        quitBrowser();
    }

    @Test(description = "C243656 - Verify Counter value on top of the screen")
    public void TaskHistory_VerifyCorrectAmountOfRecordsAreDisplayed() throws Exception {
        var payload = new JSONObject();
        payload.put("pageSize", 25);
        payload.put("page", 1);
        payload.put("filters", new ArrayList<>());

        var response = TaskUIRequests.getTaskHistoryGrid(payload, jwt);
        var expectedNumberOfRecords = response.jsonPath().getInt("totalRowCount");
        var numberOfRecordsDisplayed = taskHistoryPage.getNumberOfRecordsDisplayed();
        Assert.assertEquals(numberOfRecordsDisplayed, expectedNumberOfRecords, "Number of records displayed does not match expected number of records");
    }

    @Test(description = "C255901 - Verify default timestamp filter is set to last 30 days, Newest to oldest")
    public void TaskHistory_VerifyDefaultTimestampFilterIsCorrect() {
        var timestampFilter = taskHistoryPage.openTimestampFilter();

        var selectedSortOption = timestampFilter.getSelectedSortOption();
        Assert.assertEquals(selectedSortOption, "Newest to Oldest", "Default sort option is not correct");

        var selectedDateFilterOption = timestampFilter.getSelectedDateFilterOption();
        Assert.assertEquals(selectedDateFilterOption, "Last 30 Days", "Default date filter option is not correct");
    }

    @Test(description = "C243659 - Verify user can change the date range on Timestamp column")
    public void TaskHistory_VerifyTimestampFilterWorksAsExpected() {
        var timestampOfFirstRowBefore = taskHistoryPage.getFirstRowData().get("Timestamp");

        // Sort oldest to newest
        var timestampFilter = taskHistoryPage.openTimestampFilter();
        timestampFilter.selectSortOption("Oldest to Newest");
        taskHistoryPage = timestampFilter.clickUpdateButton(TaskHistoryPage.class);

        var timestampOfFirstRowAfter = taskHistoryPage.getFirstRowData().get("Timestamp");
        Assert.assertNotEquals(timestampOfFirstRowBefore, timestampOfFirstRowAfter, "Records were not sorted correctly");

        // Sort newest to oldest, last 60 days
        timestampFilter = taskHistoryPage.openTimestampFilter();
        timestampFilter.selectSortOption("Newest to Oldest");
        timestampFilter.selectDateFilterOption("Last 60 Days");
        taskHistoryPage = timestampFilter.clickUpdateButton(TaskHistoryPage.class);

        timestampOfFirstRowAfter = taskHistoryPage.getFirstRowData().get("Timestamp");
        Assert.assertEquals(timestampOfFirstRowBefore, timestampOfFirstRowAfter, "Records were not sorted back to default");

        // Use custom date range
        var startDate = DateUtility.formatDate(LocalDate.now().minusYears(1).toString());
        var endDate = DateUtility.formatDate(LocalDate.now().toString());

        timestampFilter = taskHistoryPage.openTimestampFilter();
        timestampFilter.selectDateRange(startDate, endDate);
        taskHistoryPage = timestampFilter.clickUpdateButton(TaskHistoryPage.class);

        timestampFilter = taskHistoryPage.openTimestampFilter();
        var selectedDateFilterOption = timestampFilter.getSelectedDateFilterOption();
        Assert.assertEquals(selectedDateFilterOption, "Date Range", selectedDateFilterOption + " was still selected after inserting custom range");

        timestampFilter.clickCancelButton();
    }

    @Test(description = "C243661 - Verify user can sort Assignment Name")
    public void TaskHistory_VerifySortableColumns() throws InterruptedException {
        var columnToTest = "Assignment Name";
        var assignmentNameOfFirstRow = taskHistoryPage.getFirstRowData().get(columnToTest);

        // Sort A to Z
        var dropdownFilter = taskHistoryPage.openDropdownFilter(columnToTest);
        dropdownFilter.selectSortOption("A to Z");
        taskHistoryPage = dropdownFilter.clickUpdateButton(TaskHistoryPage.class);

        var assignmentNameOfFirstRowAfter = taskHistoryPage.getFirstRowData().get(columnToTest);
        Assert.assertNotEquals(assignmentNameOfFirstRow, assignmentNameOfFirstRowAfter, "Records were not sorted correctly by " + columnToTest);

        // Sort Z to A
        dropdownFilter = taskHistoryPage.openDropdownFilter(columnToTest);
        dropdownFilter.selectSortOption("Z to A");
        taskHistoryPage = dropdownFilter.clickUpdateButton(TaskHistoryPage.class);

        assignmentNameOfFirstRowAfter = taskHistoryPage.getFirstRowData().get(columnToTest);
        Assert.assertEquals(assignmentNameOfFirstRow, assignmentNameOfFirstRowAfter, "Records were not sorted correctly by " + columnToTest);
    }

    @Test(description = "C243662, C243663, C243665 - Verify user can filter by Assignment Name, User and Decision", dataProvider = "columnsToTest")
    public void TaskHistory_VerifyFiltersWorksAsExpected(String columnToTest) throws InterruptedException {
        var cellValueToFilter = taskHistoryPage.getFirstRowData().get(columnToTest);

        var dropdownFilter = taskHistoryPage.openDropdownFilter(columnToTest);
        dropdownFilter.search(cellValueToFilter);
        var options = dropdownFilter.getAllOptions();
        options.forEach(option -> Assert.assertTrue(option.contains(cellValueToFilter), "Option " + option + " does not contain " + cellValueToFilter));
        taskHistoryPage = dropdownFilter.selectOptionAndApplyChanges(cellValueToFilter, TaskHistoryPage.class);

        List<String> allColumnCellValuesDisplayed = taskHistoryPage.getTableData().stream().map(map -> map.get(columnToTest)).collect(Collectors.toList());

        Assert.assertTrue(allColumnCellValuesDisplayed.stream().allMatch(value -> value.equals(cellValueToFilter)), "Not all records in column " + columnToTest + " are " + cellValueToFilter);
    }

    //TODO - TWO ASSERTIONS FAILING IN LAST STEP DUE TO BUG CGEN-603
    @Test(description = "C243666. Full one review content chain completion. Checking task history is generated properly")
    public void TaskHistory_VerifyTaskDetails_ContentTask() throws Exception {
        List<String> idsOfPropertiesToTest = new ArrayList<>(List.of("product_title", "bullet_1"));

        var softAssert = new SoftAssert();
        var dateToday = DateUtility.getCurrentDate("M/dd/yyyy");
        var productToTest = new UserFriendlyInstancePath("QA-TASK-001", "en-US", "Amazon.com", null);
        var expectedProductNamesInDetailsModal = List.of(productToTest.productIdentifier + " | " + productToTest.retailerName);

        var taskDetails = new HashMap<String, String>() {{
            put("Topic", "QA Topic");
            put("Title", "Content task for task history tests " + SharedMethods.generateRandomNumber());
            put("Subject", "QA Subject");
        }};

        var taskTitle = taskDetails.get("Title");
        var textPrefix = "Random text for task history tests " + SharedMethods.generateRandomNumber();
        List<TaskHistoryObjects.GeneralFeedbackBox> expectedFeedbackBoxesInModal = new ArrayList<>();

        var firstTimestampBefore = taskHistoryPage.getFirstRowData().get("Timestamp");

        // CONTENT CREATE TASK
        var contentTask = launchTaskAndGoToTasksPage(productToTest, "PV Content Create - One Review", taskDetails)
                .searchTask(taskTitle)
                .clickTaskTitle(taskTitle, ContentTaskUI.class);

        var instancePath = productToTest.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED);
        List<ProductVariantProperty> stagedProperties = ProductVersioningApiService.getPropertySetData(instancePath, company._id, jwt).properties;

        String oldProductTitle = null;

        if (stagedProperties != null) {
            var iterator = stagedProperties.iterator();
            while (iterator.hasNext()) {
                var property = iterator.next();
                if (property.id.equals(idsOfPropertiesToTest.get(0))) {
                    oldProductTitle = property.values.get(0).toString();
                    iterator.remove();
                    break;
                }
            }
        }

        var newProductTitle = textPrefix + " from creation task";
        contentTask.replaceTextOfField(idsOfPropertiesToTest.get(0), newProductTitle);

        var expectedDataInDetailsTable = generateUnchangedExpectedModalProperties(stagedProperties);
        var taskDetailsModalProperties = generateExpectedModalProperties(idsOfPropertiesToTest.get(0), oldProductTitle, newProductTitle, false);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);
        var assignmentName = contentTask.getAssignmentName();

        var generalFeedback = "Random feedback for task history tests " + SharedMethods.generateRandomNumber();
        contentTask.insertGeneralFeedback(generalFeedback);
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedback));

        contentTask.saveTask();

        taskHistoryPage = contentTask.submitTaskAndRefreshTasksTable(false)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        // TESTING TASK HISTORY
        LinkedHashMap<String, String> expectedRecord = new LinkedHashMap<>();
        expectedRecord.put("Task Title", taskTitle);
        expectedRecord.put("Product Versions", "1");
        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("User", expectedUser);
        expectedRecord.put("Decision", "Submit");

        var timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(firstTimestampBefore, expectedRecord);
        var expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // CONTENT INTERNAL REVIEW TASK
        contentTask = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle)
                .clickTaskTitle(taskTitle, ContentTaskUI.class);

        oldProductTitle = newProductTitle;
        newProductTitle = textPrefix + " from internal review";
        contentTask.replaceTextOfField(idsOfPropertiesToTest.get(0), newProductTitle);

        expectedDataInDetailsTable.remove(taskDetailsModalProperties);
        taskDetailsModalProperties = generateExpectedModalProperties(idsOfPropertiesToTest.get(0), oldProductTitle, newProductTitle, false);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);
        assignmentName = contentTask.getAssignmentName();

        generalFeedback = "Random feedback for task history tests from Internal Review " + SharedMethods.generateRandomNumber();
        expectedFeedbackBoxesInModal.clear();
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedback));
        contentTask.insertGeneralFeedback(generalFeedback);

        contentTask.saveTask();

        taskHistoryPage = contentTask.approveOrRejectInternalReviewAndSubmit(ProductVersioningConstants.ReviewVerdict.APPROVE, 4)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        // TESTING TASK HISTORY
        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Approve");

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(firstTimestampBefore, expectedRecord);
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // CONTENT REVIEW TASK - REJECT
        var contentReview = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle)
                .clickTaskTitle(taskTitle, ContentClientReviewTaskUI.class);

        assignmentName = contentReview.getAssignmentName();

        generalFeedback = "Random feedback for task history tests from Client Review " + SharedMethods.generateRandomNumber();
        expectedFeedbackBoxesInModal.clear();
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedback));
        contentReview.insertGeneralFeedback(generalFeedback);

        contentReview.saveTask();

        taskHistoryPage = contentReview.approveOrRejectClientReviewAndSubmit(ProductVersioningConstants.ReviewVerdict.REVIEW, false)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        stagedProperties = ProductVersioningApiService.getPropertySetData(instancePath, company._id, jwt).properties;
        expectedDataInDetailsTable = generateUnchangedExpectedModalProperties(stagedProperties);

        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Revisions");

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(timestampOfFirstRowAfter, expectedRecord);
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // CONTENT REVISION TASK
        contentTask = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle)
                .clickTaskTitle(taskTitle, ContentTaskUI.class);

        oldProductTitle = newProductTitle;
        newProductTitle = textPrefix + " from revisions";
        var newBulletOne = "Random text for bullet one " + SharedMethods.generateRandomNumber();
        contentTask.replaceTextOfField(idsOfPropertiesToTest.get(0), newProductTitle);
        contentTask.replaceTextOfField(idsOfPropertiesToTest.get(1), newBulletOne);

        stagedProperties = ProductVersioningApiService.getPropertySetData(instancePath, company._id, jwt).properties;

        String oldBulletOneValue = null;

        var count = 0;
        var iterator = stagedProperties.iterator();
        while (iterator.hasNext()) {
            var property = iterator.next();
            if (count == idsOfPropertiesToTest.size()) {
                break;
            }
            if (property.id.equals(idsOfPropertiesToTest.get(1))) {
                oldBulletOneValue = property.values.get(0).toString();
                iterator.remove();
                count++;
            } else if (property.id.equals(idsOfPropertiesToTest.get(0))) {
                oldProductTitle = property.values.get(0).toString();
                iterator.remove();
                count++;
            }
        }

        expectedDataInDetailsTable = generateUnchangedExpectedModalProperties(stagedProperties);
        taskDetailsModalProperties = generateExpectedModalProperties(idsOfPropertiesToTest.get(1), oldBulletOneValue, newBulletOne, false);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);
        taskDetailsModalProperties = generateExpectedModalProperties(idsOfPropertiesToTest.get(0), oldProductTitle, newProductTitle, false);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);
        assignmentName = contentTask.getAssignmentName();

        generalFeedback = "Random feedback for task history tests from Revisions " + SharedMethods.generateRandomNumber();
        expectedFeedbackBoxesInModal.clear();
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedback));
        contentTask.insertGeneralFeedback(generalFeedback);

        contentTask.saveTask();

        taskHistoryPage = contentTask.submitTaskAndRefreshTasksTable(false)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Submit");

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(timestampOfFirstRowAfter, expectedRecord);
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // CONTENT REVIEW - APPROVE
        contentReview = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle)
                .clickTaskTitle(taskTitle, ContentClientReviewTaskUI.class);
        assignmentName = contentReview.getAssignmentName();

        expectedDataInDetailsTable = generateUnchangedExpectedModalProperties(stagedProperties);
        taskDetailsModalProperties = generateExpectedModalProperties(idsOfPropertiesToTest.get(1), newBulletOne, null, true);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);
        taskDetailsModalProperties = generateExpectedModalProperties(idsOfPropertiesToTest.get(0), newProductTitle, null, true);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);

        contentReview.saveTask();

        taskHistoryPage = contentReview.approveOrRejectClientReviewAndSubmit(ProductVersioningConstants.ReviewVerdict.APPROVE, true)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Approve");

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(timestampOfFirstRowAfter, expectedRecord);
        expectedFeedbackBoxesInModal.clear();
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        softAssert.assertAll();
    }

    // TODO - GENERAL FEEDBACK ASSERTIONS FAILING DUE TO CGEN-602
    @Test(description = "C255902 - Full one review image chain completion. Checking task history is generated properly")
    public void TaskHistory_VerifyTaskDetails_ImageTask() throws Exception {
        List<Map.Entry<String, String>> idAndNameOfPropertiesToTest = new ArrayList<>();
        idAndNameOfPropertiesToTest.add(Map.entry("calories_image", "Calories Image"));
        idAndNameOfPropertiesToTest.add(Map.entry("flavor_image", "Flavor Image"));

        var softAssert = new SoftAssert();
        var dateToday = DateUtility.getCurrentDate("M/dd/yyyy");
        var productToTest = new UserFriendlyInstancePath("QA-TASK-001", "en-US", null, null);
        var expectedProductNamesInDetailsModal = List.of(productToTest.productIdentifier + " | Base");

        var taskDetails = new HashMap<String, String>() {{
            put("Topic", "QA Topic");
            put("Title", "Image task for task history tests " + SharedMethods.generateRandomNumber());
            put("Subject", "QA Subject");
        }};

        var taskTitle = taskDetails.get("Title");
        List<TaskHistoryObjects.GeneralFeedbackBox> expectedFeedbackBoxesInModal = new ArrayList<>();

        var firstTimestampBefore = taskHistoryPage.getFirstRowData().get("Timestamp");

        // IMAGE CREATE TASK
        var imageTask = launchTaskAndGoToTasksPage(productToTest, "PV Image Create - One Review", taskDetails)
                .searchTask(taskTitle)
                .clickTaskTitle(taskTitle, ImageTaskUI.class);

        var instancePath = productToTest.convertToInstancePath(company, jwt, Enums.ProductVariantType.STAGED);
        List<DigitalAssetProperty> stagedDigitalAssets = ProductVersioningApiService.getDigitalAssetSetData(instancePath, company._id, jwt).digitalAssets;

        String oldDigitalAssetUrlForFirstProperty = null;

        var basePath = System.getProperty("user.dir") + "/src/test/java/com/DC/utilities/samples/images/";
        var image1 = "egg.png";
        var image2 = "ghost.png";

        if (stagedDigitalAssets != null) {
            var iterator = stagedDigitalAssets.iterator();
            while (iterator.hasNext()) {
                var digitalAsset = iterator.next();
                if (digitalAsset.id.equals(idAndNameOfPropertiesToTest.get(0).getKey())) {
                    oldDigitalAssetUrlForFirstProperty = digitalAsset.assets.get(0).url;
                    iterator.remove();
                    break;
                }
            }
        }

        var imageToAssign = oldDigitalAssetUrlForFirstProperty == null || oldDigitalAssetUrlForFirstProperty.contains(image1) ? image2 : image1;
        imageTask.assignNewMediaFileToProduct(1, idAndNameOfPropertiesToTest.get(0).getValue(), basePath + imageToAssign);
        var newDigitalAssetUrlForFirstProperty = imageTask.getMediaUrl(idAndNameOfPropertiesToTest.get(0).getValue());

        var expectedDataInDetailsTable = generateUnchangedExpectedModalDigitalAssets(stagedDigitalAssets);
        var taskDetailsModalProperties = generateExpectedModalDigitalAssets(idAndNameOfPropertiesToTest.get(0).getKey(), oldDigitalAssetUrlForFirstProperty, newDigitalAssetUrlForFirstProperty, false);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);
        var assignmentName = imageTask.getAssignmentName();

        var generalFeedbackFromCreate = "Feedback for task history tests from image creation " + SharedMethods.generateRandomNumber();
        imageTask.insertGeneralFeedback(generalFeedbackFromCreate);
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedbackFromCreate));
        imageTask.saveTask();

        taskHistoryPage = imageTask.submitTaskAndRefreshTasksTable(false)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        // TESTING TASK HISTORY
        LinkedHashMap<String, String> expectedRecord = new LinkedHashMap<>();
        expectedRecord.put("Task Title", taskTitle);
        expectedRecord.put("Product Versions", "1");
        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("User", expectedUser);
        expectedRecord.put("Decision", "Submit");

        var timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(firstTimestampBefore, expectedRecord);
        var expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // IMAGE INTERNAL REVIEW TASK
        var imageReviewTask = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle)
                .clickTaskTitle(taskTitle, ImageReviewTaskUI.class);

        assignmentName = imageTask.getAssignmentName();
        var generalFeedback = "Random feedback for task history tests from Image Internal Review " + SharedMethods.generateRandomNumber();
        expectedFeedbackBoxesInModal.clear();
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedback));
        imageReviewTask.insertGeneralFeedback(generalFeedback);

        taskHistoryPage = imageReviewTask.approveOrRejectInternalReviewAndSubmit(ProductVersioningConstants.ReviewVerdict.REVIEW, 1)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        stagedDigitalAssets = ProductVersioningApiService.getDigitalAssetSetData(instancePath, company._id, jwt).digitalAssets;
        expectedDataInDetailsTable = generateUnchangedExpectedModalDigitalAssets(stagedDigitalAssets);

        // TESTING TASK HISTORY
        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Rework");

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(firstTimestampBefore, expectedRecord);
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // IMAGE REWORK
        imageTask = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle).clickTaskTitle(taskTitle, ImageTaskUI.class);

        imageToAssign = imageToAssign.equals(image1) ? image2 : image1;

        var count = 0;
        var iterator = stagedDigitalAssets.iterator();
        while (iterator.hasNext()) {
            var digitalAsset = iterator.next();
            if (count == idAndNameOfPropertiesToTest.size()) {
                break;
            }
            if (digitalAsset.id.equals(idAndNameOfPropertiesToTest.get(0).getKey())) {
                oldDigitalAssetUrlForFirstProperty = digitalAsset.assets.get(0).url;
                iterator.remove();
                count++;
            }
        }

        imageTask.assignNewMediaFileToProduct(1, idAndNameOfPropertiesToTest.get(0).getValue(), basePath + imageToAssign);
        newDigitalAssetUrlForFirstProperty = imageTask.getMediaUrl(idAndNameOfPropertiesToTest.get(0).getValue());

        expectedDataInDetailsTable = generateUnchangedExpectedModalDigitalAssets(stagedDigitalAssets);
        taskDetailsModalProperties = generateExpectedModalDigitalAssets(idAndNameOfPropertiesToTest.get(0).getKey(), oldDigitalAssetUrlForFirstProperty, newDigitalAssetUrlForFirstProperty, false);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);

        assignmentName = imageTask.getAssignmentName();

        generalFeedback = "Feedback for task history tests from Image Rework " + SharedMethods.generateRandomNumber();
        expectedFeedbackBoxesInModal.clear();
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedbackFromCreate));
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedback));
        imageTask.insertGeneralFeedback(generalFeedback);

        imageTask.saveTask();

        taskHistoryPage = imageTask.submitTaskAndRefreshTasksTable(false)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Submit");

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(timestampOfFirstRowAfter, expectedRecord);
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // IMAGE INTERNAL REVIEW TASK - APPROVE
        imageReviewTask = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle)
                .clickTaskTitle(taskTitle, ImageReviewTaskUI.class);

        assignmentName = imageTask.getAssignmentName();
        generalFeedback = "Random feedback for task history tests from Image Internal Review. Approve " + SharedMethods.generateRandomNumber();
        imageReviewTask.insertGeneralFeedback(generalFeedback);
        expectedFeedbackBoxesInModal.clear();

        taskHistoryPage = imageReviewTask.approveOrRejectInternalReviewAndSubmit(ProductVersioningConstants.ReviewVerdict.APPROVE, 4)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        stagedDigitalAssets = ProductVersioningApiService.getDigitalAssetSetData(instancePath, company._id, jwt).digitalAssets;
        expectedDataInDetailsTable = generateUnchangedExpectedModalDigitalAssets(stagedDigitalAssets);

        // TESTING TASK HISTORY
        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Approve");

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(firstTimestampBefore, expectedRecord);
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // IMAGE CLIENT REVIEW TASK - REJECT
        imageReviewTask = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle).clickTaskTitle(taskTitle, ImageReviewTaskUI.class);

        assignmentName = imageReviewTask.getAssignmentName();
        generalFeedback = "Feedback for task history tests from Image Client Review " + SharedMethods.generateRandomNumber();
        expectedFeedbackBoxesInModal.clear();
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedback));
        imageReviewTask.insertGeneralFeedback(generalFeedback);

        imageTask.saveTask();

        taskHistoryPage = imageReviewTask.approveOrRejectClientReviewAndSubmit(ProductVersioningConstants.ReviewVerdict.REVIEW, false)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Revisions");

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(timestampOfFirstRowAfter, expectedRecord);
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // IMAGE REVISION TASK
        imageTask = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle).clickTaskTitle(taskTitle, ImageTaskUI.class);

        imageToAssign = imageToAssign.equals(image1) ? image2 : image1;

        image1 = "egg.png";
        image2 = "ghost.png";

        String oldDigitalAssetUrlSecondProperty = null;

        count = 0;
        iterator = stagedDigitalAssets.iterator();
        while (iterator.hasNext()) {
            var digitalAsset = iterator.next();
            if (count == idAndNameOfPropertiesToTest.size()) {
                break;
            }
            if (digitalAsset.id.equals(idAndNameOfPropertiesToTest.get(0).getKey())) {
                oldDigitalAssetUrlForFirstProperty = digitalAsset.assets.get(0).url;
                iterator.remove();
                count++;
            } else if (digitalAsset.id.equals(idAndNameOfPropertiesToTest.get(1).getKey())) {
                oldDigitalAssetUrlSecondProperty = digitalAsset.assets.get(0).url;
                iterator.remove();
                count++;
            }
        }

        var imageToAssignToSecondProperty = oldDigitalAssetUrlSecondProperty == null || oldDigitalAssetUrlSecondProperty.contains(image1) ? image2 : image1;

        imageTask.assignNewMediaFileToProduct(1, idAndNameOfPropertiesToTest.get(0).getValue(), basePath + imageToAssign);
        imageTask.assignNewMediaFileToProduct(1, idAndNameOfPropertiesToTest.get(1).getValue(), basePath + imageToAssignToSecondProperty);

        newDigitalAssetUrlForFirstProperty = imageTask.getMediaUrl(idAndNameOfPropertiesToTest.get(0).getValue());
        var newDigitalAssetUrlForSecondProperty = imageTask.getMediaUrl(idAndNameOfPropertiesToTest.get(1).getValue());

        expectedDataInDetailsTable = generateUnchangedExpectedModalDigitalAssets(stagedDigitalAssets);
        taskDetailsModalProperties = generateExpectedModalDigitalAssets(idAndNameOfPropertiesToTest.get(0).getKey(), oldDigitalAssetUrlForFirstProperty, newDigitalAssetUrlForFirstProperty, false);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);
        taskDetailsModalProperties = generateExpectedModalDigitalAssets(idAndNameOfPropertiesToTest.get(1).getKey(), oldDigitalAssetUrlSecondProperty, newDigitalAssetUrlForSecondProperty, false);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);

        assignmentName = imageTask.getAssignmentName();

        generalFeedback = "Feedback for task history tests from Image Revision " + SharedMethods.generateRandomNumber();
        expectedFeedbackBoxesInModal.clear();
        expectedFeedbackBoxesInModal.add(new TaskHistoryObjects.GeneralFeedbackBox(expectedUser, dateToday, generalFeedback));
        imageTask.insertGeneralFeedback(generalFeedback);

        imageTask.saveTask();

        taskHistoryPage = imageTask.submitTaskAndRefreshTasksTable(false)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Submit");

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(timestampOfFirstRowAfter, expectedRecord);
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        // IMAGE REVIEW - APPROVE
        imageReviewTask = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class)
                .searchTask(taskTitle)
                .clickTaskTitle(taskTitle, ImageReviewTaskUI.class);

        assignmentName = imageReviewTask.getAssignmentName();

        imageReviewTask.saveTask();

        expectedDataInDetailsTable = generateUnchangedExpectedModalDigitalAssets(stagedDigitalAssets);
        taskDetailsModalProperties = generateExpectedModalDigitalAssets(idAndNameOfPropertiesToTest.get(0).getKey(), newDigitalAssetUrlForFirstProperty, null, true);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);
        taskDetailsModalProperties = generateExpectedModalDigitalAssets(idAndNameOfPropertiesToTest.get(1).getKey(), newDigitalAssetUrlForSecondProperty, null, true);
        expectedDataInDetailsTable.add(taskDetailsModalProperties);
        taskHistoryPage = imageReviewTask.approveOrRejectClientReviewAndSubmit(ProductVersioningConstants.ReviewVerdict.APPROVE, true)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASK_HISTORY_URL, TaskHistoryPage.class);

        expectedRecord.put("Assignment Name", assignmentName);
        expectedRecord.put("Decision", "Approve");

        expectedFeedbackBoxesInModal.clear();

        timestampOfFirstRowAfter = verifyTaskRecordInMainPageAndReturnTimestamp(timestampOfFirstRowAfter, expectedRecord);
        expectedDetails = new TaskHistoryObjects.TaskHistoryDetailsData(timestampOfFirstRowAfter, assignmentName, taskTitle, expectedUser, expectedProductNamesInDetailsModal, expectedFeedbackBoxesInModal);
        verifyDetailsModal(expectedDetails, expectedDataInDetailsTable, softAssert);

        softAssert.assertAll();
    }

    @Test(description = "C243667 - Verify search functionality")
    public void TaskHistory_VerifySearchFunctionality() {
        taskHistoryPage = taskHistoryPage.searchRecord("Non existing record");
        var noDataRowDisplayed = taskHistoryPage.isNoDataRowDisplayed();
        Assert.assertTrue(noDataRowDisplayed, "No Data row is not displayed when searching for a non existing record");

        var recordToTest = taskHistoryPage.getFirstRowData().get("Task Title");
        taskHistoryPage = taskHistoryPage.searchRecord(recordToTest);
        List<String> allTaskTitlesDisplayed = taskHistoryPage.getTableData().stream().map(map -> map.get("Task Title")).collect(Collectors.toList());
        Assert.assertTrue(allTaskTitlesDisplayed.stream().allMatch(value -> value.contains(recordToTest)), "Not all records in column Task Title are " + recordToTest);
    }

    private TasksPage launchTaskAndGoToTasksPage(UserFriendlyInstancePath productToTest, String chainName, HashMap<String, String> taskDetails) throws Exception {
        var productsPage = taskHistoryPage.navigateToUrl(InsightsConstants.INSIGHTS_PRODUCTS_URL, ProductsPage.class);
        productsPage.searchByProductIdentifier(productToTest.productIdentifier);
        var instanceId = productsPage.getRowId(productToTest);
        return productsPage.selectProduct(productToTest).clickGenerateLaunchFile()
                .fillSectionFields("Batch_" + LocalDate.now(), 1, chainName)
                .setTaskNumber(instanceId, 1)
                .clickContinue()
                .configureAndLaunchBatch(instanceId, taskDetails)
                .navigateToUrl(InsightsConstants.INSIGHTS_TASKS_URL, TasksPage.class);
    }

    private LinkedList<DiffUtility.Diff> getExpectedDifferencesForContentTasks(String oldPropertyValue, String newPropertyValue, boolean wasPropertyCommitted) {
        LinkedList<DiffUtility.Diff> expectedDifferences = new LinkedList<>();

        if (wasPropertyCommitted) {
            var chunks = oldPropertyValue.split(" ");
            Arrays.stream(chunks).map(chunk -> new DiffUtility.Diff(DiffUtility.Operation.DELETE, chunk)).forEach(expectedDifferences::add);
            return expectedDifferences;
        }

        if (((oldPropertyValue == null) || oldPropertyValue.isEmpty()) && newPropertyValue != null) {
            var chunks = newPropertyValue.split(" ");
            Arrays.stream(chunks).map(chunk -> new DiffUtility.Diff(DiffUtility.Operation.INSERT, chunk)).forEach(expectedDifferences::add);
            return expectedDifferences;
        }

        if ((newPropertyValue == null) || Objects.equals(oldPropertyValue, newPropertyValue)) {
            return null;
        }

        return DiffUtility.compare(oldPropertyValue, newPropertyValue);
    }

    private String verifyTaskRecordInMainPageAndReturnTimestamp(String firstTimestampBefore, LinkedHashMap<String, String> expectedRecordWithoutTimestamp) {
        var firstRowData = taskHistoryPage.getFirstRowData();
        var timestampOfFirstRowAfter = firstRowData.get("Timestamp");
        Assert.assertNotEquals(firstTimestampBefore, timestampOfFirstRowAfter, "Task history table was not updated after submitting a task");

        firstRowData.remove("Timestamp");
        Assert.assertEquals(firstRowData, expectedRecordWithoutTimestamp, "Task history data in main task history page doesn't match with the expected data" +
                "\nExpected: " + expectedRecordWithoutTimestamp + "\nActual: " + firstRowData
        );
        return timestampOfFirstRowAfter;
    }

    private void verifyPropertyDiffsInDetailsModal(TaskHistoryPage.TaskDetails taskDetailsModal, List<TaskHistoryObjects.TaskDetailsProperties> expectedDataInDetailsTable, SoftAssert softAssert) throws Exception {
        List<TaskHistoryObjects.TaskDetailsProperties> tableData = taskDetailsModal.getTableData();
        sortPropertiesByIdAndDifferenceColumn(tableData);
        sortPropertiesByIdAndDifferenceColumn(expectedDataInDetailsTable);

        for (int i = 0; i < tableData.size(); i++) {
            var actual = tableData.get(i);
            var expected = expectedDataInDetailsTable.get(i);
            softAssert.assertEquals(actual.propertyId, expected.propertyId, "Property ID in task details modal is not correct");
            softAssert.assertEquals(actual.oldPropertyValue, expected.oldPropertyValue, "Old property value in task details modal is not correct");
            softAssert.assertEquals(actual.newPropertyValue, expected.newPropertyValue, "New property value in task details modal is not correct");
            softAssert.assertEquals(actual.differenceColumn, expected.differenceColumn, "Difference column in task details modal is not correct." +
                    "\nExpected: " + expected.differenceColumn + "\nActual: " + actual.differenceColumn
            );
        }
    }

    private void sortPropertiesByIdAndDifferenceColumn(List<TaskHistoryObjects.TaskDetailsProperties> properties) {
        for (var property : properties) {
            if (property.differenceColumn != null) {
                property.differenceColumn.sort(Comparator.comparing(a -> a.text));
            }
        }
        properties.sort(Comparator.comparing(a -> a.propertyId.text));
    }

    private List<TaskHistoryObjects.TaskDetailsProperties> generateUnchangedExpectedModalProperties(List<ProductVariantProperty> unchangedStagedProperties) {
        if (unchangedStagedProperties == null || unchangedStagedProperties.isEmpty()) {
            return new ArrayList<>();
        }
        return unchangedStagedProperties.stream().map(unchangedProperty -> new TaskHistoryObjects.TaskDetailsProperties(
                new DiffUtility.Diff(DiffUtility.Operation.EQUAL, unchangedProperty.id),
                new DiffUtility.Diff(DiffUtility.Operation.EQUAL, unchangedProperty.values.get(0).toString()),
                null,
                null
        )).collect(Collectors.toList());
    }

    private List<TaskHistoryObjects.TaskDetailsProperties> generateUnchangedExpectedModalDigitalAssets(List<DigitalAssetProperty> unchangedStagedDigitalAssets) {
        if (unchangedStagedDigitalAssets == null) {
            return new ArrayList<>();
        }
        return unchangedStagedDigitalAssets.stream().map(unchangedProperty -> new TaskHistoryObjects.TaskDetailsProperties(
                new DiffUtility.Diff(DiffUtility.Operation.EQUAL, unchangedProperty.id),
                new DiffUtility.Diff(DiffUtility.Operation.EQUAL, unchangedProperty.assets.get(0).url),
                null,
                null
        )).collect(Collectors.toList());
    }

    private TaskHistoryObjects.TaskDetailsProperties generateExpectedModalProperties(String propertyId, String oldPropertyValue, String newPropertyValue, boolean wasPropertyCommitted) {
        var taskDetailsProperties = new TaskHistoryObjects.TaskDetailsProperties();
        taskDetailsProperties.propertyId = new DiffUtility.Diff(DiffUtility.Operation.EQUAL, propertyId);
        taskDetailsProperties.oldPropertyValue = oldPropertyValue == null ? null : new DiffUtility.Diff(DiffUtility.Operation.EQUAL, oldPropertyValue);
        taskDetailsProperties.newPropertyValue = newPropertyValue == null ? null : new DiffUtility.Diff(DiffUtility.Operation.EQUAL, newPropertyValue);
        taskDetailsProperties.differenceColumn = getExpectedDifferencesForContentTasks(oldPropertyValue, newPropertyValue, wasPropertyCommitted);
        return taskDetailsProperties;
    }

    private LinkedList<DiffUtility.Diff> getExpectedDifferencesForImageTasks(String oldPropertyValue, String newPropertyValue, boolean wasPropertyCommitted) {
        LinkedList<DiffUtility.Diff> expectedDifferences = new LinkedList<>();

        if (wasPropertyCommitted) {
            expectedDifferences.add(new DiffUtility.Diff(DiffUtility.Operation.DELETE, oldPropertyValue));
        } else {
            expectedDifferences.add(new DiffUtility.Diff(DiffUtility.Operation.EQUAL, newPropertyValue));
        }
        return expectedDifferences;
    }

    private TaskHistoryObjects.TaskDetailsProperties generateExpectedModalDigitalAssets(String propertyId, String oldUrl, String newUrl, boolean wasPropertyCommitted) {
        var taskDetailsProperties = new TaskHistoryObjects.TaskDetailsProperties();
        taskDetailsProperties.propertyId = new DiffUtility.Diff(DiffUtility.Operation.EQUAL, propertyId);
        taskDetailsProperties.oldPropertyValue = oldUrl == null ? null : new DiffUtility.Diff(DiffUtility.Operation.EQUAL, oldUrl);
        taskDetailsProperties.newPropertyValue = newUrl == null ? null : new DiffUtility.Diff(DiffUtility.Operation.EQUAL, newUrl);
        taskDetailsProperties.differenceColumn = getExpectedDifferencesForImageTasks(oldUrl, newUrl, wasPropertyCommitted);
        return taskDetailsProperties;
    }

    private void verifyDetailsModal(TaskHistoryObjects.TaskHistoryDetailsData expectedBasicInfo, List<TaskHistoryObjects.TaskDetailsProperties> expectedTableData, SoftAssert softAssert) throws Exception {
        var taskDetailsModal = taskHistoryPage.clickFirstTimestampLink();
        var taskHistoryDetails = taskDetailsModal.getTaskHistoryDetails();

        taskHistoryDetails.feedbackBoxes.sort(Comparator.comparing(a -> a.feedback));
        expectedBasicInfo.feedbackBoxes.sort(Comparator.comparing(a -> a.feedback));
        softAssert.assertEquals(taskHistoryDetails, expectedBasicInfo, "Basic info is not correct");

        verifyPropertyDiffsInDetailsModal(taskDetailsModal, expectedTableData, softAssert);
    }
}
