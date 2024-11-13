package com.DC.apitests.productversioning.products;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.testcases.BaseClass;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.OpenSearchApiRequests;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.SharedRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.responses.productVersioning.ProductMaster;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.DC.apitests.ApiValidations.checkResponseStatus;
import static com.DC.utilities.SecurityAPI.changeInsightsCompanyAndGetJwt;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;

public class OpenSearchApiTests extends BaseClass {
    protected static String jwt;
    protected static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    @BeforeClass(alwaysRun = true)
    public void setupTests() throws Exception {
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        jwt = changeInsightsCompanyAndGetJwt(jwt, TEST_CONFIG.companyID, TEST_CONFIG.companyName);
    }

    @Test(groups = {"OpenSearchApiTests", "NoDataProvider"}, description = "Sort order is correct for sortable columns")
    public void Api_OpenSearch_SortOrderIsCorrectForSortableColumn() throws Exception {
        var propertyToSort = "test_prop_1";

        List<String> valuesOfProperty = getValuesOfPropertyFromOpenSearch(propertyToSort);

        var pageSize = 25;
        var pages = (int) Math.ceil((double) valuesOfProperty.size() / pageSize);

        testOpenSearchSortingByPropertyValues(pageSize, pages, valuesOfProperty, propertyToSort, null);
        testOpenSearchSortingByPropertyValues(pageSize, pages, valuesOfProperty, propertyToSort, "desc");
        testOpenSearchSortingByPropertyValues(pageSize, pages, valuesOfProperty, propertyToSort, "asc");
    }

    @Test(groups = {"OpenSearchApiTests", "NoDataProvider"}, description = "If payload is empty, the default sort order is by product identifier ascending")
    public void Api_OpenSearch_DefaultSortOrderIsCorrect() throws Exception {
        var productsInDatabase = ProductVersioningApiService.getAllProductMastersFromCompany(jwt)
                .stream()
                .filter(prod -> !prod.variantSets.live.isEmpty())
                .collect(Collectors.toList());

        List<String> instancesUniqueIdsInDB = new ArrayList<>();

        productsInDatabase.forEach(prod -> prod.variantSets.live.forEach(variantSet -> {
            instancesUniqueIdsInDB.add(variantSet.instances.global.uniqueId);
            instancesUniqueIdsInDB.addAll(variantSet.instances.globalCampaign.stream().map(instance -> instance.uniqueId).collect(Collectors.toList()));
            instancesUniqueIdsInDB.addAll(variantSet.instances.retailer.stream().map(instance -> instance.uniqueId).collect(Collectors.toList()));
            instancesUniqueIdsInDB.addAll(variantSet.instances.retailerCampaign.stream().map(instance -> instance.uniqueId).collect(Collectors.toList()));
        }));

        var pageSize = 25;
        var pages = (int) Math.ceil((double) instancesUniqueIdsInDB.size() / pageSize);

        testOpenSearchDefaultSorting(pageSize, pages, instancesUniqueIdsInDB, null);
        testOpenSearchDefaultSorting(pageSize, pages, instancesUniqueIdsInDB, "desc");
        testOpenSearchDefaultSorting(pageSize, pages, instancesUniqueIdsInDB, "asc");
    }

    @Test(groups = {"OpenSearchApiTests", "NoDataProvider"}, description = "Info about total row count and total unique product masters match with database")
    public void Api_OpenSearch_TotalRowCountAndTotalUniqueProductMastersAreCorrect() throws Exception {
        var productsInDatabase = ProductVersioningApiService.getAllProductMastersFromCompany(jwt)
                .stream()
                .filter(prod -> !prod.variantSets.live.isEmpty())
                .collect(Collectors.toList());

        List<String> instancesUniqueIdsInDB = new ArrayList<>();

        productsInDatabase.forEach(prod -> prod.variantSets.live.forEach(variantSet -> {
            instancesUniqueIdsInDB.add(variantSet.instances.global.uniqueId);
            instancesUniqueIdsInDB.addAll(variantSet.instances.globalCampaign.stream().map(instance -> instance.uniqueId).collect(Collectors.toList()));
            instancesUniqueIdsInDB.addAll(variantSet.instances.retailer.stream().map(instance -> instance.uniqueId).collect(Collectors.toList()));
            instancesUniqueIdsInDB.addAll(variantSet.instances.retailerCampaign.stream().map(instance -> instance.uniqueId).collect(Collectors.toList()));
        }));

        var requestBody = new JSONObject();
        requestBody.put("pageSize", 25);
        requestBody.put("filters", new ArrayList<>());
        requestBody.put("page", 1);

        var response = OpenSearchApiRequests.search(requestBody, jwt);
        checkResponseStatus(testMethodName.get(), "200", response.getStatusCode());

        var responseJpath = response.jsonPath();
        var totalRowCount = responseJpath.getInt("totalRowCount.value");
        var totalUniqueProductMasters = responseJpath.getInt("totalUniqueProductMasters");
        Assert.assertEquals(totalRowCount, instancesUniqueIdsInDB.size(), "Total row count returned is not the same as the total instances in database");
        Assert.assertEquals(totalUniqueProductMasters, productsInDatabase.size(), "Total unique product masters returned is not the same as the total product masters in database");
    }

    @Test(groups = {"OpenSearchApiTests", "NoDataProvider"})
    public void Api_OpenSearch_CanSearchByImportId() throws Exception {
        var requestBody = new JSONObject();
        requestBody.put("pageSize", 15);
        requestBody.put("page", 1);
        requestBody.put("search", "ImportProductKeywords_Empty.xlsx");

        var response = SharedRequests.getProductImportsForCompany(requestBody, jwt);
        checkResponseStatus(testMethodName.get(), "200", response.getStatusCode());

        var importToTest = response.jsonPath();
        var expectedInstancesIds = importToTest.getList("data[0].resultIds");

        var filters = new JSONObject();
        filters.put("filterType", "importId");
        filters.put("operand", importToTest.getString("data[0]._id"));

        requestBody = new JSONObject();
        requestBody.put("filters", List.of(filters));
        requestBody.put("pageSize", 5);
        requestBody.put("page", 1);

        response = OpenSearchApiRequests.search(requestBody, jwt);
        checkResponseStatus(testMethodName.get(), "200", response.getStatusCode());
        var returnedInstances = response.jsonPath().getList("data._id");
        Assert.assertEqualsNoOrder(returnedInstances, expectedInstancesIds, "Returned instances does not match with expected instances");
    }

    private void testOpenSearchSortingByPropertyValues(int pageSize, int pages, List<String> valuesOfProperty, String sortPropertyId, String sortDirection) throws Exception {
        for (int i = 0; i < pages; i++) {
            var requestBody = new JSONObject();
            requestBody.put("pageSize", pageSize);
            requestBody.put("filters", new ArrayList<>());
            requestBody.put("page", i + 1);
            requestBody.put("sortDirection", sortDirection);
            requestBody.put("sortProperty", sortPropertyId);

            var response = OpenSearchApiRequests.search(requestBody, jwt);
            checkResponseStatus(testMethodName.get(), "200", response.getStatusCode());

            List<List<Map<String, Object>>> propertiesReturned = response.jsonPath().getList("data.propertiesInherited");
            List<String> returnedValuesInProperty = new ArrayList<>();
            for (var property : propertiesReturned) {
                for (var prop : property) {
                    if (prop.get("id").equals(sortPropertyId)) {
                        var singleValue = ((List<String>) prop.get("values")).get(0);
                        returnedValuesInProperty.add(singleValue);
                    }
                }
            }

            var toIndex = (i + 1) * pageSize > valuesOfProperty.size() ? valuesOfProperty.size() : (i + 1) * pageSize;
            List<String> expectedSortedValues = null;

            if (sortDirection == null || sortDirection.equals("asc")) {
                expectedSortedValues = valuesOfProperty.stream().sorted(comparator()).collect(Collectors.toList()).subList(i * pageSize, toIndex);
            } else {
                expectedSortedValues = valuesOfProperty.stream().sorted(comparator().reversed()).collect(Collectors.toList()).subList(i * pageSize, toIndex);
            }

            Assert.assertEquals(returnedValuesInProperty, expectedSortedValues, "Returned values are not sorted correctly for property " + sortPropertyId);
        }
    }

    private void testOpenSearchDefaultSorting(int pageSize, int pages, List<String> instancesUniqueIdsInDB, String sortDirection) throws Exception {
        for (int i = 0; i < pages; i++) {
            var requestBody = new JSONObject();
            requestBody.put("pageSize", pageSize);
            requestBody.put("filters", new ArrayList<>());
            requestBody.put("page", i + 1);
            requestBody.put("sortDirection", sortDirection);

            var response = OpenSearchApiRequests.search(requestBody, jwt);
            checkResponseStatus(testMethodName.get(), "200", response.getStatusCode());

            List<String> instanceIdsReturned = new ArrayList<>();
            var dataArray = new JSONObject(response.asString()).getJSONArray("data");

            dataArray.forEach(
                    data -> {
                        ((JSONObject) data).getJSONArray("properties").forEach(
                                property -> {
                                    if (((JSONObject) property).getString("id").equals("unique_id")) {
                                        instanceIdsReturned.add(((JSONObject) property).getJSONArray("values").getString(0));
                                    }
                                }
                        );
                    }
            );

            var toIndex = (i + 1) * pageSize > instancesUniqueIdsInDB.size() ? instancesUniqueIdsInDB.size() : (i + 1) * pageSize;

            List<String> expectedInstanceIds = null;
            Map<String, String> sortedProductIdsAndNames = new LinkedHashMap<>();

            if (sortDirection == null || sortDirection.equals("asc")) {
                expectedInstanceIds = instancesUniqueIdsInDB.stream().sorted().collect(Collectors.toList()).subList(i * pageSize, toIndex);
            } else {
                expectedInstanceIds = instancesUniqueIdsInDB.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()).subList(i * pageSize, toIndex);
            }

            Assert.assertEquals(instanceIdsReturned, expectedInstanceIds,
                    "Products returned from OpenSearch are not sorted in correct order" +
                            "\nExpected:\n" + expectedInstanceIds +
                            "\nActual:\n" + instanceIdsReturned
            );
        }
    }

    private Comparator<String> comparator() {
        Pattern numberPattern = Pattern.compile("^\\d.*");
        Pattern specialCharPattern = Pattern.compile("^[^\\d!].*");
        Pattern exclamationPattern = Pattern.compile("^!.*");
        Comparator<String> valueComparator = Comparator.comparing(entry -> {
            String value = entry.toLowerCase();
            if (numberPattern.matcher(value).matches()) {
                return "0" + value; // Prefix values starting with numbers with "0" to sort them first
            } else if (specialCharPattern.matcher(value).matches()) {
                return "1" + value; // Prefix values starting with special characters with "1" to sort them before "!"
            } else if (exclamationPattern.matcher(value).matches()) {
                return "2" + value; // Prefix values starting with "!" with "2" to sort them last
            }
            return "3" + value; // Sort other values in between
        });
        return valueComparator;
    }

    private static List<String> getValuesOfPropertyFromOpenSearch(String propertyToSort) throws Exception {
        var productsInDatabase = ProductVersioningApiService.getAllProductMastersFromCompany(jwt)
                .stream()
                .filter(prod -> !prod.variantSets.live.isEmpty())
                .collect(Collectors.toList());

        int allProductsCount = 0;

        for (var product : productsInDatabase) {
            for (var variantSet : product.variantSets.live) {
                List<ProductMaster.VariantSets.Live.ProductVariantInstances.ProductInstanceGlobal> instances = new ArrayList<>();
                instances.add(variantSet.instances.global);
                instances.addAll(variantSet.instances.retailer);
                instances.addAll(variantSet.instances.globalCampaign);
                instances.addAll(variantSet.instances.retailerCampaign);

                allProductsCount += instances.size();
            }
        }

        var requestBody = new JSONObject();
        requestBody.put("pageSize", allProductsCount);
        requestBody.put("filters", new ArrayList<>());
        requestBody.put("page", 1);

        var response = OpenSearchApiRequests.search(requestBody, jwt);

        List<List<Map<String, Object>>> propertiesInherited = response.jsonPath().getList("data.propertiesInherited");
        List<String> valuesOfProperty = new ArrayList<>();
        for (var property : propertiesInherited) {
            for (var prop : property) {
                if (prop.get("id").equals(propertyToSort)) {
                    var singleValue = ((List<String>) prop.get("values")).get(0);
                    valuesOfProperty.add(singleValue);
                }
            }
        }
        return valuesOfProperty;
    }
}
