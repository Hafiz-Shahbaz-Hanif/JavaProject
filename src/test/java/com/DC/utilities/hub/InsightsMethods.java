package com.DC.utilities.hub;

import com.DC.pageobjects.PageHandler;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.navigationMenus.InsightsNavigationMenu;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.hub.insights.authservice.InsightsAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.requests.hub.insights.authservice.HubInsightsRolesRequestBody;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class InsightsMethods extends HubCommonMethods {

    public String switchCompany(String authToken, String companyId) throws Exception {
        String payload = createPayloadForCompanySwitchCall(companyId);
        Response response = InsightsAuthServiceApiRequest.supportLogin(payload, authToken);
        Assert.assertEquals(response.statusCode(), 200, "Cannot switch company.");
        return response.jsonPath().getString("data.data");
    }

    public String createPayloadForCompanySwitchCall(String companyId) {
        return "{\"companyId\":\"" + companyId + "\"}";
    }

    public JSONArray getCompaniesForUser(String authToken, String header) throws Exception {
        Response response = InsightsAuthServiceApiRequest.getCompaniesForUserExternalGateway(header, authToken);
        return new JSONObject(response.getBody().asString()).getJSONObject("data").getJSONArray("companies");
    }

    public String getDefaultCompanyIdForUser(JSONArray companies, WebDriver driver) throws Exception {
        AppHomepage hp = new AppHomepage(driver);
        String defaultCompanyId = null;
        hp.openPage("Identify", "Attribute Insights");
        String defaultCompany = hp.waitUntilAttributeValuePresentInElement(InsightsNavigationMenu.COMPANY_SWITCHER, "value");

        for (int i = 0; i < companies.length(); i++) {
            JSONObject json = companies.getJSONObject(i);
            if (json.getString("name").equals(defaultCompany)) {
                defaultCompanyId = json.getString("_id");
            }
        }
        return defaultCompanyId;
    }

    public String getCompanyIdInJwtStoredInDb(String auth0Token) throws Exception {
        String storedJwtForUser = getJwtForInsightsUser(auth0Token);
        return decodeInsightsJwtToGetCompanyId(storedJwtForUser);
    }

    public String getJwtForInsightsUser(String auth0Token) throws Exception {
        Response userInfoResponse = getUserHubInfo(auth0Token);
        return (String) getUserPlatformInfo(userInfoResponse, "onespace");
    }

    public String switchToRandomCompany(String auth0, JSONArray companiesForUser) throws Exception {
        int randomNumber = SharedMethods.getRandomNumber(companiesForUser.length() - 1);
        JSONObject jsonForCompany = companiesForUser.getJSONObject(randomNumber);
        String randomCompanyId = jsonForCompany.getString("_id");
        return switchCompany(auth0, randomCompanyId);
    }

}