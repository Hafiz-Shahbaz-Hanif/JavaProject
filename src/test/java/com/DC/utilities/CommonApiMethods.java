package com.DC.utilities;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.DC.objects.RestAPI;
import com.DC.testcases.BaseClass;
import com.DC.utilities.XLUtils.XLUtils;
import org.apache.log4j.Logger;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CommonApiMethods {

    private static final Logger logger = Logger.getLogger(CommonApiMethods.class);

    public static Response callEndpoint(String reqURI, String jwt, String httpMethod, String reqBody, String reqParams) throws Exception {
        Map<String, String> headerMap = new HashMap<>();

        headerMap.put("cache-control", "no-cache");
        headerMap.put("Content-Type", "application/json");
        headerMap.put("X-Token", jwt);

        // Add request body
        logger.info(reqBody);
        return RestAssured.given()
                .headers(headerMap)
                .params(paramBuilder(reqParams))
                .body(reqBody)
                .request(httpMethod.toUpperCase(), reqURI);
    }

    public static Response callEndpoint(RestAPI restAPI) throws Exception {
        RestAssured.baseURI = restAPI.requestURI;

        // Request object
        Map<String, String> headerMap = new HashMap<>();

        headerMap.put("cache-control", "no-cache");
        headerMap.put("Content-Type", "application/json");
        headerMap.put("X-Token", restAPI.jwt);

        Map<String, String> paramMap = paramBuilder(restAPI.requestParams);
        return RestAssured.given()
                .headers(headerMap)
                .params(paramMap)
                .queryParams(restAPI.queryParams != null ? restAPI.queryParams : new HashMap<>())
                .body(restAPI.requestBody != null ? restAPI.requestBody : "")
                .request(restAPI.method.toString(), restAPI.requestURI);
    }

    public static Response callEndpoint(String uri, String method, String body, Map<String, String> headers, String parameters) throws Exception {
        return RestAssured.given()
                .headers(headers)
                .params(paramBuilder(parameters))
                .body(body)
                .request(method, uri);
    }

    public static Response callEndpoint(String uri, String method, String body, String headers, String parameters, String jwt) throws Exception {
        return RestAssured.given().headers(headerBuilder(headers, jwt)).params(paramBuilder(parameters)).body(body).request(method, uri);
    }
    
    public static Response callEndpointToUploadFile(File file, String uri, String method, String body, String headers, String parameters, String jwt) throws Exception {
		return RestAssured.given().multiPart("file", file, "multipart/form-data").headers(headerBuilder(headers, jwt)).params(paramBuilder(parameters)).body(body).request(method, uri);
	}

    public static Map<String, String> paramBuilder(String reqParam) {
        Map<String, String> paramMap = new HashMap<>();
        if (reqParam != null && !reqParam.isEmpty()) {
            paramMap = new HashMap<>();
            //building string array based on key value pairs separated by ';'
            String[] keyValuePairs = reqParam.split(";");
            for (int i = 0; i < keyValuePairs.length; i++) {
                //building string array based on key value separated by '=' and adding to hashmap
                String[] keyValue = keyValuePairs[i].split("=");
                paramMap.put(keyValue[0], keyValue[1]);
            }
        }

        return paramMap;
    }

    public static Map<String, Object> headerBuilder(String headers, String jwt) throws Exception {
        Map<String, Object> headerMap = new HashMap<>();
        if (jwt.startsWith("Bearer") || jwt.startsWith("Basic")) {
            headerMap.put("Authorization", jwt);
        } else if (jwt.startsWith("X-API-KEY")) {
            String[] keyValue = jwt.split(" ");
            headerMap.put(keyValue[0], keyValue[1]);
        } else {
            headerMap.put("X-Token", jwt);
        }
        if (headers != null && headers != "") {
            String[] keyValuePairs = headers.split(";");
            for (int i = 0; i < keyValuePairs.length; i++) {
                String[] keyValue = keyValuePairs[i].split("=");
                if (keyValue[0].equalsIgnoreCase("X-RetailerPlatformContext")) {
                    headerMap.put(keyValue[0], keyValue[1].split(","));
                } else {
                    headerMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return headerMap;
    }

    public static String[][] getTestData(String Classname, String Methodname) throws IOException {
        String testData[][] = null;

        try {
            int rownum = XLUtils.getRowCount(BaseClass.MasterSpreadSheet, "Global");
            int colcount = XLUtils.getCellCount(BaseClass.MasterSpreadSheet, "Global", 1);

            boolean executeTestScenario = false;
            String tabName = "";
            for (int i = 1; i <= rownum; i++) {
                if (XLUtils.getCellData(BaseClass.MasterSpreadSheet, "Global", i, 0).equalsIgnoreCase("y")
                        && XLUtils.getCellData(BaseClass.MasterSpreadSheet, "Global", i, 1).equalsIgnoreCase(Classname)
                        && XLUtils.getCellData(BaseClass.MasterSpreadSheet, "Global", i, 4)
                        .equalsIgnoreCase(Methodname)) {
                    executeTestScenario = true;
                    tabName = XLUtils.getCellData(BaseClass.MasterSpreadSheet, "Global", i, 2);
                    break;
                }
            }

            if (executeTestScenario) {
                rownum = XLUtils.getRowCount(BaseClass.MasterSpreadSheet, tabName);

                // Find the count of executable test cases
                int execTestCasesCount = 0;
                int cellCount = 0;
                for (int i = 1; i <= rownum; i++) {
                    if (XLUtils.getCellData(BaseClass.MasterSpreadSheet, tabName, i, 0).equalsIgnoreCase("y") && XLUtils
                            .getCellData(BaseClass.MasterSpreadSheet, tabName, i, 1).equalsIgnoreCase(Methodname)) {
                        colcount = XLUtils.getCellCount(BaseClass.MasterSpreadSheet, tabName, i);

                        if (!Methodname.startsWith("Api")) {

                            if (execTestCasesCount < 1) {
                                int numOfCellsWithData = 0;

                                for (int j = 0; j < colcount; j++) {
                                    String cellData = XLUtils.getCellData(BaseClass.MasterSpreadSheet, tabName, i, j);
                                    if (!cellData.isEmpty()) {
                                        numOfCellsWithData++;
                                    }
                                }

                                cellCount = numOfCellsWithData;
                            }
                        } else {
                            cellCount = colcount;
                        }

                        execTestCasesCount++;
                    }
                }

                testData = new String[execTestCasesCount][cellCount - 2]; // reason doing -2 is because ignoring test execute column and test case name array is index based
                int cellCountWithData = 0;
                int rowCount = 0;
                for (int i = 1; i <= rownum; i++) {
                    // Check if the row needs to be executed or not
                    if (XLUtils.getCellData(BaseClass.MasterSpreadSheet, tabName, i, 0).equalsIgnoreCase("y") && XLUtils
                            .getCellData(BaseClass.MasterSpreadSheet, tabName, i, 1).equalsIgnoreCase(Methodname)) {
                        for (int j = 2; j < colcount; j++) {

                            String cellData = XLUtils.getCellData(BaseClass.MasterSpreadSheet, tabName, i, j);

                            if (!Methodname.startsWith("Api")) {
                                if (!cellData.isEmpty()) {
                                    testData[rowCount][cellCountWithData] = cellData;
                                    cellCountWithData++;
                                }
                            } else {
                                testData[rowCount][j - 2] = cellData;
                            }

                        }
                        rowCount++;
                        cellCountWithData = 0;
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return testData;
    }

}
