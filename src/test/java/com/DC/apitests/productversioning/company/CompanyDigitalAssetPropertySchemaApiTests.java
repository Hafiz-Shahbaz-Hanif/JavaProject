package com.DC.apitests.productversioning.company;

import com.DC.utilities.apiEngine.apiRequests.productVersioning.CompanyApiRequests;
import com.DC.utilities.apiEngine.apiServices.productversioning.CompanyApiService;
import com.DC.utilities.apiEngine.apiServices.productversioning.ProductVersioningApiService;
import com.DC.utilities.apiEngine.models.requests.productVersioning.CompanyDigitalAssetsCreate;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ProductVariantDigitalAssetSetRequestBody;
import com.DC.utilities.apiEngine.models.requests.productVersioning.ProductVariantPropertySetRequestBody;
import com.DC.utilities.apiEngine.models.responses.productVersioning.*;
import com.DC.utilities.enums.Enums;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.DC.apitests.ApiValidations.validateUnprocessableEntityError;
import static com.DC.apitests.ApiValidations.verifyEndpointReturnsCorrectObject;
import static com.DC.utilities.productManager.ProductVersioningCommonMethods.generateImageMappingProperties;
import static java.util.Arrays.asList;

public class CompanyDigitalAssetPropertySchemaApiTests extends CompanyPropertiesApiBaseClass {

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanUpdateExistingCompanyDigitalAssetProperty() throws Exception {
        setupCompanyForTest(FULL_COMPANY_TO_ADD);
        CompanyPropertiesBase companyProperties = CompanyApiService.getCompanyWithProperties(jwt).companyProperties;

        // Choose a property that doesn't have the addImageMappingSpecs set to true
        String idOfPropertyToUpdate = "digital_asset_property_5";
        CompanyDigitalAssetsCreate originalProperty = createCompanyPropertiesReqBody.digitalAssetPropertySchema
                .stream()
                .filter(property -> property.id.equals(idOfPropertyToUpdate))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        CompanyDigitalAssetsCreate propertyToUpdate = new CompanyDigitalAssetsCreate();
        propertyToUpdate.id = originalProperty.id;
        propertyToUpdate.name = originalProperty.name;
        propertyToUpdate.type = originalProperty.type;
        propertyToUpdate.helpText = originalProperty.helpText;
        propertyToUpdate.allowMultipleValues = originalProperty.allowMultipleValues;
        propertyToUpdate.dropdownValues = originalProperty.dropdownValues;
        propertyToUpdate.group = Objects.equals(originalProperty.group, "Group Digital Asset 1") ? "Group Digital Asset 2" : "Group Digital Asset 1";
        propertyToUpdate.addImageMappingSpecs = true;

        Response response = CompanyApiRequests.updateExistingDigitalAssetProperties(Collections.singletonList(propertyToUpdate), jwt);
        CompanyProperties returnedCompanyProperties = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), CompanyProperties.class);

        List<CompanyPropertiesBase.Property> expectedImageMappingProperties = generateImageMappingProperties(propertyToUpdate.id, propertyToUpdate.name);

        // VERIFY ASSOCIATED PROPERTIES WERE ADDED
        Assert.assertEquals(
                returnedCompanyProperties.propertySchema.size(),
                companyProperties.propertySchema.size() + expectedImageMappingProperties.size(),
                "Associated image mapping properties for property " + idOfPropertyToUpdate + " were not added after setting addImageMappingSpecs to true"
        );

        for (CompanyPropertiesBase.Property property : expectedImageMappingProperties) {
            boolean propertyWasAdded = returnedCompanyProperties.propertySchema.contains(property);
            Assert.assertTrue(propertyWasAdded, "Image Mapping property: " + property.id + " was not added to property schema");
        }

        CompanyPropertiesBase.DigitalAssetCompanyProperty propertyAfterUpdate = returnedCompanyProperties.digitalAssetPropertySchema
                .stream()
                .filter(property -> property.id.equals(idOfPropertyToUpdate))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        Assert.assertNotNull(propertyAfterUpdate.imageSpecMapping, "Image spec mappings were not added to property " + idOfPropertyToUpdate);
        Assert.assertEquals(propertyAfterUpdate.imageSpecMapping.mappingProperty, expectedImageMappingProperties.get(0).id, "Mapping property was not added to property " + idOfPropertyToUpdate);
        Assert.assertEquals(propertyAfterUpdate.imageSpecMapping.instructionsProperty, expectedImageMappingProperties.get(1).id, "Mapping instructions property was not added to property " + idOfPropertyToUpdate);

        // CHECK PROPERTY GROUP
        CompanyProperties.Group originalGroupBeforeUpdate = companyProperties.groupsDigitalAssets
                .stream()
                .filter(group -> group.name.equals(originalProperty.group))
                .findFirst()
                .orElse(null);

        CompanyProperties.Group originalGroupAfterUpdate = returnedCompanyProperties.groupsDigitalAssets
                .stream()
                .filter(group -> group.name.equals(originalProperty.group))
                .findFirst()
                .orElse(null);

        verifyPropertyWasRemovedFromGroup(originalProperty, originalGroupAfterUpdate, originalGroupBeforeUpdate);

        CompanyProperties.Group secondaryGroupBeforeUpdate = companyProperties.groupsDigitalAssets
                .stream()
                .filter(group -> group.name.equals(propertyToUpdate.group))
                .findFirst()
                .orElse(null);

        CompanyProperties.Group secondaryGroupAfterUpdate = returnedCompanyProperties.groupsDigitalAssets
                .stream()
                .filter(group -> group.name.equals(propertyToUpdate.group))
                .findFirst()
                .orElse(null);

        Assert.assertNotEquals(secondaryGroupAfterUpdate, secondaryGroupBeforeUpdate, "Secondary group was not updated with updated property");

        int propertyToUpdateIndexInSecondaryGroup = IntStream.range(0, secondaryGroupAfterUpdate.properties.size())
                .filter(i -> Objects.equals(secondaryGroupAfterUpdate.properties.get(i).id, originalProperty.id))
                .findFirst()
                .orElse(-1);

        Assert.assertNotEquals(propertyToUpdateIndexInSecondaryGroup, -1, "Property " + propertyToUpdate.id + " was not moved to group" + propertyToUpdate.group);
        Assert.assertEquals(propertyToUpdateIndexInSecondaryGroup, secondaryGroupAfterUpdate.properties.size() - 1, "SortIndex of property didn't match with the expected index");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanRemoveImageSpecMappingFromDigitalAssetProperty() throws Exception {
        Company company = setupCompanyForTest(FULL_COMPANY_TO_ADD);
        CompanyPropertiesBase companyProperties = CompanyApiService.getCompanyWithProperties(jwt).companyProperties;

        Company.CompanyLocales companyLocaleToUse = company.locales.get(0);
        Company.CompanyCampaign companyCampaignToUse = company.campaigns.get(0);

        ProductMaster productMaster = createProductAndVariantIfNeeded(jwt, company._id, companyLocaleToUse.localeId);

        // Choose a property that has the addImageMappingSpecs set to true
        String idOfPropertyToUpdate = "digital_asset_property_3";

        CompanyDigitalAssetsCreate originalProperty = createCompanyPropertiesReqBody.digitalAssetPropertySchema
                .stream()
                .filter(property -> property.id.equals(idOfPropertyToUpdate))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        List<CompanyPropertiesBase.Property> expectedImageMappingProperties = generateImageMappingProperties(originalProperty.id, originalProperty.name);

        // Add the image mapping property to a product variant property set
        ProductVariantPropertySetRequestBody propertySetToAdd = new ProductVariantPropertySetRequestBody(
                Enums.ProductVariantLevel.GLOBAL_CAMPAIGN.getLevel(),
                companyLocaleToUse.localeId,
                null,
                companyCampaignToUse.id,
                Collections.singletonList(new ProductVariantProperty(expectedImageMappingProperties.get(0).id, Collections.singletonList("required")))
        );

        ProductVariantPropertySet propertySetBefore = ProductVersioningApiService.replaceVariantPropertySet(propertySetToAdd, productMaster._id, jwt);

        CompanyDigitalAssetsCreate propertyToUpdate = new CompanyDigitalAssetsCreate();
        propertyToUpdate.id = originalProperty.id;
        propertyToUpdate.name = originalProperty.name;
        propertyToUpdate.type = originalProperty.type;
        propertyToUpdate.helpText = originalProperty.helpText;
        propertyToUpdate.allowMultipleValues = originalProperty.allowMultipleValues;
        propertyToUpdate.dropdownValues = originalProperty.dropdownValues;
        propertyToUpdate.removeImageMappingSpecs = true;

        Response response = CompanyApiRequests.updateExistingDigitalAssetProperties(Collections.singletonList(propertyToUpdate), jwt);
        CompanyProperties returnedCompanyProperties = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), CompanyProperties.class);

        // VERIFY ASSOCIATED PROPERTIES WERE DELETED FROM SCHEMA AND PRODUCT INSTANCES
        Assert.assertEquals(
                returnedCompanyProperties.propertySchema.size(),
                companyProperties.propertySchema.size() - expectedImageMappingProperties.size(),
                "Associated image mapping properties for property " + idOfPropertyToUpdate + " were not removed after setting removeImageMappingSpecs to true"
        );

        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMaster._id,
                companyLocaleToUse.localeId,
                Enums.ProductVariantType.LIVE,
                null,
                companyCampaignToUse.id
        );

        String errorMessageToShow = "Variant property set didn't change after removing digital asset from company";
        ProductVariantPropertySet propertySetAfter = waitForPropertySetToChange(instancePath, propertySetBefore, errorMessageToShow);

        for (CompanyPropertiesBase.Property property : expectedImageMappingProperties) {
            boolean propertyExistsInSchema = returnedCompanyProperties.propertySchema.contains(property);
            Assert.assertFalse(propertyExistsInSchema, "Image Mapping property: " + property.id + " was not removed from property schema");

            boolean propertyExistsInProduct = propertySetAfter.properties.stream().anyMatch(propertySetProperty -> propertySetProperty.id.equals(property.id));
            Assert.assertFalse(propertyExistsInProduct, "Associated image mapping property was not removed from product after setting removeImageMappingSpecs to true");
        }
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CanDeleteDigitalAssetPropertyFromCompany() throws Exception {
        Company company = setupCompanyForTest(FULL_COMPANY_TO_ADD);
        CompanyPropertiesBase companyProperties = CompanyApiService.getCompanyWithProperties(jwt).companyProperties;

        Company.CompanyLocales companyLocaleToUse = company.locales.get(0);
        Company.CompanyCampaign companyCampaignToUse = company.campaigns.get(0);

        ProductMaster productMaster = createProductAndVariantIfNeeded(jwt, company._id, companyLocaleToUse.localeId);

        // Choosing a property that has addImageMappingSpecs set to true
        String idOfPropertyToRemove = "digital_asset_property_3";
        CompanyPropertiesBase.DigitalAssetCompanyProperty propertyToRemove = createCompanyPropertiesReqBody.digitalAssetPropertySchema
                .stream()
                .filter(property -> property.id.equals(idOfPropertyToRemove))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);

        DigitalAssetProperty firstDigitalAsset = new DigitalAssetProperty(
                propertyToRemove.id,
                Collections.singletonList(
                        new DigitalAssetProperty.Assets(
                                "https://os-media-service.s3.amazonaws.com/development/OneSpaceTest/Image+Comparison/imageCompareTestData/small.jpg",
                                UUID.randomUUID().toString()
                        )
                )
        );

        DigitalAssetProperty secondDigitalAsset = new DigitalAssetProperty(
                "digital_asset_property_2",
                Collections.singletonList(
                        new DigitalAssetProperty.Assets(
                                "https://os-media-service.s3.amazonaws.com/development/OneSpaceTest/Image+Comparison/imageCompareTestData/small-modified.jpg",
                                UUID.randomUUID().toString()
                        )
                )
        );

        ProductVariantDigitalAssetSetRequestBody propertySetToAdd = new ProductVariantDigitalAssetSetRequestBody(
                Enums.ProductVariantLevel.GLOBAL_CAMPAIGN.getLevel(),
                companyLocaleToUse.localeId,
                null,
                companyCampaignToUse.id,
                asList(firstDigitalAsset, secondDigitalAsset)
        );

        ProductVariantDigitalAssetSet variantDigitalAssetSetBefore = ProductVersioningApiService.replaceVariantDigitalAssetSet(propertySetToAdd, productMaster._id, jwt);

        // Get associated image mapping properties
        List<CompanyPropertiesBase.Property> associatedPropertiesThatShouldBeDeleted = companyProperties.propertySchema
                .stream()
                .filter(property -> property.id.equals(propertyToRemove.id + "_mapping") || property.id.equals(propertyToRemove.id + "_instructions"))
                .collect(Collectors.toList());

        // Delete digital asset property from company
        Response response = CompanyApiRequests.deleteDigitalAssetPropertyFromCompany(Collections.singletonList(idOfPropertyToRemove), jwt);
        CompanyProperties returnedCompanyProperties = verifyEndpointReturnsCorrectObject(response, testMethodName.get(), CompanyProperties.class);

        Assert.assertNotEquals(returnedCompanyProperties,
                companyProperties,
                "Company digital asset property schema didn't change after removing digital asset property from company"
        );

        companyProperties.digitalAssetPropertySchema.removeIf(prop -> prop.id.equals(idOfPropertyToRemove));
        companyProperties.propertySchema.removeAll(associatedPropertiesThatShouldBeDeleted);

        Assert.assertEquals(
                returnedCompanyProperties.digitalAssetPropertySchema,
                companyProperties.digitalAssetPropertySchema,
                "Returned digital asset property schema didn't match with the expected company schema"
        );

        Assert.assertEquals(
                returnedCompanyProperties.propertySchema,
                companyProperties.propertySchema,
                "Returned property schema didn't match with the expected company schema"
        );

        company = COMPANY_COLLECTION.getCompany(company._id);
        Assert.assertEquals(
                returnedCompanyProperties._id,
                company.companyPropertiesId,
                "CompanyPropertiesId changed after removing digital asset property from company"
        );

        // CHECK PROPERTY GROUP
        CompanyProperties.Group expectedGroup = companyProperties.groupsDigitalAssets
                .stream()
                .filter(group -> group.name.equals(propertyToRemove.group))
                .findFirst()
                .orElse(null);

        CompanyProperties.Group groupAfterRemovingProperty = returnedCompanyProperties.groupsDigitalAssets
                .stream()
                .filter(group -> group.name.equals(propertyToRemove.group))
                .findFirst()
                .orElse(null);

        verifyPropertyWasRemovedFromGroup(propertyToRemove, groupAfterRemovingProperty, expectedGroup);

        for (CompanyPropertiesBase.Property property : associatedPropertiesThatShouldBeDeleted) {
            Assert.assertFalse(
                    returnedCompanyProperties.propertySchema.contains(property),
                    "Associated property " + property.id + " was not removed from company after removing digital asset property"
            );
        }

        productMaster = ProductVersioningApiService.getProductMaster(productMaster._id, jwt);
        ProductVariantInstancePath instancePath = new ProductVariantInstancePath(
                productMaster._id,
                companyLocaleToUse.localeId,
                Enums.ProductVariantType.LIVE,
                null,
                companyCampaignToUse.id
        );

        String errorMessageToShow = "Variant property set didn't change after removing digital asset from company";
        ProductVariantDigitalAssetSet variantDigitalAssetSetAfter = waitForDigitalAssetPropertySetToChange(instancePath, variantDigitalAssetSetBefore, errorMessageToShow);
        Assert.assertFalse(variantDigitalAssetSetAfter.digitalAssets.contains(firstDigitalAsset), "Digital asset was not removed from product after removing it from company");
    }

    @Test(groups = {"CompanyApiTests", "NoDataProvider"})
    public void Api_Company_CannotStoreRegularPropertiesOnDigitalAssetPropertySchema() throws Exception {
        setupCompanyForTest(FULL_COMPANY_TO_ADD);

        CompanyDigitalAssetsCreate digitalAssetProperty = new CompanyDigitalAssetsCreate();
        digitalAssetProperty.id = "regular_property_in_digital_asset_schema";
        digitalAssetProperty.name = "Regular property in digital asset schema";
        digitalAssetProperty.type = Enums.PropertyType.STRING;
        digitalAssetProperty.helpText = null;
        digitalAssetProperty.allowMultipleValues = true;
        digitalAssetProperty.dropdownValues = null;
        digitalAssetProperty.group = null;

        String expectedErrorMessage = "All properties in the digital asset property schema must be of type [digital_asset].";
        List<CompanyDigitalAssetsCreate> digitalAssetProperties = Collections.singletonList(digitalAssetProperty);

        Response response = CompanyApiRequests.replaceDigitalAssetPropertySchema(digitalAssetProperties, jwt);
        validateUnprocessableEntityError(response, expectedErrorMessage);

        response = CompanyApiRequests.mergeDigitalAssetPropertiesToDigitalAssetSchema(digitalAssetProperties, jwt);
        validateUnprocessableEntityError(response, expectedErrorMessage);

        digitalAssetProperty.id = createCompanyPropertiesReqBody.digitalAssetPropertySchema.get(0).id;
        digitalAssetProperties = Collections.singletonList(digitalAssetProperty);
        response = CompanyApiRequests.updateExistingDigitalAssetProperties(digitalAssetProperties, jwt);
        validateUnprocessableEntityError(response, expectedErrorMessage);
    }
}
