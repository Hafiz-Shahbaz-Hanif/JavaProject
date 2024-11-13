package com.DC.apitests.productversioning.company;

import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CreateCompanyRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyRetailerStandardized;
import com.DC.utilities.apiEngine.routes.productVersioning.CompanyRoutes;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.apitests.ApiValidations.validateInvalidRequestParametersError;
import static com.DC.utilities.CommonApiMethods.callEndpoint;
import static java.util.Arrays.asList;

public class CompanyRetailersApiTests extends CompanyApiBaseClass {
    
    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanReplaceCompanyRetailers() throws Exception {
        Company companyBeforeUpdate = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        CompanyRetailerStandardized firstRetailerToUse = availableRetailers.get(availableRetailers.size() - 1);
        CompanyRetailerStandardized secondRetailerToUse = availableRetailers.get(availableRetailers.size() - 2);

        List<CreateCompanyRequestBody.CompanyRetailers> companyRetailersToReplaceWith = asList(
                new CreateCompanyRequestBody.CompanyRetailers(firstRetailerToUse.domainId, firstRetailerToUse.retailerName),
                new CreateCompanyRequestBody.CompanyRetailers(firstRetailerToUse.domainId, firstRetailerToUse.retailerName),
                new CreateCompanyRequestBody.CompanyRetailers(secondRetailerToUse.domainId, secondRetailerToUse.retailerName)
        );

        Response response = CompanyApiRequests.replaceCompanyRetailers(companyRetailersToReplaceWith, jwt);
        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        List<Company.CompanyRetailers> expectedRetailers = getExpectedRetailers(companyRetailersToReplaceWith);

        Assert.assertEquals(updatedCompany._id, companyBeforeUpdate._id, "Company id was changed after replacing company retailers");
        Assert.assertEquals(updatedCompany._version, companyBeforeUpdate._version + 1, "Version didn't increase after replacing company retailers");
        Assert.assertEquals(updatedCompany.dateCreated, companyBeforeUpdate.dateCreated, "Date created value changed after replacing company retailers");
        Assert.assertNotEquals(updatedCompany.dateUpdated, companyBeforeUpdate.dateUpdated, "Date updated value didn't change after replacing company retailers");
        Assert.assertEquals(updatedCompany.retailers, expectedRetailers, "Retailers were not replaced correctly");
        Assert.assertEquals(updatedCompany.locales, companyBeforeUpdate.locales, "Locales were updated after replacing company retailers");
        Assert.assertEquals(updatedCompany.companyPropertiesId, companyBeforeUpdate.companyPropertiesId, "Company properties changed after after replacing company retailers");
        Assert.assertEquals(updatedCompany.campaigns, companyBeforeUpdate.campaigns, "Campaigns changed after after replacing company retailers");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanMergeCompanyRetailers() throws Exception {
        Company company = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        List<CompanyRetailerStandardized> retailersToUse = availableRetailers.stream()
                .filter(r -> (r.retailerName.equals("Kroger.com")) || (r.retailerName.equals("Walmart.com")))
                .collect(java.util.stream.Collectors.toList());

        List<CreateCompanyRequestBody.CompanyRetailers> retailersToMerge = asList(
                new CreateCompanyRequestBody.CompanyRetailers(retailersToUse.get(0).domainId, retailersToUse.get(0).retailerName),
                new CreateCompanyRequestBody.CompanyRetailers(retailersToUse.get(0).domainId, retailersToUse.get(0).retailerName),
                new CreateCompanyRequestBody.CompanyRetailers(retailersToUse.get(1).domainId, retailersToUse.get(1).retailerName)
        );

        Response response = CompanyApiRequests.mergeCompanyRetailers(retailersToMerge, jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertEquals(
                updatedCompany._version,
                company._version + 1,
                "Version didn't increase after merging company retailers"
        );

        Assert.assertNotEquals(updatedCompany.retailers, company.retailers, "Retailers were not merged");

        List<Company.CompanyRetailers> expectedRetailers = getExpectedRetailers(retailersToMerge);
        company.retailers.addAll(expectedRetailers);
        Assert.assertEquals(updatedCompany.retailers, company.retailers, "Retailers don't match with the expected retailers");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanDeleteCompanyRetailers() throws Exception {
        Company company = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        int indexOfRetailerToDelete = company.retailers.size() - 1;
        String retailerToDelete = company.retailers.get(indexOfRetailerToDelete).systemRetailerId;

        Response response = CompanyApiRequests.deleteCompanyRetailers(Collections.singletonList(retailerToDelete), jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertEquals(updatedCompany._version, company._version + 1, "Version didn't increase after deleting company retailer");
        Assert.assertNotEquals(updatedCompany.retailers, company.retailers, "Retailers were not deleted");

        company.retailers.remove(indexOfRetailerToDelete);
        Assert.assertEquals(updatedCompany.retailers, company.retailers, "Retailers don't match with the expected retailers");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotDeleteCompanyRetailers_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"retailerIds\": true\n" +
                "}";

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"retailerIds\" must be an array");

        Response response = callEndpoint(CompanyRoutes.getCompanyRetailersRoutePath(), jwt, "DELETE", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);

        bodyWithInvalidParameters = "{\n" +
                "    \"retailerIds\": [\"1234\"]\n" +
                "}";

        expectedErrors = new ArrayList<>();
        expectedErrors.add("\"0\" needs to be a mongo Binary object");

        response = callEndpoint(CompanyRoutes.getCompanyRetailersRoutePath(), jwt, "DELETE", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceOrMergeCompanyRetailers_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"retailers\": [\n" +
                "        {\n" +
                "            \"clientRetailerName\": 1234,\n" +
                "            \"retailerDomainId\": false\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"retailerDomainId\" must be a number");
        expectedErrors.add("\"clientRetailerName\" must be a string");

        Response response = callEndpoint(CompanyRoutes.getCompanyRetailersRoutePath(), jwt, "POST", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);

        response = callEndpoint(CompanyRoutes.getCompanyRetailersRoutePath(), jwt, "PATCH", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceOrMergeCompanyRetailers_RetailerNotInStandardTable() throws Exception {
        createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        CreateCompanyRequestBody.CompanyRetailers retailerToMerge = new CreateCompanyRequestBody.CompanyRetailers(1, "Retailer Name Not Standard");

        String expectedError = "Company Retailer with retailerName " + retailerToMerge.clientRetailerName +
                " and domainId " + retailerToMerge.retailerDomainId + " does not exist in standard retailer table";

        Response response = CompanyApiRequests.replaceCompanyRetailers(Collections.singletonList(retailerToMerge), jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = CompanyApiRequests.mergeCompanyRetailers(Collections.singletonList(retailerToMerge), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }
}
