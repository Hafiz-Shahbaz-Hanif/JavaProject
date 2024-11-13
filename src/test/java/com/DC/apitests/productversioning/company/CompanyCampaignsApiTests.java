package com.DC.apitests.productversioning.company;

import com.DC.utilities.SharedMethods;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.routes.productVersioning.CompanyRoutes;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.apitests.ApiValidations.validateInvalidRequestParametersError;
import static com.DC.utilities.CommonApiMethods.callEndpoint;
import static java.util.Arrays.asList;

public class CompanyCampaignsApiTests extends CompanyApiBaseClass {

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanReplaceCompanyCampaigns() throws Exception {
        Company companyBeforeUpdate = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        List<Company.CompanyCampaign> expectedCompanyCampaigns = asList(
                new Company.CompanyCampaign(UUID.randomUUID().toString(), "Summer"),
                new Company.CompanyCampaign(UUID.randomUUID().toString(), "Easter")
        );

        Response response = CompanyApiRequests.replaceCompanyCampaigns(expectedCompanyCampaigns, jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertEquals(updatedCompany._id, companyBeforeUpdate._id, "Company id was changed after replacing company campaigns");
        Assert.assertEquals(updatedCompany._version, companyBeforeUpdate._version + 1, "Version didn't increase after replacing company campaigns");
        Assert.assertEquals(updatedCompany.dateCreated, companyBeforeUpdate.dateCreated, "Date created value changed after replacing company campaigns");
        Assert.assertNotEquals(updatedCompany.dateUpdated, companyBeforeUpdate.dateUpdated, "Date updated value didn't change after replacing company campaigns");
        Assert.assertEquals(updatedCompany.retailers, companyBeforeUpdate.retailers, "Retailers were updated after replacing company campaigns");
        Assert.assertEquals(updatedCompany.locales, companyBeforeUpdate.locales, "Locales were updated after replacing company campaigns");
        Assert.assertEquals(updatedCompany.companyPropertiesId, companyBeforeUpdate.companyPropertiesId, "Company properties changed after replacing company campaigns");
        Assert.assertEquals(updatedCompany.campaigns, expectedCompanyCampaigns, "Campaigns changed after after replacing company campaigns");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceCompanyCampaigns_DuplicateCampaigns() throws Exception {
        Company companyBeforeUpdate = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        int indexOfCampaignToUpdate = 0;
        String campaignId = companyBeforeUpdate.campaigns.get(indexOfCampaignToUpdate).id;

        List<Company.CompanyCampaign> expectedCompanyCampaigns = asList(
                new Company.CompanyCampaign(campaignId, "Halloween"),
                new Company.CompanyCampaign(campaignId, "Easter")
        );

        Response response = CompanyApiRequests.replaceCompanyCampaigns(expectedCompanyCampaigns, jwt);
        String expectedError = "Duplicate campaigns detected: " + campaignId;
        validateUnprocessableEntityError(response, expectedError);

        String campaignName = "Christmas";
        expectedCompanyCampaigns = asList(
                new Company.CompanyCampaign(UUID.randomUUID().toString(), campaignName),
                new Company.CompanyCampaign(UUID.randomUUID().toString(), campaignName)
        );

        response = CompanyApiRequests.replaceCompanyCampaigns(expectedCompanyCampaigns, jwt);
        expectedError = "Duplicate campaigns detected: " + campaignName;
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanUpdateExistingCompanyCampaigns() throws Exception {
        Company companyBeforeUpdate = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        int indexOfCampaignToUpdate = 0;

        Company.CompanyCampaign campaignToUpdate = new Company.CompanyCampaign(
                companyBeforeUpdate.campaigns.get(indexOfCampaignToUpdate).id,
                "New Campaign Name " + SharedMethods.generateRandomNumber()
        );

        Response response = CompanyApiRequests.updateExistingCompanyCampaigns(Collections.singletonList(campaignToUpdate), jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertEquals(updatedCompany._version, companyBeforeUpdate._version + 1, "Version didn't increase after updating existing company campaign");
        Assert.assertNotEquals(updatedCompany.campaigns, companyBeforeUpdate.campaigns, "Campaign was not updated");

        companyBeforeUpdate.campaigns.set(indexOfCampaignToUpdate, campaignToUpdate);
        Assert.assertEquals(updatedCompany.campaigns, companyBeforeUpdate.campaigns, "Campaigns don't match with the expected campaigns");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotUpdateExistingCompanyCampaigns_DuplicateCampaigns() throws Exception {
        Company companyBeforeUpdate = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        String campaignToUpdateId = companyBeforeUpdate.campaigns.get(0).id;
        String duplicateCampaignName = companyBeforeUpdate.campaigns.get(1).name;

        List<Company.CompanyCampaign> expectedCompanyCampaigns = Collections.singletonList(
                new Company.CompanyCampaign(campaignToUpdateId, duplicateCampaignName)
        );

        Response response = CompanyApiRequests.updateExistingCompanyCampaigns(expectedCompanyCampaigns, jwt);
        String expectedError = "Duplicate campaigns detected: " + duplicateCampaignName;
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanMergeCompanyCampaigns() throws Exception {
        Company company = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        List<Company.CompanyCampaign> campaignsToMerge = asList(
                new Company.CompanyCampaign(UUID.randomUUID().toString(), "Campaign Name" + SharedMethods.generateRandomNumber()),
                new Company.CompanyCampaign(UUID.randomUUID().toString(), "Campaign Name 2" + SharedMethods.generateRandomNumber())
        );

        Response response = CompanyApiRequests.mergeCompanyCampaigns(campaignsToMerge, jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertEquals(updatedCompany._version, company._version + 1, "Version didn't increase after merging company campaigns");
        Assert.assertNotEquals(updatedCompany.campaigns, company.campaigns, "Campaigns were not merged");

        company.campaigns.addAll(campaignsToMerge);
        Assert.assertEquals(updatedCompany.campaigns, company.campaigns, "Campaigns don't match with the expected campaigns");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotMergeCompanyCampaigns_DuplicateCampaigns() throws Exception {
        Company companyBeforeUpdate = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        int indexOfCampaignToUpdate = 0;
        String campaignName = companyBeforeUpdate.campaigns.get(indexOfCampaignToUpdate).name;

        List<Company.CompanyCampaign> expectedCompanyCampaigns = Collections.singletonList(
                new Company.CompanyCampaign(UUID.randomUUID().toString(), campaignName)
        );

        Response response = CompanyApiRequests.mergeCompanyCampaigns(expectedCompanyCampaigns, jwt);
        String expectedError = "Duplicate campaigns detected: " + campaignName;
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanDeleteCompanyCampaigns() throws Exception {
        Company company = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        int indexOfCampaignToDelete = company.campaigns.size() - 1;
        String campaignIdToDelete = company.campaigns.get(indexOfCampaignToDelete).id;

        Response response = CompanyApiRequests.deleteCompanyCampaigns(Collections.singletonList(campaignIdToDelete), jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertEquals(updatedCompany._version, company._version + 1, "Version didn't increase after deleting company campaigns");
        Assert.assertNotEquals(updatedCompany.campaigns, company.campaigns, "Campaigns were not deleted");

        company.campaigns.remove(indexOfCampaignToDelete);
        Assert.assertEquals(updatedCompany.campaigns, company.campaigns, "Campaigns don't match with the expected campaigns");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotDeleteCompanyCampaigns_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"campaignIds\": true\n" +
                "}";

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"campaignIds\" must be an array");

        Response response = callEndpoint(CompanyRoutes.getCompanyCampaignsRoutePath(), jwt, "DELETE", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);

        bodyWithInvalidParameters = "{\n" +
                "    \"campaignIds\": [\"1234\"]\n" +
                "}";

        expectedErrors = new ArrayList<>();
        expectedErrors.add("\"0\" needs to be a mongo Binary object");

        response = callEndpoint(CompanyRoutes.getCompanyCampaignsRoutePath(), jwt, "DELETE", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceUpdateOrMergeCompanyCampaigns_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"campaigns\": [\n" +
                "        {\n" +
                "            \"id\": \"invalid id\",\n" +
                "            \"name\": 123,\n" +
                "            \"startDate\": false,\n" +
                "            \"endDate\": \"invalid date\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"id\" needs to be a mongo Binary object");
        expectedErrors.add("\"name\" must be a string");
        expectedErrors.add("\"startDate\" must be a number of milliseconds or valid date string");
        expectedErrors.add("\"endDate\" must be a number of milliseconds or valid date string");

        Response response = callEndpoint(CompanyRoutes.getCompanyCampaignsRoutePath(), jwt, "POST", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);

        response = callEndpoint(CompanyRoutes.getCompanyCampaignsRoutePath(), jwt, "PUT", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);

        response = callEndpoint(CompanyRoutes.getCompanyCampaignsRoutePath(), jwt, "PATCH", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);
    }
}
