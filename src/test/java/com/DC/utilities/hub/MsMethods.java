package com.DC.utilities.hub;

import com.DC.utilities.apiEngine.apiRequests.hub.marketshare.authservice.MarketShareAuthServiceApiRequest;
import com.DC.utilities.apiEngine.models.requests.hub.marketshare.authservice.MarketShareUserUpdateRequestBody;
import com.DC.utilities.apiEngine.models.responses.hub.marketshare.authservice.MarketShareUserInfoResponseBody;
import io.restassured.response.Response;
import org.testng.Assert;

public class MsMethods extends HubCommonMethods {

    public void updateEdgeUser(String authTokenForAdmin, String authTokenForUserToUpdate, int permissionLevel) throws Exception {
        Response usersForAdmin = MarketShareAuthServiceApiRequest.getUserInfo(authTokenForAdmin);
        Assert.assertEquals(usersForAdmin.statusCode(), 201, "Cannot get user info for Edge admin.");

        MarketShareUserInfoResponseBody usersAssociatedWithToken = usersForAdmin.getBody().as(MarketShareUserInfoResponseBody.class);
        MarketShareUserInfoResponseBody.User admin = usersAssociatedWithToken.getUsers().get(0);
        String apiKeyForUser = admin.getKey().getKey();

        Response userToUpdate = MarketShareAuthServiceApiRequest.getUserInfo(authTokenForUserToUpdate);
        Assert.assertEquals(userToUpdate.statusCode(), 201, "Cannot get user info for Edge user to update.");
        MarketShareUserInfoResponseBody users = userToUpdate.getBody().as(MarketShareUserInfoResponseBody.class);
        MarketShareUserInfoResponseBody.User user = users.getUsers().get(0);
        String clientId = user.getClient().getUuid();
        String userId = user.getUuid();

        MarketShareUserUpdateRequestBody reqBody= new MarketShareUserUpdateRequestBody("Edge", "Only", user.getEmail(), permissionLevel);
        Response updateUser = MarketShareAuthServiceApiRequest.updateMsUser(clientId, userId, reqBody,"X-API-KEY "+apiKeyForUser);
        Assert.assertEquals(updateUser.statusCode(), 200, "Cannot update Edge user.");
    }
}
