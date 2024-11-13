package com.DC.apitests.productversioning.company;

import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.*;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.apiEngine.routes.productVersioning.CompanyRoutes;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.*;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static com.DC.utilities.CommonApiMethods.callEndpoint;
import static java.util.Arrays.asList;

public class CompanyApiTests extends CompanyApiBaseClass {
    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanCreateEmptyCompany() throws Exception {
        cleanupCompany(COMPANY_TO_ADD.name);

        Response response = CompanyApiRequests.createCompany(COMPANY_TO_ADD, jwt);

        Company companyCreated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertNotNull(companyCreated._id, "Company Id was null");
        Assert.assertEquals(companyCreated._version, 1, "Version of company doesn't match with the expected version");
        Assert.assertEquals(companyCreated.name, COMPANY_TO_ADD.name, "Company name doesn't match with the expected name");
        Assert.assertNotNull(companyCreated.dateCreated, "DateCreated value was null on company created");
        Assert.assertEquals(companyCreated.dateUpdated, companyCreated.dateCreated, "DateUpdated value doesn't match with the dateCreated value");
        Assert.assertNull(companyCreated.companyPropertiesId, "Company had a propertiesId value assigned");
        Assert.assertTrue(companyCreated.retailers.isEmpty(), "List of company retailers was not empty");
        Assert.assertTrue(companyCreated.locales.isEmpty(), "List of company locales was not empty");
        Assert.assertTrue(companyCreated.campaigns.isEmpty(), "List of company campaigns was not empty");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanCreateFullCompany() throws Exception {
        cleanupCompany(FULL_COMPANY_TO_ADD.name);
        List<Company.CompanyRetailers> expectedRetailers = getExpectedRetailers(FULL_COMPANY_TO_ADD.retailers);

        Response response = CompanyApiRequests.createCompany(FULL_COMPANY_TO_ADD, jwt);

        Company companyCreated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertNotNull(companyCreated._id, "Company Id was null");
        Assert.assertEquals(companyCreated.name, FULL_COMPANY_TO_ADD.name, "Company name doesn't match with the expected name");
        Assert.assertEquals(companyCreated.retailers, expectedRetailers, "Company retailers don't match with the expected retailers");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"}, enabled = false)
    public void Api_Company_CannotCreateCompany_UnauthorizedErrorIsThrown() throws Exception {
        String unauthorizedUser = "qa+cpgdataadmin@juggle.com";
        String jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, unauthorizedUser, TEST_CONFIG.password);

        Response response = CompanyApiRequests.createCompany(FULL_COMPANY_TO_ADD, jwt);

        validateUnauthorizedError(response);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotCreateCompany_MissingParameters() throws Exception {
        String bodyWithoutRequiredParameter = "{\n" +
                "    \"retailers\": [],\n" +
                "    \"locales\": [],\n" +
                "    \"campaigns\": []\n" +
                "}";

        Response response = CompanyApiRequests.createCompany(bodyWithoutRequiredParameter, jwt);
        String missingParameter = "name";
        validateMissingRequestParametersError(response, missingParameter);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotCreateCompany_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"cpgCompanyId\": \"" + UUID.randomUUID() + "\",\n" +
                "    \"name\": \"QA Test 2\",\n" +
                "    \"retailers\": [\n" +
                "        {\n" +
                "            \"retailerDomainId\": \"Amazon\",\n" +
                "            \"clientRetailerName\": \"Amazon\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"locales\": [1],\n" +
                "    \"campaigns\": [1]\n" +
                "}";

        Response response = CompanyApiRequests.createCompany(bodyWithInvalidParameters, jwt);
        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"retailerDomainId\" must be a number");
        expectedErrors.add("\"0\" must be a string");
        expectedErrors.add("\"0\" must be a string");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotCreateCompany_DuplicateName() throws Exception {
        createCompanyIfNeeded(COMPANY_TO_ADD);
        CreateCompanyRequestBody companyToAdd = new CreateCompanyRequestBody(COMPANY_TO_ADD.name, UUID.randomUUID().toString());

        Response response = CompanyApiRequests.createCompany(companyToAdd, jwt);
        String expectedError = "Type: CompanyError. Subtype: NameAlreadyExists. Message: Company with name " + COMPANY_TO_ADD.name + " already exists.";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotCreateCompany_DuplicateRetailers() throws Exception {
        String retailer = "Amazon";

        CreateCompanyRequestBody companyToAdd = new CreateCompanyRequestBody(
                "QA-TEST-COMPANY-DUPLICATE-RETAILER",
                UUID.randomUUID().toString(),
                asList(
                        new CreateCompanyRequestBody.CompanyRetailers(1, retailer),
                        new CreateCompanyRequestBody.CompanyRetailers(2, retailer)
                ),
                Collections.singletonList("English"),
                Collections.singletonList("Halloween")
        );

        Response response = CompanyApiRequests.createCompany(companyToAdd, jwt);
        String expectedError = "Duplicate retailer names detected: " + retailer;
        validateUnprocessableEntityError(response, expectedError);

        CompanyRetailerStandardized retailerToTest = CompanyApiService.getCompanyRetailersAvailable(jwt).get(0);
        companyToAdd.retailers = asList(
                new CreateCompanyRequestBody.CompanyRetailers(1, retailerToTest.retailerName),
                new CreateCompanyRequestBody.CompanyRetailers(2, retailerToTest.retailerName)
        );

        response = CompanyApiRequests.createCompany(companyToAdd, jwt);
        expectedError = "Duplicate systemRetailerIds detected: " + retailerToTest._id;
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotCreateCompany_DuplicateLocales() throws Exception {
        String locale = "English";
        String locale2 = "French";

        CreateCompanyRequestBody companyToAdd = new CreateCompanyRequestBody(
                "QA-TEST-COMPANY-DUPLICATE-LOCALE",
                UUID.randomUUID().toString(),
                new ArrayList<>(),
                asList(locale, locale, locale2, locale2),
                Collections.singletonList("Halloween")
        );

        Response response = CompanyApiRequests.createCompany(companyToAdd, jwt);
        String expectedError = "Duplicate locale names detected: " + locale + "," + locale2;
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotCreateCompany_DuplicateCampaigns() throws Exception {
        String campaign = "Halloween";
        String campaign2 = "Christmas";

        CreateCompanyRequestBody companyToAdd = new CreateCompanyRequestBody(
                "QA-TEST-COMPANY-DUPLICATE-CAMPAIGN",
                UUID.randomUUID().toString(),
                new ArrayList<>(),
                new ArrayList<>(),
                asList(campaign, campaign, campaign2, campaign2)
        );

        Response response = CompanyApiRequests.createCompany(companyToAdd, jwt);
        String expectedError = "Duplicate campaigns detected: " + campaign + "," + campaign2;
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanUpdateCompanyName() throws Exception {
        Company companyBeforeUpdate = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        String newCompanyName = companyBeforeUpdate.name + "-NEW";

        Response response = CompanyApiRequests.updateCompanyName(newCompanyName, jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        boolean oldCompanyNameExists = COMPANY_COLLECTION.doesCompanyNameExist(FULL_COMPANY_TO_ADD.name);
        Assert.assertFalse(oldCompanyNameExists, "Previous company name still in database");
        Assert.assertEquals(updatedCompany.name, newCompanyName, "Company name was not updated");

        FULL_COMPANY_TO_ADD.name = updatedCompany.name;

        Assert.assertEquals(updatedCompany._id, companyBeforeUpdate._id, "Company id was changed after update");
        Assert.assertEquals(updatedCompany._version, companyBeforeUpdate._version + 1, "Version didn't increase");
        Assert.assertEquals(updatedCompany.dateCreated, companyBeforeUpdate.dateCreated, "Date created value changed after updating company name");
        Assert.assertNotEquals(updatedCompany.dateUpdated, companyBeforeUpdate.dateUpdated, "Date updated value didn't change after updating company name");
        Assert.assertEquals(updatedCompany.retailers, companyBeforeUpdate.retailers, "Retailers were updated");
        Assert.assertEquals(updatedCompany.locales, companyBeforeUpdate.locales, "Locales were updated");
        Assert.assertEquals(updatedCompany.companyPropertiesId, companyBeforeUpdate.companyPropertiesId, "Company properties changed after updating company name");
        Assert.assertEquals(updatedCompany.campaigns, companyBeforeUpdate.campaigns, "Campaigns changed after updating company name");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotUpdateCompanyName_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"name\": 123\n" +
                "}";

        Response response = callEndpoint(CompanyRoutes.getCompanyHost(), jwt, "PUT", bodyWithInvalidParameters, "");
        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"name\" must be a string");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanGetCompanyData() throws Exception {
        Company companyInDatabase = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        Company companyReturned = CompanyApiService.getCompany(jwt);
        Assert.assertEquals(companyInDatabase, companyReturned, "Company returned doesn't match with the company requested.");
    }

    @Test(
            groups = {"CompanyApiTests", "NoDataProvider"},
            description = "Delete a retailer or campaign from the company and make sure that both retailer or campaigns PLUS retailerCampaigns get delete from products"
    )
    public void Api_Company_DeletingRetailerOrCampaignFromCompanyUpdatesProductsProperly() throws Exception {
        createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        CompanyApiService.replaceCompanyLocales(LOCALES_TO_ADD, jwt);
        CompanyApiService.replaceCompanyRetailers(RETAILERS_TO_ADD, jwt);
        CompanyApiService.replaceCompanyCampaigns(CAMPAIGNS_TO_ADD, jwt);

        CreateCompanyPropertiesRequestBody companyPropertiesReq = getCompanyPropertiesToAdd();
        CompanyApiService.replaceCompanyProperties(companyPropertiesReq, jwt);

        Company company = CompanyApiService.getCompany(jwt);

        Company.CompanyLocales localeToUse = company.locales.get(0);
        Company.CompanyLocales secondLocaleToUse = company.locales.get(1);
        Company.CompanyRetailers retailerToDelete = company.retailers.get(0);
        Company.CompanyRetailers retailerToKeep = company.retailers.get(1);
        Company.CompanyRetailers secondRetailerToDelete = company.retailers.get(2);
        Company.CompanyCampaign campaignToDelete = company.campaigns.get(0);
        Company.CompanyCampaign campaignToKeep = company.campaigns.get(1);
        Company.CompanyCampaign secondCampaignToDelete = company.campaigns.get(2);

        createProductAndVariantIfNeeded(jwt, company._id, localeToUse.localeId);
        ProductMaster productMaster = createProductAndVariantIfNeeded(jwt, company._id, secondLocaleToUse.localeId);

        ProductVariantProperty property = new ProductVariantProperty("autogenerated_property", Collections.singletonList("test"));

        ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.RETAILER.getLevel(),
                localeToUse.localeId,
                retailerToDelete.systemRetailerId,
                null,
                Collections.singletonList(property)
        );

        // First locale - retailer instance to delete
        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        // First locale - retailer instance to keep
        propertySetToAdd.retailerId = retailerToKeep.systemRetailerId;
        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        // First locale - campaign instance to keep
        propertySetToAdd.level = Enums.ProductVariantLevel.GLOBAL_CAMPAIGN.getLevel();
        propertySetToAdd.retailerId = null;
        propertySetToAdd.campaignId = campaignToKeep.id;
        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        // First locale - campaign instance to delete
        propertySetToAdd.campaignId = campaignToDelete.id;
        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        // First locale - retailerCampaign instance to keep
        propertySetToAdd.level = Enums.ProductVariantLevel.RETAILER_CAMPAIGN.getLevel();
        propertySetToAdd.retailerId = retailerToKeep.systemRetailerId;
        propertySetToAdd.campaignId = campaignToKeep.id;
        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        // Second locale - retailer instance to delete
        propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.RETAILER.getLevel(),
                secondLocaleToUse.localeId,
                retailerToDelete.systemRetailerId,
                null,
                Collections.singletonList(property)
        );

        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        // Second locale - campaign instance to delete
        propertySetToAdd.level = Enums.ProductVariantLevel.GLOBAL_CAMPAIGN.getLevel();
        propertySetToAdd.retailerId = null;
        propertySetToAdd.campaignId = campaignToDelete.id;
        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        // Second locale retailerCampaign instance to delete (by campaign)
        propertySetToAdd.level = Enums.ProductVariantLevel.RETAILER_CAMPAIGN.getLevel();
        propertySetToAdd.retailerId = retailerToKeep.systemRetailerId;
        propertySetToAdd.campaignId = secondCampaignToDelete.id;
        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        // Second locale retailerCampaign instance to delete (by retailer)
        propertySetToAdd.retailerId = secondRetailerToDelete.systemRetailerId;
        propertySetToAdd.campaignId = campaignToKeep.id;
        ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        productMaster = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);

        CompanyApiService.deleteCompanyRetailer(retailerToDelete.systemRetailerId, jwt);
        CompanyApiService.deleteCompanyRetailer(secondRetailerToDelete.systemRetailerId, jwt);
        CompanyApiService.deleteCompanyCampaign(campaignToDelete.id, jwt);
        CompanyApiService.deleteCompanyCampaign(secondCampaignToDelete.id, jwt);

        ProductMaster productMasterAfterDeletion = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);

        // Get expected sets
        for (ProductMaster.VariantSets.Live liveVariantBefore : productMaster.variantSets.live) {
            liveVariantBefore.instances.retailer.removeIf(instance -> instance.retailerId.equals(retailerToDelete.systemRetailerId) || instance.retailerId.equals(secondRetailerToDelete.systemRetailerId));
            liveVariantBefore.instances.globalCampaign.removeIf(instance -> instance.campaignId.equals(campaignToDelete.id) || instance.campaignId.equals(secondCampaignToDelete.id));
            liveVariantBefore.instances.retailerCampaign.removeIf(instance ->
                    instance.campaignId.equals(campaignToDelete.id)
                            || instance.campaignId.equals(secondCampaignToDelete.id)
                            || instance.retailerId.equals(retailerToDelete.systemRetailerId)
                            || instance.retailerId.equals(secondRetailerToDelete.systemRetailerId)
            );
        }

        Assert.assertEquals(
                productMasterAfterDeletion.variantSets.live,
                productMaster.variantSets.live,
                "Instances were not removed properly after removing retailers and campaigns from company"
        );
    }
}