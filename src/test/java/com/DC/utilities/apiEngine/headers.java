package com.DC.utilities.apiEngine;

import com.DC.utilities.CommonApiMethods;
import com.DC.utilities.apiEngine.routes.adc.catalog.retail.RetailRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class headers {

    public static String header() {
        return "Content-Type=application/json";
    }

    public static Map<String, String> multipleHeadersForAggregatedBUs(String jwt) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Authorization", jwt);
        headerMap.put("x-businessunitcontext", "191,39");
        headerMap.put("x-currencycontext", "USD");

        return headerMap;
    }

    public static String multipleHeaders(String... headers) {
        String header = "Content-Type=application/json";
        if (headers != null && headers.length > 0) {
            header += ";" + String.join(";", headers);
        }
        return header;
    }
}
