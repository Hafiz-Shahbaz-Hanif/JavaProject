package com.DC.utilities.apiEngine.apiRequests.adc.daas;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.routes.adc.daas.DataRoutes;

import io.restassured.response.Response;

public class DataApiRequests {
	
    public static Response daasDataTemplate(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(DataRoutes.getDaasAutomatedDataTemplateRoutePath(), "GET", "", headers, parameters, jwt);
    }
    
    public static Response daasDataSourceOrigins(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(DataRoutes.getDaasAutomatedDataSourceOriginsRoutePath(), "GET", "", headers, parameters, jwt);
    }
    
    public static Response daasDataSourceOriginsExternalGateway(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(DataRoutes.getDaasAutomatedDataSourceOriginsExternalGatewayRoutePath(), "GET", "", headers, parameters, jwt);
    }

}
