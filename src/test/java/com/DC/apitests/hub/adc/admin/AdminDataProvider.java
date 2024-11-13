package com.DC.apitests.hub.adc.admin;

import java.io.IOException;
import java.lang.reflect.Method;

import com.DC.utilities.CommonApiMethods;
import org.testng.annotations.DataProvider;

public class AdminDataProvider {

    @DataProvider(name = "AdminUserInfo")
    public static String[][] AdminUserInfo(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(AdminApiTest.class.getName(), methodName);
        return testData;
    }

}