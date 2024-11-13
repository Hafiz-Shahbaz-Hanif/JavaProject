package com.DC.constants;

import com.DC.utilities.ReadConfig;

import java.util.List;

public class NetNewConstants {

    public static final ReadConfig READ_CONFIG = ReadConfig.getInstance();
    public static final String DC_LOGIN_ENDPOINT = READ_CONFIG.getDcAppUrl();

    public static String getReportsUrl(String sectionName, String pageName) {
        return DC_LOGIN_ENDPOINT + sectionName + "/" + pageName;
    }

    public static List<Integer> someCampaignIds = List.of(2653235, 2642426, 2652305, 3113443, 3005830, 3006941, 2646202);
}