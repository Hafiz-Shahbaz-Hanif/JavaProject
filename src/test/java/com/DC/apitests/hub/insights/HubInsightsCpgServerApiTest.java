package com.DC.apitests.hub.insights;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.hub.insights.HubInsightsCpgServerApiRequest;
import com.DC.utilities.apiEngine.models.requests.hub.insights.HubInsightsCategoriesFilterExportRequestBody;
import com.DC.utilities.apiEngine.models.requests.hub.insights.HubInsightsProductChainProgressRequestBody;
import io.restassured.response.Response;

public class HubInsightsCpgServerApiTest {

	Logger logger;
	ReadConfig readConfig;
	String xToken;

	HubInsightsCpgServerApiTest() {
		logger = Logger.getLogger(HubInsightsCpgServerApiTest.class);
		PropertyConfigurator.configure("log4j.properties");
	}

	@BeforeClass
	void getToken() throws Exception {
		readConfig = ReadConfig.getInstance();
		xToken = SecurityAPI.loginAndGetJwt(readConfig.getInsightsApiLoginEndpoint(),
				readConfig.getHubInsightsUsername(), readConfig.getHubInsightsPassword());
	}

	@Test(description = "PH-67 - Verify new cpg server is up and endpoints available are able to receive calls - Current Users Export")
	public void Hub_Insights_CpgServer_Current_Users_Export_Api_Test() throws Exception {
		logger.info("** Test has started.");
		String cueResponseCode;

		Response cueResponse = HubInsightsCpgServerApiRequest.getCurrentUsersExport("", "", xToken);
		cueResponseCode = String.valueOf(cueResponse.statusCode());
		Assert.assertTrue(!cueResponseCode.startsWith("5") , "** Cpg server error! Status code: " + cueResponseCode);
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-67 - Verify new cpg server is up and endpoints available are able to receive calls - Product Chain Progress")
	public void Hub_Insights_CpgServer_Product_Chain_Progress_Api_Test() throws Exception {
		logger.info("** Test has started.");
		String pcpResponseCode;

		HubInsightsProductChainProgressRequestBody pcpReqBody = new HubInsightsProductChainProgressRequestBody(1, 25, new ArrayList<>(), new ArrayList<>());
		Response pcpResponse = HubInsightsCpgServerApiRequest.productChainProgress(pcpReqBody, "", "", xToken);
		pcpResponseCode = String.valueOf(pcpResponse.statusCode());
		Assert.assertTrue(!pcpResponseCode.startsWith("5") , "** Cpg server error! Status code: " + pcpResponseCode);
		logger.info("** Test completed successfully");
	}

	@Test(description = "PH-67 - Verify new cpg server is up and endpoints available are able to receive calls - Categories Filter Export")
	public void Hub_Insights_CpgServer_Categories_Filter_Export_Api_Test() throws Exception {
		logger.info("** Test has started.");
		String cfeResponseCode;

		HubInsightsCategoriesFilterExportRequestBody cfeReqBody = new HubInsightsCategoriesFilterExportRequestBody(1, 25, new ArrayList<>());
		Response cfeResponse = HubInsightsCpgServerApiRequest.categoriesFilterExport(cfeReqBody, "", "", xToken);
		cfeResponseCode = String.valueOf(cfeResponse.statusCode());
		Assert.assertTrue(!cfeResponseCode.startsWith("5"), "** Cpg server error! Status code: " + cfeResponseCode);
		logger.info("** Test completed successfully");
	}


}