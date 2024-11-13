package com.DC.utilities;

import com.DC.utilities.hub.InsightsMethods;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.utilities.apiEngine.apiRequests.hub.insights.authservice.InsightsAuthServiceApiRequest;
import com.DC.utilities.apiEngine.apiRequests.hub.marketshare.authservice.MarketShareAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.requests.hub.insights.authservice.InsightsAuthServiceRequestBody;
import com.DC.utilities.apiEngine.models.responses.hub.insights.authservice.InsightsAuthServiceResponseBody;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SecurityAPI {
    static ReadConfig config = ReadConfig.getInstance();

    private static String payload = "{\r\n" +
            "    \"username\": \"" + config.getBearerUserName() + "\",\r\n" +
            "    \"password\":\"" + config.getBearerPassword() + "\"\r\n" +
            "}";

    public static String expiredAuth0TokenForFila = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkIzYTJsNjVyXzNvRHhYMkFwazc1VCJ9.eyJpc3M" +
            "iOiJodHRwczovL2F1dGgucWEuZGMuZmx5d2hlZWxkaWdpdGFsLmNvbS8iLCJzdWIiOiJhdXRoMHw2M2QyOTc3YjNhZGRkMTc3NzRlYzBjYjYiLCJhdWQiOlsiaHR0cHM6L" +
            "y9kYy1ub25wcm9kLWFwaS8iLCJodHRwczovL2RpZ2l0YWxjb21tZXJjZS1xYS51cy5hdXRoMC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNjgzNTUxOTk0LCJleHAiOjE2ODM2" +
            "MzgzOTQsImF6cCI6IldxU0poR3A0c09xbHFzcHV2MHpSY3htQzllNWFCQWtqIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCJ9.cpOGhc0SXs6UIacThAcTOIWiV7" +
            "xNHcK2fDvJULO1rD4LliJ_lz-HMVqtHSDh-Kjay7gcQrmaoySsBJoI4vwykyPCaLK6lYrasKdjzlcUb3P-UPNBDz0PsMW67s9o4EOE1QflaEHhWOR8_-zeHYX1r-V57KYrT5" +
            "ct-AaGWFSeT2ajP712Gu1UApFvNlwgeLptqM5ZHqXGzE-ESy7RR31IDJF0ZemadSpzKUdY0v61yLoeMGJitdg3GEuJiO3oTIU1gO2uPDTxqLceZOG2Kvy0DSXGmC08TlJzii" +
            "8UrZLASHqwW7GI3uXqwstA0Prt9xVE9n-4kqGDtL7-EQOPjyvLXw";
    public static String expiredAut0TokenForInsights = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkIzYTJsNjVyXzNvRHhYMkFwazc1VCJ9.eyJpc3MiOiJod" +
            "HRwczovL2F1dGgucWEuZGMuZmx5d2hlZWxkaWdpdGFsLmNvbS8iLCJzdWIiOiJhdXRoMHw2NDY1MWMzNGQ3MGZjM2E2NGIwODY0OTMiLCJhdWQiOlsiaHR0cHM6Ly9kYy1ub25" +
            "wcm9kLWFwaS8iLCJodHRwczovL2RpZ2l0YWxjb21tZXJjZS1xYS51cy5hdXRoMC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNjg2NzYwODQyLCJleHAiOjE2ODY4NDcyNDIsImF6c" +
            "CI6IldxU0poR3A0c09xbHFzcHV2MHpSY3htQzllNWFCQWtqIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCJ9.PNBmqG7qrzvuZwj8_7784KgfUXXWwmovQT79xOPhxAV" +
            "pHzI9TAk0wgeU2ZMZvixMn6Q_VDhfy9DJmiyz5WjIZWl8EJXIvtmDK7LUigxOWqhfuEpjvJl0CCuisJYDKB5bBzzmfSF8JDipMfQa9wtIJqPod_Rybq59z2JdMf6UINGGu5DqN" +
            "Px5nswWkm8hFP5JSb3QmtthJDvaWjT8rtrz7U59n4EwvmgyB3WIhqR3cZ96e5tjxLfAlIpeL8uRWt7DAx6T8A0qcghUE10CKl5JzmW6PQuiVr8QODTAa0B9NOlhwKEK1UsSapcF" +
            "_bSnPeXEpS25Xqm93UHXvqJB3Gs83Q";
    public static String expiredAuthTokenForMarketShare = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkIzYTJsNjVyXzNvRHhYMkFwazc1VCJ9." +
            "eyJpc3MiOiJodHRwczovL2F1dGgucWEuZGMuZmx5d2hlZWxkaWdpdGFsLmNvbS8iLCJzdWIiOiJhdXRoMHw2NDZmYWU1ZTJkYTk3ODIyYWFiY2RlZDEiLCJhdWQiOlsia" +
            "HR0cHM6Ly9kYy1ub25wcm9kLWFwaS8iLCJodHRwczovL2RpZ2l0YWxjb21tZXJjZS1xYS51cy5hdXRoMC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNjg2NzU4OTM0LCJleHA" +
            "iOjE2ODY4NDUzMzQsImF6cCI6IldxU0poR3A0c09xbHFzcHV2MHpSY3htQzllNWFCQWtqIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCJ9.PZDePpQcV422K2fVtY" +
            "p9L63ILvXPLMQIuDxjIuAIBIDZFc0cvxk1IcHOQyH-dNRN1EVJh7UaD_ROGWtaxQq-LOdVDxt0Te0UIFmhfDJhuR5-u9SfLqtCLl8sb8Mq3MTBY4EtN2TRmSvpucXwYmmYMga2" +
            "9YmaUo3DsN5kZkKI2u0i9-WUDBEan37Ts8WRP0B9YS5hF67Ziw_urs13ibexoIjuiJncdWZo5EYknLEOuROnw_1vdwCJG1aUxMAcqxZsix0P1T-yUQ5EwKFxLA8T7UVSnXFd" +
            "xGDvDCuhx5reKAniESUiUOmAPHvRh-91oTvuutR-HNbh27PT9F36ry4_Jw";

    public static String expiredAuthTokenForConnect = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkIzYTJsNjVyXzNvRHhYMkFwazc1VCJ9.eyJp" +
            "c3MiOiJodHRwczovL2F1dGgucWEuZGMuZmx5d2hlZWxkaWdpdGFsLmNvbS8iLCJzdWIiOiJhdXRoMHw2M2QyOTc3YjNhZGRkMTc3NzRlYzBjYjYiLCJhdWQiOlsiaHR0cHM6Ly" +
            "9kYy1ub25wcm9kLWFwaS8iLCJodHRwczovL2RpZ2l0YWxjb21tZXJjZS1xYS51cy5hdXRoMC5jb20vdXNlcmluZm8iXSwiaWF0IjoxNjkwMzAzMDM1LCJleHAiOjE2OTAzNDYy" +
            "MzUsImF6cCI6IldxU0poR3A0c09xbHFzcHV2MHpSY3htQzllNWFCQWtqIiwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCJ9.ASa3UmGleFRCVK0hbu4ffDgpM2ehR2hSxQ" +
            "_bEmb3XndQy5GaKE-AFYaoFnP-h8iIN7e2-dVlijCKF7HI5m4Ea8BLcVbMvi2DtNcn6cGRuk-dDob9Jo3yAyh_beiz8WDLy2O_ZEs2Je4sLZg3PJXT0jkAZnp_IMRRufIqxKfgb" +
            "UERJDGScx5_3FqpCNL5ODSFMLyvLo8oxb8yT0b5h2GS6vspCkFpMNxkeTIrfSW5W_tVoJSKemj-nW5S_aJeuCk64Wn_O26HOnXruhbvkYDPuWqOfKVGFefT2N2Q0ueLxCvBwIZY" +
            "EjmxAMRSnnVVvEw73FwP-Qc4JZlK3g7rLvI4Xw";

    public static String getExpiredAuth0TokenForConnect() {
        return expiredAuthTokenForConnect;
    }

    public static String getExpiredAuth0TokenForFila() {
        return expiredAuth0TokenForFila;
    }

    public static String getExpiredAuth0TokenForInsights() {
        return expiredAut0TokenForInsights;
    }

    public static String getExpiredAuthTokenForMarketShare() {
        return expiredAuthTokenForMarketShare;
    }

    public static String getBasicAuthValue() {
        return "Basic " + config.getBasicAuthValue();
    }

    public static String getTokenAPI() throws Exception {
        String token = null;
        RestAssured.baseURI = config.getBearerURL();

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.headers(headerMap);
        httpRequest.body(payload);

        // Response object
        Response response = httpRequest.request(Method.POST);

        //Retrieve the token
        if (response.statusCode() == HttpStatus.SC_OK) {
            token = response.jsonPath().getString("accessToken");
        }

        return token;
    }

    public static String getBodyForOktaToken(String username, String password) {
        String payload = "{\r\n" +
                "    \"username\": \"" + username + "\",\r\n" +
                "    \"password\":\"" + password + "\"\r\n" +
                "}";
        return payload;
    }

    public static String getOktaTokenForUser(String username, String password) throws Exception {
        String token = null;
        RestAssured.baseURI = config.getBearerURL();

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.headers(headerMap);
        httpRequest.body(getBodyForOktaToken(username, password));

        Response response = httpRequest.request(Method.POST);

        if (response.statusCode() == HttpStatus.SC_OK) {
            token = response.jsonPath().getString("accessToken");
        }
        return token;
    }

    public static Response login(String endpoint, String username, String password) throws Exception {
        RestAssured.baseURI = endpoint;
        String payload = "{\r\n" +
                "    \"username\": \"" + username + "\",\r\n" +
                "    \"password\":\"" + password + "\"\r\n" +
                "}";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("cache-control", "no-cache");

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.headers(headerMap);
        httpRequest.body(payload);

        return httpRequest.request(Method.POST);
    }

    public static Map<String, String> loginAndGetCookies(String endpoint, String username, String password) throws Exception {
        Response response = login(endpoint, username, password);
        return response.getCookies();
    }

    public static String loginAndGetJwt(String endpoint, String username, String password) throws Exception {
        Response response = login(endpoint, username, password);
        return response.getCookie("jsid");
    }

    public static Response changeInsightsCompany(String jwt, String companyId, String companyName) {
        RestAssured.baseURI = config.getInsightsApiSupportuserLoginEndpoint();
        String payload = "{\r\n" +
                "    \"companyId\": \"" + companyId + "\",\r\n" +
                "    \"companyName\":\"" + companyName + "\"\r\n" +
                "}";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("cache-control", "no-cache");
        headerMap.put("X-Token", jwt);

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.headers(headerMap);
        httpRequest.body(payload);

        return httpRequest.request(Method.POST);
    }

    public static String changeInsightsCompanyAndGetJwt(String jwt, String companyId, String companyName) {
        Response response = changeInsightsCompany(jwt, companyId, companyName);
        JsonPath jsonPathEvaluator = response.getBody().jsonPath();
        return jsonPathEvaluator.getString("jwt");
    }

    public static String getHubMarketShareApiKey(String auth0Token) throws Exception {
        Response authServiceResponse = MarketShareAuthServiceApiRequest.getApiKey(auth0Token);
        return authServiceResponse.jsonPath().getString("api_key");
    }

    public static String getHubInsightsJwt(String auth0Token) throws Exception {
        String headers = "Content-Type=application/json";

        InsightsAuthServiceRequestBody authServiceRequestBody = new InsightsAuthServiceRequestBody(auth0Token);
        Response authServiceResponse = InsightsAuthServiceApiRequest.getUserToken(authServiceRequestBody, headers, "", getBasicAuthValue());

        InsightsAuthServiceResponseBody authServiceResponseBody = authServiceResponse.getBody().as(InsightsAuthServiceResponseBody.class);
        String userToken = authServiceResponseBody.getJwt().getToken();

        Response jwtResponse = InsightsAuthServiceApiRequest.getJwt(authServiceResponseBody, headers, "", userToken);
        return jwtResponse.jsonPath().getString("jwt");
    }

    /**
     * Logs in to DC App, obtains token and quits driver.
     *
     * @param driver
     * @param useremail
     * @param password
     * @return
     * @throws InterruptedException
     */
    public static String loginToDcAppToGetAuthToken(WebDriver driver, String useremail, String password) throws InterruptedException {
        DCLoginPage lp = new DCLoginPage(driver);
        driver.get(config.getDcAppUrl());
        lp.loginDcApp(useremail, password);
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        String token = (String) js.executeScript("return window.localStorage.getItem('auth0-token');");
        driver.close();
        driver.quit();
        return token;
    }

    /**
     * This method needs DC App logged in to get auth0 token.
     * Does not quit driver.
     *
     * @param driver
     * @return
     * @throws InterruptedException
     */

    public static String getAuthToken(WebDriver driver) throws InterruptedException {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        return (String) js.executeScript("return window.localStorage.getItem('auth0-token');");
    }

    public static String getJwtForInsightsUser(WebDriver driver) throws Exception {
        String insightsUserAuthToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        return new InsightsMethods().getJwtForInsightsUser(insightsUserAuthToken);
    }

    public static String getAuthOToken(String userEmail, String userPassword) throws Exception {
        String clientId = config.getAuth0ClientId();
        String callbackUrl = "https://cloud.development.dc.flywheeldigital.com/app";
        String scope = "openid profile mail";
        String audience = "https://dc-nonprod-api/";

        //authorize call to get state from redirect url
        CookieHandler.setDefault(new CookieManager());
        HttpURLConnection authorizeCall = openHttpURLConnection("https://auth.non-prod.dc.flywheeldigital.com/authorize?client_id=" + clientId + "&scope=" + URLEncoder.encode(scope, "UTF-8") + "&response_type=token&redirect_uri=" + callbackUrl + "&audience=" + audience);
        authorizeCall.setRequestMethod("GET");
        authorizeCall.getResponseCode();
        String state = authorizeCall.getURL().toString().split("state=")[1].split("&")[0];

        //login call
        Map<String, Object> loginCallPayload = new HashMap<>();
        loginCallPayload.put("audience", audience);
        loginCallPayload.put("client_id", clientId);
        loginCallPayload.put("connection", "Username-Password-Authentication");
        loginCallPayload.put("redirect_uri", callbackUrl);
        loginCallPayload.put("response_type", "code");
        loginCallPayload.put("scope", scope);
        loginCallPayload.put("state", state);
        loginCallPayload.put("tenant", "digitalcommerce-qa");
        loginCallPayload.put("username", userEmail);
        loginCallPayload.put("password", userPassword);
        String loginCallPayloadString = new ObjectMapper().writeValueAsString(loginCallPayload);

        HttpURLConnection loginCall = openHttpURLConnection("https://auth.non-prod.dc.flywheeldigital.com/usernamepassword/login");
        loginCall.setRequestMethod("POST");
        loginCall.setRequestProperty("Content-Type", "application/json");
        loginCall.setDoOutput(true);
        passPayloadToConnection(loginCall, loginCallPayloadString);

        //login call response
        StringBuilder loginCallResponse = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(loginCall.getInputStream(), StandardCharsets.UTF_8));
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            loginCallResponse.append(responseLine.trim());
        }

        //parse login response to document and get form payload
        Document loginResponseDocument = Jsoup.parse(loginCallResponse.toString());
        Elements docInputs = loginResponseDocument.select("input[name]");
        Map<String, String> formPayload = new HashMap<>();
        for (Element input : docInputs) {
            formPayload.put(input.attr("name"), input.val());
        }

        String formPayloadString = formPayload.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        //callback call and token retrieval
        HttpURLConnection callbackCall = openHttpURLConnection("https://auth.non-prod.dc.flywheeldigital.com/login/callback");
        callbackCall.setRequestMethod("POST");
        callbackCall.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        callbackCall.setDoOutput(true);
        passPayloadToConnection(callbackCall, formPayloadString);
        callbackCall.getResponseCode();
        return "Bearer " + callbackCall.getURL().toString().split("access_token=")[1].split("&")[0];
    }

    private static void passPayloadToConnection(HttpURLConnection connection, String payload) throws IOException {
        OutputStream os = connection.getOutputStream();
        byte[] input = payload.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
    }

    private static HttpURLConnection openHttpURLConnection(String url) throws IOException {
        URL authUrl = new URL(url);
        return (HttpURLConnection) authUrl.openConnection();
    }
}

