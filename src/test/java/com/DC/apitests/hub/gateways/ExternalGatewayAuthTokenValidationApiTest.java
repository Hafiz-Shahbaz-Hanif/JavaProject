package com.DC.apitests.hub.gateways;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.ReadConfig;
import io.restassured.response.Response;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.util.Dictionary;

public class ExternalGatewayAuthTokenValidationApiTest {

    Logger logger;
    ReadConfig readConfig;
    String header = "Content-Type=application/json";

    ExternalGatewayAuthTokenValidationApiTest() {
        logger = Logger.getLogger(ExternalGatewayAuthTokenValidationApiTest.class);
        PropertyConfigurator.configure("log4j.properties");
        readConfig = ReadConfig.getInstance();
    }

    @Test(dataProvider = "Auth0_Token_Validation", dataProviderClass = GatewayDataProvider.class, description = "PH-260 -  Auth0 Token Validation Through External Gateway (Expired)- FILA/OS/MS")
    public void Hub_Auth0_Expired_Token_Validation_External_Gateway_Test(Dictionary<String, String> apiCall) throws Exception {
        logger.info("** Test has started.");

        SoftAssert softAssert = new SoftAssert();
        String url = apiCall.get("url");
        String token = apiCall.get("token");
        String headers = apiCall.get("platform").equalsIgnoreCase("fila") ? header : "";

        Response response = CommonApiMethods.callEndpoint(url, "GET", "", headers, "", token);
        softAssert.assertEquals(response.getStatusCode(), 401, "Unexpected status code.");
        softAssert.assertTrue(response.getBody().asString().isEmpty(), "Response body for external gateway auth token validation is expected to be empty. It is not.");

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }

    @Test(dataProvider = "Auth0_Token_Validation", dataProviderClass = GatewayDataProvider.class, description = "PH-260 - Auth0 Token Validation Through External Gateway (Invalid) - FILA/OS/MS")
    public void Hub_Auth0_Invalid_Token_Validation_External_Gateway_Test(Dictionary<String, String> apiCall) throws Exception {
        logger.info("** Test has started.");

        SoftAssert softAssert = new SoftAssert();
        String url = apiCall.get("url");
        String token = apiCall.get("token");
        String newToken = token.substring(0, token.lastIndexOf(".") + 1);
        String headers = apiCall.get("platform").equalsIgnoreCase("fila") ? header : "";

        Response response = CommonApiMethods.callEndpoint(url, "GET", "", headers, "", newToken);
        softAssert.assertEquals(response.getStatusCode(), 401, "Unexpected status code.");
        softAssert.assertTrue(response.getBody().asString().isEmpty(), "Response body for external gateway auth token validation is expected to be empty. It is not.");

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }

}