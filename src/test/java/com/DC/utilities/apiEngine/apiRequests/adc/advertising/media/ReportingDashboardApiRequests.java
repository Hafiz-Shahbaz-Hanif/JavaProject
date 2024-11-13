package com.DC.utilities.apiEngine.apiRequests.adc.advertising.media;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.FlightdeckRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.MultiPlatformViewFiltersRequestBody;
import com.DC.utilities.apiEngine.models.requests.adc.advertisig.media.ReportingDashboardRequestBody;
import com.DC.utilities.apiEngine.routes.adc.advertising.media.MediaRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;


public class ReportingDashboardApiRequests {
    
    public static Response reportingDashboard(ReportingDashboardRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(MediaRoutes.getReportingDashboardRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }
    
    public static Response flightdeck(FlightdeckRequestBody requestBody, String headers, String parameters, String jwt, String retailer) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(MediaRoutes.getMediaFlightDeckRoutePath(retailer.toLowerCase()), "POST", reqBody, headers, parameters, jwt);
    }
    
    public static Response getExistingMpvFilters(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(MediaRoutes.getMultiPlatformViewFiltersRoutePath(), "GET", "", headers, parameters, jwt);
    }
    
    public static Response getAllMpvFilters(String headers, String parameters, String jwt, int businessUnitId) throws Exception {
    	return CommonApiMethods.callEndpoint(MediaRoutes.getSegmentationTypeEntityForBusinessUnitRoutePath(businessUnitId), "GET", "", headers, parameters, jwt);
    }
    
    public static Response createMpvFilter(MultiPlatformViewFiltersRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(MediaRoutes.getMultiPlatformViewFilterCreationRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }
    
    public static Response deleteMpvFilter(String headers, String jwt, String id) throws Exception {
    	return CommonApiMethods.callEndpoint(MediaRoutes.getMultiPlatformViewFilterDeleteRoutePath(id), "DELETE", "", headers, "", jwt);
    }
    
    public static Response reportingDashboardExternalGateway(ReportingDashboardRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(MediaRoutes.getReportingDashboardExternalGatewayRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response flightdeckExternalGateway(FlightdeckRequestBody requestBody, String headers, String parameters, String jwt, String retailer) throws Exception {
     	String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(MediaRoutes.getMediaFlightDeckRoutePath(retailer.toLowerCase()), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response getExistingMpvFiltersExternalGateway(String headers, String parameters, String jwt) throws Exception {
    	return CommonApiMethods.callEndpoint(MediaRoutes.getMultiPlatformViewFiltersExternalGatewayRoutePath(), "GET", "", headers, parameters, jwt);
    }

    public static Response getAllMpvFiltersExternalGateway(String headers, String parameters, String jwt, int businessUnitId) throws Exception {
    	return CommonApiMethods.callEndpoint(MediaRoutes.getSegmentationTypeEntityForBusinessUnitExternalGatewayRoutePath(businessUnitId), "GET", "", headers, parameters, jwt);
    }

    public static Response createMpvFilterExternalGateway(MultiPlatformViewFiltersRequestBody requestBody, String headers, String parameters, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
    	return CommonApiMethods.callEndpoint(MediaRoutes.getMultiPlatformViewFilterCreationExternalGatewayRoutePath(), "POST", reqBody, headers, parameters, jwt);
    }

    public static Response deleteMpvFilterExternalGateway(String headers, String jwt, String id) throws Exception {
    	return CommonApiMethods.callEndpoint(MediaRoutes.getMultiPlatformViewFilterDeleteExternalGatewayRoutePath(id), "DELETE", "", headers, "", jwt);
    }
    
	
}
