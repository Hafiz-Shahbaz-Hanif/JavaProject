package com.DC.apitests.productversioning.company;

import com.DC.db.productVersioning.CompanyPropertiesTemplateCollection;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CompanyPropertiesTemplateCreate;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CompanyPropertiesTemplateUpdate;
import com.DC.utilities.apiEngine.models.responses.productVersioning.Company;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyProperties;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyPropertiesBase;
import com.DC.utilities.apiEngine.models.responses.productVersioning.CompanyPropertiesTemplate;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.DC.apitests.ApiValidations.verifyEndpointReturnsCorrectObject;
import static com.DC.utilities.SecurityAPI.loginAndGetJwt;
import static java.util.Arrays.asList;

public class CompanyPropertiesTemplatesApiTests extends CompanyPropertiesApiBaseClass {
    CompanyPropertiesTemplateCreate templateToTest;

    @BeforeClass()
    public void setupTemplatesTests() {
        templateToTest = getCompanyTemplateToAdd();
    }

    @AfterClass
    public void cleanup() {
        new CompanyPropertiesTemplateCollection().deleteCompanyPropertyTemplate(templateToTest.name);
    }

    @Test(priority = 1, groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanCreateCompanyPropertiesTemplate() throws Exception {
        if (jwt == null) {
            jwt = loginAndGetJwt(TEST_CONFIG.loginEndpoint, TEST_CONFIG.supportUsername, TEST_CONFIG.password);
        }

        Response response = CompanyApiRequests.createCompanyPropertiesTemplate(templateToTest, jwt);
        verifyEndpointReturnsCorrectObject(response, testMethodName.get(), CompanyPropertiesTemplate.class);
    }

    @Test(priority = 2, groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanApplyAndUpdateCompanyPropertiesTemplateByType() throws Exception {
        Company company = setupCompanyForTest(FULL_COMPANY_TO_ADD);
        CompanyPropertiesTemplate qaTemplate = createTemplateIfNeeded(templateToTest);
        List<CompanyPropertiesTemplate> expectedTemplatesToBeApplied = CompanyApiService.getCompanyPropertiesTemplatesByType(templateToTest.templateType, jwt);
        applyTemplateAndVerifyCompanyHasTemplateProperties(company.companyPropertiesId, templateToTest, expectedTemplatesToBeApplied);
        updateTemplateAndVerifyChangesAreReflectedInCompany(qaTemplate, templateToTest);
    }

    private CompanyPropertiesTemplate createTemplateIfNeeded(CompanyPropertiesTemplateCreate templateToAdd) throws Exception {
        List<CompanyPropertiesTemplate> standardTemplates = CompanyApiService.getCompanyPropertiesTemplatesByType(templateToAdd.templateType, jwt);

        CompanyPropertiesTemplate qaTemplate = standardTemplates
                .stream()
                .filter(template -> template.name.equals(templateToAdd.name))
                .findFirst()
                .orElse(null);

        if (qaTemplate == null) {
            qaTemplate = CompanyApiService.createCompanyPropertiesTemplate(templateToAdd, jwt);
        }
        return qaTemplate;
    }

    private void applyTemplateAndVerifyCompanyHasTemplateProperties(String companyPropertiesId, CompanyPropertiesTemplateCreate templateToAdd, List<CompanyPropertiesTemplate> expectedTemplatesToBeApplied) throws Exception {
        CompanyProperties expectedCompanyProperties = CompanyApiService.getCompanyProperties(companyPropertiesId, jwt);

        Response response = CompanyApiRequests.applyPropertyTemplateToCompanyByType(templateToAdd.templateType, jwt);
        CompanyProperties companyPropertiesAfterApplyingTemplate = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), CompanyProperties.class);

        expectedCompanyProperties.propertySchema.removeIf(property -> property.id.equals(templateToAdd.propertySchema.get(0).id));
        expectedCompanyProperties.propertySchema.addAll(expectedTemplatesToBeApplied.stream().map(template -> template.propertySchema).flatMap(List::stream).collect(Collectors.toList()));
        expectedCompanyProperties.digitalAssetPropertySchema.addAll(expectedTemplatesToBeApplied.stream().map(template -> template.digitalAssetPropertySchema).flatMap(List::stream).collect(Collectors.toList()));

        Assert.assertEqualsNoOrder(
                companyPropertiesAfterApplyingTemplate.propertySchema.toArray(),
                expectedCompanyProperties.propertySchema.toArray(),
                "Company properties schema does not match expected schema after applying standard templates"
        );
        Assert.assertEqualsNoOrder(
                companyPropertiesAfterApplyingTemplate.digitalAssetPropertySchema.toArray(),
                expectedCompanyProperties.digitalAssetPropertySchema.toArray(),
                "Digital asset property schema does not match expected schema after applying standard templates"
        );

        for (CompanyPropertiesTemplate template : expectedTemplatesToBeApplied) {
            for (CompanyProperties.GroupCreate group : template.groups) {
                boolean groupWasAddedToCompany = companyPropertiesAfterApplyingTemplate.groups.stream().anyMatch(grp -> grp.name.equals(group.name));
                Assert.assertTrue(groupWasAddedToCompany, "Group " + group.name + " was not added to company");
            }
            for (CompanyProperties.GroupCreate group : template.groupsDigitalAssets) {
                boolean groupWasAddedToCompany = companyPropertiesAfterApplyingTemplate.groupsDigitalAssets.stream().anyMatch(grp -> grp.name.equals(group.name));
                Assert.assertTrue(groupWasAddedToCompany, "Digital Asset Group " + group.name + " was not added to company");
            }
        }
    }

    private void updateTemplateAndVerifyChangesAreReflectedInCompany(CompanyPropertiesTemplate qaTemplate, CompanyPropertiesTemplateCreate templateToAdd) throws Exception {
        CompanyProperties.Property extraProperty = new CompanyProperties.Property();
        extraProperty.id = "extra_property_template";
        extraProperty.name = "Extra Property From Template";
        extraProperty.type = Enums.PropertyType.STRING;
        extraProperty.helpText = "Original Help Text";
        extraProperty.allowMultipleValues = true;
        extraProperty.group = null;

        String newHelpText = "Updated Help Text";
        String idOfPropertyToUpdate = templateToAdd.propertySchema.get(0).id;

        qaTemplate.propertySchema.add(extraProperty);
        qaTemplate.propertySchema.stream().filter(prop -> prop.id.equals(idOfPropertyToUpdate)).findFirst().orElseThrow(NoSuchElementException::new).helpText = newHelpText;

        CompanyPropertiesTemplateUpdate templateWithUpdates = new CompanyPropertiesTemplateUpdate();
        templateWithUpdates.enabled = qaTemplate.enabled;
        templateWithUpdates.name = qaTemplate.name;
        templateWithUpdates.digitalAssetPropertySchema = qaTemplate.digitalAssetPropertySchema;
        templateWithUpdates.propertySchema = qaTemplate.propertySchema;
        templateWithUpdates.groups = qaTemplate.groups;
        templateWithUpdates.groupsDigitalAssets = qaTemplate.groupsDigitalAssets;

        Response response = CompanyApiRequests.updateCompanyPropertiesTemplate(templateWithUpdates, qaTemplate._id, jwt);
        verifyEndpointReturnsCorrectObject(response, testMethodName.get(), CompanyPropertiesTemplate.class);

        List<CompanyPropertiesBase.Property> propertySchema = CompanyApiService.getCompanyWithProperties(jwt).companyProperties.propertySchema;
        Assert.assertTrue(propertySchema.contains(extraProperty), "Extra property was not added to company after updating template");
        String expectedHelpText = propertySchema.stream().filter(prop -> prop.id.equals(idOfPropertyToUpdate)).findFirst().orElseThrow(NoSuchElementException::new).helpText;
        Assert.assertEquals(expectedHelpText, newHelpText, "Help text was not updated in company after updating template");
    }

    private CompanyPropertiesTemplateCreate getCompanyTemplateToAdd() {
        CompanyProperties.GroupCreate group = new CompanyProperties.GroupCreate("QA Group 1", "Automated Group");
        CompanyProperties.GroupCreate group2 = new CompanyProperties.GroupCreate("QA Group 2", "Automated Group 2");

        CompanyProperties.Property property = new CompanyProperties.Property();
        property.id = "autogenerated_property_template";
        property.name = "Autogenerated Property Template";
        property.type = Enums.PropertyType.STRING;
        property.helpText = "Original Help Text";
        property.allowMultipleValues = true;
        property.group = group.name;

        CompanyProperties.Property propertyToDelete = new CompanyProperties.Property();
        propertyToDelete.id = "autogenerated_property_template_delete";
        propertyToDelete.name = "Autogenerated Property Template To Delete";
        propertyToDelete.type = Enums.PropertyType.STRING;
        propertyToDelete.helpText = "Original Help Text";
        propertyToDelete.allowMultipleValues = true;
        propertyToDelete.group = group.name;

        CompanyPropertiesBase.DigitalAssetCompanyProperty digitalAssetProperty = new CompanyPropertiesBase.DigitalAssetCompanyProperty();
        digitalAssetProperty.id = "digital_asset_property_template";
        digitalAssetProperty.name = "Digital Asset Property Template";
        digitalAssetProperty.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty.helpText = "Original Help Text Digital Asset";
        digitalAssetProperty.allowMultipleValues = false;
        digitalAssetProperty.dropdownValues = null;
        digitalAssetProperty.group = group2.name;

        CompanyPropertiesTemplateCreate createCompanyPropertiesReqBody = new CompanyPropertiesTemplateCreate();
        createCompanyPropertiesReqBody.name = "Template For Automated Tests";
        createCompanyPropertiesReqBody.templateType = Enums.CompanyPropertiesTemplateType.STANDARD;
        createCompanyPropertiesReqBody.templateSubType = "qa-automation";
        createCompanyPropertiesReqBody.propertySchema = asList(property, propertyToDelete);
        createCompanyPropertiesReqBody.digitalAssetPropertySchema = Collections.singletonList(digitalAssetProperty);
        createCompanyPropertiesReqBody.groups = Collections.singletonList(group);
        createCompanyPropertiesReqBody.groupsDigitalAssets = Collections.singletonList(group2);

        return createCompanyPropertiesReqBody;
    }
}
