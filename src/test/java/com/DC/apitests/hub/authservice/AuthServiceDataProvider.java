package com.DC.apitests.hub.authservice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Hashtable;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.ReadConfig;
import org.testng.annotations.DataProvider;

public class AuthServiceDataProvider {

    private static ReadConfig readConfig =  ReadConfig.getInstance();

    @DataProvider(name = "Auth_Service_Health_Ready_Check")
    public static Object[][] Auth_Service_Health_Ready_Check(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(AuthServiceApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Authorize_Auth_User")
    public static Object[][] Authorize_Auth_User(Method method) throws IOException, NoSuchMethodException {
        String methodName = method.getName();
        String testData[][] = CommonApiMethods.getTestData(AuthServiceApiTest.class.getName(), methodName);
        return testData;
    }

    @DataProvider(name = "Hub_NetNew_User_Info")
    public static Object[][] Hub_NetNew_User_Info(Method method) {
        Dictionary<String, String> firstUser = new Hashtable<String, String>(){
            {
                put("username", readConfig.getHubFilaOnlyUserEmail());
                put("password", readConfig.getHubFilaOnlyUserPassword());
            }
        };
        Dictionary<String, String> secondUser = new Hashtable<String, String>(){
            {
                put("username", readConfig.getHubInsightsUserEmail());
                put("password", readConfig.getHubInsightsUserPassword());
            }
        };
        Dictionary<String, String> thirdUser = new Hashtable<String, String>() {
            {
                put("username", readConfig.getHubFilaInsightsUserEmail());
                put("password", readConfig.getHubFilaInsightsUserPassword());
            }
        };
        Object[][] users = {{firstUser}, {secondUser}, {thirdUser}};
        return users;
    }

}