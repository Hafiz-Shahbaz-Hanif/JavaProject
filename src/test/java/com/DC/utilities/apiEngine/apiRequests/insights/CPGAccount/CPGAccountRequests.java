package com.DC.utilities.apiEngine.apiRequests.insights.CPGAccount;

import com.DC.utilities.apiEngine.routes.insights.CPGAccount.CPGAccountRoutes;
import io.restassured.response.Response;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class CPGAccountRequests {

    public static Response getAccountInfo(String jwt) throws Exception {
        String url = CPGAccountRoutes.getCpgAccountHost() + "/account";
        return callEndpoint(url, jwt, "GET", "", "");
    }

    public static Response getCompanyInfo(String companyId, String jwt) throws Exception {
        String url = CPGAccountRoutes.getCpgAccountHost() + "/company/" + companyId;
        return callEndpoint(url, jwt, "GET", "", "");
    }
}
