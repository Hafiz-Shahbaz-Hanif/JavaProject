package com.DC.utilities.apiEngine.models.requests.hub.insights.authservice;

import java.util.List;

public class HubInsightsRolesRequestBody {

    public List<String> roles;

    public HubInsightsRolesRequestBody(List<String> roles) {
        this.roles = roles;
    }
}
