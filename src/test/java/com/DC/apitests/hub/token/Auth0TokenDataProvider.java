package com.DC.apitests.hub.token;

import java.io.IOException;
import java.lang.reflect.Method;

import com.DC.utilities.CommonApiMethods;
import org.testng.annotations.DataProvider;

public class Auth0TokenDataProvider {

    @DataProvider(name = "Auth0_ReportingDashboard")
    public static Object[][] Auth0_ReportingDashboard(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_Flightdeck")
    public static Object[][] Auth0_Flightdeck(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_SearchOfVoive")
    public static Object[][] Auth0_SearchOfVoive(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_AdminClientSelection")
    public static Object[][] Auth0_AdminClientSelection(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Expired_Autoh0_Token")
    public static Object[][] Expired_Autoh0_Token(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_DaasAutomatedDataTemplates")
    public static Object[][] Auth0_DaasAutomatedDataTemplates(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_RetailScratchpad")
    public static Object[][] Auth0_RetailScratchpad(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_AdminUser")
    public static Object[][] Auth0_AdminUser(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_SegmentationGroups")
    public static Object[][] Auth0_SegmentationGroups(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_DaasAutomatedDataSourceOrigins")
    public static Object[][] Auth0_DaasAutomatedDataSourceOrigins(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_MPV_Create_Delete")
    public static Object[][] Auth0_MPV_Create_Delete(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_Import_Export_File")
    public static Object[][] Auth0_Import_Export_File(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Valid_Auth0_Token_Generates_Insights_JWT")
    public static Object[][] Valid_Auth0_Token_Generates_Insights_JWT(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForInsightsApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Expired_Auth0_Token_Not_Generate_Insights_JWT")
    public static Object[][] Expired_Auth0_Token_Not_Generate_Insights_JWT(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForInsightsApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "User_Has_Auth0Id_In_FilaDb")
    public static Object[][] User_Has_Auth0Id_In_FilaDb(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0_Token_User_Has_No_Access_To_Business_Unit")
    public static Object[][] Auth0_Token_User_Has_No_Access_To_Business_Unit(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForAdcApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Verify_MS_User_Info_Data")
    public static Object[][] Verify_MS_User_Info_Data(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForMarketShareApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Auth0Token_Fetches_MS_ApiKey")
    public static Object[][] Auth0Token_Fetches_MS_ApiKey(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForMarketShareApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Expired_Auth0_Token_Cannot_Generate_MarketShare_ApiKey")
    public static Object[][] Expired_Auth0_Token_Cannot_Generate_MarketShare_ApiKey(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(Auth0TokenForMarketShareApiTest.class.getName(), methodName);
        return testData;
    }

}
