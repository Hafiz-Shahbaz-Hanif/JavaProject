package com.DC.apitests.adc.advertising.media;

import com.DC.utilities.CommonApiMethods;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.lang.reflect.Method;

public class MediaApiDataProvider {

    @DataProvider(name = "ReportingScratchpad_Slicer_Amazon")
    public static Object[][] ReportingScratchpad_Slicer_Amazon(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ScratchpadServicesApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingScratchpad_Slicer_Yoy_Amazon")
    public static Object[][] ReportingScratchpad_Slicer_Yoy_Amazon(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ScratchpadServicesApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingScratchpad_Slicer_Yoy_Summary_Amazon")
    public static Object[][] ReportingScratchpad_Slicer_Yoy_Summary_Amazon(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ScratchpadServicesApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingDashboard_Amazon")
    public static Object[][] ReportingDashboard_Amazon(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ReportingDashboardApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingDashboard_Walmart")
    public static Object[][] ReportingDashboard_Walmart(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ReportingDashboardApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingDashboard_Doordash")
    public static Object[][] ReportingDashboard_Doordash(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ReportingDashboardApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingDashboard_Instacart")
    public static Object[][] ReportingDashboard_Instacart(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ReportingDashboardApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingDashboard_PromoteIq")
    public static Object[][] ReportingDashboard_PromoteIq(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ReportingDashboardApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingDashboard_CitrusAd")
    public static Object[][] ReportingDashboard_CitrusAd(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ReportingDashboardApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingDashboard_Criteo")
    public static Object[][] ReportingDashboard_Criteo(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ReportingDashboardApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Flightdeck")
    public static Object[][] Flightdeck(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(FlightdeckApiTests.class.getName(), methodName);
        return testData;
    }
}
