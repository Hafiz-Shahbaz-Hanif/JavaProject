package com.DC.apitests.advertisingServices.media;

import com.DC.utilities.CommonApiMethods;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.lang.reflect.Method;

public class MediaApiDataProvider {

    @DataProvider(name = "ReportingScratchpad_Slicer_Amazon")
    public static Object[][] ReportingScratchpad_Slicer_Amazon(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ScratchpadAPIServicesTests.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingScratchpad_Slicer_Yoy_Amazon")
    public static Object[][] ReportingScratchpad_Slicer_Yoy_Amazon(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ScratchpadAPIServicesTests.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "ReportingScratchpad_Slicer_Yoy_Summary_Amazon")
    public static Object[][] ReportingScratchpad_Slicer_Yoy_Summary_Amazon(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(ScratchpadAPIServicesTests.class.getName(), methodName);
        return testData;
    }
}
