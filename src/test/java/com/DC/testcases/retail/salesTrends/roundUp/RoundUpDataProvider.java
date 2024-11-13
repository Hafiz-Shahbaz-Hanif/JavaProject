//TODO - Class not used as of 7/17/23 - Will revisit once this page makes it to the merge app

//package com.DC.testcases.retail.salesTrends.roundUp;

import com.DC.utilities.CommonApiMethods;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.lang.reflect.Method;

/*public class RoundUpDataProvider {

    @DataProvider(name = "Retail_RoundUp_C147973")
    public static Object[] RoundUp_C147973(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Retail_RoundUp_C147974")
    public static Object[] RoundUp_C147974(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147977")
    public static Object[] RoundUp_C147977(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C798")
    public static Object[] RoundUp_C798(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147978")
    public static Object[] RoundUp_C147978(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147980")
    public static Object[] RoundUp_C147980(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147981")
    public static Object[] RoundUp_C147981(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147983")
    public static Object[] RoundUp_C147983(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147984")
    public static Object[] RoundUp_C147984(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Retail_RoundUp_C147992")
    public static Object[] RoundUp_C147992(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Retail_RoundUp_C147985")
    public static Object[] RoundUp_C147985(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147993")
    public static Object[] RoundUp_C147993(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147986")
    public static Object[] RoundUp_C147986(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147987")
    public static Object[] RoundUp_C147987(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147989")
    public static Object[] RoundUp_C147989(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147990")
    public static Object[] RoundUp_C147990(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147991")
    public static Object[] RoundUp_C147991(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Retail_RoundUp_C147982")
    public static Object[] RoundUp_C147982(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "Retail_RoundUp_C147979")
    public static Object[] RoundUp_C147979(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "C156536")
    public static Object[] RoundUp_C156536(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "C156535")
    public static Object[] RoundUp_C156535(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "C156534")
    public static Object[] RoundUp_C156534(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }
    
    @DataProvider(name = "C156532")
    public static Object[] RoundUp_C156532(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(RoundUp.class.getName(), methodName);
        return testData;
    }

}*/

