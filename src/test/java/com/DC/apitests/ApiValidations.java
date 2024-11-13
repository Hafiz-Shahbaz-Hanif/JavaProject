package com.DC.apitests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.util.*;

public class ApiValidations {

   private static Logger logger = Logger.getLogger(ApiValidations.class);

    public static void validateUnauthorizedError(Response response) {
        JsonPath jsonPathEvaluator = response.getBody().jsonPath();
        int statusCode = Integer.parseInt(jsonPathEvaluator.getString("statusCode"));
        String error = jsonPathEvaluator.getString("error");
        String message = jsonPathEvaluator.getString("message");
        Assert.assertEquals(statusCode, 401);
        Assert.assertEquals(error, "Unauthorized");
        Assert.assertEquals(message, "Request JWT token does not have the required roles");
    }

    public static void validateInternalServerError(Response response) {
        JsonPath jsonPathEvaluator = response.getBody().jsonPath();
        int statusCode = Integer.parseInt(jsonPathEvaluator.getString("statusCode"));
        String message = jsonPathEvaluator.getString("message");
        Assert.assertEquals(statusCode, 500);
        Assert.assertEquals(message, "An internal server error occurred");
    }

    public static void validateMissingRequestParametersError(Response response, String missingParameter) {
        JsonPath jsonPathEvaluator = response.getBody().jsonPath();
        String success = jsonPathEvaluator.getString("success");
        String message = jsonPathEvaluator.getString("message");
        String error = jsonPathEvaluator.getString("data.errors[0]");
        Assert.assertFalse(Boolean.parseBoolean(success));
        Assert.assertEquals(error, "\"" + missingParameter + "\"" + " is required");
        Assert.assertEquals(message, "The provided request parameters were invalid.");
    }

    public static void validateMissingRequestParametersError(Response response, List<String> missingParameters) {
        JsonPath jsonPathEvaluator = response.getBody().jsonPath();
        String success = jsonPathEvaluator.getString("success");
        String message = jsonPathEvaluator.getString("message");
        List<String> errors = jsonPathEvaluator.getList("data.errors");
        Assert.assertFalse(Boolean.parseBoolean(success));
        Assert.assertEquals(errors.size(), missingParameters.size(), "Incorrect error count");

        for (String missingParameter : missingParameters) {
            Assert.assertTrue(errors.contains("\"" + missingParameter + "\"" + " is required"));
        }

        Assert.assertEquals(message, "The provided request parameters were invalid.");
    }

    public static void validateInvalidRequestParametersError(Response response, List<String> expectedErrors) {
        JsonPath jsonPathEvaluator = response.getBody().jsonPath();
        String success = jsonPathEvaluator.getString("success");
        String message = jsonPathEvaluator.getString("message");
        List<String> errors = jsonPathEvaluator.getList("data.errors");
        Assert.assertFalse(Boolean.parseBoolean(success));
        Assert.assertEquals(message, "The provided request parameters were invalid.");
        Assert.assertEquals(errors.size(), expectedErrors.size(), "Lists don't have the same size");

        for (String error : expectedErrors) {
            Assert.assertTrue(errors.contains(error), error + " was not on the response error list. Errors return:" +
                    "\n" + errors);
        }
    }

    public static void validateUnprocessableEntityError(Response response, String expectedErrorMessage) {
        JsonPath jsonPathEvaluator = response.getBody().jsonPath();
        int statusCode = Integer.parseInt(jsonPathEvaluator.getString("statusCode"));
        String error = jsonPathEvaluator.getString("error");
        String message = jsonPathEvaluator.getString("message");
        Assert.assertEquals(statusCode, 422);
        Assert.assertEquals(error, "Unprocessable Entity");
        Assert.assertEquals(message, expectedErrorMessage);
    }

    public static void validateUrlNotFoundError(Response response) {
        JsonPath jsonPathEvaluator = response.getBody().jsonPath();
        int statusCode = Integer.parseInt(jsonPathEvaluator.getString("statusCode"));
        String error = jsonPathEvaluator.getString("error");
        Assert.assertEquals(statusCode, 404);
        Assert.assertEquals(error, "Not Found");
    }

    public static void checkResponseStatus(String testCaseID, String respCode, int returnedStatusCode) {
        // Log if response code is missing in input data sheet
        if (respCode == "" || respCode == null) {
            logger.info("TestCaseID: " + testCaseID + " Check response code is not present in Input data sheet");
        }
        checkResponseStatus(testCaseID, Integer.parseInt(respCode), returnedStatusCode);
    }

    public static void checkResponseStatus(String testCaseID, int expectedStatusCode, int returnedStatusCode) {
        // If response status code doesn't match, fail the test case
        if (expectedStatusCode == returnedStatusCode) {
            logger.info("TestCaseID: " + testCaseID + " response code matched. Expected Response code:" + expectedStatusCode + ", Actual Response code:" + returnedStatusCode);
        } else {
            logger.error("TestCaseID: " + testCaseID + " failed. Expected Response code:" + expectedStatusCode + ", Actual Response code:" + returnedStatusCode);
            Assert.fail("TestCaseID: " + testCaseID + " failed. Expected Response code:" + expectedStatusCode + ", Actual Response code:" + returnedStatusCode);
        }
    }

    public static <T> T verifyEndpointReturnsCorrectObject(Response response, String testCaseID, Class<T> responseClass) {
        int statusCode = response.getStatusCode();
        checkResponseStatus(testCaseID, "200", statusCode);

        T responseBody = null;
        try {
            responseBody = response.getBody().as(responseClass);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return responseBody;
    }

    public static <T> T verifyEndpointReturnsCorrectCreatedObject(Response response, String testCaseID, Class<T> responseClass) {
        int statusCode = response.getStatusCode();
        checkResponseStatus(testCaseID, "201", statusCode);

        T responseBody = null;
        try {
            responseBody = response.getBody().as(responseClass);
        } catch (Exception e) {
            logger.error("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
            Assert.fail("TestCaseID: " + testCaseID + " failed. Unable to deserialize response body\n" + e.getMessage());
        }
        return responseBody;
    }
}
