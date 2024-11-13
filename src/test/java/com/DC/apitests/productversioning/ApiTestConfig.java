package com.DC.apitests.productversioning;

import com.DC.utilities.ReadConfig;

public class ApiTestConfig {

    public static ReadConfig readConfig = ReadConfig.getInstance();

    public static class TestConfig {

        public String loginEndpoint;

        public String productMasterRepoEndpoint;

        public String username;

        public String supportUsername;

        public String password;

        public String supportLoginEndpoint;

        public String companyID;

        public String companyName;

        public String environment;
    }

    public static TestConfig getTestConfig() {
        TestConfig commonConfig = new TestConfig();
        commonConfig.username = readConfig.getInsightsUsername();
        commonConfig.supportUsername = readConfig.getInsightsSupportUsername();
        commonConfig.password = readConfig.getInsightsPassword();
        commonConfig.loginEndpoint = readConfig.getInsightsApiLoginEndpoint();
        commonConfig.productMasterRepoEndpoint = readConfig.getProductVariantRepoEndpoint();
        commonConfig.supportLoginEndpoint = readConfig.getInsightsApiSupportuserLoginEndpoint();
        commonConfig.companyID = readConfig.getInsightsAutomatedTestCompanyId();
        commonConfig.companyName = readConfig.getInsightsAutomatedTestCompanyName();
        commonConfig.environment = readConfig.getEnvironment();
        return commonConfig;
    }
}
