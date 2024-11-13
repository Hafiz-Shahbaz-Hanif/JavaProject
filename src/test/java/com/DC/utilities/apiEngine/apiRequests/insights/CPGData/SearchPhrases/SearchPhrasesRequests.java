package com.DC.utilities.apiEngine.apiRequests.insights.CPGData.SearchPhrases;


import com.DC.utilities.apiEngine.routes.insights.CPGData.SearchPhrases.SearchPhrasesRoutes;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.List;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class SearchPhrasesRequests {

    public static Response getSearchPhraseVolumes(List<String> searchPhrases, List<Integer> domainIds, String jwt) throws Exception {
        String reqBody = "{\"SearchPhrases\":" + new ObjectMapper().writeValueAsString(searchPhrases) + ",\"DomainIds\":" + new ObjectMapper().writeValueAsString(domainIds) + "}";
        return callEndpoint(SearchPhrasesRoutes.getSearchPhraseVolumesRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response getSearchPhraseVolumes(List<String> searchPhrases, String jwt) throws Exception {
        String reqBody = "{\"SearchPhrases\":" + new ObjectMapper().writeValueAsString(searchPhrases) + "}";
        return callEndpoint(SearchPhrasesRoutes.getSearchPhraseVolumesRoutePath(), jwt, "POST", reqBody, "");
    }
}
