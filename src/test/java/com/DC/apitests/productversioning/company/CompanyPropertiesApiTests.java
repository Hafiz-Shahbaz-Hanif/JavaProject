package com.DC.apitests.productversioning.company;

import com.DC.db.productVersioning.CompanyPropertiesCollection;
import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.*;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static com.DC.apitests.ApiValidations.*;
import static com.DC.utilities.productManager.ProductVersioningCommonMethods.generateImageMappingProperties;
import static java.util.Arrays.asList;

public class CompanyPropertiesApiTests extends CompanyPropertiesApiBaseClass {
    @Test(
            groups = {"CompanyApiTests", "NoDataProvider"},
            description = "Verifies company properties can be replaced/created." +
                    "Also verifies image spec mapping properties are added if addImageSpec is set to true in digital assets."
    )
    public void Api_Company_CanReplaceCompanyProperties() throws Exception {
        Company company = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        List<CompanyPropertiesBase.DigitalAssetCompanyProperty> expectedDigitalAssetPropertySchema = new ArrayList<>();
        List<CompanyPropertiesBase.Property> expectedPropertySchema = new ArrayList<>(createCompanyPropertiesReqBody.propertySchema);
        storeExpectedPropertiesAndDigitalAssetProperties(expectedDigitalAssetPropertySchema, expectedPropertySchema);

        Response response = CompanyApiRequests.replaceCompanyProperties(createCompanyPropertiesReqBody, jwt);
        CompanyProperties companyPropertiesCreated = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), CompanyProperties.class);

        Assert.assertNotNull(companyPropertiesCreated._id, "Company properties id was null");
        String companyPropertiesId = CompanyApiService.getCompany(jwt).companyPropertiesId;
        Assert.assertEquals(companyPropertiesId, companyPropertiesCreated._id, "Company properties were not assigned to company");
        Assert.assertEquals(companyPropertiesCreated._version, 1, "Company properties version value doesn't match with the expected value");
        Assert.assertNotNull(companyPropertiesCreated.dateCreated, "DateCreated value was null on company properties created");
        Assert.assertEquals(companyPropertiesCreated.dateUpdated, companyPropertiesCreated.dateCreated, "DateUpdated value doesn't match with the dateCreated value");
        Assert.assertEquals(companyPropertiesCreated.name, createCompanyPropertiesReqBody.name, "Company properties name doesn't match with the expected name");
        Assert.assertEquals(companyPropertiesCreated.companyId, company._id, "Company properties were not added to the expected company");
        Assert.assertEquals(companyPropertiesCreated.digitalAssetPropertySchema, expectedDigitalAssetPropertySchema, "Digital Asset Property Schema created doesn't match with the expected schema");
        Assert.assertEquals(companyPropertiesCreated.propertySchema, expectedPropertySchema, "Property Schema created doesn't match with the expected schema");

        List<CompanyProperties.Group> expectedGroups = new ArrayList<>(createCompanyPropertiesReqBody.groups);
        List<CompanyProperties.Group> expectedDigitalAssetGroups = new ArrayList<>(createCompanyPropertiesReqBody.groupsDigitalAssets);

        for (CompanyProperties.Property property : expectedPropertySchema) {
            generateExpectedGroups(expectedGroups, property);
        }

        for (CompanyProperties.Property property : expectedDigitalAssetPropertySchema) {
            generateExpectedGroups(expectedDigitalAssetGroups, property);
        }

        Assert.assertEquals(companyPropertiesCreated.groups, expectedGroups, "Groups created don't match with the expected groups");
        Assert.assertEquals(companyPropertiesCreated.groupsDigitalAssets, expectedDigitalAssetGroups, "Digital assets groups created don't match with the expected groups");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceCompanyProperties_DuplicatePropertyIds() throws Exception {
        createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        String expectedError = "Duplicate ids detected: ";

        CompanyDigitalAssetsCreate digitalAssetProperty = new CompanyDigitalAssetsCreate();
        digitalAssetProperty.id = "duplicate_autogenerated_digital_asset_property";
        digitalAssetProperty.name = "Duplicate Autogenerated Digital Asset Property";
        digitalAssetProperty.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty.helpText = null;
        digitalAssetProperty.allowMultipleValues = true;
        digitalAssetProperty.dropdownValues = null;
        digitalAssetProperty.group = null;

        CreateCompanyPropertiesRequestBody companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();
        companyPropertiesToAdd.name = "Autogenerated Properties QA Digital Assets";
        companyPropertiesToAdd.digitalAssetPropertySchema = asList(digitalAssetProperty, digitalAssetProperty);
        Response response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        validateUnprocessableEntityError(response, expectedError + digitalAssetProperty.id);

        CompanyProperties.Property property = new CompanyProperties.Property();
        property.id = "duplicate_autogenerated_property";
        property.name = "Duplicate Autogenerated Property";
        property.type = Enums.PropertyType.STRING;
        property.helpText = null;
        property.allowMultipleValues = true;
        property.dropdownValues = null;
        property.group = null;

        companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();
        companyPropertiesToAdd.name = "Autogenerated Properties QA";
        companyPropertiesToAdd.propertySchema = asList(property, property);
        response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        validateUnprocessableEntityError(response, expectedError + property.id);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceCompanyProperties_DuplicatePropertyNames() throws Exception {
        createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        String expectedError = "Duplicate property names detected: ";

        CompanyProperties.Property property = new CompanyProperties.Property();
        property.id = "autogenerated_property_1";
        property.name = "Autogenerated Property 1";
        property.type = Enums.PropertyType.STRING;
        property.helpText = null;
        property.allowMultipleValues = true;
        property.dropdownValues = null;
        property.group = null;

        CompanyProperties.Property property2 = new CompanyProperties.Property();
        property2.id = "autogenerated_property_2";
        property2.name = property.name;
        property2.type = Enums.PropertyType.NUMBER;
        property2.helpText = null;
        property2.allowMultipleValues = true;
        property2.dropdownValues = null;
        property2.group = null;

        CreateCompanyPropertiesRequestBody companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();
        companyPropertiesToAdd.name = "Autogenerated Properties QA";
        companyPropertiesToAdd.propertySchema = asList(property, property2);
        Response response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        validateUnprocessableEntityError(response, expectedError + property.name);

        companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();

        CompanyDigitalAssetsCreate digitalAssetProperty = new CompanyDigitalAssetsCreate();
        digitalAssetProperty.id = "autogenerated_digital_asset_property_1";
        digitalAssetProperty.name = "Autogenerated Digital Asset Property 1";
        digitalAssetProperty.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty.helpText = null;
        digitalAssetProperty.allowMultipleValues = true;
        digitalAssetProperty.dropdownValues = null;
        digitalAssetProperty.group = null;

        CompanyDigitalAssetsCreate digitalAssetProperty2 = new CompanyDigitalAssetsCreate();
        digitalAssetProperty2.id = "autogenerated_digital_asset_property_2";
        digitalAssetProperty2.name = digitalAssetProperty.name;
        digitalAssetProperty2.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty2.helpText = null;
        digitalAssetProperty2.allowMultipleValues = true;
        digitalAssetProperty2.dropdownValues = null;
        digitalAssetProperty2.group = null;

        companyPropertiesToAdd.digitalAssetPropertySchema = asList(digitalAssetProperty, digitalAssetProperty2);
        response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        validateUnprocessableEntityError(response, expectedError + digitalAssetProperty.name);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceCompanyProperties_InvalidPropertyIds() throws Exception {
        createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        CompanyDigitalAssetsCreate property = new CompanyDigitalAssetsCreate();
        property.id = "invalid property";
        property.name = "Autogenerated Property";
        property.type = Enums.PropertyType.DIGITAL_ASSET;
        property.helpText = null;
        property.allowMultipleValues = true;
        property.dropdownValues = null;
        property.group = null;

        String expectedError = "Invalid ids detected: " + property.id;

        CreateCompanyPropertiesRequestBody companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();
        companyPropertiesToAdd.name = "Autogenerated Properties QA";
        companyPropertiesToAdd.digitalAssetPropertySchema = Collections.singletonList(property);
        Response response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceCompanyProperties_IncorrectTypeInDigitalAssetSchema() throws Exception {
        createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        CompanyDigitalAssetsCreate digitalAssetProperty = new CompanyDigitalAssetsCreate();
        digitalAssetProperty.id = "digital_asset_property";
        digitalAssetProperty.name = "Digital Asset Property";
        digitalAssetProperty.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty.helpText = null;
        digitalAssetProperty.allowMultipleValues = true;
        digitalAssetProperty.dropdownValues = null;
        digitalAssetProperty.group = null;

        CompanyDigitalAssetsCreate digitalAssetPropertyWrongType = new CompanyDigitalAssetsCreate();
        digitalAssetPropertyWrongType.id = "digital_asset_property_2";
        digitalAssetPropertyWrongType.name = "Digital Asset Property 2";
        digitalAssetPropertyWrongType.type = Enums.PropertyType.STRING;
        digitalAssetPropertyWrongType.helpText = null;
        digitalAssetPropertyWrongType.allowMultipleValues = true;
        digitalAssetPropertyWrongType.dropdownValues = null;
        digitalAssetPropertyWrongType.group = null;

        CreateCompanyPropertiesRequestBody companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();
        companyPropertiesToAdd.name = "Autogenerated Properties QA";
        companyPropertiesToAdd.digitalAssetPropertySchema = asList(digitalAssetProperty, digitalAssetPropertyWrongType);

        Response response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        String expectedError = "All properties in the digital asset property schema must be of type [digital_asset].";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceCompanyProperties_InvalidGroupAssignedToProperty() throws Exception {
        createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        CompanyProperties.Property property = new CompanyProperties.Property();
        property.id = "autogenerated_property";
        property.name = "Autogenerated Property";
        property.type = Enums.PropertyType.STRING;
        property.helpText = null;
        property.allowMultipleValues = false;
        property.dropdownValues = null;
        property.group = "Invalid Group";

        CreateCompanyPropertiesRequestBody companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();
        companyPropertiesToAdd.name = "Autogenerated Properties QA";
        companyPropertiesToAdd.propertySchema = Collections.singletonList(property);

        Response response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        String expectedError = "Invalid group assigned to " + property.id + ".";
        validateUnprocessableEntityError(response, expectedError);

        companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();

        CompanyDigitalAssetsCreate digitalAssetProperty = new CompanyDigitalAssetsCreate();
        digitalAssetProperty.id = property.id;
        digitalAssetProperty.name = property.name;
        digitalAssetProperty.type = Enums.PropertyType.DIGITAL_ASSET;
        digitalAssetProperty.helpText = property.helpText;
        digitalAssetProperty.allowMultipleValues = property.allowMultipleValues;
        digitalAssetProperty.dropdownValues = property.dropdownValues;
        digitalAssetProperty.group = property.group;

        companyPropertiesToAdd.digitalAssetPropertySchema = Collections.singletonList(digitalAssetProperty);

        response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        expectedError = "Invalid group assigned to " + property.id + ".";
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceCompanyProperties_DuplicateGroups() throws Exception {
        createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        CompanyProperties.Group groupToAdd = new CompanyProperties.Group("Duplicate Group", "", 0, new ArrayList<>());

        CreateCompanyPropertiesRequestBody companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();
        companyPropertiesToAdd.groups = asList(groupToAdd, groupToAdd);

        Response response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        String expectedError = "Duplicate groups detected: " + groupToAdd.name;
        validateUnprocessableEntityError(response, expectedError);

        companyPropertiesToAdd.groups = null;
        companyPropertiesToAdd.groupsDigitalAssets = asList(groupToAdd, groupToAdd);

        response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceCompanyProperties_DuplicateGroupsAcrossGroupTypes() throws Exception {
        CompanyProperties.Group groupToAdd = new CompanyProperties.Group("Duplicate Group", "", 0, new ArrayList<>());

        CreateCompanyPropertiesRequestBody companyPropertiesToAdd = new CreateCompanyPropertiesRequestBody();
        companyPropertiesToAdd.groups = Collections.singletonList(groupToAdd);
        companyPropertiesToAdd.groupsDigitalAssets = Collections.singletonList(groupToAdd);

        Response response = CompanyApiRequests.replaceCompanyProperties(companyPropertiesToAdd, jwt);
        String expectedError = "Duplicate names detected across groups types: " + groupToAdd.name;
        validateUnprocessableEntityError(response, expectedError);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotReplaceCompanyProperties_InvalidParameters() throws Exception {
        String bodyWithInvalidParameters = "{\n" +
                "    \"name\": null,\n" +
                "    \"propertySchema\": [\n" +
                "        {\n" +
                "            \"id\": null,\n" +
                "            \"name\": 12,\n" +
                "            \"type\": \"invalid type\",\n" +
                "            \"helpText\": 123,\n" +
                "            \"allowMultipleValues\": \"yes\",\n" +
                "            \"group\": []\n" +
                "        }\n" +
                "    ],\n" +
                "    \"groups\": 123,\n" +
                "    \"groupsDigitalAssets\": [123]\n" +
                "}";

        Response response = CompanyApiRequests.replaceCompanyProperties(bodyWithInvalidParameters, jwt);

        List<String> expectedErrors = new ArrayList<>();
        expectedErrors.add("\"name\" must be a string");
        expectedErrors.add("\"id\" must be a string");
        expectedErrors.add("\"name\" must be a string");
        expectedErrors.add("\"type\" must be one of [string, number, dropdown, boolean, date, digital_asset, link, html, rich_text, image_mapping, image_instructions]");
        expectedErrors.add("\"helpText\" must be a string");
        expectedErrors.add("\"allowMultipleValues\" must be a boolean");
        expectedErrors.add("\"group\" must be a string");
        expectedErrors.add("\"groups\" must be an array");
        expectedErrors.add("\"0\" must be an object");
        validateInvalidRequestParametersError(response, expectedErrors);
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanGetCompanyPropertiesData() throws Exception {
        Company company = createCompanyIfNeeded(FULL_COMPANY_TO_ADD);

        if (company.companyPropertiesId == null) {
            CompanyProperties companyProperties = CompanyApiService.replaceCompanyProperties(createCompanyPropertiesReqBody, jwt);
            if (companyProperties == null) {
                String errorMessage = "Unable to add properties to company";
                logger.error(errorMessage);
                throw new Exception(errorMessage);
            }
            company = CompanyApiService.getCompany(jwt);
        }

        Response response = CompanyApiRequests.getCompanyProperties(company.companyPropertiesId, jwt);

        CompanyProperties returnedCompanyProperties = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), CompanyProperties.class);
        CompanyProperties companyPropertiesInDatabase = new CompanyPropertiesCollection().getCompanyProperties(company.companyPropertiesId);
        Assert.assertEquals(
                returnedCompanyProperties,
                companyPropertiesInDatabase,
                "Returned company properties don't match with the company properties in db" +
                        "\nReturned company properties: " + returnedCompanyProperties +
                        "\nCompany properties in db: " + companyPropertiesInDatabase
        );
    }

    private void storeExpectedPropertiesAndDigitalAssetProperties(List<CompanyPropertiesBase.DigitalAssetCompanyProperty> expectedDigitalAssetPropertySchema, List<CompanyPropertiesBase.Property> expectedPropertySchema) {
        List<CompanyPropertiesBase.Property> additionalProperties = new ArrayList<>();
        for (int index = 0; index < createCompanyPropertiesReqBody.digitalAssetPropertySchema.size(); index++) {
            CompanyDigitalAssetsCreate property = createCompanyPropertiesReqBody.digitalAssetPropertySchema.get(index);

            CompanyPropertiesBase.DigitalAssetCompanyProperty digitalAssetCompanyProperty = new CompanyPropertiesBase.DigitalAssetCompanyProperty();
            digitalAssetCompanyProperty.id = property.id;
            digitalAssetCompanyProperty.name = property.name;
            digitalAssetCompanyProperty.type = property.type;
            digitalAssetCompanyProperty.dropdownValues = property.dropdownValues;
            digitalAssetCompanyProperty.helpText = property.helpText;
            digitalAssetCompanyProperty.allowMultipleValues = property.allowMultipleValues;
            digitalAssetCompanyProperty.group = property.group;

            if (property.addImageMappingSpecs) {

                List<CompanyPropertiesBase.Property> imageMappingProperties = generateImageMappingProperties(property.id, property.name);

                digitalAssetCompanyProperty.imageSpecMapping = new CompanyPropertiesBase.ImageSpecMapping(imageMappingProperties.get(0).id, imageMappingProperties.get(1).id);

                additionalProperties.addAll(imageMappingProperties);
            }

            expectedDigitalAssetPropertySchema.add(index, digitalAssetCompanyProperty);
        }
        expectedPropertySchema.addAll(additionalProperties);
    }

    private void generateExpectedGroups(List<CompanyProperties.Group> expectedGroups, CompanyProperties.Property property) {
        if (property.group != null) {
            CompanyProperties.Group expectedGroup = expectedGroups.stream()
                    .filter(group -> group.name.equals(property.group))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);

            int groupPropertyIndex = expectedGroup.properties.size();
            CompanyProperties.Group.GroupProperty groupProperty = new CompanyProperties.Group.GroupProperty(property.id, groupPropertyIndex);

            List<CompanyProperties.Group.GroupProperty> groupProperties = new ArrayList<>(expectedGroup.properties);
            groupProperties.add(groupProperty);
            expectedGroup.properties = groupProperties;
        }
    }


}
