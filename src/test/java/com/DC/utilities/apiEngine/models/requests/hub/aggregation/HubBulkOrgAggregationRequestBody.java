package com.DC.utilities.apiEngine.models.requests.hub.aggregation;

import java.util.ArrayList;
import java.util.List;

public class HubBulkOrgAggregationRequestBody {

    public List<HubOrganizationAggregationRequestBody> bulkOrgs = new ArrayList<>();

    public void addOrg(HubOrganizationAggregationRequestBody org){
        bulkOrgs.add(org);
    }


}