package com.DC.utilities.apiEngine.models.responses.adc.advertising.media;

import java.util.List;

public class CitrusAdCampaignResponseBody extends BaseClassFlightdeckResponseBody {

    public List<Items> items;

    public List<Items> getItems() {
        return items;
    }
}