package com.DC.apitests.productversioning.company;

import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.routes.productVersioning.CompanyRoutes;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.apitests.ApiValidations.validateInvalidRequestParametersError;
import static com.DC.utilities.CommonApiMethods.callEndpoint;
import static java.util.Arrays.asList;

public class CompanyLocalesApiTests extends CompanyApiBaseClass {

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanReplaceCompanyLocales() throws Exception {
        Company companyBeforeUpdate = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        List<String> localeNames = asList(availableLocales.get(0).localeName, availableLocales.get(1).localeName, availableLocales.get(0).localeName);
        List<Company.CompanyLocales> expectedCompanyLocales = getExpectedLocales(localeNames);

        Response response = CompanyApiRequests.replaceCompanyLocales(localeNames, jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertEquals(updatedCompany._id, companyBeforeUpdate._id, "Company id was changed after replacing company locales");
        Assert.assertEquals(updatedCompany._version, companyBeforeUpdate._version + 1, "Version didn't increase after replacing company locales");
        Assert.assertEquals(updatedCompany.dateCreated, companyBeforeUpdate.dateCreated, "Date created value changed after replacing company locales");
        Assert.assertNotEquals(updatedCompany.dateUpdated, companyBeforeUpdate.dateUpdated, "Date updated value didn't change after replacing company locales");
        Assert.assertEquals(updatedCompany.retailers, companyBeforeUpdate.retailers, "Retailers were updated after replacing company locales");
        Assert.assertEquals(updatedCompany.locales, expectedCompanyLocales, "Locales were not replaced correctly");
        Assert.assertEquals(updatedCompany.companyPropertiesId, companyBeforeUpdate.companyPropertiesId, "Company properties changed after after replacing company locales");
        Assert.assertEquals(updatedCompany.campaigns, companyBeforeUpdate.campaigns, "Campaigns changed after after replacing company locales");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanMergeCompanyLocales() throws Exception {
        Company company = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        List<String> localeNamesToMerge = asList("en-CA", "es-ES", "en-CA");

        Response response = CompanyApiRequests.mergeCompanyLocales(localeNamesToMerge, jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertEquals(updatedCompany._version, company._version + 1, "Version didn't increase after merging company locales");
        Assert.assertNotEquals(updatedCompany.locales, company.locales, "Locales were not merged");

        List<Company.CompanyLocales> expectedCompanyLocales = getExpectedLocales(localeNamesToMerge);
        company.locales.addAll(expectedCompanyLocales);
        Assert.assertEquals(updatedCompany.locales, company.locales, "Locales don't match with the expected locales");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanDeleteCompanyLocales() throws Exception {
        Company company = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        int indexOfLocaleToDelete = company.locales.size() - 1;
        String localeIdToDelete = company.locales.get(indexOfLocaleToDelete).localeId;

        Response response = CompanyApiRequests.deleteCompanyLocales(Collections.singletonList(localeIdToDelete), jwt);

        Company updatedCompany = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), Company.class);

        Assert.assertEquals(updatedCompany._version, company._version + 1, "Version didn't increase after deleting company locale");
        Assert.assertNotEquals(updatedCompany.locales, company.locales, "Locales were not deleted");

        company.locales.remove(indexOfLocaleToDelete);
        Assert.assertEquals(updatedCompany.locales, company.locales, "Locales don't match with the expected retailers");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotDeleteCompanyLocales_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"localeIds\": true\n" +
                "}";

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"localeIds\" must be an array");

        Response response = callEndpoint(CompanyRoutes.getCompanyLocalesRoutePath(), jwt, "DELETE", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);

        bodyWithInvalidParameters = "{\n" +
                "    \"localeIds\": [\"1234\"]\n" +
                "}";

        expectedErrors = new ArrayList<>();
        expectedErrors.add("\"0\" needs to be a mongo Binary object");

        response = callEndpoint(CompanyRoutes.getCompanyLocalesRoutePath(), jwt, "DELETE", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceOrMergeCompanyLocales_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n    \"locales\": \"invalid\"\n}";

        List<String> expectedErrors = Collections.singletonList("\"locales\" must be an array");

        Response response = callEndpoint(CompanyRoutes.getCompanyLocalesRoutePath(), jwt, "POST", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);

        response = callEndpoint(CompanyRoutes.getCompanyLocalesRoutePath(), jwt, "PATCH", bodyWithInvalidParameters, "");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceOrMergeCompanyLocales_LocaleNotInStandardTable() throws Exception {
        createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        String localeNotInStandardTable = "automation-QA";

        String expectedError = "Company Locale with localeName " + localeNotInStandardTable + " does not exist in standard locale table";

        Response response = CompanyApiRequests.replaceCompanyLocales(Collections.singletonList(localeNotInStandardTable), jwt);
        validateUnprocessableEntityError(response, expectedError);

        response = CompanyApiRequests.mergeCompanyLocales(Collections.singletonList(localeNotInStandardTable), jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    private static List<Company.CompanyLocales> getExpectedLocales(List<String> companyLocalesToReplaceWith) {
        List<String> companyLocalesWithNoDuplicates = companyLocalesToReplaceWith.stream().distinct().collect(Collectors.toList());
        return availableLocales.stream()
                .filter(availableRetailer -> companyLocalesWithNoDuplicates.contains(availableRetailer.localeName))
                .map(availableRetailer -> new Company.CompanyLocales(availableRetailer._id, availableRetailer.localeName))
                .collect(Collectors.toList());
    }
}
