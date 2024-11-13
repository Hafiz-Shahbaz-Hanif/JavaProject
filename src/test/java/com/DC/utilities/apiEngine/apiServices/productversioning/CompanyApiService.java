package com.DC.utilities.apiEngine.apiServices.productversioning;

import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.models.requests.productVersioning.*;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompanyApiService {
    private static final Logger logger = Logger.getLogger(CompanyApiService.class);

    public static Company getCompany(String jwt) throws Exception {
        logger.info("Getting company");
        Response response = CompanyApiRequests.getCompany(jwt);
        return response.getBody().as(Company.class);
    }

    public static Company createCompany(CreateCompanyRequestBody requestBody, String jwt) throws Exception {
        logger.info("Creating company");
        Response response = CompanyApiRequests.createCompany(requestBody, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company updateCompanyName(String newCompanyName, String jwt) throws Exception {
        logger.info("Updating company name");
        Response response = CompanyApiRequests.updateCompanyName(newCompanyName, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company replaceCompanyRetailers(List<CreateCompanyRequestBody.CompanyRetailers> requestBody, String jwt) throws Exception {
        logger.info("Replacing company retailers");
        Response response = CompanyApiRequests.replaceCompanyRetailers(requestBody, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company mergeCompanyRetailers(List<CreateCompanyRequestBody.CompanyRetailers> requestBody, String jwt) throws Exception {
        logger.info("Merging company retailers");
        Response response = CompanyApiRequests.mergeCompanyRetailers(requestBody, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company deleteCompanyRetailer(String retailerId, String jwt) throws Exception {
        return deleteCompanyRetailers(Collections.singletonList(retailerId), jwt);
    }

    public static Company deleteCompanyRetailers(List<String> retailerIds, String jwt) throws Exception {
        logger.info("Deleting company retailers");
        Response response = CompanyApiRequests.deleteCompanyRetailers(retailerIds, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company replaceCompanyLocales(List<String> localeNames, String jwt) throws Exception {
        logger.info("Replacing company locales");
        Response response = CompanyApiRequests.replaceCompanyLocales(localeNames, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company mergeCompanyLocales(List<String> localeNames, String jwt) throws Exception {
        logger.info("Merging company locales");
        Response response = CompanyApiRequests.mergeCompanyLocales(localeNames, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company deleteCompanyLocales(List<String> localeIds, String jwt) throws Exception {
        logger.info("Deleting company locales");
        Response response = CompanyApiRequests.deleteCompanyLocales(localeIds, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company replaceCompanyCampaigns(List<Company.CompanyCampaign> requestBody, String jwt) throws Exception {
        logger.info("Replacing company locales");
        Response response = CompanyApiRequests.replaceCompanyCampaigns(requestBody, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company updateExistingCompanyCampaigns(List<Company.CompanyCampaign> requestBody, String jwt) throws Exception {
        logger.info("Updating company campaigns");
        Response response = CompanyApiRequests.updateExistingCompanyCampaigns(requestBody, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company mergeCompanyCampaigns(List<Company.CompanyCampaign> requestBody, String jwt) throws Exception {
        logger.info("Merging company campaigns");
        Response response = CompanyApiRequests.mergeCompanyCampaigns(requestBody, jwt);
        return response.getBody().as(Company.class);
    }

    public static Company deleteCompanyCampaign(String campaignId, String jwt) throws Exception {
        return deleteCompanyCampaigns(Collections.singletonList(campaignId), jwt);
    }

    public static Company deleteCompanyCampaigns(List<String> campaignIds, String jwt) throws Exception {
        logger.info("Deleting company campaigns");
        Response response = CompanyApiRequests.deleteCompanyCampaigns(campaignIds, jwt);
        return response.getBody().as(Company.class);
    }

    public static CompanyProperties getCompanyProperties(String companyPropertiesId, String jwt) throws Exception {
        logger.info("Getting company properties");
        Response response = CompanyApiRequests.getCompanyProperties(companyPropertiesId, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyProperties replaceCompanyProperties(CreateCompanyPropertiesRequestBody requestBody, String jwt) throws Exception {
        logger.info("Replacing company properties");
        Response response = CompanyApiRequests.replaceCompanyProperties(requestBody, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyProperties replaceRegularPropertySchema(List<CompanyProperties.Property> requestBody, String jwt) throws Exception {
        logger.info("Replacing property schema");
        Response response = CompanyApiRequests.replaceRegularPropertySchema(requestBody, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyProperties deleteRegularPropertiesFromCompany(List<String> propertyIdsToRemove, String jwt) throws Exception {
        Response response = CompanyApiRequests.deleteRegularPropertiesFromCompany(propertyIdsToRemove, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyProperties updateExistingRegularCompanyProperties(List<CompanyProperties.Property> propertiesToUpdate, String jwt) throws Exception {
        Response response = CompanyApiRequests.updateExistingRegularCompanyProperties(propertiesToUpdate, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyProperties mergeRegularPropertiesToPropertySchema(List<CompanyProperties.Property> propertiesToMerge, String jwt) throws Exception {
        Response response = CompanyApiRequests.mergeRegularPropertiesToPropertySchema(propertiesToMerge, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyProperties replaceDigitalAssetPropertySchema(List<CompanyDigitalAssetsCreate> requestBody, String jwt) throws Exception {
        logger.info("Replacing digital asset schema");
        Response response = CompanyApiRequests.replaceDigitalAssetPropertySchema(requestBody, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyProperties updateExistingDigitalAssetProperties(List<CompanyDigitalAssetsCreate> propertiesToUpdate, String jwt) throws Exception {
        Response response = CompanyApiRequests.updateExistingDigitalAssetProperties(propertiesToUpdate, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyProperties deleteDigitalAssetPropertyFromCompany(List<String> digitalAssetIdsToRemove, String jwt) throws Exception {
        Response response = CompanyApiRequests.deleteDigitalAssetPropertyFromCompany(digitalAssetIdsToRemove, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static ExportResponse exportCompanyProperties(List<String> propertyIdsToExport, String jwt) throws Exception {
        Response response = CompanyApiRequests.exportCompanyProperties(propertyIdsToExport, jwt);
        return response.getBody().as(ExportResponse.class);
    }

    public static CompanyWithProperties getCompanyWithProperties(String jwt) throws Exception {
        Response response = CompanyApiRequests.getCompanyWithProperties(jwt);
        return response.getBody().as(CompanyWithProperties.class);
    }

    public static CompanyProperties applyPropertyTemplateToCompany(String templateId, String jwt) throws Exception {
        logger.info("applying property template to company");
        Response response = CompanyApiRequests.applyPropertyTemplateToCompany(templateId, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyProperties applyPropertyTemplateToCompanyByType(Enums.CompanyPropertiesTemplateType templateType, String jwt) throws Exception {
        logger.info("applying property template " + templateType + " to company");
        Response response = CompanyApiRequests.applyPropertyTemplateToCompanyByType(templateType, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static CompanyPropertiesTemplate createCompanyPropertiesTemplate(CompanyPropertiesTemplateCreate propertiesTemplate, String jwt) throws Exception {
        Response response = CompanyApiRequests.createCompanyPropertiesTemplate(propertiesTemplate, jwt);
        return response.getBody().as(CompanyPropertiesTemplate.class);
    }

    public static CompanyPropertiesTemplate updateCompanyPropertiesTemplate(CompanyPropertiesTemplateUpdate propertiesTemplate, String templateId, String jwt) throws Exception {
        Response response = CompanyApiRequests.updateCompanyPropertiesTemplate(propertiesTemplate, templateId, jwt);
        return response.getBody().as(CompanyPropertiesTemplate.class);
    }

    public static List<CompanyPropertiesTemplate> getAllCompanyPropertiesTemplates(String jwt) throws Exception {
        Response response = CompanyApiRequests.getAllCompanyPropertiesTemplates(jwt);
        return response.getBody().as(new TypeRef<List<CompanyPropertiesTemplate>>() {});
    }

    public static List<CompanyPropertiesTemplate> getCompanyPropertiesTemplatesByType(Enums.CompanyPropertiesTemplateType templateType, String jwt) throws Exception {
        return CompanyApiService.getAllCompanyPropertiesTemplates(jwt)
                .stream()
                .filter(template -> template.templateType.equals(templateType))
                .collect(Collectors.toList());
    }

    public static String getIdOfPropertyTemplate(String templateNameToFind, String jwt) throws Exception {
        Response response = CompanyApiRequests.getAllCompanyPropertiesTemplates(jwt);
        List<Map<String, Object>> templates = response.getBody().as(new TypeRef<List<Map<String, Object>>>() {});
        return templates.stream().filter(template -> templateNameToFind.equals(template.get("name")))
                .findFirst().orElseThrow(() -> new Exception("Template not found")).get("_id").toString();

    }

    public static List<CompanyRetailerStandardized> getCompanyRetailersAvailable(String jwt) throws Exception {
        Response response = CompanyApiRequests.getCompanyRetailersAvailable(jwt);
        return response.getBody().as(new TypeRef<>() {});
    }

    public static List<CompanyLocaleStandardized> getCompanyLocalesAvailable(String jwt) throws Exception {
        Response response = CompanyApiRequests.getCompanyLocalesAvailable(jwt);
        return response.getBody().as(new TypeRef<>() {});
    }

    public static CompanyProperties deletePropertyGroupsFromCompany(List<String> propertyGroupNamesToRemove, boolean isDigitalAssetGroup, String jwt) throws Exception {
        Response response = CompanyApiRequests.deletePropertyGroupsFromCompany(propertyGroupNamesToRemove, isDigitalAssetGroup, jwt);
        return response.getBody().as(CompanyProperties.class);
    }

    public static HashMap<String, String> getIdsAndNamesOfAvailableBusinessUnits(String jwt) throws Exception {
        var businessUnitsJsonPath = CompanyApiRequests.getAvailableBusinessUnits(jwt).getBody().jsonPath();
        List<HashMap<String, Object>> businessUnits = businessUnitsJsonPath.getList("");

        HashMap<String, String> businessUnitMap = new HashMap<>();

        for (HashMap<String, Object> unit : businessUnits) {
            String businessUnitId = unit.get("businessUnitId").toString();
            String businessUnitName = unit.get("businessUnitName").toString();
            businessUnitMap.put(businessUnitId, businessUnitName);
        }
        return businessUnitMap;
    }
}
