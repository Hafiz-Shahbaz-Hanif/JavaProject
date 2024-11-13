package com.DC.uitests.adc.analyze.retailReporting;

import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class RetailScratchpadDataProvider {

    @DataProvider(name = "Product_Titles")
    public static Object[][] Product_Titles(Method method) {
        Dictionary<String, Object> vendorPremium = new Hashtable<>(){
            {
                put("client", List.of("3M"));
                put("interval", "Monthly");
            }
        };
        Dictionary<String, Object> seller = new Hashtable<>(){
            {
                put("client", List.of("Nielsen-Massey"));
                put("interval", "Weekly");
            }
        };
        Dictionary<String, Object> vendorBasic = new Hashtable<>(){
            {
                put("client", List.of("Haleon-Consumer-Healthcare UAE"));
                put("accountType", "ARAB - Alphamed UAE");
                put("interval", "Daily");
            }
        };
        Dictionary<String, Object> vendorHybrid = new Hashtable<>(){
            {
                put("client", List.of("Bayer UK"));
                put("interval", "Monthly");
            }
        };
        Dictionary<String, Object> sellerVendor = new Hashtable<>(){
            {
                put("client", List.of("Runa"));
                put("interval", "Weekly");
            }
        };
        Dictionary<String, Object> aggregatedBus = new Hashtable<>(){
            {
                put("client", List.of("3M", "3M UK"));
                put("interval", "Daily");
            }
        };
        Object[][] users = {{vendorPremium}, {seller}, {vendorBasic}, {vendorHybrid}, {sellerVendor}, {aggregatedBus}};
        return users;
    }

}