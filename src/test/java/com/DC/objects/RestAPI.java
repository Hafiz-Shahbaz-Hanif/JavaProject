package com.DC.objects;

import com.DC.utilities.enums.Enums;

import java.util.Map;

public class RestAPI {

    public String requestURI;

    public String jwt;

    public Enums.APIRequestMethod method;

    public String requestBody;

    public String requestParams;

    public Map<String, Object> queryParams;

    public RestAPI(String requestURI, String jwt, Enums.APIRequestMethod method, String requestBody, String requestParams, Map<String, Object> queryParams) {
        this.requestURI = requestURI;
        this.jwt = jwt;
        this.method = method;
        this.requestBody = requestBody;
        this.requestParams = requestParams;
        this.queryParams = queryParams;
    }
}
