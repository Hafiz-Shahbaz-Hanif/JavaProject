package com.DC.apitests.hub.aggregation;

import com.DC.utilities.ReadConfig;
import com.DC.utilities.SharedMethods;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class HubAggregationServiceDataProvider {

    private static ReadConfig readConfig =  ReadConfig.getInstance();

    @DataProvider(name = "Hub_Aggregation")
    public static Object[][] Hub_Aggregation_Service(Method method) {
        String organizationId = SharedMethods.generateUUID();
        String buId = SharedMethods.generateUUID();
        Dictionary<String, Object> fila = new Hashtable<>(){
            {
                put("legacyPlatformOrganizationId", SharedMethods.getRandomNumber(150));
                put("organizationId", organizationId);
                put("legacyPlatformName", "fila");
                put("legacyPlatformBusinessUnitId", SharedMethods.getRandomNumber(150));
                put("businessUnitId", buId);
                put("retailerPlatformId", SharedMethods.generateUUID());
                put("type", "authinfo");
            }
        };
        Dictionary<String, Object> onespace = new Hashtable<>(){
            {
                put("legacyPlatformOrganizationId", SharedMethods.generateUUID());
                put("organizationId", organizationId);
                put("legacyPlatformName", "onespace");
                put("legacyPlatformBusinessUnitId", SharedMethods.generateUUID());
                put("businessUnitId", buId);
                put("retailerPlatformId", SharedMethods.generateUUID());
                put("type", "jwt");
            }
        };
        Dictionary<String, Object> marketshare = new Hashtable<>(){
            {
                put("legacyPlatformOrganizationId", SharedMethods.getRandomNumber(150));
                put("organizationId", organizationId);
                put("legacyPlatformName", "marketshare");
                put("legacyPlatformBusinessUnitId", SharedMethods.getRandomNumber(150));
                put("businessUnitId", buId);
                put("retailerPlatformId", SharedMethods.generateUUID());
                put("type", "authinfo");
            }
        };
        Object[][] users = {{fila}, {onespace}, {marketshare}};
        return users;
    }

    @DataProvider(name = "Hub_Aggregation_Organization_Bulk_Creation")
    public static Object[][] Hub_Aggregation_Organization_Bulk_Creation(Method method) {
        List<Dictionary<String, Object>> orgDetails = new ArrayList<>();

        String organizationId = SharedMethods.generateUUID();
        Dictionary<String, Object> fila = new Hashtable<>(){
            {
                put("legacyPlatformOrganizationId", SharedMethods.getRandomNumber(150));
                put("organizationId", organizationId);
                put("legacyPlatformName", "fila");
                put("legacyPlatformBusinessUnitId", SharedMethods.getRandomNumber(150));
                put("businessUnitId", SharedMethods.generateUUID());
                put("retailerPlatformId", SharedMethods.generateUUID());
                put("orgId", SharedMethods.generateUUID());
            }
        };
        Dictionary<String, Object> onespace = new Hashtable<>(){
            {
                put("legacyPlatformOrganizationId", SharedMethods.generateUUID());
                put("organizationId", organizationId);
                put("legacyPlatformName", "onespace");
                put("legacyPlatformBusinessUnitId", SharedMethods.generateUUID());
                put("businessUnitId", SharedMethods.generateUUID());
                put("retailerPlatformId", SharedMethods.generateUUID());
                put("orgId", SharedMethods.generateUUID());
            }
        };
        Dictionary<String, Object> marketshare = new Hashtable<>(){
            {
                put("legacyPlatformOrganizationId", SharedMethods.getRandomNumber(150));
                put("organizationId", organizationId);
                put("legacyPlatformName", "marketshare");
                put("legacyPlatformBusinessUnitId", SharedMethods.getRandomNumber(150));
                put("businessUnitId", SharedMethods.generateUUID());
                put("retailerPlatformId", SharedMethods.generateUUID());
                put("orgId", SharedMethods.generateUUID());
            }
        };

        orgDetails.add(fila);
        orgDetails.add(onespace);
        orgDetails.add(marketshare);
        Object[][] orgs = {{orgDetails}};
        return orgs;
    }

}