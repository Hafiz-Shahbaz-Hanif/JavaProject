package com.DC.uitests.hub.authlogin;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.lang.reflect.Method;
import org.testng.annotations.DataProvider;
import com.DC.utilities.ReadConfig;

public class LoginDataProvider {

	static ReadConfig readConfig = ReadConfig.getInstance(); 

    @DataProvider(name = "FrontEndAuthLogin")
    public static Object[][] FilaNext(Method method) throws IOException, NoSuchMethodException {

    	Dictionary<String, String>  filaNextUser = new Hashtable<>();
    	filaNextUser.put("useremail", readConfig.getHubFilaUserEmail());
    	filaNextUser.put("password", readConfig.getHubFilaUserPassword());

    	Dictionary<String, String>  msUser = new Hashtable<>();

    	Dictionary<String, String>  insightsUser = new Hashtable<>();
    	insightsUser.put("useremail", readConfig.getHubInsightsUserEmail());
    	insightsUser.put("password", readConfig.getHubInsightsPassword());

    	Object[][] dicData = {{filaNextUser}, {msUser}, {insightsUser}};
    	return dicData;

    }


}