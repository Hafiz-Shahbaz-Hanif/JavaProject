package com.DC.apitests.hub.token;

import java.util.List;

import com.DC.testcases.BaseClass;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.DC.apitests.hub.gateways.ExternalGatewayInsightsApiTest;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.hub.marketshare.authservice.MarketShareAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice.MarketShareUserInfoResponseBody;
import com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice.MarketShareUserInfoResponseBody.User;
import com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice.MarketShareUserInfoResponseBody.User.Client;
import com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice.MarketShareUserInfoResponseBody.User.Key;
import io.restassured.response.Response;

public class Auth0TokenForMarketShareApiTest {

    Logger logger;
    ReadConfig readConfig;
    String auth0Token;
    String expiredAuth0Token;
    String msuseremail;
    BaseClass base;
    WebDriver driver;

    Auth0TokenForMarketShareApiTest() {
        logger = Logger.getLogger(ExternalGatewayInsightsApiTest.class);
        readConfig =  ReadConfig.getInstance();
        PropertyConfigurator.configure("log4j.properties");
        base = new BaseClass();
    }

    @BeforeClass
    private void getTestData(ITestContext testContext) throws Exception {
        driver = base.initializeBrowser(testContext, false);
        auth0Token = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, readConfig.getHubEdgeUserEmail(), readConfig.getHubEdgeUserPassword());
        expiredAuth0Token = SecurityAPI.getExpiredAuthTokenForMarketShare();
        msuseremail = readConfig.getHubEdgeUserEmail();
    }

    @Test(description = "PH-159 - Verify MS user info response data")
    public void Verify_MS_User_Info_Data_Api_Test() throws Exception {
        logger.info("** Test has started.");
        SoftAssert softAssert = new SoftAssert();

        logger.info("** Calling MS user info endpoint to get users associated with email in auth0 account");
        Response usersForToken = MarketShareAuthServiceApiRequest.getUserInfo(auth0Token);
        Assert.assertEquals(usersForToken.statusCode(), 201, "** Error calling MS user info endpoint.");
        MarketShareUserInfoResponseBody users = usersForToken.getBody().as(MarketShareUserInfoResponseBody.class);

        logger.info("** Checking auth account level data");
        String userEmail = users.getAuth0().getEmail();
        String authId = users.getAuth0().getSub();

        softAssert.assertEquals(userEmail, msuseremail, "** Auth0 email does not match with MS user email");
        softAssert.assertTrue(authId.startsWith("auth0"), "** Auth0 id could not be identified");

        List<User> usrs = users.getUsers();
        softAssert.assertTrue(usrs.size() > 0, "** Auth0 token is not associated with any user on MS side");

        logger.info("** Checking MS user specific data");
        for (User user : usrs) {
            int userCount = 1;
            softAssert.assertNotNull(user.getId(), "**User("+userCount+") id is null");
            softAssert.assertNotNull(user.getUuid(), "**User("+userCount+") uuid is null");
            softAssert.assertNotNull(user.getEmail(), "**User("+userCount+") id email null");
            softAssert.assertNotNull(user.getUsername(), "**User("+userCount+") username is null");

            Key key = user.getKey();
            softAssert.assertNotNull(key.getId(), "**User("+userCount+") key id is null");
            softAssert.assertNotNull(key.getKey(), "**User("+userCount+") api key null");
            softAssert.assertNotNull(key.getClientId(), "**User("+userCount+") client id is null");
            softAssert.assertNotNull(key.getUserId(), "**User("+userCount+") key user id is null");
            softAssert.assertEquals(user.getId(), key.getUserId(), "** User("+userCount+") id and key user id does not match");

            Client clnt = user.getClient();
            softAssert.assertNotNull(clnt.getId(), "**User("+userCount+") client id is null");
            softAssert.assertNotNull(clnt.getUuid(), "**User("+userCount+") client uuid is null");
            softAssert.assertNotNull(clnt.getSubscriptions(), "**User("+userCount+") client subscriptions is null");
            softAssert.assertNotNull(clnt.getClientName(), "**User("+userCount+") client name is null");
            softAssert.assertNotNull(clnt.getDomain(), "**User("+userCount+") client domain is null");
            softAssert.assertEquals(key.getClientId(), clnt.getId(), "**User("+userCount+") key and client ids do not match");
            userCount++;
        }

        softAssert.assertAll();
        logger.info("** Test completed successfully");
    }

    @Test(description = "PH-159 - Verify auth token can fetch api-key for MS user and make furter calls to MS endpoints")
    public void Verify_Auth0Token_Fetches_MS_ApiKey_For_MS_User_Api_Test() throws Exception {
        logger.info("** Test has started.");
        SoftAssert softAssert = new SoftAssert();
        String apiKeyForUser = null;

        logger.info("** Sending auth0 token to get users associated with the token's email");
        Response usersForToken = MarketShareAuthServiceApiRequest.getUserInfo(auth0Token);
        softAssert.assertEquals(usersForToken.statusCode(), 201, "** Error calling MS user info endpoint.");
        MarketShareUserInfoResponseBody users = usersForToken.getBody().as(MarketShareUserInfoResponseBody.class);

        logger.info("** Selecting a random user to get api key for the user");
        List<User> usrs = users.getUsers();
        User randomUserAssociatedWithEmail = (User) SharedMethods.getRandomItemFromList(usrs);
        apiKeyForUser = randomUserAssociatedWithEmail.getKey().getKey();

        logger.info("** Making call to MS tld currency end point using MS user ("+randomUserAssociatedWithEmail.getUsername()+") api key ("+apiKeyForUser+")");
        Response clientsResponse = MarketShareAuthServiceApiRequest.getTldCurrency("X-API-KEY "+apiKeyForUser);
        softAssert.assertEquals(clientsResponse.statusCode(), 200, "** Error calling MS tld currency endpoint.");

        logger.info("** Making call to MS tlds end point using MS user api key");
        Response tldsResponse = MarketShareAuthServiceApiRequest.getTlds("X-API-KEY "+apiKeyForUser);
        softAssert.assertEquals(tldsResponse.statusCode(), 200, "** Error calling MS tlds endpoint.");

        softAssert.assertAll();
        logger.info("** Test completed successfully");

    }

    @Test(description = "PH-159 - Verify expired auth token throws 401 error")
    public void Expired_Auth0_Token_Cannot_Generate_MarketShare_ApiKey_Api_Test() throws Exception {
        logger.info("** Test has started.");

        logger.info("** Sending expired auth0 token to MS user info endpoint");
        Response usersForToken = MarketShareAuthServiceApiRequest.getUserInfo(expiredAuth0Token);
        Assert.assertEquals(usersForToken.statusCode(), 401, "** Expired auth0 token should expected to throw 401.");

        logger.info("** Test completed successfully");
    }

}