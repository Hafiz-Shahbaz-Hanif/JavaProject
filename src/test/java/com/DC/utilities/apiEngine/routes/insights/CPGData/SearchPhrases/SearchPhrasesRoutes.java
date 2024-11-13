package com.DC.utilities.apiEngine.routes.insights.CPGData.SearchPhrases;

import com.DC.utilities.ReadConfig;

public class SearchPhrasesRoutes {

    private static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    public static final String SEARCH_PHRASE_HOST = READ_CONFIG.getCpgDataServiceUrl() + "/searchphrases";

    public static final String getSearchPhraseVolumesRoutePath() {
        return SEARCH_PHRASE_HOST + "/volume";
    }

}
