package com.DC.utilities.apiEngine.apiServices.insights.CPGData.SearchPhrases;


import com.DC.utilities.apiEngine.apiRequests.insights.CPGData.SearchPhrases.SearchPhrasesRequests;
import com.DC.utilities.apiEngine.models.responses.insights.CPGData.SearchPhrases.OrderedSearchPhrasesVolumeResponse;
import io.restassured.response.Response;

import java.util.List;

public class SearchPhrasesService {

    public static OrderedSearchPhrasesVolumeResponse getSearchPhraseVolumes(List<String> searchPhrases, List<Integer> domainIds, String jwt) throws Exception {
        Response response = SearchPhrasesRequests.getSearchPhraseVolumes(searchPhrases, domainIds, jwt);
        return response.getBody().as(OrderedSearchPhrasesVolumeResponse.class);
    }

    public static OrderedSearchPhrasesVolumeResponse getSearchPhraseVolumes(List<String> searchPhrases, String jwt) throws Exception {
        Response response = SearchPhrasesRequests.getSearchPhraseVolumes(searchPhrases, jwt);
        return response.getBody().as(OrderedSearchPhrasesVolumeResponse.class);
    }
}
