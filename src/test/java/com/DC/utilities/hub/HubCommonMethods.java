package com.DC.utilities.hub;

import com.DC.utilities.apiEngine.apiRequests.hub.aggregation.AggregationServiceApiRequest;
import com.DC.utilities.apiEngine.apiRequests.hub.authservice.AuthServiceApiRequest;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class HubCommonMethods {

    public Response getUserHubInfo(String authToken) throws Exception {
        Response userInfoResponse =	AuthServiceApiRequest.getUserAuthorizationInfo(authToken);
        Assert.assertEquals(userInfoResponse.statusCode(), 200, "Cannot get aggregated user info.");
        return userInfoResponse;
    }

    public Object getUserPlatformInfo(Response userInfoResponse, String platform) throws Exception {
        Object user = null;
        String userInfo = userInfoResponse.getBody().asString();
        JSONArray userInfoForFilaUser = new JSONObject(userInfo).getJSONArray("data");

        for(int i = 0; i < userInfoForFilaUser.length(); i++) {
            JSONObject json = userInfoForFilaUser.getJSONObject(i);
            String type = json.getString("type");

            if (type.equalsIgnoreCase("authinfo")){
                String legacyPlatformName = json.getString("legacyPlatformName");
                if (legacyPlatformName.equalsIgnoreCase(platform) && platform.equalsIgnoreCase("onespace")) {
                    user = json.getJSONObject("data").getString("token");
                } else if (legacyPlatformName.equalsIgnoreCase(platform) && platform.equalsIgnoreCase("fila")) {
                    user = json.getJSONObject("data");
                } else if (legacyPlatformName.equalsIgnoreCase(platform) && platform.equalsIgnoreCase("marketshare")) {
                    user = json.getJSONObject("data");
                }
            } else if (type.equalsIgnoreCase("userinfo") && platform.equalsIgnoreCase("netnew")) {
                user = json.getJSONObject("data");
            }
        }
        return user;
    }

    public List<JSONObject> getOrganizationsForUser(JSONObject hubUserInfoForFilaUser){
        JSONArray userOrgs = hubUserInfoForFilaUser.getJSONArray("userOrganizations");
        List<JSONObject> orgs = new ArrayList<>();
        for (Object userOrg : userOrgs) {
            JSONObject org = (JSONObject) userOrg;
            orgs.add(org);
        }
        return orgs;
    }

    public List<String> getOrganizationIdsForUser(JSONObject hubUserInfoForFilaUser){
        List<JSONObject> userOrgs = getOrganizationsForUser(hubUserInfoForFilaUser);
        List<String> orgIds = new ArrayList<>();
        for (JSONObject userOrg : userOrgs) {
            JSONObject org = userOrg;
            orgIds.add(org.getString("organizationId"));
        }
        Collections.sort(orgIds);
        return orgIds;
    }

    public List<JSONObject> getBusinessUnitsForUser(JSONObject organization){
        JSONArray UserBus = organization.getJSONArray("businessUnits");
        List<JSONObject> bus = new ArrayList<>();
        for (Object UserBu : UserBus) {
            JSONObject bu = (JSONObject) UserBu;
            bus.add(bu);
        }
        return bus;
    }

    public List<String> getBusinessUnitIdsForUser(JSONObject organization){
        List<JSONObject> userBus = getBusinessUnitsForUser(organization);
        List<String> buIds = new ArrayList<>();
        for (JSONObject userBu : userBus) {
            JSONObject bu = userBu;
            buIds.add(bu.getString("businessUnitId"));
        }
        Collections.sort(buIds);
        return buIds;
    }

    public List<JSONObject> getBuRetailersForUser(JSONObject businessUnits){
        JSONArray UserBuRetailers = businessUnits.getJSONArray("retailerPlatforms");
        List<JSONObject> buRetailers = new ArrayList<>();
        for (Object UserBuRetailer : UserBuRetailers) {
            JSONObject buRetailer = (JSONObject) UserBuRetailer;
            buRetailers.add(buRetailer);
        }
        return buRetailers;
    }

    public List<String> getBuRetailerIdsForUser(JSONObject businessUnit){
        List<JSONObject> userBuRetailers = getBuRetailersForUser(businessUnit);
        List<String> buRetailerIds = new ArrayList<>();
        for (JSONObject userBuRetailer : userBuRetailers) {
            JSONObject retailer = userBuRetailer;
            buRetailerIds.add(retailer.getString("retailerId"));
        }
        Collections.sort(buRetailerIds);
        return buRetailerIds;
    }

    public List<JSONObject> getBuRetailerPlatformsForUser(JSONObject retailers){
        JSONArray userBuRetailerPlatforms = retailers.getJSONArray("platforms");
        List<JSONObject> buRetailerPlarforms = new ArrayList<>();
        for (Object userBuRetailerPlatform : userBuRetailerPlatforms) {
            JSONObject buRetailerPlatform = (JSONObject) userBuRetailerPlatform;
            buRetailerPlarforms.add(buRetailerPlatform);
        }
        return buRetailerPlarforms;
    }

    public List<String> getBuRetailerPlatformIdsForUser(JSONObject retailers){
        List<JSONObject> userBuRetailerPlatforms = getBuRetailerPlatformsForUser(retailers);
        List<String> buRetailerPlatformIds = new ArrayList<>();
        for (JSONObject userBuRetailerPlatform : userBuRetailerPlatforms) {
            JSONObject retailerPlatform = userBuRetailerPlatform;
            buRetailerPlatformIds.add(retailerPlatform.getString("retailerPlatformId"));
        }
        Collections.sort(buRetailerPlatformIds);
        return buRetailerPlatformIds;
    }

    public List<String> getBuRetailerPlatformIdsForAuthorizeUser(JSONObject retailers){
        List<JSONObject> userBuRetailerPlatforms = getBuRetailerPlatformsForUser(retailers);
        List<String> buRetailerPlatformIds = new ArrayList<>();
        for (JSONObject userBuRetailerPlatform : userBuRetailerPlatforms) {
            JSONObject retailerPlatform = userBuRetailerPlatform;
            buRetailerPlatformIds.add(retailerPlatform.getString("platformId"));
        }
        Collections.sort(buRetailerPlatformIds);
        return buRetailerPlatformIds;
    }

    public String decodeInsightsJwtAndGetUserId(String jwt) {
        String[] body = jwt.split("\\.");
        JSONObject jsonBody = new JSONObject(new String(Base64.getUrlDecoder().decode(body[1])));
        return jsonBody.getString("urn:onespace:user:id");
    }

    public String decodeInsightsJwtToGetCompanyId(String jwt) {
        String[] body = jwt.split("\\.");
        JSONObject jsonBody = new JSONObject(new String(Base64.getUrlDecoder().decode(body[1])));
        return jsonBody.getString("cpgCompanyId");
    }

    public String getAuth0IdFromToken(String auth0Token) {
        String token = auth0Token.startsWith("Bearer") ? auth0Token.split("Bearer ")[1] : auth0Token;
        String[] body = token.split("\\.");
        JSONObject jsonBody = new JSONObject(new String(Base64.getUrlDecoder().decode(body[1])));
        return jsonBody.getString("sub");
    }

    public List<JSONObject> getBusinessUnitsForAuthorizeUser(JSONObject authorizeUser){
        JSONObject user = authorizeUser.getJSONObject("data");
        JSONArray UserBus = user.getJSONArray("businessUnitProvisions");
        List<JSONObject> bus = new ArrayList<>();
        for (Object UserBu : UserBus) {
            JSONObject bu = (JSONObject) UserBu;
            bus.add(bu);
        }
        return bus;
    }

    public List<String> getBusinessUnitIdsForAuthorizeUser(JSONObject authorizeUser){
        List<JSONObject> userBus = getBusinessUnitsForAuthorizeUser(authorizeUser);
        List<String> buIds = new ArrayList<>();
        for (JSONObject userBu : userBus) {
            JSONObject bu = userBu;
            buIds.add(bu.getString("businessUnitId"));
        }
        Collections.sort(buIds);
        return buIds;
    }

    public List<JSONObject> getModulesForBu(JSONObject bu) {
        JSONArray buModules = bu.getJSONArray("modules");
        List<JSONObject> modules = new ArrayList<>();
        for (Object buModule : buModules) {
            JSONObject module = (JSONObject) buModule;
            modules.add(module);
        }
        return modules;
    }

    public List<String> getModulePrivileges(JSONObject module) {
        JSONArray modulePrivileges = module.getJSONArray("privileges");
        List<String> privileges = new ArrayList<>();
        for (int i = 0; i < modulePrivileges.length(); i++) {
            privileges.add(modulePrivileges.getString(i));
        }
        Collections.sort(privileges);
        return privileges;
    }

    public List<JSONObject> getBusinessUnitModulesForUserInfo(JSONObject userInfo){
        JSONObject moduleProvisions = userInfo.getJSONObject("moduleProvisions");
        return getBusinessUnitModulesForUser(moduleProvisions);
    }

    public List<JSONObject> getBusinessUnitModulesForAuthorizeUser(JSONObject authorizeUser){
        JSONObject user = authorizeUser.getJSONObject("data");
        JSONObject moduleProvisions = user.getJSONObject("moduleProvisions");
        return getBusinessUnitModulesForUser(moduleProvisions);
    }

    public List<JSONObject> getBusinessUnitModulesForUser(JSONObject moduleProvisions){
        JSONArray businessUnitModules = moduleProvisions.getJSONArray("businessUnitModules");
        List<JSONObject> buModules = new ArrayList<>();
        for (Object businessUnitModule : businessUnitModules) {
            JSONObject buModule = (JSONObject) businessUnitModule;
            buModules.add(buModule);
        }
        return buModules;
    }

    public List<String> getBusinessUnitIdsForModules(List<JSONObject> buModules){
        List<String> buIds = new ArrayList<>();
        for (JSONObject userBu : buModules) {
            JSONObject bu = userBu;
            buIds.add(bu.getString("businessUnitId"));
        }
        Collections.sort(buIds);
        return buIds;
    }

//    public List<String> getBusinessUnitIdsForModules(JSONObject authorizeUser){
//        List<JSONObject> userBus = getBusinessUnitModulesForAuthorizeUser(authorizeUser);
//        List<String> buIds = new ArrayList<>();
//        for (JSONObject userBu : userBus) {
//            JSONObject bu = userBu;
//            buIds.add(bu.getString("businessUnitId"));
//        }
//        Collections.sort(buIds);
//        return buIds;
//    }

//    public List<JSONObject> getBusinessUnitAgnosticModulesForAuthorizeUser(JSONObject authorizeUser){
//        JSONObject user = authorizeUser.getJSONObject("data");
//        JSONObject moduleProvisions = user.getJSONObject("moduleProvisions");
//        JSONArray businessUnitModules = moduleProvisions.getJSONArray("userModules");
//        List<JSONObject> buModules = new ArrayList<>();
//        for (Object businessUnitModule : businessUnitModules) {
//            JSONObject buModule = (JSONObject) businessUnitModule;
//            buModules.add(buModule);
//        }
//        return buModules;
//    }

    public List<JSONObject> getBusinessUnitAgnosticModulesForUserInfo(JSONObject userInfo){
        JSONObject moduleProvisions = userInfo.getJSONObject("moduleProvisions");
        return getBusinessUnitAgnosticModulesForUser(moduleProvisions);
    }

    public List<JSONObject> getBusinessUnitAgnosticModulesForAuthorizeUser(JSONObject authorizeUser){
        JSONObject user = authorizeUser.getJSONObject("data");
        JSONObject moduleProvisions = user.getJSONObject("moduleProvisions");
        return getBusinessUnitAgnosticModulesForUser(moduleProvisions);
    }

    public List<JSONObject> getBusinessUnitAgnosticModulesForUser(JSONObject moduleProvisions){
        JSONArray businessUnitModules = moduleProvisions.getJSONArray("userModules");
        List<JSONObject> buModules = new ArrayList<>();
        for (Object businessUnitModule : businessUnitModules) {
            JSONObject buModule = (JSONObject) businessUnitModule;
            buModules.add(buModule);
        }
        return buModules;
    }

    public List<String> getBusinessUnitAgnosticModuleIdsForAuthorizeUser(List<JSONObject> userModules){
        List<String> buIds = new ArrayList<>();
        for (JSONObject userBu : userModules) {
            JSONObject bu = userBu;
            buIds.add(bu.getString("moduleId"));
        }
        Collections.sort(buIds);
        return buIds;
    }


//    public List<String> getBusinessUnitAgnosticModuleIdsForAuthorizeUser(JSONObject authorizeUser){
//        List<JSONObject> userBus = getBusinessUnitAgnosticModulesForAuthorizeUser(authorizeUser);
//        List<String> buIds = new ArrayList<>();
//        for (JSONObject userBu : userBus) {
//            JSONObject bu = userBu;
//            buIds.add(bu.getString("moduleId"));
//        }
//        Collections.sort(buIds);
//        return buIds;
//    }

    public List<JSONObject> getModulesForBusinessUnit(JSONObject buModule){
        JSONArray modules = buModule.getJSONArray("modules");
        List<JSONObject> buModules = new ArrayList<>();
        for (Object module : modules) {
            JSONObject bModule = (JSONObject) module;
            buModules.add(bModule);
        }
        return buModules;
    }

    public List<String> getModuleIdsForBusinessUnit(JSONObject buModule){
        List<JSONObject> buModules = getModulesForBusinessUnit(buModule);
        List<String> moduleIds = new ArrayList<>();
        for (JSONObject module : buModules) {
            JSONObject bModule = module;
            moduleIds.add(bModule.getString("moduleId"));
        }
        Collections.sort(moduleIds);
        return moduleIds;
    }

    public Response getAuthorizationAggregationInfo(String authToken) throws Exception {
        Response userInfoResponse =	AggregationServiceApiRequest.getUserAuthorizationAggregationInfo(authToken);
        Assert.assertEquals(userInfoResponse.statusCode(), 200, "Cannot get authorization aggregation info for user.");
        return userInfoResponse;
    }

    public Object getAuthorizationAggregationPlatformInfo(Response userInfoResponse, String platform) throws Exception {
        Object user = null;
        String userInfo = userInfoResponse.getBody().asString();
        JSONArray userInfoForFilaUser = new JSONObject(userInfo).getJSONArray("data");

        for(int i = 0; i < userInfoForFilaUser.length(); i++) {
            JSONObject json = userInfoForFilaUser.getJSONObject(i);
            String type = json.getString("type");

            if(type.equalsIgnoreCase("jwt") && platform.equalsIgnoreCase("onespace")) {
                user = json;
            } else if (type.equals("authinfo") && (platform.equalsIgnoreCase("fila") || platform.equalsIgnoreCase("marketshare"))) {
                String legacyPlatformName = json.getString("legacyPlatformName");
                if (legacyPlatformName.equalsIgnoreCase("fila") && platform.equalsIgnoreCase("fila")) {
                    user = json;
                } else if (legacyPlatformName.equalsIgnoreCase("marketshare") && platform.equalsIgnoreCase("marketshare")) {
                    user = json;
                }
            }
        }
        return user;
    }


}