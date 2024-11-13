package com.DC.apitests.hub.insights;

import java.io.IOException;
import java.lang.reflect.Method;

import com.DC.utilities.CommonApiMethods;
import org.testng.annotations.DataProvider;

public class HubInsightsCpgServerDataProvider {

    @DataProvider(name = "Current_Users_Export")
    public static Object[][] Current_Users_Export(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(HubInsightsCpgServerApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Product_Chain_Progress")
    public static Object[][] Product_Chain_Progress(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(HubInsightsCpgServerApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Categories_Filter_Export")
    public static Object[][] Categories_Filter_Export(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(HubInsightsCpgServerApiTest.class.getName(), methodName);
        return testData;
    }

}