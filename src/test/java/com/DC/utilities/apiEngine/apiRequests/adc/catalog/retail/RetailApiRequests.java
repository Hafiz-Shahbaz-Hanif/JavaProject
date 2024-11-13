package com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.headers;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.*;
import com.DC.utilities.apiEngine.routes.adc.catalog.retail.RetailRoutes;
import com.DC.utilities.apiEngine.routes.hub.aggregation.AggregationServiceRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class RetailApiRequests {

    private static String header = "Content-Type=application/json";
	
    public static Response retailScratchpad(RetailScratchpadRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(RetailRoutes.getRetailScratchpadRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }
    
    public static Response segmentationsGrouped(String headers, String parameters, String jwt, int businessUnitId) throws Exception {
    	return CommonApiMethods.callEndpoint(RetailRoutes.getSegmentationGroupsForBusinessUnitRoutePath(businessUnitId), "GET", "", headers, parameters, jwt);
    }
    
    public static Response retailScratchpadExternalGateway(RetailScratchpadRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(RetailRoutes.getRetailScratchpadRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response segmentationsGroupedExternalGateway(String headers, String parameters, String jwt, int businessUnitId) throws Exception {
    	return CommonApiMethods.callEndpoint(RetailRoutes.getSegmentationGroupsForBusinessUnitRoutePath(businessUnitId), "GET", "", headers, parameters, jwt);
    }

    public static <T> Response getScratchpadData(T requestBody, String endpoint, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return getScratchpadData(reqBody, endpoint, jwt);
    }

    public static Response getScratchpadData(String requestBody,String endpoint, String jwt) throws Exception {

        String header = "Content-Type=application/json";

        return CommonApiMethods.callEndpoint(RetailRoutes.getScratchpadRoutePath(endpoint),
                "POST", requestBody, header, "", jwt);
    }

    public static <T> Response getRetailScratchpadData(T requestBody, String endpoint, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return getRetailScratchpadData(reqBody, endpoint, jwt);
    }

    public static Response getRetailScratchpadData(String requestBody,String endpoint, String jwt) throws Exception {

        String header = "Content-Type=application/json";

        return CommonApiMethods.callEndpoint(RetailRoutes.getRetailScratchpadRoutePath(endpoint),
                "POST", requestBody, header, "", jwt);
    }

    public static <T> Response getScratchpadAggbusData(T requestBody, String endpoint, String jwt, String... headers) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        String header = "Content-Type=application/json";
        if (headers != null && headers.length > 0) {
            header += ";" + String.join(";", headers);
        }
        return CommonApiMethods.callEndpoint(RetailRoutes.getScratchpadRoutePath(endpoint),
                "POST", reqBody, header, "", jwt);
    }

    public static Response getAsinSegmentationData(AsinSegmentationRequestbody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return getAsinSegmentationData(reqBody, RetailRoutes.ASIN_SEGMENTATION, jwt);
    }

    public static Response getAsinSegmentationPoGoldenData(AsinSegmentationRequestbody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return getAsinSegmentationData(reqBody, RetailRoutes.ASIN_SEGMENTATION_PO_GOLDEN_DATA, jwt);
    }

    public static Response getAsinSegmentationMasterData(AsinSegmentationRequestbody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return getAsinSegmentationData(reqBody, RetailRoutes.ASIN_SEGMENTATION_MASTER_DATA, jwt);
    }

    public static Response getCreateAsinData(CreateAsinRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return getAsinSegmentationData(reqBody, RetailRoutes.CREATE_ASIN, jwt);
    }

    public static Response getAsinSegmentationData(String requestBody,String endpoint, String jwt) throws Exception {

        String header = "Content-Type=application/json";

        return CommonApiMethods.callEndpoint(RetailRoutes.getAsinSegmentationRoutePath(endpoint),
                "POST", requestBody, header, "", jwt);
    }

    public static <T> Response getDeleteAsinData(T requestBody, String endpoint, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return getAsinSegmentationDeleteData(reqBody, endpoint, jwt);
    }

    public static Response getAsinSegmentationDeleteData(String requestBody,String endpoint, String jwt) throws Exception {

        String header = "Content-Type=application/json";

        return CommonApiMethods.callEndpoint(RetailRoutes.getAsinSegmentationRoutePath(endpoint),
                "PUT", requestBody, header, "", jwt);
    }

    public static Response asinScratchpad(AsinScratchpadRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(RetailRoutes.getRetailScratchpadRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static <T> Response getSCMData(T requestBody,String endpoint, String jwt, String... headers) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        String header = "Content-Type=application/json";
        if (headers != null && headers.length > 0) {
            header += ";" + String.join(";", headers);
        }
        return CommonApiMethods.callEndpoint(RetailRoutes.getSalesCorrelationRoutePath(endpoint),
                "POST", reqBody, header, "", jwt);
    }

    public static Response getGainerAndDrainerData(GainersAndDrainersRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(RetailRoutes.getGainerAndDrainersRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static Response getGainerAndDrainerIntervalData(GainersAndDrainersRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(RetailRoutes.getGainerAndDrainersIntervalRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static Response roundupAll(RoundupRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(RetailRoutes.getRetailRoundupAllRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static JSONObject roundupAllJson(RoundupRequestBody requestBody, String jwt) throws Exception {
        Response response = roundupAll(requestBody, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Round up all call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response roundupProduct(RoundupRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(RetailRoutes.getRetailRoundupProductRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static JSONObject roundupProductJson(RoundupRequestBody requestBody, String jwt) throws Exception {
        Response response = roundupProduct(requestBody, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Round up product call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response roundupSegmentation(RoundupRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(RetailRoutes.getRetailRoundupSegmentationRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static <T> Response getRoundUpAggbusData(T requestBody, String endpoint, String jwt, String... multipleHeaders) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(RetailRoutes.getRetailRoundupAggBusRoutePath(endpoint),
                "POST", reqBody, headers.multipleHeaders(multipleHeaders), "", jwt);
    }

    public static JSONObject roundupSegmentationJson(RoundupRequestBody requestBody, String jwt) throws Exception {
        Response response = roundupSegmentation(requestBody, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Round up segmentation call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response roundupExportByAsinByDate(RoundupRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return CommonApiMethods.callEndpoint(RetailRoutes.getRetailRoundupExportRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static JSONObject roundupExportByAsinByDateJson(RoundupRequestBody requestBody, String jwt) throws Exception {
        Response response = roundupExportByAsinByDate(requestBody, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Round up export by asin by date call not successful.");
        return new JSONObject(response.asString());
    }

    public static Response roundupMultiBuExportByAsinByDate(RoundupRequestBody requestBody, int bu1, int bu2, String currency, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        header = header +";x-businessunitcontext=" + bu1 + "," + bu2 + ";x-currencycontext=" + currency;
        return CommonApiMethods.callEndpoint(RetailRoutes.getRetailRoundupMultiBuExportRoutePath(), "POST", reqBody, header, "", jwt);
    }

    public static JSONObject roundupMultiBuExportByAsinByDateJson(RoundupRequestBody requestBody, int bu1, int bu2, String currency, String jwt) throws Exception {
        Response response = roundupMultiBuExportByAsinByDate(requestBody, bu1, bu2, currency, jwt);
        Assert.assertEquals(response.statusCode(), 200, "** Round up export by asin by date call not successful.");
        return new JSONObject(response.asString());
    }

    public static List<JSONObject> getHourlyObjectsByDay (JSONArray hourlyObjects, int day){
        List<JSONObject> hourlyObjectList = new ArrayList<>();
        for (int i = 0; i < hourlyObjects.length(); i++) {
            if (hourlyObjects.getJSONObject(i).getInt("day") == day) {
                hourlyObjectList.add(hourlyObjects.getJSONObject(i));
            }
        }
        return hourlyObjectList;
    }

    public static List<JSONObject> getAllRealTimeSalesObjects(JSONArray dailyObjects){
        List<JSONObject> dailyObjectList = new ArrayList<>();
        for (int i = 0; i < dailyObjects.length(); i++) {
            if (dailyObjects.getJSONObject(i).getBoolean("isRealTimeSalesData")) {
                dailyObjectList.add(dailyObjects.getJSONObject(i));
            }
        }
        return dailyObjectList;
    }

    public static List<JSONObject> getAllNonRealTimeSalesObjects(JSONArray dailyObjects){
        List<JSONObject> hourlyObjectList = new ArrayList<>();
        boolean flag = false;
        int unitResponse = 0;
        for (int i = 0; i < dailyObjects.length(); i++) {
            if(!flag){
                try {
                    unitResponse = dailyObjects.getJSONObject(i).getInt("current");
                } catch (Exception e) {

                }
            }
            if (unitResponse > 0){
                flag = true;
            }
            if(flag && !dailyObjects.getJSONObject(i).getBoolean("isRealTimeSalesData")){
                hourlyObjectList.add(dailyObjects.getJSONObject(i));
            }
        }
        return hourlyObjectList;
    }
}
