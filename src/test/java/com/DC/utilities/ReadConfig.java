package com.DC.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ReadConfig {
    public static ReadConfig singleInstance = null;

    static Properties pro = null;

    private ReadConfig() {
        File src = new File("src/test/resources/environment/development/config.properties");
        Properties systemProperties = System.getProperties();
        if (!systemProperties.containsKey("baseURL")) {
            try {
                FileInputStream fis = new FileInputStream(src);
                pro = new Properties();
                pro.load(fis);
            } catch (Exception e) {
                System.out.println("Exception is " + e.getMessage());
            }
        }
    }

    public static ReadConfig getInstance() {
        if (singleInstance == null) {
            singleInstance = new ReadConfig();
        }
        return singleInstance;
    }

    public String getUsername() {
        return pro != null ? pro.getProperty("username") : System.getenv("username");
    }

    public String getPassword() {
        return pro != null ? pro.getProperty("password") : System.getenv("password");
    }

    public String getSQLDatabaseName() {
        return pro != null ? pro.getProperty("sqlDBName") : System.getenv("sqlDBName");
    }

    public String getSQLDBUsername() {
        return pro != null ? pro.getProperty("sqlDBusername") : System.getenv("sqlDBusername");
    }

    public String getSQLDBPassword() {
        return pro != null ? pro.getProperty("sqlDBpassword") : System.getenv("sqlDBpassword");
    }

    public String getSshHost() {
        return pro != null ? pro.getProperty("sshhost") : System.getenv("sshhost");
    }

    public String getSshUser() {
        return pro != null ? pro.getProperty("sshuser") : System.getenv("sshuser");
    }

    public Integer getSshPort() {
        return pro != null ? Integer.parseInt(pro.getProperty("sshport")) : Integer.parseInt(System.getenv("sshport"));
    }

    public String getSshKey() {
        return pro != null ? pro.getProperty("sshkey") : System.getenv("sshkey");
    }

    public String getDBHost() {
        return pro != null ? pro.getProperty("dbhost") : System.getenv("dbhost");
    }

    public Integer getDBPort() {
        return pro != null ? Integer.parseInt(pro.getProperty("dbport")) : Integer.parseInt(System.getenv("dbport"));
    }

    public String getFilaBaseUri() {
        return pro != null ? pro.getProperty("filaBaseUri") : System.getenv("filaBaseUri");
    }

    public String getBearerURL() {
        return pro != null ? pro.getProperty("bearerurl") : System.getenv("bearerurl");
    }

    public String getBearerUserName() {
        return pro != null ? pro.getProperty("bearerusername") : System.getenv("bearerusername");
    }

    public String getBearerPassword() {
        return pro != null ? pro.getProperty("bearerpassword") : System.getenv("bearerpassword");
    }

    public String getRedShiftUserName() {
        return pro != null ? pro.getProperty("redshiftusername") : System.getenv("redshiftusername");
    }

    public String getRedShiftPassword() {
        return pro != null ? pro.getProperty("redshiftpassword") : System.getenv("redshiftpassword");
    }

    public String getRedShiftURL() {
        return pro != null ? pro.getProperty("redshifturl") : System.getenv("redshifturl");
    }

    public Integer getRedShiftPort() {
        return pro != null ? Integer.parseInt(pro.getProperty("redshiftport")) : Integer.parseInt(System.getenv("redshiftport"));
    }

    public Boolean getHeadlessMode() {
        return pro != null ? Boolean.parseBoolean(pro.getProperty("headless")) : Boolean.parseBoolean(System.getenv("headless"));
    }

    public String getInsightsUsername() {
        return pro != null ? pro.getProperty("insightsusername") : System.getenv("insightsusername");
    }

    public String getInsightsSupportUsername() {
        return pro != null ? pro.getProperty("insightssupportusername") : System.getenv("insightssupportusername");
    }

    public String getInsightsPassword() {
        return pro != null ? pro.getProperty("insightspassword") : System.getenv("insightspassword");
    }

    public String getProductVariantRepoEndpoint() {
        return pro != null ? pro.getProperty("productmasterrepoendpoint") : System.getenv("productmasterrepoendpoint");
    }

    public String getInsightsApiLoginEndpoint() {
        return pro != null ? pro.getProperty("insightsapiloginendpoint") : System.getenv("insightsapiloginendpoint");
    }

    public String getInsightsApiSupportuserLoginEndpoint() {
        return pro != null ? pro.getProperty("insightsapisupportuserloginendpoint") : System.getenv("insightsapisupportuserloginendpoint");
    }

    public String getInsightsDigitalShelfAuditEndpoint() {
        return pro != null ? pro.getProperty("insightsdigitalshelfauditorendpoint") : System.getenv("insightsdigitalshelfauditorendpoint");
    }

    public String getInsightsAutomatedTestCompanyId() {
        return pro != null ? pro.getProperty("insightsautomatedtestcompanyid") : System.getenv("insightsautomatedtestcompanyid");
    }

    public String getInsightsAutomatedTestCompanyName() {
        return pro != null ? pro.getProperty("insightsautomatedtestcompanyname") : System.getenv("insightsautomatedtestcompanyname");
    }

    public String getInsightsAutomatedTestCompanyTwoId() {
        return pro != null ? pro.getProperty("insightsautomatedtestcompanytwoid") : System.getenv("insightsautomatedtestcompanytwoid");
    }

    public String getInsightsAutomationGridCompanyId() {
        return pro != null ? pro.getProperty("insightsautomationgridtestcompanyid") : System.getenv("insightsautomationgridtestcompanyid");
    }

    public String getInsightsTaskUIMappingId() {
        return pro != null ? pro.getProperty("insightstaskuimappingid") : System.getenv("insightstaskuimappingid");
    }

    public String getInsightsFamilyProductId() {
        return pro != null ? pro.getProperty(("testfamilyproductid")) : System.getenv("testfamilyproductid");
    }

    public String getInsightsBrandProductId() {
        return pro != null ? pro.getProperty(("testbrandproductid")) : System.getenv("testbrandproductid");
    }

    public String getInsightsProductRepoEndpoint() {
        return pro != null ? pro.getProperty("productRepoendpoint") : System.getenv("productRepoendpoint");
    }

    public String getEnvironment() {
        return pro != null ? pro.getProperty("environment") : System.getenv("environment");
    }

    public String getMongodbHosts() {
        return pro != null ? pro.getProperty("mongodbhosts") : System.getenv("mongodbhosts");
    }

    public String getMongoDatabase() {
        return pro != null ? pro.getProperty("mongodbdatabase") : System.getenv("mongodbdatabase");
    }

    public String getMongodbUsername() {
        return pro != null ? pro.getProperty("mongodbusername") : System.getenv("mongodbusername");
    }

    public String getMongodbPassword() {
        return pro != null ? pro.getProperty("mongodbpassword") : System.getenv("mongodbpassword");
    }

    public String getMongodbReplicaSet() {
        return pro != null ? pro.getProperty("mongodbreplicaset") : System.getenv("mongodbreplicaset");
    }

    public String getHubExternalGateway() {
        return pro != null ? pro.getProperty("externalGatewayUri") : System.getenv("externalGatewayUri");
    }

    public String getInsightsAuthServiceEndpoint() {
        return pro != null ? pro.getProperty("insightsauthserviceendpoint") : System.getenv("insightsauthserviceendpoint");
    }

    public String getInsightsJwtEndpoint() {
        return pro != null ? pro.getProperty("insigthsjwtendpoint") : System.getenv("insigthsjwtendpoint");
    }

    public String getInsightsCompanySchemaEndpoint() {
        return pro != null ? pro.getProperty("insightscompanyschemaendpoint") : System.getenv("insightscompanyschemaendpoint");
    }

    public String getBasicAuthValue() {
        return pro != null ? pro.getProperty("basicauthvalue") : System.getenv("basicauthvalue");
    }

    public String getInsightsCompanyAllCountriesEndpoint() {
        return pro != null ? pro.getProperty("insightscompanyallcountriesendpoint") : System.getenv("insightscompanyallcountriesendpoint");
    }

    public String getCpgServerUrl() {
        return pro != null ? pro.getProperty("cpgserverurl") : System.getenv("cpgserverurl");
    }

    public String getHubInsightsUsername() {
        return pro != null ? pro.getProperty("hubinsightsusername") : System.getenv("username");
    }

    public String getHubInsightsPassword() {
        return pro != null ? pro.getProperty("hubinsightspassword") : System.getenv("hubinsightspassword");
    }

    public String getMarketShareBaseUri() {
        return pro != null ? pro.getProperty("marketsharebaseuri") : System.getenv("marketsharebaseuri");
    }

    public String getaAuthServiceBaseUri() {
        return pro != null ? pro.getProperty("authservicebaseuri") : System.getenv("authservicebaseuri");
    }

    public String getCpgDataServiceUrl() {
        return pro != null ? pro.getProperty("cpgdataserviceurl") : System.getenv("cpgdataserviceurl");
    }

    public String getCpgEndpoint() {
        return pro != null ? pro.getProperty("cpgendpoint") : System.getenv("cpgendpoint");
    }

    public String getDcAppUrl() {
        return pro != null ? pro.getProperty("baseCommerceCloudAppUrl") : System.getenv("baseCommerceCloudAppUrl");
    }

    public String getInsightsEnvironment() {
        return pro != null ? pro.getProperty("insightsenvironment") : System.getenv("insightsenvironment");
    }

    public String getDcAppFilaLegacyUrl() {
        return pro != null ? pro.getProperty("dcAppFilaLegacyUrl") : System.getenv("dcAppFilaLegacyUrl");
    }

    public String getDcAppEdgeUrl() {
        return pro != null ? pro.getProperty("dcAppEdgeUrl") : System.getenv("dcAppEdgeUrl");
    }

    public String getDcAppInsightsUrl() {
        return pro != null ? pro.getProperty("commerceCloudAppInsightsUrl") : System.getenv("commerceCloudAppInsightsUrl");
    }

    public String getDcBetaUrl() {
        return pro != null ? pro.getProperty("commereCloudBetaUrl") : System.getenv("commereCloudBetaUrl");
    }

    public String getHubFilaUserEmail() {
        return pro != null ? pro.getProperty("hubfilausername") : System.getenv("hubfilausername");
    }

    public String getHubFilaUserPassword() {
        return pro != null ? pro.getProperty("hubfilapassword") : System.getenv("hubfilapassword");
    }

    public String getHubInsightsUserEmail() {
        return pro != null ? pro.getProperty("hubinsightsusername") : System.getenv("hubinsightsusername");
    }

    public String getHubInsightsUserPassword() {
        return pro != null ? pro.getProperty("hubinsightspassword") : System.getenv("hubinsightspassword");
    }

    public String getHubEdgeUserEmail() {
        return pro != null ? pro.getProperty("hubedgeusername") : System.getenv("hubedgeusername");
    }

    public String getHubEdgeUserPassword() {
        return pro != null ? pro.getProperty("hubedgepassword") : System.getenv("hubedgepassword");
    }

    public String getHubEdgeOnlyUserEmail() {
        return pro != null ? pro.getProperty("hubedgeonlyusername") : System.getenv("hubedgeonlyusername");
    }

    public String getHubEdgeOnlyUserPassword() {
        return pro != null ? pro.getProperty("hubedgeonlypassword") : System.getenv("hubedgeonlypassword");
    }

    public String getHubFilaInsightsUserEmail() {
        return pro != null ? pro.getProperty("hubfilainsightsusername") : System.getenv("hubfilainsightsusername");
    }

    public String getHubFilaInsightsUserPassword() {
        return pro != null ? pro.getProperty("hubfilainsightspassword") : System.getenv("hubfilainsightspassword");
    }

    public String getHubFilaEdgeUserEmail() {
        return pro != null ? pro.getProperty("hubfilaedgeusername") : System.getenv("hubfilaedgeusername");
    }

    public String getHubFilaEdgeUserPassword() {
        return pro != null ? pro.getProperty("hubfilaedgepassword") : System.getenv("hubfilaedgepassword");
    }

    public String getHubInsightsEdgeUserEmail() {
        return pro != null ? pro.getProperty("hubinsightsedgeusername") : System.getenv("hubinsightsedgeusername");
    }

    public String getHubInsightsEdgeUserPassword() {
        return pro != null ? pro.getProperty("hubinsightsedgepassword") : System.getenv("hubinsightsedgepassword");
    }

    public String getHubFilaInsightsEdgeUserEmail() {
        return pro != null ? pro.getProperty("hubfilainsightsedgeusername") : System.getenv("hubfilainsightsedgeusername");
    }

    public String getHubFilaInsightsEdgeUserPassword() {
        return pro != null ? pro.getProperty("hubfilainsightsedgepassword") : System.getenv("hubfilainsightsedgepassword");
    }

    public String getHubFilaOnlyUserEmail() {
        return pro != null ? pro.getProperty("hubfilaonlyusername") : System.getenv("hubfilaonlyusername");
    }

    public String getHubFilaOnlyUserPassword() {
        return pro != null ? pro.getProperty("hubfilaonlypassword") : System.getenv("hubfilaonlypassword");
    }

    public String getHubFilaOnlyUserOktaPassword() {
        return pro != null ? pro.getProperty("hubfilaonlyuseroktapassword") : System.getenv("hubfilaonlyuseroktapassword");
    }

    public String getHubInsightsSupportUsername() {
        return pro != null ? pro.getProperty("hubinsightssupportusername") : System.getenv("hubinsightssupportusername");
    }

    public String getHubInsightsSupportUserPassword() {
        return pro != null ? pro.getProperty("hubinsightssupportpassword") : System.getenv("hubinsightssupportpassword");
    }

    public String getPostgresDbHost() {
        return pro != null ? pro.getProperty("postgresdbhost") : System.getenv("postgresdbhost");
    }

    public String getPostgresDbPort() {
        return pro != null ? pro.getProperty("postgresdbport") : System.getenv("postgresdbport");
    }

    public String getPostgresDbUsername() {
        return pro != null ? pro.getProperty("postgresdbusername") : System.getenv("postgresdbusername");
    }

    public String getPostgresDbPassword() {
        return pro != null ? pro.getProperty("postgresdbpassword") : System.getenv("postgresdbpassword");
    }

    public String getHubEdgeExternalUserEmail() {
        return pro != null ? pro.getProperty("hubedgeexternalusername") : System.getenv("hubedgeexternalusername");
    }

    public String getHubEdgeExternalUserPassword() {
        return pro != null ? pro.getProperty("hubedgeexternaluserpassword") : System.getenv("hubedgeexternaluserpassword");
    }

    public String getInsightsAccountServiceUrl() {
        return pro != null ? pro.getProperty("insightsaccountserviceurl") : System.getenv("insightsaccountserviceurl");
    }

    public String getRedisHost() {
        return pro != null ? pro.getProperty("hubredishost") : System.getenv("hubredishost");
    }

    public String getRedisPort() {
        return pro != null ? pro.getProperty("hubredisport") : System.getenv("hubredisport");
    }

    public String getRedisUsername() {
        return pro != null ? pro.getProperty("hubredisusername") : System.getenv("hubredisusername");
    }

    public String getRedisPassword() {
        return pro != null ? pro.getProperty("hubredispassword") : System.getenv("hubredispassword");
    }

    public String getHubConnectUserEmail() {
        return pro != null ? pro.getProperty("hubconnectusername") : System.getenv("hubconnectusername");
    }

    public String getHubConnectUserPassword() {
        return pro != null ? pro.getProperty("hubconnectpassword") : System.getenv("hubconnectpassword");
    }

    public String getHubConnectUserOktaPassword() {
        return pro != null ? pro.getProperty("hubconnectuseroktapassword") : System.getenv("hubconnectuseroktapassword");
    }

    public String getSnowflakeDbHost() {
        return pro != null ? pro.getProperty("snowflakedbhost") : System.getenv("snowflakedbhost");
    }

    public String getSnowflakeDbPort() {
        return pro != null ? pro.getProperty("snowflakedbport") : System.getenv("snowflakedbport");
    }

    public String getSnowflakeDbUsername() {
        return pro != null ? pro.getProperty("snowflakedbusername") : System.getenv("snowflakedbusername");
    }

    public String getSnowflakeDbPassword() {
        return pro != null ? pro.getProperty("snowflakedbpassword") : System.getenv("snowflakedbpassword");
    }

    public String getAggregationServiceBaseUri() {
        return pro != null ? pro.getProperty("aggregationservicebaseuri") : System.getenv("aggregationservicebaseuri");
    }

    public String getHubFilaQaUserEmail() {
        return pro != null ? pro.getProperty("hubfilaqausername") : System.getenv("hubfilaqausername");
    }

    public String getHubFilaQaUserPassword() {
        return pro != null ? pro.getProperty("hubfilaqapassword") : System.getenv("hubfilaqapassword");
    }

    public String getCpgAccountUrl() {
        return pro != null ? pro.getProperty("cpgaccounturl") : System.getenv("cpgaccounturl");
    }

    public String getSnowflakeDatabase() {
        return pro != null ? pro.getProperty("snowflakedatabase") : System.getenv("snowflakedatabase");
    }

    public String getSnowflakeWarehouse() {
        return pro != null ? pro.getProperty("snowflakedbwarehouse") : System.getenv("snowflakedbwarehouse");
    }

    public String getSnowflakeRole() {
        return pro != null ? pro.getProperty("snowflakerole") : System.getenv("snowflakerole");
    }

    public String getAuth0ClientId() {
        return pro != null ? pro.getProperty("auth0ClientId") : System.getenv("auth0ClientId");
    }

    public String getWFLUrl() {
        return pro != null ? pro.getProperty("workflowlabsbaseuri") : System.getenv("workflowlabsbaseuri");
    }

    public String getWFLUsername() {
        return pro != null ? pro.getProperty("wflusername") : System.getenv("wflusername");
    }

    public String getWFLPassword() {
        return pro != null ? pro.getProperty("wflpassword") : System.getenv("wflpassword");
    }

    public String getWFLApiUrl() {
        return pro != null ? pro.getProperty("workflowlabsapiurl") : System.getenv("workflowlabsapiurl");
    }

    public String getAuthNonProdURL() {
        return pro != null ? pro.getProperty("authserviceuri") : System.getenv("authserviceuri");
    }

    public String getPartnerIdForWFL() {
        return pro != null ? pro.getProperty("partnerIdForWFL") : System.getenv("partnerIdForWFL");
    }

    public String getPartnerIdForSupplyChain() {
        return pro != null ? pro.getProperty("partnerIdForSupplyChain") : System.getenv("partnerIdForSupplyChain");
    }
}




