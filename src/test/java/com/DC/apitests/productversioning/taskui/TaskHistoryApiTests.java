package com.DC.apitests.productversioning.taskui;

import com.DC.apitests.ApiValidations;
import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.TaskVersionHistoryCollection;
import com.DC.objects.productVersioning.UserFriendlyInstancePath;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.TaskUIRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import com.DC.utilities.enums.Enums;
import com.beust.ah.A;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.DC.utilities.SecurityAPI.loginAndGetJwt;

public class TaskHistoryApiTests extends BaseClass {
    private final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();
    private final TaskVersionHistoryCollection TASK_HISTORY_COLLECTION = new TaskVersionHistoryCollection();
    private final List<JSONObject> RECORDS_IN_DATABASE = new ArrayList<>();

    private String jwt;

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.username, TEST_CONFIG.password);
        RECORDS_IN_DATABASE.addAll(TASK_HISTORY_COLLECTION.getAllTasksHistoryForCompany(TEST_CONFIG.companyID));
    }

    @Test(priority = 1, description = "Get all aggregated task history data (as related to products) for display")
    public void Api_TaskHistory_CanGetAllTaskHistoryData() throws Exception {
        var payload = new JSONObject();
        payload.put("pageSize", 10);
        payload.put("page", 1);
        payload.put("filters", new ArrayList<>());

        Set<String> uniqueChangeIds = RECORDS_IN_DATABASE.stream().map(record -> record.getJSONObject("meta").getString("changeId")).collect(Collectors.toSet());

        var response = TaskUIRequests.getTaskHistoryGrid(payload, jwt);
        var totalRowCountInResponse = response.jsonPath().getInt("totalRowCount");
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);

        Assert.assertEquals(totalRowCountInResponse, uniqueChangeIds.size(), "Total row count in response does not match expected row count");

        testTimestampFilterReturnsCorrectData();

        payload = new JSONObject();
        payload.put("filters", new ArrayList<>());
        response = TaskUIRequests.getTaskHistoryFilterOptions(payload, jwt);
        var filterOptions = new JSONObject(response.body().asString());
        testFilterReturnsCorrectData(filterOptions, "assignment", "assignmentName");
        testFilterReturnsCorrectData(filterOptions, "user", "user");
        testFilterReturnsCorrectData(filterOptions, "reviewDecision", "reviewDecision");

        // Testing search input
        var taskTitleToTest = ((JSONObject) SharedMethods.getRandomItemFromList(RECORDS_IN_DATABASE)).getJSONObject("meta").getString("taskTitle");
        var productIdentifierToTest = ((JSONObject) SharedMethods.getRandomItemFromList(RECORDS_IN_DATABASE)).getJSONObject("record").getString("unique_id");
        var products = ProductVersioningApiService.getAllProductMastersFromCompany(jwt);
        testFilterSearchIdsReturnsCorrectData(taskTitleToTest, products);
        testFilterSearchIdsReturnsCorrectData(productIdentifierToTest, products);
    }


    @Test(priority = 2, description = "Can get all task history data")
    public void Api_TaskHistory_CanGetAllTaskHistoryDetailsData() throws Exception {
        var randomIndex = SharedMethods.getRandomNumberBetweenRange(1, RECORDS_IN_DATABASE.size() - 1);
        var activityToTest = RECORDS_IN_DATABASE.get(randomIndex);
        var meta = activityToTest.getJSONObject("meta");
        var activityId = meta.getString("workItemId");
        var assignmentId = meta.getString("assignmentId");
        var changeId = meta.getString("changeId");

        List<JSONObject> productsInSameTask = RECORDS_IN_DATABASE.stream().filter(record -> record.getJSONObject("meta").getString("changeId").equals(changeId)).collect(Collectors.toList());

        var response = TaskUIRequests.getTaskHistoryActivityDetails(activityId, assignmentId, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);
        var dataInResponse = new JSONObject(response.body().asString()).getJSONArray("data");

        Assert.assertEquals(response.jsonPath().getInt("totalRowCount"), productsInSameTask.size(), "Total row count in response does not match expected row count");

        var company = CompanyApiService.getCompany(jwt);
        var propertiesToTest = List.of("workItemId", "taskTitle", "assignmentName", "changeContext", "workerId", "workflowItemId");
        var dataComparisonKeys = List.of("properties", "keywords", "attributes");

        for (var data : dataInResponse) {
            var detail = new JSONObject(data.toString());
            var retailerName = detail.get("retailer").toString();
            var campaignName = detail.get("campaign").toString();
            var localeName = detail.get("locale").toString();
            var masterUniqueId = detail.get("masterUniqueId").toString();
            var userFriendlyPath = new UserFriendlyInstancePath(masterUniqueId, localeName, retailerName, campaignName);
            var instancePath = userFriendlyPath.convertToInstancePath(company, jwt, Enums.ProductVariantType.LIVE);

            var recordMetaData = productsInSameTask.stream().filter(record -> Objects.equals(record.getJSONObject("record").get("campaignId"), instancePath.campaignId) &&
                            Objects.equals(record.getJSONObject("record").get("retailerId"), instancePath.retailerId) &&
                            Objects.equals(record.getJSONObject("record").get("localeId"), instancePath.localeId) &&
                            Objects.equals(record.getJSONObject("record").get("unique_id"), userFriendlyPath.productIdentifier)
                    )
                    .findFirst()
                    .orElseThrow(() -> new Exception("Record for instance path " + instancePath + " not found"))
                    .getJSONObject("meta");

            for (var property : propertiesToTest) {
                Assert.assertEquals(detail.get(property), recordMetaData.get(property), "Property " + property + " does not match expected value");
            }

            for (var key : dataComparisonKeys) {
                JSONObject dataInResponseForKey = detail.getJSONObject("dataComparison").getJSONObject(key);
                JSONObject dataInActivity;
                if (key.equals("properties") && detail.get("assignmentName").toString().contains("Image")) {
                    dataInActivity = recordMetaData.getJSONObject("dataComparison").getJSONObject("digitalAssets").getJSONObject("digitalAssets");
                } else {
                    dataInActivity = recordMetaData.getJSONObject("dataComparison").getJSONObject(key).getJSONObject(key);
                }
                dataInActivity.remove("hasChanged");
                Assert.assertEquals(dataInResponseForKey.toString(), dataInActivity.toString(), "Data for key " + key + " does not match expected value");
            }
        }
    }

    @Test(priority = 3, description = "Can get all task history data filter options")
    public void Api_TaskHistory_CanGetAllTaskHistoryDataFilterOptions() throws Exception {
        var payload = new JSONObject();
        payload.put("filters", new ArrayList<>());

        var response = TaskUIRequests.getTaskHistoryFilterOptions(payload, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);

        var responseBody = new JSONObject(response.body().asString());
        var expectedKeysInResponse = List.of("user", "assignment", "reviewDecision");
        for (var expectedKey : expectedKeysInResponse) {
            var keyIsInResponse = responseBody.has(expectedKey);
            var propertyArrayIsNotEmpty = responseBody.getJSONArray(expectedKey).isEmpty();
            Assert.assertTrue(keyIsInResponse, "Response body does not have property " + expectedKey);
            Assert.assertFalse(propertyArrayIsNotEmpty, "Response body has empty array for key " + expectedKey);
        }
    }

    private void testTimestampFilterReturnsCorrectData() throws Exception {
        var startDate = LocalDate.now().minusWeeks(1);
        var endDate = LocalDate.now();
        var datePostfix = "T00:00:00.000Z";

        var filters = new JSONObject();
        filters.put("filterType", "timestamp");
        filters.put("operator", "between");
        filters.put("operand", startDate + datePostfix);
        filters.put("operand2", endDate + datePostfix);

        var payload = new JSONObject();
        payload.put("pageSize", 10);
        payload.put("page", 1);
        payload.put("filters", new ArrayList<>(List.of(filters)));

        var response = TaskUIRequests.getTaskHistoryGrid(payload, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);

        List<String> allDatesAccepted = DateUtility.getDatesInDateRange(startDate.toString(), endDate.toString());
        var allDatesInResponse = response.jsonPath().getList("data.dateCreated");
        for (var date : allDatesInResponse) {
            var dateString = date.toString();
            var dateCreated = dateString.substring(0, dateString.indexOf("T"));
            Assert.assertTrue(allDatesAccepted.contains(dateCreated), "Date " + dateCreated + " is not in the accepted date range\nAccepted dates: " + allDatesAccepted);
        }
    }

    private void testFilterReturnsCorrectData(JSONObject filterOptions, String filterType, String fieldToFind) throws Exception {
        var filterOptionArray = filterOptions.getJSONArray(filterType);
        String operand;
        String expectedName = null;

        if (filterType.equals("user")) {
            operand = filterOptionArray.getJSONObject(0).getString("key");
            expectedName = filterOptionArray.getJSONObject(0).getString("value");
        } else {
            operand = filterOptionArray.getString(0);
        }

        var filters = new JSONObject();
        filters.put("filterType", filterType);
        filters.put("operator", "in");
        filters.put("operand", List.of(operand));

        var payload = new JSONObject();
        payload.put("pageSize", 10);
        payload.put("page", 1);
        payload.put("filters", List.of(filters));

        var response = TaskUIRequests.getTaskHistoryGrid(payload, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);

        var valuesOfFilterTypeInResponse = response.jsonPath().getList("data." + fieldToFind);
        for (var value : valuesOfFilterTypeInResponse) {
            if (filterType.equals("user")) {
                Assert.assertEquals(value.toString(), expectedName, "Name " + value + " is not equal to expected name " + expectedName);
                continue;
            }
            Assert.assertEquals(value.toString(), operand, "Value " + value + " is not equal to expected value " + operand + " for filter type " + filterType);
        }
    }

    private void testFilterSearchIdsReturnsCorrectData(String operand, List<ProductMaster> products) throws Exception {
        if (operand.isBlank()) {
            throw new Exception("Cannot continue with test, operand is None");
        }

        var filters = new JSONObject();
        filters.put("filterType", "searchIds");
        filters.put("operator", "contains");
        filters.put("operand", List.of(operand));

        var payload = new JSONObject();
        payload.put("pageSize", 10);
        payload.put("page", 1);
        payload.put("filters", List.of(filters));

        var response = TaskUIRequests.getTaskHistoryGrid(payload, jwt);
        ApiValidations.checkResponseStatus(testMethodName.get(), response.statusCode(), 200);

        var responseData = response.jsonPath().getList("data");
        Assert.assertFalse(responseData.isEmpty(), "Cannot continue with test because response does not have data for operand: " + operand);

        List<String> taskTitles = response.jsonPath().getList("data.taskTitle");
        var instanceIds = response.jsonPath().getList("data.products.flatten()", String.class);

        List<String> uniqueIds = new ArrayList<>();
        for (var instanceId : instanceIds) {
            for (var product : products) {
                var liveVariantSets = product.variantSets.live;
                for (var liveVariantSet : liveVariantSets) {
                    if (liveVariantSet.instances.global.id.equals(instanceId)) {
                        uniqueIds.add(liveVariantSet.instances.global.uniqueId);
                        uniqueIds.add(product.uniqueId);
                        break;
                    }

                    var instanceFound = addUniqueIdsIfFound(product.uniqueId, instanceId, uniqueIds, liveVariantSet.instances.retailer);
                    if (instanceFound) break;
                    instanceFound = addUniqueIdsIfFound(product.uniqueId, instanceId, uniqueIds, liveVariantSet.instances.globalCampaign);
                    if (instanceFound) break;
                    instanceFound = addUniqueIdsIfFound(product.uniqueId, instanceId, uniqueIds, liveVariantSet.instances.retailerCampaign);
                    if (instanceFound) break;
                }
            }
        }

        var itemsWithCriteria = new ArrayList<>(taskTitles);
        itemsWithCriteria.addAll(uniqueIds);
        itemsWithCriteria.retainAll(List.of(operand));
        Assert.assertFalse(itemsWithCriteria.isEmpty(), "None of the items matched operand: " + operand + " for filter type searchIds");
    }

    private boolean addUniqueIdsIfFound(String productUniqueId, String instanceIdToFind, List<String> uniqueIds, List<? extends ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal> instances) {
        for (var instance : instances) {
            if (instance.id.equals(instanceIdToFind)) {
                uniqueIds.add(instance.uniqueId);
                uniqueIds.add(productUniqueId);
                return true;
            }
        }
        return false;
    }

    private String getUniqueIdOfInstance(List<ProductMaster> products, String instanceIdToFind) throws Exception {
        for (var product : products) {
            var liveVariantSets = product.variantSets.live;
            for (var liveVariantSet : liveVariantSets) {
                if (liveVariantSet.instances.global.id.equals(instanceIdToFind)) {
                    return liveVariantSet.instances.global.uniqueId;
                }
                for (var retailerInstance : liveVariantSet.instances.retailer) {
                    if (retailerInstance.id.equals(instanceIdToFind)) {
                        return retailerInstance.uniqueId;
                    }
                }
                for (var campaignInstance : liveVariantSet.instances.globalCampaign) {
                    if (campaignInstance.id.equals(instanceIdToFind)) {
                        return campaignInstance.uniqueId;
                    }
                }
                for (var retailerCampaignInstance : liveVariantSet.instances.retailerCampaign) {
                    if (retailerCampaignInstance.id.equals(instanceIdToFind)) {
                        return retailerCampaignInstance.uniqueId;
                    }
                }
            }
        }
        throw new Exception("Unable to find instance id: " + instanceIdToFind);
    }
}
