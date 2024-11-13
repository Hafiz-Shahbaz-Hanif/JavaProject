package com.DC.apitests.adc.advertising.media;

import com.DC.testcases.BaseClass;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ReportingDashboardRequestBody;
import com.DC.utilities.apiEngine.models.responses.adc.advertising.media.ReportingDashboardResponseBody;
import com.DC.utilities.apiEngine.routes.adc.advertising.media.MediaRoutes;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.response.Response;


public class ReportingDashboardApiTest extends BaseClass {
	
	private final String DAILY = "DAILY";
	private final String WEEKLY = "WEEKLY";
	private final String MONTHLY = "MONTHLY";

	@Test(dataProvider = "ReportingDashboard_Amazon", dataProviderClass = MediaApiDataProvider.class, description = "Media reporting dashboard API test for Amazon (Daily-Last 7 days)")
	public void ReportingDashboard_Amazon_Api_Test(String tcId, String testDescription, String businessUnit, String client, String headers, String params, String statusCode) throws Exception {

		LOGGER.info("** Media reporting dashboard test case (" + tcId + ") has started.");

		SoftAssert softAssert = new SoftAssert();
		
		String expectedFirstDayOfLastSevenDays = DateUtility.getFirstDayOfLastSevenDays();
		String expectedYesterday = DateUtility.getYesterday();
		
		ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(DAILY, "actual", "direct_sales", SharedMethods.createList("advertisedSkuSales,otherSkuSales"), expectedFirstDayOfLastSevenDays, expectedYesterday, 39, "AMAZON", null, "ATTR_14D");

		Gson gson = new GsonBuilder().create();
		String reqBody = gson.toJson(rd);

		Response response = CommonApiMethods.callEndpoint(MediaRoutes.getReportingDashboardRoutePath(), "POST", reqBody, headers, (params.equalsIgnoreCase("x") ? "" : params), SecurityAPI.getTokenAPI());

		Assert.assertEquals(response.statusCode(), Integer.parseInt(statusCode), "** Failure! Status code! Expected: " + statusCode + "-" + "Actual: " + response.statusCode());

		LOGGER.info("** Deserializing the response");
		ReportingDashboardResponseBody responseBody = gson.fromJson(response.getBody().asString(), ReportingDashboardResponseBody.class);
		
		String responseInterval = responseBody.getInterval();
		String responseFirstDayOfLastSevenDays = responseBody.getAmsInfo().get(0).getSeriesInfo().get(0).getDateKey();
		String responseYesterday= responseBody.getAmsInfo().get(0).getSeriesInfo().get(6).getDateKey();
		expectedFirstDayOfLastSevenDays = DateUtility.formatDate(expectedFirstDayOfLastSevenDays);
		expectedYesterday = DateUtility.formatDate(expectedYesterday);
		
		softAssert.assertEquals(DAILY, responseInterval, "** Failure! Date interval! Expected: DAILY -" + "Actual: " + responseInterval);
		softAssert.assertEquals(responseFirstDayOfLastSevenDays, expectedFirstDayOfLastSevenDays, "** Failure! First day of last 7 days do not match in reponse.");
		softAssert.assertEquals(responseYesterday, expectedYesterday, "** Failure! Last day of last 7 days do not match in reponse.");
		
		for (int i = 0; i < responseBody.getSum().getSumData().size(); i++) {
			softAssert.assertNotNull(responseBody.getSum().getSumData().get(i), "** Failure! Null value found in response body for Sum data");
		}

		for (int i = 0; i < responseBody.getAmsInfo().get(0).getSeriesInfo().size(); i++) {
			for (int j = 0; j < responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForAmazon().size(); j++) {
				String dailyData = responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForAmazon().get(j);
				softAssert.assertNotNull(dailyData, "** Failure! Null value found in response body for daily data");
			}
		}
		
		softAssert.assertAll();
		LOGGER.info("** Execution for test case (" + tcId + ") has completed successfully");   
	}
	
	@Test(dataProvider = "ReportingDashboard_Walmart", dataProviderClass = MediaApiDataProvider.class, description = "Media reporting dashboard API test for Walmart (Monthly-Last 6 months)")
	public void ReportingDashboard_Walmart_Api_Test(String tcId, String testDescription, String businessUnit, String client, String headers, String params, String statusCode) throws Exception {

		LOGGER.info("** Media reporting dashboard test case (" + tcId + ") has started.");

		SoftAssert softAssert = new SoftAssert();
		
		String expectedFirstDayOfLastSixMonths = DateUtility.getFirstDayOfLastSixMonths();
		String expectedFirstDayOfLastMonth = DateUtility.getFirstDayOfLastMonth();

		ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(MONTHLY, "actual", "direct_sales", SharedMethods.createList("advertisedSkuSales,otherSkuSales"), expectedFirstDayOfLastSixMonths, expectedFirstDayOfLastMonth, 39, "WALMART", null, "ATTR_14D");

		Gson gson = new GsonBuilder().create();
		String reqBody = gson.toJson(rd);

		Response response = CommonApiMethods.callEndpoint(MediaRoutes.getReportingDashboardRoutePath(), "POST", reqBody, headers, (params.equalsIgnoreCase("x") ? "" : params), SecurityAPI.getTokenAPI());

		Assert.assertEquals(response.statusCode(), Integer.parseInt(statusCode), "** Failure! Expected status code: " + statusCode + "-" + "Actual status code: " + response.statusCode());

		LOGGER.info("** Deserializing the response");
		ReportingDashboardResponseBody responseBody = gson.fromJson(response.getBody().asString(), ReportingDashboardResponseBody.class);
		
		String responseInterval = responseBody.getInterval();
		String responseFirstDayOfLastSixMonths = responseBody.getAmsInfo().get(0).getSeriesInfo().get(0).getDateKey();
		String responseFirstDayOfLastMonth = responseBody.getAmsInfo().get(0).getSeriesInfo().get(5).getDateKey();
		expectedFirstDayOfLastSixMonths = DateUtility.formatDate(expectedFirstDayOfLastSixMonths);
		expectedFirstDayOfLastMonth = DateUtility.formatDate(expectedFirstDayOfLastMonth);

		softAssert.assertEquals(MONTHLY, responseInterval, "** Failure! Date interval! Expected: MONTHLY -" + "Actual: " + responseInterval);
		softAssert.assertEquals(responseFirstDayOfLastSixMonths, expectedFirstDayOfLastSixMonths, "** Failure! First day of last 6 months do not match in reponse. ");
		softAssert.assertEquals(responseFirstDayOfLastMonth, expectedFirstDayOfLastMonth, "** Failure! First day of last month do not match in reponse. ");

		for (int i = 0; i < responseBody.getSum().getSumData().size(); i++) {
			softAssert.assertNotNull(responseBody.getSum().getSumData().get(i), "** Failure! Null value found in response body for Sum data");
		}
		
		for (int i = 0; i < responseBody.getAmsInfo().get(0).getSeriesInfo().size(); i++) {
			for (int j = 0; j < responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForWalmart().size(); j++) {
				String monthlyData = responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForWalmart().get(j);
				softAssert.assertNotNull(monthlyData, "** Failure! Null value found in response body for monthly data");
			}
		}
		
		softAssert.assertAll();
		LOGGER.info("** Execution for test case (" + tcId + ") has completed successfully"); 
	}
	
	@Test(dataProvider = "ReportingDashboard_Criteo", dataProviderClass = MediaApiDataProvider.class, description = "Media reporting dashboard API test for Criteo (Weekly-Last 4 weeks)")
	public void ReportingDashboard_Criteo_Api_Test(String tcId, String testDescription, String businessUnit, String client, String headers, String params, String statusCode) throws Exception {

		LOGGER.info("** Media reporting dashboard test case (" + tcId + ") has started.");

		SoftAssert softAssert = new SoftAssert();
		
		String expectedFirstDayOfLastFourWeeks = DateUtility.getFirstDayOfLastFourWeeks();
		String expectedLastDayOfLastFourWeeks = DateUtility.getLastDayOfLastFourWeeks();

		ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(WEEKLY, "actual", "direct_sales", SharedMethods.createList("advertisedSkuSales,otherSkuSales"), expectedFirstDayOfLastFourWeeks, expectedLastDayOfLastFourWeeks, 39, "CRITEO", null, "ATTR_14D");

		Gson gson = new GsonBuilder().create();
		String reqBody = gson.toJson(rd);

		Response response = CommonApiMethods.callEndpoint(MediaRoutes.getReportingDashboardRoutePath(), "POST", reqBody, headers, (params.equalsIgnoreCase("x") ? "" : params), SecurityAPI.getTokenAPI());

		Assert.assertEquals(response.statusCode(), Integer.parseInt(statusCode), "** Failure! Expected status code: " + statusCode + "-" + "Actual status code: " + response.statusCode());

		LOGGER.info("** Deserializing the response");
		ReportingDashboardResponseBody responseBody = gson.fromJson(response.getBody().asString(), ReportingDashboardResponseBody.class);
		
		String responseInterval = responseBody.getInterval();
		String responseFirstDayOfLastFourWeeks = responseBody.getAmsInfo().get(0).getSeriesInfo().get(0).getDateKey();
		String responseLastDayOfLastFourWeeks = responseBody.getAmsInfo().get(0).getSeriesInfo().get(3).getDateKey();
		expectedFirstDayOfLastFourWeeks = DateUtility.formatDate(expectedFirstDayOfLastFourWeeks);
		expectedLastDayOfLastFourWeeks = DateUtility.formatDate(expectedLastDayOfLastFourWeeks);
		
		softAssert.assertEquals(WEEKLY, responseInterval, "** Failure! Date interval! Expected: WEEKLY -" + "Actual: " + responseInterval);
		softAssert.assertEquals(responseFirstDayOfLastFourWeeks, expectedFirstDayOfLastFourWeeks, "** Failure! First day of last 4 weeks do not match in reponse. ");
		softAssert.assertEquals(responseLastDayOfLastFourWeeks, expectedLastDayOfLastFourWeeks, "** Failure! Last day of last 4 weeks do not match in reponse. ");
		
		for (int i = 0; i < responseBody.getSum().getSumData().size(); i++) {
			softAssert.assertNotNull(responseBody.getSum().getSumData().get(i), "** Failure! Null value found in response body for Sum data");
		}

		for (int i = 0; i < responseBody.getAmsInfo().get(0).getSeriesInfo().size(); i++) {
			for (int j = 0; j < responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForCriteo().size(); j++) {
				String weeklyData = responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForCriteo().get(j);
				softAssert.assertNotNull(weeklyData, "** Failure! Null value found in response body for weekly data");
			}
		}
		
		softAssert.assertAll();
		LOGGER.info("** Execution for test case (" + tcId + ") has completed successfully"); 
	}
	
	@Test(dataProvider = "ReportingDashboard_CitrusAd", dataProviderClass = MediaApiDataProvider.class, description = "Media reporting dashboard API test for Citrus Ad (Weekly-Last 13 weeks)")
	public void ReportingDashboard_CitrusAd_Api_Test(String tcId, String testDescription, String businessUnit, String client, String headers, String params, String statusCode) throws Exception {

		LOGGER.info("** Media reporting dashboard test case (" + tcId + ") has started.");

		SoftAssert softAssert = new SoftAssert();
		
		String expectedFirstDayOfLastThirteenWeeks = DateUtility.getFirstDayOfLastThirteenWeeks();
		String expectedLastDayOfLastFourWeeks = DateUtility.getLastDayOfLastFourWeeks();

		LOGGER.info("** Deserializing the response");
		ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(WEEKLY, "actual", "direct_sales", SharedMethods.createList("advertisedSkuSales,otherSkuSales"), expectedFirstDayOfLastThirteenWeeks, expectedLastDayOfLastFourWeeks, 534, "CITRUS_AD", null, "ATTR_14D");

		Gson gson = new GsonBuilder().create();
		String reqBody = gson.toJson(rd);

		Response response = CommonApiMethods.callEndpoint(MediaRoutes.getReportingDashboardRoutePath(), "POST", reqBody, headers, (params.equalsIgnoreCase("x") ? "" : params), SecurityAPI.getTokenAPI());

		Assert.assertEquals(response.statusCode(), Integer.parseInt(statusCode), "** Failure! Expected status code: " + statusCode + "-" + "Actual status code: " + response.statusCode());
			
		ReportingDashboardResponseBody responseBody = gson.fromJson(response.getBody().asString(), ReportingDashboardResponseBody.class);
		
		String responseInterval = responseBody.getInterval();
		String responseFirstDayOfLastThirteenWeeks = responseBody.getAmsInfo().get(0).getSeriesInfo().get(0).getDateKey();
		String responseLastDayOfLastFourWeeks = responseBody.getAmsInfo().get(0).getSeriesInfo().get(12).getDateKey();
		expectedFirstDayOfLastThirteenWeeks = DateUtility.formatDate(expectedFirstDayOfLastThirteenWeeks);
		expectedLastDayOfLastFourWeeks = DateUtility.formatDate(expectedLastDayOfLastFourWeeks);
		
		softAssert.assertEquals(WEEKLY, responseInterval, "** Failure! Date interval! Expected: WEEKLY -" + "Actual: " + responseInterval);
		softAssert.assertEquals(responseFirstDayOfLastThirteenWeeks, expectedFirstDayOfLastThirteenWeeks, "** Failure! First day of last 13 weeks do not match in reponse. ");
		softAssert.assertEquals(responseLastDayOfLastFourWeeks, expectedLastDayOfLastFourWeeks, "** Failure! Last day of last 13 weeks do not match in reponse. ");
		
		for (int i = 0; i < responseBody.getSum().getSumData().size(); i++) {
			softAssert.assertNotNull(responseBody.getSum().getSumDataForCitrusAd().get(i), "** Failure! Null value found in response body for Sum data");
		}

		for (int i = 0; i < responseBody.getAmsInfo().get(0).getSeriesInfo().size(); i++) {
			for (int j = 0; j < responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForCitrusAd().size(); j++) {
				String weeklyData = responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForCitrusAd().get(j);
				softAssert.assertNotNull(weeklyData, "** Failure! Null value found in response body for weekly data");
			}
		}
		
		softAssert.assertAll();
		LOGGER.info("** Execution for test case (" + tcId + ") has completed successfully"); 
	}
	
	@Test(dataProvider = "ReportingDashboard_Doordash", dataProviderClass = MediaApiDataProvider.class, description = "Media reporting dashboard API test for Doordash (Daily-Last 30 days)")
	public void ReportingDashboard_Doordash_Api_Test(String tcId, String testDescription, String businessUnit, String client, String headers, String params, String statusCode) throws Exception {

		LOGGER.info("** Media reporting dashboard test case (" + tcId + ") has started.");

		SoftAssert softAssert = new SoftAssert();
		
		String expectedFirstDayOfLastThirtyDays = DateUtility.getFirstDayOfLastThirtyDays();
		String expectedYesterday = DateUtility.getYesterday();

		LOGGER.info("** Deserializing the response");
		ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(DAILY, "actual", "direct_sales", SharedMethods.createList("advertisedSkuSales,otherSkuSales"), expectedFirstDayOfLastThirtyDays, expectedYesterday, 39, "DOORDASH", null, "ATTR_14D");

		Gson gson = new GsonBuilder().create();
		String reqBody = gson.toJson(rd);

		Response response = CommonApiMethods.callEndpoint(MediaRoutes.getReportingDashboardRoutePath(), "POST", reqBody, headers, (params.equalsIgnoreCase("x") ? "" : params), SecurityAPI.getTokenAPI());

		Assert.assertEquals(response.statusCode(), Integer.parseInt(statusCode), "** Failure! Expected status code: " + statusCode + "-" + "Actual status code: " + response.statusCode());
			
		ReportingDashboardResponseBody responseBody = gson.fromJson(response.getBody().asString(), ReportingDashboardResponseBody.class);
		
		String responseInterval = responseBody.getInterval();
		String responseFirstDayOfLastThirtyDays = responseBody.getAmsInfo().get(0).getSeriesInfo().get(0).getDateKey();
		String responseYesterday = responseBody.getAmsInfo().get(0).getSeriesInfo().get(29).getDateKey();
		expectedFirstDayOfLastThirtyDays = DateUtility.formatDate(expectedFirstDayOfLastThirtyDays);
		expectedYesterday = DateUtility.formatDate(expectedYesterday);
		
		softAssert.assertEquals(DAILY, responseInterval, "** Failure! Date interval! Expected: DAILY -" + "Actual: " + responseInterval);
		softAssert.assertEquals(responseFirstDayOfLastThirtyDays, expectedFirstDayOfLastThirtyDays, "** Failure! First day of last 30 days do not match in reponse. ");
		softAssert.assertEquals(responseYesterday, expectedYesterday, "** Failure! Last day of last 30 days do not match in reponse. ");
		
		for (int i = 0; i < responseBody.getSum().getSumData().size(); i++) {
			softAssert.assertNotNull(responseBody.getSum().getSumData().get(i), "** Failure! Null value found in response body for Sum data");
		}

		for (int i = 0; i < responseBody.getAmsInfo().get(0).getSeriesInfo().size(); i++) {
			for (int j = 0; j < responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForDoordash().size(); j++) {
				String dailyData = responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForDoordash().get(j);
				softAssert.assertNotNull(dailyData, "** Failure! Null value found in response body for daily data");
			}
		}
		
		softAssert.assertAll();
		LOGGER.info("** Execution for test case (" + tcId + ") has completed successfully"); 
	}
	
	
	@Test(dataProvider = "ReportingDashboard_Instacart", dataProviderClass = MediaApiDataProvider.class, description = "Media reporting dashboard API test for Instacart (Monthly-Last month)")
	public void ReportingDashboard_Instacart_Api_Test(String tcId, String testDescription, String businessUnit, String client, String headers, String params, String statusCode) throws Exception {

		LOGGER.info("** Media reporting dashboard test case (" + tcId + ") has started.");

		SoftAssert softAssert = new SoftAssert();
		
		String expectedFirstDayOfLastMonth = DateUtility.getFirstDayOfLastMonth();
		String expectedLastDayOfLastMonth = DateUtility.getLastDayOfLastMonth(); 
				
		ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(MONTHLY, "actual", "direct_sales", SharedMethods.createList("advertisedSkuSales,otherSkuSales"), expectedFirstDayOfLastMonth, expectedLastDayOfLastMonth, 39, "INSTACART", null, "ATTR_14D");

		Gson gson = new GsonBuilder().create();
		String reqBody = gson.toJson(rd);

		Response response = CommonApiMethods.callEndpoint(MediaRoutes.getReportingDashboardRoutePath(), "POST", reqBody, headers, (params.equalsIgnoreCase("x") ? "" : params), SecurityAPI.getTokenAPI());

		Assert.assertEquals(response.statusCode(), Integer.parseInt(statusCode), "** Failure! Expected status code: " + statusCode + "-" + "Actual status code: " + response.statusCode());

		LOGGER.info("** Deserializing the response");
		ReportingDashboardResponseBody responseBody = gson.fromJson(response.getBody().asString(), ReportingDashboardResponseBody.class);
		
		String responseInterval = responseBody.getInterval();
		String responseFirstDayOfLastMonth = responseBody.getAmsInfo().get(0).getSeriesInfo().get(0).getDateKey();
		expectedFirstDayOfLastMonth = DateUtility.formatDate(expectedFirstDayOfLastMonth);
		
		softAssert.assertEquals(MONTHLY, responseInterval, "** Failure! Date interval! Expected: MONTHLY -" + "Actual: " + responseInterval);
		softAssert.assertEquals(responseFirstDayOfLastMonth, expectedFirstDayOfLastMonth, "** Failure! First day of last month does not match in reponse. ");
		
		for (int i = 0; i < responseBody.getSum().getSumData().size(); i++) {
			softAssert.assertNotNull(responseBody.getSum().getSumData().get(i), "** Failure! Null value found in response body for Sum data");
		}

		for (int i = 0; i < responseBody.getAmsInfo().get(0).getSeriesInfo().size(); i++) {
			for (int j = 0; j < responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForInstacart().size(); j++) {
				String monthlyData = responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForInstacart().get(j);
				softAssert.assertNotNull(monthlyData, "** Failure! Null value found in response body for monthly data");
			}
		}
		
		softAssert.assertAll();
		LOGGER.info("** Execution for test case (" + tcId + ") has completed successfully"); 
	}
	
	
	@Test(dataProvider = "ReportingDashboard_PromoteIq", dataProviderClass = MediaApiDataProvider.class, description = "Media reporting dashboard API test for Promote IQ (Daily-Last 14 days)")
	public void ReportingDashboard_PromoteIq_Api_Test(String tcId, String testDescription, String businessUnit, String client, String headers, String params, String statusCode) throws Exception {

		LOGGER.info("** Media reporting dashboard test case (" + tcId + ") has started.");

		SoftAssert softAssert = new SoftAssert();
		
		String expectedFirstDayOfLastFourteenDays = DateUtility.getFirstDayOfLastFourteenDays();
		String expectedYesterday = DateUtility.getYesterday();

		ReportingDashboardRequestBody rd = new ReportingDashboardRequestBody(DAILY, "actual", "direct_sales", SharedMethods.createList("advertisedSkuSales,otherSkuSales"), expectedFirstDayOfLastFourteenDays, expectedYesterday, 39, "PROMOTE_IQ", SharedMethods.createList("Kroger"), "ATTR_14D");

		Gson gson = new GsonBuilder().create();
		String reqBody = gson.toJson(rd);

		Response response = CommonApiMethods.callEndpoint(MediaRoutes.getReportingDashboardRoutePath(), "POST", reqBody, headers, (params.equalsIgnoreCase("x") ? "" : params), SecurityAPI.getTokenAPI());

		Assert.assertEquals(response.statusCode(), Integer.parseInt(statusCode), "** Failure! Expected status code: " + statusCode + "-" + "Actual status code: " + response.statusCode());

		LOGGER.info("** Deserializing the response");
		ReportingDashboardResponseBody responseBody = gson.fromJson(response.getBody().asString(), ReportingDashboardResponseBody.class);
		
		String responseInterval = responseBody.getInterval();
		String responseFirstDayOfLastFourteenDays = responseBody.getAmsInfo().get(0).getSeriesInfo().get(0).getDateKey();
		String responseYesterday = responseBody.getAmsInfo().get(0).getSeriesInfo().get(13).getDateKey();
		expectedFirstDayOfLastFourteenDays = DateUtility.formatDate(expectedFirstDayOfLastFourteenDays);
		expectedYesterday = DateUtility.formatDate(expectedYesterday);
		
		softAssert.assertEquals(DAILY, responseInterval, "** Failure! Date interval! Expected: DAILY -" + "Actual: " + responseInterval);
		softAssert.assertEquals(responseFirstDayOfLastFourteenDays, expectedFirstDayOfLastFourteenDays, "** Failure! First day of last 14 days do not match in reponse. ");
		softAssert.assertEquals(responseYesterday, expectedYesterday, "** Failure! Last day of last 14 days do not match in reponse. ");

		for (int i = 0; i < responseBody.getSum().getSumData().size(); i++) {
			softAssert.assertNotNull(responseBody.getSum().getSumData().get(i), "** Failure! Null value found in response body for Sum data");
		}

		for (int i = 0; i < responseBody.getAmsInfo().get(0).getSeriesInfo().size(); i++) {
			for (int j = 0; j < responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForPromoteIq().size(); j++) {
				String dailyData = responseBody.getAmsInfo().get(0).getSeriesInfo().get(i).getDataForPromoteIq().get(j);
				softAssert.assertNotNull(dailyData, "** Failure! Null value found in response body for daily data");
			}
		}
		
		softAssert.assertAll();
		LOGGER.info("** Execution for test case (" + tcId + ") has completed successfully"); 
	}
	
}