package com.DC.utilities.apiEngine.apiServices.insights.CPGAccount;

import com.DC.utilities.apiEngine.apiRequests.insights.CPGAccount.CPGAccountRequests;
import com.DC.utilities.apiEngine.models.responses.insights.CPGAccount.CpgAccount;
import io.restassured.response.Response;
import org.apache.log4j.Logger;

public class CPGAccountService {

    private static final Logger logger = Logger.getLogger(CPGAccountService.class);

    public static CpgAccount getAccountInfo(String jwt) throws Exception {
        logger.info("Getting account info");
        Response response = CPGAccountRequests.getAccountInfo(jwt);
        return response.getBody().as(CpgAccount.class);
    }
}
