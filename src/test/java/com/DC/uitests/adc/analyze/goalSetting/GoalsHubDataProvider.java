package com.DC.uitests.adc.analyze.goalSetting;

import com.DC.utilities.apiEngine.apiRequests.adc.analyze.goalSetting.GoalsHubRequests;
import org.testng.annotations.DataProvider;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Hashtable;

public class GoalsHubDataProvider {

    @DataProvider(name = "Goals_Hub")
    public static Object[][] Goals_Hub_PDP_Goal_Calculation(Method method) {
        Dictionary<String, String> firstSet = new Hashtable<>(){
            {
                put("period", "Past Goals");
                put("interval", "Quarterly");
            }
        };
        Dictionary<String, String> secondSet = new Hashtable<>(){
            {
                put("period", "Past Goals");
                put("interval", "Monthly");
            }
        };
        Dictionary<String, String> thirdSet = new Hashtable<>(){
            {
                put("period", "Past Goals");
                put("interval", "Yearly");
            }
        };
        Object[][] users = {{firstSet}, {secondSet}, {thirdSet}};
        return users;
    }

    @DataProvider(name = "Sales_Goals_Hub")
    public static Object[][] Goals_Hub_Sale_Goal_Calculation(Method method) {
        Dictionary<String, String> firstSet = new Hashtable<>(){
            {
                put("period", "Past Goals");
                put("interval", "Quarterly");
                put("salesMetric", "ORDERED_REVENUE");
            }
        };
        Dictionary<String, String> secondSet = new Hashtable<>(){
            {
                put("period", "Past Goals");
                put("interval", "Monthly");
                put("salesMetric", "ORDERED_UNITS");
            }
        };
        Object[][] users = {{firstSet}, {secondSet}};
        return users;
    }

    @DataProvider(name = "Goals_Hub_YoY_PoP")
    public static Object[][] Goals_Hub_YoY_PoP(Method method) {
        Dictionary<String, String> firstSet = new Hashtable<>(){
            {
                put("period", "Past Goals");
                put("interval", "Monthly");
                put("salesMetric", "SHIPPED_REVENUE");
            }
        };
        Dictionary<String, String> secondSet = new Hashtable<>(){
            {
                put("period", "Past Goals");
                put("interval", "Monthly");
                put("salesMetric", "AVAILABILITY_PERCENTAGE_IN_STOCK");
            }
        };
        Object[][] users = {{firstSet}, {secondSet}        };
        return users;
    }

    @DataProvider(name = "Goals_Nightly_Calculations")
    public static Object[][] Goals_Nightly_Calculations(Method method) {
        Object[][] users = {{GoalsHubRequests.metricTypes.get("pdp")}, {GoalsHubRequests.metricTypes.get("retail")}};
        return users;
    }
}