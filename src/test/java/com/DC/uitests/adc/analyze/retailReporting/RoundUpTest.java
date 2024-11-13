package com.DC.uitests.adc.analyze.retailReporting;

import com.DC.db.analyze.RoundUpDbFunctions;
import com.DC.pageobjects.adc.AppHomepage;
import com.DC.pageobjects.adc.DCLoginPage;
import com.DC.pageobjects.adc.analyze.retailReporting.RoundUpPage;
import com.DC.pageobjects.filters.DCFilters;
import com.DC.testcases.BaseClass;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail.RetailApiRequests;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.RoundupRequestBody;
import com.DC.utilities.sharedElements.DateAndIntervalPickerPage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.sql.SQLException;
import java.util.*;

public class RoundUpTest extends BaseClass {

    String authToken;
    DCLoginPage loginPage;
    AppHomepage homePage;
    DCFilters dcFilters;
    RoundUpDbFunctions dbFunctions;
    RoundUpPage roundUpPage;
    DateAndIntervalPickerPage datePage;
    int bu1Id = 198;
    private final Map<String, String> RGBA_MAP = new LinkedHashMap<>() {{ put("rtsColor", "rgba(100,150,204,0.75)"); put("spApiColor", "rgba(2,64,132,0.75)");}};

    @BeforeClass()
    public void setUp(ITestContext testContext) throws InterruptedException, SQLException {
        dbFunctions = new RoundUpDbFunctions();
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        loginPage = new DCLoginPage(driver);
        loginPage.openLoginPage(driver, READ_CONFIG.getDcAppUrl());
        loginPage.loginDcApp(READ_CONFIG.getHubFilaUserEmail(), READ_CONFIG.getHubFilaUserPassword());
        homePage = new AppHomepage(driver);
        dcFilters = new DCFilters(driver);
        authToken = "Bearer " + SecurityAPI.getAuthToken(driver);
        homePage.openPage("Analyze", "Round Up");
        roundUpPage = new RoundUpPage(driver);
        datePage = new DateAndIntervalPickerPage(driver);
        roundUpPage.selectBU("McCormick");
    }

    @Test(groups = "realtime", description = "RAR-549 - Real Time Sales - Hourly Data Show Faded in Graph")
    public void RoundUp_Real_Time_Sales_Hourly_Data_Show_Faded_In_Graph_Test() throws Exception {
        datePage.selectInterval("Hourly");
        roundUpPage.clickApplyButton();
        Assert.assertTrue(roundUpPage.realTimeSalesHourlyGraphShowsInColor(RGBA_MAP.get("rtsColor")), "RTS hourly graph is not showing in faded color.");
    }

    @Test(groups = "realtime", description = "RAR-549 - Real Time Sales - Daily Data Show Faded in Graph")
    public void RoundUp_Real_Time_Sales_Daily_Data_Show_Faded_In_Graph_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        RoundupRequestBody roundUpAllDailyReqBody = new RoundupRequestBody(null, bu1Id, "DAILY", DateUtility.getDayBeforeToday(15), DateUtility.getTodayDate(), null, null,"amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject response = RetailApiRequests.roundupAllJson(roundUpAllDailyReqBody, authToken);
        JSONArray popWidgetObjects = response.getJSONArray("popWidget");

        JSONObject nonRealtimeSalesDailyApi = RetailApiRequests.getAllNonRealTimeSalesObjects(popWidgetObjects).get(0);
        int latestDaySpApiAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(nonRealtimeSalesDailyApi.getInt("year"), nonRealtimeSalesDailyApi.getInt("month"), nonRealtimeSalesDailyApi.getInt("day")));

        List<JSONObject> realTimeSalesDailyDataApi = RetailApiRequests.getAllRealTimeSalesObjects(popWidgetObjects);
        if (!realTimeSalesDailyDataApi.isEmpty()) {
            JSONObject realTimeSalesDataLatestDayApi = realTimeSalesDailyDataApi.get(0);
            int latestDayRealTimeDailyAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(realTimeSalesDataLatestDayApi.getInt("year"), realTimeSalesDataLatestDayApi.getInt("month"), realTimeSalesDataLatestDayApi.getInt("day")));

            datePage.selectInterval("Daily");
            roundUpPage.selectPeriod("Last 7 Days");
            roundUpPage.selectRoundUpDateRange(latestDaySpApiAvailableApi, latestDayRealTimeDailyAvailableApi);
            dcFilters.selectCogsUnitsRevenue("Ordered Units");
            roundUpPage.clickApplyButton();

            softAssert.assertTrue(roundUpPage.realTimeSalesHourlyGraphShowsInColor(RGBA_MAP.get("spApiColor")), "SP Api available but not showing on graph.");
            softAssert.assertTrue(roundUpPage.realTimeSalesHourlyGraphShowsInColor(RGBA_MAP.get("rtsColor")), "Daily RTS available but not showing on graph.");
        }
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-549 - Real Time Sales - Weekly Data Show Faded in Graph")
    public void RoundUp_Real_Time_Sales_Weekly_Data_Show_Faded_In_Graph_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        RoundupRequestBody roundUpAllWeeklyReqBody = new RoundupRequestBody(null, bu1Id, "WEEKLY", DateUtility.getDayBeforeToday(45), DateUtility.getTodayDate(), null, null,"amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject responseWeekly = RetailApiRequests.roundupAllJson(roundUpAllWeeklyReqBody, authToken);
        JSONArray popWidgetObjects = responseWeekly.getJSONArray("popWidget");

        JSONObject nonRealtimeSalesWeeklyApi = RetailApiRequests.getAllNonRealTimeSalesObjects(popWidgetObjects).get(0);
        int latestWeekSpApiAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(nonRealtimeSalesWeeklyApi.getInt("year"), nonRealtimeSalesWeeklyApi.getInt("month"), nonRealtimeSalesWeeklyApi.getInt("day")));

        List<JSONObject> realTimeSalesDailyDataApi = RetailApiRequests.getAllRealTimeSalesObjects(popWidgetObjects);
        if (!realTimeSalesDailyDataApi.isEmpty()) {
            JSONObject realTimeSalesDataLatestDayApi = realTimeSalesDailyDataApi.get(0);
            int latestDayRealTimeWeeklyAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(realTimeSalesDataLatestDayApi.getInt("year"), realTimeSalesDataLatestDayApi.getInt("month"), realTimeSalesDataLatestDayApi.getInt("day")));

            datePage.selectInterval("Weekly");
            roundUpPage.selectPeriod("Last Week");
            roundUpPage.selectRoundUpDateRange(latestWeekSpApiAvailableApi, DateUtility.addDaysToDate(latestDayRealTimeWeeklyAvailableApi, 6));
            dcFilters.selectCogsUnitsRevenue("Ordered Revenue");
            roundUpPage.clickApplyButton();

            softAssert.assertTrue(roundUpPage.realTimeSalesHourlyGraphShowsInColor(RGBA_MAP.get("spApiColor")), "SP Api available but not showing on graph.");
            softAssert.assertTrue(roundUpPage.realTimeSalesHourlyGraphShowsInColor(RGBA_MAP.get("rtsColor")), "Daily RTS available but not showing on graph.");
        }
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-549 - Real Time Sales - Monthly Data Show Faded in Graph")
    public void RoundUp_Real_Time_Sales_Monthly_Data_Show_Faded_In_Graph_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        RoundupRequestBody roundUpAllMonthlyReqBody = new RoundupRequestBody(null, bu1Id, "MONTHLY", DateUtility.getDayBeforeToday(90), DateUtility.getTodayDate(), null, null,"amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject responseMonthly = RetailApiRequests.roundupAllJson(roundUpAllMonthlyReqBody, authToken);
        JSONArray popWidgetObjects = responseMonthly.getJSONArray("popWidget");

        JSONObject nonRealtimeSalesMonthlyApi = RetailApiRequests.getAllNonRealTimeSalesObjects(popWidgetObjects).get(0);
        int latestMonthlySpApiAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(nonRealtimeSalesMonthlyApi.getInt("year"), nonRealtimeSalesMonthlyApi.getInt("month"), nonRealtimeSalesMonthlyApi.getInt("day")));

        List<JSONObject> realTimeSalesMonthlyDataApi = RetailApiRequests.getAllRealTimeSalesObjects(popWidgetObjects);
        if (!realTimeSalesMonthlyDataApi.isEmpty()){
            JSONObject realTimeSalesDataLatestDayApi = realTimeSalesMonthlyDataApi.get(0);
            int latestDayRealTimeMonthlyAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(realTimeSalesDataLatestDayApi.getInt("year"), realTimeSalesDataLatestDayApi.getInt("month"), realTimeSalesDataLatestDayApi.getInt("day")));

            datePage.selectInterval("Monthly");
            roundUpPage.selectPeriod("Last Month");
            roundUpPage.selectRoundUpDateRange(latestMonthlySpApiAvailableApi, DateUtility.getLastDayOfMonth(latestDayRealTimeMonthlyAvailableApi));
            dcFilters.selectCogsUnitsRevenue("Ordered Units");
            roundUpPage.clickApplyButton();

            softAssert.assertTrue(roundUpPage.realTimeSalesHourlyGraphShowsInColor(RGBA_MAP.get("spApiColor")), "SP Api available but not showing on graph.");
            softAssert.assertTrue(roundUpPage.realTimeSalesHourlyGraphShowsInColor(RGBA_MAP.get("rtsColor")), "Daily RTS available but not showing on graph.");
        }
        softAssert.assertAll();
    }
}