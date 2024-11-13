package com.DC.apitests.productversioning.company;

import com.DC.apitests.productversioning.ApiTestConfig;
import com.DC.db.productVersioning.CompanyCollection;
import com.DC.db.productVersioning.CompanyPropertiesCollection;
import com.DC.db.productVersioning.CompanyVersionHistoryCollection;
import com.DC.db.productVersioning.ProductMasterCollection;
import com.DC.testcases.BaseClass;
import com.DC.utilities.ReadConfig;
import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CompanyDigitalAssetsCreate;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateCompanyPropertiesRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateCompanyRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateProductMasterRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.DC.utilities.SecurityAPI.changeInsightsCompanyAndGetJwt;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static java.util.Arrays.asList;

public class CompanyApiBaseClass extends BaseClass {
    protected static Logger logger;

    protected static String jwt;

    protected static final ApiTestConfig.TestConfig TEST_CONFIG = ApiTestConfig.getTestConfig();

    protected static final ReadConfig READ_CONFIG = ReadConfig.getInstance();

    protected static final CompanyCollection COMPANY_COLLECTION = new CompanyCollection();

    protected static final CreateCompanyRequestBody COMPANY_TO_ADD = new CreateCompanyRequestBody(
            "QA-TEST-COMPANY",
            READ_CONFIG.getInsightsAutomatedTestCompanyTwoId()
    );

    protected static final List<String> LOCALES_TO_ADD = asList("en-US", "es-MX", "fr-FR");
    protected static final List<String> RETAILER_NAMES_TO_ADD = asList("Amazon.com", "Walmart.com", "WalmartGrocery.com");
    protected static final List<CreateCompanyRequestBody.CompanyRetailers> RETAILERS_TO_ADD = new ArrayList<>();

    protected static final List<Company.CompanyCampaign> CAMPAIGNS_TO_ADD = asList(
            new Company.CompanyCampaign(UUID.randomUUID().toString(), "Halloween"),
            new Company.CompanyCampaign(UUID.randomUUID().toString(), "Christmas"),
            new Company.CompanyCampaign(UUID.randomUUID().toString(), "St. Patrick")
    );

    protected static final CreateCompanyRequestBody FULL_COMPANY_TO_ADD = new CreateCompanyRequestBody(
            "QA-TEST-COMPANY-FULL",
            READ_CONFIG.getInsightsAutomationGridCompanyId(),
            new ArrayList<>(),
            LOCALES_TO_ADD,
            CAMPAIGNS_TO_ADD.stream().map(companyCampaign -> companyCampaign.name).collect(Collectors.toList())
    );

    protected static final int randomNumber = SharedMethods.generateRandomNumber();

    protected static final CreateProductMasterRequestBody PRODUCT_TO_ADD = new CreateProductMasterRequestBody(
            "QA-COMPANY-API-TEST-" + randomNumber,
            "QA Company Api Test " + randomNumber,
            null
    );

    protected static List<CompanyRetailerStandardized> availableRetailers = new ArrayList<>();
    protected static List<CompanyLocaleStandardized> availableLocales = new ArrayList<>();

    CompanyApiBaseClass() {
        logger = Logger.getLogger(CompanyApiBaseClass.class);
        PropertyConfigurator.configure("log4j.properties");
    }

    @BeforeTest()
    public void setupTests() throws Exception {
        logger.info("Setting up company api tests");
        jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        availableRetailers = CompanyApiService.getCompanyRetailersAvailable(jwt);
        availableLocales = CompanyApiService.getCompanyLocalesAvailable(jwt);
        setRetailerToAddAndInitializeFullCompanyToAdd();
        cleanupCompany(COMPANY_TO_ADD.name);
        cleanupCompany(FULL_COMPANY_TO_ADD.name);
    }

    @AfterTest()
    public void cleanupTests() throws Exception {
        logger.info("Cleaning up company api tests");
        cleanupCompany(COMPANY_TO_ADD.name);
        cleanupCompany(FULL_COMPANY_TO_ADD.name);
    }

    protected void cleanupCompany(String companyName) throws Exception {
        Company company = COMPANY_COLLECTION.getCompanyByName(companyName);
        if (company == null) {
            return;
        }

        List<String> companyProducts = new ArrayList<>();
        try {
            String currentCompanyFromJWT = CompanyApiService.getCompany(jwt).name;
            if (!currentCompanyFromJWT.equals(companyName)) {
                jwt = changeInsightsCompanyAndGetJwt(jwt, company._id, company.name);
            }
            companyProducts = ProductVersioningApiService.getAllProductMastersFromCompany(jwt).stream().map(productMaster -> productMaster._id).collect(Collectors.toList());
        } catch (Exception e) {
            logger.info("Unable to get products from company");
        }

        for (String productId : companyProducts) {
            ProductVersioningApiService.deleteProductMaster(productId, jwt);
        }

        COMPANY_COLLECTION.deleteCompany(company._id);
        new CompanyVersionHistoryCollection().deleteCompanyVersionHistory(company._id);
        new CompanyPropertiesCollection().deleteAllCompanyProperties(company._id);
    }

    protected Company createCompanyIfNeeded(CreateCompanyRequestBody companyToAdd) throws Exception {
        Company company = COMPANY_COLLECTION.getCompanyByName(companyToAdd.name);

        if (company == null) {
            company = CompanyApiService.createCompany(companyToAdd, jwt);
        }

        String currentCompanyFromJWT = CompanyApiService.getCompany(jwt).name;
        if (!currentCompanyFromJWT.equals(companyToAdd.name)) {
            jwt = changeInsightsCompanyAndGetJwt(jwt, company._id, company.name);
        }

        return company;
    }

    protected CreateCompanyPropertiesRequestBody getCompanyPropertiesToAdd() {
        CompanyProperties.Group group = new CompanyProperties.Group("Group 1", null, 0, new ArrayList<>());
        CompanyProperties.Group group2 = new CompanyProperties.Group("Group 2", null, 1, new ArrayList<>());
        CompanyProperties.Group imageWorkflowMapping = new CompanyProperties.Group("Image Workflow Mapping", null, 2, new ArrayList<>());
        CompanyProperties.Group imageWorkflowInstructions = new CompanyProperties.Group("Image Workflow Instructions", null, 3, new ArrayList<>());

        CompanyProperties.Group digitalAssetGroup = new CompanyProperties.Group("Group Digital Asset 1", null, 0, new ArrayList<>());
        CompanyProperties.Group digitalAssetGroup2 = new CompanyProperties.Group("Group Digital Asset 2", null, 1, new ArrayList<>());
        CompanyProperties.Group taskPropertiesAssetsGroup = new CompanyProperties.Group("Task Properties Assets", null, 2, new ArrayList<>());

        List<CompanyProperties.Group> groups = asList(group, group2, imageWorkflowMapping, imageWorkflowInstructions);
        List<CompanyProperties.Group> digitalAssetGroups = asList(digitalAssetGroup, digitalAssetGroup2, taskPropertiesAssetsGroup);

        CompanyDigitalAssetsCreate digitalAssetProperty = new CompanyDigitalAssetsCreate();
        digitalAssetProperty.id = "digital_asset_property";
        digitalAssetProperty.name = "Digital Asset Property";
        digitalAssetProperty.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty.helpText = null;
        digitalAssetProperty.allowMultipleValues = false;
        digitalAssetProperty.dropdownValues = null;
        digitalAssetProperty.group = null;
        digitalAssetProperty.addImageMappingSpecs = true;

        CompanyDigitalAssetsCreate digitalAssetProperty2 = new CompanyDigitalAssetsCreate();
        digitalAssetProperty2.id = "digital_asset_property_2";
        digitalAssetProperty2.name = "Digital Asset Property 2";
        digitalAssetProperty2.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty2.helpText = null;
        digitalAssetProperty2.allowMultipleValues = false;
        digitalAssetProperty2.dropdownValues = null;
        digitalAssetProperty2.group = digitalAssetGroups.get(0).name;

        CompanyDigitalAssetsCreate digitalAssetProperty3 = new CompanyDigitalAssetsCreate();
        digitalAssetProperty3.id = "digital_asset_property_3";
        digitalAssetProperty3.name = "Digital Asset Property 3";
        digitalAssetProperty3.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty3.helpText = null;
        digitalAssetProperty3.allowMultipleValues = false;
        digitalAssetProperty3.dropdownValues = null;
        digitalAssetProperty3.group = digitalAssetGroups.get(1).name;
        digitalAssetProperty3.addImageMappingSpecs = true;

        CompanyDigitalAssetsCreate digitalAssetProperty4 = new CompanyDigitalAssetsCreate();
        digitalAssetProperty4.id = "digital_asset_property_4";
        digitalAssetProperty4.name = "Digital Asset Property 4";
        digitalAssetProperty4.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty4.helpText = null;
        digitalAssetProperty4.allowMultipleValues = false;
        digitalAssetProperty4.dropdownValues = null;
        digitalAssetProperty4.group = digitalAssetGroups.get(1).name;

        CompanyDigitalAssetsCreate digitalAssetProperty5 = new CompanyDigitalAssetsCreate();
        digitalAssetProperty5.id = "digital_asset_property_5";
        digitalAssetProperty5.name = "Digital Asset Property 5";
        digitalAssetProperty5.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty5.helpText = null;
        digitalAssetProperty5.allowMultipleValues = true;
        digitalAssetProperty5.dropdownValues = null;
        digitalAssetProperty5.group = digitalAssetGroups.get(1).name;

        CompanyProperties.Property property = new CompanyProperties.Property();
        property.id = "autogenerated_property";
        property.name = "Autogenerated Property";
        property.type = Enums.PropertyType.STRING;
        property.helpText = null;
        property.allowMultipleValues = false;
        property.group = groups.get(0).name;

        CompanyProperties.Property property2 = new CompanyProperties.Property();
        property2.id = "autogenerated_property_2";
        property2.name = "Autogenerated Property 2";
        property2.type = Enums.PropertyType.DROPDOWN;
        property2.helpText = null;
        property2.allowMultipleValues = true;
        property2.dropdownValues = Collections.singletonList(new CompanyProperties.PropertyDropdownValue("value_1", "value"));
        property2.group = groups.get(0).name;

        CompanyProperties.Property property3 = new CompanyProperties.Property();
        property3.id = "autogenerated_property_3";
        property3.name = "Autogenerated Property 3";
        property3.type = Enums.PropertyType.NUMBER;
        property3.helpText = null;
        property3.allowMultipleValues = false;
        property3.dropdownValues = null;
        property3.group = groups.get(1).name;

        CompanyProperties.Property property4 = new CompanyProperties.Property();
        property4.id = "autogenerated_property_4";
        property4.name = "Autogenerated Property 4";
        property4.type = Enums.PropertyType.STRING;
        property4.helpText = null;
        property4.allowMultipleValues = true;
        property4.dropdownValues = null;
        property4.group = groups.get(1).name;

        CompanyProperties.Property property5 = new CompanyProperties.Property();
        property5.id = "autogenerated_property_5";
        property5.name = "Autogenerated Property 5";
        property5.type = Enums.PropertyType.STRING;
        property5.helpText = null;
        property5.allowMultipleValues = true;
        property5.dropdownValues = null;
        property5.group = groups.get(1).name;

        CompanyProperties.Property propertyFromTemplate = new CompanyProperties.Property();
        propertyFromTemplate.id = "autogenerated_property_template";
        propertyFromTemplate.name = "Autogenerated Property Template";
        propertyFromTemplate.type = Enums.PropertyType.STRING;
        propertyFromTemplate.helpText = "Customized Help Text";
        propertyFromTemplate.allowMultipleValues = true;
        propertyFromTemplate.group = group.name;

        return new CreateCompanyPropertiesRequestBody(
                "Autogenerated Test Properties",
                asList(digitalAssetProperty, digitalAssetProperty2, digitalAssetProperty3, digitalAssetProperty4, digitalAssetProperty5),
                asList(property, property2, property3, property4, property5, propertyFromTemplate),
                groups,
                digitalAssetGroups
        );
    }

    protected ProductMaster createProductAndVariantIfNeeded(String jwt, String companyId, String localeId) throws Exception {
        ProductMaster productMaster = new ProductMasterCollection().getProductMaster(PRODUCT_TO_ADD.uniqueId, companyId);

        if (productMaster == null) {
            productMaster = ProductVersioningApiService.createProductMaster(PRODUCT_TO_ADD, jwt);
            return ProductVersioningApiService.createProductVariant(productMaster._id, localeId, jwt);
        } else {
            boolean variantExists = productMaster
                    .variantSets.live
                    .stream().anyMatch(variant -> variant.localeId.equals(localeId));

            if (!variantExists) {
                return ProductVersioningApiService.createProductVariant(productMaster._id, localeId, jwt);
            }
        }

        return productMaster;
    }

    private static void setRetailerToAddAndInitializeFullCompanyToAdd() {
        List<CreateCompanyRequestBody.CompanyRetailers> retailersToAdd = availableRetailers.stream()
                .filter(availableRetailer -> RETAILER_NAMES_TO_ADD.contains(availableRetailer.retailerName))
                .map(availableRetailer -> new CreateCompanyRequestBody.CompanyRetailers(availableRetailer.domainId, availableRetailer.retailerName))
                .collect(Collectors.toList());

        RETAILERS_TO_ADD.addAll(retailersToAdd);
        FULL_COMPANY_TO_ADD.retailers = RETAILERS_TO_ADD;
    }

    protected List<Company.CompanyRetailers> getExpectedRetailers(List<CreateCompanyRequestBody.CompanyRetailers> companyRetailersToReplaceWith) {
        List<Company.CompanyRetailers> expectedRetailers = new ArrayList<>();

        for (CreateCompanyRequestBody.CompanyRetailers retailer : companyRetailersToReplaceWith) {
            CompanyRetailerStandardized retailerStandardized = availableRetailers.stream()
                    .filter(r -> (r.domainId == retailer.retailerDomainId) && (r.retailerName.equals(retailer.clientRetailerName)))
                    .findFirst()
                    .orElseThrow(null);

            var expectedRetailer = new Company.CompanyRetailers(retailerStandardized._id, retailerStandardized.domainId, retailerStandardized.retailerName);
            boolean retailerAlreadyExists = expectedRetailers.stream().anyMatch(r -> r.systemRetailerId.equals(expectedRetailer.systemRetailerId));
            if (!retailerAlreadyExists) {
                expectedRetailers.add(expectedRetailer);
            }
        }
        return expectedRetailers;
    }
}


