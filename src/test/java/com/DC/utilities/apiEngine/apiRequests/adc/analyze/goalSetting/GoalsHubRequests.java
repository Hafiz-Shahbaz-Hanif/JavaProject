package com.DC.utilities.apiEngine.apiRequests.adc.analyze.goalSetting;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.models.requests.adc.analyze.goalSetting.AllGoalsHubRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.analyze.goalSetting.GoalsHubRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.RoundupRequestBody;
import com.DC.utilities.apiEngine.routes.adc.analyze.goalSetting.GoalsHubRoutes;
import com.DC.utilities.apiEngine.routes.adc.catalog.retail.RetailRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class GoalsHubRequests {

    private static String headers = "Content-Type=application/json";

    public static Map<String, String> metrics = new HashMap<>() {{
        put("shipped_revenue", "SHIPPED_REVENUE");
        put("ordered_units", "ORDERED_UNITS");
        put("shipped_units", "SHIPPED_UNITS");
        put("shipped_cogs", "SHIPPED_COGS");
        put("availability", "AVAILABILITY_PERCENTAGE_IN_STOCK");
        put("ordered_revenue", "ORDERED_REVENUE");
    }};

    public static Map<String, String> periods = new HashMap<>() {{
        put("future", "FUTURE");
        put("past", "PAST");
        put("present", "PRESENT");
        put("all", "ALL");
    }};

    public static Map<String, String> intervals = new HashMap<>() {{
        put("custom", "CUSTOM");
        put("monthly", "MONTHLY");
        put("yearly", "YEARLY");
        put("quarterly", "QUARTERLY");
    }};

    public static Map<String, String> metricTypes = new HashMap<>() {{
        put("retail", "RETAIL");
        put("pdp", "PDP");
    }};

    public static Map<String, String> distributorViews = new HashMap<>() {{
        put("sourcing", "SOURCING");
        put("manufacturing", "MANUFACTURING");
    }};

    public static Map<String, String> segmentationTypes = new HashMap<>() {{
        put("brand", "BRAND");
        put("category", "CATEGORY");
        put("segment", "SEGMENT");
        put("subcategory", "SUBCATEGORY");
    }};

    public static Response goalsHub(GoalsHubRequestBody requestBody, String header, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(GoalsHubRoutes.getGoalsHubRoutePath(), "POST", reqBody, headers + ";" + header, "", jwt);
    }

    public static Response getBrands(String header, String jwt) throws Exception {
        return CommonApiMethods.callEndpoint(GoalsHubRoutes.getGoalBrandsRoutePath(), "GET", "", headers + ";" + header, "", jwt);
    }

    public static String getBrandId(String brand, String header, String jwt) throws Exception {
        String brandId = null;
        Response response = getBrands(header, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Get brands call not successful.");

        JSONArray brands = new JSONArray(response.asString());
        for (int i = 0; i < brands.length(); i++) {
            JSONObject brandObj = brands.getJSONObject(i);
            if (brandObj.getString("label").equalsIgnoreCase("Brand")) {
                JSONArray values = brandObj.getJSONArray("values");
                for (int j = 0; j < values.length(); j++) {
                    JSONObject valueObj = values.getJSONObject(j);
                    if (valueObj.getString("value").equalsIgnoreCase(brand)) {
                        brandId = valueObj.getString("valueId");
                        break;
                    }
                }
            }
        }
        return brandId;
    }

    public static Response getSegmentation(String header, String jwt) throws Exception {
        return CommonApiMethods.callEndpoint(GoalsHubRoutes.getGoalSegmentationRoutePath(), "GET", "", headers + ";" + header, "", jwt);
    }

    public static String getSegmentId(String segmentationType, String value, String header, String jwt) throws Exception {
        String segmentId = null;
        Response response = getSegmentation(header, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Get segmentation call not successful.");

        JSONArray segmentations = new JSONArray(response.asString());
        for (int i = 0; i < segmentations.length(); i++) {
            JSONObject label = segmentations.getJSONObject(i);
            if (label.getString("label").equalsIgnoreCase(segmentationType)) {
                JSONArray values = label.getJSONArray("values");
                for (int j = 0; j < values.length(); j++) {
                    JSONObject valueObj = values.getJSONObject(j);
                    if (valueObj.getString("value").equalsIgnoreCase(value)) {
                        segmentId = valueObj.getString("valueId");
                        break;
                    }
                }
            }
        }
        return segmentId;
    }

    public static String goalsHubString(GoalsHubRequestBody requestBody, String header, String jwt) throws Exception {
        Response response = goalsHub(requestBody, header, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Goals hub call not successful.");
        String responseMsg =  response.asString();
        return responseMsg.substring(1, responseMsg.length() - 1);
    }

    public static Response goalsBulkCreate(File file, String header, String jwt) throws Exception {
        return CommonApiMethods.callEndpointToUploadFile(file, GoalsHubRoutes.goalBulkCreateRoutePath(), "POST", "", header, "", jwt);
    }

    public static JSONObject goalsBulkCreateJson(File file, String header, String jwt) throws Exception {
        Response response = goalsBulkCreate(file, header, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Create bulk goals call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response goalsBulkEdit(File file, String header, String jwt) throws Exception {
        return CommonApiMethods.callEndpointToUploadFile(file, GoalsHubRoutes.goalBulkCreateRoutePath(), "PUT", "", header, "", jwt);
    }

    public static JSONObject goalsBulkEditJson(File file, String header, String jwt) throws Exception {
        Response response = goalsBulkEdit(file, header, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Update bulk goals call not successful.");
        return new JSONObject(response.asString());
    }

    public static void deleteGoal(String goalId, String header, String jwt) throws Exception {
        Response response = CommonApiMethods.callEndpoint(GoalsHubRoutes.getGoalsHubDeleteRoutePath(goalId), "DELETE", "", headers + ";" + header, "", jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Goals hub delete call not successful.");
    }

    public static void updateGoal(GoalsHubRequestBody requestBody, String header, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        Response response = CommonApiMethods.callEndpoint(GoalsHubRoutes.getGoalsHubUpdateRoutePath(), "PUT", reqBody, headers + ";" + header, "", jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Goals hub update call not successful.");
    }

    public static Response allGoalsHub(AllGoalsHubRequestBody requestBody, String header, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(GoalsHubRoutes.getAllGoalsHubRoutePath(), "POST", reqBody, headers + ";" + header, "", jwt);
    }

    public static JSONObject allGoalsHubJson(AllGoalsHubRequestBody requestBody, String header, String jwt) throws Exception {
        Response response = allGoalsHub(requestBody, header, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Get all goals call not successful.");
        return new JSONObject(response.asString());
    }

    public static JSONObject getRandomPdpGoal(AllGoalsHubRequestBody requestBody, String header, String jwt) throws Exception {
        List<JSONObject> goalsList = new ArrayList<>();
        JSONObject response = allGoalsHubJson(requestBody, header, jwt);
        JSONArray goals = response.getJSONArray("PDP");

        for (int i=0; i < goals.length(); i++){
            JSONObject goal = goals.getJSONObject(i);
            if (!Objects.equals(goal.get("goalReached"), null)) {
                Double goalReached =  goal.getDouble("goalReached");
                if (goalReached != 0.0){
                    JSONObject specification = goal.getJSONObject("specification");
                    if (specification.getBigDecimal("maxValue").compareTo(new BigDecimal(5000)) <= 0) {
                        goalsList.add(goals.getJSONObject(i));
                    }
                }
            }
        }
        Assert.assertTrue(goalsList.size() > 0, "Not necessarily a failure. No goals found.");
        return (JSONObject) SharedMethods.getRandomItemFromList(goalsList);
    }

    public static JSONObject getRandomSalesGoal(AllGoalsHubRequestBody requestBody, String header, String jwt) throws Exception {
        List<JSONObject> goalsList = new ArrayList<>();
        JSONObject response = allGoalsHubJson(requestBody, header, jwt);
        JSONArray goals = response.getJSONArray("RETAIL");

        for (int i=0; i < goals.length(); i++){
            JSONObject goal = goals.getJSONObject(i);
            if (!Objects.equals(goal.get("goalReached"), null)) {
                Double goalReached =  goal.getDouble("goalReached");
                if (goalReached != 0.0){
                    goalsList.add(goals.getJSONObject(i));
                }
            }
        }
        Assert.assertTrue(goalsList.size() > 0, "Not necessarily a failure. No goals found.");
        return (JSONObject) SharedMethods.getRandomItemFromList(goalsList);
    }

    public static JSONObject getRandomGoal(AllGoalsHubRequestBody requestBody, String metricType, String header, String jwt) throws Exception {
        List<JSONObject> goalsList = new ArrayList<>();
        JSONObject response = allGoalsHubJson(requestBody, header, jwt);
        JSONArray goals = response.getJSONArray(metricType);
        for (int i=0; i < goals.length(); i++){
            goalsList.add(goals.getJSONObject(i));
        }
        return (JSONObject) SharedMethods.getRandomItemFromList(goalsList);
    }

    public static Response getGoal(String goalId, String header, String jwt) throws Exception {
        return CommonApiMethods.callEndpoint(GoalsHubRoutes.getGoalRoutePath(goalId), "GET", "", headers + ";" + header, "", jwt);
    }

    public static JSONObject getGoalJson(String goalId, String header, String jwt) throws Exception {
        Response response = getGoal(goalId, header, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Goals hub get goal call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response getGoalMetric(String header, String jwt) throws Exception {
        return CommonApiMethods.callEndpoint(GoalsHubRoutes.getGoalMetricRoutePath(), "GET", "", headers + ";" + header, "", jwt);
    }

    public static List<JSONObject> getGoalMetricJson(String header, String jwt) throws Exception {
        List<JSONObject> arrList = new ArrayList<>();
        Response response = getGoalMetric(header, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Goals hub get goal metric call not successful.");
        JSONArray arr = new JSONArray(response.asString());
        for (int i=0; i < arr.length(); i++){
            arrList.add(arr.getJSONObject(i));
        }
        return arrList;
    }

    public static void deleteGoals(String metricType, JSONObject goals, String headers, String authToken) throws Exception {
        JSONArray saleGoals = null;
        boolean hasGoals = goals.has(metricType.toUpperCase());
        if (hasGoals){
            saleGoals = goals.getJSONArray(metricType.toUpperCase());
            for (int i = 0; i < saleGoals.length(); i++){
                JSONObject goal = saleGoals.getJSONObject(i);
                String goalMetricID = goal.getString("goalMetricID");
                GoalsHubRequests.deleteGoal(goalMetricID, headers, authToken);
            }
        }
    }

    public static Response goalsExport(AllGoalsHubRequestBody requestBody, String header, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(GoalsHubRoutes.goalsExportRoutePath(), "POST", reqBody, headers + ";" + header, "", jwt);
    }

    public static String goalsExportString(AllGoalsHubRequestBody requestBody, String header, String jwt) throws Exception {
        Response response = goalsExport(requestBody, header, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Goals export call not successful.");
        return response.asString();
    }

}