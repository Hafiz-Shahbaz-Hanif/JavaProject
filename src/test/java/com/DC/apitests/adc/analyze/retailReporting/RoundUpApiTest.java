package com.DC.apitests.adc.analyze.retailReporting;

import com.DC.db.analyze.RoundUpDbFunctions;
import com.DC.testcases.BaseClass;
import com.DC.utilities.CsvUtility;
import com.DC.utilities.DateUtility;
import com.DC.utilities.SecurityAPI;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.adc.catalog.retail.RetailApiRequests;
import com.DC.utilities.apiEngine.models.requests.adc.catalog.retail.RoundupRequestBody;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.util.*;

public class RoundUpApiTest extends BaseClass {

    String authToken;
    RoundUpDbFunctions dbFunctions;
    int bu1Id = 198;
    int bu2Id = 354;

    @BeforeClass()
    public void setUp(ITestContext testContext) throws InterruptedException {
        PropertyConfigurator.configure("log4j.properties");
        driver = initializeBrowser(testContext, READ_CONFIG.getHeadlessMode());
        dbFunctions = new RoundUpDbFunctions();
        authToken = "Bearer " + SecurityAPI.loginToDcAppToGetAuthToken(driver, READ_CONFIG.getHubFilaUserEmail(), READ_CONFIG.getHubFilaUserPassword());
    }

    @Test(groups = "realtime", description = "RAR-534 - Real Time Sales Hourly Data - Graph - Ordered Units")
    public void Api_RoundUp_Real_Time_Sales_Data_Ordered_Units_Hourly_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String today = DateUtility.getTodayDate();
        Map<String, Object> latestDate = dbFunctions.getLatestDayFullHourlyDataAvailable(DateUtility.convertDateToInt(today), bu1Id);
        String endDate = DateUtility.calculateDate((Integer) latestDate.get("year"), (Integer) latestDate.get("dayofyear"));
        String startDate = DateUtility.subtractDaysFromDate(endDate, 1);

        RoundupRequestBody roundUpAllHourlyReqBody = new RoundupRequestBody(null, bu1Id, "HOURLY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject response = RetailApiRequests.roundupAllJson(roundUpAllHourlyReqBody, authToken);
        JSONArray popWidgetObjects = response.getJSONArray("popWidget");

        List<JSONObject> dayOne = RetailApiRequests.getHourlyObjectsByDay(popWidgetObjects, DateUtility.getDayOfDate(startDate));
        softAssert.assertEquals(dayOne.size(), 24, "Missing hourly data for day: " + startDate);
        List<JSONObject> dayTwo = RetailApiRequests.getHourlyObjectsByDay(popWidgetObjects, DateUtility.getDayOfDate(endDate));
        softAssert.assertEquals(dayTwo.size(), 24, "Missing hourly data for day: " + endDate);
        dayOne.addAll(dayTwo);

        for (int i = 0; i < dayOne.size(); i++) {
            JSONObject hourlyData = dayOne.get(i);
            try {
                hourlyData.getInt("current");
            } catch (Exception e) {
                softAssert.fail("No hourly data for " + hourlyData.getInt("year") + "-" + hourlyData.getInt("month") + "-" + hourlyData.getInt("day") + " and hour " + hourlyData.getInt("hour"));
            }
        }

        Map<String, String> hourlyData = dbFunctions.getSumOfHourlyData(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(endDate), bu1Id);
        int sumOfHourlyDataDb = Integer.parseInt(hourlyData.get("ordered_units"));
        int sumOfHourlyDataApi = response.getJSONObject("metricAndAspWidget").getJSONObject("metric").getInt("current");
        softAssert.assertEquals(sumOfHourlyDataApi, sumOfHourlyDataDb, "Sum of hourly data is not matching for " + startDate + " and " + endDate);

        JSONObject latestHourlyDataApi = dayOne.get(0);
        int date = DateUtility.convertDateToInt(DateUtility.createDate(latestHourlyDataApi.getInt("year"), latestHourlyDataApi.getInt("month"), latestHourlyDataApi.getInt("day")));
        int hour = latestHourlyDataApi.getInt("hour");
        int unitApi = latestHourlyDataApi.getInt("current");
        double avgSellingPriceApi = latestHourlyDataApi.getDouble("averageSellingPrice");

        Map<String, String> hourlyDataDb = dbFunctions.getRealTimeSalesHourlyData(date, bu1Id, hour);
        int unitDb = Integer.valueOf(hourlyDataDb.get("units"));
        double avgSellingPriceDb = Double.valueOf(hourlyDataDb.get("averagesellingprice"));

        softAssert.assertEquals(unitApi, unitDb, "Unit count is not matching for " + startDate + " and hour " + hour);
        softAssert.assertEquals(avgSellingPriceApi, avgSellingPriceDb, "Avg selling price is not matching for " + startDate + " and hour " + hour);
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-534 - Real Time Sales Hourly Data - Graph - Ordered Revenue")
    public void Api_RoundUp_Real_Time_Sales_Data_Ordered_Revenue_Hourly_Test() throws Exception {
        String today = DateUtility.getTodayDate();
        Map<String, Object> latestDate = dbFunctions.getLatestDayFullHourlyDataAvailable(DateUtility.convertDateToInt(today), bu1Id);
        String endDate = DateUtility.calculateDate((Integer) latestDate.get("year"), (Integer) latestDate.get("dayofyear"));
        String startDate = DateUtility.subtractDaysFromDate(endDate, 1);

        RoundupRequestBody roundUpAllHourlyReqBody = new RoundupRequestBody(null, bu1Id, "HOURLY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_REVENUE");

        JSONObject response = RetailApiRequests.roundupAllJson(roundUpAllHourlyReqBody, authToken);
        JSONArray popWidgetObjects = response.getJSONArray("popWidget");

        List<JSONObject> allHoursInLatestDay = RetailApiRequests.getHourlyObjectsByDay(popWidgetObjects, DateUtility.getDayOfDate(startDate));
        JSONObject latestHourlyDataApi = allHoursInLatestDay.get(0);
        int date = DateUtility.convertDateToInt(DateUtility.createDate(latestHourlyDataApi.getInt("year"), latestHourlyDataApi.getInt("month"), latestHourlyDataApi.getInt("day")));
        int hour = latestHourlyDataApi.getInt("hour");
        double orderedRevenueApi = latestHourlyDataApi.getDouble("current");

        Map<String, String> hourlyDataDb = dbFunctions.getRealTimeSalesHourlyData(date, bu1Id, hour);
        double orderedRevenueDb = Double.valueOf(hourlyDataDb.get("revenue"));
        Assert.assertEquals(orderedRevenueApi, orderedRevenueDb, "Ordered revenue is not matching for the hour " + hour);
    }

    @Test(groups = "realtime", description = "RAR-535 - Real Time Sales Daily Data - Graph - Ordered Units")
    public void Api_RoundUp_Real_Time_Sales_Data_Daily_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String startDate = DateUtility.getDayBeforeToday(15);
        String endDate = DateUtility.getTodayDate();

        RoundupRequestBody roundUpAllDailyReqBody = new RoundupRequestBody(null, bu1Id, "DAILY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject response = RetailApiRequests.roundupAllJson(roundUpAllDailyReqBody, authToken);
        JSONArray popWidgetObjects = response.getJSONArray("popWidget");

        JSONObject nonRealtimeSalesDailyApi = RetailApiRequests.getAllNonRealTimeSalesObjects(popWidgetObjects).get(0);
        int latestDaySpApiAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(nonRealtimeSalesDailyApi.getInt("year"), nonRealtimeSalesDailyApi.getInt("month"), nonRealtimeSalesDailyApi.getInt("day")));
        int latestDaySpApiAvailableDb = dbFunctions.getLatestDayDataAvailableFromSource(bu1Id, "SP API Ingestion");
        softAssert.assertEquals(latestDaySpApiAvailableApi, latestDaySpApiAvailableDb, "Latest day for SP API is not matching.");

        List<JSONObject> realTimeSalesDailyDataApi = RetailApiRequests.getAllRealTimeSalesObjects(popWidgetObjects);
        if (realTimeSalesDailyDataApi.size() > 0) {
            JSONObject realTimeSalesDataLatestDayApi = realTimeSalesDailyDataApi.get(0);
            int latestDayRealTimeDailyAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(realTimeSalesDataLatestDayApi.getInt("year"), realTimeSalesDataLatestDayApi.getInt("month"), realTimeSalesDataLatestDayApi.getInt("day")));
            int latestDayRealTimeDailyAvailableDb = dbFunctions.getLatestDayDataAvailableFromSource(bu1Id, "REAL TIME INGESTION");
            softAssert.assertEquals(latestDayRealTimeDailyAvailableApi, latestDayRealTimeDailyAvailableDb, "Latest day for REAL TIME INGESTION is not matching.");

            Map<String, String> realTimeSalesDailyDataDb = dbFunctions.getRealTimeSalesDailyData(latestDayRealTimeDailyAvailableApi, bu1Id);
            int orderedUnitsDailyDb = Integer.valueOf(realTimeSalesDailyDataDb.get("ordered_units"));

            List<Integer> hourlyOrderedUnitesForDayDb = dbFunctions.getRealTimeSalesHourlyData(latestDayRealTimeDailyAvailableApi, bu1Id);
            int totalHourlyOrderedUnitesForDayDb = SharedMethods.sumList(hourlyOrderedUnitesForDayDb);
            softAssert.assertTrue(hourlyOrderedUnitesForDayDb.size() == 24, "A day's hourly data is not available in the database for " + latestDayRealTimeDailyAvailableApi);
            softAssert.assertEquals(totalHourlyOrderedUnitesForDayDb, orderedUnitsDailyDb, "Hourly total and daily unit count is not matching for day " + latestDayRealTimeDailyAvailableApi);
        } else {
            softAssert.assertEquals(dbFunctions.getLatestDayDataAvailableFromSource(bu1Id, "REAL TIME INGESTION"), 0);
        }
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-535 - Real Time Sales Weekly Data - Graph - Ordered Units")
    public void Api_RoundUp_Real_Time_Sales_Data_Weekly_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String startDate = DateUtility.getDayBeforeToday(45);
        String endDate = DateUtility.getTodayDate();

        RoundupRequestBody roundUpAllWeeklyReqBody = new RoundupRequestBody(null, bu1Id, "WEEKLY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject responseWeekly = RetailApiRequests.roundupAllJson(roundUpAllWeeklyReqBody, authToken);
        JSONArray popWidgetObjects = responseWeekly.getJSONArray("popWidget");

        JSONObject nonRealtimeSalesWeeklyApi = RetailApiRequests.getAllNonRealTimeSalesObjects(popWidgetObjects).get(0);
        int latestWeekSpApiAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(nonRealtimeSalesWeeklyApi.getInt("year"), nonRealtimeSalesWeeklyApi.getInt("month"), nonRealtimeSalesWeeklyApi.getInt("day")));
        int latestWeekSpApiAvailableDb = dbFunctions.getLatestWeekDataAvailableFromSource(bu1Id, "SP API Ingestion");
        softAssert.assertEquals(latestWeekSpApiAvailableApi, latestWeekSpApiAvailableDb, "Latest week for SP API is not matching.");

        List<JSONObject> realTimeSalesWeeklyDataApi = RetailApiRequests.getAllRealTimeSalesObjects(popWidgetObjects);
        if (realTimeSalesWeeklyDataApi.size() > 0) {
            JSONObject realTimeSalesDataLatestWeekApi = realTimeSalesWeeklyDataApi.get(0);
            int latestWeekRealTimeDailyAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(realTimeSalesDataLatestWeekApi.getInt("year"), realTimeSalesDataLatestWeekApi.getInt("month"), realTimeSalesDataLatestWeekApi.getInt("day")));
            int latestWeekRealTimeDailyAvailableDb = dbFunctions.getLatestWeekDataAvailableFromSource(bu1Id, "REAL TIME INGESTION");
            softAssert.assertEquals(latestWeekRealTimeDailyAvailableApi, latestWeekRealTimeDailyAvailableDb, "Latest week for SP API is not matching.");

            int orderedUnitLatestWeekApi = realTimeSalesDataLatestWeekApi.getInt("current");
            int orderedUnitLatestWeekDb = dbFunctions.getRealTimeSalesOrderedUnitsForLatestWeek(bu1Id);
            softAssert.assertEquals(orderedUnitLatestWeekApi, orderedUnitLatestWeekDb, "Ordered units for latest week is not matching.");
        } else {
            softAssert.assertEquals(dbFunctions.getLatestWeekDataAvailableFromSource(bu1Id, "REAL TIME INGESTION"), 0);
        }
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-535 - Real Time Sales Monthly Data - Graph - Ordered Units")
    public void Api_RoundUp_Real_Time_Sales_Data_Monthly_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String startDate = DateUtility.getDayBeforeToday(90);
        String endDate = DateUtility.getTodayDate();

        RoundupRequestBody roundUpAllMonthlyReqBody = new RoundupRequestBody(null, bu1Id, "MONTHLY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject responseMonthly = RetailApiRequests.roundupAllJson(roundUpAllMonthlyReqBody, authToken);
        JSONArray popWidgetObjects = responseMonthly.getJSONArray("popWidget");

        JSONObject nonRealtimeSalesMonthlyApi = RetailApiRequests.getAllNonRealTimeSalesObjects(popWidgetObjects).get(0);
        int latestMonthSpApiAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(nonRealtimeSalesMonthlyApi.getInt("year"), nonRealtimeSalesMonthlyApi.getInt("month"), nonRealtimeSalesMonthlyApi.getInt("day")));
        int latestMonthSpApiAvailableDb = dbFunctions.getLatestMonthSpApiAvailable(bu1Id, "SP API Ingestion");
        softAssert.assertEquals(latestMonthSpApiAvailableApi, latestMonthSpApiAvailableDb, "Latest month for SP API is not matching.");

        List<JSONObject> realTimeSalesMonthlyDataApi = RetailApiRequests.getAllRealTimeSalesObjects(popWidgetObjects);
        if (realTimeSalesMonthlyDataApi.size() > 0) {
            JSONObject realTimeSalesDataLatestMonthApi = realTimeSalesMonthlyDataApi.get(0);
            int latestMonthRealTimeDailyAvailableApi = DateUtility.convertDateToInt(DateUtility.createDate(realTimeSalesDataLatestMonthApi.getInt("year"), realTimeSalesDataLatestMonthApi.getInt("month"), realTimeSalesDataLatestMonthApi.getInt("day")));
            int latestMonthRealTimeDailyAvailableDb = dbFunctions.getLatestMonthSpApiAvailable(bu1Id, "REAL TIME INGESTION");
            softAssert.assertEquals(latestMonthRealTimeDailyAvailableApi, latestMonthRealTimeDailyAvailableDb, "Latest month for SP API is not matching.");

            int orderedUnitLatestMonthApi = realTimeSalesDataLatestMonthApi.getInt("current");
            int orderedUnitLatestMonthDb = dbFunctions.getRealTimeSalesOrderedUnitsForLatestMonth(bu1Id);
            softAssert.assertEquals(orderedUnitLatestMonthApi, orderedUnitLatestMonthDb, "Ordered units for latest month is not matching.");
        } else {
            softAssert.assertEquals(dbFunctions.getLatestMonthSpApiAvailable(bu1Id, "REAL TIME INGESTION"), 0);
        }
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-540 - Real Time Sales Daily Data - Product Grid - Ordered Revenue")
    public void Api_RoundUp_Real_Time_Sales_Data_Product_Grid_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String startDate = DateUtility.getDayBeforeToday(15);
        String endDate = DateUtility.getTodayDate();

        int asinId = dbFunctions.getAsinWithOrderedUnits(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(endDate), bu1Id);

        RoundupRequestBody.PagingAttributes page = new RoundupRequestBody.PagingAttributes(100,
                1, "current", false);

        RoundupRequestBody roundUpAllDailyReqBody = new RoundupRequestBody(page, bu1Id, "DAILY", startDate, endDate, null, Arrays.asList(String.valueOf(asinId)), "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_REVENUE");

        JSONObject response = RetailApiRequests.roundupProductJson(roundUpAllDailyReqBody, authToken);
        JSONArray items = response.getJSONArray("items");

        JSONObject item = RetailApiRequests.getAllNonRealTimeSalesObjects(items).get(0);
        int orderedRevenueForAsin = (int) item.getDouble("current");

        double rtsOrderedRevenue = dbFunctions.getRealTimeSalesDailyOrderedRevenue(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(endDate), asinId, bu1Id, "REAL TIME INGESTION");
        double spApiOrderedRevenue = dbFunctions.getRealTimeSalesDailyOrderedRevenue(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(endDate), asinId, bu1Id, "SP API Ingestion");
        int totalOrderedRevenue = (int) (rtsOrderedRevenue + spApiOrderedRevenue);

        if (rtsOrderedRevenue == 0.0) {
            LOGGER.info("Real time sales data is not available for this ASIN.");
        }

        softAssert.assertEquals(orderedRevenueForAsin, totalOrderedRevenue, "Ordered revenue for ASIN is not matching.");
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-539 - Real Time Sales Daily Data - Segment, Category, SubCategory Grids - Ordered Units")
    public void Api_RoundUp_Real_Time_Sales_Data_Segment_Category_SubCategory_Grids_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String startDate = DateUtility.getDayBeforeToday(15);

        RoundupRequestBody.SegmentationFilters segFilters = new RoundupRequestBody.SegmentationFilters(2857, Arrays.asList(136458));
        RoundupRequestBody roundUpAllDailyReqBody = new RoundupRequestBody(null, bu1Id, "HOURLY", startDate, startDate, Arrays.asList(segFilters), null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject response = RetailApiRequests.roundupSegmentationJson(roundUpAllDailyReqBody, authToken);
        JSONArray segments = response.getJSONArray("segment");
        JSONObject segment = segments.getJSONObject(0);
        int orderedUnitForSegmentApi = segment.getInt("current");

        JSONArray categories = response.getJSONArray("category");
        JSONObject category = categories.getJSONObject(0);
        int orderedUnitForCategoryApi = category.getInt("current");

        JSONArray subCategories = response.getJSONArray("subCategory");
        JSONObject subCategory = subCategories.getJSONObject(0);
        int orderedUnitForSubCategoryApi = subCategory.getInt("current");

        int orderedUnitForSegmentDb = dbFunctions.getRealTimeSalesHourlyOrderedUnitsForSegment(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(startDate), "Core H&S", bu1Id);
        softAssert.assertEquals(orderedUnitForSegmentApi, orderedUnitForSegmentDb, "Ordered units for segment is not matching.");
        softAssert.assertEquals(orderedUnitForCategoryApi, orderedUnitForSegmentDb, "Ordered units for category is not matching.");
        softAssert.assertEquals(orderedUnitForSubCategoryApi, orderedUnitForSegmentDb, "Ordered units for subcategory is not matching.");
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-537 - Real Time Sales - Single BU - Hourly - Export by ASIN by Date")
    public void Api_RoundUp_Real_Time_Sales_Single_Bu_Hourly_Export_By_Asin_By_Date_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        String today = DateUtility.getTodayDate();
        Map<String, Object> latestDate = dbFunctions.getLatestDayFullHourlyDataAvailable(DateUtility.convertDateToInt(today), bu1Id);
        String endDate = DateUtility.calculateDate((Integer) latestDate.get("year"), (Integer) latestDate.get("dayofyear"));
        String startDate = DateUtility.subtractDaysFromDate(endDate, 1);

        RoundupRequestBody roundUpExportByAsinByDateReqBody = new RoundupRequestBody(bu1Id, "HOURLY", startDate, endDate, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS", "DETAIL");

        JSONObject response = RetailApiRequests.roundupExportByAsinByDateJson(roundUpExportByAsinByDateReqBody, authToken);
        String exportUrl = response.getString("fileUrl");
        File file = SharedMethods.importFileFromUrl(exportUrl, downloadFolder + "/roundup.csv");
        String filePath = file.getPath();

        int totalHourlyRecordsCsv = CsvUtility.getRowCount(filePath) - 2;
        int totalHourlyRecordsDb = dbFunctions.getTotalHourlyRecords(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(endDate), bu1Id);
        verifyCsvDbRecords(softAssert, totalHourlyRecordsCsv, totalHourlyRecordsDb, startDate, endDate);
        int randomRow = SharedMethods.getRandomNumberBetweenRange(2, totalHourlyRecordsCsv);

        String startDateCsv = CsvUtility.getCellContent(filePath, randomRow, "Start Date");
        String hourCsv = CsvUtility.getCellContent(filePath, randomRow, "Hour of the Day");
        String asinCsv = CsvUtility.getCellContent(filePath, randomRow, "ASIN");
        String orderedUnits = CsvUtility.getCellContent(filePath, randomRow, "Ordered Units");
        int orderedUnitsCsv = (int) Double.parseDouble(orderedUnits);

        Map<String, String> hourlyDataByAsin = dbFunctions.getRealTimeSalesHourlyDataByAsinByBuId(DateUtility.convertDateToInt(startDateCsv), asinCsv, bu1Id, Integer.parseInt(hourCsv));
        int orderedUnitsDb = Integer.parseInt(hourlyDataByAsin.get("units"));
        softAssert.assertEquals(orderedUnitsCsv, orderedUnitsDb, "CSV and DB ordered units for ASIN not matching. Date:" + startDateCsv + "- ASIN:" + asinCsv);
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-537 - Real Time Sales - Multi BU - Hourly - Export by ASIN by Date")
    public void Api_RoundUp_Real_Time_Sales_Multi_Bu_Hourly_Export_By_Asin_By_Date_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        String today = DateUtility.getTodayDate();
        Map<String, Object> latestDate = dbFunctions.getLatestDayFullHourlyDataAvailable(DateUtility.convertDateToInt(today), bu1Id);
        String endDate = DateUtility.calculateDate((Integer) latestDate.get("year"), (Integer) latestDate.get("dayofyear"));
        String startDate = DateUtility.subtractDaysFromDate(endDate, 1);

        RoundupRequestBody roundUpExportByAsinByDateReqBody = new RoundupRequestBody(bu1Id, "HOURLY", startDate, endDate, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS", "DETAIL");

        JSONObject response = RetailApiRequests.roundupMultiBuExportByAsinByDateJson(roundUpExportByAsinByDateReqBody, bu1Id, bu2Id, "USD", authToken);
        String exportUrl = response.getString("fileUrl");
        File file = SharedMethods.importFileFromUrl(exportUrl, downloadFolder + "/roundup.csv");
        String filePath = file.getPath();

        int totalHourlyRecordsCsv = CsvUtility.getRowCount(filePath) - 2;
        int totalHourlyRecordsDb = dbFunctions.getMultiBuTotalHourlyRecords(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(endDate), bu1Id, bu2Id);
        verifyCsvDbRecords(softAssert, totalHourlyRecordsCsv, totalHourlyRecordsDb, startDate, endDate);
        int randomRow = SharedMethods.getRandomNumberBetweenRange(2, totalHourlyRecordsCsv);

        String startDateCsv = CsvUtility.getCellContent(filePath, randomRow, "Start Date");
        String hourCsv = CsvUtility.getCellContent(filePath, randomRow, "Hour of the Day");
        String asinCsv = CsvUtility.getCellContent(filePath, randomRow, "ASIN");
        String orderedUnits = CsvUtility.getCellContent(filePath, randomRow, "Ordered Units");
        String buName = CsvUtility.getCellContent(filePath, randomRow, "Business Unit Name");
        int orderedUnitsCsv = (int) Double.parseDouble(orderedUnits);
        List<String> buUnitNames = CsvUtility.getAllCellValuesInColumn(filePath, "Business Unit Name");
        softAssert.assertEquals(buUnitNames.size(), 2, "More or less than 2 BUs in Csv file.");

        Map<String, String> hourlyDataByAsin = dbFunctions.getRealTimeSalesMultiBuHourlyDataByAsinByBuName(DateUtility.convertDateToInt(startDateCsv), asinCsv, buName, Integer.parseInt(hourCsv));
        int orderedUnitsDb = Integer.parseInt(hourlyDataByAsin.get("units"));
        softAssert.assertEquals(orderedUnitsCsv, orderedUnitsDb, "CSV and DB ordered units for ASIN not matching. Date:" + startDateCsv + "- ASIN:" + asinCsv);
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-536 - Real Time Sales - Single BU - Export by ASIN")
    public void Api_RoundUp_Real_Time_Sales_Single_Bu_Hourly_Export_By_Asin_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        String today = DateUtility.getTodayDate();
        Map<String, Object> latestDate = dbFunctions.getLatestDayFullHourlyDataAvailable(DateUtility.convertDateToInt(today), bu1Id);
        String endDate = DateUtility.calculateDate((Integer) latestDate.get("year"), (Integer) latestDate.get("dayofyear"));
        String startDate = DateUtility.subtractDaysFromDate(endDate, 1);

        RoundupRequestBody roundUpExportByAsinReqBody = new RoundupRequestBody(bu1Id, "HOURLY", startDate, startDate, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS", "SUMMARY");

        JSONObject response = RetailApiRequests.roundupExportByAsinByDateJson(roundUpExportByAsinReqBody, authToken);
        String exportUrl = response.getString("fileUrl");
        File file = SharedMethods.importFileFromUrl(exportUrl, downloadFolder + "/roundup.csv");
        String filePath = file.getPath();

        int totalHourlyRecordsCsv = CsvUtility.countNonEmptyRowsInColumn(filePath, "Ordered Revenue");
        int totalHourlyRecordsDb = dbFunctions.getTotalHourlyRecordsByAsin(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(startDate), bu1Id);
        verifyCsvDbRecords(softAssert, totalHourlyRecordsCsv, totalHourlyRecordsDb, startDate, endDate);
        int randomRow = SharedMethods.getRandomNumberBetweenRange(3, totalHourlyRecordsCsv);

        String asinCsv = CsvUtility.getCellContent(filePath, randomRow, "ASIN");
        String orderedUnits = CsvUtility.getCellContent(filePath, randomRow, "Ordered Units");
        int orderedUnitsCsv = (int) Double.parseDouble(orderedUnits);

        Map<String, String> fullDayHourlyDataByAsin = dbFunctions.getRealTimeSalesHourlyDataForFullDayByAsin(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(startDate), asinCsv, bu1Id);
        int orderedUnitsDb = Integer.parseInt(fullDayHourlyDataByAsin.get("units"));
        softAssert.assertEquals(orderedUnitsCsv, orderedUnitsDb, "CSV and DB ordered units for ASIN not matching. Date:" + startDate + "- ASIN:" + asinCsv);
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-536 - Real Time Sales - Multi BU - Export by ASIN")
    public void Api_RoundUp_Real_Time_Sales_Multi_Bu_Hourly_Export_By_Asin_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();

        String today = DateUtility.getTodayDate();
        Map<String, Object> latestDate = dbFunctions.getLatestDayFullHourlyDataAvailable(DateUtility.convertDateToInt(today), bu1Id);
        String endDate = DateUtility.calculateDate((Integer) latestDate.get("year"), (Integer) latestDate.get("dayofyear"));
        String startDate = DateUtility.subtractDaysFromDate(endDate, 1);

        RoundupRequestBody roundUpExportByAsinReqBody = new RoundupRequestBody(bu1Id, "HOURLY", startDate, startDate, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS", "SUMMARY");

        JSONObject response = RetailApiRequests.roundupMultiBuExportByAsinByDateJson(roundUpExportByAsinReqBody, bu1Id, bu2Id, "USD", authToken);
        String exportUrl = response.getString("fileUrl");
        File file = SharedMethods.importFileFromUrl(exportUrl, downloadFolder + "/roundup.csv");
        String filePath = file.getPath();

        int totalHourlyRecordsCsv = CsvUtility.countNonEmptyRowsInColumn(filePath, "Ordered Revenue");
        int totalHourlyRecordsDb = dbFunctions.getMultiBuTotalHourlyRecordsByAsin(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(startDate), bu1Id, bu2Id);
        verifyCsvDbRecords(softAssert, totalHourlyRecordsCsv, totalHourlyRecordsDb, startDate, endDate);
        int randomRow = SharedMethods.getRandomNumberBetweenRange(2, totalHourlyRecordsCsv);

        String asinCsv = CsvUtility.getCellContent(filePath, randomRow, "ASIN");
        String orderedUnits = CsvUtility.getCellContent(filePath, randomRow, "Ordered Units");
        int orderedUnitsCsv = (int) Double.parseDouble(orderedUnits);
        String buName = CsvUtility.getCellContent(filePath, randomRow, "Business Unit Name");
        List<String> buUnitNames = CsvUtility.getAllCellValuesInColumn(filePath, "Business Unit Name");
        softAssert.assertEquals(buUnitNames.size(), 2, "More or less than 2 BUs in Csv file.");

        Map<String, String> fullDayHourlyDataByAsin = dbFunctions.getRealTimeSalesMultiBuFullDayHourlyDataByAsinByBuName(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(startDate), asinCsv, buName);
        int orderedUnitsDb = Integer.parseInt(fullDayHourlyDataByAsin.get("units"));
        softAssert.assertEquals(orderedUnitsCsv, orderedUnitsDb, "CSV and DB ordered units for ASIN not matching. Date:" + startDate + "- ASIN:" + asinCsv);
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-538 - Real Time Sales - Daily - Export by ASIN by Date")
    public void Api_RoundUp_Real_Time_Sales_Single_Bu_Daily_Export_By_Asin_By_Date_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String startDate = DateUtility.getDayBeforeToday(15);
        String endDate = DateUtility.getTodayDate();

        RoundupRequestBody roundUpDailyReqBody = new RoundupRequestBody(null, bu1Id, "DAILY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject response = RetailApiRequests.roundupAllJson(roundUpDailyReqBody, authToken);
        JSONArray popWidgetObjects = response.getJSONArray("popWidget");

        JSONObject nonRealtimeSalesDailyApi = RetailApiRequests.getAllNonRealTimeSalesObjects(popWidgetObjects).get(0);
        String latestDaySpApiAvailableApi = DateUtility.createDate(nonRealtimeSalesDailyApi.getInt("year"), nonRealtimeSalesDailyApi.getInt("month"), nonRealtimeSalesDailyApi.getInt("day"));
        String followingDay = DateUtility.getFollowingDayFromDate(DateUtility.convertDateToInt(latestDaySpApiAvailableApi), 1);

        roundUpDailyReqBody = new RoundupRequestBody(bu1Id, "DAILY", followingDay, followingDay, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS", "DETAIL");

        response = RetailApiRequests.roundupExportByAsinByDateJson(roundUpDailyReqBody, authToken);
        String exportUrl = response.getString("fileUrl");
        File file = SharedMethods.importFileFromUrl(exportUrl, downloadFolder + "/roundup.csv");
        String filePath = file.getPath();

        List<JSONObject> realTimeSalesDailyDataApi = RetailApiRequests.getAllRealTimeSalesObjects(popWidgetObjects);
        if (realTimeSalesDailyDataApi.size() > 0) {
            int totalDailyRecordsCsv = CsvUtility.getRowCount(filePath) - 2;
            int totalDailyRecordsDb = dbFunctions.getTotalDailyRecords(DateUtility.convertDateToInt(followingDay), DateUtility.convertDateToInt(followingDay), "REAL TIME INGESTION", bu1Id);
            verifyCsvDbRecords(softAssert, totalDailyRecordsCsv, totalDailyRecordsDb, startDate, endDate);
            int randomRow = SharedMethods.getRandomNumberBetweenRange(2, totalDailyRecordsCsv);

            String startDateCsv = CsvUtility.getCellContent(filePath, randomRow, "Start Date");
            String asinCsv = CsvUtility.getCellContent(filePath, randomRow, "ASIN");
            String orderedUnits = CsvUtility.getCellContent(filePath, randomRow, "Ordered Units");
            int orderedUnitsCsv = (int) Double.parseDouble(orderedUnits);

            Map<String, String> dailyDataByAsin = dbFunctions.getRealTimeSalesDailyDataByAsinByBuId(DateUtility.convertDateToInt(followingDay), asinCsv, bu1Id, "REAL TIME INGESTION");
            int orderedUnitsDb = Integer.parseInt(dailyDataByAsin.get("ordered_units"));
            softAssert.assertEquals(orderedUnitsCsv, orderedUnitsDb, "CSV and DB ordered units for ASIN not matching. Date:" + startDateCsv + "- ASIN:" + asinCsv);
        } else {
            List<String> daysInCsv = CsvUtility.getAllCellValuesInColumn(filePath, "Start Date");
            softAssert.assertEquals(daysInCsv.size(), 0, "No daily RTS available. But CSV has data");
        }
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-538 - Real Time Sales - Weekly - Export by ASIN by Date")
    public void Api_RoundUp_Real_Time_Sales_Single_Bu_Weekly_Export_By_Asin_By_Date_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String startDate = DateUtility.getDayBeforeToday(45);
        String endDate = DateUtility.getTodayDate();

        RoundupRequestBody roundUpAllWeeklyReqBody = new RoundupRequestBody(null, bu1Id, "WEEKLY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject response = RetailApiRequests.roundupAllJson(roundUpAllWeeklyReqBody, authToken);
        JSONArray popWidgetObjects = response.getJSONArray("popWidget");

        JSONObject nonRealtimeSalesWeeklyApi = RetailApiRequests.getAllNonRealTimeSalesObjects(popWidgetObjects).get(0);
        String latestWeekSpApiAvailableApi = DateUtility.createDate(nonRealtimeSalesWeeklyApi.getInt("year"), nonRealtimeSalesWeeklyApi.getInt("month"), nonRealtimeSalesWeeklyApi.getInt("day"));
        String followingWeek = DateUtility.getFollowingWeekFromDate(DateUtility.convertDateToInt(latestWeekSpApiAvailableApi), 1);

        roundUpAllWeeklyReqBody = new RoundupRequestBody(bu1Id, "WEEKLY", followingWeek, followingWeek, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS", "DETAIL");

        response = RetailApiRequests.roundupExportByAsinByDateJson(roundUpAllWeeklyReqBody, authToken);
        String exportUrl = response.getString("fileUrl");
        File file = SharedMethods.importFileFromUrl(exportUrl, downloadFolder + "/roundup.csv");
        String filePath = file.getPath();

        List<JSONObject> realTimeSalesWeeklyDataApi = RetailApiRequests.getAllRealTimeSalesObjects(popWidgetObjects);
        if (realTimeSalesWeeklyDataApi.size() > 0) {
            int totalWeeklyRecordsCsv = CsvUtility.getRowCount(filePath) - 2;
            int totalWeeklyRecordsDb = dbFunctions.getTotalWeeklyRecords(DateUtility.convertDateToInt(followingWeek), DateUtility.convertDateToInt(followingWeek), bu1Id);
            verifyCsvDbRecords(softAssert, totalWeeklyRecordsCsv, totalWeeklyRecordsDb, startDate, endDate);
            int randomRow = SharedMethods.getRandomNumberBetweenRange(2, totalWeeklyRecordsCsv);

            String startDateCsv = CsvUtility.getCellContent(filePath, randomRow, "Start Date");
            String asinCsv = CsvUtility.getCellContent(filePath, randomRow, "ASIN");
            String orderedUnits = CsvUtility.getCellContent(filePath, randomRow, "Ordered Units");
            int orderedUnitsCsv = (int) Double.parseDouble(orderedUnits);

            Map<String, String> weeklyDataByAsin = dbFunctions.getRealTimeSalesWeeklyDataByAsinByBuId(DateUtility.convertDateToInt(startDateCsv), asinCsv, bu1Id, "REAL TIME INGESTION");
            int orderedUnitsDb = Integer.parseInt(weeklyDataByAsin.get("ordered_units"));
            softAssert.assertEquals(orderedUnitsCsv, orderedUnitsDb, "CSV and DB ordered units for ASIN not matching. Date:" + startDateCsv + "- ASIN:" + asinCsv);
        } else {
            List<String> weeksInCsv = CsvUtility.getAllCellValuesInColumn(filePath, "Start Date");
            softAssert.assertEquals(weeksInCsv.size(), 0, "No weekly RTS available. But CSV has data");
        }
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-538 - Real Time Sales - Single BU - Monthly - Export by ASIN by Date")
    public void Api_RoundUp_Real_Time_Sales_Single_Bu_Monthly_Export_By_Asin_By_Date_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String startDate = DateUtility.getDayBeforeToday(90);
        String endDate = DateUtility.getTodayDate();

        RoundupRequestBody roundUpAllMonthlyReqBody = new RoundupRequestBody(null, bu1Id, "MONTHLY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");

        JSONObject response = RetailApiRequests.roundupAllJson(roundUpAllMonthlyReqBody, authToken);
        JSONArray popWidgetObjects = response.getJSONArray("popWidget");

        JSONObject nonRealtimeSalesMonthlyApi = RetailApiRequests.getAllNonRealTimeSalesObjects(popWidgetObjects).get(0);
        String latestMonthSpApiAvailableApi = DateUtility.createDate(nonRealtimeSalesMonthlyApi.getInt("year"), nonRealtimeSalesMonthlyApi.getInt("month"), nonRealtimeSalesMonthlyApi.getInt("day"));
        String followingMonth = DateUtility.getFollowingMonthFromDate(DateUtility.convertDateToInt(latestMonthSpApiAvailableApi), 1);

        roundUpAllMonthlyReqBody = new RoundupRequestBody(bu1Id, "MONTHLY", followingMonth, followingMonth, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS", "DETAIL");

        response = RetailApiRequests.roundupExportByAsinByDateJson(roundUpAllMonthlyReqBody, authToken);
        String exportUrl = response.getString("fileUrl");
        File file = SharedMethods.importFileFromUrl(exportUrl, downloadFolder + "/roundup.csv");
        String filePath = file.getPath();

        List<JSONObject> realTimeSalesMonthlyDataApi = RetailApiRequests.getAllRealTimeSalesObjects(popWidgetObjects);
        if (realTimeSalesMonthlyDataApi.size() > 0) {
            int totalMonthlyRecordsCsv = CsvUtility.getRowCount(filePath) - 2;
            int totalMonthlyRecordsDb = dbFunctions.getTotalMonthlyRecords(DateUtility.convertDateToInt(followingMonth), DateUtility.convertDateToInt(followingMonth), bu1Id);
            softAssert.assertEquals(totalMonthlyRecordsCsv, totalMonthlyRecordsDb, "Count of CSV monthly records not matching with Db. Date: " + startDate + " to " + endDate);
            verifyCsvDbRecords(softAssert, totalMonthlyRecordsCsv, totalMonthlyRecordsDb, startDate, endDate);
            int randomRow = SharedMethods.getRandomNumberBetweenRange(2, totalMonthlyRecordsCsv);

            String startDateCsv = CsvUtility.getCellContent(filePath, randomRow, "Start Date");
            String asinCsv = CsvUtility.getCellContent(filePath, randomRow, "ASIN");
            String orderedUnits = CsvUtility.getCellContent(filePath, randomRow, "Ordered Units");
            int orderedUnitsCsv = (int) Double.parseDouble(orderedUnits);

            Map<String, String> monthlyDataByAsin = dbFunctions.getRealTimeSalesMonthlyDataByAsinByBuId(DateUtility.convertDateToInt(followingMonth), asinCsv, bu1Id, "REAL TIME INGESTION");
            int orderedUnitsDb = Integer.parseInt(monthlyDataByAsin.get("ordered_units"));
            softAssert.assertEquals(orderedUnitsCsv, orderedUnitsDb, "CSV and DB ordered units for ASIN not matching. Date:" + startDateCsv + "- ASIN:" + asinCsv);
        } else {
            List<String> monthsInCsv = CsvUtility.getAllCellValuesInColumn(filePath, "Start Date");
            softAssert.assertEquals(monthsInCsv.size(), 0, "No monthly RTS available. But CSV has data");
        }
        softAssert.assertAll();
    }

    @Test(groups = "realtime", description = "RAR-549 - Real Time Sales - Hourly Data Aggregated in Daily Table")
    public void Api_RoundUp_Real_Time_Sales_Hourly_Data_Aggregated_In_Daily_Table_Test() throws Exception {
        String today = DateUtility.getTodayDate();
        Map<String, Object> latestDate = dbFunctions.getLatestDayFullHourlyDataAvailable(DateUtility.convertDateToInt(today), bu1Id);
        int latestDayFullHourlyDataAvailable = DateUtility.convertDateToInt(DateUtility.calculateDate((Integer) latestDate.get("year"), (Integer) latestDate.get("dayofyear")));
        int latestDaySpApiDataAvailableDb = dbFunctions.getLatestDayDataAvailableFromSource(bu1Id, "SP API Ingestion");
        int latestDayRealtimeSalesDataAvailableDb = dbFunctions.getLatestDayDataAvailableFromSource(bu1Id, "REAL TIME INGESTION");
        Assert.assertEquals(latestDayFullHourlyDataAvailable, latestDayRealtimeSalesDataAvailableDb, "RTS days missing in daily table. Available full day in hourly latest: " + latestDayFullHourlyDataAvailable + ", SP API Ingestion daily latest: " + latestDaySpApiDataAvailableDb + ", RTS Ingestion daily latest: " + latestDayRealtimeSalesDataAvailableDb);
    }

    @Test(groups = "realtime", description = "RAR-534 - Real Time Sales - Graph Summary Data")
    public void Api_RoundUp_Real_Time_Sales_Graph_Summary_Data_Test() throws Exception {
        SoftAssert softAssert = new SoftAssert();
        String today = DateUtility.getTodayDate();
        Map<String, Object> latestDate = dbFunctions.getLatestDayFullHourlyDataAvailable(DateUtility.convertDateToInt(today), bu1Id);
        String endDate = DateUtility.calculateDate((Integer) latestDate.get("year"), (Integer) latestDate.get("dayofyear"));
        String startDate = DateUtility.subtractDaysFromDate(endDate, 1);

        RoundupRequestBody roundUpAllHourlyReqBody = new RoundupRequestBody(null, bu1Id, "HOURLY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_UNITS");
        JSONObject response = RetailApiRequests.roundupAllJson(roundUpAllHourlyReqBody, authToken);
        int orderedUnitsApi = response.getJSONObject("metricAndAspWidget").getJSONObject("metric").getInt("current");

        roundUpAllHourlyReqBody = new RoundupRequestBody(null, bu1Id, "HOURLY", startDate, endDate, null, null, "amazon.com", "PREMIUM", "VENDOR",
                false, false, false, "DEFAULT OBSOLETE ASIN SEGMENT", "AMAZON RETAIL", "Manufacturing", "ORDERED_REVENUE");
        response = RetailApiRequests.roundupAllJson(roundUpAllHourlyReqBody, authToken);
        double averageSellingPriceApi = response.getJSONObject("metricAndAspWidget").getJSONObject("averageSellingPrice").getDouble("current");
        int orderedRevenueApi = (int) response.getJSONObject("metricAndAspWidget").getJSONObject("metric").getDouble("current");

        Map<String, String> tileValuesDb = dbFunctions.getSumOfHourlyData(DateUtility.convertDateToInt(startDate), DateUtility.convertDateToInt(endDate), bu1Id);
        int orderedUnitsDb = Integer.parseInt(tileValuesDb.get("ordered_units"));
        double averageSellingPriceDb = Double.parseDouble(tileValuesDb.get("average_selling_price"));
        int orderedRevenueDb = (int) Double.parseDouble(tileValuesDb.get("ordered_revenue"));

        softAssert.assertEquals(orderedUnitsApi, orderedUnitsDb, "Ordered Units not matching. API: " + orderedUnitsApi + ", DB: " + orderedUnitsDb);
        softAssert.assertEquals(averageSellingPriceApi, averageSellingPriceDb, "Average Selling Price not matching. API: " + averageSellingPriceApi + ", DB: " + averageSellingPriceDb);
        softAssert.assertEquals(orderedRevenueApi, orderedRevenueDb, "Ordered Revenue not matching. API: " + orderedRevenueApi + ", DB: " + orderedRevenueDb);
        softAssert.assertAll();
    }

    public void verifyCsvDbRecords(SoftAssert softAssert, int totalRecordsCsv, int totalRecordsDb, String startDate, String endDate) {
        int difference = Math.abs(totalRecordsCsv - totalRecordsDb);
        int largerNumber = Math.max(totalRecordsCsv, totalRecordsDb);
        double onePercentOfLargerNumber = 0.01 * largerNumber;
        softAssert.assertTrue(difference <= onePercentOfLargerNumber, "Count of CSV records not within range. Date: " + startDate + " to " + endDate);
    }
}