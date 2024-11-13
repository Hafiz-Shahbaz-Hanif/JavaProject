package com.DC.utilities.apiEngine.apiRequests.productVersioning;

import com.DC.utilities.apiEngine.models.requests.productVersioning.*;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyProperties;
import com.DC.utilities.apiEngine.routes.productVersioning.CompanyRoutes;
import com.DC.utilities.apiEngine.routes.productVersioning.SharedRoutes;
import com.DC.utilities.enums.Enums;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.DC.utilities.CommonApiMethods.callEndpoint;

public class CompanyApiRequests extends SharedRequests {
    public static Response getCompany(String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyHost(), jwt, "GET", "", "");
    }

    public static Response createCompany(CreateCompanyRequestBody requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return createCompany(reqBody, jwt);
    }

    public static Response createCompany(String requestBody, String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyHost(), jwt, "POST", requestBody, "");
    }

    public static Response updateCompanyName(String newCompanyName, String jwt) throws Exception {
        String reqBody = "{\"name\"" + ": \"" + newCompanyName + "\"}";
        return callEndpoint(CompanyRoutes.getCompanyHost(), jwt, "PUT", reqBody, "");
    }

    public static Response replaceCompanyRetailers(List<CreateCompanyRequestBody.CompanyRetailers> requestBody, String jwt) throws Exception {
        Map<String, List<CreateCompanyRequestBody.CompanyRetailers>> bodyData = new HashMap<>();
        bodyData.put("retailers", requestBody);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyRetailersRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response mergeCompanyRetailers(List<CreateCompanyRequestBody.CompanyRetailers> requestBody, String jwt) throws Exception {
        Map<String, List<CreateCompanyRequestBody.CompanyRetailers>> bodyData = new HashMap<>();
        bodyData.put("retailers", requestBody);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyRetailersRoutePath(), jwt, "PATCH", reqBody, "");
    }

    public static Response deleteCompanyRetailers(List<String> retailerIds, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("retailerIds", retailerIds);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyRetailersRoutePath(), jwt, "DELETE", reqBody, "");
    }

    public static Response replaceCompanyLocales(List<String> localeNames, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("locales", localeNames);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyLocalesRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response mergeCompanyLocales(List<String> localeNames, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("locales", localeNames);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyLocalesRoutePath(), jwt, "PATCH", reqBody, "");
    }

    public static Response deleteCompanyLocales(List<String> localeIds, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("localeIds", localeIds);
        ObjectMapper objectMapper = new ObjectMapper();
        String reqBody = objectMapper.writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyLocalesRoutePath(), jwt, "DELETE", reqBody, "");
    }

    public static Response replaceCompanyCampaigns(List<Company.CompanyCampaign> requestBody, String jwt) throws Exception {
        Map<String, List<Company.CompanyCampaign>> bodyData = new HashMap<>();
        bodyData.put("campaigns", requestBody);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyCampaignsRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response updateExistingCompanyCampaigns(List<Company.CompanyCampaign> requestBody, String jwt) throws Exception {
        Map<String, List<Company.CompanyCampaign>> bodyData = new HashMap<>();
        bodyData.put("campaigns", requestBody);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyCampaignsRoutePath(), jwt, "PUT", reqBody, "");
    }

    public static Response mergeCompanyCampaigns(List<Company.CompanyCampaign> requestBody, String jwt) throws Exception {
        Map<String, List<Company.CompanyCampaign>> bodyData = new HashMap<>();
        bodyData.put("campaigns", requestBody);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyCampaignsRoutePath(), jwt, "PATCH", reqBody, "");
    }

    public static Response deleteCompanyCampaigns(List<String> campaignIds, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("campaignIds", campaignIds);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyCampaignsRoutePath(), jwt, "DELETE", reqBody, "");
    }

    public static Response getCompanyProperties(String companyPropertiesId, String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyPropertiesRoutePath(companyPropertiesId), jwt, "GET", "", "");
    }

    public static Response replaceCompanyProperties(CreateCompanyPropertiesRequestBody requestBody, String jwt) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String reqBody = objectMapper.writeValueAsString(requestBody);
        return replaceCompanyProperties(reqBody, jwt);
    }

    public static Response replaceCompanyProperties(String requestBody, String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyPropertiesRoutePath(), jwt, "PUT", requestBody, "");
    }

    public static Response replaceRegularPropertySchema(List<CompanyProperties.Property> requestBody, String jwt) throws Exception {
        Map<String, List<CompanyProperties.Property>> bodyData = new HashMap<>();
        bodyData.put("propertySchema", requestBody);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return replaceRegularPropertySchema(reqBody, jwt);
    }

    public static Response replaceRegularPropertySchema(String reqBody, String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyPropertySchemaRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response deleteRegularPropertiesFromCompany(List<String> propertyIdsToRemove, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("propertyIds", propertyIdsToRemove);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return deleteRegularPropertiesFromCompany(reqBody, jwt);
    }

    public static Response deleteRegularPropertiesFromCompany(String reqBody, String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyPropertySchemaRoutePath(), jwt, "DELETE", reqBody, "");
    }

    public static Response updateExistingRegularCompanyProperties(List<CompanyProperties.Property> propertiesToUpdate, String jwt) throws Exception {
        Map<String, List<CompanyProperties.Property>> bodyData = new HashMap<>();
        bodyData.put("propertySchema", propertiesToUpdate);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyPropertySchemaRoutePath(), jwt, "PUT", reqBody, "");
    }

    public static Response mergeRegularPropertiesToPropertySchema(List<CompanyProperties.Property> propertiesToMerge, String jwt) throws Exception {
        Map<String, List<CompanyProperties.Property>> bodyData = new HashMap<>();
        bodyData.put("propertySchema", propertiesToMerge);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyPropertySchemaRoutePath(), jwt, "PATCH", reqBody, "");
    }

    public static Response replaceDigitalAssetPropertySchema(List<CompanyDigitalAssetsCreate> requestBody, String jwt) throws Exception {
        Map<String, List<CompanyDigitalAssetsCreate>> bodyData = new HashMap<>();
        bodyData.put("propertySchema", requestBody);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return replaceDigitalAssetPropertySchema(reqBody, jwt);
    }

    public static Response replaceDigitalAssetPropertySchema(String reqBody, String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyDigitalAssetsSchemaRoutePath(), jwt, "POST", reqBody, "");
    }

    public static Response updateExistingDigitalAssetProperties(List<CompanyDigitalAssetsCreate> propertiesToUpdate, String jwt) throws Exception {
        Map<String, List<CompanyDigitalAssetsCreate>> bodyData = new HashMap<>();
        bodyData.put("propertySchema", propertiesToUpdate);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyDigitalAssetsSchemaRoutePath(), jwt, "PUT", reqBody, "");
    }

    public static Response mergeDigitalAssetPropertiesToDigitalAssetSchema(List<CompanyDigitalAssetsCreate> propertiesToMerge, String jwt) throws Exception {
        Map<String, List<CompanyDigitalAssetsCreate>> bodyData = new HashMap<>();
        bodyData.put("propertySchema", propertiesToMerge);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyDigitalAssetsSchemaRoutePath(), jwt, "PATCH", reqBody, "");
    }

    public static Response deleteDigitalAssetPropertyFromCompany(List<String> digitalAssetIdsToRemove, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("propertyIds", digitalAssetIdsToRemove);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return callEndpoint(CompanyRoutes.getCompanyDigitalAssetsSchemaRoutePath(), jwt, "DELETE", reqBody, "");
    }

    public static Response exportCompanyProperties(List<String> propertyIdsToExport, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("propertyIds", propertyIdsToExport);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);
        return exportCompanyProperties(reqBody, jwt);
    }

    public static Response exportCompanyProperties(String requestBody, String jwt) throws Exception {
        return callEndpoint(SharedRoutes.getProductVariantExportRoutePathDebug("company"), jwt, "POST", requestBody, "");
    }

    public static Response getCompanyWithProperties(String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyHost() + "/with-properties", jwt, "GET", "", "");
    }

    public static Response applyPropertyTemplateToCompany(String templateId, String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyPropertiesRoutePath() + "/template/" + templateId, jwt, "PUT", "", "");
    }

    public static Response applyPropertyTemplateToCompanyByType(Enums.CompanyPropertiesTemplateType templateType, String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyPropertiesRoutePath() + "/template/type/" + templateType.toString().toLowerCase(), jwt, "PUT", "", "");
    }

    public static Response createCompanyPropertiesTemplate(CompanyPropertiesTemplateCreate requestBody, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return callEndpoint(CompanyRoutes.getCompanyPropertiesTemplateHost(), jwt, "POST", reqBody, "");
    }

    public static Response updateCompanyPropertiesTemplate(CompanyPropertiesTemplateUpdate requestBody, String templateId, String jwt) throws Exception {
        String reqBody = new ObjectMapper().writeValueAsString(requestBody);
        return callEndpoint(CompanyRoutes.getCompanyPropertiesTemplateHost() + "/" + templateId, jwt, "PUT", reqBody, "");
    }

    public static Response getAllCompanyPropertiesTemplates(String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyPropertiesTemplateHost(), jwt, "GET", "", "");
    }

    public static Response getCompanyRetailersAvailable(String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyRetailersHost(), jwt, "GET", "", "");
    }

    public static Response getCompanyLocalesAvailable(String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.getCompanyLocalesHost(), jwt, "GET", "", "");
    }

    public static Response getAvailableBusinessUnits(String jwt) throws Exception {
        return callEndpoint(CompanyRoutes.COMPANY_HOST + "/business-units", jwt, "GET", "", "");
    }

    public static Response deletePropertyGroupsFromCompany(List<String> propertyGroupNamesToRemove, boolean isDigitalAssetGroup, String jwt) throws Exception {
        Map<String, List<String>> bodyData = new HashMap<>();
        bodyData.put("groups", propertyGroupNamesToRemove);
        String reqBody = new ObjectMapper().writeValueAsString(bodyData);

        var reqURI = isDigitalAssetGroup ? CompanyRoutes.getCompanyPropertiesDigitalAssetGroupsRoutePath() : CompanyRoutes.getCompanyPropertiesGroupsRoutePath();
        return callEndpoint(reqURI, jwt, "DELETE", reqBody, "");
    }
}
